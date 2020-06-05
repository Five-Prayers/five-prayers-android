package com.bouzidi.prayer_times.ui.home;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bouzidi.prayer_times.MainActivity;
import com.bouzidi.prayer_times.R;
import com.bouzidi.prayer_times.notifier.NotifierHelper;
import com.bouzidi.prayer_times.timings.DayPrayer;
import com.bouzidi.prayer_times.timings.PrayerEnum;
import com.bouzidi.prayer_times.ui.clock.ClockView;
import com.bouzidi.prayer_times.utils.PrayerUtils;
import com.bouzidi.prayer_times.utils.TimingUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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

    private HomeViewModel dashboardViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(root);

        dashboardViewModel.getDayPrayers().observe(getViewLifecycleOwner(), new Observer<DayPrayer>() {
            @Override
            public void onChanged(@Nullable DayPrayer dayPrayer) {
                updateNextPrayerViews(dayPrayer);
                updateDatesTextViews(dayPrayer);
                updateTimingsTextViews(dayPrayer);
                NotifierHelper.scheduleNextPrayerAlarms(mainActivity, dayPrayer);
            }
        });
        return root;
    }

    private void initializeViews(View root) {
        mainActivity = (MainActivity) getActivity();

        todayDate = Calendar.getInstance().getTime();

        locationTextView = root.findViewById(R.id.locationTextView);
        hijriTextView = root.findViewById(R.id.hijriTextView);
        gregorianTextView = root.findViewById(R.id.gregorianTextView);
        prayerNametextView = root.findViewById(R.id.prayerNametextView);
        prayerTimetextView = root.findViewById(R.id.prayerTimetextView);
        timeRemainingTextView = root.findViewById(R.id.timeRemainingTextView);
        circularProgressBar = root.findViewById(R.id.circularProgressBar);

        fajrClock = root.findViewById(R.id.farj_clock_view);
        dohrClock = root.findViewById(R.id.dohr_clock_view);
        asrClock = root.findViewById(R.id.asr_clock_view);
        maghribClock = root.findViewById(R.id.maghreb_clock_view);
        ichaClock = root.findViewById(R.id.ichaa_clock_view);

        fajrTimingTextView = root.findViewById(R.id.fajr_timing_text_view);
        dohrTimingTextView = root.findViewById(R.id.dohr_timing_text_view);
        asrTimingTextView = root.findViewById(R.id.asr_timing_text_view);
        maghribTimingTextView = root.findViewById(R.id.maghrib_timing_text_view);
        ichaTimingTextView = root.findViewById(R.id.icha_timing_text_view);

        fajrLabel = root.findViewById(R.id.fajr_label_text_view);
        dohrLabel = root.findViewById(R.id.dohr_label_text_view);
        asrLabel = root.findViewById(R.id.asr_label_text_view);
        maghribLabel = root.findViewById(R.id.maghrib_label_text_view);
        ichaLabel = root.findViewById(R.id.icha_label_text_view);
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
        clock.setColor(0xFF17C5FF);
        clock.setCalendar(calendar);
    }

    @Override
    public void onDestroy() {
        cancelTimer();
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

        hijriTextView.setText(StringUtils.capitalize(hijriDate));
        gregorianTextView.setText(StringUtils.capitalize(gregorianDate));
        String locationText = dayPrayer.getCity(); // + ", " + dayPrayer.getCountry() + " (" + TimeZoneFormat.format(todayDate) + ")";
        locationTextView.setText(StringUtils.capitalize(locationText));
    }

    private float getProgressBarPercentage(long timeRemaining, long timeBetween) {
        return 100 - ((float) (timeRemaining * 100) / (timeBetween));
    }

    private void startAnimationTimer(final long timeRemaining, final long timeBetween, final DayPrayer dayPrayer) {
        circularProgressBar.setProgressWithAnimation(getProgressBarPercentage(timeRemaining, timeBetween), 1000L);
        TimeRemainingCTimer = new CountDownTimer(timeRemaining, 1000L) {
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