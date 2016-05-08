package com.zjl.checkticket.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by zjl on 2016/5/7.
 */
public class HttpClient {
    private static OkHttpClient okHttpClient;

    private HttpClient() {
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (HttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
                        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url.host(), cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    }).build();
                }
            }
        }
        return okHttpClient;
    }

}
