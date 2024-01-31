package com.welgram.crawler.direct.life.klp;

import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class KLP_CCR_D002 extends CrawlingKLP {



	public static void main(String[] args) {
		executeCommand(new KLP_CCR_D002(), args);
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

			/*
			//화면 메인창
			String windowIdMain = driver.getWindowHandle();
			//화면 여러창
			Set<String> windowId = driver.getWindowHandles();
			Iterator<String> handles = windowId.iterator();

			subHandle = null;
			while (handles.hasNext()) {
				subHandle = handles.next();
				logger.debug(subHandle);
				WaitUtil.loading(1);
			}
			//새로 뜨는 창 닫기
			driver.switchTo().window(subHandle).close();
			WaitUtil.loading(1);
			//메인창으로 돌아오기
			driver.switchTo().window(windowIdMain);
			*/


			//logger.info("담보명 확인");
			//checkProductMaster(info,"#wrap > div.visual.new > div > div.area_product > section > h1");

			logger.info("생년월일");
			helper.sendKeys1_check(By.id("plnnrBrdt"), info.fullBirth);

			logger.info("성별");
			setGender(info.gender);

			logger.info("흡연");
			setSmoke(info.discount);

			logger.info("보험료 확인/가입");
			setConfirmPremium(By.id("fastPayCalc"));

			logger.info("진단보험금 (일반암 기준)");
			setCancerReturnPremium(info.assureMoney);

			logger.info("보험기간");
			setInsTerm(By.id("inspdContents"), info.insTerm);

			logger.info("납입기간");
			setNapTerm(info.napTerm, info);

			logger.info("환급방식확인");
			setProductKind(info);

			logger.info("결과 확인하기");
			confirmResult();

			logger.info("보험료");
			getPremium("#premiumLabel2", info);

			logger.info("해약환급금 조회");
			getReturns("cancel1", info);

	}


}
