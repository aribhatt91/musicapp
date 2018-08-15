
# Optimizations
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
    }
# Make Crashlytics reports more informative
-keepattributes SourceFile,LineNumberTable, Signature, InnerClasses

# Don't break support libraries
-keep class android.support.v7.widget.SearchView { *; }
-keep class org.jaudiotagger.** { *; }
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }

-dontwarn android.support.design.**
-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
-dontwarn android.support.appcompat.**
-dontwarn com.google.android.**
-dontwarn com.sothree.**
-dontwarn com.triggertrap.seekarc.**
-dontwarn com.truizlop.fabreveallayout.**
-dontwarn xyz.danoz.recyclerviewfastscroller.**
-dontwarn com.h6ah4i.android.widget.**
-dontwarn org.jaudiotagger.**
-dontwarn com.wdullaer.materialdatetimepicker.**

-dontnote android.support.design.**
-dontnote android.support.v4.**
-dontnote android.support.v7.**
-dontnote android.support.appcompat.**
-dontnote com.google.android.**
-dontnote com.sothree.**
-dontnote com.triggertrap.seekarc.**
-dontnote com.truizlop.fabreveallayout.**
-dontnote xyz.danoz.recyclerviewfastscroller.**
-dontnote com.h6ah4i.android.widget.**
-dontnote org.jaudiotagger.**
-dontnote com.wdullaer.materialdatetimepicker.**

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform

# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn okio.**

-dontwarn java.lang.invoke.*




# Remove logcat logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-keepnames class com.optimus.music.player.onix.Common.Instances.**
-keep class sun.misc.Unsafe { *; }


