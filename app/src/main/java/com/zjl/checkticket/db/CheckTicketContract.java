package com.zjl.checkticket.db;

import android.provider.BaseColumns;

/**
 * Created by zjl on 2016/5/10.
 */
public final class CheckTicketContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private CheckTicketContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class CheckTicketEntry implements BaseColumns {
        public static final String TABLE_NAME = "check_ticket_table";
        public static final String COLUMN_TICKET_ID = "ticket_id";
        public static final String COLUMN_FIRST_CHECK_TIME = "first_check_time";
        public static final String COLUMN_IS_CHECKED = "is_checked";
    }
}
