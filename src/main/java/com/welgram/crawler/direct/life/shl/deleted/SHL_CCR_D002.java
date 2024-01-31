package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


/**
 * 신한생명 - 무배당 신한인터넷 생활비주는암보험
 * 
 * @author SungEun Koo <aqua@welgram.com>
 */
// 2022.12.14			| 최우진 			| 다이렉트_암
// SHL_CCR_D002 		|
public class SHL_CCR_D002 extends CrawlingSHL {



	public static void main(String[] args) {
		executeCommand(new SHL_CCR_D002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		// 이름
		logger.info("[고객정보]이름 설정");
		String name = PersonNameGenerator.generate();
		logger.debug("name: {}", name);
		setName(name);

		// 생년월일
		logger.info("[고객정보]생년월일 설정");
		setBirth(info.fullBirth);

		// 성별
		logger.info("[고객정보]성별 설정");
		setGender(info.gender);

		// 설계목록가져오기
		logger.info("설계목록가져오기");
		getPlans();

		logger.info("보험형태 선택 :: {}", info.textType);
		selectPlan(By.id("insFormCd"), info.textType);

		// 보험가입금액(가입금액, 보험기간, 납입기간)
		logger.info("[주계약 /특약설계]보험가입금액 설정");
		setPremium(info);

		logger.info("[보험형태] 설정");
//			// 보험형태(보험형태, 보험종류, 납입주기)
//			setInsuranceType(info.productKind);	// 보험형태
		setInsuranceKind(info.productKind); // 보험종류
		setNapCycle(info.napCycle); // 납입주기

		// 보험료 계산하기
		logger.info("보험료계산");
		calculatePremium();

		// 보험료 조회
		logger.info("보험료 조회");
		getCrawlingResults(info.treatyList);

		takeScreenShot(info);

		logger.info("해약환급금 조회");
		getReturns(info);

		return true;
	}

}
