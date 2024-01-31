package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class KBF_DRV_D018 extends CrawlingKBFDirect {


	// KB 다이렉트 플러스 운전자보험(무배당)(3년이상)
	public static void main(String[] args) {
		executeCommand(new KBF_DRV_D018(), args);
	}


	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $span = null;
		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.waitFor(2);

		logger.info("[자가용] 선택");
		$span = driver.findElement(By.xpath("//span[contains(.,'자가용')]"));
		click($span);

		logger.info("[본인] 선택");
		$span = driver.findElement(By.xpath("//span[contains(.,'본인')]"));
		click($span);

		logger.info("성별");
		setGender(info.getGender());

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("보험료 확인");
		$a = driver.findElement(By.linkText("간편하게 보험료 확인"));
		click($a);

		logger.info("직업정보");
		setJob("중·고등학교 교사");

		logger.info("보험료 확인 이벤트 알럿창 발생 여부 확인");
		popUpAlert();

		logger.info("보기/납기 선택");
		String script = "return $('ul.pc_urgent_top')[0]";
		setInsTerm(info.getInsTerm() + "납입 / " + info.getNapTerm() + "만기", script);

		logger.info("플랜 선택");
		By planLocate = By.xpath("//ul[@class='pc_plan_tab_box item4']");
		setPlan(info, planLocate);

		logger.info("특약 확인");
		setTreaties(info);

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("해약환급금 표 확인");
		crawlReturnMoneyList(info);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}

	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {
		String title = "보험료 크롤링";
		String script = "";

		CrawlingProduct info = (CrawlingProduct) obj[0];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(
			ProductGubun.주계약)).findFirst().get();
		ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

		try {

			//보험료 크롤링 전에는 대기시간을 넉넉히 준다
			WaitUtil.waitFor(5);

			WebElement $premiumEm = driver.findElement(By.cssSelector(".left.pc_w_46 .pc_tit_p2.ng-binding"));
			String premium = $premiumEm.getText().replaceAll("[^0-9]", "");

			mainTreaty.monthlyPremium = premium;

			if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
				logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
				throw new PremiumCrawlerException(exceptionEnum.getMsg());
			} else {
				logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
			}

			String saveMoney =  driver.findElement(By.cssSelector(".left.pc_w_46.al_right .pc_tit_p2.ng-binding")).getText().replaceAll("[^0-9]","");
			info.savePremium = saveMoney;
			logger.info("적립보험료 : " + info.savePremium);

		} catch (Exception e) {
			throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}
	}
}
