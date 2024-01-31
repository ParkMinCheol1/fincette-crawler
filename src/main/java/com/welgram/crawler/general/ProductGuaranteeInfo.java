package com.welgram.crawler.general;


public class ProductGuaranteeInfo {

	private String productGubun = "";	// 주계약, 선택특약, 고정부가특약
	private String productName = "";	// 보험명
	private String payReason = "";		// 지급사유
	private String payMoney = "";		// 지급금액
	private String etc = "";			// 기타
	private String regTime = "";		// 등록일자
	
	public String getProductGubun() {
		return productGubun;
	}
	public void setProductGubun(String productGubun) {
		this.productGubun = productGubun;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getPayReason() {
		return payReason;
	}
	public void setPayReason(String payReason) {
		this.payReason = payReason;
	}
	public String getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}
	public String getEtc() {
		return etc;
	}
	public void setEtc(String etc) {
		this.etc = etc;
	}
	public String getRegTime() {
		return regTime;
	}
	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}
	



}
