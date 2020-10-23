package com.herewhite.demo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.herewhite.demo.app.App;

import java.util.concurrent.locks.ReentrantLock;

public class SettingManager {
    private final static String TAG = "SettingManager";

    public static final String SETTING_NAME = "name";
    public static final String SETTING_DOWNLOAD_ZIP = "download_zip";


    private SharedPreferences mShare;
    private Context mContext;


    private boolean mInitialized = false;
    private String mName;
    private boolean mDownLoadZip;

    private static SettingManager sSettingManager;

    public static SettingManager get() {
        if (sSettingManager == null) {
            synchronized (SettingManager.class) {
                if (sSettingManager == null) {
                    sSettingManager = new SettingManager();
                }
            }
        }
        return sSettingManager;
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
        mDownLoadZip = mShare.getBoolean(SETTING_DOWNLOAD_ZIP, false);
        mInitialized = true;
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

    public boolean getDownLoadZip() {
        initialize();
        return mDownLoadZip;
    }

    public void setDownLoadZip(boolean bool) {
        mDownLoadZip = bool;
        mShare.edit().putBoolean(SETTING_DOWNLOAD_ZIP, bool).apply();
    }
}
