package com.herewhite.demo.utils;

import android.graphics.Color;

public class ColorUtil {

    public static int[] changeColor2Arr(int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        return new int[] {red, green, blue};
    }

    public static int changeColor2int(int[] colorArr) {
        return Color.rgb(colorArr[0], colorArr[1], colorArr[2]);
    }
}
