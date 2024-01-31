package com.welgram.crawler.direct.fire.hnf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class HNF_DSS_F027 extends CrawlingHNFAnnounce {

	// 무배당 건강하면 더 좋은 하나의 보험(2309) 1종(세만기형, 일반형)
	public static void main(String[] args) {
		executeCommand(new HNF_DSS_F027(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		logger.info("[STEP 1]기본정보 설정");
		setUserInfo(info);

		logger.info("[STEP 2]가입조건 설정");
		// setJoinCondition(info);
		setInsuName("표준체");

		logger.info("보험기간 설정");
		setInsTerm(info.insTerm);

		logger.info("납입기간 설정");
		setNapTerm(info.napTerm);

		logger.info("납입주기 설정");
		setNapCycle("월납");

		logger.info("담보갱신주기 설정");
		WebElement $selectInput = driver.findElement(By.xpath("//select[@id='selCovdPaymCyclCd']/option[@value='20']"));
		click($selectInput);
		// setRenewType(info);

		logger.info("직업 설정");
		setJob("관리자 (사무직)", "전문서비스 관리직", "연구 사무직 관리자", "연구 사무직 관리자");

		logger.info("조회버튼");
		WebElement $button = driver.findElement(By.id("btnTrtySrch"));
		click($button);

		logger.info("[STEP 3]특약 설정");
		setTreaties(info.getTreatyList());

		logger.info("[STEP 4]보험료 설정 및 크롤링");
		setPremium(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		return true;

	}

}