package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 * 검진쏘옥NH용종진단보험(Self가입형,무배당)
 *
 */
public class NHL_DSS_D005 extends CrawlingNHLDirect {


	public static void main(String[] args) {
		executeCommand(new NHL_DSS_D005(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "cal_gender1" : "cal_gender2";
		String genderText = (info.getGender() == MALE) ? "남자" : "여자";

		logger.info("NHL_DSS_D005 :: {}", info.getProductName());
		WaitUtil.waitFor(2);

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(By.id("birth"), info.getFullBirth());

		logger.info("성별 :: {}", genderText);
		setGender(By.xpath("//input[@id='" + genderOpt + "']/parent::label"), genderText);

		logger.info("보험료 확인");
		btnClick(By.id(("calcPremium")), 2);
		helper.waitForCSSElement("#uiPOPLoading1");

		// 보험료 알아보기 이벤트 팝업
		checkEventPopup(By.id("uiPOPMesProdEvnt"), By.xpath("//button[@id='closePopBtn']"));

		logger.info("월보험료 가져오기");
		crawlPremium(By.id("premium"), info);

		logger.info("해약환급금 가져오기");
		crawlReturnMoneyList(info, By.cssSelector("#polypReturn_uiPOPRefund1 > table > tbody > tr"));

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;

	}
}