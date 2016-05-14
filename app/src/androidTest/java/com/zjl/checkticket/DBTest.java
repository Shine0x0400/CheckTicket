package com.zjl.checkticket;


import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.zjl.checkticket.db.CheckTicketDAO;
import com.zjl.checkticket.model.Ticket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by zjl on 2016/5/14.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class DBTest {

    @Before
    public void sleepUntilReady() {
        try {
            System.out.println("sleep --- ");
            Thread.sleep(5000);
            System.out.println("awake --- ");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryTicketTest() {
        Ticket ticket = CheckTicketDAO.getInstance().queryTicket("scenery3dPZ0200000000196");
        System.out.println(ticket);
    }

    @Test
    public void queryCheckedTicketsBeforeTimeTest() {
        System.out.println(CheckTicketDAO.getInstance().queryCheckedTicketsBeforeTime(System.currentTimeMillis()));
    }
}
