package com.hbouzidi.fiveprayers.job;

import android.content.Context;
import android.location.Address;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;

import com.hbouzidi.fiveprayers.FakeFivePrayerApplication;
import com.hbouzidi.fiveprayers.di.factory.worker.WorkerProviderFactory;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.shadows.ShadowAladhanAPIService;
import com.hbouzidi.fiveprayers.shadows.ShadowGeocoder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Locale;

import io.appflate.restmock.JVMFileParser;
import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RESTMockServerStarter;
import io.appflate.restmock.android.AndroidLogger;

import static io.appflate.restmock.utils.RequestMatchers.pathContains;

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
//
//    @Rule
//    public PowerMockRule rule = new PowerMockRule();
//
//    @Mock
//    Context mockContext;
//
//    @Mock
//    AladhanTimingsService aladhanTimingsService;
//
//    @Before
//    public void before() {
//        this.mockContext = Mockito.mock(Context.class);
//        this.aladhanTimingsService = Mockito.mock(AladhanTimingsService.class);
//    }
//
//    @Test
//    public void testPrayerUpdaterWork() throws Exception {
//        PowerMockito.mockStatic(LocationHelper.class);
//        PowerMockito.mockStatic(AddressHelper.class);
//        PowerMockito.mockStatic(PreferencesHelper.class);
//        PowerMockito.mockStatic(AladhanTimingsService.class);
//        PowerMockito.mockStatic(TimingServiceFactory.class);
//
//        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
//        newLocation.setLatitude(0.12);
//        newLocation.setLongitude(3.14);
//
//        Address lastKnownAddress = new Address(Locale.getDefault());
//        lastKnownAddress.setLatitude(45.2);
//        lastKnownAddress.setLongitude(-12.2);
//        lastKnownAddress.setLocality("Colombes");
//        lastKnownAddress.setCountryName("France");
//
//        PowerMockito.when(PreferencesHelper.getCalculationMethod(mockContext)).thenReturn(CalculationMethodEnum.getDefault());
//        PowerMockito.when(LocationHelper.getLocation(mockContext)).thenReturn(Single.just(newLocation));
//        PowerMockito.when(AddressHelper.getAddressFromLocation(newLocation, mockContext)).thenReturn(Single.just(lastKnownAddress));
//        PowerMockito.when(TimingServiceFactory.create(any())).thenReturn(aladhanTimingsService);
//
//        Mockito.when(aladhanTimingsService.getTimingsByCity(any(), any(), any())).thenReturn(Single.just(new DayPrayer()));
//
//        PowerMockito.spy(PrayerAlarmScheduler.class);
//        PowerMockito
//                .doNothing()
//                .when(PrayerAlarmScheduler.class, "scheduleNextPrayerAlarms", any(), any());
//
//        PrayerUpdater prayerUpdater = TestListenableWorkerBuilder.from(mockContext, PrayerUpdater.class).build();
//
//        ListenableWorker.Result result = prayerUpdater.startWork().get();
//
//        Assert.assertEquals(ListenableWorker.Result.success(), result);
//    }
//
//    @Test
//    public void testPrayerUpdaterWork_when_single_throw_error() throws Exception {
//        PowerMockito.mockStatic(LocationHelper.class);
//        PowerMockito.mockStatic(AddressHelper.class);
//        PowerMockito.mockStatic(AladhanTimingsService.class);
//        PowerMockito.mockStatic(PreferencesHelper.class);
//        PowerMockito.mockStatic(TimingServiceFactory.class);
//
//        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
//        newLocation.setLatitude(0.12);
//        newLocation.setLongitude(3.14);
//
//        Address lastKnownAddress = new Address(Locale.getDefault());
//        lastKnownAddress.setLatitude(45.2);
//        lastKnownAddress.setLongitude(-12.2);
//        lastKnownAddress.setLocality("Colombes");
//        lastKnownAddress.setCountryName("France");
//
//        PowerMockito.when(PreferencesHelper.getCalculationMethod(mockContext)).thenReturn(CalculationMethodEnum.getDefault());
//        PowerMockito.when(LocationHelper.getLocation(mockContext)).thenReturn(Single.just(newLocation));
//        PowerMockito.when(AddressHelper.getAddressFromLocation(newLocation, mockContext)).thenReturn(Single.just(lastKnownAddress));
//        PowerMockito.when(TimingServiceFactory.create(any())).thenReturn(aladhanTimingsService);
//
//        Mockito.when(aladhanTimingsService.getTimingsByCity(any(), any(), any())).thenReturn(Single.error(new Exception()));
//
//        PowerMockito.spy(PrayerAlarmScheduler.class);
//        PowerMockito
//                .doNothing()
//                .when(PrayerAlarmScheduler.class, "scheduleNextPrayerAlarms", any(), any());
//
//        PrayerUpdater prayerUpdater = TestListenableWorkerBuilder.from(mockContext, PrayerUpdater.class).build();
//
//        ListenableWorker.Result result = prayerUpdater.startWork().get();
//
//        Assert.assertEquals(ListenableWorker.Result.retry(), result);
//    }
//
//    @Test
//    public void testPrayerUpdaterWork_when_notifier_throw_error() throws Exception {
//        PowerMockito.mockStatic(LocationHelper.class);
//        PowerMockito.mockStatic(AddressHelper.class);
//        PowerMockito.mockStatic(AladhanTimingsService.class);
//        PowerMockito.mockStatic(PreferencesHelper.class);
//        PowerMockito.mockStatic(TimingServiceFactory.class);
//
//        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
//        newLocation.setLatitude(0.12);
//        newLocation.setLongitude(3.14);
//
//        Address lastKnownAddress = new Address(Locale.getDefault());
//        lastKnownAddress.setLatitude(45.2);
//        lastKnownAddress.setLongitude(-12.2);
//        lastKnownAddress.setLocality("Colombes");
//        lastKnownAddress.setCountryName("France");
//
//        PowerMockito.when(PreferencesHelper.getCalculationMethod(mockContext)).thenReturn(CalculationMethodEnum.getDefault());
//        PowerMockito.when(LocationHelper.getLocation(mockContext)).thenReturn(Single.just(newLocation));
//        PowerMockito.when(AddressHelper.getAddressFromLocation(newLocation, mockContext)).thenReturn(Single.just(lastKnownAddress));
//        PowerMockito.when(TimingServiceFactory.create(any())).thenReturn(aladhanTimingsService);
//
//        Mockito.when(aladhanTimingsService.getTimingsByCity(any(), any(), any())).thenReturn(Single.just(new DayPrayer()));
//
//        PowerMockito.spy(PrayerAlarmScheduler.class);
//        PowerMockito
//                .doCallRealMethod()
//                .when(PrayerAlarmScheduler.class, "scheduleNextPrayerAlarms", any(), any());
//
//        PrayerUpdater prayerUpdater = TestListenableWorkerBuilder.from(mockContext, PrayerUpdater.class).build();
//
//        ListenableWorker.Result result = prayerUpdater.startWork().get();
//
//        Assert.assertEquals(ListenableWorker.Result.retry(), result);
//    }
}