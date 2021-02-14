package com.hbouzidi.fiveprayers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "five_prayer.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PrayerModel.SQL_CREATE_TABLE);
        db.execSQL(QuranBookmarkModel.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PrayerModel.SQL_DELETE_TABLE);
        db.execSQL(QuranBookmarkModel.SQL_DELETE_TABLE);
        onCreate(db);
    }
}
