package com.welgram.crawler.direct.fire.hmf;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class HMF_CCR_F046 extends CrawlingHMFAnnounce {

	// 무배당 흥국화재 더플러스 종합보험(24.01)_(4종)(30년갱신형)(해약환급금 미지급형)
	public static void main(String[] args) {
		executeCommand(new HMF_CCR_F046(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromAnnounce(info);
		return true;
	}

	private void crawlFromAnnounce(CrawlingProduct info) throws Exception{

		logger.info("생년월일 설정 : {}", info.fullBirth);
		setBirthday(info.fullBirth);

		logger.info("성별 설정 : {}", (info.gender == MALE) ? "남자" : "여자");
		setGender(info.gender);

		logger.info("확인 버튼 클릭");
		WebElement $confirmButton = driver.findElement(By.linkText("확인"));
		clickButton($confirmButton);

		logger.info("판매플랜 설정 : {}", "(고정)홈페이지가격공시플랜");
		setProductType("select_SALE_PLAN", "홈페이지가격공시플랜");

		logger.info("보험기간 설정");
		setAnnounceTerms("select_PAYMENT_INSURANCE_PERIODCD", "100세", "100세");

		logger.info("납입방법 설정");
		WebElement $cycleSelect = driver.findElement(By.id("select_PAYMENT_METHODCD"));
		setNapCycle($cycleSelect, info.getNapCycleName());

		logger.info("상해급수 설정 : (고정)1급");
		WebElement $injuryLevelSelect = driver.findElement(By.id("select_INJCD"));
		setInjuryLevel($injuryLevelSelect, "1급");

		logger.info("운전차의 용도: (고정)자가용");
		selectOption(By.id("select_DRV_CARCD"), "자가용");

		logger.info("합계보험료 설정 : (임시)30000");
		setTextToInputBox(By.id("TBIB061_ACU_PREM2"), "30000");

		logger.info("특약 설정");
		setTreaties(info, "100세");

		logger.info("계산하기 버튼 클릭");
		clickCalculateButton(By.linkText("계산하기"));

		logger.info("해당 상품은 보장보험료가 3만원이 넘지 않아도 합계보험료로 가입이 되기때문에 보장보험료로 체크해준다");
		WebElement $grantPremium = driver.findElement(By.id("TBIB061_GRANT_PREM"));
		checkPremium($grantPremium, 30000);

		logger.info("주계약 보험료 설정");
		WebElement $premium = driver.findElement(By.id("sumPrem"));
		crawlPremium($premium, info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);
	}

	public void setAnnounceTerms(Object... obj) throws SetInsTermException {
		// 해당은 선택된 값 찾을 때 return $(arguments[0]).find('option:selected') 로 접근이 불가하여 직접 id를 받아 사용
		String title = "납기/보기";
		String tagId = (String) obj[0];
		WebElement $announceTermSelect = driver.findElement(By.id(tagId));
		String insTermText = (String) obj[1];
		String napTermText = (String) obj[2];

		String expectedTermText = insTermText + "납" + napTermText + "만기";

		String actualTermText = "";

		try {
			// 납입 설정
			selectOption($announceTermSelect, expectedTermText);
			actualTermText = ((JavascriptExecutor)driver).executeScript("return $(\"#" + tagId + " option:selected\").text()").toString();

			// 납입 비교
			super.printLogAndCompare(title, expectedTermText, actualTermText);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
			throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
		}
		waitAnnounceLoadingImg();
	}

	// 보장보험료 0원일 때 사용
	public void checkPremium(Object... obj) throws Exception {
		WebElement $grantPremium = (WebElement) obj[0];
		Integer minPremium = (Integer) obj[1];

		Integer monthlyPremium = Integer.parseInt($grantPremium.getText().replaceAll("[^0-9]", ""));
		if ( minPremium < monthlyPremium) {
			throw new Exception("보장보혐료가 기준금액 미만으로 가입불가한 설계입니다.");
		} else {
			logger.info("보장보험료: {} 로 가입가능한 설계입니다.", monthlyPremium);
		}
	}
}
