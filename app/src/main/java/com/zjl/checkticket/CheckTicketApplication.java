package com.zjl.checkticket;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by zjl on 2016/5/5.
 */
public class CheckTicketApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "CheckTicketApplication";
    public static final String SELECTED_PARK_PREFERENCE = "selected_park";

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: --- ");
        super.onCreate();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        String parkId = sharedPref.getString(SELECTED_PARK_PREFERENCE, "");
//
//        if (!TextUtils.isEmpty(parkId)) {
//            Log.i(TAG, "onCreate: parkId=" + parkId + ", init fetch tickets procedure");
//            TicketDataManager.getInstance().setCurrentParkId(parkId);
//            TicketDataManager.getInstance().fetchCurrentParkTickets();
//        }

        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }



    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate: --- ");
        super.onTerminate();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: key=" + key);
        if (key.equals(SELECTED_PARK_PREFERENCE)) {
            String parkId = sharedPreferences.getString(SELECTED_PARK_PREFERENCE, "");

            if (!TextUtils.isEmpty(parkId)) {
                Log.i(TAG, "onSharedPreferenceChanged: parkId=" + parkId + ", restart fetch tickets procedure");
                TicketDataManager.getInstance().setCurrentParkId(parkId);
                TicketDataManager.getInstance().fetchCurrentParkTickets();
            }
        }
    }
}
