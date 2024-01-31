package com.welgram.crawler.direct.fire;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.SaleChannel;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.NotFoundPensionAgeException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.comparer.impl.PlanTreatyComparer;

import com.welgram.crawler.direct.fire.sfi.common.PlanState;
import com.welgram.crawler.general.*;

import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.scraper.Scrapable;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.util.function.Function;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author SungEun Koo <aqua@welgram.com> 삼성
 */

public abstract class CrawlingSFI extends SeleniumCrawler implements Scrapable {

	@Override
	public void setNapCycleNew(Object obj) throws SetNapCycleException {

	}

	//크롤링 옵션 정의 메서드
	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setBrowserType(CrawlingOption.BrowserType.Chrome);
		option.setImageLoad(true);
		option.setUserData(false);
	}

	/**
	 * 안내 모달창 닫기 메서드
	 * 삼성화재 마케팅에 따라 안내 및 이벤트 모달창 갯수가 달라짐. 떠있는 모달창을 모두 닫고 싶을 때 사용
	 */
	protected void closeModal() {
		try {
			waitForLoading();
			WaitUtil.loading(1);

			// 3초 정도 모달이 나타나길 기다리는 게 좋을 것 같다.
			new WebDriverWait(driver, Duration.ofSeconds(6).getSeconds())
				.until( driver -> {
					List<WebElement> modals = driver.findElements(By.xpath("//a[@title='팝업닫기']"));
					return modals.stream().anyMatch(WebElement::isDisplayed);
				});

			// 팝업창 전부 정리
			helper.executeJavascript("$('a.btn-popup-x').click()");

		} catch (Exception e) {
			logger.info(e.getClass().getSimpleName());
			logger.info(e.getMessage());
			logger.info(Arrays.toString(e.getStackTrace()));
			logger.info("모달 확인창 없음");
		}
	}

	protected void closeModal(By btn) {
		// 안내 모달창 닫기
		try {
			waitForLoading();
			WaitUtil.loading(1);
			int attempt = 2;

			while (attempt > 0) {

				for (WebElement webElement : driver.findElements(btn)) {

					try {
						webElement.click();
					} catch (Exception e) {
						logger.info(webElement.getText() + " 버튼 클릭 실패");
					}
				}

				--attempt;
			}

		} catch (Exception e) {
			logger.info("확인창 없음.. ");
		}
	}

	protected void beforeEnter() throws CommonCrawlerException {
		try {
			WaitUtil.waitFor(1);
			helper.click(
				By.xpath("//a[contains(.,'보험료 계산')]")
				, "보험료 계산 버튼"
			);
		} catch (Exception e) {
			throw new CommonCrawlerException(ExceptionEnum.FAIL, e);
		}
	}


	//특약별 가입, 미가입 처리
	protected void setTreaties(CrawlingProduct info) throws Exception{
		//모든 버튼을 가입으로 처리시켜놓는다(고급플랜의 경우 재진단암 진단비만 미가입 처리 돼있는 경우가 있다)
		int btnCount = driver.findElements(By.className("txt-toggle")).size();

		for(int i=0; i<btnCount; i++) {
			WebElement joinBtn = driver.findElements(By.className("txt-toggle")).get(i);

			//버튼이 보이게 스크롤 처리
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", joinBtn);

			if(joinBtn.getText().equals("미가입")) {
				joinBtn.findElement(By.xpath("parent::span")).findElement(By.tagName("label")).click();
			}
		}

		List<String> myTreatyList = new ArrayList<>();
		List<String> targetTreatyList = new ArrayList<>();
		String strNum = "^[0-9]+$";
		String productMasterTreatyList = "";

		for(CrawlingTreaty myTreaty : info.treatyList) {

			if(info.insTerm.equals("20년")){
				if(!myTreaty.treatyName.contains("(10년 갱신)")){
					productMasterTreatyList = myTreaty.treatyName.replace("(5년 갱신)", "(10년 갱신)");
					logger.info("보기가 20년인 경우에는 10년갱신으로 수정 : "+productMasterTreatyList);
					myTreatyList.add(productMasterTreatyList);
					continue;
				}
			}

			if(info.insTerm.equals("15년")){
				if(!myTreaty.treatyName.contains("5년 갱신")){
					productMasterTreatyList = myTreaty.treatyName.replace("(10년 갱신)", "(5년 갱신)");
					logger.info("보기가 15년인 경우에는 5년갱신으로 수정 : "+productMasterTreatyList);
					myTreatyList.add(productMasterTreatyList);
					continue;
				}
			}

			myTreatyList.add(myTreaty.treatyName);
		}

		int targetSize = driver.findElements(By.cssSelector("#coverage-list .coverage-item td:nth-child(1)")).size();

		for(int i=0; i<targetSize; i++) {
			WebElement targetEl = driver.findElements(By.cssSelector("#coverage-list .coverage-item")).get(i);

			//특약명이 보이게 스크롤 처리
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", targetEl);

			String treatyName = targetEl.findElement(By.cssSelector("td:nth-child(1)")).getText().trim();
			String guaranteeMoney = targetEl.findElement(By.cssSelector("td:nth-child(3)")).getText().trim();
			logger.info("treatyName :: " + treatyName + " :: " +guaranteeMoney);
			targetTreatyList.add(treatyName);
		}

		if(targetTreatyList.size() < myTreatyList.size()){

			myTreatyList.removeAll(targetTreatyList);
			String saveTreaty = "";
			for(int i=0; i<myTreatyList.size(); i++){
				logger.info("웹에 존재하지 않는 특약 목록 : "+myTreatyList.get(i));
				saveTreaty += myTreatyList.get(i)+" / ";
			}

			try{
				throw new Exception("웹에 특약보다 상품마스터의 특약이 더 많습니다."+"개수 : "+myTreatyList.size()+"개 -- "+"특약 목록 : "+saveTreaty);
			}catch (Exception e){
				throw e;
			}
		}
		//targetTreatyList에는 미가입 처리할 특약만 남게된다.
		targetTreatyList.removeAll(myTreatyList);

		for(int i=0; i<targetTreatyList.size(); i++){
			logger.info("현재 존재하지 않는 특약을 담아 놓은 것 : "+targetTreatyList.get(i));
		}

		elements = driver.findElement(By.cssSelector("#coverage-list")).findElements(By.cssSelector("tr"));

		int webTreatyLoop = elements.size();

		for(int i=0; i<webTreatyLoop; i++){
			try {
				if(elements.get(i).findElement(By.cssSelector("td:nth-child(4) > span > span")).getText().trim().equals("미가입")){
					logger.info("미가입인 특약을 가입하도록 클릭");
					elements.get(i).findElement(By.cssSelector("td:nth-child(4) > span > label")).click();
					WaitUtil.waitFor(1);

					try {
						WaitUtil.mSecLoading(800);
						driver.findElement(By.id("alert-message-area"));
						logger.info("특약을 가입처리를 하다가 알럿창 발생!");
						driver.findElement(By.cssSelector("#CommonAlert .modal-footer button")).click();
						WaitUtil.mSecLoading(500);
					}catch (Exception e){
					}
				}
			}catch (Exception e){
			}

			//특약명이 보이게 스크롤 처리
			if(webTreatyLoop > i+1) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elements.get(i + 1));
			}
		}

		boolean result = false;
		for(int i=0; i<targetTreatyList.size(); i++) {
			String treatyName = targetTreatyList.get(i);

			WebElement targetEl = driver.findElement(By.xpath("//td[contains(., '" + treatyName + "')]"));

			//특약명이 보이게 스크롤 처리
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", targetEl);

			WebElement joinBtn = targetEl.findElement(By.xpath("parent::tr")).findElement(By.cssSelector("td:nth-child(4)")).findElement(By.tagName("label"));
			String joinText = joinBtn.findElement(By.xpath("parent::span")).findElement(By.cssSelector(".txt-toggle")).getText();

			//현재 버튼의 상태가 가입이어야만 클릭시켜 미가입 처리를 한다.
			if (joinText.equals("가입")) {
				try {
					joinBtn.click();

					WaitUtil.mSecLoading(800);
					driver.findElement(By.id("alert-message-area"));
					logger.info("특약 미가입 처리를 하다가 알럿창 발생!");
					driver.findElement(By.cssSelector("#CommonAlert .modal-footer button")).click();
					WaitUtil.mSecLoading(500);

				} catch (NoSuchElementException e) {

				} catch (ElementClickInterceptedException e) {
					WaitUtil.mSecLoading(800);
					driver.findElement(By.cssSelector("#CommonAlert .modal-footer button")).click();
					WaitUtil.mSecLoading(500);
				}
			}

		}

		//확인 버튼 클릭!
		helper.click(By.id("btn-confirm"));
		waitForLoading();

		//Alert case 1 : 최저보험료로 가입하는 경우에는 '일부환급형'으로 진행된다는 알럿창이 뜰 수도 있다.
		try {
			driver.findElement(By.id("message"));
			driver.findElement(By.id("btn-confirm")).click();
		}catch(NoSuchElementException e) {

		}catch (ElementClickInterceptedException e) {

		}

		//Alert case 2 : 보험료를 최종으로 다시 계산했습니다 라는 알럿창이 뜰 수도 있다.
		try {
			driver.findElement(By.xpath("//div[contains(., '보험료를 최종으로 다시 계산했습니다.')]"));
			driver.findElement(By.id("btn-popup-x")).click();
			WaitUtil.waitFor(2);
		}catch(ElementClickInterceptedException e) {

		}catch(NoSuchElementException e) {

		}

	}

	protected void setSelectBox(String cssValue, String value) throws Exception {
		boolean result = false;

		elements = helper.waitPresenceOfElementLocated(By.cssSelector(cssValue)).findElements(By.tagName("option"));

		for (WebElement option : elements) {
			if (option.getText().contains(value)) {
				option.click();
				result = true;
				break;
			}
		}

		if (!result) {
			throw new Exception("selectBox 선택 오류!");
		}
	}

	protected void btnCalc() throws Exception {
		// 계산버튼 클릭
		helper.click(By.cssSelector("button.btn-main-blue"));
		logger.info("계산버튼 클릭");

		// 로딩이 끝날 때까지 기다림
		try {
			wait.until(ExpectedConditions.invisibilityOf(
				helper.waitVisibilityOfElementLocated(By.cssSelector("div.ui-loading-wrap"))));
		} catch (Exception e) {
		}

		WebElement modal = null;
		String message = "";

		try{
			// 알람 모달창이 떴을 때
			modal = helper.waitVisibilityOf(driver.findElement(By.id("modalAlertTxt")));
			message = modal.findElement(By.tagName("textarea")).getText();
			logger.info("에러 메세지: " + message);
			String amount = "";

			//다음과 같은 사유로 보험료 계산이 불가합니다. 설계번호=미채번, [120] 1회보험료가 부족합니다. 1회보험료를 55668 원 이상 입력하세요.
			if(message.contains("1회보험료가 부족합니다.")) {
				amount = message.substring(message.indexOf("1회보험료를") + 6, message.indexOf("원 이상")).replaceAll("[^0-9]", "").trim();
				logger.info("재입력할 보험료 금액 : " + amount);

				//해당 상품은 월납환산보험료 기준 10,000원 이상으로 설계하셔야 합니다.
			} else if (message.contains("원 이상으로 설계하셔야 합니다.")) {
				amount = message.substring(message.indexOf("월납환산보험료 기준") + 10, message.indexOf("원 이상")).replaceAll("[^0-9]", "").trim();
				logger.info("재입력할 보험료 금액 : " + amount);

				// 기타 다른 알람은 프로그램 종료시켜야
			} else {
				throw new Exception(message);
			}

			helper.click(By.id("__confirm"));
			logger.info("알림 모달창 확인버튼 클릭");

			// 모달창 사라질때까지 기다림
			try {
				wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.id("modalAlertTxt"))));
			} catch (Exception e) {

			}

			// 보험료 재입력
			sendKeys(amount);

			// 재귀. 알람창이 뜨지 않을 때까지.
			btnCalc();

		} catch (Exception e1){
			if (modal == null) {
				logger.info("알람창이 뜨지 않았습니다.");
			} else {
				logger.info("알람 메세지 ::" + message);
				throw new Exception(message);
			}
		}
	}

	protected void sendKeys(String text) throws InterruptedException {
		element = helper.waitVisibilityOfElementLocated(By.id("premaftertaxAm"));
		helper.click(element);
		logger.info("보험료 입력 box 클릭");
		element.clear();
		element.sendKeys(text);
		logger.info("보험료 " + text + " 원으로 재입력" );
	}

	// 특약보험료
	protected void getPremium(CrawlingTreaty info) throws InterruptedException {
		String premium = "";
		String getName = "";
		int height = 0;

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("$('#mCSB_1_container').attr('style', 'position: relative; top:-" + height + "px; left:0px;');");
		driver.findElement(By.cssSelector(".tbl-scroll-head")).click();

		elements = driver.findElements(By.cssSelector("#p_CoverageInfo tbody tr td[id='p_zzobjtNmTt'] label"));

		for (int i = 0; i < elements.size(); i++) {
			getName = elements.get(i).getText().trim();
			/*
			 * if ("".equals(getName)) { js.executeScript("$('#mCSB_1_container').attr('style', 'position: relative; top:-" + height + "px; left:0px;');"); height = height + 10; i = i - 1; continue; }
			 */

			if (getName.equals(info.treatyName)) {
				height = height + 15;
				((JavascriptExecutor) driver).executeScript("$('#mCSB_1_container').attr('style', 'position: relative; top:-" + height + "px; left:0px;');");

				element = elements.get(i).findElement(By.xpath("parent::*")).findElement(By.xpath("parent::*"));
				premium = element.findElement(By.id("p_premaftertaxAm")).getText().replaceAll("[^0-9]", "");
				info.monthlyPremium = premium;
				logger.debug(info.treatyName + " 월 보험료: " + premium);
				break;
			}
		}
	}


	protected void front_doClick(By locator) throws Exception {
		waitForLoading();
		helper.click(locator);
	}

	protected void front_doClick(WebElement webElement) throws Exception {
		waitForLoading();
		helper.click(webElement);
	}

	protected void front_doSendKeys(By by, String value) throws InterruptedException {
		waitForLoading();

		helper.waitVisibilityOfElementLocated(by);
		element = helper.waitElementToBeClickable(by);
		element.click();
		element.clear();
		element.sendKeys(value);
	}

	protected void selectDropDown_front (String id, String value) throws Exception {
		boolean found = false;

		WebElement button = helper.waitElementToBeClickable(By.id(id));
		helper.click(button);
		List<WebElement> ulList = driver.findElements(By.cssSelector("ul.sfd-dropdown-menu.dropdown-menu.scrollable-menu.overflow_scrollable"));
		List<WebElement> liList = new ArrayList<WebElement>();

		for (WebElement ul : ulList ) {
			if (ul.getAttribute("style").contains("display: block")) {
				liList.addAll(ul.findElements(By.tagName("li")));
				break;
			}
		}

		for (WebElement li : liList) {
			String dataValue = li.getAttribute("data-value");
			logger.info("보기 : " + dataValue );
			if (dataValue.equals(value)) {
				li.findElement(By.xpath("a")).click();
				found = true;
				break;
			}
		}

		if (!found) {
			throw new NotFoundPensionAgeException("납입기간 : "+ value + "를 선택할 수 없습니다.");
		}
	}

	public static String getAssureMoneyStr(int amount) {
		DecimalFormat decFormat = new DecimalFormat("###,###");

		String unit = "";
		String amountStr = String.valueOf(amount);

		if (amountStr.contains("00000000")) {
			unit = "억";
			amountStr = amountStr.replace("00000000", "");
		} else if (amountStr.contains("0000")) {
			unit = "만";
			amountStr = amountStr.replace("0000", "");
		}

		amountStr = decFormat.format(Integer.parseInt(amountStr));
		amountStr = amountStr + unit + "원";

		return amountStr;
	}

	public static int getAssureMoneyInt(String amountStr) {

		if (amountStr.contains("(")) {
			String 괄호내용 = amountStr.substring(amountStr.indexOf("(") + 1, amountStr.indexOf(")"));
			amountStr = amountStr.replace(괄호내용, "");
		}

		amountStr = amountStr
			.substring(amountStr.indexOf("일") + 1)
			.replace("백", "00")
			.replace("천", "000")
			.replace("만", "0000")
			.replace("억", "00000000")
			.replaceAll("\\D", "");

		return amountStr.equals("") ? 0 : Integer.parseInt(amountStr); // 숫자를 제외한 문자열 제거
	}

	public static String getRefinedPageTreatyName(String pageTreatyName) {
		return pageTreatyName
			.substring(0, (pageTreatyName.contains("*") ?  pageTreatyName.indexOf("*"): pageTreatyName.length()))
			.replace("보장명", "")
			.replace("보장 항목", "")
			.replace("UPGRADE", "")
			.replace("NEW", "")
			.replace("더보기", "")
			.replace("HOT", "")
			.replace("툴팁", "")
			.replaceAll("\\n", "")
			.replaceAll(System.getProperty("line.separator"), "").trim();
	}

	// 원수사의 해당 가설 특약리스트 가져오기 (pagePlanTreatyList)
	// -- type 1: table에서 바로 가져오기 (not 한번에 보장 바꾸기)
	// -- 대표상품의 경우 미가입 => 가입으로 변경처리해서 가져온다. (나머지 일반 가설은 원수사 상태 그대로)
	protected List<CrawlingTreaty> getPageTreatyListFromTable(CrawlingProduct info, Boolean customizedPlan) throws Exception {

		List<WebElement> pageTr = helper.waitPresenceOfElementLocated(By.cssSelector("tbody[id^='coverage']")).findElements(By.cssSelector("tr"));
		List<CrawlingTreaty> newTreatyList = new ArrayList<>();

		for (int i = 0; i < pageTr.size(); i++) {  // <원수사 treaty loop>

			waitForLoading();

			WebElement tr = helper.waitPresenceOfElementLocated(
				By.cssSelector("tbody[id^='coverage']")).findElements(By.cssSelector("tr")).get(i);

			// 스크롤 : 해당 특약 정보를 가지고 올 수 있도록
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

			PlanState planState;               // planState:  가입 / 미가입 / 가입불가 상태
			String pageTreatyName =            // pageTreatyName: 원수사 보장명
				getRefinedPageTreatyName(tr.findElement(By.cssSelector("th")).getText());
			WebElement pageAssureMoneyEl;    // pageAssureMoneyEl: 보장금액이 쓰여진 엘리먼트
			int pageAssureMoney = 0;        // pageAssureMoney: 보장금액

			// 카테고리별로 보장금액이 쓰여진 엘리먼트 위치가 조금씩 다르다
			if (info.productCode.contains("SFI_OST")) {
				pageAssureMoneyEl = tr.findElement(By.cssSelector("td.selectedPlan > span.cont"));
			} else if (info.productCode.contains("SFI_DMT")) {
				pageAssureMoneyEl = tr.findElement(By.cssSelector("td"));
			} else {
				pageAssureMoneyEl = tr.findElement(By.cssSelector("td.select"));
			}
			String pageAssureMoneyCell = pageAssureMoneyEl.getText().trim();

			// customizedPlan = true, 원수사에서 제시된 실속,표준,고급 디폴트 플랜이 아니라 보답에서 새로 정의한 플랜이라는 의미
			boolean hasTreaty = info.treatyList.stream().anyMatch(t -> pageTreatyName.contains(t.treatyName));

			// 더보기 버튼
//			WebElement seeMoreBtn = tr.findElement(By.cssSelector("th button"));
			WebElement seeMoreBtn = tr.findElement(By.xpath("th//button[text()='더보기']"));

			// 가입 가능한 상태. 대표가설의 경우 가입시 금액을 가져와야 함
			if (pageAssureMoneyCell.contains("미가입")) {
				planState = PlanState.미가입;

				// 보답임의설계인데 해당 특약이 포함된 경우 : 가입금액 가져오고 가입버튼도 눌러놓기
				if (customizedPlan && hasTreaty) {
					pageAssureMoney = processTreatyAndGetAssureMoney(seeMoreBtn, PlanState.가입);
					planState = PlanState.가입;
				}

			} else if (pageAssureMoneyCell.contains("-")) { // 가입불가일 경우
				planState = PlanState.가입불가;

			} else { // 특약이 이미 가입된 상태.

				//보답임의설계에 해당 원수사 특약이 포함되지 아니한 경우 : 미가입처리
				if (customizedPlan && !hasTreaty) {
					processTreatyAndGetAssureMoney(seeMoreBtn, PlanState.미가입);
					planState = PlanState.미가입;

				} else { // 금액 가져오기.

					pageAssureMoney = getAssureMoneyInt(pageAssureMoneyCell); // 상세 모달에 들어가지 않아도 row에서 바로 보이는 가입금액

					// 대표상품이거나 보답임의설계인데 해당 특약이 포함된 경우 : 가입금액 가져오고 가입버튼도 눌러놓기
/*					if (info.mainYn.equals("Y")) {
						// 팝업창을 열어 가장 높은 가입금액을 가져온다.
						pageAssureMoney = processTreatyAndGetAssureMoney(seeMoreBtn, PlanState.가입);
					} else {
						pageAssureMoney = getAssureMoneyInt(pageAssureMoneyCell); // 상세 모달에 들어가지 않아도 row에서 바로 보이는 가입금액
					}*/

					planState = PlanState.가입;
				}
			}

			// 가입 상태인 특약을 원수사 특약 리스트에 넣어준다
			if (planState == PlanState.가입) {

				CrawlingTreaty crawlingTreaty = new CrawlingTreaty();
				crawlingTreaty.treatyName = pageTreatyName;
				crawlingTreaty.assureMoney = pageAssureMoney;
				newTreatyList.add(crawlingTreaty);
			}
		}

//		return pagePlanTreatyList;
		return newTreatyList;
	}

	// 더보기 버튼을 눌러 특약의 상세 모달에서 가입/미가입처리하고, 가입처리한 경우 특약 가입금액 리턴하기
	private int processTreatyAndGetAssureMoney(WebElement seeMoreBtn, PlanState planState) throws Exception {
		int pageAssureMoney = 0;

		// 더보기 버튼 누르기
		WaitUtil.loading(1);
		waitForLoading();
		helper.click(seeMoreBtn);

		// 가입금액 li들 가져오기
		List<WebElement> amountLiList = helper.waitVisibilityOfAllElementsLocatedBy(
			By.cssSelector("div[id*='coverage-value'] li"));

		WebElement li;

		if (planState.equals(PlanState.가입)) { // 가입 처리

			li = amountLiList.get(amountLiList.size() - 1); // 제시된 가입금액 중 최대 가입금액
			String pageAssureMoneyStr = li.findElement(By.cssSelector("p.label")).getText();
			pageAssureMoney = getAssureMoneyInt(pageAssureMoneyStr);

		} else { // 미가입 처리
			li = amountLiList.get(0);
		}

		// 가입(가입금액) 혹은 미가입 누르기
		helper.click(li);

		// 확인 버튼 눌러서 모달창 닫기
		helper.click(By.cssSelector("#btn-confirm"));
		waitForLoading();

		// 연계조건 안내 알람이 뜨면 처리하기
		WaitUtil.loading(1);
		closeModal(By.cssSelector("div[class='modal-dialog'] #btn-confirm"));
		waitForLoading();

		return pageAssureMoney; // 선택한 가입금액 리턴하기
	}


	// 원수사의 해당 가설 특약리스트 가져오기 (pagePlanTreatyList)
	// -- type 1: table에서 바로 가져오기 (not 한번에 보장 바꾸기)
	// -- 대표상품의 경우 미가입 => 가입으로 변경처리해서 가져온다. (나머지 일반 가설은 원수사 상태 그대로)
	// todo -- 일반상품의 경우 가설에서 불포함된 특약이 가입되어 있는 경우 미가입으로 변경하는 코드 짜야함
	protected List<CrawlingTreaty> getPageTreatyListFromTable_smart() {

		List<WebElement> pageTr = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("tr.coverage-item"));
		List<CrawlingTreaty> newTreatyList = new ArrayList<>();

		for (WebElement tr : pageTr) {

			// 스크롤 : 해당 특약 정보를 가지고 올 수 있도록
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

			// ageTreatyName: 원수사 보장명
			// pageAssureMoney: 해당 가입설계의 금액
			// planState:  가입 / 미가입 / 가입불가 상태

			String pageTreatyName = getRefinedPageTreatyName(tr.findElement(By.cssSelector("td.coverage-title")).getText());
			int pageAssureMoney = getAssureMoneyInt(tr.findElement(By.cssSelector("td.coverage-amount")).getText().trim());

			CrawlingTreaty crawlingTreaty = new CrawlingTreaty();
			crawlingTreaty.treatyName = pageTreatyName;
			crawlingTreaty.assureMoney = pageAssureMoney;
			newTreatyList.add(crawlingTreaty);
		}

