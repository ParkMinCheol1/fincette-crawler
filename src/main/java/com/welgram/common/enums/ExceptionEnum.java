package com.welgram.common.enums;


/*
    fincette_crawler의 예외처리와 연관된 Enum입니다
    사용방법 : 미정입니다 수정편하게 하시고 슬랙에 노티만 남겨주세요 (작업중...)
        - SAMPLE 01 : CrawlingSHL, SHL_DTL_F001
*/
public enum ExceptionEnum {

    // 기본 | 공통
    FAIL                                        ("ERR000",     "실패입니다"),
    UNKNOWN                                     ("ERR001",     "알수 없는 오류 입니다"),

    // CrawlingProduct 에러
    ERR_BY_TEXTTYPE                             ("ERR002",      "텍스트타입이 잘못되었습니다"),
    ERR_BY_INSTERM                              ("ERR003",      "보험기간이 잘못되었습니다"),
    ERR_BY_NAPTERM                              ("ERR004",      "납입기간이 잘못되었습니다"),
    ERR_BY_BIRTH                                ("ERR005",      "생년월일이 잘못되었습니다"),
    ERR_BY_ASSUREMONEY                          ("ERR006",      "가입금액이 잘못되었습니다"),
    ERR_BY_SCREENSHOT                           ("ERR007",      "스크린샷이 잘못되었습니다"),
    ERR_BY_PRODUCT_KIND                         ("ERR008",      "환급종류가 잘못되었습니다"),
    ERR_BY_SUB_PLAN                             ("ERR009",      "서브플랜명이 잘못되었습니다"),
    ERR_BY_NAPCYCLE                             ("ERR010",      "납입주기가 잘못되었습니다"),
    ERR_BY_PREMIUM                              ("ERR011",      "보험료가 잘못되었습니다"),
    ERR_BY_JOB                                  ("ERR012",      "직업이 잘못되었습니다"),
    ERR_BY_REFUND_TYPE                          ("ERR013",      "환급형태 설정이 잘못되었습니다"),
    ERR_BY_RENEW_TYPE                           ("ERR014",      "갱신형태 설정이 잘못되었습니다"),
    ERR_BY_RETURN_PREMIUM                       ("ERR015",      "만기환급금 크롤링이 잘못되었습니다"),
    ERR_BY_RETURN_MONEY_LIST                    ("ERR016",      "해약환급금 테이블 에러"),
    ERR_BY_GENDER                               ("ERR017",      "성별이 잘못되었습니다"),
    ERR_BY_ANNUITY_AGE                          ("ERR018",      "연금개시나이가 잘못되었습니다"),
    ERR_BY_PRODUCT_NAME                         ("ERR019",      "상품명이 잘못되었습니다"),
    ERR_BY_TREATY                               ("ERR020",      "없는 특약이거나 특약 선택이 잘못되었습니다"),
    ERR_BY_MISMATCH_VALUE                       ("ERR021",      "기대값과 실제값이 불일치합니다"),
    ERR_BY_ANNUITY_TYPE                         ("ERR022",      "연금수령형태(종신 or 확정 등)가 잘못되었습니다"),
    ERR_BY_PREVALENCE_TYPE                      ("ERR023",      "심사유형(간편심사/일반심사)이 잘못되었습니다"),
    ERR_BY_ANNUITY_RECEIVE_CYCLE                ("ERR024",      "연금수령방법(=연금수령주기 | 매월 or 매년 등)이 잘못되었습니다"),
    ERR_BY_ANNUITY_GIVE_TYPE                    ("ERR025",      "연금지급형태(균등설계형 등)가 잘못되었습니다"),
    ERR_BY_ANNUITY_RECEIVE_PERIOD               ("ERR026",      "연금지급기간(연금수령형태의 년수)가 잘못되었습니다"),

