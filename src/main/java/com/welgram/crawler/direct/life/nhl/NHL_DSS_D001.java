package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 * NH온라인뇌심장튼튼건강보험(무배당)
 *
 * NHL_DSS_D001 상품의 공시실은 존재하지않아 Homepage크롤링 코드만 작성
 * (공시실에서 현 상품의 보험료계산 클릭 시 Homepage크롤링 브라우저와 동일한 Web브라우저가 화면에 뜬다. - 21.01.15. 확인)
 */
public class NHL_DSS_D001 extends CrawlingNHLDirect {
	

	
	// NH온라인뇌심장튼튼건강보험(무배당)
	public static void main(String[] args) {
		executeCommand(new NHL_DSS_D001(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "cal_gender1" : "cal_gender2";
		String genderText = (info.getGender() == MALE) ? "남자" : "여자";

		logger.info("NHL_DSS_D001 :: {}", info.getProductName());
		WaitUtil.waitFor(2);

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(By.id("birth"), info.getFullBirth());

		logger.info("성별 :: {}", genderText);
		setGender(By.xpath("//input[@id='" + genderOpt + "']/parent::label"), genderText);

		logger.info("보험료 확인");
		btnClick(By.id(("calcPremium")), 2);
		helper.waitForCSSElement("#uiPOPLoading1");

		// 가입금액 value : 500, 1000, 1500, 2000
		logger.info("가입금액 :: {}", info.getAssureMoney());
		setAssureMoney(By.id("insuredAmountMy"), info.getAssureMoney());

		logger.info("보험기간 :: {}", info.getInsTerm());
		setInsTerm(By.id("insTermMy"), info.getInsTerm());

		logger.info("납입기간 :: {}", info.getNapTerm());
		setNapTerm(By.id("napTermMy"), info.getNapTerm());

		logger.info("다시 계산하기");
		calcBtnClickforPremium(By.id("calcPremium"));

		logger.info("월 보험료 가져오기");
		crawlPremium(By.id("premiumMy"), info);

		logger.info("스크린샷");
		takeScreenShot(info);

		logger.info("해약환급금 가져오기");
		crawlReturnMoneyList(info, By.cssSelector("#polypReturn_uiPOPRefund1 > table > tbody > tr"));


		return true;
	}
}
