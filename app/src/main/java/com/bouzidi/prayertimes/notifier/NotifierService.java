package com.bouzidi.prayertimes.notifier;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.bouzidi.prayertimes.timings.DayPrayer;

import java.util.Objects;

public class NotifierService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DayPrayer dayPrayer = (DayPrayer) intent.getSerializableExtra("dayPrayer");
        NotifierHelper.scheduleNextPrayerAlarms(getApplicationContext(), Objects.requireNonNull(dayPrayer));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}