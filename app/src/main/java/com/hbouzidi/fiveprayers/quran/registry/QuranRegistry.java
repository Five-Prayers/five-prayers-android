package com.hbouzidi.fiveprayers.quran.registry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hbouzidi.fiveprayers.database.DatabaseHelper;
import com.hbouzidi.fiveprayers.quran.dto.Ayah;
import com.hbouzidi.fiveprayers.quran.dto.Page;
import com.hbouzidi.fiveprayers.quran.dto.Surah;
import com.hbouzidi.fiveprayers.quran.model.AyahModel;
import com.hbouzidi.fiveprayers.quran.model.SurahModel;

import java.util.ArrayList;
import java.util.List;

public class QuranRegistry {

    private static QuranRegistry quranRegistry;
    private final Context context;
    private DatabaseHelper databaseHelper;

    private QuranRegistry(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public static QuranRegistry getInstance(Context context) {
        if (quranRegistry == null) {
            quranRegistry = new QuranRegistry(context);
        }
        return quranRegistry;
    }

    public List<Surah> getSurahs() {
        List<Surah> surahs = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sortOrder =
                SurahModel.COLUMN_NAME_NUMBER + " ASC";

        Cursor cursor = db.query(
                SurahModel.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            do {
                surahs.add(createSurah(cursor));
            } while (cursor.moveToNext());
        }

        return surahs;
    }

    public List<Page> getAllPages() {
        List<Page> pages = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sortOrder =
                AyahModel.COLUMN_NAME_NUMBER + " ASC";

        Cursor cursor = db.query(
                AyahModel.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        );

        Page currentPage = new Page(1);

        if (cursor.moveToFirst()) {
            do {
                Ayah ayah = createAyah(cursor);

                currentPage.setJuz(ayah.getJuz());
                currentPage.setRubHizb(ayah.getHizbQuarter());

                if (currentPage.getPageNum() == ayah.getPage()) {
                    currentPage.getAyahs().add(ayah);
                } else {
                    pages.add(currentPage);
                    currentPage = new Page(ayah.getPage());
                    currentPage.getAyahs().add(ayah);
                }

                if (cursor.isLast()) {
                    pages.add(currentPage);
                }

            } while (cursor.moveToNext());
        }

        return pages;
    }

    public List<Ayah> getPageAyahs(int page) {
        List<Ayah> ayahs = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String selection = AyahModel.COLUMN_NAME_PAGE + " = ?";

        String[] selectionArgs = {
                String.valueOf(page)
        };

        String sortOrder =
                AyahModel.COLUMN_NAME_NUMBER + " ASC";

        Cursor cursor = db.query(
                AyahModel.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            do {
                ayahs.add(createAyah(cursor));
            } while (cursor.moveToNext());
        }

        return ayahs;
    }

    private Surah createSurah(Cursor cursor) {
        Surah surah = new Surah();

        surah.setNumber(cursor.getInt(cursor.getColumnIndex(SurahModel.COLUMN_NAME_NUMBER)));
        surah.setNumberOfAyahs(cursor.getInt(cursor.getColumnIndex(SurahModel.COLUMN_NAME_NUMBER_OF_AYAHS)));
        surah.setEnglishName(cursor.getString(cursor.getColumnIndex(SurahModel.COLUMN_NAME_ENGLISH_NAME)));
        surah.setEnglishNameTranslation(cursor.getString((cursor.getColumnIndex(SurahModel.COLUMN_NAME_ENGLISH_NAME_TRANSLATION))));
        surah.setName(cursor.getString((cursor.getColumnIndex(SurahModel.COLUMN_NAME_NAME))));
        surah.setPage(cursor.getInt((cursor.getColumnIndex(SurahModel.COLUMN_NAME_PAGE))));
        surah.setRevelationType(cursor.getString((cursor.getColumnIndex(SurahModel.COLUMN_NAME_REVELATION_TYPE))));

        return surah;
    }

    private Ayah createAyah(Cursor cursor) {
        Ayah ayah = new Ayah();

        ayah.setSurahNumber(cursor.getInt(cursor.getColumnIndex(AyahModel.COLUMN_NAME_SURAH_NUMBER)));
        ayah.setNumber(cursor.getInt(cursor.getColumnIndex(AyahModel.COLUMN_NAME_NUMBER)));
        ayah.setNumberInSurah(cursor.getInt(cursor.getColumnIndex(AyahModel.COLUMN_NAME_NUMBER_IN_SURAH)));
        ayah.setText(cursor.getString(cursor.getColumnIndex(AyahModel.COLUMN_NAME_TEXT)));
        ayah.setHizbQuarter(cursor.getInt(cursor.getColumnIndex(AyahModel.COLUMN_NAME_HIZB_QUARTER)));
        ayah.setJuz(cursor.getInt(cursor.getColumnIndex(AyahModel.COLUMN_NAME_JUZ)));
        ayah.setManzil(cursor.getInt(cursor.getColumnIndex(AyahModel.COLUMN_NAME_MANZIL)));
        ayah.setPage(cursor.getInt(cursor.getColumnIndex(AyahModel.COLUMN_NAME_PAGE)));
        ayah.setRuku(cursor.getInt(cursor.getColumnIndex(AyahModel.COLUMN_NAME_RUKU)));

        return ayah;
    }
}
