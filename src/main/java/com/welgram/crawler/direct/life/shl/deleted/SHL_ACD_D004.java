package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2022.12.14 			| 최우진 			| 다이렉트_상해
// SHL_ACD_D004			|
public class SHL_ACD_D004 extends CrawlingSHL {

	public static void main(String[] args) {
		executeCommand(new SHL_ACD_D004(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		waitMobileLoadingImg();

		logger.info("보험료 계산하기 버튼 클릭!");
		helper.waitElementToBeClickable(By.id("btnCalc")).click();

		logger.info("성별 설정");
		setMobileGender(info.gender);

		logger.info("생년월일 설정");
		setMobileBirth(info.fullBirth);

		logger.info("확인 버튼 클릭!");
		helper.waitElementToBeClickable(By.xpath("//div[@class='o-layer-dim block']//span[text()='확인']")).click();
		waitMobileLoadingImg();

		logger.info("년 보험료 설정");
		setMobilePremium(info);
		takeScreenShot(info);

		return true;
	}
}
