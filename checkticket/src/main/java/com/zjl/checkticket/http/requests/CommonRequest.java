package com.zjl.checkticket.http.requests;

import android.widget.Toast;

import com.zjl.checkticket.CheckTicketApplication;
import com.zjl.checkticket.account.AccountManager;
import com.zjl.checkticket.connectivity.NetworkUtil;
import com.zjl.checkticket.http.HttpClient;
import com.zjl.checkticket.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zjl on 2016/5/7.
 */
public class CommonRequest {
    protected String url;
    protected Request request;
    protected boolean blockIfSessionExpired = false;

    protected CommonRequest() {

    }

    public Call enqueueRequest(Callback callback) {
        Call call = HttpClient.getOkHttpClient().newCall(request);

        if (!NetworkUtil.getInstance().isConnected()) {
            callback.onFailure(call, new IOException("no network connected"));
            return call;
        }

        if (blockIfSessionExpired && HttpUtil.isSessionExpired(request.url())) {
            // TODO: 2016/6/18 Try implement by callback later.
            Toast.makeText(CheckTicketApplication.sApplicationContext, "任务过期，请重新登录！", Toast.LENGTH_LONG).show();
            AccountManager.getInstance().logout();

            callback.onFailure(call, new IOException("session expired, block request"));
            return call;
        }

        call.enqueue(callback);
        return call;
    }

    public Response executeRequest() throws IOException {
        Call call = HttpClient.getOkHttpClient().newCall(request);

        if (!NetworkUtil.getInstance().isConnected()) {
            throw new IOException("no network connected");
        }

        if (blockIfSessionExpired && HttpUtil.isSessionExpired(request.url())) {
            // TODO: 2016/6/18 Try implement by callback later.
            Toast.makeText(CheckTicketApplication.sApplicationContext, "任务过期，请重新登录！", Toast.LENGTH_LONG).show();
            AccountManager.getInstance().logout();

            throw new IOException("session expired, block request");
        }

        Response response = call.execute();
        return response;
    }
}
