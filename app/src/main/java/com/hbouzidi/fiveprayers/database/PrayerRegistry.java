package com.hbouzidi.fiveprayers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDate;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanCalendarResponse;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanData;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanTimings;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;
import com.hbouzidi.fiveprayers.utils.TimingUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrayerRegistry {

    private static PrayerRegistry prayerRegistry;
    private DatabaseHelper databaseHelper;

    private PrayerRegistry(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public static PrayerRegistry getInstance(Context context) {
        if (prayerRegistry == null) {
            prayerRegistry = new PrayerRegistry(context);
        }
        return prayerRegistry;
    }

    public long savePrayerTiming(String LocalDateString,
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

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        AladhanDate aladhanDate = data.getDate();
        AladhanTimings aladhanTimings = data.getTimings();

        ContentValues values = new ContentValues();
        values.put(PrayerModel.COLUMN_NAME_DATE, LocalDateString);
        values.put(PrayerModel.COLUMN_NAME_DATE_TIMESTAMP, aladhanDate.getTimestamp());
        values.put(PrayerModel.COLUMN_NAME_TIMEZONE, data.getMeta().getTimezone());

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

        values.put(PrayerModel.COLUMN_NAME_LATITUDE, data.getMeta().getLatitude());
        values.put(PrayerModel.COLUMN_NAME_LONGITUDE, data.getMeta().getLongitude());


        return db.insertWithOnConflict(PrayerModel.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public DayPrayer getPrayerTimings(String dateString, String city, String country,
                                      CalculationMethodEnum calculationMethodEnum,
                                      LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                                      SchoolAdjustmentMethod schoolAdjustmentMethod,
                                      MidnightModeAdjustmentMethod midnightModeAdjustmentMethod,
                                      int hijriAdjustment,
                                      String tune) {

        Log.i(PrayerRegistry.class.getName(), "Getting Timings rows");

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String selection = PrayerModel.COLUMN_NAME_DATE + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_CITY + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_COUNTRY + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_CALCULATION_METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_LATITUDE_ADJUSTMENT_METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_SCHOOL_ADJUSTMENT__METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_MIDNIGHT_MODE_ADJUSTMENT_METHOD + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_HIJRI_ADJUSTMENT + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_TIMINGS_TUNE + " = ?";

        String[] selectionArgs = {
                dateString,
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
                             AladhanCalendarResponse aladhanCalendarResponse
    ) {

        for (AladhanData aladhanData : aladhanCalendarResponse.getData()) {
            savePrayerTiming(aladhanData.getDate().getGregorian().getDate(),
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
        DayPrayer dayPrayer = null;

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

        timings.put(PrayerEnum.FAJR, TimingUtils.transformTimingToDate(fajrTiming, dateStr, false));
        timings.put(PrayerEnum.DHOHR, TimingUtils.transformTimingToDate(dohrTiming, dateStr, false));
        timings.put(PrayerEnum.ASR, TimingUtils.transformTimingToDate(asrTiming, dateStr, false));
        timings.put(PrayerEnum.MAGHRIB, TimingUtils.transformTimingToDate(maghribTiming, dateStr, TimingUtils.isBeforeOnSameDay(maghribTiming, dohrTiming)));
        timings.put(PrayerEnum.ICHA, TimingUtils.transformTimingToDate(ichaTiming, dateStr, TimingUtils.isBeforeOnSameDay(ichaTiming, dohrTiming)));

        complementaryTiming.put(ComplementaryTimingEnum.SUNRISE, TimingUtils.transformTimingToDate(sunriseTiming, dateStr, false));
        complementaryTiming.put(ComplementaryTimingEnum.SUNSET, TimingUtils.transformTimingToDate(sunsetTiming, dateStr, TimingUtils.isBeforeOnSameDay(ichaTiming, dohrTiming)));
        complementaryTiming.put(ComplementaryTimingEnum.MIDNIGHT, TimingUtils.transformTimingToDate(midnightTiming, dateStr, TimingUtils.isBeforeOnSameDay(midnightTiming, dohrTiming)));
        complementaryTiming.put(ComplementaryTimingEnum.IMSAK, TimingUtils.transformTimingToDate(imsakTiming, dateStr, false));

        dayPrayer.setTimings(timings);
        dayPrayer.setComplementaryTiming(complementaryTiming);
        dayPrayer.setCalculationMethodEnum(CalculationMethodEnum.valueOf(calculationMethod));
        dayPrayer.setTimezone(cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_TIMEZONE)));
        dayPrayer.setLatitude(cursor.getDouble(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_LATITUDE)));
        dayPrayer.setLongitude(cursor.getDouble(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_LONGITUDE)));

        return dayPrayer;
    }
}
