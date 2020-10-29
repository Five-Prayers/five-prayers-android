package com.hbouzidi.fiveprayers.notifier;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.hbouzidi.fiveprayers.timings.DayPrayer;

import java.util.Objects;

public class NotifierJobService extends JobIntentService {

    private static final int JOB_ID = 201;

    @Override
    public void onHandleWork(@NonNull Intent intent) {
        DayPrayer dayPrayer = (DayPrayer) intent.getSerializableExtra("dayPrayer");
        PrayerAlarmScheduler.scheduleNextPrayerAlarms(getApplicationContext(), Objects.requireNonNull(dayPrayer));
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotifierJobService.class, JOB_ID, intent);
    }
}