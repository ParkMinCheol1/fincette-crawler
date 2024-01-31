package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// 2023.11.16 | 최우진 |
public class KLP_DSS_D011 extends CrawlingKLP {

	public static void main(String[] args) {
		executeCommand(new KLP_DSS_D011(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);

		return true;
	}



	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

			/*
			WebDriverWait wait = new WebDriverWait(driver, 5);
			logger.info("상품 개정(보험료인상)안내 팝업이 뜰 때까지 5초 대기");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#rbmmPop00050 > div.cls_area > a:nth-child(2)")));

			try{
				driver.findElement(By.cssSelector("#rbmmPop00050 > div.cls_area > a:nth-child(2)"));
				logger.info("상품 개정(보험료인상)안내 팝업이 있음");
				WaitUtil.waitFor(1);
				driver.findElement(By.cssSelector("#rbmmPop00050 > div.cls_area > a:nth-child(2)")).click();
				WaitUtil.waitFor(1);
			}catch (Exception e){
				logger.info("상품 개정(보험료인상)안내 팝업이 없음");
			}
			*/


			logger.info("생년월일 입력");
			driver.findElement(By.cssSelector("#birth")).sendKeys(info.fullBirth);
			WaitUtil.waitFor(1);


			logger.info("성별선택");
			if (Integer.toString(info.gender).equals("0")) {
				driver.findElement(By.cssSelector("#gndrCdArea > span:nth-child(1) > label")).click();
			}else {
				driver.findElement(By.cssSelector("#gndrCdArea > span:nth-child(2) > label")).click();
			}
			WaitUtil.waitFor(1);

			logger.info("생년월일 입력 후, 보험료계산하기 클릭");
			driver.findElement(By.cssSelector("#commPopApply")).sendKeys(Keys.ENTER);
			helper.waitForCSSElement("#loading_area");
			WaitUtil.waitFor(1);

			driver.findElement(By.cssSelector("#container > div.btn-process > div > div > a.btn-primary.js-btn-active.item")).sendKeys(Keys.ENTER);
			helper.waitForCSSElement("#loading_area");
			WaitUtil.waitFor(2);


			/*
			logger.info("상품 개정(보험료인상)안내 팝업이 뜰 때까지 5초 대기");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#rbmmPop00050 > div.cls_area > a:nth-child(2)")));

			try{
				driver.findElement(By.cssSelector("#rbmmPop00050 > div.cls_area > a:nth-child(2)"));
				logger.info("상품 개정(보험료인상)안내 팝업이 있음");
				WaitUtil.waitFor(1);
				driver.findElement(By.cssSelector("#rbmmPop00050 > div.cls_area > a:nth-child(2)")).click();
				WaitUtil.waitFor(1);
			}catch (Exception e){
				logger.info("상품 개정(보험료인상)안내 팝업이 없음");
			}
			*/


			logger.info("보험료 알아보기 선택");

			elements = driver.findElements(By.cssSelector("#bestList > ul > li"));
			int elementsSize = elements.size();
			for(int i=0; i<elementsSize; i++){
				if(elements.get(i).getText().trim().contains(info.insTerm)){
					logger.info("보기 : "+info.insTerm);
					elements.get(i).click();
				}
			}
			helper.waitForCSSElement("#loading_area");
			WaitUtil.waitFor(1);

			logger.info("선택완료");
			driver.findElement(By.cssSelector("#btn_slctPln")).click();
			helper.waitForCSSElement("#loading_area");
			WaitUtil.waitFor(1);



			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);
			WaitUtil.waitFor(1);
			logger.info("일시납 보험료 가져오기");
			String premium = driver.findElement(By.cssSelector("#rslt_sumPrm")).getText().trim().replaceAll("[^0-9]", "");
			info.treatyList.get(0).monthlyPremium = premium;
			logger.info("초회 보험료 : " + info.treatyList.get(0).monthlyPremium);


