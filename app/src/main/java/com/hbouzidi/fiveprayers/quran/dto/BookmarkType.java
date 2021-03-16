package com.hbouzidi.fiveprayers.quran.dto;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public enum BookmarkType {

    USER_MADE(0, "USER_MADE"), AUTOMATIC(1, "AUTOMATIC");

    private final String name;
    private final int id;

    BookmarkType(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
