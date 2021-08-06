package com.hbouzidi.fiveprayers.ui.quran.index;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
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

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranBaseIndexFragment extends Fragment {

    protected List<Surah> surahs;
    protected List<QuranPage> quranPages;
    private final static int AUTOMATIC_BOOKMARK_MAX_SIZE = 3;

    private ActivityResultLauncher<Intent> intentActivityResultLauncher;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::activityResultCallback);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        ((FivePrayerApplication) context.getApplicationContext())
                .appComponent
                .quranComponent()
                .create()
                .inject(this);

        super.onAttach(context);
    }

    protected void gotoSuraa(int pageNumber, List<Surah> surahs) {
        Bundle bundle = new Bundle();
        bundle.putInt("PAGE_NUMBER", pageNumber);
        bundle.putParcelableArrayList("SURAHS", (ArrayList<? extends Parcelable>) surahs);

        Intent ayahsAcivity = new Intent(requireContext(), QuranPageActivity.class);
        ayahsAcivity.putExtra("BUNDLE", bundle);

        intentActivityResultLauncher.launch(ayahsAcivity);
    }

    private void saveAutomaticBookmark(QuranPage quranPage) {
        long timestamps = ZonedDateTime.now(ZoneOffset.systemDefault()).toEpochSecond();
        QuranBookmark newAutomaticBookmark = new QuranBookmark(timestamps, BookmarkType.AUTOMATIC, quranPage);

        List<QuranBookmark> oldAutomaticBenchmarkList = preferencesHelper.getSortedAutomaticBookmarkList();

        oldAutomaticBenchmarkList.removeIf(item -> item.getQuranPage().getPageNum() == quranPage.getPageNum());

        if (oldAutomaticBenchmarkList.size() == AUTOMATIC_BOOKMARK_MAX_SIZE) {
            oldAutomaticBenchmarkList.remove(oldAutomaticBenchmarkList.size() - 1);
        }

        oldAutomaticBenchmarkList.add(newAutomaticBookmark);
        preferencesHelper.saveAutomaticBookmarkList(oldAutomaticBenchmarkList);
    }

    private void activityResultCallback(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            int lastpageShown = result.getData().getIntExtra(QuranPageActivity.LAST_PAGE_SHOWN_IDENTIFIER, 0);

            if (lastpageShown != 0) {
                saveAutomaticBookmark(quranPages.get(lastpageShown - 1));
            }
        }
    }
}