//		return pagePlanTreatyList;
		return newTreatyList;
	}

	protected enum TreatyPlace {
		TABLE,
		MODAL
	}

	// type 2: 한번에 보장 바꾸기창에서 가져오기
	protected List<CrawlingTreaty> getPageTreatyListFromModal(CrawlingProduct info)
		throws CommonCrawlerException, InterruptedException {

		boolean DTL = info.getProductCode().contains("DTL");

		// ##### 사용하는 메서드들 #####
		Function<WebElement, String> getPageTreatyNameFromTr = (tr) -> getRefinedPageTreatyName(
			tr.findElement(By.cssSelector("td:nth-child(1)")).getText());

		Function<WebElement, String> getPageAssureMoneyStrFromTr = (tr) -> tr.findElement(By.cssSelector("td:nth-child(3)")).getText().trim();

		Function<String, Integer> getPageAssureMoenyAsInteger = (str) -> getAssureMoneyInt(str);

		Function<WebElement, Integer> getPageAssureMoney = getPageAssureMoneyStrFromTr.andThen(getPageAssureMoenyAsInteger);

		// ##### 로직 시작 #####
		logger.info("CrawlingSFI.getPageTreatyListFromModal :: 한번에 보장 바꾸기 창 진입 :: 원수사 가입설계 내용 담기");

		// 한번에 보장 변경 클릭
		clickTreatyPageBtn();

		List<CrawlingTreaty> newTreatyList = new ArrayList<>();

		List<WebElement> pageTr = helper.waitPresenceOfElementLocated(By.cssSelector("#coverage-list")).findElements(By.cssSelector("tr"));
		int trCount = pageTr.size();

		String tempPageTreatyName = ""; // 치아보험에서 사용
		int tempMaxPageAssureMoney = 0;

		for (int i = 1 ; i <= trCount; i++) {

			String trCssSelector = "#coverage-list > tr:nth-child(" + i + ")";
			WebElement tr = driver.findElement(By.cssSelector(trCssSelector));
			WebElement treatyNameTd = tr.findElement(By.cssSelector("td:nth-child(1)"));

			// 스크롤
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

			String pageTreatyName;        // 원수사 특약 보장명
			int pageAssureMoney = 0;         // 원수사 특약 가입금액
			PlanState planState;        // 원수사 가입 / 미가입 상태   (한번에 보장 바꾸기 창에서 가입불가 상품은 뜨지 않는다.)

			String tdType = "";
			String tdClass = treatyNameTd.getAttribute("class");

			if (tdClass.contains("title-group")) {
				tdType = DTL ? "DTL_TITLE" : "TITLE";

			} else if (DTL && tdClass.contains("group-last")) { // ne-group-tbl group-last
				tdType = "DTL_LAST_GRT"; // group-last가 나올때까지 가입금액 max를 구해야 한다.

			} else if (DTL && tdClass.contains("ne-group-tbl")) {
				tdType = "DTL_GRT";

			}

			pageTreatyName = getPageTreatyNameFromTr.apply(tr);

			// ex) [암진단비] 처럼 특약 그룹 제목은 건너뛴다. 특약명이 아님.
			if (tdType.contains("TITLE")) {
				// 예외사항 - 삼성화재 어린이보험 - 치아특약 불포함시키는 코드
				if (info.getProductCode().contains("CHL") && (pageTreatyName.contains("치아") || pageTreatyName.contains("예약"))) break;
				if (tdType.equals("DTL_TITLE")) {
					tempPageTreatyName = pageTreatyName;
				}
				continue;

			} else if (tdType.equals("DTL_GRT")) { // 치아보험
				tempMaxPageAssureMoney = Math.max(tempMaxPageAssureMoney, getPageAssureMoney.apply(tr));

			} else if (tdType.equals("DTL_LAST_GRT")) {
				tempMaxPageAssureMoney = Math.max(tempMaxPageAssureMoney, getPageAssureMoney.apply(tr));// 치아보험 grt 마지막
				pageAssureMoney = tempMaxPageAssureMoney;

			} else {
				pageAssureMoney = getPageAssureMoney.apply(tr);
			}

            /*
			필수가입일 경우 td element만 있고 하위에 자식 태그가 없음 (span, input, label 등이 없음)
			그외 나머지 경우엔 (가입 / 미가입 상태) 하위 자식 요소들이 있음
			 */
			WebElement planStateTd = tr.findElement(By.cssSelector("td:nth-child(4)"));
			if (planStateTd.getText().replaceAll("\\s","").contains("필수가입")) {
				planState = PlanState.필수가입;
			} else {
				planState = getPlanState(info, trCssSelector, pageTreatyName, planStateTd); // 대표가설일 경우 가입 상태가 되도록 토글 버튼을 클릭해서 처리
			}

			if ((planState.equals(PlanState.가입) || planState.equals(PlanState.필수가입)) &&
				!tdType.contains("TITLE") && !tdType.equals("DTL_GRT")) {

				CrawlingTreaty crawlingTreaty = new CrawlingTreaty();

				if (tdType.contains("DTL_LAST_GRT")) {
					crawlingTreaty.treatyName = tempPageTreatyName;
					crawlingTreaty.assureMoney = tempMaxPageAssureMoney;
					tempPageTreatyName = ""; // 초기화
				} else {
					crawlingTreaty.treatyName = pageTreatyName;
					crawlingTreaty.assureMoney = pageAssureMoney;
				}

				newTreatyList.add(crawlingTreaty);
			}
		}

		helper.click(By.id("btn-confirm"));

		this.waitForLoading();
		closeModal();

		return newTreatyList;
	}

