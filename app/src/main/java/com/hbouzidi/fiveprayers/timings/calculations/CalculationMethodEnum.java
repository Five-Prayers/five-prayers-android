package com.hbouzidi.fiveprayers.timings.calculations;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public enum CalculationMethodEnum {
    SHIA_ITHNA_ANSARI(0, "16", "4", "14", false),
    UNIVERSITY_OF_ISLAMIC_SCIENCES_KARACHI(1, "18", "null", "18", false),
    ISLAMIC_SOCIETY_OF_NORTH_AMERICA(2, "15", "null", "15", false),
    MUSLIM_WORLD_LEAGUE(3, "18", "null", "17", false),
    UMM_AL_QURA_UNIVERSITY_MAKKAH(4, "18.5", "null", "90 min", true),
    EGYPTIAN_GENERAL_AUTHORITY_OF_SURVEY(5, "19.5", "null", "17.5", false),
    INSTITUTE_OF_GEOPHYSICS_UNIVERSITY_OF_TEHRAN(7, "17.7", "4.5", "14", false),
    GULF_REGION(8, "19.5", "null", "90 min", true),
    KUWAIT(9, "18", "null", "17.5", false),
    QATAR(10, "18", "null", "90 min", true),
    MAJLIS_UGAMA_ISLAM_SINGAPURA(11, "20", "null", "18", false),
    UNION_ORGANIZATION_ISLAMIC_DE_FRANCE(12, "12", "null", "12", false),
    DIYANET_ISLERI_BASKANLIGI_TURKEY(13, "18", "null", "17", false),
    SPIRITUAL_ADMINISTRATION_OF_MUSLIMS_OF_RUSSIA(14, "16", "null", "15", false),

    LONDON_UNIFIED_PRAYER_TIMES(99, "18", "null", "17", false),
    DEPARTMENT_OF_ISLAMIC_ADVANCEMENT_MALAYSIA(99, "20", "null", "18", false),
    KEMENTERIAN_AGAMA_RI_INDONESIA(99, "20", "null", "18", false),
    MOROCCAN_MINISTRY_OF_ISLAMIC_AFFAIRS(99, "19.1", "4 min", "17", false),
    ALGERIAN_MINISTRY_OF_RELIGIOUS_AFFAIRS_AND_WAKFS(99, "18", "3 min", "17", false),
    TUNISIAN_MINISTRY_OF_RELIGIOUS_AFFAIRS(99, "18", "3 min", "18", false),
    MOSQUEE_DE_PARIS_FRANCE(99, "18", "3 min", "18", false),
    FRANCE_15(99, "15", "3 min", "15", false),
    FRANCE_18(99, "18", "3 min", "18", false);

    private final int methodId;
    private final String fajrAngle;
    private final String maghribAngle;
    private final String ichaAngle;
    private final boolean isIchaAngleInminute;

    CalculationMethodEnum(int methodId, String fajrAngle, String maghribAngle, String ichaAngle, boolean isIchaAngleInminute) {
        this.methodId = methodId;
        this.fajrAngle = fajrAngle;
        this.maghribAngle = maghribAngle;
        this.ichaAngle = ichaAngle;
        this.isIchaAngleInminute = isIchaAngleInminute;
    }

    public int getMethodId() {
        return methodId;
    }

    public String getFajrAngle() {
        return fajrAngle;
    }

    public String getIchaAngle() {
        return ichaAngle;
    }

    public String getMaghribAngle() {
        return maghribAngle;
    }

    public static CalculationMethodEnum getDefault() {
        return MUSLIM_WORLD_LEAGUE;
    }

    public boolean isIchaAngleInMinute() {
        return isIchaAngleInminute;
    }
}
