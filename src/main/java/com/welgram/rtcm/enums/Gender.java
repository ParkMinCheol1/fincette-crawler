package com.welgram.rtcm.enums;

public enum Gender {
    MALE("M", "남성"),
    FEMALE("F", "여성");

    private final String code;
    private final String desc;

    Gender(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }



    public String getCode() {
        return code;
    }



    public String getDesc() {
        return desc;
    }



    public static Gender fromCode(String code) {

        Gender result = null;

        for (Gender gender : Gender.values()) {
            if (gender.code.equalsIgnoreCase(code)) {
                result = gender;
                break;
            }
        }

        return result;
    }
}
