package com.hbouzidi.fiveprayers.utils;

import androidx.annotation.NonNull;

import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class PrayerUtilsTest {

    @Test
    public void getNextPrayer() {
        Map<PrayerEnum, LocalDateTime> prayers = new LinkedHashMap<>(5);
        prayers.put(PrayerEnum.FAJR, TimingUtils.transformTimingToDate("03:28", "08-06-2020", false));
        prayers.put(PrayerEnum.DHOHR, TimingUtils.transformTimingToDate("12:59", "08-06-2020", false));
        prayers.put(PrayerEnum.ASR, TimingUtils.transformTimingToDate("16:55", "08-06-2020", false));
        prayers.put(PrayerEnum.MAGHRIB, TimingUtils.transformTimingToDate("20:12", "08-06-2020", false));
        prayers.put(PrayerEnum.ICHA, TimingUtils.transformTimingToDate("00:21", "08-06-2020", true));

        //Icha is after midnight
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 0, 0)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate("09-06-2020", 0, 0)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayers, getDate("09-06-2020", 0, 21)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 2, 0)));
        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 3, 28)));
        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 3, 29)));

        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 12, 58)));
        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 12, 59)));
        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 13, 0)));

        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 16, 54)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 16, 55)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 16, 56)));

        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 18, 0)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 20, 12)));

        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayers, getDate("08-06-2020", 23, 30)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayers, getDate("09-06-2020", 0, 55)));

        //Icha is before midnight
        Map<PrayerEnum, LocalDateTime> prayerTwo = new LinkedHashMap<>(5);
        prayerTwo.put(PrayerEnum.FAJR, TimingUtils.transformTimingToDate("04:13", "08-06-2020", false));
        prayerTwo.put(PrayerEnum.DHOHR, TimingUtils.transformTimingToDate("13:50", "08-06-2020", false));
        prayerTwo.put(PrayerEnum.ASR, TimingUtils.transformTimingToDate("18:06", "08-06-2020", false));
        prayerTwo.put(PrayerEnum.MAGHRIB, TimingUtils.transformTimingToDate("21:52", "08-06-2020", false));
        prayerTwo.put(PrayerEnum.ICHA, TimingUtils.transformTimingToDate("23:27", "08-06-2020", false));


        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 23, 42)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 0, 30)));

        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 4, 0)));
        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 4, 13)));
        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 5, 42)));

        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 12, 0)));
        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 13, 50)));
        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 18, 5)));

        assertEquals(PrayerEnum.ASR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 17, 23)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 18, 6)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 20, 5)));

        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 21, 0)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 21, 52)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 21, 53)));

        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 23, 26)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 23, 27)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerTwo, getDate("08-06-2020", 0, 0)));

        //Maghrib & Icha is after midnight
        Map<PrayerEnum, LocalDateTime> prayerthree = new LinkedHashMap<>(5);
        prayerthree.put(PrayerEnum.FAJR, TimingUtils.transformTimingToDate("02:01", "08-06-2020", false));
        prayerthree.put(PrayerEnum.DHOHR, TimingUtils.transformTimingToDate("13:29", "08-06-2020", false));
        prayerthree.put(PrayerEnum.ASR, TimingUtils.transformTimingToDate("18:22", "08-06-2020", false));
        prayerthree.put(PrayerEnum.MAGHRIB, TimingUtils.transformTimingToDate("00:09", "08-06-2020", true));
        prayerthree.put(PrayerEnum.ICHA, TimingUtils.transformTimingToDate("00:55", "08-06-2020", true));

        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayerthree, getDate("08-06-2020", 23, 33)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerthree, getDate("08-06-2020", 0, 8)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerthree, getDate("08-06-2020", 0, 0)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayerthree, getDate("09-06-2020", 0, 0)));
        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getNextPrayer(prayerthree, getDate("09-06-2020", 0, 8)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerthree, getDate("08-06-2020", 0, 9)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayerthree, getDate("09-06-2020", 0, 9)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerthree, getDate("08-06-2020", 0, 54)));
        assertEquals(PrayerEnum.ICHA, PrayerUtils.getNextPrayer(prayerthree, getDate("09-06-2020", 0, 54)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerthree, getDate("08-06-2020", 0, 55)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerthree, getDate("09-06-2020", 0, 55)));
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getNextPrayer(prayerthree, getDate("08-06-2020", 1, 42)));
        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getNextPrayer(prayerthree, getDate("08-06-2020", 2, 1)));
    }

    @Test
    public void getNextPrayerKeyString() {
        Map<String, LocalDateTime> prayers = new HashMap<>(6);
        prayers.put(String.valueOf(PrayerEnum.FAJR), TimingUtils.transformTimingToDate("03:28", "08-06-2020", false));
        prayers.put(String.valueOf(ComplementaryTimingEnum.SUNRISE), TimingUtils.transformTimingToDate("05:28", "08-06-2020", false));
        prayers.put(String.valueOf(PrayerEnum.DHOHR), TimingUtils.transformTimingToDate("12:59", "08-06-2020", false));
        prayers.put(String.valueOf(PrayerEnum.ASR), TimingUtils.transformTimingToDate("16:55", "08-06-2020", false));
        prayers.put(String.valueOf(PrayerEnum.MAGHRIB), TimingUtils.transformTimingToDate("20:12", "08-06-2020", false));
        prayers.put(String.valueOf(PrayerEnum.ICHA), TimingUtils.transformTimingToDate("00:21", "08-06-2020", true));

        //Icha is after midnight
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 0, 0)));
        assertEquals(String.valueOf(PrayerEnum.ICHA), PrayerUtils.getNextPrayerKeyString(prayers, getDate("09-06-2020", 0, 0)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("09-06-2020", 0, 21)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 2, 0)));
        assertEquals(String.valueOf(ComplementaryTimingEnum.SUNRISE), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 3, 28)));
        assertEquals(String.valueOf(ComplementaryTimingEnum.SUNRISE), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 3, 29)));
        assertEquals(String.valueOf(PrayerEnum.DHOHR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 5, 28)));
        assertEquals(String.valueOf(PrayerEnum.DHOHR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 5, 29)));

        assertEquals(String.valueOf(PrayerEnum.DHOHR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 12, 58)));
        assertEquals(String.valueOf(PrayerEnum.ASR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 12, 59)));
        assertEquals(String.valueOf(PrayerEnum.ASR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 13, 0)));

        assertEquals(String.valueOf(PrayerEnum.ASR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 16, 54)));
        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 16, 55)));
        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 16, 56)));

        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 18, 0)));
        assertEquals(String.valueOf(PrayerEnum.ICHA), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 20, 12)));

        assertEquals(String.valueOf(PrayerEnum.ICHA), PrayerUtils.getNextPrayerKeyString(prayers, getDate("08-06-2020", 23, 30)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayers, getDate("09-06-2020", 0, 55)));

        //Icha is before midnight
        Map<String, LocalDateTime> prayerTwo = new HashMap<>(6);
        prayerTwo.put(String.valueOf(PrayerEnum.FAJR), TimingUtils.transformTimingToDate("04:13", "08-06-2020", false));
        prayerTwo.put(String.valueOf(ComplementaryTimingEnum.SUNRISE), TimingUtils.transformTimingToDate("5:50", "08-06-2020", false));
        prayerTwo.put(String.valueOf(PrayerEnum.DHOHR), TimingUtils.transformTimingToDate("13:50", "08-06-2020", false));
        prayerTwo.put(String.valueOf(PrayerEnum.ASR), TimingUtils.transformTimingToDate("18:06", "08-06-2020", false));
        prayerTwo.put(String.valueOf(PrayerEnum.MAGHRIB), TimingUtils.transformTimingToDate("21:52", "08-06-2020", false));
        prayerTwo.put(String.valueOf(PrayerEnum.ICHA), TimingUtils.transformTimingToDate("23:27", "08-06-2020", false));


        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 23, 42)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 0, 30)));

        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 4, 0)));
        assertEquals(String.valueOf(ComplementaryTimingEnum.SUNRISE), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 4, 13)));
        assertEquals(String.valueOf(ComplementaryTimingEnum.SUNRISE), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 5, 42)));
        assertEquals(String.valueOf(PrayerEnum.DHOHR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 5, 50)));

        assertEquals(String.valueOf(PrayerEnum.DHOHR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 12, 0)));
        assertEquals(String.valueOf(PrayerEnum.ASR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 13, 50)));
        assertEquals(String.valueOf(PrayerEnum.ASR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 18, 5)));

        assertEquals(String.valueOf(PrayerEnum.ASR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 17, 23)));
        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 18, 6)));
        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 20, 5)));

        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 21, 0)));
        assertEquals(String.valueOf(PrayerEnum.ICHA), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 21, 52)));
        assertEquals(String.valueOf(PrayerEnum.ICHA), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 21, 53)));

        assertEquals(String.valueOf(PrayerEnum.ICHA), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 23, 26)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 23, 27)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerTwo, getDate("08-06-2020", 0, 0)));

        //Maghrib & Icha is after midnight
        Map<String, LocalDateTime> prayerthree = new LinkedHashMap<>(6);
        prayerthree.put(String.valueOf(PrayerEnum.FAJR), TimingUtils.transformTimingToDate("02:01", "08-06-2020", false));
        prayerthree.put(String.valueOf(ComplementaryTimingEnum.SUNRISE), TimingUtils.transformTimingToDate("05:31", "08-06-2020", false));
        prayerthree.put(String.valueOf(PrayerEnum.DHOHR), TimingUtils.transformTimingToDate("13:29", "08-06-2020", false));
        prayerthree.put(String.valueOf(PrayerEnum.ASR), TimingUtils.transformTimingToDate("18:22", "08-06-2020", false));
        prayerthree.put(String.valueOf(PrayerEnum.MAGHRIB), TimingUtils.transformTimingToDate("00:09", "08-06-2020", true));
        prayerthree.put(String.valueOf(PrayerEnum.ICHA), TimingUtils.transformTimingToDate("00:55", "08-06-2020", true));

        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("08-06-2020", 23, 33)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("08-06-2020", 0, 8)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("08-06-2020", 0, 0)));
        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("09-06-2020", 0, 0)));
        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("09-06-2020", 0, 8)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("08-06-2020", 0, 9)));
        assertEquals(String.valueOf(PrayerEnum.ICHA), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("09-06-2020", 0, 9)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("08-06-2020", 0, 54)));
        assertEquals(String.valueOf(PrayerEnum.ICHA), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("09-06-2020", 0, 54)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("08-06-2020", 0, 55)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("09-06-2020", 0, 55)));
        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("08-06-2020", 1, 42)));
        assertEquals(String.valueOf(ComplementaryTimingEnum.SUNRISE), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("08-06-2020", 2, 1)));
        assertEquals(String.valueOf(PrayerEnum.DHOHR), PrayerUtils.getNextPrayerKeyString(prayerthree, getDate("08-06-2020", 5, 31)));
    }

    @Test
    public void getPreviousPrayerKey() {
        assertEquals(PrayerEnum.FAJR, PrayerUtils.getPreviousPrayerKey(PrayerEnum.DHOHR));

        assertEquals(PrayerEnum.DHOHR, PrayerUtils.getPreviousPrayerKey(PrayerEnum.ASR));

        assertEquals(PrayerEnum.ASR, PrayerUtils.getPreviousPrayerKey(PrayerEnum.MAGHRIB));

        assertEquals(PrayerEnum.MAGHRIB, PrayerUtils.getPreviousPrayerKey(PrayerEnum.ICHA));

        assertEquals(PrayerEnum.ICHA, PrayerUtils.getPreviousPrayerKey(PrayerEnum.FAJR));
    }

    @Test
    public void getPreviousMixedPrayerKey() {
        assertEquals(String.valueOf(ComplementaryTimingEnum.SUNRISE), PrayerUtils.getPreviousMixedPrayerKey(String.valueOf(PrayerEnum.DHOHR)));

        assertEquals(String.valueOf(PrayerEnum.DHOHR), PrayerUtils.getPreviousMixedPrayerKey(String.valueOf(PrayerEnum.ASR)));

        assertEquals(String.valueOf(PrayerEnum.ASR), PrayerUtils.getPreviousMixedPrayerKey(String.valueOf(PrayerEnum.MAGHRIB)));

        assertEquals(String.valueOf(PrayerEnum.MAGHRIB), PrayerUtils.getPreviousMixedPrayerKey(String.valueOf(PrayerEnum.ICHA)));

        assertEquals(String.valueOf(PrayerEnum.ICHA), PrayerUtils.getPreviousMixedPrayerKey(String.valueOf(PrayerEnum.FAJR)));

        assertEquals(String.valueOf(PrayerEnum.FAJR), PrayerUtils.getPreviousMixedPrayerKey(String.valueOf(ComplementaryTimingEnum.SUNRISE)));
    }

    @NonNull
    private LocalDateTime getDate(String dateStr, int hour, int minute) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault());
        return LocalDate.parse(dateStr, f).atTime(hour, minute);
    }
}