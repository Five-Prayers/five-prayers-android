package com.hbouzidi.fiveprayers.database;

import android.provider.BaseColumns;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
class QuranBookmarkModel implements BaseColumns {

    static final String TABLE_NAME = "quran_bookmark";

    static final String COLUMN_NAME_DATE_TIMESTAMP = "date_timestamp";
    static final String COLUMN_NAME_PAGE_NUMBER = "page_number";
    static final String COLUMN_NAME_SURAH_NUMBER = "surah_number";
    static final String COLUMN_NAME_JUZ_NUMBER = "juz_number";
    static final String COLUMN_NAME_RUB_HIZB_NUMBER = "rub_hizb_number";
    static final String COLUMN_NAME_BOOKMARK_TYPE = "bookmark_type";

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_DATE_TIMESTAMP + " INTEGER," +
                    COLUMN_NAME_PAGE_NUMBER + " INTEGER," +
                    COLUMN_NAME_SURAH_NUMBER + " INTEGER," +
                    COLUMN_NAME_JUZ_NUMBER + " INTEGER," +
                    COLUMN_NAME_RUB_HIZB_NUMBER + " INTEGER," +
                    COLUMN_NAME_BOOKMARK_TYPE + " TEXT," +

                    " UNIQUE(" +
                    COLUMN_NAME_PAGE_NUMBER + "," +
                    COLUMN_NAME_BOOKMARK_TYPE + ")" +
                    ")";

    static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
