package com.bouzidi.prayertimes.timings.aladhan;

public class AladhanTodayTimingsResponse {

    private String code;
    private String status;
    private AladhanData data;

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public AladhanData getData() {
        return data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(AladhanData data) {
        this.data = data;
    }
}