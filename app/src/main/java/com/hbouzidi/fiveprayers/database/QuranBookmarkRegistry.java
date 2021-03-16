package com.hbouzidi.fiveprayers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hbouzidi.fiveprayers.quran.dto.BookmarkType;
import com.hbouzidi.fiveprayers.quran.dto.QuranBookmark;
import com.hbouzidi.fiveprayers.quran.dto.QuranPage;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranBookmarkRegistry {

    private static QuranBookmarkRegistry quranBookmarkRegistry;
    private final DatabaseHelper databaseHelper;

    private QuranBookmarkRegistry(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public static QuranBookmarkRegistry getInstance(Context context) {
        if (quranBookmarkRegistry == null) {
            quranBookmarkRegistry = new QuranBookmarkRegistry(context);
        }
        return quranBookmarkRegistry;
    }

    public long saveBookmark(QuranPage quranPage, BookmarkType bookmarkType) {
        Log.i(QuranBookmarkRegistry.class.getName(), "Inserting new Bookmark");

        long timestamps = ZonedDateTime.now(ZoneOffset.systemDefault()).toEpochSecond();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QuranBookmarkModel.COLUMN_NAME_DATE_TIMESTAMP, timestamps);
        values.put(QuranBookmarkModel.COLUMN_NAME_PAGE_NUMBER, quranPage.getPageNum());
        values.put(QuranBookmarkModel.COLUMN_NAME_SURAH_NUMBER, quranPage.getSurahNumber());
        values.put(QuranBookmarkModel.COLUMN_NAME_JUZ_NUMBER, quranPage.getJuz());
        values.put(QuranBookmarkModel.COLUMN_NAME_RUB_HIZB_NUMBER, quranPage.getRubHizb());
        values.put(QuranBookmarkModel.COLUMN_NAME_BOOKMARK_TYPE, bookmarkType.getName());

        return db.insertWithOnConflict(QuranBookmarkModel.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long deleteBookmark(int pageNumber) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        return db.delete(QuranBookmarkModel.TABLE_NAME, QuranBookmarkModel.COLUMN_NAME_PAGE_NUMBER + " = ?", new String[]{String.valueOf(pageNumber)});
    }

    public QuranBookmark getBookmarkByPageNumber(int pageNumber, BookmarkType BookmarkType) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Log.i(PrayerRegistry.class.getName(), "Getting Bookmark page " + pageNumber);

        String selection = QuranBookmarkModel.COLUMN_NAME_PAGE_NUMBER + " = ?" +
                " AND " + QuranBookmarkModel.COLUMN_NAME_BOOKMARK_TYPE + " = ?";

        String[] selectionArgs = {
                String.valueOf(pageNumber),
                String.valueOf(BookmarkType.getName())
        };

        Cursor cursor = db.query(
                QuranBookmarkModel.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean first = cursor.moveToFirst();

        QuranBookmark quranBookmark = null;

        if (first) {
            quranBookmark = createBookmark(cursor);
        }

        return quranBookmark;
    }

    public List<QuranBookmark> getAllBookmarks() {
        List<QuranBookmark> quranBookmarks = new ArrayList<>();

        Log.i(QuranBookmarkRegistry.class.getName(), "Getting All Bookmarks");

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sortOrder =
                QuranBookmarkModel.COLUMN_NAME_DATE_TIMESTAMP + " DESC";

        Cursor cursor = db.query(
                QuranBookmarkModel.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            do {
                quranBookmarks.add(createBookmark(cursor));
            } while (cursor.moveToNext());
        }

        return quranBookmarks;
    }

    private QuranBookmark createBookmark(Cursor cursor) {
        long timestamp = cursor.getLong(cursor.getColumnIndex(QuranBookmarkModel.COLUMN_NAME_DATE_TIMESTAMP));
        String bookmarkType = cursor.getString(cursor.getColumnIndex(QuranBookmarkModel.COLUMN_NAME_BOOKMARK_TYPE));

        QuranPage quranPage = new QuranPage(
                cursor.getInt(cursor.getColumnIndex(QuranBookmarkModel.COLUMN_NAME_PAGE_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(QuranBookmarkModel.COLUMN_NAME_SURAH_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(QuranBookmarkModel.COLUMN_NAME_JUZ_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(QuranBookmarkModel.COLUMN_NAME_RUB_HIZB_NUMBER))
        );

        return new QuranBookmark(timestamp, BookmarkType.valueOf(bookmarkType), quranPage);
    }
}
