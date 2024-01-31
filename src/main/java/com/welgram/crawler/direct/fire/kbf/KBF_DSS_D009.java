package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KBF_DSS_D009 extends CrawlingKBFDirect {

	// KB다이렉트 암건강보험
	public static void main(String[] args) {
		executeCommand(new KBF_DSS_D009(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $a = null;

		waitLoadingBar();
		WaitUtil.waitFor(2);

		logger.info("이벤트 알럿 확인");
		popUpAlert();

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("성별");
		setGender(info.getGender());

		logger.info("보험료 확인");
		$a = driver.findElement(By.linkText("간편하게 보험료 확인"));
		click($a);

		logger.info("직업정보");
		setJob("중·고등학교 교사");

		logger.info("보기/납기 선택");
		String script = "return $('ul.pc_urgent_top')[0]";
		setInsTerm(info.getInsTerm() + "납입 / " + info.getNapTerm() + "만기(갱신형)", script);

		logger.info("플랜 선택");
		By planLocate = By.xpath("//ul[@class='pc_plan_tab_box item3']");
		setPlan(info, planLocate);

		logger.info("특약 확인");
		setTreaties(info);

		logger.info("보험료 크롤링");
		By monthlyPremium = By.id("count1");
		crawlPremium(info, monthlyPremium);

		logger.info("해약환급금 표 확인");
		crawlReturnMoneyList(info);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}
}
