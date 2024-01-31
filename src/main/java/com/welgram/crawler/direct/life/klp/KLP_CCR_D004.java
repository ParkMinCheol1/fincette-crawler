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


public class KLP_CCR_D004 extends CrawlingKLP {

	public static void main(String[] args) {
		executeCommand(new KLP_CCR_D004(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setBrowserType(BrowserType.Chrome);
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

			logger.info("보험료 계산하기 클릭");
			//driver.findElement(By.cssSelector("#btnResult2")).sendKeys(Keys.ENTER);
			//driver.findElement(By.cssSelector("#birth")).sendKeys(Keys.ENTER);
			//driver.findElement(By.cssSelector("#btnResult")).click();
			WaitUtil.waitFor(2);

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


			logger.info("흡연체 선택");
			driver.findElement(By.cssSelector("#rcmmInfo > span.ipt-wrap.in-block.ml20 > span:nth-child(1) > label")).click();
			WaitUtil.waitFor(1);
			helper.waitForCSSElement("#loading_area");


			logger.info("보험료 알아보기 선택");
			WaitUtil.waitFor(3);
			//driver.findElement(By.cssSelector("#fee_self")).click();
			driver.findElement(By.cssSelector("#bestList > ul > li:nth-child(1)")).click();
			WaitUtil.waitFor(1);

			logger.info("선택완료");
			driver.findElement(By.cssSelector("#btn_slctPln")).click();
			helper.waitForCSSElement("#loading_area");
			WaitUtil.waitFor(1);

			logger.info("일반암 진단 시 버튼 input클릭");
			driver.findElement(By.cssSelector("#insuAmt1_dummy")).click();
			helper.waitForCSSElement("#loading_area");
			WaitUtil.waitFor(1);

		boolean result;
		try {
				result = false;
				elements = driver.findElements(By.cssSelector("body > div.mbsc-mobiscroll.dw-bottom.dw-liq.dw-select > div > div.dw.dw-ltr > div > div.dwcc > div > div > div > div > div.dwww > div.dww > div > div > div"));
				int elementsSize = elements.size();

				int selectedNumber = 0;
				logger.info("선택해야하는 진단보험금 가격 : " + info.assureMoney);

				/*
				WebElement size = driver.findElement(By.cssSelector("div.dww"));
				((JavascriptExecutor) driver).executeScript("arguments[0].style.height = '500px'", size);

				WaitUtil.waitFor(1);

				WebElement divSize = driver.findElement(By.cssSelector("div.dw-ul"));
				((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style', 'margin-top:250px')", divSize);
				*/

				for (int i = 0; i < elementsSize; i++) {

					logger.info("에어리어 셀렉티드 : "+elements.get(i).getAttribute("aria-selected"));

					if(elements.get(i).getAttribute("aria-selected").equals("true")){
						selectedNumber = i;
						break;
					}
				}

				logger.info("selectedNumber의 값 : "+selectedNumber);
				logger.info("selectedNumber의 data-val : "+elements.get(selectedNumber).getAttribute("data-val").trim());

				if(Integer.parseInt(info.assureMoney) <= Integer.parseInt(elements.get(selectedNumber).getAttribute("data-val").trim())) {

					for(int i=selectedNumber; i>=0; i--){

						logger.info("진단보험금 : " + elements.get(i).getText().trim());
						if (info.assureMoney.equals(elements.get(i).getAttribute("data-val").trim())) {
							logger.info("선택한 진단보험금 : " + elements.get(i).getAttribute("data-val").trim());
							result = true;
							elements.get(i).click();
							WaitUtil.waitFor(1);
							break;
						}
						elements.get(i-1).click();
						WaitUtil.waitFor(1);
					}
					if (!result) {
						throw new Exception("진단보험금 : " + info.assureMoney + "을 선택할 수 없습니다.");
					}
				}
				else if(Integer.parseInt(info.assureMoney) >= Integer.parseInt(elements.get(selectedNumber).getAttribute("data-val").trim())){
						for (int i = 0; i < elementsSize; i++) {

							logger.info("진단보험금 : " + elements.get(i).getText().trim());

							if (info.assureMoney.equals(elements.get(i).getAttribute("data-val").trim())) {
								logger.info("선택한 진단보험금 : " + elements.get(i).getAttribute("data-val").trim());
								result = true;
								elements.get(i).click();
								WaitUtil.waitFor(1);
								break;
							}
							elements.get(i+1).click();
							WaitUtil.waitFor(1);
						}
						if (!result) {
							throw new Exception("진단보험금 : " + info.assureMoney + "을 선택할 수 없습니다.");
						}
					}
			}catch (Exception e){
				throw e;
			}

			logger.info("확인버튼 클릭");
			driver.findElement(By.cssSelector("body > div.mbsc-mobiscroll.dw-bottom.dw-liq.dw-select > div > div.dw.dw-ltr > div > div.dwbc > div.dwbw.dwb-s.ui-complete")).click();
			WaitUtil.waitFor(2);




			logger.info("보험기간 선택");
			driver.findElement(By.cssSelector("#inspd_dummy")).click();
			WaitUtil.waitFor(2);

			try {
				result = false;
				elements = driver.findElements(By.cssSelector("body > div.mbsc-mobiscroll.dw-bottom.dw-liq.dw-select > div > div.dw.dw-ltr > div > div.dwcc > div > div > div > div > div.dwww > div.dww > div > div > div"));
				int insTermSize = elements.size();

				int selectedNumber = 0;
				logger.info("클릭해야 하는 보험기간 : " + info.insTerm);

				String insTermStr = info.insTerm.replaceAll("[^0-9]", "").trim();


				for (int i = 0; i < insTermSize; i++) {

					logger.info("에어리어 셀렉티드 : "+elements.get(i).getAttribute("aria-selected"));

					if(elements.get(i).getAttribute("aria-selected").equals("true")){
						selectedNumber = i;
						break;
					}
				}

				logger.info("selectedNumber의 값 : "+selectedNumber);
				logger.info("selectedNumber의 data-val : "+elements.get(selectedNumber).getAttribute("data-val").trim());

				if(Integer.parseInt(insTermStr) <= Integer.parseInt(elements.get(selectedNumber).getAttribute("data-val").trim())) {

					for(int i=selectedNumber; i>=0; i--){

						logger.info("진단보험금 : " + elements.get(i).getText().trim());

						if (insTermStr.equals(elements.get(i).getAttribute("data-val").trim())) {
							logger.info("선택한 진단보험금 : " + elements.get(i).getAttribute("data-val").trim());
							result = true;
							elements.get(i).click();
							WaitUtil.waitFor(1);
							break;
						}
						elements.get(i-1).click();
						WaitUtil.waitFor(1);
					}
					if (!result) {
						throw new Exception("진단보험금 : " + info.insTerm + "을 선택할 수 없습니다.");
					}
				}
				else if(Integer.parseInt(insTermStr) >= Integer.parseInt(elements.get(selectedNumber).getAttribute("data-val").trim())){
					for (int i = 0; i < insTermSize; i++) {

						logger.info("진단보험금 : " + elements.get(i).getText().trim());

						if (insTermStr.equals(elements.get(i).getAttribute("data-val").trim())) {
							logger.info("선택한 진단보험금 : " + elements.get(i).getAttribute("data-val").trim());
							result = true;
							elements.get(i).click();
							WaitUtil.waitFor(1);
							break;
						}
						elements.get(i+1).click();
						WaitUtil.waitFor(1);
					}
					if (!result) {
						throw new Exception("진단보험금 : " + info.insTerm + "을 선택할 수 없습니다.");
					}
				}
			}catch (Exception e){
				throw e;
			}
			driver.findElement(By.cssSelector("body > div.mbsc-mobiscroll.dw-bottom.dw-liq.dw-select > div > div.dw.dw-ltr > div > div.dwbc > div.dwbw.dwb-s.ui-complete")).click();
			WaitUtil.waitFor(2);



			logger.info("납입기간 선택");
			driver.findElement(By.cssSelector("#insuTerm_dummy")).click();
			WaitUtil.waitFor(1);

			try {
				result = false;
				elements = driver.findElements(By.cssSelector(
					"body > div.mbsc-mobiscroll.dw-bottom.dw-liq.dw-select > div > div.dw.dw-ltr > div > div.dwcc > div > div > div > div > div.dwww > div.dww > div > div > div"));
				int napTermSize = elements.size();

				int selectedNumber = 0;
				logger.info("개수 : "+napTermSize);

				String napTermStr = info.napTerm.replaceAll("[^0-9]", "");

				for (int i = 0; i < napTermSize; i++) {

					logger.info("에어리어 셀렉티드 : "+elements.get(i).getAttribute("aria-selected"));

					if(elements.get(i).getAttribute("aria-selected").equals("true")){
						selectedNumber = i;
						break;
					}
				}

				logger.info("selectedNumber의 값 : "+selectedNumber);
				logger.info("selectedNumber의 data-val : "+elements.get(selectedNumber).getAttribute("data-val").trim());

				if(Integer.parseInt(napTermStr) <= Integer.parseInt(elements.get(selectedNumber).getAttribute("data-val").trim())) {

					for(int i=selectedNumber; i>=0; i--){

						logger.info("진단보험금 : " + elements.get(i).getText().trim());

						if (napTermStr.equals(elements.get(i).getAttribute("data-val").trim())) {
							logger.info("선택한 진단보험금 : " + elements.get(i).getAttribute("data-val").trim());
							result = true;
							elements.get(i).click();
							WaitUtil.waitFor(1);
							break;
						}
						elements.get(i-1).click();
						WaitUtil.waitFor(1);
					}
					if (!result) {
						throw new Exception("진단보험금 : " + info.insTerm + "을 선택할 수 없습니다.");
					}
				}
				else if(Integer.parseInt(napTermStr) >= Integer.parseInt(elements.get(selectedNumber).getAttribute("data-val").trim())){
					for (int i = 0; i < napTermSize; i++) {

						logger.info("진단보험금 : " + elements.get(i).getText().trim());

						if (napTermStr.equals(elements.get(i).getAttribute("data-val").trim())) {
							logger.info("선택한 진단보험금 : " + elements.get(i).getAttribute("data-val").trim());
							result = true;
							elements.get(i).click();
							WaitUtil.waitFor(1);
							break;
						}
						elements.get(i+1).click();
						WaitUtil.waitFor(1);
					}
					if (!result) {
						throw new Exception("진단보험금 : " + info.insTerm + "을 선택할 수 없습니다.");
					}
				}
			}catch (Exception e){
				throw e;
			}

			logger.info("확인버튼 클릭");
			driver.findElement(By.cssSelector("body > div.mbsc-mobiscroll.dw-bottom.dw-liq.dw-select > div > div.dw.dw-ltr > div > div.dwbc > div.dwbw.dwb-s.ui-complete")).click();
			WaitUtil.waitFor(2);



			logger.info("설계 결과 확인 후 가입하기 버튼클릭");
			driver.findElement(By.cssSelector("#openCheckSelfPlan")).click();
			helper.waitForCSSElement("#loading_area");
			WaitUtil.waitFor(1);


			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);
			WaitUtil.waitFor(1);
			logger.info("월보험료 가져오기");
			String premium = driver.findElement(By.cssSelector("#rslt_sumPrm2")).getText().trim().replaceAll("[^0-9]", "");
			info.treatyList.get(0).monthlyPremium = premium;
			logger.info("초회 보험료 : " + info.treatyList.get(0).monthlyPremium);


			logger.info("해약환급금 예시 버튼클릭");
			driver.findElement(By.cssSelector("#btn_refundPop2")).click();
			WaitUtil.waitFor(2);
			helper.waitForCSSElement("#loading_area > div > div > svg");

			logger.info("해약환급금 크롤링");


			//2021 - 10 - 25
			//해약환급금을 따로 크롤링하지 않도록 수정


			elements = driver.findElements(By.cssSelector("#srrRstList"));
			int trSize = elements.size();

			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

			//총납입한 보험료 계산 [모바일페이지 크롤링이므로 일단 주석, 필요한 경우 주석풀고 사용]
			String premiumSum = null;


			for(int i=0; i<trSize; i++) {

				logger.info("______해약환급급[{}]_______ ", i);

				String term = elements.get(i).findElement(By.cssSelector("#eprePeri")).getAttribute("innerText").trim(); // 경과기간
				String infoNapTerm = info.napTerm.replaceAll("[^0-9]", "");

				if(term.contains("개월") || Integer.parseInt(infoNapTerm) >= Integer.parseInt(term.replaceAll("[^0-9]", ""))) {

					if (term.contains("개월")) {
						premiumSum = Integer.toString(Integer.parseInt(premium) * Integer.parseInt(
							term.replaceAll("[^0-9]", ""))); // 납입보험료
					} else {
						premiumSum = Integer.toString(Integer.parseInt(premium) * Integer.parseInt(
							term.replaceAll("[^0-9]", "")) * 12); // 납입보험료
					}
				}
				else if(Integer.parseInt(infoNapTerm) < Integer.parseInt(term.replaceAll("[^0-9]", ""))){
					premiumSum = Integer.toString(Integer.parseInt(premium) * Integer.parseInt(infoNapTerm) * 12); // 납입보험료
				}


				String returnMoney = elements.get(i).findElement(By.cssSelector("#pbanIratFrcs_Amt_Rfdrt")).getAttribute("innerText").trim(); // 해약환급금
				String[] returnMoneyStr = returnMoney.split("\\(");
				String returnMoneySave = returnMoneyStr[0].trim();

				String returnRate = elements.get(i).findElement(By.cssSelector("#pbanIratFrcs_Amt_Rfdrt")).getAttribute("innerText").trim();
				String returnRateSave = returnRate.substring(returnRate.lastIndexOf("(")+1).replace(")","");// 환급률


				logger.info("|--경과기간: {}", term);
				//logger.info("|--납입보험료: {}", premiumSum);
				logger.info("|--해약환급금: {}", returnMoneySave);
				logger.info("|--환급률: {}", returnRateSave);


				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender(info.getGenderEnum().name());
				planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

				planReturnMoney.setTerm(term); // 경과기간
				//planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
				planReturnMoney.setReturnMoney(returnMoneySave); // 환급금
				planReturnMoney.setReturnRate(returnRateSave); // 환급률

				planReturnMoneyList.add(planReturnMoney);

				info.returnPremium = returnMoneySave.replaceAll("[^0-9]", "");

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
