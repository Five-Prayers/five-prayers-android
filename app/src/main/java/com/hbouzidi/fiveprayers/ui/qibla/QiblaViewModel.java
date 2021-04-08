package com.hbouzidi.fiveprayers.ui.qibla;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QiblaViewModel extends AndroidViewModel {

    private final LocationHelper locationHelper;
    private final MutableLiveData<Location> mLocation;
    private final AddressHelper addressHelper;

    private CompositeDisposable compositeDisposable;

    @Inject
    public QiblaViewModel(@NonNull Application application,
                          @NonNull LocationHelper locationHelper,
                          @NonNull AddressHelper addressHelper
    ) {
        super(application);
        this.locationHelper = locationHelper;
        this.addressHelper = addressHelper;

        mLocation = new MutableLiveData<>();

        setLiveData(application.getApplicationContext());
    }

    public LiveData<Location> getLocation() {
        return mLocation;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    private void setLiveData(Context context) {
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                locationHelper.getLocation()
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Location>() {
                            @Override
                            public void onSuccess(@NotNull Location location) {
                                mLocation.setValue(location);
                                addressHelper.getAddressFromLocation(location);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            }
                        }));
    }
}