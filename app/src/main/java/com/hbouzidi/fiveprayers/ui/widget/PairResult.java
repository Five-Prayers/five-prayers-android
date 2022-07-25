package com.hbouzidi.fiveprayers.ui.widget;

import com.hbouzidi.fiveprayers.openweathermap.OpenWeatherMapResponse;
import com.hbouzidi.fiveprayers.timings.DayPrayer;

public class PairResult {

    private final DayPrayer dayPrayer;
    private final OpenWeatherMapResponse openWeatherMapResponse;

    public PairResult(DayPrayer dayPrayer, OpenWeatherMapResponse openWeatherMapResponse) {
        this.dayPrayer = dayPrayer;
        this.openWeatherMapResponse = openWeatherMapResponse;
    }

    public DayPrayer getDayPrayer() {
        return dayPrayer;
    }

    public OpenWeatherMapResponse getOpenWeatherMapResponse() {
        return openWeatherMapResponse;
    }

}
