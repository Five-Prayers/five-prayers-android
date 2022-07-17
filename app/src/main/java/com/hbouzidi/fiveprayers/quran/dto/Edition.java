package com.hbouzidi.fiveprayers.quran.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class Edition implements Parcelable {

    private String identifier;
    private String language;
    private String name;
    private String englishName;
    private String format;
    private String type;
    private String direction;

    public Edition() {
    }

    public Edition(Parcel in) {
        this.identifier = in.readString();
        this.language = in.readString();
        this.name = in.readString();
        this.englishName = in.readString();
        this.format = in.readString();
        this.type = in.readString();
        this.direction = in.readString();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(language);
        dest.writeString(name);
        dest.writeString(englishName);
        dest.writeString(format);
        dest.writeString(type);
        dest.writeString(direction);
    }

    public static final Creator<Edition> CREATOR = new Creator<Edition>() {
        @Override
        public Edition createFromParcel(Parcel in) {
            return new Edition(in);
        }

        @Override
        public Edition[] newArray(int size) {
            return new Edition[size];
        }
    };
}
