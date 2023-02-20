package com.hbouzidi.fiveprayers.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.faltenreich.skeletonlayout.Skeleton;
import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.job.WorkCreator;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.ui.clock.AnalogClock;
import com.hbouzidi.fiveprayers.ui.widget.WidgetUpdater;
import com.hbouzidi.fiveprayers.utils.AlertHelper;
import com.hbouzidi.fiveprayers.utils.PrayerUtils;
import com.hbouzidi.fiveprayers.utils.TimingUtils;
import com.hbouzidi.fiveprayers.utils.UiUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import cl.jesualex.stooltip.Position;
import cl.jesualex.stooltip.Tooltip;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class HomeFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    WidgetUpdater widgetUpdater;

    @Inject
    PreferencesHelper preferencesHelper;

    private LocalDateTime todayDate;
    private CountDownTimer TimeRemainingCTimer;

    private TextView locationTextView;
    private TextView calculationMethodTextView;
    //   private TextView holidayIndicatorTextView;
    private TextView todayDateTextView;
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

    private AnalogClock fajrClock;
    private AnalogClock dohrClock;
    private AnalogClock asrClock;
    private AnalogClock maghribClock;
    private AnalogClock ichaClock;

    private ImageView calculationModeIndicator;

    private CircularProgressBar circularProgressBar;
    private String adhanCallsPreferences;
    private String adhanCallKeyPart;
    private Skeleton skeleton;

    @Override
    public void onAttach(@NonNull Context context) {
        ((FivePrayerApplication) requireContext().getApplicationContext())
                .appComponent
                .homeComponent()
                .create()
                .inject(this);

        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        todayDate = LocalDateTime.now();

        adhanCallsPreferences = PreferencesConstants.ADTHAN_CALLS_SHARED_PREFERENCES;
        adhanCallKeyPart = PreferencesConstants.ADTHAN_CALL_ENABLED_KEY;

        TypedArray typedArray = requireContext().getTheme().obtainStyledAttributes(R.styleable.mainStyles);
        int navigationBackgroundStartColor = typedArray.getColor(R.styleable.mainStyles_navigationBackgroundStartColor, ContextCompat.getColor(requireContext(), R.color.alabaster));
        int navigationBackgroundEndColor = typedArray.getColor(R.styleable.mainStyles_navigationBackgroundEndColor, ContextCompat.getColor(requireContext(), R.color.alabaster));
        typedArray.recycle();

        HomeViewModel homeViewModel = viewModelFactory.create(HomeViewModel.class);

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(rootView);

        skeleton.setMaskColor(navigationBackgroundStartColor);
        skeleton.setShimmerColor(navigationBackgroundEndColor);
        skeleton.showSkeleton();

        homeViewModel
                .getError()
                .observe(
                        getViewLifecycleOwner(),
                        error -> AlertHelper.displayLocationErrorDialog(requireActivity(),
                                getResources().getString(R.string.location_alert_title),
                                error));

        homeViewModel.getDayPrayers().observe(getViewLifecycleOwner(), dayPrayer -> {
            updateDatesTextViews(dayPrayer);
            updateNextPrayerViews(dayPrayer);
            updateTimingsTextViews(dayPrayer);
            startPrayerSchedulerWork(dayPrayer);

            widgetUpdater.updateHomeScreenWidgets(requireContext());

            skeleton.showOriginal();

            showWhatsNewDialog();
        });

        ViewTreeObserver observer = rootView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        return rootView;
    }

    private void showWhatsNewDialog() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PreferencesConstants.APP_META, MODE_PRIVATE);
        int previousVersion = sharedPreferences.getInt(PreferencesConstants.PREVIOUS_INSTALLED_VERSION, 0);

        if (previousVersion < BuildConfig.VERSION_CODE) {
            LovelyCustomDialog customInformationDialog = AlertHelper.createCustomInformationDialog(requireContext(),
                    getResources().getString(R.string.whats_new_dialog_title),
                    getResources().getString(R.string.whats_new_dialog_message)
            );

            customInformationDialog.setListener(R.id.btnOK, v -> {
                sharedPreferences
                        .edit()
                        .putInt(PreferencesConstants.PREVIOUS_INSTALLED_VERSION, BuildConfig.VERSION_CODE)
                        .apply();
                customInformationDialog.dismiss();
            });

            customInformationDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }

    private void initializeViews(View rootView) {
        skeleton = rootView.findViewById(R.id.skeletonLayout);

        locationTextView = rootView.findViewById(R.id.location_text_view);
        todayDateTextView = rootView.findViewById(R.id.todayDateTextView);
        //    holidayIndicatorTextView = rootView.findViewById(R.id.holiday_indicator_text_view);
        prayerNametextView = rootView.findViewById(R.id.prayerNametextView);
        prayerTimetextView = rootView.findViewById(R.id.prayerTimetextView);
        timeRemainingTextView = rootView.findViewById(R.id.timeRemainingTextView);
        circularProgressBar = rootView.findViewById(R.id.circularProgressBar);
        calculationMethodTextView = rootView.findViewById(R.id.calculation_method_text_view);

        fajrClock = rootView.findViewById(R.id.farj_clock_view);
        dohrClock = rootView.findViewById(R.id.dohr_clock_view);
        asrClock = rootView.findViewById(R.id.asr_clock_view);
        maghribClock = rootView.findViewById(R.id.maghreb_clock_view);
        ichaClock = rootView.findViewById(R.id.ichaa_clock_view);

        fajrTimingTextView = rootView.findViewById(R.id.fajr_timing_text_view);

        ImageView fajrCallImageView = rootView.findViewById(R.id.fajr_call_image_view);
        ConstraintLayout fajrCallConstraintLayout = rootView.findViewById(R.id.fajr_call_constraint_layout);
        initializeImageViewIcon(fajrCallConstraintLayout, fajrCallImageView, PrayerEnum.FAJR);

        ImageView dohrCallImageView = rootView.findViewById(R.id.dohr_call_image_view);
        ConstraintLayout dohrCallConstraintLayout = rootView.findViewById(R.id.dohr_call_constraint_layout);
        initializeImageViewIcon(dohrCallConstraintLayout, dohrCallImageView, PrayerEnum.DHOHR);

        ImageView asrCallImageView = rootView.findViewById(R.id.asr_call_image_view);
        ConstraintLayout asrCallConstraintLayout = rootView.findViewById(R.id.asr_call_constraint_layout);
        initializeImageViewIcon(asrCallConstraintLayout, asrCallImageView, PrayerEnum.ASR);

        ImageView maghrebCallImageView = rootView.findViewById(R.id.maghreb_call_image_view);
        ConstraintLayout maghrebCallConstraintLayout = rootView.findViewById(R.id.maghreb_call_constraint_layout);
        initializeImageViewIcon(maghrebCallConstraintLayout, maghrebCallImageView, PrayerEnum.MAGHRIB);

        ImageView ichaCallImageView = rootView.findViewById(R.id.icha_call_image_view);
        ConstraintLayout ichaCallConstraintLayout = rootView.findViewById(R.id.icha_call_constraint_layout);
        initializeImageViewIcon(ichaCallConstraintLayout, ichaCallImageView, PrayerEnum.ICHA);


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

        calculationModeIndicator = rootView.findViewById(R.id.calculation_mode_indicator);
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

    private void updateClockTime(AnalogClock clock, int hour, int minute) {
        clock.setTime(hour, minute, 0);
    }

    private void updateNextPrayerViews(DayPrayer dayPrayer) {
        Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();

        PrayerEnum nextPrayerKey = PrayerUtils.getNextPrayer(timings, LocalDateTime.now());
        PrayerEnum previousPrayerKey = PrayerUtils.getPreviousPrayerKey(nextPrayerKey);

        long timeRemaining = TimingUtils.getTimeBetweenTwoPrayer(todayDate, Objects.requireNonNull(timings.get(nextPrayerKey)));
        long timeBetween = TimingUtils.getTimeBetweenTwoPrayer(Objects.requireNonNull(timings.get(previousPrayerKey)), Objects.requireNonNull(timings.get(nextPrayerKey)));

        String prayerName = requireContext().getResources().getString(
                getResources().getIdentifier(nextPrayerKey.toString(), "string", requireContext().getPackageName()));

        prayerNametextView.setText(prayerName);
        prayerTimetextView.setText(UiUtils.formatTiming(Objects.requireNonNull(timings.get(nextPrayerKey))));
        timeRemainingTextView.setText(UiUtils.formatTimeForTimer(timeRemaining));

        startAnimationTimer(timeRemaining, timeBetween, dayPrayer);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateDatesTextViews(DayPrayer dayPrayer) {
        //holidayIndicatorTextView.setVisibility(View.INVISIBLE);

        ZonedDateTime zonedDateTime = TimingUtils.getZonedDateTimeFromTimestamps(dayPrayer.getTimestamp(), dayPrayer.getTimezone());
        String nameOfTheDay = zonedDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());

        String hijriMonth = requireContext().getResources().getString(
                getResources().getIdentifier("hijri_month_" + dayPrayer.getHijriMonthNumber(), "string", requireContext().getPackageName()));

        String hijriDate = UiUtils.formatFullHijriDate(
                nameOfTheDay,
                dayPrayer.getHijriDay(),
                hijriMonth,
                dayPrayer.getHijriYear()
        );

        String gregorianDate = UiUtils.formatReadableGregorianDate(zonedDateTime);
        String timezone = UiUtils.formatReadableTimezone(zonedDateTime);

        todayDateTextView.setText(StringUtils.capitalize(hijriDate));

        todayDateTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (todayDateTextView.getText().equals(StringUtils.capitalize(hijriDate))) {
                    todayDateTextView.setText(StringUtils.capitalize(gregorianDate));
                } else {
                    todayDateTextView.setText(StringUtils.capitalize(hijriDate));
                }
            }
            return false;
        });

        String locationText;
        if (dayPrayer.getCity() != null && dayPrayer.getCountry() != null) {
            locationText = StringUtils.capitalize(dayPrayer.getCity());
            locationText += StringUtils.capitalize(" - " + dayPrayer.getCountry() + " (" + timezone + ")");
        } else {
            locationText = UiUtils.convertAndFormatCoordinates(dayPrayer.getLatitude(), dayPrayer.getLongitude());
        }

        locationTextView.setText(locationText);

        String methodKey = String.valueOf(dayPrayer.getCalculationMethodEnum()).toLowerCase();
        String fajrAngle = dayPrayer.getCalculationMethodEnum().getFajrAngle();
        String ichaAngle = dayPrayer.getCalculationMethodEnum().getIchaAngle();
        boolean isIchaAngleInMinute = dayPrayer.getCalculationMethodEnum().isIchaAngleInMinute();
        String tooltipText = formatCalculationMethodAngle(fajrAngle, ichaAngle, isIchaAngleInMinute);


        int id = getResources().getIdentifier("short_method_" + methodKey, "string", requireContext().getPackageName());

        if (id != 0) {
            String methodName = getResources().getString(id);
            calculationMethodTextView.setText(methodName);
        }

        TypedArray typedArray = requireContext().getTheme().obtainStyledAttributes(R.styleable.tooltipStyle);
        int toolTipBackgroundColor = typedArray.getColor(R.styleable.tooltipStyle_tooltipBackgroundColor, ContextCompat.getColor(requireContext(), R.color.alabaster));
        int toolTipTextColor = typedArray.getColor(R.styleable.tooltipStyle_tooltipTextColor, ContextCompat.getColor(requireContext(), R.color.mine_shaft));
        typedArray.recycle();

        calculationMethodTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Tooltip.on(calculationMethodTextView)
                        .text(tooltipText)
                        .textColor(toolTipTextColor)
                        .textSize(13)
                        .color(toolTipBackgroundColor)
                        .border(toolTipTextColor, 1f)
                        .clickToHide(true)
                        .arrowSize(0, 0)
                        .corner(10)
                        .position(Position.END)
                        .show(5000);
            }
            return false;
        });

        calculationModeIndicator.setVisibility(preferencesHelper.isOfflineCalculationMode() ? View.VISIBLE : View.GONE);

