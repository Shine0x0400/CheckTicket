package com.zjl.checkticket.model;

import java.io.Serializable;

/**
 * Created by zjl on 2016/5/3.
 */
public class Ticket implements Serializable {
    private String id;
    private String isChecked;// "0"--no, "1"--yes.
    private long firstCheckTime;// first check timestamp
    private String park;// park id

    public Ticket() {
    }

    public Ticket(String id, String isChecked, long firstCheckTime, String park) {
        this.id = id;
        this.isChecked = isChecked;
        this.firstCheckTime = firstCheckTime;
        this.park = park;
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

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", isChecked='" + isChecked + '\'' +
                ", firstCheckTime=" + firstCheckTime +
                ", park='" + park + '\'' +
                '}';
    }
}
