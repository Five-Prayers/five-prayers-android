package com.hbouzidi.fiveprayers.ui.settings.common;

import static android.content.Context.AUDIO_SERVICE;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.SeekBarPreference;

import com.hbouzidi.fiveprayers.R;

import java.io.IOException;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class VolumeSeekbarPreference extends SeekBarPreference {

    private final MediaPlayer mMediaPlayer;

    public VolumeSeekbarPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_ALARM));

        setValue(audioManager
                .getStreamVolume(AudioManager.STREAM_ALARM));

        mMediaPlayer = new MediaPlayer();

        setOnPreferenceChangeListener((preference, newValue) -> {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (Integer) newValue, AudioManager.FLAG_PLAY_SOUND);
            initializeAndPlayTakbir(context);
            return true;
        });
    }

    private void initializeAndPlayTakbir(Context context) {
        Uri takbirUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.takbeer_only_call);

        if (takbirUri != null) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(getContext(), takbirUri);
                setAudioAttribute();
                mMediaPlayer.setLooping(false);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAudioAttribute() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setUsage(AudioAttributes.USAGE_ALARM);
            builder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            builder.setLegacyStreamType(AudioManager.STREAM_ALARM);

            mMediaPlayer.setAudioAttributes(builder.build());
        } else {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
    }
}
