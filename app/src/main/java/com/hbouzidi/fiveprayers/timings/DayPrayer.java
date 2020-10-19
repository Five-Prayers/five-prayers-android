package com.hbouzidi.fiveprayers.timings;

import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class DayPrayer implements Serializable {

    private String date;
    private long timestamp;
    private String timezone;
    private String city;
    private String country;

    private int hijriDay;
    private int hijriMonthNumber;
    private int hijriYear;

    private int gregorianDay;
    private int gregorianMonthNumber;
    private int gregorianYear;

    private Map<PrayerEnum, LocalDateTime> timings;
    private Map<ComplementaryTimingEnum, LocalDateTime> complementaryTiming;

    private CalculationMethodEnum calculationMethodEnum;

    private double latitude;
    private double longitude;

    public DayPrayer() {
    }

    public DayPrayer(String date, long timestamp, String city, String country, int hijriDay, int hijriMonthNumber, int hijriYear,
                     int gregorianDay, int gregorianMonthNumber,
                     int gregorianYear) {

        this.date = date;
        this.timestamp = timestamp;
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

    public String getDate() {
        return date;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public Map<PrayerEnum, LocalDateTime> getTimings() {
        return timings;
    }

    public void setTimings(Map<PrayerEnum, LocalDateTime> timings) {
        this.timings = timings;
    }

    public CalculationMethodEnum getCalculationMethodEnum() {
        return calculationMethodEnum;
    }

    public void setCalculationMethodEnum(CalculationMethodEnum calculationMethodEnum) {
        this.calculationMethodEnum = calculationMethodEnum;
    }

    public Map<ComplementaryTimingEnum, LocalDateTime> getComplementaryTiming() {
        return complementaryTiming;
    }

    public void setComplementaryTiming(Map<ComplementaryTimingEnum, LocalDateTime> complementaryTiming) {
        this.complementaryTiming = complementaryTiming;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DayPrayer dayPrayer = (DayPrayer) o;

        return new EqualsBuilder()
                .append(date, dayPrayer.date)
                .append(city, dayPrayer.city)
                .append(country, dayPrayer.country)
                .append(calculationMethodEnum, dayPrayer.calculationMethodEnum)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(date)
                .append(city)
                .append(country)
                .append(calculationMethodEnum)
                .toHashCode();
    }
}
