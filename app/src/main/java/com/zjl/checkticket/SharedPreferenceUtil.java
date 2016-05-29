package com.zjl.checkticket;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zjl on 2016/5/29.
 */
public class SharedPreferenceUtil {
    private static volatile SharedPreferenceUtil sInstance;

    public static final String PREF_KEY_SYNC_FREQ = "sync_frequency";
    public static final String PREF_KEY_SELECTED_PARK = "selected_park";
    public static final String PREF_KEY_NOTIFICATION = "notifications_new_message";
    public static final String PREF_KEY_VIBRATE = "notifications_new_message_vibrate";
    public static final String PREF_KEY_WARNING_SOUND = "notifications_new_message_ringtone";


    public static final String SYNC_FREQ_DEF_VALUE = "-1";

    private SharedPreferences mSharedPref;

    private SharedPreferenceUtil() {
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(CheckTicketApplication.sApplicationContext);
    }

    public static SharedPreferenceUtil getInstance() {
        if (sInstance == null) {
            synchronized (SharedPreferenceUtil.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreferenceUtil();
                }
            }
        }
        return sInstance;
    }

    public boolean isNotificationOn() {
        return mSharedPref.getBoolean(PREF_KEY_NOTIFICATION, false);
    }

    public boolean isVibrateOn() {
        return mSharedPref.getBoolean(PREF_KEY_VIBRATE, false);
    }

    public String getWarningSoundPath() {
        return mSharedPref.getString(PREF_KEY_WARNING_SOUND, "");
    }
}
