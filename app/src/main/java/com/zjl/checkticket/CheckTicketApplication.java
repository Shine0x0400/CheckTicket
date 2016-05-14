package com.zjl.checkticket;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by zjl on 2016/5/5.
 */
public class CheckTicketApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "CheckTicketApplication";

    public static final String PREF_KEY_SYNC_FREQ = "sync_frequency";
    public static final String PREF_KEY_SELECTED_PARK = "selected_park";
    public static final String SYNC_FREQ_DEF_VALUE = "-1";

    public static Context sApplicationContext;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: --- ");
        super.onCreate();
        sApplicationContext = this;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        String parkId = sharedPref.getString(PREF_KEY_SELECTED_PARK, "");
//
//        if (!TextUtils.isEmpty(parkId)) {
//            Log.i(TAG, "onCreate: parkId=" + parkId + ", init fetch tickets procedure");
//            TicketDataManager.getInstance().setCurrentParkId(parkId);
//            TicketDataManager.getInstance().fetchCurrentParkTickets();
//        }

        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }


    // NOTE: this callback will never be called!
    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate: --- ");
        super.onTerminate();
        sApplicationContext = null;
        TicketDataManager.getInstance().cancelAutoSyncTask();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: key=" + key);
        if (key.equals(PREF_KEY_SELECTED_PARK)) {
            String parkId = sharedPreferences.getString(PREF_KEY_SELECTED_PARK, "");

            if (!TextUtils.isEmpty(parkId)) {
                Log.i(TAG, "onSharedPreferenceChanged: parkId=" + parkId + ", restart fetch tickets procedure");
                TicketDataManager.getInstance().setCurrentParkId(parkId);
                TicketDataManager.getInstance().fetchCurrentParkTickets();
            }
        } else if (key.equals(PREF_KEY_SYNC_FREQ)) {
            int syncFreq = Integer.parseInt(sharedPreferences.getString(PREF_KEY_SYNC_FREQ, SYNC_FREQ_DEF_VALUE));
            Log.i(TAG, "onSharedPreferenceChanged: syncFreq=" + syncFreq + ", restart auto sync task");

            TicketDataManager.getInstance().cancelAutoSyncTask();
            if (syncFreq > 0) {
                TicketDataManager.getInstance().startAutoSyncTask();
            }

        }
    }
}
