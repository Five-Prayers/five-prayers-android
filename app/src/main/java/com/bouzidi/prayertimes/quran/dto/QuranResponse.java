package com.bouzidi.prayertimes.quran.dto;

public class QuranResponse {

    private String code;
    private String status;
    private QuranData data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public QuranData getData() {
        return data;
    }

    public void setData(QuranData data) {
        this.data = data;
    }
}
