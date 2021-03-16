package com.hbouzidi.fiveprayers.timings.calculations;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public enum CalculationMethodEnum {
    SHIA_ITHNA_ANSARI(0, "16", "4", "14"),
    UNIVERSITY_OF_ISLAMIC_SCIENCES_KARACHI(1, "18", "null", "18"),
    ISLAMIC_SOCIETY_OF_NORTH_AMERICA(2, "15", "null", "15"),
    MUSLIM_WORLD_LEAGUE(3, "18", "null", "17"),
    UMM_AL_QURA_UNIVERSITY_MAKKAH(4, "18.5", "null", "90 min"),
    EGYPTIAN_GENERAL_AUTHORITY_OF_SURVEY(5, "19.5", "null", "17.5"),
    INSTITUTE_OF_GEOPHYSICS_UNIVERSITY_OF_TEHRAN(7, "17.7", "4.5", "14"),
    GULF_REGION(8, "19.5", "null", "90 min"),
    KUWAIT(9, "18", "null", "17.5"),
    QATAR(10, "18", "null", "90 min"),
    MAJLIS_UGAMA_ISLAM_SINGAPURA(11, "20", "null", "18"),
    UNION_ORGANIZATION_ISLAMIC_DE_FRANCE(12, "12", "null", "12"),
    DIYANET_ISLERI_BASKANLIGI_TURKEY(13, "18", "null", "17"),
    SPIRITUAL_ADMINISTRATION_OF_MUSLIMS_OF_RUSSIA(14, "16", "null", "15"),

    LONDON_UNIFIED_PRAYER_TIMES(99, "18.5", "null", "18"),
    DEPARTMENT_OF_ISLAMIC_ADVANCEMENT_MALAYSIA(99, "20", "null", "18"),
    MOROCCAN_MINISTRY_OF_ISLAMIC_AFFAIRS(99, "19.1", "4 min", "17"),
    ALGERIAN_MINISTRY_OF_RELIGIOUS_AFFAIRS_AND_WAKFS(99, "18", "3 min", "17"),
    TUNISIAN_MINISTRY_OF_RELIGIOUS_AFFAIRS (99, "18", "3 min", "18"),
    MOSQUEE_DE_PARIS_FRANCE(99, "18", "3 min", "18"),
    FRANCE_15(99, "15", "3 min", "15"),
    FRANCE_18(99, "18", "3 min", "18");

    private int methodId;
    private String fajrAngle;
    private String maghribAngle;
    private String ichaAngle;

    CalculationMethodEnum(int methodId, String fajrAngle, String maghribAngle, String ichaAngle) {
        this.methodId = methodId;
        this.fajrAngle = fajrAngle;
        this.maghribAngle = maghribAngle;
        this.ichaAngle = ichaAngle;
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
}
