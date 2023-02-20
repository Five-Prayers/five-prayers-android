package com.hbouzidi.fiveprayers.timings;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ApplicationProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbouzidi.fiveprayers.FakeFivePrayerApplication;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.database.PrayerRegistry;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.shadows.ShadowTimingUtils;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanAPIService;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTimingsService;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.londonprayertimes.LondonUnifiedPrayerAPIService;
import com.hbouzidi.fiveprayers.timings.londonprayertimes.LondonUnifiedPrayerTimingsService;
import com.hbouzidi.fiveprayers.timings.offline.OfflineTimingsService;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.appflate.restmock.JVMFileParser;
import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RESTMockServerStarter;
import io.appflate.restmock.android.AndroidLogger;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static io.appflate.restmock.utils.RequestMatchers.pathContains;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@RunWith(RobolectricTestRunner.class)
@Config(minSdk = 28, maxSdk = 28, application = FakeFivePrayerApplication.class, shadows = {ShadowTimingUtils.class})
public class TimingsServiceTest {

    Context applicationContext;

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();
    private TimingServiceFactory timingServiceFactory;

    @Before
    public void before() {
        RESTMockServerStarter.startSync(new JVMFileParser(), new AndroidLogger());

        applicationContext = ApplicationProvider.getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(applicationContext);

        AladhanAPIService aladhanAPIService = new AladhanAPIService(provideRetrofit());
        PrayerRegistry prayerRegistry = new PrayerRegistry(applicationContext, preferencesHelper);
        OfflineTimingsService offlineTimingsService = new OfflineTimingsService(preferencesHelper);

        AladhanTimingsService aladhanTimingsService = new AladhanTimingsService(aladhanAPIService, prayerRegistry, offlineTimingsService, preferencesHelper);

        LondonUnifiedPrayerAPIService londonUnifiedPrayerAPIService = new LondonUnifiedPrayerAPIService(provideRetrofit());
        LondonUnifiedPrayerTimingsService londonUnifiedPrayerTimingsService = new LondonUnifiedPrayerTimingsService(aladhanAPIService, londonUnifiedPrayerAPIService, prayerRegistry, offlineTimingsService, preferencesHelper);

        timingServiceFactory = new TimingServiceFactory(londonUnifiedPrayerTimingsService, aladhanTimingsService);
    }

    @After
    public void tearDown() throws IOException {
        ShadowTimingUtils.TIMEZONE = "Europe/Paris";
        RESTMockServer.shutdown();
        RESTMockServer.reset();
    }

    @Test
    public void should_get_timings_from_adhan_api() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<DayPrayer> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/timings"))
                .thenReturnFile(200, "responses/london_adhan_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setLocality("Westminster");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.getDefault());
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.getDefault());

