package com.hbouzidi.fiveprayers.job;

import android.app.AlarmManager;
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
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowLegacySystemClock;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
@Config(minSdk = 18, maxSdk = 28, application = FakeFivePrayerApplication.class, shadows = {ShadowGeocoder.class, ShadowAladhanAPIService.class})
public class PrayerUpdaterTest {

    Context applicationContext;

    @Before
    public void before() {
        applicationContext = ApplicationProvider.getApplicationContext();
        RESTMockServerStarter.startSync(new JVMFileParser(), new AndroidLogger());
    }

    @After
    public void tearDown() throws IOException {
        ShadowGeocoder.setIsPresent(true);
        RESTMockServer.shutdown();
    }

    @Test
    public void testPrayerUpdaterWork() throws Exception {
        AlarmManager alarmMgr = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
        ShadowAlarmManager shadowAlarmManager = Shadows.shadowOf(alarmMgr);

        RESTMockServer
                .whenGET(pathContains("/timings"))
                .thenReturnFile(200, "responses/adhan_api_response.json");

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        PreferencesHelper.updateAddressPreferences(applicationContext, lastKnownAddress);

        WorkerProviderFactory workerProviderFactory = ((FakeFivePrayerApplication) applicationContext).appComponent.workerProviderFactory();

        PrayerUpdater prayerUpdater = TestListenableWorkerBuilder
                .from(applicationContext, PrayerUpdater.class)
                .setWorkerFactory(workerProviderFactory)
                .build();

        ListenableWorker.Result result = prayerUpdater.startWork().get();

        Assert.assertEquals(ListenableWorker.Result.success(), result);
    }


    @Test
    public void testPrayerUpdaterWork_when_single_throw_error() throws Exception {
        RESTMockServer
                .whenGET(pathContains("/timings"))
                .thenReturnFile(500, "responses/adhan_api_response.json");

        Address lastKnownAddress = new Address(Locale.getDefault());
        lastKnownAddress.setLatitude(51.5073509);
        lastKnownAddress.setLongitude(-0.1277583);
        lastKnownAddress.setLocality("London");
        lastKnownAddress.setCountryName("United Kindom");
        lastKnownAddress.setCountryCode("UK");

        PreferencesHelper.updateAddressPreferences(applicationContext, lastKnownAddress);

        WorkerProviderFactory workerProviderFactory = ((FakeFivePrayerApplication) applicationContext).appComponent.workerProviderFactory();

        PrayerUpdater prayerUpdater = TestListenableWorkerBuilder
                .from(applicationContext, PrayerUpdater.class)
                .setWorkerFactory(workerProviderFactory)
                .build();

        ListenableWorker.Result result = prayerUpdater.startWork().get();

        Assert.assertEquals(ListenableWorker.Result.retry(), result);
    }
}