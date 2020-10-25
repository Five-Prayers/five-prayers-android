package com.hbouzidi.fiveprayers.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.network.NetworkUtil;
import com.hbouzidi.fiveprayers.notifier.NotifierService;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.common.HijriHoliday;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.ui.AlertHelper;
import com.hbouzidi.fiveprayers.ui.clock.ClockView;
import com.hbouzidi.fiveprayers.utils.PrayerUtils;
import com.hbouzidi.fiveprayers.utils.TimingUtils;
import com.hbouzidi.fiveprayers.utils.UiUtils;
import com.faltenreich.skeletonlayout.Skeleton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private final int disabledColor = 0xFFD81B60;
    private final int enabledColor = 0xFF00C167;
    private LocalDateTime todayDate;
    private CountDownTimer TimeRemainingCTimer;
    private Context context;

    private TextView countryTextView;
    private TextView locationTextView;
    private TextView hijriTextView;
    private TextView holidayIndicatorTextView;
    private TextView gregorianTextView;
    private TextView prayerNametextView;
    private TextView prayerTimetextView;
    private TextView timeRemainingTextView;
    private TextView fajrTimingTextView;
    private TextView dohrTimingTextView;
    private TextView asrTimingTextView;
    private TextView maghribTimingTextView;
    private TextView ichaTimingTextView;
    private TextView sunriseTimingTextView;
    private TextView sunsetTimingTextView;
    private TextView fajrLabel;
    private TextView dohrLabel;
    private TextView asrLabel;
    private TextView maghribLabel;
    private TextView ichaLabel;

    private ClockView fajrClock;
    private ClockView dohrClock;
    private ClockView asrClock;
    private ClockView maghribClock;
    private ClockView ichaClock;

    private CircularProgressBar circularProgressBar;
    private String adhanCallsPreferences;
    private String adhanCallKeyPart;
    private Skeleton skeleton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = requireActivity().getApplicationContext();
        todayDate = LocalDateTime.now();

        adhanCallsPreferences = PreferencesConstants.ADTHAN_CALLS_SHARED_PREFERENCES;
        adhanCallKeyPart = PreferencesConstants.ADTHAN_CALL_ENABLED_KEY;

        HomeViewModel dashboardViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(rootView);

        skeleton.showSkeleton();

        dashboardViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            AlertHelper.displayAlertDialog(requireActivity(), getResources().getString(R.string.common_error), error);
        });

        dashboardViewModel.getDayPrayers().observe(getViewLifecycleOwner(), dayPrayer -> {
            updateDatesTextViews(dayPrayer);
            updateNextPrayerViews(dayPrayer);
            updateTimingsTextViews(dayPrayer);
            startNotifierService(dayPrayer);

            skeleton.showOriginal();
        });

        ViewTreeObserver observer = rootView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (!NetworkUtil.isNetworkAvailable(context)) {
                    AlertHelper.displayAlertDialog(requireActivity(), getResources().getString(R.string.common_error), getResources().getString(R.string.network_unavailable));
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }

    private void initializeViews(View rootView) {
        skeleton = rootView.findViewById(R.id.skeletonLayout);

        locationTextView = rootView.findViewById(R.id.location_text_view);
        countryTextView = rootView.findViewById(R.id.country_text_view);
        hijriTextView = rootView.findViewById(R.id.hijriTextView);
        gregorianTextView = rootView.findViewById(R.id.gregorianTextView);
        holidayIndicatorTextView = rootView.findViewById(R.id.holiday_indicator_text_view);
        prayerNametextView = rootView.findViewById(R.id.prayerNametextView);
        prayerTimetextView = rootView.findViewById(R.id.prayerTimetextView);
        timeRemainingTextView = rootView.findViewById(R.id.timeRemainingTextView);
        circularProgressBar = rootView.findViewById(R.id.circularProgressBar);

        fajrClock = rootView.findViewById(R.id.farj_clock_view);
        dohrClock = rootView.findViewById(R.id.dohr_clock_view);
        asrClock = rootView.findViewById(R.id.asr_clock_view);
        maghribClock = rootView.findViewById(R.id.maghreb_clock_view);
        ichaClock = rootView.findViewById(R.id.ichaa_clock_view);

        fajrTimingTextView = rootView.findViewById(R.id.fajr_timing_text_view);

        ImageView fajrCallImageView = rootView.findViewById(R.id.fajr_call_image_view);
        initializeImageViewIcon(fajrCallImageView, PrayerEnum.FAJR);

        ImageView dohrCallImageView = rootView.findViewById(R.id.dohr_call_image_view);
        initializeImageViewIcon(dohrCallImageView, PrayerEnum.DHOHR);

        ImageView asrCallImageView = rootView.findViewById(R.id.asr_call_image_view);
        initializeImageViewIcon(asrCallImageView, PrayerEnum.ASR);

        ImageView maghrebCallImageView = rootView.findViewById(R.id.maghrib_call_image_view);
        initializeImageViewIcon(maghrebCallImageView, PrayerEnum.MAGHRIB);

        ImageView ichaCallImageView = rootView.findViewById(R.id.icha_call_image_view);
        initializeImageViewIcon(ichaCallImageView, PrayerEnum.ICHA);


        dohrTimingTextView = rootView.findViewById(R.id.dohr_timing_text_view);
        asrTimingTextView = rootView.findViewById(R.id.asr_timing_text_view);
        maghribTimingTextView = rootView.findViewById(R.id.maghreb_timing_text_view);
        ichaTimingTextView = rootView.findViewById(R.id.icha_timing_text_view);

        sunriseTimingTextView = rootView.findViewById(R.id.sunrise_timing_text_view);
        sunsetTimingTextView = rootView.findViewById(R.id.sunset_timing_text_view);

        fajrLabel = rootView.findViewById(R.id.fajr_label_text_view);
        dohrLabel = rootView.findViewById(R.id.dohr_label_text_view);
        asrLabel = rootView.findViewById(R.id.asr_label_text_view);
        maghribLabel = rootView.findViewById(R.id.maghrib_label_text_view);
        ichaLabel = rootView.findViewById(R.id.icha_label_text_view);
    }

    private void updateTimingsTextViews(DayPrayer dayPrayer) {
        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();

        LocalDateTime fajrTiming = timings.get(PrayerEnum.FAJR);
        LocalDateTime dohrTiming = timings.get(PrayerEnum.DHOHR);
        LocalDateTime asrTiming = timings.get(PrayerEnum.ASR);
        LocalDateTime maghribTiming = timings.get(PrayerEnum.MAGHRIB);
        LocalDateTime ichaTiming = timings.get(PrayerEnum.ICHA);

        updateClockTime(fajrClock, Objects.requireNonNull(fajrTiming).getHour(), fajrTiming.getMinute());
        updateClockTime(dohrClock, Objects.requireNonNull(dohrTiming).getHour(), dohrTiming.getMinute());
        updateClockTime(asrClock, Objects.requireNonNull(asrTiming).getHour(), asrTiming.getMinute());
        updateClockTime(maghribClock, Objects.requireNonNull(maghribTiming).getHour(), maghribTiming.getMinute());
        updateClockTime(ichaClock, Objects.requireNonNull(ichaTiming).getHour(), ichaTiming.getMinute());

        fajrTimingTextView.setText(UiUtils.formatTiming(fajrTiming));
        dohrTimingTextView.setText(UiUtils.formatTiming(dohrTiming));
        asrTimingTextView.setText(UiUtils.formatTiming(asrTiming));
        maghribTimingTextView.setText(UiUtils.formatTiming(maghribTiming));
        ichaTimingTextView.setText(UiUtils.formatTiming(ichaTiming));

        LocalDateTime sunriseTiming = dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE);
        LocalDateTime sunsetTiming = dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNSET);

        sunriseTimingTextView.setText(UiUtils.formatTiming(Objects.requireNonNull(sunriseTiming)));
        sunsetTimingTextView.setText(UiUtils.formatTiming(Objects.requireNonNull(sunsetTiming)));

        fajrLabel.setText(R.string.FAJR);
        dohrLabel.setText(R.string.DHOHR);
        asrLabel.setText(R.string.ASR);
        maghribLabel.setText(R.string.MAGHRIB);
        ichaLabel.setText(R.string.ICHA);
    }

    private void updateClockTime(ClockView clock, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        clock.setColor(0xFF17C5FF);
        clock.setCalendar(calendar);
    }

    private void updateNextPrayerViews(DayPrayer dayPrayer) {
        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();

        PrayerEnum nextPrayerKey = PrayerUtils.getNextPrayer(timings, LocalDateTime.now());
        PrayerEnum previousPrayerKey = PrayerUtils.getPreviousPrayerKey(nextPrayerKey);

        long timeRemaining = TimingUtils.getTimeBetweenTwoPrayer(todayDate, Objects.requireNonNull(timings.get(nextPrayerKey)));
        long timeBetween = TimingUtils.getTimeBetweenTwoPrayer(Objects.requireNonNull(timings.get(previousPrayerKey)), Objects.requireNonNull(timings.get(nextPrayerKey)));

        String prayerName = context.getResources().getString(
                getResources().getIdentifier(nextPrayerKey.toString(), "string", context.getPackageName()));

        prayerNametextView.setText(prayerName);
        prayerTimetextView.setText(UiUtils.formatTiming(Objects.requireNonNull(timings.get(nextPrayerKey))));
        timeRemainingTextView.setText(UiUtils.formatTimeForTimer(timeRemaining));

        startAnimationTimer(timeRemaining, timeBetween, dayPrayer);
    }

    private void updateDatesTextViews(DayPrayer dayPrayer) {
        holidayIndicatorTextView.setVisibility(View.INVISIBLE);

        String hijriMonth = context.getResources().getString(
                getResources().getIdentifier("hijri_month_" + dayPrayer.getHijriMonthNumber(), "string", context.getPackageName()));

        String hijriDate = UiUtils.formatHijriDate(
                dayPrayer.getHijriDay(),
                hijriMonth,
                dayPrayer.getHijriYear()
        );

        ZonedDateTime zonedDateTime = TimingUtils.getZonedDateTimeFromTimestamps(dayPrayer.getTimestamp(), dayPrayer.getTimezone());
        String gregorianDate = UiUtils.formatReadableGregorianDate(zonedDateTime);
        String timezone = UiUtils.formatReadableTimezone(zonedDateTime);

        hijriTextView.setText(StringUtils.capitalize(hijriDate));
        gregorianTextView.setText(StringUtils.capitalize(gregorianDate));
        String locationText = dayPrayer.getCity();
        String country = dayPrayer.getCountry() + " (" + timezone + ")";
        countryTextView.setText(StringUtils.capitalize(country));
        locationTextView.setText(StringUtils.capitalize(locationText));

        HijriHoliday holiday = HijriHoliday.getHoliday(dayPrayer.getHijriDay(), dayPrayer.getHijriMonthNumber());

        if (holiday != null) {
            String holidayName = getResources().getString(
                    getResources().getIdentifier(holiday.toString(), "string", context.getPackageName()));

            holidayIndicatorTextView.setText(holidayName);
            holidayIndicatorTextView.setVisibility(View.VISIBLE);
        }
    }

    private float getProgressBarPercentage(long timeRemaining, long timeBetween) {
        return 100 - ((float) (timeRemaining * 100) / (timeBetween));
    }

    private void startAnimationTimer(final long timeRemaining, final long timeBetween, final DayPrayer dayPrayer) {
        circularProgressBar.setProgressWithAnimation(getProgressBarPercentage(timeRemaining, timeBetween), 1000L);
        TimeRemainingCTimer = new CountDownTimer(timeRemaining, 1000L) {
            public void onTick(long millisUntilFinished) {
                timeRemainingTextView.setText(UiUtils.formatTimeForTimer(millisUntilFinished));
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

    private void initializeImageViewIcon(ImageView adhanCallImageView, PrayerEnum prayerEnum) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(adhanCallsPreferences, MODE_PRIVATE);
        String callPreferenceKey = prayerEnum.toString() + adhanCallKeyPart;

        boolean adhanCallEnabled = sharedPreferences.getBoolean(callPreferenceKey, false);

        adhanCallImageView.setImageResource(adhanCallEnabled ? R.drawable.ic_notifications_24dp : R.drawable.ic_notifications_off_24dp);
        adhanCallImageView.setColorFilter(adhanCallEnabled ? enabledColor : disabledColor);

        setNotifImgOnClickListener(adhanCallImageView, callPreferenceKey);
    }

    private void setNotifImgOnClickListener(ImageView imageView, String callPreferenceKey) {
        imageView.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences(adhanCallsPreferences, MODE_PRIVATE);

            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibe.vibrate(10);
            }

            boolean adhanCallEnabled = sharedPreferences.getBoolean(callPreferenceKey, false);

            imageView.setImageResource(adhanCallEnabled ? R.drawable.ic_notifications_off_24dp : R.drawable.ic_notifications_24dp);
            imageView.setColorFilter(adhanCallEnabled ? disabledColor : enabledColor);

            SharedPreferences.Editor edit = sharedPreferences.edit();

            edit.putBoolean(callPreferenceKey, !adhanCallEnabled);
            edit.apply();
        });
    }

    private void startNotifierService(DayPrayer dayPrayer) {
        Intent intent = new Intent(context, NotifierService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("dayPrayer", dayPrayer);
        intent.putExtras(bundle);

        NotifierService.enqueueWork(context, intent);
    }
}