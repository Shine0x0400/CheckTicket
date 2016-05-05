package com.zjl.checkticket.account;

import com.alibaba.fastjson.JSONObject;
import com.zjl.checkticket.http.HttpConstants;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zjl on 2016/5/5.
 */
public class AccountManager {
    private static AccountManager sInstance = null;

    private AccountManager() {
    }

    public static AccountManager getInstance() {
        if (sInstance == null) {
            synchronized (AccountManager.class) {
                if (sInstance == null) {
                    sInstance = new AccountManager();
                }
            }
        }
        return sInstance;
    }

    public void login(String username, String password, Callback callback) {
        OkHttpClient httpClient = new OkHttpClient();

        StringBuilder url = new StringBuilder(HttpConstants.HOST_NAME);
        url.append(HttpConstants.URL_PATH_LOG_IN);

        String jsonBody = JSONObject.toJSONString(new LoginParamsModel(username, password));

        Request request = new Request.Builder()
                .url(url.toString())
                .post(RequestBody.create(HttpConstants.MEDIA_TYPE_JSON, jsonBody))
                .build();

        httpClient.newCall(request).enqueue(callback);
    }

    static class LoginParamsModel {
        String username;
        String password;

        public LoginParamsModel(String name, String pwd) {
            this.username = name;
            this.password = pwd;
        }
    }

}
