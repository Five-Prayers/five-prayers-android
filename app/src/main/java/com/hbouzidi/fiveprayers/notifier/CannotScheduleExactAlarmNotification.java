package com.hbouzidi.fiveprayers.notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
@RequiresApi(api = Build.VERSION_CODES.S)
class CannotScheduleExactAlarmNotification extends BaseNotification {

    @Inject
    public CannotScheduleExactAlarmNotification(PreferencesHelper preferencesHelper, Context context) {
        super(preferencesHelper, context);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = NotifierConstants.ALERT_NOTIFICATION_CHANNEL_NAME;
            String description = NotifierConstants.ALERT_NOTIFICATION_CHANNEL_DESCRIPTION;
            String id = NotifierConstants.ALERT_NOTIFICATION_CHANNEL_ID;
            int importance = NotificationManager.IMPORTANCE_HIGH;

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

    public void createNotification() {
        String notificationTitle = context.getString(R.string.scheduling_adhan_alert_notification_title);
        String content = context.getString(R.string.scheduling_adhan_alert_notification_text);
        String grantActionTitle = context.getString(R.string.scheduling_adhan_alert_notification_action_title);

        PendingIntent notificationIntent = getNotificationIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotifierConstants.ALERT_NOTIFICATION_CHANNEL_ID)
                .setColor(getNotificationColor())
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(notificationIntent)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .addAction(android.R.drawable.ic_dialog_alert, grantActionTitle, getNotificationIntent())
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1158, builder.build());
    }

    private PendingIntent getNotificationIntent() {
        Intent notificationIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntentCreator.getActivity(context, 1158,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
