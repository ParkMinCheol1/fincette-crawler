package com.welgram.rtcm.enums;

import lombok.Getter;

@Getter
public enum RefundType {

    PGT("CD00006", "순수보장형"),
    MRT("CD00007", "만기환급형");

    private final String code;
    private final String desc;

    RefundType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }



    public static RefundType fromCode(String code) {
        RefundType result = null;

        for (RefundType refundType : RefundType.values()) {
            if (refundType.code.equalsIgnoreCase(code)) {
                result = refundType;
                break;
            }
        }

        return result;
    }



    public String getCode() {
        return code;
    }



    public String getDesc() {
        return desc;
    }

}
