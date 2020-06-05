package com.bouzidi.prayertimes.timings.aladhan;

public class AladhanDateType {

    private String date;
    private String format;
    private String day;
    private String year;
    private AladhanMonth month;
    private AladhanDesignation designation;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public AladhanMonth getMonth() {
        return month;
    }

    public void setMonth(AladhanMonth month) {
        this.month = month;
    }

    public AladhanDesignation getDesignation() {
        return designation;
    }

    public void setDesignation(AladhanDesignation designation) {
        this.designation = designation;
    }
}
