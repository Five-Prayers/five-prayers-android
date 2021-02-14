package com.hbouzidi.fiveprayers.ui.quran.surahs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;

import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.quran.dto.BookmarkType;
import com.hbouzidi.fiveprayers.quran.dto.QuranBookmark;
import com.hbouzidi.fiveprayers.quran.dto.QuranPage;
import com.hbouzidi.fiveprayers.quran.dto.Surah;
import com.hbouzidi.fiveprayers.ui.quran.pages.AyahsActivity;
import com.hbouzidi.fiveprayers.utils.TimingUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuranBaseIndexFragment extends Fragment {

    protected List<Surah> surahs;
    protected List<QuranPage> quranPages;
    private final static int AUTOMATIC_BOOKMARK_MAX_SIZE = 3;

    protected void gotoSuraa(int pageNumber, List<Surah> surahs) {
        Bundle bundle = new Bundle();
        bundle.putInt("PAGE_NUMBER", pageNumber);
        bundle.putParcelableArrayList("SURAHS", (ArrayList<? extends Parcelable>) surahs);

        Intent ayahsAcivity = new Intent(requireContext(), AyahsActivity.class);
        ayahsAcivity.putExtra("BUNDLE", bundle);

        startActivityForResult(ayahsAcivity, AyahsActivity.AYAH_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (AyahsActivity.AYAH_ACTIVITY_REQUEST_CODE): {
                if (resultCode == Activity.RESULT_OK) {
                    int lastpageShown = data.getIntExtra(AyahsActivity.LAST_PAGE_SHOWN_IDENTIFIER, 0);

                    if (lastpageShown != 0) {
                        saveAutomaticBookmark(quranPages.get(lastpageShown - 1));
                    }
                }
                break;
            }
        }
    }

    private void saveAutomaticBookmark(QuranPage quranPage) {
        long timeInMilli = TimingUtils.getTimeInMilliIgnoringSeconds(LocalDateTime.now());
        QuranBookmark newAutomaticBookmark = new QuranBookmark(timeInMilli, BookmarkType.AUTOMATIC, quranPage);

        List<QuranBookmark> oldAutomaticBenchmarkList = PreferencesHelper.getSortedAutomaticBookmarkList(requireContext());

        oldAutomaticBenchmarkList.removeIf(item -> item.getQuranPage().getPageNum() == quranPage.getPageNum());

        if (oldAutomaticBenchmarkList.size() == AUTOMATIC_BOOKMARK_MAX_SIZE) {
            oldAutomaticBenchmarkList.remove(oldAutomaticBenchmarkList.size() - 1);
        }

        oldAutomaticBenchmarkList.add(newAutomaticBookmark);
        PreferencesHelper.saveAutomaticBookmarkList(oldAutomaticBenchmarkList, requireContext());
    }
}
