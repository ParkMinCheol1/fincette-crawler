package com.welgram.crawler.direct.fire.hnf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class HNF_CCR_F025 extends CrawlingHNFAnnounce {

	public static void main(String[] args) {
		executeCommand(new HNF_CCR_F025(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		logger.info("생년월일 설정");
		setBirthday(info.getFullBirth());

		logger.info("성별 설정");
		setGender(info.getGender());

		logger.info("상품종형 설정");
		setProductType(info.getTextType().split("#")[0]);

		logger.info("납입면제형태");
		WebElement selpyExemTpCd = driver.findElement(By.xpath("//*[@id='selpyExemTpCd']/option[1]"));
		click(selpyExemTpCd);

		logger.info("납입기간 설정");
		setNapTerm(info.getNapTerm());

		logger.info("보험기간 설정");
		setInsTerm(info.getInsTerm());

		logger.info("담보갱신주기 설정");
		setRenewType(info.getTextType().split("#")[1]);

		logger.info("납입주기 설정");
		setNapCycle(info.getNapCycleName());

		logger.info("직업 설정");
		setJob("전문가 및 관련 종사자", "과학 전문가 및 관련직", "생명과학 연구원", "생명과학 연구원");

		logger.info("조회버튼 클릭");
		WebElement $button = driver.findElement(By.id("btnTrtySrch"));
		click($button);

		logger.info("특약 설정");
		setTreaties(info.getTreatyList());

		logger.info("보험료 설정 및 크롤링");
		setPremium(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		return true;

	}

}