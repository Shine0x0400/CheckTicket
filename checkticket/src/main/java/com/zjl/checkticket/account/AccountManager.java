package com.zjl.checkticket.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zjl.checkticket.connectivity.NetworkUtil;
import com.zjl.checkticket.http.BaseRequestParams;
import com.zjl.checkticket.http.HttpClient;
import com.zjl.checkticket.http.HttpConstants;
import com.zjl.checkticket.http.ResponseBodyModel;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zjl on 2016/5/5.
 */
public class AccountManager {
    private static final String TAG = "AccountManager";
    private static AccountManager sInstance = null;

    private Account mAccount = null;

    public Account getAccount() {
        return mAccount;
    }

    private List<OnAccountStateChangedListener> listeners;

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

    public boolean hasAccountLogged() {
        return mAccount != null;
    }

    public void logout() {
        if (hasAccountLogged()) {
            mAccount = null;
            notifyAccountStateChanged(OnAccountStateChangedListener.STATE_LOGGED_OUT);
        }
    }

    public void login(final String username, String password, final LoginCallback callback) {
        if (!NetworkUtil.getInstance().isConnected()) {
            callback.onLoginComplete(false);
            return;
        }

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
                e.printStackTrace();
                callback.onLoginComplete(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    callback.onLoginComplete(false);
                    throw new IOException("Unexpected code " + response);
                }

                Log.d(TAG, "onResponse: response=" + response);

                String bodyString = response.body().string();
                Log.d(TAG, "onResponse: body=" + bodyString);

                final ResponseBodyModel<Boolean> model = JSON.parseObject(bodyString,
                        new TypeReference<ResponseBodyModel<Boolean>>() {
                        });

                mAccount = new Account(username);
                callback.onLoginComplete(model.getData());
                notifyAccountStateChanged(OnAccountStateChangedListener.STATE_LOGGED_IN);
            }
        });
    }

    public void addOnAccountStateChangedListener(OnAccountStateChangedListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (this) {
            if (listeners == null) {
                listeners = new ArrayList<>();
            }

            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    public void removeOnAccountStateChangedListener(OnAccountStateChangedListener listener) {
        synchronized (this) {
            if (listeners != null && listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }
    }

    private void notifyAccountStateChanged(int state) {
        for (OnAccountStateChangedListener l : listeners) {
            l.onAccountStateChanged(state);
        }
    }

    public static class LoginParamsModel extends BaseRequestParams {
        private String username;
        private String password;

        public LoginParamsModel(String name, String pwd) {
            this.username = name;
            this.password = pwd;
        }

        public LoginParamsModel() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public interface LoginCallback {

        /**
         * called when login procedure complete.
         *
         * @param success if login successfully.
         */
        void onLoginComplete(boolean success);
    }

    public interface OnAccountStateChangedListener {
        public static final int STATE_LOGGED_OUT = 0;
        public static final int STATE_LOGGED_IN = 1;

        /**
         * @param state
         *            for the possible values
         *            {@link #STATE_LOGGED_OUT} {@link #STATE_LOGGED_IN}
         */
        void onAccountStateChanged(int state);
    }
}
