package com.hbouzidi.fiveprayers.timings;

import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.MidnightModeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;

public class TimingsPreferences {

    private CalculationMethodEnum method;
    private String tune;
    private LatitudeAdjustmentMethod latitudeAdjustmentMethod;
    private SchoolAdjustmentMethod schoolAdjustmentMethod;
    private MidnightModeAdjustmentMethod midnightModeAdjustmentMethod;
    private int hijriAdjustment;

    public TimingsPreferences(CalculationMethodEnum method,
                              String tune,
                              LatitudeAdjustmentMethod latitudeAdjustmentMethod,
                              SchoolAdjustmentMethod schoolAdjustmentMethod,
                              MidnightModeAdjustmentMethod midnightModeAdjustmentMethod,
                              int hijriAdjustment) {
        this.method = method;
        this.tune = tune;
        this.latitudeAdjustmentMethod = latitudeAdjustmentMethod;
        this.schoolAdjustmentMethod = schoolAdjustmentMethod;
        this.midnightModeAdjustmentMethod = midnightModeAdjustmentMethod;
        this.hijriAdjustment = hijriAdjustment;
    }

    public CalculationMethodEnum getMethod() {
        return method;
    }

    public String getTune() {
        return tune;
    }

    public LatitudeAdjustmentMethod getLatitudeAdjustmentMethod() {
        return latitudeAdjustmentMethod;
    }

    public SchoolAdjustmentMethod getSchoolAdjustmentMethod() {
        return schoolAdjustmentMethod;
    }

    public MidnightModeAdjustmentMethod getMidnightModeAdjustmentMethod() {
        return midnightModeAdjustmentMethod;
    }

    public int getHijriAdjustment() {
        return hijriAdjustment;
    }
}
