package com.zjl.checkticket;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

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

    // literal string used in code to append between strings
    private static final String PARK_INFO_SEPARATOR_LITERAL = "$*&^$";
    // regular expression, used to split string
    private static final String PARK_INFO_SEPARATOR_REGULAR_EXP = "\\$\\*&\\^\\$";

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

    public int getSyncInterval() {
        return Integer.parseInt(mSharedPref.getString(SharedPreferenceUtil.PREF_KEY_SYNC_FREQ,
                SharedPreferenceUtil.SYNC_FREQ_DEF_VALUE));
    }

    public String getParkName() {
        String park = mSharedPref.getString(PREF_KEY_SELECTED_PARK, "");
        try {
            return TextUtils.isEmpty(park) ? null
                    : park.split(PARK_INFO_SEPARATOR_REGULAR_EXP, 2)[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getParkId() {
        String park = mSharedPref.getString(PREF_KEY_SELECTED_PARK, "");
        try {
            return TextUtils.isEmpty(park) ? null
                    : park.split(PARK_INFO_SEPARATOR_REGULAR_EXP, 2)[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String makeParkSavedString(String parkId, String parkName) {
        if (parkId == null) {
            return null;
        }

        if (parkName == null) {
            parkName = "";
        }

        return new StringBuilder().append(parkId).append(PARK_INFO_SEPARATOR_LITERAL)
                .append(parkName).toString();
    }
}
