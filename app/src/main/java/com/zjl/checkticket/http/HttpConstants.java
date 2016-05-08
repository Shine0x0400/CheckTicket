package com.zjl.checkticket.http;

import okhttp3.MediaType;

/**
 * Created by zjl on 2016/5/5.
 */
public class HttpConstants {
    public static final String HOST_NAME = "http://www.chinashunwei.cn";

    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * 登录校验
     */
    public static final String URL_PATH_LOG_IN = "/afc-ui/app/login";

    /**
     * 获取园区列表
     */
    public static final String URL_PATH_GET_PARKS = "/afc-ui/appCheck/getPark";

    /**
     * get tickets for a specified park
     */
    public static final String URL_PATH_GET_PARK_TICKETS = "/afc-ui/appCheck/getParkTickets";
}
