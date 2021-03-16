package com.hbouzidi.fiveprayers.timings.calculations;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
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
