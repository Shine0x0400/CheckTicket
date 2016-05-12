package com.zjl.checkticket;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.zjl.checkticket.db.CheckTicketContract;
import com.zjl.checkticket.db.CheckTicketDAO;
import com.zjl.checkticket.http.CommonCallback;
import com.zjl.checkticket.http.ResponseBodyModel;
import com.zjl.checkticket.http.requests.GetParkTicketsRequest;
import com.zjl.checkticket.http.requests.GetParksRequest;
import com.zjl.checkticket.model.Park;
import com.zjl.checkticket.model.Ticket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

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

    private static volatile TicketDataManager manager;

    private String mCurrentParkId;


    private final ArrayList<Park> parks = new ArrayList<>();


    private final ArrayList<String> parkTickets = new ArrayList<>();


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

                setChanged();
                notifyObservers(new MessageBundle(MessageType.PARKS_DATA_CHANGED, parks));
            }
        });
    }

    public void fetchCurrentParkTickets() {
        // TODO: 2016/5/7 mock
        //yuan qu 1
//        mCurrentParkId = "8f07d2ceeba44dee9de357f6bf92a234";

        if (TextUtils.isEmpty(mCurrentParkId)) {
            return;
        }
        new GetParkTicketsRequest(mCurrentParkId).enqueueRequest(new CommonCallback() {
            @Override
            public void handleSuccess(Call call, Response response) throws IOException {
                // TODO: 2016/5/12 need to check if this response still valid for current park id.

                String bodyString = response.body().string();
                Log.d(TAG, "onResponse: body=" + bodyString);


                final ResponseBodyModel<JSONArray> model = JSON.parseObject(bodyString,
                        new TypeReference<ResponseBodyModel<JSONArray>>() {
                        });

                synchronized (SYNCHRONIZE_LOCK_PARK_TICKETS) {
                    List<String> ticketIds = Arrays.asList(model.getData().toArray(new String[model.getData().size()]));
                    parkTickets.clear();
                    parkTickets.addAll(ticketIds);

                    ArrayList<Ticket> tickets = new ArrayList<Ticket>();
                    for (String id : parkTickets) {
                        // TODO: 2016/5/13 mock
                        Ticket t = new Ticket(id, CheckTicketContract.CheckTicketEntry.VALUE_IS_NOT_CHECKED, System.currentTimeMillis());
                        tickets.add(t);
                    }
                    CheckTicketDAO.getInstance().updateTableWithFreshData(tickets);
                }
            }
        });
    }

    public void uploadCheckedTickets() {

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
        TICKETS_DATA_CHANGED
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
}
