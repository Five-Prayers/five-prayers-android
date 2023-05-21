package com.hbouzidi.fiveprayers.ui.quran.index;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Objects;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranIndexPagerAdapter extends FragmentStateAdapter {

    public QuranIndexPagerAdapter(@NonNull FragmentManager fragmentManager,
                                  @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = SurahIndexFragment.newInstance();
                break;
            case 1:
                fragment = QuranScheduleIndexFragment.newInstance();
                break;
            case 2:
                fragment = BookmarkIndexFragment.newInstance();
                break;
        }
        return Objects.requireNonNull(fragment);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
