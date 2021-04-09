package com.hbouzidi.fiveprayers.notifier;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hbouzidi.fiveprayers.FivePrayerApplication;

import java.util.Objects;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NotifierActionReceiver extends BroadcastReceiver {

    @Inject
    AdhanPlayer adhanPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((FivePrayerApplication) context.getApplicationContext())
                .receiverComponent
                .inject(this);

        String action = intent.getAction();
        int notificationId = intent.getIntExtra("notificationId", 0);

        if (Objects.equals(action, NotifierConstants.ADTHAN_NOTIFICATION_CANCEL_ADHAN_ACTION)) {
            muteAdhanCaller();
            closeNotification(notificationId, context);
        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void closeNotification(int notificationId, Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }

    public void muteAdhanCaller() {
        adhanPlayer.stopAdhan();
    }
}
