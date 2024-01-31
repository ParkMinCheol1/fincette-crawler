package com.welgram.crawler.direct.fire.crf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetPrevalenceTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.scraper.ScrapableNew;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public abstract class CrawlingCRFDirect extends SeleniumCrawler implements ScrapableNew {

	@Override
	public void setBirthday(Object... obj) throws SetBirthdayException {

		try{
			By by = (By) obj[0];
			String fullBirth = (String) obj[1];

			helper.sendKeys4_check(by, fullBirth);
			WaitUtil.waitFor(1);

			// 검증
			checkValue("생년월일", fullBirth, by);

		} catch (Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
			throw new SetBirthdayException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}


	@Override
	public void setGender(Object... obj) throws SetGenderException {

		try{
			By by = (By) obj[0];
			String genderText = (String) obj[1];

			driver.findElement(by).click();
			WaitUtil.waitFor(1);

			// 검증
			checkValue("성별", genderText, by);

		} catch(Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
			throw new SetGenderException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	@Override
	public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {}

	@Override
	public void setJob(Object... obj) throws SetJobException {}

	@Override
	public void setInsTerm(Object... obj) throws SetInsTermException {}

	@Override
	public void setNapTerm(Object... obj) throws SetNapTermException {}

	@Override
	public void setNapCycle(Object... obj) throws SetNapCycleException {}

	@Override
	public void setRenewType(Object... obj) throws SetRenewTypeException {}

	@Override
	public void setAssureMoney(Object... obj) throws SetAssureMoneyException {}

	@Override
	public void setRefundType(Object... obj) throws SetRefundTypeException {}

	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {

		try{
			CrawlingProduct info = (CrawlingProduct) obj[0];
			By $premiumEl = (By) obj[1];
			String premium = driver.findElement($premiumEl).getText().replaceAll("[^0-9]", "");

			logger.debug("월 보험료: " + premium);
			info.getTreatyList().get(0).monthlyPremium = premium;

		} catch (Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
			throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	@Override
	public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {}

	@Override
	public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {}

	@Override
	public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {}

	@Override
	public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {}

	@Override
	public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {}

	@Override
	public void setUserName(Object... obj) throws SetUserNameException {

		By by = (By) obj[0];
		String name = (String) obj[1];

		try {
			helper.sendKeys4_check(by, name);
			WaitUtil.waitFor(2);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_USER_NAME;
			throw new SetUserNameException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	@Override
	public void setDueDate(Object... obj) throws SetDueDateException {}

	@Override
	public void setTravelDate(Object... obj) throws SetTravelPeriodException {

		try{
			logger.info("여행시작일 선택");
			driver.findElement(By.id("departureDate_dd1")).click();
			WaitUtil.waitFor(2);

			// 오늘 정보
			LocalDate today = LocalDate.now();
			LocalDate departureDateInfo = today.plusDays(7);    // 7일 뒤 출발
			LocalDate arrivalDateInfo = today.plusDays(13);      // 13일 뒤 도착

			// 출발, 도착 날짜 정보 String, Date | 년, 월, 일
			String todayMonth = String.valueOf(today.getMonthValue());
			String departMonth = String.valueOf(departureDateInfo.getMonthValue());
			String departDate = String.valueOf(departureDateInfo.getDayOfMonth());
			String arrivalMonth = String.valueOf(arrivalDateInfo.getMonthValue());
			String arrivalDate = String.valueOf(arrivalDateInfo.getDayOfMonth());

			if(departDate.length() == 1) {
				departDate = "0" + departDate;
			}
			if(arrivalDate.length() == 1 ) {
				arrivalDate = "0" + arrivalDate;
			}

			logger.info("======================================");
			logger.info("departMonth  :: {}", departMonth);
			logger.info("departDate   :: {}", departDate);
			logger.info("arrivalMonth :: {}", arrivalMonth);
			logger.info("arrivalDate  :: {}", arrivalDate);
			logger.info("======================================");

			logger.info("출발일 선택");
			setTravelDepatureDate(today, departureDateInfo, departDate);

			logger.info("출발시간 선택");
			setTravelTime("departureTime","departureTime_1");

			logger.info("도착일 선택");
			setTravelArrivalDate(departMonth, arrivalMonth, arrivalDate);

			logger.info("도착시간 선택");
			setTravelTime("arrivalTime","arrivalTime_24");

		} catch(Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
			throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	// 여행출발날짜 선택
	protected void setTravelDepatureDate(LocalDate today, LocalDate departureDateInfo, String departDate) throws SetTravelPeriodException{

		List<WebElement> $trList = null;

		try{
			if(today.getMonthValue() != departureDateInfo.getMonthValue()) {

				logger.info("다음달 선택하기");
				driver.findElement(By.cssSelector("#calWrap_departureDate_dd1 > div.datepicker-head > div.datepicker-head-btn > button.btn-arrow.ui-datepicker-next")).click();
				WaitUtil.waitFor(2);
			}

			$trList = driver.findElements(By.cssSelector("#calWrap_departureDate_dd1 > div.datepicker-core > table > tbody > tr"));
			selectDay(departDate, $trList);

		} catch (Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
			throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	// 여행도착날짜 선택
	protected void setTravelArrivalDate(String departMonth, String arrivalMonth, String arrivalDate) throws SetTravelPeriodException{

		WebElement $button = null;
		List<WebElement> $trList = null;

		try{
			driver.findElement(By.id("arrivalDate_dd1")).click();
			WaitUtil.waitFor(2);

			// 도착월이 자동으로 다음달로 세팅되는 경우 존재
			if(!arrivalMonth.equals(departMonth)) {
				// 현재 선택된 월을 가져와서 비교
				String selectedMonth = driver.findElement(By.cssSelector("#calWrap_arrivalDate_dd1 > div.datepicker-head > div.datepicker-head-date > span.month"))
						.getText().replaceAll("[^0-9]","");

				if(arrivalMonth.length() == 1 ) {
					arrivalMonth = "0" + arrivalMonth;
				}

				if(!selectedMonth.equals(arrivalMonth)){
					driver.findElement(By.cssSelector("#calWrap_arrivalDate_dd1 > div.datepicker-head > div.datepicker-head-btn > button.btn-arrow.ui-datepicker-next")).click();
					WaitUtil.waitFor(2);
				}
			}

			$trList = driver.findElements(By.cssSelector("#calWrap_arrivalDate_dd1  > div.datepicker-core > table > tbody > tr"));
			selectDay(arrivalDate, $trList);

		} catch (Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
			throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	/*
	 * 여행 출발/도착시간 클릭 메서드
	 * @param1 type : 시간선택 드롭박스 id
	 * @param2 time : 시간 엘리먼트 id
	 */
	protected void setTravelTime(String type, String time) throws SetTravelPeriodException{

		WebElement $button = null;
		String ids[] = {type, time};

		try{
			for(String id : ids){
				driver.findElement(By.xpath("//button[@id='" +  id + "']")).click();
				WaitUtil.waitFor(2);
			}
		} catch (Exception e){
			throw new SetTravelPeriodException("여행시간 선택 오류 ::" + "\n" + e.getMessage());
		}
	}


	@Override
	public void setProductType(Object... obj) throws SetProductTypeException {

		try{
			By $byElement = (By) obj[0];

			driver.findElement($byElement).click();
			WaitUtil.waitFor(2);

		} catch(Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
			throw new SetProductTypeException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	@Override
	public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}

	@Override
	public void setVehicle(Object... obj) throws SetVehicleException {}

	public void setTreaties(List<CrawlingTreaty> welgramTreatyList, By $element) throws SetTreatyException {

		try{
			int scrollTop = 0;
			String homepageTreatyname = ""; // 홈페이지의 특약명
			String homepageTreatyAmt = ""; // 홈페이지의 특약금액
			String welgramTreatyName = "";
			WebElement $button = null;

			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
			List<WebElement> $homepageTreatyList = driver.findElements($element);

			for(WebElement homepageTreaty : $homepageTreatyList){
				boolean exist = false;
				homepageTreatyname = homepageTreaty.findElement(By.cssSelector("th")).getText();
				homepageTreatyAmt = homepageTreaty.findElement(By.xpath("td")).getText();

				// 스크롤 이동
				element = driver.findElement(By.xpath("//*[normalize-space()='" + homepageTreatyname + "']"));
				((JavascriptExecutor) driver). executeScript("arguments[0].scrollIntoView(true);", element);
				WaitUtil.waitFor(1);

				// 가설 특약과 비교
				for(int j = 0; j < welgramTreatyList.size(); j++){
					welgramTreatyName = welgramTreatyList.get(j).getTreatyName();

					if(homepageTreatyname.contains(welgramTreatyName)){ // 특약명 일치
						// 가입금액 변환
						homepageTreatyAmt = String.valueOf(MoneyUtil.toDigitMoney(homepageTreatyAmt.replace("가입", "50000")));

						logger.info("===========================================================");
						logger.info("특약명 :: {}", homepageTreatyname);
						logger.info("가입금액 :: {}", homepageTreatyAmt);
						logger.info("===========================================================");

						CrawlingTreaty targetTreaty = new CrawlingTreaty();
						targetTreaty.setTreatyName(homepageTreatyname);
						targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyAmt));

						targetTreatyList.add(targetTreaty);

						exist = true;
						break;
					}
				}

				if(!exist){ // 미가입 처리
					String unSubscribed = homepageTreaty.findElement(By.xpath("td")).getText();

					if(!(unSubscribed.equals("미가입"))){
						// btn-arrow
						homepageTreaty.findElement(By.cssSelector("td > button")).click();
						WaitUtil.waitFor(2);

						driver.findElement(By.xpath("//*[@id='tooltipSideBar']//span[text()='미가입']")).click();
						WaitUtil.waitFor(2);

						// 미가입 팝업
						logger.info("특약 미가입 팝업");
						checkUnsubPopup();

						//레이어 닫기
						driver.findElement(By.xpath("//*[@id='tooltipSideBar']//button[@class='btn-close tooltip-side-close']")).click();
						WaitUtil.waitFor(2);
					}
				}

				scrollTop += 70;
				((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + scrollTop + ");");
			}

			WaitUtil.waitFor(2);
			logger.info("===========================================================");

			logger.info("특약 비교 및 확인");
			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

			if (result) {
				logger.info("특약 정보가 모두 일치합니다");
			} else {
				logger.error("특약 정보 불일치");
				throw new Exception();
			}

		} catch(Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
			throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	/*
	 * 버튼 클릭 메서드
	 * @param1 byElement 클릭하고자 하는 요소
	 * @param2 sec 대기 시간
	 */
	protected void btnClick(By by, int sec) throws Exception {
		// todo 로딩바 활성화 여부도 선택할지 고려할 것
		WebElement $element = driver.findElement(by);

		$element.click();
		WaitUtil.waitFor(sec);
	}

	/*
	 * 버튼 클릭 메서드
	 * @param1 byElement 클릭하고자 하는 요소
	 * @param2 sec 대기 시간
	 */
	protected void btnClick(WebElement $element, int sec) throws Exception {
		// todo 로딩바 활성화 여부도 선택할지 고려할 것
		$element.click();
		WaitUtil.waitFor(sec);
	}

//	// next 버튼 클릭
//	protected void btnNext() throws CommonCrawlerException {
//
//		try {
//			driver.findElement(By.xpath("//button[@id='btn-basic-start']")).click();
//			WaitUtil.waitFor(2);
//
//		} catch (Exception e) {
//			throw new CommonCrawlerException("실패 : next 버튼 클릭");
//		}
//	}

	// 달력에서 날짜 클릭하기
	protected void selectDay(String day, List<WebElement> $trList) throws CommonCrawlerException {

		try{
			boolean isClicked = false;

			for (WebElement $tr : $trList) {
				List<WebElement> $buttons = $tr.findElements(By.xpath("//td[not(contains(@class, 'empty'))]/button"));

				for (WebElement $button : $buttons) {
					String buttonTxt = $button.getText();

					if (buttonTxt.equals(day)) {
						logger.info("BUTTON TXT :: {}", buttonTxt);
						logger.info("DAY TXT :: {}", day);

						((JavascriptExecutor) driver).executeScript("arguments[0].click();", $button);
						WaitUtil.waitFor(2);
						isClicked = true;
						break;
					}
				}
				if (isClicked == true) {
					break;
				}
			}
		} catch (Exception e){
			throw new CommonCrawlerException("날짜 선택 중 에러 발생" + "\n" + e.getMessage());
		}
	}

	// 특약 미가입 팝업
	protected void checkUnsubPopup() throws CommonCrawlerException {

		try {
			WebElement $div = null;
			$div = driver.findElement(By.cssSelector(".ui-modal-wrap"));

			if ($div.isDisplayed()) {
				logger.debug("미가입 알럿표시 확인!!!");
				$div.findElement(By.xpath("//button[contains(., '확인')]")).click();
				WaitUtil.waitFor(2);
			}
		} catch (Exception e) {
			logger.info("알럿표시 없음!!!");
		}
	}

	// 팝업창 확인
	protected void checkPopup(By by) throws CommonCrawlerException {

		try {
			helper.waitElementToBeClickable(by).click();
			WaitUtil.waitFor(2);

		} catch (Exception e) {
			logger.info("팝업창이 없어요");
		}
	}

	// 자동차보험 가입 모달창 끄기
	protected void closeModal() throws CommonCrawlerException {

		try {
			helper.waitElementToBeClickable(By.xpath("/html/body/div[3]/div/div[1]/div[2]//*[name()='svg']")).click();
			WaitUtil.waitFor(3);

			// 작은 모달 재등장
			helper.waitElementToBeClickable(By.xpath("/html/body/div[3]/div/div[1]//*[name()='svg']")).click();
			WaitUtil.waitFor(2);

			if (helper.isAlertShowed()) {
				driver.switchTo().alert().accept();
				logger.info("알럿창 끔");
				WaitUtil.waitFor(2);
			}

		} catch (NoSuchElementException nsee) {
			logger.info("모달이 안뜸");

		} catch (Exception e) {
			throw new CommonCrawlerException("모달창 오류입니다." + "\n" + e.getMessage());
		}
	}

	// 상품유형 선택
	protected void setPlanType(By $byElement) throws CommonCrawlerException {

		try{
			driver.findElement($byElement).click();
			WaitUtil.waitFor(2);

		} catch(Exception e) {
			throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_PLAN_NAME  + "\n" + e.getMessage());
		}
	}

	/**
	 * 선택값 검증 메서드
	 *
	 * @param   title           선택항목
	 * @param   expectedValue   선택하려는 값
	 * @param   selectedBy      실제 선택된 엘리먼트
	 */
	public void checkValue(String title, String expectedValue, By selectedBy) throws CommonCrawlerException {

		try{
			WebElement selectedElement = driver.findElement(selectedBy);
			// 실제 입력된 값
			String selectedValue = "";
			String script = "return $(arguments[0]).find('option:selected').text();";

			if(selectedElement.getTagName().equals("select")){
				selectedValue = String.valueOf(helper.executeJavascript(script,selectedElement));
			} else{
				selectedValue = selectedElement.getText().trim();

				if(selectedValue.equals("")){
					script = "return $(arguments[0]).val();";
					selectedValue = String.valueOf(helper.executeJavascript(script, selectedElement));
				}

				// 생년월일
				if(title.equals("생년월일")){
					selectedValue = selectedValue.replace(".", "");
				}
			}

//            logger.info("selected value :: {}", selectedValue);
			printLogAndCompare(title, expectedValue, selectedValue);

		} catch (Exception e){
			throw new CommonCrawlerException("선택값 검증 중 오류가 발생했습니다.\n" + e.getMessage());
		}
	}
}