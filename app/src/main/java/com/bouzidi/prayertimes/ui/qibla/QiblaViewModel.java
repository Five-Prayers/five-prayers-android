package com.bouzidi.prayertimes.ui.qibla;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bouzidi.prayertimes.location.address.AddressHelper;
import com.bouzidi.prayertimes.location.tracker.LocationHelper;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QiblaViewModel extends AndroidViewModel {

    private MutableLiveData<Location> mLocation;
    private CompositeDisposable compositeDisposable;


    public QiblaViewModel(@NonNull Application application) {
        super(application);
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
                LocationHelper.getLocation(context)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                mLocation.setValue(location);
                                AddressHelper.getAddressFromLocation(location, context);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            }
                        }));
    }
}