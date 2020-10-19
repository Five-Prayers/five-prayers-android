package com.hbouzidi.fiveprayers.quran.dto;

import java.util.List;

public class QuranData {

    private List<Surah> surahs;
    private Edition edition;

    public List<Surah> getSurahs() {
        return surahs;
    }

    public void setSurahs(List<Surah> surahs) {
        this.surahs = surahs;
    }

    public Edition getEdition() {
        return edition;
    }

    public void setEdition(Edition edition) {
        this.edition = edition;
    }
}
