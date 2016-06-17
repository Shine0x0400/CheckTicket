package com.zjl.checkticket.statistics;

import com.zjl.checkticket.LoginActivity;
import com.zjl.checkticket.R;
import com.zjl.checkticket.TicketDataManager;
import com.zjl.checkticket.account.AccountManager;
import com.zjl.checkticket.db.CheckTicketContract;
import com.zjl.checkticket.db.CheckTicketDAO;
import com.zjl.checkticket.model.Ticket;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    public static final int REQUEST_LOGIN_CODE = 100;

    private static final String TAG = "StatisticsActivity";
    private TextView mInfoTv;
    private TextView mSyncBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: 16/6/18 this will cause the CheckTicketActivity been destroyed and recreated.
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mInfoTv = (TextView) findViewById(R.id.info_tv);
        mInfoTv.setText(getString(R.string.statistics_info, 0, 0));

        mSyncBtn = (TextView) findViewById(R.id.sync_btn);
        mSyncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountManager.getInstance().hasAccountLogged()) {
                    Intent intent = new Intent(StatisticsActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN_CODE);
                    return;
                }

                TicketDataManager.getInstance().syncTickets();
                finish();
            }
        });

        new QueryTicketsTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        // login success
        if (requestCode == REQUEST_LOGIN_CODE && resultCode == RESULT_OK) {
            TicketDataManager.getInstance().syncTickets();
        }
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
