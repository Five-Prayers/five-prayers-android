package com.hbouzidi.fiveprayers.ui.timingtable;

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
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TimingTableViewModel extends AndroidViewModel {

    private final LocalDate todayDate;
    private MutableLiveData<List<DayPrayer>> mCalendar;
    private CompositeDisposable compositeDisposable;

    public TimingTableViewModel(@NonNull Application application) {
        super(application);
        mCalendar = new MutableLiveData<>();
        todayDate = LocalDate.now();

        setLiveData(application.getApplicationContext());
    }


    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    LiveData<List<DayPrayer>> getCalendar() {
        return mCalendar;
    }


    private void setLiveData(Context context) {
        TimingsService timingsService = TimingServiceFactory.create();

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                LocationHelper.getLocation(context)
                        .flatMap(location ->
                                AddressHelper.getAddressFromLocation(location, context)
                        ).flatMap(address ->
                        timingsService.getCalendarByCity(
                                address,
                                todayDate.getMonthValue(),
                                todayDate.getYear(),
                                context
                        ))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<DayPrayer>>() {
                            @Override
                            public void onSuccess(List<DayPrayer> calendar) {
                                mCalendar.postValue(calendar);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                //TODO
                                //mErrorMessage.setValue(e.getMessage());
                            }
                        }));
    }
}