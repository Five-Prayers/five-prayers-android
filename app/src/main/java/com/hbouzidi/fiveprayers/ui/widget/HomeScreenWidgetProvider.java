package com.hbouzidi.fiveprayers.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
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
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class HomeScreenWidgetProvider extends AppWidgetProvider {

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

                            int layoutId = R.layout.home_screen_widget;

                            if (localeUtils.getLocale().getLanguage().equals("ar")) {
                                layoutId = R.layout.home_screen_widget_rtl;
                            }

                            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutId);

                            PrayerEnum nextPrayerKey = PrayerUtils.getNextPrayer(dayPrayer.getTimings(), LocalDateTime.now());

                            String hijriMonth = context.getApplicationContext().getResources().getString(
                                    context.getResources().getIdentifier("hijri_month_" + dayPrayer.getHijriMonthNumber(), "string", context.getPackageName()));

                            String hijriDate = UiUtils.formatHijriDate(
                                    dayPrayer.getHijriDay(),
                                    hijriMonth,
                                    dayPrayer.getHijriYear()
                            );

                            populateDateTextView(remoteViews, dayPrayer, context);

                            populateTimingsTextViews(dayPrayer, remoteViews, nextPrayerKey, context);

                            Intent intent = new Intent(context, HomeScreenWidgetProvider.class);
                            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                            appWidgetManager.updateAppWidget(widgetId, remoteViews);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                    }
                });
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

        remoteViews.setTextViewText(R.id.date_text_View, StringUtils.capitalize(hijriDate) + " - " + StringUtils.capitalize(gregorianDate));
    }

    private void populateTimingsTextViews(@NotNull DayPrayer dayPrayer, RemoteViews remoteViews, PrayerEnum nextPrayerKey, Context context) {
        remoteViews.setTextViewText(R.id.chourouk_text_View, UiUtils.formatTiming(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE)));
        remoteViews.setTextViewText(R.id.chourouk_title_text_View, context.getString(R.string.SUNRISE));

        remoteViews.setTextViewText(R.id.fajr_text_View, getStyledText(UiUtils.formatTiming(dayPrayer.getTimings().get(PrayerEnum.FAJR)), PrayerEnum.FAJR, nextPrayerKey));
        remoteViews.setTextViewText(R.id.dohr_text_View, getStyledText(UiUtils.formatTiming(dayPrayer.getTimings().get(PrayerEnum.DHOHR)), PrayerEnum.DHOHR, nextPrayerKey));
        remoteViews.setTextViewText(R.id.asr_text_View, getStyledText(UiUtils.formatTiming(dayPrayer.getTimings().get(PrayerEnum.ASR)), PrayerEnum.ASR, nextPrayerKey));
        remoteViews.setTextViewText(R.id.maghrib_text_View, getStyledText(UiUtils.formatTiming(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB)), PrayerEnum.MAGHRIB, nextPrayerKey));
        remoteViews.setTextViewText(R.id.ichaa_text_View, getStyledText(UiUtils.formatTiming(dayPrayer.getTimings().get(PrayerEnum.ICHA)), PrayerEnum.ICHA, nextPrayerKey));

        remoteViews.setTextViewText(R.id.fajr_title_text_View, getStyledText(context.getString(R.string.SHORT_FAJR), PrayerEnum.FAJR, nextPrayerKey));
        remoteViews.setTextViewText(R.id.dohr_title_text_View, getStyledText(context.getString(R.string.SHORT_DHOHR), PrayerEnum.DHOHR, nextPrayerKey));
        remoteViews.setTextViewText(R.id.asr_title_text_View, getStyledText(context.getString(R.string.SHORT_ASR), PrayerEnum.ASR, nextPrayerKey));
        remoteViews.setTextViewText(R.id.maghrib_title_text_View, getStyledText(context.getString(R.string.SHORT_MAGHRIB), PrayerEnum.MAGHRIB, nextPrayerKey));
        remoteViews.setTextViewText(R.id.ichaa_title_text_View, getStyledText(context.getString(R.string.SHORT_ICHA), PrayerEnum.ICHA, nextPrayerKey));

        remoteViews.setTextColor(R.id.fajr_text_View, getTextColor(context, PrayerEnum.FAJR, nextPrayerKey));
        remoteViews.setTextColor(R.id.dohr_text_View, getTextColor(context, PrayerEnum.DHOHR, nextPrayerKey));
        remoteViews.setTextColor(R.id.asr_text_View, getTextColor(context, PrayerEnum.ASR, nextPrayerKey));
        remoteViews.setTextColor(R.id.maghrib_text_View, getTextColor(context, PrayerEnum.MAGHRIB, nextPrayerKey));
        remoteViews.setTextColor(R.id.ichaa_text_View, getTextColor(context, PrayerEnum.ICHA, nextPrayerKey));

        remoteViews.setTextColor(R.id.fajr_title_text_View, getTextColor(context, PrayerEnum.FAJR, nextPrayerKey));
        remoteViews.setTextColor(R.id.dohr_title_text_View, getTextColor(context, PrayerEnum.DHOHR, nextPrayerKey));
        remoteViews.setTextColor(R.id.asr_title_text_View, getTextColor(context, PrayerEnum.ASR, nextPrayerKey));
        remoteViews.setTextColor(R.id.maghrib_title_text_View, getTextColor(context, PrayerEnum.MAGHRIB, nextPrayerKey));
        remoteViews.setTextColor(R.id.ichaa_title_text_View, getTextColor(context, PrayerEnum.ICHA, nextPrayerKey));
    }

    private SpannableString getStyledText(String text, PrayerEnum prayerKey, PrayerEnum nextPrayerKey) {
        SpannableString s = new SpannableString(text);

        if (prayerKey.equals(nextPrayerKey)) {
            s.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), 0);
        } else {
            s.setSpan(new StyleSpan(Typeface.NORMAL), 0, text.length(), 0);
        }
        return s;
    }

    private int getTextColor(Context context, PrayerEnum prayerKey, PrayerEnum nextPrayerKey) {
        if (prayerKey.equals(nextPrayerKey)) {
            return ContextCompat.getColor(context, R.color.white);
        }
        return ContextCompat.getColor(context, R.color.black_squeeze);
    }
}
