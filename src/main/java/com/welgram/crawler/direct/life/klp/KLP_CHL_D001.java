package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 교보라이프플래닛 - (무)라이프플래닛e플러스어린이보험(어린이)
 */
public class KLP_CHL_D001 extends CrawlingKLP {



	public static void main(String[] args) {
		executeCommand(new KLP_CHL_D001(), args);
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


			logger.info("브레이크 포인트");
			// ((JavascriptExecutor) driver).executeScript("scroll(0,300);");
			helper.oppositionWaitForLoading("plnnrBrdt");

			// 부모나이
			parentBirth();

			// 부모성별
			parentGender();

			// 자녀 - 어린이/태아 중 선택
			childSet(info.productCode);

			// 자녀 생년월일
			logger.info(info.fullBirth);
			helper.sendKeys1_check(By.id("childBrdt"), info.fullBirth);

			// 자녀성별
			childGender(info.gender);

			// 보험료 확인/가입
			setConfirmPremium(By.id("btnCalculMyInsuPay"));

			// 가입금액
			childPremium(String.valueOf(Integer.parseInt(info.assureMoney) / 10000));

			// 자녀 보험기간
			childInsTerm(info.insTerm);

			// 자녀 납입기간
			childNapTerm(info.napCycle, info.napTerm);

			// 환급률
			maturityReturnPerCent(info.insuName);

			// 결과 확인하기
			confirmResult();

			// 보험료
			getPremium("#premiumLabel2", info);

			// 해약환급금
			//getReturnPremium("#cancel1", info);

			// 해약환급금(예시표)
			getReturns("cancel1", info);

	}



	protected void childPremium(String premium) throws Exception {

		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
			driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1)")));
		Thread.sleep(500);

		String getPremium;


		new Actions(driver).moveToElement(driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1)"))).perform();

		element = wait.until(ExpectedConditions.elementToBeClickable(
			By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_3 > div.box_sel > span")));

		WaitUtil.waitFor("childPremium");
		element.click();

		element = driver.findElement(By.cssSelector("._sel_option.sel_m"));

		elements = element.findElements(By.tagName("li"));
		int premiumSum = Integer.parseInt(premium);
		logger.info("가입금액 :: " + premiumSum);
		for (WebElement li : elements) {
			element = li.findElement(By.tagName("span"));
			getPremium = element.getText().replace(",", "").replace("만원", "");

			if (getPremium.contains("진단보험금")) {
				continue;
			}

			// 실제 가입금액
			//getPremium = String.valueOf(Integer.parseInt(getPremium) / 5);
			logger.info("getPremium :: " + getPremium);
			if (getPremium.equals(Integer.toString(premiumSum))) {
				li.click();
				WaitUtil.waitFor(2);
				break;
			}
		}
	}

}
