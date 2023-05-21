package com.hbouzidi.fiveprayers.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingSchedule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class ReadingScheduleRegistry {

    private final DatabaseHelper databaseHelper;

    @Inject
    public ReadingScheduleRegistry(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void saveReadingSchedule(List<ReadingSchedule> readingSchedules) {
        Log.i(ReadingScheduleRegistry.class.getName(), "Saving Reading Schedule");

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        for (ReadingSchedule readingSchedule : readingSchedules) {

            ContentValues values = new ContentValues();
            values.put(ReadingScheduleModel.COLUMN_NAME_DAY_NUMBER, readingSchedule.getDayNumber());
            values.put(ReadingScheduleModel.COLUMN_NAME_TOTAL_DAYS, readingSchedule.getTotalDays());
            values.put(ReadingScheduleModel.COLUMN_NAME_START_PAGE, readingSchedule.getStartPage());
            values.put(ReadingScheduleModel.COLUMN_NAME_START_QUARTER, readingSchedule.getStartQuarter());
            values.put(ReadingScheduleModel.COLUMN_NAME_START_AYAH_NUMBER, readingSchedule.getStartAyahNumber());
            values.put(ReadingScheduleModel.COLUMN_NAME_START_SURAH_NUMBER, readingSchedule.getStartSurahNumber());
            values.put(ReadingScheduleModel.COLUMN_NAME_END_PAGE, readingSchedule.getEndPage());
            values.put(ReadingScheduleModel.COLUMN_NAME_END_QUARTER, readingSchedule.getEndQuarter());
            values.put(ReadingScheduleModel.COLUMN_NAME_STATUS, readingSchedule.getStatus());

            db.insertWithOnConflict(ReadingScheduleModel.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public long updateScheduleStatus(int dayNumber, int status) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ReadingScheduleModel.COLUMN_NAME_STATUS, status);

        return db.update(ReadingScheduleModel.TABLE_NAME, cv, ReadingScheduleModel.COLUMN_NAME_DAY_NUMBER + " = ?", new String[]{String.valueOf(dayNumber)});
    }

    public List<ReadingSchedule> getReadingSchedule() {
        List<ReadingSchedule> readingSchedules = new ArrayList<>();

        Log.i(ReadingScheduleRegistry.class.getName(), "Getting Reading Schedule");

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sortOrder =
                ReadingScheduleModel.COLUMN_NAME_DAY_NUMBER + " ASC";

        Cursor cursor = db.query(
                ReadingScheduleModel.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            do {
                readingSchedules.add(createReadingSchedule(cursor));
            } while (cursor.moveToNext());
        }

        return readingSchedules;
    }

    public ReadingSchedule getNextReadingScheduleOccurrence() {
        ReadingSchedule nextReadingSchedule = null;

        Log.i(ReadingScheduleRegistry.class.getName(), "Getting Next Reading Schedule Occurrence");

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sortOrder =
                ReadingScheduleModel.COLUMN_NAME_DAY_NUMBER + " ASC";

        String selection = ReadingScheduleModel.COLUMN_NAME_STATUS + " = ?";

        String[] selectionArgs = {
                String.valueOf(0),
        };

        Cursor cursor = db.query(
                ReadingScheduleModel.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        boolean first = cursor.moveToFirst();

        if (first) {
            nextReadingSchedule = createReadingSchedule(cursor);
        }

        return nextReadingSchedule;
    }

    public void deleteReadingSchedule() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        db.execSQL("delete from " + ReadingScheduleModel.TABLE_NAME);
    }

    @SuppressLint("Range")
    private ReadingSchedule createReadingSchedule(Cursor cursor) {

        return new ReadingSchedule(
                cursor.getInt(cursor.getColumnIndex(ReadingScheduleModel.COLUMN_NAME_DAY_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(ReadingScheduleModel.COLUMN_NAME_TOTAL_DAYS)),
                cursor.getInt(cursor.getColumnIndex(ReadingScheduleModel.COLUMN_NAME_START_PAGE)),
                cursor.getInt(cursor.getColumnIndex(ReadingScheduleModel.COLUMN_NAME_START_QUARTER)),
                cursor.getInt(cursor.getColumnIndex(ReadingScheduleModel.COLUMN_NAME_START_AYAH_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(ReadingScheduleModel.COLUMN_NAME_START_SURAH_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(ReadingScheduleModel.COLUMN_NAME_END_PAGE)),
                cursor.getInt(cursor.getColumnIndex(ReadingScheduleModel.COLUMN_NAME_END_QUARTER)),
                cursor.getInt(cursor.getColumnIndex(ReadingScheduleModel.COLUMN_NAME_STATUS)));
    }
}
