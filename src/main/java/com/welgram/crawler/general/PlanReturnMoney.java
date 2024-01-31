package com.welgram.crawler.general;

import com.welgram.crawler.SeleniumCrawler;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanReturnMoney {

    public final static Logger logger = LoggerFactory.getLogger(PlanReturnMoney.class);

    private int planId;                    // 가설 아이디
    private int insAge;                    // 보험나이
    private String gender = "";            // 성별
    private int sort;                    // 순번
    private String term = "";            // 납입기간
    private String premiumSum = "";        // 합계 보험료
    private String returnMoney = "";    // 환급금(공시)
    private String returnRate = "";        // 환급률(공시)
    private String returnMoneyMin = "";    // 환급금(최저)
    private String returnRateMin = "";    // 환급률(최저)
    private String returnMoneyAvg = "";    // 환급금(평균)
    private String returnRateAvg = "";    // 환급률(평균)
    private String regTime = "";        // 등록일자
    private double tmpSort;

    public PlanReturnMoney() {
    }

    public PlanReturnMoney(String term) {
        this.term = term;
    }

    public PlanReturnMoney(String term, String premiumSum, String returnMoney, String returnRate) {

        logger.info("|--경과기간: {}", term);
        logger.info("|--납입보험료: {}", premiumSum);
        logger.info("|--해약환급금: {}", returnMoney);
        logger.info("|--환급률: {}", returnRate);

        this.term = term;
        this.premiumSum = premiumSum.replaceAll("[^0-9]", "");
        this.returnMoney = returnMoney.replaceAll("[^0-9]", "");
        this.returnRate = returnRate;
    }

    public PlanReturnMoney(String term, String premiumSum, String returnMoney, String returnRate,
        String returnMoneyMin, String returnRateMin, String returnMoneyAvg, String returnRateAvg) {

        logger.info("|--경과기간: {}", term);
        logger.info("|--납입보험료: {}", premiumSum);
        logger.info("|--해약환급금: {}", returnMoney);
        logger.info("|--환급률: {}", returnRate);
        logger.info("|--최저해약환급금: {}", returnMoneyMin);
        logger.info("|--최저해약환급률: {}", returnRateMin);
        logger.info("|--평균해약환급금: {}", returnMoneyAvg);
        logger.info("|--평균해약환급률: {}", returnRateAvg);

        this.term = term;
        this.premiumSum = premiumSum.replaceAll("[^0-9]", "");
        this.returnMoney = returnMoney.replaceAll("[^0-9]", "");
        this.returnRate = returnRate;
        this.returnMoneyMin = returnMoneyMin.replaceAll("[^0-9]", "");
        this.returnRateMin = returnRateMin;
        this.returnMoneyAvg = returnMoneyAvg.replaceAll("[^0-9]", "");
        this.returnRateAvg = returnRateAvg;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getInsAge() {
        return insAge;
    }

    public void setInsAge(int insAge) {
        this.insAge = insAge;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getPremiumSum() {
        return premiumSum;
    }

    public void setPremiumSum(String premiumSum) {
        this.premiumSum = premiumSum;
    }

    public String getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(String returnMoney) {
        this.returnMoney = returnMoney;
    }

    public String getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(String returnRate) {
        this.returnRate = returnRate;
    }

    public String getReturnMoneyMin() {
        return returnMoneyMin;
    }

    public void setReturnMoneyMin(String returnMoneyMin) {
        this.returnMoneyMin = returnMoneyMin;
    }

    public String getReturnRateMin() {
        return returnRateMin;
    }

    public void setReturnRateMin(String returnRateMin) {
        this.returnRateMin = returnRateMin;
    }

    public String getReturnMoneyAvg() {
        return returnMoneyAvg;
    }

    public void setReturnMoneyAvg(String returnMoneyAvg) {
        this.returnMoneyAvg = returnMoneyAvg;
    }

    public String getReturnRateAvg() {
        return returnRateAvg;
    }

    public void setReturnRateAvg(String returnRateAvg) {
        this.returnRateAvg = returnRateAvg;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public double getTmpSort() {
        return tmpSort;
    }

    public void setTmpSort(double tmpSort) {
        this.tmpSort = tmpSort;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(term).append(premiumSum).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        PlanReturnMoney rhs = (PlanReturnMoney) obj;

        if (obj instanceof PlanReturnMoney) {
            return new EqualsBuilder().append(term, rhs.term).append(premiumSum, rhs.premiumSum).isEquals();
        }

        return false;
    }

    @Override
    public String toString() {
        return "PlanReturnMoney{" +
                "term='" + term + '\'' +
                ", premiumSum='" + premiumSum + '\'' +
                ", returnMoney='" + returnMoney + '\'' +
                ", returnRate='" + returnRate + '\'' +
                ", returnMoneyMin='" + returnMoneyMin + '\'' +
                ", returnRateMin='" + returnRateMin + '\'' +
                ", returnMoneyAvg='" + returnMoneyAvg + '\'' +
                ", returnRateAvg='" + returnRateAvg + '\'' +
                '}' + "\n";
    }
}
