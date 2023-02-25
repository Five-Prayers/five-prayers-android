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
    private final PreferencesHelper preferencesHelper;
    private final Context context;
    private boolean mediaCompleted;

    @Inject
    public AdhanPlayer(PreferencesHelper preferencesHelper, Context context) {
        this.preferencesHelper = preferencesHelper;
        this.context = context;
        adhanMediaPlayer = new MediaPlayer();
    }

    public void playAdhan(boolean fajr) {
        if (!adhanMediaPlayer.isPlaying()) {
            try {
                mediaCompleted = false;
                prepareMediaPlayer(getAdhanUri(fajr));
            } catch (Exception e) {
                Log.e("AdhanPlayer", "Cannot play Adhan", e);
            }
        }

        MediaSessionCompat adhanMediaSession = createMediaSession();

        adhanMediaPlayer.setOnPreparedListener(mp -> adhanMediaPlayer.start());

        adhanMediaPlayer.setOnCompletionListener(mp -> {
            if (preferencesHelper.isDouaeAfterAdhanEnabled()
                    && !Uri.parse(getCallerUriString(fajr)).equals(UiUtils.uriFromRaw(PreferencesConstants.SHORT_PRAYER_CALL, context))
                    && !Uri.parse(getCallerUriString(fajr)).equals(UiUtils.uriFromRaw(PreferencesConstants.TAKBEER_ONLY_CALL, context))
            ) {

                try {
                    if (!mediaCompleted) {
                        prepareMediaPlayer(getDouaeUri(context));
                        mediaCompleted = true;
                    } else {
                        adhanMediaSession.release();
                    }
                } catch (Exception e) {
                    Log.e("AdhanPlayer", "Cannot play Douae", e);
                }
            } else {
                adhanMediaSession.release();
            }
        });
    }

    public void stopAdhan() {
        if (adhanMediaPlayer != null && adhanMediaPlayer.isPlaying()) {
            adhanMediaPlayer.stop();
        }
    }

    private void prepareMediaPlayer(Uri uri) throws IOException {
        adhanMediaPlayer.reset();
        adhanMediaPlayer.setDataSource(context, uri);
        setAudioAttribute(adhanMediaPlayer);
        adhanMediaPlayer.setLooping(false);
        adhanMediaPlayer.prepareAsync();
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

    private MediaSessionCompat createMediaSession() {
        MediaSessionCompat mediaSession = new MediaSessionCompat(context, "Adhan");
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

    private String getCallerUriString(boolean fajr) {
        return fajr ? preferencesHelper.getFajrAdhanCaller() : preferencesHelper.getAdhanCaller();
    }
}