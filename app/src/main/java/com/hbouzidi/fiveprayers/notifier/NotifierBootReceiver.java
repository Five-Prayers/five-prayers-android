package com.hbouzidi.fiveprayers.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hbouzidi.fiveprayers.job.PeriodicWorkCreator;

import java.util.Objects;

public class NotifierBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            PeriodicWorkCreator.schedulePrayerUpdater(context);
        }
    }
}
