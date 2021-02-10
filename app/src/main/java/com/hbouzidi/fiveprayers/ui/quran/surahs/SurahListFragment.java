package com.hbouzidi.fiveprayers.ui.quran.surahs;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.quran.dto.Surah;
import com.hbouzidi.fiveprayers.ui.quran.pages.AyahsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SurahListFragment extends Fragment {

    private RecyclerView surahRecyclerView;
    private LinearLayout progressBarLinearLayout;

    private TextView downloadTextView;
    private TextView unzippingTextView;
    private TextView errorTextView;

    private final static int QURAN_PAGES_COUNT = 604;
    private BroadcastReceiver onDownloadComplete;

    public SurahListFragment() {
    }

    public static SurahListFragment newInstance() {
        SurahListFragment fragment = new SurahListFragment();
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

        QuranViewModel quranViewModel = new ViewModelProvider(this).get(QuranViewModel.class);
        quranViewModel.getSurahs().observe(getViewLifecycleOwner(), this::initRecyclerView);

        initContentAndUpdateVisibility(quranViewModel);

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

    private void gotoSuraa(int pageNumber, List<Surah> surahs) {
        Bundle bundle = new Bundle();
        bundle.putInt("PAGE_NUMBER", pageNumber);
        bundle.putParcelableArrayList("SURAHS", (ArrayList<? extends Parcelable>) surahs);

        Intent ayahsAcivity = new Intent(requireContext(), AyahsActivity.class);
        ayahsAcivity.putExtra("BUNDLE", bundle);
        startActivity(ayahsAcivity);
    }

    private void initContentAndUpdateVisibility(QuranViewModel quranViewModel) {
        File file = new File(requireContext().getFilesDir().getAbsolutePath(), BuildConfig.QURAN_IMAGES_FOLDER_NAME);

        if (!file.exists() || Objects.requireNonNull(file.listFiles()).length != QURAN_PAGES_COUNT) {
            quranViewModel.downloadAnsUnzipQuranImages(requireContext());

            progressBarLinearLayout.setVisibility(View.VISIBLE);
            surahRecyclerView.setVisibility(View.INVISIBLE);

            quranViewModel.getmDownloadID().observe(getViewLifecycleOwner(), this::createAndRegisterDownloadReceiver);
            quranViewModel.getmPercentage().observe(getViewLifecycleOwner(), percentage -> downloadTextView.setText(getString(R.string.message_download_quran_files_in_progress, percentage)));
            quranViewModel.getmUnzipPercentage().observe(getViewLifecycleOwner(), percentage -> unzippingTextView.setText(getString(R.string.message_unzip_quran_files_in_progress, percentage, QURAN_PAGES_COUNT)));

            quranViewModel.getmDownloadAndUnzipFinished().observe(getViewLifecycleOwner(), finished -> {
                if (finished) {
                    progressBarLinearLayout.setVisibility(View.INVISIBLE);
                    surahRecyclerView.setVisibility(View.VISIBLE);
                }
            });

            quranViewModel.getmDownloadError().observe(getViewLifecycleOwner(), downloadError -> {
                if (downloadError) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(getString(R.string.message_download_quran_files_error));
                }
            });

            quranViewModel.getmUnzipError().observe(getViewLifecycleOwner(), unzipError -> {
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