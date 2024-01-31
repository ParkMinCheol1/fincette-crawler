package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.common.except.CannotBeSelectedException;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class KLP_DTL_D001 extends CrawlingKLP {

	// (무)e건강치아보험
	public static void main(String[] args) {
		executeCommand(new KLP_DTL_D001(), args);
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

			driver.manage().window().maximize();

			logger.info("담보명 확인");
			checkProductMaster(info,"#wrap > div.visual.new > div > div.area_product > section > h1");

			logger.info("생년월일");
			helper.sendKeys1_check(By.id("plnnrBrdt"), info.fullBirth);

			logger.info("성별");
			setGender(info.gender);

			logger.info("보험료 확인/가입");
			setConfirmPremium(By.id("fastPayCalc"));

			logger.info("보험기간");
			setInsTerm(null, info.insTerm);

			logger.info("납입기간");
			setNapTerm(null, info.napTerm);

			logger.info("결과 확인하기");
			confirmResult();

			logger.info("보험료");
			getPremium("//*[@id='content']/section[3]/div/div/div[1]/div/div/div/div[1]/div/div[2]/strong", info);

			logger.info("해약환급금 조회");
			getReturns("cancel1", info);

	}



	@Override
	protected void setGender(int value) throws InterruptedException {

		driver.findElement(By.xpath("//div[@id='content']/section[1]/div/div[2]/div[2]/form/div/div[4]/div/div/label")).click();
		List<WebElement> elements = driver.findElements(By.xpath("//div[@id='mCSB_3_container']//ul//li"));
		// elements에 li로 3개가 담겨있으며, li(1)이 남성, li(2)가 여성임
		elements.get(value+1).click();
		WaitUtil.waitFor(1);

	}



	@Override
	protected void setInsTerm(By by, String values) throws Exception {

		String title = "보험 기간";
		String text = "년 동안";
		String value = values.replace("년", "").replace("세", "") + text;
		String insTerm;

		// 스크롤 이동
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 1200);");

		try {
			WebElement e = driver.findElement(By.xpath("//span[text()='" + value +"']/parent::label"));
			e.click();
			insTerm = e.getText();
		} catch (Exception e) {
			throw new CannotBeSelectedException("보험기간을 선택할 수 없습니다.");
		}

		printLogAndCompare(title, value, insTerm);
		WaitUtil.waitFor(2);

	}



	@Override
	protected void setNapTerm(By by, String values) throws Exception {

		String title = "납입 기간";
		String text = "년 동안";
		String value = values.replace("년", "").replace("세", "") + text;
		String napTerm;
		try {
			WebElement e = driver.findElement(By.xpath("//div[@id='insuTermContents']//div//div//label"));
			e.click();
			napTerm = e.getText();
		} catch (Exception e) {
			throw new CannotBeSelectedException("해당 나이에서는 납입기간을 선택할수 없음 : " + value);
		}

		printLogAndCompare(title, value, napTerm);

		WaitUtil.waitFor(2);
		driver.findElement(By.id("btnExpectInsuPay")).click();
		WaitUtil.waitFor(1);

	}



	@Override
	protected void getPremium(String by, CrawlingProduct info) throws Exception {

		String premium = "";
		element = driver.findElement(By.xpath(by));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

		premium = element.getText().replaceAll("[^0-9]", "");
		logger.debug("###### 월보험료: " + premium);

		WaitUtil.waitFor(1);
		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		info.treatyList.get(0).monthlyPremium = premium;
		info.errorMsg = "";

	}



	/**
	 * 해약환급금(예시표) 조회
	 *
	 * @param info
	 * @throws InterruptedException
	 */
	protected void getReturns(String id, CrawlingProduct info) throws Exception {

		int num = 2;

		driver.findElement(By.xpath("//*[@id='content']/section[3]/div/div/div[2]/div/div[3]/div/div[2]/div/div[2]/div/div/div[2]/a")).click();
		WaitUtil.loading(2);

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;
		while (handles.hasNext()) {
			subHandle = handles.next();

			logger.debug(subHandle);
			WaitUtil.loading(2);
		}
		driver.switchTo().window(subHandle);
		elements = driver.findElements(By.xpath("//table[@id='listArea']//tbody"));

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		if (elements.size() > 0) {

			for (int i = 0; i < elements.size(); i++) {

				WebElement tbody = elements.get(i);
				logger.info("______해약환급급[{}]_______ ", i);

				String term 		= tbody.findElement(By.xpath("./tr[1]//th")).getAttribute("innerHTML"); // 경과기간
				String premiumSum 	= tbody.findElement(By.xpath("./tr[2]//th//span")).getAttribute("innerHTML").replaceAll("[^0-9]", ""); // 납입보험료
				String returnMoney  = tbody.findElement(By.xpath("./tr[1]//td//span")).getAttribute("innerHTML").replaceAll("[^0-9]", ""); // 해약환급금
				String returnRate 	= tbody.findElement(By.xpath("./tr[2]//td//span")).getAttribute("innerHTML"); // 환급률

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
				planReturnMoney.setReturnMoney(returnMoney); // 환급금
				planReturnMoney.setReturnRate(returnRate); // 환급률

				planReturnMoneyList.add(planReturnMoney);

				// todo | 확인 및 수정 필요
				// info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
			}

			info.setPlanReturnMoneyList(planReturnMoneyList);

		} else {
			logger.info("해약환급금 내역이 없습니다.");
		}

	}

}
