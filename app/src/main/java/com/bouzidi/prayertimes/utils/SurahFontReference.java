package com.bouzidi.prayertimes.utils;

public class SurahFontReference {

    private Integer fontResourceId;
    private String regularText;
    private String boldText;

    public SurahFontReference(Integer fontResourceId, String regularText, String boldText) {
        this.fontResourceId = fontResourceId;
        this.regularText = regularText;
        this.boldText = boldText;
    }

    public Integer getFontResourceId() {
        return fontResourceId;
    }

    public String getRegularText() {
        return regularText;
    }

    public String getBoldText() {
        return boldText;
    }
}
