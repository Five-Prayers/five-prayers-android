package com.hbouzidi.fiveprayers.notifier;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

import java.io.IOException;

public class AdhanPlayer {

    public static MediaPlayer mediaPlayer;
    private static AdhanPlayer adhanPlayer;

    private AdhanPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    public static AdhanPlayer getInstance() {
        if (adhanPlayer == null) {
            adhanPlayer = new AdhanPlayer();
        }
        return adhanPlayer;
    }

    public void playAdhan(Context context, boolean fajr) {
        if (!mediaPlayer.isPlaying()) {
            try {
                initializeMediaPlayer(context, fajr);
            } catch (IOException e) {
                Log.e("AdhanPlayer", "Cannot play Adhan", e);
            }
            mediaPlayer.start();
        }
    }

    public void stopAdhan() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mediaPlayer.setOnCompletionListener(listener);
    }

    private void initializeMediaPlayer(Context context, boolean fajr) throws IOException {
        mediaPlayer.setDataSource(context, getAdhanUri(context, fajr));
        setAudioAttribute(mediaPlayer);
        mediaPlayer.setLooping(false);
        mediaPlayer.prepare();
    }

    private void setAudioAttribute(MediaPlayer mediaPlayer) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setUsage(AudioAttributes.USAGE_ALARM);
            builder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            builder.setLegacyStreamType(AudioManager.STREAM_ALARM);

            mediaPlayer.setAudioAttributes(builder.build());
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
    }

    private Uri getAdhanUri(Context context, boolean fajr) {
        int mediaId;
        if (fajr) {
            switch (PreferencesHelper.getFajrAdhanCaller(context)) {
                case "ADHAN_FAJR_ABDELBASSET_ABDESSAMAD_EGYTE":
                    mediaId = R.raw.adhan_fajr_abdelbasset_abdessamad_egyte;
                    break;
                case "ADHAN_FAJR_AL_HARAM_EL_MADANI_SAOUDIA":
                    mediaId = R.raw.adhan_fajr_al_haram_el_madani_saoudia;
                    break;
                default:
                    mediaId = R.raw.adhan_fajr_meshary_al_fasy_kuwait;
            }
        } else {
            switch (PreferencesHelper.getAdhanCaller(context)) {
                case "ADHAN_ABDELBASSET_ABDESSAMAD_EGYTE":
                    mediaId = R.raw.adhan_abdelbasset_abdessamad_egyte;
                    break;
                case "ADHAN_OMAR_AL_KAZABRI_MOROCCO":
                    mediaId = R.raw.adhan_omar_al_kazabri_morocco;
                    break;
                case "ADHAN_RIAD_AL_DJAZAIRI_ALGERIA":
                    mediaId = R.raw.adhan_riad_al_djazairi_algeria;
                    break;
                default:
                    mediaId = R.raw.adhan_meshary_al_fasy_kuwait;
            }
        }
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + mediaId);
    }
}