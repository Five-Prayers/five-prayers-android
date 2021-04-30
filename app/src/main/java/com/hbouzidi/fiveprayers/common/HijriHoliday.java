package com.hbouzidi.fiveprayers.common;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public enum HijriHoliday {

    FIRST_DAY_OF_YEAR(1, 1),
    YAWM_ASHURA(10, 1),
    MAWLID_A_NABAWI(12, 3),
    LAILAT_AL_MIRAJ(27, 7),
    MID_SHAABAN_NIGHT(14, 8),
    MID_SHAABAN(15, 8),
    FIRST_DAY_OF_RAMADAN(1, 9),
    DAY_20_OF_RAMADAN(20, 9),
    DAY_21_OF_RAMADAN(21, 9),
    DAY_22_OF_RAMADAN(22, 9),
    DAY_23_OF_RAMADAN(23, 9),
    DAY_24_OF_RAMADAN(24, 9),
    DAY_25_OF_RAMADAN(25, 9),
    DAY_26_OF_RAMADAN(26, 9),
    DAY_27_OF_RAMADAN(27, 9),
    DAY_28_OF_RAMADAN(28, 9),
    DAY_29_OF_RAMADAN(29, 9),
    DAY_30_OF_RAMADAN(30, 9),
    EID_AL_FITR(1, 10),
    FIRST_DAY_OF_AL_Hajj(8, 12),
    DAY_OF_ARAFAH(9, 12),
    EID_AL_ADHA(10, 12),
    FOURTH_DAY_OF_AL_Hajj(11, 12),
    FIFTH_DAY_OF_AL_Hajj(12, 12),
    SIXT_DAY_OF_AL_Hajj(13, 12);

    private int day;
    private int month;

    HijriHoliday(int day, int month) {
        this.day = day;
        this.month = month;
    }

    public static HijriHoliday getHoliday(int day, int month) {
        for (HijriHoliday hijriHoliday : values()) {
            if (hijriHoliday.getMonth() == month && hijriHoliday.getDay() == day) {
                return hijriHoliday;
            }
        }
        return null;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }
}
