package com.herewhite.demo.utils;

import android.content.Context;
import android.util.TypedValue;

public class CommonUtil {

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

}
