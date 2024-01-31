package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_SAV_D001 extends CrawlingSLIDirect {

	public static void main(String[] args) {
		executeCommand(new SLI_SAV_D001(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) {
		option.setImageLoad(false);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
//      공시실 크롤링은 숨김 처리
//		doCrawlInsurancePublic(info);

		WebElement $button = null;
		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.loading(2);

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("성별");
		setGender(info.getGender());

		logger.info("내 수령액 확인 버튼 선택");
		$button = driver.findElement(By.id("calculate"));
		click($button);

		logger.info("보험 기간 선택");
		By location = By.id("insTermBox");
		setInsTermRadioButton(info.insTerm, location);

		logger.info("납입 기간 선택");
		location = By.id("napTerm");
		setNapTerm(info.napTerm + "납", location);

		logger.info("납입금액 입력");
		location = By.id("napMoney");
		setInputAssureMoney(info, location);

		logger.info("주계약 보험료 세팅");
		info.treatyList.get(0).monthlyPremium = info.assureMoney;

		logger.info("다시계산 버튼 클릭");
		location = By.id("reCalc");
		reCalculate(location);

		logger.info("알럿 확인");
		alert();

		logger.info("해약환급금 버튼 클릭");
		$a = driver.findElement(By.xpath("//a[text()='보장내용/해약환급금']"));
		click($a);

		logger.info("해약환급금 크롤링");
		crawlReturnMoneyList3(info);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}


//	private void doCrawlInsurancePublic(CrawlingProduct info) throws Exception {
//
//		logger.info("공시실열기");
//		openAnnouncePageNew(info);
//
//		logger.info("생년월일 세팅");
//		setBirthNew(info);
//
//		logger.info("성별");
//		setGenderNew(By.name("sxdsCd0"), info.gender);
//
//		logger.info("다음 클릭 !!");
//		driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
//		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
//
//		logger.info("가입조건 :: 다음 클릭 !!");
//		driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
//		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
//
//		logger.info("보험료세팅");
//		for (CrawlingTreaty item : info.treatyList) {
//			if (item.productGubun.equals(ProductGubun.주계약)){
//				setMainTreatyNew(info, item);
//			}
//		}
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
//		WaitUtil.loading(1);
//
//		getReturnMoneyNewEx(info, By.cssSelector(""));
//		logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
//
//	}
//
//	protected void getReturnMoneyNewEx(CrawlingProduct info, By by) throws Exception {
//
//		logger.info("해약환급금 테이블선택");
//		elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#content2 > div.component-wrap.next-content > div > div > table > tbody > tr")));
//
//		// 주보험 영역 Tr 개수만큼 loop
//		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
//		int scrollTop=0;
//		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
//		for (WebElement tr : elements) {
//			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//			String term = tr.findElements(By.tagName("td")).get(0).getText();
//			String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
//			String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText();
//			String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
//			String returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText();
//			String returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();
//			String returnMoney = tr.findElements(By.tagName("td")).get(6).getText();
//			String returnRate = tr.findElements(By.tagName("td")).get(7).getText();
//			logger.info(term + " :: " + premiumSum );
//
//			planReturnMoney.setTerm(term);
//			planReturnMoney.setPremiumSum(premiumSum);
//
//			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
//			planReturnMoney.setReturnRateMin(returnRateMin);
//			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
//			planReturnMoney.setReturnRateAvg(returnRateAvg);
//			planReturnMoney.setReturnMoney(returnMoney);
//			planReturnMoney.setReturnRate(returnRate);
//
//			planReturnMoneyList.add(planReturnMoney);
//
//
//			info.returnPremium = returnMoney.replace(",", "").replace("원", "");
//			logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);
//
//			scrollTop += 65;
//			WaitUtil.mSecLoading(300);
//			eventFiringWebDriver.executeScript("document.querySelector(\"div[class='section-main section-disclosure section-insurance-calculate']\").parentNode.scrollTop = " + scrollTop );
//		}
//
//		info.setPlanReturnMoneyList(planReturnMoneyList);
//		// 해약환급금 관련 End
//	}

}
