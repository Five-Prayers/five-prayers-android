package com.bouzidi.prayertimes.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UiUtilsTest {

    @Test
    public void formatTimeForTimer() {
        String str = UiUtils.formatTimeForTimer(21767000);
        assertEquals("- 06:02:47", str);
    }

    @Test
    public void formatDate() {
        String str = UiUtils.formatHijriDate(10, "Safar", 1415);
        assertEquals("10 Safar, 1415", str);
    }
}