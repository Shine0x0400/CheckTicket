package com.zjl.checkticket;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.zjl.checkticket.connectivity.NetworkUtil;
import com.zjl.checkticket.db.CheckTicketContract;
import com.zjl.checkticket.db.CheckTicketDAO;
import com.zjl.checkticket.http.ResponseBodyModel;
import com.zjl.checkticket.http.callbacks.CommonCallback;
import com.zjl.checkticket.http.requests.GetParkTicketsRequest;
import com.zjl.checkticket.http.requests.GetParksRequest;
import com.zjl.checkticket.http.requests.SyncTicketsRequest;
import com.zjl.checkticket.model.Park;
import com.zjl.checkticket.model.Ticket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 检票数据管理类，处理同步、存储等事务。
 * Created by zjl on 2016/5/3.
 */
public class TicketDataManager extends Observable {
    private static final String TAG = "TicketDataManager";

    public static final String SYNCHRONIZE_LOCK_PARKS = "synchronize_lock_parks";
    public static final String SYNCHRONIZE_LOCK_PARK_TICKETS = "synchronize_lock_park_tickets";

    // network transfer locks
    public static final String SYNCHRONIZE_LOCK_UPLOADING_TICKETS = "synchronize_lock_uploading_tickets";
    public static final String SYNCHRONIZE_LOCK_FETCHING_TICKETS = "synchronize_lock_fetching_tickets";

    // notification
    public static final int DOWNLOAD_NOTIFICATION_ID = 0;
    // NOTE: use one same id for both.
    public static final int UPLOAD_NOTIFICATION_ID = 0;

    private static volatile TicketDataManager manager;

    private String mCurrentParkId;

    private int mSyncFreq = -1;

    private boolean mIsFetchingTickets = false;
    private boolean mIsUploadingTickets = false;

    private final ArrayList<Park> parks = new ArrayList<>();
    private final ArrayList<String> parkTickets = new ArrayList<>();

