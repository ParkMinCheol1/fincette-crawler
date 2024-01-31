package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.common.except.CannotBeSelectedException;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * 교보라이프플래닛 - (무)라이프플래닛e연금저축보험(유니버셜)
 *
 */
public class KLP_ASV_D001 extends CrawlingKLP {



	public static void main(String[] args) {
		executeCommand(new KLP_ASV_D001(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// 연금저축 가입 연령 체크
		if (info.napTerm.indexOf("년") > -1) {

			// 최대 가입 연령 = (연금개시나이 - 납입기간)세
			int maxAge = Integer.parseInt(info.annuityAge) - Integer.parseInt(info.napTerm.replaceAll("년", "").trim());
			logger.info("최대 가입 연령 : " + maxAge);
			logger.info("가입 나이 : " + info.age);

			if (maxAge < Integer.parseInt(info.age)) {
				logger.info("최대 가입 연령 초과");
				return true;
			}
		}

		crawlFromHomepage(info);

		return true;
	}



	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setBrowserType(BrowserType.Chrome);
	}



	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

			logger.info("생년월일");
			helper.sendKeys1_check(By.id("frontJuminNum"), info.fullBirth);

			// setBirth(By.id("frontJuminNum"), info.birth);

			logger.info("성별");
			setGender(info.gender);

			logger.info("보험료 확인/가입");
			setConfirmPremium(By.id("btnCalculMyInsuPay"));

			logger.info("월보험료");
			setMonthlyPremium(By.id("insuMonthPayManwon"), String.valueOf(Integer.parseInt(info.assureMoney) / 10000));

			try {
				logger.info("연금개시나이");
				setAnnuityAge(info.annuityAge);
			}catch(CannotBeSelectedException e) {
				throw new CannotBeSelectedException(e.getMessage());
			}

			try {
				logger.info("납입기간");
				setNapTerm3(info.napTerm.replace("년", ""), info);
			}catch(CannotBeSelectedException e) {
				throw new CannotBeSelectedException(e.getMessage());
			}

			WaitUtil.waitFor(2);

			logger.info("연금수령방식 선택");
			if(info.annuityType.contains("종신 10년")){
				logger.info("기본 : 종신연금형(10년보증) 100%");
			}

			else if(info.annuityType.contains("종신 20년")) {
				driver.findElement(By.cssSelector("#btnInsuPayType > span")).click();
				WaitUtil.waitFor(2);
				driver.findElement(By.cssSelector("#divLikePlan > div.section_plan_info > ul > li:nth-child(4) > div.box_middle.type_2 > div > div.cnt2 > ul > li:nth-child(1) > div.box_rdo > span:nth-child(2)")).click();
				WaitUtil.waitFor(1);
				driver.findElement(By.cssSelector("#copyBtnInsuPayType > span")).click();
				WaitUtil.waitFor(1);
			}

			WaitUtil.waitFor(1);

			logger.info("결과 확인하기");
			confirmResult();
			helper.waitForCSSElement("#loadingArea");
			WaitUtil.waitFor(2);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);
			WaitUtil.waitFor(1);
			info.treatyList.get(0).monthlyPremium = info.assureMoney;
			WaitUtil.waitFor(1);

			logger.info("연금수령액");
			String annuityPremium = driver.findElement(By.cssSelector("#resultGraph > div.graph_top > div.area.right.type2 > div.inner_l > div > strong")).getAttribute("textContent");
			WaitUtil.waitFor(1);
			annuityPremium = annuityPremium.replaceAll("[^0-9]", "");
			WaitUtil.waitFor(1);
			logger.info("연금수령액 : "+annuityPremium+"원");

			if(info.annuityType.contains("종신")){
				info.annuityPremium = annuityPremium;
			}
			/*if(info.annuityType.contains("확정 10년")) {
				info.annuityPremium = annuityPremium;
				info.fixedAnnuityPremium  = annuityPremium;
			}*/
			WaitUtil.waitFor(1);
			getAnnuity("expect1", info);

