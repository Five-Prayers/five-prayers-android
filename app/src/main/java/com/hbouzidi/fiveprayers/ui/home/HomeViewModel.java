package com.hbouzidi.fiveprayers.ui.home;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.TimingsService;
import com.hbouzidi.fiveprayers.timings.TimingServiceFactory;

import java.time.LocalDate;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<DayPrayer> mDayPrayers;
    private MutableLiveData<Boolean> mLocationAvailable;
    private MutableLiveData<String> mErrorMessage;
    private LocalDate todayDate;
    private CompositeDisposable compositeDisposable;
    private TimingsService timingsService;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        todayDate = LocalDate.now();
        mDayPrayers = new MutableLiveData<>();
        mLocationAvailable = new MutableLiveData<>();
        mErrorMessage = new MutableLiveData<>();
        timingsService = TimingServiceFactory.create();
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
                        timingsService.getTimingsByCity(
                                todayDate,
                                address,
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