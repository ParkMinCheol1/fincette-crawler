package com.welgram.rtcm.vo;

import java.math.BigInteger;
import java.util.Date;

public class RtcmReturnVO {
    private Integer seq;
    private Integer planSeq;
    private Integer planId;
    private String term;
    private BigInteger premiumSum;
    private BigInteger returnPremium;
    private String returnRate;
    private BigInteger returnPremiumMin;
    private String returnRateMin;
    private BigInteger returnPremiumAvg;
    private String returnRateAvg;
    private Date modTime;
    private Date regTime;

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getPlanSeq() {
        return planSeq;
    }

    public void setPlanSeq(Integer planSeq) {
        this.planSeq = planSeq;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public BigInteger getPremiumSum() {
        return premiumSum;
    }

    public void setPremiumSum(BigInteger premiumSum) {
        this.premiumSum = premiumSum;
    }

    public BigInteger getReturnPremium() {
        return returnPremium;
    }

    public void setReturnPremium(BigInteger returnPremium) {
        this.returnPremium = returnPremium;
    }

    public String getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(String returnRate) {
        this.returnRate = returnRate;
    }

    public BigInteger getReturnPremiumMin() {
        return returnPremiumMin;
    }

    public void setReturnPremiumMin(BigInteger returnPremiumMin) {
        this.returnPremiumMin = returnPremiumMin;
    }

    public String getReturnRateMin() {
        return returnRateMin;
    }

    public void setReturnRateMin(String returnRateMin) {
        this.returnRateMin = returnRateMin;
    }

    public BigInteger getReturnPremiumAvg() {
        return returnPremiumAvg;
    }

    public void setReturnPremiumAvg(BigInteger returnPremiumAvg) {
        this.returnPremiumAvg = returnPremiumAvg;
    }

    public String getReturnRateAvg() {
        return returnRateAvg;
    }

    public void setReturnRateAvg(String returnRateAvg) {
        this.returnRateAvg = returnRateAvg;
    }

    public Date getModTime() {
        return modTime;
    }

    public void setModTime(Date modTime) {
        this.modTime = modTime;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }
}
