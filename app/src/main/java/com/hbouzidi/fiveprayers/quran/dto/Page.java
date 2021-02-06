package com.hbouzidi.fiveprayers.quran.dto;

public class Page {
    private int pageNum;
    private int rubHizb;
    private int juz;
    private int surahNumber;

    public Page(int pageNum) {
        this.pageNum = pageNum;
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
