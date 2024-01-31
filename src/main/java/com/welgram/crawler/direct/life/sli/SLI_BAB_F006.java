package com.welgram.crawler.direct.life.sli;

import com.welgram.common.InsuranceUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_BAB_F006 extends CrawlingSLIAnnounce {

	public static void main(String[] args) {
		executeCommand(new SLI_BAB_F006(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) {
		option.setImageLoad(false);
	}


	@Override
	protected boolean preValidation(CrawlingProduct info) {
		boolean result = true;

		if (info.gender == MALE) {
			logger.info("남성은 가입불가합니다.");
			result = false;
		}    //남성은 가입 불가이므로 크롤링 시작 전에 예외처리

		return result;
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $button = null;
		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.loading(2);

		logger.info("공시실 상품 찾기");
		findProduct(info);

		logger.info("태아 생년월일 설정 :: 0세");
		setBirthday(InsuranceUtil.getBirthday(0));

		logger.info("임산부 나이 설정 :: {}", info.fullBirth);
		setMotherBirthday(info.fullBirth);

//		logger.info("성별");
//		setGender(info.getGender());

		logger.info("보험종류 :: {}", info.textType);
		By location = By.id("hptsLineCd");
		setPlan(info, location, info.textType);

		logger.info("임신주수 기간 :: 12 주");
		WaitUtil.waitFor(2);
		location = By.id("prgwkFgr");
		setPlanInputBox(location, "12");

		logger.info("다음 버튼 선택");
		$button = driver.findElement(By.xpath("//button[contains(.,'다음')]"));
		click($button);

		logger.info("주계약 설정");
		setMainTreaty(info);

		logger.info("선택 특약 설정");
		setDirectlySubTreaties(info);

		logger.info("보험료 계산 버튼 클릭");
		$button = driver.findElement(By.xpath("//button[@class='btn primary secondary round']"));
		moveToElement($button);
		click($button);

		logger.info("출생 이전 보험료 크롤링 = 월 보험료");
		location = By.xpath("//strong[text()='출생예정일 이전 합계보험료']//ancestor::li//span[@class='price']");
		crawlPremium(info, location);

		logger.info("출생 이후 보험료 크롤링 = 계속 보험료");
		location = By.xpath("//strong[text()='출생예정일 이후 합계보험료']//ancestor::li//span[@class='price']");
		crawlNextPremium(info, location);

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
