package com.bouzidi.prayer_times.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.bouzidi.prayer_times.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.MODE_PRIVATE;

class PrayerNotification {

    private PrayerNotification() {
    }

    static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.adthan_notification_channel_name);
            String description = context.getString(R.string.adthan_notification_channel_description);
            String id = context.getString(R.string.adthan_notification_channel_id);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    static void createNotification(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        String prayerTiming = intent.getStringExtra("prayerTiming");

        String prayerName = context.getResources().getString(
                context.getResources().getIdentifier(intent.getStringExtra("prayerKey"),
                        "string", context.getPackageName()));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ADHAN_CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Prayer Time !")
                .setAutoCancel(true)
                .setDeleteIntent(createOnDismissedIntent(context, notificationId))
                .setContentText(prayerName + " : " + prayerTiming);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        final SharedPreferences sharedPreferences = context.getSharedPreferences("notification", MODE_PRIVATE);

        boolean notificationAdhanEnabled = sharedPreferences.getBoolean("notification_adhan_enabled", true);
        if (notificationAdhanEnabled) {
            AdhanPlayer.playAdhan(context);
        }

        notificationManager.notify(notificationId, builder.build());
    }

    private static PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        return PendingIntent.getBroadcast(context.getApplicationContext(),
                notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
