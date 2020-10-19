package com.hbouzidi.fiveprayers.names.model;

public class AllahName {

    private int number;
    private String name;
    private String transliteration;
    private String fontReference;

    public AllahName(int number, String name, String transliteration, String fontReference) {
        this.name = name;
        this.transliteration = transliteration;
        this.number = number;
        this.fontReference = fontReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransliteration() {
        return transliteration;
    }

    public void setTransliteration(String transliteration) {
        this.transliteration = transliteration;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFontReference() {
        return fontReference;
    }

    public void setFontReference(String fontReference) {
        this.fontReference = fontReference;
    }
}
