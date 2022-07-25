package com.hbouzidi.fiveprayers.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.utils.LocaleHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class WidgetUpdater {

    @Inject
    LocaleHelper localeUtils;

    @Inject
    public WidgetUpdater() {
    }

    public void updateHomeScreenWidgets(Context context) {
        updateHomeScreenWidget(context);
        updateNextPrayerHomeScreenWidget(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            updateClockHomeScreenWidget(context);
        }
    }

    private void updateHomeScreenWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int layoutId = R.layout.home_screen_widget;

        if (localeUtils.getLocale().getLanguage().equals("ar")) {
            layoutId = R.layout.home_screen_widget_rtl;
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

        appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), HomeScreenWidgetProvider.class.getName()), views);


        Intent intent = new Intent(context, HomeScreenWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, HomeScreenWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void updateNextPrayerHomeScreenWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int layoutId = R.layout.next_prayer_home_screen_widget;

        if (localeUtils.getLocale().getLanguage().equals("ar")) {
            layoutId = R.layout.next_prayer_home_screen_widget_rtl;
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

        appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), NextPrayerHomeScreenWidgetProvider.class.getName()), views);


        Intent intent = new Intent(context, NextPrayerHomeScreenWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NextPrayerHomeScreenWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateClockHomeScreenWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int layoutId = R.layout.clock_home_screen_widget;

        if (localeUtils.getLocale().getLanguage().equals("ar")) {
            layoutId = R.layout.clock_home_screen_widget_rtl;
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

        appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), ClockHomeScreenWidgetProvider.class.getName()), views);


        Intent intent = new Intent(context, ClockHomeScreenWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ClockHomeScreenWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
