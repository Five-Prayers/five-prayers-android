package com.bouzidi.prayertimes.notifier;

import android.content.ContentResolver;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.bouzidi.prayertimes.R;

public class AdhanPlayer {

    public static Ringtone r;

    public static void playAdhan(Context context) {
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.adhan_meshary_al_fasy);
        r = RingtoneManager.getRingtone(context, soundUri);
        r.play();
    }

    public static void stopAdhan() {
        r.stop();
    }
}
