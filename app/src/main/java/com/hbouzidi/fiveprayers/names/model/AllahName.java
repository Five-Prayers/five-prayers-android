package com.hbouzidi.fiveprayers.names.model;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class AllahName {

    private int number;
    private String name;
    private String transliteration;
    private String fontReference;
    private String drawableName;

    public AllahName(int number, String drawableName, String name, String transliteration, String fontReference) {
        this.name = name;
        this.transliteration = transliteration;
        this.number = number;
        this.fontReference = fontReference;
        this.drawableName = drawableName;
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

    public String getDrawableName() {
        return drawableName;
    }

    public void setDrawableName(String drawableName) {
        this.drawableName = drawableName;
    }
}
