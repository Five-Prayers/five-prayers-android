package com.hbouzidi.fiveprayers.timings.londonprayertimes;

import java.util.Map;

public class LondonUnifiedCalendarResponse {

    private Map<String, LondonUnifiedTimingsResponse> times;

    public void setTimes(Map<String, LondonUnifiedTimingsResponse> times) {
        this.times = times;
    }

    public Map<String, LondonUnifiedTimingsResponse> getTimes() {
        return times;
    }
}