package com.hbouzidi.fiveprayers.di.component;

import com.hbouzidi.fiveprayers.di.module.AppModule;
import com.hbouzidi.fiveprayers.di.module.NetworkModule;
import com.hbouzidi.fiveprayers.di.module.WidgetModule;
import com.hbouzidi.fiveprayers.ui.widget.HomeScreenWidgetProvider;
import com.hbouzidi.fiveprayers.ui.widget.NextPrayerHomeScreenWidgetProvider;

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
                WidgetModule.class,
                NetworkModule.class
        })
public interface WidgetComponent {

    void inject(HomeScreenWidgetProvider homeScreenWidgetProvider);

    void inject(NextPrayerHomeScreenWidgetProvider nextPrayerHomeScreenWidgetProvider);
}
