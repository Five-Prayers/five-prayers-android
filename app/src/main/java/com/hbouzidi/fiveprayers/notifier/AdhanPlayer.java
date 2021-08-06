package com.hbouzidi.fiveprayers.notifier;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.media.VolumeProviderCompat;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.utils.UiUtils;

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

    private final MediaPlayer adhanMediaPlayer;
    private final MediaPlayer douaMediaPlayer;
    private final PreferencesHelper preferencesHelper;
    private final Context context;

    @Inject
    public AdhanPlayer(PreferencesHelper preferencesHelper, Context context) {
        this.preferencesHelper = preferencesHelper;
        this.context = context;
        adhanMediaPlayer = new MediaPlayer();
        douaMediaPlayer = new MediaPlayer();
    }

    public void playAdhan(boolean fajr) {
        if (!adhanMediaPlayer.isPlaying() || !douaMediaPlayer.isPlaying()) {
            try {
                initializeAdhanMediaPlayer(fajr);
                initializeDouaeMediaPlayer();
            } catch (IOException e) {
                Log.e("AdhanPlayer", "Cannot play Adhan", e);
            }

            adhanMediaPlayer.start();

            if (preferencesHelper.isDouaeAfterAdhanEnabled() &&
                    !Uri.parse(preferencesHelper.getAdhanCaller()).equals(UiUtils.uriFromRaw(PreferencesConstants.SHORT_PRAYER_CALL, context))) {

                adhanMediaPlayer.setNextMediaPlayer(douaMediaPlayer);
            }
        }

        setOnCompletionListeners();
    }

    public void stopAdhan() {
        if (adhanMediaPlayer.isPlaying()) {
            adhanMediaPlayer.stop();
        }

        if (douaMediaPlayer.isPlaying()) {
            douaMediaPlayer.stop();
        }
    }

    public void setOnCompletionListeners() {
        MediaSessionCompat adhanMediaSession = createMediaSession("Adhan");
        MediaSessionCompat douaeMediaSession = createMediaSession("Douae");

        adhanMediaPlayer.setOnCompletionListener(mp -> adhanMediaSession.release());
        douaMediaPlayer.setOnCompletionListener(mp -> douaeMediaSession.release());
    }

    private void initializeAdhanMediaPlayer(boolean fajr) throws IOException {
        adhanMediaPlayer.reset();
        adhanMediaPlayer.setDataSource(context, getAdhanUri(fajr));
        setAudioAttribute(adhanMediaPlayer);
        adhanMediaPlayer.setLooping(false);
        adhanMediaPlayer.prepare();
    }

    private void initializeDouaeMediaPlayer() throws IOException {
        douaMediaPlayer.reset();
        douaMediaPlayer.setDataSource(context, getDouaeUri(context));
        setAudioAttribute(douaMediaPlayer);
        douaMediaPlayer.setLooping(false);
        douaMediaPlayer.prepare();
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

    private Uri getAdhanUri(boolean fajr) {
        if (fajr) {
            return Uri.parse(preferencesHelper.getFajrAdhanCaller());
        }
        return Uri.parse(preferencesHelper.getAdhanCaller());
    }

    private Uri getDouaeUri(Context context) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.douae_after_athan);
    }

    private MediaSessionCompat createMediaSession(String tag) {
        MediaSessionCompat mediaSession = new MediaSessionCompat(context, tag);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0)
                .build());

        VolumeProviderCompat volumeProvider =
                new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, 100, 50) {
                    @Override
                    public void onAdjustVolume(int direction) {
                        if (direction == -1) {
                            stopAdhan();
                        }
                        mediaSession.release();
                    }
                };
        mediaSession.setPlaybackToRemote(volumeProvider);
        mediaSession.setActive(true);

        return mediaSession;
    }
}