package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
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
import org.openqa.selenium.WebElement;

public class SFI_DSS_D003 extends CrawlingSFIDirect {

	public static void main(String[] args) {
		executeCommand(new SFI_DSS_D003(), args);
	}


	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $button = null;

		waitLoadingBar();

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("가입형태 설정");
		setJoinType("본인");

		logger.info("생년월일 설정");
		setBirthday(info.getFullBirth());

		logger.info("성별 설정");
		setGender(info.getGender());

		logger.info("직업 설정");
		setJob("중고등학교교사");

		logger.info("보험료 계산하기 버튼 클릭");
		$button = driver.findElement(By.id("btn-next-step"));
		click($button);

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("환급유형 설정");
		String refundType = info.getProductKind();
		refundType = "만기환급형".equals(refundType) ? "일부환급형" : refundType;
		setRefundType(refundType);

		logger.info("상품유형(=심사유형) 설정");
		setPrevalenceType(info.getTextType());

		logger.info("보험기간 설정");
		setInsTerm(info.getInsTerm());
		modalCheck();

		logger.info("플랜 설정");
		setPlan(info.getTextType());

		logger.info("특약 설정");
		setTreaties(info.getTreatyList());

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("한번에 보장 변경 팝업을 닫기 위해 확인 버튼 클릭");
		$button = driver.findElement(By.id("btn-confirm"));
		click($button);
		modalCheck();

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		logger.info("해약환급금 크롤링");
		crawlReturnMoneyList(info);

