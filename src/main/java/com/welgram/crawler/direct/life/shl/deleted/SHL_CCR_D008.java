package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


public class SHL_CCR_D008 extends CrawlingSHL {
	//암 파인 암보험(무배당) 1종(남성형)


	public static void main(String[] args) {
		executeCommand(new SHL_CCR_D008(), args);
	}

	@Override
	protected boolean preValidation(CrawlingProduct info) {
		boolean result = true;

		if(info.gender == FEMALE){
			logger.info("여성은 가입이 불가합니다.");
			result = false;
		}

		return result;
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
