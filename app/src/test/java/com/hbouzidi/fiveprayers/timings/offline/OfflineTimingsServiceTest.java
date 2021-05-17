package com.hbouzidi.fiveprayers.timings.offline;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;

import androidx.test.core.app.ApplicationProvider;

import com.hbouzidi.fiveprayers.FakeFivePrayerApplication;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.shadows.ShadowTimingUtils;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.TimingsPreferences;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDate;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@RunWith(RobolectricTestRunner.class)
@Config(minSdk = 28, maxSdk = 28, application = FakeFivePrayerApplication.class, shadows = {ShadowTimingUtils.class})
public class OfflineTimingsServiceTest {

    Context applicationContext;

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();
    private OfflineTimingsService offlineTimingsService;

    @Before
    public void before() {
        applicationContext = ApplicationProvider.getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(applicationContext);
        offlineTimingsService = new OfflineTimingsService(preferencesHelper);
    }

    @Test
    public void should_get_day_prayers() {
        LocalDate localDate = LocalDate.of(2021, 4, 25);
        Address address = new Address(Locale.getDefault());
        address.setLatitude(48.9220615);
        address.setLongitude(2.2533313);

        setTunes(0, 0, 0, 0, 0);

        TimingsPreferences timingsPreferences = new TimingsPreferences(
                CalculationMethodEnum.getDefault(),
                null,
                LatitudeAdjustmentMethod.getDefault(),
                SchoolAdjustmentMethod.getDefault(),
                MidnightModeAdjustmentMethod.getDefault(),
                0
        );

        DayPrayer prayerTimings = offlineTimingsService.getPrayerTimings(localDate, address, timingsPreferences);

        Assertions.assertThat(prayerTimings.getCity()).isNull();
        Assertions.assertThat(prayerTimings.getCountry()).isNull();
        Assertions.assertThat(prayerTimings.getLatitude()).isEqualTo(48.9220615);
        Assertions.assertThat(prayerTimings.getLongitude()).isEqualTo(2.2533313);
        Assertions.assertThat(prayerTimings.getGregorianDay()).isEqualTo(25);
        Assertions.assertThat(prayerTimings.getGregorianMonthNumber()).isEqualTo(4);
        Assertions.assertThat(prayerTimings.getGregorianYear()).isEqualTo(2021);

        Assertions.assertThat(prayerTimings.getHijriDay()).isEqualTo(13);
        Assertions.assertThat(prayerTimings.getHijriMonthNumber()).isEqualTo(9);
        Assertions.assertThat(prayerTimings.getHijriYear()).isEqualTo(1442);

        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 4, 34));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 13, 49));