		return true;
	}


	//로딩바 명시적 대기
	@Override
	public void waitLoadingBar() {
		try {
			//해당 상품의 경우 로딩바가 사라지고 난 후에 또 다른 로딩바가 뜨는 경우가 있음.
			helper.waitForCSSElement("#loading-message-box");
			helper.waitForCSSElement("#loading-common");
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

			//보험료 크롤링 전에는 대기시간을 넉넉히 준다
			WaitUtil.waitFor(5);

			WebElement $premiumAreaStrong = driver.findElement(By.id("applyPremium"));
			WebElement $premiumStrong = $premiumAreaStrong.findElement(By.xpath("./strong[@class='blind']"));
			String premium = $premiumStrong.getText();
			premium = String.valueOf(MoneyUtil.toDigitMoney(premium));

			mainTreaty.monthlyPremium = premium;

			if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
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

			//해약환급금 팝업 오픈 버튼 element 찾기
			logger.info("해약환급금 팝업 오픈 버튼 클릭");
			WebElement $openReturnMoneyPopupButton = driver.findElement(By.id("btn-more"));
			click($openReturnMoneyPopupButton);


			//해약환급금 관련 정보 element 찾기
			WebElement $returnMoneyDiv = driver.findElement(By.id("CommonMaturityRefund"));
			WebElement $returnMoneyTbody = $returnMoneyDiv.findElement(By.xpath(".//tbody[@class='__refund-list']"));
			List<WebElement> $returnMoneyTrList = $returnMoneyTbody.findElements(By.tagName("tr"));

			for(WebElement $tr : $returnMoneyTrList) {
				//tr이 보이도록 스크롤 조정. 스크롤을 조정하지 않으면 해약환급금 금액을 크롤링 할 수 없음.
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
	 * 삼성화재 다이렉트 특약설정 TYPE1 : 한번에 보장 변경 팝업에서 처리
	 * @param welgramTreatyList 가입설계 특약 목록
	 * @throws SetTreatyException
	 */
	public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
		String script = "";
		ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;


		try {

			//플랜 영역 thead
			WebElement $planAreaThead = driver.findElement(By.id("coverage-header"));
			WebElement $selectedPlanLabel = $planAreaThead.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
			WebElement $changeButton = $selectedPlanLabel.findElement(By.xpath("./following-sibling::button"));


			//선택된 플랜의 "한번에 보장 변경" 버튼 클릭
			click($changeButton);


			//한번에 보장 변경 팝업 element 찾기
			WebElement $treatyPopupDiv = driver.findElement(By.id("inner-scroll"));
			WebElement $treatyAreaTbody = $treatyPopupDiv.findElement(By.id("coverage-list"));
			List<WebElement> $targetTreatyTrList = $treatyAreaTbody.findElements(By.xpath("./tr[@class='coverage-item']"));


			/**
			 * xpath로 특약명과 완전 일치하는 element를 찾아오게 하는데 특약명 element에 &nbsp가 껴잇는 경우가 있다.
			 * xpath에서 &nbsp에 대한 처리가 불가능함. 따라서 일단 jquery로 &npsp를 모두 제거시킨 다음에 element를 찾게한다.
			 */
			script = "$(arguments[0]).find('td:nth-child(1)').html(function (i, html) {"
				+ "    return html.replace(/&nbsp;/g, '');"
				+ "});";


			helper.executeJavascript(script, $treatyAreaTbody);



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


			//원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
			script = "$(arguments[0]).find('span.vertical-line').remove();";
			helper.executeJavascript(script, $treatyAreaTbody);


			//원수사 특약명 수집
			for(WebElement $treatyTr : $targetTreatyTrList) {
				WebElement $treatyNameTd = $treatyTr.findElement(By.xpath("./td[1]"));

				//원수사 특약명을 가져오기 위해 특약명이 보이도록 스크롤 처리를 해야함.
				helper.moveToElementByJavascriptExecutor($treatyNameTd);
				String targetTreatyName = $treatyNameTd.getText().trim();

				targetTreatyNameList.add(targetTreatyName);
			}


			//가입설계 특약명 수집
			welgramTreatyNameList = welgramTreatyList.stream().map(t -> t.getTreatyName()).collect(Collectors.toList());


			//원수사 특약명 vs 가입설계 특약명 비교 처리(유지, 삭제, 추가돼야할 특약명 분간하는 작업)
			List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기
			List<String> copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);     //원본 리스트가 훼손되므로 복사본 떠두기
			List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
			List<String> dismatchedTreatyNameList = new ArrayList<>();                             //원수사에서 미가입 처리해야하는 특약명 리스트
			List<String> strangeTreatyNameList = new ArrayList<>();                                //이상 있는 특약명 리스트


			//(원수사와 가입설계 특약 비교해서)공통된 특약명 찾기
			targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
			matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구


			//(원수사와 가입설계 특약 비교해서)불일치 특약명 찾기(원수사에서 미가입처리 해줄 특약명들)
			targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
			dismatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구



			/**
			 * ===========================================================================================
			 * [STEP 2]
			 * 특약 가입/미가입 처리 진행하기
			 * ===========================================================================================
			 */

			//불일치 특약들에 대해서 원수사에서 미가입 처리 진행
			for(String treatyName : dismatchedTreatyNameList) {
				//미가입 처리를 위해 특약의 가입 처리 영역 element 찾기
				boolean isToggleJoin = false;
				WebElement $treatyJoinInput = null;
				WebElement $treatyJoinLabel = null;


				logger.info("미가입 처리해야할 특약명 : {}", treatyName);
				WebElement $treatyNameTd = $treatyAreaTbody.findElement(By.xpath(".//td[1][text()='" + treatyName + "']"));
				WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
				WebElement $treatyJoinTd = $treatyTr.findElement(By.xpath("./td[4]"));


				helper.moveToElementByJavascriptExecutor($treatyJoinTd);


				//특약이 필수가입인지 가입/미가입인지 확인(필수 가입인 경우 토글 버튼이 없음)
				By by = By.xpath("./span[@class='ne-chk-toggle']");
				isToggleJoin = helper.existElement($treatyJoinTd, by);

				if(isToggleJoin) {
					//미가입 처리해야하는 특약이 가입/미가입 토글인 경우
					$treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input"));
					$treatyJoinLabel = $treatyJoinTd.findElement(By.xpath(".//label"));


					//미가입 처리해야하는 특약이 "가입" 상태인 경우에만 "미가입" 처리를 진행한다.
					if($treatyJoinInput.isSelected()) {
						logger.info("특약명 : {} 미가입 처리를 진행합니다.", treatyName);
						click($treatyJoinLabel);


						/**
						 * 토글의 가입상태 값을 변경하게 되면 불필요하게 삭제 처리한 element들이 다시 원상 복구된다.
						 * 다시 불필요한 element들을 삭제하는 처리가 필요함.
						 */

						/**
						 * xpath로 특약명과 완전 일치하는 element를 찾아오게 하는데 특약명 element에 &nbsp가 껴잇는 경우가 있다.
						 * xpath에서 &nbsp에 대한 처리가 불가능함. 따라서 일단 jquery로 &npsp를 모두 제거시킨 다음에 element를 찾게한다.
						 */
						script = "$(arguments[0]).find('td:nth-child(1)').html(function (i, html) {"
							+ "    return html.replace(/&nbsp;/g, '');"
							+ "});";

						helper.executeJavascript(script, $treatyAreaTbody);


						//원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
						script = "$(arguments[0]).find('span.vertical-line').remove();";
						helper.executeJavascript(script, $treatyAreaTbody);


					}
				} else {
					//미가입 처리해야하는 특약이 필수가입인 경우
					logger.info("해당 특약({})은 필수가입 해야하는 특약입니다. "
						+ "가입설계에 해당 특약을 추가해주세요.", treatyName);
					strangeTreatyNameList.add(treatyName);
				}
			}



			//공통된 특약들에 대해서는 원수사에서 가입 처리 진행
			for(String treatyName : matchedTreatyNameList) {
				//가입 처리를 위해 특약의 가입 처리 영역 element 찾기
				boolean isToggleJoin = false;
				WebElement $treatyJoinInput = null;
				WebElement $treatyJoinLabel = null;


				logger.info("가입 처리해야할 특약명 : {}", treatyName);
				WebElement $treatyNameTd = $treatyAreaTbody.findElement(By.xpath(".//td[1][text()='" + treatyName + "']"));
				WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
				WebElement $treatyJoinTd = $treatyTr.findElement(By.xpath("./td[4]"));


				helper.moveToElementByJavascriptExecutor($treatyJoinTd);


				//특약이 필수가입인지 가입/미가입인지 확인(필수 가입인 경우 토글 버튼이 없음)
				By by = By.xpath("./span[@class='ne-chk-toggle']");
				isToggleJoin = helper.existElement($treatyJoinTd, by);

				if(isToggleJoin) {
					//가입 처리해야하는 특약이 가입/미가입 토글인 경우
					$treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input"));
					$treatyJoinLabel = $treatyJoinTd.findElement(By.xpath(".//label"));


					//가입 처리해야하는 특약이 "미가입" 상태인 경우에만 "가입" 처리를 진행한다.
					if(!$treatyJoinInput.isSelected()) {
						logger.info("특약명 : {} 가입 처리를 진행합니다.", treatyName);
						click($treatyJoinLabel);


						/**
						 * 토글의 가입상태 값을 변경하게 되면 불필요하게 삭제 처리한 element들이 다시 원상 복구된다.
						 * 다시 불필요한 element들을 삭제하는 처리가 필요함.
						 */

						/**
						 * xpath로 특약명과 완전 일치하는 element를 찾아오게 하는데 특약명 element에 &nbsp가 껴잇는 경우가 있다.
						 * xpath에서 &nbsp에 대한 처리가 불가능함. 따라서 일단 jquery로 &npsp를 모두 제거시킨 다음에 element를 찾게한다.
						 */
						script = "$(arguments[0]).find('td:nth-child(1)').html(function (i, html) {"
							+ "    return html.replace(/&nbsp;/g, '');"
							+ "});";

						helper.executeJavascript(script, $treatyAreaTbody);


						//원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
						script = "$(arguments[0]).find('span.vertical-line').remove();";
						helper.executeJavascript(script, $treatyAreaTbody);


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

			//특약에 대해 가입/미가입 처리를 진행하면 tr의 상태가 바꼈기 때문에 다시한번 element를 찾아줘야한다.
			//안그러면 StaleElementReference 예외 발생
			$treatyAreaTbody = $treatyPopupDiv.findElement(By.id("coverage-list"));
			$targetTreatyTrList = $treatyAreaTbody.findElements(By.xpath("./tr[@class='coverage-item']"));
			for(WebElement $treatyTr : $targetTreatyTrList) {

				//특약 정보가 보이도록 스크롤 조정
				helper.moveToElementByJavascriptExecutor($treatyTr);


				//특약명, 특약가입금액, 특약가입영역 element 찾기
				String treatyName = "";
				String treatyAssureMoney = "";
				boolean isToggle = false;
				WebElement $treatyNameTd = $treatyTr.findElement(By.xpath("./td[1]"));
				WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[3]"));
				WebElement $treatyJoinTd = $treatyTr.findElement(By.xpath("./td[4]"));
				WebElement $treatyJoinInput = null;


				//특약명, 특약가입금액 읽어오기
				treatyName = $treatyNameTd.getText();
				treatyAssureMoney = $treatyAssureMoneyTd.getText();
				treatyAssureMoney = treatyAssureMoney.replace("지급", "").replaceAll("[0-9]일", "");
				treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));


				//특약 가입여부 확인
				By by = By.xpath("./span[@class='ne-chk-toggle']");
				isToggle = helper.existElement($treatyJoinTd, by);


				//원수사 특약 정보 적재
				CrawlingTreaty targetTreaty = new CrawlingTreaty();
				targetTreaty.setTreatyName(treatyName);
				targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

				if(isToggle) {
					$treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input"));

					//특약이 "가입" 처리된 경우에만
					if($treatyJoinInput.isSelected()) {
						targetTreatyList.add(targetTreaty);
					}

				} else {
					//특약이 필수가입인 경우
					targetTreatyList.add(targetTreaty);
				}

			}


			if(strangeTreatyNameList.size() > 0) {
				strangeTreatyNameList.forEach(System.out::println);
				throw new Exception("필수가입 특약이 가입설계에 빠져있습니다.");
			}

			/**
			 * ===========================================================================================
			 * [STEP 4]
			 * 원수사 특약 정보 vs 가입설계 특약 정보 비교
			 * ===========================================================================================
			 */
			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
			if(result) {
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





//package com.welgram.crawler.direct.fire.sfi;
//
//import com.welgram.common.MoneyUtil;
//import com.welgram.common.enums.ExceptionEnum;
//import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
//import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
//import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
//import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
//import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
//import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
//import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
//import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
//import com.welgram.crawler.general.CrawlingProduct;
//import com.welgram.crawler.general.CrawlingProduct.Type;
//import com.welgram.crawler.general.CrawlingTreaty;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//
//public class SFI_DSS_D012 extends CrawlingSFIMobile {
//
//	public static void main(String[] args) {
//		executeCommand(new SFI_DSS_D012(), args);
//	}
//
//
//	@Override
//	protected boolean scrap(CrawlingProduct info) throws Exception {
//		WebElement $button = null;
//
//		waitLoadingBar();
//
//		logger.info("네이버페이 관련 팝업 확인 버튼 클릭");
//		WebElement $popUpSection = driver.findElement(By.id("V2Dlpo"));
//		$button = $popUpSection.findElement(By.xpath(".//button[text()='확인']"));
//		click($button);
//
//		logger.info("생년월일 설정");
//		setBirthday(info.getFullBirth());
//
//		logger.info("성별 설정");
//		setGender(info.getGender());
//
//		logger.info("직업 설정");
//		setJob("중고등학교교사");
//
//		logger.info("다음 버튼 클릭");
//		$button = driver.findElement(By.xpath("//footer[@id='footer']//button[text()='다음']"));
//		click($button);
//
//		logger.info("이벤트 참여 안내 팝업 닫기 버튼 클릭");
//		$popUpSection = driver.findElement(By.id("V2Alert"));
//		$button = $popUpSection.findElement(By.xpath(".//button[text()='닫기']"));
//		click($button);
//
//		logger.info("이벤트 페이지를 닫으실 경우 참여가 불가능합니다 팝업 닫기 버튼 클릭");
//		$popUpSection = driver.findElement(By.id("V2Alert"));
//		$button = $popUpSection.findElement(By.xpath(".//button[text()='닫기']"));
//		click($button);
//
//		logger.info("조건 변경하기 버튼 클릭");
//		$button = driver.findElement(By.id("refund-rate"));
//		click($button);
//
//		logger.info("가입형태 설정");
//		setRenewType(info.getProductType());
//
//		logger.info("보험기간 설정");
//		setInsTerm(info.getInsTerm());
//
//		logger.info("납입방법 설정");
//		setNapCycle(info.getNapCycleName());
//
//		logger.info("환급방법 설정");
//		setRefundType(info.getProductKind());
//
//		logger.info("선택완료 버튼 클릭");
//		$button = driver.findElement(By.xpath(".//button[text()='선택완료']"));
//		click($button);
//
//		logger.info("플랜 설정");
//		setPlan(info.planSubName);
//
//		logger.info("특약 설정");
//		setTreaties(info.getTreatyList());
//
//		logger.info("보험료 크롤링");
//		crawlPremium(info);
//
//		logger.info("해약환급금 크롤링");
//		crawlReturnMoneyList(info);
//
//
//		return true;
//	}
//
//
//
//	/**
//	 * 삼성화재 다이렉트 특약설정 TYPE2 : 특약 더보기 버튼을 클릭해서 처리
//	 */
//	public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
//		String script = "";
//		ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
//
//		try {
//
//			//원수사 특약목록 영역
//			WebElement $treatyDiv = driver.findElement(By.xpath("//div[@id='calc-dambolist-table']/div[@class='result-list']"));
//			List<WebElement> $treatyGroupDivList = $treatyDiv.findElements(By.xpath("./div[@class[contains(., 'row-group')][not(contains(., 'tit-type'))]]"));
//
//			//특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리 (*문자열로 시작하는 span 삭제)
//			script = "$(arguments[0]).find('span.basic-bullet.color-type03').remove();";
//			helper.executeJavascript(script, $treatyDiv);
//
//			//특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(UPGRADE/HOT/NEW span 삭제)
//			script = "$(arguments[0]).find('span.tip-layer').remove();";
//			helper.executeJavascript(script, $treatyDiv);
//
//
//			/**
//			 * ===========================================================================================
//			 * [STEP 1]
//			 * 원수사 특약명, 가입설계 특약명 수집 진행하기
//			 * ===========================================================================================
//			 */
//			List<String> targetTreatyNameList = new ArrayList<>();
//			List<String> welgramTreatyNameList = new ArrayList<>();
//
//
//			//원수사 특약명 수집
//			for(WebElement $treatyGroupDiv : $treatyGroupDivList) {
//				WebElement $treatyNameDiv = $treatyGroupDiv.findElement(By.xpath(".//div[@class='tit-name']"));
//
//				String targetTreatyName = $treatyNameDiv.getText().trim();
//				targetTreatyNameList.add(targetTreatyName);
//			}
//
//
//			//가입설계 특약명 수집
//			welgramTreatyNameList = welgramTreatyList.stream().map(t -> t.getTreatyName()).collect(Collectors.toList());
//
//
//			//원수사 특약명 vs 가입설계 특약명 비교 처리(유지, 삭제, 추가돼야할 특약명 분간하는 작업)
//			List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기
//			List<String> copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);     //원본 리스트가 훼손되므로 복사본 떠두기
//			List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
//			List<String> dismatchedTreatyNameList = new ArrayList<>();                             //원수사에서 미가입 처리해야하는 특약명 리스트
//			List<String> strangeTreatyNameList = new ArrayList<>();                                //이상 있는 특약명 리스트
//
//
//			//(원수사와 가입설계 특약 비교해서)공통된 특약명 찾기
//			targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
//			matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
//			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구
//
//
//			//(원수사와 가입설계 특약 비교해서)불일치 특약명 찾기(원수사에서 미가입처리 해줄 특약명들)
//			targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
//			dismatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
//			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);
//
//
//
//			/**
//			 * ===========================================================================================
//			 * [STEP 2]
//			 * 특약 가입/미가입 처리 진행하기
//			 * ===========================================================================================
//			 */
//
//
//			//불일치 특약들에 대해서 원수사에서 미가입 처리 진행
//			for(String treatyName : dismatchedTreatyNameList) {
//				String treatyAssureMoney = "";
//
//				logger.info("특약명 : {} 미가입 처리 진행중...", treatyName);
//
//
//				//특약명, 특약 가입금액 관련 element 찾기
//				WebElement $treatyNameDiv = $treatyDiv.findElement(By.xpath(".//div[text()='" + treatyName + "']"));
//				WebElement $treatyGroupDiv = $treatyNameDiv.findElement(By.xpath("./ancestor::div[@class='row-group'][1]"));
//				WebElement $treatyAssureMoneyDiv = $treatyGroupDiv.findElement(By.xpath("./div[@class[contains(., 'active')]]"));
//				WebElement $treatyAssureMoneySpan = $treatyAssureMoneyDiv.findElement(By.xpath(".//span[@class='price']"));
//				WebElement $treatyAssureMoneyButton = $treatyAssureMoneySpan.findElement(By.xpath("./parent::button"));
//
//				treatyAssureMoney = $treatyAssureMoneySpan.getText();
//
//				//미가입 처리해야하는 특약의 가입 상태가 "가입"인 경우(=가입금액란에 가입금액이 표시된 경우)
//				boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);
//				if(isJoin) {
//
//					//특약 가입금액 조정 팝업창 열기
//					click($treatyAssureMoneyButton);
//
//					//미가입 버튼 클릭
//					WebElement $popupSection = driver.findElement(By.id("V2LongEditDambo"));
//					WebElement $popupUl = $popupSection.findElement(By.id("security-list"));
//					WebElement $미가입Div = $popupUl.findElement(By.xpath(".//div[text()='미가입']"));
//					WebElement $미가입Label = $미가입Div.findElement(By.xpath("./parent::label"));
//					click($미가입Label);
//
//					//확인 버튼 클릭
//					WebElement $confirmButton = $popupSection.findElement(By.xpath(".//button[text()='확인']"));
//					click($confirmButton);
//				}
//			}
//
//
//
//			/**
//			 * TODO 이게 다이렉트 상품에 한해 적합한 프로세스인지 한번 확인해볼 필요 있음
//			 * 공통된 특약명에 대해서는 사실 가입금액 조정 과정이 필요하다.
//			 * 하지만 다이렉트 상품의 경우 원수사에서 default로 설정한 특약의 가입금액이 의미있다고 판단된다.
//			 * 따라서 굳이 원수사가 default로 설정한 가입금액을 우리 가입설계 특약의 금액에 맞게 꾸역꾸역
//			 * 조정하는 과정이 필요할까?
//			 * 원수사가 default로 설정한 가입금액이 가입설계 가입금액과 다르면 가입설계의 가입금액을
//			 * 수정하도록 예외를 발생시키는게 더 의미있다고 판단하기에
//			 * 공통된 특약들에 대해서는 따로 가입금액 조정 처리를 하지 않도록 하겠다.
//			 *
//			 */
//
//
//
//			/**
//			 * ===========================================================================================
//			 * [STEP 3]
//			 * 실제 가입처리된 원수사 특약 정보를 수집한다(유효성 검사를 하기 위함)
//			 * ===========================================================================================
//			 */
//			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
//
//			for(WebElement $treatyGroup : $treatyGroupDivList) {
//
//				//특약명, 특약가입금액  element 찾기
//				String treatyName = "";
//				String treatyAssureMoney = "";
//				WebElement $treatyNameDiv = $treatyGroup.findElement(By.xpath(".//div[@class='tit-name']"));
//				WebElement $treatyAssureMoneyDiv = $treatyGroup.findElement(By.xpath("./div[@class[contains(., 'active')]]"));
//				WebElement $treatyAssureMoneySpan = $treatyAssureMoneyDiv.findElement(By.xpath(".//span[@class='price']"));
//
//
//				//특약명, 특약가입금액 읽어오기
//				treatyName = $treatyNameDiv.getText();
//				treatyAssureMoney = $treatyAssureMoneySpan.getText();
//
//
//				//가입하는 특약에 대해서만 원수사 특약 정보 적재
//				boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);
//				if(isJoin) {
//					treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));
//
//					CrawlingTreaty targetTreaty = new CrawlingTreaty();
//					targetTreaty.setTreatyName(treatyName);
//					targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
//
//					targetTreatyList.add(targetTreaty);
//				}
//
//			}
//
//
//
//			/**
//			 * ===========================================================================================
//			 * [STEP 4]
//			 * 원수사 특약 정보 vs 가입설계 특약 정보 비교
//			 * ===========================================================================================
//			 */
//			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
//			if(result) {
//				logger.info("특약 정보 모두 일치");
//			} else {
//				logger.info("특약 정보 불일치");
//				throw new Exception();
//			}
//
//
//		} catch (Exception e) {
//			throw new SetTreatyException(e, exceptionEnum.getMsg());
//		}
//	}
//
//
//
//	@Override
//	public void setRenewType(Object... obj) throws SetRenewTypeException {
//		String title = "가입형태(=갱신유형)";
//		Type renewType = (Type) obj[0];
//		String expectedRenewType = (renewType == Type.갱신형) ? "갱신형" : "비갱신형";
//		String actualRenewType = "";
//		String script = "";
//
//
//		try {
//
//			//갱신유형 관련 element 찾기
//			WebElement $renewTypeDiv = driver.findElement(By.id("product-cls"));
//			WebElement $renewTypeLabel = $renewTypeDiv.findElement(By.xpath(".//label[text()='" + expectedRenewType + "']"));
//
//
//			//실제 선택된 갱신유형 값 읽어오기
//			script = "return $('input[name=product-cls]:checked').attr('id');";
//			String id = String.valueOf(helper.executeJavascript(script));
//			$renewTypeLabel = $renewTypeDiv.findElement(By.xpath(".//label[@for='product-cls']"));
//			actualRenewType = $renewTypeLabel.getText();
//
//
//			//비교
//			super.printLogAndCompare(title, expectedRenewType, actualRenewType);
//
//		} catch (Exception e) {
//			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RENEW_TYPE;
//			throw new SetRenewTypeException(e, exceptionEnum.getMsg());
//		}
//	}
//
//
//
//	@Override
//	public void setInsTerm(Object... obj) throws SetInsTermException {
//		String title = "보험기간";
//		String expectedInsTerm = (String) obj[0];
//		String actualInsTerm = "";
//		String script = "";
//
//		try {
//
//			//보험기간 관련 element 찾기
//			WebElement $insTermDiv = driver.findElement(By.id("product-cls"));
//			WebElement $insTermLabel = $insTermDiv.findElement(By.xpath(".//label[text()='" + expectedInsTerm + "']"));
//
//
//			//실제 선택된 보험기간 값 읽어오기
//			script = "return $('input[name=insured-term]:checked').attr('id');";
//			String id = String.valueOf(helper.executeJavascript(script));
//			$insTermLabel = $insTermDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
//			actualInsTerm = $insTermLabel.getText();
//
//
//			//비교
//			super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);
//
//		} catch (Exception e) {
//			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
//			throw new SetInsTermException(e, exceptionEnum.getMsg());
//		}
//	}
//
//
//
//	@Override
//	public void setNapTerm(Object... obj) throws SetNapTermException {
//		String title = "납입기간";
//		String expectedNapTerm = (String) obj[0];
//		String actualNapTerm = "";
//		String script = "";
//
//		try {
//
//			//납입기간 관련 element 찾기
//			WebElement $napTermDiv = driver.findElement(By.id("payment-term"));
//			WebElement $napTermLabel = $napTermDiv.findElement(By.xpath(".//label[text()='" + expectedNapTerm + "']"));
//
//
//			//실제 선택된 납입기간 값 읽어오기
//			script = "return $('input[name=payment-term]:checked').attr('id');";
//			String id = String.valueOf(helper.executeJavascript(script));
//			$napTermLabel = $napTermDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
//			actualNapTerm = $napTermLabel.getText();
//
//
//			//비교
//			super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);
//
//		} catch (Exception e) {
//			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
//			throw new SetNapTermException(e, exceptionEnum.getMsg());
//		}
//	}
//
//
//
//	@Override
//	public void setNapCycle(Object... obj) throws SetNapCycleException {
//		String title = "납입방법";
//		String expectedNapCycle = (String) obj[0];
//		String actualNapCycle = "";
//		String script = "";
//
//		try {
//
//			//납입방법 관련 element 찾기
//			WebElement $napCycleDiv = driver.findElement(By.id("payment-method"));
//			WebElement $napCycleLabel = $napCycleDiv.findElement(By.xpath(".//label[text()='" + expectedNapCycle + "']"));
//
//
//			//실제 선택된 납입방법 값 읽어오기
//			script = "return $('input[name=payment-method]:checked').attr('id');";
//			String id = String.valueOf(helper.executeJavascript(script));
//			$napCycleLabel = $napCycleDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
//			actualNapCycle = $napCycleLabel.getText();
//
//
//			//비교
//			super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);
//
//		} catch (Exception e) {
//			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
//			throw new SetNapCycleException(e, exceptionEnum.getMsg());
//		}
//	}
//
//
//
//	@Override
//	public void setRefundType(Object... obj) throws SetRefundTypeException {
//		String title = "환급방법";
//		String expectedRefundType = (String) obj[0];
//		String actualRefundType = "";
//		String script = "";
//
//		try {
//
//			//환급방법 관련 element 찾기
//			WebElement $refundTypeDiv = driver.findElement(By.id("refund-rate"));
//			WebElement $refundTypeLabel = $refundTypeDiv.findElement(By.xpath(".//label[text()='" + expectedRefundType + "']"));
//
//
//			//실제 선택된 환급방법 값 읽어오기
//			script = "return $('input[name=refund-rate]:checked').attr('id');";
//			String id = String.valueOf(helper.executeJavascript(script));
//			$refundTypeLabel = $refundTypeDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
//			actualRefundType = $refundTypeLabel.getText();
//
//
//			//비교
//			super.printLogAndCompare(title, expectedRefundType, actualRefundType);
//
//		} catch (Exception e) {
//			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_REFUND_TYPE;
//			throw new SetRefundTypeException(e, exceptionEnum.getMsg());
//		}
//	}
//
//
//
//	@Override
//	public void setGender(Object... obj) throws SetGenderException {
//		String title = "성별";
//
//		int gender = (int) obj[0];
//		String expectedGenderText = (gender == MALE) ? "남성" : "여성";
//		String actualGenderText = "";
//
//		try {
//
//			//성별 element 찾기
//			WebElement $genderSection = driver.findElement(By.id("V2Dropdown"));
//			WebElement $genderButton = $genderSection.findElement(By.xpath(".//button[text()='" + expectedGenderText + "']"));
//
//			//성별 클릭
//			click($genderButton);
//
//			//실제 선택된 성별 값 읽어오기
//			WebElement $genderDiv = driver.findElement(By.id("gender"));
//			$genderButton = $genderDiv.findElement(By.xpath(".//button[@class[contains(., 'value')]]"));
//			actualGenderText = $genderButton.getText();
//
//			//비교
//			super.printLogAndCompare(title, expectedGenderText, actualGenderText);
//
//		} catch (Exception e) {
//			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
//			throw new SetGenderException(e, exceptionEnum.getMsg());
//		}
//	}
//
//}
