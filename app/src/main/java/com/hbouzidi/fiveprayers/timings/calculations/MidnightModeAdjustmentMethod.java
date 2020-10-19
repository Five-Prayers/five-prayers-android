package com.hbouzidi.fiveprayers.timings.calculations;

public enum MidnightModeAdjustmentMethod {

    STANDARD(0),
    JAFARI(1);

    private int value;

    MidnightModeAdjustmentMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MidnightModeAdjustmentMethod getDefault() {
        return STANDARD;
    }
}
