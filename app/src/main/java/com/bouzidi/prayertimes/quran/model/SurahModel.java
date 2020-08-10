package com.bouzidi.prayertimes.quran.model;

import android.provider.BaseColumns;

public class SurahModel implements BaseColumns {

    public static final String TABLE_NAME = "surah";

    public static final String COLUMN_NAME_NUMBER = "number";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_PAGE = "page";
    public static final String COLUMN_NAME_ENGLISH_NAME = "english_name";
    public static final String COLUMN_NAME_ENGLISH_NAME_TRANSLATION = "english_name_translation";
    public static final String COLUMN_NAME_NUMBER_OF_AYAHS = "number_of_ayahs";
    public static final String COLUMN_NAME_REVELATION_TYPE = "revelation_type";

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_NUMBER + " INTEGER," +
                    COLUMN_NAME_NAME + " TEXT," +
                    COLUMN_NAME_ENGLISH_NAME + " TEXT," +
                    COLUMN_NAME_PAGE + " INTEGER," +
                    COLUMN_NAME_ENGLISH_NAME_TRANSLATION + " TEXT," +
                    COLUMN_NAME_NUMBER_OF_AYAHS + " INTEGER," +
                    COLUMN_NAME_REVELATION_TYPE + " TEXT" +
                    ")";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
