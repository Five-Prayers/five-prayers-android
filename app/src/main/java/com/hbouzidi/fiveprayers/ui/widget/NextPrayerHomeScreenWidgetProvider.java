package com.hbouzidi.fiveprayers.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
import com.hbouzidi.fiveprayers.notifier.PendingIntentCreator;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.TimingServiceFactory;
import com.hbouzidi.fiveprayers.timings.TimingsService;
import com.hbouzidi.fiveprayers.utils.LocaleHelper;
import com.hbouzidi.fiveprayers.utils.PrayerUtils;
import com.hbouzidi.fiveprayers.utils.TimingUtils;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NextPrayerHomeScreenWidgetProvider extends AppWidgetProvider {

    @Inject
    LocationHelper locationHelper;

    @Inject
    AddressHelper addressHelper;

    @Inject
    TimingServiceFactory timingServiceFactory;

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

        dayPrayerSingle
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<DayPrayer>() {
                    @Override
                    public void onSuccess(@NotNull DayPrayer dayPrayer) {
                        for (int widgetId : appWidgetIds) {

                            int layoutId = R.layout.next_prayer_home_screen_widget;

                            if (localeUtils.getLocale().getLanguage().equals("ar")) {
                                layoutId = R.layout.next_prayer_home_screen_widget_rtl;
                            }

                            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutId);

                            Map<String, LocalDateTime> mixedTimingsMap = getMixedTimingsMap(dayPrayer);
                            updateNextPrayerViews(remoteViews, mixedTimingsMap, context);
                            updateNextPrayersViews(remoteViews, mixedTimingsMap, context);
                            populateDateTextView(remoteViews, dayPrayer, context);
                            populateUpdateTextView(remoteViews, context);

                            Intent intent = new Intent(context, NextPrayerHomeScreenWidgetProvider.class);
                            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

                            PendingIntent pendingSync = PendingIntentCreator.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            remoteViews.setOnClickPendingIntent(R.id.update_widget_ll, pendingSync);

                            appWidgetManager.updateAppWidget(widgetId, remoteViews);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                    }
                });
    }

    private void updateNextPrayerViews(RemoteViews remoteViews, Map<String, LocalDateTime> mixedTimingMap, Context context) {
        String nextPrayerKey = PrayerUtils.getNextPrayerKeyString(mixedTimingMap, LocalDateTime.now());
        String previousPrayerKey = PrayerUtils.getPreviousMixedPrayerKey(nextPrayerKey);

        long timeRemaining = TimingUtils.getTimeBetweenTwoPrayer(LocalDateTime.now(), Objects.requireNonNull(mixedTimingMap.get(nextPrayerKey)));
        long timeBetween = TimingUtils.getTimeBetweenTwoPrayer(Objects.requireNonNull(mixedTimingMap.get(previousPrayerKey)), Objects.requireNonNull(mixedTimingMap.get(nextPrayerKey)));

        String prayerName = context.getResources().getString(
                context.getResources().getIdentifier("SHORT_" + nextPrayerKey, "string", context.getPackageName()));

        String hoursSeparator = context.getResources().getString(
                context.getResources().getIdentifier("common.hours.separator", "string", context.getPackageName()));

        String minutesSeparator = context.getResources().getString(
                context.getResources().getIdentifier("common.minutes.separator", "string", context.getPackageName()));

        String inString = context.getResources().getString(
                context.getResources().getIdentifier("common.in", "string", context.getPackageName()));

        String atString = context.getResources().getString(
                context.getResources().getIdentifier("common.at", "string", context.getPackageName()));

        remoteViews.setTextViewText(R.id.common_in_tv, inString + " " + UiUtils.formatTimeForWidgetTimer(timeRemaining, hoursSeparator, minutesSeparator));
        remoteViews.setTextViewText(R.id.next_prayer_name_tv, StringUtils.capitalize(prayerName));
        remoteViews.setTextViewText(R.id.common_at_tv, atString + " " + UiUtils.formatTiming(Objects.requireNonNull(mixedTimingMap.get(nextPrayerKey))));

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
        String nextNextNextPrayerKey = PrayerUtils.getNextPrayerKeyString(mixedTimingMap, Objects.requireNonNull(mixedTimingMap.get(nextNextPrayerKey)));

        String nextNextPrayerName = context.getResources().getString(
                context.getResources().getIdentifier("SHORT_" + nextNextPrayerKey, "string", context.getPackageName()));

        String nextNextNextPrayerName = context.getResources().getString(
                context.getResources().getIdentifier("SHORT_" + nextNextNextPrayerKey, "string", context.getPackageName()));

        remoteViews.setTextViewText(R.id.next_next_prayer_name_tv, StringUtils.capitalize(nextNextPrayerName));
        remoteViews.setTextViewText(R.id.next_next_prayer_timing_tv, UiUtils.formatTiming(Objects.requireNonNull(mixedTimingMap.get(nextNextPrayerKey))));

        remoteViews.setTextViewText(R.id.next_next_next_prayer_name_tv, StringUtils.capitalize(nextNextNextPrayerName));
        remoteViews.setTextViewText(R.id.next_next_next_prayer_timing_tv, UiUtils.formatTiming(Objects.requireNonNull(mixedTimingMap.get(nextNextNextPrayerKey))));
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
}
