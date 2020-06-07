package com.bouzidi.prayertimes.timings;

public enum CalculationMethodEnum {
    SHIA_ITHNA_ANSARI(0),
    UNIVERSITY_OF_ISLAMIC_SCIENCES_KARACHI(1),
    ISLAMIC_SOCIETY_OF_NORTH_AMERICA(2),
    MUSLIM_WORLD_LEAGUE(3),
    UMM_AL_QURA_UNIVERSITY_MAKKAH(4),
    EGYPTIAN_GENERAL_AUTHORITY_OF_SURVEY(5),
    INSTITUTE_OF_GEOPHYSICS_UNIVERSITY_OF_TEHRAN(7),
    GULF_REGION(8),
    KUWAIT(9),
    QATAR(10),
    MAJLIS_UGAMA_ISLAM_SINGAPURA(11),
    UNION_ORGANIZATION_ISLAMIC_DE_FRANCE(12),
    DIYANET_ISLERI_BASKANLIGI_TURKEY(13),
    SPIRITUAL_ADMINISTRATION_OF_MUSLIMS_OF_RUSSIA(14);

    private int methodId;

    CalculationMethodEnum(int methodId) {
        this.methodId = methodId;
    }

    public int getValue() {
        return methodId;
    }

    public static CalculationMethodEnum getDefault() {
        return UNION_ORGANIZATION_ISLAMIC_DE_FRANCE;
    }
}
