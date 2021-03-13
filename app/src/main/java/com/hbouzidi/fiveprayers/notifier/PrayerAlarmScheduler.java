package com.hbouzidi.fiveprayers.notifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.utils.TimingUtils;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class PrayerAlarmScheduler {

    public static final String TAG = "PrayerAlarmScheduler";

    public static void scheduleNextPrayerAlarms(Context context, @NonNull DayPrayer dayPrayer) {
        Log.i(TAG, "Start scheduling Alarm for: " + dayPrayer.getDate());

        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();

        int index = 0;
        for (PrayerEnum key : timings.keySet()) {
            index++;

            LocalDateTime timing = timings.get(key);

            if (timing != null && LocalDateTime.now().isBefore(timing)) {
                Log.i(TAG, "Scheduling " + key.toString() + " Alarm at : " + TimingUtils.formatTiming(timing));

                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotifierReceiver.class);
                intent.putExtra("prayerKey", key.toString());
                intent.putExtra("prayerTiming", UiUtils.formatTiming(timing));
                intent.putExtra("prayerCity", dayPrayer.getCity());
                intent.putExtra("notificationId", 1000);

                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, index, intent, FLAG_UPDATE_CURRENT);
                alarmMgr.cancel(alarmIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, TimingUtils.getTimeInMilliIgnoringSeconds(timing), alarmIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmMgr.setExact(AlarmManager.RTC_WAKEUP, TimingUtils.getTimeInMilliIgnoringSeconds(timing), alarmIntent);
                } else {
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, TimingUtils.getTimeInMilliIgnoringSeconds(timing), alarmIntent);
                }
            }
        }

        Log.i(TAG, "End scheduling Alarm for: " + dayPrayer.getDate());
    }
}
