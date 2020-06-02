package com.bouzidi.prayer_times.timings;

public class Prayer {

    private PrayerEnum key;
    private String timing;

    public Prayer(PrayerEnum key, String timing) {
        this.key = key;
        this.timing = timing;
    }

    public PrayerEnum getKey() {
        return key;
    }

    public String getTiming() {
        return timing;
    }
}
