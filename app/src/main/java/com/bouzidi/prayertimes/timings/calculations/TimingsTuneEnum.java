package com.bouzidi.prayertimes.timings.calculations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TimingsTuneEnum {

    MOROCCAN_MINISTRY_OF_ISLAMIC_AFFAIRS(0, 0, 0, 5, 0, 0, 0, 0, 0),
    MOSQUEE_DE_PARIS_FRANCE(0, 0, 0, 0, 0, 0, 0, 0, 0),
    DEFAULT(0, 0, 0, 0, 0, 0, 0, 0, 0);

    private int imsak;
    private int fajr;
    private int sunrise;
    private int dhuhr;
    private int asr;
    private int maghrib;
    private int sunset;
    private int isha;
    private int midnight;

    TimingsTuneEnum(int imsak,
                    int fajr,
                    int sunrise,
                    int dhuhr,
                    int asr,
                    int maghrib,
                    int sunset,
                    int isha,
                    int midnight) {
        this.imsak = imsak;
        this.fajr = fajr;
        this.sunrise = sunrise;
        this.dhuhr = dhuhr;
        this.asr = asr;
        this.maghrib = maghrib;
        this.sunset = sunset;
        this.isha = isha;
        this.midnight = midnight;
    }

    public static TimingsTuneEnum getValueByName(String name) {
        List<String> names = Arrays
                .stream(values())
                .map(String::valueOf)
                .collect(Collectors.toList());

        if (names.contains(name)) {
            return valueOf(name);
        }
        return DEFAULT;
    }

    public int getImsak() {
        return imsak;
    }

    public int getFajr() {
        return fajr;
    }

    public int getSunrise() {
        return sunrise;
    }

    public int getDhuhr() {
        return dhuhr;
    }

    public int getAsr() {
        return asr;
    }

    public int getMaghrib() {
        return maghrib;
    }

    public int getSunset() {
        return sunset;
    }

    public int getIsha() {
        return isha;
    }

    public int getMidnight() {
        return midnight;
    }
}
