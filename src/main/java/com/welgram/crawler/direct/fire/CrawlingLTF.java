package com.welgram.crawler.direct.fire;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanCalc;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.ProductMasterVO;

import java.util.*;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @author SungEun Koo <aqua@welgram.com> 삼성
 */

public abstract class CrawlingLTF extends SeleniumCrawler {

	// 공시실
	protected void openAnnouncePage(String productName) {
//		helper.elementWait("tbody#before");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("tbody#before")));
		element = helper.waitVisibilityOfElementLocated((By.cssSelector("tbody#before")));
		elements = element.findElements(By.className("alignL"));

		// 현재 창
		// currentHandle = driver.getWindowHandles().iterator().next();
		// logger.info("윈도우핸들 사이즈 : " + driver.getWindowHandles().size());
		logger.info(productName + " 상품 찾는 중...");
		for (WebElement td : elements) {
			// logger.info("td.getText().trim()" + td.getText().trim());
			if (td.getText().trim().contains(productName.trim())) {
				element = td.findElement(By.xpath("parent::*")).findElement(By.tagName("span"));
				element.click();
				break;
			}
		}
		// switchToWindow(currentHandle, driver.getWindowHandles(), true);
		switchtowindows(2);
	}

	// 생년월일
	protected void setBirth(String fullBirth, String id) throws Exception {
		element = driver.findElement(By.cssSelector(id));
		element.clear();
		element.sendKeys(fullBirth);
		WaitUtil.loading(4);
	}

	// 생년월일 각각선택 ( year,month,day )
	protected void WebsetBirthYYMMDD(String fullBirth,String tdpath){

		JavascriptExecutor j = (JavascriptExecutor) driver;
		element = driver.findElement(By.cssSelector(tdpath));
		elements = element.findElements(By.tagName("input"));

		// classname : nextInputField
		for(int index=0 ; index < elements.size(); index++) {
			String inputname = String.valueOf(j.executeScript("return document.getElementsByClassName('nextInputField')["+index+"].getAttribute('name')"));
			// year
			if("birthYear".equals(inputname)){
				logger.info(inputname+"::"+fullBirth.substring(0,4));
				elements.get(index).clear();
				elements.get(index).sendKeys(fullBirth.substring(0,4));
			}
			// monday
			else if("birthMonth".equals(inputname)){
				logger.info(inputname+"::"+fullBirth.substring(4,6));
				elements.get(index).clear();
				elements.get(index).sendKeys(fullBirth.substring(4,6));
			}
			// day
			else{
				logger.info(inputname+"::"+fullBirth.substring(6,8));
				elements.get(index).clear();
				elements.get(index).sendKeys(fullBirth.substring(6,8));
			}
		}
	}

	// 성별
	protected void setGender(int gender, By by) throws Exception {
		String text ;
		if(gender == 0) {text = "남자";}
		else {text = "여자";}
		WaitUtil.loading(2);
		elements = driver.findElements(by);

		for (WebElement input : elements) {
			if ((input.getAttribute("title") + "자").equals(text)) {
				input.click();
				WaitUtil.loading(1);
				break;
			}
		}
	}

	// 직업
	protected void setJob() throws Exception {
		String text = "사무직";
		// 현재 창
		currentHandle = driver.getWindowHandles().iterator().next();

		// 검색 버튼 클릭
		helper.click(By.cssSelector("a.bt_02_49"));

		// new windows 창으로 전환
		switchtowindows(3);

		// 직업찾기 창으로 전환
		// switchToWindow(currentHandle, driver.getWindowHandles(), true);

		// 직업명 input 태그에 입력
		WaitUtil.loading(4);

		element = driver.findElement(By.cssSelector("#content > div.section_udline > p > label > input"));
		element.click();
		element.sendKeys(text);
		
		// 검색 버튼 클릭
		helper.click(driver.findElement(By.cssSelector("#content > div.section_udline > p > span > a")));

		element = helper.waitVisibilityOfElementLocated(By.cssSelector("tbody#addr_list"));

		element = helper.waitVisibilityOf(element.findElements(By.tagName("tr")).get(0));
		helper.click(element.findElement(By.tagName("a")));
		WaitUtil.loading(1);

		// 보험료 산출 창으로 전환
		switchtowindows(2);

		// 다음
		helper.click(driver.findElement(By.id("btn1_1")));
	}

