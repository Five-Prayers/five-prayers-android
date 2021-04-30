package com.hbouzidi.fiveprayers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanData;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDate;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanMeta;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTimings;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;
import com.hbouzidi.fiveprayers.utils.TimingUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class PrayerRegistry {

    private final DatabaseHelper databaseHelper;

    @Inject
    public PrayerRegistry(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public long savePrayerTiming(LocalDate localDate,
                                 String city,
                                 String country,
                                 CalculationMethodEnum calculationMethod,
                                 LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                                 SchoolAdjustmentMethod schoolAdjustmentMethod,
                                 MidnightModeAdjustmentMethod midnightModeAdjustmentMethod,
                                 int hijriAdjustment,
                                 String tune,
                                 AladhanData data) {

        Log.i(PrayerRegistry.class.getName(), "Inserting new Timings rows");

        String localDateString = TimingUtils.formatDateForAdhanAPI(localDate);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        AladhanDate aladhanDate = data.getDate();
        AladhanMeta meta = data.getMeta();
        AladhanTimings aladhanTimings = data.getTimings();

        ContentValues values = new ContentValues();
        values.put(PrayerModel.COLUMN_NAME_DATE, localDateString);
        values.put(PrayerModel.COLUMN_NAME_DATE_TIMESTAMP, aladhanDate.getTimestamp());
        values.put(PrayerModel.COLUMN_NAME_TIMEZONE, meta.getTimezone());

        values.put(PrayerModel.COLUMN_NAME_CITY, city);
        values.put(PrayerModel.COLUMN_NAME_COUNTRY, country);
        values.put(PrayerModel.COLUMN_NAME_CALCULATION_METHOD, String.valueOf(calculationMethod));

        values.put(PrayerModel.COLUMN_NAME_GREGORIAN_DAY, aladhanDate.getGregorian().getDay());
        values.put(PrayerModel.COLUMN_NAME_GREGORIAN_MONTH_NUMBER, aladhanDate.getGregorian().getMonth().getNumber());
        values.put(PrayerModel.COLUMN_NAME_GREGORIAN_YEAR, aladhanDate.getGregorian().getYear());

        values.put(PrayerModel.COLUMN_NAME_HIJRI_DAY, aladhanDate.getHijri().getDay());
        values.put(PrayerModel.COLUMN_NAME_HIJRI_MONTH_NUMBER, aladhanDate.getHijri().getMonth().getNumber());
        values.put(PrayerModel.COLUMN_NAME_HIJRI_YEAR, aladhanDate.getHijri().getYear());

        values.put(PrayerModel.COLUMN_NAME_FAJR_TIMING, aladhanTimings.getFajr());
        values.put(PrayerModel.COLUMN_NAME_DHOHR_TIMING, aladhanTimings.getDhuhr());
        values.put(PrayerModel.COLUMN_NAME_ASR_TIMING, aladhanTimings.getAsr());
        values.put(PrayerModel.COLUMN_NAME_MAGHRIB_TIMING, aladhanTimings.getMaghrib());
        values.put(PrayerModel.COLUMN_NAME_ICHA_TIMING, aladhanTimings.getIsha());

        values.put(PrayerModel.COLUMN_NAME_SUNRISE_TIMING, aladhanTimings.getSunrise());
        values.put(PrayerModel.COLUMN_NAME_SUNSET_TIMING, aladhanTimings.getSunset());
        values.put(PrayerModel.COLUMN_NAME_MIDNIGHT_TIMING, aladhanTimings.getMidnight());
        values.put(PrayerModel.COLUMN_NAME_IMSAK_TIMING, aladhanTimings.getImsak());

        values.put(PrayerModel.COLUMN_NAME_TIMINGS_TUNE, tune);
        values.put(PrayerModel.COLUMN_NAME_LATITUDE_ADJUSTMENT_METHOD, String.valueOf(latitudeAdjustmentMethod));
        values.put(PrayerModel.COLUMN_NAME_SCHOOL_ADJUSTMENT__METHOD, String.valueOf(schoolAdjustmentMethod));
        values.put(PrayerModel.COLUMN_NAME_MIDNIGHT_MODE_ADJUSTMENT_METHOD, String.valueOf(midnightModeAdjustmentMethod));
        values.put(PrayerModel.COLUMN_NAME_HIJRI_ADJUSTMENT, hijriAdjustment);

        values.put(PrayerModel.COLUMN_NAME_LATITUDE, meta.getLatitude());
        values.put(PrayerModel.COLUMN_NAME_LONGITUDE, meta.getLongitude());

        return db.insertWithOnConflict(PrayerModel.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public DayPrayer getPrayerTimings(LocalDate localDate, String city, String country,
                                      CalculationMethodEnum calculationMethodEnum,
                                      LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                                      SchoolAdjustmentMethod schoolAdjustmentMethod,
                                      MidnightModeAdjustmentMethod midnightModeAdjustmentMethod,
                                      int hijriAdjustment,
                                      String tune) {

        Log.i(PrayerRegistry.class.getName(), "Getting Timings rows");

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String selection = PrayerModel.COLUMN_NAME_DATE + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_TIMEZONE + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_CITY + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_COUNTRY + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_CALCULATION_METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_LATITUDE_ADJUSTMENT_METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_SCHOOL_ADJUSTMENT__METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_MIDNIGHT_MODE_ADJUSTMENT_METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_HIJRI_ADJUSTMENT + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_TIMINGS_TUNE + " = ?";

        String[] selectionArgs = {
                TimingUtils.formatDateForAdhanAPI(localDate),
                TimingUtils.getDefaultTimeZone(),
                city,
                country,
                String.valueOf(calculationMethodEnum),
                String.valueOf(latitudeAdjustmentMethod),
                String.valueOf(schoolAdjustmentMethod),
                String.valueOf(midnightModeAdjustmentMethod),
                String.valueOf(hijriAdjustment),
                tune
        };

        String sortOrder =
                PrayerModel._ID + " DESC";

        Cursor cursor = db.query(
                PrayerModel.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        boolean first = cursor.moveToFirst();

        DayPrayer dayPrayer = null;

        if (first) {
            dayPrayer = createDayPrayer(cursor);
        }

        return dayPrayer;
    }

    public void saveCalendar(String city,
                             String country,
                             CalculationMethodEnum calculationMethod,
                             LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                             SchoolAdjustmentMethod schoolAdjustmentMethod,
                             MidnightModeAdjustmentMethod midnightModeAdjustmentMethod,
                             int hijriAdjustment,
                             String tune,
                             List<AladhanData> data
    ) {

        for (AladhanData aladhanData : data) {
            String dateString = aladhanData.getDate().getGregorian().getDate();

            savePrayerTiming(TimingUtils.parseAdhanAPIDate(dateString),
                    city, country, calculationMethod, latitudeAdjustmentMethod,
                    schoolAdjustmentMethod, midnightModeAdjustmentMethod,
                    hijriAdjustment, tune, aladhanData);
        }
    }

    public List<DayPrayer> getPrayerCalendar(String city, String country, int monthNumber, int year,
                                             CalculationMethodEnum calculationMethodEnum,
                                             LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                                             SchoolAdjustmentMethod schoolAdjustmentMethod,
                                             MidnightModeAdjustmentMethod midnightModeAdjustmentMethod,
                                             int hijriAdjustment,
                                             String tune) {

        Log.i(PrayerRegistry.class.getName(), "Getting Calendar rows");

        List<DayPrayer> monthPrayer = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String selection = PrayerModel.COLUMN_NAME_GREGORIAN_MONTH_NUMBER + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_GREGORIAN_YEAR + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_TIMEZONE + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_CITY + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_COUNTRY + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_CALCULATION_METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_LATITUDE_ADJUSTMENT_METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_SCHOOL_ADJUSTMENT__METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_MIDNIGHT_MODE_ADJUSTMENT_METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_HIJRI_ADJUSTMENT + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_TIMINGS_TUNE + " = ?";

        String[] selectionArgs = {
                String.valueOf(monthNumber),
                String.valueOf(year),
                TimingUtils.getDefaultTimeZone(),
                city,
                country,
                String.valueOf(calculationMethodEnum),
                String.valueOf(latitudeAdjustmentMethod),
                String.valueOf(schoolAdjustmentMethod),
                String.valueOf(midnightModeAdjustmentMethod),
                String.valueOf(hijriAdjustment),
                tune
        };

        String sortOrder =
                PrayerModel.COLUMN_NAME_GREGORIAN_DAY + " ASC";

        Cursor cursor = db.query(
                PrayerModel.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        Log.e("timing", LocalDateTime.now().toString());
        if (cursor.moveToFirst()) {
            do {

                monthPrayer.add(createDayPrayer(cursor));
            } while (cursor.moveToNext());
        }
        Log.e("timing end", LocalDateTime.now().toString());

        return monthPrayer;
    }

    private DayPrayer createDayPrayer(Cursor cursor) {
        DayPrayer dayPrayer;

        Map<PrayerEnum, LocalDateTime> timings = new LinkedHashMap<>(5);
        Map<ComplementaryTimingEnum, LocalDateTime> complementaryTiming = new LinkedHashMap<>(4);

        String fajrTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_FAJR_TIMING));
        String dohrTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_DHOHR_TIMING));
        String asrTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_ASR_TIMING));
        String maghribTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_MAGHRIB_TIMING));
        String ichaTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_ICHA_TIMING));

        String sunriseTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_SUNRISE_TIMING));
        String sunsetTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_SUNSET_TIMING));
        String midnightTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_MIDNIGHT_TIMING));
        String imsakTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_IMSAK_TIMING));

        String dateStr = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_DATE));

        String calculationMethod = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_CALCULATION_METHOD));

        dayPrayer = new DayPrayer(
                dateStr,
                cursor.getLong(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_DATE_TIMESTAMP)),
                cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_CITY)),
                cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_COUNTRY)),
                cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_HIJRI_DAY)),
                cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_HIJRI_MONTH_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_HIJRI_YEAR)),
                cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_GREGORIAN_DAY)),
                cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_GREGORIAN_MONTH_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_GREGORIAN_YEAR))
        );

        LocalDateTime fajrTime = TimingUtils.transformTimingToDate(fajrTiming, dateStr, false);
        LocalDateTime maghribTime = TimingUtils.transformTimingToDate(maghribTiming, dateStr, TimingUtils.isBeforeOnSameDay(maghribTiming, dohrTiming));

        timings.put(PrayerEnum.FAJR, fajrTime);
        timings.put(PrayerEnum.DHOHR, TimingUtils.transformTimingToDate(dohrTiming, dateStr, false));
        timings.put(PrayerEnum.ASR, TimingUtils.transformTimingToDate(asrTiming, dateStr, false));
        timings.put(PrayerEnum.MAGHRIB, maghribTime);
        timings.put(PrayerEnum.ICHA, TimingUtils.transformTimingToDate(ichaTiming, dateStr, TimingUtils.isBeforeOnSameDay(ichaTiming, dohrTiming)));

        LocalDateTime sunriseTime = TimingUtils.transformTimingToDate(sunriseTiming, dateStr, false);

        complementaryTiming.put(ComplementaryTimingEnum.SUNRISE, sunriseTime);
        complementaryTiming.put(ComplementaryTimingEnum.SUNSET, TimingUtils.transformTimingToDate(sunsetTiming, dateStr, TimingUtils.isBeforeOnSameDay(ichaTiming, dohrTiming)));
        complementaryTiming.put(ComplementaryTimingEnum.MIDNIGHT, TimingUtils.transformTimingToDate(midnightTiming, dateStr, TimingUtils.isBeforeOnSameDay(midnightTiming, dohrTiming)));
        complementaryTiming.put(ComplementaryTimingEnum.IMSAK, TimingUtils.transformTimingToDate(imsakTiming, dateStr, false));
        complementaryTiming.put(ComplementaryTimingEnum.DOHA, sunriseTime.plusMinutes(TimingUtils.DOHA_INTERVAL));
        complementaryTiming.put(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT, getLastThirdOfTheNight(fajrTime, maghribTime));

        dayPrayer.setTimings(timings);
        dayPrayer.setComplementaryTiming(complementaryTiming);
        dayPrayer.setCalculationMethodEnum(CalculationMethodEnum.valueOf(calculationMethod));
        dayPrayer.setTimezone(cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_TIMEZONE)));
        dayPrayer.setLatitude(cursor.getDouble(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_LATITUDE)));
        dayPrayer.setLongitude(cursor.getDouble(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_LONGITUDE)));

        return dayPrayer;
    }

    private LocalDateTime getLastThirdOfTheNight(LocalDateTime fajrTime, LocalDateTime maghribTime) {
        final long nightDurationInSeconds =  maghribTime.until(fajrTime.plus(1, ChronoUnit.DAYS), ChronoUnit.SECONDS);

        return maghribTime.plus((long) (nightDurationInSeconds * (2.0 / 3.0)), ChronoUnit.SECONDS).withSecond(0).withNano(0);
    }
}
