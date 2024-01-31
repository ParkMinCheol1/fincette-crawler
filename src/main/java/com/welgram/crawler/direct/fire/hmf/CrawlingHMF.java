package com.welgram.crawler.direct.fire.hmf;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author SungEun Koo <aqua@welgram.com> 흥국화재
 */

public abstract class CrawlingHMF extends SeleniumCrawler {

	private final String NAME = "김흥국";
	private String subHandle;

	protected void setParentInfo(By id) throws InterruptedException {

	}

	//element가 보이게끔 이동
	protected void moveToElement(WebElement element) {
		Actions action = new Actions(driver);
		action.moveToElement(element).build();
		action.perform();
	}

	//select box에서 text가 일치하는 option을 클릭하는 메서드
	protected void selectOption(By by, String text) throws Exception{
		WebElement element = driver.findElement(by);

		selectOption(element, text);
	}

	//select box에서 text가 일치하는 option을 클릭하는 메서드
	protected void selectOption(WebElement element, String text) throws Exception{
		Select select = new Select(element);

		try {
			select.selectByVisibleText(text);
		} catch (NoSuchElementException e) {
			throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')를 찾을 수 없습니다.");
		}
	}

	//inputBox에 텍스트 입력하는 메서드
	protected void setTextToInputBox(By element, String text) {
		WebElement inputBox = driver.findElement(element);
		inputBox.click();
		inputBox.clear();
		inputBox.sendKeys(text);
	}

	//inputBox에 텍스트 입력하는 메서드
	protected void setTextToInputBox(WebElement element, String text) {
		WebElement inputBox = element;
		inputBox.click();
		inputBox.clear();
		inputBox.sendKeys(text);
	}

	// 이름
	protected void setName(By id) throws InterruptedException {
		element = driver.findElement(id);
		element.clear();
		element.sendKeys(NAME);
	}

	// 생년월일
	protected void setBirth(String birth) throws Exception {
		
		boolean result1 = false;
		boolean result2 = false;
		boolean result3 = false;
		
		// 년도
		elements = driver.findElement(By.cssSelector("#info_YY")).findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (option.getText().trim().equals(birth.substring(0, 4))) {
				option.click();
				result1 = true;
				WaitUtil.waitFor();;
				break;
			}
		}

