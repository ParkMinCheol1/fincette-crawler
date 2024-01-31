package com.welgram.crawler.direct.life;

import com.welgram.common.MoneyUtil;
import java.util.ArrayList;
import java.util.List;

import com.welgram.common.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.Gson;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;

/**
 * @author SungEun Koo <aqua@welgram.com> 국민
 */

public abstract class CrawlingKBL extends SeleniumCrawler {

	// 생년월일
	protected void setBirth(By id, String birth) throws InterruptedException {
		element = driver.findElement(id);
		element.clear();
		element.sendKeys(birth);
		WaitUtil.waitFor();
	}

	// 성별
	protected void setGender(By id, int gender) throws InterruptedException {
		elements = driver.findElement(id).findElements(By.cssSelector("span"));
		if (gender == MALE) {
			element = elements.get(0);
		}
		if (gender == FEMALE) {
			element = elements.get(1);
		}

		element.click();
		WaitUtil.waitFor();

	}

	// 보장 실속형, 고보장형
	protected void setCoverage(By id, String premium) throws Exception {
		elements = driver.findElement(id).findElements(By.tagName("dt"));
		for (WebElement dt : elements) {
			if ("가입금액".equals(dt.getText().trim())) {
				element = dt.findElement(By.xpath("parent::*"));
				element = element.findElement(By.tagName("em"));
				if (premium.equals(element.getText().replaceAll("[^0-9]", ""))) {
					element = element.findElement(By.xpath("parent::*")).findElement(By.xpath("parent::*"));
					element = element.findElement(By.xpath("parent::*")).findElement(By.xpath("parent::*"));
					element = element.findElement(By.xpath("parent::*")).findElement(By.cssSelector(".ksAsHead"));
					element.click();
					helper.waitForCSSElement(".loading");
					break;
				}
			}
		}
	}

	// 보험기간, 납입기간
	protected void setInfo(By id, String value) throws Exception {
		boolean result = false;
		driver.findElement(id).findElement(By.className("anchor")).click();

		element = driver.findElement(id).findElement(By.cssSelector("ul.panel"));
		elements = element.findElements(By.tagName("a"));
		for (WebElement a : elements) {
			if (value.equals(a.getText().trim())) {
				a.click();
				result = true;
				WaitUtil.waitFor();
				break;
			}
		}

		if (!result) {
			throw new Exception(value + "을 선택할 수 없습니다.");
		}
	}

	// 고객 건강정보 set
	protected void setHealthInfo(CrawlingProduct info) throws Exception {

		String MALE_ARG_HEIGHT = "174";
		String FEMALE_ARG_HEIGHT = "163";
		String MALE_ARG_WEIGHT = "67";
		String FEMALE_ARG_WEIGHT = "54";

		helper.elementWaitFor("#modalHealthType");
		// 평생동안 한번이라도 흡연을 한 적 있으신가요?
		element = driver.findElement(By.id("lbl_answer_yes1")).findElement(By.xpath("parent::*"));
		element.click();
		Thread.sleep(1500);

		// 현재 흡연 중이거나, 최근 1년 이내에 흡연을 하신 적 있으신가요?
		element = driver.findElement(By.id("lbl_answer_yes2")).findElement(By.xpath("parent::*"));
		element.click();
		Thread.sleep(1500);

		// 저혈압, 고혈압 진단을 받으신 적이 있으신가요?
		element = driver.findElement(By.id("lbl_answer_no3")).findElement(By.xpath("parent::*"));
		element.click();
		Thread.sleep(1500);

		// 당뇨진잔 X
		element = driver.findElement(By.id("lbl_answer_no4")).findElement(By.xpath("parent::*"));
		element.click();
		Thread.sleep(1500);

		if (info.gender == MALE) {
			// 남자(키 174cm/몸무게67kg)
			driver.findElement(By.id("lbl_height")).sendKeys(MALE_ARG_HEIGHT);
			Thread.sleep(1500);
			driver.findElement(By.id("lbl_weight")).sendKeys(MALE_ARG_WEIGHT);
			Thread.sleep(1500);
		} else {
			// 여자(키163cm/몸무게54kg)
			driver.findElement(By.id("lbl_height")).sendKeys(FEMALE_ARG_HEIGHT);
			Thread.sleep(1500);
			driver.findElement(By.id("lbl_weight")).sendKeys(FEMALE_ARG_WEIGHT);
			Thread.sleep(1500);
		}
		driver.findElement(By.cssSelector("#modalHealthType .btn-4x.btn-yellow")).click();
		helper.waitForCSSElement(".loading");
	}
	
