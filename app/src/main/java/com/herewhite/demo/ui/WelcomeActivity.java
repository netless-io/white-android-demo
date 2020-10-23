package com.herewhite.demo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.herewhite.demo.R;
import com.herewhite.demo.StartActivity;

import androidx.annotation.UiThread;


public class WelcomeActivity extends Activity {

    private static final int AUTO_HIDE_DELAY_MILLIS = 1000;


    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            loadMainUI();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(AUTO_HIDE_DELAY_MILLIS);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @UiThread
    private void loadMainUI() {
        Intent intent = new Intent(this, SelectActivity.class);
        startActivity(intent);
        finish();
    }
}
