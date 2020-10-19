package com.hbouzidi.fiveprayers.timings.aladhan;

public class AladhanData {

    private AladhanDate date;
    private AladhanTimings timings;
    private AladhanMeta meta;

    public AladhanDate getDate() {
        return date;
    }

    public void setDate(AladhanDate date) {
        this.date = date;
    }

    public AladhanTimings getTimings() {
        return timings;
    }

    public void setTimings(AladhanTimings timings) {
        this.timings = timings;
    }

    public AladhanMeta getMeta() {
        return meta;
    }

    public void setMeta(AladhanMeta meta) {
        this.meta = meta;
    }
}
