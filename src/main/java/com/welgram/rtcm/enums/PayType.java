package com.welgram.rtcm.enums;

/**
 * 납입기간 유형
 */
public enum PayType {
    YEAR("Y", "년"),
    AGE("A", "세"),
    ONCE("S", "일시납");

    private final String code;                  //코드
    private final String desc;                  //설명

    PayType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }



    public String getCode() {
        return code;
    }



    public String getDesc() {
        return desc;
    }



    public static PayType fromCode(String code) {

        PayType result = null;

        for (PayType payType : PayType.values()) {
            if (payType.code.equalsIgnoreCase(code)) {
                result = payType;
                break;
            }
        }

        return result;
    }



    public static PayType fromDesc(String desc) {

        PayType result = null;

        for (PayType payType : PayType.values()) {
            if (payType.desc.equalsIgnoreCase(desc)) {
                result = payType;
                break;
            }
        }

        return result;
    }
}
