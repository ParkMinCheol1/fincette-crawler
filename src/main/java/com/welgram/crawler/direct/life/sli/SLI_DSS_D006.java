package com.welgram.crawler.direct.life.sli;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.CrawlingSite;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.util.ArrayList;
import java.util.List;

public class SLI_DSS_D006 extends CrawlingSLI {

	public static void main(String[] args) {
		executeCommand(new SLI_DSS_D006(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) {
		option.setImageLoad(false);
	}

	@Override
	protected boolean preValidation(CrawlingProduct info) {
		boolean result = true;
		try {
			info.setCrawlingSite(CrawlingSite.사용자웹); // 추후 명령어 방식으로 변경 필요
			crawlUrl = info.getSiteWebUrl();
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		if (info.getCrawlingSite() == CrawlingProduct.CrawlingSite.공시실){
			logger.info("공시실 크롤링");
			doCrawlingPublic(info);  	// 공시실 크롤링
		}else{
			logger.info("사용자웹 크롤링");
			doCrawlingFrontWeb(info);	// 사용자웹 크롤링
		}
		return true;
	}

	private void doCrawlingPublic(CrawlingProduct info) throws Exception{

		logger.info("공시실열기");
		openAnnouncePageNew(info);

		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");
		logger.info("생년월일 세팅");
		setBirthNew(info);

		logger.info("성별");
		setGenderNew(By.name("sxdsCd0"), info.gender);

		logger.info("직업 세팅");
		setJobNew();

		logger.info("납입주기선택");
		logger.info("월납 고정");

		logger.info("다음 클릭 !!");
		driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");

		// 가입금액 담보선택

		for (CrawlingTreaty item : info.treatyList) {
			if (item.productGubun.equals(ProductGubun.주계약)){
				logger.info(item.productGubun.toString());
				setMainTreatyNew(info, item);
			}else{
				logger.info(item.productGubun.toString());
				setSubTreatyNew(info, item);
			}

		}
		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
		WaitUtil.mSecLoading(300);
		eventFiringWebDriver.executeScript("document.querySelector(\"div[class='section-main section-disclosure section-insurance-calculate']\").parentNode.scrollTop = 2600");

		logger.info("보험료계산");
		driver.findElement(By.cssSelector("button[class='btn primary secondary round']")).click();
		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");

		logger.info("합계 보험료 가져오기");
		element = driver.findElement(By.cssSelector("ul[class='prd-amount-group']"));
		element = element.findElement(By.cssSelector("li:nth-child(1) > div.amount-desc"));
		String premium = element.getText().replaceAll("[^0-9]", "");
		logger.info("#월보험료: " + premium);
		info.treatyList.get(0).monthlyPremium = premium;

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		logger.info("해약환급금 탭 클릭 ");
		driver.findElement(By.linkText("해약환급금 예시")).click();
		WaitUtil.loading(2);

		getReturnMoneyNew(info, By.cssSelector(""));
		logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));

	}

	private void doCrawlingFrontWeb(CrawlingProduct info) throws Exception{
		logger.info("생년월일 세팅");
		WaitUtil.loading(1);

		element = driver.findElement(By.id("birthday"));
		element.sendKeys(info.getFullBirth());

		logger.info("성별");
		WaitUtil.loading(1);
		String genderCss = "";
		if (info.getGender() == MALE ){
			genderCss = "#proCalculatorArea1 > div.label-check1 > span:nth-child(1) > label";
		}else{
			genderCss = "#proCalculatorArea1 > div.label-check1 > span:nth-child(2) > label";
		}
		driver.findElement(By.cssSelector(genderCss)).click();

		logger.info("보험료 계산버튼 클릭");
		WaitUtil.loading(1);
		driver.findElement(By.id("calculate")).click();

		try{
			logger.info("알럿창");
			driver.findElement(By.cssSelector("#uiPopDiyCalcMode > div.area-action > button.btn-action.ui-close")).click();
		}catch (Exception e){
			logger.info("Exception!!");
		}

		helper.waitForCSSElement("#uiPOPLoading1");


		logger.info("가입금액 설정");
		WaitUtil.waitFor(1);

		// 사이트 특약을 돌리면서 확인해야할듯..
		setSubTreaty(info);

		logger.info("보험료계산");
		driver.findElement(By.cssSelector("#reCalc")).click();
		helper.waitForCSSElement("#uiPOPLoading1");

		logger.info("합계 보험료 가져오기");
		element = driver.findElement(By.cssSelector("#premium"));
		String premium = element.getText().replaceAll("[^0-9]", "");
		logger.info("#월보험료: " + premium);
		info.treatyList.get(0).monthlyPremium = premium;

		logger.info("스크린샷 찍기");
		takeScreenShot(info);


		logger.info("해약환급금 클릭 ");

		driver.findElement(By.cssSelector("#mCSB_2_container > div.wrap-planresult3.result-diy > div > a")).click();

		logger.info("해약환급금 탭클릭");
		WaitUtil.loading(2);
		driver.findElement(By.cssSelector("#uiPOPProInfo > ul > li:nth-child(2) > a")).click();

		logger.info("전체기간 펼치기");
		WaitUtil.loading(1);

		element = driver.findElement(By.cssSelector("#uiTabProInfo2 > div > div > button:nth-child(1)"));
		JavascriptExecutor js2 = (JavascriptExecutor) driver;
		js2.executeScript("arguments[0].click();", element);


		logger.info("해약환급금 가져오기");
		WaitUtil.loading(1);

		logger.info("해약환급금 테이블선택");
		elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#pReturnCancel > tr")));


		// 주보험 영역 Tr 개수만큼 loop
		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		int scrollTop=0;
		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);

		for (WebElement tr : elements) {
			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			String term = tr.findElements(By.tagName("td")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
			String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
			String returnRate = tr.findElements(By.tagName("td")).get(3).getText();
			logger.info(term + " :: " + premiumSum );

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoneyList.add(planReturnMoney);

			// 기본 해약환급금 세팅
			info.returnPremium = returnMoney.replace(",", "").replace("원", "");
			logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);

			scrollTop += 50;
			eventFiringWebDriver.executeScript("document.querySelector('#mCSB_4').scrollTop = " + scrollTop );
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

	}

	protected void setSubTreaty(CrawlingProduct info) throws Exception {

		element = helper.waitPresenceOfElementLocated(By.cssSelector("#uiTabCalculation1"));
		elements = element.findElements(By.cssSelector("div > div > table > tbody > tr"));

		// 주보험 영역 Tr 개수만큼 loop
		int scrollTop=0;
		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);

		WaitUtil.mSecLoading(800);

		// 여기는 사이트 특약 Loop
		int i=0;
		for (WebElement tr : elements) {
			String prdtNm = "";

			if (i == 0){
				prdtNm = tr.findElements(By.tagName("td")).get(0).getText().replace("\n"," ");
			}else{
				prdtNm = tr.findElements(By.tagName("td")).get(0).findElement(By.tagName("label")).getText().replace("\n"," ");
			}

			logger.info("사이트 특약 :: " + prdtNm);

			if (prdtNm.isEmpty()){
				logger.info("사이트 특약 못찾음 !!!!!");
				continue;
			}
			// 담보명과 이름이 같은지 확인
			boolean isFindTreaty = false;

			// 설계 마스타 특약 Loop
			for (CrawlingTreaty item : info.treatyList) {
				logger.info("설계마스타 담보명 :::::::::: " + item.treatyName);

				if (info.gender == MALE){
					if (item.getGenderType().equals("F")){ logger.info("남자인경우는 여자특약 Skip !!!! ");	break;	}
				}else{
					if (item.getGenderType().equals("M")){ logger.info("여자인경우는 남자특약 Skip !!!! ");	break;	}
				}

				if (item.treatyName.trim().equals(prdtNm.trim())){
					// 같으면 보기, 납기, 가입금액을 셋한다.
					isFindTreaty = true;
					logger.info("[ "+ prdtNm +" ] "+ "설계한 특약이 있습니다.");
					try{
						element = tr.findElements(By.tagName("td")).get(0).findElement(By.cssSelector("input[type='checkbox']"));
						logger.info("체크박스가 있는경우 확인후 선택");
						if (!element.isSelected()) {
							element.click();
							logger.info(item.treatyName + " 특약 click!");
						}
					}catch (Exception e){
						logger.info("체크박스가 없습니다.");
					}

					logger.info("가입금액을 선택합니다.");
					try{
						Select selectMoney = new Select(tr.findElements(By.tagName("td")).get(1).findElement(By.tagName("select")));
						selectMoney.selectByIndex(0);
						selectMoney.selectByValue(item.assureMoney+"");

						WaitUtil.mSecLoading(100);



						element = tr.findElements(By.tagName("td")).get(2);
						element.click();
						String amt = element.getText();
						logger.info("amt :: " + amt);

					}catch (Exception e){
						logger.info("선택할 셀렉트박스가 없어요");
					}
				}

				if (isFindTreaty) {
					logger.info("특약 찾음 !");
					break;
				}
			}



			if (!isFindTreaty){
				logger.info("설계마스타 특약을 찾을수 없습니다...");

				try{
					element = tr.findElements(By.tagName("td")).get(0).findElement(By.cssSelector("input[type='checkbox']"));
					logger.info("체크박스가 있는경우 확인후 선택");
					if (element.isSelected()) {
						element = element.findElement(By.xpath("parent::*"));
						element = element.findElement(By.tagName("label"));
						element.click();

						logger.info("특약 선택 해제 click!!!");
					}
					element = tr.findElements(By.tagName("td")).get(2);
					element.click();

				}catch (Exception e){
					logger.info("체크박스가 없습니다.");
				}

			}

			scrollTop = element.getLocation().getY();
			logger.info(scrollTop + "" );
			i++;


			if (i % 4 == 0 ){
				try {
					eventFiringWebDriver.executeScript("document.querySelector(\"div[id='mCSB_2']\").scrollTop = " + (scrollTop-100) );
				}catch (Exception e){
					logger.info("스크롤이 없는경우 Skip .. ");
				}
			}

			WaitUtil.mSecLoading(100);

		}
	}

}
