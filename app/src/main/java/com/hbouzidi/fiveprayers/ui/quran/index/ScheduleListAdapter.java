package com.hbouzidi.fiveprayers.ui.quran.index;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.database.ReadingScheduleRegistry;
import com.hbouzidi.fiveprayers.quran.dto.Surah;
import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingSchedule;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {

    private List<ReadingSchedule> readingSchedules;
    private List<Surah> surahs;

    @Inject
    ReadingScheduleRegistry readingScheduleRegistry;
    private ScheduleListener scheduleListener;

    public ScheduleListAdapter(List<ReadingSchedule> readingSchedules, List<Surah> surahs) {
        this.readingSchedules = readingSchedules;
        this.surahs = surahs;
    }

    public void setScheduleListener(ScheduleListAdapter.ScheduleListener scheduleListener) {
        this.scheduleListener = scheduleListener;
    }

    @NonNull
    @Override
    public ScheduleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ((FivePrayerApplication) parent.getContext().getApplicationContext())
                .adapterComponent
                .inject(this);

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull ScheduleListAdapter.ViewHolder holder, int position) {
        final ReadingSchedule scheduleItem = readingSchedules.get(position);

        int startSurahNumber = scheduleItem.getStartSurahNumber();
        int startAyahNumber = scheduleItem.getStartAyahNumber();
        int dayNumber = scheduleItem.getDayNumber();

        holder.readingFromSurahNameTextView.setText(surahs.get(startSurahNumber - 1).getName());
        holder.readingFromAyahNumberTextView.setText(Integer.toString(startAyahNumber));
        holder.readingFromQuarterNumberTextView.setText(Integer.toString(scheduleItem.getStartQuarter()));
        holder.readingToQuarterNumberTextView.setText(Integer.toString(scheduleItem.getEndQuarter()));
        holder.numberTextView.setText(Integer.toString(dayNumber));
        holder.status.setChecked(scheduleItem.getStatus() == 1);


        holder.status.setOnClickListener(view -> readingScheduleRegistry.updateScheduleStatus(dayNumber, holder.status.isChecked() ? 1 : 0));
    }

    @Override
    public int getItemCount() {
        return readingSchedules.size();
    }

    interface ScheduleListener {
        void onSura(int pos, int startPageNumber, int endPageNumber);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView readingFromSurahNameTextView;
        public TextView readingFromAyahNumberTextView;
        public TextView readingFromQuarterNumberTextView;
        public TextView readingToQuarterNumberTextView;
        public TextView numberTextView;

        public CheckBox status;

        public ViewHolder(View itemView) {
            super(itemView);
            this.readingFromSurahNameTextView = itemView.findViewById(R.id.reading_from_surah_name_text_view);
            this.readingFromAyahNumberTextView = itemView.findViewById(R.id.reading_from_ayah_number_text_view);
            this.readingFromQuarterNumberTextView = itemView.findViewById(R.id.reading_from_quarter_number_text_view);
            this.readingToQuarterNumberTextView = itemView.findViewById(R.id.reading_to_quarter_number_text_view);
            this.numberTextView = itemView.findViewById(R.id.number_text_view);
            this.status = itemView.findViewById(R.id.status_checkbox);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                scheduleListener.onSura(1, readingSchedules.get(pos).getStartPage(), readingSchedules.get(pos).getEndPage());
            });
        }
    }
}
