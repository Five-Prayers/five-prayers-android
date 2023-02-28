package com.hbouzidi.fiveprayers.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
import com.hbouzidi.fiveprayers.openweathermap.OpenWeatherMapAPIService;
import com.hbouzidi.fiveprayers.openweathermap.OpenWeatherMapResponse;
import com.hbouzidi.fiveprayers.openweathermap.TemperatureUnit;
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
    private final OpenWeatherMapAPIService openWeatherMapAPIService;
    private final PreferencesHelper preferencesHelper;

    private final MutableLiveData<DayPrayer> mDayPrayers;
    private final MutableLiveData<OpenWeatherMapResponse> mOpenWeatherMapResponse;
    private final MutableLiveData<String> mErrorMessage;

    private final MutableLiveData<String> mmOpenWeatherMapErrorMessage;
    private final LocalDate todayDate;
    private CompositeDisposable compositeDisposable;

    @Inject
    public HomeViewModel(@NonNull Application application,
                         @NonNull LocationHelper locationHelper,
                         @NonNull AddressHelper addressHelper,
                         @NonNull TimingServiceFactory timingServiceFactory,
                         @NonNull OpenWeatherMapAPIService openWeatherMapAPIService,
                         @NonNull PreferencesHelper preferencesHelper
    ) {
        super(application);

        this.locationHelper = locationHelper;
        this.addressHelper = addressHelper;
        this.timingServiceFactory = timingServiceFactory;
        this.openWeatherMapAPIService = openWeatherMapAPIService;
        this.preferencesHelper = preferencesHelper;

        todayDate = LocalDate.now();
        mDayPrayers = new MutableLiveData<>();
        mOpenWeatherMapResponse = new MutableLiveData<>();
        mErrorMessage = new MutableLiveData<>();
        mmOpenWeatherMapErrorMessage = new MutableLiveData<>();
        setLiveData();
    }

    LiveData<DayPrayer> getDayPrayers() {
        return mDayPrayers;
    }

    LiveData<OpenWeatherMapResponse> getOpenWeatherData() {
        return mOpenWeatherMapResponse;
    }

    LiveData<String> getError() {
        return mErrorMessage;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    private void setLiveData() {
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

        compositeDisposable.add(
                locationHelper.getLocation()
                        .flatMap(
                                location ->
                                        openWeatherMapAPIService.getCurrentWeatherData(location.getLatitude(), location.getLongitude(), preferencesHelper.getOpenWeatherAPIKey(), TemperatureUnit.valueOf(preferencesHelper.getOpenWeatherUnit()))
                        )
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<OpenWeatherMapResponse>() {
                            @Override
                            public void onSuccess(@NotNull OpenWeatherMapResponse openWeatherMapResponse) {
                                mOpenWeatherMapResponse.postValue(openWeatherMapResponse);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            }
                        })
        );
    }
}