package com.welgram.crawler.general;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 상품마스터 VO
 */
@Getter
@Setter
public class ProductMasterVO implements Serializable {
	private static final long serialVersionUID = 1204710988425554810L;
	private int companyId;
	private String saleChannel; 		 // 판매채널
	private String productName;			 // 상품 이름
	private String productGubuns;		 // 주계약, 고정부가특약, 선택특약
	private List<String> productKinds;	 // 상품종류 (순수보장, 만기환급형 등)
	private List<String> productTypes;	 // 상품타입 (갱신형, 비갱신형)
	private List<String> napCycles;		 // 납입주기 (월납, 년납, 일시납)
	private List<String> insTerms;		 // 보험기간유형 ( 10년, 20년 )
	private List<String> napTerms; 		 // 납입기간유형 ( 10년, 전기납 )
	private List<String> annuityAges;	 // 연금개시나이
	private List<String> annuityTypes;	 // 연금타입
	private List<String> assureMoneys;	 // 가입금액 (5000000, 10000000)
	private String minAssureMoney;		 // 가입금액최소
	private String maxAssureMoney;		 // 가입금액최대
	private String productId;			 // 상품아이디

	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public String getSaleChannel() {
		return saleChannel;
	}
	public void setSaleChannel(String saleChannel) {
		this.saleChannel = saleChannel;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductGubuns() {
		return productGubuns;
	}
	public void setProductGubuns(String productGubuns) {
		this.productGubuns = productGubuns;
	}
	public List<String> getProductKinds() {
		return productKinds;
	}
	public void setProductKinds(List<String> productKinds) {
		this.productKinds = productKinds;
	}
	public List<String> getProductTypes() {
		return productTypes;
	}
	public void setProductTypes(List<String> productTypes) {
		this.productTypes = productTypes;
	}
	public List<String> getNapCycles() {
		return napCycles;
	}
	public void setNapCycles(List<String> napCycles) {
		this.napCycles = napCycles;
	}
	public List<String> getInsTerms() {
		return insTerms;
	}
	public void setInsTerms(List<String> insTerms) {
		this.insTerms = insTerms;
	}
	public List<String> getNapTerms() {
		return napTerms;
	}
	public void setNapTerms(List<String> napTerms) {
		this.napTerms = napTerms;
	}
	public List<String> getAnnuityAges() {
		return annuityAges;
	}
	public void setAnnuityAges(List<String> annuityAges) {
		this.annuityAges = annuityAges;
	}
	public List<String> getAnnuityTypes() {
		return annuityTypes;
	}
	public void setAnnuityTypes(List<String> annuityTypes) {
		this.annuityTypes = annuityTypes;
	}
	public List<String> getAssureMoneys() {
		return assureMoneys;
	}
	public void setAssureMoneys(List<String> assureMoneys) {
		this.assureMoneys = assureMoneys;
	}
	public String getMinAssureMoney() {
		return minAssureMoney;
	}
	public void setMinAssureMoney(String minAssureMoney) {
		this.minAssureMoney = minAssureMoney;
	}
	public String getMaxAssureMoney() {
		return maxAssureMoney;
	}
	public void setMaxAssureMoney(String maxAssureMoney) {
		this.maxAssureMoney = maxAssureMoney;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Override
	public String toString() {
		return "ProductMasterVO "
			+ "\n [ "
			+ "\n companyId=" + companyId + ", "
			+ "\n saleChannel=" + saleChannel + ", "
			+ "\n productName=" + productName + ", "
			+ "\n productGubuns=" + productGubuns + ", "
			+ "\n productKinds=" + productKinds + ", "
			+ "\n productTypes=" + productTypes + ", "
			+ "\n napCycles=" + napCycles + ", "
			+ "\n insTerms=" + insTerms + ", "
			+ "\n napTerms=" + napTerms + ", "
			+ "\n annuityAges=" + annuityAges + ", "
			+ "\n annuityTypes=" + annuityTypes + ", "
			+ "\n assureMoneys=" + assureMoneys + ", "
			+ "\n minAssureMoney=" + minAssureMoney + ", "
			+ "\n maxAssureMoney=" + maxAssureMoney + ", "
			+ "\n productId=" + productId +
			"]";
	}

	

	

	

}