//	@NotNull
private PlanState getPlanState(CrawlingProduct info,
	String trCssSelector,
	String pageTreatyName,
	WebElement _4thTd)
	throws InterruptedException, CommonCrawlerException {

	PlanState planState = PlanState.미가입;

	String planStateTxt = _4thTd.findElement(By.cssSelector("span > span")).getText(); // 가입 미가입 표시 텍스트

/*		if (planStateTxt.contains("미가입") && info.mainYn.equals("Y")) { // 대표가설일 경우 전부 가입처리
			int attempt = 0;
			do {
				helper.doClick(_4thTd.findElement(By.cssSelector("span > label")),
					"대표가설이므로 미가입 특약 가입버튼 클릭");
				WaitUtil.loading(2);

				planStateTxt = driver.findElement(By.cssSelector(trCssSelector + " td:nth-child(4) > span > span ")).getText();
				attempt++;
			} while (planStateTxt.contains("미가입") && attempt < 2);

			if (planStateTxt.contains("미가입")) {
				throw new CommonCrawlerException(
					ExceptionEnum.ERROR_BY_TREATIES_GETTING,
					pageTreatyName + " - 가입 처리 실패"
				);
			}
		}*/

	if (!planStateTxt.contains("미가입")) {
		planState = PlanState.가입;
	}

	return planState;
}

	// type 3: 모바일 페이지에서 가져오기
	protected List<CrawlingTreaty> getPageTreatyListFromMobile(CrawlingProduct info, int planColNo) throws Exception {

		logger.info("모바일 :: 원수사 가입설계 내용 담기");
		List<CrawlingTreaty> newTreatyList = new ArrayList<>();
		String rowGroupCss = "#calc-dambolist-table > div.result-list > div.row-group";
		List<WebElement> rowGroupList = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector(rowGroupCss));

		for (WebElement rowGroup : rowGroupList) {

			String span = "";
			WebElement titNameDiv = rowGroup.findElement(By.cssSelector("div.tit-name"));
			try {
				span = titNameDiv.findElement(By.tagName("span")).getText();
			} catch (Exception e) {
				// span 태그 없음
			}

			String pageTreatyName = getRefinedPageTreatyName(
				titNameDiv.getText().replaceAll(span, ""));

			String pageTreayAmountStr = rowGroup.findElement(
				By.cssSelector("div.row:nth-child(" + planColNo + ") > button > span")).getText();

			if (!pageTreayAmountStr.contains("-")) {

				int pageAssureMoney = getAssureMoneyInt(pageTreayAmountStr);
				PlanState planState = PlanState.가입;

				logger.info("-------------------------------");
				logger.info("원수사 특약 ::" + pageTreatyName);
				logger.info("원수사 특약 가입금액 (한글) :: " + pageTreayAmountStr);
				logger.info("원수사 특약 가입금액 (숫자) :: " + pageAssureMoney);
				logger.info("원수사 특약 가입상태 :: " + planState);

				CrawlingTreaty crawlingTreaty = new CrawlingTreaty();
				crawlingTreaty.treatyName = pageTreatyName;
				crawlingTreaty.assureMoney = pageAssureMoney;
				newTreatyList.add(crawlingTreaty);
			}
		}

