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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class ReminderPlayer {

    private final MediaPlayer mediaPlayer;
    private final Context context;

    @Inject
    public ReminderPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    public void playAdhan(boolean isReminder) {
        if (!mediaPlayer.isPlaying()) {
            try {
                initializeAdhanMediaPlayer(isReminder);
            } catch (IOException e) {
                Log.e("AdhanPlayer", "Cannot play Adhan", e);
            }

            mediaPlayer.start();
        }

        setOnCompletionListeners();
    }

    public void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void setOnCompletionListeners() {
        MediaSessionCompat adhanMediaSession = createMediaSession();

        mediaPlayer.setOnCompletionListener(mp -> adhanMediaSession.release());
    }

    private void initializeAdhanMediaPlayer(boolean isReminder) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(context, getAdhanUri(context, isReminder));
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

    private Uri getAdhanUri(Context context, boolean isReminder) {
        int mediaId;

        if (isReminder) {
            mediaId = R.raw.prayer_reminder_call;
        } else {
            mediaId = R.raw.short_prayer_call;
        }

        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + mediaId);
    }

    private MediaSessionCompat createMediaSession() {
        MediaSessionCompat mediaSession = new MediaSessionCompat(context, "Adhan_Reminder");
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0)
                .build());

        VolumeProviderCompat volumeProvider =
                new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, 100, 50) {
                    @Override
                    public void onAdjustVolume(int direction) {
                        if (direction == -1) {
                            stop();
                        }
                        mediaSession.release();
                    }
                };
        mediaSession.setPlaybackToRemote(volumeProvider);
        mediaSession.setActive(true);

        return mediaSession;
    }
}