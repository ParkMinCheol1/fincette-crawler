package com.welgram.crawler.direct.fire.hnf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


public class HNF_ACD_D002 extends CrawlingHNFMobile {

	// 원데이 귀가안심보험(2)
	public static void main(String[] args) {
		executeCommand(new HNF_ACD_D002(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $button = null;

		// STEP 1 : 가입하기,보험료 계산,다음 버튼 선택
		$button = driver.findElement(By.id("btnJoin"));
		click($button);

		$button = driver.findElement(By.id("btnStep01Next"));
		click($button);

		//STEP 2 : 보험료 크롤링
		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("보장내용 크롤링");
		setTreaties(info.getTreatyList());

		logger.info("스크린샷 찍기");
		helper.executeJavascript("window.scrollTo(0,0);");
		takeScreenShot(info);

		return true;

	}



	@Override
	public void setUserName(Object... obj) throws SetUserNameException {

		String title = "이름 세팅";
		String expectedUserName = (String) obj[0];
		String actualUserName = "";

		try {
			WebElement $userNameInput = driver.findElement(By.id("sBossName"));
			actualUserName = helper.sendKeys4_check($userNameInput, expectedUserName);
			super.printLogAndCompare(title, expectedUserName, actualUserName);

			WebElement $button = driver.findElement(By.xpath("//*[@id=\"s\"]/div[2]/div/button"));
			helper.waitElementToBeClickable($button).click();
		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_USER_NAME;
			throw new SetUserNameException(exceptionEnum.getMsg());
		}

	}



	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {

		String title = "보험료 크롤링";
		CrawlingProduct info = (CrawlingProduct) obj[0];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약)).findFirst().get();

		try {
			WaitUtil.waitFor(5);
			WebElement $premiumDiv = driver.findElement(By.xpath("//*[@id=\"calPremBox\"]"));
			String premium = $premiumDiv.getText();
			premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

			mainTreaty.monthlyPremium = premium;

			if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
				logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
			} else {
				logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
			}

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
			throw new PremiumCrawlerException(exceptionEnum.getMsg());
		}

	}

	@Override
	public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

		String title = "보장내용";

		try {
			WebElement $treatyTbody = driver.findElement(By.xpath("//*[@id=\"wrap\"]/div[1]/div[2]/div/div[2]"));
			List<WebElement> $treatyTrList = $treatyTbody.findElements(By.xpath("//*[@id=\"coverList\"]"));

			List<String> targetTreatyNameList = new ArrayList<>();
			List<String> welgramTreatyNameList = new ArrayList<>();

			for (WebElement $treatyTr : $treatyTrList) {
				WebElement $treatyNameLi = $treatyTr.findElement(By.xpath("/html/body/div[1]/div[1]/div[2]/div/div[2]/ul"));

				WebElement $treatyNameDiv = $treatyNameLi.findElement(By.id("pCover151220"));

				helper.moveToElementByJavascriptExecutor($treatyNameDiv);
				String targetTreatyName = $treatyNameDiv.getText().trim();

				targetTreatyNameList.add(targetTreatyName);
			}

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
			throw new SetTreatyException(exceptionEnum.getMsg());
		}

	}

}