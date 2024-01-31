package com.welgram.rtcm.vo;

import com.welgram.rtcm.cli.RtcmCrawlerCommand;
import com.welgram.rtcm.enums.Gender;
import com.welgram.rtcm.util.Birthday;
import com.welgram.rtcm.util.InsuranceUtil;

public class RtcmCrawlingInfo {
    private String productName;
    private String productCode;
    private String category;
    private String crawlUrl;

    private Birthday birthday;
    private Gender gender;
    private final Birthday babyBirthday = InsuranceUtil.getBirthday(0);
    private final Gender babyGender = Gender.MALE;
    private final String job = "초등학교 교사";
    private final String pregnancyWeek = "12";
    private final String largeAmountContract = "고액적립";
    private final String healthType = "표준체";
    private final String medicalBeneficiary = "일반(비대상)";
    private final String lifeDesignCostAge = "65";
    private final String lifeDesignCostPeriod = "15년";

    private RtcmPlanVO rtcmPlanVO;
    private RtcmCrawlerCommand rtcmCrawlerCommand;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCrawlUrl() {
        return crawlUrl;
    }

    public void setCrawlUrl(String crawlUrl) {
        this.crawlUrl = crawlUrl;
    }

    public Birthday getBirthday() {
        return birthday;
    }

    public void setBirthday(Birthday birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Birthday getBabyBirthday() {
        return babyBirthday;
    }

    public Gender getBabyGender() {
        return babyGender;
    }

    public String getJob() {
        return job;
    }

    public String getPregnancyWeek() {
        return pregnancyWeek;
    }

    public String getLargeAmountContract() {
        return largeAmountContract;
    }

    public String getHealthType() {
        return healthType;
    }

    public String getMedicalBeneficiary() {
        return medicalBeneficiary;
    }

    public String getLifeDesignCostAge() {
        return lifeDesignCostAge;
    }

    public String getLifeDesignCostPeriod() {
        return lifeDesignCostPeriod;
    }

    public RtcmPlanVO getRtcmPlanVO() {
        return rtcmPlanVO;
    }

    public void setRtcmPlanVO(RtcmPlanVO rtcmPlanVO) {
        this.rtcmPlanVO = rtcmPlanVO;
    }

    public RtcmCrawlerCommand getRtcmCrawlerCommand() {
        return rtcmCrawlerCommand;
    }

    public void setRtcmCrawlerCommand(RtcmCrawlerCommand rtcmCrawlerCommand) {
        this.rtcmCrawlerCommand = rtcmCrawlerCommand;
    }
}