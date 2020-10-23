package com.hbouzidi.fiveprayers.timings;

import android.content.Context;
import android.location.Address;

import java.time.LocalDate;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface TimingsService {

    Single<DayPrayer> getTimingsByCity(final LocalDate localDate,
                                       final Address address,
                                       final Context context);

    Single<List<DayPrayer>> getCalendarByCity(
            final Address address,
            int month, int year,
            final Context context);
}
