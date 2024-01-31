package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
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

import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.ProductMasterVO;
import com.welgram.crawler.scraper.Scrapable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;


public abstract class CrawlingLIN extends SeleniumCrawler implements Scrapable {

	// 기본정보입력(부모)
	protected void setParentInfo(String parentName, String parentFullBirth) throws InterruptedException {
		driver.findElement(By.id("parent_name")).sendKeys(parentName);
		WaitUtil.loading(2);
		driver.findElement(By.id("icontract_resid_no1")).sendKeys(parentFullBirth);
		WaitUtil.loading(2);
	}

	protected void doInputBox(By by, String value) {
		element = driver.findElement(by);
		element.click();
		element.clear();
		element.sendKeys(value);
	}

	protected void doRadioButton(By by, String value){
		elements = driver.findElements(by);
		element = elements.get(Integer.parseInt(value));
		element.click();
	}

	// 성별 선택
	protected void selectGender(int gender) {
		// 남성일 경우
		if (gender == 0) {
			element = driver.findElement(By.id("main_btn_male"));
		} else {
			element = driver.findElement(By.id("main_btn_female"));
		}
		element.click();
	}

	// 성별 선택
	protected void webSelectGender(int gender) {
		// 남성일 경우
		if (gender == 0) {
			element = driver.findElement(By.cssSelector("#contents > div.page_head.product99 > div > div > div.ip_info > div > a.on"));
		} else {
			element = driver.findElement(By.cssSelector("#contents > div.page_head.product99 > div > div > div.ip_info > div > a:nth-child(2)"));
		}
		element.click();
	}

	protected void setSpecialPremium(String insuName, String premium) throws Exception {
		if (insuName.contains("무배당다이렉트보철특약")) {
			elements = driver.findElement(By.id("rider_amount")).findElements(By.tagName("option"));
			for (WebElement option : elements) {
				if (option.getAttribute("value").equals(premium)) {
					option.click();
					helper.waitForCSSElement("#LOADING_BAR");
					break;
				}
			}
		}
	}

	protected void doSelectBox(By by, String value) {
		int Replaybutton = 0 ;

		wait.until(ExpectedConditions.elementToBeClickable(by));
		element = driver.findElement(by);
		elements = element.findElements(By.tagName("option"));

		if(value=="0")
		{
			value = "선택안함";
		}

		if(value.contains("종신보장")){
			value = "999:99";
		}

		value = value.replace("년", "").replace("세", "");
		for (WebElement option : elements) {
			if (option.getAttribute("value").contains(value)) {
				option.click();
				break;
			}
			else
				{
					if(Replaybutton == 0 ) {
						option.click();
						Replaybutton++;
					}
				}
		}
	}

	protected void doButton(By by) throws Exception {
		element = driver.findElement(by);
		element.click();
		helper.waitForCSSElement("#LOADING_BAR");
	}

	protected void getCrawlingResult(CrawlingProduct info, By by) throws Exception {
		doButton(By.className("g_btn_9"));
		WaitUtil.loading(2);
		doButton(By.className("g_btn_10"));

		info.treatyList.get(0).monthlyPremium = "9900";

		info.errorMsg = "";
		info.returnPremium = "0";
		info.annuityPremium = "0";
	}
	
	protected void setMainTreaty(CrawlingProduct info, CrawlingTreaty item) throws InterruptedException {

		WaitUtil.loading(1);
		logger.info("특약 및 가입금액 선택");
		List<WebElement> subElements = driver.findElements(By.cssSelector(
				"#wrap > div.p_con > fieldset > div.cont_right > div:nth-child(3) > table > tbody > tr"));
		WaitUtil.loading(1);

		for (WebElement trElement : subElements) {
			List<WebElement> tdElements = trElement.findElements(By.tagName("td"));
			String pName = tdElements.get(0).getText().trim();
			// 가입설계 특약과 같은 항목을 찾는다.
			if (item.treatyName.equals(pName)) {

				logger.info("가입금액 세팅");
				String assureMoney = item.assureMoney + "";
				Select selectAssureMoney = new Select(tdElements.get(3).findElement(By.cssSelector("div > select")));
				selectAssureMoney.selectByValue(assureMoney);
				WaitUtil.loading(1);

				break;

			}

		}
	}	

