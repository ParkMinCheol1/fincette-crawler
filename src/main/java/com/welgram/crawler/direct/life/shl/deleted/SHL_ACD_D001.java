package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;


/**
 * (무)Birth Start Travel 선물보험
 * 코드작성일 21.02.03 - mincheol
 * 21.02.03 - 현재 신한생명 홈페이지에서 상품 선택 시 상품준비 중으로 알럿
 * 
 */
// 2022.12.01 		| 최우진 			| 다이렉트_상해
// SHL_ACD_D001 	| (무)Birth Start Travel 선물보험
//public class SHL_ACD_D001 extends CrawlingSHL implements ScrapableNew {
public class SHL_ACD_D001 extends CrawlingSHL {

	public static void main(String[] args) {
		executeCommand(new SHL_ACD_D001(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// INFORMATION
		String name = PersonNameGenerator.generate();

		// PROCESS
		// 이름
		logger.info("[고객정보]이름 설정");
		setName(name);

		// 생년월일
		logger.info("[고객정보]생년월일 설정");
		setBirth(info.getFullBirth());
		logger.info("생년월일 :: {}",info.getFullBirth());

		// 성별
		logger.info("[고객정보]성별 설정 :: {}", info.getGender());
		setGender(info.getGender());

		// 설계목록가져오기
		logger.info("설계목록가져오기");
		getPlans();

		// 보험형태
		logger.info("[보험 형태] 설정");
		setInsuranceKind("연납"); // 보험종류

		// 보험가입금액(가입금액, 보험기간, 납입기간)
		logger.info("[ 주계약 /특약설계 ] 보험가입금액 설정");
		setPremium(info);

		// 보험료 계산하기
		logger.info("보험료계산");
		calculatePremium();
		takeScreenShot(info);

		// 보험료 조회
		getCrawlingResults(info.getTreatyList());

		logger.info("해약환급금 조회");
		getReturns(info);

		return true;
	}

}
