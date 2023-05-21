package com.hbouzidi.fiveprayers.di.component;

import com.hbouzidi.fiveprayers.di.module.AppModule;
import com.hbouzidi.fiveprayers.di.module.NetworkModule;
import com.hbouzidi.fiveprayers.ui.quran.index.BookmarkListAdapter;
import com.hbouzidi.fiveprayers.ui.quran.index.SurahAdapter;
import com.hbouzidi.fiveprayers.ui.quran.pages.PageAdapter;
import com.hbouzidi.fiveprayers.ui.quran.index.ScheduleListAdapter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
@Component(modules =
        {
                AppModule.class,
                NetworkModule.class
        })
public interface AdapterComponent {

    void inject(PageAdapter PageAdapter);

    void inject(BookmarkListAdapter bookmarkListAdapter);

    void inject(SurahAdapter surahAdapter);

    void inject(ScheduleListAdapter scheduleListAdapter);
}
