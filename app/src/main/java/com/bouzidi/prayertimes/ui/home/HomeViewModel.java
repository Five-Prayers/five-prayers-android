package com.bouzidi.prayertimes.ui.home;

import android.app.Application;
import android.content.Context;

import com.bouzidi.prayertimes.location.address.AddressHelper;
import com.bouzidi.prayertimes.location.tracker.LocationHelper;
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
    private MutableLiveData<String> mErrorMessage;
    private Date todayDate;
    private CompositeDisposable compositeDisposable;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        todayDate = Calendar.getInstance().getTime();
        mDayPrayers = new MutableLiveData<>();
        mLocationAvailable = new MutableLiveData<>();
        mErrorMessage = new MutableLiveData<>();
        setLiveData(application.getApplicationContext());
    }

    LiveData<DayPrayer> getDayPrayers() {
        return mDayPrayers;
    }

    LiveData<Boolean> isLocationAvailable() {
        return mLocationAvailable;
    }

    LiveData<String> getError() {
        return mErrorMessage;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    private void setLiveData(Context context) {
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                LocationHelper.getLocation(context)
                        .flatMap(location ->
                                AddressHelper.getAddressFromLocation(location, context)
                        ).flatMap(address ->
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
                                mErrorMessage.setValue(e.getMessage());
                            }
                        }));
    }
}