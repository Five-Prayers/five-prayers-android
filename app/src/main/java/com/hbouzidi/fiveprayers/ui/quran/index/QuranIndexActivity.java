package com.hbouzidi.fiveprayers.ui.quran.index;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.ui.BaseActivity;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranIndexActivity extends BaseActivity {

    @Inject
    PreferencesHelper preferencesHelper;

    private final int[] titles = {R.string.surat, R.string.quran_daily_schedule_desc, R.string.bookmarks};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((FivePrayerApplication) getApplicationContext())
                .appComponent
                .quranComponent()
                .create()
                .inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran);
        QuranIndexPagerAdapter quranIndexPagerAdapter = new QuranIndexPagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(quranIndexPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tab_color_selector));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getString(titles[position]))
        ).attach();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("tab_to_open", 0);
            viewPager.setCurrentItem(value, false);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());

        initNightModeSwitchButton();
    }

    private void initNightModeSwitchButton() {
        SwitchCompat nightModeSwitchButton = findViewById(R.id.night_mode_switch);
        nightModeSwitchButton.setChecked(preferencesHelper.isNightModeActivated());

        nightModeSwitchButton.setOnCheckedChangeListener((buttonView, isChecked) -> preferencesHelper.setNightModeActivated(isChecked));
    }
}