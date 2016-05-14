package com.zjl.checkticket.statistics;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.zjl.checkticket.R;
import com.zjl.checkticket.TicketDataManager;
import com.zjl.checkticket.db.CheckTicketContract;
import com.zjl.checkticket.db.CheckTicketDAO;
import com.zjl.checkticket.model.Ticket;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";
    private TextView mInfoTv;
    private TextView mSyncBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mInfoTv = (TextView) findViewById(R.id.info_tv);
        mInfoTv.setText(getString(R.string.statistics_info, 0, 0));

        mSyncBtn = (TextView) findViewById(R.id.sync_btn);
        mSyncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketDataManager.getInstance().syncTickets();
                finish();
            }
        });

        new QueryTicketsTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    class QueryTicketsTask extends AsyncTask<Void, Void, ArrayList<Ticket>> {

        @Override
        protected ArrayList<Ticket> doInBackground(Void... params) {
            return CheckTicketDAO.getInstance().queryAllTicketsWithCheckedInHead();
        }

        @Override
        protected void onPostExecute(ArrayList<Ticket> tickets) {
            super.onPostExecute(tickets);

            int total = tickets.size();
            int checked = 0;

            if (total > 0) {
                for (Ticket t : tickets) {
                    if (!CheckTicketContract.CheckTicketEntry.VALUE_IS_CHECKED.equals(t.getIsChecked())) {
                        break;
                    }
                    checked++;
                }
            }

            mInfoTv.setText(getString(R.string.statistics_info, total, checked));
        }
    }
}
