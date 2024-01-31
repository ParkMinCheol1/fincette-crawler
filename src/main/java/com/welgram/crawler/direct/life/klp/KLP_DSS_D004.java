package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class KLP_DSS_D004 extends CrawlingKLP {



	public static void main(String[] args) {
		executeCommand(new KLP_DSS_D004(), args);
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


			logger.info("담보명 확인");
			checkProductMaster(info,"#wrap > div.visual.new > div > div.area_product > section > h1");

			logger.info("생년월일");
			helper.sendKeys1_check(By.id("plnnrBrdt"), info.fullBirth);

			logger.info("성별");
			setGender(info.gender);

			logger.info("흡연");
//			setSmoke(info.discount);

			logger.info("보험료 확인/가입");
			setConfirmPremium(By.id("fastPayCalc"));

//			logger.info("상품마스터");
//			getTreaties(info, exeType);

			logger.info("진단보험금");
			// 2000만원 이상인경우만 처리
			logger.debug(info.getAssureMoney());
			if (Integer.parseInt(info.getAssureMoney())>10000000){
				logger.info("2000만원 이상인경우만 처리");
				setDiagnosticBenefit(info.assureMoney);
			}

			logger.info("보험기간");
			setInsTerm(By.id("inspdContents"), info.insTerm);

			logger.info("납입기간");
			setNapTerm(info.napTerm, info);

			logger.info("만기환급률");
			//maturityReturnPerCent(info.insuName);

			logger.info("결과 확인하기");
			confirmResult();

			logger.info("보험료");
			getPremium("#premiumLabel2", info);

//			logger.info("만기환급금");
//			getReturnPremium("#cancel1", info);

			logger.info("해약환급금 조회");
			getReturns("cancel1", info);

	}

	protected void setConfirmPremium(By by) throws Exception {

		// logger.debug(driver.findElement(By.cssSelector(".btn_talk_close")).isDisplayed());
		// 해당 클래스명이 보이면 true
		WaitUtil.loading(6);
		if (driver.findElement(By.cssSelector(".btn_talk_close")).isDisplayed()) {
			Thread.sleep(1000);
			element = driver.findElement(By.className("btn_talk_close"));
			element.click();

			logger.debug("상담톡 닫기!");
			Thread.sleep(1000);
		}
		logger.debug("step1");
		WaitUtil.loading(1);
		//((JavascriptExecutor) driver).executeScript("scroll(0,1000);");
		element = driver.findElement(by);
		element.click();
		logger.debug("step2");

		helper.waitForCSSElement("#loadingArea");
	}

	protected void setDiagnosticBenefit(String value) throws Exception {
		boolean result = true;
		String premium = value;
		for (int i = 0; i < 3; i++) {
			WaitUtil.waitFor(2);
			if (!(driver.findElements(By.className("tab_rdo")).get(0).isDisplayed())) {
				logger.debug("로딩 중....");
			} else {
				logger.debug("로딩 끝....");
				break;
			}
		}
		element = driver.findElements(By.className("tab_rdo")).get(0);
		elements = element.findElements(By.tagName("li"));

		while (result) {
			for (int i = 0; i < elements.size(); i++) {
				element = elements.get(i);
				element = element.findElement(By.tagName("input"));
				if (element.getAttribute("value").equals(premium)) {
					elements.get(i).click();
					helper.waitForCSSElement("#loadingArea");
					// 체크되지 않은 경우에 다시 클릭
					if (elements.get(i).getAttribute("class").contains("_unChecked")) {
						logger.debug("####### 다시 클릭!!!");
						elements.get(i).click();
						helper.waitForCSSElement("#loadingArea");
					} else {
						logger.debug("######## 가입금액: " + premium + "원 선택완료!");
						result = false;
						break;
					}
				}
			}
		}
	}
}
