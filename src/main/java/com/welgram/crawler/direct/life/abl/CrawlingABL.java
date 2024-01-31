package com.welgram.crawler.direct.life.abl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
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
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.ProductMasterVO;
import com.welgram.crawler.scraper.Scrapable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


// @beangelus
public abstract class CrawlingABL extends SeleniumCrawler implements Scrapable {

	public CrawlingABL() { super(); }
	public CrawlingABL(String productCode) { super(productCode); }

	protected enum TermType {
		ttIns, ttNap
	};

	private List<WebElement> eles;

	// 변액 투자 정보
	// 삼성 : 40, 미래에셋 : 30, 하나USB : 30
	private static final String FUND_SAMSUNG = "40";
	private static final String FUND_MIRAEASSET = "30";
	private static final String FUND_HANA_USB = "30";

	protected void waitForLoadingALI() throws Exception {

		boolean sendException = false;
		String alertMessage = "";
		try {
			while (true) {

				if (helper.isAlertShowed()) {
					Alert alert = driver.switchTo().alert();
					alertMessage = alert.getText();
					alert.accept();
					WaitUtil.loading(3);

					sendException = true;
					throw new Exception(alertMessage);
				}

				WaitUtil.loading(3);
				if (driver.findElement(By.className("access-deny")).isDisplayed()) {
					logger.info("로딩 중....");
				} else {
					logger.info("로딩 끝....");
					break;
				}
			}
			WaitUtil.loading(1);

		} catch (Exception e) {
			logger.info("로딩 끝....");

		} finally {
			if (sendException) {
				throw new Exception(alertMessage);
			}
		}
	}



	/*********************************************************
	 * <성별 세팅 메소드>
	 * @param  gender {int} - 성별 ( gender == 0 : 남성 , gender == 1 : 여성  )
	 * @throws Exception - 성별 세팅시 예외처리
	 *********************************************************/
	protected void setGenderFormat(int gender)  throws Exception {

		try {
			elements = helper
				.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#sxdsCd1 > option"));
			for (WebElement el : elements) {
				if (el.getAttribute("value").equals(Integer.toString(gender + 1))) {
					logger.info(el.getText());
					el.click();
					break;
				}
			}
		}

		catch(Exception e) {
			throw new Exception();
		}
	}



	protected void setGenderSelectFormat(int gender) {
		if(gender == 0){
			driver.findElement(By.cssSelector("#sxdsDiv > span:nth-child(1) > label")).click();
		} else {
			driver.findElement(By.cssSelector("#sxdsDiv > span:nth-child(2) > label")).click();
		}
	}



	// 연금개시연령
	protected void setAnnage(String annAge) throws Exception {
		elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#anutBgnAge > option"));
		boolean chk = false;
		for (WebElement option : elements) {
			if (option.getAttribute("value").equals(annAge)) {
				logger.info("연금개시연령 :: {} +클릭!" , annAge);
				chk = true;
				option.click();
				helper.waitForCSSElement(".state-load-data");
				break;
			}
		}
		
		if (!chk){
			logger.debug("선택할 연금개시연령이 없습니다!!!");
			throw new Exception("선택할 연금개시연령이 없습니다!!!");
		}

		WaitUtil.loading(2);
	}



	protected void setBirth(By by, String value) throws InterruptedException {

		element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		element.click();

		element.sendKeys(Keys.BACK_SPACE);
		Thread.sleep(200);
		element.sendKeys(Keys.BACK_SPACE);
		Thread.sleep(200);
		element.sendKeys(Keys.BACK_SPACE);
		Thread.sleep(200);
		element.sendKeys(Keys.BACK_SPACE);
		Thread.sleep(200);
		element.sendKeys(Keys.BACK_SPACE);
		Thread.sleep(200);
		element.sendKeys(Keys.BACK_SPACE);
		Thread.sleep(200);
		element.sendKeys(Keys.BACK_SPACE);
		Thread.sleep(200);
		element.sendKeys(Keys.BACK_SPACE);
		Thread.sleep(200);
		element.sendKeys(Keys.BACK_SPACE);
		Thread.sleep(200);
		element.sendKeys(Keys.DELETE);
		Thread.sleep(200);
		element.sendKeys(Keys.DELETE);
		Thread.sleep(200);
		element.sendKeys(value);
		element.sendKeys(Keys.TAB);
		WaitUtil.loading(2);
	}



	protected void setMonthlyPremium(String value) throws Exception {

		logger.info("월 보험료 : {} " , value);
		logger.debug("step1");
		Thread.sleep(2000);
		element = driver.findElement(By.id("mnContPrm"));
		logger.debug("step2");
		element.click();
		Thread.sleep(300);
		logger.debug("step3");
		element.sendKeys(Keys.DELETE);
		Thread.sleep(300);
		element.sendKeys(Keys.DELETE);
		Thread.sleep(300);
		element.sendKeys(Keys.DELETE);
		Thread.sleep(300);
		element.sendKeys(Keys.DELETE);
		Thread.sleep(300);
		element.sendKeys(Keys.DELETE);
		Thread.sleep(300);
		element.sendKeys(Keys.DELETE);
		Thread.sleep(300);
		element.sendKeys(Keys.DELETE);
		Thread.sleep(300);
		element.sendKeys(Keys.DELETE);
		Thread.sleep(300);
		element.clear();
		logger.debug("step4");
		element.sendKeys(value);
		logger.debug("step5");
		WaitUtil.loading(2);	
	}



	protected void setAssuredPremium(String value) throws Exception {

		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mnContEntAmt")));
		elements = element.findElements(By.tagName("option"));
		for (WebElement el : elements) {
			if (el.getAttribute("value").equals(value)) {
				if (elements.get(0).getAttribute("value").equals(value)) {
					WaitUtil.loading(3);
				} else {
					el.click();
					helper.waitForCSSElement(".state-load-data");
				}
				break;
			}
		}
	}



	protected void setFundRateOf() throws Exception {

		// 채권
		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fndRat0")));
		element.sendKeys(Keys.BACK_SPACE);
		element.sendKeys(Keys.BACK_SPACE);
		element.sendKeys(Keys.BACK_SPACE);

		// 삼성
		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fndRat2")));
		element.sendKeys(FUND_SAMSUNG);

		// 미래에셋
		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fndRat1")));
		element.sendKeys(FUND_MIRAEASSET);

		// 하나USB
		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fndRat5")));
		element.sendKeys(FUND_HANA_USB);
	}



	/*********************************************************
	 * <보험료 계산 메소드>
	 * @param  id {String} - id
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 * @throws Exception - 보험료 계산시 예외처리
	 *********************************************************/
	protected void calculation(String id, CrawlingProduct info) throws Exception {

		String alertMessage = "";

		WaitUtil.loading(2);
		element = driver.findElement(By.id(id));

		try {
			element.click();
		} catch (Exception e) {
			logger.info("다시계산하기 버튼이 없습니다!");
		}

		if (helper.isAlertShowed()) {

			Alert alert = driver.switchTo().alert();
			WaitUtil.loading(3);
			alertMessage = alert.getText();
			alert.accept();
			WaitUtil.loading(3);
			throw new Exception(alertMessage);
		}

		WaitUtil.waitFor(3);
	}



	/*********************************************************
	 * <계약관계정보 적용 세팅 메소드>
	 * @param  by {By} - id
	 * @throws Exception - 계약관계정보 적용 세팅 예외처리
	 *********************************************************/
	protected void doClickButton(By by) throws Exception {

		try {
			element = wait.until(ExpectedConditions.elementToBeClickable(by));
			element.click();
			helper.waitForCSSElement(".state-load-data");
			helper.waitForCSSElement(".state-load-data");

		} catch (Exception e){
			throw new Exception("계약관계정보 적용 세팅 시 예외처리 발생");
		}
	}



	protected void setMainProduct(String napTerm, String code, String insuName) throws Exception {

		if (code.equals("ABL_DTL_D001")) {
			elements = driver.findElement(By.id("mnInsCd")).findElements(By.tagName("option"));
			for (WebElement option : elements) {
				if (option.getText().contains(napTerm)) {
					option.click();
					helper.waitForCSSElement(".state-load-data");
					break;
				}
			}
		}
	}



