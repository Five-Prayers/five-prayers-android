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
import com.hbouzidi.fiveprayers.ui.invocations.EveningInvocationActivity;
import com.hbouzidi.fiveprayers.ui.invocations.MorningInvocationActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class InvocationNotification extends BaseNotification {

    @Inject
    public InvocationNotification(PreferencesHelper preferencesHelper, Context context) {
        super(preferencesHelper, context);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = NotifierConstants.INVOCATIONS_NOTIFICATION_CHANNEL_ID;
            CharSequence name = NotifierConstants.INVOCATIONS_NOTIFICATION_CHANNEL_NAME;
            String description = NotifierConstants.INVOCATIONS_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.WHITE);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void createNotification(Intent intent) {
        boolean isMorningInvocations = intent.getBooleanExtra("IS_MORNING_INVOCATIONS", true);
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);

        String notificationTitle = context.getString(isMorningInvocations ? R.string.morning_invocations : R.string.evening_invocations);
        String notificationDescription = context.getString(isMorningInvocations ? R.string.read_morning_invocations : R.string.read_evening_invocations);
        ;
        String actionTitle = context.getString(R.string.common_see_more);

        PendingIntent pendingIntent = getNotificationIntent(isMorningInvocations);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotifierConstants.INVOCATIONS_NOTIFICATION_CHANNEL_ID)
                .setColor(getNotificationColor())
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setContentIntent(pendingIntent)
                .addAction(isMorningInvocations ? R.drawable.ic_morning_invocations : R.drawable.ic_evening_invocations, actionTitle, getNotificationIntent(isMorningInvocations))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(android.R.drawable.ic_popup_reminder);
        } else {
            builder.setSmallIcon(isMorningInvocations ? R.drawable.ic_morning_invocations : R.drawable.ic_evening_invocations);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

    private PendingIntent getNotificationIntent(boolean isMorningInvocations) {
        Intent notifyIntent = new Intent(context, isMorningInvocations ? MorningInvocationActivity.class : EveningInvocationActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntentCreator.getActivity(context, 0,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
