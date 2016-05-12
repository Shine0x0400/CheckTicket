package com.zjl.checkticket;

import com.zjl.checkticket.db.CheckTicketContract;
import com.zjl.checkticket.db.CheckTicketDAO;
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
        long time = System.currentTimeMillis();
        // TODO: 2016/5/13 time-consuming
        Ticket ticket = CheckTicketDAO.getInstance().queryTicket(tickedId);

        if (ticket == null || CheckTicketContract.CheckTicketEntry.VALUE_IS_CHECKED.equals(ticket.getIsChecked())) {
            return false;
        } else {
            // update database
            ticket.setFirstCheckTime(time);
            ticket.setIsChecked(CheckTicketContract.CheckTicketEntry.VALUE_IS_CHECKED);

            // TODO: 2016/5/13 time-consuming
            CheckTicketDAO.getInstance().updateTicket(ticket);
            return true;
        }

//        return TicketDataManager.getInstance().getParkTickets().contains(tickedId);
    }
}
