package com.bouzidi.prayertimes.job;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PrayerAlarmScheduler extends Worker {

    private Context context;

    public PrayerAlarmScheduler(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override
    public Result doWork() {
        return Result.success();
    }
}



