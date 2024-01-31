package com.welgram.crawler.direct.life.sli;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SLI_CCR_F001 extends CrawlingSLI {

	public static void main(String[] args) {
		executeCommand(new SLI_CCR_F001(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) {
		option.setImageLoad(false);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		doCrawlInsurancePublic(info);
		return true;
	}

	private void doCrawlInsurancePublic(CrawlingProduct info) throws Exception {

		logger.info("공시실열기");
		openAnnouncePageNew(info);

		logger.info("생년월일 세팅");
		setBirthNew(info);

		logger.info("성별");
		setGenderNew(By.name("sxdsCd0"), info.gender);

		logger.info("가입조건 :: 보험종류");
		String productKind = info.getProductKind().equals("순수보장형") ? "순수보장형" : "만기지급형";
		helper.selectOptionByClick(By.id("hptsLineCd"),
			productKind + "(" + info.getInsTerm() + " " + info.getProductType().name() + ")");

		logger.info("다음 클릭 !!");
		helper.click(By.xpath("//button[contains(.,'다음')]"));
		helper.waitForLoading(By.cssSelector("body > div.vld-overlay.is-active.is-full-page"));

		// 주계약 조건 설정
		setMainTreatyNew(
			info,
			info.treatyList.stream().filter( t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get());

		// 선택특약 조건 설정
		for (CrawlingTreaty item : info.treatyList) {
			if (!item.productGubun.equals(ProductGubun.주계약)) {
				logger.info(item.productGubun.toString());
				setSubTreatyNew(info, item);
			}
		}

		// 보험료 계산
		WebElement clacBtn = driver.findElement(
			By.cssSelector("button[class='btn primary secondary round']"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", clacBtn);
		helper.click(clacBtn, "보험료 계산버튼");
		helper.waitForCSSElement("body > div.vld-overlay.is-active.is-full-page");

		logger.info("합계 보험료 가져오기");
		element = driver.findElement(By.cssSelector("ul[class='prd-amount-group']"));
		element = element.findElement(By.cssSelector("li:nth-child(1) > div.amount-desc"));
		String premium = element.getText().replaceAll("[^0-9]", "");
		logger.info("#월보험료: " + premium);
		info.treatyList.get(0).monthlyPremium = premium;

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		logger.info("해약환급금 탭 클릭 ");
		helper.click(By.xpath("//a[contains(.,'해약환급금 예시')]"));
		WaitUtil.loading(1);

		getReturnMoneyNew(info, By.cssSelector(""));
		logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));

	}

	protected void getReturnMoneyNew(CrawlingProduct info, By by) throws Exception {

		logger.info("해약환급금 테이블선택");
		elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#content2 > div.component-wrap.next-content > div > div > table > tbody > tr")));

		// 주보험 영역 Tr 개수만큼 loop
		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
		for (WebElement tr : elements) {

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			String term = tr.findElements(By.tagName("td")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
			String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
			String returnRate = tr.findElements(By.tagName("td")).get(3).getText();
			logger.info(term + " :: " + premiumSum );

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoneyList.add(planReturnMoney);
		}

		// 순수보장형 만기환급금 0
//		info.returnPremium = 0+"";
		PlanReturnMoney last = planReturnMoneyList.get(planReturnMoneyList.size() - 1);
		if (last.getTerm().equals(info.insTerm)) {
			info.returnPremium = planReturnMoneyList.get(planReturnMoneyList.size()-1).getReturnMoney() +"";
		}
		logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);

		info.setPlanReturnMoneyList(planReturnMoneyList);
		// 해약환급금 관련 End
	}
}
