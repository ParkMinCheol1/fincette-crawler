package com.welgram.crawler.direct.life;

import com.welgram.util.Birthday;
import com.welgram.util.InsuranceUtil;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CrawlingInfo implements Serializable {

	private static final long serialVersionUID = -3873914510641962696L;

	private Birthday birthday;

	public enum DisCount {
		기본,
		건강체,
		고액할인
	}

	public enum Type {
		갱신형,
		비갱신형
	}

	public final String MALE_ARG_HEIGHT = "174";
	public final String FEMALE_ARG_HEIGHT = "163";
	public final String MALE_ARG_WEIGHT = "67";
	public final String FEMALE_ARG_WEIGHT = "54";

	public String BIRTH_MONTH_DAY = "";
	public String parent_FullBirth = "";
	public String parent_Birth = "";
	public final String parent_Name = "김그램";

	public final String NAP_CYCLE_MONTH = "01";
	public final String NAP_CYCLE_YEAR = "02";
	public final String NAP_CYCLE_ALL = "00";

	public String pregnancyWeek;
	public DisCount discount; // 할인적용
	public Type productType; // 갱신형, 비갱신형

	public String checkSiteYn = "";
	public String checkPriceYn = ""; // 가격변동여부(Y변동/N변동없음)
	public String productCode; // 상품코드
	public String companyName; // 보험사 이름
	public String categoryName; // 보험종류
	public String age; // 가입자 만 나이
	public String insuName; // 보험명
	public String productName; // 상품이름
	public String productKind; // 환급종류
	public String napCycle; // 납입주기
	public String insTerm; // 보험기간
	public String napTerm; // 납입기간
	public String annAge; // 연금개시나이
	public String premium; // 월보험료
	public String insuAmount; // 가입금액
	public String birth; // 790708
	public String fullBirth; // 19790708
	public String returnPremium; // 해약환급금
	public String annuityPremium; // 연금수령액
	public String fixedAnnuityPremium; // 확정연금수령액
	public String errorMsg; // 에러메세지
	public String crawlingTime; // 크롤링에 걸린 시간
	public String totalTime; // 크롤링에 걸린 총 시간
	public String planId; // planId
	public String mapperId; // mapperId

	public int totalCount;
	public int currentCount;
	public int gender; // 성별
	public int caseNumber; // planCalcsId

	public CrawlingInfo() {
		age = "";
		insuName = "";
		productKind = "";
		insTerm = "";
		napTerm = "";
		annAge = "";
		premium = "";
		productName = "";
		birth = "";
		fullBirth = "";
		returnPremium = "";
		annuityPremium = "";
		errorMsg = "";
		crawlingTime = "";

		gender = 0;
		caseNumber = 0;
		totalCount = 0;
		currentCount = 0;
	}

	public String getFEMALE_ARG_HEIGHT() {
		return FEMALE_ARG_HEIGHT;
	}

	public String getFEMALE_ARG_WEIGHT() {
		return FEMALE_ARG_WEIGHT;
	}

	public String getMALE_ARG_HEIGHT() {
		return MALE_ARG_HEIGHT;
	}

	public String getMALE_ARG_WEIGHT() {
		return MALE_ARG_WEIGHT;
	}

	public String getBIRTH_MONTH_DAY() {
		return BIRTH_MONTH_DAY;
	}

	public void setBIRTH_MONTH_DAY(String BIRTH_MONTH_DAY) {
		BIRTH_MONTH_DAY = birthday.getMonth() + birthday.getDay();
	}

	public String getParent_FullBirth() {
		return parent_FullBirth;
	}

	public void setParent_FullBirth(int parent_FullBirth) {
		birthday = InsuranceUtil.getBirthday(parent_FullBirth);
		this.parent_FullBirth = birthday.getYear().substring(0, 4) + birthday.getMonth() + birthday.getDay();
	}

	public String getParent_Birth() {
		return parent_Birth;
	}

	public void setParent_Birth(int parent_Birth) {
		birthday = InsuranceUtil.getBirthday(parent_Birth);
		this.parent_Birth = birthday.getYear().substring(2, 4) + birthday.getMonth() + birthday.getDay();
	}

	public String getPregnancyWeek() {
		return pregnancyWeek;
	}

	public void setPregnancyWeek(int pregnancyWeek) {
		this.pregnancyWeek = InsuranceUtil.getDateOfBirth(pregnancyWeek);
	}

	public void setProductType(String productType) {
		if (productType.equals(Type.갱신형.toString())) {
			this.productType = Type.갱신형;
		} else {
			this.productType = Type.비갱신형;
		}
	}

	public Type getProductType() {
		return productType;
	}

	public void setDiscount(String discount) {
		if (discount.equals(DisCount.기본.toString())) {
			this.discount = DisCount.기본;
		}
		if (discount.equals(DisCount.건강체.toString())) {
			this.discount = DisCount.건강체;
		}
		if (discount.equals(DisCount.고액할인.toString())) {
			this.discount = DisCount.고액할인;
		}
	}

	public DisCount getDiscount() {
		return discount;
	}

	public void setInsTerm(String insTerm) {
		if (insTerm.equals("종신")) {
			this.insTerm = insTerm;
		} else if (insTerm.equals("종신보장")) {
			this.insTerm = insTerm;
		} else if (insTerm.contains("년보장")) {
			this.insTerm = insTerm.substring(0, insTerm.length() - "년보장".length());
		} else if (insTerm.contains("세만기")) {
			this.insTerm = insTerm.substring(0, insTerm.length() - "세만기".length());
		} else if (insTerm.contains("세보장")) {
			this.insTerm = insTerm.substring(0, insTerm.length() - "세보장".length());
		} else if (insTerm.contains("년")) {
			this.insTerm = insTerm;
		} else if (insTerm.contains("세")) {
			this.insTerm = insTerm;
		}
		this.insTerm = this.insTerm.trim();
	}

	public String getInsTerm() {
		return insTerm;
	}

	public void setNapTerm(String napTerm) {
		if (napTerm.trim().equals("전기납")) {
			this.napTerm = this.insTerm;
		} else if (napTerm.contains("년납")) {
			this.napTerm = napTerm.substring(0, napTerm.length() - "년납".length());
		} else if (napTerm.contains("세납")) {
			this.napTerm = napTerm.substring(0, napTerm.length() - "세납".length());
		} else if (napTerm.contains("년")) {
			this.napTerm = napTerm;
		} else if (napTerm.contains("세")) {
			this.napTerm = napTerm;
		}
		this.napTerm = napTerm.trim();
	}

	public String getNapTerm() {
		return napTerm;
	}

	public void setAnnAge(String annAge) {
		this.annAge = annAge.replace("연금개시", "");
		this.annAge = this.annAge.replace("세", "");
	}

	public void setPremium(String premium) {
		this.premium = Integer.toString(Integer.parseInt(premium) / 10000);
	}

	public String getPremium() {
		return premium;
	}

	public void setNapCycle(String napCycle) {
		if (napCycle.trim().equals("월납")) {
			this.napCycle = NAP_CYCLE_MONTH;
		} else if (napCycle.trim().equals("년납")) {
			this.napCycle = NAP_CYCLE_YEAR;
		} else if (napCycle.trim().equals("일시납")) {
			this.napCycle = NAP_CYCLE_ALL;
		}
	}

	public String getNapCycle() {
		return napCycle;
	}

	public String getCheckSiteYn() {
		return checkSiteYn;
	}

	public void setCheckSiteYn(String checkSiteYn) {
		this.checkSiteYn = checkSiteYn;
	}

	public String getCheckPriceYn() {
		return checkPriceYn;
	}

	public void setCheckPriceYn(String checkPriceYn) {
		this.checkPriceYn = checkPriceYn;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getInsuName() {
		return insuName;
	}

	public void setInsuName(String insuName) {
		this.insuName = insuName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductKind() {
		return productKind;
	}

	public void setProductKind(String productKind) {
		this.productKind = productKind;
	}

	public String getInsuAmount() {
		return insuAmount;
	}

	public void setInsuAmount(String insuAmount) {
		this.insuAmount = insuAmount;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getFullBirth() {
		return fullBirth;
	}

	public void setFullBirth(String fullBirth) {
		this.fullBirth = fullBirth;
	}

	public String getReturnPremium() {
		return returnPremium;
	}

	public void setReturnPremium(String returnPremium) {
		this.returnPremium = returnPremium;
	}

	public String getAnnuityPremium() {
		return annuityPremium;
	}

	public void setAnnuityPremium(String annuityPremium) {
		this.annuityPremium = annuityPremium;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getCrawlingTime() {
		return crawlingTime;
	}

	public void setCrawlingTime(String crawlingTime) {
		this.crawlingTime = crawlingTime;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getMapperId() {
		return mapperId;
	}

	public void setMapperId(String mapperId) {
		this.mapperId = mapperId;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getCaseNumber() {
		return caseNumber;
	}

	public void setCaseNumber(int caseNumber) {
		this.caseNumber = caseNumber;
	}

	public String getAnnAge() {
		return annAge;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
