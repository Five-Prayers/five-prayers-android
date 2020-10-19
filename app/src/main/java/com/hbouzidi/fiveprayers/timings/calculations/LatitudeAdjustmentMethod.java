package com.hbouzidi.fiveprayers.timings.calculations;

public enum LatitudeAdjustmentMethod {

    MIDDLE_OF_THE_NIGHT(1),
    ONE_SEVENTH(2),
    ANGLE_BASED(3);

    private int value;

    LatitudeAdjustmentMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LatitudeAdjustmentMethod getDefault() {
        return ANGLE_BASED;
    }
}
