package com.hbouzidi.fiveprayers.timings;

public class TimingServiceFactory {

    public static TimingsService create() {
        return DefaultTimingsService.getInstance();
    }
}
