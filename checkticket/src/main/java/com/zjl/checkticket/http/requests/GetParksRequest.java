package com.zjl.checkticket.http.requests;

import com.zjl.checkticket.http.HttpConstants;

import okhttp3.Request;

/**
 * Created by zjl on 2016/5/7.
 */
public class GetParksRequest extends CommonRequest {

    public GetParksRequest() {
        url = HttpConstants.HOST_NAME + HttpConstants.URL_PATH_GET_PARKS;
        request = new Request.Builder()
                .url(url)
                .build();
    }
}
