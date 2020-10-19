package com.hbouzidi.fiveprayers.quran.model;

import android.provider.BaseColumns;

public class AyahModel implements BaseColumns {

    public static final String TABLE_NAME = "ayah";

    public static final String COLUMN_NAME_SURAH_NUMBER = "surah_number";
    public static final String COLUMN_NAME_NUMBER = "number";
    public static final String COLUMN_NAME_TEXT = "text";
    public static final String COLUMN_NAME_NUMBER_IN_SURAH = "number_in_surah";
    public static final String COLUMN_NAME_JUZ = "juz";
    public static final String COLUMN_NAME_MANZIL = "manzil";
    public static final String COLUMN_NAME_PAGE = "page";
    public static final String COLUMN_NAME_RUKU = "ruku";
    public static final String COLUMN_NAME_HIZB_QUARTER = "hizb_quarter";
    public static final String COLUMN_NAME_SAJDA = "sajda";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_SURAH_NUMBER + " INTEGER," +
                    COLUMN_NAME_NUMBER + " INTEGER," +
                    COLUMN_NAME_TEXT + " TEXT," +
                    COLUMN_NAME_NUMBER_IN_SURAH + " INTEGER," +
                    COLUMN_NAME_JUZ + " INTEGER," +
                    COLUMN_NAME_MANZIL + " INTEGER," +
                    COLUMN_NAME_PAGE + " INTEGER," +
                    COLUMN_NAME_RUKU + " INTEGER," +
                    COLUMN_NAME_HIZB_QUARTER + " INTEGER," +
                    COLUMN_NAME_SAJDA + " INTEGER" +
                    ")";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
