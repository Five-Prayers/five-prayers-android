package com.hbouzidi.fiveprayers.timings.londonprayertimes;

import java.util.Map;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class LondonUnifiedCalendarResponse {

    private Map<String, LondonUnifiedTimingsResponse> times;

    public void setTimes(Map<String, LondonUnifiedTimingsResponse> times) {
        this.times = times;
    }

    public Map<String, LondonUnifiedTimingsResponse> getTimes() {
        return times;
    }
}