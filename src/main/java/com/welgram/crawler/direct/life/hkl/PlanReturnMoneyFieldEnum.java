package com.welgram.crawler.direct.life.hkl;

//@Getter
//@RequiredArgsConstructor

public enum PlanReturnMoneyFieldEnum {

    term("경과기간"),
    premiumSum("납입보험료"),
    returnMoney("해약환급금"),
    returnRate("환급률"),
    returnMoneyMin("최저해약환급금"),
    returnRateMin("최저해약환급률"),
    returnMoneyAvg("평균해약환급금"),
    returnRateAvg("평균해약환급률");

    public final String desc;



    PlanReturnMoneyFieldEnum(String desc) {
        this.desc = desc;
    }



    public String getDesc() {
        return desc;
    }
}
