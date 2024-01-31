package com.welgram.crawler.direct.life.klp;

import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

/**
 * (무)e입원비보험
 */

public class KLP_DSS_D002 extends CrawlingKLP {



	public static void main(String[] args) {
		executeCommand(new KLP_DSS_D002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);

		return true;
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


			// 생년월일
			logger.info("생년월일 설정");
			helper.sendKeys1_check(By.id("plnnrBrdt"), info.fullBirth);

			// 성별
			logger.info("성별 설정");
			setGender(info.gender);

			// 보험료 확인/가입
			logger.info("보험료 확인/가입");
			setConfirmPremium(By.id("fastPayCalc"));

			// 보험가입금액
			logger.info("보험가입 설정");
			info.assureMoney = String.valueOf(Integer.parseInt(info.assureMoney)*3/1000);
			setPremium(info);

			// 보험기간
			logger.info("보험기간 설정");
			setInsTerm(By.id("inspdContents"), info.insTerm);

			// 납입기간
			logger.info("납입기간 설정");
			setNapTerm(info.napTerm, info);

			// 만기환급률
			logger.info("만기환급률");
			maturityReturnPerCent(info.insuName);

			// 결과 확인하기
			logger.info("결과 확인하기");
			confirmResult();

			// 창 닫기
			logger.info("창 닫기");
			iFrameCheck();

			// 보험료
			logger.info("보험료");
			getPremium("#premiumLabel2", info);

			logger.info("해약환급금(예시표)");
			getReturns("cancel1", info);

	}



}