//		return pagePlanTreatyList;
		return newTreatyList;
	}

	protected void selectOption(By selectBy, By optionBy) throws InterruptedException {
		WebElement selectBox = helper.waitElementToBeClickable(selectBy);
		helper.click(selectBox);
		helper.click(selectBox.findElement(optionBy));
		helper.click(selectBox); // 한번 더 눌러주기
	}

	protected void setUserInfo(CrawlingProduct info) throws CommonCrawlerException {
		setBirthdayNew(info);
		setGenderNew(info);
	}

	@Override
	public void setBirthdayNew(Object obj) throws SetBirthdayException {
		try {
			CrawlingProduct info = (CrawlingProduct) obj;
			WebElement el = helper.waitPresenceOfElementLocated(By.cssSelector("input[id^='birth']"));
			helper.sendKeys2_check(el, info.fullBirth, "생년월일");
		} catch (Exception e) {
			throw new SetBirthdayException(e);
		}
	}

	@Override
	public void setGenderNew(Object obj) throws SetGenderException {
		try {
			CrawlingProduct info = (CrawlingProduct) obj;
			String infoGender = Gender.values()[info.gender].getDesc();

			Optional<WebElement> genderLabel = helper.waitVisibilityOfAllElementsLocatedBy(
					By.cssSelector("label[for^='gender']"))
				.stream().filter(
					label -> label.getText().contains(infoGender))
				.findFirst();

			helper.click(genderLabel.get(), "성별 선택 - ", infoGender );
		} catch (Exception e) {
			throw new SetGenderException(e);
		}
	}

	public void setJobNew(Object obj) throws SetJobException {

		try {
			CrawlingProduct info = (CrawlingProduct) obj;

			helper.click(By.id("job-button"), "직업찾기 버튼");

			// 직업 입력창 입력 가능한 상태가 되도록 기다리기
			wait.until(ExpectedConditions.attributeContains(By.id("sjob-tab-search"), "class", "active"));

			helper.sendKeys3_check(
				By.id("sjob-search-text"),
				"교사",
				"직업 입력");

			try {
				helper.click(
					By.cssSelector("#sjob-tab-search > div:nth-child(2) > div.ne-box-search > button"),
					"검색 버튼(돋보기 이미지)");
			} catch (Exception e) {
				helper.click(
					By.cssSelector("button[class^='btn-search']"),"검색 버튼(돋보기 이미지)");
			}

			helper.click(
				wait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//div[@id='sjob-search-result']//button[contains(.,'고등학교')]"))),
				"중고등학교교사");
			WaitUtil.loading(1);

			helper.click(
				driver.findElement(By.id("sjob-select-agree")).findElement(By.xpath("ancestor::label"))
				, "직업정보 고지 유의사항 체크");

			WebElement nextBtn = helper.waitElementToBeClickable(By.cssSelector(".modal-footer > .btn-next-step"));

			wait.until(driver -> !nextBtn.getAttribute("class").contains("disabled"));
			helper.click(nextBtn, "직업 찾기 모달창 - ", "다음 버튼");
		} catch (Exception e) {
			throw new SetJobException(e);
		}
	}

	@Override
	public void setRefundTypeNew(Object obj) throws SetRefundTypeException {
		try {
			CrawlingProduct info = (CrawlingProduct) obj;

			String productKind = info.getProductKind(); // 환급형태
			String jobDesc = "환급형태 선택";
			logger.info("CrawlingSFI.setReturnType :: {} :: {}", jobDesc, productKind);

			// 선택 가능한 환급형태
			List<WebElement> list = helper.waitVisibilityOfAllElementsLocatedBy(
				By.cssSelector("button[name='refundCls']")
			);

			// 일치하는 환급형태 버튼
			Optional<WebElement> matchedOpt = list.stream().filter(el -> {
				String returnType = "없음";
				if (productKind.equals("순수보장형")) {
					returnType = "순수보장형";
				} else if (productKind.contains("환급")) {
					returnType = "환급형";
				}

				return el.getText().contains(returnType);
			}).findFirst();

			// 없으면 throw
			matchedOpt.orElseThrow(
				() -> new SetRefundTypeException(productKind + "이 선택항목에 없습니다."));

			// 환급형태 클릭
			WebElement matchedEl = matchedOpt.get();
			if (!matchedEl.getAttribute("class").contains("active")) {
				helper.click(matchedEl, jobDesc, productKind);
				this.waitForLoading();
			}

		} catch (SetRefundTypeException e) {
			throw e;
		} catch (Exception e) {
			throw new SetRefundTypeException(e);
		}
	}

	protected void goToNext() throws CommonCrawlerException {
		try {
			helper.click(By.id("btn-next-step"), "보험료 계산하기 버튼");
			this.waitForLoading();
			closeModal();
		} catch (Exception e) {
			throw new CommonCrawlerException(ExceptionEnum.FAIL, e);
		}
	}

	@Override
	public void setInsTermNew(Object obj) throws SetInsTermException {
		try {
			WaitUtil.loading(3);

			CrawlingProduct info = (CrawlingProduct) obj;

			String insTerm = info.getInsTerm();
			String jobDesc = "보험기간 선택";
			logger.info("CrawlingSFI.setInsTerm :: {} :: {}", jobDesc, insTerm);
			List<WebElement> labels = null;

			try {
				// 선택가능한 보험기간
				WebElement ddEl = helper.waitPesenceOfAllElementsLocatedBy(
						By.cssSelector("dd.btn-group")).stream()
					.filter(dd -> dd.getAttribute("id").contains("insured-term") && dd.isDisplayed())
					.findAny().get();

				labels = ddEl.findElements(By.tagName("label"));
			} catch (Exception e) {
				throw new SetInsTermException(e, jobDesc + " 요소를 찾지 못했습니다.");
			}

			// 일치하는 보험기간
			Optional<WebElement> matched = labels.stream().filter(el ->
				el.getText().contains(insTerm)).findFirst();

			// 없으면 throw
			matched.orElseThrow(
				() -> new SetInsTermException(insTerm + "는 선택항목에 없습니다."));

			// 보험기간 클릭
			WebElement matchedEl = matched.get();
			if (!matchedEl.getAttribute("class").contains("active")) {
				helper.click(matchedEl, jobDesc, insTerm);
				this.waitForLoading();
			}

		} catch (SetInsTermException e) {
			throw e;
		} catch (Exception e) {
			throw new SetInsTermException(e);
		}
	}

	protected void waitForLoading(By... by) throws InterruptedException {

		WebDriverWait waitForSFI = new WebDriverWait(driver,
			Duration.ofSeconds(60).getSeconds());
		logger.info("CrawlingSFI.waitForLoading");

		waitForSFI.until(ExpectedConditions.invisibilityOfElementLocated(
			By.cssSelector("[(class*='loading') || (class*='Loading) || (id*='loading') || (id*='Loading')]")));
		if (by.length > 0) {
			for (By i : by) {
				wait.until(ExpectedConditions.invisibilityOfElementLocated(i));
			}
		}
	}

	@Override
	public void setNapTermNew(Object obj) throws SetNapTermException {
		CrawlingProduct info = (CrawlingProduct) obj;

		try {
			WaitUtil.loading(3);

			String napTerm = info.getNapTerm();
			String jobDesc = "납입기간 선택";
			logger.info("CrawlingSFI.setNapTerm :: {} :: {}", jobDesc, napTerm);
			List<WebElement> labels = null;

			try {
				// 선택가능한 납입기간
				WebElement ddEl = helper.waitPesenceOfAllElementsLocatedBy(
						By.cssSelector("dd.btn-group")).stream()
					.filter(dd -> dd.getAttribute("id").contains("payment-term"))
					.findAny().get();

				labels = ddEl.findElements(By.tagName("label"));
			} catch (Exception e) {
				throw new SetNapTermException(e, jobDesc + " 요소를 찾지 못했습니다.");
			}

			// 일치하는 납입기간
			Optional<WebElement> matched = labels.stream().filter(el ->
				el.getText().contains(napTerm)).findFirst();

			// 없으면 throw
			matched.orElseThrow(
				() -> new SetNapTermException(napTerm + "이 선택항목에 없습니다."));

			// 보험기간 클릭
			WebElement matchedEl = matched.get();
			if (!matchedEl.getAttribute("class").contains("active")) {
				helper.click(matchedEl, jobDesc, napTerm);
				this.waitForLoading();
			}

		} catch (SetNapTermException e) {
			throw e;
		} catch (Exception e) {
			throw new SetNapTermException(e);
		}
	}

	@Override
	public void setRenewTypeNew(Object obj) throws SetRenewTypeException {
		CrawlingProduct info = (CrawlingProduct) obj;

		try {
			String renewType = info.getProductType().name();
			String jobDesc = "갱신형태 선택";
			logger.info("CrawlingSFI.setRenewType :: {} :: {}", jobDesc, renewType);
			List<WebElement> labels = null;

			try {
				// 선택가능한 갱신형태
				WebElement ddEl = helper.waitPesenceOfAllElementsLocatedBy(
						By.cssSelector("dd.btn-group")).stream()
					.peek(el -> logger.info(el.getText()))
					.filter(dd -> dd.getAttribute("id").contains("product-cls"))
					.findAny().get();

				labels = ddEl.findElements(By.tagName("label"));
			} catch (Exception e) {
				throw new SetRenewTypeException(e, jobDesc + " 요소를 찾지 못했습니다.");
			}

			// 일치하는 갱신형태
			Optional<WebElement> matched = labels.stream().filter(el ->
				el.getText().contains(renewType)).findFirst();

			// 없으면 throw
			matched.orElseThrow(
				() -> new SetRenewTypeException(renewType + "이 선택항목에 없습니다."));

			// 갱신형태 클릭
			WebElement matchedEl = matched.get();
			if (!matchedEl.getAttribute("class").contains("active")) {
				helper.click(matchedEl, jobDesc, renewType);
				this.waitForLoading();
			}
		} catch (SetRenewTypeException e) {
			throw e;
		} catch (Exception e) {
			throw new SetRenewTypeException(e);
		}
	}

	protected void setSubPlan(CrawlingProduct info) throws CommonCrawlerException, InterruptedException {

		WaitUtil.loading(3);

		String planSubName = info.planSubName; // (서브)플랜명
		String jobDesc = "플랜 선택";
		logger.info("CrawlingSFI.setPlan :: {} :: {}", jobDesc, planSubName);

		String subStr =
			planSubName.substring(0,
					planSubName.contains("플랜") ? planSubName.indexOf("플랜") : planSubName.length())
				.trim();

		// 선택 가능한 플랜
		List<WebElement> list = helper.waitPesenceOfAllElementsLocatedBy(
			By.cssSelector("thead#coverage-header label")
		);

		// 일치하는 플랜
		Optional<WebElement> matchedOpt = list.stream().filter( el ->
			el.getText().contains(subStr)).findFirst();

		// 없으면 throw
		matchedOpt.orElseThrow(
			() -> new CommonCrawlerException(info.planSubName + "이 선택항목에 없습니다."));

		WebElement matchedEl = matchedOpt.get();
		if (!matchedEl.getAttribute("class").contains("active")) {
			helper.click(matchedEl, jobDesc, planSubName);
			this.waitForLoading();
		}
	}

	protected void clickTreatyPageBtn() throws InterruptedException {
		helper.click(
			helper.waitVisibilityOfElementLocated(
					By.cssSelector("#coverage-header .btn.btn-radio.active"))
				.findElement(By.xpath("parent::th"))
				.findElement(By.tagName("button"))
			, "한번에 보장 변경 버튼");
		WaitUtil.waitFor(2);
	}

	protected void enter() throws CommonCrawlerException {
		try {
			waitForLoading();
			closeModal();
			closeSideBar();
			WaitUtil.loading(2);
		} catch (Exception e) {
			throw new CommonCrawlerException(ExceptionEnum.FAIL, e);
		}
	}

	protected void closeSideBar() throws CommonCrawlerException {
		try {
			if (helper.existElement(By.cssSelector("#uiQuickWrap button"))) {
				WebElement sidebarBtn = helper.waitVisibilityOfElementLocated(
					By.cssSelector("#uiQuickWrap button"));

				int attempt = 2;

				while (attempt > 0 && sidebarBtn.getAttribute("class").contains("on")) {
					helper.click(sidebarBtn);
					attempt--;
				}
			}
		} catch (Exception e) {
			throw new CommonCrawlerException(e);
		}
	}

	// SaleChannel.PC 용
	@Override
	public void crawlPremiumNew(Object obj) throws PremiumCrawlerException {
		try {
			CrawlingProduct info = (CrawlingProduct) obj;
			String premium;

			WebElement tbody = helper.waitPesenceOfAllElementsLocatedBy(
					By.xpath("//div[@class='modal-body']//table[@class='ne-table']//tbody"))
				.stream().filter(WebElement::isDisplayed).findFirst().get();

			WebElement preminumEl = helper.waitVisibilityOf(
				tbody.findElement(By.xpath(".//th[text()='보험료']/following::td")));

			premium = preminumEl.getText().replaceAll("[^0-9]", "");

			if (premium.equals("")) throw new PremiumCrawlerException("보험료 스크래핑에 문제가 있습니다.");

			info.treatyList.get(0).monthlyPremium = premium;

			logger.info("월보험료 : " + premium);

		} catch (Exception e) {
			throw new PremiumCrawlerException(e);
		}
	}

	@Override
	public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {

		try {
			CrawlingProduct info = (CrawlingProduct) obj;
			String returnPremium;

			WebElement tbody = helper.waitPesenceOfAllElementsLocatedBy(
					By.xpath("//div[@class='modal-body']//table[@class='ne-table']//tbody"))
				.stream().filter(WebElement::isDisplayed).findFirst().get();

			WebElement returnPremiumEl = helper.waitVisibilityOf(
				tbody.findElement(By.xpath(".//th[text()='예상만기환급금']/following::td")));

			returnPremium = returnPremiumEl.getText();

			info.returnPremium = returnPremium
				.substring(0,
					returnPremium.contains("(") ? returnPremium.indexOf("(") : returnPremium.length()-1)
				.replaceAll("[^0-9]", "");

			logger.info("만기환급금 : " + info.returnPremium);

		} catch (Exception e) {
			throw new ReturnPremiumCrawlerException(e);
		}
	}

	protected void compareTreaty(
		CrawlingProduct info,
		SaleChannel saleChannel,
		TreatyPlace place,
		Boolean... customizedPlan)
        throws CommonCrawlerException {

		List<CrawlingTreaty> pageTreatyList = new ArrayList<>();

		try {
			if (saleChannel.equals(SaleChannel.PC)) {
				if (place.equals(TreatyPlace.MODAL)) pageTreatyList = getPageTreatyListFromModal(info);
				if (place.equals(TreatyPlace.TABLE)) {
					if (customizedPlan.length == 0) {
						throw new CommonCrawlerException("customizedPlan 설정이 필요합니다.");
					}
					pageTreatyList = getPageTreatyListFromTable(info, customizedPlan[0]);
				}
			}
		} catch (Exception e) {
			throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_TREATIES_GETTING, e);
		}

		logger.info("원수사 현재 담보구성 저장하기");
		info.setCurrentTreatyList(pageTreatyList);

		logger.info("담보구성 비교하기");
		if (!new PlanTreatyComparer(info).comparePlanComposition()) {
			throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_TREATIES_COMPOSIOTION);
		}
	}

	// SaleChannel.PC 용
	protected void openReturnMoneyTableModal() throws InterruptedException {
		helper.click(By.id("btn-more"), "해약환급금 버튼");
		WaitUtil.loading(4);
	}

	@Override
	public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
		CrawlingProduct info = (CrawlingProduct) obj;
		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

		try {

			WebElement table = helper.waitPesenceOfAllElementsLocatedBy(
					By.cssSelector("table.basic-table.content-table"))
				.stream().filter(WebElement::isDisplayed).findFirst().get();

			List<WebElement> trs = table.findElements(By.tagName("tr"));

			for (WebElement tr : trs) {

				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

				// 납입기간 & 합계 보험료
				String term = tr.findElements(By.tagName("td")).get(0).getText();
				String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");

				// 최저
				String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
				String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();

				// 공시
				String returnMoney = tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
				String returnRate = tr.findElements(By.tagName("td")).get(5).getText();

				logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

				logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
				logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

				logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
				logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

				logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
				logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);

				planReturnMoney.setReturnMoneyMin(returnMoneyMin);
				planReturnMoney.setReturnRateMin(returnRateMin);

				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);

				planReturnMoneyList.add(planReturnMoney);

				info.returnPremium = returnMoney;
			}

			// 해약환급금 테이블
			info.setPlanReturnMoneyList(planReturnMoneyList);
			logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));

		} catch (Exception e) {
			throw new ReturnMoneyListCrawlerException(e);
		}
	}

	@Override
	public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {

	}
}
