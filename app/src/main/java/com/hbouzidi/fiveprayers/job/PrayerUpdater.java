package com.hbouzidi.fiveprayers.job;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import androidx.work.rxjava3.RxWorker;

import com.hbouzidi.fiveprayers.di.factory.worker.ChildWorkerFactory;
import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
import com.hbouzidi.fiveprayers.notifier.PrayerAlarmScheduler;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.TimingServiceFactory;
import com.hbouzidi.fiveprayers.timings.TimingsService;

import java.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Provider;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class PrayerUpdater extends RxWorker {

    private static final String TAG = "PrayerUpdater";
    private final Context context;
    private final LocationHelper locationHelper;
    private final AddressHelper addressHelper;
    private final TimingServiceFactory timingServiceFactory;
    private final PrayerAlarmScheduler prayerAlarmScheduler;
    private int runAttemptCount = 0;

    @Inject
    public PrayerUpdater(@NonNull Context context,
                         @NonNull WorkerParameters params,
                         @NonNull LocationHelper locationHelper,
                         @NonNull AddressHelper addressHelper,
                         @NonNull TimingServiceFactory timingServiceFactory,
                         @NonNull PrayerAlarmScheduler prayerAlarmScheduler
    ) {
        super(context, params);
        this.context = context;
        this.locationHelper = locationHelper;
        this.addressHelper = addressHelper;
        this.timingServiceFactory = timingServiceFactory;
        this.prayerAlarmScheduler = prayerAlarmScheduler;

        Log.i(TAG, "Prayer Updater Initialized");
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        Log.i(TAG, "Starting Create Prayer Updater Work");

        TimingsService timingsService = timingServiceFactory.create(PreferencesHelper.getCalculationMethod(context));

        Single<DayPrayer> dayPrayerSingle =
                locationHelper.getLocation()
                        .flatMap(addressHelper::getAddressFromLocation)
                        .flatMap(address ->
                                timingsService.getTimingsByCity(
                                        LocalDate.now(),
                                        address,
                                        context
                                ));

        return dayPrayerSingle
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(prayerAlarmScheduler::scheduleNextPrayerAlarms)
                .map(dayPrayer -> {
                    Log.i(TAG, "Prayers alarm updated successfully");
                    return Result.success();
                })
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

    public static class Factory implements ChildWorkerFactory {

        private final Provider<LocationHelper> locationHelperProvider;
        private final Provider<AddressHelper> addressHelperProvider;
        private final Provider<TimingServiceFactory> timingServiceFactoryProvider;
        private final Provider<PrayerAlarmScheduler> prayerAlarmSchedulerProvider;

        @Inject
        public Factory(Provider<LocationHelper> locationHelperProvider,
                       Provider<AddressHelper> addressHelperProvider,
                       Provider<TimingServiceFactory> timingServiceFactoryProvider,
                       Provider<PrayerAlarmScheduler> prayerAlarmSchedulerProvider
        ) {

            this.locationHelperProvider = locationHelperProvider;
            this.addressHelperProvider = addressHelperProvider;
            this.timingServiceFactoryProvider = timingServiceFactoryProvider;
            this.prayerAlarmSchedulerProvider = prayerAlarmSchedulerProvider;
        }

        @Override
        public ListenableWorker create(Context context, WorkerParameters workerParameters) {
            return new PrayerUpdater(context,
                    workerParameters,
                    locationHelperProvider.get(),
                    addressHelperProvider.get(),
                    timingServiceFactoryProvider.get(),
                    prayerAlarmSchedulerProvider.get()
            );
        }
    }
}
