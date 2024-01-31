package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class KBF_DSS_D010 extends CrawlingKBFMobile {

	public static void main(String[] args) {
		executeCommand(new KBF_DSS_D010(), args);
	}



	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setMobile(true);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $button = null;
		WebElement $a = null;
		String script = "return $('ul.mo_stop_day.mo_two')[0]";

		waitLoadingBar();
		WaitUtil.loading(2);

		//하단에 보험료 알아보기 배너클릭
		logger.info("보험료 알아보기");
		$button = driver.findElement(By.xpath("//button[contains(text(), '보험료  알아보기')]"));
		click($button);

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("성별");
		setGender1(info.getGender());

		logger.info("운전형태 :: 운전안함 선택");
		$a = driver.findElement(By.xpath("//div[@class='btn-check ']//a[contains(., '운전안함')]"));
		click($a);

		logger.info("보험료 계산하기 버튼 클릭");
		$button = driver.findElement(By.xpath("//button[contains(text(), '보험료 계산하기')]"));
		click($button);

		logger.info("직업정보");
		setJob("중·고등학교 교사");

		waitLoadingBar();
		WaitUtil.loading(2);

		logger.info("플랜 선택 :: {}", info.textType);
		$a = driver.findElement(By.xpath("//div[@class='mo_kb_wrapper mo_top_pt ng-scope']//strong[contains(., '"+ info.textType+"')]"));
		click($a);

		logger.info("보기 선택 :: {}", info.insTerm);
		setInsTermAndNapTerm1(info);

		logger.info("특약 체크");
		setTreaties3(info);
		WaitUtil.waitFor(3);

		logger.info("보험료 크롤링을 위해 불필요한 element 삭제");
		script = "$('span.txt_won').remove();";
		helper.executeJavascript(script);
		script = "$('li.on span.ng-binding').remove();";
		helper.executeJavascript(script);

		logger.info("월 보험료");
		By premiumLocation = By.xpath("//ul[@class='mo_com_tab item2 mo_fix_top mo_clearfix mo_cvr_cnt']//li[@class='on']");
		crawlPremium(info, premiumLocation);

		logger.info("예상만기환급금 조회");
		crawlReturnPremium(info);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}

	//해약환급금
	public void crawlReturnPremium(CrawlingProduct info) throws ReturnPremiumCrawlerException {

		try{
			String returnMoney = driver.findElement(By.xpath("//div[@class='bottom_content_wrapper ng-scope']//li[1]//dl[@class='clfix _imgWrapper']//dd")).getText().replaceAll("[^0-9]", "");
			returnMoney = returnMoney.replace(",", "").replace("원", "");

			if(!("".equals(returnMoney))){
				List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender(info.getGenderEnum().name());
				planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));


				planReturnMoney.setReturnMoney(returnMoney); // 환급금

				planReturnMoneyList.add(planReturnMoney);

				info.returnPremium = returnMoney;
			}
		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
			throw new ReturnPremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}

	}
}

