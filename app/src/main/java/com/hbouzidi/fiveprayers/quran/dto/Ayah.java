package com.hbouzidi.fiveprayers.quran.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class Ayah implements Parcelable {

    private int number;
    private String text;
    private Edition edition;
    private Surah surah;

    private int numberInSurah;
    private int juz;
    private int manzil;
    private int page;
    private int ruku;
    private int hizbQuarter;

    public Ayah() {
    }

    public Ayah(Parcel in) {
        this.number = in.readInt();
        this.text = in.readString();
        this.edition = in.readParcelable(Parcelable.class.getClassLoader());
        this.surah = in.readParcelable(Surah.class.getClassLoader());
        this.numberInSurah = in.readInt();
        this.juz = in.readInt();
        this.manzil = in.readInt();
        this.page = in.readInt();
        this.ruku = in.readInt();
        this.hizbQuarter = in.readInt();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Edition getEdition() {
        return edition;
    }

    public void setEdition(Edition edition) {
        this.edition = edition;
    }

    public Surah getSurah() {
        return surah;
    }

    public void setSurah(Surah surah) {
        this.surah = surah;
    }

    public int getNumberInSurah() {
        return numberInSurah;
    }

    public void setNumberInSurah(int numberInSurah) {
        this.numberInSurah = numberInSurah;
    }

    public int getJuz() {
        return juz;
    }

    public void setJuz(int juz) {
        this.juz = juz;
    }

    public int getManzil() {
        return manzil;
    }

    public void setManzil(int manzil) {
        this.manzil = manzil;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRuku() {
        return ruku;
    }

    public void setRuku(int ruku) {
        this.ruku = ruku;
    }

    public int getHizbQuarter() {
        return hizbQuarter;
    }

    public void setHizbQuarter(int hizbQuarter) {
        this.hizbQuarter = hizbQuarter;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeString(text);
        dest.writeParcelable(edition, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeParcelable(surah, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeInt(numberInSurah);
        dest.writeInt(juz);
        dest.writeInt(manzil);
        dest.writeInt(page);
        dest.writeInt(ruku);
        dest.writeInt(hizbQuarter);
    }

    public static final Creator<Ayah> CREATOR = new Creator<Ayah>() {
        @Override
        public Ayah createFromParcel(Parcel in) {
            return new Ayah(in);
        }

        @Override
        public Ayah[] newArray(int size) {
            return new Ayah[size];
        }
    };
}
