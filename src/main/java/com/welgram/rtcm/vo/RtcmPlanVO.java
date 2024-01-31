package com.welgram.rtcm.vo;

import com.welgram.rtcm.enums.ErrorCode;
import com.welgram.rtcm.enums.TreatyType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RtcmPlanVO {
    private Integer planSeq;
    private Integer planId;
    private Integer requestSeq;
    private String productCode;
    private Integer age;
    private String gender;
    private String productName;
    private String siteUrl;
    private String categoryCode;                //TODO enum으로 변환하기
    private String textType;
    private String[] textTypeArr;
    private String returnType;                 //TODO enum으로 변환하기
    private String payCycle;                   //TODO enum으로 변환하기
    private Integer annuityAge;
    private String annuityType;                //TODO enum으로 변환하기
    private Integer annuityGuaranteePeriod;
    private Date modTime;
    private Date regTime;

    private RtcmPremiumVO rtcmPremiumVO = new RtcmPremiumVO();
    private List<RtcmReturnVO> rtcmReturnVOList = new ArrayList<>();
    private List<RtcmTreatyVO> rtcmTreatyVOList = new ArrayList<>();

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

    public Integer getRequestSeq() {
        return requestSeq;
    }

    public void setRequestSeq(Integer requestSeq) {
        this.requestSeq = requestSeq;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    public String[] getTextTypeArr() {
        return this.textType.split("#");
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getPayCycle() {
        return payCycle;
    }

    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    public Integer getAnnuityAge() {
        return annuityAge;
    }

    public void setAnnuityAge(Integer annuityAge) {
        this.annuityAge = annuityAge;
    }

    public String getAnnuityType() {
        return annuityType;
    }

    public void setAnnuityType(String annuityType) {
        this.annuityType = annuityType;
    }

    public Integer getAnnuityGuaranteePeriod() {
        return annuityGuaranteePeriod;
    }

    public void setAnnuityGuaranteePeriod(Integer annuityGuaranteePeriod) {
        this.annuityGuaranteePeriod = annuityGuaranteePeriod;
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

    public RtcmPremiumVO getRtcmPremiumVO() {
        return rtcmPremiumVO;
    }

    public void setRtcmPremiumVO(RtcmPremiumVO rtcmPremiumVO) {
        this.rtcmPremiumVO = rtcmPremiumVO;
    }

    public List<RtcmReturnVO> getRtcmReturnVOList() {
        return rtcmReturnVOList;
    }

    public void setRtcmReturnVOList(List<RtcmReturnVO> rtcmReturnVOList) {
        this.rtcmReturnVOList = rtcmReturnVOList;
    }

    public List<RtcmTreatyVO> getRtcmTreatyVOList() {
        return rtcmTreatyVOList;
    }

    public void setRtcmTreatyVOList(List<RtcmTreatyVO> rtcmTreatyVOList) {
        this.rtcmTreatyVOList = rtcmTreatyVOList;
    }

    public RtcmTreatyVO getMainTreaty() throws Exception {
        return this.getRtcmTreatyVOList().stream()
            .filter(t -> TreatyType.MAIN.getAnalysisCode().equals(t.getTreatyType()))
            .findFirst()
            .orElseThrow(() -> new Exception(ErrorCode.ERROR_BY_MAIN_PLAN.getMessage()));
    }
}
