package com.zjl.checkticket.check;

/**
 * Created by zjl on 16/5/13.
 */
public class HistoryModel {
    private String ticketId;
    private long checkTime;
    private boolean isPassed;

    public HistoryModel(String ticketId, long checkTime, boolean isPassed) {
        this.ticketId = ticketId;
        this.checkTime = checkTime;
        this.isPassed = isPassed;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public long getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }
}
