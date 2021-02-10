package com.hbouzidi.fiveprayers.ui.quran.surahs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Objects;

public class SectionsPagerAdapter extends FragmentStateAdapter {

    public SectionsPagerAdapter(@NonNull FragmentManager fragmentManager,
                                @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = SurahListFragment.newInstance();
                break;
            case 1:
                fragment = BookmarksFragment.newInstance();
                break;
        }
        return Objects.requireNonNull(fragment);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
