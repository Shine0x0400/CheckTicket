package com.zjl.checkticket.account;

import com.alibaba.fastjson.JSON;
import com.zjl.checkticket.http.HttpClient;
import com.zjl.checkticket.http.HttpConstants;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zjl on 2016/5/5.
 */
public class AccountManager {
    private static AccountManager sInstance = null;

    private Account mAccount = null;

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account account) {
        this.mAccount = account;
    }

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

    public void login(String username, String password, final Callback callback) {

        StringBuilder url = new StringBuilder(HttpConstants.HOST_NAME);
        url.append(HttpConstants.URL_PATH_LOG_IN);

        String jsonBody = JSON.toJSONString(new LoginParamsModel(username, password));

        Request request = new Request.Builder()
                .url(url.toString())
                .post(RequestBody.create(HttpConstants.MEDIA_TYPE_JSON, jsonBody))
                .build();

        HttpClient.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(call, response);
            }
        });
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
