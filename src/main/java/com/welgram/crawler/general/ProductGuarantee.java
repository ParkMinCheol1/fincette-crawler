package com.welgram.crawler.general;

import java.util.List;

public class ProductGuarantee {

	private String productId = "" ;				// 상품 아이디
	private String stdInfo = "" ;				// 기준데이터정보
	private String content = "";				// 기타 컨텐츠
	private String modTime = "";				// 수정일자
	private String regTime = "";				// 등록일자
	private List<ProductGuaranteeInfo> productGuaranteeList ;	// 보장내역 리스트

	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getStdInfo() {
		return stdInfo;
	}
	public void setStdInfo(String stdInfo) {
		this.stdInfo = stdInfo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public List<ProductGuaranteeInfo> getProductGuaranteeList() {
		return productGuaranteeList;
	}
	public void setProductGuaranteeList(List<ProductGuaranteeInfo> productGuaranteeList) {
		this.productGuaranteeList = productGuaranteeList;
	}



	
}
