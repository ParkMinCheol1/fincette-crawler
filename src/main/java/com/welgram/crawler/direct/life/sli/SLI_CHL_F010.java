package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_CHL_F010 extends CrawlingSLIAnnounce {

	public static void main(String[] args) {
		executeCommand(new SLI_CHL_F010(), args);
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

		logger.info("보험종류 :: {}", info.getTextType());
		By location = By.id("hptsLineCd");
		setPlan(info, location, info.getTextType());

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
		crawlReturnMoneyList1(info, location);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}
}
