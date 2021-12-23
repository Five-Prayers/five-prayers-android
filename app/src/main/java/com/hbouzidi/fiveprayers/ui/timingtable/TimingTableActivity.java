package com.hbouzidi.fiveprayers.ui.timingtable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.ui.BaseActivity;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class TimingTableActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_table);
        TimingTablePagerAdapter timingTableIndexPagerAdapter = new TimingTablePagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(timingTableIndexPagerAdapter);

        String[] titles = {StringUtils.capitalize(UiUtils.formatShortDate(LocalDate.now())),
                StringUtils.capitalize(UiUtils.formatShortDate(LocalDate.now().plusMonths(1)))};

        TabLayout tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(titles[position])
        ).attach();

        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);

        String lastKnownLocality = sharedPreferences.getString(PreferencesConstants.LAST_KNOWN_LOCALITY, "");
        String toolBarTitle;

        if (!lastKnownLocality.isEmpty()) {
            toolBarTitle = getString(R.string.calendar_view_title) + " " + sharedPreferences.getString(PreferencesConstants.LAST_KNOWN_LOCALITY, "");
        } else {
            toolBarTitle = getString(R.string.title_calendar);
        }

        ((TextView) findViewById(R.id.timing_table_toolbar_title)).setText(toolBarTitle);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());
    }
}