			logger.info("해약환급 예시보기 클릭");
			driver.findElement(By.cssSelector("#btn_refundPop")).click();
			helper.waitForCSSElement("#loading_area > div > div > svg");
			WaitUtil.waitFor(2);


			logger.info("해약환급금 크롤링");

			elements = driver.findElements(By.cssSelector("#srrRstList"));
			int trSize = elements.size();

			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

			for(int i=0; i<trSize; i++) {

				logger.info("______해약환급급[{}]_______ ", i);


				String term = elements.get(i).findElement(By.cssSelector("#eprePeri")).getAttribute("innerText").trim(); // 경과기간
				String premiumSum = elements.get(i).findElement(By.cssSelector("#pmtPrm")).getAttribute("innerText").trim(); // 납입보험료
				String returnMoney = elements.get(i).findElement(By.cssSelector("#pbanIratFrcsSrrRstAmt")).getAttribute("innerText").trim(); // 해약환급금
				String returnRate = elements.get(i).findElement(By.cssSelector("#pbanIratFrcsRfdrt")).getAttribute("innerText").trim(); // 해약환급률


				logger.info("|--경과기간: {}", term);
				logger.info("|--납입보험료: {}", premiumSum);
				logger.info("|--해약환급금: {}", returnMoney);
				logger.info("|--환급률: {}", returnRate);


				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender(info.getGenderEnum().name());
				planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

				planReturnMoney.setTerm(term); // 경과기간
				planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
				planReturnMoney.setReturnMoney(returnMoney); // 환급금
				planReturnMoney.setReturnRate(returnRate); // 환급률

				planReturnMoneyList.add(planReturnMoney);

				info.returnPremium = returnMoney.replaceAll("[^0-9]", "");

			}
			logger.info("만기환급금 : "+info.returnPremium+"원");

			info.setPlanReturnMoneyList(planReturnMoneyList);

	}








/*
	@Override
	public boolean doCrawlInsurance(CrawlingProduct info) {

		boolean result = false;

		try {

			logger.info("크롤링시 필요한 옵션정의");
			CrawlingOption option = info.getCrawlingOption();
			option.setBrowserType(BrowserType.Chrome);
			//option.setImageLoad(true);
			info.setCrawlingOption(option);
			startDriver(info);



			try{
				driver.findElement(By.cssSelector("#VP_btn_install > span")).click();
				logger.info("보안프로그램 설치 버튼 클릭됨");
			}catch (Exception e){
				e.printStackTrace();
			}


			// 생년월일
			logger.debug("생년월일 설정");
			helper.doInputBox(By.id("plnnrBrdt"), info.fullBirth);

			// 성별
			logger.debug("성별 설정");
			setGender(info.gender);

			// 흡연
			logger.debug("최근 1년이내 흡연여부 설정");
			setSmoke(info.discount);

			// 보험료 확인/가입
			logger.debug("보험료 확인/가입");
			setConfirmPremium(By.id("fastPayCalc"));

			// 가입금액
			logger.debug("가입금액 설정");
			setPremium(info.assureMoney);

			// 보험기간
			logger.debug("보험기간 설정");
			setInsTerm(By.id("inspdContents"), info.insTerm);

			// 납입기간
			logger.debug("납입기간 설정");
			setNapTerm3(info.napTerm, info.insTerm);

			logger.debug("결과 확인하기");
			confirmResult();

			// 보험료
			logger.debug("보험료 확인하기");
			getPremium("#premiumLabel2", info);

			logger.debug("해약환급금(예시표) 조회");
			getReturns("cancel1", info);

			result = true;
		} catch (Exception e) {

			logger.error("크롤링 에러: " + e.getMessage());
			e.printStackTrace();

			info.totPremium = "0";
			info.treatyList.get(0).monthlyPremium = "0";
			info.returnPremium = "0";
			info.annuityPremium = "0";
			info.errorMsg = e.getMessage();


			CrawlerSlackClient.errorPost(HostUtil.getUsername(),info.productCode, e.getMessage());

		} finally {
			stopDriver(info);
		}

		return result;
	}
*/


}
