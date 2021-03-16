package com.hbouzidi.fiveprayers.ui.quran.index;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranIndexActivity extends AppCompatActivity {

    private final int[] titles = {R.string.surat, R.string.bookmarks};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran);
        QuranIndexPagerAdapter quranIndexPagerAdapter = new QuranIndexPagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(quranIndexPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getString(titles[position]))
        ).attach();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());

        initNightModeSwitchButton();
    }

    private void initNightModeSwitchButton() {
        SwitchCompat nightModeSwitchButton = findViewById(R.id.night_mode_switch);
        nightModeSwitchButton.setChecked(PreferencesHelper.isNightModeActivated(getApplicationContext()));

        nightModeSwitchButton.setOnCheckedChangeListener((buttonView, isChecked) -> PreferencesHelper.setNightModeActivated(getApplicationContext(), isChecked));
    }
}