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
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public abstract class CrawlingCRFMobile extends SeleniumCrawler implements ScrapableNew {

	@Override
	public void setBirthday(Object... obj) throws SetBirthdayException {

		try{
			By by = (By) obj[0];
			String birthday = (String) obj[1];

			helper.sendKeys4_check(by, birthday);
			WaitUtil.waitFor(1);

			// 검증
			checkValue("생년월일", birthday, by);

		}catch(Exception e){
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
			WaitUtil.waitFor(2);

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
	public void setJob(Object... obj) throws SetJobException {

		try {
			String job = (String) obj[0];
			WebElement $input = null;
			WebElement $button = null;

			logger.info("{} 입력", job);
			$input = driver.findElement(By.cssSelector("#searchJob"));
			$input.click();
			WaitUtil.waitFor(2);

			$input.sendKeys(job, Keys.ENTER);
			WaitUtil.waitFor(4);

			logger.info("{} 선택", job);
			$button = driver.findElement(By.xpath("//*[@id='baseMain']//button[@class='item']"));
			$button.click();
			WaitUtil.waitFor(2);

			// 검증
			checkValue("직업", job, By.cssSelector("#searchJob"));

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
			throw new SetJobException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}


	}

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
			By by = (By) obj[1];
			String premium = driver.findElement(by).getText().replaceAll("[^0-9]", "");

			info.getTreatyList().get(0).monthlyPremium = premium;
			logger.info("월 보험료: " + info.getTreatyList().get(0).monthlyPremium);

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

		try{
			By by = (By) obj[0];
			String name = (String) obj[1];

			helper.sendKeys4_check(by, name);
			WaitUtil.waitFor(1);

		} catch(Exception e){
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_USER_NAME;
			throw new SetUserNameException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	@Override
	public void setDueDate(Object... obj) throws SetDueDateException {}

	@Override
	public void setTravelDate(Object... obj) throws SetTravelPeriodException {}

	@Override
	public void setProductType(Object... obj) throws SetProductTypeException {}

	@Override
	public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}

	@Override
	public void setVehicle(Object... obj) throws SetVehicleException {

		try {
			By by = (By) obj[0];
			String vehiclePurpose = (String) obj[1];

			driver.findElement(by).click();
			WaitUtil.waitFor(2);

//			// 검증
//			checkValue("차량용도", vehiclePurpose, by);

		} catch(Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
			throw new SetVehicleException(exceptionEnum.getMsg() + "\n" + e.getMessage());
		}
	}

	protected void setTreaties(List<CrawlingTreaty> welgramTreatyList, By by) throws SetTreatyException, Exception {

		try{
			int scrollTop = 0;
			String homepageTreatyname = ""; // 홈페이지의 특약명
			String homepageTreatyAmt = ""; // 홈페이지의 특약금액
			String welgramTreatyName = "";

			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
			List<WebElement> $homepageTreatyList = driver.findElements(by);

			for(WebElement homepageTreaty : $homepageTreatyList){
				boolean exist = false;
				homepageTreatyname = homepageTreaty.findElement(By.xpath("./div[1]")).getText();
				homepageTreatyAmt = homepageTreaty.findElement(By.xpath("./div[2]")).getText();

				// 스크롤 이동
				element = driver.findElement(By.xpath("//*[text()='"+homepageTreatyname+"']"));
				((JavascriptExecutor) driver). executeScript("arguments[0].scrollIntoView(true);", element);
				WaitUtil.waitFor(1);

				// 가설 특약과 비교
				for(int j = 0; j < welgramTreatyList.size(); j++){
					welgramTreatyName = welgramTreatyList.get(j).treatyName;

					if(homepageTreatyname.contains(welgramTreatyName)){ // 특약명 일치
						// 가입금액 변환
						homepageTreatyAmt = String.valueOf(MoneyUtil.toDigitMoney(homepageTreatyAmt));

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
					String unSubscribed = homepageTreaty.findElement(By.xpath("./div[1]")).getText().trim();

					if(!(unSubscribed.equals("미가입"))){
						// btn-arrow
						homepageTreaty.findElement(By.xpath("./div[2]/div[2]")).click();
						WaitUtil.waitFor(2);

						driver.findElement(By.xpath("//div[text()='미가입']")).click();
						WaitUtil.waitFor(2);

						//레이어 닫기
						driver.findElement(By.xpath("//div[@class='popup-layout-header']//button")).click();
						WaitUtil.waitFor(2);
					}
				}

				scrollTop += 70;
				((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + scrollTop + ");");
			}
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
	protected void btnClick(By byElement, int sec) throws Exception {
		//todo 로딩바 활성화 여부도 선택할지 고려할 것
		driver.findElement(byElement).click();
		WaitUtil.waitFor(sec);
	}

	// 텍스트 타입 체크
	protected String[] checkTextType(String textType) throws CommonCrawlerException {

		String[] arrTextType = textType.split("#");

		for(int i = 0; i < arrTextType.length; i++) {

			arrTextType[i] = arrTextType[i].trim();
			logger.info("textType [" + i +"] :: " + arrTextType[i]);
		}

		return arrTextType;
	}

	// 다음버튼 클릭
	protected void clickNextBtn(String elementType) throws CommonCrawlerException {

		try {
			By by = null;

			if(elementType.equals("span")){
				by = By.xpath("//span[text()='다음']");
			} else {
				by = By.xpath("//button[text()='다음']");
			}

			driver.findElement(by).click();
			WaitUtil.waitFor(3);

		} catch (Exception e) {
			throw new CommonCrawlerException("실패 : 다음 버튼 클릭");
		}
	}

	// 자동차보험 가입 모달창 끄기
	protected void closeModal() throws CommonCrawlerException{

		try {
			By modal = null;

			modal = By.xpath("/html/body/div[3]/div/div[1]/div[2]//*[name()='svg']");
			helper.waitElementToBeClickable(modal).click();
			WaitUtil.waitFor(3);

			// 작은 모달 재등장
			modal = By.xpath("/html/body/div[3]/div/div[1]//*[name()='svg']");
			helper.waitElementToBeClickable(modal).click();
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

			printLogAndCompare(title, expectedValue, selectedValue);

		} catch (Exception e){
			throw new CommonCrawlerException("선택값 검증 중 오류가 발생했습니다.\n" + e.getMessage());
		}
	}



	// 자동차 번호 입력 팝업 체크
	protected void checkCarNumberPopup() throws Exception {

		if(helper.existElement(By.cssSelector("#root-modal > div > div.sc-csCMJt.jWWupb"))){
			driver.findElement(By.cssSelector("#root-modal > div > div.sc-csCMJt.jWWupb > div > div.sc-ieZDjg.tXNPN > span > svg")).click();
			WaitUtil.waitFor(1);
		}
	}

}