	protected void setSubTreaty(CrawlingProduct info, CrawlingTreaty item, By listEl) throws InterruptedException {

		WaitUtil.loading(1);
		logger.info("특약 및 가입금액 선택");
		List<WebElement> subElements = driver.findElements(listEl);
		WaitUtil.loading(1);

		if(item.treatyName.contains("주계약")){
			return;
		}

		String insTermVal;

		logger.info("========================================");
		logger.info("특약 : " +item.treatyName);
		logger.info("========================================");

		for (WebElement trElement : subElements) {


			List<WebElement> tdElements = trElement.findElements(By.tagName("td"));
			String pName = tdElements.get(0).getText().trim();
			item.treatyName = item.treatyName.replace("III","Ⅲ");

			// 가입설계 특약과 같은 항목을 찾는다.
			if (item.treatyName.replace(" ","").equals(pName)) {

				logger.info("보험기간 세팅");
				if(item.insTerm.contains("년")){
					insTermVal = item.insTerm.replaceAll("[^0-9]", "") + " 년만기";
				} else {
					insTermVal = item.insTerm.replaceAll("[^0-9]", "") + " 세만기";
				}
				Select selectInsTerm = new Select(tdElements.get(3).findElement(By.cssSelector("div > select")));
				selectInsTerm.selectByVisibleText(insTermVal);
				WaitUtil.loading(1);

				logger.info("납입기간 세팅");
				String napTermVal = item.napTerm.replaceAll("[^0-9]", "") + " 년납";
				Select selectNapTerm = new Select(tdElements.get(4).findElement(By.cssSelector("div > select")));
				selectNapTerm.selectByVisibleText(napTermVal);
				WaitUtil.loading(1);

				logger.info("가입금액 세팅");
				String assureMoney = item.assureMoney + "";
				Select selectAssureMoney = new Select(tdElements.get(2).findElement(By.cssSelector("div > select")));
				selectAssureMoney.selectByValue(assureMoney);
				WaitUtil.loading(1);

				break;

			}

		}
	}

