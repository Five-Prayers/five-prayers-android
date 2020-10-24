package com.hbouzidi.fiveprayers.timings.aladhan;

import com.hbouzidi.fiveprayers.common.api.BaseAPIService;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;

public class AladhanAPIService extends BaseAPIService {

    private static AladhanAPIService aladhanAPIService;

    private AladhanAPIService() {
        okHttpClient = new OkHttpClient.Builder().build();

        BASE_URL = "https://api.aladhan.com/v1/";
    }

    public static AladhanAPIService getInstance() {
        if (aladhanAPIService == null) {
            aladhanAPIService = new AladhanAPIService();
        }
        return aladhanAPIService;
    }

    public AladhanTodayTimingsResponse getTimingsByLatLong(final String LocalDateString,
                                                           final double latitude,
                                                           final double longitude,
                                                           final CalculationMethodEnum method,
                                                           final LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                                                           SchoolAdjustmentMethod schoolAdjustmentMethod,
                                                           MidnightModeAdjustmentMethod midnightModeAdjustmentMethod, final int adjustment,
                                                           final String tune
    ) throws IOException {

        AladhanAPIResource aladhanAPIResource = provideRetrofit().create(AladhanAPIResource.class);

        Call<AladhanTodayTimingsResponse> call
                = aladhanAPIResource
                .getTimingsByLatLong(LocalDateString, latitude, longitude, method.getMethodId(),
                        getMethodSettings(method),
                        latitudeAdjustmentMethod.getValue(),
                        schoolAdjustmentMethod.getValue(),
                        midnightModeAdjustmentMethod.getValue(),
                        adjustment, tune);

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
                        tune);

        return call.execute().body();
    }

    @NotNull
    private String getMethodSettings(CalculationMethodEnum method) {
        return method.getFajrAngle() + "," + method.getMaghribAngle() + "," + method.getIchaAngle();
    }
}
