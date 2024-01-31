package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 * 효밍아웃NH부모님안전보험(무배당)
 *
 * 웹 크롤링으로 진행
 *
 */
public class NHL_DSS_D003 extends CrawlingNHLDirect {
	


	public static void main(String[] args) {
		executeCommand(new NHL_DSS_D003(), args);
	}

	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "아버지" : "어머니";

		logger.info("NHL_DSS_D003 :: {}", info.getProductName());
		WaitUtil.waitFor(2);

		logger.info("성별 :: {}", genderOpt);
		setGender(By.xpath("//span[contains(., '" + genderOpt + "')]"), genderOpt);

		logger.info("해약환급금 조회 버튼 클릭");
		btnClick(By.xpath("//*[@id='showReturn1'][contains(text(),'해약환급금')]"), 5);

		logger.info("월 보험료 가져오기");
		crawlPremium(By.xpath("//*[@id='uiPOPRefund']//dd[@class='result']"), info);

		logger.info("해약환급금 가져오기");
		crawlReturnMoneyList(info, By.cssSelector("#hyoReturn_uiPOPRefund1 > table > tbody > tr"));

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}
}
