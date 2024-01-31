package com.welgram.crawler.general;

public class PlanCalc {
    public PlanCalc(int mapperId, int planCalcId, int insAge, String gender) {
        this.mapperId = mapperId;
        this.planCalcId = planCalcId;
        this.insAge = insAge;
        this.gender = gender;
    }

    private int planCalcId;

    /** 매퍼 아이디 */
    private int mapperId;

    /** 보험나이 */
    private int insAge;

    /** 성별 */
    private String gender;

    /** 보험금 */
    private String insMoney;

    /** 환급금 */
    private String expMoney;

    /** 연금수령액 */
    private String annMoney;

    /** 확정형연금수령액 */
    private String fixedAnnMoney;

    /** 적립보험료 화재대응 */
    private String saveMoney;

    /** 예상적립금 */
    private String expectSaveMoney;

    /** 가입금액 라이나생명 대응 */
    private String assureMoney;

    /** 계속보험료 */
    private String nextMoney;

    /** 수정일 */
    private String modTime;

    /** 등록일 */
    private String regTime;

    /** 오류 메시지 */
    private String errorMsg;

    public PlanCalc() {

    }


    public int getPlanCalcId() {
        return planCalcId;
    }

    public void setPlanCalcId(int planCalcId) {
        this.planCalcId = planCalcId;
    }

    public int getMapperId() {
        return mapperId;
    }

    public void setMapperId(int mapperId) {
        this.mapperId = mapperId;
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

    public String getInsMoney() {
        return insMoney;
    }

    public void setInsMoney(String insMoney) {
        this.insMoney = insMoney;
    }

    public String getExpMoney() {
        return expMoney;
    }

    public void setExpMoney(String expMoney) {
        this.expMoney = expMoney;
    }

    public String getAnnMoney() {
        return annMoney;
    }

    public void setAnnMoney(String annMoney) {
        this.annMoney = annMoney;
    }

    public String getFixedAnnMoney() {
        return fixedAnnMoney;
    }

    public void setFixedAnnMoney(String fixedAnnMoney) {
        this.fixedAnnMoney = fixedAnnMoney;
    }

    public String getSaveMoney() {
        return saveMoney;
    }

    public void setSaveMoney(String saveMoney) {
        this.saveMoney = saveMoney;
    }

    public String getExpectSaveMoney() {
        return expectSaveMoney;
    }

    public void setExpectSaveMoney(String expectSaveMoney) {
        this.expectSaveMoney = expectSaveMoney;
    }

    public String getAssureMoney() {
        return assureMoney;
    }

    public void setAssureMoney(String assureMoney) {
        this.assureMoney = assureMoney;
    }

    public String getNextMoney() {
        return nextMoney;
    }

    public void setNextMoney(String nextMoney) {
        this.nextMoney = nextMoney;
    }

    public String getModTime() {
        return modTime;
    }

    public void setModTime(String modTime) {
        this.modTime = modTime;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "{" +
                "planCalcId=" + planCalcId +
                ", gender=" + gender +
                ", insAge=" + insAge +
                ", errorMsg=" + errorMsg +
                '}';
    }
}
