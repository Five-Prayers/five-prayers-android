package com.bouzidi.prayer_times.ui.home;

import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bouzidi.prayer_times.MainActivity;
import com.bouzidi.prayer_times.R;
import com.bouzidi.prayer_times.database.PrayerRegistry;
import com.bouzidi.prayer_times.location.address.LocationAddressHelper;
import com.bouzidi.prayer_times.location.tracker.LocationTrackerHelper;
import com.bouzidi.prayer_times.timings.CalculationMethodEnum;
import com.bouzidi.prayer_times.timings.DayPrayer;
import com.bouzidi.prayer_times.timings.Prayer;
import com.bouzidi.prayer_times.timings.PrayerHelper;
import com.bouzidi.prayer_times.utils.PrayerUtils;
import com.bouzidi.prayer_times.utils.TimingUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private TextView locationTextView;
    private TextView hijriTextView;
    private TextView gregorianTextView;
    private TextView prayerNametextView;
    private TextView prayerTimetextView;
    private TextView timeRemainingTextView;
    private CircularProgressBar circularProgressBar;

    private CountDownTimer TimeRemainingCTimer = null;
    private DayPrayer dayPrayer = null;
    private MainActivity mainActivity;
    private PrayerRegistry prayerRegistry;
    private CompositeDisposable disposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        prayerRegistry = PrayerRegistry.getInstance(mainActivity);

        locationTextView = mainActivity.findViewById(R.id.locationTextView);
        hijriTextView = mainActivity.findViewById(R.id.hijriTextView);
        gregorianTextView = mainActivity.findViewById(R.id.gregorianTextView);
        prayerNametextView = mainActivity.findViewById(R.id.prayerNametextView);
        prayerTimetextView = mainActivity.findViewById(R.id.prayerTimetextView);
        timeRemainingTextView = mainActivity.findViewById(R.id.timeRemainingTextView);
        circularProgressBar = mainActivity.findViewById(R.id.circularProgressBar);

        Location location = LocationTrackerHelper.getLocation(mainActivity);

        disposable = new CompositeDisposable();
        disposable.add(
                LocationAddressHelper.getAddressFromLocation(location.getLatitude(), location.getLongitude(), mainActivity)
                        .flatMap(
                                address ->
                                        PrayerHelper.getTimingsByCity(
                                                Calendar.getInstance().getTime(),
                                                address.getLocality(),
                                                address.getCountryName(),
                                                CalculationMethodEnum.getDefault(),
                                                mainActivity
                                        ))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DayPrayer>() {
                            @Override
                            public void onSuccess(DayPrayer dayPrayer) {
                                updateNextPrayerViews(dayPrayer);
                                updateDateTextViews(dayPrayer);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            }
                        }));
    }

    @Override
    public void onDestroy() {
        cancelTimer();
        disposable.dispose();
        super.onDestroy();
    }

    // FIXEME : Find a good way to do !
    private void updateNextPrayerViews(DayPrayer dayPrayer) {
        Date currentTime = Calendar.getInstance().getTime();

        Prayer[] prayers = dayPrayer.getPrayers();

        long timeRemaining;
        int nextPrayerIndex =
                PrayerUtils.getNextPrayerIndex(prayers, currentTime);
        if (nextPrayerIndex == -1) {
            timeRemaining = TimingUtils.getRemainingTiming(
                    currentTime,
                    prayers[0].getTiming(), true);
        } else {
            timeRemaining = TimingUtils.getRemainingTiming(
                    currentTime,
                    prayers[nextPrayerIndex].getTiming(), false);
        }

        long timeBetween;
        if (nextPrayerIndex == 0) {
            timeBetween = TimingUtils.getTimingBetween(
                    prayers[4].getTiming(),
                    prayers[nextPrayerIndex].getTiming(),
                    true
            );
        } else if (nextPrayerIndex == -1) {
            nextPrayerIndex = 4;
            timeBetween = TimingUtils.getTimingBetween(
                    prayers[nextPrayerIndex].getTiming(),
                    prayers[nextPrayerIndex - 1].getTiming(),
                    false
            );
        } else {
            timeBetween = TimingUtils.getTimingBetween(
                    prayers[nextPrayerIndex].getTiming(),
                    prayers[nextPrayerIndex - 1].getTiming(),
                    false
            );
        }

        String nextPrayerKey = prayers[nextPrayerIndex].getKey().toString();
        String nextPrayerTime = prayers[nextPrayerIndex].getTiming();

        String prayerName = mainActivity.getResources().getString(
                getResources().getIdentifier(nextPrayerKey, "string", mainActivity.getPackageName()));

        prayerNametextView.setText(prayerName);
        prayerTimetextView.setText(nextPrayerTime);
        timeRemainingTextView.setText(TimingUtils.formatTime(timeRemaining));

        startAnimationTimer(timeRemaining, timeBetween, dayPrayer);
    }

    private void updateDateTextViews(DayPrayer dayPrayer) {
        String hijriDate = TimingUtils.formatDate(
                dayPrayer.getHijriDay(),
                "Shawwal",
                dayPrayer.getHijriYear()
        );

        String gregorianDate = TimingUtils.formatDate(
                dayPrayer.getGregorianDay(),
                "May",
                dayPrayer.getGregorianYear()
        );

        hijriTextView.setText(hijriDate);
        gregorianTextView.setText(gregorianDate);
        String locationText = dayPrayer.getCity() + "," + dayPrayer.getCountry();
        locationTextView.setText(locationText);
    }

    private float getProgressBarPercentage(long timeRemaining, long timeBetween) {
        return 100 - ((float) (timeRemaining * 100) / (timeBetween));
    }

    private void startAnimationTimer(final long timeRemaining, final long timeBetween, final DayPrayer dayPrayer) {
        circularProgressBar.setProgressWithAnimation(getProgressBarPercentage(timeRemaining, timeBetween), 500L);
        TimeRemainingCTimer = new CountDownTimer(timeRemaining, 1000L) {
            public void onTick(long millisUntilFinished) {
                timeRemainingTextView.setText(TimingUtils.formatTime(millisUntilFinished));
                circularProgressBar.setProgress(getProgressBarPercentage(timeRemaining, timeBetween));
            }

            public void onFinish() {
                updateNextPrayerViews(dayPrayer);
            }
        };
        TimeRemainingCTimer.start();
    }

    private void cancelTimer() {
        if (TimeRemainingCTimer != null)
            TimeRemainingCTimer.cancel();
    }
}