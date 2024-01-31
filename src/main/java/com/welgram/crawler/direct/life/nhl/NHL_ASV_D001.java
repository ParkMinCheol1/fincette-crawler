package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 * NH온라인연금저축보험(무배당)
 *
 * NHL_ASV_D001 상품의 공시실은 존재하지않아 Homepage크롤링 코드만 작성
 * (공시실에서 현 상품의 보험료계산 클릭 시 Homepage크롤링 브라우저와 동일한 Web브라우저가 화면에 뜬다. - 21.01.15. 확인)
 */
public class NHL_ASV_D001 extends CrawlingNHLDirect {


	// NH온라인연금저축보험(무배당)
	public static void main(String[] args) {
		executeCommand(new NHL_ASV_D001(), args);
	}


	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "cal_gender1" : "cal_gender2";
		String genderText = (info.getGender() == MALE) ? "남자" : "여자";

		logger.info("NHL_ASV_D001 :: {}", info.getProductName());
		WaitUtil.waitFor(2);

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(By.id("birth"), info.getFullBirth());

		logger.info("성별 :: {}", genderText);
		setGender(By.xpath("//input[@id='" + genderOpt + "']/parent::label"), genderText);

		logger.info("보험료 계산하기");
		calcBtnClickforPremium(By.id("calcPremium"));

		// 이벤트 팝업
		// checkEventPopup(팝업 영역, 클로즈 버튼)
		checkEventPopup(By.xpath("//*[@id=\"uiPOPMesProdEvnt\"]"), By.xpath("//button[@id='closePopBtn']"));

		logger.info("연금개시나이 :: {}", info.getAnnuityAge());
		setAnnuityAge(By.id("annuityAgeMy"), info.getAnnuityAge());

		logger.info("납입기간 :: {}", info.getNapTerm());
		setNapTerm(By.id("napTermMy"), info.getNapTerm());

		logger.info("연금지급형태 :: {}", info.getAnnuityType());
		setAnnuityType(By.id("annuityPayTypeMy"), info.getAnnuityType());

		// 매년 고정
		logger.info("연급지급주기 :: {}", "매년");
		setAnnuityReceiveCycle(By.id("annuityPayPeriodMy"), "매년");

		logger.info("월 납입금액 :: {}", info.getAssureMoney());
		setPremium(By.id("premiumMy"), info.getAssureMoney());

		logger.info("다시 계산하기 버튼 클릭");
		calcBtnClickforPremium(By.id("calcPremium"));

		// 이벤트 팝업
		checkEventPopup(By.xpath("//*[@id=\"uiPOPMesProdEvnt\"]"), By.xpath("//button[@id='closePopBtn']"));

		// 월 납입보험료
		// 사이트에서 입력된 값을 얻을 수 없어 부득이 가설의 가입금액을 넣어준다.
		info.treatyList.get(0).monthlyPremium = info.getAssureMoney();

		logger.info("스크린샷");
		takeScreenShot(info);

		// 연금수령액 / 연금테이블
		crawlExpectedSavePremium(info);
		logger.info("예상 연금 수령액 :: {} ", info.getAnnuityPremium());

		//연금 수령액 10년,20년,30년 모두 값을 가져온 후 다시 해약환급금을 가져오기 위해 가설의 annuityType선택
		logger.info("연금지급형태 :: {}", info.getAnnuityType());
		setAnnuityType(By.id("annuityPayTypeMy"), info.getAnnuityType());

		logger.info("다시 계산하기 버튼 클릭");
		calcBtnClickforPremium(By.id("calcPremium"));
		// 보험료 알아보기 팝업
		checkEventPopup(By.xpath("//*[@id=\"uiPOPMesProdEvnt\"]"), By.xpath("//button[@id='closePopBtn']"));



		logger.info("해약환급금 가져오기");
		crawlReturnMoneyList2(info, By.cssSelector("#annuityReturn_uiPOPRefund1 .ui-tab-con.active tbody tr"));

		return true;
	}
}