//        HijriHoliday holiday = HijriHoliday.getHoliday(dayPrayer.getHijriDay(), dayPrayer.getHijriMonthNumber());
//
//        if (holiday != null) {
//            String holidayName = getResources().getString(
//                    getResources().getIdentifier(holiday.toString(), "string", requireContext().getPackageName()));
//
//                holidayIndicatorTextView.setText(holidayName);
//                holidayIndicatorTextView.setVisibility(View.VISIBLE);
//        }
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

    private void initializeImageViewIcon(ConstraintLayout adhanCallConstraintLayout, ImageView adhanCallImageView, PrayerEnum prayerEnum) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(adhanCallsPreferences, MODE_PRIVATE);
        String callPreferenceKey = prayerEnum.toString() + adhanCallKeyPart;

        boolean adhanCallEnabled = sharedPreferences.getBoolean(callPreferenceKey, false);

        adhanCallImageView.setImageResource(adhanCallEnabled ? R.drawable.ic_notifications_on_24dp : R.drawable.ic_notifications_off_24dp);

        setNotifImgOnClickListener(adhanCallConstraintLayout, adhanCallImageView, callPreferenceKey);
    }

    private void setNotifImgOnClickListener(ConstraintLayout adhanCallConstraintLayout, ImageView imageView, String callPreferenceKey) {
        adhanCallConstraintLayout.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(adhanCallsPreferences, MODE_PRIVATE);

            Vibrator vibe = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibe.vibrate(10);
            }

            boolean adhanCallEnabled = sharedPreferences.getBoolean(callPreferenceKey, false);

            imageView.setImageResource(adhanCallEnabled ? R.drawable.ic_notifications_off_24dp : R.drawable.ic_notifications_on_24dp);

            SharedPreferences.Editor edit = sharedPreferences.edit();

            edit.putBoolean(callPreferenceKey, !adhanCallEnabled);
            edit.apply();
        });
    }

    private void startPrayerSchedulerWork(DayPrayer dayPrayer) {
        WorkCreator.scheduleOneTimePrayerUpdater(requireContext(), dayPrayer);
    }

    private String formatCalculationMethodAngle(String fajrAngle, String ichaAngle, boolean isAngleInMinute) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        StringBuilder stringBuilder = new StringBuilder();

        String formattedIchaAngle;
        if (isAngleInMinute) {
            String[] result = new String[2];
            Pattern p = Pattern.compile("([0-9]{1,2})");
            Matcher m = p.matcher(ichaAngle);
            if (m.find()) {
                result[0] = m.group(1);
            }
            formattedIchaAngle = numberFormat.format(Float.parseFloat(Objects.requireNonNull(result[0])));
        } else {
            formattedIchaAngle = numberFormat.format(Float.parseFloat(Objects.requireNonNull(ichaAngle)));
        }

        stringBuilder
                .append(requireContext().getString(R.string.method_fajr_angle))
                .append(" : ")
                .append(numberFormat.format(Float.parseFloat(fajrAngle)))
                .append("° - ")
                .append(requireContext().getString(R.string.method_ichaa_angle))
                .append(" : ")
                .append(formattedIchaAngle)
                .append(isAngleInMinute ? " " + requireContext().getString(R.string.common_minutes) : "°");

        return stringBuilder.toString();
    }
}