package com.welgram.crawler.coverage;

import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.CrawlingTreaty.ProductType;

/**
 * @author aqua
 *
 */
public class CrawlingCoverageInfo {
			
	public String insuranceKind;	// 보장성/저축성
	public String companyName;		//보험사명
	public String categoryName;		// 보험 종류
	public String productCode;		
	public ProductKind productKind;	// 환급형
	public ProductType productType;	// 갱신형/비갱신형
	public String insuName;			// 상품명
	public String planId;
	public String priceIndex;		// 보험지수
	public String publicRate;		// 공시이율
	public String minRate;			// 최저보증이율
	public String sTypeAndOType;	// 표준형/선택형Ⅱ
	public String returnType;
	public String errMsg;
		
	public CrawlingCoverageInfo() {
		this.errMsg = "";
		this.companyName = "";
		this.categoryName = "";
		this.productCode = "";
		this.insuName = "";
		this.priceIndex = "";
		this.publicRate = "";
		this.minRate = "";
	}
	
	
	public void setRenewalOrNonRenewal(String value){
		this.productType = productType.비갱신형;
		if(value.equals(productType.갱신형)){
			this.productType = productType.갱신형;
		}
	}
	
	public void setStandardAndOptionalType(String insuName) {
		if(insuName.contains("표준형")){
			sTypeAndOType = "표준형";
		} else {
			sTypeAndOType = "선택형Ⅱ";
		}
	}

	public void setInsuName(String insuName) {
		
		if(insuName.equals("(무)ABL인터넷어린이보험")){
			insuName = "(무)올라잇어린이보험"; // 다른상품은 이름수정됬는데 어린이보험은 아직 안됬음
		}
		if(insuName.equals("현대라이프 ZERO 정기보험 (무배당)")){
			insuName = insuName.replace("(무배당)", "무배당"); // 순수보장형, 만기보장형 구별하여 이름 선택해야함
		}
		if(insuName.equals("현대라이프 ZERO 암보험") || insuName.equals("현대라이프 ZERO 성인병보험")){
			insuName = insuName + "무배당 갱신형(D)";
		}
		if(insuName.contains("현대라이프 ZERO 양·한방건강보험")){
			insuName = insuName.substring(0, 19) + "무배당";
		}
		if(insuName.equals("현대라이프 ZERO 상해보험")){
			insuName = insuName + " 무배당";
		}
		if(insuName.equals("현대라이프 ZERO 교통재해보험 (무배당)")){
			insuName = insuName.replace("(무배당)", "무배당");	
		}
		if(insuName.equals("현대라이프 실손의료비보험무배당/갱신형(표준형)")){
			insuName = insuName.replace("(표준형)", "");
		}
		if(insuName.equals("KB착한정기보험") || insuName.equals("KB착한종신보험") || insuName.equals("KB착한어린이보험")){
			insuName = "(무)" + insuName;
		}
		if(insuName.equals("(무)KDB꼭! 필요한 실손의료비보험")){
			insuName = insuName + "(기본형)";
		}
		if(insuName.equals("(무)흥국생명 온라인우리아이플러스보장보험(기본형)")){
			insuName = insuName.replace("(기본형)", "");
		}
		if(insuName.equals("(무)흥국생명 온라인실손의료비보험(표준형_종합형)")){
			insuName = insuName.replace("(표준형_종합형)", "");
		}
		if(insuName.equals("(무)하나멤버스교통사고재해보험(교통재해형)")){
			insuName = insuName.replace("(교통재해형)", "");
		}
		if(insuName.equals("무)KB착한어린이보험")){
			insuName = "(" + insuName;
		}
		if(insuName.equals("(무)KDB다이렉트 암보험(비갱신형)")){
			insuName = insuName.replace("(비갱신형)", "");
		}
		if(insuName.contains("(무)KDB꼭! 필요한 실손의료비보험")){
			insuName = insuName.substring(0, 20);
			insuName = insuName + "(기본형)";
		}
		if(insuName.contains("(무) KDB다이렉트 새로운 정기보험")){
			returnType = insuName.substring(20);
			insuName = insuName.substring(0, 20) + "(무해약환급형)";
		}
		if(insuName.contains("(무)라이프플래닛e정기보험Ⅱ")){
			insuName = insuName + "(순수보장형, 표준체)";
		}
		if(insuName.equals("(무)라이프플래닛e종신보험Ⅲ (일반형)")){
			insuName = insuName.substring(0, 15);
			insuName = insuName + "(일반형, 표준체)";
		} else if(insuName.equals("(무)라이프플래닛e종신보험Ⅲ (체감형)")){
			insuName = insuName.substring(0, 15);
			insuName = insuName + "(체감형, 표준체)";
		}
		if(insuName.equals("(무)라이프플래닛e암보험Ⅱ") || insuName.equals("(무)라이프플래닛e5대성인병보험 (순수보장형)")){
			insuName = insuName.replace("(순수보장형)", "");
			insuName = insuName + "(순수보장형, 표준체)";
		}
		if(insuName.equals("(무)부모사랑e정기보험")){
			insuName = insuName + "(표준체)";
		}
		if(insuName.equals("온라인 정기보험(무)1704_건강체")){
			insuName = "미래에셋생명 온라인 정기보험 무배당 1704";
		}
		if(insuName.equals("온라인 암보험(무)1701")){
			insuName = "미래에셋생명 온라인 암보험 무배당 1705";
		}
		if(insuName.equals("온라인 든든보장보험(무)1701")){
			insuName = "미래에셋생명 온라인 든든보장보험 무배당 1701";
		}
		if(insuName.equals("온라인 성인질병보험(무)1701")){
			insuName = "미래에셋생명 온라인 성인질병보험 무배당 1701";
		}
		if(insuName.equals("온라인 상해보험(무)1701")){
			insuName = "미래에셋생명 온라인 상해보험 무배당 1701";
		}
		if(insuName.equals("온라인 어린이보험(무)1701")){
			insuName = "미래에셋생명 온라인 어린이보험 무배당 1701";
		}
		if(insuName.contains("삼성생명 인터넷 실손의료비보장보험")){
			insuName = insuName.substring(0, 33) + sTypeAndOType;
		}
		if(insuName.equals("온라인 연금저축보험(무)1701")){
			insuName = insuName.replace("(무)", "무배당");
			insuName = "미래에셋생명" + insuName;
		}
		if(insuName.equals("교보프리미어종신보험II(보증비용부과형)") || insuName.equals("행복knowhow연금저축보험") || insuName.equals("KB착한연금보험")){
			insuName = "(무)" + insuName;
		}
		if(insuName.contains("한화생명")){
			if(insuName.equals("한화생명 e암보험(무)")){
				insuName = insuName + "(" + this.productType + ")";
			}
			insuName = insuName.replace("(무)", "");
			insuName = insuName + "무배당";
		}
		if(insuName.equals("무배당 IBK 인터넷연금보험_1704")){
			insuName = "무배당_IBK_인터넷연금보험_1704";
		}
		if(insuName.equals("삼성생명 인터넷저축보험1.7(무배당)")){
			insuName = insuName.replace(")", "");	
		}
		if(insuName.equals("통합유니버설종신보험3.0(무배당,보증비용부과형)")){
			insuName = "삼성생명" + insuName;
		}
		if(insuName.equals("(무)프리스타일통합종신보험(보증비용부과형)")){
			StringBuffer name = new StringBuffer(insuName);
			name.insert(3, "수호천사");
			insuName = String.valueOf(name);
		}
		if(insuName.contains("한화생명 기본형 e실손의료비보장보험")){
			insuName = insuName.substring(0, 24) + "(무)";
			insuName = insuName.replace("e", "");
		}
		if(insuName.contains("NH온라인실손의료비보험")){
			insuName = insuName.substring(0, 21);
		}
		if(insuName.contains("(무)자녀사랑e정기보험")){
			insuName = insuName + "(표준체)";
		}
		this.insuName = insuName;
	}
	
