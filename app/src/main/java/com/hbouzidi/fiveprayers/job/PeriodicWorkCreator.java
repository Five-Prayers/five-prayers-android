package com.hbouzidi.fiveprayers.job;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
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

        Constraints networkConstraint = new Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest
                        .Builder(PrayerUpdater.class, 60, TimeUnit.MINUTES, 50, TimeUnit.MINUTES)
                        .setConstraints(networkConstraint)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                10,
                                TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("FIVE_PRAYERS_UPDATER", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }
}
