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
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class SFI_CHL_D017 extends CrawlingSFIDirect {

	public static void main(String[] args) {
		executeCommand(new SFI_CHL_D017(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $button = null;

		waitLoadingBar();

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("가입형태 설정");
		setJoinType("미성년 자녀");

		logger.info("자녀의 생년월일 설정");
		setBirthday(info.getFullBirth());

		logger.info("자녀의 성별 설정");
		setGender(info.getGender());

		logger.info("자녀정보 설정");
		setChildType("초등학생");

		logger.info("보험료 계산하기 버튼 클릭");
		$button = driver.findElement(By.id("btn-next-step"));
		click($button);

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("환급유형 설정");
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
		String refundType = (ProductKind.만기환급형 == mainTreaty.productKind) ? "일부환급형" : "순수보장형";
		setRefundType(refundType);

		logger.info("상품유형(=갱신유형) 설정");
		String renewType = info.getProductType().name();
		setRenewType(renewType);

		logger.info("보험기간 설정");
		setInsTerm(info.getInsTerm());

		logger.info("플랜 설정");
		setPlan(info.planSubName);

		logger.info("특약 설정");
		setTreaties(info.getTreatyList(), Integer.parseInt(info.getAge()));

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
	public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

		CrawlingProduct info = (CrawlingProduct) obj[0];
		List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

		try {
			// 해약환급금 팝업 오픈 버튼 element 찾기
			logger.info("해약환급금 팝업 오픈 버튼 클릭");
			WebElement $openReturnMoneyPopupButton = driver.findElement(By.id("btn-more"));
			click($openReturnMoneyPopupButton);

			// 해약환급금 관련 정보 element 찾기
			String script = "return $('tbody.__refund-list:visible')[0]";
			WebElement $returnMoneyTbody = (WebElement) helper.executeJavascript(script);
			List<WebElement> $returnMoneyTrList = $returnMoneyTbody.findElements(By.tagName("tr"));

			for (WebElement $tr : $returnMoneyTrList) {
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
				returnMoney = returnMoney.replaceAll("[^0-9]", "");
				returnMoneyMin = returnMoneyMin.replaceAll("[^0-9]", "");

				logger.info("경과기간 : {} | 납입보험료 : {} | 최저환급금 : {} | 최저환급률 : {} | 공시환급금 : {} | 공시환급률 : {}"
					, term, premiumSum, returnMoneyMin, returnRateMin, returnMoney, returnRate);

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



	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {

		String title = "보험료 크롤링";

		CrawlingProduct info = (CrawlingProduct) obj[0];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
		ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

		try {
			// 보험료 크롤링 전에는 대기시간을 넉넉히 준다
			WaitUtil.waitFor(5);

			WebElement $premiumDiv = driver.findElement(By.id("applyPremium"));
			WebElement $premiumStrong = $premiumDiv.findElement(By.xpath("./strong[@class='blind']"));
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



	/**
	 * 삼성화재 다이렉트 특약설정 TYPE1 : 한번에 보장 변경 팝업에서 처리
	 * @param welgramTreatyList 가입설계 특약 목록
	 * @throws SetTreatyException
	 */
	public void setTreaties(List<CrawlingTreaty> welgramTreatyList, int age) throws SetTreatyException {

		String script = "";
		ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;

		try {
			/**
			 * 원수사 특약명이 다발성소아암 진단비(n세 만기)인데 가입연령에 따라 20세부터 시작해 1살씩 증가
			 * 가설의 특약명을 다발성소아암 진단비로 세팅하고 스크립트에서 맞춰준다
			 */

			for (int i = 0; i < welgramTreatyList.size(); i++) {
				String treatyName = welgramTreatyList.get(i).treatyName;

				if (treatyName.equals("다발성소아암 진단비")) {
					age = age + 20;
					welgramTreatyList.get(i).treatyName = treatyName + "(" + age + "세 만기)";
				}
			}

			// 플랜 영역 thead
			WebElement $planAreaThead = driver.findElement(By.id("coverage-header"));
			WebElement $selectedPlanLabel = $planAreaThead.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
			WebElement $changeButton = $selectedPlanLabel.findElement(By.xpath("./following-sibling::button"));

			// 선택된 플랜의 "한번에 보장 변경" 버튼 클릭
			click($changeButton);

			// 한번에 보장 변경 팝업 element 찾기
			WebElement $treatyPopupDiv = driver.findElement(By.id("ChildDamboAllChange"));
			WebElement $treatyAreaTbody = $treatyPopupDiv.findElement(By.id("coverage-list"));
			List<WebElement> $targetTreatyTrList = $treatyAreaTbody.findElements(By.xpath("./tr[@class='coverage-item']"));

			/**
			 * xpath로 특약명과 완전 일치하는 element를 찾아오게 하는데 특약명 element에 &nbsp가 껴잇는 경우가 있다.
			 * xpath에서 &nbsp에 대한 처리가 불가능함. 따라서 일단 jquery로 &npsp를 모두 제거시킨 다음에 element를 찾게한다.
			 */

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

			// 원수사 특약명 수집
			for (WebElement $treatyTr : $targetTreatyTrList) {
				WebElement $treatyNameTd = $treatyTr.findElement(By.xpath("./td[1]"));

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
				// 미가입 처리를 위해 특약의 가입 처리 영역 element 찾기
				boolean isToggleJoin = false;
				WebElement $treatyJoinInput = null;
				WebElement $treatyJoinLabel = null;

				logger.info("미가입 처리해야할 특약명 : {}", treatyName);
				WebElement $treatyNameTd = $treatyAreaTbody.findElement(By.xpath(".//td[1][text()='" + treatyName + "']"));
				WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
				WebElement $treatyJoinTd = $treatyTr.findElement(By.xpath("./td[4]"));

				helper.moveToElementByJavascriptExecutor($treatyJoinTd);

				// 특약이 필수가입인지 가입/미가입인지 확인(필수 가입인 경우 토글 버튼이 없음)
				By togglePosition = By.xpath("./span[@class='ne-chk-toggle']");
				isToggleJoin = helper.existElement($treatyJoinTd, togglePosition);

				if (isToggleJoin) {
					// 미가입 처리해야하는 특약이 가입/미가입 토글인 경우
					$treatyJoinInput = $treatyJoinTd.findElement(By.xpath(".//input"));
					$treatyJoinLabel = $treatyJoinTd.findElement(By.xpath(".//label"));

					// 미가입 처리해야하는 특약이 "가입" 상태인 경우에만 "미가입" 처리를 진행한다.
					if ($treatyJoinInput.isSelected()) {
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

						removeWhiteSpaceFromElement($treatyAreaTbody);

						// 원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
						script = "$(arguments[0]).find('span.vertical-line').remove();";
						helper.executeJavascript(script, $treatyAreaTbody);
					}
				}
			}

			// 공통된 특약들에 대해서는 원수사에서 가입 처리 진행
			for (String treatyName : matchedTreatyNameList) {
				// 가입 처리를 위해 특약의 가입 처리 영역 element 찾기
				boolean isToggleJoin = false;
				WebElement $treatyJoinInput = null;
				WebElement $treatyJoinLabel = null;

				logger.info("가입 처리해야할 특약명 : {}", treatyName);
				WebElement $treatyNameTd = $treatyAreaTbody.findElement(By.xpath(".//td[1][text()='" + treatyName + "']"));
				WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
				WebElement $treatyJoinTd = $treatyTr.findElement(By.xpath("./td[4]"));

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

						/**
						 * 토글의 가입상태 값을 변경하게 되면 불필요하게 삭제 처리한 element들이 다시 원상 복구된다.
						 * 다시 불필요한 element들을 삭제하는 처리가 필요함.
						 */

						/**
						 * xpath로 특약명과 완전 일치하는 element를 찾아오게 하는데 특약명 element에 &nbsp가 껴잇는 경우가 있다.
						 * xpath에서 &nbsp에 대한 처리가 불가능함. 따라서 일단 jquery로 &npsp를 모두 제거시킨 다음에 element를 찾게한다.
						 */
						removeWhiteSpaceFromElement($treatyAreaTbody);

						// 원수사 특약명을 수월하게 수집하기 위해서 불필요한 element 삭제 처리
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

			// 특약에 대해 가입/미가입 처리를 진행하면 tr의 상태가 바꼈기 때문에 다시한번 element를 찾아줘야한다.
			// 안그러면 StaleElementReference 예외 발생
			$treatyAreaTbody = $treatyPopupDiv.findElement(By.id("coverage-list"));
			$targetTreatyTrList = $treatyAreaTbody.findElements(By.xpath("./tr[@class='coverage-item']"));
			for (WebElement $treatyTr : $targetTreatyTrList) {

				// 특약 정보가 보이도록 스크롤 조정
				helper.moveToElementByJavascriptExecutor($treatyTr);

				// 특약명, 특약가입금액, 특약가입영역 element 찾기
				String treatyName = "";
				String treatyAssureMoney = "";
				boolean isToggle = false;
				WebElement $treatyNameTd = $treatyTr.findElement(By.xpath("./td[1]"));
				WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[3]"));
				WebElement $treatyJoinTd = $treatyTr.findElement(By.xpath("./td[4]"));
				WebElement $treatyJoinInput = null;

				// 특약명, 특약가입금액 읽어오기
				treatyName = $treatyNameTd.getText();

				treatyAssureMoney = $treatyAssureMoneyTd.getText();
				treatyAssureMoney = treatyAssureMoney.replace("지급", "").replaceAll("[0-9]일", "");
				treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

				// 특약 가입여부 확인
				By togglePosition = By.xpath("./span[@class='ne-chk-toggle']");
				isToggle = helper.existElement($treatyJoinTd, togglePosition);

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
					// 특약이 필수가입인 경우
					targetTreatyList.add(targetTreaty);
				}
			}

			/**
			 * ===========================================================================================
			 *  4]
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



	// 로딩바 명시적 대기
	@Override
	public void waitLoadingBar() {
		try {
			helper.waitForCSSElement("#loading-message-box");
			helper.waitForCSSElement("#loading-common");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}