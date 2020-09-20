package com.herewhite.demo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.herewhite.demo.app.App;

import java.util.concurrent.locks.ReentrantLock;

public class SettingManager {
    private final static String TAG = "SettingManager";

    public static final String SETTING_NAME = "name";


    private SharedPreferences mShare;
    private Context mContext;


    private boolean mInitialized = false;
    private String mName;

    private static SettingManager sTpe;

    public static SettingManager get() {
        if (sTpe == null) {
            synchronized (SettingManager.class) {
                if (sTpe == null) {
                    sTpe = new SettingManager();
                }
            }
        }
        return sTpe;
    }

    private SettingManager() {
        mContext = App.get();
        mShare = mContext.getSharedPreferences(TAG, 0);
    }

    public synchronized boolean initialize() {
        if (mInitialized) {
            return true;
        }
        mName = mShare.getString(SETTING_NAME, null);
        return true;
    }


    public String getName() {
        initialize();
        return mName;
    }

    public void setName(String name) {
        mName = name;
        mShare.edit().putString(SETTING_NAME, name).apply();
    }
}
