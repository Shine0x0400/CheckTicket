package com.zjl.checkticket.model;

/**
 * Created by zjl on 2016/5/7.
 */
public class Park {
//    {
//        "id" : "3a91ba0c889c450797b27f8caacc9db9",
//            "ts" : 1460126976415,
//            "dr" : 0,
//            "version" : 0,
//            "createDate" : null,
//            "sceneryId" : "2e6037ab001340f3823542107832ecd7",
//            "creatorId" : null,
//            "creatorName" : null,
//            "name" : "园区2",
//            "code" : "park2",
//            "parkDesc" : "这是颐和园"
//    }

    private String id;
    private long ts;
    private int dr;
    private int version;
    private long createDate;
    private String sceneryId;
    private String creatorId;
    private String creatorName;
    private String name;
    private String code;
    private String parkDesc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getDr() {
        return dr;
    }

    public void setDr(int dr) {
        this.dr = dr;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getSceneryId() {
        return sceneryId;
    }

    public void setSceneryId(String sceneryId) {
        this.sceneryId = sceneryId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParkDesc() {
        return parkDesc;
    }

    public void setParkDesc(String parkDesc) {
        this.parkDesc = parkDesc;
    }
}
