package com.bouzidi.prayertimes.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.bouzidi.prayertimes.location.address.AddressHelper;
import com.bouzidi.prayertimes.location.tracker.LocationHelper;
import com.bouzidi.prayertimes.timings.DayPrayer;
import com.bouzidi.prayertimes.timings.PrayerHelper;

import java.time.LocalDate;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotifierBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

            CompositeDisposable disposable = new CompositeDisposable();
            disposable.add(
                    LocationHelper.getLocation(context)
                            .flatMap(location ->
                                    AddressHelper.getAddressFromLocation(location, context)
                            ).flatMap(address ->
                            PrayerHelper.getTimingsByCity(
                                    LocalDate.now(),
                                    address.getLocality(),
                                    address.getCountryName(),
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
        }
    }
}