		// 월
		elements = driver.findElement(By.cssSelector("#info_MM")).findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (option.getText().trim().equals(birth.substring(4, 6))) {
				option.click();
				result2 = true;
				WaitUtil.waitFor();;
				break;
			}
		}
		
		// 일
		elements = driver.findElement(By.cssSelector("#info_DD")).findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (option.getText().trim().equals(birth.substring(6, 8))) {
				option.click();
				result3 = true;
				WaitUtil.waitFor();;
				break;
			}
		}
		
		if (!(result1 && result2 && result3)) {
			throw new Exception("생년월일 selectBox 선택 오류!");
		} 
		
		logger.debug("생년월일 입력 완료");
		
	}

	// 성별
	protected void setGender(int gender) throws InterruptedException {
		if (gender == MALE) {
			element = driver.findElement(By.cssSelector("#info_GENDER_1"));
		} else {
			element = driver.findElement(By.cssSelector("#info_GENDER_2"));
		}
		element.click();
		WaitUtil.waitFor();;
		
		logger.debug("성별 클릭 완료");
	}

	// 조회 클릭
	protected void clickButton(By className, String text) throws Exception {
		elements = driver.findElements(className);
		for (WebElement txt : elements) {
			if (txt.getText().trim().equals(text)) {
				txt.click();
				//waitForCSSElement("#dataLoadingBar");
				helper.waitForCSSElement("#id_gongsiPriceProgress");
				//WaitUtil.waitFor();;
				
				logger.debug(text + " 클릭 완료");
			}
		}
	}

	// 해약환급금
	protected void getReturnPremium(CrawlingProduct info) throws Exception {
		String returnPremium = "";
		String value = "";
		boolean result = false;
		int num = 0;

		if (info.napTerm.equals("일시납")) {
			value = info.insTerm;
		} else {
			value = info.napTerm;
		}

		if (info.productCode.equals("TYL00129")) {
			num = 8;
		}
		if (info.productCode.equals("TYL00130")) {
			num = 10;
		}
		if (info.productCode.equals("TYL00132")) {
			int reTurnYear = 0;
			num = 3;
			reTurnYear = Integer.parseInt(info.insTerm.replaceAll("[^0-9]", "")) - Integer.parseInt(info.age);
			value = reTurnYear + "년";
		}

		element = driver.findElement(By.cssSelector(".tblCol.tableCyber")).findElement(By.tagName("tbody"));
		elements = element.findElements(By.tagName("tr"));
		for (WebElement tr : elements) {
			element = tr.findElements(By.tagName("td")).get(0);
			if (element.getText().trim().equals(value)) {
				element = tr.findElements(By.tagName("td")).get(num);
				returnPremium = element.getText().trim().replaceAll("[^0-9]", "") + "0000";
				logger.debug("해약환급금: " + returnPremium + "원");
				result = true;
				break;
			}
		}
		if (!result) {
			throw new Exception("해약환급금을 찾을 수 없습니다.");
		}

		//info.monthlyPremium = Integer.parseInt(info.premium + "0000");
		//info.treatyList.get(0).monthlyPremium = "0";
		info.returnPremium = returnPremium;
		info.errorMsg = "";
	}

	protected void getReturnMoney(CrawlingProduct info) throws Exception {
		
		logger.info("해약환급금 테이블선택");
		
		element = driver.findElement(By.cssSelector(".tblCol.tableCyber")).findElement(By.tagName("tbody"));
		elements = element.findElements(By.tagName("tr"));
		
		// 주보험 영역 Tr 개수만큼 loop
		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		for (WebElement tr : elements) {
			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			String term = tr.findElements(By.tagName("td")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(2).getText() + "0000";
			String returnMoney = tr.findElements(By.tagName("td")).get(3).getText() ;
			String returnRate = tr.findElements(By.tagName("td")).get(4).getText();
			logger.info(term + " :: " + premiumSum );
			if (!returnMoney.equals("0")){
				returnMoney =  returnMoney + "0000";				
			}
//			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
//			planReturnMoney.setGender(info.getGender() == MALE ? "M" : "F");
//			planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoneyList.add(planReturnMoney);
			
			// 기본 해약환급금 세팅
			info.returnPremium = returnMoney.replace(",", "").replace("원", "");
			logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);
		}
		
		info.setPlanReturnMoneyList(planReturnMoneyList);
		// 해약환급금 관련 End
	}	
	
	// 보험종류
	protected void insuType(By id, String insuName, String code) throws InterruptedException {
		String text = "";
		if (code.equals("TYL00127")) {
			if (insuName.contains("순수보장형")) {
				text = "순수보장형";
			}
			if (insuName.contains("무해약환급형")) {
				text = "무해약환급형";
			}
		}
		if (code.equals("TYL00128")) {
			if (insuName.contains("표준형-상해보장형(입원-비위험)-최초")) {
				text = "표준형-상해보장형(입원-비위험)-최초";
			}
			if (insuName.contains("선택형Ⅱ-상해보장형(입원-비위험)-최초")) {
				text = "선택형Ⅱ-상해보장형(입원-비위험)-최초";
			}
		}
		if (code.equals("TYL00131")) {
			if (insuName.contains("순수보장형")) {
				text = "(순수보장형)-표준체";
			}
			if (insuName.contains("무해약환급형")) {
				text = "(무해약환급형)-표준체";
			}
		}
		if (code.equals("TYL00132")) {
			if (insuName.contains("순수보장형")) {
				text = "순수보장형";
			}
			if (insuName.contains("100%환급형")) {
				text = "100%환급형";
			}
		}
		if (code.equals("TYL00133")) {
			if (insuName.contains("프리미엄형")) {
				text = "프리미엄형";
			}
			if (insuName.contains("실속형")) {
				text = "실속형";
			}
		}
		if (code.equals("TYL00134")) {
			if (insuName.contains("태아형")) {
				text = "태아형";
			}
			if (insuName.contains("어린이형")) {
				text = "어린이형";
			}
		}
		if (code.equals("TYL00135")) {
			if (insuName.contains("어린이보험 80")) {
				text = "어린이보험 80";
			}
			if (insuName.contains("어린이보험 30")) {
				text = "어린이보험 30";
			}
		}

		elements = driver.findElement(id).findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (option.getText().contains(text)) {
				option.click();
				WaitUtil.waitFor();;
				break;
			}
		}
	}

	// 특약선택
	protected void setTreaty(String insuName) throws InterruptedException {
		
		if (insuName.contains("표준형-상해보장형(입원-비위험)-최초")) {
			driver.findElement(By.id("YUEG23AQ5")).click(); //YUEG23AQ5
			WaitUtil.waitFor();;
			driver.findElement(By.id("YUEH23AS5")).click(); //YUEH23AS5
			WaitUtil.waitFor();;
			driver.findElement(By.id("YUEL23AS6")).click(); //YUEL23AS6
			WaitUtil.waitFor();;
			
		}
		if (insuName.contains("선택형Ⅱ-상해보장형(입원-비위험)-최초")) {
			driver.findElement(By.id("YUFM23AQ5")).click();//YUFM23AQ5
			WaitUtil.waitFor();;
			driver.findElement(By.id("YUFN23AS5")).click();//YUFN23AS5
			WaitUtil.waitFor();;
			driver.findElement(By.id("YUFR23AS6")).click();//YUFR23AS6
			WaitUtil.waitFor();;

		}		
		
		driver.findElement(By.id("YUGI23AW5")).click();//YUGI23AW5 
		WaitUtil.waitFor();;
		driver.findElement(By.id("YUGU23AW5")).click();//YUGU23AW5
		WaitUtil.waitFor();;
		driver.findElement(By.id("YUHG23AW5")).click();//YUHG23AW5
		WaitUtil.waitFor();;
	}

	// 가입금액
	protected void setPremium(By id, String premium) throws InterruptedException {
		element = driver.findElement(id);
		element.click();
		Actions builder = new Actions(driver);
		builder.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
				.sendKeys(Keys.DELETE).build().perform();
		element.sendKeys(premium);
		WaitUtil.waitFor();;
	}

	// 월보험료
	protected void getPremium(CrawlingProduct info) throws InterruptedException {
		String premium = "";
		element = driver.findElement(By.id("ipbTotalPrem"));
		premium = element.getText().replaceAll("[^0-9]", "");
		logger.debug("월 보험료: " + premium + "원");
		
		info.treatyList.get(0).monthlyPremium = premium;
		info.errorMsg = "";

	}

	// 월보험료
	protected void getSubPremium(CrawlingTreaty crawlingTreaty) throws InterruptedException {
		boolean result = false;
		
		elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#step5_div > table > tbody > tr")));

		for (WebElement tr : elements) {
			
			String prdtNm = tr.findElements(By.tagName("td")).get(0).getText();
			
			// 담보명과 이름이 같은지 확인
			if (crawlingTreaty.treatyName.indexOf(prdtNm) > -1){
				// 가입금액
				String money = tr.findElements(By.tagName("td")).get(5).getText().replaceAll("[^0-9]", "");
				crawlingTreaty.monthlyPremium = money;
				result = true;
			}
			if (result) {
				break;
			}
		}

	}
	// 공시실 상품검색
	protected void openAnnouncePage(By id, String insuName) throws Exception {
		element = driver.findElement(id);
		//elements = element.findElements(By.cssSelector("h3.contitle"));
		elements = element.findElements(By.cssSelector("div > ul > li"));

		// 보험가격공시 - 일반보험 탭
		//#contents > div > ul > li:eq(0) > a > span
		
		// 보험가격공시 탭(일반보험 | 장기보험 | 자동차보험)
		for (WebElement li : elements) {
			//element = li.findElement(By.cssSelector("a > span:eq(0)"));
			String tabName = li.findElement(By.cssSelector("a > span")).getText();
			if (tabName.trim().equals("일반보험") && insuName.equals("더 든든한 이유다이렉트 운전자보험")) {
				li.findElement(By.cssSelector("a > span")).click();
				break;
			}
		}

		// 보험가격공시 - 상품목록
		//#contents > div > section > div.on > div > table > tbody
		
		elements = element.findElements(By.cssSelector("div > section > div.on > div > table > tbody > tr"));
		
		boolean breakYn = false;
		
		// 보험가격공시 - 상품목록
		for (WebElement tr : elements) {

			for (WebElement td : tr.findElements(By.cssSelector("td"))) {
				
				// 상품명
				String tdText = td.getText().trim();
				
				if (tdText.equals("더 든든한 이유다이렉트 운전자보험_CM") && insuName.equals("더 든든한 이유다이렉트 운전자보험")) {
					element = td.findElement(By.xpath("parent::*"));
					
					element.findElement(By.tagName("a")).click();
					
					logger.debug("보험료계산 팝업 오픈");
					
					WaitUtil.waitFor();;

					Set<String> windowId = driver.getWindowHandles();
					Iterator<String> handles = windowId.iterator();
					// 메인 윈도우 창 확인
					subHandle = null;

					while (handles.hasNext()) {
						subHandle = handles.next();
						WaitUtil.waitFor();;
					}

					driver.switchTo().window(subHandle);
					breakYn = true;
					break;
				}
			}
			
			if(breakYn) {
				break;
			}

		}
	}

	protected void selectBox(By id, String value) throws Exception {
		boolean result = false;
		elements = driver.findElement(id).findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (option.getText().trim().equals(value)) {
				option.click();
				result = true;
				WaitUtil.waitFor();;
				break;
			}
		}

		if (!result) {
			throw new Exception("selectBox 선택 오류!");
		}
	}

	protected void setNapTerm(By id, String napTerm) throws Exception {
		boolean result = false;
		if (napTerm.equals("01")) {
			napTerm = "월납";
		}
		if (napTerm.equals("00")) {
			napTerm = "일시납";
		}
		if (napTerm.equals("02")) {
			napTerm = "연납";
		}

		elements = driver.findElement(id).findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (option.getText().trim().equals(napTerm)) {
				option.click();
				result = true;
				WaitUtil.waitFor();;
				break;
			}
		}

		if (!result) {
			throw new Exception("selectBox 선택 오류!");
		}
	}

	// 보험계약기간 세팅
	protected void setInsTerm(String insTerm) throws Exception {
		
		//boolean result = false;
		//elements = driver.findElement(id).findElements(By.tagName("option"));
		
		int insTermVal = Integer.parseInt(insTerm.replaceAll("년", ""));
		
		Date today = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    String selStartDate = df.format(today);	// 계약 시작일
		
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.YEAR, (int) insTermVal);	// 오늘날짜에 보험기간을 더함
	    String selEndDate = df.format(cal.getTime()); // 계약 종료일
	    
		element = driver.findElement(By.id("selStart"));
		element.clear();
		element.sendKeys(selStartDate);	// 계약 시작일 입력
		WaitUtil.waitFor();;
		
		element = driver.findElement(By.id("selEnd"));
		element.click();
		element.clear();
		
		// alert 창 확인
		try {
		    WebDriverWait wait = new WebDriverWait(driver, 2000);
		    wait.until(ExpectedConditions.alertIsPresent());
		    Alert alert = driver.switchTo().alert();
		    alert.accept();
		    logger.debug(alert.getText() + " 얼럿창 확인 완료");
		} catch (Exception e) {
		    //exception handling
		}
		
		element.sendKeys(selEndDate);	// 계약 종료일 입력
		WaitUtil.waitFor();;
		
		logger.debug("보험계약기간 입력 완료");
		
	}
	
	// 라디오 버튼 클릭
	protected void radioBtn(By id, String value) throws Exception {
		
		List<WebElement> radioBtns = driver.findElements(id);
		
		for (WebElement radioBtn : radioBtns) {
			if(radioBtn.getAttribute("value").equals(value)) {
				radioBtn.click();
				break;
			}
		}	
		
		WaitUtil.waitFor();;
		
		logger.debug(value + " 라디오버튼 클릭 완료");
	}
	
	// 가입유형(실속형 | 표준형 | 고급형)
	protected void setSubscriptionType(String textType) throws Exception {
		
		String value = "";
		
		switch (textType) {
		case "실속형":
			value = "A";
			break;

		case "표준형":
			value = "B";
			break;
			
		case "고급형":
			value = "C";
			break;
		
		default:
			break;
		}
		
		radioBtn(By.name("grid_ch"), value);
		
	}

	//element 클릭 명시적 대기
	protected WebElement waitElementToBeClickable(WebElement element) throws Exception {
		WebElement returnElement = null;
		boolean isClickable = element.isDisplayed() && element.isEnabled();

		if(isClickable) {
			//element가 화면상으로 보이며 활성화 되어있을 때만 클릭 가능함
			returnElement = wait.until(ExpectedConditions.elementToBeClickable(element));
		} else {
			throw new Exception("element가 클릭 불가능한 상태입니다.");
		}

		return returnElement;
	}

	//element 보일때까지 명시적 대기
	protected WebElement waitPresenceOfElementLocated(By by) throws Exception {
		return wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}

	//element 보일때까지 명시적 대기
	protected List<WebElement> waitPresenceOfAllElementsLocatedBy(By by) throws Exception {
		return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
	}

	//element 보일때까지 명시적 대기
	protected WebElement waitVisibilityOfElementLocated(By by) throws Exception {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	//element 보일때까지 명시적 대기
	protected List<WebElement> waitVisibilityOfAllElementsLocatedBy(By by) throws Exception {
		return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
	}

	//element 보일때까지 명시적 대기
	protected List<WebElement> waitVisibilityOfAllElements(By by) throws Exception {
		List<WebElement> elements = driver.findElements(by);
		return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
	}

	//해당 element가 보이게 스크롤 이동
	protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
		((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	//해당 element가 보이게 스크롤 이동
	protected void moveToElementByJavascriptExecutor(By by) throws Exception {
		WebElement element = driver.findElement(by);
		((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	//공시실 페이지 로딩바 대기
	protected void waitAnnouncePageLoadingBar() throws Exception {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("commonProgressBar")));
	}

	protected Object executeJavascript(String script) {
		return ((JavascriptExecutor)driver).executeScript(script);
	}

	protected Object executeJavascript(String script, WebElement element) {
		return ((JavascriptExecutor)driver).executeScript(script, element);
	}
}
