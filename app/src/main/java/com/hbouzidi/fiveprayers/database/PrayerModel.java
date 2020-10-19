package com.hbouzidi.fiveprayers.database;

import android.provider.BaseColumns;

class PrayerModel implements BaseColumns {

    static final String TABLE_NAME = "prayer_timing";

    static final String COLUMN_NAME_DATE = "date_time";
    static final String COLUMN_NAME_DATE_TIMESTAMP = "date_timestamp";
    static final String COLUMN_NAME_TIMEZONE = "timezone";
    static final String COLUMN_NAME_CITY = "city";
    static final String COLUMN_NAME_COUNTRY = "country";

    static final String COLUMN_NAME_GREGORIAN_DAY = "gregorian_day";
    static final String COLUMN_NAME_GREGORIAN_MONTH_NUMBER = "gregorian_month_number";
    static final String COLUMN_NAME_GREGORIAN_YEAR = "gregorian_year";

    static final String COLUMN_NAME_HIJRI_DAY = "hijri_day";
    static final String COLUMN_NAME_HIJRI_MONTH_NUMBER = "hijri_month_number";
    static final String COLUMN_NAME_HIJRI_YEAR = "hijri_year";

    static final String COLUMN_NAME_FAJR_TIMING = "fajr_timing";
    static final String COLUMN_NAME_DHOHR_TIMING = "dhohr_timing";
    static final String COLUMN_NAME_ASR_TIMING = "asr_timing";
    static final String COLUMN_NAME_MAGHRIB_TIMING = "maghrib_timing";
    static final String COLUMN_NAME_ICHA_TIMING = "icha_timing";

    static final String COLUMN_NAME_SUNRISE_TIMING = "sunrise_timing";
    static final String COLUMN_NAME_SUNSET_TIMING = "sunset_timing";
    static final String COLUMN_NAME_MIDNIGHT_TIMING = "midnight_timing";
    static final String COLUMN_NAME_IMSAK_TIMING = "imsak_timing";

    static final String COLUMN_NAME_LATITUDE = "latitude";
    static final String COLUMN_NAME_LONGITUDE = "longitude";

    static final String COLUMN_NAME_CALCULATION_METHOD = "calculation_method";
    static final String COLUMN_NAME_TIMINGS_TUNE = "timings_tune";
    static final String COLUMN_NAME_LATITUDE_ADJUSTMENT_METHOD = "latitude_adjustment_method";
    static final String COLUMN_NAME_SCHOOL_ADJUSTMENT__METHOD = "school_adjustment_method";
    static final String COLUMN_NAME_MIDNIGHT_MODE_ADJUSTMENT_METHOD = "midnight_mode_adjustment_method";
    static final String COLUMN_NAME_HIJRI_ADJUSTMENT = "hijri_adjustment";

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_DATE + " TEXT," +
                    COLUMN_NAME_DATE_TIMESTAMP + " INTEGER," +
                    COLUMN_NAME_TIMEZONE + " TEXT," +
                    COLUMN_NAME_CITY + " TEXT," +
                    COLUMN_NAME_COUNTRY + " TEXT," +
                    COLUMN_NAME_LATITUDE + " REAL," +
                    COLUMN_NAME_LONGITUDE + " REAL," +
                    COLUMN_NAME_CALCULATION_METHOD + " TEXT," +
                    COLUMN_NAME_GREGORIAN_DAY + " INTEGER," +
                    COLUMN_NAME_GREGORIAN_MONTH_NUMBER + " INTEGER," +
                    COLUMN_NAME_GREGORIAN_YEAR + " INTEGER," +
                    COLUMN_NAME_HIJRI_DAY + " INTEGER," +
                    COLUMN_NAME_HIJRI_MONTH_NUMBER + " INTEGER," +
                    COLUMN_NAME_HIJRI_YEAR + " TEXT," +
                    COLUMN_NAME_FAJR_TIMING + " TEXT," +
                    COLUMN_NAME_DHOHR_TIMING + " TEXT," +
                    COLUMN_NAME_ASR_TIMING + " TEXT," +
                    COLUMN_NAME_MAGHRIB_TIMING + " TEXT," +
                    COLUMN_NAME_ICHA_TIMING + " TEXT," +
                    COLUMN_NAME_SUNRISE_TIMING + " TEXT," +
                    COLUMN_NAME_SUNSET_TIMING + " TEXT," +
                    COLUMN_NAME_MIDNIGHT_TIMING + " TEXT," +
                    COLUMN_NAME_IMSAK_TIMING + " TEXT," +
                    COLUMN_NAME_TIMINGS_TUNE + " TEXT," +
                    COLUMN_NAME_LATITUDE_ADJUSTMENT_METHOD + " TEXT," +
                    COLUMN_NAME_SCHOOL_ADJUSTMENT__METHOD + " TEXT," +
                    COLUMN_NAME_MIDNIGHT_MODE_ADJUSTMENT_METHOD + " TEXT," +
                    COLUMN_NAME_HIJRI_ADJUSTMENT + " INTEGER," +
                    " UNIQUE(" +
                    COLUMN_NAME_DATE + "," +
                    COLUMN_NAME_CITY + "," +
                    COLUMN_NAME_COUNTRY + "," +
                    COLUMN_NAME_TIMINGS_TUNE + "," +
                    COLUMN_NAME_LATITUDE_ADJUSTMENT_METHOD + "," +
                    COLUMN_NAME_SCHOOL_ADJUSTMENT__METHOD + "," +
                    COLUMN_NAME_MIDNIGHT_MODE_ADJUSTMENT_METHOD + "," +
                    COLUMN_NAME_HIJRI_ADJUSTMENT + "," +
                    COLUMN_NAME_CALCULATION_METHOD + ")" +
                    ")";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
