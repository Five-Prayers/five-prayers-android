package com.hbouzidi.fiveprayers.di.component;

import com.hbouzidi.fiveprayers.di.module.AppModule;
import com.hbouzidi.fiveprayers.di.module.NetworkModule;
import com.hbouzidi.fiveprayers.ui.DefaultActivity;
import com.hbouzidi.fiveprayers.ui.MainActivity;

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
public interface DefaultComponent {

    void inject(MainActivity mainActivity);

    void inject(DefaultActivity defaultActivity);
}
