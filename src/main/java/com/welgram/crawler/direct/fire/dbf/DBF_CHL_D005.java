package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;



public class DBF_CHL_D005 extends CrawlingDBFDirect {

	public static void main(String[] args) { executeCommand(new DBF_CHL_D005(), args); }


	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		driver.manage().window().maximize();

		logger.info("이벤트 팝업 체크");
		checkPopUp();
		
		logger.info("생년월일");
		setBirthday(By.xpath("//span[@class='wrap_inp']/input[@name='chdBirth']"), info.fullBirth);

		logger.info("성별");
		setGender(info);

		logger.info("자녀 취학정보");
		int age = Integer.parseInt(info.age);
		if (age < 8) {    // 미취학 아동
			driver.findElement(By.id("jobCd1")).click();    // 미취학 아동
			logger.info("미취학 아동 선택");
		} else {    // 초/중/고 학생
			driver.findElement(By.id("jobCd2")).click();    // 초/중/고 학생
		}
		WaitUtil.waitFor(2);

		logger.info("보험료 확인하기 버튼 클릭");
		driver.findElement(By.xpath("//div[@class='btn_foot']/a[@class='btns btn_calc btn_active']")).click();
		WaitUtil.waitFor(2);

		// 가입형태 : 실속형 | 고급형
		waitDirectLoadingImg();
		logger.info("가입형태");
		setProductType(info);
		WaitUtil.waitFor(2);

		//특약 체크
		waitDirectLoadingImg();
		logger.info("특약 체크");
		setTreaties(info.treatyList);

		// 보험기간
		logger.info("보험기간");
		setInsTerm("selArcTrm", info.insTerm);
		WaitUtil.waitFor(2);

		// 납입기간
		logger.info("납입기간");
		setNapTerm("selPymTrm", info.napTerm);
		WaitUtil.waitFor(2);

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
		waitDirectLoadingImg();

		// 예상해약환급금 버튼 클릭
		logger.info("해약환급금");
		getReturnMoney(info, By.cssSelector("#smPrmDiv > dl > dd > a"));

		return true;
	}


	@Override
	public void setGender(Object... obj) throws SetGenderException {
		try {
			CrawlingProduct info = (CrawlingProduct) obj[0];
			int gender = info.gender;
			String expectedGenderText = (gender == MALE) ? "남자" : "여자";

			driver.findElement(By.xpath("//*[@id='sForm']/div[1]/div[1]/div[1]/div[1]/dl[1]/dd/ul/li/label//span[normalize-space()='"+expectedGenderText+"']")).click();

		} catch (Exception e) {
			throw new SetGenderException(e.getMessage());
		}
	}


	@Override
	public void setProductType(Object... obj) throws SetProductTypeException {
		try {
			CrawlingProduct info = (CrawlingProduct) obj[0];
			String expectedProductTypeText = info.textType;

			WebElement $productTypeText = driver.findElement(By.xpath("//*[@id='sForm']/div[1]/div[1]/div[2]/div[1]/div/ul/li[3]/div/div/label/span[contains(.,'"+expectedProductTypeText+"')]"));
			$productTypeText.click();

		} catch (Exception e) {
			throw new SetProductTypeException(e.getMessage());
		}
	}


	@Override
	public void setTreaties(Object... obj) throws SetTreatyException {
		try {
			List<CrawlingTreaty> welgramTreatyList = (List<CrawlingTreaty>) obj[0];

			List<WebElement> targetTreatyEl = driver.findElements(By.xpath("//div[@class='plan-fix-body']/ul/li[@class[contains(., 'plan02  on')]]//dd[@class='on_altm_0']"));
			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();    // 원수사 특약명, 특약금액 리스트

			for (WebElement treatyDd : targetTreatyEl) {
                // 원수사 특약명, 특약금액 리스트
                WebElement treatyDdName = treatyDd.findElement(By.tagName("span"));
                WebElement treatyDdMoney = treatyDd.findElement(By.xpath(".//li[@class='pmoney']/span"));

				//원수사 특약명을 가져오기 위해 특약명이 보이도록 스크롤 처리를 해야함.
				String treatyNameDdName = treatyDdName.getText().trim();
				logger.info("특약명 : {}", treatyNameDdName);

				String treatyMoneySpan = treatyDdMoney.getText();
				treatyMoneySpan = treatyMoneySpan.substring(0, treatyMoneySpan.indexOf("원"));
				treatyMoneySpan = String.valueOf(MoneyUtil.getDigitMoneyFromHangul(treatyMoneySpan));
				logger.info("money :: {}", treatyMoneySpan);

				CrawlingTreaty targetTreaty = new CrawlingTreaty();
				targetTreaty.setTreatyName(treatyNameDdName);
				targetTreaty.setAssureMoney(Integer.parseInt(treatyMoneySpan));
				targetTreatyList.add(targetTreaty);

            }

			compareTreaties(targetTreatyList, welgramTreatyList);

		} catch (Exception e) {
			throw new SetTreatyException(e.getMessage());
		}
	}


	// 다시계산하기 버튼이 있는경우 클릭 그외 바로 넘김
	protected void reCompute() throws Exception {
		element = driver.findElement(By.linkText("다시 계산"));

		if(element.isDisplayed()){
			logger.info("다시계산버튼클릭");
			element.click();
			helper.waitForCSSElement(".loadmask");
		} else{
			logger.info("다시계산 버튼 없음");
		}
		WaitUtil.waitFor(1);
	}


	protected void getReturnMoney(CrawlingProduct info, By byReturnBtn) throws Exception {

		// 해약환급금 관련 Start
		logger.info("해약환급금 예시 버튼 클릭");
		//element = waitElementToBeClickable(By.linkText("해약환급금 예시"));
		element = helper.waitElementToBeClickable(byReturnBtn);
		element.click();

		helper.waitForCSSElement(".loadmask");

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