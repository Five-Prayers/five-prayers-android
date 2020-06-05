package com.bouzidi.prayertimes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bouzidi.prayertimes.timings.CalculationMethodEnum;
import com.bouzidi.prayertimes.timings.DayPrayer;
import com.bouzidi.prayertimes.timings.PrayerEnum;
import com.bouzidi.prayertimes.timings.aladhan.AladhanDate;
import com.bouzidi.prayertimes.timings.aladhan.AladhanTimings;
import com.bouzidi.prayertimes.timings.aladhan.AladhanTodayTimingsResponse;
import com.bouzidi.prayertimes.utils.TimingUtils;

import java.util.LinkedHashMap;
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

    public long savePrayerTiming(String dateString,
                                 String city,
                                 String country,
                                 CalculationMethodEnum calculationMethod,
                                 AladhanTodayTimingsResponse aladhanTodayTimingsResponse) {

        Log.i(PrayerRegistry.class.getName(), "Inserting new Timings rows");

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        AladhanDate aladhanDate = aladhanTodayTimingsResponse.getData().getDate();
        AladhanTimings aladhanTimings = aladhanTodayTimingsResponse.getData().getTimings();

        ContentValues values = new ContentValues();
        values.put(PrayerModel.COLUMN_NAME_DATE, dateString);

        values.put(PrayerModel.COLUMN_NAME_CITY, city);
        values.put(PrayerModel.COLUMN_NAME_COUNTRY, country);
        values.put(PrayerModel.COLUMN_NAME_CALCULATION_METHOD, calculationMethod.getValue());

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

        return db.insertWithOnConflict(PrayerModel.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public DayPrayer getPrayerTimings(String dateString, String city, CalculationMethodEnum calculationMethodEnum) {
        Log.i(PrayerRegistry.class.getName(), "Getting Timings rows");

        DayPrayer dayPrayer = null;

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String selection = PrayerModel.COLUMN_NAME_DATE + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_CITY + " = ?" +
                " AND " + PrayerModel.COLUMN_NAME_CALCULATION_METHOD + " = ?";
        String[] selectionArgs = {dateString, city, String.valueOf(calculationMethodEnum.getValue())};

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

        if (first) {
            Map<PrayerEnum, String> timings = new LinkedHashMap<>(5);

            String fajrTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_FAJR_TIMING));
            String dohrTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_DHOHR_TIMING));
            String asrTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_ASR_TIMING));
            String maghribTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_MAGHRIB_TIMING));
            String ichaTiming = cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_ICHA_TIMING));

            timings.put(PrayerEnum.FAJR, fajrTiming);
            timings.put(PrayerEnum.DHOHR, dohrTiming);
            timings.put(PrayerEnum.ASR, asrTiming);
            timings.put(PrayerEnum.MAGHRIB, maghribTiming);
            timings.put(PrayerEnum.ICHA, ichaTiming);

            dayPrayer = new DayPrayer(
                    cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_DATE)),
                    cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_CITY)),
                    cursor.getString(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_COUNTRY)),
                    cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_HIJRI_DAY)),
                    cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_HIJRI_MONTH_NUMBER)),
                    cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_HIJRI_YEAR)),
                    cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_GREGORIAN_DAY)),
                    cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_GREGORIAN_MONTH_NUMBER)),
                    cursor.getInt(cursor.getColumnIndex(PrayerModel.COLUMN_NAME_GREGORIAN_YEAR))
            );

            dayPrayer.setTimings(timings);
            dayPrayer.setMaghribAfterMidnight(TimingUtils.isBeforeOnSameDay(maghribTiming, dohrTiming));
            dayPrayer.setIchaAfterMidnight(TimingUtils.isBeforeOnSameDay(ichaTiming, dohrTiming));
        }

        return dayPrayer;
    }
}
