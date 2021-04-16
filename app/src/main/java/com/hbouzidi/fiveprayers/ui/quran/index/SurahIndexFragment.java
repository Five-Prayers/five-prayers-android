package com.hbouzidi.fiveprayers.ui.quran.index;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.quran.dto.Surah;

import java.io.File;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class SurahIndexFragment extends QuranBaseIndexFragment {

    private RecyclerView surahRecyclerView;
    private LinearLayout progressBarLinearLayout;

    private TextView downloadTextView;
    private TextView unzippingTextView;
    private TextView errorTextView;

    private final static int QURAN_PAGES_COUNT = 604;
    private BroadcastReceiver onDownloadComplete;

    public SurahIndexFragment() {
    }

    public static SurahIndexFragment newInstance() {
        SurahIndexFragment fragment = new SurahIndexFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_surah_list, container, false);

        surahRecyclerView = rootView.findViewById(R.id.surah_recycler_view);
        progressBarLinearLayout = rootView.findViewById(R.id.progress_bar_linear_layout);
        downloadTextView = rootView.findViewById(R.id.download_text_view);
        unzippingTextView = rootView.findViewById(R.id.unzipping_text_view);
        errorTextView = rootView.findViewById(R.id.error_text_view);

        QuranIndexViewModel quranIndexViewModel = viewModelFactory.create(QuranIndexViewModel.class);

        quranIndexViewModel.getQuranPages().observe(getViewLifecycleOwner(), quranPages -> this.quranPages = quranPages);
        quranIndexViewModel.getSurahs().observe(getViewLifecycleOwner(), this::initRecyclerView);

        initContentAndUpdateVisibility(quranIndexViewModel);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onDownloadComplete != null) {
            requireActivity().unregisterReceiver(onDownloadComplete);
        }
    }

    private void initRecyclerView(List<Surah> surahs) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        SurahAdapter surahAdapter = new SurahAdapter();
        surahAdapter.setSurahList(surahs);

        surahRecyclerView.setLayoutManager(linearLayoutManager);
        surahRecyclerView.setAdapter(surahAdapter);
        surahRecyclerView.setHasFixedSize(true);

        surahAdapter.setSurahListner(pos -> gotoSuraa(pos, surahs));
    }

    private void initContentAndUpdateVisibility(QuranIndexViewModel quranIndexViewModel) {
        File file = new File(requireContext().getFilesDir().getAbsolutePath(), BuildConfig.QURAN_IMAGES_FOLDER_NAME);

        if (!file.exists() || Objects.requireNonNull(file.listFiles()).length != QURAN_PAGES_COUNT) {
            quranIndexViewModel.downloadAnsUnzipQuranImages(requireContext());

            progressBarLinearLayout.setVisibility(View.VISIBLE);
            surahRecyclerView.setVisibility(View.INVISIBLE);

            quranIndexViewModel.getmDownloadID().observe(getViewLifecycleOwner(), this::createAndRegisterDownloadReceiver);
            quranIndexViewModel.getmPercentage().observe(getViewLifecycleOwner(), percentage -> downloadTextView.setText(getString(R.string.message_download_quran_files_in_progress, percentage)));
            quranIndexViewModel.getmUnzipPercentage().observe(getViewLifecycleOwner(), percentage -> unzippingTextView.setText(getString(R.string.message_unzip_quran_files_in_progress, percentage, QURAN_PAGES_COUNT)));

            quranIndexViewModel.getmDownloadAndUnzipFinished().observe(getViewLifecycleOwner(), finished -> {
                if (finished) {
                    progressBarLinearLayout.setVisibility(View.INVISIBLE);
                    surahRecyclerView.setVisibility(View.VISIBLE);
                }
            });

            quranIndexViewModel.getmDownloadError().observe(getViewLifecycleOwner(), downloadError -> {
                if (downloadError) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(getString(R.string.message_download_quran_files_error));
                }
            });

            quranIndexViewModel.getmUnzipError().observe(getViewLifecycleOwner(), unzipError -> {
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

        requireActivity().registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}