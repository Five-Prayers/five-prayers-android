package com.bouzidi.prayertimes.notifier;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.bouzidi.prayertimes.timings.DayPrayer;

import java.util.Objects;

public class NotifierService extends JobIntentService {

    private static final int JOB_ID = 201;

    @Override
    public void onHandleWork(@NonNull Intent intent) {
        DayPrayer dayPrayer = (DayPrayer) intent.getSerializableExtra("dayPrayer");
        NotifierHelper.scheduleNextPrayerAlarms(getApplicationContext(), Objects.requireNonNull(dayPrayer));
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotifierService.class, JOB_ID, intent);
    }
}