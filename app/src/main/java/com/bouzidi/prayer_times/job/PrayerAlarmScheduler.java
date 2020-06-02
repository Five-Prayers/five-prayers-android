package com.bouzidi.prayer_times.job;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.bouzidi.prayer_times.alarm.AlarmHelper;
import com.bouzidi.prayer_times.location.address.LocationAddressHelper;
import com.bouzidi.prayer_times.timings.CalculationMethodEnum;
import com.bouzidi.prayer_times.timings.DayPrayer;
import com.bouzidi.prayer_times.timings.PrayerHelper;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PrayerAlarmScheduler extends Worker {

    private Context context;

    public PrayerAlarmScheduler(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override
    public Result doWork() {
        return Result.success();
    }
}



