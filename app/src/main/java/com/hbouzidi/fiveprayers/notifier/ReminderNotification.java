package com.hbouzidi.fiveprayers.notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.common.TimingType;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.ui.MainActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
class ReminderNotification extends BaseNotification {

    private final ReminderPlayer reminderPlayer;

    @Inject
    public ReminderNotification(ReminderPlayer reminderPlayer, PreferencesHelper preferencesHelper, Context context) {
        super(preferencesHelper, context);
        this.reminderPlayer = reminderPlayer;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = NotifierConstants.ADTHAN_NOTIFICATION_CHANNEL_NAME;
            String description = NotifierConstants.ADTHAN_NOTIFICATION_CHANNEL_DESCRIPTION;
            String id = NotifierConstants.ADTHAN_NOTIFICATION_CHANNEL_ID;
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

    public void createNotification(Intent intent) {
        String notificationTitle;

        String prayerType = intent.getStringExtra("prayerType");
        int notificationId = intent.getIntExtra("notificationId", 0);
        String prayerTiming = intent.getStringExtra("prayerTiming");
        String prayerKey = intent.getStringExtra("prayerKey");
        String prayerCity = intent.getStringExtra("prayerCity");

        String prayerName = context.getResources().getString(
                context.getResources().getIdentifier(prayerKey,
                        "string", context.getPackageName()));

        PendingIntent pendingIntent = getNotificationIntent();

        boolean isComplementaryTiming = prayerType.equals(TimingType.COMPLEMENTARY.toString());

        if (prayerType != null && isComplementaryTiming) {
            notificationTitle = context.getString(R.string.adthan_notification_title);
        } else {
            notificationTitle = context.getString(R.string.adthan_reminder_notification_title);
        }

        String content = prayerName + " : " + prayerTiming;

        if (prayerCity != null) {
            content += " (" + prayerCity + ")";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotifierConstants.ADTHAN_NOTIFICATION_CHANNEL_ID)
                .setColor(getNotificationColor())
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setDeleteIntent(createOnDismissedIntent(notificationId))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(android.R.drawable.ic_popup_reminder);
        } else {
            builder.setSmallIcon(getNotificationIcon());
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notificationId, builder.build());

        if (preferencesHelper.isVibrationActivated()) {
            createVibration();
        }

        setupCall(!isComplementaryTiming, prayerKey);
    }

    private PendingIntent getNotificationIntent() {
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntentCreator.getActivity(context, 0,
                notificationIntent, 0);
    }

    private void createVibration() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = new long[]{0, 1000, 500, 1000, 500, 500, 500};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1),
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
        } else {
            vibrator.vibrate(pattern, -1);
        }
    }

    private PendingIntent createOnDismissedIntent(int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.setClass(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        return PendingIntentCreator.getBroadcast(context.getApplicationContext(),
                notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private void setupCall(boolean isReminder, String prayerKey) {
        boolean callEnabled;

        if (isReminder) {
            callEnabled = preferencesHelper.isReminderCallEnabled();
        } else {
            String adhanCallKeyPart = PreferencesConstants.TIMING_REMINDER_CALL_ENABLED;
            String callPreferenceKey = prayerKey + adhanCallKeyPart;

            final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            callEnabled = defaultSharedPreferences.getBoolean(callPreferenceKey, false);
        }

        if (callEnabled) {
            reminderPlayer.playAdhan(isReminder);
        }
    }
}
