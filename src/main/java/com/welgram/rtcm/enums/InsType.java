package com.welgram.rtcm.enums;

/**
 * 보험기간 유형
 */
public enum InsType {

    YEAR("Y", "년"),
    AGE("A", "세"),
    DAY("D", "일");

    private final String code;                  //코드
    private final String desc;                  //설명

    InsType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }



    public String getDesc() {
        return desc;
    }



    public static InsType fromCode(String code) {

        InsType result = null;

        for(InsType insType : InsType.values()) {
            if(insType.code.equalsIgnoreCase(code)) {
                result = insType;
                break;
            }
        }

        return result;
    }



    public static InsType fromDesc(String desc) {

        InsType result = null;

        for (InsType insType : InsType.values()) {
            if (insType.desc.equalsIgnoreCase(desc)) {
                result = insType;
                break;
            }
        }

        return result;
    }
}