    //=============== 새롭게 추가 ========================================
    ERROR_BY_USER_NAME                          ("ERR030", "고객명 관련 에러입니다."),
    ERROR_BY_DUEDATE                            ("ERR031", "출생예정일 관련 에러입니다."),
    ERROR_BY_RENEW_CYCLE                        ("ERR032", "갱신주기 관련 에러입니다."),
    ERROR_BY_PRODUCT_CATEGORY                   ("ERR033", "상품 카테고리 관련 에러입니다."),
    ERROR_BY_PLAN_NAME                          ("ERR034", "플랜명 관련 에러입니다."),
    ERROR_BY_VEHICLE_CNT                        ("ERR035", "차량가입대수 관련 에러입니다."),
    ERROR_BY_ONE_TIME_PREMIUM                   ("ERR036", "1회보험료 관련 에러입니다."),
    ERROR_BY_SMOKE                              ("ERR037", "흡연여부 관련 에러입니다."),
    ERROR_BY_NATIONAL_HEALTH_INSURANCE          ("ERR038", "국민건강보험 가입여부 관련 에러입니다."),
    ERROR_BY_MEDICAL_BENEFICIARY                ("ERR039", "의료수급권자여부 관련 에러입니다."),
    ERROR_BY_TRAVEL_PERIOD                      ("ERR040", "여행일 관련 에러입니다."),
    ERROR_BY_JOIN_TYPE                          ("ERR041", "가입형태(본인/배우자 및 부모 등등) 관련 에러입니다."),
    ERROR_BY_TRAVEL_REGION                      ("ERR042", "여행지역 관련 에러입니다."),
    ERROR_BY_GURANTEE_TYPE                      ("ERR043", "보장유형(일반형/체증형 등) 관련 에러입니다."),
    ERROR_BY_TRAVEL_GOAL                        ("ERR044", "여행목적(여행/관광 | 업무/출장 | 유학 등) 관련 에러입니다."),
    ERROR_BY_CHILD_TYPE                         ("ERR045", "자녀정보(영유아 | 초등학생 등) 관련 에러입니다."),
    ERROR_BY_TRAVEL_TYPE                        ("ERR046", "여행유형(개인여행 | 동반여행 등) 관련 에러입니다."),
    ERROR_BY_FOREIGN_TYPE                       ("ERR047", "외국인유형(내국인 | 외국인 등) 관련 에러입니다."),
    ERROR_BY_WORKER_INSURANCE_JOIN              ("ERR048", "산재보험 가입유무 관련 에러입니다."),
    ERROR_BY_VEHICLE_INJURY_LEVEL               ("ERR049", "교통상해급수 관련 에러입니다."),
    ERROR_BY_TREATY_ATTRIBUTE                   ("ERR050", "담보 속성 관련 에러입니다."),
    ERROR_BY_MULTI_CHILD_DISCOUNT               ("ERR051", "다자녀 할인 관련 에러입니다."),
    ERROR_BY_PAYMENT_EXEMPTION                  ("ERR052", "보험료 납입면제 관련 에러입니다."),
    ERROR_BY_LARGE_AMOUNT_CONTRACT              ("ERR053", "고액계약 관련 에러입니다."),
    ERROR_BY_ADDITIONAL_TREATY                  ("ERR054", "부가특약 관련 에러입니다."),
    ERROR_BY_LIVING_MONEY_AGE                   ("ERR055", "생활자금 개시나이 관련 에러입니다."),
    ERROR_BY_LIVING_MONEY_PAYMENT_PERIOD        ("ERR056", "생활자금 지급기간 관련 에러입니다."),
    ERROR_BY_HEALTH_GRADE                       ("ERR057", "건강등급 관련 에러입니다."),
    ERROR_BY_AFTER_CONVERSION_INSTERM           ("ERR058", "전환후 보험기간 관련 에러입니다."),


    // =================================================================
    // 엘리먼트 에러(lv:1000)
    ERR_BY_ELEMENT                              ("ERR1001",    "엘리멘트 요류입니다."),
    ERR_BY_BUTTON                               ("ERR1002",    "버튼 오류입니다."),
    ERR_BY_XPATH                                ("ERR1008",    "잘못된 XPATH설정입니다"),

    // 핀셋크롤러 사용자 정보 입력 오류 (lv:100)
    ERROR_BY_USER_INFO                          ("ERR101",          "사용자 정보의 입력이 잘못되었습니다"),
    INVALID_GENDER                              ("ERR102",          "적합한 '성(性)'이 아닙니다"),
    INVALID_AGE                                 ("ERR103",          "적합한 '나이'가 아닙니다"),
    INVALID_JOB                                 ("ERR104",          "적합한 '직업'이 아닙니다"),
    INVALID_BIRTHDAY                            ("ERR105",          "적합한 생년월일이 아닙니다"),
    ERR_BY_VEHICLE                              ("ERR106",         "차량용도 관련 에러"),
    ERR_BY_PRODUCT_TYPE                         ("ERR107",         "상품 종구분 관련 에러"),
    ERR_BY_INJURY_LEVEL                         ("ERR108",         "상해급수 관련 에러"),

    // 주계약 가입조건 입력오류 (lv:200)
    ERROR_BY_MAIN_TERATY                        ("ERR201",          "주계약 가입조건의 입력이 잘못되었습니다"),
    ERROR_BY_SUB_TERATY                         ("ERR202",          "선택계약 가입조건의 입력이 잘못되었습니다"),

    // 특약 구성 선택오류 (lv:300)
    ERROR_BY_TREATIES_GETTING                   ("ERR300",          "원수사 특약정보 획득 중 문제가 발생했습니다"),
    ERROR_BY_TREATIES_COMPOSIOTION              ("ERR301",          "특약의 구성이 잘못되었습니다"),
    ERROR_BY_CRAWL_TREATIES                     ("ERR302",          "특약 크롤링 관련 에러"),

    // 보험료 스크래핑 오류 (lv:400)
    ERROR_BY_SCRAPING_MONTHLY_PREMIUM           ("ERR401",          "보험료를 스크래핑하던 중 에러가 발생하였습니다"),

    // 해약환급금 스크래핑 오류 (lv:500)
    ERROR_BY_SCRAPIN_RETURN_MONEY               ("ERR501",          "환급금을 스크래핑하던 중 에러가 발생하였습니다"),
    ERROR_BY_SCRAPING_ANNUITY_MONEY               ("ERR502",          "연금수령액을 스크래핑하던 중 에러가 발생하였습니다"),

    // =================================        ================================
// todo | test용 회사별 전용 에러코드 SHL
// todo | 혹은 스크립트 작성자 전용 에러코드 필요시...
    ERROR_BY_SHL                                ("ERR901",          "SHL | 에러입니다"),
    ERROR_BY_SHL_SEARCH_BOX                     ("ERR902",          "SHL | 공시실 메인 검색창 에러입니다"),
    ERROR_BY_SHL_ALERT                          ("ERR903",          "SHL | 알수없는 ALERT 발생"),
    ERROR_BY_SHL_INPUT_MAIN_TRT                 ("ERR904",          "SHL | 주계약 계산중 에러가 발생하였습니다"),
    ERROR_BY_SHL_CCR_F004                       ("ERR905",          "SHL | SHL_CCR_F004의 알수없는 에러..."),

    ;

    private final String code;
    private final String msg;

    ExceptionEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
