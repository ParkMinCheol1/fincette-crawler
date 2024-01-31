package com.welgram.crawler.general;

public class CrawlScreenShot {


	private Integer seq;			// 번호
	private Integer planId;			// 플랜id
	private String productId;		// 상품id
	private Integer insAge;			// 나이
	private String gender;			// 성별
	private String category;		// 상품종류
	private String status;		// 모니터링 성공 여부
	private String fileName;		// 파일명
	private String premium;			// 월보험료
	private String capturedTime;	// 캡쳐시간
	private String regTime;			// 등록시간
	private String encodedData;		// BASE64 코드

	public CrawlScreenShot() { }

	public Integer getSeq() {		return seq;	}
	public void setSeq(Integer seq) {		this.seq = seq;	}
	public Integer getPlanId() {		return planId;	}
	public void setPlanId(Integer planId) {		this.planId = planId;	}
	public String getProductId() {		return productId;	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public Integer getInsAge() { return insAge; }
	public void setInsAge(Integer insAge) { this.insAge = insAge; }
	public String getGender() { return gender; }
	public void setGender(String gender) { this.gender = gender; }
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getStatus() {		return status;	}
	public void setStatus(String status) {		this.status = status;	}
	public String getFileName() {		return fileName;	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getCapturedTime() {
		return capturedTime;
	}
	public String getPremium() { return premium; }
	public void setPremium(String premium){ this.premium = premium; }
	public void setCapturedTime(String capturedTime) {
		this.capturedTime = capturedTime;
	}
	public String getRegTime() {
		return regTime;
	}
	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}
	public String getEncodedData() {
		return encodedData;
	}
	public void setEncodedData(String encodedData) {
		this.encodedData = encodedData;
	}

	@Override
	public String toString() {
		return "CrawlScreenShot{" +
				"seq=" + seq +
				", planId=" + planId +
				", productId='" + productId + '\'' +
				", insAge='" + insAge + '\'' +
				", gender='" + gender + '\'' +
				", category='" + category + '\'' +
				", status='" + status + '\'' +
				", fileName='" + fileName + '\'' +
				", premium='" + premium + '\'' +
				", capturedTime='" + capturedTime + '\'' +
				", regTime='" + regTime + '\'' +
				", encodedData='" + encodedData + '\'' +
				'}';
	}
}
