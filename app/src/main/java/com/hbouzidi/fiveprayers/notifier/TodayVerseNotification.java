package com.hbouzidi.fiveprayers.notifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.os.ConfigurationCompat;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.quran.dto.Ayah;
import com.hbouzidi.fiveprayers.ui.dailyverse.DailyVerseActivity;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class TodayVerseNotification extends BaseNotification {

    @Inject
    public TodayVerseNotification(PreferencesHelper preferencesHelper, Context context) {
        super(preferencesHelper, context);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = NotifierConstants.TODAY_VERSE_NOTIFICATION_CHANNEL_ID;
            CharSequence name = NotifierConstants.TODAY_VERSE_NOTIFICATION_CHANNEL_NAME;
            String description = NotifierConstants.TODAY_VERSE_NOTIFICATION_CHANNEL_DESCRIPTION;
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

    public void createNotification(@NonNull List<Ayah> todayVerse) {
        String notificationTitle = context.getString(R.string.daily_verse_notification_title);
        String notificationDescription;
        String actionTitle = context.getString(R.string.common_see_more);

        Locale systemLocale = ConfigurationCompat
                .getLocales(Resources.getSystem().getConfiguration())
                .get(0);

        String currentLanguage = (systemLocale != null) ? systemLocale.getLanguage() : Locale.getDefault().getLanguage();

        Ayah ayahInArabicLangage = todayVerse.stream()
                .filter((ayah -> ayah.getEdition().getLanguage().equals("ar")))
                .findFirst()
                .orElse(todayVerse.get(0));

        if (currentLanguage.equals("ar")) {
            notificationDescription = context
                    .getString(R.string.daily_verse_notification_description,
                            ayahInArabicLangage.getSurah().getName(), ayahInArabicLangage.getNumberInSurah());
        } else {
            notificationDescription = context
                    .getString(R.string.daily_verse_notification_description,
                            ayahInArabicLangage.getSurah().getEnglishName(), ayahInArabicLangage.getNumberInSurah());
        }

        PendingIntent pendingIntent = getNotificationIntent();

        int notificationId = ayahInArabicLangage.getNumber();

        Bitmap versePicture = UiUtils.textToBitmap(
                ayahInArabicLangage.getText(), 15,
                R.font.me_quran, R.color.mine_shaft,
                R.color.mine_shaft_light, R.color.white, context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotifierConstants.TODAY_VERSE_NOTIFICATION_CHANNEL_ID)
                .setColor(getNotificationColor())
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_book_24dp, actionTitle, getNotificationIntent())
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(versePicture)
                )
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(android.R.drawable.ic_popup_reminder);
        } else {
            builder.setSmallIcon(getNotificationIcon());
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notificationId, builder.build());
    }


    @Override
    protected int getNotificationIcon() {
        return R.drawable.ic_book_24dp;
    }

    private PendingIntent getNotificationIntent() {
        Intent notifyIntent = new Intent(context, DailyVerseActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntentCreator.getActivity(context, 0,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
