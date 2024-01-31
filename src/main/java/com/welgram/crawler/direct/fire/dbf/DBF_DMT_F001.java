package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DBF_DMT_F001 extends CrawlingDBFAnnounce {

	// 프로미 국내여행보험
	public static void main(String[] args) {
		executeCommand(new DBF_DMT_F001(), args);
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

		logger.info("생년월일 입력");
		setBirthday(By.cssSelector("#birthymd"), info.fullBirth);

		logger.info("성별선택");
		setGender("sex_type", info.gender);

		logger.info("여행일 선택: 여행기간 7일 고정");
		WebElement $selectDate = driver.findElement(By.name("select_date"));
		helper.selectByText_check($selectDate, "7일까지");

		logger.info("여행목적 선택: 여행 고정");
		setRadioLabel("tour_aim_temp_0", "여행");

		logger.info("직업 입력");
		setJob("경영지원 사무직 관리자");

		logger.info("특약 셋팅");
		List<WebElement> $trList = driver.findElements(By.xpath("//td[contains(@id, 'dambo_nm')]/parent::tr"));
		setTreatiesACD($trList, info.treatyList);

		logger.info("보험료 산출버튼 클릭");
		helper.waitElementToBeClickable(driver.findElement(By.linkText("보험료 산출"))).click();
		waitAnnounceLoadingImg();

		logger.info("월 보험료 크롤링");
		WebElement $monthlyPremium = driver.findElement(By.xpath("//tbody[@id='resultList']/tr[" + (info.treatyList.size()+1) + "]/td/div/input"));
		String premium = $monthlyPremium.getAttribute("value").trim().replaceAll("[^0-9]", "");
		info.treatyList.get(0).monthlyPremium = premium;
		logger.info("월 보험료 확인 : " + premium);

		WaitUtil.waitFor(1);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.cssSelector("#pm1")));

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

	}
}
