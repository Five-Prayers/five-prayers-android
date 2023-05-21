package com.hbouzidi.fiveprayers.database;

import android.provider.BaseColumns;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
class ReadingScheduleModel implements BaseColumns {

    static final String TABLE_NAME = "reading_schedule";

    static final String COLUMN_NAME_DAY_NUMBER = "day_number";
    static final String COLUMN_NAME_TOTAL_DAYS = "total_days";
    static final String COLUMN_NAME_START_PAGE = "start_page";
    static final String COLUMN_NAME_START_QUARTER = "start_quarter";
    static final String COLUMN_NAME_START_AYAH_NUMBER = "start_ayah_number";
    static final String COLUMN_NAME_START_SURAH_NUMBER = "start_surah_number";
    static final String COLUMN_NAME_END_PAGE = "end_page";
    static final String COLUMN_NAME_END_QUARTER = "end_quarter";
    static final String COLUMN_NAME_STATUS = "status";

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_DAY_NUMBER + " INTEGER," +
                    COLUMN_NAME_TOTAL_DAYS + " INTEGER," +
                    COLUMN_NAME_START_PAGE + " INTEGER," +
                    COLUMN_NAME_START_QUARTER + " INTEGER," +
                    COLUMN_NAME_END_PAGE + " INTEGER," +
                    COLUMN_NAME_END_QUARTER + " INTEGER," +
                    COLUMN_NAME_START_AYAH_NUMBER + " INTEGER," +
                    COLUMN_NAME_START_SURAH_NUMBER + " INTEGER," +
                    COLUMN_NAME_STATUS + " INTEGER" +
                    ")";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