//        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 17, 46));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 20, 58));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 22, 57));

        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 6, 41));
        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 6, 56));
    }

    @Test
    public void should_get_day_prayers_with_adjustment() {
        LocalDate localDate = LocalDate.of(2021, 4, 25);
        Address address = new Address(Locale.getDefault());
        address.setLatitude(48.9220615);
        address.setLongitude(2.2533313);

        setTunes(1, 1, 1, 1, -1);

        TimingsPreferences timingsPreferences = new TimingsPreferences(
                CalculationMethodEnum.getDefault(),
                null,
                LatitudeAdjustmentMethod.getDefault(),
                SchoolAdjustmentMethod.getDefault(),
                MidnightModeAdjustmentMethod.getDefault(),
                -1
        );

        DayPrayer prayerTimings = offlineTimingsService.getPrayerTimings(localDate, address, timingsPreferences);

        Assertions.assertThat(prayerTimings.getCity()).isNull();
        Assertions.assertThat(prayerTimings.getCountry()).isNull();
        Assertions.assertThat(prayerTimings.getLatitude()).isEqualTo(48.9220615);
        Assertions.assertThat(prayerTimings.getLongitude()).isEqualTo(2.2533313);
        Assertions.assertThat(prayerTimings.getGregorianDay()).isEqualTo(25);
        Assertions.assertThat(prayerTimings.getGregorianMonthNumber()).isEqualTo(4);
        Assertions.assertThat(prayerTimings.getGregorianYear()).isEqualTo(2021);

        Assertions.assertThat(prayerTimings.getHijriDay()).isEqualTo(12);
        Assertions.assertThat(prayerTimings.getHijriMonthNumber()).isEqualTo(9);
        Assertions.assertThat(prayerTimings.getHijriYear()).isEqualTo(1442);

        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 4, 35));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 13, 50));
     //   Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 17, 47));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 20, 59));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 22, 56));

        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 6, 41));
        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 6, 56));
    }

    @Test
    public void should_get_day_prayers_with_um_al_Qura_method_in_ramadan() {
        LocalDate localDate = LocalDate.of(2021, 4, 25);
        Address address = new Address(Locale.getDefault());
        address.setLatitude(48.9220615);
        address.setLongitude(2.2533313);

        setTunes(0, 0, 0, 0, 0);

        TimingsPreferences timingsPreferences = new TimingsPreferences(
                CalculationMethodEnum.UMM_AL_QURA_UNIVERSITY_MAKKAH,
                null,
                LatitudeAdjustmentMethod.getDefault(),
                SchoolAdjustmentMethod.getDefault(),
                MidnightModeAdjustmentMethod.getDefault(),
                0
        );

        DayPrayer prayerTimings = offlineTimingsService.getPrayerTimings(localDate, address, timingsPreferences);

        Assertions.assertThat(prayerTimings.getCity()).isNull();
        Assertions.assertThat(prayerTimings.getCountry()).isNull();
        Assertions.assertThat(prayerTimings.getLatitude()).isEqualTo(48.9220615);
        Assertions.assertThat(prayerTimings.getLongitude()).isEqualTo(2.2533313);
        Assertions.assertThat(prayerTimings.getGregorianDay()).isEqualTo(25);
        Assertions.assertThat(prayerTimings.getGregorianMonthNumber()).isEqualTo(4);
        Assertions.assertThat(prayerTimings.getGregorianYear()).isEqualTo(2021);

        Assertions.assertThat(prayerTimings.getHijriDay()).isEqualTo(13);
        Assertions.assertThat(prayerTimings.getHijriMonthNumber()).isEqualTo(9);
        Assertions.assertThat(prayerTimings.getHijriYear()).isEqualTo(1442);

        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 4, 29));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 13, 49));
  //      Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 17, 46));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 20, 58));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(prayerTimings.getTimings().get(PrayerEnum.MAGHRIB).plusMinutes(120).withNano(0));

        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 6, 41));
        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(2021, 4, 25, 6, 56));
    }

    @Test
    public void should_get_day_prayers_with_um_al_Qura_method_in_chawal() {
        LocalDate localDate = LocalDate.of(2021, 5, 25);
        Address address = new Address(Locale.getDefault());
        address.setLatitude(48.9220615);
        address.setLongitude(2.2533313);

        setTunes(0, 0, 0, 0, 0);

        TimingsPreferences timingsPreferences = new TimingsPreferences(
                CalculationMethodEnum.UMM_AL_QURA_UNIVERSITY_MAKKAH,
                null,
                LatitudeAdjustmentMethod.getDefault(),
                SchoolAdjustmentMethod.getDefault(),
                MidnightModeAdjustmentMethod.getDefault(),
                0
        );

        DayPrayer prayerTimings = offlineTimingsService.getPrayerTimings(localDate, address, timingsPreferences);

        Assertions.assertThat(prayerTimings.getCity()).isNull();
        Assertions.assertThat(prayerTimings.getCountry()).isNull();
        Assertions.assertThat(prayerTimings.getLatitude()).isEqualTo(48.9220615);
        Assertions.assertThat(prayerTimings.getLongitude()).isEqualTo(2.2533313);
        Assertions.assertThat(prayerTimings.getGregorianDay()).isEqualTo(25);
        Assertions.assertThat(prayerTimings.getGregorianMonthNumber()).isEqualTo(5);
        Assertions.assertThat(prayerTimings.getGregorianYear()).isEqualTo(2021);

        Assertions.assertThat(prayerTimings.getHijriDay()).isEqualTo(13);
        Assertions.assertThat(prayerTimings.getHijriMonthNumber()).isEqualTo(10);
        Assertions.assertThat(prayerTimings.getHijriYear()).isEqualTo(1442);

        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 3, 24));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 13, 48));
  //      Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 18, 1));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 21, 39));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(prayerTimings.getTimings().get(PrayerEnum.MAGHRIB).plusMinutes(90).withNano(0));

        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 5, 57));
        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 6, 12));
    }

    @Test
    public void should_get_day_prayers_with_shafii_mathod() {
        LocalDate localDate = LocalDate.of(2021, 5, 25);
        Address address = new Address(Locale.getDefault());
        address.setLatitude(48.9220615);
        address.setLongitude(2.2533313);

        setTunes(0, 0, 0, 0, 0);

        TimingsPreferences timingsPreferences = new TimingsPreferences(
                CalculationMethodEnum.UMM_AL_QURA_UNIVERSITY_MAKKAH,
                null,
                LatitudeAdjustmentMethod.getDefault(),
                SchoolAdjustmentMethod.HANAFI,
                MidnightModeAdjustmentMethod.getDefault(),
                0
        );

        DayPrayer prayerTimings = offlineTimingsService.getPrayerTimings(localDate, address, timingsPreferences);

        Assertions.assertThat(prayerTimings.getCity()).isNull();
        Assertions.assertThat(prayerTimings.getCountry()).isNull();
        Assertions.assertThat(prayerTimings.getLatitude()).isEqualTo(48.9220615);
        Assertions.assertThat(prayerTimings.getLongitude()).isEqualTo(2.2533313);
        Assertions.assertThat(prayerTimings.getGregorianDay()).isEqualTo(25);
        Assertions.assertThat(prayerTimings.getGregorianMonthNumber()).isEqualTo(5);
        Assertions.assertThat(prayerTimings.getGregorianYear()).isEqualTo(2021);

        Assertions.assertThat(prayerTimings.getHijriDay()).isEqualTo(13);
        Assertions.assertThat(prayerTimings.getHijriMonthNumber()).isEqualTo(10);
        Assertions.assertThat(prayerTimings.getHijriYear()).isEqualTo(1442);

        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 3, 24));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 13, 48));
    //    Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 19, 12));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 21, 39));
        Assertions.assertThat(prayerTimings.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(prayerTimings.getTimings().get(PrayerEnum.MAGHRIB).plusMinutes(90).withNano(0));

        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 5, 57));
        Assertions.assertThat(prayerTimings.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(2021, 5, 25, 6, 12));
    }

    @Test
    public void should_get_day_prayers_for_all_methods() {
        LocalDate localDate = LocalDate.of(2021, 5, 25);
        Address address = new Address(Locale.getDefault());
        address.setLatitude(48.9220615);
        address.setLongitude(2.2533313);

        setTunes(0, 0, 0, 0, 0);

        for (CalculationMethodEnum methodEnum : CalculationMethodEnum.values()) {
            TimingsPreferences timingsPreferences = new TimingsPreferences(
                    methodEnum,
                    null,
                    LatitudeAdjustmentMethod.getDefault(),
                    SchoolAdjustmentMethod.getDefault(),
                    MidnightModeAdjustmentMethod.getDefault(),
                    0
            );

            DayPrayer prayerTimings = offlineTimingsService.getPrayerTimings(localDate, address, timingsPreferences);

            Assertions.assertThat(prayerTimings).isNotNull();
            Assertions.assertThat(prayerTimings.getCity()).isNull();
            Assertions.assertThat(prayerTimings.getCountry()).isNull();
        }
    }

    @Test
    public void should_get_day_prayers_for_all_latitude_methods() {
        LocalDate localDate = LocalDate.of(2021, 5, 25);
        Address address = new Address(Locale.getDefault());
        address.setLatitude(48.9220615);
        address.setLongitude(2.2533313);

        setTunes(0, 0, 0, 0, 0);

        for (LatitudeAdjustmentMethod latitudeAdjustmentMethod : LatitudeAdjustmentMethod.values()) {
            TimingsPreferences timingsPreferences = new TimingsPreferences(
                    CalculationMethodEnum.getDefault(),
                    null,
                    latitudeAdjustmentMethod,
                    SchoolAdjustmentMethod.getDefault(),
                    MidnightModeAdjustmentMethod.getDefault(),
                    0
            );

            DayPrayer prayerTimings = offlineTimingsService.getPrayerTimings(localDate, address, timingsPreferences);

            Assertions.assertThat(prayerTimings).isNotNull();
            Assertions.assertThat(prayerTimings.getCity()).isNull();
            Assertions.assertThat(prayerTimings.getCountry()).isNull();
        }
    }

    @Test
    public void should_get_calendar() {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(48.9220615);
        address.setLongitude(2.2533313);

        setTunes(0, 0, 0, 0, 0);

        TimingsPreferences timingsPreferences = new TimingsPreferences(
                CalculationMethodEnum.getDefault(),
                null,
                LatitudeAdjustmentMethod.getDefault(),
                SchoolAdjustmentMethod.getDefault(),
                MidnightModeAdjustmentMethod.getDefault(),
                0
        );

        List<DayPrayer> prayerCalendar = offlineTimingsService.getPrayerCalendar(address, 4, 2021, timingsPreferences);

        Assertions.assertThat(prayerCalendar).isNotNull();
        Assertions.assertThat(prayerCalendar.size()).isEqualTo(30);
    }

    @Test
    public void should_get_hijri_calendar() {
        List<AladhanDate> hijriCalendar = offlineTimingsService.getHijriCalendar(4, 2021, 0);

        Assertions.assertThat(hijriCalendar).isNotNull();
        Assertions.assertThat(hijriCalendar.size()).isEqualTo(30);
    }

    private void setTunes(int fajrTimingAdjustment, int dohrTimingAdjustment, int asrTimingAdjustment, int maghrebTimingAdjustment, int ichaTimingAdjustment) {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(PreferencesConstants.TIMING_ADJUSTMENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(PreferencesConstants.FAJR_TIMING_ADJUSTMENT, fajrTimingAdjustment);
        editor.putInt(PreferencesConstants.DOHR_TIMING_ADJUSTMENT, dohrTimingAdjustment);
        editor.putInt(PreferencesConstants.ASR_TIMING_ADJUSTMENT, asrTimingAdjustment);
        editor.putInt(PreferencesConstants.MAGHREB_TIMING_ADJUSTMENT, maghrebTimingAdjustment);
        editor.putInt(PreferencesConstants.ICHA_TIMING_ADJUSTMENT, ichaTimingAdjustment);

        editor.commit();
    }
}