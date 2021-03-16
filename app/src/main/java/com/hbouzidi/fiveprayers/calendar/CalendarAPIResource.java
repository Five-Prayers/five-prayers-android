package com.hbouzidi.fiveprayers.calendar;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public interface CalendarAPIResource {

    @GET("gToHCalendar/{month}/{year}")
    Call<CalendarAPIResponse> getHijriCalendar(
            @Path("month") int month,
            @Path("year") int year,
            @Query("adjustment") int adjustment
    );
}
