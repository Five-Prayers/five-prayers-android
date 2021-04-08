package com.hbouzidi.fiveprayers.ui.quran.di;

import com.hbouzidi.fiveprayers.ui.quran.index.BookmarkIndexFragment;
import com.hbouzidi.fiveprayers.ui.quran.index.SurahIndexFragment;
import com.hbouzidi.fiveprayers.ui.quran.pages.QuranPageActivity;

import dagger.Subcomponent;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Subcomponent(modules = {QuranModule.class})
public interface QuranComponent {

    @Subcomponent.Factory
    interface Factory {
        QuranComponent create();
    }

    void inject(QuranPageActivity quranPageActivity);

    void inject(BookmarkIndexFragment bookmarkIndexFragment);

    void inject(SurahIndexFragment surahIndexFragment);
}
