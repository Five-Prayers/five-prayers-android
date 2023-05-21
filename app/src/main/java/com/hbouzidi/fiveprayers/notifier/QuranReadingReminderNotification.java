package com.hbouzidi.fiveprayers.notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingSchedule;
import com.hbouzidi.fiveprayers.ui.quran.index.QuranIndexActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
class QuranReadingReminderNotification extends BaseNotification {

    @Inject
    public QuranReadingReminderNotification(PreferencesHelper preferencesHelper, Context context) {
        super(preferencesHelper, context);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = NotifierConstants.QURAN_READING_NOTIFICATION_CHANNEL_NAME;
            String description = NotifierConstants.QURAN_READING_NOTIFICATION_CHANNEL_DESCRIPTION;
            String id = NotifierConstants.QURAN_READING_NOTIFICATION_CHANNEL_ID;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void createNotification(ReadingSchedule nextReadingScheduleOccurrence) {
        String notificationTitle;

        PendingIntent pendingIntent = getNotificationIntent();

        notificationTitle = context.getString(R.string.quran_daily_schedule_notification_title);

        String content =
                context.getString(R.string.quarter_to_display)
                        + " "
                        + nextReadingScheduleOccurrence.getStartQuarter()
                        + " "
                        + context.getString(R.string.common_to)
                        + " "
                        + nextReadingScheduleOccurrence.getEndQuarter()
                        + " ("
                        + context.getString(R.string.common_day)
                        + " "
                        + nextReadingScheduleOccurrence.getDayNumber()
                        + " )";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotifierConstants.QURAN_READING_NOTIFICATION_CHANNEL_ID)
                .setColor(getNotificationColor())
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(android.R.drawable.ic_popup_reminder);
        } else {
            builder.setSmallIcon(getNotificationIcon());
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(nextReadingScheduleOccurrence.getStartQuarter(), builder.build());
    }

    @Override
    protected int getNotificationIcon() {
        return R.drawable.ic_quran_silhuoette;
    }

    private PendingIntent getNotificationIntent() {
        Intent notificationIntent = new Intent(context, QuranIndexActivity.class);
        notificationIntent.putExtra("tab_to_open", 1);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntentCreator.getActivity(context, 3,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
    }
}