	protected boolean alert(String value) throws Exception {
		WebElement element = null;
		try {
			element = new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(value))));
		} catch (Exception e){}

		if(element == null) {
			return false;
		} else {
			return true;
		}
	}

	// 보험료계산하기
	protected void calculatePremium(By id) throws Exception {
		driver.findElement(id).click();
		helper.waitForCSSElement(".loading");
		if (alert("#pcps_alert")) {
			// 납입기간에 따른 가입나이 제한 오류
			throw new Exception("");
		}
	}

	// 보험가입금액 직접계산
	protected void setPremium(String premium, String value) throws InterruptedException {
		element = driver.findElement(By.cssSelector("input[name='" + value + "']"));
		element.clear();
		element.sendKeys(premium);
		WaitUtil.waitFor();
	}

	// 보험가입금액 직접계산
	protected void setSelfPremium(By id, String premium) throws InterruptedException {
		driver.findElement(id).findElement(By.className("anchor")).click();
		element = driver.findElement(id).findElement(By.cssSelector("ul.panel"));
		element = element.findElement(By.cssSelector("li:last-child")).findElement(By.tagName("a"));
		element.click();
		WaitUtil.waitFor();

		WebElement element2 = driver.findElement(id).findElement(By.className("anchor"));
		if ("직접입력".equals(element2.getText())) {
		} else {
			element.click();
		}

		element = driver.findElement(By.cssSelector("input[title='월보험료']"));
		element.sendKeys(premium);
		WaitUtil.waitFor();
	}

	// 보험료 조회
	protected void getCrawlingResult(By id, CrawlingProduct info) throws InterruptedException {
		String premium = "";
		premium = driver.findElement(id).getText().replaceAll("[^0-9]", "");
		logger.debug("월 보험료: " + premium + "원");
		info.treatyList.get(0).monthlyPremium = premium;
		info.errorMsg = "";
		info.returnPremium = "0";
		info.annuityPremium = "0";
	}

	protected void getReturnPremium(CrawlingProduct info) throws Exception {

		helper.click(By.id("buttonRefundView"));
		helper.waitForCSSElement("div.loading");

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		element = helper.waitPresenceOfElementLocated(By.id("annuityRefundPop"));
		wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
				element.findElement(By.cssSelector("div.modal-body > div > div > table > tbody")), By.tagName("tr")));
		elements = element
				.findElement(By.cssSelector("div.modal-body > div > div > table > tbody"))
				.findElements(By.tagName("tr"));

		for (WebElement tr : elements) {

			// 납입기간
			String term 			= tr.findElements(By.tagName("td")).get(0).getText();
			logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			logger.info("해약환급금 크롤링:: 납입기간 :: " + term);

			// 합계 보험료
			String premiumSum 		= tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

			// -----------------------------------------------------------------
			// 공시 환급금
			String returnMoney 		= tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);

			// 공시 환급률
			String returnRate 		= tr.findElements(By.tagName("td")).get(3).getText();
			logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);


			// -----------------------------------------------------------------

			// 평균 환급금
			String returnMoneyAvg 	= tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);

			// 평균 환급률
			String returnRateAvg 	= tr.findElements(By.tagName("td")).get(5).getText();
			logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

			// -----------------------------------------------------------------

			// 최저 환급금
			String returnMoneyMin 	= tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);

			// 최저 환급률
			String returnRateMin 	= tr.findElements(By.tagName("td")).get(7).getText();
			logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

			// -----------------------------------------------------------------

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();

			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
			planReturnMoney.setReturnRateMin(returnRateMin);;
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
			planReturnMoney.setReturnRateAvg(returnRateAvg);

			planReturnMoneyList.add(planReturnMoney);

			info.returnPremium = planReturnMoneyList.get(planReturnMoneyList.size() - 1).getReturnMoney().replace(",", "").replace("원", "");
			logger.info(info.returnPremium);
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

		// 해약환급금 닫기
		helper.click(By.cssSelector("#annuityRefundPop > div > div.modal-footer > button"));

		logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
	}

	protected void getViewerReturnPremium(CrawlingProduct info, String[] returnMoneyPageList, String moneyUtilLocation) throws Exception {
		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

		logger.info("[보장내역 보기] 버튼 선택");
		moveToElement(By.id("buttonResultDocumentView"));
		helper.click(By.id("buttonResultDocumentView"));
		helper.waitForCSSElement("div.loading");
		WaitUtil.waitFor(3);

		logger.info("상품설명서 창 전환");
		ArrayList<String> tab = new ArrayList<>(driver.getWindowHandles());
		driver.switchTo().window(tab.get(1));

		logger.info("해약환급금 페이지 조회");
		ArrayList arrayList = confirmReturnPremium(returnMoneyPageList);

		for(int i = 0 ; i < arrayList.size(); i++){
			logger.info("해약환급금 조회");

			try{
				driver.findElement(By.id("crownix-toolbar-move")).click();
				WaitUtil.waitFor(3);
			} catch (ElementClickInterceptedException e){
				e.printStackTrace();
				logger.info(" [ "+arrayList.get(i) + " ] 페이지는 존재하지 않는 페이지입니다.");
				continue;
			}

			setTextToInputBox(By.cssSelector(".aTextbox"), String.valueOf(arrayList.get(i)));
			driver.findElement(By.xpath("//button[text()='확인']")).click();
			WaitUtil.waitFor(4);

			String unit = "";
			try{
				String moneyUnit = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div["+moneyUtilLocation+"]")).getText();
				int unitStart = moneyUnit.indexOf(":");
				int unitEnd = moneyUnit.indexOf(")");
				unit = moneyUnit.substring(unitStart+1, unitEnd).replace(" ", "");
			} catch (StringIndexOutOfBoundsException e) {
				logger.info("[" + arrayList.get(i) + "] 페이지는 해약환급금이 아닙니다.");
				continue;
			}

			List<WebElement> elements = driver.findElements(By.xpath("//*[@id='m2soft-crownix-text']//div"));
			int idx = 0;
			for(int j = 0; j < elements.size(); j++){
				try{
					WebElement div = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+j+"]"));
					if(div.getText().contains("D/A")){
						idx = j + 1;
						break;
					}
				} catch (NoSuchElementException e){

				}
			}

			boolean isEnd = false;
			while(!isEnd){
				try{
					moveToElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+idx+"]"));
					String term = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+idx+"]")).getText();
					String premiumSum = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+3)+"]")).getText();
					String returnMoneyMin = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+5)+"]")).getText();
					String returnRateMin = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+6)+"]")).getText();
					String returnMoneyAvg = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+8)+"]")).getText();
					String returnRateAvg = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+9)+"]")).getText();
					String returnMoney = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+11) + ")")).getText();
					String returnRate = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+12) + ")")).getText();

					if(term.length() > 4) {
						throw new NoSuchElementException("경과기간에 해당하는 div가 아닙니다.");
					}

					logger.info("================================");
					logger.info("경과기간 : {}", term);
					logger.info("납입보험료 : {}", premiumSum);
					logger.info("최저 환급금 : {}", returnMoneyMin);
					logger.info("최저 환급률 : {}", returnRateMin);
					logger.info("평균 환급금 : {}", returnMoneyAvg);
					logger.info("평균 환급률 : {}", returnRateAvg);
					logger.info("해약환급금 : {}", returnMoney);
					logger.info("환급률 : {}", returnRate);

					PlanReturnMoney planReturnMoney = new PlanReturnMoney();

					planReturnMoney.setPlanId(Integer.parseInt(info.planId));
					planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
					planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

					planReturnMoney.setTerm(term);
					planReturnMoney.setPremiumSum(String.valueOf(MoneyUtil.toDigitMoney(premiumSum+unit)));
					planReturnMoney.setReturnMoneyMin(String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin+unit)));
					planReturnMoney.setReturnRateMin(returnRateMin);
					planReturnMoney.setReturnMoneyAvg(String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg+unit)));
					planReturnMoney.setReturnRateAvg(returnRateAvg);
					planReturnMoney.setReturnMoney(String.valueOf(MoneyUtil.toDigitMoney(returnMoney+unit)));
					planReturnMoney.setReturnRate(returnRate);

					planReturnMoneyList.add(planReturnMoney);

					info.returnPremium = planReturnMoney.getReturnMoney();

					idx += 13;
				} catch(NoSuchElementException e) {
					isEnd = true;
				} catch (MoveTargetOutOfBoundsException e){
					isEnd = true;
				}
			}

			info.setPlanReturnMoneyList(planReturnMoneyList);

			logger.info("만기환급급 :: {}원", info.returnPremium);
		}
	}

	protected ArrayList confirmReturnPremium(String[] returnMoneyPageList) throws Exception{
		WaitUtil.waitFor(15);

		ArrayList arrayList = new ArrayList();

		String[] page = returnMoneyPageList;
		for(int i = 0; i < page.length; i++){
			try {
				driver.findElement(By.id("crownix-toolbar-move")).click();
				WaitUtil.waitFor(1);

				setTextToInputBox(By.cssSelector(".aTextbox"), page[i]);
				driver.findElement(By.xpath("//button[text()='확인']")).click();
				WaitUtil.waitFor(2);
				if(existElement(By.xpath("//button[text()='OK']"))) {
					helper.click(driver.findElement(By.xpath("//button[text()='OK']")));
				} else {
					driver.findElement(By.xpath("//*[@id='m2soft-crownix-text'][contains(., '해약환급금(주계약')]"));
					arrayList.add(page[i]);
				}
			} catch (Exception e) {
				try{
					driver.findElement(By.xpath("//*[@id='m2soft-crownix-text'][contains(., '해약환급금 및 주계약')]"));
					arrayList.add(page[i]);
				} catch (Exception ex){
					try{
						driver.findElement(By.xpath("//*[@id='m2soft-crownix-text'][contains(., '해약환급금 예시표')]"));
						arrayList.add(page[i]);
					} catch (Exception exe){
						logger.info("{}페이지는 해약환급금 예시 페이지가 아닙니다.", page[i]);
					}

				}
			}

			try{
				if(driver.findElement(By.xpath("//button[text()='OK']")).isDisplayed()){
					helper.click(driver.findElement(By.xpath("//button[text()='OK']")));
				}
			} catch (Exception e){

			}
//				try{
//					helper.doClick(By.xpath("//button[text()='OK']"));
//				} catch (Exception e){
//
//				}
		}
		if(arrayList.size() == 0) throw new Exception("해약환급금 페이지를 찾을 수 없습니다. 상품설명서를 확인해주세요.");

		return arrayList;
	}

	protected boolean existElement(By element) {
		boolean isExist = false;

		try {
			driver.findElement(element);
			isExist = true;
		} catch(NoSuchElementException e) {

		}

		return isExist;
	}

	protected void setTextToInputBox(By element, String text) {
		WebElement inputBox = driver.findElement(element);
		inputBox.click();
		inputBox.clear();
		inputBox.sendKeys(text);
	}

	protected void moveToElement(By location){
		Actions actions = new Actions(driver);
		WebElement element = driver.findElement(location);
		actions.moveToElement(element);
		actions.perform();
	}

	// 예상연금수령액
	protected void getAnnuityPremium(CrawlingProduct info, String value) throws Exception {
		String annuityPremium = "";
		element = driver.findElement(By.cssSelector(value));
		annuityPremium = element.getText();
		logger.debug("예상연금수령액: " + annuityPremium + "원");
		annuityPremium = annuityPremium.replaceAll("[^0-9]", "");

		info.annuityPremium = annuityPremium;
		info.treatyList.get(0).monthlyPremium = info.assureMoney;
		info.savePremium = info.assureMoney;
	}

	protected void checkProductMaster(CrawlingProduct info, String el) {
		try {
			for (CrawlingTreaty item : info.treatyList) {
				String treatyName = item.treatyName;
				String prdtName = driver.findElement(By.cssSelector(el)).getText();

				if (treatyName.indexOf(prdtName) > -1){
					info.siteProductMasterCount ++;
					logger.info("담보명 확인 완료 !! ");
				}
			}
		}catch(Exception e){
			logger.info("담보명 확인 에러 발생 !!");
		}
		
	}

	protected void selectDropDown_front (By byForSearchingDiv, String stringForSearching) throws Exception {
		boolean found = false;

		WaitUtil.waitFor(2);
		WebElement div = helper.waitElementToBeClickable(byForSearchingDiv);
		element = div.findElement(By.className("select-box"));
		element.click();
		wait.until(ExpectedConditions.attributeContains(element, "class" ,"active"));
		elements = element.findElements(By.tagName("li"));
		for(WebElement li : elements) {
			WebElement a = li.findElement(By.tagName("a"));

			String annuityAge = a.getText().replaceAll("[^0-9]", "");

			logger.debug("annuityAge : " + annuityAge);
			logger.debug("stringForSearching : " + stringForSearching);
			
			if(annuityAge.equals(stringForSearching)) {
			//if(a.getText().equals(stringForSearching)) {
				helper.click(a);
				WaitUtil.waitFor(3);
				logger.info(a.findElement(By.xpath("parent::*")).getText() + " 선택");
				found = true;
				break;
			}
		}

		if (!found) {
			throw new Exception("selectDropDown : 찾는 조건이 없습니다." );
		}
	}

	protected void selectGender_front (int intGender) throws InterruptedException {
		elements = driver.findElements(By.name("genderCode"));
		String gender = "";
		if (intGender == 0) {
			gender = "1";
		} else if (intGender == 1) {
			gender = "2";
		}
		for (WebElement input : elements) {
			String value = input.getAttribute("value");
			if(value.equals(gender)) { // 남자면
				helper.click(input.findElement(By.xpath("ancestor::label")));
				break;
			}
		}
	}

	protected void lifePlanReturnMoney(CrawlingProduct info) throws Exception {

		element = helper.waitPresenceOfElementLocated(By.id("annuityRefundPop2_tbody1"));
		elements = element.findElements(By.tagName("tr"));
		String unitMoneyLocation = element.findElement(By.xpath("//div[@class='ksAgreeWrap01']//span[@class='grey-text-lighten-9']")).getText();
		String unitMoney = "";
		if(unitMoneyLocation.contains("만원")){
			unitMoney = "0000";
		} else {
			logger.info("금액의 단위가 기존의 [만원]이 아닙니다. 확인해주세요.");
		}

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

		for(int i = 0; i<elements.size(); i++){
			WebElement tr = elements.get(i);
			List<WebElement> tdList = tr.findElements(By.tagName("td"));

//            Actions action = new Actions(driver);
//            action.moveToElement(tr);
//            action.perform();

			String term = tdList.get(0).getText();
			String premiumSum = tdList.get(1).getText().replace(",", "") + unitMoney;
			String returnMoney = tdList.get(2).getText().replace(",", "") + unitMoney;
			String returnRate = tdList.get(3).getText();
			String returnMoneyAvg = tdList.get(4).getText().replace(",", "") + unitMoney;
			String returnRateAvg = tdList.get(5).getText();
			String returnMoneyMin = tdList.get(6).getText().replace(",", "") + unitMoney;
			String returnRateMin = tdList.get(7).getText();

			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--공시이율 해약환급금: {}", returnMoney);
			logger.info("|--공시이율 환급률: {}", returnRate);
			logger.info("|--최저보증이율 해약환급금: {}", returnMoneyMin);
			logger.info("|--최저보증이율 환급률: {}", returnRateMin);
			logger.info("|--평균공시이율 해약환급금: {}", returnMoneyAvg);
			logger.info("|--평균공시이율 환급률: {}", returnRateAvg);
			logger.info("|_______________________");

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			planReturnMoney.setGender(info.getGenderEnum().name());
			planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

			planReturnMoney.setTerm(term);                        // 경과기간

			planReturnMoney.setPremiumSum(premiumSum);            // 보험료 합계
			planReturnMoney.setReturnMoney(returnMoney);        // 공시이율 해약환급금
			planReturnMoney.setReturnRate(returnRate);            // 공시이율 환급률
			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);    // 평균공시이율 해약환급금
			planReturnMoney.setReturnRateAvg(returnRateAvg);    // 평균공시이율 환급률
			planReturnMoney.setReturnMoneyMin(returnMoneyMin);    // 최저보증이율 해약환급금
			planReturnMoney.setReturnRateMin(returnRateMin);    // 최저보증이율 환급률

			planReturnMoneyList.add(planReturnMoney);

//			info.returnPremium = returnMoney.replace(",", "").replace("원", "");
			info.returnPremium = String.valueOf(returnMoney);
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

		helper.click(By.xpath("//*[@id='annuityRefundPop2']//button[text()='확인']"));
		logger.debug("planReturnMoney :: " + (new Gson()).toJson(info.getPlanReturnMoneyList()));
	}

	protected void scrapRefund (CrawlingProduct info) throws Exception {
		helper.click(By.id("buttonRefundView"));
		helper.waitForCSSElement("div.loading");

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		element = helper.waitPresenceOfElementLocated(By.id("annuityRefundPop"));
		wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
				element.findElement(By.cssSelector("div.modal-body > div > div > table > tbody")), By.tagName("tr")));
		elements = element
				.findElement(By.cssSelector("div.modal-body > div > div > table > tbody"))
				.findElements(By.tagName("tr"));

		for (WebElement tr : elements) {

			// 납입기간
			String term 			= tr.findElements(By.tagName("td")).get(0).getText();
			logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			logger.info("해약환급금 크롤링:: 납입기간 :: " + term);

			// 합계 보험료
			String premiumSum 		= tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

			// -----------------------------------------------------------------
			// 공시 환급금
			String returnMoney 		= tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);

			// 공시 환급률
			String returnRate 		= tr.findElements(By.tagName("td")).get(3).getText();
			logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);

			// -----------------------------------------------------------------
			// 평균 환급금
			String returnMoneyAvg 	= tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);

			// 평균 환급률
			String returnRateAvg 	= tr.findElements(By.tagName("td")).get(5).getText();
			logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

			// -----------------------------------------------------------------
			// 최저 환급금
			String returnMoneyMin 	= tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);

			// 최저 환급률
			String returnRateMin 	= tr.findElements(By.tagName("td")).get(7).getText();
			logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
			planReturnMoney.setReturnRateMin(returnRateMin);;
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
			planReturnMoney.setReturnRateAvg(returnRateAvg);

			planReturnMoneyList.add(planReturnMoney);

			if(term.equals(info.napTerm)){
				info.returnPremium = returnMoney.replace("[^0-9]", "");
			}
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

		// 해약환급금 닫기
		helper.click(By.cssSelector("#annuityRefundPop > div > div.modal-footer > button"));

		logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
	}

	protected void inputAssureMoney(CrawlingProduct info) throws Exception {
		String assureMoney = String.valueOf(Integer.parseInt(info.assureMoney) / 10000);
		WebElement element = driver.findElement(By.id("mainAmount"));
		element.click();
		element.sendKeys(Keys.DELETE);
		element.sendKeys(assureMoney);
		element.sendKeys(Keys.TAB);
	}

	protected String getNapCycleName(String napCycle) {
		String napCycleText = "";

		if (napCycle.equals("01")) {
			napCycleText = "월납";
		} else if (napCycle.equals("02")) {
			napCycleText = "년납";
		} else if (napCycle.equals("00")) {
			napCycleText = "일시납";
		}

		return napCycleText;
	}
}
