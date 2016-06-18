package com.zjl.checkticket.utils;

import com.zjl.checkticket.http.HttpClient;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by zjl on 2016/6/18.
 */
public class HttpUtil {
    public static String SESSION_NAME_FOR_EXPIRE_CHECK = "JSESSIONID";

    public static boolean isSessionExpired(HttpUrl url) {
        List<Cookie> cookies = HttpClient.getOkHttpClient().cookieJar().loadForRequest(url);

        if (cookies != null && !cookies.isEmpty()) {
            for (Cookie c : cookies) {
                if (c != null && SESSION_NAME_FOR_EXPIRE_CHECK.equals(c.name())) {
                    return System.currentTimeMillis() >= c.expiresAt();
                }
            }
        }

        return true;
    }
}
