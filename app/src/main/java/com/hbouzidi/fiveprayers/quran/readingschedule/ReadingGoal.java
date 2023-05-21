package com.hbouzidi.fiveprayers.quran.readingschedule;


public enum ReadingGoal {

    ONCE_IN_FOUR_MONTH(120, 0.25, 0.5, 2),
    ONCE_IN_TWO_MONTH(60, 0.5, 1, 4),
    ONCE_A_MONTH(30, 1, 2, 8),
    ONCE_IN_THREE_WEEKS(20, 1.5, 3, 12),
    TWICE_A_MONTH(15, 2, 4, 16),
    THREE_TIMES_A_MONTH(10, 3, 6, 24),
    FOUR_TIMES_A_MONTH(8, 4, 8, 32);

    private final int totalDays;
    private final double juz;
    private final double hizb;
    private final int rubu;

    ReadingGoal(int totalDays, double juz, double hizb, int rubu) {
        this.totalDays = totalDays;
        this.juz = juz;
        this.hizb = hizb;
        this.rubu = rubu;
    }

    public double getJuz() {
        return juz;
    }

    public double getHizb() {
        return hizb;
    }

    public int getRubu() {
        return rubu;
    }

    public int getTotalDays() {
        return totalDays;
    }
}
