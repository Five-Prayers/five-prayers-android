package com.bouzidi.prayertimes.job;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import com.bouzidi.prayertimes.notifier.NotifierHelper;
import com.bouzidi.prayertimes.location.address.LocationAddressHelper;
import com.bouzidi.prayertimes.timings.CalculationMethodEnum;
import com.bouzidi.prayertimes.timings.DayPrayer;
import com.bouzidi.prayertimes.timings.PrayerHelper;
import com.bouzidi.prayertimes.utils.UserPreferencesUtils;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public class PrayerUpdater extends Worker {

    private Context context;

    public PrayerUpdater(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override
    public Result doWork() {
        Location location = getLocation(context);

        if (location == null) {
            return Result.failure();
        }

        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                LocationAddressHelper.getAddressFromLocation(location.getLatitude(), location.getLongitude(), context)
                        .flatMap(
                                address ->
                                        PrayerHelper.fetchTimingsByCity(
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

    private Data createOutputData(String date, String city, String country, CalculationMethodEnum calculationMethodEnum) {
        return new Data.Builder()
                .putString("DATE", date)
                .putString("CITY", city)
                .putString("COUNTRY", country)
                .putInt("CALCULATION METHOD ENUM", calculationMethodEnum.getValue())
                .build();
    }

    private static Location getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            return null;
        }
        if (isNetworkEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (isGPSEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (location != null) {
            final SharedPreferences sharedPreferences = context.getSharedPreferences("location", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            UserPreferencesUtils.putDouble(editor, "last_known_latitude", location.getLatitude());
            UserPreferencesUtils.putDouble(editor, "last_known_longitude", location.getLongitude());
        }

        return location;
    }
}



