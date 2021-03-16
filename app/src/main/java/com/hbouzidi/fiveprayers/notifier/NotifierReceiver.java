package com.hbouzidi.fiveprayers.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hbouzidi.fiveprayers.widget.WidgetUpdater;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NotifierReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PrayerNotification.createNotificationChannel(context);
        PrayerNotification.createNotification(context, intent);

        WidgetUpdater.updateHomeScreenWidget(context);
    }
}