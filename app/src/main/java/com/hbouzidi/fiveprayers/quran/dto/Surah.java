package com.hbouzidi.fiveprayers.quran.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Surah implements Parcelable {

    private int number;
    private String name;
    private String englishName;
    private String englishNameTranslation;
    private String revelationType;
    private List<Ayah> ayahs;

    private transient int page;
    private transient int numberOfAyahs;

    public Surah() {
    }

    public Surah(Parcel in) {
        this.number = in.readInt();
        this.name = in.readString();
        this.englishName = in.readString();
        this.englishNameTranslation = in.readString();
        this.revelationType = in.readString();
        in.readList(this.ayahs, Ayah.class.getClassLoader());
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public String getEnglishNameTranslation() {
        return englishNameTranslation;
    }

    public void setEnglishNameTranslation(String englishNameTranslation) {
        this.englishNameTranslation = englishNameTranslation;
    }

    public String getRevelationType() {
        return revelationType;
    }

    public void setRevelationType(String revelationType) {
        this.revelationType = revelationType;
    }

    public List<Ayah> getAyahs() {
        return ayahs;
    }

    public void setAyahs(List<Ayah> ayahs) {
        this.ayahs = ayahs;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getNumberOfAyahs() {
        return numberOfAyahs;
    }

    public void setNumberOfAyahs(int numberOfAyahs) {
        this.numberOfAyahs = numberOfAyahs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeString(name);
        dest.writeString(englishName);
        dest.writeString(englishNameTranslation);
        dest.writeString(revelationType);
        dest.writeList(ayahs);
    }

    public static final Creator<Surah> CREATOR = new Creator<Surah>() {
        @Override
        public Surah createFromParcel(Parcel in) {
            return new Surah(in);
        }

        @Override
        public Surah[] newArray(int size) {
            return new Surah[size];
        }
    };
}
