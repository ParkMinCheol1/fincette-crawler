package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SFI_ASV_D004 extends CrawlingSFIDirect {

	public static void main(String[] args) {
		executeCommand(new SFI_ASV_D004(), args);
	}


	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $button = null;

		waitLoadingBar();


		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("생년월일 설정");
		setBirthday(info.getFullBirth());

		logger.info("성별 설정");
		setGender(info.getGender());

		logger.info("보험료 계산하기 버튼 클릭");
		$button = driver.findElement(By.id("btn-next-step"));
		click($button);

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("========== 보험료 납입 조건 GROUP 설정 ==========");
		logger.info("납입주기 설정");
		setNapCycle(info.getNapCycleName());

		logger.info("가입금액 설정");
		setAssureMoney(info);

		logger.info("납입기간 설정");
		setNapTerm(info.getNapTerm());

		logger.info("========== 연금 수령 조건 GROUP 설정 ==========");
		logger.info("연금개시나이 설정");
		setAnnuityAge(info.getAnnuityAge());

		logger.info("연금수령기간(연금지급기간) 설정");
		String annuityReceivePeriod = info.getAnnuityType().substring(3) + "간";
		setAnnuityReceivePeriod(annuityReceivePeriod);

		logger.info("연금수령주기 설정");
		setAnnuityReceiveCycle("매년");

		logger.info("예상 연금수령액 확인 버튼 클릭");
		$button = driver.findElement(By.id("show-body"));
		click($button);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		logger.info("연금수령액 크롤링");
		crawlAnnuityPremium(info);

		logger.info("해약환급금 크롤링");
		crawlReturnMoneyList(info);

		return true;
	}



	@Override
	public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

		CrawlingProduct info = (CrawlingProduct) obj[0];
		List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

		try {


			//해약환급금 팝업 오픈 버튼 element 찾기
			logger.info("해약환급금 팝업 오픈 버튼 클릭(연금수령시점 예상적립액 영역의 화살표 버튼)");
			WebElement $expectedSavePremiumSpan = driver.findElement(By.id("prng8Amt"));
			WebElement $openReturnMoneyPopupButton = $expectedSavePremiumSpan.findElement(By.xpath("./ancestor::div[1]/button"));
			click($openReturnMoneyPopupButton);


			//해약환급금 관련 정보 element 찾기
			WebElement $returnMoneyTable = driver.findElement(By.id("calc-table_low"));
			WebElement $returnMoneyTbody = $returnMoneyTable.findElement(By.tagName("tbody"));
			List<WebElement> $returnMoneyTrList = $returnMoneyTbody.findElements(By.tagName("tr"));

			for(WebElement $tr : $returnMoneyTrList) {
				//tr이 보이도록 스크롤 조정. 스크롤을 조정하지 않으면 해약환급금 금액을 크롤링 할 수 없음.
				helper.moveToElementByJavascriptExecutor($tr);

				List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

				String term = $tdList.get(0).getText();
				String premiumSum = $tdList.get(1).getText();
				String returnMoneyMin = $tdList.get(2).getText();
				String returnRateMin = $tdList.get(3).getText();
				String returnMoney = $tdList.get(4).getText();
				String returnRate = $tdList.get(5).getText();

				premiumSum = premiumSum.replaceAll("[^0-9]", "");
				returnMoneyMin = returnMoneyMin.replaceAll("[^0-9]", "");
				returnMoney = returnMoney.replaceAll("[^0-9]", "");

				logger.info("경과기간 : {} | 납입보험료 : {} | 최저환급금 : {} | 최저환급률 : {} | 공시환급금 : {} | 공시환급률 : {}", term, premiumSum, returnMoneyMin, returnRateMin, returnMoney, returnRate);

				PlanReturnMoney p = new PlanReturnMoney();
				p.setTerm(term);
				p.setPremiumSum(premiumSum);
				p.setReturnMoneyMin(returnMoneyMin);
				p.setReturnRateMin(returnRateMin);
				p.setReturnMoney(returnMoney);
				p.setReturnRate(returnRate);

				planReturnMoneyList.add(p);
				info.returnPremium = returnMoney;
			}

			logger.info("만기환급금 : {}원", info.returnPremium);


		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
			throw new ReturnMoneyListCrawlerException(e, exceptionEnum.getMsg());
		}
	}



	@Override
	public void setNapCycle(Object... obj) throws SetNapCycleException {
		String title = "납입주기";
		String expectedNapCycle = (String) obj[0];
		String actualNapCycle = "";
		String script = "";

		try {

			//납입주기를 원수사의 납입주기 포맷에 맞게 text값 수정
			expectedNapCycle = "월납".equals(expectedNapCycle) ? "매월" : expectedNapCycle;


			//납입주기 세팅을 위해 버튼 클릭
			WebElement $napCycleSpan = driver.findElement(By.id("dropdown-paymentCycle"));
			WebElement $napCycleButton = $napCycleSpan.findElement(By.xpath(".//button[@class[contains(., 'btn-adropdown')]]"));
			click($napCycleButton);


			/**
			 * ul에서 납입주기를 선택해야 하는 구조다.
			 * 여기서 납입주기에 해당하는 ul을 찾기위해서는 :visible 속성으로 찾아야한다.
			 * :visible은 By.cssSelector()로는 동작하지 않음. executeScript()를 통해서 실행해야 함.
			 */
			script = "return $('ul[id^=sfddropdown-menu]:visible')[0]";
			WebElement $napCycleUl = (WebElement) helper.executeJavascript(script);
			selectLiByTextFromUl($napCycleUl, expectedNapCycle);


			$napCycleSpan = $napCycleSpan.findElement(By.xpath(".//span[@class='label']"));
			actualNapCycle = $napCycleSpan.getText();


			//비교
			super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);


		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
			throw new SetNapCycleException(e, exceptionEnum.getMsg());
		}
	}


	@Override
	public void setNapTerm(Object... obj) throws SetNapTermException {
		String title = "납입기간";
		String expectedNapTerm = (String) obj[0];
		String actualNapTerm = "";
		String script = "";

		try {

			//납입기간을 원수사의 납입기간 포맷에 맞게 text값 수정
			expectedNapTerm = expectedNapTerm + "간";


			//납입기간 세팅을 위해 버튼 클릭
			WebElement $napTermSpan = driver.findElement(By.id("dropdown-paymentPeriod"));
			WebElement $napTermButton = $napTermSpan.findElement(By.xpath(".//button[@class[contains(., 'btn-adropdown')]]"));
			click($napTermButton);


			/**
			 * ul에서 납입기간을 선택해야 하는 구조다.
			 * 여기서 납입기간에 해당하는 ul을 찾기위해서는 :visible 속성으로 찾아야한다.
			 * :visible은 By.cssSelector()로는 동작하지 않음. executeScript()를 통해서 실행해야 함.
			 */
			script = "return $('ul[id^=sfddropdown-menu]:visible')[0]";
			WebElement $napTermUl = (WebElement) helper.executeJavascript(script);
			selectLiByTextFromUl($napTermUl, expectedNapTerm);


			$napTermSpan = $napTermSpan.findElement(By.xpath(".//span[@class='label']"));
			actualNapTerm = $napTermSpan.getText();


			//비교
			super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);



		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
			throw new SetNapTermException(e, exceptionEnum.getMsg());
		}
	}

	@Override
	public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
		String title = "가입금액";
		CrawlingProduct info = (CrawlingProduct) obj[0];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();


		String expectedAssureMoney = info.getAssureMoney();
		String actualAssureMoney = "";
		String script = "";


		logger.info("주계약 보험료 세팅");
		mainTreaty.monthlyPremium = expectedAssureMoney;


		try {

			//가입금액을 원수사의 가입금액 포맷에 맞게 text값 수정
			expectedAssureMoney = (Integer.parseInt(expectedAssureMoney) / 10000) + "만원씩";


			//가입금액 세팅을 위해 버튼 클릭
			WebElement $assureMoneySpan = driver.findElement(By.id("dropdown-paymentPremium"));
			WebElement $assureMoneyButton = $assureMoneySpan.findElement(By.xpath(".//button[@class[contains(., 'btn-adropdown')]]"));
			click($assureMoneyButton);


			/**
			 * ul에서 가입금액을 선택해야 하는 구조다.
			 * 여기서 가입금액에 해당하는 ul을 찾기위해서는 :visible 속성으로 찾아야한다.
			 * :visible은 By.cssSelector()로는 동작하지 않음. executeScript()를 통해서 실행해야 함.
			 */
			script = "return $('ul[id^=sfddropdown-menu]:visible')[0]";
			WebElement $assureMoneyUl = (WebElement) helper.executeJavascript(script);
			selectLiByTextFromUl($assureMoneyUl, expectedAssureMoney);


			$assureMoneySpan = $assureMoneySpan.findElement(By.xpath(".//span[@class='label']"));
			actualAssureMoney = $assureMoneySpan.getText();


			//비교
			super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);



		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
			throw new SetAssureMoneyException(e, exceptionEnum.getMsg());
		}
	}
}



