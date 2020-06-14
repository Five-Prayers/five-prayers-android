package com.bouzidi.prayertimes.notifier;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.bouzidi.prayertimes.R;

import java.io.IOException;

public class AdhanPlayer {

    public static MediaPlayer mediaPlayer;
    private static AdhanPlayer adhanPlayer;

    private AdhanPlayer(Context context) {
        try {
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.adhan_meshary_al_fasy);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, soundUri);

            setAudioAttribute(mediaPlayer);

            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("AdhanPlayer", "Cannot play Adhan");
        }
    }

    public static AdhanPlayer getInstance(Context context) {
        if (adhanPlayer == null) {
            adhanPlayer = new AdhanPlayer(context);
        }
        return adhanPlayer;
    }

    public void playAdhan() {
        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    public void stopAdhan() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void setAudioAttribute(MediaPlayer mediaPlayer) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setUsage(AudioAttributes.USAGE_NOTIFICATION);
            builder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            builder.setLegacyStreamType(AudioManager.STREAM_NOTIFICATION);

            mediaPlayer.setAudioAttributes(builder.build());
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        }
    }
}