	protected void setPeriod(TermType termType, String value) throws Exception {

		By by = null;
		switch (termType) {
		case ttIns:
			by = By.id("mnInsrPrdYys");
			break;

		case ttNap:
			by = By.id("mnInsrPadPrdYys");
			break;
		}

		element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		elements = element.findElements(By.tagName("option"));
		boolean chk = false;
		for (WebElement el : elements) {
			if (value.equals(el.getText().trim())) {
				chk = true;
				if (elements.get(0).getText().contains(value)) {
					logger.info("============================");
					logger.info("납입기간  :: " + elements.get(0).getText());
					logger.info("============================");
					WaitUtil.loading(3);
				} else {
					el.click();
					helper.waitForCSSElement(".state-load-data");
				}
				break;
			}
		}

		if (!chk) {
			logger.debug("선택할 납입기간이 없습니다!!!");
			throw new Exception("선택할 납입기간이 없습니다!!!");
		}
	}



	protected void getPremium(CrawlingProduct info) throws Exception {

		// 합계보험료 : smtotPrm
		// 납입보험료 : prdPrm
		String id = "prdPrm";

		try {
			element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
			info.treatyList.get(0).monthlyPremium = element.getAttribute("value")
				.replaceAll(",", "");
			logger.info("============================");
			logger.debug("보험료 : {}", info.treatyList.get(0).monthlyPremium);
			logger.info("============================");
			info.siteProductMasterCount++;
			info.errorMsg = "";

		} catch (Exception e){
			throw new Exception(e.getMessage());
		}
	}



