package com.zjl.checkticket.model;

/**门票基本信息
 * Created by zzg on 2016/5/1.
 */
public class Ticket {
    String name;
    String kind;
    String time;
    /**
     * 1 有效票
     * 2 检票
     * 3 二次检票
     */
    int status;
    String riqi;
    /**
     * 0 同步
     * 1 未同步
     *
     */

    int uploadStatus;

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getRiqi() {
        return riqi;
    }

    public void setRiqi(String riqi) {
        this.riqi = riqi;
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



}
