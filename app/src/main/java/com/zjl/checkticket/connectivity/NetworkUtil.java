package com.zjl.checkticket.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zjl.checkticket.CheckTicketApplication;

/**
 * Created by zjl on 2016/5/14.
 */
public class NetworkUtil {
    private static volatile NetworkUtil sInstance;

    private ConnectivityManager manager;

    private NetworkUtil() {
        manager = (ConnectivityManager) CheckTicketApplication.sApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static NetworkUtil getInstance() {
        if (sInstance == null) {
            synchronized (NetworkUtil.class) {
                if (sInstance == null) {
                    sInstance = new NetworkUtil();
                }
            }
        }
        return sInstance;
    }

    public boolean isConnected() {
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public boolean isWifiConnected() {
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean isMobileConnected() {
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }
}