	/*********************************************************
	 * <해약환급금 & 연금수령액 세팅 메소드>
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 * @throws Exception - 해약환급금 & 연금수령액 세팅 시 예외처리
	 *********************************************************/
	protected void getReturnPremium(CrawlingProduct info) throws Exception {

		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnEntplRprtPbl")));
		element.click();
		WaitUtil.loading(3);

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();

		String currentHandle = driver.getWindowHandle();
		String nextHandle = null;

		while (handles.hasNext()) {
			nextHandle = handles.next();
			WaitUtil.loading(2);
		}

		driver.switchTo().window(nextHandle);

		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tabRefnd")));
		element.click();
		helper.waitForCSSElement(".state-load-data");

		int num = 0;
		String text = "";

		if (info.productCode.equals("ABL00099")) {
			text = "해약환급금(투자수익률3.75%)";
		} else {
			text = "해약환급금(공시이율)";
		}

		WaitUtil.loading(3);
		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("refndTab")));
		List<WebElement> thElements = element.findElements(By.cssSelector("table thead th"));
		for (int i = 0; i < thElements.size(); i++) {
			if (thElements.get(i).getText().replace("\n", "").equals(text)) {
				num = i;
				break;
			}
		}

		element = element.findElement(By.tagName("table")).findElement(By.tagName("tbody"));
		elements = element.findElements(By.tagName("tr"));
		
		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		for (WebElement tr : elements) {
			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			String term = tr.findElements(By.tagName("td")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
			String returnMoney = tr.findElements(By.tagName("td")).get(num).getText();
			String returnRate = tr.findElements(By.tagName("td")).get(num+1).getText();
			logger.info(term + " :: " + premiumSum + " || 해약환급금 :: "+ returnMoney );

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoneyList.add(planReturnMoney);

			// 기본 해약환급금 세팅
			info.returnPremium = tr.findElements(By.tagName("td")).get(num).getText().replace(",", "");		
		}
		
		info.setPlanReturnMoneyList(planReturnMoneyList);

		info.savePremium = "0"; // 적립보험료
		// info.treatyList.get(0).monthlyPremium = "0"; // 납입보험료
		info.errorMsg = "";

		driver.close();
		driver.switchTo().window(currentHandle);
	}



	/*********************************************************
	 * <공시실 세팅 메소드>
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 * @throws InterruptedException - 공시실 세팅 시 예외처리
	 *********************************************************/
	protected void openAnnouncePage(CrawlingProduct info) throws InterruptedException {

		String productName = "";
		elements = driver.findElements(By.className("pd_prod_list"));

		logger.info("================");
		logger.info("공시실 크롤링 시작!");
		logger.info("================");

		for (WebElement ul : elements) {
			eles = ul.findElements(By.cssSelector("span.link"));
			for (WebElement span : eles) {
				element = span.findElement(By.tagName("a"));
				productName = element.getAttribute("title").replace("가입설계", "").replace("새창열림","").replace(",","").trim();
				productName = productName.replace("(일반심사형)","");
				if (info.productName.indexOf(productName) > -1) {
					span.click();
					WaitUtil.loading(3);
					break;
				}
			}
		}

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;

		while (handles.hasNext()) {
			subHandle = handles.next();
			WaitUtil.loading(2);
		}

		driver.switchTo().window(subHandle);
	}



	protected void disclosureOpenAnnouncePage(CrawlingProduct info) throws InterruptedException {

		String productName = "";
		elements = driver.findElements(By.className("pd_prod_list"));

		for (WebElement ul : elements) {
			eles = ul.findElements(By.cssSelector("span.link"));
			for (WebElement span : eles) {
				element = span.findElement(By.tagName("a"));
				productName = element.getAttribute("title").replace("가입설계", "").trim().replace("새창열림","").replace(" ","").replace(",","");
				if (info.productName.replace(" ","").indexOf(productName) > -1) {
					span.click();
					WaitUtil.loading(3);
					break;
				}
			}
		}

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;

		while (handles.hasNext()) {
			subHandle = handles.next();
			WaitUtil.loading(2);
		}
		driver.switchTo().window(subHandle);
	}



	protected void getSubTreaty(CrawlingProduct info) {
		
		logger.info("상품마스터 특약보험 입력시작");
		
		element = helper.waitPresenceOfElementLocated(By.id("trtyTable"));
		elements = element.findElements(By.cssSelector("tbody > tr"));

		for (WebElement tr : elements) {

			String prdtNm = tr.findElements(By.tagName("td")).get(1).getAttribute("innerText");
			// 보험기간
			List<WebElement> insTermEl = tr.findElements(By.tagName("td")).get(2).findElements(By.tagName("option"));
			List<String> insTerms = new ArrayList<String>();
			for (WebElement option : insTermEl) {
				if (!("선택").equals(option.getText())) {
					insTerms.add(option.getText());
				}
			}

			// 납입기간
			List<WebElement> napTermEl = tr.findElements(By.tagName("td")).get(3).findElements(By.tagName("option"));
			List<String> napTerms = new ArrayList<String>();
			for (WebElement option : napTermEl) {
				if (!("선택").equals(option.getText())) {
					napTerms.add(option.getText());
				}
			}

			// 가입금액
			List<String> assureMoneys = new ArrayList<String>();
			String assureMoney = tr.findElements(By.tagName("td")).get(4).getText();
			assureMoneys.add(assureMoney);

			List<String> productKinds = new ArrayList<String>();
			productKinds.add("순수보장형");

			ProductMasterVO productMasterVO = new ProductMasterVO();
			productMasterVO.setProductId(info.productCode);
			productMasterVO.setProductKinds(info.defaultProductKind); // 정확히 알면 표기
			productMasterVO.setProductTypes(info.defaultProductType); // 정확히 알면 표기
			productMasterVO.setProductGubuns("선택특약");
			productMasterVO.setSaleChannel(info.getSaleChannel());
			productMasterVO.setProductName(prdtNm);
			productMasterVO.setInsTerms(insTerms);
			productMasterVO.setNapTerms(napTerms);
			productMasterVO.setAssureMoneys(assureMoneys);
			productMasterVO.setCompanyId(info.getCompanyId());
			logger.info("상품마스터 :: " + productMasterVO.toString());
			info.getProductMasterVOList().add(productMasterVO);
		}
	}



	protected void getMainTreaty(CrawlingProduct info) {
		
		logger.info("모니터링인경우 담보명을 크롤링한다");
		logger.info("[{}]:상품명 조회", info.insuName);
		element = driver.findElement(By.id("mnInsCd"));
		elements = element.findElements(By.tagName("option"));

		String prdtNm = elements.get(0).getText();
		logger.debug("prdtNm: {}", prdtNm);

		logger.info("[{}]보험기간 조회", info.insuName);
		element = driver.findElement(By.cssSelector("select[title=보험기간]"));
		elements = element.findElements(By.tagName("option"));
		List<String> insTerms = new ArrayList<>();
		for (WebElement el : elements) {
			String insTerm = el.getAttribute("innerText");
			insTerms.add(insTerm);
		}

		logger.info("[{}]납입기간 조회", info.insuName);
		element = driver.findElement(By.cssSelector("select[title=납입기간]"));
		elements = element.findElements(By.tagName("option"));
		List<String> napTerms = new ArrayList<>();
		for (WebElement el : elements) {
			String napTerm = el.getAttribute("innerText");
			logger.debug("napTerm: {}", napTerm);
			napTerms.add(napTerm);
		}

		logger.info("[{}]납입주기 조회", info.insuName);
		element = driver.findElement(By.cssSelector("select[title=납입주기]"));
		elements = element.findElements(By.tagName("option"));
		List<String> napCycles = new ArrayList<>();
		for (WebElement el : elements) {
			String napCycle = el.getAttribute("innerText");
			logger.debug("napCycle: {}", napCycle);
			napCycles.add(napCycle);
		}

		logger.info("[{}]가입금액 조회", info.insuName);
		element = driver.findElement(By.cssSelector("select[title=가입금액]"));
		elements = element.findElements(By.tagName("option"));
		List<String> assureMoneys = new ArrayList<>();
		for (WebElement el : elements) {
			String assureMoney = el.getAttribute("innerText");
			logger.debug("assureMoney: {}", assureMoney);
			assureMoneys.add(assureMoney);
		}

		String minAssureMoney = assureMoneys.get(0);
		String maxAssureMoney = assureMoneys.get(assureMoneys.size() - 1);

		ProductMasterVO productMasterVO = new ProductMasterVO();
		productMasterVO.setProductId(info.productCode);
		productMasterVO.setProductKinds(info.defaultProductKind); // 정확히 알면 표기
		productMasterVO.setProductTypes(info.defaultProductType); // 정확히 알면 표기
		productMasterVO.setProductGubuns("주계약");
		productMasterVO.setSaleChannel(info.getSaleChannel());
		productMasterVO.setProductName(prdtNm);
		productMasterVO.setInsTerms(insTerms);
		productMasterVO.setNapTerms(napTerms);
		productMasterVO.setAssureMoneys(assureMoneys);
		productMasterVO.setMinAssureMoney(minAssureMoney);
		productMasterVO.setMaxAssureMoney(maxAssureMoney);
		productMasterVO.setCompanyId(info.getCompanyId());

		logger.info("상품마스터 :: " + productMasterVO.toString());
		info.getProductMasterVOList().add(productMasterVO);
	}

	
	protected void setMainTreaty(CrawlingProduct info, String treatyName, int assureMoney, String insTerm, String napTerm) throws Exception {

		elements = driver.findElement(By.id("entplMGrpPrcd")).findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (treatyName.contains("표준") && option.getText().contains("표준")) {
				option.click();
				break;
			}

			if (treatyName.contains("선택") && option.getText().contains("선택")) {
				option.click();
				break;
			}
		}
		helper.waitForCSSElement(".state-load-data");

		elements = driver.findElement(By.id("mnInsCd")).findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (treatyName.contains("질병입원형") && option.getText().contains("질병입원형")) {
				option.click();
				break;
			}
			if (treatyName.contains("질병통원형") && option.getText().contains("질병통원형")) {
				option.click();
				break;
			}
			if (treatyName.contains("상해입원형") && option.getText().contains("상해입원형")) {
				option.click();
				break;
			}
			if (treatyName.contains("상해통원형") && option.getText().contains("상해통원형")) {
				option.click();
				break;
			}
		}
		helper.waitForCSSElement(".state-load-data");
	}	

	protected void setSubTreaty(CrawlingProduct info, String treatyName, int assureMoney, String insTerm, String napTerm) throws Exception {

		boolean result = false;

		element = helper.waitPresenceOfElementLocated(By.id("trtyTable"));
		elements = element.findElements(By.cssSelector("tbody > tr"));

		if (insTerm.equals(napTerm)) {
			napTerm = "전기납";
		}

		// 주보험 영역 Tr 개수만큼 loop
		for (WebElement tr : elements) {
			String prdtNm = tr.findElements(By.tagName("td")).get(1).getText();
			// 담보명과 이름이 같은지 확인
			if (treatyName.indexOf(prdtNm) > -1) {
				info.siteProductMasterCount ++; // 등록된 담보명과 같은지 검증하는 카운트
				// 같으면 보기, 납기, 가입금액을 셋한다.

				element = tr.findElements(By.tagName("td")).get(0).findElement(By.cssSelector("input[type='checkbox']"));
				// 선택박스 처리
				if (!element.isSelected()) {
					element.click();
					logger.info(treatyName + " 특약 click!");
				}
			}

			if (result) {
				break;
			}
		}
	}	



	protected void getMainPremium(CrawlingTreaty crawlingTreaty) throws Exception {

		// 주계약 보험료 가져오기
		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mnContPrm"))); //prdPrm
		crawlingTreaty.monthlyPremium = element.getAttribute("value").replaceAll(",", "");
	}	



	protected void getSubPremium(CrawlingTreaty crawlingTreaty, By id) throws Exception {
		boolean result = false;

		element = helper.waitPresenceOfElementLocated(By.id("trtyTable"));
		elements = element.findElements(By.cssSelector("tbody > tr"));

		// 주보험 영역 Tr 개수만큼 loop
		for (WebElement tr : elements) {
			
			String prdtNm = tr.findElements(By.tagName("td")).get(1).getText();
			// 담보명과 이름이 같은지 확인
			if (crawlingTreaty.treatyName.indexOf(prdtNm) > -1){

				// 가입금액
				String money = tr.findElements(By.tagName("td")).get(5).findElement(By.tagName("input")).getAttribute("value");
					
				crawlingTreaty.monthlyPremium = money.replaceAll("[^0-9]", "");
				result = true;
			}

			if (result) {
				break;
			}
		}
	}



	// ***** 사용자웹  ******* //

	// 성별선택
	protected void WebsetGender(int gender) throws Exception{
		driver.findElement(By.cssSelector("#sxdsDiv > span:nth-child(" + (gender + 1) + ") > label")).click();
	}



	// 생월입력
	protected void WebsetBirth(String fullBirth) throws Exception {
		driver.findElement(By.id("brthDay")).click();
		driver.findElement(By.id("brthDay")).sendKeys(fullBirth);
	}



	// 해약환급금 세팅
	protected void WebgetReturnPremium(CrawlingProduct info) throws Exception {

		// 기본 해약환급금 세팅
		driver.findElement(By.id("srdvl00Btn")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#tabBtn02 > a"))).click();

		// 전체기간 확인하기
		logger.info("전체기간 확인하기 클릭!");
		helper.click(By.cssSelector("#tab_area_box_2 > div.acc_solo > div > a"));
		WaitUtil.loading();

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

		List<WebElement> trElements = driver.findElements(By.cssSelector("#rscRefndList1 > tr"));
		for (WebElement tr : trElements) {
			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			String term = tr.findElements(By.tagName("td")).get(0).getAttribute("innerText");
			String premiumSum = tr.findElements(By.tagName("td")).get(1).getAttribute("innerText");
			String returnMoney = tr.findElements(By.tagName("td")).get(2).getAttribute("innerText");
			String returnRate = tr.findElements(By.tagName("td")).get(3).getAttribute("innerText");
			logger.info(term + " :: " + premiumSum);

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoneyList.add(planReturnMoney);

		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

		// 연금수령액
		WebgetAnnuityPremium(info);
	}



	// 연금수령액
	protected void WebgetAnnuityPremium(CrawlingProduct info) throws Exception {

		String annuityPremium = "";
		String fixedAnnuityPremium = "";
		// 기본 해약환급금 세팅
		driver.findElement(By.id("srdvl00Btn")).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#tabBtn02 > a"))).click();

		driver.findElement(By.id("tabBtn03")).click();
		WaitUtil.loading(3);

		elements = driver.findElements(By.cssSelector("#anutIlstList3 > tr "));
		logger.info("elements.size() :: " + elements.size());
		// TrTags
		for (WebElement tr : elements) {
			elements = tr.findElements(By.tagName("td"));
			// tdTags
			for (WebElement td : elements) {
				if (td.getText().equals("종신연금형")) {
					element = td.findElement(By.xpath("parent::*"));
					WebElement el = element.findElements(By.tagName("td")).get(2);
					if (el.getText().equals("10년보증")) {
						annuityPremium = element.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
						info.annuityPremium = annuityPremium + "0000"; // 매년
						logger.info("연금수령액: " + annuityPremium + "만원");
					}
				}

				if (("확정연금형").equals(td.getText())) {
					element = td.findElement(By.xpath("parent::*"));
					WebElement el = element.findElements(By.tagName("td")).get(1);
					logger.info("el.getText(): " + el.getText());
					if (el.getText().contains("10년")) {
						fixedAnnuityPremium = MoneyUtil.getDigitMoneyFromWord(element.findElements(By.tagName("td")).get(4).getText()).toString();
						info.fixedAnnuityPremium = fixedAnnuityPremium; // 매년
						logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");
					}
				}
			}
		}
	}



	// 보험료 가져오기
	public void WebgetPremium(CrawlingProduct info) {

		String result = driver.findElement(By.id("vwMnContPrm00")).getAttribute("innerText")+"0000";
		logger.info("result" + result );
		info.treatyList.get(0).monthlyPremium = MoneyUtil.toDigitMoney(result).toString();

		logger.info("보험료: " + result);
	}



	// 월 보험료 가져오기 ( Select Box 일 경우 )
	public void setSelectMonthlyPremium(String value) {

		elements = driver.findElements(By.cssSelector("#mnContEntAmt > option"));
		for(WebElement option : elements){
			if(option.getAttribute("value").contains(value)){
				logger.info(value + "클릭!");
				option.click();
				break;
			}
		}
	}



	/*********************************************************
	 * <가입금액 세팅 메소드>
	 * @param  productObj {Object} - 크롤링 상품 객체
	 * @throws Exception - 가입금액 세팅 시 예외처리
	 *********************************************************/
	public void setAssuremoney(CrawlingProduct productObj) throws Exception {

		// 가입금액 세팅
		productObj.assureMoney = Integer.toString(Integer.parseInt(productObj.assureMoney) / 10000).replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",");
		driver.findElement(By.xpath("//*[@id=\"mnContEntAmt00\"]/option[contains(., '" + productObj.assureMoney + "')]")).click();
		WaitUtil.waitFor(2);
	}



	// ABL생명 공시실 스크롤 맨 밑으로 내리기
	protected void discusroomscrollbottom() {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(""
			+ " var $div = $(\"#mainDiv\");"
			+ " $div.scrollTop($div[0].scrollHeight);");
	}



	// ABL생명 사이트웹 스크롤 맨 밑으로 내리기
	protected void webscrollbottom() {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,document.body.scrollHeight/4);");
	}



	// 내맘대로 설계하기 - 보험기간
	public void designmyowninsTerm(CrawlingProduct product){

		elements = driver.findElements(By.cssSelector("#mnInsrPrdYysInnerDiv00 > label"));
		for (WebElement label : elements) {
			if (label.getText().contains(product.insTerm)) {
				logger.info("보험기간 :: " + product.insTerm + "클릭!");
				label.click();
			}
		}
	}



	// 내맘대로 설계하기 - 납입기간
	public void designmyownnapTerm(CrawlingProduct product){

		elements = driver.findElements(By.cssSelector("#mnInsrPadPrdYysInnerDiv00 > label"));
		for (WebElement label : elements) {
			if (label.getText().contains(product.napTerm)) {
				logger.info("납입기간 :: " + product.napTerm + "클릭!");
				label.click();
			}
		}
	}



	protected void switchToWindow(String currentHandle, Set<String> windowId, boolean value) {

		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;

		while (handles.hasNext()) {
			subHandle = handles.next();
			if (subHandle.equals(currentHandle)) {
				continue;
			} else {
				// true : 이전 창을 닫지 않음, false : 이전 창을 닫음
				if (!value) {
					driver.close();
				}
				driver.switchTo().window(subHandle);
				wait = new WebDriverWait(driver, 30);
				break;
			}
		}
	}



	/* ---------------------------------------------------
							공시실
	-----------------------------------------------------*/



	// 납입기간선택
	protected void webSetPeriod(TermType termType, String value) throws SetInsTermException {

		String termName = "";
		By by = null;
		switch (termType) {
			case ttIns:
				by = By.id("mnInsrPrdYys");
				termName = "보험기간";
				break;

			case ttNap:
				by = By.id("mnInsrPadPrdYys");
				termName = "납입기간";
				break;
		}
		element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		elements = element.findElements(By.tagName("option"));
		boolean chk = false;
		for (WebElement el : elements) {
			if (value.equals(el.getText().trim())) {
				chk = true;
				logger.info("=============================");
				logger.info(termName +" | "+el.getText() + " 클릭!");
				logger.info("=============================");
				el.click();
				try {
					helper.waitForCSSElement(".state-load-data");
				} catch (Exception e){
					throw new SetInsTermException(e.getMessage());
				}
				break;
			}
		}

		if (!chk) {
			logger.debug("선택할 납입기간이 없습니다!!!");
			throw new SetInsTermException("선택할 납입기간이 없습니다!!!");
		}

	}



	/*********************************************************
	 * <주상품에서 해당하는 상품 찾기 메소드>
	 * @param  info {CrawlingProduct} - 상품 크롤링 객체
	 * @throws Exception - 특약 세팅시 예외처리
	 *********************************************************/
	protected void compareProduct(CrawlingProduct info) throws Exception{

		elements = driver.findElements(By.cssSelector("#mnInsCd > option"));
		String[] productArray = info.productName.split(" ");

		try {
			for (WebElement productName : elements) {
				if (productName.getText().contains(productArray[1]) && productName.getText()
					.contains(info.textType) && productName.getText().contains(info.insTerm)) {
					productName.click();
					logger.info(productName.getText() + "클릭!");
					break;
				}
			}
		} catch (Exception e){
			throw new Exception(e.getMessage());
		}
	}



	// 주상품에서 해당하는 상품 찾기
	protected void compareDentalProduct(CrawlingProduct info) {

		elements = driver.findElements(By.cssSelector("#mnInsCd > option"));

		for (WebElement productName : elements) {
			if (productName.getText().contains(info.insTerm)) {
				productName.click();
				logger.info(productName.getText() + "클릭!");
				break;
			}
		}
	}



	/*********************************************************
	 * <특약리스트를 돌면서 특약의 가입조건을 세팅 메소드>
	 * @param  treatyList {CrawlingTreaty} - 특약 리스트
	 * @throws Exception - 특약 세팅시 예외처리
	 *********************************************************/
	protected void setTreaty(List<CrawlingTreaty> treatyList) throws Exception {

		try {
			for (CrawlingTreaty treaty : treatyList) {
				// 주계약은 공시실에서 세팅해주기 때문에 제외하고 선택특약일 때만
				if (treaty.productGubun.equals(ProductGubun.선택특약)) {

					String treatyName = treaty.treatyName;
					String treatyAssureMoney = String.valueOf(treaty.assureMoney);
					String insTerm = treaty.insTerm;
					String napTerm = treaty.napTerm;

					WebElement td = driver.findElement(
						By.xpath("//table[@id='trtyTable']//td[text()='" + treatyName + "']"));
					WebElement tr = td.findElement(By.xpath("./parent::tr"));
					WebElement joinInput = tr.findElement(By.xpath("./td[1]/input"));
					WebElement insTermSelect = tr.findElement(By.xpath("./td[3]/select"));
					WebElement napTermSelect = tr.findElement(By.xpath("./td[4]/select"));
					WebElement assureMoneyInput = tr.findElement(By.xpath("./td[5]/input"));

					boolean isChecked = !joinInput.isEnabled() || joinInput.isSelected();

					//특약 체크박스 클릭
					if (!isChecked) {
						String script = "arguments[0].click()";
						executeJavascript(script, joinInput);

						//보기 클릭
						selectOptionByText(insTermSelect, insTerm);

						//납기 클릭
						napTerm = (insTerm.equals(napTerm)) ? "전기납" : napTerm;
						selectOptionByText(napTermSelect, napTerm);

						//가입금액 설정
						treatyAssureMoney = String
							.valueOf(Integer.parseInt(treatyAssureMoney) / 10000);
						setTextToInputBox(assureMoneyInput, treatyAssureMoney);
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("특약세팅시 예외처리 발생");
		}
	}



	// 해약환급금 가져오기
	protected void getDisClosureRoomReturnPremium(CrawlingProduct info) throws Exception {

		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnEntplRprtPbl")));
		element.click();
		WaitUtil.loading(3);

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();

		String currentHandle = driver.getWindowHandle();
		String nextHandle = null;

		while (handles.hasNext()) {
			nextHandle = handles.next();
			WaitUtil.loading(2);
		}

		driver.switchTo().window(nextHandle);

		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tabRefnd")));
		element.click();
		helper.waitForCSSElement(".state-load-data");

		int num = 0;
		String text = "";

		if (info.productCode.equals("ABL00099")) {
			text = "해약환급금(투자수익률3.75%)";
		} else {
			text = "해약환급금(공시이율)";
		}

		WaitUtil.loading(3);
		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("refndTab")));
		List<WebElement> thElements = element.findElements(By.cssSelector("table thead th"));
//        for (int i = 0; i < thElements.size(); i++) {
//            if (thElements.get(i).getText().replace("\n", "").equals(text)) {
//                num = i;
//                break;
//            }
//        }

		element = element.findElement(By.tagName("table")).findElement(By.tagName("tbody"));
		elements = element.findElements(By.tagName("tr"));

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		for (WebElement tr : elements) {
			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			String term = tr.findElements(By.tagName("td")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
			String returnMoney = tr.findElements(By.tagName("td")).get(4).getText();
			String returnRate = tr.findElements(By.tagName("td")).get(5).getText();

			logger.info("경과기간   :: {}", term);
			logger.info("납입보험료 :: {}", premiumSum);
			logger.info("해약환급금 :: {}", returnMoney);
			logger.info("환급률    :: {}", returnRate);
			logger.info("=================================");

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoneyList.add(planReturnMoney);

			// 기본 해약환급금 세팅
			info.returnPremium = tr.findElements(By.tagName("td")).get(5).getText()
				.replace(",", "");
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

		info.savePremium = "0"; // 적립보험료
		// info.treatyList.get(0).monthlyPremium = "0"; // 납입보험료
		info.errorMsg = "";

		driver.close();
		driver.switchTo().window(currentHandle);
	}



	// 해약환급금 가져오기
	protected void getDisClosureRoomDentalReturnPremium(CrawlingProduct info) throws Exception {

		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnEntplRprtPbl")));
		element.click();
		WaitUtil.loading(3);

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();

		String currentHandle = driver.getWindowHandle();
		String nextHandle = null;

		while (handles.hasNext()) {
			nextHandle = handles.next();
			WaitUtil.loading(2);
		}

		driver.switchTo().window(nextHandle);

		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tabRefnd")));
		element.click();
		helper.waitForCSSElement(".state-load-data");

		WaitUtil.loading(3);
		element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("refndTab")));

		element = element.findElement(By.tagName("table")).findElement(By.tagName("tbody"));
		elements = element.findElements(By.tagName("tr"));

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		for (WebElement tr : elements) {

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			String term = tr.findElements(By.tagName("td")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
			String returnMoneyMin = tr.findElements(By.tagName("td")).get(3).getText();
			String returnRateMin = tr.findElements(By.tagName("td")).get(4).getText();
			logger.info(term + " :: " + premiumSum);
			logger.info(term + " :: 최저 해약환급금 :: " + returnMoneyMin);
			logger.info(term + " :: 최저 환급률    :: " + returnRateMin);
			logger.info("=============================================");

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoneyMin); // 최저 해약환급금
			planReturnMoney.setReturnRateMin(returnRateMin);   // 최저 환급률
			planReturnMoneyList.add(planReturnMoney);

			// 기본 해약환급금 세팅
			info.returnPremium
				= tr.findElements(By.tagName("td"))
					.get(4)
					.getText()
					.replace(",", "");
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

		info.savePremium = "0"; // 적립보험료
		// info.treatyList.get(0).monthlyPremium = "0"; // 납입보험료
		info.errorMsg = "";

		driver.close();
		driver.switchTo().window(currentHandle);
	}



	// 연금전환연령
	protected void setTransitionAge(CrawlingProduct info) {

		elements = driver.findElements(By.cssSelector("#anutBgnAge > option"));
		for(WebElement el : elements){
			if(el.getText().contains(info.annuityAge)){
				el.click();
				logger.info("=============================");
				logger.info("연금전환연령 | " +el.getText()+" 클릭!");
				logger.info("=============================");
				break;
			}
		}
	}



	protected Object executeJavascript(String script) {

		return ((JavascriptExecutor)driver).executeScript(script);
	}



	protected Object executeJavascript(String script, WebElement element) {

		return ((JavascriptExecutor)driver).executeScript(script, element);
	}



	//inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
	protected void setTextToInputBox(By el, String text) {

		WebElement element = driver.findElement(el);
		element.clear();
		element.sendKeys(text);
	}



	//inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
	protected void setTextToInputBox(WebElement element, String text) {

		element.clear();
		element.sendKeys(text);
	}



	// select 태그에서 해당 text의 option을 클릭한다(홈페이지, 공시실 둘 다 사용 가능한 메서드)
	protected void selectOptionByText(By element, String text) throws NotFoundTextInSelectBoxException {

		WebElement selectEl = driver.findElement(element);
		selectOptionByText(selectEl, text);
	}



	// select 태그에서 해당 text의 option을 클릭한다(홈페이지, 공시실 둘 다 사용 가능한 메서드)
	protected void selectOptionByText(WebElement selectEl, String text) throws NotFoundTextInSelectBoxException {

		Select select = new Select(selectEl);

		try {
			select.selectByVisibleText(text);
		} catch (NoSuchElementException e) {
			throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')값을 찾을 수 없습니다.");
		}
	}



	// 보기 , 납기 선택
	protected void discussSetPeriod(TermType termType, String value) throws Exception {

		By by = null;
		switch (termType) {
			case ttIns:
				by = By.id("mnInsrPrdYys");
				break;

			case ttNap:
				by = By.id("mnInsrPadPrdYys");
				break;
		}

		if(value.contains("종신보장")){ value = "종신";}

		element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		elements = element.findElements(By.tagName("option"));
		boolean chk = false;
		for (WebElement el : elements) {
			if (value.equals(el.getText().trim())) {
				chk = true;
				logger.info(el.getText() + "클릭!");
				el.click();
				helper.waitForCSSElement(".state-load-data");
				break;
			}
		}

		if (!chk) {
			logger.debug("선택할 납입기간이 없습니다!!!");
			throw new Exception("선택할 납입기간이 없습니다!!!");
		}

	}



	/*********************************************************
	 * <해약환급금 세팅 메소드>
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 * @throws Exception - 해약환급금 세팅시 예외처리
	 *********************************************************/
	protected void getReturnAssuremoies(CrawlingProduct info) throws Exception {

		try {
			element = wait
				.until(ExpectedConditions.presenceOfElementLocated(By.id("btnEntplRprtPbl")));
			element.click();
			WaitUtil.loading(3);

			Set<String> windowId = driver.getWindowHandles();
			Iterator<String> handles = windowId.iterator();

			String currentHandle = driver.getWindowHandle();
			String nextHandle = null;

			while (handles.hasNext()) {
				nextHandle = handles.next();
				WaitUtil.loading(2);
			}

			driver.switchTo().window(nextHandle);

			element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tabRefnd")));
			element.click();
			helper.waitForCSSElement(".state-load-data");

			WaitUtil.loading(3);
			element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("refndTab")));
			element = element.findElement(By.tagName("table")).findElement(By.tagName("tbody"));
			elements = element.findElements(By.tagName("tr"));

			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
			for (WebElement tr : elements) {
				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				String term = tr.findElements(By.tagName("td")).get(0).getText();
				String premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
				String returnMoney = tr.findElements(By.tagName("td")).get(3).getText();
				String returnRate = tr.findElements(By.tagName("td")).get(4).getText();
				logger.info(term + " :: " + premiumSum);
				logger.info(term + " :: 해약환급금 :: " + returnMoney);
				logger.info(term + " :: 환급률    :: " + returnRate);

				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);
				planReturnMoneyList.add(planReturnMoney);

				// 기본 해약환급금 세팅
				info.returnPremium = returnMoney;
				logger.info("만기환급금 세팅 : {} ", info.returnPremium);
			}

			info.setPlanReturnMoneyList(planReturnMoneyList);

			info.savePremium = "0"; // 적립보험료
			info.errorMsg = "";

			driver.close();
			driver.switchTo().window(currentHandle);

		} catch (Exception e){
			throw new Exception(e.getMessage());
		}
	}



	/*********************************************************
	 * <해약환급금 가져오기 메소드>
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 * @throws Exception - 해약환급금 세팅시 예외처리
	 *********************************************************/
	protected void getReturnAllAssuremoies(CrawlingProduct info) throws Exception {

		try {
			element = wait
				.until(ExpectedConditions.presenceOfElementLocated(By.id("btnEntplRprtPbl")));
			element.click();
			WaitUtil.loading(3);

			Set<String> windowId = driver.getWindowHandles();
			Iterator<String> handles = windowId.iterator();

			String currentHandle = driver.getWindowHandle();
			String nextHandle = null;

			while (handles.hasNext()) {
				nextHandle = handles.next();
				WaitUtil.loading(2);
			}

			driver.switchTo().window(nextHandle);

			element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tabRefnd")));
			element.click();
			helper.waitForCSSElement(".state-load-data");

			WaitUtil.loading(3);
			element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("refndTab")));
			element = element.findElement(By.tagName("table")).findElement(By.tagName("tbody"));
			elements = element.findElements(By.tagName("tr"));

			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
			for (WebElement tr : elements) {
				PlanReturnMoney planReturnMoney = new PlanReturnMoney();

				String term = tr.findElements(By.tagName("td")).get(0).getText();
				String premiumSum = tr.findElements(By.tagName("td")).get(2).getText();

				String returnMoneyMin = tr.findElements(By.tagName("td")).get(3).getText();
				String returnRateMin = tr.findElements(By.tagName("td")).get(4).getText();

				String returnMoneyAvg = tr.findElements(By.tagName("td")).get(5).getText();
				String returnRateAvg = tr.findElements(By.tagName("td")).get(6).getText();

				String returnMoney = tr.findElements(By.tagName("td")).get(7).getText();
				String returnRate = tr.findElements(By.tagName("td")).get(8).getText();

				logger.info("경과기간   :: {}", term);
				logger.info("납입보험료 :: {}", premiumSum);
				logger.info("해약환급금 :: {}", returnMoney);
				logger.info("환급률    :: {}", returnRate);
				logger.info("최저해약환급금 :: {}", returnMoneyMin);
				logger.info("최저해약환급률 :: {}", returnRateMin);
				logger.info("평균해약환급금 :: {}", returnMoneyAvg);
				logger.info("평균해약환급률 :: {}", returnRateAvg);
				logger.info("=================================");

				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoneyMin(returnMoneyMin);
				planReturnMoney.setReturnRateMin(returnRateMin);
				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);
				planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
				planReturnMoney.setReturnRateAvg(returnRateAvg);

				planReturnMoneyList.add(planReturnMoney);

				// 기본 해약환급금 세팅
				info.returnPremium = tr.findElements(By.tagName("td")).get(7).getText()
					.replace(",", "");
			}

			info.setPlanReturnMoneyList(planReturnMoneyList);

			// 연금수령액
			getAnnuityPremium(info);

			info.savePremium = "0"; // 적립보험료
			info.errorMsg = "";

			driver.close();
			driver.switchTo().window(currentHandle);

		} catch (Exception e){
			throw new Exception(e.getMessage());
		}
	}



	/*********************************************************
	 * <원수사 보험료 확인 메소드>
	 * @throws Exception - 보험료 확인 시 예외처리
	 *********************************************************/
	protected void webConfirm() throws Exception {

		try {
			driver.findElement(By.id("calcStrBtn")).click();
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("globalLoadingDiv")));
			WaitUtil.waitFor(2);

		} catch (Exception e){
			throw new Exception();
		}
	}



	/*********************************************************
	 * <보험료 재계산 메소드>
	 * @param  productObj {Object} - 성별 객체
	 * @throws Exception - 성별 세팅 시 예외처리
	 *********************************************************/
	public void recalculate(Object productObj) throws Exception {

		CrawlingProduct info = (CrawlingProduct) productObj;
		JavascriptExecutor js = (JavascriptExecutor) driver;


		try {

			js.executeScript("$(\"#calcPrm00Btn\").click();");
			WaitUtil.loading(2);

			element = driver.findElement(By.cssSelector("#alertMsgBox > div.pop-layer > div > strong"));

			// 보험료 미달 여부
			if(element.isDisplayed()){
				logger.info("alert 확인!");
				String alertMessage = element.getText();
				if(alertMessage.contains("고객님")){
					logger.info(alertMessage);
					logger.info(info.age +"세는 보헙가입비 미달입니다.");
				}
			}

			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("globalLoadingDiv")));
			WaitUtil.waitFor(2);

		}

		catch(Exception e){
			throw new Exception();
		}
	}



	/*********************************************************
	 * <원수사 보험기간 세팅 메소드>
	 * @param  info {Object} - 크롤링 상품 객체
	 * @throws Exception - 보험기간 세팅 시 예외처리
	 *********************************************************/
	protected void webSetInsTerm(CrawlingProduct info) throws Exception {

		try {
			elements = driver.findElements(By.cssSelector("#mnInsrPrdYysInnerDiv00 > label"));
			for (WebElement option : elements) {
				if (option.getText().contains(info.insTerm)) {
					logger.info(option.getText() + "클릭!");
					option.click();
				}
			}

		} catch (Exception e) {
			throw new Exception();
		}
	}

	/*********************************************************
	 * <원수사 납입기간 세팅 메소드>
	 * @param  info {Object} - 크롤링 상품 객체
	 * @throws Exception - 보험기간 세팅 시 예외처리
	 *********************************************************/
	protected void webSetNapTerm(CrawlingProduct info) throws Exception {

		int napTermchk = 0 ;
 	 	elements = driver.findElements(By.cssSelector("#mnInsrPadPrdYys02 > option"));
		for(WebElement option : elements) {
			if(option.getText().contains(info.napTerm)){
				napTermchk++;
				logger.info(option.getText() + "클릭!");
				option.click();
			}
		}
		if(napTermchk == 0){ // 납입기간이 존재하지 않는다면
			throw new Exception();
		}
	}


	/*********************************************************
	 * <성별 세팅                                                   메소드>
	 * @param  productObj {Object} - 성별 객체
	 * @throws SetGenderException - 성별 세팅 시 예외처리
	 *********************************************************/
	@Override
	public void setGenderNew(Object productObj) throws SetGenderException {
		CrawlingProduct info = (CrawlingProduct) productObj;

		try {
			setGenderFormat(info.gender);
			WaitUtil.loading(2);
		}

		catch(Exception e){
			throw new SetGenderException(e.getMessage());
		}
	}

	/*********************************************************
	 * <생년월일 세팅 메소드>
	 * @param  productObj {Object} - 크롤링 상품 객체
	 * @throws SetBirthdayException - 생년월일 세팅시 예외처리
	 *********************************************************/
	@Override
	public void setBirthdayNew(Object productObj) throws SetBirthdayException {

		CrawlingProduct info = (CrawlingProduct) productObj;

		try {
			setBirth(By.id("insrdSno_jupiDate1"), info.fullBirth);

			logger.info("===============================");
			logger.info("생년월일 {} 세팅 !",info.fullBirth);
			logger.info("===============================");
			WaitUtil.loading(2);
		}

		catch(Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
			throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
		}
	}

	/*********************************************************
	 * <보험기간 세팅 메소드>
	 * @param  productObj {Object} - 크롤링 상품 객체
	 * @throws SetInsTermException - 보험기간 세팅 시 예외처리
	 *********************************************************/
	@Override
	public void setInsTermNew(Object productObj) throws SetInsTermException {
		CrawlingProduct info = (CrawlingProduct) productObj;

		try {

			// 대면연금보험일 경우
			if(info.productCode.contains("ABL_ASV_F001")){
				// 대면연금보험일 경우 보험기간 확정은 존재x -> 종신보장으로 통일
				if(info.annuityType.contains("확정")){
					info.insTerm = "종신보장";
				}
			}

			discussSetPeriod(TermType.ttIns, info.insTerm);
			WaitUtil.loading(2);

		} catch (Exception e) {
			throw new SetInsTermException(e.getMessage());
		}
	}

	/*********************************************************
	 * <납입기간 세팅 메소드>
	 * @param  productObj {Object} - 크롤링 상품 객체
	 * @throws SetNapTermException - 납입기간 세팅 시 예외처리
	 *********************************************************/
	@Override
	public void setNapTermNew(Object productObj) throws SetNapTermException {
		CrawlingProduct info = (CrawlingProduct) productObj;

		try{

			discussSetPeriod(TermType.ttNap, info.napTerm);
			WaitUtil.loading(2);

		}catch (Exception e){
			throw new SetNapTermException(e.getMessage());
		}
	}

	/*********************************************************
	 * <가입금액 세팅 메소드>
	 * @param  productObj {Object} - 크롤링 상품 객체
	 * @throws SetAssureMoneyException - 가입금액 세팅 시 예외처리
	 *********************************************************/
	@Override
	public void setAssureMoneyNew(Object productObj) throws SetAssureMoneyException {
		CrawlingProduct info = (CrawlingProduct) productObj;

		try {

			// 대면연금저축을 제외한 상품
			if(!info.productCode.contains("ABL_ASV_F001")){
				setSelectMonthlyPremium(info.assureMoney);
			} else {
				// 대면연금저축일 경우
				helper.sendKeys2_check(By.cssSelector("#mnContPrm"), info.assureMoney);
			}
			WaitUtil.loading(2);

		} catch (Exception e) {
			throw new SetAssureMoneyException(e.getMessage());
		}
	}

	/*********************************************************
	 * <보험료 가져오기 메소드>
	 * @param  productObj {Object} - 크롤링 상품 객체
	 * @throws PremiumCrawlerException - 보험료 세팅시 예외처리
	 *********************************************************/
	@Override
	public void crawlPremiumNew(Object productObj) throws PremiumCrawlerException {
		CrawlingProduct info = (CrawlingProduct) productObj;

		try {
			getPremium(info);
		} catch (Exception e) {
			throw new PremiumCrawlerException(e.getMessage());
		}
	}

	/*********************************************************
	 * <해약환급금 세팅 메소드>
	 * @param  productObj {Object} - 크롤링 상품 객체
	 * @throws ReturnMoneyListCrawlerException - 해약환급금 세팅시 예외처리
	 *********************************************************/
	@Override
	public void crawlReturnMoneyListNew(Object productObj) throws ReturnMoneyListCrawlerException {

		CrawlingProduct info = (CrawlingProduct) productObj;

		try {
			if(!info.productCode.contains("ABL_ASV_F001")){
				getReturnAssuremoies(info);
			} else {
				getReturnAllAssuremoies(info);
			}
		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
			throw new ReturnMoneyListCrawlerException(e.getCause(),exceptionEnum.getMsg());
		}
	}

	/*********************************************************
	 * <직업 세팅 메소드>
	 * @param  obj {Object} - 크롤링 상품 객체
	 * @throws SetJobException - 직업 세팅시 예외처리
	 *********************************************************/
	@Override
	public void setJobNew(Object obj) throws SetJobException {
		try{

		}catch (Exception e){
			throw new SetJobException(e.getMessage());
		}
	}

	/*********************************************************
	 * <갱신유형 세팅 메소드>
	 * @param  obj {Object} - 크롤링 상품 객체
	 * @throws SetRenewTypeException - 갱신유형 세팅 예외처리
	 *********************************************************/
	@Override
	public void setRenewTypeNew(Object obj) throws SetRenewTypeException {
		try{

		}catch (Exception e){
			throw new SetRenewTypeException(e.getMessage());
		}
	}

	/*********************************************************
	 * <환급형태 세팅 메소드>
	 * @param  obj {Object} - 크롤링 상품 객체
	 * @throws SetRefundTypeException - 환급형태 세팅시 예외처리
	 *********************************************************/
	@Override
	public void setRefundTypeNew(Object obj) throws SetRefundTypeException {
		try{

		}catch (Exception e){
			throw new SetRefundTypeException(e.getMessage());
		}
	}

	/*********************************************************
	 * <만기환급금 세팅 메소드>
	 * @param  obj {Object} - 크롤링 상품 객체
	 * @throws ReturnPremiumCrawlerException - 만기환급금 세팅시 예외처리
	 *********************************************************/
	@Override
	public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {
		try {
			CrawlingProduct info = (CrawlingProduct) obj;

		} catch (Exception e) {
			throw new ReturnPremiumCrawlerException(e.getMessage());
		}
	}

	/*********************************************************
	 * <스크롤이동 메소드>
	 * @param  y {String} - 스크롤할 y 좌표
	 * @throws InterruptedException - 스크롤 예외
	 *********************************************************/
	protected void scrollMove(String y) throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(""
			+ " var $div = $(\"#mainDiv\");"
			+ " $div.scrollTop("+y+");");
		WaitUtil.loading(4);
	}

	/*********************************************************
	 * <납입주기 세팅 메소드>
	 * @param  obj {Object} - 크롤링 상품 객체
	 * @throws SetNapCycleException - 납입주기 세팅시 예외처리
	 *********************************************************/
	@Override
	public void setNapCycleNew(Object obj) throws SetNapCycleException {
		CrawlingProduct info = (CrawlingProduct) obj;

		try {

		} catch (Exception e) {
			throw new SetNapCycleException(e.getMessage());
		}

	}

	/*********************************************************
	 * <연금수령액 가져오기 메소드>
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 * @throws Exception - 연금수령액 세팅시 예외처리
	 *********************************************************/
	protected void getAnnuityPremium(CrawlingProduct info) throws Exception {

		String annuityPremium = "";
		String fixedAnnuityPremium = "";
		PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
		driver.findElement(By.id("tabAnut")).click();
		WaitUtil.loading(3);

		try {
			element = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8)"));
			elements = element.findElements(By.tagName("td"));

			if (info.annuityType.contains("종신")) { // 종신연금형일 경우
				if (info.annuityType.contains("10년")) { // 종신 10년일 경우
					annuityPremium = driver.findElement(By.cssSelector(
						"#anutTab > table:nth-child(8) > tbody > tr:nth-child(1) > td:nth-child(4)"))
						.getText().replaceAll("10년", "").replaceAll("[^0-9]", "");
					info.annuityPremium = annuityPremium + "0000"; // 매년
					logger.info("종신연금수령액: " + info.annuityPremium + "원");

				} else if (info.annuityType.contains("20년")) { // 종신 20년일 경우
					annuityPremium = driver.findElement(By.cssSelector(
						"#anutTab > table:nth-child(8) > tbody > tr:nth-child(2) > td:nth-child(2)"))
						.getText().replaceAll("20년", "").replaceAll("[^0-9]", "");
					info.annuityPremium = annuityPremium + "0000"; // 매년
					logger.info("종신연금수령액: " + info.annuityPremium + "원");

				}
			} else if (info.annuityType.contains("확정")) { // 확정연금형일 경우
				if (info.annuityType.contains("10년")) { // 확정 10년일 경우

					fixedAnnuityPremium = driver.findElement(By.cssSelector(
						"#anutTab > table:nth-child(8) > tbody > tr:nth-child(3) > td:nth-child(3)"))
						.getText().replaceAll("10년", "").replaceAll("[^0-9]", "");
					info.fixedAnnuityPremium = fixedAnnuityPremium + "0000";
					; // 매년
					logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");
				} else if (info.annuityType.contains("15년")) { // 확정 15년일 경우
					fixedAnnuityPremium = driver.findElement(By.cssSelector(
						"#anutTab > table:nth-child(8) > tbody > tr:nth-child(4) > td:nth-child(2)"))
						.getText().replaceAll("15년", "").replaceAll("[^0-9]", "");
					info.fixedAnnuityPremium = fixedAnnuityPremium + "0000";
					; // 매년
					logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");
				} else if (info.annuityType.contains("20년")) { // 확정 20년일 경우

					fixedAnnuityPremium = driver.findElement(By.cssSelector(
						"#anutTab > table:nth-child(8) > tbody > tr:nth-child(5) > td:nth-child(2)"))
						.getText().replaceAll("20년", "").replaceAll("[^0-9]", "");
					info.fixedAnnuityPremium = fixedAnnuityPremium + "0000"; // 매년
					logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");
				}
			}

			// 종신형
			planAnnuityMoney.setWhl10Y(driver.findElement(By.cssSelector(
				"#anutTab > table:nth-child(8) > tbody > tr:nth-child(1) > td:nth-child(4)"))
				.getText().replaceAll("[^0-9]", "") + "0000");    //종신 10년
			planAnnuityMoney.setWhl20Y(driver.findElement(By.cssSelector(
				"#anutTab > table:nth-child(8) > tbody > tr:nth-child(2) > td:nth-child(2)"))
				.getText().replaceAll("[^0-9]", "") + "0000");    //종신 20년

			// 확정형
			String Fxd10 = driver.findElement(By.cssSelector(
				"#anutTab > table:nth-child(8) > tbody > tr:nth-child(3) > td:nth-child(3)"))
				.getText();  // 확정 10년
			int Fxd10last = Fxd10.indexOf("씩"); // 매년기준이기 때문에 문자열 "씩" 앞까지만

			String Fxd15 = driver.findElement(By.cssSelector(
				"#anutTab > table:nth-child(8) > tbody > tr:nth-child(4) > td:nth-child(2)"))
				.getText();  // 확정 15년
			int Fxd15last = Fxd15.indexOf("씩"); // 매년기준이기 때문에 문자열 "씩" 앞까지만

			String Fxd20y = driver.findElement(By.cssSelector(
				"#anutTab > table:nth-child(8) > tbody > tr:nth-child(5) > td:nth-child(2)"))
				.getText();  // 확정 20년
			int Fxd20last = Fxd20y.indexOf("씩"); // 매년기준이기 때문에 문자열 "씩" 앞까지만

			planAnnuityMoney.setFxd10Y(
				Fxd10.substring(0, Fxd10last + 1).replaceAll("[^0-9]", "") + "0000");    //확정 10년
			planAnnuityMoney.setFxd15Y(
				Fxd15.substring(0, Fxd15last + 1).replaceAll("[^0-9]", "") + "0000");    //확정 15년
			planAnnuityMoney.setFxd20Y(
				Fxd20y.substring(0, Fxd20last + 1).replaceAll("[^0-9]", "") + "0000");    //확정 20년

			logger.info("종신10년 :: " + planAnnuityMoney.getWhl10Y());
			logger.info("종신20년 :: " + planAnnuityMoney.getWhl20Y());
			logger.info("확정10년 :: " + planAnnuityMoney.getFxd10Y());
			logger.info("확정15년 :: " + planAnnuityMoney.getFxd15Y());
			logger.info("확정20년 :: " + planAnnuityMoney.getFxd20Y());

			info.planAnnuityMoney = planAnnuityMoney;
		} catch (Exception e){
			throw new Exception(e.getMessage());
		}
	}


	/*********************************************************
	 * <납입기간선택 메소드>
	 * @param  termType {TermType} - 납입기간
	 * @param  value {String} - 납입기간 값
	 * @throws SetNapCycleException - 납입주기 세팅시 예외처리
	 *********************************************************/
	protected void webSetNewPeriod(TermType termType, String value) throws Exception {

		By by = null;
		switch (termType) {
			case ttIns:
				by = By.id("mnInsrPrdYys");
				break;

			case ttNap:
				by = By.id("mnInsrPadPrdYys");
				break;
		}
		logger.info("value ::" + value);

		element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		elements = element.findElements(By.tagName("option"));
		boolean chk = false;
		for (WebElement el : elements) {
			if (value.equals(el.getText().trim())) {
				chk = true;
				logger.info(el.getText() + "클릭!");
				el.click();
				helper.waitForCSSElement(".state-load-data");
				break;
			}
		}
		if (!chk) {
			logger.debug("선택할 납입기간이 없습니다!!!");
			throw new Exception("선택할 납입기간이 없습니다!!!");
		}

	}



	/*********************************************************
	 * <연금 지급 방법 세팅 메소드>
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 *********************************************************/
	protected void setAnutPymMth(CrawlingProduct info) {

		elements = driver.findElements(By.cssSelector("#anutPymMth > option"));

		for (WebElement option : elements) {
			if (option.getText().contains("매년지급")) {
				option.click();
				logger.info("{} 클릭!", option.getText());
				break;
			}
		}
	}



	/*********************************************************
	 * <공시실 상품이름 찾기 메소드>
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 *********************************************************/
	protected void openUniqueAnnouncePage(CrawlingProduct info) throws InterruptedException {

		String productName = "";
		elements = driver.findElements(By.className("pd_prod_list"));

		for (WebElement ul : elements) {
			eles = ul.findElements(By.cssSelector("span.link"));
			for (WebElement span : eles) {
				element = span.findElement(By.tagName("a"));
				productName = element.getAttribute("title").replace("가입설계", "").trim();

				if (productName.contains(info.productName.substring(0, info.productName.indexOf("보험")))) {
					span.click();
					WaitUtil.loading(3);
					break;
				}
			}
		}

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;

		while (handles.hasNext()) {
			subHandle = handles.next();
			WaitUtil.loading(2);
		}

		driver.switchTo().window(subHandle);
	}



	/*********************************************************
	 * <연금지급방법 선택 메소드>
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 *********************************************************/
	protected void setAnnPayment(CrawlingProduct info) throws Exception {

		String annPayment = "";

		try {
			elements = driver.findElements(By.cssSelector("#anutPymMth > option"));

			if (info.textType.contains("매월")) {
				annPayment = "01";
			} else {
				annPayment = "12";
			}

			for (WebElement option : elements) {
				if (option.getAttribute("value").contains(annPayment)) {
					option.click();
					logger.info(option.getText() + "클릭!");
					break;
				}
			}

			WaitUtil.loading(4);
		} catch(Exception e){
			throw new Exception(e.getMessage());
		}
	}

}
