package com.hbouzidi.fiveprayers.timings;

public enum HijriHoliday {

    FIRST_DAY_OF_YEAR(1, 1),
    YAWM_ASHURA(10, 1),
    MAWLID_A_NABAWI(12, 3),
    LAILAT_AL_MIRAJ(27, 7),
    MID_SHAABAN_NIGHT(14, 8),
    MID_SHAABAN(15, 8),
    FIRST_DAY_OF_RAMADAN(1, 9),
    FIRST_DAY_OF_LAILAT_AL_QADR(19, 9),
    SECOND_DAY_OF_LAILAT_AL_QADR(21, 9),
    THIRD_DAY_OF_LAILAT_AL_QADR(23, 9),
    FOURTH_DAY_OF_LAILAT_AL_QADR(25, 9),
    FIFTH_DAY_OF_LAILAT_AL_QADR(27, 9),
    SIXT_DAY_OF_LAILAT_AL_QADR(29, 9),
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
