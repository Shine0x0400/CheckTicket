package com.zjl.checkticket.check;

import com.zjl.checkticket.R;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zjl on 16/5/13.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<HistoryModel> historyRecords = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        int passColor = 0xff070707;
        int failColor = 0xffff0000;

        TextView ticketIdTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ticketIdTv = (TextView) itemView;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryModel item = historyRecords.get(position);
        holder.ticketIdTv.setText(item.getTicketId());
        holder.ticketIdTv.setTextColor(item.isPassed() ? holder.passColor : holder.failColor);
    }

    @Override
    public int getItemCount() {
        return historyRecords.size();
    }

    public void setHistoryRecords(ArrayList<HistoryModel> historyRecords) {
        this.historyRecords = historyRecords;
    }
}
