package com.welgram.rtcm.enums;

/**
 * 특약유형(주계약/선택특약)
 */

public enum TreatyType {
    MAIN("MAIN", "CD00001", "주계약"),
    SUB("SUB", "CD00003", "선택특약");

    private final String analysisCode;          //분석쪽 코드
    private final String platformCode;          //플랫폼쪽 코드
    private final String desc;                  //설명

    TreatyType(String analysisCode, String platformCode, String desc) {
        this.analysisCode = analysisCode;
        this.platformCode = platformCode;
        this.desc = desc;
    }



    public String getAnalysisCode() {
        return analysisCode;
    }



    public String getPlatformCode() {
        return platformCode;
    }



    public String getDesc() {
        return desc;
    }



    public static TreatyType fromAnalysisCode(String analysisCode) {

        TreatyType result = null;

        for (TreatyType treatyType : TreatyType.values()) {
            if (treatyType.analysisCode.equalsIgnoreCase(analysisCode)) {
                result = treatyType;
                break;
            }
        }

        return result;
    }



    public static TreatyType fromPlatformCode(String platformCode) {

        TreatyType result = null;

        for (TreatyType treatyType : TreatyType.values()) {
            if (treatyType.platformCode.equalsIgnoreCase(platformCode)) {
                result = treatyType;
                break;
            }
        }

        return result;
    }
}
