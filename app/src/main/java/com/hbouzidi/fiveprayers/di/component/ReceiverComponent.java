package com.hbouzidi.fiveprayers.di.component;

import com.hbouzidi.fiveprayers.di.module.AppModule;
import com.hbouzidi.fiveprayers.di.module.NetworkModule;
import com.hbouzidi.fiveprayers.di.module.WidgetModule;
import com.hbouzidi.fiveprayers.notifier.NotificationDismissedReceiver;
import com.hbouzidi.fiveprayers.notifier.NotifierActionReceiver;
import com.hbouzidi.fiveprayers.notifier.NotifierReceiver;

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
public interface ReceiverComponent {

    void inject(NotificationDismissedReceiver notificationDismissedReceiver);

    void inject(NotifierActionReceiver notifierActionReceiver);

    void inject(NotifierReceiver notifierReceiver);
}
