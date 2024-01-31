package com.welgram.crawler.direct.fire.hnf;

import com.welgram.crawler.general.CrawlingProduct;



public class HNF_DSS_F007 extends CrawlingHNFAnnounce {

	public static void main(String[] args) {
		executeCommand(new HNF_DSS_F007(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		logger.info("[STEP 1]기본정보 설정");
		setUserInfo(info);

		logger.info("[STEP 2]가입조건 설정");
		setJoinCondition(info);

		logger.info("[STEP 3]특약 설정");
		setTreaties(info.getTreatyList());

		logger.info("[STEP 4]보험료 설정 및 크롤링");
		setPremium(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		return true;

	}

}