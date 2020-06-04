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
import com.bouzidi.prayer_times.location.address.LocationAddressHelper;
import com.bouzidi.prayer_times.location.tracker.LocationTrackerHelper;
import com.bouzidi.prayer_times.notifier.NotifierHelper;
import com.bouzidi.prayer_times.timings.CalculationMethodEnum;
import com.bouzidi.prayer_times.timings.DayPrayer;
import com.bouzidi.prayer_times.timings.PrayerEnum;
import com.bouzidi.prayer_times.timings.PrayerHelper;
import com.bouzidi.prayer_times.ui.clock.ClockView;
import com.bouzidi.prayer_times.utils.PrayerUtils;
import com.bouzidi.prayer_times.utils.TimingUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
    private MainActivity mainActivity;
    private CompositeDisposable disposable;

    private ClockView fajrClock;
    private ClockView dohrClock;
    private ClockView asrClock;
    private ClockView maghribClock;
    private ClockView ichaClock;

    private TextView fajrTimingTextView;
    private TextView dohrTimingTextView;
    private TextView asrTimingTextView;
    private TextView maghribTimingTextView;
    private TextView ichaTimingTextView;
    private TextView fajrLabel;
    private TextView dohrLabel;
    private TextView asrLabel;
    private TextView maghribLabel;
    private TextView ichaLabel;
    private Date todayDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        todayDate = Calendar.getInstance().getTime();

        locationTextView = mainActivity.findViewById(R.id.locationTextView);
        hijriTextView = mainActivity.findViewById(R.id.hijriTextView);
        gregorianTextView = mainActivity.findViewById(R.id.gregorianTextView);
        prayerNametextView = mainActivity.findViewById(R.id.prayerNametextView);
        prayerTimetextView = mainActivity.findViewById(R.id.prayerTimetextView);
        timeRemainingTextView = mainActivity.findViewById(R.id.timeRemainingTextView);
        circularProgressBar = mainActivity.findViewById(R.id.circularProgressBar);

        fajrClock = mainActivity.findViewById(R.id.farj_clock_view);
        dohrClock = mainActivity.findViewById(R.id.dohr_clock_view);
        asrClock = mainActivity.findViewById(R.id.asr_clock_view);
        maghribClock = mainActivity.findViewById(R.id.maghreb_clock_view);
        ichaClock = mainActivity.findViewById(R.id.ichaa_clock_view);

        fajrTimingTextView = mainActivity.findViewById(R.id.fajr_timing_text_view);
        dohrTimingTextView = mainActivity.findViewById(R.id.dohr_timing_text_view);
        asrTimingTextView = mainActivity.findViewById(R.id.asr_timing_text_view);
        maghribTimingTextView = mainActivity.findViewById(R.id.maghrib_timing_text_view);
        ichaTimingTextView = mainActivity.findViewById(R.id.icha_timing_text_view);

        fajrLabel = mainActivity.findViewById(R.id.fajr_label_text_view);
        dohrLabel = mainActivity.findViewById(R.id.dohr_label_text_view);
        asrLabel = mainActivity.findViewById(R.id.asr_label_text_view);
        maghribLabel = mainActivity.findViewById(R.id.maghrib_label_text_view);
        ichaLabel = mainActivity.findViewById(R.id.icha_label_text_view);

        Location location = LocationTrackerHelper.getLocation(mainActivity);

        disposable = new CompositeDisposable();
        disposable.add(
                LocationAddressHelper.getAddressFromLocation(location.getLatitude(), location.getLongitude(), mainActivity)
                        .flatMap(
                                address ->
                                        PrayerHelper.getTimingsByCity(
                                                todayDate,
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
                                updateDatesTextViews(dayPrayer);
                                updateTimingsTextViews(dayPrayer);
                                NotifierHelper.scheduleNextPrayerAlarms(mainActivity, dayPrayer);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            }
                        }));
    }

    private void updateTimingsTextViews(DayPrayer dayPrayer) {
        Map<PrayerEnum, String> timings = dayPrayer.getTimings();

        String fajrTiming = timings.get(PrayerEnum.FAJR);
        String dohrTiming = timings.get(PrayerEnum.DHOHR);
        String asrTiming = timings.get(PrayerEnum.ASR);
        String maghribTiming = timings.get(PrayerEnum.MAGHRIB);
        String ichaTiming = timings.get(PrayerEnum.ICHA);

        updateClockTime(fajrClock, getTimingPart(Objects.requireNonNull(fajrTiming))[0], getTimingPart(Objects.requireNonNull(fajrTiming))[1]);
        updateClockTime(dohrClock, getTimingPart(Objects.requireNonNull(dohrTiming))[0], getTimingPart(Objects.requireNonNull(dohrTiming))[1]);
        updateClockTime(asrClock, getTimingPart(Objects.requireNonNull(asrTiming))[0], getTimingPart(Objects.requireNonNull(asrTiming))[1]);
        updateClockTime(maghribClock, getTimingPart(Objects.requireNonNull(maghribTiming))[0], getTimingPart(Objects.requireNonNull(maghribTiming))[1]);
        updateClockTime(ichaClock, getTimingPart(Objects.requireNonNull(ichaTiming))[0], getTimingPart(Objects.requireNonNull(ichaTiming))[1]);

        fajrTimingTextView.setText(fajrTiming);
        dohrTimingTextView.setText(dohrTiming);
        asrTimingTextView.setText(asrTiming);
        maghribTimingTextView.setText(maghribTiming);
        ichaTimingTextView.setText(ichaTiming);

        fajrLabel.setText(R.string.FAJR);
        dohrLabel.setText(R.string.DHOHR);
        asrLabel.setText(R.string.ASR);
        maghribLabel.setText(R.string.MAGHRIB);
        ichaLabel.setText(R.string.ICHA);
    }

    private String[] getTimingPart(String timing) {
        String[] parts = timing.split(":");
        return parts;
    }

    private void updateClockTime(ClockView clock, String hour, String minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.valueOf(minute));
        clock.setColor(0xFF9B0493);
        clock.setCalendar(calendar);
    }

    @Override
    public void onDestroy() {
        cancelTimer();
        disposable.dispose();
        super.onDestroy();
    }

    private void updateNextPrayerViews(DayPrayer dayPrayer) {
        Map<PrayerEnum, String> timings = dayPrayer.getTimings();

        PrayerEnum nextPrayerKey = PrayerUtils.getNextPrayer(timings, todayDate);
        PrayerEnum previousPrayerKey = PrayerUtils.getPreviousPrayerKey(nextPrayerKey);

        long timeRemaining = TimingUtils.getRemainingTiming(todayDate, Objects.requireNonNull(timings.get(nextPrayerKey)));
        long timeBetween = TimingUtils.getTimingBetween(Objects.requireNonNull(timings.get(previousPrayerKey)), Objects.requireNonNull(timings.get(nextPrayerKey)));

        String prayerName = mainActivity.getResources().getString(
                getResources().getIdentifier(nextPrayerKey.toString(), "string", mainActivity.getPackageName()));

        prayerNametextView.setText(prayerName);
        prayerTimetextView.setText(timings.get(nextPrayerKey));
        timeRemainingTextView.setText(TimingUtils.formatTimeForTimer(timeRemaining));

        startAnimationTimer(timeRemaining, timeBetween, dayPrayer);
    }

    private void updateDatesTextViews(DayPrayer dayPrayer) {
        String hijriMonth = mainActivity.getResources().getString(
                getResources().getIdentifier("hijri_month_" + dayPrayer.getHijriMonthNumber(), "string", mainActivity.getPackageName()));

        String hijriDate = TimingUtils.formatDate(
                dayPrayer.getHijriDay(),
                hijriMonth,
                dayPrayer.getHijriYear()
        );

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd MMMM, yyyy", Locale.getDefault());
        SimpleDateFormat TimeZoneFormat = new SimpleDateFormat("ZZZZZ", Locale.getDefault());
        String gregorianDate = simpleDateFormat.format(todayDate);

        hijriTextView.setText(hijriDate);
        gregorianTextView.setText(gregorianDate);
        String locationText = dayPrayer.getCity() + ", " + dayPrayer.getCountry() + " (" + TimeZoneFormat.format(todayDate) + ")";
        locationTextView.setText(locationText);
    }

    private float getProgressBarPercentage(long timeRemaining, long timeBetween) {
        return 100 - ((float) (timeRemaining * 100) / (timeBetween));
    }

    private void startAnimationTimer(final long timeRemaining, final long timeBetween, final DayPrayer dayPrayer) {
        circularProgressBar.setProgressWithAnimation(getProgressBarPercentage(timeRemaining, timeBetween), 1000L);
        TimeRemainingCTimer = new CountDownTimer(timeRemaining, 1000L * 60) {
            public void onTick(long millisUntilFinished) {
                timeRemainingTextView.setText(TimingUtils.formatTimeForTimer(millisUntilFinished));
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