        Single<DayPrayer> timingsByCity = timingsService.getTimingsByCity(localDate, lastKnownAddress);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayer -> {
                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619305200);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(3, 23)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 58)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 56)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 14)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(22, 25)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 44)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 59)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 0)));

                    return true;
                });
    }

    @Test
    public void should_get_offline_timings_when_locality_is_null() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<DayPrayer> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/timings"))
                .thenReturnFile(200, "responses/london_adhan_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.getDefault());
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.getDefault());

        Single<DayPrayer> timingsByCity = timingsService.getTimingsByCity(localDate, lastKnownAddress);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayer -> {
                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619305200);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(3, 24)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 58)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 57)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 14)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(22, 25)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 44)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 59)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 0)));

                    return true;
                });
    }

    @Test
    public void should_get_offline_timings_when_api_throws_error() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<DayPrayer> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/timings"))
                .thenReturnFile(403, "responses/london_adhan_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setLocality("Westminster");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.getDefault());
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.getDefault());

        Single<DayPrayer> timingsByCity = timingsService.getTimingsByCity(localDate, lastKnownAddress);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayer -> {
                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619305200);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(3, 24)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 58)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 57)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 14)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(22, 25)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 44)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 59)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 0)));

                    return true;
                });
    }

    @Test
    public void should_get_timings_from_lut_api() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<DayPrayer> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/timings"))
                .thenReturnFile(200, "responses/london_adhan_api_response.json");

        RESTMockServer
                .whenGET(pathContains("/times"))
                .thenReturnFile(200, "responses/lut_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setLocality("Westminster");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES);
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES);

        Single<DayPrayer> timingsByCity = timingsService.getTimingsByCity(localDate, lastKnownAddress);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayer -> {
                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619305200);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(4, 6)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(13, 3)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 57)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 17)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(21, 30)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 41)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 56)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 29)));

                    return true;
                });
    }


    @Test
    public void should_get_timings_from_api_error() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<DayPrayer> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/timings"))
                .thenReturnFile(500, "responses/london_adhan_api_response.json");

        RESTMockServer
                .whenGET(pathContains("/times"))
                .thenReturnFile(500, "responses/lut_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setLocality("Westminster");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES);
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES);

        Single<DayPrayer> timingsByCity = timingsService.getTimingsByCity(localDate, lastKnownAddress);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayer -> {
                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619305200);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(3, 24)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 58)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 57)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 14)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(22, 25)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 44)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 59)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 0)).withNano(0));

                    return true;
                });
    }

    @Test
    public void should_get_morocco_timings_from_adhan_api() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Africa/Casablanca";

        TestObserver<DayPrayer> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/timings"))
                .thenReturnFile(200, "responses/morocco_adhan_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 27);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(33.9715904);
        lastKnownAddress.setLongitude(-6.8498129);
        lastKnownAddress.setLocality("Rabat");
        lastKnownAddress.setCountryName("Morocco");
        lastKnownAddress.setCountryCode("MA");

        setCalculationMethod(CalculationMethodEnum.MOROCCAN_MINISTRY_OF_ISLAMIC_AFFAIRS);
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.MOROCCAN_MINISTRY_OF_ISLAMIC_AFFAIRS);

        Single<DayPrayer> timingsByCity = timingsService.getTimingsByCity(localDate, lastKnownAddress);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayer -> {
                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(27);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619510344L);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Africa/Casablanca");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(4, 3)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 25)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 7)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(19, 8)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 38)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 42)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 57)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 4)));

                    return true;
                });
    }

    @Test
    public void should_get_morocco_offline_timings_when_adhan_api_error() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Africa/Casablanca";

        TestObserver<DayPrayer> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/timings"))
                .thenReturnFile(500, "responses/morocco_adhan_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 27);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(33.9715904);
        lastKnownAddress.setLongitude(-6.8498129);
        lastKnownAddress.setLocality("Rabat");
        lastKnownAddress.setCountryName("Morocco");
        lastKnownAddress.setCountryCode("MA");

        setCalculationMethod(CalculationMethodEnum.MOROCCAN_MINISTRY_OF_ISLAMIC_AFFAIRS);
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.MOROCCAN_MINISTRY_OF_ISLAMIC_AFFAIRS);

        Single<DayPrayer> timingsByCity = timingsService.getTimingsByCity(localDate, lastKnownAddress);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayer -> {
                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(27);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619481600);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Africa/Casablanca");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(4, 5)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 25)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 7)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(19, 8)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 34)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 42)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 57)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 6)));

                    return true;
                });
    }

    @Test
    public void should_get_calendar_from_adhan_api() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<List<DayPrayer>> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/calendar"))
                .thenReturnFile(200, "responses/london_calendar_adhan_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setLocality("Westminster");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.getDefault());
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.getDefault());

        Single<List<DayPrayer>> timingsByCity = timingsService.getCalendarByCity(lastKnownAddress, 4, 2021);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayers -> {
                    Assertions.assertThat(dayPrayers.size()).isEqualTo(30);

                    DayPrayer dayPrayer = dayPrayers.get(24);

                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619337661);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(3, 23)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 58)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 56)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 14)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(22, 25)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 44)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 59)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 0)));

                    return true;
                });
    }


    @Test
    public void should_get_offline_calendar_when_locality_is_null() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<List<DayPrayer>> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/calendar"))
                .thenReturnFile(200, "responses/london_calendar_adhan_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.getDefault());
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.getDefault());

        Single<List<DayPrayer>> timingsByCity = timingsService.getCalendarByCity(lastKnownAddress, 4, 2021);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayers -> {

                    Assertions.assertThat(dayPrayers.size()).isEqualTo(30);

                    DayPrayer dayPrayer = dayPrayers.get(24);

                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619305200);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(3, 24)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 58)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 57)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 14)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(22, 25)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 44)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 59)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 0)));

                    return true;
                });
    }

    @Test
    public void should_get_offline_calendar_when_api_error() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<List<DayPrayer>> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/calendar"))
                .thenReturnFile(403, "responses/london_calendar_adhan_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setCountryName("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.getDefault());
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.getDefault());

        Single<List<DayPrayer>> timingsByCity = timingsService.getCalendarByCity(lastKnownAddress, 4, 2021);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayers -> {

                    Assertions.assertThat(dayPrayers.size()).isEqualTo(30);

                    DayPrayer dayPrayer = dayPrayers.get(24);

                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619305200);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(3, 24)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 58)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 57)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 14)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(22, 25)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 44)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 59)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 0)));

                    return true;
                });
    }

    @Test
    public void should_get_calendar_from_lut_api() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<List<DayPrayer>> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/calendar"))
                .thenReturnFile(200, "responses/london_calendar_adhan_api_response.json");

        RESTMockServer
                .whenGET(pathContains("/times"))
                .thenReturnFile(200, "responses/lut_calendar_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setLocality("Westminster");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES);
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES);

        Single<List<DayPrayer>> timingsByCity = timingsService.getCalendarByCity(lastKnownAddress, 4, 2021);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayers -> {

                    Assertions.assertThat(dayPrayers.size()).isEqualTo(30);

                    DayPrayer dayPrayer = dayPrayers.get(24);

                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619337661L);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(4, 6)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(13, 3)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 57)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 17)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(21, 30)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 41)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 56)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 29)));

                    return true;
                });
    }

    @Test
    public void should_get_offline_calendar_when_lut_api_error() throws Exception {
        ShadowTimingUtils.TIMEZONE = "Europe/London";

        TestObserver<List<DayPrayer>> observer = new TestObserver<>();

        RESTMockServer
                .whenGET(pathContains("/calendar"))
                .thenReturnFile(200, "responses/london_calendar_adhan_api_response.json");

        RESTMockServer
                .whenGET(pathContains("/times"))
                .thenReturnFile(404, "responses/lut_calendar_api_response.json");

        LocalDate localDate = LocalDate.of(2021, 4, 25);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.508515);
        lastKnownAddress.setLongitude(-0.1254872);
        lastKnownAddress.setLocality("Westminster");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        setCalculationMethod(CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES);
        TimingsService timingsService = timingServiceFactory.create(CalculationMethodEnum.LONDON_UNIFIED_PRAYER_TIMES);

        Single<List<DayPrayer>> timingsByCity = timingsService.getCalendarByCity(lastKnownAddress, 4, 2021);

        timingsByCity.subscribe(observer);
        observer.await()
                .assertComplete()
                .assertNoErrors()
                .assertValue(dayPrayers -> {

                    Assertions.assertThat(dayPrayers.size()).isEqualTo(30);

                    DayPrayer dayPrayer = dayPrayers.get(24);

                    Assertions.assertThat(dayPrayer.getGregorianYear()).isEqualTo(2021);
                    Assertions.assertThat(dayPrayer.getGregorianMonthNumber()).isEqualTo(4);
                    Assertions.assertThat(dayPrayer.getGregorianDay()).isEqualTo(25);
                    Assertions.assertThat(dayPrayer.getTimestamp()).isEqualTo(1619305200);
                    Assertions.assertThat(dayPrayer.getTimezone()).isEqualTo("Europe/London");
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.FAJR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(3, 24)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.DHOHR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(12, 58)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ASR).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(16, 57)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.MAGHRIB).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(20, 14)));
                    Assertions.assertThat(dayPrayer.getTimings().get(PrayerEnum.ICHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(22, 25)));

                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.SUNRISE).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 44)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.DOHA).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(5, 59)));
                    Assertions.assertThat(dayPrayer.getComplementaryTiming().get(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT).withNano(0)).isEqualTo(LocalDateTime.of(localDate, LocalTime.of(1, 0)));

                    return true;
                });
    }

    private Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .baseUrl(RESTMockServer.getUrl())
                .client(provideNonCachedOkHttpClient())
                .build();
    }

    private Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    private OkHttpClient provideNonCachedOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        return builder
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private void setCalculationMethod(CalculationMethodEnum calculationMethod) {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();

        edit.putString(PreferencesConstants.TIMINGS_CALCULATION_METHOD_PREFERENCE, String.valueOf(calculationMethod));

        edit.commit();
    }
}