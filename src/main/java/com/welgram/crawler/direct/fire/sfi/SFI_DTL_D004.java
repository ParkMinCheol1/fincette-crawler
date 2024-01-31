package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class SFI_DTL_D004 extends CrawlingSFIDirect {

	public static void main(String[] args) {
		executeCommand(new SFI_DTL_D004(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $button = null;
		String insTerm = info.getInsTerm();
		
		logger.info("보험료 계산 버튼 클릭");
		WaitUtil.loading(3);
		$button = helper.waitElementToBeClickable(driver.findElement(By.xpath("//span[text()='보험료 계산']")));
		click($button);

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("가입형태 설정");
		setJoinType("본인");

		logger.info("생년월일 설정");
		setBirthday(info.getFullBirth(), By.id("birth-input"));

		logger.info("성별 설정");
		setGender(info.getGender(), "남성");

		logger.info("직업 설정");
		setJob("중고등학교교사");

		logger.info("보험료 계산하기 버튼 클릭");
		$button = driver.findElement(By.id("js-btn-next"));
		click($button);

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("플랜 설정");
		setPlan(info.planSubName);

		logger.info("조건 변경하기 버튼 클릭");
		click(driver.findElement(By.id("btn-insured-periods")));

		logger.info("갱신주기 설정");
		insTerm = insTerm.substring(0, insTerm.indexOf("년") + 1) + " 자동갱신";
		setInsTerm(insTerm);

		logger.info("납입방법 설정");
		setNapCycle(info.getNapCycle());

		logger.info("환급유형 설정");
		setRefundType(info.getProductKind());

		$button = driver.findElement(By.xpath("//div[@class='btn-wrap middle']/button[normalize-space()='변경']"));
		click($button);

		logger.info("특약 설정");
		setTreaties(info.getTreatyList());

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("한번에 보장 변경 팝업을 닫기 위해 확인 버튼 클릭");
		$button = driver.findElement(By.id("btn-confirm"));
		click($button);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		logger.info("해약환급금 크롤링");
		crawlReturnMoneyList(info);

		return true;
	}



	@Override
	public void setPlan(String expectedPlan) throws CommonCrawlerException {

		String title = "플랜";
		String actualPlan = "";
		String[] textTypes = expectedPlan.split("\\|");
		String script = "";

		try {
			// 플랜 관련 element 찾기
			WebElement $planAreaThead = driver.findElement(By.id("header-roll-area"));
			WebElement $planLabel = null;

			for (String textType : textTypes) {
				try {
					// 플랜 클릭
					textType = textType.trim();

					$planLabel = $planAreaThead.findElement(By.xpath(".//label[contains(@class,'btn-radio')]/h2[text()='" + textType +"']"));
					script = "$(arguments[0]).click();";
					helper.executeJavascript(script, $planLabel);
					expectedPlan = textType;
					break;

				} catch (NoSuchElementException e) { }
			}

			// 실제 선택된 플랜 값 읽어오기
			$planLabel = $planAreaThead.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
			actualPlan = $planLabel.getText().trim();
			actualPlan = actualPlan.substring(0, actualPlan.indexOf("\n"));

			// 비교
			super.printLogAndCompare(title, expectedPlan, actualPlan);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
			throw new CommonCrawlerException(e, exceptionEnum.getMsg());
		}
	}



	@Override
	public void setInsTerm(Object... obj) throws SetInsTermException {

		String title = "갱신 주기";
		String expectedInsTerm = (String) obj[0];
		String actualInsTerm = "";

		try {
//			String script = "return $('dd[id*=insured-term]:visible')[0]";
//			WebElement $insTermAreaDd = (WebElement) helper.executeJavascript(script);
			WebElement $insTermAreaDd = driver.findElement(By.xpath("//div[@id='renw-sc-cycle']/dd"));
			WebElement $insTermLabel = $insTermAreaDd.findElement(By.xpath(".//label[contains(.,'" + expectedInsTerm + "')]"));
			// 보험기간 라벨 텍스트 예:  "30세 만기(계약 전환시 최대 100세 보장)" -> 시작 텍스트가 보험기간 선택의 기준이 된다.
			click($insTermLabel);

			// 실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
			$insTermLabel = $insTermAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
			actualInsTerm = $insTermLabel.getText().trim();

			// 비교
			super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
			throw new SetInsTermException(exceptionEnum.getMsg());
		}
	}



	@Override
	public void setNapCycle(Object... obj) throws SetNapCycleException {

		String title = "납입방법";
		String expectedNapCycle = (String) obj[0];
		String actualNapCycle = "";

		try {
			if (expectedNapCycle.equals("01")) {
				expectedNapCycle = "월납";
			}

			/**
			 * 어떤 다이렉트 상품의 경우 크롤링 서버를 통해 원수사 사이트에 접속하게 되면
			 * 납입방법 ui가 깨져있다. 해당 경우에 selenium이 제공하는 click 기능이 작동하지 않는다.
			 * 따라서 jquery 문법으로 클릭시켜야한다.
			 */

			String script = "$(arguments[0]).click();";

			WebElement $napCycleAreaDd = driver.findElement(By.xpath("//div[@id='payment-method']/dd"));
			WebElement $napCycleLabel = $napCycleAreaDd.findElement(By.xpath(".//label[normalize-space()='" + expectedNapCycle + "']"));
			helper.executeJavascript(script, $napCycleLabel);

			//실제 선택된 납입방법 값 읽어오기(원수사에서는 실제 선택된 납입방법 element 클래스 속성에 active를 준다)
			$napCycleLabel = $napCycleAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
			actualNapCycle = $napCycleLabel.getText().trim();

			//비교
			super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
			throw new SetNapCycleException(e, exceptionEnum.getMsg());
		}
	}



	@Override
	public void setRefundType(Object... obj) throws SetRefundTypeException {

		String title = "환급유형";
		String expectedRefundType = (String) obj[0];
		String actualRefundType = "";

		try {
			// 환급유형 관련 element 찾기
			WebElement $refundTypeDiv = driver.findElement(By.id("refund-rate"));
			WebElement $refundTypeButton = $refundTypeDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedRefundType + "']"));

			// 실제 클릭된 환급유형 값 읽어오기
			$refundTypeButton = $refundTypeDiv.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
			actualRefundType = $refundTypeButton.getText();

			// 비교
			super.printLogAndCompare(title, expectedRefundType, actualRefundType);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_REFUND_TYPE;
			throw new SetRefundTypeException(e, exceptionEnum.getMsg());
		}
	}



	// 로딩바 명시적 대기
	@Override
	public void waitLoadingBar() {

		try {
			helper.waitForCSSElement("#loading-message-box");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {

		String title = "보험료 크롤링";

		CrawlingProduct info = (CrawlingProduct) obj[0];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
		ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

		try {
			// 보험료 크롤링 전에는 대기시간을 넉넉히 준다
			WaitUtil.waitFor(5);

			WebElement $premiumStrong = driver.findElement(By.xpath("//strong[@class='blind']"));
			String premium = $premiumStrong.getText();
			premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

			mainTreaty.monthlyPremium = premium;

			if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
				logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
				throw new PremiumCrawlerException(exceptionEnum.getMsg());
			} else {
				logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
			}

		} catch (Exception e) {
			throw new PremiumCrawlerException(e, exceptionEnum.getMsg());
		}
	}



	@Override
	public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

		CrawlingProduct info = (CrawlingProduct) obj[0];
		List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

		try {
			// 해약환급금 팝업 오픈 버튼 element 찾기
			logger.info("해약환급금 팝업 오픈 버튼 클릭");
			WebElement $openReturnMoneyPopupButton = driver.findElement(By.xpath("//div[contains(@class, 'select')]//button[@class='btn-rate']"));
			click($openReturnMoneyPopupButton);

			// 해약환급금 관련 정보 element 찾기
			WebElement $returnMoneyDiv = driver.findElement(By.xpath("//div[@class='modal-content']//table"));
			WebElement $returnMoneyTbody = $returnMoneyDiv.findElement(By.xpath("./tbody[@id='refund-list']"));
			List<WebElement> $returnMoneyTrList = $returnMoneyTbody.findElements(By.tagName("tr"));

			for(WebElement $tr : $returnMoneyTrList) {

				// tr이 보이도록 스크롤 조정. 스크롤을 조정하지 않으면 해약환급금 금액을 크롤링 할 수 없음.
				helper.moveToElementByJavascriptExecutor($tr);

				List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

				String term = $tdList.get(0).getText();
				String premiumSum = $tdList.get(1).getText();
				String returnMoneyMin = $tdList.get(2).getText();
				String returnRateMin = $tdList.get(3).getText();
				String returnMoney = $tdList.get(4).getText();
				String returnRate = $tdList.get(5).getText();

				premiumSum = premiumSum.replaceAll("[^0-9]", "");
				returnMoneyMin = returnMoneyMin.replaceAll("[^0-9]", "");
				returnMoney = returnMoney.replaceAll("[^0-9]", "");

				logger.info("경과기간 : {} | 납입보험료 : {} | 최저환급금 : {} | 최저환급률 : {} | 공시환급금 : {} | 공시환급률 : {}", term, premiumSum, returnMoneyMin, returnRateMin, returnMoney, returnRate);

				PlanReturnMoney p = new PlanReturnMoney();
				p.setTerm(term);
				p.setPremiumSum(premiumSum);
				p.setReturnMoneyMin(returnMoneyMin);
				p.setReturnRateMin(returnRateMin);
				p.setReturnMoney(returnMoney);
				p.setReturnRate(returnRate);

				planReturnMoneyList.add(p);
				info.returnPremium = returnMoney;
			}

			logger.info("만기환급금 : {}원", info.returnPremium);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
			throw new ReturnMoneyListCrawlerException(e, exceptionEnum.getMsg());
		}
	}



	/**
	 *
	 * TODO 다이렉트 치아보험 상품의 경우 특약 하위 보장명이 표기되기 때문에 코드가 너무 길어진다. 추후에 더 좋은 코드로 리팩토링 진행
	 *
	 * 삼성화재 다이렉트 특약설정 TYPE1 : 한번에 보장 변경 팝업에서 처리
	 * @param welgramTreatyList 가입설계 특약 목록
	 * @throws SetTreatyException
	 */
	public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

		String script = "";
		ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;

		try {
			//선택된 플랜의 "한번에 보장 변경" 버튼 클릭
			WebElement $changeButton = driver.findElement(By.xpath("//div[contains(@class,'plan group1 select')]//button[normalize-space()='한번에 보장 변경']"));
			click($changeButton);

			//한번에 보장 변경 팝업 element 찾기
			WebElement $treatyPopupDiv = driver.findElement(By.xpath("//div[@class='slimScrollDiv']"));
			WebElement $treatyAreaTbody = $treatyPopupDiv.findElement(By.id("coverage-list"));
			By modalPosition = By.xpath("//div[@class='modal-dialog']");
			boolean isModal= false;

			removeWhiteSpaceFromElement($treatyAreaTbody);

			/**
			 * ===========================================================================================
			 * [STEP 1]
			 * 원수사 특약명, 가입설계 특약명 수집 진행하기
			 * ===========================================================================================
			 */

			/**
			 * 원수사 모든 특약명 조회
			 */

			List<String> targetTreatyNameList = new ArrayList<>();
			List<String> welgramTreatyNameList = new ArrayList<>();

			// 원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
			script = "$(arguments[0]).find('span.vertical-line').remove();";
			helper.executeJavascript(script, $treatyAreaTbody);

			/**
			 * 원수사 특약명 수집
			 * - 해당 상품은 특약명이 아닌 보장명까지 원수사에서 표기하고 있다. 보장명은 제외하고 특약명에 해당하는 tr만 가져온다.
			 */

			List<WebElement> $targetTreatyTrList = $treatyAreaTbody.findElements(By.xpath("./tr/td[1][not(@class[contains(., 'ne-group-tbl')])]/parent::tr"));
			for (WebElement $treatyTr : $targetTreatyTrList) {
				WebElement $treatyNameTd = $treatyTr.findElement(By.xpath(".//p"));

				// 원수사 특약명을 가져오기 위해 특약명이 보이도록 스크롤 처리를 해야함.
				helper.moveToElementByJavascriptExecutor($treatyNameTd);
				String targetTreatyName = $treatyNameTd.getText().trim();

				targetTreatyNameList.add(targetTreatyName);
			}

			// 가입설계 특약명 수집
			welgramTreatyNameList = welgramTreatyList.stream().map(t -> t.getTreatyName()).collect(Collectors.toList());

			// 원수사 특약명 vs 가입설계 특약명 비교 처리(유지, 삭제, 추가돼야할 특약명 분간하는 작업)
			List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기
			List<String> copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);     //원본 리스트가 훼손되므로 복사본 떠두기
			List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
			List<String> dismatchedTreatyNameList = new ArrayList<>();                             //원수사에서 미가입 처리해야하는 특약명 리스트

			// (원수사와 가입설계 특약 비교해서)공통된 특약명 찾기
			targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
			matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구

			// (원수사와 가입설계 특약 비교해서)불일치 특약명 찾기(원수사에서 미가입처리 해줄 특약명들)
			targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
			dismatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구

			/**
			 * ===========================================================================================
			 * [STEP 2]
			 * 특약 가입/미가입 처리 진행하기
			 * ===========================================================================================
			 */

			// 불일치 특약들에 대해서 원수사에서 미가입 처리 진행
			for (String treatyName : dismatchedTreatyNameList) {

				boolean isContainsGrt = false;                //해당 특약이 보장명을 포함하고 있는지 여부
				boolean isToggleJoin = false;
				WebElement $treatyNameTd = $treatyAreaTbody.findElement(By.xpath(".//th/p"));
//				WebElement $treatyNameTd = $treatyAreaTbody.findElement(By.xpath(".//td[1][text()='" + treatyName + "']"));
				WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./ancestor::tr"));
				WebElement $treatyJoinTd = null;
				WebElement $treatyJoinInput = null;
				WebElement $treatyJoinLabel = null;

				// 해당 특약이 하위 보장명을 포함하는 특약인지 가려내기
				script = "return $(arguments[0]).hasClass('title-group');";
				isContainsGrt = (boolean) helper.executeJavascript(script, $treatyNameTd);

				if (isContainsGrt) {
					logger.info("특약 : {}는 하위 보장명을 포함합니다.", treatyName);

					// 하위 보장명이 존재하는 특약인 경우
					List<WebElement> $grtTrList = new ArrayList<>();
					WebElement $stratGrtTr = null;
					WebElement $endGrtTr = null;

					// 해당 특약의 보장명이 시작되는 tr 찾기
					script = "return $(arguments[0]).next()[0];";
					$stratGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);

					// 해당 특약의 보장명이 끝나는 tr 찾기
					script = "return $(arguments[0]).nextAll().find('td.group-last').first().parent()[0];";
					$endGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);

					// 특약의 보장명 리스트
					script = "return $(arguments[0]).nextUntil($(arguments[1])).andSelf().add($(arguments[1]));";
					$grtTrList = (List<WebElement>) helper.executeJavascript(script, $stratGrtTr, $endGrtTr);

//					// 특약 하위의 모든 보장명을 미가입 처리한다.
//					for (int i = 0; i < $grtTrList.size(); i++) {
//						WebElement $grtTr = $grtTrList.get(i);
//						helper.moveToElementByJavascriptExecutor($grtTr);
//
//						WebElement $grtNameTd = $grtTr.findElement(By.xpath("./td[1]"));
//						WebElement $grtJoinTd = $grtTr.findElement(By.xpath("./td[2]"));
//						WebElement $grtJoinSpan = $grtJoinTd.findElement(By.xpath(".//span[@class='txt-toggle']"));
//						WebElement $grtJoinLabel = $grtJoinTd.findElement(By.xpath(".//label"));
//
//						String grtName = $grtNameTd.getText();
//						String grtJoin = $grtJoinSpan.getText();
//
//						logger.info("보장명 : {} | 가입상태 : {}", grtName, grtJoin);
//
//						// 보장명이 "가입" 상태인 경우에만 "미가입" 처리를 진행한다
//						if ("가입".equals(grtJoin)) {
//							logger.info("보장명 : {} 을 미가입 처리합니다.", grtName);
//							click($grtJoinLabel);
//
//							modalPosition = By.xpath("//div[@class='modal-dialog']");
//							isModal = helper.existElement(modalPosition);
//
//							// 보장명을 "미가입" 처리하다가 모달창이 발생하는 경우 확인 버튼을 눌러준다.
//							if (isModal) {
//								WebElement $modal = driver.findElement(modalPosition);
//								WebElement $button = $modal.findElement(By.id("btn-confirm"));
//								click($button);
//							}
//
//							/**
//							 * 가입 처리를 진행한 후에는 반드시 보장명 리스트를 다시한번 DOM에서 찾아줘야한다. 상태가 바꼈기 때문에
//							 */
//							$treatyAreaTbody = $treatyPopupDiv.findElement(By.id("coverage-list"));
//							$treatyNameTd = $treatyAreaTbody.findElement(By.xpath(".//td[1][text()='" + treatyName + "']"));
//							$treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
//
//							/**
//							 * 토글의 가입상태 값을 변경하게 되면 불필요하게 삭제 처리한 element들이 다시 원상 복구된다.
//							 * 다시 불필요한 element들을 삭제하는 처리가 필요함.
//							 */
//
//							removeWhiteSpaceFromElement($treatyAreaTbody);
//
//							// 원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
//							script = "$(arguments[0]).find('span.vertical-line').remove();";
//							helper.executeJavascript(script, $treatyAreaTbody);
//
//							// 해당 특약의 보장명이 시작되는 tr 찾기
//							script = "return $(arguments[0]).next()[0];";
//							$stratGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);
//
//							// 해당 특약의 보장명이 끝나는 tr 찾기
//							script = "return $(arguments[0]).nextAll().find('td.group-last').first().parent()[0];";
//							$endGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);
//
//							// 특약의 보장명 리스트
//							script = "return $(arguments[0]).nextUntil($(arguments[1])).andSelf().add($(arguments[1]));";
//							$grtTrList = (List<WebElement>) helper.executeJavascript(script, $stratGrtTr, $endGrtTr);
//						}
//					}
				} else {
					// 하위 보장명이 없는 특약인 경우
					$treatyJoinTd = $treatyTr.findElement(By.xpath("./td[2]"));

					helper.moveToElementByJavascriptExecutor($treatyJoinTd);

					// 특약이 필수가입인지 가입/미가입인지 확인(필수 가입인 경우 토글 버튼이 없음)
					By togglePosition = By.xpath("./span[@class='ne-chk-toggle']");
					isToggleJoin = helper.existElement($treatyJoinTd, togglePosition);

					if (isToggleJoin) {
						//미가입 처리해야하는 특약이 가입/미가입 토글인 경우
						$treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input"));
						$treatyJoinLabel = $treatyJoinTd.findElement(By.xpath(".//label"));

						//미가입 처리해야하는 특약이 "가입" 상태인 경우에만 "미가입" 처리를 진행한다.
						if ($treatyJoinInput.isSelected()) {
							logger.info("특약명 : {} 미가입 처리를 진행합니다.", treatyName);
							click($treatyJoinLabel);

							modalPosition = By.xpath("//div[@class='modal-dialog']");
							isModal = helper.existElement(modalPosition);

							//보장명을 "가입" 처리하다가 모달창이 발생하는 경우 확인 버튼을 눌러준다.
							if (isModal) {
								WebElement $modal = driver.findElement(modalPosition);
								WebElement $button = $modal.findElement(By.id("btn-confirm"));
								click($button);
							}

							/**
							 * 토글의 가입상태 값을 변경하게 되면 불필요하게 삭제 처리한 element들이 다시 원상 복구된다.
							 * 다시 불필요한 element들을 삭제하는 처리가 필요함.
							 */

							removeWhiteSpaceFromElement($treatyAreaTbody);

							//원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
							script = "$(arguments[0]).find('span.vertical-line').remove();";
							helper.executeJavascript(script, $treatyAreaTbody);
						}
					}
				}
			}

			// 공통된 특약들에 대해서는 원수사에서 가입 처리 진행
			for (String treatyName : matchedTreatyNameList) {
				// 가입 처리를 위해 특약의 가입 처리 영역 element 찾기
				logger.info("가입 처리해야할 특약명 : {}", treatyName);

				boolean isContainsGrt = false;                // 해당 특약이 보장명을 포함하고 있는지 여부
				boolean isToggleJoin = false;
				WebElement $treatyNameTd = $treatyAreaTbody.findElement(By.xpath(".//th/p[text()='" + treatyName + "']"));
				WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./ancestor::tr"));
				WebElement $treatyJoinTd = null;
				WebElement $treatyJoinInput = null;
				WebElement $treatyJoinLabel = null;

				//해당 특약이 하위 보장명을 포함하는 특약인지 가려내기
				script = "return $(arguments[0]).hasClass('title-group');";
				isContainsGrt = (boolean) helper.executeJavascript(script, $treatyNameTd);

				if (isContainsGrt) {

					logger.info("특약 : {}는 하위 보장명을 포함합니다.", treatyName);

					//하위 보장명이 존재하는 특약인 경우
					List<WebElement> $grtTrList = new ArrayList<>();
					WebElement $stratGrtTr = null;
					WebElement $endGrtTr = null;

					// 해당 특약의 보장명이 시작되는 tr 찾기
					script = "return $(arguments[0]).next()[0];";
					$stratGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);

					// 해당 특약의 보장명이 끝나는 tr 찾기
					script = "return $(arguments[0]).nextAll().find('td.group-last').first().parent()[0];";
					$endGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);

					// 특약의 보장명 리스트
					script = "return $(arguments[0]).nextUntil($(arguments[1])).andSelf().add($(arguments[1]));";
					$grtTrList = (List<WebElement>) helper.executeJavascript(script, $stratGrtTr, $endGrtTr);

					// 특약 하위의 모든 보장명이 가입처리여야만 해당 특약을 가입으로 간주한다.
					for (int i = 0; i < $grtTrList.size(); i++) {
						WebElement $grtTr = $grtTrList.get(i);
						helper.moveToElementByJavascriptExecutor($grtTr);

						WebElement $grtNameTd = $grtTr.findElement(By.xpath("./td[1]"));
						WebElement $grtJoinTd = $grtTr.findElement(By.xpath("./td[4]"));
						WebElement $grtJoinSpan = $grtJoinTd.findElement(By.xpath(".//span[@class='txt-toggle']"));
						WebElement $grtJoinLabel = $grtJoinTd.findElement(By.xpath(".//label"));

						String grtName = $grtNameTd.getText();
						String grtJoin = $grtJoinSpan.getText();

						logger.info("보장명 : {} | 가입상태 : {}", grtName, grtJoin);

						// 보장명이 "미가입" 상태인 경우에만 "가입" 처리를 진행한다
						if ("미가입".equals(grtJoin)) {
							logger.info("보장명 : {} 을 가입 처리합니다.", grtName);
							click($grtJoinLabel);

							modalPosition = By.xpath("//div[@class='modal-dialog']");
							isModal = helper.existElement(modalPosition);

							// 보장명을 "가입" 처리하다가 모달창이 발생하는 경우 확인 버튼을 눌러준다.
							if (isModal) {
								WebElement $modal = driver.findElement(modalPosition);
								WebElement $button = $modal.findElement(By.id("btn-confirm"));
								click($button);
							}

							/**
							 * 가입 처리를 진행한 후에는 반드시 보장명 리스트를 다시한번 DOM에서 찾아줘야한다. 상태가 바꼈기 때문에
							 */

							$treatyAreaTbody = $treatyPopupDiv.findElement(By.id("coverage-list"));
							$treatyNameTd = $treatyAreaTbody.findElement(By.xpath(".//th/p[text()='" + treatyName + "']"));
							$treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

							/**
							 * 토글의 가입상태 값을 변경하게 되면 불필요하게 삭제 처리한 element들이 다시 원상 복구된다.
							 * 다시 불필요한 element들을 삭제하는 처리가 필요함.
							 */

							removeWhiteSpaceFromElement($treatyAreaTbody);

							// 원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
							script = "$(arguments[0]).find('span.vertical-line').remove();";
							helper.executeJavascript(script, $treatyAreaTbody);

							// 해당 특약의 보장명이 시작되는 tr 찾기
							script = "return $(arguments[0]).next()[0];";
							$stratGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);

							// 해당 특약의 보장명이 끝나는 tr 찾기
							script = "return $(arguments[0]).nextAll().find('td.group-last').first().parent()[0];";
							$endGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);

							// 특약의 보장명 리스트
							script = "return $(arguments[0]).nextUntil($(arguments[1])).andSelf().add($(arguments[1]));";
							$grtTrList = (List<WebElement>) helper.executeJavascript(script, $stratGrtTr, $endGrtTr);
						}
					}
				} else {
					//하위 보장명이 없는 특약인 경우
					$treatyJoinTd = $treatyTr.findElement(By.xpath("./td[2]"));

					helper.moveToElementByJavascriptExecutor($treatyJoinTd);

					// 특약이 필수가입인지 가입/미가입인지 확인(필수 가입인 경우 토글 버튼이 없음)
					By togglePosition = By.xpath("./span[@class='ne-chk-toggle']");
					isToggleJoin = helper.existElement($treatyJoinTd, togglePosition);

					if (isToggleJoin) {
						// 가입 처리해야하는 특약이 가입/미가입 토글인 경우
						$treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input"));
						$treatyJoinLabel = $treatyJoinTd.findElement(By.xpath(".//label"));

						// 가입 처리해야하는 특약이 "미가입" 상태인 경우에만 "가입" 처리를 진행한다.
						if (!$treatyJoinInput.isSelected()) {
							logger.info("특약명 : {} 가입 처리를 진행합니다.", treatyName);
							click($treatyJoinLabel);

							modalPosition = By.xpath("//div[@class='modal-dialog']");
							isModal = helper.existElement(modalPosition);

							// 보장명을 "가입" 처리하다가 모달창이 발생하는 경우 확인 버튼을 눌러준다.
							if (isModal) {
								WebElement $modal = driver.findElement(modalPosition);
								WebElement $button = $modal.findElement(By.id("btn-confirm"));
								click($button);
							}

							/**
							 * 토글의 가입상태 값을 변경하게 되면 불필요하게 삭제 처리한 element들이 다시 원상 복구된다.
							 * 다시 불필요한 element들을 삭제하는 처리가 필요함.
							 */

							removeWhiteSpaceFromElement($treatyAreaTbody);

							// 원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
							script = "$(arguments[0]).find('span.vertical-line').remove();";
							helper.executeJavascript(script, $treatyAreaTbody);
						}
					}
				}
			}

			/**
			 * ===========================================================================================
			 * [STEP 3]
			 * 실제 가입처리된 원수사 특약 정보를 수집한다(유효성 검사를 하기 위함)
			 * ===========================================================================================
			 */

			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

			// 특약에 대해 가입/미가입 처리를 진행하면 tr의 상태가 바꼈기 때문에 다시한번 element를 찾아줘야한다.
			// 안그러면 StaleElementReference 예외 발생
			$treatyAreaTbody = $treatyPopupDiv.findElement(By.id("coverage-list"));
			$targetTreatyTrList = $treatyAreaTbody.findElements(By.xpath("./tr/td[1][not(@class[contains(., 'ne-group-tbl')])]/parent::tr"));
			for (WebElement $treatyTr : $targetTreatyTrList) {
				//특약 정보가 보이도록 스크롤 조정
				helper.moveToElementByJavascriptExecutor($treatyTr);

				//특약명, 특약가입금액, 특약가입영역 element 찾기
				String treatyName = "";
				String treatyAssureMoney = "";
				boolean isToggle = false;
				boolean isContainsGrt = false;
				WebElement $treatyNameTd = $treatyTr.findElement(By.xpath("./th/p"));
				WebElement $treatyJoinTd = null;
				WebElement $treatyAssureMoneyTd = null;
				WebElement $treatyJoinInput = null;

				treatyName = $treatyNameTd.getText();

				//해당 특약이 하위 보장명을 포함하는 특약인지 가려내기
				script = "return $(arguments[0]).hasClass('title-group');";
				isContainsGrt = (boolean) helper.executeJavascript(script, $treatyNameTd);

				if (isContainsGrt) {
					logger.info("특약 : {}는 하위 보장명을 포함합니다.", treatyName);

					// 하위 보장명이 존재하는 특약인 경우
					List<WebElement> $grtTrList = new ArrayList<>();
					WebElement $stratGrtTr = null;
					WebElement $endGrtTr = null;

					// 해당 특약의 보장명이 시작되는 tr 찾기
					script = "return $(arguments[0]).next()[0];";
					$stratGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);

					// 해당 특약의 보장명이 끝나는 tr 찾기
					script = "return $(arguments[0]).nextAll().find('td.group-last').first().parent()[0];";
					$endGrtTr = (WebElement) helper.executeJavascript(script, $treatyTr);

					// 특약의 보장명 리스트
					script = "return $(arguments[0]).nextUntil($(arguments[1])).andSelf().add($(arguments[1]));";
					$grtTrList = (List<WebElement>) helper.executeJavascript(script, $stratGrtTr, $endGrtTr);

					// 특약 하위의 모든 보장명의 가입 상태를 조회한다
					List<String> joinStatusList = new ArrayList<>();

					for (WebElement $grtTr : $grtTrList) {
						helper.moveToElementByJavascriptExecutor($grtTr);

						WebElement $grtNameTd = $grtTr.findElement(By.xpath("./td[1]"));
						WebElement $grtJoinTd = $grtTr.findElement(By.xpath("./td[4]"));
						WebElement $grtJoinSpan = $grtJoinTd.findElement(By.xpath(".//span[@class='txt-toggle']"));

						String grtName = $grtNameTd.getText();
						String grtJoin = $grtJoinSpan.getText();

						logger.info("보장명 : {} | 현재 가입상태 : {}", grtName, grtJoin);
						joinStatusList.add(grtJoin);
					}

					// 특약 하위 보장명 중 하나라도 "미가입" 상태가 있는 경우 해당 특약은 미가입으로 간주한다.
					if (!joinStatusList.contains("미가입")) {
						//TODO 처리 로직 필요
						//원수사 특약 정보 적재. 단, 하위 보장명이 존재하는 특약의 가입금액은 원수사에 표기가 안되어 있음.
						//따라서 그냥 분석쪽에서 세팅해준 가입금액을 기반으로 세팅한다.
						String finalTreatyName = treatyName;
						CrawlingTreaty welgramTreaty = welgramTreatyList.stream().filter(t -> t.getTreatyName().equals(finalTreatyName)).findFirst().orElseGet(CrawlingTreaty::new);

						CrawlingTreaty targetTreaty = new CrawlingTreaty();
						targetTreaty.setTreatyName(treatyName);
						targetTreaty.setAssureMoney(welgramTreaty.getAssureMoney());

						targetTreatyList.add(targetTreaty);
					}
				} else {
					$treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[1]"));
					$treatyJoinTd = $treatyTr.findElement(By.xpath("./td[2]"));

					// 특약명, 특약가입금액 읽어오기
					treatyName = $treatyNameTd.getText();
					treatyAssureMoney = $treatyAssureMoneyTd.getText();
					treatyAssureMoney = treatyAssureMoney.replace("지급", "").replaceAll("[0-9]일", "");
					treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

					// 특약 가입여부 확인
					By by = By.xpath("./span[@class='ne-chk-toggle']");
					isToggle = helper.existElement($treatyJoinTd, by);

					// 원수사 특약 정보 적재
					CrawlingTreaty targetTreaty = new CrawlingTreaty();
					targetTreaty.setTreatyName(treatyName);
					targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

					if (isToggle) {
						$treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input"));

						// 특약이 "가입" 처리된 경우에만
						if ($treatyJoinInput.isSelected()) {
							targetTreatyList.add(targetTreaty);
						}
					} else {
						//특약이 필수가입인 경우
						targetTreatyList.add(targetTreaty);
					}
				}
			}

			/**
			 * ===========================================================================================
			 * [STEP 4]
			 * 원수사 특약 정보 vs 가입설계 특약 정보 비교
			 * ===========================================================================================
			 */

			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

			if (result) {
				logger.info("특약 정보 모두 일치");
			} else {
				logger.info("특약 정보 불일치");
				throw new Exception();
			}

		} catch (Exception e) {
			throw new SetTreatyException(e, exceptionEnum.getMsg());
		}
	}
}