package com.welgram.rtcm.enums;

import lombok.Getter;

@Getter
public enum RenewType {

    RENEWAL("CD00004", "갱신형"),
    NON_RENEWAL("CD00005", "비갱신형");

    private final String code;
    private final String desc;

    RenewType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }



    public static RenewType fromCode(String code) {
        RenewType result = null;

        for (RenewType renewType : RenewType.values()) {
            if (renewType.code.equalsIgnoreCase(code)) {
                result = renewType;
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
