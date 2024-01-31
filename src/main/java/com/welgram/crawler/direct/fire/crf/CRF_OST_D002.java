package com.welgram.crawler.direct.fire.crf;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;


// 2022.08.01 			| 최우진 				| 다이렉트_여행보험
// CRF_OST_D002 		| 캐롯해외여행보험
public class CRF_OST_D002 extends CrawlingCRFDirect {

	public static void main(String[] args) {
		executeCommand(new CRF_OST_D002(), args);
	}


	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == 0) ? "gender1": "gender2";
		String genderText = (info.getGender() == 0) ? "남자" : "여자";

		logger.info("START :: CRF_OST_D002 :: {}", info.getProductName());
		WaitUtil.loading(10);

		logger.info("자동차보험 가입 모달창 끄기");
		closeModal();

		logger.info("이름 입력");
		setUserName(By.xpath("//*[@id='ownerName']"), PersonNameGenerator.generate());

		logger.info("생년월일 입력 :: {}", info.getFullBirth());
		setBirthday(By.id("birthday"), info.getFullBirth());

		logger.info("성별 설정 :: {}", genderText);
		setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

		logger.info("next 버튼 클릭");
		btnClick(By.xpath("//button[@id='btn-basic-start']"), 2);

		logger.info("상품유형 :: [개인형] 선택");
		setProductType(By.xpath("//div[@class='fx-item']//*[text()='개인형']"));

		logger.info("다음 클릭");
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 300)");
		btnClick(By.xpath("//span[text()='다음']"), 2);

		logger.info("여행 날짜 설정");
		setTravelDate();

		logger.info("다음 클릭");
		btnClick(By.xpath("//span[text()='다음']"), 2);

		logger.info("담보 중복 가입 안내 레이어 닫기");
		btnClick(By.cssSelector("#OverlapGuidePop > div:nth-child(1) > div > div > div.box-footer > button"), 10);

		logger.info("로딩레이어");
		helper.waitForCSSElement("#carrot-loading-wrap");

		logger.info("보험 플랜 선택 :: {}", info.getTextType());
		setPlanType(By.xpath("//strong[contains(text(), '" + info.getTextType() + "')]"));

		logger.info("특약 세팅");
		setTreaties(info.getTreatyList(), By.xpath("//tbody//tr"));

		logger.info("보험료 가져오기");
		crawlPremium(info, By.xpath("//li[@class='plan-select-item selected']//em[@class='item-stit']"));

		logger.info("스크린샷");
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, -500)");
		takeScreenShot(info);

		logger.info("해약환급금정보 없음");

		return true;
	}
}
