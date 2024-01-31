package com.welgram.rtcm.vo;

import java.math.BigInteger;
import java.util.Date;

public class RtcmPremiumVO {
    private Integer seq;
    private Integer planSeq;
    private Integer planId;
    private Integer premium;
    private Integer savePremium;
    private Integer preBirthPremium;
    private Integer afterBirthPremium;
    private BigInteger annuityPremium;
    private BigInteger returnPremium;
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

    public Integer getPremium() {
        return premium;
    }

    public void setPremium(Integer premium) {
        this.premium = premium;
    }

    public Integer getSavePremium() {
        return savePremium;
    }

    public void setSavePremium(Integer savePremium) {
        this.savePremium = savePremium;
    }

    public Integer getPreBirthPremium() {
        return preBirthPremium;
    }

    public void setPreBirthPremium(Integer preBirthPremium) {
        this.preBirthPremium = preBirthPremium;
    }

    public Integer getAfterBirthPremium() {
        return afterBirthPremium;
    }

    public void setAfterBirthPremium(Integer afterBirthPremium) {
        this.afterBirthPremium = afterBirthPremium;
    }

    public BigInteger getAnnuityPremium() {
        return annuityPremium;
    }

    public void setAnnuityPremium(BigInteger annuityPremium) {
        this.annuityPremium = annuityPremium;
    }

    public BigInteger getReturnPremium() {
        return returnPremium;
    }

    public void setReturnPremium(BigInteger returnPremium) {
        this.returnPremium = returnPremium;
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
