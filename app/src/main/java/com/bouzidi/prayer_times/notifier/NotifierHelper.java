package com.bouzidi.prayer_times.notifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bouzidi.prayer_times.timings.DayPrayer;
import com.bouzidi.prayer_times.timings.PrayerEnum;
import com.bouzidi.prayer_times.utils.TimingUtils;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class NotifierHelper {

    public static void scheduleNextPrayerAlarms(Context context, DayPrayer dayPrayer) {
        Calendar calendar = Calendar.getInstance();

        Map<PrayerEnum, String> timings = dayPrayer.getTimings();

        for (PrayerEnum key : timings.keySet()) {
            String timing = Objects.requireNonNull(timings.get(key));
            boolean isTimingAfterMidnight = false;

            if (key.equals(PrayerEnum.MAGHRIB)) {
                isTimingAfterMidnight = dayPrayer.isMaghribAfterMidnight();
            }

            if (key.equals(PrayerEnum.ICHA)) {
                isTimingAfterMidnight = dayPrayer.isIchaAfterMidnight();
            }

            if (TimingUtils.isBeforeTiming(calendar.getTime(), timing, isTimingAfterMidnight)) {
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotifierReceiver.class);
                intent.putExtra("prayerKey", key.toString());
                intent.putExtra("prayerTiming", timing);
                intent.putExtra("notificationId", 1);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1, intent, FLAG_UPDATE_CURRENT);

                String[] endParts = timing.split(":");

                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (isTimingAfterMidnight) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmMgr.setExact(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
                } else {
                    alarmMgr.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
                }
            }
        }
    }
}
