package com.bouzidi.prayertimes.database;

import android.provider.BaseColumns;

class PrayerModel implements BaseColumns {

    static final String TABLE_NAME = "prayer_timing";

    static final String COLUMN_NAME_DATE = "date_time";
    static final String COLUMN_NAME_CITY = "city";
    static final String COLUMN_NAME_COUNTRY = "country";
    static final String COLUMN_NAME_CALCULATION_METHOD = "calculation_method";

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

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_DATE + " TEXT," +
                    COLUMN_NAME_CITY + " TEXT," +
                    COLUMN_NAME_COUNTRY + " TEXT," +
                    COLUMN_NAME_CALCULATION_METHOD + " INTEGER," +
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
                    " UNIQUE(" + COLUMN_NAME_DATE + "," + COLUMN_NAME_CITY + "," + COLUMN_NAME_CALCULATION_METHOD + ")" +
                    ")";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
