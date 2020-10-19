package com.hbouzidi.fiveprayers.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.R;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    ArrayList<DashboardModel> dashboardModelArrayList;
    private Activity activity;

    public DashboardAdapter(ArrayList<DashboardModel> dashboardModelArrayList, Activity activity) {
        this.dashboardModelArrayList = dashboardModelArrayList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String ret_head = dashboardModelArrayList.get(position).getHead();
        holder.setheader(ret_head);

        String ret_sub = dashboardModelArrayList.get(position).getSub();
        holder.set_sub(ret_sub);

        int ret_image = dashboardModelArrayList.get(position).getImage();
        holder.set_image(ret_image);

        Intent intent = dashboardModelArrayList.get(position).getIntent();
        if (intent != null) {
            holder.setOnclickListener(intent);
        }
    }

    @Override
    public int getItemCount() {
        return dashboardModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView header, sub_header;
        ImageView images;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void setheader(String h) {
            header = itemView.findViewById(R.id.header);
            header.setText(h);
        }

        public void set_sub(String s) {
            sub_header = itemView.findViewById(R.id.sub_header);
            sub_header.setText(s);
        }

        public void set_image(int i) {
            images = itemView.findViewById(R.id.dash_image);
            images.setImageResource(i);
        }

        public void setOnclickListener(Intent targetIntent) {
            itemView.setOnClickListener(v -> activity.startActivity(targetIntent));
        }
    }
}
