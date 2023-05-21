package com.hbouzidi.fiveprayers.quran.readingschedule;

public class ReadingSchedule {

    private final int dayNumber;
    private final int totalDays;
    private final int startPage;
    private final int startQuarter;
    private final int startAyahNumber;
    private final int startSurahNumber;
    private final int endPage;
    private final int endQuarter;
    private final int status;

    public ReadingSchedule(int dayNumber, int totalDays, int startPage, int startQuarter, int startAyahNumber,
                           int startSurahNumber, int endPage, int endQuarter, int status) {
        this.dayNumber = dayNumber;
        this.totalDays = totalDays;
        this.startPage = startPage;
        this.startQuarter = startQuarter;
        this.startAyahNumber = startAyahNumber;
        this.startSurahNumber = startSurahNumber;
        this.endPage = endPage;
        this.endQuarter = endQuarter;
        this.status = status;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getStartQuarter() {
        return startQuarter;
    }

    public int getStartAyahNumber() {
        return startAyahNumber;
    }

    public int getStartSurahNumber() {
        return startSurahNumber;
    }

    public int getEndPage() {
        return endPage;
    }

    public int getEndQuarter() {
        return endQuarter;
    }

    public int getStatus() {
        return status;
    }
}
