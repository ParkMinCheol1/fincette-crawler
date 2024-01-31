package com.welgram.crawler.direct.fire.hmf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.GenderMismatchException;
import com.welgram.common.except.NotFoundTreatyException;
import com.welgram.common.except.PlanTypeMismatchException;
import com.welgram.common.except.TreatyMisMatchException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HMF_DRV_D007 extends CrawlingHMF {

	// (무) 흥국화재 다이렉트 운전자보험(23.03) - 2023-06-14 기준 사용안함
	public static void main(String[] args) {
		executeCommand(new HMF_DRV_D007(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);
		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setMobile(true);
	}

	private void crawlFromHomepage(CrawlingProduct info) throws Exception {
		logger.info("생년월일 설정 : {}", info.fullBirth);
		setTextToInputBox(By.xpath("//input[@title='생년월일']"), info.fullBirth);

		logger.info("성별 설정");
		setMobileGender(info.gender);

		logger.info("운전여부 : 자가용(고정)");
		helper.waitElementToBeClickable(By.xpath("//label[@for='rdo_motor1']")).click();

		logger.info("다음 버튼 클릭");
		helper.waitElementToBeClickable(By.id("btn_next")).click();

		waitMobileLoadingImg();

		logger.info("납기, 보기 설정");
		setMobileTerms(info.napTerm, info.insTerm);

		logger.info("플랜 설정");
		setMobilePlan(info.textType);

		logger.info("특약 비교");
		compareTreaties(info);

		logger.info("월 보험료 설정");
		setMobilePremiums(info);

//		logger.info("만기환급률 설정");
//		setMobileReturnPremium(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);
	}

	private void compareTreaties(CrawlingProduct info) throws Exception {
		List<CrawlingTreaty> treatyList = info.treatyList;
		HashMap<String, String> homepageTreatyMap = new HashMap<>();

		List<WebElement> trList = driver.findElements(By.xpath("//div[@class='ctn-tb-wrap']//tbody//tr"));
		for(WebElement tr : trList) {
			moveToElement(tr);
			String homepageTreatyName = tr.findElement(By.xpath("./th/a")).getText().replaceAll("\n더보기", "").replaceAll("\\[특약\\]", "").replaceAll(" ", "");
			String homepageTreatyAssureMoney = tr.findElement(By.xpath("./td")).getText();

			if(!("-".equals(homepageTreatyAssureMoney) || "미가입".equals(homepageTreatyAssureMoney))) {
				homepageTreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(homepageTreatyAssureMoney));
				homepageTreatyMap.put(homepageTreatyName, homepageTreatyAssureMoney);
			}
		}

		if(homepageTreatyMap.size() == treatyList.size()) {
			//Good Case :: 홈페이지와 가입설계 특약 수가 일치할 때. 이 경우는 특약명이 일치하는지, 특약 가입금액이 일치하는지 비교해줘야 함.

			for(CrawlingTreaty myTreaty : treatyList) {
				String myTreatyName = myTreaty.treatyName.replaceAll(" ", "");
				String myTreatyMoney = String.valueOf(myTreaty.assureMoney);

				//특약명이 불일치할 경우
				if(!homepageTreatyMap.containsKey(myTreatyName)) {
					throw new NotFoundTreatyException("특약명(" + myTreatyName + ")은 존재하지 않는 특약입니다.");
				} else {
					//특약명은 일치하지만, 금액이 다른경우
					if(!homepageTreatyMap.get(myTreatyName).equals(myTreatyMoney)) {
						logger.info("특약명 : {}", myTreatyName);
						logger.info("홈페이지 금액 : {}원", homepageTreatyMap.get(myTreatyName));
						logger.info("가입설계 금액 : {}원", myTreatyMoney);

						throw new TreatyMisMatchException("특약명(" + myTreatyName + ")의 가입금액이 일치하지 않습니다.");
					}
				}
			}

		} else if(homepageTreatyMap.size() > treatyList.size()) {
			//Wrong Case :: 홈페이지의 특약 개수가 더 많을 때. 이 경우 가입설계에 어떤 특약을 추가해야 하는지 알려야 함.

			List<String> myTreatyNameList = new ArrayList<>();
			for(CrawlingTreaty myTreaty :treatyList) {
				myTreatyNameList.add(myTreaty.treatyName);
			}

			List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
			targetTreatyList.removeAll(myTreatyNameList);

			logger.info("가입설계에 추가해야 할 특약 리스트 :: {}", targetTreatyList);

			throw new TreatyMisMatchException(targetTreatyList + "의 특약들을 추가해야 합니다.");

		} else {
			//Wrong Case : 가입설계의 특약 개수가 더 많을 때. 이 경우 가입설계에서 어떤 특약이 제거돼야 한다고 알려야 함.

			List<String> myTreatyNameList = new ArrayList<>();
			for(CrawlingTreaty myTreaty :treatyList) {
				myTreatyNameList.add(myTreaty.treatyName);
			}

			List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
			myTreatyNameList.removeAll(targetTreatyList);

			logger.info("가입설계에서 제거돼야 할 특약 리스트 :: {}", myTreatyNameList);

			throw new TreatyMisMatchException(myTreatyNameList + "의 특약들을 제거해야 합니다.");

		}

	}

	protected void setMobileReturnPremium(CrawlingProduct info) throws Exception {

		/*
		int monthlyPremium = Integer.parseInt(info.treatyList.get(0).monthlyPremium);
		int napTerm = Integer.parseInt(info.napTerm.replaceAll("[^0-9]", ""));
		double returnRate = Double.parseDouble(driver.findElement(By.cssSelector(".returnRatePercent")).getText());

		double returnPremium = (monthlyPremium * 12 * napTerm * returnRate) / 100;

		int castedReturnPremium = Integer.parseInt(String.valueOf(Math.round(returnPremium)));
		info.returnPremium = String.valueOf(castedReturnPremium);

		logger.info("만기환급금 : {}", info.returnPremium);
		*/




		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

		//만기환급률
		String returnRate = driver.findElement(By.xpath("//div[@class='bx-copy-01']/div[@class='txt-01'][3]/span[2]")).getText().replaceAll("[^0-9.]", "");

		logger.info("만기환급률: {}", returnRate);

		PlanReturnMoney planReturnMoney = new PlanReturnMoney();
		planReturnMoney.setPlanId(Integer.parseInt(info.planId));
		planReturnMoney.setGender(info.getGenderEnum().name());
		planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
		planReturnMoney.setTerm(info.napTerm);					// 납입기간
		planReturnMoney.setReturnRate(returnRate);				// 만기환급률

		planReturnMoneyList.add(planReturnMoney);

		info.setPlanReturnMoneyList(planReturnMoneyList);
	}

	protected void setMobilePremiums(CrawlingProduct info) throws Exception {
		WebElement element = driver.findElement(By.xpath("//div[@class='bx-copy-01']/div[@class='txt-01'][1]/span[2]"));
		moveToElement(element);

		String realPremium = driver.findElement(By.xpath("//div[@class='bx-copy-01']/div[@class='txt-01'][1]/span[2]")).getText().replaceAll("[^0-9]", "");
		String savePremium = driver.findElement(By.xpath("//div[@class='bx-copy-01']/div[@class='txt-01'][2]/span[2]")).getText().replaceAll("[^0-9]", "");
		String monthlyPremium = String.valueOf(Integer.parseInt(realPremium) - Integer.parseInt(savePremium));

		logger.info("월 보험료 : {}원", monthlyPremium);
		logger.info("적립 보험료 : {}원", savePremium);
		logger.info("실제 납입보험료 : {}원", realPremium);

		info.treatyList.get(0).monthlyPremium = monthlyPremium;
		info.savePremium = savePremium;
	}

	protected void setMobileTerms(String napTerm, String insTerm) throws Exception {
		String terms = napTerm + "납 " + insTerm + "만기";

		helper.waitElementToBeClickable(By.id("cbo_payPeriod-button")).click();
		helper.waitElementToBeClickable(By.xpath("//ul[@id='cbo_payPeriod-menu']//div[contains(., '" + terms + "')]")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loader")));
		WaitUtil.waitFor(2);

		String checkedTerms = driver.findElement(By.xpath("//span[@id='cbo_payPeriod-button']//span[@class='ui-selectmenu-text']")).getText();

		logger.info("클릭된 납기, 보기 : {}", checkedTerms);

		if(!checkedTerms.contains(terms)) {
			logger.info("납기, 보기 불일치");
			logger.info("@@@클릭된 납기, 보기 : {}", checkedTerms.trim());
			logger.info("@@@가입설계 납기, 보기 {}", terms);

			throw new Exception("납기, 보기 불일치. 홈페이지 : " + checkedTerms + ", 가입설계 : " + terms);
		}
	}

	protected void setMobilePlan(String textType) throws Exception {
//		WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='" + textType + "']")));
//		element.click();
//		waitMobileLoadingImg();

		helper.waitElementToBeClickable(By.xpath("//ul[@class='tab-01 ui-tabs h-65']//strong[contains(., '" + textType + "')]")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loader")));
		WaitUtil.waitFor(2);


		//클릭된 플랜이 제대로 클릭된게 맞는지 검사
		String checkedPlan = driver.findElement(By.xpath("//ul[@class='tab-01 ui-tabs h-65']/li[@class='on']//strong")).getText();

		logger.info("클릭된 플랜 : {}", checkedPlan);

		if(!checkedPlan.contains(textType)) {
			logger.info("플랜 불일치");
			logger.info("@@@클릭된 플랜 : {}", checkedPlan);
			logger.info("@@@가입설계 플랜 {}", textType);

			throw new PlanTypeMismatchException("플랜 불일치. 홈페이지 : " + checkedPlan + ", 가입설계 : " + textType);
		}
	}

	protected void setMobileJob() throws Exception {
		WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("jobSearchText")));
		setTextToInputBox(element, "교사");
		driver.findElement(By.id("btnSearchStr")).click();
		WaitUtil.waitFor(1);

		driver.findElement(By.xpath("//a[contains(., '초등학교')]")).click();
	}

	protected void waitMobileLoadingImg() throws Exception {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".box-loading")));
		WaitUtil.waitFor(2);
	}

	//모바일용 성별 세팅
	protected void setMobileGender(int gender) throws Exception{
		String genderText = (gender == MALE) ? "남자" : "여자";
		String genderId = (gender == MALE) ? "rdo_211" : "rdo_212";

		helper.waitElementToBeClickable(By.xpath("//label[@for='" + genderId + "']")).click();

		//클릭된 성별이 제대로 클릭된게 맞는지 검사
		String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='rdo_211']:checked\").attr('id')").toString();
		String checkedGender = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']/span")).getText();

		logger.info("클릭된 성별 : {}", checkedGender);

		if(!checkedGender.equals(genderText)) {
			logger.info("성별 불일치");
			logger.info("@@@클릭된 성별 : {}", checkedGender);
			logger.info("@@@가입설계 성별 : {}", genderText);

			throw new GenderMismatchException("성별 불일치. 홈페이지 : " + checkedGender + ", 가입설계 : " + genderText);
		}
	}
}
