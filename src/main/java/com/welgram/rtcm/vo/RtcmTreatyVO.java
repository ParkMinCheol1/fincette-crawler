package com.welgram.rtcm.vo;

import java.math.BigInteger;
import java.util.Date;

public class RtcmTreatyVO {
    private Integer seq;
    private Integer planSeq;
    private Integer planId;
    private String treatyName;
    private String treatyType;
    private String renewalType;
    private Integer insPeriod;
    private String insType;
    private Integer payPeriod;
    private String payType;
    private BigInteger assureMoney;
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

    public String getTreatyName() {
        return treatyName;
    }

    public void setTreatyName(String treatyName) {
        this.treatyName = treatyName;
    }

    public String getTreatyType() {
        return treatyType;
    }

    public void setTreatyType(String treatyType) {
        this.treatyType = treatyType;
    }

    public String getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(String renewalType) {
        this.renewalType = renewalType;
    }

    public Integer getInsPeriod() {
        return insPeriod;
    }

    public void setInsPeriod(Integer insPeriod) {
        this.insPeriod = insPeriod;
    }

    public String getInsType() {
        return insType;
    }

    public void setInsType(String insType) {
        this.insType = insType;
    }

    public Integer getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(Integer payPeriod) {
        this.payPeriod = payPeriod;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigInteger getAssureMoney() {
        return assureMoney;
    }

    public void setAssureMoney(BigInteger assureMoney) {
        this.assureMoney = assureMoney;
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
