package com.hbouzidi.fiveprayers.job;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import androidx.work.rxjava3.RxWorker;

import com.google.gson.Gson;
import com.hbouzidi.fiveprayers.di.factory.worker.ChildWorkerFactory;
import com.hbouzidi.fiveprayers.notifier.PrayerAlarmScheduler;
import com.hbouzidi.fiveprayers.timings.DayPrayer;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Provider;

import io.reactivex.rxjava3.core.Single;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class InstantPrayerScheduler extends RxWorker {

    private static final String TAG = "INST_PRAYER_SCHEDULER";
    private final PrayerAlarmScheduler prayerAlarmScheduler;

    @Inject
    public InstantPrayerScheduler(@NonNull Context context,
                                  @NonNull WorkerParameters params,
                                  @NonNull PrayerAlarmScheduler prayerAlarmScheduler
    ) {
        super(context, params);
        this.prayerAlarmScheduler = prayerAlarmScheduler;

        Log.i(TAG, "Instant Prayer Scheduler Initialized");
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        Log.i(TAG, "Starting Create Instant Prayer Scheduler Work");

        String dayPrayerString = getInputData().getString("DAY_PRAYER_PARAM");
        Gson gson = new Gson();

        try {
            DayPrayer dayPrayer = gson.fromJson(dayPrayerString, DayPrayer.class);
            prayerAlarmScheduler.scheduleAlarmsAndReminders(Objects.requireNonNull(dayPrayer));
            return Single.just(Result.success());
        } catch (Exception e) {
            return Single.just(Result.failure());
        }
    }

    public static class Factory implements ChildWorkerFactory {

        private final Provider<PrayerAlarmScheduler> prayerAlarmSchedulerProvider;

        @Inject
        public Factory(Provider<PrayerAlarmScheduler> prayerAlarmSchedulerProvider) {
            this.prayerAlarmSchedulerProvider = prayerAlarmSchedulerProvider;
        }

        @Override
        public ListenableWorker create(Context context, WorkerParameters workerParameters) {
            return new InstantPrayerScheduler(context, workerParameters, prayerAlarmSchedulerProvider.get());
        }
    }
}
