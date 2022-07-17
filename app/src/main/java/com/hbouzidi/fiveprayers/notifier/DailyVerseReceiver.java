package com.hbouzidi.fiveprayers.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.quran.dto.Ayah;
import com.hbouzidi.fiveprayers.quran.parser.QuranParser;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class DailyVerseReceiver extends BroadcastReceiver {

    @Inject
    TodayVerseNotification todayVerseNotification;

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((FivePrayerApplication) context.getApplicationContext())
                .receiverComponent
                .inject(this);

        sendDailyVerseNotification(context);
    }

    private void sendDailyVerseNotification(Context context) {
        QuranParser quranParser = QuranParser.getInstance();
        Map<String, List<Ayah>> dailyVerses = quranParser.getDailyVerses(context);

        Random rand = new Random();
        int random = rand.nextInt(dailyVerses.keySet().size() - 1);

        String dailyVerseKey = String.valueOf(random);

        List<Ayah> todayVerse = dailyVerses.get(dailyVerseKey);

        if (todayVerse != null) {
            preferencesHelper.setDailyVerseKey(dailyVerseKey);

            if (preferencesHelper.isNotificationsEnabled()) {
                todayVerseNotification.createNotificationChannel();
                todayVerseNotification.createNotification(todayVerse);
            }
        }
    }
}