package com.zjl.checkticket.http;

import com.zjl.checkticket.connectivity.NetworkUtil;

/**
 * Created by zjl on 2016/5/29.
 */
public class BaseRequestParams {
    protected String mac = NetworkUtil.getLocalMacAddressFromIp();

    public BaseRequestParams() {
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
