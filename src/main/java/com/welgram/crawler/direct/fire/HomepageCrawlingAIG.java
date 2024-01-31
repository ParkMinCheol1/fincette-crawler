package com.welgram.crawler.direct.fire;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/*
 * 2020.12.01
 * @author 조하연
 * AIG 상품 홈페이지용 클래스
 * */


//AIG손해보험 상품 중 홈페이지에서 크롤링해오는 상품에 대해서는 HomepageCrawlingAIG를 상속받는다.
public abstract class HomepageCrawlingAIG extends SeleniumCrawler {
	//크롤링 옵션 정의 메서드
	protected void setChromeOptionAIG(CrawlingProduct info) throws Exception{
		CrawlingOption option = info.getCrawlingOption();

		option.setBrowserType(CrawlingOption.BrowserType.Chrome);
		option.setImageLoad(false);
		option.setUserData(false);

		info.setCrawlingOption(option);
	}

	//버튼 클릭 메서드
	protected void btnClick(By element) throws Exception{
		driver.findElement(element).click();
		waitHomepageLoadingImg();
		WaitUtil.waitFor(2);
	}

	//버튼 클릭 메서드
	protected void btnClick(WebElement element) throws Exception{
		element.click();
		waitHomepageLoadingImg();
		WaitUtil.waitFor(2);
	}


	//inputBox에 텍스트 입력하는 메서드
	protected void setTextToInputBox(By element, String text) {
		WebElement inputBox = driver.findElement(element);
		inputBox.click();
		inputBox.clear();
		inputBox.sendKeys(text);
	}


	//홈페이지용 보험료 계산 버튼 클릭 메서드
	protected void homepageCalcBtnClick() throws Exception{
		btnClick(By.linkText("보험료 계산"));
	}

	//홈페이지용 직접 설계 버튼 클릭 메서드
	protected void homepageDirectDesignBtnClick() throws Exception{
		btnClick(By.linkText("직접 설계"));
	}


	//홈페이지용 오늘날짜를 기준으로 +day일한 날짜를 "yyyyMMdd" 형태의 문자열로 리턴한다.
	protected String homepagePlusDateBasedOnToday(int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String date = null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, day);

		date = sdf.format(cal.getTime());

