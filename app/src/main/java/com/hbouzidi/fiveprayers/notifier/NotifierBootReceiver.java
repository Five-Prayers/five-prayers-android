package com.hbouzidi.fiveprayers.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hbouzidi.fiveprayers.job.PeriodicWorkCreator;

public class NotifierBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PeriodicWorkCreator.schedulePrayerUpdater(context);
    }
}
