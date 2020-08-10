package com.bouzidi.prayertimes.ui.quran.surahs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.quran.dto.Surah;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.Holder> {

    private SurahListner surahListner;
    private List<Surah> surahs;

    public SurahAdapter() {
        surahs = new ArrayList<>();
    }

    public void setSurahListner(SurahListner surahListner) {
        this.surahListner = surahListner;
    }

    public void setSurahList(List<Surah> newList) {
        surahs = new ArrayList<>(newList);
    }

    public List<Surah> getSurahs() {
        return surahs;
    }

    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.surah_item, viewGroup, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Surah surah = surahs.get(i);

        holder.surahNameTextView.setText(surah.getName());
        holder.surahPageNumberTextView.setText(String.valueOf(surah.getPage()));
        holder.surahNumberTextView.setText(String.valueOf(surah.getNumber()));

        String numberOfAyahText = surah.getNumberOfAyahs() + " " + holder.itemView.getContext().getString(R.string.ayahs);

        holder.surahAyahsNumberTextView.setText(numberOfAyahText);
        holder.surahRevelationTextView.setText(
                surah.getRevelationType().equals("Meccan") ?
                        holder.itemView.getContext().getString(R.string.sura_rev_mackkia) :
                        holder.itemView.getContext().getString(R.string.sura_rev_madniaa)
        );
    }

    @Override
    public int getItemCount() {
        return surahs.size();
    }

    interface SurahListner {
        void onSura(int pos);
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView surahNumberTextView;
        TextView surahNameTextView;
        TextView surahRevelationTextView;
        TextView surahAyahsNumberTextView;
        TextView surahPageNumberTextView;

        public Holder(@NonNull View itemView) {
            super(itemView);

            surahNumberTextView = itemView.findViewById(R.id.surah_number_text_view);
            surahNameTextView = itemView.findViewById(R.id.surah_name_text_view);
            surahRevelationTextView = itemView.findViewById(R.id.surah_revelation_text_view);
            surahAyahsNumberTextView = itemView.findViewById(R.id.surah_ayahs_num_text_view);
            surahPageNumberTextView = itemView.findViewById(R.id.surah_page_num_text_view);

            itemView.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                surahListner.onSura(surahs.get(pos).getPage());
            });
        }
    }
}