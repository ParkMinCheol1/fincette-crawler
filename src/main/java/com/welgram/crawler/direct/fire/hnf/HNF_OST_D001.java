package com.welgram.crawler.direct.fire.hnf;

import com.welgram.common.DateUtil;
import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Date;


public class HNF_OST_D001 extends CrawlingHNFMobile {

	public static void main(String[] args) {
		executeCommand(new HNF_OST_D001(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $element = null;

		logger.info("가입하기 버튼 클릭");
		$element = driver.findElement(By.xpath("//span[normalize-space()='가입하기']/parent::button"));
		click($element);

		logger.info("일반 여행자 플랜");
		driver.findElement(By.xpath("//*[@id=\"btnJoinOverseas\"]")).click();

		logger.info("여행 목적 설정");
		setTravelGoal("여행 / 관광");

		logger.info("여행 목적 체크항목 체크");
		$element = driver.findElement(By.xpath("//label[@for='chkTravel1']"));
		clickByJavascriptExecutor($element);

		logger.info("다음 버튼 클릭");
		$element = driver.findElement(By.xpath("//span[normalize-space()='다음']/parent::button[not(@class[contains(., 'disabled')])]"));
		click($element);

		logger.info("여행시작일 설정");
		String departureDate = DateUtil.dateAfter7Days(new Date());
		setTravelDepartureDate(departureDate);

		logger.info("여행도착일 설정");
		String arrivalDate = DateUtil.dateAfter13Days(new Date());
		setTravelArrivalDate(arrivalDate);

		logger.info("다음 버튼 클릭");
		$element = driver.findElement(By.id("btnNext"));
		click($element);
		click($element);

		logger.info("생년월일 입력");
		setBirthday(info.getFullBirth());

		logger.info("성별 입력");
		setGender(info.getGender());

		logger.info("다음 버튼 클릭");
		$element = driver.findElement(By.id("btnFlow3Next"));
		click($element);

		logger.info("실손의료보험 중복가입 유의사항 체크");
		$element = driver.findElement(By.xpath("//label[@for='chkDuplicateConfirm']"));
		click($element);

		logger.info("확인 버튼 클릭");
		$element = driver.findElement(By.id("btnDuplicateConfirm"));
		click($element);

		logger.info("플랜 선택");
		setPlan(info.planSubName);

		logger.info("특약 설정");
		setTreatiesToggleType(info.getTreatyList());

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		return true;

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

			WebElement $premiumDiv = driver.findElement(By.xpath("//div[@class[contains(., 'total-price')]]"));
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



	@Override
	public void setBirthday(Object... obj) throws SetBirthdayException {

		String title = "생년월일";
		String expectedBirth = (String) obj[0];
		String actualBirth = "";

		try {
			WebElement $birthInput = driver.findElement(By.id("insrd_sBirth"));

			// 생년월일 설정
			actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

			// 비교
			super.printLogAndCompare(title, expectedBirth, actualBirth);
		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
			throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setGender(Object... obj) throws SetGenderException {

		String title = "성별";

		int gender = (int) obj[0];
		String expectedGender = (gender == MALE) ? "남" : "여";
		String actualGender = "";

		try {
			WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='select-tab']"));
			WebElement $genderLabel = $genderDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedGender + "']"));

			// 성별 클릭
			clickByJavascriptExecutor($genderLabel);

			// 실제 클릭된 성별 값 읽어오기
			String script = "return $('input[name=rdoMyGender]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$genderLabel = $genderDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
			actualGender = $genderLabel.getText().trim();

			// 성별 비교
			super.printLogAndCompare(title, expectedGender, actualGender);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
			throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	public void setPlan(String expectedPlan) throws CommonCrawlerException {

		String title = "플랜명";
		String actualPlan = "";

		try {
			WebElement $planDiv = driver.findElement(By.xpath("//div[@class='select-tab type-col']"));
			WebElement $planUl = $planDiv.findElement(By.tagName("ul"));
			WebElement $planButton = $planUl.findElement(By.xpath(".//button[normalize-space()='" + expectedPlan + "']"));
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

}