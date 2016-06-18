package com.zjl.checkticket.http.requests;

import com.alibaba.fastjson.JSON;
import com.zjl.checkticket.http.HttpConstants;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zjl on 2016/5/7.
 */
public class GetParkTicketsRequest extends CommonRequest {

    public GetParkTicketsRequest(String parkId) {
        url = HttpConstants.HOST_NAME + HttpConstants.URL_PATH_GET_PARK_TICKETS;

        String jsonBody = JSON.toJSONString(new RequestModel(parkId));
        RequestBody requestBody = RequestBody.create(HttpConstants.MEDIA_TYPE_JSON, jsonBody);

        request = new Request.Builder().url(url).post(requestBody).build();

        blockIfSessionExpired = true;
    }

    public static class RequestModel {
        private String park;

        public RequestModel(String parkId) {
            this.park = parkId;
        }

        public RequestModel() {
        }

        public String getPark() {
            return park;
        }

        public void setPark(String park) {
            this.park = park;
        }
    }
}
