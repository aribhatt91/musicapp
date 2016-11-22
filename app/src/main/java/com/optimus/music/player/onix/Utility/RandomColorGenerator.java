package com.optimus.music.player.onix.Utility;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author amulya
 * @datetime 14 Oct 2014, 5:20 PM
 */
public class RandomColorGenerator {

    public static RandomColorGenerator DEFAULT;

    public static RandomColorGenerator MATERIAL;

    static {
        DEFAULT = create(Arrays.asList(
                0xFFFC267C,//party pink
                0xFFABCF00,//green
                0xFF03A9F4,//blue
                0xFFA900FD,//violet
                0xFFFF7300,//orange
                0xFFFF0059,//deep pink
                0xFFFFC107//yellow
        ));
        MATERIAL = create(Arrays.asList(
                0xffe57373,
                0xfff06292,
                0xffba68c8,
                0xff9575cd,
                0xff7986cb,
                0xff64b5f6,
                0xff4fc3f7,
                0xff4dd0e1,
                0xff4db6ac,
                0xff81c784,
                0xffaed581,
                0xffff8a65,
                0xffd4e157,
                0xffffd54f,
                0xffffb74d,
                0xffa1887f,
                0xff90a4ae
        ));
    }

    private final List<Integer> mColors;
    private final Random mRandom;

    public static RandomColorGenerator create(List<Integer> colorList) {
        return new RandomColorGenerator(colorList);
    }

    private RandomColorGenerator(List<Integer> colorList) {
        mColors = colorList;
        mRandom = new Random(System.currentTimeMillis());
    }

    public int getRandomColor() {
        return mColors.get(mRandom.nextInt(mColors.size()));
    }

    public int getColor(Object key) {
        return mColors.get(Math.abs(key.hashCode()) % mColors.size());
    }

}