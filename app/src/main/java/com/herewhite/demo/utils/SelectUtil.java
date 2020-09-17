package com.herewhite.demo.utils;

import android.view.MotionEvent;
import android.view.View;

public class SelectUtil {

    public static void setSelect(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setAlpha(0.6f);
                } else {
                    view.setAlpha(1f);
                }
                return false;
            }
        });
    }
}
