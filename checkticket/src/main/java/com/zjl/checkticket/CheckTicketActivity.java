package com.zjl.checkticket;

import com.zjl.checkticket.account.AccountManager;
import com.zjl.checkticket.check.HistoryAdapter;
import com.zjl.checkticket.check.HistoryModel;
import com.zjl.checkticket.setting.SettingsActivity;
import com.zjl.checkticket.statistics.StatisticsActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * 检票界面，显示检票结果；
 * 该界面不可见时，应该将检票工作委托给Service后台任务。
 */
public class CheckTicketActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AccountManager.OnAccountStateChangedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

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

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mNavUserNameTv;
    private TextView mNavLocationTv;
    private View.OnClickListener mNavHeaderOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mNavUserNameTv) {
                Log.d(TAG, "onClick: nav username been clicked");

                if (AccountManager.getInstance().hasAccountLogged()) {
                    Toast.makeText(CheckTicketActivity.this, "长按退出", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CheckTicketActivity.this, LoginActivity.class);
                    startActivityForResult(intent, StatisticsActivity.REQUEST_LOGIN_CODE);
                }
            }
        }
    };

    private AlertDialog mExitAppDialog;
    private AlertDialog mLogoutDialog;

    private HistoryAdapter mAdapter;

    // resources
    private int mCheckPassBgColor;
    private int mCheckFailBgColor;
    private int mCheckPassTextColor;
    private int mCheckFailTextColor;

    private String mCheckPassSentence;
    private String mCheckFailSentence;

    //vibrator
    private Vibrator mVibrator;
    private long[] mVibratePattern = {0, 300, 100, 300};

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
        setContentView(R.layout.activity_check);

        initResources();
        initWidgets();

        initScan();
        configScan();

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        AccountManager.getInstance().addOnAccountStateChangedListener(this);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

        registerReceiver(mScanReceiver, mScanFilter);

        updateLocation();
        PreferenceManager.getDefaultSharedPreferences(CheckTicketApplication.sApplicationContext)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();

        unregisterReceiver(mScanReceiver);

        PreferenceManager.getDefaultSharedPreferences(CheckTicketApplication.sApplicationContext)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();

        AccountManager.getInstance().removeOnAccountStateChangedListener(this);
        TicketDataManager.destroyInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * 参数名	参数类型	备注
     * EXTRA_SCAN_POWER	INT	值 = 0 表示禁用扫描功能
     * = 1 表示打开扫描功能
     * 说明：当扫描头刚打开的时候需要初始化扫描头，需要一定时间，此时将忽略相关扫描请求
     * EXTRA_TRIG_MODE	INT	值 = 0 配置扫描头为普通触发模式
     * = 1 配置扫描头为连续扫描模式
     * EXTRA_SCAN_MODE	INT	值 = 1 ：直接填充模式
     * = 2 ：虚拟按键模式
     * = 3 ：API输出模式
     * EXTRA_SCAN_AUTOENT	INT	值 = 0 关闭自动换行
     * = 1 允许自动换行
     * EXTRA_SCAN_NOTY_SND	INT	值 = 0 关闭声音提示
     * = 1 打开声音提示
     * EXTRA_SCAN_NOTY_VIB	INT	值 = 0 关闭振动提示
     * = 1 打开振动提示
     * EXTRA_SCAN_NOTY_LED	INT	值 = 0 关闭指示灯提示
     * = 1 打开指示灯提示
     */
    private void configScan() {
        Intent intent = new Intent("ACTION_BAR_SCANCFG");

        // 设置扫描为API输出模式
        intent.putExtra("EXTRA_SCAN_MODE", 3);
        // disable vibrate
        intent.putExtra("EXTRA_SCAN_NOTY_VIB", 0);
        // enable sound and led
        intent.putExtra("EXTRA_SCAN_NOTY_SND", 1);
        intent.putExtra("EXTRA_SCAN_NOTY_LED", 1);

        this.sendBroadcast(intent);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "扫描...", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

//                handleCheckResult(false);

                // issue a scan action
                Intent intent = new Intent("ACTION_BAR_TRIGSCAN");
                CheckTicketActivity.this.sendBroadcast(intent);

            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mNavUserNameTv = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        mNavLocationTv = (TextView) navigationView.getHeaderView(0).findViewById(R.id.location);

        updateAccount();
        mNavUserNameTv.setOnClickListener(mNavHeaderOnClickListener);
        mNavUserNameTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: nav username been long clicked");
                if (!AccountManager.getInstance().hasAccountLogged()) {
                    return true;
                }

                if (mLogoutDialog == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckTicketActivity.this);
                    mLogoutDialog = builder.setMessage(getString(R.string.alert_logout_msg))
                            .setPositiveButton(getString(R.string.alert_exit_app_positive),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.d(TAG, "Exit AlertDialog onPositiveButtonClick: ");
                                            AccountManager.getInstance().logout();
                                        }
                                    })
                            .setNegativeButton(getString(R.string.alert_exit_app_negative),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.d(TAG, "Exit AlertDialog onNegativeButtonClick: ");
                                        }
                                    })
                            .create();
                }

                mLogoutDialog.show();
                return true;
            }
        });

        try {
            navigationView.getMenu().findItem(R.id.nav_version)
                    .setTitle(getString(R.string.app_version_name,
                            getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateLocation() {
        String park = SharedPreferenceUtil.getInstance().getParkName();
        mNavLocationTv.setText(park == null ? getString(R.string.nav_location_tip) : park);
    }

    private void updateAccount() {
        if (AccountManager.getInstance().hasAccountLogged()) {
            mNavUserNameTv.setText(AccountManager.getInstance().getAccount().getName());
        } else {
            mNavUserNameTv.setText(R.string.nav_account_tip);
        }
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
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.i(TAG, "onOptionsItemSelected: the drawerToggle has handled the app icon touch event");
            return true;
        }

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

    private void handleCheckResult(boolean checkResult) {
        // update UI
        mResultImg.setBackgroundResource(
                checkResult ? R.drawable.check_success : R.drawable.check_fail);
        // mResultLayout.setBackgroundColor(checkResult ? mCheckPassBgColor : mCheckFailBgColor);
        mResultTv.setTextColor(checkResult ? mCheckPassTextColor : mCheckFailTextColor);
        mResultTv.setText(checkResult ? mCheckPassSentence : mCheckFailSentence);

        if (!checkResult) {
            if (SharedPreferenceUtil.getInstance().isNotificationOn()) {
                if (SharedPreferenceUtil.getInstance().isVibrateOn() && mVibrator.hasVibrator()) {
                    mVibrator.vibrate(mVibratePattern, -1);
                }

                String soundPath = SharedPreferenceUtil.getInstance().getWarningSoundPath();
                if (!TextUtils.isEmpty(soundPath)) {
                    // TODO: 2016/5/29 cache this ringtone
                    Ringtone ringtone = RingtoneManager.getRingtone(this, Uri.parse(soundPath));
                    ringtone.play();
                }
            }
        }
    }

    private void resetResultUI() {
//        mResultLayout.setBackgroundColor(mCheckPassBgColor);
        mResultTv.setTextColor(mCheckPassTextColor);

        mResultTv.setText("");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged: ");
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mExitAppDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                mExitAppDialog = builder.setMessage(getString(R.string.alert_exit_app_msg))
                        .setPositiveButton(getString(R.string.alert_exit_app_positive),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG, "Exit AlertDialog onPositiveButtonClick: ");
                                        CheckTicketActivity.super.onBackPressed();
                                    }
                                })
                        .setNegativeButton(getString(R.string.alert_exit_app_negative),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG, "Exit AlertDialog onNegativeButtonClick: ");
                                    }
                                })
                        .create();
            }

            mExitAppDialog.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_statistics) {
            gotoStatistics();
        } else if (id == R.id.nav_settings) {
            gotoSettings();
        }

        if (id != R.id.nav_version) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onPostCreate: ");
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
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
            handleCheckResult(isPassed);

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

    @Override
    public void onAccountStateChanged(final int state) {
        Log.i(TAG, "onAccountStateChanged: state=" + state);

        if (state == AccountManager.OnAccountStateChangedListener.STATE_LOGGED_IN
                || state == AccountManager.OnAccountStateChangedListener.STATE_LOGGED_OUT) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateAccount();
                }
            });
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == SharedPreferenceUtil.PREF_KEY_SELECTED_PARK) {
            updateLocation();
        }
    }
}
