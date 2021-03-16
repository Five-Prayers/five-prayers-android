package com.hbouzidi.fiveprayers.ui.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.R;

import java.util.List;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {

    private List<String> holidays;

    public static class HolidayViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public HolidayViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.item_holiday_text_view);
        }
    }

    public HolidayAdapter(List<String> holidays) {
        this.holidays = holidays;
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holiday_item_view, parent, false);

        return new HolidayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        if (getItemCount() == 0) {
            holder.textView.setVisibility(View.INVISIBLE);
        } else {
            holder.textView.setText(holidays.get(position));
            holder.textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return holidays.size();
    }
}
