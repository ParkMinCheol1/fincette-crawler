package com.welgram.crawler.direct.fire.hmf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class HMF_DMT_F001 extends CrawlingHMFAnnounce {

	// 국내여행보험 / 2023-05-25 기준 상품미사용
	public static void main(String[] args) {
		executeCommand(new HMF_DMT_F001(), args);
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

		logger.info("보험계약기간 설정");
		int contractTerm = 0;	// 계약기간이 고정인 여행보험일 경우 계약기간을 0으로 보낸다.
		WebElement $startDate = driver.findElement(By.id("selStart"));
		WebElement $endDate = driver.findElement(By.id("selEnd"));
		setTravelDate(contractTerm, $startDate, $endDate);

		logger.info("급수 설정 : (고정)1급");
		setInjuryLevelRadio("radNClass", "1급");

		logger.info("특약 설정");
		setTreatiesNew(info.treatyList);

		logger.info("계산하기 버튼 클릭");
		clickCalculateButton(By.linkText("계산하기"));

		logger.info("주계약 보험료 설정");
		WebElement $premium = driver.findElement(By.id("ipbTotalPrem"));
		crawlPremium($premium, info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);
	}

}
