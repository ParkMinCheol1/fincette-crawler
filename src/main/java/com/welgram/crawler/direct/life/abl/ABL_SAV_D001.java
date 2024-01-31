package com.welgram.crawler.direct.life.abl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;



/** 해약환급금 공시이율이 나와 있는 공시실로 크롤링 */
public class ABL_SAV_D001 extends CrawlingABLAnnounce {

	public static void main(String[] args) {
		executeCommand(new ABL_SAV_D001(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// 공시실
		openAnnouncePage(info);

		// 성별
		setGender("sxdsCd1", info.getGender());

		// 생년월일
		setBirthday(By.id("insrdSno_jupiDate1"), info.getFullBirth());

		// 계약관계정보 적용
		doClickButton(By.id("applyContRltnInfo"));

		// 월납입보험료
		setMonthlyPremium(info.getAssureMoney());

		// 보험료 계산
		calculation("calcPremium");

		// 공시실 스크롤 내리기
		logger.info("스크롤 내리기");
		discusroomscrollbottom();

		// 스크린샷 추가
		logger.info("스크린샷");
		takeScreenShot(info);

		// 보험료
		crawlPremium("prdPrm", info);

		// 해약환급금 & 연금수령액
		crawlReturnMoneyList(info);

		return true;
	}



	// 공시실 - 해약환급금
	@Override
	public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

		CrawlingProduct info = (CrawlingProduct) obj[0];

		try {
			element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnEntplRprtPbl")));
			element.click();
			WaitUtil.loading(3);

			Set<String> windowId = driver.getWindowHandles();
			Iterator<String> handles = windowId.iterator();

			String currentHandle = driver.getWindowHandle();
			String nextHandle = null;

			while (handles.hasNext()) {
				nextHandle = handles.next();
				WaitUtil.loading(2);
			}

			driver.switchTo().window(nextHandle);

			element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tabRefnd")));
			element.click();
			helper.waitForCSSElement(".state-load-data");

			int num = 0;
			String text = "";

			if (info.productCode.equals("ABL00099")) {
				text = "해약환급금(투자수익률3.75%)";
			} else {
				text = "해약환급금(공시이율)";
			}

			WaitUtil.loading(3);
			element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("refndTab")));
			element = element.findElement(By.tagName("table")).findElement(By.tagName("tbody"));
			elements = element.findElements(By.tagName("tr"));

			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
			for (WebElement tr : elements) {
				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				String term = tr.findElements(By.tagName("td")).get(0).getText();
				String premiumSum = tr.findElements(By.tagName("td")).get(2).getText();
				String returnMoneyMin = tr.findElements(By.tagName("td")).get(3).getText();
				String returnRateMin = tr.findElements(By.tagName("td")).get(4).getText();
				String returnMoneyAvg = tr.findElements(By.tagName("td")).get(5).getText();
				String returnRateAvg = tr.findElements(By.tagName("td")).get(6).getText();
				String returnMoney = tr.findElements(By.tagName("td")).get(7).getText();
				String returnRate = tr.findElements(By.tagName("td")).get(8).getText();

				logger.info(term + " :: " + premiumSum );
				logger.info("환급률" + " :: " + returnRate );
				logger.info("해약환급금" + " :: " + returnMoney );
				logger.info("=========================");

				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoneyMin(returnMoneyMin);
				planReturnMoney.setReturnRateMin(returnRateMin);
				planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
				planReturnMoney.setReturnRateAvg(returnRateAvg);
				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);
				planReturnMoneyList.add(planReturnMoney);

				// 계산테이블 해약환급금 필드에 값 추가
				info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
			}

			info.setPlanReturnMoneyList(planReturnMoneyList);

			info.savePremium = "0"; // 적립보험료
			// info.treatyList.get(0).monthlyPremium = "0"; // 납입보험료
			info.errorMsg = "";

			driver.close();
			driver.switchTo().window(currentHandle);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
			throw new ReturnMoneyListCrawlerException(e.getCause(),exceptionEnum.getMsg());
		}
	}
}
