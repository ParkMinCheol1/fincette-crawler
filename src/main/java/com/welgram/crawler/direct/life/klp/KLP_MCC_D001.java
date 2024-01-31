package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class KLP_MCC_D001 extends CrawlingKLP {



	public static void main(String[] args) {
		executeCommand(new KLP_MCC_D001(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setBrowserType(BrowserType.Chrome);
		option.setImageLoad(true);
	}


	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

			logger.info("생년월일");
			helper.sendKeys1_check(By.id("plnnrBrdt"), info.birth);

			logger.info("성별 :: " + info.gender);

			if (info.gender == 0){
				element = driver.findElement(By.id("gender1"));
			}else{
				element = driver.findElement(By.id("gender2"));
			}
			element.click();
			WaitUtil.waitFor(2);

			logger.info("가입조건 확인하기");
			element = driver.findElement(By.id("entCndtCfmBtn"));
			element.click();
			WaitUtil.loading(2);

			logger.info("보험기간 - 10년 고정");

			logger.info("납입기간");
			if (info.getNapTerm().equals("일시납")){
				element = driver.findElement(By.id("pmtpd0"));
			}else if (info.getNapTerm().equals("5년")){
				element = driver.findElement(By.id("pmtpd5"));
			}
			element.click();
			WaitUtil.loading(2);

			logger.info("결과 확인하기");
			element = driver.findElement(By.id("calcBtn"));
			element.click();
			WaitUtil.loading(2);

			logger.info("보험료");

			String premium;
			element = driver.findElement(By.cssSelector("#result_pltcPrm"));
			premium = element.getText().replaceAll("[^0-9]", "");
			logger.debug("###### 월보험료: " + premium);
			logger.info("###### 월보험료: " + premium);
			info.treatyList.get(0).monthlyPremium = premium;
			info.errorMsg = "";


			WaitUtil.loading(2);

			logger.info("해약환급금 조회");
			getReturns("aRefundPop", info);

	}
}
