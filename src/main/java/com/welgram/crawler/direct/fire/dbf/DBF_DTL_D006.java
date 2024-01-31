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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DBF_DTL_D006 extends CrawlingDBF {		// 다이렉트 참좋은치아사랑보험1904(CM)

	

	public static void main(String[] args) {
		executeCommand(new DBF_DTL_D006(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// 자녀플랜 0 - 만 18세
		// 성인플랜 만 19 - 만 70

		/*int age = Integer.parseInt(info.getAge()); // 만나이
		if (info.planName.contains("성인형") && (age < 18) || (age > 70)) {
			throw new Exception("가입연령 오류: 성인플랜 가입 연령이 아닙니다");
		}

		if (info.planName.contains("자녀형") && age > 19) {
			throw new Exception("가입연령 오류: 자녀플랜 가입 연령이 아닙니다");
		}

		int maxAge = 50;
		if (age > maxAge) {
			logger.info(maxAge + "세를 초과할 경우 특약이 변경되어 스킵 처리");	// 20200831
			return false;
		}*/

		crawlFromHomepage(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setUserData(true);
	}


	public void crawlFromHomepage(CrawlingProduct info) throws Exception {

			// 1차 보장내용 크롤링에 저장할 보기, 납기 리스트를 먼저 스크랩
			//HashMap<String, Object> terms = storeTerms();


			int age = Integer.parseInt(info.getAge()); // 만나이

			if(age < 19){
				driver.findElement(By.cssSelector("#sForm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > ul.intro_tab.clfix > li.li01 > a")).click();
			}else{
				driver.findElement(By.cssSelector("#sForm > div.wrap_contents.clfix > div.wrap_form_area > div.wrap_info_regist.clfix > ul.intro_tab.clfix > li.li02 > a")).click();
			}

			WaitUtil.waitFor(1);

			logger.info("생년월일");
			helper.sendKeys3_check(By.id("birthday"), info.fullBirth);


			logger.info("성별");
			List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(By.name("sxCd"));
			for (WebElement radioBtn : radioBtns) {
				if (radioBtn.getAttribute("value")
						.equals(Integer.toString(info.getGender() == MALE ? 1 : 2))) {
					radioBtn.findElement(By.xpath("ancestor::label")).click();
					logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
					break;
				}
			}

			if(age < 8){
				driver.findElement(By.cssSelector("#jobCd1")).click();
			}
			else if(age >= 8 && age < 19){
				driver.findElement(By.cssSelector("#jobCd2")).click();
			}

			WaitUtil.waitFor(1);

			// 보험료 확인하기
			logger.info("보험료 확인하기 버튼 클릭");
			clickByLinkText("보험료 확인하기");

			WaitUtil.waitFor(4);
			helper.waitForCSSElement(".loadmask");



			// 가입형태 : 실속형 | 고급형
			logger.info("가입형태");
			String textTypeNumber = "02";
			if (info.textType.equals("실속형")) {
				textTypeNumber = "01";
				driver.findElement(By.id("pdcPanCd1")).sendKeys(Keys.ENTER);
				driver.findElement(By.id("pdcPanCd1")).click();    // 실속형
				logger.info("선택 : " + info.textType);
			} else if (info.textType.equals("고급형")) {
				textTypeNumber = "03";
				driver.findElement(By.id("pdcPanCd3")).sendKeys(Keys.ENTER);
				driver.findElement(By.id("pdcPanCd3")).click();    // 고급형
				logger.info("선택 : " + info.textType);
			}
			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(2);


			loopTreatyDtlList(info, textTypeNumber);


			// 보험기간
			logger.info("보험기간");
			setRadioBtnByText(By.name("selArcTrm"), info.insTerm);
			WaitUtil.waitFor(3);

			// 납입기간
			logger.info("납입기간");
			setRadioBtnByText(By.name("selPymTrm"), info.napTerm);
			WaitUtil.waitFor(3);

			//다시계산 버튼이 있는 겨우 클릭
			reCompute();

			// 월 보험료
			logger.info("월 보험료");
			String premium;
		    premium = driver.findElement(By.cssSelector("#totPrm")).getText().replace(",", "").replace("원", "");
		    info.treatyList.get(0).monthlyPremium = premium;
		    logger.info("월 보험료 확인 : " + premium);

			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);
			WaitUtil.waitFor(1);

			// 예상해약환급금 버튼 클릭
			logger.info("해약환급금");
			getReturnMoneyList(info);
	}


	//특약 loop
	protected void loopTreatyDtlList(CrawlingProduct info,String textTypeNumber) throws InterruptedException {

		List<String> webTreatyList = new ArrayList<>();
		List<String> apiTreatyList = new ArrayList<>();

		elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > div.plan-fix > div.plan-fix-body > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));

		for (WebElement treatyList : elements) {
			webTreatyList.add(treatyList.findElement(By.cssSelector("span")).getText());

			logger.info("특약명 : "+treatyList.findElement(By.cssSelector("span")).getText());
		}


		for(CrawlingTreaty name : info.treatyList){
			String treatyNameSave = name.treatyName;
			String[] treatyNameSplitSave = treatyNameSave.split(" - ");

			logger.info("특약명 : "+treatyNameSplitSave[0]);
			apiTreatyList.add(treatyNameSplitSave[0]);
		}


		webTreatyList.removeAll(apiTreatyList);

		// 특약 loop
		for (WebElement selectSignup : elements) {

			logger.info("특약체크 중....");

			if (selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("필수가입") || selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("")) {
				continue;
			}

			for (String treatyName : webTreatyList) {
				if (selectSignup.findElement(By.cssSelector("span")).getText().equals(treatyName)) {
					if (selectSignup.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box on")) {
						WebElement signupClick = selectSignup.findElement(By.cssSelector("ul > li.signup > a"));
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupClick);
						webTreatyList.remove(treatyName);
						WaitUtil.waitFor(1);
						break;
					}
				}
			}
		}
	}


	protected void getReturnMoneyList(CrawlingProduct info) throws Exception {

		// 해약환급금 관련 Start
		logger.info("해약환급금 예시 버튼 클릭");
		driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_total.ui_plan_slider > div.right_plan > dl.plan_money > dd > a")).click();
		helper.waitForCSSElement(".loadmask");
		WaitUtil.waitFor(1);

		logger.info("해약환급금 테이블선택");
		elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#tbodyExCancel > tr")));

		// 주보험 영역 Tr 개수만큼 loop

		int loop = 0;
		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		for (WebElement tr : elements) {
			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			String term = tr.findElements(By.tagName("th")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
			String returnMoneyMin = tr.findElements(By.tagName("td")).get(1).getText();
			String returnRateMin = tr.findElements(By.tagName("td")).get(2).getText();
			String returnMoneyAvg = tr.findElements(By.tagName("td")).get(3).getText();
			String returnRateAvg = tr.findElements(By.tagName("td")).get(4).getText();
			String returnMoney = tr.findElements(By.tagName("td")).get(5).getText();
			String returnRate = tr.findElements(By.tagName("td")).get(6).getText();


			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
			planReturnMoney.setReturnRateMin(returnRateMin);
			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
			planReturnMoney.setReturnRateAvg(returnRateAvg);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);

			planReturnMoneyList.add(planReturnMoney);
			logger.info("------------------------------------");
			logger.info(term + " 경과기간 :: " + term);
			logger.info(term + " 납입보험료 :: " + premiumSum);
			logger.info(term + " 최저해약환급금 :: " + returnMoneyMin);
			logger.info(term + " 최저해약환급률 :: " + returnRateMin);
			logger.info(term + " 평균해약환급금 :: " + returnMoneyAvg);
			logger.info(term + " 평균해약환급률 :: " + returnRateAvg);
			logger.info(term + " 현재해약환급금 :: " + returnMoney);
			logger.info(term + " 현재해약환급률 :: " + returnRate);
			logger.info("------------------------------------");

			loop++;

			if(elements.size() == loop){
				info.returnPremium = returnMoney.replace(",", "").replace("원", "");
				logger.info("만기환급금 : "+info.returnPremium);
			}
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);
		// 해약환급금 관련 End
	}

}
