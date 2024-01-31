package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DBF_DSS_D006 extends CrawlingDBFDirect {

	// 무배당 프로미라이프 다이렉트 간편건강보험2301(CM) 일반형
	public static void main(String[] args) {
		executeCommand(new DBF_DSS_D006(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);
		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setUserData(true);
	}

	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

		logger.info("생일 입력: {}", info.fullBirth);
		setBirthday(By.cssSelector("#birthday"), info.fullBirth);

		logger.info("성별선택");
		setGender("sxCd", info.gender);

		logger.info("보험료 확인하기 클릭");
		helper.waitElementToBeClickable(driver.findElement(By.xpath("//span[contains(.,'보험료 확인하기')]"))).click();
		waitDirectLoadingImg();

		logger.info("운전형태 선택: 자가용 고정");
		setVehicle("oprtVhDvcd", "자가용");

		logger.info("이륜자동차 및 원동기장치 자전거 미사용 선택");
		driver.findElement(By.cssSelector("#mtccDrveYn2")).click();
		driver.findElement(By.cssSelector("#personalMobYn2")).click();
		driver.findElement(By.cssSelector("#q_chk01")).click();

		logger.info("직업 선택: 경영지원 사무직 관리자 고정");
		setJob("경영지원 사무직 관리자");

		logger.info("입력 정보 확인 버튼 클릭");
		driver.findElement(By.cssSelector("#privateAgree")).click();

		logger.info("다음 버튼 클릭");
		helper.waitElementToBeClickable(driver.findElement(By.linkText("다음"))).click();
		waitDirectLoadingImg();

		logger.info("상품유형에 따라 선택값이 초기화 되므로 상품유형 먼저 선택: {}", info.getTextType().split("#")[0]);
		setProductType("healthProdType", info.getTextType().split("#")[0]);
		waitDirectLoadingImg();

		logger.info("보험기간 선택: {}", info.insTerm);
		setInsTerm("selArcTrm", info.insTerm);
		waitDirectLoadingImg();

		logger.info("납입주기 선택: {}", info.napCycle);
		setNapCycle("pymMtdCd", info.napCycle);
		waitDirectLoadingImg();

		logger.info("보장내용 선택: {}", info.getTextType().split("#")[1]);
		setWarranty("pdcPanCd", info.getTextType().split("#")[1]);
		waitDirectLoadingImg();

		logger.info("특약셋팅");
		setTreaties(info.treatyList);
		waitDirectLoadingImg();

		logger.info("다시 계산하기 버튼 클릭");
		reComputeCssSelect(By.xpath("//span[contains(.,'다시 계산')]"));

		logger.info("월납입보험료 가져오기");
		WebElement $monthlyPremiumElement = driver.findElement(By.cssSelector("#totPrm"));
		crawlPremium($monthlyPremiumElement, info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);
		WaitUtil.waitFor(1);

		logger.info("해약환급금 예시 버튼 클릭");
		driver.findElement(By.xpath("//span[contains(.,'해약환급금 예시')]")).click();
		waitDirectLoadingImg();

		logger.info("해약환급금 저장");
		getReturnPremium(info);

	}

	@Override
	public void setBirthday(Object... obj) throws SetBirthdayException {
		String title = "생년월일";
		By $birthBy = (By) obj[0];

		String expectBirth = (String) obj[1];
		String actualBirth = "";

		try {

			actualBirth = helper.sendKeys4_check($birthBy, expectBirth);

			super.printLogAndCompare(title, expectBirth, actualBirth);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
			throw new SetBirthdayException(exceptionEnum.getMsg());
		}
	}

	@Override
	public void setGender(Object... obj) throws SetGenderException {
		String title = "성별";
		String tagName = (String) obj[0];

		int gender = (int) obj[1];
		String expectGender = (gender == MALE) ? "남자" : "여자";
		String script = "return $('input[name=" + tagName + "]:checked').attr('id');";

		try {

			WebElement $genderSelect = driver.findElement(By.xpath("//span[contains(.,'"+expectGender+"')]"));
			helper.waitElementToBeClickable($genderSelect).click();

			String actualGenderId = String.valueOf(helper.executeJavascript(script, expectGender));
			String actualGender = driver.findElement(By.xpath("//label[@for='"+ actualGenderId +"']")).getText();

			super.printLogAndCompare(title, expectGender, actualGender);
			WaitUtil.loading(1);


		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
			throw new SetGenderException(exceptionEnum.getMsg());
		}
	}

	@Override
	public void setProductType(Object... obj) throws SetProductTypeException {
		String title = "가입유형";

		String tagName = (String) obj[0];
		String expectedProductTypeText = (String) obj[1];
		String script = "return $('input[name="+tagName+"]:checked').attr('id')";

		try {

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
			throw new SetProductTypeException(exceptionEnum.getMsg());
		}
	}
}
