package com.welgram.crawler.general;

// 해약환급금 스크랩시 PlanReturnMoney class 스크래핑할 변수에 해당하는 td 순서를 명시함
public class PlanReturnMoneyTdIdx {

    private int term = -1;               // 납입기간
    private int premiumSum = -1;        // 합계 보험료
    private int returnMoney = -1;       // 환급금(공시)
    private int returnRate = -1;        // 환급률(공시)
    private int returnMoneyMin = -1;    // 환급금(최저)
    private int returnRateMin = -1;    // 환급률(최저)
    private int returnMoneyAvg = -1;    // 환급금(평균)
    private int returnRateAvg = -1;    // 환급률(평균)

    public PlanReturnMoneyTdIdx(int term, int premiumSum, int returnMoney, int returnRate) {
        this.term = term;
        this.premiumSum = premiumSum;
        this.returnMoney = returnMoney;
        this.returnRate = returnRate;
    }

    public PlanReturnMoneyTdIdx(int term, int premiumSum, int returnMoney, int returnRate,
        int returnMoneyMin, int returnRateMin, int returnMoneyAvg, int returnRateAvg) {
        this.term = term;
        this.premiumSum = premiumSum;
        this.returnMoney = returnMoney;
        this.returnRate = returnRate;
        this.returnMoneyMin = returnMoneyMin;
        this.returnRateMin = returnRateMin;
        this.returnMoneyAvg = returnMoneyAvg;
        this.returnRateAvg = returnRateAvg;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getPremiumSum() {
        return premiumSum;
    }

    public void setPremiumSum(int premiumSum) {
        this.premiumSum = premiumSum;
    }

    public int getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(int returnMoney) {
        this.returnMoney = returnMoney;
    }

    public int getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(int returnRate) {
        this.returnRate = returnRate;
    }

    public int getReturnMoneyMin() {
        return returnMoneyMin;
    }

    public void setReturnMoneyMin(int returnMoneyMin) {
        this.returnMoneyMin = returnMoneyMin;
    }

    public int getReturnRateMin() {
        return returnRateMin;
    }

    public void setReturnRateMin(int returnRateMin) {
        this.returnRateMin = returnRateMin;
    }

    public int getReturnMoneyAvg() {
        return returnMoneyAvg;
    }

    public void setReturnMoneyAvg(int returnMoneyAvg) {
        this.returnMoneyAvg = returnMoneyAvg;
    }

    public int getReturnRateAvg() {
        return returnRateAvg;
    }

    public void setReturnRateAvg(int returnRateAvg) {
        this.returnRateAvg = returnRateAvg;
    }
}
