package com.hbouzidi.fiveprayers.notifier;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.timings.DayPrayer;

import java.util.Objects;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class NotifierJobService extends JobIntentService {

    private static final int JOB_ID = 201;

    @Inject
    public PrayerAlarmScheduler prayerAlarmScheduler;

    @Override
    public void onCreate() {
        ((FivePrayerApplication)getApplicationContext())
                .serviceComponent
                .inject(this);

        super.onCreate();
    }

    @Override
    public void onHandleWork(@NonNull Intent intent) {
        DayPrayer dayPrayer = (DayPrayer) intent.getSerializableExtra("dayPrayer");
        prayerAlarmScheduler.scheduleNextPrayerAlarms(Objects.requireNonNull(dayPrayer));
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotifierJobService.class, JOB_ID, intent);
    }
}