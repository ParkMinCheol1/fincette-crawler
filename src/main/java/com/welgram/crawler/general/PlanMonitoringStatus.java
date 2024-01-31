package com.welgram.crawler.general;

import com.welgram.crawler.Crawler;
import java.math.BigInteger;
import java.sql.Timestamp;

public class PlanMonitoringStatus {
    private final Integer planId;                         //모니터링 대상이 된 가설ID
    private final String gender;                          //모니터링을 돌린 시점의 성별
    private final Integer insAge;                         //모니터링을 돌린 시점의 나이
    private final String productId;                       //모니터링의 대상이 된 가설의 상품ID
    private String siteStatus;                      //모니터링 대상이 된 가설의 사이트 정상여부. 사이트 정상(Y) | 사이트 비정상(N)
    private String insMoneyStatus;                  //모니터링 대상이 된 가설의 보험료 변동여부. 보험료 변동(Y) | 보험료 변동없음(N)
    private String returnMoneyStatus;               //모니터링 대상이 된 가설의 중도 해약환급금 변동여부. 중도 해약환급금 변동(Y) | 중도 해약환급금 변동없음(N)
    private String expMoneyStatus;                  //모니터링 대상이 된 가설의 만기환급금 변동여부. 만기환급금 변동(Y) | 만기환급금 변동없음(N)
    private String annMoneyStatus;                  //모니터링 대상이 된 가설의 연금수령액 변동여부. 연금수령액 변동(Y) | 연금수령액 변동없음(N)
    private Integer recentlyInsMoney;               //모니터링시 크롤링해온 최근 보험료
    private BigInteger recentlyExpMoney;            //모니터링시 크롤링해온 최근 만기환급금
    private BigInteger recentlyAnnMoney;            //모니터링시 크롤링해온 최근 연금수령액
    private Timestamp regTime;                      //모니터링 대상이 된 가설이 최초로 DB에 등록된 시간
    private Timestamp modTime;                      //모니터링 대상이 된 가설이 DB에서 수정된 시간
    private BigInteger jobId;                       //모니터링 가설이 crawling_job 테이블에 등록됐을 때의 작업ID(jobId). 모니터링이 끝나면 0으로 리셋됨
    private BigInteger recentlyJobId;               //모니터링 가설의 최근 crawling jobId
    private String exceptionInfo;                   //모니터링 실패시 오류 정보

    public PlanMonitoringStatus(CrawlingProduct info) {
        this.planId = Integer.parseInt(info.planId);
        this.insAge = Integer.parseInt(info.age);
        this.gender = (info.gender == Crawler.MALE) ? "M" : "F";
        this.productId = info.getProductCode();
    }

    public Integer getPlanId() {
        return planId;
    }

    public String getGender() {
        return gender;
    }

    public Integer getInsAge() {
        return insAge;
    }

    public String getProductId() {
        return productId;
    }

    public String getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(String siteStatus) {
        this.siteStatus = siteStatus;
    }

    public String getInsMoneyStatus() {
        return insMoneyStatus;
    }

    public void setInsMoneyStatus(String insMoneyStatus) {
        this.insMoneyStatus = insMoneyStatus;
    }

    public String getReturnMoneyStatus() {
        return returnMoneyStatus;
    }

    public void setReturnMoneyStatus(String returnMoneyStatus) {
        this.returnMoneyStatus = returnMoneyStatus;
    }

    public String getAnnMoneyStatus() {
        return annMoneyStatus;
    }

    public void setAnnMoneyStatus(String annMoneyStatus) {
        this.annMoneyStatus = annMoneyStatus;
    }

    public Integer getRecentlyInsMoney() {
        return recentlyInsMoney;
    }

    public void setRecentlyInsMoney(Integer recentlyInsMoney) {
        this.recentlyInsMoney = recentlyInsMoney;
    }

    public BigInteger getRecentlyExpMoney() {
        return recentlyExpMoney;
    }

    public void setRecentlyExpMoney(BigInteger recentlyExpMoney) {
        this.recentlyExpMoney = recentlyExpMoney;
    }

    public BigInteger getRecentlyAnnMoney() {
        return recentlyAnnMoney;
    }

    public void setRecentlyAnnMoney(BigInteger recentlyAnnMoney) {
        this.recentlyAnnMoney = recentlyAnnMoney;
    }

    public Timestamp getRegTime() {
        return regTime;
    }

    public void setRegTime(Timestamp regTime) {
        this.regTime = regTime;
    }

    public Timestamp getModTime() {
        return modTime;
    }

    public void setModTime(Timestamp modTime) {
        this.modTime = modTime;
    }

    public BigInteger getJobId() {
        return jobId;
    }

    public void setJobId(BigInteger jobId) {
        this.jobId = jobId;
    }


    public BigInteger getRecentlyJobId() {
        return recentlyJobId;
    }

    public void setRecentlyJobId(BigInteger recentlyJobId) {
        this.recentlyJobId = recentlyJobId;
    }

    public String getExpMoneyStatus() {
        return expMoneyStatus;
    }

    public void setExpMoneyStatus(String expMoneyStatus) {
        this.expMoneyStatus = expMoneyStatus;
    }

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    @Override
    public String toString() {
        return "PlanMonitoringStatus{" +
            "planId=" + planId +
            ", gender='" + gender + '\'' +
            ", insAge=" + insAge +
            ", productId='" + productId + '\'' +
            ", siteStatus='" + siteStatus + '\'' +
            ", insMoneyStatus='" + insMoneyStatus + '\'' +
            ", returnMoneyStatus='" + returnMoneyStatus + '\'' +
            ", expMoneyStatus='" + expMoneyStatus + '\'' +
            ", annMoneyStatus='" + annMoneyStatus + '\'' +
            ", recentlyInsMoney=" + recentlyInsMoney +
            ", recentlyExpMoney=" + recentlyExpMoney +
            ", recentlyAnnMoney=" + recentlyAnnMoney +
            ", regTime=" + regTime +
            ", modTime=" + modTime +
            ", jobId=" + jobId +
            ", recentlyJobId=" + recentlyJobId +
            ", exceptionInfo=" + exceptionInfo +
            '}';
    }


}