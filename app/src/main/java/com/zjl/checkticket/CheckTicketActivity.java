package com.zjl.checkticket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjl.checkticket.check.HistoryAdapter;
import com.zjl.checkticket.check.HistoryModel;
import com.zjl.checkticket.setting.SettingsActivity;
import com.zjl.checkticket.statistics.StatisticsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 检票界面，显示检票结果；
 * 该界面不可见时，应该将检票工作委托给Service后台任务。
 */
public class CheckTicketActivity extends AppCompatActivity {

    private static final String TAG = "CheckTicketActivity";

    private static final String SCAN_STATUS_OK = "ok";
    private static final String SCAN_STATUS_FAIL = "fail";

    // widgets
    private Button mCheckBtn;
    private RelativeLayout mResultLayout;
    private ImageView mResultImg;
    private TextView mResultTv;
    private TextView mTicketIdTv;
    private TextView mTimeTv;
    private ProgressBar mProgressBar;
    private RecyclerView mHistoryRecycler;

    private HistoryAdapter mAdapter;

    // resources
    private int mCheckPassBgColor;
    private int mCheckFailBgColor;
    private int mCheckPassTextColor;
    private int mCheckFailTextColor;

    private String mCheckPassSentence;
    private String mCheckFailSentence;

    // last ticket that passed the check
    private String mLastPassedTicketId;

    private ArrayList<HistoryModel> historyRecords = new ArrayList<>();

    SimpleDateFormat resultSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    // scan
    private BroadcastReceiver mScanReceiver;
    private IntentFilter mScanFilter;

    // checking flag
    private boolean isChecking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_ticket);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initResources();
        initWidgets();

        initScan();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

        registerReceiver(mScanReceiver, mScanFilter);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();

        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private void initScan() {
        mScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "receive");

                //此处获取扫描结果信息
                final String scanStatus = intent.getStringExtra("EXTRA_SCAN_STATE");
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");

                Log.i(TAG, "scanResult = " + scanResult + ", scanStatus = " + scanStatus);

                if (SCAN_STATUS_OK.equals(scanStatus) && !TextUtils.isEmpty(scanResult)) {
                    if (isChecking) {
                        Log.i(TAG, "scan onReceive: but is checking last ticket, drop this request");
                        return;
                    }

                    long time = System.currentTimeMillis();

                    mTicketIdTv.setText(scanResult);
                    mTimeTv.setText(resultSdf.format(new Date(time)));

                    new CheckTicketTask(scanResult, time).execute(scanResult);
                }
            }
        };

        mScanFilter = new IntentFilter("ACTION_BAR_SCAN");
        //在用户自行获取数据时，将广播的优先级调到最高 1000，***此处必须***
    }

    private void initResources() {
        Resources resources = getResources();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCheckPassBgColor = resources.getColor(R.color.check_pass_bg, null);
            mCheckFailBgColor = resources.getColor(R.color.check_fail_bg, null);
            mCheckPassTextColor = resources.getColor(R.color.check_pass_text, null);
            mCheckFailTextColor = resources.getColor(R.color.check_fail_text, null);
        } else {
            mCheckPassBgColor = resources.getColor(R.color.check_pass_bg);
            mCheckFailBgColor = resources.getColor(R.color.check_fail_bg);
            mCheckPassTextColor = resources.getColor(R.color.check_pass_text);
            mCheckFailTextColor = resources.getColor(R.color.check_fail_text);
        }

        mCheckPassSentence = resources.getString(R.string.check_pass);
        mCheckFailSentence = resources.getString(R.string.check_fail);
    }

    private void initWidgets() {
        mResultLayout = (RelativeLayout) findViewById(R.id.result_layout);
        mResultImg = (ImageView) mResultLayout.findViewById(R.id.check_img);
        mResultTv = (TextView) mResultLayout.findViewById(R.id.result_txt);
        mTicketIdTv = (TextView) mResultLayout.findViewById(R.id.ticket_id_txt);
        mTimeTv = (TextView) mResultLayout.findViewById(R.id.time_txt);
        mProgressBar = (ProgressBar) mResultLayout.findViewById(R.id.check_pb);
        mHistoryRecycler = (RecyclerView) findViewById(R.id.history_recycler);

        mAdapter = new HistoryAdapter();
        mAdapter.setHistoryRecords(historyRecords);
        mHistoryRecycler.setAdapter(mAdapter);
        mHistoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.notifyDataSetChanged();

        mCheckBtn = (Button) findViewById(R.id.check_btn);
        mCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckTicketTask("test", 8).execute("test");
            }
        });
    }

    private void gotoSettings() {
        Intent intent = new Intent(CheckTicketActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void gotoStatistics() {
        Intent intent = new Intent(CheckTicketActivity.this, StatisticsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_checkticket_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                gotoSettings();
                return true;

            case R.id.action_statistics:
                gotoStatistics();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateResultUI(boolean checkResult) {
        mResultImg.setBackgroundResource(
                checkResult ? R.drawable.check_success : R.drawable.check_fail);

        // mResultLayout.setBackgroundColor(checkResult ? mCheckPassBgColor : mCheckFailBgColor);
        mResultTv.setTextColor(checkResult ? mCheckPassTextColor : mCheckFailTextColor);

        mResultTv.setText(checkResult ? mCheckPassSentence : mCheckFailSentence);
    }

    private void resetResultUI() {
//        mResultLayout.setBackgroundColor(mCheckPassBgColor);
        mResultTv.setTextColor(mCheckPassTextColor);

        mResultTv.setText("");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged: ");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_exit_app_msg))
                .setPositiveButton(getString(R.string.alert_exit_app_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Exit AlertDialog onPositiveButtonClick: ");
                        CheckTicketActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(getString(R.string.alert_exit_app_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Exit AlertDialog onNegativeButtonClick: ");
                    }
                })
                .create()
                .show();
    }

    class CheckTicketTask extends AsyncTask<String, Void, Boolean> {
        private String ticketId;
        private long time;

        public CheckTicketTask(String id, long time) {
            this.ticketId = id;
            this.time = time;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String id = params[0];
            if (TextUtils.isEmpty(id)) {
                return false;
            }

            if (id.equals(mLastPassedTicketId)) {
                return false;
            } else {
                return TicketUtil.getInstance().checkValidity(id);
            }
        }

        @Override
        protected void onPreExecute() {
            isChecking = true;
            resetResultUI();
            mProgressBar.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean isPassed) {
            Log.d(TAG, "checkTicketTask onPostExecute: ");
            isChecking = false;
            super.onPostExecute(isPassed);
            if (isPassed) {
                mLastPassedTicketId = ticketId;
            }

            mProgressBar.setVisibility(View.GONE);
            updateResultUI(isPassed);

            historyRecords.add(0, new HistoryModel(ticketId, time, isPassed));
            mAdapter.notifyItemInserted(0);
            mHistoryRecycler.scrollToPosition(0);
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "checkTicketTask onCancelled: ");
            isChecking = false;
            super.onCancelled();
        }
    }
}