		return date;
	}


	//select box에서 text가 일치하는 option을 클릭하는 메서드
	protected void selectOption(By element, String text) {
		Select select = new Select(driver.findElement(element));
		select.selectByVisibleText(text);
	}


	//select box에서 text가 일치하는 option을 클릭하는 메서드
	protected void selectOption(WebElement selectEl, String text) {
		Select select = new Select(selectEl);
		select.selectByVisibleText(text);
	}


	//홈페이지용 로딩이미지 명시적 대기
	protected void waitHomepageLoadingImg() {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadBox")));
	}


	//홈페이지용 성별 설정 메서드
	protected void setHomepageGender(int gender) throws Exception{
		String genderTag = (gender == MALE) ? "man" : "woman";
		btnClick(By.cssSelector("label[for='" + genderTag + "']"));
	}


	//홈페이지용 생년월일 설정 메서드(1개 입력)
	protected void setHomepageBirth(By element, String fullBirth) {
		setTextToInputBox(element, fullBirth);
	}


	//홈페이지용 플랜 설정 메서드
	protected String setHomepagePlan(String planType) {
		String monthlyPremium = "";
		List<WebElement> planList = driver.findElements(By.cssSelector("div.directPlan > div.directPlanSubIn > strong"));

		for(WebElement planEl : planList) {
			String targetPlanType = planEl.getText();

			if(targetPlanType.contains(planType)) {
				WebElement premiumEl = planEl.findElement(By.xpath("parent::div")).findElement(By.cssSelector("div.potBox > p"));
				monthlyPremium = premiumEl.getText().replaceAll("[^0-9]", "");

				return monthlyPremium;
			}
		}

		return monthlyPremium;
	}


	//홈페이지용 납입기간, 보험기간 동시 설정 메서드
	protected void setHomepageTerms(String napTerm, String insTerm) throws Exception{
		String terms = napTerm + "납 " + insTerm + "만기";
		selectOption(By.id("termChk"), terms);
		WaitUtil.waitFor(2);
	}

	//홈페이지용 특약별 가입금액 설정 메서드
	protected void setHomepageSubTreaties(List<CrawlingTreaty> treatyList) {
		List<WebElement> trList = driver.findElements(By.cssSelector("#prodContents tr"));

		for(CrawlingTreaty treaty : treatyList) {
			String myTreatyName = treaty.treatyName;
			int assureMoney = treaty.assureMoney;

			for(WebElement tr : trList) {
				List<WebElement> tdList = tr.findElements(By.tagName("td"));

				String targetTreatyName = tdList.get(1).findElement(By.tagName("a")).getText();
				if(myTreatyName.equals(targetTreatyName)) {
					try {
						WebElement checkBox = tdList.get(0).findElement(By.cssSelector("input[type=checkbox]"));

						//체크박스가 존재하며, 체크해제 돼있다면 클릭
						if(checkBox != null && !checkBox.isSelected()) {
							checkBox.click();
						}

						//특약 가입금액 설정
						String convertedAssureMoney = convertHomepageAssureMoney(assureMoney);
						WebElement selectEl = tdList.get(2).findElement(By.tagName("select"));
						selectOption(selectEl, convertedAssureMoney);

						logger.info("특약명 : {}, 가입금액 : {}", myTreatyName, convertedAssureMoney);
					}catch(NoSuchElementException e){

					}
					break;
				}
			}
		}
	}

	//홈페이지용 가입금액 변환 메서드
	protected String convertHomepageAssureMoney(int assureMoney) {
		String _assureMoney = String.valueOf(assureMoney);
		int value = Integer.parseInt(_assureMoney.substring(0, _assureMoney.indexOf("0")));
		String unit = "";

		switch (assureMoney / value) {
			case 10000000 :
				unit = "천만원";
				break;
			case 1000000 :
				unit = "백만원";
				break;
			case 100000 :
				unit = "십만원";
				break;
			case 10000 :
				unit = "만원";
				break;
		}

		unit = value + unit;

		return unit;
	}


	//홈페이지용 주계약 보험료 설정 메서드
	protected void setHomepagePremium(CrawlingTreaty mainTreaty, String monthlyPremium) {
		mainTreaty.monthlyPremium = monthlyPremium;
	}

	//홈페이지용 주계약 보험료 설정 메서드
	protected void setHomepagePremium(CrawlingProduct info) {
		String monthlyPremium = driver.findElement(By.className("priceRbox")).getText().replaceAll("[^0-9]", "");
		
		logger.info("월 보험료 : {}", monthlyPremium + "원");
		info.treatyList.get(0).monthlyPremium = monthlyPremium;
	}


	//홈페이지용 출발일 설정 메서드(여행자 보험의 경우 사용)
	protected void setHomepageDeparture() {
		String departureDate = homepagePlusDateBasedOnToday(1);
		String deparetureTime = "01시";

		logger.info("출발일 : {}", departureDate);
		setTextToInputBox(By.id("insStDt"), departureDate);

		logger.info("출발시간 : {}", deparetureTime);
		selectOption(By.id("ctrSttm"), deparetureTime);
	}


	//홈페이지용 도착일 설정 메서드(여행자 보험의 경우 사용)
	protected void setHomepageArrival() {
		String arrivalDate = homepagePlusDateBasedOnToday(8);
		String arrivalTime = "01시";

		logger.info("도착일 : {}", arrivalDate);
		setTextToInputBox(By.id("insEndt"), arrivalDate);

		logger.info("도착시간 : {}", arrivalTime);
		selectOption(By.id("ctrCltm"), arrivalTime);
	}


}
