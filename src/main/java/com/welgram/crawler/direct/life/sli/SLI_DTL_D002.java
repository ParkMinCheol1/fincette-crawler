package com.welgram.crawler.direct.life.sli;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.CrawlingSite;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class SLI_DTL_D002 extends CrawlingSLIDirect {

	public static void main(String[] args) {
		executeCommand(new SLI_DTL_D002(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) {
		option.setImageLoad(false);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
//		공시실 크롤링은 숨김 처리
//		doCrawlingPublic(info);  	// 공시실 크롤링

		WebElement $button = null;
		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.loading(2);

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("성별");
		setGender(info.getGender());

		logger.info("내 보험료 확인 버튼 선택");
		$button = driver.findElement(By.id("calculate"));
		click($button);

		logger.info("선택할 플랜 number");
		int planNum = getPlanNum(info);

		logger.info("플랜 선택");
		By location = By.id("result" + planNum);
		setPlan(info, location);

		logger.info("가입금액 선택");
		location = By.id("contAmt");
		setSelectBoxAssureMoney(info, location);

		logger.info("다시계산 버튼 클릭");
		location = By.id("reCalc");
		reCalculate(location);

		logger.info("보험료 크롤링");
		location = By.id("premium" + planNum);
		crawlPremium(info, location);

		logger.info("해약환급금 버튼 클릭");
		$a = driver.findElement(By.xpath("//a[text()='보장내용/해약환급금']"));
		click($a);

		logger.info("해약환급금 스크랩");
		crawlReturnMoneyList2(info, planNum);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}

	@Override
	protected int getPlanNum(CrawlingProduct info) {
		WebElement planEl = helper.waitVisibilityOfElementLocated(By.xpath("//a[contains(.,'" + info.textType + "')]"));

		String planInputId = planEl.getAttribute("id");

		// 해약환급금 버튼과 테이블 요소에 쓰일 번호 추출
		int planNum = Integer.parseInt(planInputId.replaceAll("\\D", ""));

		logger.info("플랜 num : " + planNum);

		return planNum;
	}

	@Override
	public void setPlan(CrawlingProduct info, By location) throws CommonCrawlerException {
		String expectedPlan = info.textType;

		String title = "플랜";
		WebElement $span = null;
		String actualPlan = "";

		try {
			$span = driver.findElement(location);
			click($span);

			WebElement planEl = helper.waitVisibilityOfElementLocated(
				By.xpath("//a[contains(.,'" + info.textType + "')]"));

			if(planEl.getText().contains(expectedPlan)){
				actualPlan = expectedPlan;
			}
			//비교
			super.printLogAndCompare(title, expectedPlan, actualPlan);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
			throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}
	}

//	private void doCrawlingPublic(CrawlingProduct info) throws Exception {
//		logger.info("공시실열기");
//		openAnnouncePageNew(info);
//
//		String tt = driver.findElement(By.cssSelector(".modal-tit")).getText();
//		logger.info("tt :: " + tt);
//
//		logger.info("생년월일 세팅");
//		setBirthNew(info);
//
//		logger.info("성별");
//		setGenderNew(By.name("sxdsCd0"), info.gender);
//
//		logger.info("납입주기선택");
//		logger.info("월납 고정");
//
//		logger.info("다음 클릭 !!");
//		driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
//		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
//
//		// 가입금액 담보선택
//
//		for (CrawlingTreaty item : info.treatyList) {
//			if (item.productGubun.equals(ProductGubun.주계약)){
//				logger.info(item.productGubun.toString());
//				setMainTreatyNew(info, item);
//			}else{
//				logger.info(item.productGubun.toString());
//				setSubTreatyNew(info, item);
//			}
//
//		}
//		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
//		WaitUtil.mSecLoading(300);
//		eventFiringWebDriver.executeScript("document.querySelector(\"div[class='section-main section-disclosure section-insurance-calculate']\").parentNode.scrollTop = 900");
//
//
//		logger.info("보험료계산");
//		driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
//		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
//
//		logger.info("합계 보험료 가져오기");
//		element = driver.findElement(By.cssSelector("ul[class='prd-amount-group']"));
//		element = element.findElement(By.cssSelector("li:nth-child(1) > div.amount-desc"));
//		String premium = element.getText().replaceAll("[^0-9]", "");
//		logger.info("#월보험료: " + premium);
//		info.treatyList.get(0).monthlyPremium = premium;
//
//		logger.info("스크린샷 찍기");
//		takeScreenShot(info);
//
//		logger.info("해약환급금 탭 클릭 ");
//		driver.findElement(By.linkText("해약환급금 예시")).click();
//		WaitUtil.loading(2);
//
//		getReturnMoneyNew(info, By.cssSelector(""));
//		logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
//	}

}
