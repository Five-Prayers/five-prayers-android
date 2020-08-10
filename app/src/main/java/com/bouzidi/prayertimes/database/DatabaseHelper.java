package com.bouzidi.prayertimes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bouzidi.prayertimes.quran.dto.Ayah;
import com.bouzidi.prayertimes.quran.dto.QuranResponse;
import com.bouzidi.prayertimes.quran.dto.Surah;
import com.bouzidi.prayertimes.quran.model.AyahModel;
import com.bouzidi.prayertimes.quran.model.SurahModel;
import com.bouzidi.prayertimes.quran.parser.QuranParser;
import com.bouzidi.prayertimes.utils.TashkeelUtils;

import java.io.IOException;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "five_prayer.db";
    private final Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PrayerModel.SQL_CREATE_TABLE);
        db.execSQL(SurahModel.SQL_CREATE_TABLE);
        db.execSQL(AyahModel.SQL_CREATE_TABLE);

        try {
            QuranResponse quranResponse = QuranParser.parseQuranFromAssets(Objects.requireNonNull(this.context));

            for (Surah surah : quranResponse.getData().getSurahs()) {
                saveSurah(surah, db);
                for (Ayah ayah : surah.getAyahs()) {
                    saveAyah(ayah, surah, db);
                }
            }
        } catch (IOException e) {
            Log.e(DatabaseHelper.class.getName(), "Cannot persist Quran data", e);
        }
    }

    private void saveAyah(Ayah ayah, Surah surah, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(AyahModel.COLUMN_NAME_SURAH_NUMBER, surah.getNumber());
        values.put(AyahModel.COLUMN_NAME_NUMBER, ayah.getNumber());
        values.put(AyahModel.COLUMN_NAME_TEXT, ayah.getText());
        values.put(AyahModel.COLUMN_NAME_NUMBER_IN_SURAH, ayah.getNumberInSurah());
        values.put(AyahModel.COLUMN_NAME_JUZ, ayah.getJuz());
        values.put(AyahModel.COLUMN_NAME_MANZIL, ayah.getManzil());
        values.put(AyahModel.COLUMN_NAME_PAGE, ayah.getPage());
        values.put(AyahModel.COLUMN_NAME_RUKU, ayah.getRuku());
        values.put(AyahModel.COLUMN_NAME_HIZB_QUARTER, ayah.getHizbQuarter());
        values.put(AyahModel.COLUMN_NAME_SAJDA, (ayah.getSajda() instanceof Boolean) ? 1 : 0);

        db.insert(AyahModel.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PrayerModel.SQL_DELETE_TABLE);
        db.execSQL(SurahModel.SQL_DELETE_TABLE);
        db.execSQL(AyahModel.SQL_DELETE_TABLE);
        onCreate(db);
    }

    private void saveSurah(Surah surah, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(SurahModel.COLUMN_NAME_NUMBER, surah.getNumber());
        values.put(SurahModel.COLUMN_NAME_NAME, TashkeelUtils.removeTashkeel(surah.getName()));
        values.put(SurahModel.COLUMN_NAME_PAGE, surah.getAyahs().get(0).getPage());
        values.put(SurahModel.COLUMN_NAME_ENGLISH_NAME, surah.getEnglishName());
        values.put(SurahModel.COLUMN_NAME_ENGLISH_NAME_TRANSLATION, surah.getEnglishNameTranslation());
        values.put(SurahModel.COLUMN_NAME_NUMBER_OF_AYAHS, surah.getAyahs().size());
        values.put(SurahModel.COLUMN_NAME_REVELATION_TYPE, surah.getRevelationType());

        db.insert(SurahModel.TABLE_NAME, null, values);
    }
}
