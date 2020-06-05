package com.bouzidi.prayertimes.timings.aladhan;

public class AladhanDate {

    private AladhanDateType hijri;
    private AladhanDateType gregorian;

    public AladhanDateType getHijri() {
        return hijri;
    }

    public void setHijri(AladhanDateType hijri) {
        this.hijri = hijri;
    }

    public AladhanDateType getGregorian() {
        return gregorian;
    }

    public void setGregorian(AladhanDateType gregorian) {
        this.gregorian = gregorian;
    }
}
