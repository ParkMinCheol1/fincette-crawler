package com.welgram.crawler.direct.life.sli;


import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_WLF_F068 extends CrawlingSLIAnnounce {

	public static void main(String[] args) {
		executeCommand(new SLI_WLF_F068(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) {
		option.setImageLoad(false);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $button = null;
		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.loading(2);

		logger.info("공시실 상품 찾기");
		findProduct(info);

		logger.info("생년월일 세팅");
		setBirthday(info.getFullBirth());

		logger.info("성별");
		setGender(info.getGender());

		logger.info("보험종류 :: {}", info.textType);
		By location = By.id("hptsLineCd");
		setPlan(info, location, info.textType);

		logger.info("다음 버튼 선택");
		$button = driver.findElement(By.xpath("//button[contains(.,'다음')]"));
		click($button);

		logger.info("주계약 설정");
		setMainTreaty(info);

		logger.info("선택 특약 설정");
		setSubTreaties(info);

		logger.info("보험료 계산 버튼 클릭");
		$button = driver.findElement(By.xpath("//button[@class='btn primary secondary round']"));
		moveToElement($button);
		click($button);

		logger.info("보험료 크롤링");
		location = By.xpath("//span[@class='price']");
		crawlPremium(info, location);

		logger.info("해약환급금 버튼 클릭");
		$a = driver.findElement(By.xpath("//a[contains(.,'해약환급금 예시')]"));
		click($a);

		logger.info("해약환급금 크롤링");
		location = By.xpath("//div[@class='component-wrap next-content']//tbody//tr");
		crawlReturnMoneyList(info, location);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}

	@Override
	public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
		CrawlingProduct info = (CrawlingProduct) obj[0];
		List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

		try {
			logger.info("해약환급금 예시 버튼 클릭");
			WebElement $button = driver.findElement(By.xpath("//a[normalize-space()='해약환급금 예시']"));
			helper.moveToElementByJavascriptExecutor($button);
			click($button);

			String script = "return $('tbody:visible')[0]";
			WebElement $tbody = (WebElement) helper.executeJavascript(script);
			List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

			for(WebElement $tr : $trList) {
				helper.moveToElementByJavascriptExecutor($tr);
				List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

				//해약환급금 정보 크롤링
				String term = $tdList.get(0).getText().trim();
				String premiumSum = $tdList.get(1).getText().trim();
				String returnMoney = $tdList.get(2).getText();
				String returnRate = $tdList.get(3).getText();

				premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
				returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

				//해약환급금 적재
				PlanReturnMoney p = new PlanReturnMoney();
				p.setTerm(term);
				p.setPremiumSum(premiumSum);
				p.setReturnMoney(returnMoney);
				p.setReturnRate(returnRate);

				planReturnMoneyList.add(p);

				logger.info("경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}"
						, term, premiumSum, returnMoney, returnRate);

				//만기환급금 세팅
				info.returnPremium = returnMoney;
			}

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
			throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}
	}
}
