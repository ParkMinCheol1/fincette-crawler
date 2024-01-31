package com.welgram.common.enums;




// 2022.10.21 | 최우진 | 카테고리 관련 기능 추가
// 카테고리별 검사내용 | ex. 연금관련 상품은 연금에 관련해서 검사해야할 내용이 있습니다

public enum Category {

    // 카테고리별 코드값과 봇의 클래스의 미들네임을 갖고 있습니다

    운전자보험          ("CD00376",  "DRV"),
    암보험             ("",""),

    ;

    String code;
    String tag;

    Category(String code, String tag) {
        this.code = code;
        this.tag = tag;
    }

    public String getCode() { return this.code; }
    public String getTag() { return this.tag; }

}
