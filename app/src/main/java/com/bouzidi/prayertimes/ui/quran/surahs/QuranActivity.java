package com.bouzidi.prayertimes.ui.quran.surahs;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.preferences.PreferencesHelper;
import com.bouzidi.prayertimes.quran.dto.Surah;
import com.bouzidi.prayertimes.ui.quran.pages.AyahsActivity;

import java.io.Serializable;
import java.util.List;

public class QuranActivity extends AppCompatActivity {

    private RecyclerView surahRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran);

        surahRecyclerView = findViewById(R.id.surah_recycler_view);

        initToolbar();
        initNightModeSwitchButton();

        QuranViewModel quranViewModel = new ViewModelProvider(this).get(QuranViewModel.class);
        quranViewModel.getSurahs().observe(this, this::initRecyclerView);
    }

    private void initRecyclerView(List<Surah> surahs) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        SurahAdapter surahAdapter = new SurahAdapter();
        surahAdapter.setSurahList(surahs);

        surahRecyclerView.setLayoutManager(linearLayoutManager);
        surahRecyclerView.setAdapter(surahAdapter);
        surahRecyclerView.setHasFixedSize(true);

        surahAdapter.setSurahListner(pos -> gotoSuraa(pos, surahs));
    }

    private void gotoSuraa(int pageNumber, List<Surah> surahs) {
        Bundle bundle = new Bundle();
        bundle.putInt("PAGE_NUMBER", pageNumber);
        bundle.putSerializable("SURAHS", (Serializable) surahs);

        Intent openAcivity = new Intent(this, AyahsActivity.class);
        openAcivity.putExtra("BUNDLE", bundle);
        startActivity(openAcivity);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.quran_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initNightModeSwitchButton() {
        SwitchCompat nightModeSwitchButton = findViewById(R.id.night_mode_switch);
        nightModeSwitchButton.setChecked(PreferencesHelper.isNightModeActivated(this));

        nightModeSwitchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferencesHelper.setNightModeActivated(this, isChecked);
        });
    }
}