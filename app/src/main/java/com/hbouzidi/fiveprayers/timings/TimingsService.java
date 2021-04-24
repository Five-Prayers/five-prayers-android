package com.hbouzidi.fiveprayers.timings;

import android.location.Address;

import java.time.LocalDate;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public interface TimingsService {

    Single<DayPrayer> getTimingsByCity(final LocalDate localDate,
                                       final Address address);

    Single<List<DayPrayer>> getCalendarByCity(
            final Address address,
            int month, int year);
}
