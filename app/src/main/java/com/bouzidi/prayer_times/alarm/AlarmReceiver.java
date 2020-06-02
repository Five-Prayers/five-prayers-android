package com.bouzidi.prayer_times.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PrayerNotification.createNotificationChannel(context);
        PrayerNotification.createNotification(context, intent);
    }
}