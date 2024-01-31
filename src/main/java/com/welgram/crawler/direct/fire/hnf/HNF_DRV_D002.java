package com.welgram.crawler.direct.fire.hnf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class HNF_DRV_D002 extends CrawlingHNFMobile {

	public static void main(String[] args) {
		executeCommand(new HNF_DRV_D002(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $button = null;

		// step1. 사용자 정보 입력
		logger.info("가입하기 버튼 클릭");
		$button = driver.findElement(By.id("btnJoin"));
		click($button);

		// 보험 시작일, 종료일 설정은 생략하도록 한다.
		logger.info("체크박스 모두 체크");
		List<WebElement> $labelList = driver.findElements(By.cssSelector("#lifeNoticeArea label[for^=chkNotice]"));
		for (WebElement $label : $labelList) {
			click($label);
		}

		logger.info("보험료 계산하기 버튼 클릭");
		$button = driver.findElement(By.id("btnStep01Next"));
		click($button);

		logger.info("생년월일 입력");
		setBirthday(info.getFullBirth());

		logger.info("성별 입력");
		setGender(info.getGender());

		logger.info("다음 버튼 클릭");
		$button = driver.findElement(By.id("btnStep010101Next"));
		click($button);

		logger.info("플랜 설정");
		setPlan(info.planSubName);

		logger.info("특약 설정");
		setTreatiesToggleType(info.getTreatyList());

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("스크린샷 찍기");
		helper.executeJavascript("window.scrollTo(0,0);");
		takeScreenShot(info);

		return true;

	}



	public void setPlan(String expectedPlan) throws CommonCrawlerException {

		String title = "플랜명";
		String actualPlan = "";

		try {
			WebElement $planDiv = driver.findElement(By.xpath("//div[@class='select-tab']"));
			WebElement $planButton = $planDiv.findElement(By.xpath(".//button[normalize-space()='" + expectedPlan + "']"));

			// 플랜 선택
			click($planButton);

			// 실제 클릭된 플랜 읽어오기
			$planButton = $planDiv.findElement(By.xpath(".//button[@class[contains(., 'selected')]]"));
			actualPlan = $planButton.getText().trim();

			// 비교
			super.printLogAndCompare(title, expectedPlan, actualPlan);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
			throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setGender(Object... obj) throws SetGenderException {

		String title = "성별";

		int gender = (int) obj[0];
		String expectedGender = (gender == MALE) ? "남" : "여";
		String actualGender = "";

		try {
			WebElement $genderDiv = driver.findElement(By.xpath("//div[@class[contains(., 'select-gender')]]"));
			WebElement $genderLabel = $genderDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedGender + "']"));

			// 성별 클릭
			clickByJavascriptExecutor($genderLabel);

			// 실제 클릭된 성별 값 읽어오기
			String script = "return $('input[name=rdoSex]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$genderLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
			actualGender = $genderLabel.getText().trim();

			// 성별 비교
			super.printLogAndCompare(title, expectedGender, actualGender);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
			throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {

		String title = "보험료 크롤링";

		CrawlingProduct info = (CrawlingProduct) obj[0];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
		ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

		try {
			// 보험료 크롤링 전에는 대기시간을 넉넉히 준다
			WaitUtil.waitFor(5);

			WebElement $premiumDiv = driver.findElement(By.id("calPremBox"));
			$premiumDiv = $premiumDiv.findElement(By.xpath(".//div[@class[contains(., 'total-price')]]"));
			String premium = $premiumDiv.getText();
			premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

			mainTreaty.monthlyPremium = premium;

			if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
				logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
				throw new PremiumCrawlerException(exceptionEnum.getMsg());
			} else {
				logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
			}

		} catch (Exception e) {
			throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}

	}

}



//package com.welgram.crawler.direct.fire.hnf;
//
//import com.welgram.common.MoneyUtil;
//import com.welgram.common.WaitUtil;
//import com.welgram.common.enums.ExceptionEnum;
//import com.welgram.common.except.NotFoundTreatyException;
//import com.welgram.common.except.TreatyMisMatchException;
//import com.welgram.common.except.crawler.CommonCrawlerException;
//import com.welgram.crawler.direct.fire.CrawlingHNF;
//import com.welgram.crawler.general.CrawlingOption;
//import com.welgram.crawler.general.CrawlingProduct;
//import com.welgram.crawler.general.CrawlingTreaty;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebElement;
//
//
//public class HNF_DRV_D002 extends CrawlingHNF {
//
//	public static void main(String[] args) {
//		executeCommand(new HNF_DRV_D002(), args);
//	}
//
//
//	@Override
//	protected boolean scrap(CrawlingProduct info) throws Exception {
//		crawlFromMobile(info);
//
//		return true;
//	}
//
//	@Override
//	protected void configCrawlingOption(CrawlingOption option) throws Exception {
//		option.setMobile(true);
//	}
//
//
//	private void crawlFromMobile(CrawlingProduct info) throws Exception{
//
//		waitMobileLoadingImg();
//
//		logger.info("보험료 계산하기 버튼 클릭");
//		element = driver.findElement(By.id("btnJoin"));
//		helper.waitElementToBeClickable(element).click();
//		waitMobileLoadingImg();
//
//
//		logger.info("보험 시작일 선택");
//		setMobileInsuranceStartDate();
//
//
//		logger.info("체크항목 모두 체크");
//		List<WebElement> checkBoxElements = 	driver.findElements(By.xpath("//div[@class='Check']//label"));
//		for(WebElement checkBox : checkBoxElements) {
//			helper.waitElementToBeClickable(checkBox).click();
//		}
//
//		logger.info("보험료 계산하기 버튼 클릭");
//		element = driver.findElement(By.id("btnStep01Next"));
//		helper.waitElementToBeClickable(element).click();
//		waitMobileLoadingImg();
//
//		logger.info("생년월일 설정");
//		setMobileFullBirth(info.fullBirth);
//
//		logger.info("성별 설정");
//		setMobileGender(info.gender);
//
//		logger.info("다음 버튼 클릭");
//		element = driver.findElement(By.id("btnStep010101Next"));
//		helper.waitElementToBeClickable(element).click();
//		waitMobileLoadingImg();
//
//		logger.info("플랜 설정");
//		setMobilePlanType(info.planSubName);
//
//		logger.info("특약 비교");
//		compareMobileTreaties(info.treatyList);
//
//		//주계약 보험료의 경우 애니메이션?이 있어 정확한 값을 크롤링하기위해 5초 대기
//		logger.info("주계약 보험료 설정");
//		WaitUtil.waitFor(5);
//		setMonthlyPremium(info.treatyList.get(0));
//
//		logger.info("스크린샷 찍기");
//		takeScreenShot(info);
//	}
//
//
//	private void setMobilePlanType(String planSubName) throws CommonCrawlerException {
//		String title = "플랜유형";
//
//		try {
//
//			//플랜 클릭
//			WebElement $button = driver.findElement(By.xpath("//button[text()='" + planSubName + "']"));
//			helper.waitElementToBeClickable($button).click();
//			waitMobileLoadingImg();
//
//			//실제 클릭된 플랜 읽어오기
//			$button = driver.findElement(By.xpath("//div[@class='select-tab']//button[@class[contains(., 'selected')]]"));
//			String selectedPlan = $button.getText();
//
//			//비교
//			printLogAndCompare(title, planSubName, selectedPlan);
//
//		} catch (Exception e) {
//			throw new CommonCrawlerException(ExceptionEnum.ERR_BY_SUB_PLAN);
//		}
//	}
//
//
//	private void setMobileGender(int gender) throws CommonCrawlerException {
//		String title = "성별";
//		String genderText = (gender == MALE) ? "남" : "여";
//
//		try {
//
//			//성별 클릭
//			WebElement $label = driver.findElement(By.xpath("//label[text()='" + genderText + "']"));
//			WebElement $li = $label.findElement(By.xpath("./parent::li"));
//			helper.waitElementToBeClickable($li).click();
//
//			//실제 클릭된 성별 읽어오기
//			String script = "return $('input[name=rdoSex]:checked').attr('id');";
//			String selectedGenderId = String.valueOf(helper.executeJavascript(script));
//			String selectedGenderText = driver.findElement(By.xpath("//label[@for='" + selectedGenderId + "']")).getText();
//
//			//비교
//			printLogAndCompare(title, genderText, selectedGenderText);
//
//		} catch (Exception e) {
//			new CommonCrawlerException(ExceptionEnum.ERR_BY_GENDER, e.getCause());
//		}
//
//
//
//	}
//
//	private void setMobileFullBirth(String fullBirth) throws CommonCrawlerException {
//		String title = "생년월일";
//
//		try {
//
//			//생년월일 입력
//			WebElement $input = driver.findElement(By.id("txtBirth"));
//			String targetFullBirth = helper.setTextToInputBox($input, fullBirth);
//			WaitUtil.waitFor(2);
//
//			//비교
//			printLogAndCompare(title, fullBirth, targetFullBirth);
//
//		} catch (Exception e) {
//			throw new CommonCrawlerException(ExceptionEnum.ERR_BY_BIRTH);
//		}
//	}
//
//	private void setMonthlyPremium(CrawlingTreaty mainTreaty) throws Exception{
//		String monthlyPremium = "";
//		List<WebElement> elements = driver.findElements(By.xpath("//div[@class='odometer-inside']//span[@class='odometer-value']"));
//		for(WebElement element : elements) {
//			monthlyPremium += element.getText().trim();
//		}
//
//		mainTreaty.monthlyPremium = monthlyPremium;
//
//		if("".equals(mainTreaty.monthlyPremium)) {
//			throw new Exception("주계약 가입금액이 비어있습니다");
//		}
//
//		logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
//	}
//
//	//보험 시작일 설정
//	private void setMobileInsuranceStartDate() throws Exception{
//		logger.info("보험 시작일 버튼 클릭");
//		helper.waitElementToBeClickable(By.xpath("//span[text()='보험 시작일']/parent::div//button")).click();
//
//
//		//날짜 선택하는 첫번째 휠
//		WebElement firstWheel = driver.findElement(By.cssSelector("#picker > div.mbsc-datepicker-tab-wrapper.mbsc-ios > div > div > div > div.mbsc-scroller-wheel-group.mbsc-ios > div.mbsc-scroller-wheel-wrapper.mbsc-scroller-wheel-wrapper-0.mbsc-datetime-date-wheel.mbsc-ios.mbsc-ltr > div > div.mbsc-scroller-wheel-cont.mbsc-scroller-wheel-cont-inline.mbsc-scroller-wheel-single.mbsc-ios > div"));
//
//		//휠 안의 요소의 높이를 잰다.
//		WebElement element = firstWheel.findElement(By.xpath(".//div[@class[contains(., 'mbsc-selected')]]"));
//		String elementStyle = element.getAttribute("style");
//		int start = elementStyle.indexOf("height");
//		int end = elementStyle.indexOf(";", start);
//		String elementHeight = elementStyle.substring(start, end + 1);
//		elementHeight = elementHeight.replaceAll("[^0-9]", "");
//
//		//7일 후의 날짜를 지정하기 위해서 elementHeight * 7 을 해야한다.
//		elementHeight = String.valueOf(Integer.parseInt(elementHeight) * 7);
//
//
//		//첫번째 휠 안에서 원하는 날짜를 클릭하기 위해 휠의 style 속성을 조정해야 함.
//		String wheelStyle = firstWheel.getAttribute("style");
//		start = wheelStyle.indexOf("translate3d");
//		end = wheelStyle.indexOf(";", start);
//		String wheelPoint = wheelStyle.substring(start, end + 1);
//		String convertPoint = "translate3d(0px, -" + elementHeight + "px, 0px);";
//
//		wheelStyle = wheelStyle.replace(wheelPoint, convertPoint);
//
//		((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style', arguments[1]);", firstWheel, wheelStyle);
//		//휠의 스타일을 변경하면 원하는 날짜로 휠을 스크롤하는데에 시간이 걸리므로 2초대기
//		WaitUtil.waitFor(2);
//
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DATE, 7);
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		String startDate = sdf.format(cal.getTime());
//
//		String year = startDate.substring(2, 4);
//		String month = startDate.substring(4, 6);
//		String date = startDate.substring(6);
//		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
//		String day = "";
//
//		switch (dayOfWeek) {
//			case 1:
//				day = "일";
//				break;
//			case 2:
//				day = "월";
//				break;
//			case 3:
//				day = "화";
//				break;
//			case 4:
//				day = "수";
//				break;
//			case 5:
//				day = "목";
//				break;
//			case 6:
//				day = "금";
//				break;
//			case 7:
//				day = "토";
//				break;
//		}
//
//		startDate = year + "/" + month + "/" + date + "(" + day + ")";
//
//		//휠을 조종해 원하는 보험시작일 텍스트가 보이게 맞춰놓은 상태에서 클릭
//		element = firstWheel.findElement(By.xpath(".//div[text()='" + startDate + "']"));
//		helper.waitElementToBeClickable(element).click();
//
//		String selectedStartDate = firstWheel.findElement(By.xpath(".//div[@class[contains(., 'mbsc-selected')]]")).getText();
//		logger.info("============================================================================");
//		logger.info("가입설계 보험시작일 : {}", startDate);
//		logger.info("홈페이지 선택된 보험시작일 : {}", selectedStartDate);
//		logger.info("============================================================================");
//
//		if(!selectedStartDate.equals(startDate)) {
//			throw new Exception("보험시작일 불일치");
//		} else {
//			logger.info("result :: 가입설계 보험시작일({}) == 홈페이지 선택된 보험시작일({})", startDate, selectedStartDate);
//			logger.info("============================================================================");
//		}
//
//
//	}
//
//	private void waitMobileLoadingImg() throws Exception {
//		helper.waitForCSSElement("#loading");
////		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading"))); 	// display: none; 으로 바뀜
//	}
//
//
//	//특약 정보 비교 메서드
//	protected void compareMobileTreaties(List<CrawlingTreaty> treatyList) throws Exception {
//
//		//홈페이지의 해당 플랜의 특약 정보를 담는다(key : 특약명, value : 특약금액)
//		HashMap<String, String> homepageTreatyMap = new HashMap<>();
//		List<WebElement> liList = helper.waitVisibilityOfAllElementsLocatedBy(By.xpath("//ul[@id='coverList']//li"));
//
//		//1. 홈페이지의 특약목록을 돈다
//		for(WebElement li : liList) {
//			String homepageTreatyName = li.findElement(By.xpath("./p")).getText().trim();
//			String homepageTreatyMoney = li.findElement(By.xpath("./div/span")).getText().trim();
//
//			//가입하는 특약에 대해서만
//			if(!"미가입".equals(homepageTreatyMoney)) {
//				homepageTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney(homepageTreatyMoney));
//				homepageTreatyMap.put(homepageTreatyName, homepageTreatyMoney);
//			}
//		}
//
//		//2. 홈페이지의 가입특약 수와 가입설계 가입특약 수를 비교한다.
//		if(homepageTreatyMap.size() == treatyList.size()) {
//			//Good Case :: 홈페이지와 가입설계 특약 수가 일치할 때. 이 경우는 특약명이 일치하는지와와 특약 가입금액이 일하는지 비교해줘야 함.
//
//			for(CrawlingTreaty myTreaty : treatyList) {
//				String myTreatyName = myTreaty.treatyName;
//				String myTreatyMoney = String.valueOf(myTreaty.assureMoney);
//
//				//특약명이 불일치할 경우
//				if(!homepageTreatyMap.containsKey(myTreatyName)) {
//					throw new NotFoundTreatyException("특약명(" + myTreatyName + ")은 존재하지 않는 특약입니다.");
//				}
//
//				if(homepageTreatyMap.get(myTreatyName).equals(myTreatyMoney)) {
//					logger.info("특약명 : {} | 가입금액 : {}원", myTreatyName, myTreatyMoney);
//				} else {
//					//특약명은 일치하지만, 금액이 다른경우
//					logger.info("============================================================================");
//					logger.info("특약명 : {}", myTreatyName);
//					logger.info("홈페이지 금액 : {}원", homepageTreatyMap.get(myTreatyName));
//					logger.info("가입설계 금액 : {}원", myTreatyMoney);
//					logger.info("============================================================================");
//
//					throw new TreatyMisMatchException("특약명(" + myTreatyName + ")의 가입금액이 일치하지 않습니다.");
//				}
//			}
//			logger.info("============================================================================");
//			logger.info("result :: 특약이 모두 일치합니다 ^0^");
//			logger.info("============================================================================");
//		} else if(homepageTreatyMap.size() > treatyList.size()) {
//			//Wrong Case :: 홈페이지의 특약 개수가 더 많을 때. 이 경우 가입설계에 어떤 특약을 추가해야 하는지 알려야 함.
//
//			List<String> myTreatyNameList = new ArrayList<>();
//			for(CrawlingTreaty myTreaty :treatyList) {
//				myTreatyNameList.add(myTreaty.treatyName);
//			}
//
//			List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
//			targetTreatyList.removeAll(myTreatyNameList);
//
//			logger.info("============================================================================");
//			logger.info("가입설계에 추가해야할 특약 리스트 :: {}", targetTreatyList);
//			logger.info("============================================================================");
//
//			throw new TreatyMisMatchException(targetTreatyList + "의 특약들을 추가해야 합니다.");
//
//		} else {
//			//Wrong Case : 가입설계의 특약 개수가 더 많을 때. 이 경우 가입설계에서 어떤 특약이 제거돼야 한다고 알려야 함.
//
//			List<String> myTreatyNameList = new ArrayList<>();
//			for(CrawlingTreaty myTreaty :treatyList) {
//				myTreatyNameList.add(myTreaty.treatyName);
//			}
//
//			List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
//			myTreatyNameList.removeAll(targetTreatyList);
//
//			logger.info("============================================================================");
//			logger.info("가입설계에서 제거돼야할 특약 리스트 :: {}", myTreatyNameList);
//			logger.info("============================================================================");
//
//			throw new TreatyMisMatchException(myTreatyNameList + "의 특약들을 제거해야 합니다.");
//
//		}
//	}
//}
