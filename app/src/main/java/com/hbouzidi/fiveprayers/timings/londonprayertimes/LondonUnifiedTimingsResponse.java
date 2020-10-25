package com.hbouzidi.fiveprayers.timings.londonprayertimes;

import com.google.gson.annotations.SerializedName;

public class LondonUnifiedTimingsResponse {

    private String date;
    private String fajr;

    @SerializedName("fajr_jamat")
    private String fajrJamat;

    private String sunrise;
    private String dhuhr;

    @SerializedName("dhuhr_jamat")
    private String dhuhrJamat;
    private String asr;

    @SerializedName("asr_2")
    private String asrTwo;

    @SerializedName("asr_jamat")
    private String asrJamat;
    private String magrib;

    @SerializedName("magrib_jamat")
    private String magribJamat;
    private String isha;

    @SerializedName("isha_jamat")
    private String ishaJamat;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFajr() {
        return fajr;
    }

    public void setFajr(String fajr) {
        this.fajr = fajr;
    }

    public String getFajrJamat() {
        return fajrJamat;
    }

    public void setFajrJamat(String fajrJamat) {
        this.fajrJamat = fajrJamat;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getDhuhr() {
        return dhuhr;
    }

    public void setDhuhr(String dhuhr) {
        this.dhuhr = dhuhr;
    }

    public String getDhuhrJamat() {
        return dhuhrJamat;
    }

    public void setDhuhrJamat(String dhuhrJamat) {
        this.dhuhrJamat = dhuhrJamat;
    }

    public String getAsr() {
        return asr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }

    public String getAsrTwo() {
        return asrTwo;
    }

    public void setAsrTwo(String asrTwo) {
        this.asrTwo = asrTwo;
    }

    public String getAsrJamat() {
        return asrJamat;
    }

    public void setAsrJamat(String asrJamat) {
        this.asrJamat = asrJamat;
    }

    public String getMagrib() {
        return magrib;
    }

    public void setMagrib(String magrib) {
        this.magrib = magrib;
    }

    public String getMagribJamat() {
        return magribJamat;
    }

    public void setMagribJamat(String magribJamat) {
        this.magribJamat = magribJamat;
    }

    public String getIsha() {
        return isha;
    }

    public void setIsha(String isha) {
        this.isha = isha;
    }

    public String getIshaJamat() {
        return ishaJamat;
    }

    public void setIshaJamat(String ishaJamat) {
        this.ishaJamat = ishaJamat;
    }
}