package com.zjl.checkticket.model;

/**
 * Created by zzg on 2016/5/2.
 */
public class TicketPOJO {
    String name;
    String kind;

    /**
     * 1 有效票
     * 2 检票
     * 3 二次检票
     */
    int status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
