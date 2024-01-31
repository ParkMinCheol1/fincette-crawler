package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.common.except.CannotBeSelectedException;
import com.welgram.crawler.direct.life.CrawlingKLP;
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
import org.openqa.selenium.support.ui.ExpectedConditions;


/**
 * 교보라이프플래닛 - (무)라이프플래닛e연금보험(유니버셜)
 *
 */
public class KLP_ANT_D001 extends CrawlingKLP {

	public static void main(String[] args) {
		executeCommand(new KLP_ANT_D001(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		logger.info("담보명 확인");
		checkProductMaster(info,"#wrap > div.visual.new > div > div.area_product > section > h1");

		logger.info("생년월일");
		helper.sendKeys1_check(By.id("frontJuminNum"), info.fullBirth);

		logger.info("성별");
		setGender(info.gender);

		logger.info("보험료 확인/가입");
		setConfirmPremium(By.id("btnCalculMyInsuPay"));

		logger.info("월보험료");
		doInputBoxData(String.valueOf(Integer.parseInt(info.assureMoney) / 10000));

		try {
			logger.info("연금개시나이");
			setAnnuityAge(info.annuityAge);
		} catch(CannotBeSelectedException e) {
			throw new CannotBeSelectedException(e.getMessage());
		}

		try {
			logger.info("목표납입기간");
			setNapTerm3(info.napTerm.replace("년", ""), info);
		} catch(CannotBeSelectedException e) {
			throw new CannotBeSelectedException(e.getMessage());
		}

		logger.info("연금수령방식 선택");
		if ((info.annuityType.contains("종신10년보증"))
			|| (info.annuityType.contains("확정 10년"))
		) {

			logger.info("기본 : 종신연금형(10년보증) 100% 으로 시작 (종신/확정 나중에 확인)");
		}

		logger.info("결과 확인하기");
		confirmResult();
		helper.waitForCSSElement("#loadingArea");
		WaitUtil.waitFor(1);
		logger.info("스크린샷 찍기");
		takeScreenShot(info);
		WaitUtil.waitFor(1);
		info.treatyList.get(0).monthlyPremium = info.assureMoney;
		WaitUtil.waitFor(1);

		logger.info("연금수령액");

		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#resultGraph > div.graph_top > div.area.right.type2 > div.inner_l > div > strong")));
		WaitUtil.waitFor(1);
		String annuityPremium = driver.findElement(By.cssSelector("#resultGraph > div.graph_top > div.area.right.type2 > div.inner_l > div > strong")).getText().trim();
		WaitUtil.waitFor(1);
		annuityPremium = annuityPremium.replaceAll("[^0-9]", "");
		logger.info("연금수령액 : " + annuityPremium + "원");

		if (info.annuityType.contains("종신")) {
			WaitUtil.waitFor(1);
			info.annuityPremium = annuityPremium;
		}

//			if(info.annuityType.contains("확정")) {
//				WaitUtil.waitFor(1);
//				info.annuityPremium = annuityPremium;
//				WaitUtil.waitFor(1);
//			}

		WaitUtil.waitFor(1);
		getAnnuity("expect1", info);

		WaitUtil.waitFor(1);
		logger.info("해약환급금(예시표) 보기");
		getReturns("cancel1", info);

		logger.info("END");

		return true;
	}



	/**
	 * 해약환급금(예시표) 조회
	 *
	 * @param info
	 * @throws InterruptedException
	 */
	@Override
	protected void getReturns(String id, CrawlingProduct info) throws InterruptedException {

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

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

		if (elements.size() > 0) {

			for (int i = 0; i < elements.size(); i++) {

				WebElement tr = elements.get(i);
				List<WebElement> tdList = tr.findElements(By.tagName("td"));

				logger.info("______해약환급급[{}]_______ ", i);

				String term = tdList.get(0).getAttribute("innerText"); 			// 경과기간
				String premiumSum = tdList.get(1).getAttribute("innerText"); 	// 납입보험료

				String returnMoneyMin = tdList.get(2).getAttribute("innerText"); // 최저해약환급금
				String returnRateMin = tdList.get(3).getAttribute("innerText");  // 최저해약환급률

				String returnMoneyAvg = tdList.get(4).getAttribute("innerText"); // 평균해약환급금
				String returnRateAvg = tdList.get(5).getAttribute("innerText");  // 평균해약환급률

				String returnMoney = tdList.get(6).getAttribute("innerText"); 	// 현재해약환급금
				String returnRate = tdList.get(7).getAttribute("innerText"); 	// 현재해약환급률

				logger.info("|--경과기간: {}", term);
				logger.info("|--납입보험료: {}", premiumSum);
				logger.info("|--최저해약환급금: {}", returnMoneyMin);
				logger.info("|--최저해약환급률: {}", returnRateMin);
				logger.info("|--평균해약환급금: {}", returnMoneyAvg);
				logger.info("|--평균해약환급률: {}", returnRateAvg);
				logger.info("|--현재해약환급금: {}", returnMoney);
				logger.info("|--현재해약환급률: {}", returnRate);
				logger.info("|_______________________");

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender(info.getGenderEnum().name());
				planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

				planReturnMoney.setTerm(term); // 경과기간
				planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계

				planReturnMoney.setReturnMoney(returnMoney); // 환급금
				planReturnMoney.setReturnRate(returnRate); // 환급률

				planReturnMoney.setReturnMoneyAvg(returnMoneyAvg); // 평균해약환급금
				planReturnMoney.setReturnRateAvg(returnRateAvg); // 평균환급률

				planReturnMoney.setReturnMoneyMin(returnMoneyMin); // 최저해약환급금
				planReturnMoney.setReturnRateMin(returnRateMin); // 최저환급률

				planReturnMoneyList.add(planReturnMoney);

				int intTerm = Integer.parseInt(term.replaceAll("[^0-9]", ""));

				logger.info("TERM        :: {}", intTerm);
				logger.info("ANN_AGE     :: {}", info.annuityAge);

				int diffChecker = Integer.parseInt(info.annuityAge) - Integer.parseInt(info.age.replaceAll("[^0-9]", ""));
				logger.info("diffChecker :: {}", diffChecker);

				info.returnPremium = "-1";
				if(Integer.parseInt(term.replaceAll("[^0-9]", "")) == diffChecker && !term.contains("개월")) {
					logger.info("TERM :: ", term);
					info.returnPremium = returnMoney;
					logger.info("RM :: {}", info.getReturnPremium());
				}
			}
			info.setPlanReturnMoneyList(planReturnMoneyList);

		} else {

			logger.info("해약환급금 내역이 없습니다.");
		}
	}



	// inputBox에 키 입력
	public void doInputBoxData(String value) throws InterruptedException {
		WaitUtil.waitFor(2);
		WebElement element = driver.findElement(By.id("insuMonthPay"));
		element.sendKeys(Keys.ENTER);
		element.click();
		// 기존에 쓰여있는 값 지우기.
		element.clear();
		// 아래코드 크롬에서 안먹음
//		Actions builder = new Actions(driver);
//		builder.keyDown(Keys.CONTROL)
//				.sendKeys("a")
//				.keyUp(Keys.CONTROL)
//				.sendKeys(Keys.DELETE)
//				.build().perform();
		WaitUtil.loading(1);
		logger.info("value :: " + value);
		element.sendKeys(value);
	}


	// 연금정보 담기
	private void getAnnuity(String id, CrawlingProduct info) throws InterruptedException {

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

		int unit = 1;
		String unitFlag = driver.findElement(By.xpath("//*[@id='content']/div[2]/div[2]/p")).getText();
		if(unitFlag.contains("천원")) {
			unit = 1000;
		}
		logger.info("금액단위(unit) :: {}", unit);

		PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
		String fixedAnnuityPremium;

		String whl10yamt = String.valueOf(Integer.valueOf(driver.findElement(By.id("whliAnnFixYy10GutAnam1")).getText().replaceAll("[^0-9]","")) * unit);
		String whl20yamt =  String.valueOf(Integer.valueOf(driver.findElement(By.id("whliAnnFixYy20GutAnam1")).getText().replaceAll("[^0-9]","")) * unit);
		String whl100oamt = String.valueOf(Integer.valueOf(driver.findElement(By.id("whliAnnFixAge100GutAnam1")).getText().replaceAll("[^0-9]","")) * unit);
		String fxd10yamt =  String.valueOf(Integer.valueOf(driver.findElement(By.id("crtnAnnYy10Anam1")).getText().replaceAll("[^0-9]","")) * unit);
		String fxd20yamt =  String.valueOf(Integer.valueOf(driver.findElement(By.id("crtnAnnYy20Anam1")).getText().replaceAll("[^0-9]","")) * unit);

		planAnnuityMoney.setWhl10Y(whl10yamt);    //종신 10년
		planAnnuityMoney.setWhl20Y(whl20yamt);    //종신 20년
		//planAnnuityMoney.setWhl30y(driver.findElement(By.id("")).getText() + "000");    //종신 30년
		planAnnuityMoney.setWhl100A(whl100oamt);  //종신 100세
		planAnnuityMoney.setFxd10Y(fxd10yamt);    //확정 10년
		//planAnnuityMoney.setFxd15y(driver.findElement(By.id("")).getText() + "000");    //확정 15년
		planAnnuityMoney.setFxd20Y(fxd20yamt);    //확정 20년
		//planAnnuityMoney.setFxd30y(driver.findElement(By.id("maxTpAmt_30")).getText() + "000");    //확정 30년

		logger.info("종신 10년 : " + whl10yamt);
		logger.info("종신 20년 : " + whl20yamt);
//		logger.info("종신 30년 : " + planAnnuityMoney.getWhl30Y());
		logger.info("종신 100년 : " + whl100oamt);

		logger.info("확정 10년 : " + fxd10yamt);
//		logger.info("확정 15년 : " + planAnnuityMoney.getFxd15Y());
		logger.info("확정 20년 : " + fxd20yamt);
//		logger.info("확정 25년 : " + planAnnuityMoney.getFxd25Y());
//		logger.info("확정 30년 : " + planAnnuityMoney.getFxd30Y());

		fixedAnnuityPremium = driver.findElement(By.id("crtnAnnYy10Anam1")).getText().trim().replaceAll("[^0-9]", "");
		fixedAnnuityPremium = String.valueOf(Integer.parseInt(fixedAnnuityPremium) * unit);
		info.fixedAnnuityPremium = fixedAnnuityPremium;

		logger.info("확정연금수령액 : "+fixedAnnuityPremium+"원");

		info.planAnnuityMoney = planAnnuityMoney;

		WaitUtil.waitFor(1);
		driver.close();
		WaitUtil.waitFor(1);
		driver.switchTo().window(currentHandle);

	}
}
