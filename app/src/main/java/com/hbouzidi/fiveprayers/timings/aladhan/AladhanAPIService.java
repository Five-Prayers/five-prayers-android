package com.hbouzidi.fiveprayers.timings.aladhan;

import android.os.Build;

import androidx.annotation.NonNull;

import com.hbouzidi.fiveprayers.common.api.BaseAPIService;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import retrofit2.Call;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class AladhanAPIService extends BaseAPIService {

    private static AladhanAPIService aladhanAPIService;

    private AladhanAPIService() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            BASE_URL = "http://api.aladhan.com/v1/";
        } else {
            BASE_URL = "https://api.aladhan.com/v1/";
        }
    }

    public static AladhanAPIService getInstance() {
        if (aladhanAPIService == null) {
            aladhanAPIService = new AladhanAPIService();
        }
        return aladhanAPIService;
    }

    public AladhanTodayTimingsResponse getTimingsByLatLong(final double latitude,
                                                           final double longitude,
                                                           final CalculationMethodEnum method,
                                                           final LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                                                           SchoolAdjustmentMethod schoolAdjustmentMethod,
                                                           MidnightModeAdjustmentMethod midnightModeAdjustmentMethod, final int adjustment,
                                                           final String tune
    ) throws IOException {

        long epochSecond = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        AladhanAPIResource aladhanAPIResource = provideRetrofit().create(AladhanAPIResource.class);

        Call<AladhanTodayTimingsResponse> call
                = aladhanAPIResource
                .getTimingsByLatLong(String.valueOf(epochSecond), latitude, longitude, method.getMethodId(),
                        getMethodSettings(method),
                        latitudeAdjustmentMethod.getValue(),
                        schoolAdjustmentMethod.getValue(),
                        midnightModeAdjustmentMethod.getValue(),
                        adjustment, tune, ZoneId.systemDefault().getId());

        return call.execute().body();
    }

    public AladhanCalendarResponse getCalendarByLatLong(
            final double latitude,
            final double longitude,
            final int month, final int year,
            final CalculationMethodEnum method,
            final LatitudeAdjustmentMethod latitudeAdjustmentMethod,
            SchoolAdjustmentMethod schoolAdjustmentMethod,
            MidnightModeAdjustmentMethod midnightModeAdjustmentMethod,
            final int adjustment,
            final String tune) throws IOException {

        AladhanAPIResource aladhanAPIResource = provideRetrofit().create(AladhanAPIResource.class);

        Call<AladhanCalendarResponse> call
                = aladhanAPIResource
                .getCalendarByLatLong(latitude, longitude, month, year, false, method.getMethodId(),
                        getMethodSettings(method),
                        latitudeAdjustmentMethod.getValue(),
                        schoolAdjustmentMethod.getValue(),
                        midnightModeAdjustmentMethod.getValue(),
                        adjustment,
                        tune,
                        ZoneId.systemDefault().getId()
                );

        return call.execute().body();
    }

    @NonNull
    private String getMethodSettings(CalculationMethodEnum method) {
        return method.getFajrAngle() + "," + method.getMaghribAngle() + "," + method.getIchaAngle();
    }
}
