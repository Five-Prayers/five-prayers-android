package com.bouzidi.prayer_times.timings;

public class DayPrayer {

    private String date;
    private String city;
    private String country;

    private int hijriDay;
    private int hijriMonthNumber;
    private int hijriYear;

    private int gregorianDay;
    private int gregorianMonthNumber;
    private int gregorianYear;

    private Prayer[] prayers;

    public DayPrayer(String date, String city, String country, int hijriDay, int hijriMonthNumber, int hijriYear,
                     int gregorianDay, int gregorianMonthNumber,
                     int gregorianYear) {

        this.date = date;
        this.city = city;
        this.country = country;
        this.hijriDay = hijriDay;
        this.hijriMonthNumber = hijriMonthNumber;
        this.hijriYear = hijriYear;
        this.gregorianDay = gregorianDay;
        this.gregorianMonthNumber = gregorianMonthNumber;
        this.gregorianYear = gregorianYear;
    }

    public int getHijriDay() {
        return hijriDay;
    }

    public int getHijriMonthNumber() {
        return hijriMonthNumber;
    }

    public int getHijriYear() {
        return hijriYear;
    }

    public int getGregorianDay() {
        return gregorianDay;
    }

    public int getGregorianMonthNumber() {
        return gregorianMonthNumber;
    }

    public int getGregorianYear() {
        return gregorianYear;
    }

    public Prayer[] getPrayers() {
        return prayers;
    }

    public void setPrayers(Prayer[] prayers) {
        this.prayers = prayers;
    }

    public String getDate() {
        return date;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
