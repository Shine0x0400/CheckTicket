package com.zjl.checkticket;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void doFetchParks() {
        System.out.println(TicketDataManager.getInstance().getParks());
        TicketDataManager.getInstance().fetchParks();
        System.out.println(TicketDataManager.getInstance().getParks());
    }

    @Test
    public void doFetchParkTickets() {
        System.out.println(TicketDataManager.getInstance().getParkTickets());
        TicketDataManager.getInstance().fetchCurrentParkTickets();
        System.out.println(TicketDataManager.getInstance().getParkTickets());
    }
}