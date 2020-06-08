package com.bouzidi.prayertimes.job;

import android.content.Context;

import com.bouzidi.prayertimes.location.address.AddressHelper;
import com.bouzidi.prayertimes.location.tracker.LocationHelper;
import com.bouzidi.prayertimes.notifier.NotifierHelper;
import com.bouzidi.prayertimes.timings.CalculationMethodEnum;
import com.bouzidi.prayertimes.timings.DayPrayer;
import com.bouzidi.prayertimes.timings.PrayerHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PrayerUpdater extends Worker {

    private Context context;

    public PrayerUpdater(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NotNull
    @Override
    public Result doWork() {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                LocationHelper.getLocation(context)
                        .flatMap(location ->
                                AddressHelper.getAddressFromLocation(location, context)
                        ).flatMap(address ->
                        PrayerHelper.getTimingsByCity(
                                Calendar.getInstance().getTime(),
                                address.getLocality(),
                                address.getCountryName(),
                                CalculationMethodEnum.getDefault(),
                                context
                        ))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DayPrayer>() {
                            @Override
                            public void onSuccess(DayPrayer dayPrayer) {
                                NotifierHelper.scheduleNextPrayerAlarms(context, dayPrayer);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                            }
                        }));

        return Result.success();
    }
}
