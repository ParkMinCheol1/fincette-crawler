package com.welgram.rtcm.enums;

public enum ErrorCode {

    //크롤링 시 발생하는 기능 단위 에러코드
    //TODO 더 세부적인 에러코드 정의 필요
    ERROR_BY_PRODUCT_NAME(500, "공시실 상품명 관련 에러입니다."),
    ERROR_BY_BIRTH(500, "본인 생년월일 관련 에러입니다."),
    ERROR_BY_GENDER(500, "본인 성별 관련 에러입니다."),
    ERROR_BY_JOB(500, "직업 관련 에러입니다."),
    ERROR_BY_BABY_BIRTH(500, "태아 생년월일 관련 에러입니다."),
    ERROR_BY_BABY_GENDER(500, "태아 성별 관련 에러입니다."),
    ERROR_BY_HEALTH_TYPE(500, "주피건강체 관련 에러입니다."),
    ERROR_BY_MEDICAL_BENEFICIARY(500, "의료수급권자 관련 에러입니다."),
    ERROR_BY_PREGNANCY_WEEK(500, "임신주수 관련 에러입니다."),
    ERROR_BY_LARGE_AMOUNT_CONTRACT(500, "고액계약 관련 에러입니다."),
    ERROR_BY_LIFE_DESIGN_COST_AGE(500, "생애설계자금 나이 관련 에러입니다."),
    ERROR_BY_LIFE_DESIGN_COST_PERIOD(500, "생애설계자금 지급기간 관련 에러입니다."),
    ERROR_BY_PRODUCT_KIND(500, "보험종류(종형) 관련 에러입니다."),
    ERROR_BY_INS_TERM(500, "주계약 보험기간 관련 에러입니다."),
    ERROR_BY_NAP_TERM(500, "주계약 납입기간 관련 에러입니다."),
    ERROR_BY_PAY_CYCLE(500, "주계약 납입주기 관련 에러입니다."),
    ERROR_BY_ASSURE_MONEY(500, "주계약 가입금액 관련 에러입니다."),
    ERROR_BY_SUB_TREATY_INS_TERM(500, "선택특약 보험기간 관련 에러입니다."),
    ERROR_BY_SUB_TREATY_NAP_TERM(500, "선택특약 납입기간 관련 에러입니다."),
    ERROR_BY_SUB_TREATY_ASSURE_MONEY(500, "선택특약 가입금액 관련 에러입니다."),
    ERROR_BY_ANNUITY_AGE(500, "연금개시나이 관련 에러입니다."),
    ERROR_BY_ANNUITY_TYPE(500, "연금형태 관련 에러입니다."),
    ERROR_BY_JOIN_TARGET(500, "가입대상 관련 에러입니다."),
    ERROR_BY_ALERT(500, "삼성생명 알럿창 발생."),

    ERROR_BY_MAIN_PLAN(500, "가설에 주계약이 존재하지 않습니다."),
    ERROR_BY_CRAWL_PREMIUM(500, "보험료 크롤링 관련 에러입니다."),
    ERROR_BY_CRAWL_RETURN_PREMIUM(500, "해약환급금 크롤링 관련 에러입니다."),
    ERROR_BY_CRAWL_ANNUITY_PREMIUM(500, "연금수령액 크롤링 관련 에러입니다."),
    ERROR_BY_TREATY(500, "선택특약 세팅 에러입니다.");

    private final int status;
    private final String message;



    public int getStatus() {
        return status;
    }



    public String getMessage() {
        return message;
    }



    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