//package com.welgram.crawler.direct.fire.sfi;
//
//import com.google.gson.Gson;
//import com.welgram.common.WaitUtil;
//import com.welgram.crawler.direct.fire.CrawlingSFI;
//import com.welgram.crawler.general.CrawlingProduct;
//import com.welgram.crawler.general.PlanAnnuityMoney;
//import com.welgram.crawler.general.PlanReturnMoney;
//import java.util.ArrayList;
//import java.util.List;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.events.EventFiringWebDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//
//public class SFI_ASV_D004 extends CrawlingSFI { // 삼성화재 다이렉트 연금저축보험(유배당)
//
//	public static void main(String[] args) {
//		executeCommand(new SFI_ASV_D004(), args);
//	}
//
//	@Override
//	protected boolean scrap(CrawlingProduct info) throws Exception {
//
//		// 연금저축 가입 연령 체크
//		if (info.napTerm.contains("년")) {
//
//			// 최대 가입 연령 = (연금개시나이 - 납입기간)세
//			int maxAge = Integer.parseInt(info.annAge) - Integer.parseInt(info.napTerm.replaceAll("년", "").trim());
//			logger.info("최대 가입 연령 : " + maxAge);
//			logger.info("가입 나이 : " + info.age);
//
//			if (maxAge < Integer.parseInt(info.age)) {
//				logger.info("최대 가입 연령 초과");
//				return true;
//			}
//		}
//
//		closeModal();
//
//		waitForLoading();
//		WaitUtil.waitFor(2);
//
//		logger.info("생년월일");
//		helper.doSendKeys2(By.id("birthS-input"), info.fullBirth);
//
//		logger.info("성별");
//		if (Integer.toString(info.gender).equals("0")) { // 남자
//			front_doClick(By.cssSelector("#gender-radio > label:first-child"));
//		} else if (Integer.toString(info.gender).equals("1")) { // 여자
//			front_doClick(By.cssSelector("#gender-radio > label:last-child"));
//		}
//
//		logger.info("보험료 계산하기 버튼");
//		front_doClick(By.id("btn-next-step"));
//		waitForLoading();
//		WaitUtil.waitFor(3);
//
//		logger.info("(고정)납입주기 : " + info.napCycle);
//		// 대부분 월납으로 가설하므로 매월로 설정되어 있는 부분 그대로 둡니다.
//
//		logger.info("가입금액: " + info.assureMoney);
//		selectDropDown_assureMoeny("dropdown-paymentPremium", info.assureMoney);
//
//		logger.info("납입기간: " + info.napTerm);
//		selectDropDown_front("dropdown-paymentPeriod", info.napTerm.replaceAll("[^0-9]",""));
//
//		logger.info("연금개시나이: " + info.annAge);
//		selectDropDown_front("dropdown-receiveAge", info.annAge);
//
//		logger.info("연금수령타입: " + info.annuityType);
//		selectDropDown_front("dropdown-receivePeriod", info.annuityType.replaceAll("[^0-9]",""));
//
//		logger.info("연금수령주기: " + "매년");
//		selectDropDown_front("dropdown-receiveCycle", "01");
//
//		logger.info("예상 연금수령액 확인");
//		helper.doClick(By.id("show-body"));
//		waitForLoading();
//		WaitUtil.loading(4);
//
//		logger.info("스크린샷 찍기");
//		takeScreenShot(info);
//		WaitUtil.waitFor(1);
//
//		String fixedAnnuityPremium = helper.waitVisibilityOfElementLocated(By.cssSelector("strong.blind")).getText().replaceAll("[^0-9]","");
//		info.annuityPremium = fixedAnnuityPremium;
//		info.fixedAnnuityPremium = fixedAnnuityPremium;
//
//		logger.info("예상 연금수령액 : " + info.annuityPremium);
//		logger.info("예상 확정 연금수령액 : " + info.fixedAnnuityPremium);
//
//		//2021-12-09기준으로 확정 10년 고정
//		if(info.annuityType.equals("확정 10년")){
//			PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
//			planAnnuityMoney.setFxd10Y(driver.findElement(By.cssSelector("strong.blind")).getText().replaceAll("[^0-9]",""));    //확정 10년
//			logger.info("확정 10년 : "+planAnnuityMoney.getFxd10Y());
//			info.planAnnuityMoney = planAnnuityMoney;
//		}
//
//		else if(info.annuityType.equals("확정 25년")){
//			PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
//			planAnnuityMoney.setFxd25Y(driver.findElement(By.cssSelector("strong.blind")).getText().replaceAll("[^0-9]",""));    //확정 10년
//			logger.info("확정 25년 : "+planAnnuityMoney.getFxd25Y());
//			info.planAnnuityMoney = planAnnuityMoney;
//		}
//
//		logger.info("해약환급금 가져오기");
//		helper.doClick(By.cssSelector("li.box-result.plan-box2 dl:first-child dd span"));
//		waitForLoading();
//
//		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
//		WaitUtil.loading(3);
//		element = helper.waitPresenceOfElementLocated(By.id("calc-table_low"));
//		wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(By.id("calc-table_low"), By.tagName("tr")));
//		elements = element.findElements(By.cssSelector("tbody tr"));
//		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
//		int scrollTop = 0;
//
//		for (WebElement tr : elements) {
//
//			String term 			= tr.findElements(By.tagName("td")).get(0).getText();
//			logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
//			logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
//			String premiumSum 		= tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
//			logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);
//			String returnMoneyMin 	= tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
//			logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
//			String returnRateMin 	= tr.findElements(By.tagName("td")).get(3).getText();
//			logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);
//			String returnMoney 		= tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
//			logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
//			String returnRate 		= tr.findElements(By.tagName("td")).get(5).getText();
//			logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
////				String returnMoneyAvg 	= tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "");
////				logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
////				String returnRateAvg 	= tr.findElements(By.tagName("td")).get(7).getText();
////				logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);
//
//			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//
//			planReturnMoney.setTerm(term);
//			planReturnMoney.setPremiumSum(premiumSum);
//			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
//			planReturnMoney.setReturnRateMin(returnRateMin);;
//			planReturnMoney.setReturnMoney(returnMoney);
//			planReturnMoney.setReturnRate(returnRate);
////				planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
////				planReturnMoney.setReturnRateAvg(returnRateAvg);
//
//			planReturnMoneyList.add(planReturnMoney);
//
//			info.returnPremium = returnMoney;
//
//			scrollTop += 46;
//			eventFiringWebDriver.executeScript("document.querySelector('#inner-scroll').scrollTop = " + scrollTop );
//		}
//
//		info.setPlanReturnMoneyList(planReturnMoneyList);
//
//		logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
//
//		// 해약환급금 창 닫기
//		front_doClick(By.id("btn-confirm"));
//
//		info.getTreatyList().get(0).monthlyPremium = info.assureMoney;
//		logger.info("월보험료 : " + info.getTreatyList().get(0).monthlyPremium);
//
//		return true;
//	}
//
//	protected void selectDropDown_assureMoeny (String id, String value) throws Exception {
//		boolean found = false;
//
//		WebElement button = helper.waitElementToBeClickable(By.id(id));
//		helper.doClick(button);
//		List<WebElement> ulList = driver.findElements(By.cssSelector("ul.sfd-dropdown-menu.dropdown-menu.scrollable-menu.overflow_scrollable"));
//		List<WebElement> liList = new ArrayList<WebElement>();
//
//		for (WebElement ul : ulList ) {
//			if (ul.getAttribute("style").contains("display: block")) {
//				liList.addAll(ul.findElements(By.tagName("li")));
//				break;
//			}
//		}
//
//		for (WebElement li : liList) {
//			String dataValue = li.getAttribute("data-value");
//			logger.info("보기 : " + dataValue );
//			if (dataValue.equals("ETC")) {
//				li.findElement(By.xpath("a")).click();
//				found = true;
//				break;
//			}
//		}
//
//		if (!found) {
//			throw new Exception( value + "찾을 수 없습니다.");
//		}
//		WaitUtil.loading(2);
//		WebElement el = driver.findElement(By.cssSelector("#inputData"));
//		helper.doClick(el);
//
//		el.clear();
//		String assureMoney = Integer.parseInt(value) / 10000 + "";
//		el.sendKeys(assureMoney);
//
//		helper.doClick(driver.findElement(By.cssSelector("#btn-confirm")));
//
//		WaitUtil.loading(1);
//	}
//
//}