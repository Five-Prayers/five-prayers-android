package com.hbouzidi.fiveprayers.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.database.ReadingScheduleRegistry;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingSchedule;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranReadingReceiver extends BroadcastReceiver {

    @Inject
    QuranReadingReminderNotification quranReadingReminderNotification;

    @Inject
    PreferencesHelper preferencesHelper;

    @Inject
    ReadingScheduleRegistry readingScheduleRegistry;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((FivePrayerApplication) context.getApplicationContext())
                .receiverComponent
                .inject(this);

        sendQuranReadingReminderNotification();
    }

    private void sendQuranReadingReminderNotification() {
        ReadingSchedule nextReadingScheduleOccurrence = readingScheduleRegistry.getNextReadingScheduleOccurrence();

        if (nextReadingScheduleOccurrence != null) {
            if (preferencesHelper.isNotificationsEnabled()) {
                quranReadingReminderNotification.createNotificationChannel();
                quranReadingReminderNotification.createNotification(nextReadingScheduleOccurrence);
            }
        }
    }
}