package com.hbouzidi.fiveprayers.job;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;
import androidx.work.rxjava3.RxWorker;

import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
import com.hbouzidi.fiveprayers.notifier.NotifierHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.TimingServiceFactory;
import com.hbouzidi.fiveprayers.timings.TimingsService;

import java.time.LocalDate;

import io.reactivex.rxjava3.core.Single;

public class PrayerUpdater extends RxWorker {

    private static final String TAG = "PrayerUpdater";
    private Context context;
    private int runAttemptCount = 0;

    public PrayerUpdater(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        TimingsService timingsService = TimingServiceFactory.create();

        Single<DayPrayer> dayPrayerSingle =
                LocationHelper.getLocation(context)
                        .flatMap(location ->
                                AddressHelper.getAddressFromLocation(location, context)
                        ).flatMap(address ->
                        timingsService.getTimingsByCity(
                                LocalDate.now(),
                                address,
                                context
                        ));

        return dayPrayerSingle
                .doOnSuccess(dayPrayer -> NotifierHelper.scheduleNextPrayerAlarms(context, dayPrayer))
                .map(dayPrayer -> Result.success())
                .onErrorReturn(error -> {
                    Log.e(TAG, "Prayer Updater Failure", error);

                    if (runAttemptCount > 3) {
                        Log.e(TAG, "Cancel Prayer Updater and return failure");
                        return Result.failure();
                    } else {
                        runAttemptCount++;
                        Log.e(TAG, "Retry Prayer Updater, runAttemptCount=" + runAttemptCount);
                        return Result.retry();
                    }
                });
    }
}
