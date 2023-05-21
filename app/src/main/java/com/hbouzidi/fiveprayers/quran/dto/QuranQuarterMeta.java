package com.hbouzidi.fiveprayers.quran.dto;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranQuarterMeta {

    private int surah;

    private int ayah;

    public QuranQuarterMeta(int surah, int ayah) {
        this.surah = surah;
        this.ayah = ayah;
    }

    public void setSurah(int surah) {
        this.surah = surah;
    }

    public void setAyah(int ayah) {
        this.ayah = ayah;
    }

    public int getSurah() {
        return surah;
    }

    public int getAyah() {
        return ayah;
    }
}
