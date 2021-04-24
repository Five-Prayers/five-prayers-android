package com.hbouzidi.fiveprayers.ui.home;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.TimingServiceFactory;
import com.hbouzidi.fiveprayers.timings.TimingsService;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class HomeViewModel extends AndroidViewModel {

    private final LocationHelper locationHelper;
    private final AddressHelper addressHelper;
    private final TimingServiceFactory timingServiceFactory;
    private final PreferencesHelper preferencesHelper;

    private final MutableLiveData<DayPrayer> mDayPrayers;
    private final MutableLiveData<String> mErrorMessage;
    private final LocalDate todayDate;
    private CompositeDisposable compositeDisposable;

    @Inject
    public HomeViewModel(@NonNull Application application,
                         @NonNull LocationHelper locationHelper,
                         @NonNull AddressHelper addressHelper,
                         @NonNull TimingServiceFactory timingServiceFactory,
                         @NonNull PreferencesHelper preferencesHelper
    ) {
        super(application);

        this.locationHelper = locationHelper;
        this.addressHelper = addressHelper;
        this.timingServiceFactory = timingServiceFactory;
        this.preferencesHelper = preferencesHelper;

        todayDate = LocalDate.now();
        mDayPrayers = new MutableLiveData<>();
        mErrorMessage = new MutableLiveData<>();
        setLiveData(application.getApplicationContext());
    }

    LiveData<DayPrayer> getDayPrayers() {
        return mDayPrayers;
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
        TimingsService timingsService = timingServiceFactory.create(preferencesHelper.getCalculationMethod());

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                locationHelper.getLocation()
                        .flatMap(addressHelper::getAddressFromLocation)
                        .flatMap(address ->
                                timingsService.getTimingsByCity(todayDate, address))
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DayPrayer>() {
                            @Override
                            public void onSuccess(@NotNull DayPrayer dayPrayer) {
                                mDayPrayers.postValue(dayPrayer);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                mErrorMessage.postValue(e.getMessage());
                            }
                        }));
    }
}