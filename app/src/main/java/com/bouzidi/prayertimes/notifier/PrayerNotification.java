package com.bouzidi.prayertimes.notifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.VolumeProviderCompat;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.preferences.PreferencesConstants;
import com.bouzidi.prayertimes.timings.PrayerEnum;
import com.bouzidi.prayertimes.ui.MainActivity;

import static android.content.Context.MODE_PRIVATE;

class PrayerNotification {

    private PrayerNotification() {
    }

    static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = NotifierConstants.ADTHAN_NOTIFICATION_CHANNEL_NAME;
            String description = NotifierConstants.ADTHAN_NOTIFICATION_CHANNEL_DESCRIPTION;
            String id = NotifierConstants.ADTHAN_NOTIFICATION_CHANNEL_ID;
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
        String prayerKey = intent.getStringExtra("prayerKey");

        String prayerName = context.getResources().getString(
                context.getResources().getIdentifier(prayerKey,
                        "string", context.getPackageName()));

        PendingIntent pendingIntent = getNotificationIntent(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotifierConstants.ADTHAN_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mosque_24dp)
                .setContentTitle(context.getString(R.string.adthan_notification_title))
                .setAutoCancel(true)
                .setDeleteIntent(createOnDismissedIntent(context, notificationId))
                .setContentIntent(pendingIntent)
                .setContentText(prayerName + " : " + prayerTiming);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        setupAdhanCall(context, prayerKey);

        notificationManager.notify(notificationId, builder.build());
    }

    private static PendingIntent getNotificationIntent(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
    }

    private static PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        return PendingIntent.getBroadcast(context.getApplicationContext(),
                notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private static void setupAdhanCall(Context context, String prayerKey) {
        String adhanCallKeyPart = PreferencesConstants.ADTHAN_CALL_ENABLED_KEY;
        String callPreferenceKey = prayerKey + adhanCallKeyPart;

        final SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesConstants.ADTHAN_CALLS_SHARED_PREFERENCES, MODE_PRIVATE);
        boolean callEnabled = sharedPreferences.getBoolean(callPreferenceKey, false);

        if (callEnabled) {
            AdhanPlayer.getInstance().playAdhan(context, PrayerEnum.FAJR.toString().equals(prayerKey));
            setMediaSession(context);
        }
    }

    private static void setMediaSession(Context context) {
        MediaSessionCompat mediaSession = new MediaSessionCompat(context, "PrayerNotification");
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0)
                .build());

        VolumeProviderCompat myVolumeProvider =
                new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, 100, 50) {
                    @Override
                    public void onAdjustVolume(int direction) {
                        AdhanPlayer.getInstance().stopAdhan();
                        mediaSession.release();
                    }
                };
        mediaSession.setPlaybackToRemote(myVolumeProvider);
        mediaSession.setActive(true);

        AdhanPlayer.getInstance().setOnCompletionListener(mp -> mediaSession.release());
    }
}
