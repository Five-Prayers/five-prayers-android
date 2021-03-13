package com.hbouzidi.fiveprayers.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hbouzidi.fiveprayers.widget.WidgetUpdater;

public class NotifierReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PrayerNotification.createNotificationChannel(context);
        PrayerNotification.createNotification(context, intent);

        WidgetUpdater.updateHomeScreenWidget(context);
    }
}