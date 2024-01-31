package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class DBF_DSS_D005 extends CrawlingDBF {        // 무배당 프로미라이프 다이렉트 굿바이 미세먼지건강보험2301(CM)

	

	public static void main(String[] args) {
		executeCommand(new DBF_DSS_D005(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setUserData(true);
	}


	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

			logger.info("생일 입력 : " + info.fullBirth);
			driver.findElement(By.cssSelector("#birthday")).sendKeys(info.fullBirth);
			WaitUtil.waitFor(1);


			logger.info("운전형태 선택");
			driver.findElement(By.cssSelector("#oprtVhDvcd1")).click();
			WaitUtil.waitFor(1);


			logger.info("성별선택 : " + info.gender);
			if (info.gender == 0) {
				driver.findElement(By.cssSelector("#sForm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(2) > dd > ul > li:nth-child(1) > label > span")).click();
			} else {
				driver.findElement(By.cssSelector("#sForm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > dl:nth-child(2) > dd > ul > li:nth-child(2) > label > span")).click();
			}
			WaitUtil.waitFor(1);


			logger.info("직업 보험사무원 선택");
			driver.findElement(By.cssSelector("#tmpJobNm")).click();
			WaitUtil.waitFor(1);
			driver.findElement(By.cssSelector("#_name_job_tab_ > div:nth-child(1) > div.wrap_inp.clear_input_box > input")).sendKeys("보험 사무원");
			WaitUtil.waitFor(1);
			driver.findElement(By.cssSelector("#_name_job_tab_ > div:nth-child(1) > a > span")).click();
			WaitUtil.waitFor(1);
			driver.findElement(By.cssSelector("#job_32021")).click();
			WaitUtil.waitFor(1);
			driver.findElement(By.cssSelector("#ltmJobNmChk")).click();
			WaitUtil.waitFor(1);
			driver.findElement(By.cssSelector("#_btn_job_complete_ > a")).click();
			WaitUtil.waitFor(2);



			logger.info("보험료 확인하기 클릭");
			driver.findElement(By.cssSelector("#sForm > div.wrap_contents.clfix > div.wrap_form_area > div.btn_foot > a.btns.btn_calc.btn_active > span")).click();
			helper.waitForCSSElement(".loadmask");
			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(2);

			String textPlan = "";
			String textPlanType = "";
			ArrayList<String> textTreatyType = new ArrayList<>();

			String[] textTypeSplit = info.textType.split(",");
			if(textTypeSplit.length != 1 || textTypeSplit.length != 0){

				textPlan = textTypeSplit[0];
				textPlanType = textTypeSplit[1];

				for(int i=2; i<textTypeSplit.length; i++){
					logger.info("들어가 있는 값: "+textTypeSplit[i]);
					textTreatyType.add(textTypeSplit[i]);
				}

				for(int i=0; i<textTreatyType.size(); i++){
					logger.info("리스트에 들어 있는 값 : "+textTreatyType.get(i));
				}
			}

			logger.info("플랜의 타입 : "+textPlan);
			logger.info("플랜의 서브 타입 : "+textPlanType);
			String textType = "";

			//플랜 선택
			if (textPlan.equals("실속형")) {
				textType = "01";
			}

			if (textPlan.equals("표준형")) {
				//기본적으로 체크되어 있는 플랜
				textType = "02";
			}

			if (textPlan.equals("고급형")) {
				textType = "03";
			}

			//특약 체크
			//treatyCheck(info.treatyList, textType);

			logger.info("21년 7월 16일 기준으로 질병보험의 케이스를 정할 때 이 메소드를 사용");
			subCategoryTreatyCheck(textTreatyType, textType);



			logger.info("보험기간 선택 : " + info.insTerm);
			if (info.insTerm.equals("20년")) {
				driver.findElement(By.cssSelector("#selArcTrm3")).click();
			}
			if (info.insTerm.equals("10년")) {
				driver.findElement(By.cssSelector("#selArcTrm2")).click();
			}
			if (info.insTerm.equals("3년")) {
				driver.findElement(By.cssSelector("#selArcTrm1")).click();
			}
			WaitUtil.waitFor(1);


			logger.info("납입기간 선택 : " + info.napTerm);
			if (info.napTerm.equals("20년")) {
				driver.findElement(By.cssSelector("#selPymTrm3")).click();
			}
			if (info.napTerm.equals("10년")) {
				driver.findElement(By.cssSelector("#selPymTrm2")).click();
			}
			if (info.napTerm.equals("3년")) {
				driver.findElement(By.cssSelector("#selPymTrm1")).click();
			}
			WaitUtil.waitFor(1);


			//다시계산 버튼이 있는 경우 클릭
			reComputeCssSelect(By.cssSelector("#sForm > div.wrap_contents > div.plan_total.ui_plan_slider > div.right_plan_again > a"));


			logger.info("월 보험료");
			String premium;
			premium = driver.findElement(By.cssSelector("#totPrm")).getText().replace(",", "").replace("원", "");
			info.treatyList.get(0).monthlyPremium = premium;
			logger.info("월 보험료 확인 : " + premium);

			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.cssSelector("#totPrm")));

			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);
			WaitUtil.waitFor(1);


			logger.info("해약환급금 예시 클릭");
			driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_total.ui_plan_slider > div.right_plan > dl.plan_money > dd > a > span")).click();
			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(2);


			// 해약환급금 테이블 크롤링
			logger.info("해약환급금 저장");
			getReturnPremium(info);

	}


	private void subCategoryTreatyCheck(ArrayList<String> textTreatyType, String textType) throws Exception {

		elements = driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > div.complexlist > ul")).findElements(By.cssSelector("li.com_list"));
		int elementsSize = elements.size();

		driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > div.complexlist > div > button")).click();
		WaitUtil.waitFor(1);

		for(int i=0; i<elementsSize; i++){
			for(int j=0; j<textTreatyType.size(); j++){

				elements = driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > div.complexlist > ul")).findElements(By.cssSelector("li.com_list"));

				String webName = elements.get(i).findElement(By.cssSelector("div.com_list_box.clfix > div.plan_tit > label > span")).getText().trim();

				if(webName.equals("기본(필수)")){
					//driver.findElement(By.cssSelector("#group_0" + i * 10)).click();
					elements.get(i).findElement(By.cssSelector("div.com_list_box.clfix > div.toggle")).click();
					WaitUtil.waitFor(1);
					if(!textType.equals("02")) {
						elements.get(i).findElement(By.cssSelector("div.plan_select_wrap > ul > li.plan" + textType + " > dl > dt > div > label > input[type=radio]")).click();
						WaitUtil.waitFor(1);
					}
					break;
				}

				if(webName.equals(textTreatyType.get(j))){
					driver.findElement(By.cssSelector("#group_" + i * 10)).click();
					WaitUtil.waitFor(1);
					if(!textType.equals("02")) {
						elements.get(i).findElement(By.cssSelector("div.plan_select_wrap > ul > li.plan" + textType + " > dl > dt > div > label > input[type=radio]")).click();
						WaitUtil.waitFor(1);
					}
				}
			}
		}
	}



	private void treatyCheck(List<CrawlingTreaty> treatyList, String textType) throws Exception {

		//카테고리 다 열기
		List<WebElement> toggleBtns = driver.findElements(By.className("toggle"));
		int liNumber = 1;
		for(WebElement btn : toggleBtns) {
			helper.waitElementToBeClickable(btn).click();
			WaitUtil.waitFor(1);
			if(!textType.equals("02")) {
				driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > div.complexlist > ul > li:nth-child("+liNumber+") > div.plan_select_wrap > ul > li.plan"+textType+" > dl > dt > div > label > input[type=radio]")).sendKeys(Keys.SPACE);
				driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > div.complexlist > ul > li:nth-child("+liNumber+") > div.plan_select_wrap > ul > li.plan"+textType+" > dl > dt > div > label > input[type=radio]")).click();
				WaitUtil.waitFor(1);
				liNumber++;
			}
		}

		List<WebElement> targetTreatyEl = driver.findElements(By.cssSelector(".plan_select.ui_plan_select dd div strong"));
		List<String> targetTreatyList = new ArrayList<String>();	//web특약
		List<String> myTreatyList = new ArrayList<String>();		//내 특약


		for(WebElement target : targetTreatyEl) {
			targetTreatyList.add(target.getText());

		}

		for(CrawlingTreaty myTreaty : treatyList) {
			myTreatyList.add(myTreaty.treatyName);
		}

		targetTreatyList.removeAll(myTreatyList);

		//미가입 처리할 특약들만 돈다.
		for(int i=0; i<targetTreatyList.size(); i++) {
			boolean hasJoinBtn = true;			//가입여부버튼
			String treatyName = targetTreatyList.get(i);	//미가입시킬 특약명
			WebElement findEl = driver.findElement(By.xpath("//strong[contains(text(), '" + treatyName + "')]"));
			List<WebElement> ddEl = findEl.findElement(By.xpath("ancestor::dl")).findElements(By.tagName("dd"));

			int ddIdx = 0;
			for(WebElement dd : ddEl) {
				if(dd.findElement(By.cssSelector("strong")).getText().equals(treatyName)) {
					break;
				}
				ddIdx++;
			}

			WebElement joinEl = findEl.findElements(By.xpath("ancestor::ul[@class='plan_select ui_plan_select']/li")).get(2);
			WebElement joinBtn = null;
			String signOnOff = "";

			try {
				joinBtn = joinEl.findElements(By.tagName("dd")).get(ddIdx).findElement(By.cssSelector(".signup a"));

				signOnOff = joinEl.findElements(By.tagName("dd")).get(ddIdx).findElement(By.cssSelector("em[class=hide_txt]")).getText();
			}catch(NoSuchElementException e) {
				hasJoinBtn = false;
				logger.info("해당 특약은 필수가입 특약입니다.");
			}


			//가입여부버튼이 존재하고, 가입으로 세팅되어있을 경우에만 미가입으로 변경이 가능하다.
			if(hasJoinBtn && signOnOff.equals("ON")) {
				joinBtn.click();
				WaitUtil.waitFor(1);
			}

		}
	}


	// 해약환급금 : 사용중
	protected void getReturnPremium(CrawlingProduct info) {

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

		elements = helper.waitPresenceOfElementLocated(By.id("tbodyExCancel")).findElements(By.tagName("tr"));
		for (WebElement tr : elements) {

			String term = helper.waitVisibilityOf(tr.findElement(By.tagName("th"))).getText();
			logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
			String premiumSum = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(0)).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

			String returnMoneyMin = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(1)).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
			String returnRateMin = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(2)).getText();
			logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

			String returnMoneyAvg = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(3)).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
			String returnRateAvg = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(4)).getText();
			logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

			String returnMoney = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(5)).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
			String returnRate = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(6)).getText();
			logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
			planReturnMoney.setReturnRateMin(returnRateMin);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
			planReturnMoney.setReturnRateAvg(returnRateAvg);
			planReturnMoneyList.add(planReturnMoney);

			info.returnPremium = returnMoneyAvg;
		}
		info.setPlanReturnMoneyList(planReturnMoneyList);

	}
}
