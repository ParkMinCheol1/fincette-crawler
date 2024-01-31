package com.welgram.crawler.comparer;

public class TreatyDiffInfo {

    private  String planId;
    private String productMasterId;
    private String pageName;
    private int pageAmount;
    private String apiTreatyName;
    private int apiTreatyAmount;

    public TreatyDiffInfo(String planId, String productMasterId, String pageName, int pageAmount, String apiTreatyName, int apiTreatyAmount) {
        this.planId = planId;
        this.productMasterId = productMasterId;
        this.pageName = pageName;
        this.pageAmount = pageAmount;
        this.apiTreatyName = apiTreatyName;
        this.apiTreatyAmount = apiTreatyAmount;
    }

    public TreatyDiffInfo(String planId, String productMasterId, String apiTreatyName, int apiTreatyAmount) {
        this.planId = planId;
        this.productMasterId = productMasterId;
        this.apiTreatyName = apiTreatyName;
        this.apiTreatyAmount = apiTreatyAmount;
    }

    public TreatyDiffInfo(String planId, String pageName, int pageAmount) {
        this.planId = planId;
        this.pageName = pageName;
        this.pageAmount = pageAmount;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getProductMasterId() {
        return productMasterId;
    }

    public void setProductMasterId(String productMasterId) {
        this.productMasterId = productMasterId;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public int getPageAmount() {
        return pageAmount;
    }

    public void setPageAmount(int pageAmount) {
        this.pageAmount = pageAmount;
    }

    public String getApiTreatyName() {
        return apiTreatyName;
    }

    public void setApiTreatyName(String apiTreatyName) {
        this.apiTreatyName = apiTreatyName;
    }

    public int getApiTreatyAmount() {
        return apiTreatyAmount;
    }

    public void setApiTreatyAmount(int apiTreatyAmount) {
        this.apiTreatyAmount = apiTreatyAmount;
    }
}
