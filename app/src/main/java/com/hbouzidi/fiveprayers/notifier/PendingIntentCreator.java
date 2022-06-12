package com.hbouzidi.fiveprayers.notifier;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class PendingIntentCreator {

    public static PendingIntent getActivity(Context context, int id, Intent intent, int flags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getActivity(context, id, intent, flags | PendingIntent.FLAG_IMMUTABLE);
        } else {
            return PendingIntent.getActivity(context, id, intent, flags);
        }
    }

    public static PendingIntent getBroadcast(Context context, int id, Intent intent, int flags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(context, id, intent, flags | PendingIntent.FLAG_IMMUTABLE);
        } else {
            return PendingIntent.getBroadcast(context, id, intent, flags);
        }
    }
}
