package com.bouzidi.prayertimes.notifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bouzidi.prayertimes.timings.DayPrayer;
import com.bouzidi.prayertimes.timings.PrayerEnum;
import com.bouzidi.prayertimes.utils.TimingUtils;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class NotifierHelper {

    public static void scheduleNextPrayerAlarms(Context context, DayPrayer dayPrayer) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Map<PrayerEnum, String> timings = dayPrayer.getTimings();

        boolean ignoreAarmUpdate = false;
        boolean betweenMidgntAndFajr = TimingUtils.isBetweenTiming("00:00", calendar.getTime(), Objects.requireNonNull(timings.get(PrayerEnum.FAJR)));

        int index = 0;
        for (PrayerEnum key : timings.keySet()) {
            index++;
            String timing = Objects.requireNonNull(timings.get(key));
            boolean isTimingAfterMidnight = false;

            if (key.equals(PrayerEnum.MAGHRIB)) {
                isTimingAfterMidnight = dayPrayer.isMaghribAfterMidnight();
                ignoreAarmUpdate = isTimingAfterMidnight && betweenMidgntAndFajr;
            }

            if (key.equals(PrayerEnum.ICHA)) {
                isTimingAfterMidnight = dayPrayer.isIchaAfterMidnight();
                ignoreAarmUpdate = isTimingAfterMidnight && betweenMidgntAndFajr;
            }

            if (TimingUtils.isBeforeTiming(calendar.getTime(), timing, isTimingAfterMidnight) && !ignoreAarmUpdate) {
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotifierReceiver.class);
                intent.putExtra("prayerKey", key.toString());
                intent.putExtra("prayerTiming", timing);
                intent.putExtra("notificationId", 1);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, index, intent, FLAG_UPDATE_CURRENT);

                String[] endParts = timing.split(":");

                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (isTimingAfterMidnight) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

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
