package com.hbouzidi.fiveprayers.ui.quran.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.quran.dto.QuranPage;
import com.hbouzidi.fiveprayers.quran.dto.Surah;
import com.hbouzidi.fiveprayers.ui.BaseActivity;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranPageActivity extends BaseActivity {

    public static final String LAST_PAGE_SHOWN_IDENTIFIER = "LAST_PAGE_SHOWN_IDENTIFIER";

    private RecyclerView PagesRecyclerView;

    private int pageToDisplay = 1;
    private int textColor, backgroundColor;
    private int lastpageShown = 1;

    private List<Surah> surahs;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((FivePrayerApplication) getApplicationContext())
                .appComponent
                .quranComponent()
                .create()
                .inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayahs);

        PagesRecyclerView = findViewById(R.id.pages_recycler_view);

        Bundle bundle = getIntent().getBundleExtra("BUNDLE");

        if (bundle != null) {
            pageToDisplay = bundle.getInt("PAGE_NUMBER", 1);
            surahs = bundle.getParcelableArrayList("SURAHS");
        }

        QuranPageViewModel quranPageViewModel = viewModelFactory.create(QuranPageViewModel.class);
        quranPageViewModel.getPages().observe(this, this::initRecyclerView);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initRecyclerView(List<QuranPage> quranPages) {
        prepareColors();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);

        PagesRecyclerView.setLayoutManager(manager);
        PagesRecyclerView.setHasFixedSize(true);
        PageAdapter pageAdapter = new PageAdapter(textColor, backgroundColor);
        pageAdapter.setQuranPages(quranPages);
        pageAdapter.setSurahs(surahs);
        PagesRecyclerView.setAdapter(pageAdapter);
        PagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        PagesRecyclerView.scrollToPosition(pageToDisplay - 1);
        new PagerSnapHelper().attachToRecyclerView(PagesRecyclerView);

        pageAdapter.setPageShown((pos, holder) -> {
            lastpageShown = pos + 1;
            Intent resultIntent = new Intent();

            resultIntent.putExtra(QuranPageActivity.LAST_PAGE_SHOWN_IDENTIFIER, lastpageShown);
            setResult(Activity.RESULT_OK, resultIntent);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            PagesRecyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    private void prepareColors() {
        if (preferencesHelper.isNightModeActivated()) {
            textColor = 255;
            backgroundColor = ResourcesCompat.getColor(getResources(), R.color.mine_shaft, null);
        } else {
            textColor = -255;
            backgroundColor = ResourcesCompat.getColor(getResources(), R.color.scotch_mist, null);
        }
    }
}
