package com.hbouzidi.fiveprayers.quran.dto;

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
