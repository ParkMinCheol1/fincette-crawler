package com.welgram.rtcm.enums;

/**
 * 납입주기
 */
public enum PayCycle {
    MONTH("M", "CD00008", "월납"),
    YEAR("Y", "CD00009", "연납"),
    ONCE("S", "CD00072", "일시납");

    private final String analysisCode;          //분석쪽 코드
    private final String platformCode;          //플랫폼쪽 코드
    private final String desc;                  //설명

    PayCycle(String analysisCode, String platformCode, String desc) {

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



    public static PayCycle fromAnalysisCode(String analysisCode) {

        PayCycle result = null;

        for(PayCycle payCycle : PayCycle.values()) {
            if(payCycle.analysisCode.equalsIgnoreCase(analysisCode)) {
                result = payCycle;
                break;
            }
        }

        return result;
    }



    public static PayCycle fromPlatformCode(String platformCode) {

        PayCycle result = null;

        for(PayCycle payCycle : PayCycle.values()) {
            if(payCycle.platformCode.equalsIgnoreCase(platformCode)) {
                result = payCycle;
                break;
            }
        }

        return result;
    }
}
