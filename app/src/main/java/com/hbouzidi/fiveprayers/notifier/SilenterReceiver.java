package com.hbouzidi.fiveprayers.notifier;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */

public class SilenterReceiver extends BroadcastReceiver {

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((FivePrayerApplication) context.getApplicationContext())
                .receiverComponent
                .inject(this);

        if(!isNotificationPolicyAccessGranted(context)) {
            return;
        }

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean turnToSilent = intent.getBooleanExtra("TURN_TO_SILENT", false);

        if (turnToSilent) {
            preferencesHelper.savePreviousRingerModeBeforeSilent(audioManager.getRingerMode());
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else {
            audioManager.setRingerMode(preferencesHelper.getPreviousRingerModeBeforeSilent());
        }
    }

    private boolean isNotificationPolicyAccessGranted(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return notificationManager.isNotificationPolicyAccessGranted();
        } else {
            return true;
        }
    }
}
