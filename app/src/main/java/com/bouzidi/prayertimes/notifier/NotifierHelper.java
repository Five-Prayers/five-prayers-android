package com.bouzidi.prayertimes.notifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bouzidi.prayertimes.timings.DayPrayer;
import com.bouzidi.prayertimes.timings.PrayerEnum;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class NotifierHelper {

    public static void scheduleNextPrayerAlarms(Context context, DayPrayer dayPrayer) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();

        int index = 0;
        for (PrayerEnum key : timings.keySet()) {
            index++;

            LocalDateTime timing = timings.get(key);

            if (LocalDateTime.now().isBefore(timing)) {
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotifierReceiver.class);
                intent.putExtra("prayerKey", key.toString());
                intent.putExtra("prayerTiming", timing);
                intent.putExtra("notificationId", 1);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, index, intent, FLAG_UPDATE_CURRENT);

                calendar.set(Calendar.DAY_OF_MONTH, timing.getDayOfMonth());
                calendar.set(Calendar.HOUR_OF_DAY, timing.getHour());
                calendar.set(Calendar.MINUTE, timing.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                alarmMgr.cancel(alarmIntent);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                } else {
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                }
            }
        }
    }
}
