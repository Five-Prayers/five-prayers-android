package com.hbouzidi.fiveprayers.quran.dto;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranBookmark {

    private long timestamps;
    private BookmarkType bookmarkType;
    private QuranPage quranPage;

    public QuranBookmark(long timestamps, BookmarkType bookmarkType, QuranPage quranPage) {
        this.timestamps = timestamps;
        this.bookmarkType = bookmarkType;
        this.quranPage = quranPage;
    }

    public long getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(long timestamps) {
        this.timestamps = timestamps;
    }

    public QuranPage getQuranPage() {
        return quranPage;
    }

    public void setQuranPage(QuranPage quranPage) {
        this.quranPage = quranPage;
    }

    public BookmarkType getBookmarkType() {
        return bookmarkType;
    }

    public void setBookmarkType(BookmarkType bookmarkType) {
        this.bookmarkType = bookmarkType;
    }
}
