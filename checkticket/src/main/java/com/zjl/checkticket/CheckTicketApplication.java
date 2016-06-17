package com.zjl.checkticket;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by zjl on 2016/5/5.
 */
public class CheckTicketApplication extends Application {

    private static final String TAG = "CheckTicketApplication";

    public static Context sApplicationContext;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: --- ");
        super.onCreate();
        sApplicationContext = this;
    }


    // NOTE: this callback will never be called!!!
    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate: --- ");
        super.onTerminate();
        sApplicationContext = null;
        TicketDataManager.destroyInstance();
    }

}
