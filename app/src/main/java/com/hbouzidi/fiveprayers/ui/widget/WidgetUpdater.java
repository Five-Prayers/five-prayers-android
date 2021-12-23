package com.hbouzidi.fiveprayers.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.hbouzidi.fiveprayers.R;

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
    public WidgetUpdater() {
    }

    public void updateHomeScreenWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_screen_widget);

        appWidgetManager.updateAppWidget(new ComponentName(context.getPackageName(), HomeScreenWidgetProvider.class.getName()), views);


        Intent intent = new Intent(context, HomeScreenWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, HomeScreenWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
