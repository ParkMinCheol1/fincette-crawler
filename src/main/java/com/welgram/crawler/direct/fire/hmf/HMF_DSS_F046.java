package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HMF_DSS_F046 extends CrawlingHMFAnnounce {

	// 무배당 흥국화재 든든한 325 간편종합보험(23.07)_(2종)(10년갱신형)(일반심사형)(해약환급금지급형)
	public static void main(String[] args) {
		executeCommand(new HMF_DSS_F046(), args);
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

		logger.info("합계보험료 설정 : (고정)30000");
		setTextToInputBox(By.id("TBIB061_ACU_PREM2"), "30000");

		logger.info("특약 설정");
		setTreaties(info, "skip");

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

}
