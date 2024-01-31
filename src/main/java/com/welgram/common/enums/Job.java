package com.welgram.common.enums;



public enum Job {

    // todo | 분류에 의한 직업선택시 사용 합니다
    //      | 필요한 경우 아래 직업분류에 추가해주세요

    //                  div1                        div2                            div3
    DIVISION_CODE       ("대분류",     "중분류",          "소분류"),

    MANAGER             ("관리자",        "경영지원",         "경영지원 사무직 관리자"),
    TEACHER             ("교사",          "초등학교",         "초등학교 교사"),
    CHILD               ("미취학아동",     "미취학아동",       "미취학아동"),
    ;

    private String mainCategory;
    private String detailCategory;
    private String codeValue;

    Job(String mainCategory, String detailCategory, String codeValue) {
        this.mainCategory = mainCategory;
        this.detailCategory = detailCategory;
        this.codeValue = codeValue;
    }

    public String getMainCategory() {
        return mainCategory;
    }
    public String getDetailCategory() {
        return detailCategory;
    }
    public String getCodeValue() { return codeValue; }

    // tester
//    public static void main(String[] args) {
//        System.out.println(MANAGER.getMainCategory());
//        System.out.println(MANAGER.getDetailCategory());
//        System.out.println(MANAGER.getCodeValue());
//
//        System.out.println(Job.TEACHER.getCodeValue());
//    }
}
