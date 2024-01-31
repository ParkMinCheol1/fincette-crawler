package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * (무)종신보험
 */

public class KLP_WLF_D003 extends CrawlingKLP {

	public static void main(String[] args) {
		executeCommand(new KLP_WLF_D003(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		crawlFromHomepage(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setBrowserType(BrowserType.Chrome);
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

			//흡연여부
			logger.info("흡연여부 선택");
			driver.findElement(By.cssSelector("#frmTopInfo > div.inner_box > ul > li.box_smoke_none > div.form_calc.type2 > span:nth-child(1)")).click();
			logger.info(driver.findElement(By.cssSelector("#frmTopInfo > div.inner_box > ul > li.box_smoke_none > div.form_calc.type2 > span:nth-child(1)")).getText().trim());

			// 보험료 확인/가입
			logger.info("보험료 확인/가입 설정");
			setConfirmPremium(By.id("fastPayCalc"));

			//보험가입기간 세팅
			logger.info("보험가입기간 세팅을 확인해야 함.");

			// 보험종류
			logger.info("보험종류 설정");
			setAmountInsuredTerm(info.assureMoney, info.age);

			logger.info("값 확인 : "+driver.findElement(By.cssSelector("#prodGubunArea > span:nth-child(1)")).getText().trim());

				if(info.textType.equals("일반형")){
					driver.findElement(By.cssSelector("#prodGubunArea > span.rdo_m.l._enabled._checked")).click();
				}

				else if(info.textType.equals("체감형")){
					driver.findElement(By.cssSelector("#prodGubunArea > span.rdo_m.r._enabled._unChecked > label")).click();
				}

			// 납입기간
			logger.info("납입기간 설정");
			updateSetNapTerm(By.id("insuTermArea"), info.napTerm);

			/*elements = driver.findElements(By.cssSelector("#insuTermArea > span"));

			for(int i=0; i<elements.size(); i++){
				if(info.napTerm.contains(elements.get(i).getText().trim())){
					logger.info("납입기간 확인 : "+elements.get(i).getText().trim());
					elements.get(i).click();
				}
			}*/

			// 결과 확인하기
			logger.info("결과 확인하기");
			confirmResult();

			// 보험료
			logger.info("보험료 확인 : "+driver.findElement(By.cssSelector("#preferredArea_N > div.area_r > span.txt_2 > span.digitFlow")).getText().trim());
			logger.info("보험료 조회");
			getPremium("#preferredArea_N > div.area_r > span.txt_2 > span.digitFlow", info);

			logger.info("해약환급금(예시표)");
			getReturns("cancel1", info);

	}


	protected void setAmountInsuredTerm(String value, String age) throws Exception {
		value = String.valueOf(Integer.parseInt(value) / 10000);
		int value1 = (Integer.parseInt(value) / 1000) / 10; // 억원
		int value2 = (Integer.parseInt(value) / 1000) % 10; // 천원

		WebElement menu = driver.findElement(By.className("list_sel"));

		Actions build = new Actions(driver); // heare you state ActionBuider
		build.moveToElement(menu).build().perform(); // Here you perform hover mouse over the needed elemnt to triger
		// the visibility of the hidden
		WaitUtil.loading(2);
		logger.debug("step B");
		if (value1 != 0) {
			// 억원
			// 억원 세팅
			element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(0);
			element.click();
			elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));
			for (WebElement li : elements) {
				// 보험가입 금액 선택하면 해당
				if ((value1 + "억").equals(li.findElement(By.tagName("span")).getText().trim())) {
					li.click();
					break;
				}
			}

			// 로딩 대기
			helper.waitForCSSElement("#loadingArea");
			// 천만원 세팅
			// 계산했을 때 보험가입금액 부분
			if (value2 != 0) {
				element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(1);
				element.click();
				elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));
				for (WebElement li : elements) {

					if ((value2 + "천만원").equals(li.findElement(By.tagName("span")).getText().trim())) {
						li.click();
						break;
					}
				}
			} else {
				element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(1);
				element.click();
				element = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li")).get(0);
				element.click();
			}
		} else {

			// 천만원
			// 천만원대 먼저 세팅
			element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(1);

			element.click();
			WaitUtil.loading(2);
			elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));
			int loopSize = 0;

			for (WebElement li : elements) {
				if ((value2 + "천만원").equals(li.findElement(By.tagName("span")).getText().trim())) {
					li.click();

					//천만원이 선택되어야 하는 상황에 억단위가 기본 단위로 선택되면 경고창이 2번뜨고 친만원대 값을 적용하려면 필요함.
					if (isAlertShowed()) {
						Alert alert = driver.switchTo().alert();
						String alertText = alert.getText();
						logger.info("alertText :: " + alertText);
						alert.accept();
						WaitUtil.waitFor(2);

						driver.findElement(By.cssSelector(
								"#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_7 > ul > li:nth-child(1) > span"))
							.click();
						WaitUtil.waitFor(1);
						driver.findElement(By.cssSelector(
							"body > div._sel_option.sel_m > ul > li:nth-child(1)")).click();

						Alert alert2 = driver.switchTo().alert();
						String alertText2 = alert.getText();
						logger.info("alertText :: " + alertText2);
						alert2.accept();

						WaitUtil.waitFor(2);
						driver.findElement(By.cssSelector(
								"#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_7 > ul > li:nth-child(2)"))
							.click();
						WaitUtil.waitFor(2);
						elements = driver.findElement(By.className("_sel_option"))
							.findElements(By.tagName("li"));
						elements.get(loopSize).click();
						break;
						//알럿창이 뜨지 않고 천만단위로 선택할 경우
					}else{
						driver.findElement(By.cssSelector(
								"#frmSelfInfo > ul > li:nth-child(1) > div.box_middle > ul > li:nth-child(1) > span"))
							.click();
						WaitUtil.waitFor(1);
						driver.findElement(By.cssSelector(
							"body > div._sel_option.sel_m > ul > li:nth-child(1) > span")).click();

						WaitUtil.waitFor(2);
						driver.findElement(By.cssSelector(
								"#frmSelfInfo > ul > li:nth-child(1) > div.box_middle > ul > li:nth-child(2) > span"))
							.click();
						WaitUtil.waitFor(2);
						//body > div._sel_option.sel_m > ul > li:nth-child(2) > span
						elements = driver.findElement(By.className("_sel_option"))
							.findElements(By.tagName("li"));
						elements.get(loopSize).click();
						break;
					}
				}
				loopSize++;
			}
			// 알럿이 있는지 확인해서 있으면 Exception 처리를 해야한다.
            /*if (isAlertShowed()) {
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                logger.info("alertText :: " + alertText);
                alert.accept();
                throw new Exception(alertText);
            }*/

			// 로딩 대기
			helper.waitForCSSElement("#loadingArea");
			WaitUtil.loading(2);
			// 억원대 초기화
			element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(0);
			element.click();
			WaitUtil.loading(2);
			element = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li")).get(0);
			element.click();
		}
		// 로딩 대기

		helper.waitForCSSElement("#loadingArea");
	}


}
