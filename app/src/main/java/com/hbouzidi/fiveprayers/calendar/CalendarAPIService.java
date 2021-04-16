package com.hbouzidi.fiveprayers.calendar;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class CalendarAPIService {

    private final Retrofit retrofit;

    @Inject
    public CalendarAPIService(@Named("adhan_api") Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public CalendarAPIResponse getHijriCalendar(int month,
                                                int year,
                                                int adjustment) throws IOException {


        CalendarAPIResource calendarAPIResource = retrofit.create(CalendarAPIResource.class);

        Call<CalendarAPIResponse> call = calendarAPIResource.getHijriCalendar(month, year, adjustment);

        return call.execute().body();
    }
}
