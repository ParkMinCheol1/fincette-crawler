package com.welgram.crawler.general;

import com.welgram.crawler.comparer.PlanCompareResult;
import com.welgram.crawler.validator.ValidatePlanReturnMoney;
import com.welgram.util.Birthday;
import com.welgram.util.InsuranceUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CrawlingProduct {

	@Getter
	@RequiredArgsConstructor
	public enum Gender {
		M ("남"),
		F ("여");

		private final String desc;
		private final String order = ordinal() + "";
		private final String incrementedOrder = ordinal() + 1 + "";
	}

	public enum CrawlingSite {공시실, 사용자웹, 사용자모바일}
	public enum DisCount { 일반, 비흡연 }
	public enum Type { 갱신형, 비갱신형 }
	public enum PlanType { 실속형, 표준형, 고급형 }

	// 납입기간 구분 상수
	public final String NAP_CYCLE_MONTH = "01";
	public final String NAP_CYCLE_YEAR = "02";
	public final String NAP_CYCLE_ALL = "00";

	// 크롤링 옵션
	public CrawlingOption crawlingOption ;	 // 크롤링 실행시 옵션정의

	// 가입자 정보
	public int gender;					// 가입자 성별 (1:남자, 2:여자)
	private Gender genderEnum;			// 가입자 성별 enum
	private Birthday birthday;			// 가입자 생년월일 // todo 확인필요
	public String age; 					// 가입자 보험나이
	public String birth;				// 가입자 생년월일 // todo 확인필요
	public String fullBirth;			// 가입자 생년월일 19900909
	public String parent_FullBirth = "";
	public String parent_Birth = "";
	public String pregnancyWeek; 		// 임신 주수

	// 상품 속성 (product) : todo 심사 유형 추가 필요 (reviewType)
	public String productCode; 			// 크롤링 상품 코드
	public String productName; 			// 상품명
	public String insuName; 			// 보험명 (상품명 productName 과 동일하다 todo 확인 및 정리필요)
	public String productNamePublic;	// 공시실 상품명
	public String saleChannel;			// 판매채널 코드
	public String category;				// 상품 카테고리 코드
	public String categoryName;			// 상품 카테고리 이름
	public int companyId;				// 회사 id (company table)
	public String companyName; 			// 보험사 이름 (todo 확인필요: null로 들어오는 듯)
	public String siteWebUrl = "";		// 사용자 웹 URL
	public String siteMobileUrl = "";	// 사용자 모바일 URL
	public CrawlingSite crawlingSite = CrawlingSite.공시실 ;

	// 상품의 가입설계 속성
	public String planId; 				// planId
	public String mainYn; 				// 가설의 메인여부
	public String planName; 			// 가입설계 명 ( = 상품 product 명과 대부분 동일.)
	public String planSubName;          // 가입설계 하위 구분명
	public PlanType planType; 			// 실속형, 표준형, 고급형
	public DisCount discount; 			// 할인적용
	public String textType;				// plan을 식별할 수 있는 특징 저장
	public int minInsAge = 0;			// 가입설계에서 정한 최소 나이
	public int maxInsAge = 0;			// 가입설계에서 정한 최대 나이

	// 상품의 주계약 속성
	public String mapperId; 				// mapperId
	public int caseNumber; 					// planCalcsId // todo 확인필요
	public String assureMoney = ""; 		// 가입금액
	public Type productType; 				// 갱신형, 비갱신형
	public String insTerm; 					// 보험기
	public String napTerm; 					// 납입기간
	public String napCycle; 				// 납입주기
	public String productKind; 				// 환급종류
	public String annuityAge; 				// 연금개시나이 (연금보험, 연금저축보험) // annuityAge
	public String annuityType = "";			// 연금수령타입 (연금보험, 연금저축보험)
	public List<String> defaultProductType = new ArrayList<>( Arrays.asList("갱신형","비갱신형")); 	  // todo 확인 필요
	public List<String> defaultProductKind = new ArrayList<>( Arrays.asList("순수보장형","만기환급형"));  // todo 확인 필요

	// 상품의 특약 속성 (주계약 + 선택특약)
	public List<CrawlingTreaty> treatyList;		// 담보리스트

	// 유효성 검사용 변수
	public List<CrawlingTreaty> currentTreatyList;	// 원수사에서 현재 판매중인 담보리스트
	private PlanCompareResult planCompareResult;    // 원수사 vs 보답 관리자 등록 담보리스트 비교 결과
	public int siteProductMasterCount = 0;			// 사이트 공시실에서 DB특약과 일치하는 카운트   // todo 확인 필요

	// *** 수집 데이터 저장 변수 ***
	public String totPremium = ""; 				// 총납입보험료
	public String savePremium = ""; 			// 적립보험료 (plan_calc.save_money)
	public String returnPremium = ""; 			// 만기환급금 (plan_calc.exp_money)
	public String annuityPremium = ""; 			// 연금수령액 (plan_calc.ann_money) // todo 확인 필요
	public String fixedAnnuityPremium = ""; 	// 확정연금수령액 (plan_calc.fixed_ann_money) // todo 확인 필요
	public String nextMoney = "";				// 계속보험료 (태아보험의 출산후 보험료, plan_calc.next_money)
	public String expectSavePremium = "";		// 예상적립금 (plan_calc.expect_save_money)
	public List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();		// 중도해약환급금
	public PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney(); 			// 연금수령액
	public String errorMsg = ""; 				// 에러메시지

	// 모니터링 결과
	public String checkSiteYn = "";	 	// 사이트 변동 여부 (Y:변동 / N변동없음)
	public String checkPriceYn = ""; 	// 가격 변동 여부 (Y:변동 / N변동없음)
	private PlanMonitoringStatus planMonitoringStatus; 	// 모니터링 상태

	// 수집 시간, 케이스 카운트
	public String totalTime; 			// 크롤링에 걸린 총 시간
	public String crawlingTime; 		// 크롤링에 걸린 시간
	public String crawCompleteRate; 	// 크롤링진행률 // todo 확인 필요
	public int totalCrawlCount;			// todo 확인 필요
	public int currentCrawlCount;		// todo 확인 필요
	public int totalMasterCount;		// todo 확인 필요
	public int currentMasterCount;		// todo 확인 필요

	// 특약 크롤링 테스트용 변수 // todo 확인필요
	public List<ProductMasterVO> productMasterVOList = new ArrayList<ProductMasterVO>();	// 상품마스터(특약) 리스트



	public CrawlingProduct() {
    
		this.treatyList = new ArrayList<>();
		this.returnPremium = "";
		this.annuityPremium = "";
		this.fixedAnnuityPremium = "";
		this.errorMsg = "";
		this.crawlingTime = "";

		// 기본옵션을 초기에 세팅해준다.
		crawlingOption = new CrawlingChromeSimpleOption();
	}



	public CrawlingProduct(String productCode) {
    
		this();
		this.productCode = productCode;
	}


	public void setGender(int gender) {
    
		this.gender = gender;
		this.genderEnum = Arrays.stream(Gender.values())
				.filter(g -> Objects.equals(g.getOrder(), String.valueOf(this.gender)))
				.findFirst().get();
	}



	public void setParent_FullBirth(int parent_FullBirth) {
    
		birthday = InsuranceUtil.getBirthday(parent_FullBirth);
		this.parent_FullBirth = birthday.getYear().substring(0, 4) + birthday.getMonth() + birthday.getDay();
	}



	public void setParent_Birth(int parent_Birth) {
    
		birthday = InsuranceUtil.getBirthday(parent_Birth);
		this.parent_Birth = birthday.getYear().substring(2, 4) + birthday.getMonth() + birthday.getDay();
	}



	public void setPregnancyWeek(int pregnancyWeek) {
    
		this.pregnancyWeek = InsuranceUtil.getDateOfBirth(pregnancyWeek);
	}



	public void setDiscount(String discount) {
    
		if (discount.equals(DisCount.일반.toString())) {
			this.discount = DisCount.일반;
		}
		if (discount.equals(DisCount.비흡연.toString())) {
			this.discount = DisCount.비흡연;
		}
		/*if (discount.equals(DisCount.고액할인.toString())) {
			this.discount = DisCount.고액할인;
		}*/
	}



	public void setProductType(String productType) {
    
		if (productType.equals(Type.갱신형.toString())) {
			this.productType = Type.갱신형;
		} else {
			this.productType = Type.비갱신형;
		}
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



	public void setAnnuityAge(String annuityAge) {
    
		this.annuityAge = annuityAge.replace("연금개시", "");
		this.annuityAge = this.annuityAge.replace("세", "");
	}



	public void setAge(String age) {
    
		this.age = age;

		Birthday birthDay = InsuranceUtil.getBirthday(Integer.parseInt(this.age));
		this.birth = birthDay.getYear().substring(2, 4) + birthDay.getMonth() + birthDay.getDay();
		this.fullBirth = birthDay.getYear().substring(0, 4) + birthDay.getMonth() + birthDay.getDay();
	}




	public String getNapCycleName() {
    
		return NAP_CYCLE_MONTH.equals(napCycle) ? "월납": (NAP_CYCLE_YEAR.equals(napCycle) ? "연납" : "일시납");
	}



	public void setPlanReturnMoneyList(List<PlanReturnMoney> planReturnMoneyList) {

		new ValidatePlanReturnMoney().validatePlanReturnMoneyList(planReturnMoneyList)
			.ifPresent(errorMsg -> {
				throw new RuntimeException(errorMsg);
			});

		this.planReturnMoneyList = planReturnMoneyList;
	}




	public void setTotPremium(String totPremium) {
    
	  this.totPremium = Integer.toString(Integer.parseInt(totPremium) / 10000);
	}



	public PlanCalc getMainTreatyPlanCalc() {
		return treatyList.get(0).getPlanCalc();
	}



	public synchronized PlanMonitoringStatus getPlanMonitoringStatus() {

		if (planMonitoringStatus == null) {
			planMonitoringStatus = new PlanMonitoringStatus(this);
		}
		return planMonitoringStatus;
	}
}