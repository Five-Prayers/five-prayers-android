package com.hbouzidi.fiveprayers.quran.dto;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranPage {
    private int pageNum;
    private int surahNumber;
    private int juz;
    private int rubHizb;

    public QuranPage(int pageNum, int surahNumber, int juz, int rubHizb) {
        this.pageNum = pageNum;
        this.juz = juz;
        this.rubHizb = rubHizb;
        this.surahNumber = surahNumber;
    }

    public int getJuz() {
        return juz;
    }

    public void setJuz(int juz) {
        this.juz = juz;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getRubHizb() {
        return rubHizb;
    }

    public void setRubHizb(int rubHizb) {
        this.rubHizb = rubHizb;
    }

    public void setSurahNumber(int surahNumber) {
        this.surahNumber = surahNumber;
    }

    public int getSurahNumber() {
        return this.surahNumber;
    }
}
