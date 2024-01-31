package com.welgram.crawler.direct.life;

import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * @author aqua
 */
public abstract class CrawlingMRA extends SeleniumCrawler {
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
		wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.xpath("//div[@class[contains(., 'load')]]"))));
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading")));
	}


	protected Object executeJavascript(String script) {
		return ((JavascriptExecutor)driver).executeScript(script);
	}

	protected Object executeJavascript(String script, WebElement element) {
		return ((JavascriptExecutor)driver).executeScript(script, element);
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
		element.click();
		element.clear();
		element.sendKeys(text);
	}



	//select box에서 text와 일치하는 option 클릭하는 메서드
	protected void selectOptionByText(By by, String text) throws Exception{
		Select select = new Select(driver.findElement(by));

		try {
			select.selectByVisibleText(text);
		}catch (NoSuchElementException e) {
			throw new NoSuchElementException("selectbox에서 해당 text(" + text + ")를 찾을 수 없습니다");
		}
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


	//select box에서 value값이 일치하는 option 클릭하는 메서드
	protected void selectOptionByValue(By by, String value) throws Exception{
		Select select = new Select(driver.findElement(by));

		try {
			select.selectByValue(value);
		}catch (NoSuchElementException e) {
			throw new NoSuchElementException("selectbox에서 해당 value(" + value + ")를 찾을 수 없습니다");
		}
	}


	//select box에서 value값이 일치하는 option 클릭하는 메서드
	protected void selectOptionByValue(WebElement element, String value) throws Exception{
		Select select = new Select(element);

		try {
			select.selectByValue(value);
		}catch (NoSuchElementException e) {
			throw new NoSuchElementException("selectbox에서 해당 value(" + value + ")를 찾을 수 없습니다");
		}
	}


	protected boolean compareTreaties(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList) throws Exception {
		boolean result = true;

		List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
		List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트
		List<String> samedTreatyNameList = null;				//가입설계와 홈페이지 둘 다 일치하는 특약명 리스트


		//홈페이지 특약명 리스트
		List<String> homepageTreatyNameList = new ArrayList<>();
		List<String> copiedHomepageTreatyNameList = null;
		for(CrawlingTreaty t : homepageTreatyList) {
			homepageTreatyNameList.add(t.treatyName);
		}
		copiedHomepageTreatyNameList = new ArrayList<>(homepageTreatyNameList);


		//가입설계 특약명 리스트
		List<String> myTreatyNameList = new ArrayList<>();
		List<String> copiedMyTreatyNameList = null;
		for(CrawlingTreaty t : welgramTreatyList) {
			myTreatyNameList.add(t.treatyName);
		}
		copiedMyTreatyNameList = new ArrayList<>(myTreatyNameList);




		//일치하는 특약명만 추림
		homepageTreatyNameList.retainAll(myTreatyNameList);
		samedTreatyNameList = new ArrayList<>(homepageTreatyNameList);
		homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);



		//가입설계에 추가해야하는 특약명만 추림
		homepageTreatyNameList.removeAll(myTreatyNameList);
		toAddTreatyNameList = new ArrayList<>(homepageTreatyNameList);
		homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);



		//가입설계에서 제거해야하는 특약명만 추림
		myTreatyNameList.removeAll(homepageTreatyNameList);
		toRemoveTreatyNameList = new ArrayList<>(myTreatyNameList);
		myTreatyNameList = new ArrayList<>(copiedMyTreatyNameList);



		//특약명이 일치하는 경우에는 가입금액을 비교해준다.
		for(String treatyName : samedTreatyNameList) {
			CrawlingTreaty homepageTreaty = getCrawlingTreaty(homepageTreatyList, treatyName);
			CrawlingTreaty myTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);

			int homepageTreatyAssureMoney = homepageTreaty.assureMoney;
			int myTreatyAssureMoney = myTreaty.assureMoney;


			//가입금액 비교
			if(homepageTreatyAssureMoney == myTreatyAssureMoney) {
				//금액이 일치하는 경우
				logger.info("특약명 : {} | 가입금액 : {}원", treatyName, myTreatyAssureMoney);
			} else {
				//금액이 불일치하는 경우 특약정보 출력
				result = false;

				logger.info("[불일치 특약]");
				logger.info("특약명 : {}", treatyName);
				logger.info("가입설계 가입금액 : {}", myTreatyAssureMoney);
				logger.info("홈페이지 가입금액 : {}", homepageTreatyAssureMoney);
				logger.info("==============================================================");
			}
		}


		//가입설계 추가해야하는 특약정보 출력
		if(toAddTreatyNameList.size() > 0) {
			result = false;

			logger.info("==============================================================");
			logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
			logger.info("==============================================================");

			for(int i=0; i<toAddTreatyNameList.size(); i++) {
				String treatyName = toAddTreatyNameList.get(i);

				CrawlingTreaty treaty = getCrawlingTreaty(homepageTreatyList, treatyName);
				logger.info("특약명 : {}", treaty.treatyName);
				logger.info("가입금액 : {}", treaty.assureMoney);
				logger.info("==============================================================");
			}

		}



		//가입설계 제거해야하는 특약정보 출력
		if(toRemoveTreatyNameList.size() > 0) {
			result = false;

			logger.info("==============================================================");
			logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
			logger.info("==============================================================");

			for(int i=0; i<toRemoveTreatyNameList.size(); i++) {
				String treatyName = toRemoveTreatyNameList.get(i);

				CrawlingTreaty treaty = getCrawlingTreaty(welgramTreatyList, treatyName);
				logger.info("특약명 : {}", treaty.treatyName);
				logger.info("가입금액 : {}", treaty.assureMoney);
				logger.info("==============================================================");
			}
		}


		return result;
	}






	private CrawlingTreaty getCrawlingTreaty(List<CrawlingTreaty> treatyList, String treatyName) {
		CrawlingTreaty result = null;

		for(CrawlingTreaty treaty : treatyList) {
			if(treaty.treatyName.equals(treatyName)) {
				result = treaty;
			}
		}

		return result;
	}

}