	//납입기간 Select Box
	protected void setSelectBox(String term, String value) throws Exception {
		boolean result = false;
		term = term.replaceAll("[^0-9]", "");

		element = helper.waitElementToBeClickable(By.id(value));
		elements = element.findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (option.getAttribute("numvl").equals(term)) {
				logger.info(term+"클릭!");
				option.click();
				result = true;
				WaitUtil.loading(1);
				break;
			}
		}
		if (!result) {
			throw new Exception("ID: " + term + " select box 선택 오류!");
		}
	}

	//납입주기
	protected void setNapCycle(String napCycle, String value) throws Exception {
		boolean result = false;
		String napCycleString = "";
		switch (napCycle) {
			case "01":
				napCycleString = "월납";
				break;
			case "02":
				napCycleString = "년납";
				break;
		}
		// 납입주기
		WaitUtil.loading(2);
		elements = driver.findElement(By.cssSelector(value)).findElements(By.tagName("option"));

//		elements = helper.waitElementToBeClickable(By.cssSelector(value))
//				.findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (option.getText().trim().equals(napCycleString)) {
				logger.info(option.getText()+"클릭!");
				option.click();
				helper.waitForCSSElement("#loading");
				WaitUtil.waitFor(1);
				result = true;
				break;
			}
		}

		if (!result) {
			throw new Exception("납입주기를 찾을 수 없습니다.");
		}

		// 다음
		helper.click(By.className("bt_06_14"));
		WaitUtil.loading(1);

		// driver.switchTo().window(currentHandle);
	}

	// 특약선택
	protected void setTreaty(CrawlingTreaty item) throws Exception {
		String text = "";
		String treatyName	= item.treatyName.replace(" ","").replace("Ⅱ","II");
		ProductGubun type	= item.productGubun;
		String assureMoney	= String.valueOf(item.assureMoney);
		
		boolean result = false;

//		elements = helper.waitVisibilityOfElementLocated(By.id("priceLA-step2-idambo-tbody"))
//					.findElements(By.tagName("tr"));

		WaitUtil.loading(3);
		elements = driver.findElement(By.id("priceLA-step2-idambo-tbody")).findElements(By.tagName("tr"));
		logger.info(type.toString() + " : " + treatyName + "을(를) 찾는 중....");

		if(item.treatyName.contains("갱)질병·상해3대비급여형 실손의료비")){
			helper.click(By.xpath("//*[@id=\"dambo-CLA21309\"]"));
			helper.click(By.xpath("//*[@id=\"dambo-CLA10842\"]"));
			result = true;
		}

		for (WebElement tr : elements) {
			for (WebElement td : tr.findElements(By.cssSelector("td.alignR.lst"))) {
				text = td.getAttribute("title").replace(" ","");
				
				// td와 담보명이 일치할 경우
				if (text.equals(treatyName)) {
					element = helper.waitElementToBeClickable(td.findElement(By.cssSelector("input[type='checkbox']")));
					
					//--1  체크가 안됐을 경우 체크!
					if (!element.isSelected()) {
						element.click();
						logger.info(text + " 특약 click!");
					}
					
					//--2  가입금액 입력
					boolean emptyInput ;
					boolean emptySelect ;
					WebElement assureMoneyInput = null;
					WebElement assureMoneySelect = null;
					
					//---- 인풋 태그가 있을 때에만 입력하도록 try-catch로 분기 처리
					try{
						assureMoneyInput = td
							.findElement(By.xpath("parent::tr"))
							.findElement(By.cssSelector("input[name='isamt']"));
						emptyInput = false;
					}catch(Exception e){
						emptyInput = true;
					}
					
					if(!emptyInput){
						logger.info(assureMoneyInput.getTagName());
						assureMoneyInput.click();
						assureMoneyInput.clear();
						assureMoneyInput.sendKeys(String.valueOf(Integer.parseInt(assureMoney)/10000));
					}
					
					//---- 셀렉트 태그가 있을 때에만 선택하도록 try-catch로 분기 처리
					try{
						assureMoneySelect = td
							.findElement(By.xpath("parent::tr"))
							.findElement(By.cssSelector("select[name='isamt']"));
						emptySelect = assureMoneySelect.findElements(By.tagName("option")).size() == 1;
					}catch(Exception e){
						emptySelect = true;
					}
					
					if(!emptySelect){
						helper.selectOptionByClick(assureMoneySelect, String.valueOf(Integer.parseInt(assureMoney)/10000));
						logger.info("가입금액이 고정입니다.");
					}
					
					result = true;
					break;
				} // if: 담보명 일치
			} // for: td 담보명
			if(result){
				break; 
			}
		} // for: tr

		while (true) {
			if (!element.isSelected()) {
				logger.info(text + "다시선택");
				element.click();
			} else {
				break;
			}
		}

		if (!result) {
			throw new Exception("특약명 :" + treatyName + "을(를) 찾을 수 없습니다.");
		}
	}

	//보험료 계산 버튼 누르기
	protected void calculation() throws Exception {
		helper.click(By.id("btnCalProc"));
		if (helper.isAlertShowed()) {
			Alert alert = driver.switchTo().alert();
			WaitUtil.loading(2);
			alert.accept();
		}
		WaitUtil.loading(4);
	}

	//담보별 보험료
	protected void getPremium(CrawlingProduct info , CrawlingTreaty item) throws Exception {
		String premium ;
		boolean result = false;
		
		elements = helper.waitPresenceOfElementLocated(By.id("priceLA-step2-idambo-tbody"))
				.findElements(By.tagName("tr"));
		for (WebElement tr : elements) {
			
			for (WebElement td : tr.findElements(By.tagName("td"))) {
				if (td.getAttribute("title").equals(item.treatyName)) {
					info.siteProductMasterCount ++; // 등록된 담보명과 같은지 검증하는 카운트

					element = td.findElement(By.xpath("parent::*"));
					premium = element.findElement(By.cssSelector("td:last-child")).getText().replaceAll("[^0-9]", "");

					item.monthlyPremium = premium;
					logger.info(item.treatyName + " 월 보험료: " + premium + "원");

					result = true;
					break;
				}
			}
			
			if (result) {
				break;
			}
		}
	}

	// + 적립보험료
	protected void getSavingPremium(CrawlingProduct info) {
		String premium ;
		premium = helper.waitPresenceOfElementLocated(By.id("dcbfCuPrm")).getText().replaceAll("[^0-9]", "");
		info.savePremium = premium;
		logger.info("적립보험료 : " + premium + "원");
	}

	// 해약환급금
	protected void getReturnPremium(CrawlingProduct info) throws Exception {
		//logger.info("현재창 핸들 저장");
		//currentHandle = driver.getWindowHandles().iterator().next();
		
		logger.info("해약환급금 버튼 클릭");
		helper.click(By.cssSelector(".bt_04_07"));
		helper.waitForCSSElement("#loading");
		WaitUtil.waitFor(2);

		switchtowindows(3);
		
		/*logger.info("해약환급금 팝업창으로 핸들 전환");
		if(wait.until(ExpectedConditions.numberOfWindowsToBe(2))){
			helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
		}*/


		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
		elements = helper.waitPresenceOfElementLocated(By.id("refund-tbody")).findElements(By.tagName("tr"));
		for (WebElement tr : elements) {
			String term  = tr.findElements(By.tagName("td")).get(0).getText();
			logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
			String premiumSum 		= tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);
			String returnMoneyMin 	= tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
			String returnRateMin 	= tr.findElements(By.tagName("td")).get(3).getText();
			logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);
			String returnMoney 		= tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
			String returnRate 		= tr.findElements(By.tagName("td")).get(5).getText();
			logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
			String returnMoneyAvg 	= tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
			String returnRateAvg 	= tr.findElements(By.tagName("td")).get(7).getText();
			logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
			planReturnMoney.setReturnRateMin(returnRateMin);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
			planReturnMoney.setReturnRateAvg(returnRateAvg);
			planReturnMoneyList.add(planReturnMoney);

			info.returnPremium = returnMoney;
		}
		info.setPlanReturnMoneyList(planReturnMoneyList);
		
		logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
	}

	// 싱품마스터 크롤링 구현
	protected void getTreaty(CrawlingProduct info) {
		// 특약 명시 테이블의 tr
		elements = helper.waitVisibilityOfElementLocated(By.id("priceLA-step2-idambo-tbody"))
					.findElements(By.tagName("tr"));
		
		for (WebElement tr : elements) {
			String prdtNm ; 									// 상품명
			String productGubuns ;								// 상품구분: 주계약, 고정부가특약, 선택특약
			List<String> insTerms = new ArrayList<>();		// 보기
			List<String> napTerms = new ArrayList<>();		// 납기
			List<String> assureMoneys = new ArrayList<>();	// 가입금액
			String minAssureMoney ;								// 최소 가입금액
			String maxAssureMoney ;								// 최대 가입금액

			// 상품명
			prdtNm = tr.findElement(By.cssSelector("td.dambo-cvrnm")).getAttribute("title").trim();
			logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			logger.info("담보 크롤링 :: 담보명 :: " + prdtNm);
			
			// 상품 구분: 체크박스에 체크가 되어있으면 주계약, 아니면 선택특약
			if( tr.findElement(By.cssSelector("td.dambo-cvrcd.alignR.lst"))
					.findElement(By.tagName("input")).isSelected()) {
				productGubuns = "주계약";
			} else {
				productGubuns = "선택특약";
			}
			logger.info("담보 크롤링 :: 상품구분 :: " + productGubuns);
			
			// 보험기간 
			insTerms.add(tr.findElement(By.cssSelector("td.dambo-ndcd.alignC")).getText());
			logger.info("담보 크롤링 :: 보험기간 :: " + tr.findElement(By.cssSelector("td.dambo-ndcd.alignC")).getText());
			
			// 납입기간
			napTerms.add(tr.findElement(By.cssSelector("td.dambo-pymTrmcd.alignC")).getText());
			logger.info("담보 크롤링 :: 납입기간 :: " + tr.findElement(By.cssSelector("td.dambo-pymTrmcd.alignC")).getText());
			
			// 가입금액
			List<WebElement> assureMoneyOpList;
			try{ // select box 일 경우
				assureMoneyOpList = 
						tr.findElement(By.cssSelector("td.dambo-isamt.alignR"))
						.findElement(By.tagName("select")).findElements(By.tagName("option"));

				for(WebElement option : assureMoneyOpList){
					logger.info("담보 크롤링 :: 가입금액 :: " + option.getAttribute("numvl"));
					assureMoneys.add(option.getAttribute("numvl"));
				}
			} catch (Exception e) {
				assureMoneys.add(
				tr.findElement(By.cssSelector("td.dambo-isamt.alignR"))
				.findElement(By.tagName("input")).getAttribute("value").trim() + "0000");
				logger.info("담보 크롤링 :: 가입금액 :: " + tr.findElement(By.cssSelector("td.dambo-isamt.alignR"))
				.findElement(By.tagName("input")).getAttribute("value").trim() + "0000");
			}
			
			// 가입금액 sort하고 minAssureMoney, maxAssureMoney Set
			List<Integer> assureMoneysIntArrayList = new ArrayList<>();
			for (String assureMoney : assureMoneys) {
				assureMoneysIntArrayList.add(Integer.parseInt(assureMoney));
			}
			minAssureMoney = String.valueOf(Collections.min(assureMoneysIntArrayList));
			maxAssureMoney = String.valueOf(Collections.max(assureMoneysIntArrayList));
			logger.info("담보 크롤링 :: 최소 가입금액 :: " + minAssureMoney);
			logger.info("담보 크롤링 :: 최대 가입금액 :: " + maxAssureMoney);
			
			// 연금개시나이
			// 연금타입
			
			ProductMasterVO productMasterVO = new ProductMasterVO();
			
			productMasterVO.setProductName(prdtNm); 			// 상품명 (담보명)
			productMasterVO.setProductGubuns(productGubuns);	// 상품구분: 주계약, 고정부가특약, 선택특약
			productMasterVO.setInsTerms(insTerms); 				// 보기
			productMasterVO.setNapTerms(napTerms);				// 납기 
			productMasterVO.setAssureMoneys(assureMoneys); 		// 가입금액
			productMasterVO.setMinAssureMoney(minAssureMoney);	// 최소 가입금액
			productMasterVO.setMaxAssureMoney(maxAssureMoney);	// 최대 가입금액
			
			productMasterVO.setCompanyId(info.getCompanyId());					// 회사
			productMasterVO.setProductId(info.productCode);						// 상품아이디 
			productMasterVO.setProductKinds(info.defaultProductKind);				// 상품종류 (순수보장, 만기환급형 등)
			productMasterVO.setProductTypes(info.defaultProductType);	// 상품타입 (갱신형, 비갱신형)
			productMasterVO.setSaleChannel(info.getSaleChannel());				// 판매채널
			
			info.getProductMasterVOList().add(productMasterVO);
			
		} // for: tr
		// logger.info("getMainTreaty :: " + new Gson().toJson(info));
	} // end of getTreaty()


	// windows 창 전환
	public void switchtowindows(int loop){

		// new windows 창으로 전환
		int count =0;

		Set<String> allwindows = driver.getWindowHandles();

			for(String eachwindow : allwindows)
		{
			driver.switchTo().window(eachwindow);

			count++;

			if(count==loop)
			{
				logger.info(eachwindow);//will return the reference of second window
				break;//control will be switched to second window
			}

		}
	}

	// WaitLoadingBar
	public void WaitLoadingBar(String css) throws Exception {
		int looptime = 0;
		boolean result = true;

		try {
			while(result) {
				logger.info("displayed ::" + driver.findElement(By.cssSelector(css)).isDisplayed());
				if (driver.findElement(By.cssSelector(css)).isDisplayed()) {
					logger.info("###### 로딩중 ######");
					Thread.sleep(500);
					looptime += 500;
				} else {
					logger.info("###### 로딩끝 ######");
					WaitUtil.loading();
					break;
				}
				if(looptime > 120000){
					result = false;
					throw new Exception("무한루프 오류입니다!");
				}
			}
		} catch(Exception e){
			if(!result){
				throw new Exception(e);
			}
			logger.info("###### 로딩끝 ######");
		}
	}


	// 나이대별 특약별 가입금액 메소드
	protected void getTreatyList(CrawlingProduct info) throws InterruptedException {
		WaitUtil.loading();
		String assureMoney;
		String gender;
		if (info.gender == 0) {
			gender = "M";
		} else {
			gender = "F";
		}

//		if(info.planSubName.contains("사랑해 선물")){
//			elements = driver.findElements(By.cssSelector("#damboStr3 > dd"));
//		} else if(info.planSubName.contains("안심해 선물")){
//			elements = driver.findElements(By.cssSelector("#damboStr1 > dd"));
//		} else if(info.planSubName.contains("걱정마 선물")){
//			elements = driver.findElements(By.cssSelector("#damboStr2 > dd"));
//		}

		int elementsSize = elements.size();

		for (int i = 0; i < elementsSize; i++) {

			for (int j = 0; j < info.treatyList.size(); j++) {

				String treatyName = elements.get(i).findElements(By.cssSelector("span")).get(0).getText().replace(" ","");

				if (info.treatyList.get(j).treatyName.replace(" ","").trim().contains(treatyName)) { // 보험상품의 특약명과 원수사 사이트의 보험명이 같을경우

					assureMoney = elements.get(i).findElements(By.cssSelector("span")).get(1).getText().replace(",", "").replace("원", "").replace("상세보기","").replace("변동","").replace(" ","").replace("\n","");
					logger.info("특약명 : {}" ,treatyName);
					logger.info("가입금액확인 : {}",assureMoney.replace(",", "").replace("원", "").replace("상세보기","").replace("변동","").replace(" ",""));
					logger.info("=================================");

					PlanCalc planCalc = new PlanCalc();
					planCalc.setMapperId(Integer.parseInt(info.treatyList.get(j).mapperId));
					planCalc.setGender(gender);
					planCalc.setInsAge(Integer.parseInt(info.age));
					planCalc.setAssureMoney(assureMoney.replace(",", "").replace("원", "").replace("상세보기","").replace("변동","").replace(" ","").replace("\n",""));
					info.treatyList.get(j).setPlanCalc(planCalc);
				}
			}
		}
	}

	/*
	 *
	 *  롯데손해보험 공시실 스크롤 맨 밑으로 내리기
	 *
	 * */
	protected void discusroomScrollBottom(){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,document.body.scrollHeight);");
	}

	/**
	 * 대중교통상해보험
	 * 보험료 계산클릭
	 */
	protected void calcBtn(){
		driver.findElement(By.cssSelector("#content > div:nth-child(4) > p > span > button")).click();
	}

	/**
	 * 상해보험
	 * 보험료 계산클릭
	 */
	protected void ACDcalcBtn(){
		driver.findElement(By.cssSelector("#content > div:nth-child(5) > p > span > button")).click();
	}

	/**
	 * 공시실 직업선택
	 */
	protected void selectJob() throws Exception {
		driver.findElement(By.cssSelector("#content > div:nth-child(3) > div > table > tbody > tr > td > p > span > button")).click();


		switchtowindows(3);

		logger.info("직업검색창에 교사입력");
		driver.findElement(By.cssSelector("#content > div.section_udline > p > label > input")).sendKeys("교사");
		WaitUtil.waitFor(1);

		logger.info("검색버튼클릭");
		driver.findElement(By.cssSelector("#content > div.section_udline > p > span > a")).click();
		WaitUtil.waitFor(1);

		logger.info("검색된 직업 선택");
		elements = driver.findElements(By.cssSelector("#addr_list > tr"));
		int elementsSize = elements.size();


		for(int i=0; i<elementsSize; i++){

			if(elements.get(i).findElement(By.cssSelector("td:nth-child(1) > a")).getText().equals("보건 교사")){
				elements.get(i).findElement(By.cssSelector("td:nth-child(1) > a")).click();
				WaitUtil.waitFor(1);
				break;
			}
		}

		switchtowindows(3);

	}


	protected void selectGender(int gender) throws Exception{

		if(gender == 0){
			driver.findElement(By.cssSelector("#PIsdsex1_1")).click();
			WaitUtil.waitFor(1);
		}else{
			driver.findElement(By.cssSelector("#PIsdsex1_2")).click();
			WaitUtil.waitFor(1);
		}

	}
}
