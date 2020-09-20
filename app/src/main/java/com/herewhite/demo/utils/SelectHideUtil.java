package com.herewhite.demo.utils;

import android.view.MotionEvent;
import android.view.View;

public class SelectHideUtil {

    public static void setSelect(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setAlpha(1f);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    view.setAlpha(0f);
                } else {
                    view.setAlpha(0f);
                }
                return false;
            }
        });
    }
}
