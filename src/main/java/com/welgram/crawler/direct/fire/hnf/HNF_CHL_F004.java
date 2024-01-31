package com.welgram.crawler.direct.fire.hnf;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetPrevalenceTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlScreenShot;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.util.DateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;



public class HNF_CHL_F004 extends CrawlingHNFNew {

	public static void main(String[] args) {
		executeCommand(new HNF_CHL_F004(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// (무)하나로 시작하는 건강보험 2종(해약환급금미지급형Ⅱ)

		// STEP 1. 사용자 정보 입력
		logger.info("생년월일 입력");
		setBirthday(info.fullBirth);

		logger.info("성별 입력");
		setGender(info.gender);

		// STEP 2. 가입조건 비교
		logger.info("가입유형 설정");
		setPrevalenceType("2종(해약환급금미지급형Ⅱ)");

		logger.info("보험 기간");
		setInsTerm(info.insTerm);

		logger.info("납입 기간");
		setNapTerm(info.napTerm);

		logger.info("납입 주기");
		setNapCycle("월납");

		logger.info("담보갱신주기");
		WebElement $selectInput = driver.findElement(By.xpath("//select[@id='selCovdPaymCyclCd']/option[@value='20']"));
		click($selectInput);
		// setRenewType(info.textType);

		logger.info("운전용도");
		setVehicle("자가용");

		logger.info("직업 설정");
		setJob("주부,학생 및 기타 비경제활동", "미취학아동", "미취학아동", "미취학아동");

		logger.info("조회버튼");
		WebElement $button = driver.findElement(By.id("btnTrtySrch"));
		click($button);

		// STEP 3. 특약 확인
		logger.info("특약정보세팅");
		setTreaties(info.getTreatyList());

		logger.info("가입금액");
		crawlPremium(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		return true;

	}



	// STEP 1. 사용자 정보 입력
	@Override
	public void setBirthday(Object... obj) throws SetBirthdayException {

		String title = "생년월일";
		String expectBirth = (String) obj[0];
		String actualBirth = "";

		try {
			WebElement $birth = driver.findElement(By.id("cal_birth"));
			actualBirth = helper.sendKeys4_check($birth, expectBirth);
			super.printLogAndCompare(title, expectBirth, actualBirth);

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
		String actualGenderText = "";

		try {
			WebElement $genderSelect = driver.findElement(By.id("selSex"));
			actualGenderText = helper.selectByText_check($genderSelect, expectedGender);
			super.printLogAndCompare(title, expectedGender, actualGenderText);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
			throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	// STEP 2. 가입조건 비교
	@Override
	public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

		String title = "가입유형";
		String expectedRenewType = (String) obj[0];
		String actualRenewType = "";

		try {
			WebElement $productTypeSelect = driver.findElement(By.id("selGnrzCd"));
			actualRenewType = helper.selectByText_check($productTypeSelect, expectedRenewType);
			super.printLogAndCompare(title, expectedRenewType, actualRenewType);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RENEW_TYPE;
			throw new SetPrevalenceTypeException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setInsTerm(Object... obj) throws SetInsTermException {

		String title = "보험기간";
		String expectedInsTerm = (String) obj[0];
		String actualInsTerm = "";

		try {
			WebElement $insTermSelecter = driver.findElement(By.id("selInsurTermCd"));
			actualInsTerm = helper.selectByText_check($insTermSelecter, expectedInsTerm);
			super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
			throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setNapTerm(Object... obj) throws SetNapTermException {

		String title = "납입기간";
		String expectedNapTerm = (String) obj[0];
		String actualNapTerm = "";

		try {
			WebElement $napTermSelect = driver.findElement(By.id("selPaymTermCd"));
			actualNapTerm = helper.selectByText_check($napTermSelect, expectedNapTerm).trim();
			super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
			throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setNapCycle(Object... obj) throws SetNapCycleException {

		String title = "납입주기";
		String expectedNapCycle = (String) obj[0];
		String actualNapCycle = "";

		try {
			WebElement $napCycleSelect = driver.findElement(By.id("selPaymCyclCd"));
			actualNapCycle = helper.selectByText_check($napCycleSelect, expectedNapCycle);
			super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
			throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setRenewType(Object... obj) throws SetRenewTypeException {

		String title = "담보갱신주기";
		String expectedRenewType = (String) obj[0];
		String[] textTypes = expectedRenewType.split("\\|");
		String actualRenewType = "";

		try {
			WebElement $renewTypeSelect = driver.findElement(By.id("selCovdPaymCyclCd"));

			for (String textType : textTypes) {
				try {
					textType = textType.trim();
					actualRenewType = helper.selectByText_check($renewTypeSelect, textType);
					expectedRenewType = textType;
					break;
				} catch (NoSuchElementException e) {
				}
			}
			super.printLogAndCompare(title, expectedRenewType, actualRenewType);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_RENEW_CYCLE;
			throw new SetRenewTypeException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setVehicle(Object... obj) throws SetVehicleException {

		String title = "운전용도";
		String expectedVehicle = (String) obj[0];
		String actualVehicle = "";

		try {
			WebElement $vehicle = driver.findElement(By.id("selDriverType1"));
			actualVehicle = helper.selectByText_check($vehicle, expectedVehicle);
			super.printLogAndCompare(title, expectedVehicle, actualVehicle);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
			throw new SetVehicleException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setJob(Object... obj) throws SetJobException {

		String expectedLargeCategory = (String) obj[0];
		String expectedMediumCategory = (String) obj[1];
		String expectedSmallCategory = (String) obj[2];
		String expectedJobName = (String) obj[3];

		String[] titles = {"대분류", "중분류", "소분류", "직업명"};
		String[] ids = {"selJob1", "selJob2", "selJob3", "selJob4"};
		String[] values = {expectedLargeCategory, expectedMediumCategory, expectedSmallCategory, expectedJobName};

		try {
			for (int i = 0; i < titles.length; i++) {
				String actualValue = "";

				logger.info("{} 선택", titles[i]);
				WebElement $select = driver.findElement(By.id(ids[i]));
				actualValue = helper.selectByText_check($select, values[i]);
				super.printLogAndCompare(titles[i], values[i], actualValue);
			}
		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
			throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	// STEP 3. 특약 확인
	public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

		try {
			String script = "return $('div#divInsResultBox tbody:visible')[0]";
			WebElement $treatyTbody = (WebElement) helper.executeJavascript(script);

			logger.info("가입설계 특약을 바탕으로 원수사에 세팅하기");
			for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
				String treatyName = welgramTreaty.treatyName;

				WebElement $treatyNameId = driver.findElement(By.xpath(".//td[normalize-space()='" + treatyName + "']"));
				WebElement $treatyTr = $treatyNameId.findElement(By.xpath("./ancestor::tr[1]"));

				setTreatyInfoFromTr($treatyTr, welgramTreaty);
			}

			logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
			List<WebElement> $treatyTrList = $treatyTbody.findElements(By.tagName("tr"));
			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
			// List<WebElement> $treatyList = $treatyTbody.findElements(By.cssSelector("#divInsResultBox > div > table.base_tb.sel_tb.fl.mt16 > tbody > td.tal"));

			for (WebElement $treatyTr : $treatyTrList) {
				// tr로부터 특약정보 읽어오기
				CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

				if (targetTreaty != null) {
					targetTreatyList.add(targetTreaty);
				}
			}
			logger.info("원수사 특약 정보와 가입설계 특약정보 비교");
			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());

			if (result) {
				logger.info("특약 정보 모두 일치");
			} else {
				logger.info("특약 정보 불일치");
				throw new Exception();
			}
		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
			throw new SetTreatyException(exceptionEnum.getMsg());
		}

	}



	private CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {

		CrawlingTreaty treaty = null;
		List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

		//특약명 영역
		WebElement $treatyNameTd = $tdList.get(2);

		//특약 보험기간 영역
		WebElement $treatyInsTermTd = $tdList.get(3);

		//특약 납입기간 영역
		WebElement $treatyNapTermTd = $tdList.get(4);

		//특약 보장금액 영역
		WebElement $treatyAssureMoneyTd = $tdList.get(5);
		WebElement $treatyAssureMoneySelect = $treatyAssureMoneyTd.findElement(By.tagName("select"));

		//특약 보장금액이 "미가입"이 아닌경우에만
		String script = "return $(arguments[0]).find('option:selected').text();";
		String treatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneySelect));
		boolean isJoin = !"미가입".equals(treatyAssureMoney);

		if (isJoin) {
			String treatyName = $treatyNameTd.getText().trim();
			String treatyInsTerm = $treatyInsTermTd.getText();
			String treatyNapTerm = $treatyNapTermTd.getText();

			treatyInsTerm = treatyInsTerm.replace("만기", "");
			treatyNapTerm = treatyNapTerm.replace("납", "");
			script = "return $(arguments[0]).find('option:selected').val();";
			treatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneySelect));

			treaty = new CrawlingTreaty();
			treaty.setTreatyName(treatyName);
			treaty.setInsTerm(treatyInsTerm);
			treaty.setNapTerm(treatyNapTerm);
			treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
		}

		return treaty;

	}



	/**
	 * 영업보험료 임의로 세팅 해야하는 케이스
	 * @param info
	 * @throws Exception
	 */
	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {

		CrawlingProduct info = (CrawlingProduct) obj[0];
		WebElement $premiumId = null;               // 보장보험료 영역
		WebElement $businessPremiumInput = null;    // 영업보험료 input
		WebElement $savePremiumId = null;           // 적립보험료 영역
		WebElement $returnPremiumId = null;         // 예상만기환급금 영역
		String premium = "";                        // 보장보험료
		String savePremium = "";                    // 적립보험료
		String returnPremium = "";                  // 예상만기환급금
		WebElement $button = null;


		logger.info("영업보험료에 임의로 값 세팅(100만원)");
		$businessPremiumInput = driver.findElement(By.id("txtBussPrem"));
		try {
			helper.sendKeys4_check($businessPremiumInput, "1000000");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		logger.info("보험료계산 버튼 클릭");
		$button = driver.findElement(By.id("btnInsCalc"));
		try {
			click($button);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		logger.info("보장보험료 금액 읽어오기");
		$premiumId = driver.findElement(By.id("nGrntPrem"));
		premium = $premiumId.getText();
		premium = premium.replaceAll("[^0-9]", "");

		logger.info("보장보험료 금액을 읽어 영업보험료에 세팅");
		$businessPremiumInput = driver.findElement(By.id("txtBussPrem"));
		try {
			helper.sendKeys4_check($businessPremiumInput, premium);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		logger.info("보험료계산 버튼 클릭");
		$button = driver.findElement(By.id("btnInsCalc"));
		try {
			click($button);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		logger.info("보장보험료 금액 읽어오기");
		$premiumId = driver.findElement(By.id("nGrntPrem"));
		premium = $premiumId.getText();
		premium = premium.replaceAll("[^0-9]", "");

		logger.info("예상만기환급금 금액 읽어오기");
		$returnPremiumId = driver.findElement(By.id("txtExptEndRetrnAmt"));
		returnPremium = $returnPremiumId.getText();
		returnPremium = returnPremium.replaceAll("[^0-9]", "");

		info.getTreatyList().get(0).monthlyPremium = premium;
		info.returnPremium = returnPremium;

	}



	public void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {

		String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());

		// 특약 보장금액 영역
		WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[6]"));
		WebElement $treatyAssureMoneySelect = $treatyAssureMoneyTd.findElement(By.tagName("select"));

		// 특약 보장금액 설정
		helper.selectByValue_check($treatyAssureMoneySelect, treatyAssureMoney);

	}



	// 스크린샷
	protected void takeScreenShot(CrawlingProduct info) {

		String screenShotOptionCode = getCommandOptions().getScreenShot();

		//-ss="" argument의 값이 Y일 경우
		if ("Y".equals(screenShotOptionCode)) {
			crawlScreenShot = new CrawlScreenShot();
			String capturedTime = DateUtil.formatString(new Date(), "yyyyMMddHHmmss");
			String encodedData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

			int age = Integer.parseInt(info.age);
			String gender = (info.gender == MALE) ? "M" : "F";
			String companyShortName = info.productCode.substring(0, 3); // ex)KBF
			String fileName =
					companyShortName + "/"
							+ info.productCode + "/"
							+ info.planId + "/" + info.planId + "_" + capturedTime + "_" + age + "_" + gender + ".jpg";

			crawlScreenShot.setPlanId(Integer.parseInt(info.planId));
			crawlScreenShot.setProductId(info.productCode);
			crawlScreenShot.setStatus("Y");
			crawlScreenShot.setFileName(fileName);
			crawlScreenShot.setInsAge(age);
			crawlScreenShot.setGender(gender);
			crawlScreenShot.setCapturedTime(capturedTime);
			crawlScreenShot.setEncodedData(encodedData);
		}

	}



	@Override
	public void waitLoadingBar() {

		try {
			helper.waitForCSSElement("#loading");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
