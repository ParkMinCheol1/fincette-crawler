package com.welgram.crawler.cli.excutor;

import java.util.List;

public class CommandOptions {

    private boolean monitoring;
    private String productCode;
    private List<Integer> planIdList;
    private List<Integer> ages;
    private String gender;
    private int zero;
    private VpnGroup vpnGroup;
    private String site;
    private String screenShot;

    public CommandOptions(String productCode, boolean monitoring, List<Integer> planIdList, List<Integer> ages, String gender, Integer zero, String site, String screenShot) {
        this.productCode = productCode;
        this.monitoring = monitoring;
        this.planIdList = planIdList;
        this.ages = ages;
        this.gender = gender;
        this.zero = zero;
        this.site = site;
        this.screenShot = screenShot;

    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean monitoring) {
        this.monitoring = monitoring;
    }

    public List<Integer> getPlanIdList() {
        return planIdList;
    }

    public void setPlanIdList(List<Integer> planIdList) {
        this.planIdList = planIdList;
    }

    public List<Integer> getAges() {
        return ages;
    }

    public void setAges(List<Integer> ages) {
        this.ages = ages;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getZero() {
        return zero;
    }

    public void setZero(int zero) {
        this.zero = zero;
    }

    public VpnGroup getVpnGroup() {
        return vpnGroup;
    }

    public void setVpnGroup(VpnGroup vpnGroup) {
        this.vpnGroup = vpnGroup;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getScreenShot() { return screenShot; }

    public void setScreenShot(String screenShot){ this.screenShot = screenShot; }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
