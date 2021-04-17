package com.hbouzidi.fiveprayers.notifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.common.TimingType;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.utils.TimingUtils;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class PrayerAlarmScheduler {

    public static final String TAG = "PrayerAlarmScheduler";
    private final Context context;
    private final PreferencesHelper preferencesHelper;

    @Inject
    public PrayerAlarmScheduler(Context context, PreferencesHelper preferencesHelper) {
        this.context = context;
        this.preferencesHelper = preferencesHelper;
    }

    public void scheduleAlarmsAndReminders(@NonNull DayPrayer dayPrayer) {
        scheduleNextPrayerAlarms(dayPrayer);

        if (preferencesHelper.isReminderEnabled()) {
            scheduleReminders(dayPrayer);
        }

        if (preferencesHelper.isDohaReminderEnabled()) {
            scheduleDohaComplementaryTiming(dayPrayer);
        }
    }

    private void scheduleNextPrayerAlarms(@NonNull DayPrayer dayPrayer) {
        Log.i(TAG, "Start scheduling Alarm for: " + dayPrayer.getDate());

        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();

        int index = 0;
        for (PrayerEnum key : timings.keySet()) {
            index++;

            LocalDateTime prayerTiming = timings.get(key);

            if (prayerTiming != null && LocalDateTime.now().isBefore(prayerTiming)) {
                Log.i(TAG, "Scheduling " + key.toString() + " Alarm at : " + TimingUtils.formatTiming(prayerTiming));

                schedule(dayPrayer, prayerTiming, TimingType.STANDARD, key.toString(),
                        1000, index, TimingUtils.getTimeInMilliIgnoringSeconds(prayerTiming), NotifierReceiver.class);
            }
        }

        Log.i(TAG, "End scheduling Alarm for: " + dayPrayer.getDate());
    }

    private void scheduleReminders(@NonNull DayPrayer dayPrayer) {
        Log.i(TAG, "Start scheduling Reminders for: " + dayPrayer.getDate());

        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();
        int reminderInterval = preferencesHelper.getReminderInterval();

        int index = 0;
        for (PrayerEnum key : timings.keySet()) {
            index++;

            LocalDateTime prayerTiming = timings.get(key);
            LocalDateTime reminderTiming = Objects.requireNonNull(prayerTiming).minusMinutes(reminderInterval);

            if (LocalDateTime.now().isBefore(reminderTiming)) {

                Log.i(TAG, "Scheduling " + key.toString() + " Reminder at : " + TimingUtils.formatTiming(reminderTiming));

                schedule(dayPrayer, prayerTiming, TimingType.STANDARD, key.toString(),
                        2000, index, TimingUtils.getTimeInMilliIgnoringSeconds(reminderTiming), ReminderReceiver.class);
            }
        }

        Log.i(TAG, "End scheduling Reminders for: " + dayPrayer.getDate());
    }

    private void scheduleDohaComplementaryTiming(@NonNull DayPrayer dayPrayer) {
        Log.i(TAG, "Start scheduling Complementary Timings for: " + dayPrayer.getDate());

        Map<ComplementaryTimingEnum, LocalDateTime> complementaryTimings = dayPrayer.getComplementaryTiming();

        LocalDateTime complementaryTiming = complementaryTimings.get(ComplementaryTimingEnum.DOHA);

        if (complementaryTiming != null && LocalDateTime.now().isBefore(complementaryTiming)) {

            Log.i(TAG, "Scheduling " + ComplementaryTimingEnum.DOHA.toString() + " Reminder at : " + TimingUtils.formatTiming(complementaryTiming));

            schedule(dayPrayer, complementaryTiming, TimingType.COMPLEMENTARY, ComplementaryTimingEnum.DOHA.toString(),
                    3000, 1, TimingUtils.getTimeInMilliIgnoringSeconds(complementaryTiming), ReminderReceiver.class);
        }

        Log.i(TAG, "End scheduling Complementary Timings for: " + dayPrayer.getDate());
    }

    private void schedule(@NonNull DayPrayer dayPrayer, LocalDateTime prayerTiming, TimingType timingType,
                          String prayerKey, int notificationId, int requestCode, long timeInMilliIgnoringSeconds,
                          Class<? extends BroadcastReceiver> receiverClass) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, receiverClass);
        intent.putExtra("prayerType", timingType.toString());
        intent.putExtra("prayerKey", prayerKey);
        intent.putExtra("prayerTiming", UiUtils.formatTiming(prayerTiming));
        intent.putExtra("prayerCity", dayPrayer.getCity());
        intent.putExtra("notificationId", notificationId);

        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode, intent, FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMilliIgnoringSeconds, alarmIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, timeInMilliIgnoringSeconds, alarmIntent);
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, timeInMilliIgnoringSeconds, alarmIntent);
        }
    }
}
