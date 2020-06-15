package com.bouzidi.prayertimes.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bouzidi.prayertimes.R;

import java.util.ArrayList;

public class DashAdapter extends RecyclerView.Adapter<DashAdapter.ViewHolder> {

    ArrayList<DashModel> dashModelArrayList;

    public DashAdapter(ArrayList<DashModel> dashModelArrayList) {
        this.dashModelArrayList = dashModelArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_list_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String ret_head = dashModelArrayList.get(position).getHead();
        holder.setheader(ret_head);

        String ret_sub = dashModelArrayList.get(position).getSub();
        holder.set_sub(ret_sub);

        int ret_image = dashModelArrayList.get(position).getImage();
        holder.set_image(ret_image);

    }

    @Override
    public int getItemCount() {
        return dashModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView header,sub_header;
        ImageView images;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setheader(String h)
        {
            header = myView.findViewById(R.id.header);
            header.setText(h);
        }

        public void set_sub(String s)
        {
            sub_header = myView.findViewById(R.id.sub_header);
            sub_header.setText(s);
        }
        public void set_image(int i)
        {
            images = myView.findViewById(R.id.dash_image);
            images.setImageResource(i);
        }


    }
}
