package com.hbouzidi.fiveprayers.quran.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class Invocation implements Parcelable {

    private String text;
    private String language;


    public Invocation() {
    }

    public Invocation(Parcel in) {
        this.text = in.readString();
        this.language = in.readString();
    }

    public String getText() {
        return text;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(language);
    }

    public static final Creator<Invocation> CREATOR = new Creator<Invocation>() {
        @Override
        public Invocation createFromParcel(Parcel in) {
            return new Invocation(in);
        }

        @Override
        public Invocation[] newArray(int size) {
            return new Invocation[size];
        }
    };
}
