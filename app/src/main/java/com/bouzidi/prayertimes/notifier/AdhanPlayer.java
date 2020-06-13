package com.bouzidi.prayertimes.notifier;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.bouzidi.prayertimes.R;

public class AdhanPlayer {

    public static MediaPlayer mediaPlayer;

    public static void playAdhan(Context context) {
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.adhan_meshary_al_fasy);
        mediaPlayer = MediaPlayer.create(context, soundUri);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        mediaPlayer.setLooping(false);

        mediaPlayer.start();
    }

    public static void stopAdhan() {
        mediaPlayer.stop();
    }
}
