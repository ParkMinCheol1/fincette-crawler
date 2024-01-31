package com.welgram.crawler.direct.fire.dbf;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.NotFoundPensionAgeException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DBF_ASV_D003 extends CrawlingDBF {		// 연금저축손해보험 유배당 다이렉트 연금보험1904(CM)

	public static void main(String[] args) {
		executeCommand(new DBF_ASV_D003(), args);
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
		option.setUserData(true);
	}


	public void crawlFromHomepage(CrawlingProduct info) throws Exception {

			logger.info("생년월일");
			setBirth(info.fullBirth);
			WaitUtil.loading(1);

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
			WaitUtil.loading(2);

			logger.info("보험료 확인하기 버튼 클릭");
			clickByLinkText("보험료 확인하기");
			WaitUtil.loading(2);


			// 2번째 페이지로 이동했음
			logger.info("라디오 버튼 클릭을 방해하는 요소 정리");
			helper.executeJavascript("$(\".wrap_quick\").hide()");
			WaitUtil.loading(3);

			// 납입할 보험료로 설계하기 탭 선택 (기본으로 선택되어 있음)
				logger.info("납입방법");
				doSelect("납입방법", info.napCycle.equals("01") ? "월납" : "연납");
				WaitUtil.loading(2);


			logger.info("월 납입보험료 입력");
			// main doInputBox 메서드에서 waitfor 삭제하고 사용해서 코드량을 줄일 수 있음
			int assureMoney = Integer.parseInt(info.assureMoney) / 10000;
			helper.waitElementToBeClickable(By.id("prm")).clear();
			driver.findElement(By.id("prm")).sendKeys(Integer.toString(assureMoney));
			WaitUtil.loading(2);


			try {
				logger.info("보험료납입기간 : "+info.napTerm);
				doSelect("보험료납입기간", info.napTerm);
				WaitUtil.loading(2);
			}catch(Exception e) {
				throw new NotFoundPensionAgeException(e.getMessage());
			}


			try {
				logger.info("연금개시나이 : "+info.annuityAge +"세");
				doSelect("연금개시나이", info.annuityAge +"세");
				WaitUtil.loading(2);
			}catch(Exception e) {
				throw new NotFoundPensionAgeException(e.getMessage());
			}

		try {
			logger.info("연금개시나이 : "+info.annuityAge +"세");
			doSelect("연금개시나이", info.annuityAge +"세");
			WaitUtil.loading(2);
		}catch(Exception e) {
			throw new NotFoundPensionAgeException(e.getMessage());
		}


			logger.info("연금수령기간: 10년");

			if(info.annuityType.contains("10년")){

				doSelect("연금수령기간", "10년");
				WaitUtil.loading(2);

				logger.info("연금수령주기: 년단위");
				//중간에 select안되는 문제로 수령주기만 임시로 직통으로 만들어서 사용
				annuityPeriodSelect();
				WaitUtil.loading(2);
				//doSelect("연금수령주기", "년단위");

			}

			else if(info.annuityType.contains("25년")){

				doSelect("연금수령기간", "25년");
				WaitUtil.loading(2);

				logger.info("연금수령주기: 년단위");
				//중간에 select안되는 문제로 수령주기만 임시로 직통으로 만들어서 사용
				annuityPeriodSelect();
				WaitUtil.loading(2);
				//doSelect("연금수령주기", "년단위");

			}


			logger.info("연금수령액 확인하기 버튼 클릭");
			clickByLinkText("연금수령액 확인하기");

			WaitUtil.loading(2);
			logger.info("예상연금수령액");
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("result_amt")));
			WaitUtil.loading(2);

			String premium = helper.waitPresenceOfElementLocated(By.id("result_amt")).getText().trim().replaceAll("[^0-9]", "");
			WaitUtil.loading(2);
			String fixedAnnuityPremium = premium;


			if(info.annuityType.contains("10년")){

				//연금테이블에 값추가
				PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
				planAnnuityMoney.setFxd10Y(driver.findElement(By.id("result_amt")).getText().trim().replaceAll("[^0-9]", ""));		//확정 10년
				logger.info("확정 10년 : "+planAnnuityMoney.getFxd10Y());
				info.planAnnuityMoney = planAnnuityMoney;

			}

			else if(info.annuityType.contains("25년")){

				//연금테이블에 값추가
				PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
				planAnnuityMoney.setFxd25Y(driver.findElement(By.id("result_amt")).getText().trim().replaceAll("[^0-9]", ""));		//확정 10년
				logger.info("확정 25년 : "+planAnnuityMoney.getFxd25Y());
				info.planAnnuityMoney = planAnnuityMoney;

			}


			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);

			//확정타입이 있는 경우는 여기에 추가
			if(info.annuityType.contains("확정")){

				logger.info("예상 확정연금수령액 : " + fixedAnnuityPremium);
				WaitUtil.loading(1);

				info.fixedAnnuityPremium = fixedAnnuityPremium;
				WaitUtil.loading(1);
			}
				//종신타입이 아니더라도 일단 넣어봄
				info.annuityPremium = fixedAnnuityPremium;
				WaitUtil.loading(1);

			/*//종신타입이 있는 경우는 여기에 추가
			else if(info.annuityType.contains("종신")){
				info.annuityPremium = fixedAnnuityPremium;
				WaitUtil.loading(1);
			}*/



			logger.info("해약환급금 예시버튼 클릭");
			//doClick(By.cssSelector("dl.annuity_total > dd > a"));
			//doClick 함수의 경우 안되는경우가 많이 발생 아래와 같은 코드로 변경필요
			//해약환급금 버튼이 보일때까지 대기
			element = helper.waitVisibilityOfElementLocated(By.cssSelector("#resultDiv > div.plan_total.ui_plan_slider > dl > dd > a"));
			element.click();
			logger.info("해약환급금 예시 클릭");
			helper.waitForCSSElement(".loadmask");
			WaitUtil.loading(2);

			// 경과 기간 :10년 , 3번째 공시이율 열에 해당하는 해약환급금 스크랩
			/*
			String period = info.napTerm;
			elements = driver.findElement(By.id("tbodyExCancel")).findElements(By.cssSelector("tr th"));
			for (WebElement element : elements) {
				if (element.getText().trim().equals(period)) {
					info.returnPremium = element.findElement(By.xpath("following-sibling::td[6]")).getText()
							.trim().replaceAll("[^0-9]", "");
					logger.info("해약환급금 : " + info.returnPremium);
					break;
				}
			}
			*/

			// 해약환급금 테이블 크롤링
			logger.info("해약환급금 저장");
			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

			int loop = 0;
			elements = helper.waitPresenceOfElementLocated(By.id("tbodyExCancel")).findElements(By.tagName("tr"));
			for (WebElement tr : elements) {

				String term = helper.waitVisibilityOf(tr.findElement(By.tagName("th"))).getText().trim().replaceAll(" ", "");;
				logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
				logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
				String premiumSum = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(0)).getText()
						.replaceAll("[^0-9]", "");
				logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

				String returnMoneyMin = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(1)).getText()
						.replaceAll("[^0-9]", "");
				logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
				String returnRateMin = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(2)).getText();
				logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

				String returnMoneyAvg = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(3)).getText()
						.replaceAll("[^0-9]", "");
				logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
				String returnRateAvg = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(4)).getText();
				logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

				String returnMoney = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(5)).getText()
						.replaceAll("[^0-9]", "");
				logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
				String returnRate = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(6)).getText();
				logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
//				planReturnMoney.setGender(Gender.남자.equals(info.getGender()) ? "M" : "F");
//				planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoneyMin(returnMoneyMin);
				planReturnMoney.setReturnRateMin(returnRateMin);
				planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
				planReturnMoney.setReturnRateAvg(returnRateAvg);
				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);

				/*
				 * // 기존로직 추가 ==> 이거 이상함 if (term.equals(info.getNapTerm())){ info.returnPremium
				 * = premiumSum; }
				 */

				planReturnMoneyList.add(planReturnMoney);

				/*if(info.napTerm.contains(term)){
					info.returnPremium = returnMoney;
				}*/
				loop++;
				if(elements.size() == loop){
					info.returnPremium = returnMoney;
				}


			}
			info.setPlanReturnMoneyList(planReturnMoneyList);

			logger.info("만기환급금 : "+info.returnPremium);
			logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));

			// 납입보험료 저장
			for (CrawlingTreaty crawlingTreaty : info.treatyList) {
				crawlingTreaty.monthlyPremium = info.assureMoney;
			}

	}

	protected void setBirth(String birth) throws InterruptedException {
		element = helper.waitPresenceOfElementLocated(By.id("birthday"));
		element.clear();
		element.sendKeys(birth);
	}
}
