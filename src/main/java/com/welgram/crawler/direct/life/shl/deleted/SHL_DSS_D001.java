package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;

/**
 * 신한생명 - (무)신한인터넷당뇨엔두배받는건강보험
 * 
 * @author hyunlae.kim <hyumlae@welgram.com>
 */
public class SHL_DSS_D001 extends CrawlingSHL {



	public static void main(String[] args) {
		executeCommand(new SHL_DSS_D001(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		// 공시실 열기
		logger.info("공시실 열기");
//			openAnnouncePage(info);

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

		// 보험가입금액
		logger.info("[주계약 /특약설계]보험가입금액 설정");
		setPremium(info);


		// 보험료 계산하기
		logger.info("보험료 계산하기");
		calculatePremium();

		// 보험료 조회
		logger.info("보험료 조회");
		getCrawlingResults(info.treatyList);

		logger.info("해약환급금 조회");
		getReturns(info);

		return true;
	}

}
