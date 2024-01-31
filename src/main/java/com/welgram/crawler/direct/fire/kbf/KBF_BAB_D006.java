package com.welgram.crawler.direct.fire.kbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KBF_BAB_D006 extends CrawlingKBFDirect {

	// KB다이렉트 자녀보험(태아) (
	public static void main(String[] args) {
		executeCommand(new KBF_BAB_D006(), args);
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
		WebElement $a = null;

		logger.info("팝업 확인");
		popUpAlert();

		logger.info("자녀/태아 선택 ");
		$a = driver.findElement(By.cssSelector(".left:nth-child(1) .tit"));
		click($a);

		logger.info("생년월일");
		setBirthday(info.getFullBirth());

		logger.info("출산 예정일 설정");
		setDueDate();

		logger.info("보험료 확인");
		$a = driver.findElement(By.linkText("간편하게 보험료 확인"));
		click($a);

		logger.info("로딩 대기");
		waitLoadingBar();

		logger.info("직업정보");
		setJob("중·고등학교 교사");

		logger.info("로딩 대기");
		waitLoadingBar();

		logger.info("출생전후 보험료 변경안내 창 닫기");
		$a = driver.findElement(By.xpath("//*[@class='pc_large_bottom_btn enter']"));
		click($a);

		logger.info("보험기간 선택");
		String script = "return $('ul.clfix._item2')[0]";
		setInsTerm(info.getInsTerm() + "만기" , script);

		logger.info("납입기간 선택");
		script = "return $('ul.clfix._item3')[0]";
		setNapTerm(info.getNapTerm() + "납입", script);

		logger.info("특약 확인");
		setTreaties(info);

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("만기환급금 확인");
		By premiumLocate = By.xpath("//div[@class='pc_tit_p2 font15']");
		crawlReturnPremium(info, premiumLocate, null, null);

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

			WebElement $premiumEm = driver.findElement(By.xpath("//span[@class='pc_ch_fetus_txt2 _colored']"));
			String premium = $premiumEm.getText().replaceAll("[^0-9]", "");

			mainTreaty.monthlyPremium = premium;

			if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
				logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
				throw new PremiumCrawlerException(exceptionEnum.getMsg());
			} else {
				logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
			}

			info.nextMoney = driver.findElement(By.xpath("//div[@class='after_brth _brth']//strong[@class='ng-binding']")).getText().replaceAll("[^0-9]", "");
			logger.info("2회부터 월보험료 : " + info.nextMoney);

		} catch (Exception e) {
			throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}
	}

	protected void returnMoneyCheck(CrawlingProduct info) throws Exception {
		info.returnPremium = driver.findElement(By.xpath("//div[@class='pc_tit_p2 font15']")).getText().replaceAll("[^0-9]","");

		if(!info.returnPremium.contains("")){
			List<PlanReturnMoney> returnMoneyList = new ArrayList<PlanReturnMoney>();

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			planReturnMoney.setGender(info.getGenderEnum().name());
			planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
			planReturnMoney.setReturnMoney(info.returnPremium);

			returnMoneyList.add(planReturnMoney);

			logger.info("|--예상만기환급금 : {}", info.returnPremium);
		} else {
			logger.info("없음(순수보장형)");
		}

	}
}
