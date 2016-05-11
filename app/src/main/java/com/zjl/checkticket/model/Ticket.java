package com.zjl.checkticket.model;

import java.io.Serializable;

/**
 * Created by zjl on 2016/5/3.
 */
public class Ticket implements Serializable {
    private String id;
    private String isChecked;// "0"--no, "1"--yes.
    private long firstCheckTime;// first check timestamp

    public Ticket() {
    }

    public Ticket(String id, String isChecked, long firstCheckTime) {
        this.id = id;
        this.isChecked = isChecked;
        this.firstCheckTime = firstCheckTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public long getFirstCheckTime() {
        return firstCheckTime;
    }

    public void setFirstCheckTime(long firstCheckTime) {
        this.firstCheckTime = firstCheckTime;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", isChecked='" + isChecked + '\'' +
                ", firstCheckTime=" + firstCheckTime +
                '}';
    }
}
