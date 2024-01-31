package com.welgram.crawler.direct.fire.hnf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class HNF_DSS_D001 extends CrawlingHNFMobile {

	public static void main(String[] args) {
		executeCommand(new HNF_DSS_D001(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $button = null;

		// step1. 사용자 정보 입력
		logger.info("가입하기 버튼 클릭");
		$button = driver.findElement(By.id("btnJoin"));
		click($button);

		// step2. 가입조건 비교
		logger.info("가입조건 설정");
		setJoinCondition(info);

		logger.info("보험가입금액 선택");
		setAssureMoney(info.getAssureMoney());

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("스크린샷 찍기");
		helper.executeJavascript("window.scrollTo(0,0);");
		takeScreenShot(info);

		return true;

	}



	public void setJoinCondition(CrawlingProduct info) throws Exception {

		WebElement $joinConditionP = driver.findElement(By.id("joinConTxt"));
		String joinCondition = $joinConditionP.getText().trim();

		logger.info("가입조건 : {}", joinCondition);

		String actualInsTerm = "";
		String actualNapTerm = "";
		String actualNapCycle = "";

		// 보험기간 읽어오기
		int start = joinCondition.indexOf("/");
		actualInsTerm = joinCondition.substring(0, start);
		joinCondition = joinCondition.substring(start + 1);

		// 납입기간, 납입주기 읽어오기
		actualNapTerm = joinCondition.trim();
		actualNapCycle = actualNapTerm;

		// 비교
		setInsTerm(info.getInsTerm(), actualInsTerm);
		setNapTerm(info.getNapTerm(), actualNapTerm);
		setNapCycle(info.getNapCycleName(), actualNapCycle);

	}



	@Override
	public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

		String title = "보험가입금액";

		int unit = 10000;
		String expectedAssureMoney = (String) obj[0];
		String actualAssureMoney = "";

		try {
			expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / unit);
			expectedAssureMoney = expectedAssureMoney + "만원";

			WebElement $assureMoneyDiv = driver.findElement(By.id("aInsJoinAmtSelArea"));
			WebElement $assureMoneyLabel = $assureMoneyDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedAssureMoney + "']"));

			// 성별 클릭
			clickByJavascriptExecutor($assureMoneyLabel);

			// 실제 클릭된 성별 값 읽어오기
			String script = "return $('input[name=rdoAjoinMoney]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$assureMoneyLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
			actualAssureMoney = $assureMoneyLabel.getText().trim();

			// 성별 비교
			super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
			throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
		}

	}

	@Override
	public void setInsTerm(Object... obj) throws SetInsTermException {

		String title = "보험기간";

		String expectedInsTerm = (String) obj[0];
		String actualInsTerm = (String) obj[1];

		try {
			actualInsTerm = actualInsTerm.replace("보험기간", "").trim();

			// 비교
			super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
			throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setNapTerm(Object... obj) throws SetNapTermException {

		String title = "납입기간";

		String expectedNapTerm = (String) obj[0];
		String actualNapTerm = (String) obj[1];

		try {
			actualNapTerm = actualNapTerm.trim();

			// 비교
			super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
			throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void setNapCycle(Object... obj) throws SetNapCycleException {

		String title = "납입주기";

		String expectedNapCycle = (String) obj[0];
		String actualNapCycle = (String) obj[1];

		try {
			actualNapCycle = actualNapCycle.trim();

			//비교
			super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
			throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {

		String title = "보험료 크롤링";

		CrawlingProduct info = (CrawlingProduct) obj[0];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
		ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

		try {

			// 보험료 크롤링 전에는 대기시간을 넉넉히 준다
			WaitUtil.waitFor(5);

			WebElement $premiumDiv = driver.findElement(By.id("calPremBox"));
			$premiumDiv = $premiumDiv.findElement(By.xpath(".//div[@class[contains(., 'total-price')]]"));
			String premium = $premiumDiv.getText();
			premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

			mainTreaty.monthlyPremium = premium;

			if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
				logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
				throw new PremiumCrawlerException(exceptionEnum.getMsg());
			} else {
				logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
			}

		} catch (Exception e) {
			throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}

	}

}
