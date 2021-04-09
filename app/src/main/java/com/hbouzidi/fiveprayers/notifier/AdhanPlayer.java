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

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class AdhanPlayer {

    private final MediaPlayer mediaPlayer;

    @Inject
    public AdhanPlayer() {
        mediaPlayer = new MediaPlayer();
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
        mediaPlayer.reset();
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
                case "ADHAN_FAJR_ABDELBASSET_ABDESSAMAD_EGYPTE":
                    mediaId = R.raw.adhan_fajr_abdelbasset_abdessamad_egypte;
                    break;
                case "ADHAN_FAJR_AL_HARAM_EL_MADANI_SAOUDIA":
                    mediaId = R.raw.adhan_fajr_al_haram_el_madani_saoudia;
                    break;
                case "ADHAN_FAJR_MESHARY_AL_FASY_KUWAIT":
                    mediaId = R.raw.adhan_fajr_meshary_al_fasy_kuwait;
                    break;
                default:
                    mediaId = R.raw.short_prayer_call;
            }
        } else {
            switch (PreferencesHelper.getAdhanCaller(context)) {
                case "ADHAN_ABDELBASSET_ABDESSAMAD_EGYPTE":
                    mediaId = R.raw.adhan_abdelbasset_abdessamad_egypte;
                    break;
                case "ADHAN_OMAR_AL_KAZABRI_MOROCCO":
                    mediaId = R.raw.adhan_omar_al_kazabri_morocco;
                    break;
                case "ADHAN_RIAD_AL_DJAZAIRI_ALGERIA":
                    mediaId = R.raw.adhan_riad_al_djazairi_algeria;
                    break;
                case "ADHAN_MESHARY_AL_FASY_KUWAIT":
                    mediaId = R.raw.adhan_meshary_al_fasy_kuwait;
                    break;
                default:
                    mediaId = R.raw.short_prayer_call;
            }
        }
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + mediaId);
    }
}