package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 상품명 : 우리아이지킴이NH통합어린이보험(무배당)_2301
 * 상품유형 : 1종(30세만기)1형(순수보장형)일반형
 * 웹 크롤링으로 진행
 *
 */
public class NHL_CHL_F001 extends CrawlingNHLAnnounce {


	public static void main(String[] args) {
		executeCommand(new NHL_CHL_F001(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		String genderOpt = (info.getGender() == MALE) ? "sex_m" : "sex_fm";
		String genderText = (info.getGender() == MALE) ? "남" : "여";

		logger.info("NHL_CHL_F001 :: {}", info.getProductName());

		logger.info("성별 :: {}", genderText);
		setGender(By.xpath("//label[@for='" + genderOpt + "']"), genderText);

		logger.info("생년월일 :: {}", info.getFullBirth());
		setBirthday(By.id("brdt"), info.getFullBirth());

		logger.info("상품유형 :: {}", info.getTextType());
		setPlanType(By.id("prodTpcd"), info.getTextType());

		logger.info("특약 설정");
		setTreatiesChl(info.getTreatyList());

		logger.info("보험료 계산하기 버튼 클릭 ");
		btnClickforPremium(By.cssSelector("#pop_wrapper > p > span > button"));

		logger.info("월 보험료 가져오기");
		crawlPremium(By.xpath("//*[@id='result_money_3']"), info);

		logger.info("스크린샷");
		takeScreenShot(info);

		logger.info("해약환급금 가져오기");
		// 해약환급금만 제공하는 경우 : 1 || 최저보증/공시이율에 따른 환급금을 제공하는 경우 2
		int tableType = 1;
		crawlReturnMoneyList(info, tableType);

		return true;
	}


}