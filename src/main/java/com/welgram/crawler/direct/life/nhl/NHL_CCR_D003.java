package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * NH온라인암보험보험(갱신형,무배당)
 * 		-> 보험명 변경 : NH모두의암보험(Self가입형,갱신형,비갱신형,무배당) 21.01.15. 확인 (차후 변경 예정)
 *
 * NHL_CCR_D003 상품의 공시실은 존재하지않아 Homepage크롤링 코드만 작성
 *  (공시실에서 현 상품의 보험료계산 클릭 시 Homepage크롤링 브라우저와 동일한 Web브라우저가 화면에 뜬다. - 21.01.15. 확인)
 */
public class NHL_CCR_D003 extends CrawlingNHLDirect {



	
	// NH온라인암보험보험(갱신형,무배당)
	public static void main(String[] args) {
		executeCommand(new NHL_CCR_D003(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "cal_gender1" : "cal_gender2";
		String genderText = (info.getGender() == MALE) ? "남자" : "여자";

		logger.info("NHL_CCR_D003 :: {}", info.getProductName());
		WaitUtil.waitFor(2);

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(By.id("birth"), info.getFullBirth());

		logger.info("성별 :: {}", genderText);
		setGender(By.xpath("//input[@id='" + genderOpt + "']/parent::label"), genderText);

		logger.info("보험료 확인");
		btnClick(By.id(("calcPremium")), 2);
		alert();

		logger.info("갱신 비갱신 UI 처리 :: " + info.getTextType());
		String assureId = "insuredAmountRe";
		String returnLayer = "#cancerReturn_uiPOPRefund0";
		String premiumId = "premiumRe";
		String reCalcId = "reCalcPremiumRe";

		if (info.getTextType().equals("비갱신형")){
			assureId = "insuredAmountDe";
			returnLayer = "#cancerReturn_uiPOPRefund1";
			premiumId = "premiumDe";
			reCalcId = "reCalcPremiumDe";
		}

		logger.info("가입금액 :: {}", info.getAssureMoney());
		setAssureMoney(By.xpath("//*[@id='" + assureId + "']"), info.getAssureMoney());

		logger.info("납입기간 :: {}", info.getNapTerm());
		setNapTerm(By.id("napTermDe"), info.getNapTerm());

		logger.info("다시 계산하기");
		calcBtnClickforPremium(By.id("reCalcPremiumRe"));
		alert(); //팝업 창 닫기

		logger.info("월 보험료 가져오기");
		crawlPremium(By.id(premiumId), info);

		logger.info("해약환급금 가져오기");
		crawlReturnMoneyList(info, By.cssSelector(returnLayer + " > table > tbody > tr"));

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}

	//팝업 창 닫기
	protected void alert() throws Exception{

		try{
			WebElement $button = null;

			logger.info("안내 확인 클릭");
			$button = driver.findElement(By.cssSelector("#uiPOPMesSimProdInfoSub > div.info > div.label-check > label > span"));
			btnClickByScript($button, 2);

			logger.info("일반가입형 클릭");
			$button = driver.findElement(By.id("defConfirmBtn"));
			btnClickByScript($button, 2);

			helper.waitForCSSElement("#uiPOPLoading1");

		} catch(Exception e) {
			logger.info("안내 팝업 없음 !!");
		}
	}
}

