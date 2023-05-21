package com.hbouzidi.fiveprayers.ui.quran.di;

import androidx.lifecycle.ViewModel;

import com.hbouzidi.fiveprayers.di.factory.viewmodel.ViewModelKey;
import com.hbouzidi.fiveprayers.ui.quran.index.QuranIndexViewModel;
import com.hbouzidi.fiveprayers.ui.quran.index.QuranScheduleIndexViewModel;
import com.hbouzidi.fiveprayers.ui.quran.pages.QuranPageViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Module
public abstract class QuranModule {

    @Binds
    @IntoMap
    @ViewModelKey(QuranIndexViewModel.class)
    public abstract ViewModel bindsQuranIndexViewModel(QuranIndexViewModel quranIndexViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(QuranPageViewModel.class)
    public abstract ViewModel bindsQuranPageViewModel(QuranPageViewModel quranPageViewModel);    @Binds

    @IntoMap
    @ViewModelKey(QuranScheduleIndexViewModel.class)
    public abstract ViewModel bindsQuranScheduleIndexViewModel(QuranScheduleIndexViewModel quranScheduleIndexViewModel);
}
