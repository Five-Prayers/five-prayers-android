package com.bouzidi.prayertimes.quran.dto;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private List<Ayah> ayahs;
    private int pageNum;
    private int rubHizb;
    private int juz;

    public Page(int pageNum) {
        this.pageNum = pageNum;
        ayahs = new ArrayList<>();
    }

    public int getJuz() {
        return juz;
    }

    public void setJuz(int juz) {
        this.juz = juz;
    }

    public List<Ayah> getAyahs() {
        return ayahs;
    }

    public void setAyahs(List<Ayah> ayahs) {
        this.ayahs = ayahs;
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
}
