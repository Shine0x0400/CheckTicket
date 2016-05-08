package com.zjl.checkticket;

import com.zjl.checkticket.model.Ticket;

/**
 * 检票工具类
 * Created by zjl on 2016/5/3.
 */
public class TicketUtil {
    private static TicketUtil sInstance;

    private TicketUtil() {

    }

    public static TicketUtil getInstance() {
        if (sInstance == null) {
            synchronized (TicketUtil.class) {
                if (sInstance == null) {
                    sInstance = new TicketUtil();
                }
            }
        }
        return sInstance;
    }

    public boolean checkValidity(Ticket t) {
        return false;
    }

    public boolean checkValidity(String tickedId) {
        // TODO: 2016/5/8 simple implementation
        return TicketDataManager.getInstance().getParkTickets().contains(tickedId);
    }
}
