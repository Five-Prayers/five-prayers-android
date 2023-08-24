package com.hbouzidi.fiveprayers.notifier;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

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
    private final CannotScheduleExactAlarmNotification cannotScheduleExactAlarmNotification;

    @Inject
    public PrayerAlarmScheduler(Context context, PreferencesHelper preferencesHelper, CannotScheduleExactAlarmNotification cannotScheduleExactAlarmNotification) {
        this.context = context;
        this.preferencesHelper = preferencesHelper;
        this.cannotScheduleExactAlarmNotification = cannotScheduleExactAlarmNotification;
    }

    public void scheduleAlarmsAndReminders(@NonNull DayPrayer dayPrayer) {
        if (!canScheduleExactAlarms()) {
            return;
        }

        scheduleNextPrayerAlarms(dayPrayer);

        if (preferencesHelper.isReminderEnabled()) {
            scheduleReminders(dayPrayer);
        }

        if (preferencesHelper.isDohaReminderEnabled()) {
            scheduleComplementaryTiming(dayPrayer, ComplementaryTimingEnum.DOHA, 1);
        }

        if (preferencesHelper.isLastThirdOfTheNightReminderEnabled()) {
            scheduleComplementaryTiming(dayPrayer, ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT, 2);
        }

        if (preferencesHelper.isSilenterEnabled()) {
            scheduleSilenter(dayPrayer);
        }

        if (preferencesHelper.isDailyVerseEnabled()) {
            scheduleDailyVerse(dayPrayer);
        }

        if (preferencesHelper.isQuranReadingSchedulerNotificationEnabled()
                && (LocalDate.now().isAfter(preferencesHelper.getReadingScheduleStartDateNotification())
                || LocalDate.now().isEqual(preferencesHelper.getReadingScheduleStartDateNotification()))) {
            scheduleQuranReadingSchedulerNotification();
        }

        if (preferencesHelper.isInvocationsNotificationsEnabled()) {
            scheduleInvocations(true, dayPrayer);
        }

        if (preferencesHelper.isInvocationsNotificationsEnabled()) {
            scheduleInvocations(false, dayPrayer);
        }
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmMgr.canScheduleExactAlarms()) {
                return true;
            } else {
                cannotScheduleExactAlarmNotification.createNotificationChannel();
                cannotScheduleExactAlarmNotification.createNotification();
                return false;
            }
        }
        return true;
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

                scheduleNotifications(dayPrayer, prayerTiming, TimingType.STANDARD, key.toString(),
                        1000, index, prayerTiming, NotifierReceiver.class);
            }
        }

        Log.i(TAG, "End scheduling Alarm for: " + dayPrayer.getDate());
    }

    private void scheduleReminders(@NonNull DayPrayer dayPrayer) {
        Log.i(TAG, "Start scheduling Reminders for: " + dayPrayer.getDate());

        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();
        int reminderInterval = preferencesHelper.getReminderInterval();

        int index = 10;
        for (PrayerEnum key : timings.keySet()) {
            index++;

            LocalDateTime prayerTiming = timings.get(key);
            LocalDateTime reminderTiming = Objects.requireNonNull(prayerTiming).minusMinutes(reminderInterval);

            if (LocalDateTime.now().isBefore(reminderTiming)) {

                Log.i(TAG, "Scheduling " + key.toString() + " Reminder at : " + TimingUtils.formatTiming(reminderTiming));

                scheduleNotifications(dayPrayer, prayerTiming, TimingType.STANDARD, key.toString(),
                        2000, index, reminderTiming, ReminderReceiver.class);
            }
        }

        Log.i(TAG, "End scheduling Reminders for: " + dayPrayer.getDate());
    }

    private void scheduleComplementaryTiming(@NonNull DayPrayer dayPrayer, ComplementaryTimingEnum complementaryTimingEnum, int requestCode) {
        Log.i(TAG, "Start scheduling Complementary Timings for: " + dayPrayer.getDate());

        Map<ComplementaryTimingEnum, LocalDateTime> complementaryTimings = dayPrayer.getComplementaryTiming();
        LocalDateTime complementaryTiming = complementaryTimings.get(complementaryTimingEnum);

        if (complementaryTiming != null && LocalDateTime.now().isBefore(complementaryTiming)) {

            Log.i(TAG, "Scheduling " + complementaryTimingEnum.toString() + " Reminder at : " + TimingUtils.formatTiming(complementaryTiming));

            scheduleNotifications(dayPrayer, complementaryTiming, TimingType.COMPLEMENTARY, complementaryTimingEnum.toString(),
                    3000, requestCode, complementaryTiming, ReminderReceiver.class);
        }

        Log.i(TAG, "End scheduling Complementary Timings for: " + dayPrayer.getDate());
    }

    private void scheduleNotifications(@NonNull DayPrayer dayPrayer, LocalDateTime prayerTiming, TimingType timingType,
                                       String prayerKey, int notificationId, int requestCode, LocalDateTime timingToSchedule,
                                       Class<? extends BroadcastReceiver> receiverClass) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, receiverClass);
        intent.putExtra("prayerType", timingType.toString());
        intent.putExtra("prayerKey", prayerKey);
        intent.putExtra("prayerTiming", UiUtils.formatTiming(prayerTiming));
        intent.putExtra("prayerCity", dayPrayer.getCity());
        intent.putExtra("notificationId", notificationId);

        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        PendingIntent alarmIntent = PendingIntentCreator.getBroadcast(context, requestCode, intent, FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);

        scheduleAlarm(timingToSchedule, alarmMgr, alarmIntent);
    }

    private void scheduleSilenter(DayPrayer dayPrayer) {
        Log.i(TAG, "Start scheduling Silenter for: " + dayPrayer.getDate());

        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();

        int index = 20;
        for (PrayerEnum key : timings.keySet()) {
            index++;

            LocalDateTime prayerTiming = timings.get(key);

            if (prayerTiming != null && LocalDateTime.now().isBefore(prayerTiming)) {
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                PendingIntent silentAlarmIntent = getSilenterPendingIntent(index, alarmMgr, true);
                PendingIntent unSilentAlarmIntent = getSilenterPendingIntent(index + 10, alarmMgr, false);

                LocalDateTime silentTiming = prayerTiming.plus(preferencesHelper.getSilenterStartTime(), ChronoUnit.MINUTES);
                scheduleAlarm(silentTiming, alarmMgr, silentAlarmIntent);
                Log.i(TAG, "Scheduling " + key.toString() + " Silenter at : " + TimingUtils.formatTiming(silentTiming));

                LocalDateTime unSilentTiming = getUnSilentTiming(prayerTiming, key);
                scheduleAlarm(unSilentTiming, alarmMgr, unSilentAlarmIntent);
                Log.i(TAG, "Scheduling " + key + " Un-Silenter at : " + TimingUtils.formatTiming(unSilentTiming));
            }
        }

        Log.i(TAG, "End scheduling Silenter for: " + dayPrayer.getDate());
    }

    private void scheduleDailyVerse(DayPrayer dayPrayer) {
        Log.i(TAG, "Start scheduling Daily Verse for: " + dayPrayer.getDate());

        Map<ComplementaryTimingEnum, LocalDateTime> timings = dayPrayer.getComplementaryTiming();

        LocalDateTime sunriseTiming = timings.get(ComplementaryTimingEnum.SUNRISE);

        if (sunriseTiming != null && LocalDateTime.now().isBefore(sunriseTiming)) {
            Log.i(TAG, "Scheduling Daily Verse at : " + TimingUtils.formatTiming(sunriseTiming));

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, DailyVerseReceiver.class);

            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

            PendingIntent alarmIntent = PendingIntentCreator.getBroadcast(context, 542, intent, FLAG_UPDATE_CURRENT);
            alarmMgr.cancel(alarmIntent);

            scheduleAlarm(sunriseTiming, alarmMgr, alarmIntent);
        }
        Log.i(TAG, "End scheduling Daily Verse for: " + dayPrayer.getDate());
    }

    private void scheduleQuranReadingSchedulerNotification() {
        Log.i(TAG, "Start scheduling Quran Reading for: " + LocalDate.now());

        LocalDateTime readingScheduleNotificationTime = LocalDate.now().atTime(preferencesHelper.getReadingScheduleNotificationTime());

        if (LocalDateTime.now().isBefore(readingScheduleNotificationTime)) {
            Log.i(TAG, "Scheduling Quran Reading at : " + TimingUtils.formatTiming(readingScheduleNotificationTime));

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, QuranReadingReceiver.class);

            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

            PendingIntent alarmIntent = PendingIntentCreator.getBroadcast(context, 543, intent, FLAG_UPDATE_CURRENT);
            alarmMgr.cancel(alarmIntent);

            scheduleAlarm(readingScheduleNotificationTime, alarmMgr, alarmIntent);
        }
        Log.i(TAG, "End scheduling Quran Reading for: " + LocalDate.now());
    }

    private void scheduleInvocations(@NonNull boolean morningInvocation, DayPrayer dayPrayer) {
        Log.i(TAG, "Start scheduling Notification for Invocations");

        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();

        LocalDateTime notificationTime = morningInvocation ? timings.get(PrayerEnum.FAJR).plus(30, ChronoUnit.MINUTES) : timings.get(PrayerEnum.MAGHRIB).plus(30, ChronoUnit.MINUTES);

        if (notificationTime != null && LocalDateTime.now().isBefore(notificationTime)) {
            Log.i(TAG, "Scheduling Invocations at : " + TimingUtils.formatTiming(notificationTime));

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, InvocationsReceiver.class);
            intent.putExtra("IS_MORNING_INVOCATIONS", morningInvocation);
            intent.putExtra("NOTIFICATION_ID", 1629);

            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

            PendingIntent alarmIntent = PendingIntentCreator.getBroadcast(context, 542, intent, FLAG_UPDATE_CURRENT);
            alarmMgr.cancel(alarmIntent);

            scheduleAlarm(notificationTime, alarmMgr, alarmIntent);
        }
        Log.i(TAG, "End scheduling Invocations for: " + dayPrayer.getDate());
    }

    private PendingIntent getSilenterPendingIntent(int index, AlarmManager alarmMgr, boolean turnToSilent) {
        Intent silentIntent = new Intent(context, SilenterReceiver.class);
        silentIntent.putExtra("TURN_TO_SILENT", turnToSilent);
        silentIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        PendingIntent silentAlarmIntent = PendingIntentCreator.getBroadcast(context, index, silentIntent, FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(silentAlarmIntent);
        return silentAlarmIntent;
    }

    private LocalDateTime getUnSilentTiming(LocalDateTime prayerTiming, PrayerEnum key) {
        int silenterInterval;

        if (prayerTiming.getDayOfWeek().equals(DayOfWeek.FRIDAY) && PrayerEnum.DHOHR.equals(key)) {
            silenterInterval = preferencesHelper.getSilenterIntervalForFridayPrayer();
        } else {
            silenterInterval = preferencesHelper.getSilenterInterval();
        }

        return prayerTiming.plus(preferencesHelper.getSilenterStartTime(), ChronoUnit.MINUTES).plus(silenterInterval, ChronoUnit.MINUTES);
    }

    private void scheduleAlarm(LocalDateTime timingToSchedule, AlarmManager alarmMgr, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, TimingUtils.getTimeInMilliIgnoringSeconds(timingToSchedule), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, TimingUtils.getTimeInMilliIgnoringSeconds(timingToSchedule), pendingIntent);
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, TimingUtils.getTimeInMilliIgnoringSeconds(timingToSchedule), pendingIntent);
        }
    }
}
