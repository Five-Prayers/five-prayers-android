package com.bouzidi.prayertimes.job;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;
import androidx.work.rxjava3.RxWorker;

import com.bouzidi.prayertimes.location.address.AddressHelper;
import com.bouzidi.prayertimes.location.tracker.LocationHelper;
import com.bouzidi.prayertimes.notifier.NotifierHelper;
import com.bouzidi.prayertimes.timings.DayPrayer;
import com.bouzidi.prayertimes.timings.PrayerHelper;

import java.time.LocalDate;

import io.reactivex.rxjava3.core.Single;

public class PrayerUpdater extends RxWorker {

    private Context context;

    public PrayerUpdater(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        Single<DayPrayer> dayPrayerSingle =
                LocationHelper.getLocation(context)
                        .flatMap(location ->
                                AddressHelper.getAddressFromLocation(location, context)
                        ).flatMap(address ->
                        PrayerHelper.getTimingsByCity(
                                LocalDate.now(),
                                address.getLocality(),
                                address.getCountryName(),
                                context
                        ));

        return dayPrayerSingle
                .doOnSuccess(dayPrayer -> NotifierHelper.scheduleNextPrayerAlarms(context, dayPrayer))
                .map(dayPrayer -> Result.success())
                .onErrorReturn(error -> Result.failure());
    }
}
