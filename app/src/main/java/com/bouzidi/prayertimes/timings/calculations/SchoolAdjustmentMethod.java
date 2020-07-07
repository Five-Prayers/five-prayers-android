package com.bouzidi.prayertimes.timings.calculations;

public enum SchoolAdjustmentMethod {

    SHAFII(0),
    HANAFI(1);

    private int value;

    SchoolAdjustmentMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SchoolAdjustmentMethod getDefault() {
        return SHAFII;
    }
}
