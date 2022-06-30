package com.hbouzidi.fiveprayers.notifier;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.media.session.MediaButtonReceiver;

public class CustomMediaButtonReceiver extends MediaButtonReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            super.onReceive(context, intent);
        } catch (IllegalStateException e) {
            Log.d(this.getClass().getName(), e.getMessage());
        }
    }
}
