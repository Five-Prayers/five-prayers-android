package com.hbouzidi.fiveprayers.job;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.hbouzidi.fiveprayers.timings.DayPrayer;

import java.util.concurrent.TimeUnit;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public final class WorkCreator {

    private WorkCreator() {
    }

    public static void schedulePeriodicPrayerUpdater(Context context) {

        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest
                        .Builder(PrayerUpdater.class, 30, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                10,
                                TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("FIVE_PRAYERS_UPDATER", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    public static void scheduleOneTimePrayerUpdater(Context context, DayPrayer dayPrayer) {
        Gson gson = new Gson();
        String dayPrayerString = gson.toJson(dayPrayer);

        Data data = new Data.Builder()
                .putString("DAY_PRAYER_PARAM",dayPrayerString)
                .build();

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
                .Builder(InstantPrayerScheduler.class)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(
                        "ONE_TIME_FIVE_PRAYERS_UPDATER",
                        ExistingWorkPolicy.KEEP,
                        oneTimeWorkRequest
                );
    }
}
