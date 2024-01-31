package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HMF_DRV_F002 extends CrawlingHMFAnnounce {

	// 행복누리 운전자보험 기본형
	public static void main(String[] args) {
		executeCommand(new HMF_DRV_F002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);
		return true;
	}

	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

		logger.info("생년월일 설정 : {}", info.fullBirth);
		setBirthday(info.fullBirth);

		logger.info("성별 설정 : {}", (info.gender == MALE) ? "남자" : "여자");
		setGender(info.gender);

		logger.info("확인 버튼 클릭");
		WebElement $confirmButton = driver.findElement(By.linkText("확인"));
		clickButton($confirmButton);

		logger.info("보험계약기간 설정");
		int contractTerm = 365;	// 계약기간1년(365일)
		WebElement $startDate = driver.findElement(By.id("selStart"));
		WebElement $endDate = driver.findElement(By.id("selEnd"));
		setTravelDate(contractTerm, $startDate, $endDate);

		logger.info("상해급수 설정 : (고정)1급");
		setInjuryLevelRadio("radNClass", "1급");

		logger.info("계약연수 설정 : {}", info.insTerm);
		setTermRadio("rdoMonth", info.insTerm);

		logger.info("가입형태 설정 : {}", info.textType);
		WebElement $inputB = driver.findElement(By.id("rdoMatKind_A"));
		clickButton($inputB);
//		setProductTypeRadio("grid_ch", info.textType);

		logger.info("계산하기 버튼 클릭");
		clickCalculateButton(By.linkText("계산하기"));

		logger.info("주계약 보험료 설정");
		WebElement $premium = driver.findElement(By.id("ipbTotalPrem"));
		crawlPremium($premium, info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

	}
}
