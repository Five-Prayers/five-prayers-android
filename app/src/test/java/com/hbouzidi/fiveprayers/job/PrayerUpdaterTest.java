package com.hbouzidi.fiveprayers.job;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;

import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;

import com.hbouzidi.fiveprayers.location.address.AddressHelper;
import com.hbouzidi.fiveprayers.location.tracker.LocationHelper;
import com.hbouzidi.fiveprayers.notifier.PrayerAlarmScheduler;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.TimingServiceFactory;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTimingsService;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;

import io.reactivex.rxjava3.core.Single;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */

@RunWith(RobolectricTestRunner.class)
@Config(maxSdk = 28)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({PrayerUpdater.class, AddressHelper.class, LocationHelper.class,
        AladhanTimingsService.class, PrayerAlarmScheduler.class, PreferencesHelper.class,
        AladhanTimingsService.class, TimingServiceFactory.class})
public class PrayerUpdaterTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    Context mockContext;

    @Mock
    AladhanTimingsService aladhanTimingsService;

    @Before
    public void before() {
        this.mockContext = Mockito.mock(Context.class);
        this.aladhanTimingsService = Mockito.mock(AladhanTimingsService.class);
    }

    @Test
    public void testPrayerUpdaterWork() throws Exception {
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.mockStatic(AddressHelper.class);
        PowerMockito.mockStatic(PreferencesHelper.class);
        PowerMockito.mockStatic(AladhanTimingsService.class);
        PowerMockito.mockStatic(TimingServiceFactory.class);

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(45.2);
        lastKnownAddress.setLongitude(-12.2);
        lastKnownAddress.setLocality("Colombes");
        lastKnownAddress.setCountryName("France");

        PowerMockito.when(PreferencesHelper.getCalculationMethod(mockContext)).thenReturn(CalculationMethodEnum.getDefault());
        PowerMockito.when(LocationHelper.getLocation(mockContext)).thenReturn(Single.just(newLocation));
        PowerMockito.when(AddressHelper.getAddressFromLocation(newLocation, mockContext)).thenReturn(Single.just(lastKnownAddress));
        PowerMockito.when(TimingServiceFactory.create(any())).thenReturn(aladhanTimingsService);

        Mockito.when(aladhanTimingsService.getTimingsByCity(any(), any(), any())).thenReturn(Single.just(new DayPrayer()));

        PowerMockito.spy(PrayerAlarmScheduler.class);
        PowerMockito
                .doNothing()
                .when(PrayerAlarmScheduler.class, "scheduleNextPrayerAlarms", any(), any());

        PrayerUpdater prayerUpdater = TestListenableWorkerBuilder.from(mockContext, PrayerUpdater.class).build();

        ListenableWorker.Result result = prayerUpdater.startWork().get();

        Assert.assertEquals(ListenableWorker.Result.success(), result);
    }

    @Test
    public void testPrayerUpdaterWork_when_single_throw_error() throws Exception {
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.mockStatic(AddressHelper.class);
        PowerMockito.mockStatic(AladhanTimingsService.class);
        PowerMockito.mockStatic(PreferencesHelper.class);
        PowerMockito.mockStatic(TimingServiceFactory.class);

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(45.2);
        lastKnownAddress.setLongitude(-12.2);
        lastKnownAddress.setLocality("Colombes");
        lastKnownAddress.setCountryName("France");

        PowerMockito.when(PreferencesHelper.getCalculationMethod(mockContext)).thenReturn(CalculationMethodEnum.getDefault());
        PowerMockito.when(LocationHelper.getLocation(mockContext)).thenReturn(Single.just(newLocation));
        PowerMockito.when(AddressHelper.getAddressFromLocation(newLocation, mockContext)).thenReturn(Single.just(lastKnownAddress));
        PowerMockito.when(TimingServiceFactory.create(any())).thenReturn(aladhanTimingsService);

        Mockito.when(aladhanTimingsService.getTimingsByCity(any(), any(), any())).thenReturn(Single.error(new Exception()));

        PowerMockito.spy(PrayerAlarmScheduler.class);
        PowerMockito
                .doNothing()
                .when(PrayerAlarmScheduler.class, "scheduleNextPrayerAlarms", any(), any());

        PrayerUpdater prayerUpdater = TestListenableWorkerBuilder.from(mockContext, PrayerUpdater.class).build();

        ListenableWorker.Result result = prayerUpdater.startWork().get();

        Assert.assertEquals(ListenableWorker.Result.retry(), result);
    }

    @Test
    public void testPrayerUpdaterWork_when_notifier_throw_error() throws Exception {
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.mockStatic(AddressHelper.class);
        PowerMockito.mockStatic(AladhanTimingsService.class);
        PowerMockito.mockStatic(PreferencesHelper.class);
        PowerMockito.mockStatic(TimingServiceFactory.class);

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(0.12);
        newLocation.setLongitude(3.14);

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(45.2);
        lastKnownAddress.setLongitude(-12.2);
        lastKnownAddress.setLocality("Colombes");
        lastKnownAddress.setCountryName("France");

        PowerMockito.when(PreferencesHelper.getCalculationMethod(mockContext)).thenReturn(CalculationMethodEnum.getDefault());
        PowerMockito.when(LocationHelper.getLocation(mockContext)).thenReturn(Single.just(newLocation));
        PowerMockito.when(AddressHelper.getAddressFromLocation(newLocation, mockContext)).thenReturn(Single.just(lastKnownAddress));
        PowerMockito.when(TimingServiceFactory.create(any())).thenReturn(aladhanTimingsService);

        Mockito.when(aladhanTimingsService.getTimingsByCity(any(), any(), any())).thenReturn(Single.just(new DayPrayer()));

        PowerMockito.spy(PrayerAlarmScheduler.class);
        PowerMockito
                .doCallRealMethod()
                .when(PrayerAlarmScheduler.class, "scheduleNextPrayerAlarms", any(), any());

        PrayerUpdater prayerUpdater = TestListenableWorkerBuilder.from(mockContext, PrayerUpdater.class).build();

        ListenableWorker.Result result = prayerUpdater.startWork().get();

        Assert.assertEquals(ListenableWorker.Result.retry(), result);
    }
}