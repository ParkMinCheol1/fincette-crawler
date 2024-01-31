package com.welgram.rtcm.vo;

import java.util.Date;

public class RtcmRequestVO {
    private Integer requestSeq;
    private Integer taskSeq;
    private Integer planId;
    private Integer templateSeq;
    private Integer age;
    private String gender;
    private String searchType;
    private Integer referenceSeq;
    private String duplicationYn;
    private String crawlCompleteYn;
    private String crawlSuccessYn;
    private String errorMsg;
    private Date crawlCompleteTime;
    private Date modTime;
    private Date regTime;

    private RtcmRequestVO referenceRequestVO;
    private RtcmPlanVO rtcmPlanVO;

    public Integer getRequestSeq() {
        return requestSeq;
    }

    public void setRequestSeq(Integer requestSeq) {
        this.requestSeq = requestSeq;
    }

    public Integer getTaskSeq() {
        return taskSeq;
    }

    public void setTaskSeq(Integer taskSeq) {
        this.taskSeq = taskSeq;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getTemplateSeq() {
        return templateSeq;
    }

    public void setTemplateSeq(Integer templateSeq) {
        this.templateSeq = templateSeq;
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

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public Integer getReferenceSeq() {
        return referenceSeq;
    }

    public void setReferenceSeq(Integer referenceSeq) {
        this.referenceSeq = referenceSeq;
    }

    public String getDuplicationYn() {
        return duplicationYn;
    }

    public void setDuplicationYn(String duplicationYn) {
        this.duplicationYn = duplicationYn;
    }

    public String getCrawlCompleteYn() {
        return crawlCompleteYn;
    }

    public void setCrawlCompleteYn(String crawlCompleteYn) {
        this.crawlCompleteYn = crawlCompleteYn;
    }

    public String getCrawlSuccessYn() {
        return crawlSuccessYn;
    }

    public void setCrawlSuccessYn(String crawlSuccessYn) {
        this.crawlSuccessYn = crawlSuccessYn;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Date getCrawlCompleteTime() {
        return crawlCompleteTime;
    }

    public void setCrawlCompleteTime(Date crawlCompleteTime) {
        this.crawlCompleteTime = crawlCompleteTime;
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

    public RtcmRequestVO getReferenceRequestVO() {
        return referenceRequestVO;
    }

    public void setReferenceRequestVO(RtcmRequestVO referenceRequestVO) {
        this.referenceRequestVO = referenceRequestVO;
    }

    public RtcmPlanVO getRtcmPlanVO() {
        return rtcmPlanVO;
    }

    public void setRtcmPlanVO(RtcmPlanVO rtcmPlanVO) {
        this.rtcmPlanVO = rtcmPlanVO;
    }
}
