package com.hbouzidi.fiveprayers.timings.calculations;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
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