	public void setCompanyName(String companyName) {
		if(companyName.equals("현대라이프") || companyName.equals("교보라이프플래닛")){
			companyName = companyName + "생명";
		}
		if(companyName.equals("IBK연금보험")){
			companyName = companyName.replace("보험", "");
		}
		
		this.companyName = companyName;
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
//	연금저축보험, 연금보험, 저축보험, 기타
	public void setInsurance(String categoryName, String company, String insuName){
		this.categoryName = categoryName;
		
		if(company.equals("한화생명")){
			if(categoryName.equals("어린이보험")){
				// 어린이전용연금보험이 어린이보험으로 분류되있음
				this.categoryName = "연금보험";
			}
		}
		if(insuName.equals("(무)라이프플래닛e에듀케어저축보험")){
			this.categoryName = "저축보험";
		}
		if(insuName.contains("변액적립")){
			this.categoryName = "저축보험";
		}
	
		switch(categoryName){
			case "연금저축보험" :
				insuranceKind = "저축성";
				break;
			case "어린이보험" :
				if(insuName.contains("신한인터넷 어린이보험") || insuName.contains("KDB다이렉트 어린이보험") || insuName.contains("KB착한어린이보험") || insuName.contains("하나1Q어린이보험") || insuName.contains("흥국생명 온라인우리아이플러스보장보험") || insuName.contains("(무)라이프플래닛e플러스어린이보험") || company.equals("라이나생명") || company.equals("미래에셋생명") || company.equals("ABL생명")){
					insuranceKind = "보장성";
				} else {
					insuranceKind = "저축성";
				}
				break;
			case "연금보험" :
				insuranceKind = "저축성";
				break;
			case "저축보험" :
				insuranceKind = "저축성";
				break;
			case "변액보험" :
				insuranceKind = "저축성";
				break;
			case "변액적립보험" :
				insuranceKind = "저축성";
				break;
			default : 
				insuranceKind = "보장성";
		}
		if(categoryName.equals("변액보험")){
			categoryName = "저축보험";
		}
	}

}
