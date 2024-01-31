package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.common.except.CannotBeSelectedException;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * (무)꿈꾸는e저축보험Ⅱ
 */

public class KLP_SAV_D001 extends CrawlingKLP {



	public static void main(String[] args) {
		executeCommand(new KLP_SAV_D001(), args);
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

			driver.manage().window().maximize();

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

			//보험기간
			logger.info("보험기간 설정");
			setNapTerm4(By.className("box_rdo"), info.insTerm);

			// 납입기간
			try {
				logger.info("납입기간 설정");
				setNapTerm4(By.id("insuTermContents"), info.napTerm);
			}catch(CannotBeSelectedException e) {
				throw new CannotBeSelectedException(e.getMessage());
			}

			// 월보험료
			logger.info("월보험료");
			helper.sendKeys1_check(By.id("monthlyPay"), String.valueOf(Integer.parseInt(info.assureMoney) / 10000));

			// 결과 확인하기
			logger.info("결과확인하기");
			confirmResult();

			WaitUtil.waitFor(4);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);
			WaitUtil.waitFor(1);

			info.treatyList.get(0).monthlyPremium = info.assureMoney;

			logger.info("해약환급금(예시표)");
			getReturns("cancel1", info);

	}


	protected void getReturns(String id, CrawlingProduct info) throws Exception {
		WaitUtil.loading(2);
		helper.waitForCSSElement("#loadingArea");

		WaitUtil.loading(2);
		element = driver.findElement(By.id(id));
		element.sendKeys(Keys.ENTER);
		element.click();
		WaitUtil.loading(1);

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;
		while (handles.hasNext()) {
			subHandle = handles.next();
			logger.debug(subHandle);
			WaitUtil.loading(1);
		}
		driver.switchTo().window(subHandle);
		elements = driver.findElements(By.cssSelector("#listArea > tr"));

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		if (elements.size() > 0) {

			for (int i = 0; i < elements.size(); i++) {

				WebElement tr = elements.get(i);
				List<WebElement> tdList = tr.findElements(By.tagName("td"));

				logger.info("______해약환급급[{}]_______ ", i);

				String term = tdList.get(0).getAttribute("innerText"); // 경과기간
				String premiumSum = tdList.get(1).getAttribute("innerText"); // 납입보험료
				String minReturnMoney = tdList.get(2).getAttribute("innerText"); // 최저해약환급금
				String minReturnRate = tdList.get(3).getAttribute("innerText"); // 최저환급률
				String avgReturnMoney = tdList.get(4).getAttribute("innerText"); // 평균해약환급금
				String avgReturnRate = tdList.get(5).getAttribute("innerText"); // 평균환급률
				String returnMoney = tdList.get(6).getAttribute("innerText"); // 현재해약환급금
				String returnRate = tdList.get(7).getAttribute("innerText"); // 현재환급률

				logger.info("|--경과기간: {}", term);
				logger.info("|--납입보험료: {}", premiumSum);
				logger.info("|--해약환급금: {}", returnMoney);
				logger.info("|--환급률: {}", returnRate);
				logger.info("|_______________________");

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender(info.getGenderEnum().name());
				planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

				planReturnMoney.setTerm(term); // 경과기간
				planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계

				planReturnMoney.setReturnMoneyMin(minReturnMoney); // 최저해약환급금
				planReturnMoney.setReturnRateMin(minReturnRate); // 최저해약환급률

				planReturnMoney.setReturnMoneyAvg(avgReturnMoney); // 평균해약환급금
				planReturnMoney.setReturnRateAvg(avgReturnRate); // 평균해약환급률

				planReturnMoney.setReturnMoney(returnMoney); // 환급금
				planReturnMoney.setReturnRate(returnRate); // 환급률


				planReturnMoneyList.add(planReturnMoney);

				info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
			}

			info.setPlanReturnMoneyList(planReturnMoneyList);

		} else {

			logger.info("해약환급금 내역이 없습니다.");
		}
	}

}
