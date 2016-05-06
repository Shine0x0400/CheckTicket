package com.zjl.checkticket.http;

import okhttp3.MediaType;

/**
 * Created by zjl on 2016/5/5.
 */
public class HttpConstants {
    public static final String HOST_NAME = "http://www.chinashunwei.cn";

    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String URL_PATH_LOG_IN = "/afc-ui/app/login";
}
