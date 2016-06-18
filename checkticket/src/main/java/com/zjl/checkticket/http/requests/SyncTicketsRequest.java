package com.zjl.checkticket.http.requests;

import com.alibaba.fastjson.JSON;
import com.zjl.checkticket.http.HttpConstants;
import com.zjl.checkticket.model.Ticket;

import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zjl on 2016/5/14.
 */
public class SyncTicketsRequest extends CommonRequest {

    public SyncTicketsRequest(List<Ticket> checkedTickets) {
        url = HttpConstants.HOST_NAME + HttpConstants.URL_PATH_SYNC_TICKETS;

        String jsonBody = JSON.toJSONString(checkedTickets);
        RequestBody requestBody = RequestBody.create(HttpConstants.MEDIA_TYPE_JSON, jsonBody);

        request = new Request.Builder().url(url).post(requestBody).build();

        blockIfSessionExpired = true;
    }
}
