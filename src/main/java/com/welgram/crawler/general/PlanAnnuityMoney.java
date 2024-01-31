package com.welgram.crawler.general;

public class PlanAnnuityMoney {

	private int planId;				// 가설 아이디
	private int insAge;					// 보험나이
	private String gender = "";			// 성별

	private String whl10Y = "0";			// 종신 10년형
	private String whl20Y = "0";			// 종신 20년형
	private String whl30Y = "0";			// 종신 30년형
	private String whl100A = "0";			// 종신 100세

	private String fxd10Y = "0";			// 확정 10년형
	private String fxd15Y = "0";			// 확정 15년형
	private String fxd20Y = "0";			// 확정 20년형
	private String fxd25Y = "0";			// 확정 25년형
	private String fxd30Y = "0";			// 확정 30년형

	private String regTime = "";		// 등록일자

	public int getPlanId() {		return planId;	}
	public void setPlanId(int planId) {		this.planId = planId;	}
	public int getInsAge() {		return insAge;	}
	public void setInsAge(int insAge) {		this.insAge = insAge;	}
	public String getGender() {		return gender;	}
	public void setGender(String gender) {		this.gender = gender;	}
	public String getWhl10Y() {		return whl10Y;	}
	public void setWhl10Y(String whl10y) {		this.whl10Y = whl10y;	}
	public String getWhl20Y() {		return whl20Y;	}
	public void setWhl20Y(String whl20y) {		this.whl20Y = whl20y;	}
	public String getWhl30Y() {		return whl30Y;	}
	public void setWhl30Y(String whl30y) {		this.whl30Y = whl30y;	}
	public String getWhl100A() {	return whl100A;	}
	public void setWhl100A(String whl100a) {		this.whl100A = whl100a;	}
	public String getFxd10Y() {		return fxd10Y;	}
	public void setFxd10Y(String fxd10y) {		this.fxd10Y = fxd10y;	}
	public String getFxd15Y() {		return fxd15Y;	}
	public void setFxd15Y(String fxd15y) {		this.fxd15Y = fxd15y;	}
	public String getFxd20Y() {		return fxd20Y;	}
	public void setFxd20Y(String fxd20y) {		this.fxd20Y = fxd20y;	}
	public String getFxd25Y() {		return fxd25Y;	}
	public void setFxd25Y(String fxd25y) {		this.fxd25Y = fxd25y;	}
	public String getFxd30Y() {		return fxd30Y;	}
	public void setFxd30Y(String fxd30y) {		this.fxd30Y = fxd30y;	}
	public String getRegTime() {	return regTime;	}
	public void setRegTime(String regTime) {		this.regTime = regTime;	}

	@Override
	public String toString() {
		return "PlanAnnuityMoney{" +
			", whl10Y='" + whl10Y + '\'' +
			", whl20Y='" + whl20Y + '\'' +
			", whl30Y='" + whl30Y + '\'' +
			", whl100A='" + whl100A + '\'' +
			", fxd10Y='" + fxd10Y + '\'' +
			", fxd15Y='" + fxd15Y + '\'' +
			", fxd20Y='" + fxd20Y + '\'' +
			", fxd25Y='" + fxd25Y + '\'' +
			", fxd30Y='" + fxd30Y + '\'' +
			'}';
	}
}
