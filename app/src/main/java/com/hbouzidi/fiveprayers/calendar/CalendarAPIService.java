package com.hbouzidi.fiveprayers.calendar;

import com.hbouzidi.fiveprayers.common.api.BaseAPIService;

import java.io.IOException;

import retrofit2.Call;

public class CalendarAPIService extends BaseAPIService {

    private static CalendarAPIService calendarAPIService;

    private CalendarAPIService() {
        BASE_URL = "https://api.aladhan.com/v1/";
    }

    public static CalendarAPIService getInstance() {
        if (calendarAPIService == null) {
            calendarAPIService = new CalendarAPIService();
        }
        return calendarAPIService;
    }


    public CalendarAPIResponse getHijriCalendar(int month,
                                                int year,
                                                int adjustment) throws IOException {


        CalendarAPIResource calendarAPIResource = provideRetrofit().create(CalendarAPIResource.class);

        Call<CalendarAPIResponse> call
                = calendarAPIResource
                .getHijriCalendar(month, year, adjustment);

        return call.execute().body();
    }
}
