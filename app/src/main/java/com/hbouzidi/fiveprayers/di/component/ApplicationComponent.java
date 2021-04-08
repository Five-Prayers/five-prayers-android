package com.hbouzidi.fiveprayers.di.component;

import com.hbouzidi.fiveprayers.di.factory.worker.WorkerProviderFactory;
import com.hbouzidi.fiveprayers.di.module.AppModule;
import com.hbouzidi.fiveprayers.di.module.NetworkModule;
import com.hbouzidi.fiveprayers.di.module.SubcomponentsModule;
import com.hbouzidi.fiveprayers.di.module.ViewModelFactoryModule;
import com.hbouzidi.fiveprayers.di.module.WorkerBindingModule;
import com.hbouzidi.fiveprayers.ui.calendar.di.CalendarComponent;
import com.hbouzidi.fiveprayers.ui.home.di.HomeComponent;
import com.hbouzidi.fiveprayers.ui.qibla.di.QiblaComponent;
import com.hbouzidi.fiveprayers.ui.quran.di.QuranComponent;
import com.hbouzidi.fiveprayers.ui.settings.di.SettingsComponent;
import com.hbouzidi.fiveprayers.ui.timingtable.di.TimingTableComponent;

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
                NetworkModule.class,
                ViewModelFactoryModule.class,
                WorkerBindingModule.class,
                SubcomponentsModule.class
        })
public interface ApplicationComponent {

    HomeComponent.Factory homeComponent();

    QiblaComponent.Factory qiblaComponent();

    TimingTableComponent.Factory timingTableComponent();

    CalendarComponent.Factory calendarComponent();

    QuranComponent.Factory quranComponent();

    SettingsComponent.Factory settingsComponent();

    WorkerProviderFactory workerProviderFactory();
}