	protected void getCrawlingResult(CrawlingProduct info) throws Exception {

		// 보험료 조회 버튼
		doButton(By.className("g_btn_09"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("LOADING_BAR")));

		// 보장내용 조회 버튼
		doButton(By.className("g_btn_10"));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("LOADING_BAR")));

		List<WebElement> trElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
				By.cssSelector("#wrap > div.p_con > fieldset > div > table.g_table_01.bdb_n > tbody > tr")));

		// 특약별(상품명) 보험료 조회
		logger.info("특약별(상품명) 보험료 조회");
		for (WebElement el : trElements) {

			String productName = el.findElement(By.cssSelector(" td:nth-child(2)")).getText();

			for (CrawlingTreaty treaty : info.treatyList) {
				if (treaty.treatyName.equals(productName)) {
					String premium = "";
					premium = el.findElement(By.cssSelector(" td:nth-child(6)")).getText().replaceAll("[^0-9]", "");
					premium = premium.trim();
					treaty.monthlyPremium = premium;
					logger.info("productName :: "+productName+" premium :: " + premium );
					break;
				}

			}
		}

		info.errorMsg = "";
		info.returnPremium = "0";
		info.annuityPremium = "0";
		WaitUtil.loading(1);
	}

	// ** 사용자 웹   ** //

	// 사용자 웹에서 해약환급금 가쟈오기
	protected void webGetReturns(CrawlingProduct info) {

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

		elements = driver
			.findElements(By.cssSelector("#planPonB1 > div > div.g_layer_container > div > div > table > tbody > tr "));

		for (WebElement trEl : elements) {
			String term = trEl.findElement(By.cssSelector("td:nth-child(1)")).getAttribute("innerText"); // 경과기간
			String premiumSum = trEl.findElement(By.cssSelector("td:nth-child(3)")).getAttribute("innerText"); // 납입보험료
			String returnMoney = trEl.findElement(By.cssSelector("td:nth-child(4)")).getAttribute("innerText"); // 해약환급금
			String returnRate = trEl.findElement(By.cssSelector("td:nth-child(5)")).getAttribute("innerText"); // 환급률

			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--최저납입보험료: {}", premiumSum);
			logger.info("|--환급률: {}", returnRate);
			logger.info("|=========================");
			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			planReturnMoney.setGender("F");
			planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

			planReturnMoney.setTerm(term); // 경과기간
			planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
			planReturnMoney.setReturnMoney(returnMoney); // 환급금
			planReturnMoney.setReturnRate(returnRate); // 환급률

			planReturnMoneyList.add(planReturnMoney);

			// 계산테이블에 값 추가
			info.returnPremium = returnMoney.replace(",", "").replace("원", "");
		}
		info.setPlanReturnMoneyList(planReturnMoneyList);
	}

	// 사용자 웹 보험료 가져오기
	protected void webSetMonthlyPremium(By by, CrawlingProduct info) {
		String premium ;
		helper.waitVisibilityOfElementLocated(by);
		element = driver.findElement(by);
		//  premium에 '원'이 붙어서 원을 제거해 주었습니다.
		premium = element.getText().replace(",", "").replace("원", "");
		logger.info("월 보험료: " + premium + "원");
		info.treatyList.get(0).monthlyPremium = premium;
	}



	// --------------------------- 모바일

	// 모바일 성별 선택
	protected void mobileSelectGender(int gender){
		// 남성일 경우
		if (gender == 0) {
			element = driver.findElement(By.cssSelector("#pop_joinInfo > div.inner > div.g_layer_container > div.inside > div > div:nth-child(2) > ul > li:nth-child(1) > label"));
			logger.info("남성");
		} else { // 여성일 경우
			element = driver.findElement(By.cssSelector("#pop_joinInfo > div.inner > div.g_layer_container > div.inside > div > div:nth-child(2) > ul > li:nth-child(2) > label"));
			logger.info("여성");
		}
		element.click();
	}

	// 모바일 월 보험료 가져오기
	protected void mobileSetMonthlyPremium(By by, CrawlingProduct info) {
		String premium ;
		helper.waitVisibilityOfElementLocated(by);
		element = driver.findElement(by);
		//  premium에 '원'이 붙어서 원을 제거해 주었습니다.
		premium = element.getText().replace(",", "").replace("원", "");
		logger.info("월 보험료: " + premium + "원");
		info.treatyList.get(0).monthlyPremium = premium;
	}

	// 모바일에서 해약환급금 가져오기
	protected void mobileGetReturns(CrawlingProduct info) {

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

		elements = driver
				.findElements(By.cssSelector("#tabcont_mini02 > div > table > tbody > tr "));

		for (WebElement trEl : elements) {
			String term = trEl.findElement(By.cssSelector("td:nth-child(1)")).getAttribute("innerText"); // 경과기간
			String premiumSum = trEl.findElement(By.cssSelector("td:nth-child(2)")).getAttribute("innerText"); // 납입보험료
			String returnMoney = trEl.findElement(By.cssSelector("td:nth-child(3)")).getAttribute("innerText"); // 해약환급금
			String returnRate = trEl.findElement(By.cssSelector("td:nth-child(4)")).getAttribute("innerText"); // 환급률

			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--최저납입보험료: {}", premiumSum);
			logger.info("|--환급률: {}", returnRate);
			logger.info("|_______________________");

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			// planReturnMoney.setGender("F");
			planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

			planReturnMoney.setTerm(term); // 경과기간
			planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
			planReturnMoney.setReturnMoney(returnMoney); // 환급금
			planReturnMoney.setReturnRate(returnRate); // 환급률

			planReturnMoneyList.add(planReturnMoney);

			info.returnPremium = returnMoney.replace(",", "").replace("원", "");
		}
		info.setPlanReturnMoneyList(planReturnMoneyList);
	}


	/**
	 * 리뉴얼된 모바일 성별 선택
	 * @param gender {int} - 성별 gender = 0 ( 남성  ) , gender = 1 ( 여성  )
	 */
	protected void mRenewalSelectGender(int gender){
		// 남성일 경우
		if (gender == 0) {
			element = driver.findElement(By.cssSelector("#inquiry > div.slide-pop.active > div.slide-pop-contents > div.checkbox-sex > label:nth-child(1) > span"));
			logger.info("남성");
		} else { // 여성일 경우
			element = driver.findElement(By.cssSelector("#inquiry > div.slide-pop.active > div.slide-pop-contents > div.checkbox-sex > label:nth-child(2) > span"));
			logger.info("여성");
		}
		element.click();
	}

	/**
	 * 리뉴얼된 모바일 성별 선택
	 * @param by {By} - by 태그
	 * @param info {CrawlingProduct} - 상품 크롤링 객체
	 */
	protected void mRenewalMonthlyPremium(By by, CrawlingProduct info) {
		String premium ;
		helper.waitVisibilityOfElementLocated(by);
		element = driver.findElement(by);
		//  premium에 '원'이 붙어서 원을 제거해 주었습니다.
		premium = element.getText().replace(",", "").replace("원", "");
		logger.info("월 보험료: " + premium + "원");
		info.treatyList.get(0).monthlyPremium = premium;
	}

	/**
	 * 리뉴얼된 생년월일 세팅
	 * @param by {By} - by 태그
	 * @param value {String} - 생년월일
	 */
	protected void mRenewalDoInputBox(By by, String value) {
		element = driver.findElement(by);
		element.click();
		element.clear();
		element.sendKeys(value);
	}


	/**
	 * 리뉴얼된 모바일 해약환급금 세팅
	 * @param info {CrawlingProduct} - 상품 크롤링 객체
	 */
	protected void mRenewalGetReturns(CrawlingProduct info) throws InterruptedException {

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

		// 해약환급금 버튼 클릭!
		helper.click(By.cssSelector("#tabA2_2 > button"));
		WaitUtil.loading(3);

		elements = driver
			.findElements(By.cssSelector("#rescsSrvlRefundTable > tbody > tr "));

		for (WebElement trEl : elements) {

			if(trEl.getAttribute("id").equals("rescsSrvlRefundDataHtml")){
				continue;
			}

			String term = trEl.findElements(By.tagName("td")).get(0).getAttribute("innerText"); // 경과기간
			String premiumSum = trEl.findElements(By.tagName("td")).get(1).getAttribute("innerText"); // 납입보험료
			String returnMoney = trEl.findElements(By.tagName("td")).get(2).getAttribute("innerText"); // 해약환급금
			String returnRate = trEl.findElements(By.tagName("td")).get(3).getAttribute("innerText"); // 환급률

			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--환급률: {}", returnRate);
			logger.info("|_______________________");

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();

			planReturnMoney.setTerm(term); // 경과기간
			planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
			planReturnMoney.setReturnMoney(returnMoney); // 환급금
			planReturnMoney.setReturnRate(returnRate); // 환급률

			planReturnMoneyList.add(planReturnMoney);

			if(info.treatyList.get(0).productKind == ProductKind.순수보장형) {
				info.returnPremium = "0";
			} else {
				info.returnPremium = returnMoney.replace(",", "").replace("원", "");
			}
		}
		info.setPlanReturnMoneyList(planReturnMoneyList);
	}


	// 특약 개수 비교
	protected boolean compareTreaty(CrawlingProduct product, boolean result){

		// 특약개수가 다를경우 result = false 처리
		if(product.treatyList.size() != product.siteProductMasterCount){
			logger.info("특약개수가 다릅니다.");
			logger.info("상품의 특약 개수 :: " + product.treatyList.size());
			logger.info("DB에서 일치하는 특약 개수 :: " + product.siteProductMasterCount);
			result = false;
		} else { // 특약개수가 같아도 상품 특약,DB에서 일치하는 특약 개수를 확인할 수 있는 log 추가
			logger.info("특약개수가 똑같습니다.");
			logger.info("상품의 특약 개수 :: " + product.treatyList.size());
			logger.info("DB에서 일치하는 특약 개수 :: " + product.siteProductMasterCount);
			result = true;
		}

		return result;

	}

	// 공시실 - 보험가입금액 조회 세팅
	protected void doSelectAssureMoneySet(CrawlingProduct info) {
		elements = driver.findElements(By.cssSelector("#product_amount > option"));
		for(WebElement option : elements){
			if(option.getAttribute("value").contains(info.assureMoney)){
				logger.info("|				"+ option.getText()+" 클릭!");
				option.click();
				break;
			}
		}
	}

	// 공시실 - 보험료 세팅 후 납입 보험료 가져오기
	protected void getAssureMoney(CrawlingProduct info) throws Exception {
		WaitUtil.loading(4);
		String premium = ((JavascriptExecutor) driver).executeScript("return $(\"#premium\").val();").toString().replace(",","");
		logger.info("월 보험료: {} 원" , premium);
		info.treatyList.get(0).monthlyPremium = premium;
	}

	/*
	 *
	 *  라이나생명 공시실 스크롤 맨 밑으로 내리기
	 *
	 * */
	protected void discusroomscrollbottom(){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(""
			+ " var $div = $(\"#mainDiv\");"
			+ " $div.scrollTop($div[0].scrollHeight);");
	}

	/*
	 *
	 *  라이나생명 공시실 팝업 스크롤 맨 밑으로 내리기
	 *
	 * */
	protected void discusroomAlertScrollbottom(){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0,document.body.scrollHeight);");
	}

	/*
	 *
	 *  라이나생명 사이트웹 스크롤 맨 밑으로 내리기
	 *
	 * */
	protected void webscrollmove(int count){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,document.body.scrollHeight/"+count+");");
	}

	/*
	 *
	 *   라이나생명 특약 가져오기
	 *
	 */

	protected void getMainTreaty(CrawlingProduct info) {

		List<String> insTerms = new ArrayList<>();    // 보험기간List
		List<String> napTerms = new ArrayList<>();    // 납입기간List
		List<String> assureMoneys = new ArrayList<>(Collections.singletonList("9900"));// 가입금액
		List<String> napCycles = new ArrayList<>();   // 납입주기

		List<String> productKinds = new ArrayList<>(); // 보험종류
		List<String> productTypes = new ArrayList<>();

		// 보험기간 조회
		List<WebElement> insTermOptionElements = driver
			.findElements(By.cssSelector("#policy_period > option"));

		for (WebElement element : insTermOptionElements) {
			String text;
			text = element.getText();
			text = text.replaceAll("만기", "");
			text = text.replaceAll(" ", "");
			if (!"선택".equals(text)) {
				insTerms.add(text);
			}
		}

		// 납입기간 조회
		List<WebElement> napTermOptionElements = driver
			.findElements(By.cssSelector("#pay_period > option"));

		for (WebElement element : napTermOptionElements) {
			String text;
			text = element.getText();
			text = text.replaceAll("납", "");
			text = text.replaceAll(" ", "");
			if (!"선택".equals(text)) {
				napTerms.add(text);
			}
		}

		// 납입주기 조회
		List<WebElement> napCycleOptionElements = driver
			.findElements(By.cssSelector("#premium_mode > option"));

		for (WebElement element : napCycleOptionElements) {
			String text;
			text = element.getText();
			if (!"선택".equals(text)) {
				napCycles.add(text);
			}
		}

		// 주보험 가입금액
		List<WebElement> mainElements = driver.findElements(By.cssSelector(
			"#wrap > div.p_con > fieldset > div.cont_right > div:nth-child(3) > table > tbody > tr"));

		String productName = "";
		for (WebElement element : mainElements) {

			List<WebElement> tdElements = element.findElements(By.tagName("td"));

			productName = tdElements.get(0).getText();
			String productKind = tdElements.get(1).getText();

			//보험구분
			productKinds.add(productKind);

			// 보험종류
			String productType = productName.contains("비갱신") ? "비갱신형" : "갱신형";
			productTypes.add(productType);

		}

		String minAssureMoney = assureMoneys.get(0);
		String maxAssureMoney = assureMoneys.get(assureMoneys.size() - 1);

		ProductMasterVO productMasterVO = new ProductMasterVO();
		productMasterVO.setProductId(info.productCode);
		productMasterVO.setProductKinds(productKinds); // 정확히 알면 표기
		productMasterVO.setProductTypes(productTypes); // 정확히 알면 표기
		productMasterVO.setProductGubuns("주계약");
		productMasterVO.setSaleChannel(info.getSaleChannel());
		productMasterVO.setProductName(productName);
		productMasterVO.setInsTerms(insTerms);
		productMasterVO.setNapTerms(napTerms);
		productMasterVO.setNapCycles(napCycles);
		productMasterVO.setAssureMoneys(assureMoneys);
		productMasterVO.setMinAssureMoney(minAssureMoney);
		productMasterVO.setMaxAssureMoney(maxAssureMoney);
		productMasterVO.setCompanyId(info.getCompanyId());

		logger.info("상품마스터 :: " + productMasterVO.toString());
		info.getProductMasterVOList().add(productMasterVO);
	}

	protected void getReturnMoneyList(CrawlingProduct info) {

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

		elements = driver.findElements(
			By.cssSelector(
				"#wrap > div.p_con > fieldset > div > table:nth-child(13) > tbody > tr"));

		for (WebElement trEl : elements) {
			String term = trEl.findElements(By.tagName("td")).get(0)
				.getText(); // 경과기간
			String premiumSum = trEl.findElements(By.tagName("td")).get(2)
				.getText();    // 납입보험료
			String returnMoney = trEl.findElements(By.tagName("td")).get(3)
				.getText();    // 해약환급금
			String returnRate = trEl.findElements(By.tagName("td")).get(4)
				.getText().replace(" %","");    // 환급률

			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--환급률: {}", returnRate);
			logger.info("|_______________________");

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			planReturnMoney.setGender("F");
			logger.info(info.getGenderEnum().name());
			planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

			planReturnMoney.setTerm(term); // 경과기간
			planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
			planReturnMoney.setReturnMoney(returnMoney); // 환급금
			planReturnMoney.setReturnRate(returnRate); // 환급률
			planReturnMoneyList.add(planReturnMoney);

			info.returnPremium = returnMoney.replace(",", "").replace("원", "");

		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

	}

	/*********************************************************
	 * <성별 세팅 메소드>
	 * @param  productObj {Object} - 성별 객체
	 * @throws SetGenderException - 성별 세팅 시 예외처리
	 *********************************************************/
	@Override
	public void setGenderNew(Object productObj) throws SetGenderException {
		CrawlingProduct info = (CrawlingProduct) productObj;

		try {
			doRadioButton(By.name("gender"), Integer.toString(info.gender));
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
			doInputBox(By.id("iresid_no1"), info.fullBirth);
			WaitUtil.loading(2);
		}

		catch(Exception e){
			throw new SetBirthdayException(e.getMessage());
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

			doSelectBox(By.id("policy_period"), info.insTerm);
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

			doSelectBox(By.id("pay_period"), info.napTerm);
			WaitUtil.loading(2);

		}catch (Exception e){
			throw new SetNapTermException(e.getMessage());
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

			getAssureMoney(info);

		} catch (Exception e) {
			throw new PremiumCrawlerException(e.getMessage());
		}
	}


	/*********************************************************
	 * <해약환급금 세팅 메소드>
	 * @param  productObj {Object} - 크롤링 상품 객체
	 * @throws ReturnMoneyListCrawlerException - 해약환급금 세팅시 예외처리
	 *********************************************************/
	public void crawlReturnMoneyListNew(Object productObj) throws ReturnMoneyListCrawlerException {

		CrawlingProduct info = (CrawlingProduct) productObj;

		try {
			getReturnMoneyList(info);
		} catch (Exception e) {
			throw new ReturnMoneyListCrawlerException(e.getMessage());
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
	 * @throws SetRenewTypeException - 갱신유형 세팅시 예외처리
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
	 * <가입금액 세팅 메소드>
	 * @param  productObj {Object} - 크롤링 상품 객체
	 * @throws SetAssureMoneyException - 가입금액 세팅 시 예외처리
	 *********************************************************/
	@Override
	public void setAssureMoneyNew(Object productObj) throws SetAssureMoneyException {
		CrawlingProduct info = (CrawlingProduct) productObj;

		try {
			doSelectAssureMoneySet(info);
			WaitUtil.loading(4);
		} catch (Exception e) {
			throw new SetAssureMoneyException(e.getMessage());
		}
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

	/*
	 *
	 *  스크롤 맨 밑으로 내리기
	 *
	 * */
	protected void scrollBottom() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,document.body.scrollHeight);");
	}

	/*********************************************************
	 * <해약환급금 세팅 메소드>
	 * @param  info {CrawlingProduct} - 크롤링 상품 객체
	 *********************************************************/
	protected void getReturns(CrawlingProduct info) throws InterruptedException {

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

		elements = driver.findElements(
			By.cssSelector(
				"#wrap > div.p_con > fieldset > div > table:nth-child(12) > tbody > tr"));

		for (WebElement trEl : elements) {
			String term = trEl.findElements(By.tagName("td")).get(0)
				.getText(); // 경과기간
			String premiumSum = trEl.findElements(By.tagName("td")).get(2)
				.getText();    // 납입보험료
			String returnMoney = trEl.findElements(By.tagName("td")).get(3)
				.getText();    // 해약환급금
			String returnRate = trEl.findElements(By.tagName("td")).get(4)
				.getText().replace(" %", "");    // 환급률

			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--환급률: {}", returnRate);
			logger.info("|_______________________");

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			planReturnMoney.setGender("F");
			planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

			planReturnMoney.setTerm(term); // 경과기간
			planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
			planReturnMoney.setReturnMoney(returnMoney); // 환급금
			planReturnMoney.setReturnRate(returnRate); // 환급률
			planReturnMoneyList.add(planReturnMoney);

			info.returnPremium = returnMoney.replace(",", "").replace("원", "");

		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

	}

	//버튼 클릭
	protected void btnClick(WebElement element) throws  Exception {
		element.click();
	}

	//select box에서 text와 일치하는 option 클릭하는 메서드
	protected void selectOptionByText(WebElement element, String text) throws Exception{
		Select select = new Select(element);
		try {
			select.selectByVisibleText(text);
		}catch (NoSuchElementException e) {
			throw new NoSuchElementException("selectbox에서 해당 text(" + text + ")를 찾을 수 없습니다");
		}
	}


}