			WaitUtil.waitFor(1);
			logger.info("해약환급금 조회");
			getReturns("cancel1", info);
			info.savePremium = "0";
	}

	protected void getReturns(String id, CrawlingProduct info) throws Exception {
		WaitUtil.loading(1);
		helper.waitForCSSElement("#loadingArea");
		String lastReturnMoney = null;

		WaitUtil.loading(2);
		element = driver.findElement(By.id(id));
		element.sendKeys(Keys.ENTER);
		element.click();
		WaitUtil.loading(2);

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;
		while (handles.hasNext()) {
			subHandle = handles.next();

			logger.debug(subHandle);
			WaitUtil.loading(1);
		}
		driver.switchTo().window(subHandle);
		elements = driver.findElements(By.cssSelector("#listArea > tr"));

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		if (elements.size() > 0) {

			for (int i = 0; i < elements.size(); i++) {

				WebElement tr = elements.get(i);
				List<WebElement> tdList = tr.findElements(By.tagName("td"));

				logger.info("______해약환급급[{}]_______ ", i);

				String term = tdList.get(0).getAttribute("innerText"); 			// 경과기간
				String premiumSum = tdList.get(1).getAttribute("innerText"); 	// 총납입보험료
				String minReturnMoney = tdList.get(2).getAttribute("innerText"); // 최저해약환급금
				String minReturnRate = tdList.get(3).getAttribute("innerText"); 	// 최저해약환급률
				String avgReturnMoney = tdList.get(4).getAttribute("innerText"); // 평균해약환급금
				String avgReturnRate = tdList.get(5).getAttribute("innerText"); 	// 평균해약환급률
				String returnMoney = tdList.get(6).getAttribute("innerText"); 	// 현재해약환급금
				String returnRate = tdList.get(7).getAttribute("innerText"); 	// 현재해약환급률

				logger.info("|--경과기간: {}", term);
				logger.info("|--납입보험료: {}", premiumSum);
				logger.info("|--최저해약환급금: {}", minReturnMoney);
				logger.info("|--최저해약환급률: {}", minReturnRate);
				logger.info("|--평균해약환급금: {}", avgReturnMoney);
				logger.info("|--평균해약환급률: {}", avgReturnRate);
				logger.info("|--현재해약환급금: {}", returnMoney);
				logger.info("|--현재해약환급률: {}", returnRate);
				logger.info("|_______________________");

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender(info.getGenderEnum().name());
				planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

				planReturnMoney.setTerm(term); 							// 경과기간
				planReturnMoney.setPremiumSum(premiumSum);				//총납입보험료

				planReturnMoney.setReturnMoneyMin(minReturnMoney);		//최저해약환급금
				planReturnMoney.setReturnRateMin(minReturnRate);		//최저해약환급률

				planReturnMoney.setReturnMoneyAvg(avgReturnMoney);		// 평균해약환급금
				planReturnMoney.setReturnRateAvg(avgReturnRate);		// 평균해약환급률

				planReturnMoney.setReturnMoney(returnMoney);			// 현재해약환급금
				planReturnMoney.setReturnRate(returnRate);				// 현재해약환급률

				planReturnMoneyList.add(planReturnMoney);

				if((i+1) == elements.size()) {
					WaitUtil.loading(1);
					lastReturnMoney = returnMoney;
				}
			}

			WaitUtil.loading(1);

// todo
//			info.returnPremium = lastReturnMoney.replaceAll("[^0-9]", "");

			WaitUtil.loading(1);
			info.setPlanReturnMoneyList(planReturnMoneyList);

		} else {

			logger.info("해약환급금 내역이 없습니다.");
		}
	}


	private void  getAnnuity(String id, CrawlingProduct info) throws InterruptedException {

		element = driver.findElement(By.id(id));
		element.sendKeys(Keys.ENTER);
		element.click();

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;

		while (handles.hasNext()) {
			subHandle = handles.next();

			logger.info(subHandle);
			WaitUtil.waitFor();
		}

		driver.switchTo().window(subHandle);
		elements = driver.findElements(By.cssSelector("#listArea > tr"));


		PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
		String fixedAnnuityPremium;

		planAnnuityMoney.setWhl10Y(driver.findElement(By.id("whliAnnFixYy10GutAnam1")).getText() + "000");    //종신 10년
		planAnnuityMoney.setWhl20Y(driver.findElement(By.id("whliAnnFixYy20GutAnam1")).getText() + "000");    //종신 20년
		//planAnnuityMoney.setWhl30y(driver.findElement(By.id("")).getText() + "000");    //종신 30년
		planAnnuityMoney.setWhl100A(driver.findElement(By.id("whliAnnFixAge100GutAnam1")).getText() + "000");  //종신 100세
		planAnnuityMoney.setFxd10Y(driver.findElement(By.id("crtnAnnYy10Anam1")).getText() + "000");    //확정 10년
		//planAnnuityMoney.setFxd15y(driver.findElement(By.id("")).getText() + "000");    //확정 15년
		planAnnuityMoney.setFxd20Y(driver.findElement(By.id("crtnAnnYy20Anam1")).getText() + "000");    //확정 20년
		//planAnnuityMoney.setFxd30y(driver.findElement(By.id("maxTpAmt_30")).getText() + "000");    //확정 30년

		logger.info("종신 10년 : "+planAnnuityMoney.getWhl10Y());
		logger.info("종신 20년 : "+planAnnuityMoney.getWhl20Y());
		logger.info("종신 30년 : "+planAnnuityMoney.getWhl30Y());
		logger.info("종신 100년 : "+planAnnuityMoney.getWhl100A());

		logger.info("확정 10년 : "+planAnnuityMoney.getFxd10Y());
		logger.info("확정 15년 : "+planAnnuityMoney.getFxd15Y());
		logger.info("확정 20년 : "+planAnnuityMoney.getFxd20Y());
		logger.info("확정 25년 : "+planAnnuityMoney.getFxd25Y());
		logger.info("확정 30년 : "+planAnnuityMoney.getFxd30Y());

		fixedAnnuityPremium = driver.findElement(By.id("crtnAnnYy10Anam1")).getText().trim().replaceAll("[^0-9]", "");
		fixedAnnuityPremium = fixedAnnuityPremium+"000";
		info.fixedAnnuityPremium = fixedAnnuityPremium;

		logger.info("확정연금수령액 : "+fixedAnnuityPremium+"원");

		info.planAnnuityMoney = planAnnuityMoney;

		WaitUtil.waitFor(1);
		driver.close();
		WaitUtil.waitFor(1);
		driver.switchTo().window(currentHandle);

	}


}
