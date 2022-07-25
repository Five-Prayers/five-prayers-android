package com.hbouzidi.fiveprayers.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
import com.hbouzidi.fiveprayers.notifier.PendingIntentCreator;
import com.hbouzidi.fiveprayers.openweathermap.OpenWeatherMapAPIService;
import com.hbouzidi.fiveprayers.openweathermap.OpenWeatherMapResponse;
import com.hbouzidi.fiveprayers.openweathermap.TemperatureUnit;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.TimingServiceFactory;
import com.hbouzidi.fiveprayers.timings.TimingsService;
import com.hbouzidi.fiveprayers.ui.MainActivity;
import com.hbouzidi.fiveprayers.utils.LocaleHelper;
import com.hbouzidi.fiveprayers.utils.PrayerUtils;
import com.hbouzidi.fiveprayers.utils.TimingUtils;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ClockHomeScreenWidgetProvider extends AppWidgetProvider {

    private final String TAG = "CLOCK_WIDGET_PROVIDER";

    @Inject
    LocationHelper locationHelper;

    @Inject
    AddressHelper addressHelper;

    @Inject
    TimingServiceFactory timingServiceFactory;

    @Inject
    OpenWeatherMapAPIService openWeatherMapAPIService;

    @Inject
    PreferencesHelper preferencesHelper;

    @Inject
    LocaleHelper localeUtils;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ((FivePrayerApplication) context.getApplicationContext())
                .widgetComponent
                .inject(this);

        localeUtils.refreshLocale(context, null);

        TimingsService timingsService = timingServiceFactory.create(preferencesHelper.getCalculationMethod());

        Single<DayPrayer> dayPrayerSingle =
                locationHelper.getLocation()
                        .flatMap(location ->
                                addressHelper.getAddressFromLocation(location)
                        ).flatMap(address ->
                                timingsService.getTimingsByCity(LocalDate.now(), address));

        Single<OpenWeatherMapResponse> openWeatherMapResponseSingle = locationHelper.getLocation()
                .flatMap(location ->
                        openWeatherMapAPIService.getCurrentWeatherData(location.getLatitude(), location.getLongitude(), preferencesHelper.getOpenWeatherAPIKey(), TemperatureUnit.valueOf(preferencesHelper.getOpenWeatherUnit()))
                );

        for (int widgetId : appWidgetIds) {

            int layoutId = R.layout.clock_home_screen_widget;

            if (localeUtils.getLocale().getLanguage().equals("ar")) {
                layoutId = R.layout.clock_home_screen_widget_rtl;
            }

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutId);

            remoteViews.setViewVisibility(R.id.dates_text_View, View.GONE);
            remoteViews.setViewVisibility(R.id.next_prayers_ll, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_horizontal_progressbar, View.GONE);
            remoteViews.setViewVisibility(R.id.weather_ll, View.GONE);

            openAlarmSettingsListener(remoteViews, context);
            openCalendarListener(remoteViews, context);
            openApplicationListener(remoteViews, context);

            Observable.zip(
                            dayPrayerSingle.toObservable().onErrorReturn(throwable -> new DayPrayer()),
                            openWeatherMapResponseSingle.toObservable().onErrorReturn(throwable -> new OpenWeatherMapResponse()),
                            PairResult::new
                    )
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(pairResult -> {
                        if (pairResult.getDayPrayer().getTimings() != null) {
                            updatePrayersViews(context, pairResult.getDayPrayer(), remoteViews);
                        }
                        if (pairResult.getOpenWeatherMapResponse().getWeather() != null) {
                            int iconId = context.getResources().getIdentifier("status" + pairResult.getOpenWeatherMapResponse().getWeather().get(0).getIcon(), "drawable", context.getPackageName());
                            String tempUnit = context.getApplicationContext().getResources().getString(context.getResources().getIdentifier("temperature_unit_" + preferencesHelper.getOpenWeatherUnit(), "string", context.getPackageName()));
                            String description = pairResult.getOpenWeatherMapResponse().getWeather().get(0).getDescription();
                            int temp = (int) pairResult.getOpenWeatherMapResponse().getMain().getTemp();

                            remoteViews.setImageViewResource(R.id.status_image, iconId);
                            remoteViews.setTextViewText(R.id.status_text, StringUtils.capitalize(description));
                            remoteViews.setTextViewText(R.id.actual_temp, String.format(Locale.getDefault(), "%1$02d", temp) + " " + tempUnit);

                            remoteViews.setViewVisibility(R.id.weather_ll, View.VISIBLE);
                        }
                    }, throwable -> {
                        Log.e(TAG, "Error updating Clock Widget", throwable);
                        appWidgetManager.updateAppWidget(widgetId, remoteViews);
                    }, () -> {
                        displayUpdateView(context, appWidgetIds, remoteViews);
                        appWidgetManager.updateAppWidget(widgetId, remoteViews);
                    });
        }
    }

    private void displayUpdateView(Context context, int[] appWidgetIds, RemoteViews remoteViews) {
        Intent intent = new Intent(context, ClockHomeScreenWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingSync = PendingIntentCreator.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.update_widget_ll, pendingSync);

        remoteViews.setViewVisibility(R.id.update_widget_ll, View.VISIBLE);
    }

    private void updatePrayersViews(Context context, DayPrayer dayPrayer, RemoteViews remoteViews) {
        Map<String, LocalDateTime> mixedTimingsMap = getMixedTimingsMap(dayPrayer);
        updateProgressbar(remoteViews, mixedTimingsMap);
        updateNextPrayersViews(remoteViews, mixedTimingsMap, context);
        populateDateTextView(remoteViews, dayPrayer, context);
        populateUpdateTextView(remoteViews, context);

        remoteViews.setViewVisibility(R.id.dates_text_View, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.next_prayers_ll, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.widget_horizontal_progressbar, View.VISIBLE);
    }

    private void updateProgressbar(RemoteViews remoteViews, Map<String, LocalDateTime> mixedTimingMap) {
        String nextPrayerKey = PrayerUtils.getNextPrayerKeyString(mixedTimingMap, LocalDateTime.now());
        String previousPrayerKey = PrayerUtils.getPreviousMixedPrayerKey(nextPrayerKey);

        long timeRemaining = TimingUtils.getTimeBetweenTwoPrayer(LocalDateTime.now(), Objects.requireNonNull(mixedTimingMap.get(nextPrayerKey)));
        long timeBetween = TimingUtils.getTimeBetweenTwoPrayer(Objects.requireNonNull(mixedTimingMap.get(previousPrayerKey)), Objects.requireNonNull(mixedTimingMap.get(nextPrayerKey)));

        remoteViews.setProgressBar(R.id.widget_horizontal_progressbar, 100, (int) getProgressBarPercentage(timeRemaining, timeBetween), false);
    }

    private float getProgressBarPercentage(long timeRemaining, long timeBetween) {
        return 100 - ((float) (timeRemaining * 100) / (timeBetween));
    }

    private void populateDateTextView(RemoteViews remoteViews, DayPrayer dayPrayer, Context context) {
        ZonedDateTime zonedDateTime = TimingUtils.getZonedDateTimeFromTimestamps(dayPrayer.getTimestamp(), dayPrayer.getTimezone());
        String nameOfTheDay = zonedDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());

        String hijriMonth = context.getApplicationContext().getResources().getString(
                context.getResources().getIdentifier("hijri_month_" + dayPrayer.getHijriMonthNumber(), "string", context.getPackageName()));

        String hijriDate = UiUtils.formatFullHijriDate(
                nameOfTheDay,
                dayPrayer.getHijriDay(),
                hijriMonth,
                dayPrayer.getHijriYear()
        );

        String gregorianDate = UiUtils.formatMediumReadableGregorianDate(zonedDateTime);

        remoteViews.setTextViewText(R.id.dates_text_View, StringUtils.capitalize(hijriDate) + " - " + StringUtils.capitalize(gregorianDate));
    }

    private void populateUpdateTextView(RemoteViews remoteViews, Context context) {
        String updateString = context.getResources().getString(
                context.getResources().getIdentifier("common.update", "string", context.getPackageName()));

        remoteViews.setTextViewText(R.id.update_time_text_View, updateString + " : " + UiUtils.formatTiming(LocalDateTime.now()));
    }

    private void updateNextPrayersViews(RemoteViews remoteViews, Map<String, LocalDateTime> mixedTimingMap, Context context) {
        String nextPrayerKey = PrayerUtils.getNextPrayerKeyString(mixedTimingMap, LocalDateTime.now());
        String nextNextPrayerKey = PrayerUtils.getNextPrayerKeyString(mixedTimingMap, Objects.requireNonNull(mixedTimingMap.get(nextPrayerKey)));

        String nextPrayerName = context.getResources().getString(
                context.getResources().getIdentifier("SHORT_" + nextPrayerKey, "string", context.getPackageName()));

        String nextNextPrayerName = context.getResources().getString(
                context.getResources().getIdentifier("SHORT_" + nextNextPrayerKey, "string", context.getPackageName()));

        remoteViews.setTextViewText(R.id.next_prayer_name_tv, StringUtils.capitalize(nextPrayerName));
        remoteViews.setTextViewText(R.id.next_prayer_timing_tv, UiUtils.formatTiming(Objects.requireNonNull(mixedTimingMap.get(nextPrayerKey))));

        remoteViews.setTextViewText(R.id.next_next_prayer_name_tv, StringUtils.capitalize(nextNextPrayerName));
        remoteViews.setTextViewText(R.id.next_next_prayer_timing_tv, UiUtils.formatTiming(Objects.requireNonNull(mixedTimingMap.get(nextNextPrayerKey))));
    }

    private Map<String, LocalDateTime> getMixedTimingsMap(DayPrayer dayPrayer) {
        Map<String, LocalDateTime> timingsByKeyString = new HashMap<>();
        timingsByKeyString.put(String.valueOf(PrayerEnum.FAJR), dayPrayer.getTimings().get(PrayerEnum.FAJR));
        timingsByKeyString.put(String.valueOf(ComplementaryTimingEnum.SUNRISE), dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE));
        timingsByKeyString.put(String.valueOf(PrayerEnum.DHOHR), dayPrayer.getTimings().get(PrayerEnum.DHOHR));
        timingsByKeyString.put(String.valueOf(PrayerEnum.ASR), dayPrayer.getTimings().get(PrayerEnum.ASR));
        timingsByKeyString.put(String.valueOf(PrayerEnum.MAGHRIB), dayPrayer.getTimings().get(PrayerEnum.MAGHRIB));
        timingsByKeyString.put(String.valueOf(PrayerEnum.ICHA), dayPrayer.getTimings().get(PrayerEnum.ICHA));

        return timingsByKeyString;
    }

    private void openAlarmSettingsListener(RemoteViews remoteViews, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent timeIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);

            PendingIntent timePendingIntent = PendingIntentCreator.getActivity(context, 0, timeIntent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FLAG_ACTIVITY_NEW_TASK);
            remoteViews.setOnClickPendingIntent(R.id.timeText, timePendingIntent);
        }
    }

    private void openCalendarListener(RemoteViews remoteViews, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR);

            PendingIntent pendingIntent = PendingIntentCreator.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.dates_text_View, pendingIntent);
        }
    }

    private void openApplicationListener(RemoteViews remoteViews, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(context, MainActivity.class);

            PendingIntent pendingIntent = PendingIntentCreator.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.next_prayers_ll, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.widget_horizontal_progressbar, pendingIntent);
        }
    }
}
