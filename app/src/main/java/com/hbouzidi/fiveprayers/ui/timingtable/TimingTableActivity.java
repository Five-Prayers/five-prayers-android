package com.hbouzidi.fiveprayers.ui.timingtable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class TimingTableActivity extends AppCompatActivity {

    private final String[] titles = {StringUtils.capitalize(UiUtils.formatShortDate(LocalDate.now())),
            StringUtils.capitalize(UiUtils.formatShortDate(LocalDate.now().plusMonths(1)))};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_table);
        TimingTablePagerAdapter timingTableIndexPagerAdapter = new TimingTablePagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(timingTableIndexPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(titles[position])
        ).attach();

        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);
        String toolBarTitle = getString(R.string.calendar_view_title) + " " + sharedPreferences.getString(PreferencesConstants.LAST_KNOWN_LOCALITY, "");
        ((TextView) findViewById(R.id.timing_table_toolbar_title)).setText(toolBarTitle);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());
    }
}