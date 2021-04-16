package com.hbouzidi.fiveprayers.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hbouzidi.fiveprayers.FivePrayerApplication;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NotificationDismissedReceiver extends BroadcastReceiver {

    @Inject
    AdhanPlayer adhanPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((FivePrayerApplication) context.getApplicationContext())
                .receiverComponent
                .inject(this);

        adhanPlayer.stopAdhan();
    }
}
