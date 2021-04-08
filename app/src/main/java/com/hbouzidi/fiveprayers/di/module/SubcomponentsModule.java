package com.hbouzidi.fiveprayers.di.module;


import com.hbouzidi.fiveprayers.ui.calendar.di.CalendarComponent;
import com.hbouzidi.fiveprayers.ui.home.di.HomeComponent;
import com.hbouzidi.fiveprayers.ui.qibla.di.QiblaComponent;
import com.hbouzidi.fiveprayers.ui.quran.di.QuranComponent;
import com.hbouzidi.fiveprayers.ui.quran.index.QuranIndexViewModel;
import com.hbouzidi.fiveprayers.ui.timingtable.di.TimingTableComponent;

import dagger.Module;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Module(subcomponents = {
        HomeComponent.class,
        TimingTableComponent.class,
        QiblaComponent.class,
        CalendarComponent.class,
        QuranComponent.class
})
public class SubcomponentsModule {

}
