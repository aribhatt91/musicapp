package com.optimus.music.player.onix.Utility;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.optimus.music.player.onix.Common.Library;
import com.optimus.music.player.onix.DetailScreens.ArtistFragments.BioFragment;
import com.optimus.music.player.onix.MusicPlayer.Util;
import com.optimus.music.player.onix.R;
import com.optimus.music.player.onix.Common.Instances.*;
import com.optimus.music.player.onix.SettingsActivity.Themes;


import java.util.ArrayList;
import java.util.List;

public class PlaylistDialog {

    public static class MakeNormal implements DialogInterface.OnClickListener, TextWatcher {

        private Context context;
        private View snackbarReturnView;
        private ArrayList<Song> data;
        private TextInputLayout inputLayout;
        private AppCompatEditText editText;
        private AlertDialog dialog;

        public static void alert(View view) {
            alert(view, null);
        }

        public static void alert(View view, ArrayList<Song> songs) {
            new MakeNormal(view, songs).prompt();
        }

        private MakeNormal(View view, ArrayList<Song> songs) {
            context = view.getContext();
            snackbarReturnView = view;
            data = songs;
        }

        private void buildLayout(){
            inputLayout = new TextInputLayout(context);
            editText = new AppCompatEditText(context);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setHint("Playlist");
            inputLayout.addView(editText);
            inputLayout.setErrorEnabled(true);
            editText.addTextChangedListener(this);
        }

        private void prompt(){
            buildLayout();

            dialog = new AlertDialog.Builder(context)
                    .setTitle("Create Playlist")
                    .setView(inputLayout)
                    .setPositiveButton(R.string.action_create, this)
                    .setNegativeButton(R.string.action_cancel, this)
                    .show();

            Themes.themeAlertDialog(dialog);

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            //noinspection deprecation
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    context.getResources().getColor((Themes.isLight(context)
                            ? R.color.secondary_text_disabled_material_light
                            : R.color.secondary_text_disabled_material_dark)));

            int padding = (int) context.getResources().getDimension(R.dimen.grid_padding);
            ((View) inputLayout.getParent()).setPadding(
                    padding - inputLayout.getPaddingLeft(),
                    padding,
                    padding - inputLayout.getPaddingRight(),
                    inputLayout.getPaddingBottom());
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Library.createPlaylist(snackbarReturnView, editText.getText().toString(), data);
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String error = Library.verifyPlaylistName(context, s.toString());
            inputLayout.setError(error);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(error == null && s.length() > 0);
            if (error == null && s.length() > 0){
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Themes.getAccent());
            }
            else{
                //noinspection deprecation
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                        context.getResources().getColor((Themes.isLight(context)
                                ? R.color.secondary_text_disabled_material_light
                                : R.color.secondary_text_disabled_material_dark)));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    public static class AddToNormal implements DialogInterface.OnClickListener {

        private Context context;
        private View snackbarReturnView;
        private ArrayList<Song> data;
        private Song singleData;
        private ArrayList<Playlist> choices;

        public static void alert(View view, @StringRes int header) {
            alert(view, view.getContext().getString(header));
        }

        public static void alert(View view, String header) {
            new AddToNormal(view).prompt(header);
        }

        public static void alert(View view, Song song, @StringRes int header) {
            alert(view, song, view.getContext().getString(header));
        }

        public static void alert(View view, Song song, String header) {
            new AddToNormal(view, song).prompt(header);
        }

        public static void alert(View view, ArrayList<Song> songs, @StringRes int header) {
            alert(view, songs, view.getContext().getString(header));
        }

        public static void alert(View view, ArrayList<Song> songs, String header) {
            new AddToNormal(view, songs).prompt(header);
        }

        private AddToNormal(View view){
            this.context = view.getContext();
            this.snackbarReturnView = view;

            getChoices();
        }

        private AddToNormal(View view, ArrayList<Song> data){
            this(view);
            this.data = data;
        }

        private AddToNormal(View view, Song data){
            this(view);
            this.singleData = data;
        }

        private void getChoices(){
            choices = new ArrayList<>();
            choices.add(new Playlist(-1,
                    context.getResources().getString(R.string.action_make_new_playlist)));

            for (Playlist p : Library.getPlaylists()){
                if (!(p instanceof AutoPlaylist)) choices.add(p);
            }
        }

        public void prompt(String header){
            String[] playlistNames = new String[choices.size()];

            for (int i = 0; i < choices.size(); i++ ){
                playlistNames[i] = choices.get(i).toString();
            }
            final AlertDialog addToPlaylistDialog = new AlertDialog.Builder(context)
                    .setTitle(header)
                    .setItems(playlistNames, this)
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();

            Themes.themeAlertDialog(addToPlaylistDialog);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == 0){
                if (singleData != null) {
                    ArrayList<Song> wrappedSong = new ArrayList<>();
                    wrappedSong.add(singleData);

                    new MakeNormal(snackbarReturnView, wrappedSong).prompt();
                } else {
                    new MakeNormal(snackbarReturnView, data).prompt();
                }
            } else {
                if (singleData != null){
                    Library.addPlaylistEntry(
                            context,
                            choices.get(which),
                            singleData);
                } else {
                    Library.addPlaylistEntries(
                            snackbarReturnView,
                            choices.get(which),
                            data);
                }
            }
        }
    }

    public static class FeedbackDialog implements DialogInterface.OnClickListener {
        Context context;
        ArrayList<String> choices = new ArrayList<>();

        public static void showDialog(View view){
            new FeedbackDialog(view).prompt("Feedback");

        }

        public FeedbackDialog(View view){
            this.context = view.getContext();

            getChoices();

        }

        private void getChoices(){
            choices.add("Rate Us!");
            choices.add("Join the G+ community");
            choices.add("Like us on Facebook");
            choices.add("Follow us on Twitter");
            choices.add("Mail the developer");
            choices.add("Share the app");
        }

        private void prompt(String header){
            String[] options = new String[choices.size()];

            for (int i = 0; i < choices.size(); i++ ){
                options[i] = choices.get(i);
            }
            final AlertDialog feedback = new AlertDialog.Builder(context)
                    .setTitle(header)
                    .setItems(options, this)
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();

            Themes.themeAlertDialog(feedback);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==0){
                try {
                    final String pkg = context.getPackageName();
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
                }catch (Exception e){
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.optimus.music.player.onix")));
                    Crashlytics.logException(e);
                }

            }else if(which==1){
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Util.GPLUS)));

                }catch (Exception e){


                }

            }else if(which == 2){
                try{
                    String fb = Util.FBPAGE;
                    String url = BioFragment.getFacebookPageURL(context, fb);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);

                }catch (Exception e){

                }
            }

            else if(which==3){
                String twit = "https://www.twitter.com/" + Util.TWITTERPAGE;
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twit));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found", Toast.LENGTH_SHORT).show();
                }

            }else if(which==5){
                try{
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Hey check out this awesome Music App at: https://play.google.com/store/apps/details?id=com.optimus.music.player.onix");
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);

                }catch (Exception e){

                }

            }else if(which == 4){
                try{
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"abdarklord65@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Onix Issue/Suggestion");
                    context.startActivity(i);

                }catch (Exception e){

                }
            }

        }
    }


}