package com.zjl.checkticket.http;

import java.io.Serializable;

/**
 * Created by zjl on 16/5/6.
 */
public class ResponseBodyModel<T> implements Serializable {
    private String msg;
    private T data;
    private int status;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ResponseBodyModel{" + "msg='" + msg + '\'' + ", data=" + data + ", status=" + status
                + '}';
    }
}
