package com.bouzidi.prayer_times.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bouzidi.prayer_times.timings.DayPrayer;
import com.bouzidi.prayer_times.timings.Prayer;
import com.bouzidi.prayer_times.utils.TimingUtils;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class AlarmHelper {

    public static void scheduleNextPrayerAlarms(Context context, DayPrayer dayPrayer) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = System.currentTimeMillis();
        calendar.setTimeInMillis(currentTime);

        for (Prayer prayer : dayPrayer.getPrayers()) {
            if (TimingUtils.isBeforeTiming(calendar.getTime(), prayer.getTiming())) {
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra("prayerKey", prayer.getKey().toString());
                intent.putExtra("prayerTiming", prayer.getTiming());
                intent.putExtra("notificationId", 1);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1, intent, FLAG_UPDATE_CURRENT);

                String[] endParts = prayer.getTiming().split(":");

                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmMgr.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
                } else {
                    alarmMgr.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
                }
            }
        }
    }
}
