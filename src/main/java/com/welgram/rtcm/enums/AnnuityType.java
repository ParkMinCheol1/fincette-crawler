package com.welgram.rtcm.enums;

/**
 * 연금수령형태
 */

public enum AnnuityType {

    WHOLE("WHL", "CD00102", "종신연금형"),
    FIXED("FXD", "CD00105", "확정연금형");

    private final String analysisCode;          //분석쪽 코드
    private final String platformCode;          //플랫폼쪽 코드
    private final String desc;                  //설명



    AnnuityType(String analysisCode, String platformCode, String desc) {
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



    public static AnnuityType fromAnalysisCode(String analysisCode) {

        AnnuityType result = null;

        for (AnnuityType annuityType : AnnuityType.values()) {
            if (annuityType.analysisCode.equalsIgnoreCase(analysisCode)) {
                result = annuityType;
                break;
            }
        }

        return result;
    }



    public static AnnuityType fromPlatformCode(String platformCode) {

        AnnuityType result = null;

        for (AnnuityType annuityType : AnnuityType.values()) {
            if (annuityType.platformCode.equalsIgnoreCase(platformCode)) {
                result = annuityType;
                break;
            }
        }

        return result;
    }
}
