package com.hbouzidi.fiveprayers.ui.quran.surahs;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.quran.dto.Surah;
import com.hbouzidi.fiveprayers.ui.quran.pages.AyahsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuranActivity extends AppCompatActivity {

    private RecyclerView surahRecyclerView;
    private LinearLayout progressBarLinearLayout;

    private TextView downloadTextView;
    private TextView unzippingTextView;
    private TextView errorTextView;

    private final static int QURAN_PAGES_COUNT = 604;
    private BroadcastReceiver onDownloadComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran);

        surahRecyclerView = findViewById(R.id.surah_recycler_view);
        progressBarLinearLayout = findViewById(R.id.progress_bar_linear_layout);
        downloadTextView = findViewById(R.id.download_text_view);
        unzippingTextView = findViewById(R.id.unzipping_text_view);
        errorTextView = findViewById(R.id.error_text_view);

        initToolbar();
        initNightModeSwitchButton();

        QuranViewModel quranViewModel = new ViewModelProvider(this).get(QuranViewModel.class);
        quranViewModel.getSurahs().observe(this, this::initRecyclerView);

        initContentAndUpdateVisibility(quranViewModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onDownloadComplete != null) {
            unregisterReceiver(onDownloadComplete);
        }
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
        bundle.putParcelableArrayList("SURAHS", (ArrayList<? extends Parcelable>) surahs);

        Intent ayahsAcivity = new Intent(this, AyahsActivity.class);
        ayahsAcivity.putExtra("BUNDLE", bundle);
        startActivity(ayahsAcivity);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.quran_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initNightModeSwitchButton() {
        SwitchCompat nightModeSwitchButton = findViewById(R.id.night_mode_switch);
        nightModeSwitchButton.setChecked(PreferencesHelper.isNightModeActivated(this));

        nightModeSwitchButton.setOnCheckedChangeListener((buttonView, isChecked) -> PreferencesHelper.setNightModeActivated(this, isChecked));
    }

    private void initContentAndUpdateVisibility(QuranViewModel quranViewModel) {
        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath(), BuildConfig.QURAN_IMAGES_FOLDER_NAME);

        if (!file.exists() || file.listFiles().length != QURAN_PAGES_COUNT) {

            progressBarLinearLayout.setVisibility(View.VISIBLE);
            surahRecyclerView.setVisibility(View.INVISIBLE);

            quranViewModel.downloadAnsUnzipQuranImages(getApplicationContext());

            quranViewModel.getmDownloadID().observe(this, this::createAndRegisterDownloadReceiver);
            quranViewModel.getmPercentage().observe(this, percentage -> downloadTextView.setText(getString(R.string.message_download_quran_files_in_progress, percentage)));
            quranViewModel.getmUnzipPercentage().observe(this, percentage -> unzippingTextView.setText(getString(R.string.message_unzip_quran_files_in_progress, percentage, QURAN_PAGES_COUNT)));

            quranViewModel.getmDownloadAndUnzipFinished().observe(this, finished -> {
                if (finished) {
                    progressBarLinearLayout.setVisibility(View.INVISIBLE);
                    surahRecyclerView.setVisibility(View.VISIBLE);
                }
            });

            quranViewModel.getmDownloadError().observe(this, downloadError -> {
                if (downloadError) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(getString(R.string.message_download_quran_files_error));
                }
            });

            quranViewModel.getmUnzipError().observe(this, unzipError -> {
                if (unzipError) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(getString(R.string.message_unzip_quran_files_error));
                }
            });
        }
    }

    private void createAndRegisterDownloadReceiver(long downloadID) {
        onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID == id) {
                    unzippingTextView.setVisibility(View.VISIBLE);
                }
            }
        };

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}