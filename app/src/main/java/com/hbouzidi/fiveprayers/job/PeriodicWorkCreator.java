package com.hbouzidi.fiveprayers.job;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public final class PeriodicWorkCreator {

    private PeriodicWorkCreator() {
    }

    public static void schedulePrayerUpdater(Context context) {

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
}
