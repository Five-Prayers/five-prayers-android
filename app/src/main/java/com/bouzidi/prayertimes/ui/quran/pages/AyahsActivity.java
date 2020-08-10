package com.bouzidi.prayertimes.ui.quran.pages;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.preferences.PreferencesHelper;
import com.bouzidi.prayertimes.quran.dto.Page;
import com.bouzidi.prayertimes.quran.dto.Surah;

import java.util.List;

public class AyahsActivity extends AppCompatActivity {

    private RecyclerView PagesRecyclerView;

    private int pageToDisplay = 1;
    private int textColor, backgroundColor;
    private int lastpageShown = 1;

    private List<Surah> surahs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayahs);

        PagesRecyclerView = findViewById(R.id.pages_recycler_view);

        Bundle bundle = getIntent().getBundleExtra("BUNDLE");

        if (bundle != null) {
            pageToDisplay = bundle.getInt("PAGE_NUMBER", 1);
            surahs = (List<Surah>) bundle.getSerializable("SURAHS");
        }

        AyahsViewModel ayahsViewModel = new ViewModelProvider(this).get(AyahsViewModel.class);
        ayahsViewModel.getPages().observe(this, this::initRecyclerView);
    }

    private void initRecyclerView(List<Page> pages) {
        prepareColors();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);

        PagesRecyclerView.setLayoutManager(manager);
        PagesRecyclerView.setHasFixedSize(true);
        PageAdapter pageAdapter = new PageAdapter(textColor, backgroundColor);
        pageAdapter.setQuranPages(pages);
        pageAdapter.setSurahs(surahs);
        PagesRecyclerView.setAdapter(pageAdapter);
        PagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        PagesRecyclerView.scrollToPosition(pageToDisplay - 1);
        new PagerSnapHelper().attachToRecyclerView(PagesRecyclerView);

        pageAdapter.setPageShown((pos, holder) -> lastpageShown = pos + 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            PagesRecyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    private void prepareColors() {
        if (PreferencesHelper.isNightModeActivated(this)) {
            textColor = (getResources().getColor(R.color.white));
            backgroundColor = (getResources().getColor(R.color.mine_shaft));
        } else {
            textColor = (getResources().getColor(R.color.mine_shaft));
            backgroundColor = (getResources().getColor(R.color.scotch_mist));
        }
    }
}
