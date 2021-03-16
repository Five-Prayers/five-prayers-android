package com.hbouzidi.fiveprayers.timings.aladhan;

import java.util.List;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class AladhanCalendarResponse {

    private String code;
    private String status;
    private List<AladhanData> data;

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AladhanData> getData() {
        return data;
    }

    public void setData(List<AladhanData> data) {
        this.data = data;
    }
}