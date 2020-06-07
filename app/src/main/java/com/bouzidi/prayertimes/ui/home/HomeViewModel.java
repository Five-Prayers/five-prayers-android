package com.bouzidi.prayertimes.ui.home;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.bouzidi.prayertimes.location.address.LocationAddressHelper;
import com.bouzidi.prayertimes.location.tracker.LocationTrackerHelper;
import com.bouzidi.prayertimes.timings.CalculationMethodEnum;
import com.bouzidi.prayertimes.timings.DayPrayer;
import com.bouzidi.prayertimes.timings.PrayerHelper;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<DayPrayer> mDayPrayers;
    private MutableLiveData<Boolean> mLocationAvailable;
    private Date todayDate;
    private CompositeDisposable compositeDisposable;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        todayDate = Calendar.getInstance().getTime();
        mDayPrayers = new MutableLiveData<>();
        mLocationAvailable = new MutableLiveData<>();
        setLiveData(application.getApplicationContext());
    }

    LiveData<DayPrayer> getDayPrayers() {
        return mDayPrayers;
    }

    LiveData<Boolean> isLocationAvailable() {
        return mLocationAvailable;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    private void setLiveData(Context context) {
        Location location = LocationTrackerHelper.getLocation(context);

        if (location == null) {
            mLocationAvailable.setValue(false);
        } else {
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(
                    LocationAddressHelper.getAddressFromLocation(location.getLatitude(), location.getLongitude(), context)
                            .flatMap(
                                    address ->
                                            PrayerHelper.getTimingsByCity(
                                                    todayDate,
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
                                    mDayPrayers.postValue(dayPrayer);
                                }

                                @Override
                                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                    //TODO Handle Error
                                    int toto = 0;
                                }
                            }));
        }
    }
}