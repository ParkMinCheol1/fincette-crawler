package com.welgram.rtcm.enums;

/**
 * 카테고리
 */
public enum Category {

    ACD("ACD", "CD00066", "상해보험"),
    AMD("AMD", "CD00919", "유병자실손보험"),
    ANT("ANT", "CD00081", "연금보험"),
    ASV("ASV", "CD00078", "연금저축보험"),
    BAB("BAB", "CD00382", "태아보험"),
    CCR("CCR", "CD00042", "암보험"),
    CHL("CHL", "CD00085", "어린이보험"),
    DMN("DMN", "CD00566", "치매보험"),
    DMT("DMT", "CD00375", "국내여행보험"),
    DRV("DRV", "CD00376", "운전자보험"),
    DSS("DSS", "CD00142", "질병보험"),
    DTL("DTL", "CD00383", "치아보험"),
    MDC("MDC", "CD00043", "실손보험"),
    MZI("MZI", "CD01006", "MZ보험"),
    OST("OST", "CD00549", "해외여행보험"),
    SAV("SAV", "CD00073", "저축보험"),
    TRM("TRM", "CD00041", "정기보험"),
    WLF("WLF", "CD00065", "종신보험");

    private final String analysisCode;          // 분석쪽 코드
    private final String platformCode;          // 플랫폼쪽 코드
    private final String desc;                  // 설명



    Category(String analysisCode, String platformCode, String desc) {

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



    public static Category fromAnalysisCode(String analysisCode) {

        Category result = null;

        for(Category category : Category.values()) {
            if(category.analysisCode.equalsIgnoreCase(analysisCode)) {
                result = category;
                break;
            }
        }

        return result;
    }



    public static Category fromPlatformCode(String platformCode) {

        Category result = null;

        for(Category category : Category.values()) {
            if(category.platformCode.equalsIgnoreCase(platformCode)) {
                result = category;
                break;
            }
        }

        return result;
    }
}
