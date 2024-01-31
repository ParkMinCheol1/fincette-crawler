package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;


public class DBF_MDC_F004 extends CrawlingDBFAnnounce {

	// 실손의료비보험(기본납입형)
	public static void main(String[] args) {
		executeCommand(new DBF_MDC_F004(), args);
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
		setBirthday(By.cssSelector("#birthDay"), info.fullBirth);

		logger.info("성별선택");
		setGender("sx_cd", info.gender);

		logger.info("직업 입력");
		setJob("경영지원 사무직 관리자");

		// 플랜 서브네임: 자유설계형 -> 해당 planSubName으로 가입상품 선택
		logger.info("가입유형 클릭 : "+info.textType);
		setProductType("sl_pan_cd", info.textType);

		logger.info("보험기간 선택 (보험기간+납입기간 같이)");
		WebElement $termSelect = driver.findElement(By.cssSelector("select[name=exp_pytr]"));
		String insNapTerm = "5년만기5년납";
		setTerm($termSelect, insNapTerm);

		logger.info("납입주기 선택");
		WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=pym_mtd_cd]"));
		String napCycleText = info.napCycle.equals("01") ? "월납" : "연납";
		setNapCycle($napCycleSelect, napCycleText);

		logger.info("보장목록 확인 클릭");
		helper.waitElementToBeClickable(driver.findElement(By.linkText("보장목록 확인"))).click();
		waitAnnounceLoadingImg();

		logger.info("특약 셋팅");
		List<WebElement> $trList = driver.findElements(By.cssSelector("#tableDamboList > tr"));
		setTreaties($trList, info.treatyList);

		logger.info("보험료 산출버튼 클릭");
		helper.waitElementToBeClickable(driver.findElement(By.linkText("보험료 산출"))).click();
		waitAnnounceLoadingImg();

		logger.info("월 보험료 크롤링");
		WebElement $monthlyPremiumTd = driver.findElement(By.xpath("//td[@class='ft rt']"));
		crawlPremium($monthlyPremiumTd, info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

	}

}