    // sync task
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private ScheduledFuture syncTask;
    private final Runnable syncRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "sync task run: ");
            syncTickets();
        }
    };


    private TicketDataManager() {
    }

    public static TicketDataManager getInstance() {
        if (manager == null) {
            synchronized (TicketDataManager.class) {
                if (manager == null) {
                    manager = new TicketDataManager();
                }
            }
        }

        return manager;
    }

    public void startAutoSyncTask() {
        Log.i(TAG, "startAutoSyncTask: ");
        if (syncTask == null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CheckTicketApplication.sApplicationContext);
            mSyncFreq = Integer.parseInt(sp.getString(CheckTicketApplication.PREF_KEY_SYNC_FREQ, CheckTicketApplication.SYNC_FREQ_DEF_VALUE));
            Log.i(TAG, "startAutoSyncTask: sync frequency=" + mSyncFreq);
            if (mSyncFreq > 0) {
                syncTask = executor.scheduleWithFixedDelay(syncRunnable, mSyncFreq, mSyncFreq, TimeUnit.MINUTES);
            }
        }
    }

    public void cancelAutoSyncTask() {
        Log.i(TAG, "cancelAutoSyncTask: ");
        if (syncTask != null) {
            syncTask.cancel(true);
            syncTask = null;
        }
    }

    public void fetchParks() {
        new GetParksRequest().enqueueRequest(new CommonCallback() {
            @Override
            public void handleSuccess(Call call, Response response) throws IOException {
                String bodyString = response.body().string();
                Log.d(TAG, "onResponse: body=" + bodyString);

                final ResponseBodyModel<List<Park>> model = JSON.parseObject(bodyString,
                        new TypeReference<ResponseBodyModel<List<Park>>>() {
                        });

                if (model.getData() != null) {
                    synchronized (SYNCHRONIZE_LOCK_PARKS) {
                        parks.clear();
                        parks.addAll(model.getData());
                    }
                }

                notifyFetchParksSuccess(parks);
            }
        });
    }

    public void fetchCurrentParkTickets() {
        if (TextUtils.isEmpty(mCurrentParkId)) {
            return;
        }

        synchronized (SYNCHRONIZE_LOCK_FETCHING_TICKETS) {
            Log.d(TAG, "fetchCurrentParkTickets: mIsFetchingTickets = " + mIsFetchingTickets);
            if (mIsFetchingTickets) {
                Log.i(TAG, "fetchCurrentParkTickets: lastFetching not finished yet, cancel this try");
                return;
            }

            mIsFetchingTickets = true;
        }

        notifyStartFetchTickets();
        new GetParkTicketsRequest(mCurrentParkId).enqueueRequest(new CommonCallback() {
            private String parkId = mCurrentParkId;

            @Override
            public void onFailure(Call call, IOException e) {
                synchronized (SYNCHRONIZE_LOCK_FETCHING_TICKETS) {
                    mIsFetchingTickets = false;
                }

                notifyCompleteFetchTickets();

                super.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                synchronized (SYNCHRONIZE_LOCK_FETCHING_TICKETS) {
                    mIsFetchingTickets = false;
                }

                notifyCompleteFetchTickets();

                super.onResponse(call, response);
            }

            @Override
            public void handleSuccess(Call call, Response response) throws IOException {

                String bodyString = response.body().string();
                Log.d(TAG, "onResponse: body=" + bodyString);

                if (mCurrentParkId == null || !mCurrentParkId.equals(parkId)) {
                    Log.i(TAG, "GetParkTickets Success: but current park has changed:" + parkId + " -> " + mCurrentParkId);
                    return;
                }

                final ResponseBodyModel<JSONArray> model = JSON.parseObject(bodyString,
                        new TypeReference<ResponseBodyModel<JSONArray>>() {
                        });

                synchronized (SYNCHRONIZE_LOCK_PARK_TICKETS) {
                    List<String> ticketIds = Arrays.asList(model.getData().toArray(new String[model.getData().size()]));
                    parkTickets.clear();
                    parkTickets.addAll(ticketIds);

                    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
                    for (String id : parkTickets) {
                        // TODO: assume server return only the non-checked tickets
                        Ticket t = new Ticket(id, CheckTicketContract.CheckTicketEntry.VALUE_IS_NOT_CHECKED, -1, parkId);
                        tickets.add(t);
                    }
                    CheckTicketDAO.getInstance().updateTableWithFreshDataExceptChecked(tickets);
                }
            }
        });
    }

    private boolean uploadCheckedTickets(long time) {
        Log.i(TAG, "uploadCheckedTickets: ");


        ArrayList<Ticket> tickets = CheckTicketDAO.getInstance().queryCheckedTicketsBeforeTime(time);

        if (tickets != null && !tickets.isEmpty()) {
            try {
                synchronized (SYNCHRONIZE_LOCK_UPLOADING_TICKETS) {
                    Log.d(TAG, "uploadCheckedTickets: mIsUploadingTickets = " + mIsUploadingTickets);
                    if (mIsUploadingTickets) {
                        Log.i(TAG, "uploadCheckedTickets: lastUploading not finished yet, cancel this try");
                        return false;
                    }
                    mIsUploadingTickets = true;
                }

                notifyStartUploadTickets();
                Response response = new SyncTicketsRequest(tickets).executeRequest();

                if (response.isSuccessful()) {
                    Log.i(TAG, "uploadCheckedTickets Success: ");

                    String bodyString = response.body().string();
                    Log.d(TAG, "onResponse: body=" + bodyString);

                    ResponseBodyModel<Boolean> model = JSON.parseObject(bodyString,
                            new TypeReference<ResponseBodyModel<Boolean>>() {
                            });

                    return model.getData();

                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                synchronized (SYNCHRONIZE_LOCK_UPLOADING_TICKETS) {
                    mIsUploadingTickets = false;
                }

                notifyCompleteUploadTickets();
            }
        }
        return false;
    }

    public void syncTickets() {
        Log.i(TAG, "syncTickets: ");
        if (!NetworkUtil.getInstance().isConnected()) {
            Log.d(TAG, "syncTickets: no connected network");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                if (uploadCheckedTickets(time)) {
                    Log.i(TAG, "syncTickets: uploadCheckedTickets success");
                    CheckTicketDAO.getInstance().deleteCheckedTicketsBeforeTime(time);
                }

                fetchCurrentParkTickets();
            }
        }).start();
    }

    public void setCurrentParkId(String parkId) {
        this.mCurrentParkId = parkId;
    }

    public ArrayList<Park> getParks() {
        return parks;
    }

    public ArrayList<String> getParkTickets() {
        return parkTickets;
    }

    public enum MessageType {
        PARKS_DATA_CHANGED,
        START_UPLOAD_TICKETS,
        COMPLETE_UPLOAD_TICKETS,
        START_FETCH_TICKETS,
        COMPLETE_FETCH_TICKETS
    }

    public static class MessageBundle {
        private MessageType type;
        private Object entity;

        public MessageBundle(MessageType type, Object entity) {
            this.type = type;
            this.entity = entity;
        }

        public MessageType getType() {
            return type;
        }

        public Object getEntity() {
            return entity;
        }
    }


    private void notifyFetchParksSuccess(ArrayList<Park> parks) {
        setChanged();
        notifyObservers(new MessageBundle(MessageType.PARKS_DATA_CHANGED, parks));
    }

    private void notifyStartUploadTickets() {
        setChanged();
        notifyObservers(new MessageBundle(MessageType.START_UPLOAD_TICKETS, null));

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(CheckTicketApplication.sApplicationContext)
                        .setSmallIcon(R.drawable.cloud_upload)
                        .setContentTitle("同步验票数据")
                        .setContentText("上传中...")
                        .setColor(0xff56abe4)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) CheckTicketApplication.sApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(UPLOAD_NOTIFICATION_ID, mBuilder.build());
    }

    private void notifyCompleteUploadTickets() {
        Log.i(TAG, "notifyCompleteUploadTickets: ");

        setChanged();
        notifyObservers(new MessageBundle(MessageType.COMPLETE_UPLOAD_TICKETS, null));

        // TODO: 2016/5/16 should notify the real result, really Success or Fail
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(CheckTicketApplication.sApplicationContext)
                        .setSmallIcon(R.drawable.cloud_upload)
                        .setContentTitle("同步验票数据")
                        .setContentText("上传完成")
                        .setColor(0xff56abe4)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) CheckTicketApplication.sApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(UPLOAD_NOTIFICATION_ID, mBuilder.build());
    }

    private void notifyStartFetchTickets() {
        setChanged();
        notifyObservers(new MessageBundle(MessageType.START_FETCH_TICKETS, null));

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(CheckTicketApplication.sApplicationContext)
                        .setSmallIcon(R.drawable.cloud_download)
                        .setContentTitle("同步验票数据")
                        .setContentText("下载中...")
                        .setColor(0xff56abe4)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) CheckTicketApplication.sApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(DOWNLOAD_NOTIFICATION_ID, mBuilder.build());
    }

    private void notifyCompleteFetchTickets() {
        setChanged();
        notifyObservers(new MessageBundle(MessageType.COMPLETE_FETCH_TICKETS, null));

        // TODO: 2016/5/16 should notify the real result, really Success or Fail
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(CheckTicketApplication.sApplicationContext)
                        .setSmallIcon(R.drawable.cloud_download)
                        .setContentTitle("同步验票数据")
                        .setContentText("下载完成")
                        .setColor(0xff56abe4)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) CheckTicketApplication.sApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(DOWNLOAD_NOTIFICATION_ID, mBuilder.build());
    }
}
