package com.hbouzidi.fiveprayers.calendar;

import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDate;

import java.util.List;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class CalendarAPIResponse {

    private String code;
    private String status;
    private List<AladhanDate> data;

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

    public List<AladhanDate> getData() {
        return data;
    }

    public void setData(List<AladhanDate> data) {
        this.data = data;
    }
}