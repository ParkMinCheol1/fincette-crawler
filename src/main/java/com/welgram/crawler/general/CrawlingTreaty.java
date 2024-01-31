package com.welgram.crawler.general;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrawlingTreaty implements Serializable {

	private static final long serialVersionUID = -750261788065880466L;

	public enum ProductGubun {
		주계약,
		선택특약,
		고정부가특약
	};

	public enum ProductKind {
		순수보장형,
		만기환급형,
		혼합형,
		환급형30,
		환급형50,
		환급형100,
		종신연금형,
		무해약환급형,
		저해약환급형
	};

	public enum ProductType {
		갱신형,
		비갱신형
	};

	// 납입주기 구분
	public final String NAP_CYCLE_MONTH = "01"; // 월납
	public final String NAP_CYCLE_YEAR = "02";  // 년납
	public final String NAP_CYCLE_ALL = "00";   // 일시납

	public String mapperId;

	public String productMasterId;
	public ProductGubun productGubun;
	public ProductKind productKind;
	public ProductType productType;

	public String treatyName; 		// 특약이름
	public int assureMoney; 		// 가입금액
	public String insTerm; 			// 보힘기간 (0이면 종신보장)
	public String napTerm;			// 납입기간
	public String napCycle; 		// 납입주기
	public String monthlyPremium; 	// 월 납입보험료(1회 연납이어도..)
	public String annAge; 			// 연금개시나이
	public String genderType;		// 전용 성별 여부

	public int planCalcId;
	public PlanCalc planCalc;


	public void setAnnAge(String annAge) {
		this.annAge = annAge.replace("연금개시", "");
		this.annAge = this.annAge.replace("세", "");
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

	public CrawlingTreaty() {
		this.monthlyPremium = "";
	}

	public CrawlingTreaty(String treatyName, int assureMoney) {
		this.treatyName = treatyName;
		this.assureMoney = assureMoney;
	}

	public CrawlingTreaty(String treatyName, int assureMoney, String insTerm, String napTerm) {
		this.treatyName = treatyName;
		this.assureMoney = assureMoney;
		this.insTerm = insTerm;
		this.napTerm = napTerm;
	}

	public void setProductGubun(String gubun) {
		this.productGubun = ProductGubun.주계약;

		if (gubun.equals("선택특약")) {
			this.productGubun = ProductGubun.선택특약;
		}
		if (gubun.equals("고정부가특약")) {
			this.productGubun = ProductGubun.고정부가특약;
		}
	}

	public void setProductKind(String kind) {
		this.productKind = ProductKind.순수보장형;

		switch (kind) {
			case "만기환급형":
				this.productKind = ProductKind.만기환급형;
				break;

			case "혼합형":
				this.productKind = ProductKind.혼합형;
				break;

			case "환급형30":
				this.productKind = ProductKind.환급형30;
				break;

			case "환급형50":
				this.productKind = ProductKind.환급형50;
				break;

			case "환급형100":
				this.productKind = ProductKind.환급형100;
				break;

			case "종신연금형":
				this.productKind = ProductKind.종신연금형;
				break;
		}
	}

	public void setProductType(String type) {
		this.productType = ProductType.갱신형;

		if (type.equals("비갱신형")) {
			this.productType = ProductType.비갱신형;
		}
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
		this.insTerm = insTerm.trim();
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

	public String getNapCycleName() {
		return NAP_CYCLE_MONTH.equals(napCycle) ? "월납": (NAP_CYCLE_YEAR.equals(napCycle) ? "연납" : "일시납");
	}

}
