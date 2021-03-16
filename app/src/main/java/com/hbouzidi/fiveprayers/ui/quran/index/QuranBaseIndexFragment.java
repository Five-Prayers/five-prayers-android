package com.hbouzidi.fiveprayers.ui.quran.index;

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
import com.hbouzidi.fiveprayers.ui.quran.pages.QuranPageActivity;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranBaseIndexFragment extends Fragment {

    protected List<Surah> surahs;
    protected List<QuranPage> quranPages;
    private final static int AUTOMATIC_BOOKMARK_MAX_SIZE = 3;

    protected void gotoSuraa(int pageNumber, List<Surah> surahs) {
        Bundle bundle = new Bundle();
        bundle.putInt("PAGE_NUMBER", pageNumber);
        bundle.putParcelableArrayList("SURAHS", (ArrayList<? extends Parcelable>) surahs);

        Intent ayahsAcivity = new Intent(requireContext(), QuranPageActivity.class);
        ayahsAcivity.putExtra("BUNDLE", bundle);

        startActivityForResult(ayahsAcivity, QuranPageActivity.AYAH_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (QuranPageActivity.AYAH_ACTIVITY_REQUEST_CODE): {
                if (resultCode == Activity.RESULT_OK) {
                    int lastpageShown = data.getIntExtra(QuranPageActivity.LAST_PAGE_SHOWN_IDENTIFIER, 0);

                    if (lastpageShown != 0) {
                        saveAutomaticBookmark(quranPages.get(lastpageShown - 1));
                    }
                }
                break;
            }
        }
    }

    private void saveAutomaticBookmark(QuranPage quranPage) {
        long timestamps = ZonedDateTime.now(ZoneOffset.systemDefault()).toEpochSecond();
        QuranBookmark newAutomaticBookmark = new QuranBookmark(timestamps, BookmarkType.AUTOMATIC, quranPage);

        List<QuranBookmark> oldAutomaticBenchmarkList = PreferencesHelper.getSortedAutomaticBookmarkList(requireContext());

        oldAutomaticBenchmarkList.removeIf(item -> item.getQuranPage().getPageNum() == quranPage.getPageNum());

        if (oldAutomaticBenchmarkList.size() == AUTOMATIC_BOOKMARK_MAX_SIZE) {
            oldAutomaticBenchmarkList.remove(oldAutomaticBenchmarkList.size() - 1);
        }

        oldAutomaticBenchmarkList.add(newAutomaticBookmark);
        PreferencesHelper.saveAutomaticBookmarkList(oldAutomaticBenchmarkList, requireContext());
    }
}
