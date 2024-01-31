package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.DateUtil;
import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;



public class SFI_OST_D001 extends CrawlingSFIDirect {

	public static void main(String[] args) {
		executeCommand(new SFI_OST_D001(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $button = null;

		waitLoadingBar();

		logger.info("보험료 계산 버튼 클릭");
		$button = driver.findElement(By.xpath("//span[text()='보험료 계산']"));
		click($button);

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("보험가입 형태 설정(개인형 or 동반형 등)");
		setJoinType("개인형");

		logger.info("보험료 계산/가입하기 버튼 클릭");
		$button = driver.findElement(By.id("btn-join"));
		click($button);

		logger.info("생년월일 설정");
		setBirthday(info.getFullBirth());

		logger.info("성별 설정");
		setGender(info.getGender());

		logger.info("다음 버튼 클릭");
		$button = driver.findElement(By.id("btn-next-step-person-info"));
		click($button);

		logger.info("여행 출발날짜 설정");
		String travelDepartureDate = DateUtil.dateAfter7Days(new Date());
		setTravelDepartureDate(travelDepartureDate);

		logger.info("여행 출발시간 설정");
		setTravelDepartureTime("00 시");

		logger.info("여행 도착날짜 설정");
		String travelArrivalDate = DateUtil.dateAfter13Days(new Date());
		setTravelArrivalDate(travelArrivalDate);

		logger.info("여행 도착시간 설정");
		setTravelArrivalTime("23 시");

		logger.info("여행목적 설정");
		setTravelGoal("여행/관광/캠프");

		logger.info("다음 버튼 클릭");
		$button = driver.findElement(By.id("btn-next-step-period"));
		click($button);

		logger.info("여행 관련 안내사항에 대해 전체확인 버튼 클릭");
		$button = driver.findElement(By.id("btn-all-agree"));
		click($button);

		logger.info("다음 버튼 클릭");
		$button = driver.findElement(By.id("btn-next-step-read-info"));
		click($button);
		WaitUtil.waitFor(3);

		logger.info("실손의료보험 중복가입 유의사항 팝업에 대해 확인 체크박스 체크!");
		$button = driver.findElement(By.xpath("//div[@id='checkbox-group-confirm']/label"));
		click($button);

		logger.info("확인 버튼 클릭");
		$button = driver.findElement(By.id("btn-confirm"));
		click($button);

		logger.info("플랜 설정1");
		setPlan(info.getTextType());

		logger.info("플랜 설정2");
		setPlan2(info.getTextType());

		logger.info("특약 설정");
		setTreaties(info.getTreatyList());

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		return true;
	}



	public void setPlan(String expectedPlan) throws CommonCrawlerException {
		String title = "플랜1";
		String actualPlan = "";
		String[] textTypes = expectedPlan.split("#");

		try {
			WebElement $planDiv = driver.findElement(By.id("tab-plan"));
			WebElement $planButton = null;

			for (String textType : textTypes) {
				try {
					textType = textType.trim();
					$planButton = $planDiv.findElement(By.xpath("./button[normalize-space()='" + textType + "']"));
					click($planButton);
					expectedPlan = textType;
					break;
				} catch (NoSuchElementException e) {}
			}

			// 실제 선택된 플랜 값 읽어오기
			$planButton = $planDiv.findElement(By.xpath("./button[@class[contains(., 'active')]]"));
			actualPlan = $planButton.getText().trim();

			super.printLogAndCompare(title, expectedPlan, actualPlan);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
			throw new CommonCrawlerException(e, exceptionEnum.getMsg());
		}
	}



	public void setPlan2(String expectedPlan) throws CommonCrawlerException {
		String title = "플랜2";
		String actualPlan = "";
		String[] textTypes = expectedPlan.split("#");

		try {
			// 플랜 관련 element 찾기
			WebElement $planAreaTr = driver.findElement(By.id("plans-header"));
			WebElement $planLabel = null;

			for (String textType : textTypes) {
				try {
					textType = textType.trim();
					$planLabel = $planAreaTr.findElement(By.xpath(".//label[normalize-space()='" + textType + "']"));
					click($planLabel);
					expectedPlan = textType;
					break;
				} catch (NoSuchElementException e) {}
			}

			// 실제 선택된 플랜 값 읽어오기
			$planLabel = $planAreaTr.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
			actualPlan = $planLabel.getText().trim();

			// 비교
			super.printLogAndCompare(title, expectedPlan, actualPlan);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
			throw new CommonCrawlerException(e, exceptionEnum.getMsg());
		}
	}



	public void setJoinType(String expectedJoinType) throws CommonCrawlerException {
		String title = "가입형태";
		String actualJoinType = "";

		try {
			WebElement $joinTypeAreaDiv = driver.findElement(By.id("join-type"));

			// 텍스트로 가입형태를 잘 클릭하기 위해 불필요한 element 제거
			String script = "$(arguments[0]).find('div.input-txt > span').remove();";
			helper.executeJavascript(script, $joinTypeAreaDiv);

			// 가입형태 관련 element 찾기
			WebElement $joinTypeDiv = $joinTypeAreaDiv.findElement(By.xpath(".//div[@class='input-txt'][normalize-space()='" + expectedJoinType +"']"));

			// 가입형태 클릭
			click($joinTypeDiv);

			// 실제 선택된 가입형태 값 읽어오기
			$joinTypeAreaDiv = $joinTypeAreaDiv.findElement(By.xpath("./div[@class[contains(., 'active')]]"));
			$joinTypeAreaDiv = $joinTypeAreaDiv.findElement(By.xpath(".//div[@class='input-txt']"));
			actualJoinType = $joinTypeAreaDiv.getText().trim();

			// 비교
			super.printLogAndCompare(title, expectedJoinType, actualJoinType);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_JOIN_TYPE;
			throw new CommonCrawlerException(e, exceptionEnum.getMsg());
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

			WebElement $footer = driver.findElement(By.id("left-footer-travel"));
			WebElement $selectedPlanDiv = $footer.findElement(By.xpath(".//div[@class[contains(., 'selected')]]"));
			WebElement $premiumDiv = $selectedPlanDiv.findElement(By.xpath(".//div[@class='plan_premium']"));
			String premium = $premiumDiv.getText();
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
	 * 삼성화재 다이렉트 특약설정 TYPE2 : 특약 더보기 버튼을 클릭해서 처리
	 */
	public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
		String script = "";
		ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;

		try {
			// 원수사 특약 tbody 영역
			WebElement $treatyTbody = driver.findElement(By.id("coverages-body"));
			List<WebElement> $treatyTrList = $treatyTbody.findElements(By.tagName("tr"));

			// 특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(span 삭제)
			script = "$(arguments[0]).find('span.ne-hidden').remove();";
			helper.executeJavascript(script, $treatyTbody);

			// 특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(더보기 버튼 삭제)
			script = "$(arguments[0]).find('button.ne-bt-more').remove();";
			helper.executeJavascript(script, $treatyTbody);

			// 특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(*로 시작하는 텍스트 삭제)
			script = "$(arguments[0]).find('div.unit-flag-recomm2').remove();";
			helper.executeJavascript(script, $treatyTbody);

			// 특약 가입금액 비교를 수월하게 하기 위해 불필요한 element 삭제 처리( (자기부담금N%) 텍스트 삭제)
			script = "$(arguments[0]).find('div.hue-peacock').remove();";
			helper.executeJavascript(script, $treatyTbody);

			/**
			 * ===========================================================================================
			 * [STEP 1]
			 * 원수사 특약명, 가입설계 특약명 수집 진행하기
			 * ===========================================================================================
			 */

			List<String> targetTreatyNameList = new ArrayList<>();
			List<String> welgramTreatyNameList = new ArrayList<>();

			// 원수사 특약명 수집
			for (WebElement $treatyTr : $treatyTrList) {
				WebElement $treatyNameTh = $treatyTr.findElement(By.xpath("./th[1]"));

				// 원수사 특약명을 가져오기 위해 특약명이 보이도록 스크롤 처리를 해야함.
				helper.moveToElementByJavascriptExecutor($treatyNameTh);
				String targetTreatyName = $treatyNameTh.getText().trim();

				targetTreatyNameList.add(targetTreatyName);
			}

			// 가입설계 특약명 수집
			welgramTreatyNameList = welgramTreatyList.stream().map(t -> t.getTreatyName()).collect(Collectors.toList());

			// 원수사 특약명 vs 가입설계 특약명 비교 처리(유지, 삭제, 추가돼야할 특약명 분간하는 작업)
			List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기
			List<String> copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);     //원본 리스트가 훼손되므로 복사본 떠두기
			List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
			List<String> dismatchedTreatyNameList = new ArrayList<>();                             //원수사에서 미가입 처리해야하는 특약명 리스트
			List<String> strangeTreatyNameList = new ArrayList<>();                                //이상 있는 특약명 리스트

			// (원수사와 가입설계 특약 비교해서)공통된 특약명 찾기
			targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
			matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구

			// (원수사와 가입설계 특약 비교해서)불일치 특약명 찾기(원수사에서 미가입처리 해줄 특약명들)
			targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
			dismatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
			targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);

			/**
			 * ===========================================================================================
			 * [STEP 2]
			 * 특약 가입/미가입 처리 진행하기
			 * ===========================================================================================
			 */

			// 불일치 특약들에 대해서 원수사에서 미가입 처리 진행
			for (String treatyName : dismatchedTreatyNameList) {
				String treatyAssureMoney = "";

				logger.info("특약명 : {} 미가입 처리 진행중...", treatyName);

				// 특약명, 특약 가입금액 관련 element 찾기
				WebElement $treatyNameTh = $treatyTbody.findElement(By.xpath(".//th[normalize-space()='" + treatyName + "']"));
				WebElement $treatyTr = $treatyNameTh.findElement(By.xpath("./parent::tr"));
				WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[@class[contains(., 'selectedPlan')]]"));
				WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.xpath("./span[@class='cont']"));

				treatyAssureMoney = $treatyAssureMoneySpan.getAttribute("textContent");

				// 미가입 처리해야하는 특약의 가입 상태가 "가입"인 경우(=가입금액란에 가입 또는 가입금액이 표시된 경우)
				boolean isJoin = (!"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney));
				if (isJoin) {
					// 팝업창 클릭을 위해 스크롤 조정
					helper.moveToElementByJavascriptExecutor($treatyTr);

					// 특약 가입/미가입 팝업창 열기
					click($treatyAssureMoneyTd);

					// 특약 팝업 내 가입금액 관련 element 찾기
					WebElement $treatyAssureMoneyArea = driver.findElement(By.id("coverage-values"));
					WebElement $treatyAssureMoneyUl = $treatyAssureMoneyArea.findElement(By.xpath(".//ul[@class='slider-list']"));

					// 가입금액 선택을 수월하게 하기 위해 불필요한 element 삭제 처리("가입금액" span 삭제)
					script = "$(arguments[0]).find('span.blind').remove();";
					helper.executeJavascript(script, $treatyAssureMoneyUl);

					// 가입금액 선택을 수월하게 하기 위해 불필요한 element 삭제 처리("선택됨" span 삭제)
					script = "$(arguments[0]).find('span.sr-only').remove();";
					helper.executeJavascript(script, $treatyAssureMoneyUl);

					// 미가입 버튼 클릭
					WebElement $treatyAssureMoneyP = $treatyAssureMoneyArea.findElement(By.xpath(".//p[text()='미가입']"));
					WebElement $treatyAssureMoneyA = $treatyAssureMoneyP.findElement(By.xpath("./parent::a"));
					click($treatyAssureMoneyA);

					// 특약 팝업창 닫기 위해 확인 버튼 클릭
					WebElement $popupCloseButton = driver.findElement(By.id("btn-confirm"));
					click($popupCloseButton);

					// 안내 알럿창 뜨는 경우 확인 버튼 클릭
					By alertPosition = By.id("CommonAlert");
					boolean isAlert = helper.existElement(alertPosition);

					if (isAlert) {
						WebElement $alert = driver.findElement(alertPosition);
						WebElement $alertConfirmButton = $alert.findElement(By.id("btn-confirm"));
						click($alertConfirmButton);
					}
				}
			}

			/**
			 * TODO 이게 다이렉트 상품에 한해 적합한 프로세스인지 한번 확인해볼 필요 있음
			 * 공통된 특약명에 대해서는 사실 가입금액 조정 과정이 필요하다.
			 * 하지만 다이렉트 상품의 경우 원수사에서 default로 설정한 특약의 가입금액이 의미있다고 판단된다.
			 * 따라서 굳이 원수사가 default로 설정한 가입금액을 우리 가입설계 특약의 금액에 맞게 꾸역꾸역
			 * 조정하는 과정이 필요할까?
			 * 원수사가 default로 설정한 가입금액이 가입설계 가입금액과 다르면 가입설계의 가입금액을
			 * 수정하도록 예외를 발생시키는게 더 의미있다고 판단하기에
			 * 공통된 특약들에 대해서는 따로 가입금액 조정 처리를 하지 않도록 하겠다.
			 *
			 */

			/**
			 * ===========================================================================================
			 * [STEP 3]
			 * 실제 가입처리된 원수사 특약 정보를 수집한다(유효성 검사를 하기 위함)
			 * ===========================================================================================
			 */

			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

			for (WebElement $treatyTr : $treatyTrList) {
				// 특약 정보가 보이도록 스크롤 조정
				helper.moveToElementByJavascriptExecutor($treatyTr);

				// 특약명, 특약가입금액  element 찾기
				String treatyName = "";
				String treatyAssureMoney = "";
				WebElement $treatyNameTh = $treatyTr.findElement(By.xpath("./th[1]"));
				WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[@class[contains(., 'selectedPlan')]]"));
				WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.xpath("./span[@class='cont']"));

				// 특약명, 특약가입금액 읽어오기
				treatyName = $treatyNameTh.getText();
				treatyAssureMoney = $treatyAssureMoneySpan.getText();
				treatyAssureMoney = treatyAssureMoney.replace("지급", "").replace("한도", "");

				// 가입하는 특약에 대해서만 원수사 특약 정보 적재
				boolean isJoin = !"미가입".equals(treatyAssureMoney) && !"-".equals(treatyAssureMoney);
				if (isJoin) {
					// 여권 특약의 경우 가입금액이 없고 "가입"으로 표기하기 때문에 분기처리가 필요함
					if(treatyName.contains("여권")) {
						treatyAssureMoney = "50000";
					} else {
						treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));
					}

					CrawlingTreaty targetTreaty = new CrawlingTreaty();
					targetTreaty.setTreatyName(treatyName);
					targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

					targetTreatyList.add(targetTreaty);
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



	@Override
	public void setBirthday(Object... obj) throws SetBirthdayException {
		String title = "생년월일";
		String expectedFullBirth = (String) obj[0];
		String actualFullBirth = "";

		try {
			// 생년월일 element 찾기
			WebElement $birthInput = driver.findElement(By.id("birthdate"));

			// 생년월일 설정
			actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

			// 생년월일 비교
			super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
			throw new SetBirthdayException(e, exceptionEnum.getMsg());
		}
	}



	@Override
	public void setGender(Object... obj) throws SetGenderException {
		String title = "성별";

		int gender = (int) obj[0];
		String expectedGenderText = (gender == MALE) ? "남" : "여";
		String actualGenderText = "";

		try {
 			// 성별 element 찾기
			WebElement $genderDiv = driver.findElement(By.id("select-gender"));
			WebElement $genderLabel = $genderDiv.findElement(By.xpath("./label[normalize-space()='" + expectedGenderText + "']"));

			// 성별 클릭
			click($genderLabel);

			// 실제 선택된 성별 값 읽어오기
			$genderLabel = $genderDiv.findElement(By.xpath("./label[@class[contains(., 'active')]]"));
			actualGenderText = $genderLabel.getText().trim();

			// 비교
			super.printLogAndCompare(title, expectedGenderText, actualGenderText);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
			throw new SetGenderException(e, exceptionEnum.getMsg());
		}
	}



	public void setTravelDepartureDate(String expectedDepartureDate) throws SetTravelPeriodException {
		String title = "여행 출발날짜";
		String actualDepartureDate = "";
		WebElement $button = null;

		// 예상 여행출발일 파싱 작업
		int expectedYear = Integer.parseInt(expectedDepartureDate.substring(0, 4));
		int expectedMonth = Integer.parseInt(expectedDepartureDate.substring(4, 6));
		int expectedDate = Integer.parseInt(expectedDepartureDate.substring(6));

		int calendarCurrentYear = 0;
		int calendarCurrentMonth = 0;

		try {
			// 여행 출발날짜 선택을 위해 캘린더 버튼 클릭
			WebElement $calendarDiv = driver.findElement(By.id("calendar-start-date"));
			click($calendarDiv);

			// 현재 캘린더에 default로 세팅된 년, 월 읽어오기
			$calendarDiv = driver.findElement(By.xpath("//div[@class='component-cal-container']"));
			WebElement $calendarCurrentYear = $calendarDiv.findElement(By.xpath(".//span[@class='cal-txt-year']"));
			WebElement $calendarCurrentMonth = $calendarDiv.findElement(By.xpath(".//span[@class='cal-txt-month']"));
			calendarCurrentYear = Integer.parseInt($calendarCurrentYear.getText().replaceAll("[^0-9]", ""));
			calendarCurrentMonth = Integer.parseInt($calendarCurrentMonth.getText().replaceAll("[^0-9]", ""));

			// 캘린더 년 조정(보통은 이전년도로 돌아갈 일은 없으므로 이전년도에 대한 처리는 하지 않는다)
			int clickCnt = 0;
			if (expectedYear > calendarCurrentYear) {
				// 년도의 차이만큼 다음년도 이동 버튼을 클릭한다.
				clickCnt = expectedYear - calendarCurrentYear;

				for (int i = 0; i < clickCnt; i++) {
					logger.info("다음년도 이동 버튼 클릭");
					$button = $calendarDiv.findElement(By.xpath(".//button[text()='다음년도 이동']"));
					click($button);
				}
			}

			// 캘린더 월 조정(보통은 이전달로 돌아갈 일은 없으므로 이전달에 대한 처리는 하지 않는다)
			if (expectedMonth > calendarCurrentMonth) {
				//달의 차이만큼 다음달 이동 버튼을 클릭한다.
				clickCnt = expectedMonth - calendarCurrentMonth;

				for (int i = 0; i < clickCnt; i++) {
					logger.info("다음달 이동 버튼 클릭");
					$button = $calendarDiv.findElement(By.xpath(".//button[text()='다음달 이동']"));
					click($button);
				}
			}

			// 캘린더 일 조정
			WebElement $calendarTable = $calendarDiv.findElement(By.xpath(".//table[@class='component-cal-calendar']"));
			WebElement $calendarTbody = $calendarTable.findElement(By.tagName("tbody"));
			WebElement $calendarDate = $calendarTbody.findElement(By.xpath(".//td[not(@class[contains(., 'component-cal-disable')])]/button[text()='" + expectedDate + "']"));
			click($calendarDate);

			// 실제 입력된 여행 출발날짜 읽어오기
			$calendarDiv = driver.findElement(By.id("calendar-start-date"));
			WebElement $calendarSpan = $calendarDiv.findElement(By.xpath("./span[@class='label-date']"));
			actualDepartureDate = $calendarSpan.getText().replaceAll("[\\.]", "");

			// 비교
			super.printLogAndCompare(title, expectedDepartureDate, actualDepartureDate);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
			throw new SetTravelPeriodException(e, exceptionEnum.getMsg());
		}
	}



	public void setTravelDepartureTime(String expectedDepartureTime) throws SetTravelPeriodException {
		String title = "여행 출발시간";
		String actualDepartureTime = "";

		try {
			// 여행 출발시간 선택을 위해 dropdown 펼치기
			WebElement $departureTimeSpan = driver.findElement(By.id("dropdown-start-hour"));
			click($departureTimeSpan);

			// 여행 출발시간 element 찾기
			String script = "return $('ul[id^=sfddropdown-menu]:visible')[0];";
			WebElement $departureTimeUl = (WebElement) helper.executeJavascript(script);
			WebElement $departureTimeA = $departureTimeUl.findElement(By.xpath(".//a[text()='" + expectedDepartureTime + "']"));
			click($departureTimeA);

			// 실제 선택된 여행 출발시간 값 읽어오기
			$departureTimeSpan = $departureTimeSpan.findElement(By.xpath(".//span[@class='label']"));
			actualDepartureTime = $departureTimeSpan.getText();

			// 비교
			super.printLogAndCompare(title, expectedDepartureTime, actualDepartureTime);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
			throw new SetTravelPeriodException(e, exceptionEnum.getMsg());
		}
	}



	public void setTravelArrivalDate(String expectedArrivalDate) throws SetTravelPeriodException {
		String title = "여행 도착날짜";
		String actualArrivalDate = "";
		WebElement $button = null;

		// 예상 여행도착일 파싱 작업
		int expectedYear = Integer.parseInt(expectedArrivalDate.substring(0, 4));
		int expectedMonth = Integer.parseInt(expectedArrivalDate.substring(4, 6));
		int expectedDate = Integer.parseInt(expectedArrivalDate.substring(6));

		int calendarCurrentYear = 0;
		int calendarCurrentMonth = 0;

		try {
			// 여행 도착날짜 선택을 위해 캘린더 버튼 클릭
			WebElement $calendarDiv = driver.findElement(By.id("calendar-end-date"));
			click($calendarDiv);

			// 현재 캘린더에 default로 세팅된 년, 월 읽어오기
			$calendarDiv = driver.findElement(By.xpath("//div[@class='component-cal-container']"));
			WebElement $calendarCurrentYear = $calendarDiv.findElement(By.xpath(".//span[@class='cal-txt-year']"));
			WebElement $calendarCurrentMonth = $calendarDiv.findElement(By.xpath(".//span[@class='cal-txt-month']"));
			calendarCurrentYear = Integer.parseInt($calendarCurrentYear.getText().replaceAll("[^0-9]", ""));
			calendarCurrentMonth = Integer.parseInt($calendarCurrentMonth.getText().replaceAll("[^0-9]", ""));

			// 캘린더 월 조정(보통은 이전달로 돌아갈 일은 없으므로 이전달에 대한 처리는 하지 않는다)
			int clickCnt = 0;
			if (expectedMonth > calendarCurrentMonth) {
				//달의 차이만큼 다음달 이동 버튼을 클릭한다.
				clickCnt = expectedMonth - calendarCurrentMonth;

				for (int i = 0; i < clickCnt; i++) {
					logger.info("다음달 이동 버튼 클릭");
					$button = $calendarDiv.findElement(By.xpath(".//button[text()='다음달 이동']"));
					click($button);
				}
			}

			// 캘린더 일 조정
			WebElement $calendarTable = $calendarDiv.findElement(By.xpath(".//table[@class='component-cal-calendar']"));
			WebElement $calendarTbody = $calendarTable.findElement(By.tagName("tbody"));
			WebElement $calendarDate = $calendarTbody.findElement(By.xpath(".//td[not(@class[contains(., 'component-cal-disable')])]/button[text()='" + expectedDate + "']"));
			click($calendarDate);

			// 실제 입력된 여행 도착날짜 읽어오기
			$calendarDiv = driver.findElement(By.id("calendar-end-date"));
			WebElement $calendarSpan = $calendarDiv.findElement(By.xpath("./span[@class='label-date']"));
			actualArrivalDate = $calendarSpan.getText().replaceAll("[\\.]", "");

			// 비교
			super.printLogAndCompare(title, expectedArrivalDate, actualArrivalDate);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
			throw new SetTravelPeriodException(e, exceptionEnum.getMsg());
		}
	}



	public void setTravelArrivalTime(String expectedArrivalTime) throws SetTravelPeriodException {
		String title = "여행 도착시간";
		String actualArrivalTime = "";

		try {
			// 여행 도착시간 선택을 위해 dropdown 펼치기
			WebElement $arrivalTimeSpan = driver.findElement(By.id("dropdown-end-hour"));
			click($arrivalTimeSpan);

			// 여행 도착시간 element 찾기
			String script = "return $('ul[id^=sfddropdown-menu]:visible')[0];";
			WebElement $arrivalTimeUl = (WebElement) helper.executeJavascript(script);
			WebElement $arrivalTimeA = $arrivalTimeUl.findElement(By.xpath(".//a[text()='" + expectedArrivalTime + "']"));
			click($arrivalTimeA);

			// 실제 선택된 여행 도착시간 값 읽어오기
			$arrivalTimeSpan = $arrivalTimeSpan.findElement(By.xpath(".//span[@class='label']"));
			actualArrivalTime = $arrivalTimeSpan.getText();

			// 비교
			super.printLogAndCompare(title, expectedArrivalTime, actualArrivalTime);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
			throw new SetTravelPeriodException(e, exceptionEnum.getMsg());
		}
	}



	public void setTravelGoal(String expectedTravelGoal) throws CommonCrawlerException {
		String title = "여행목적";
		String actualTravelGoal = "";

		try {
			// 여행목적 관련 element 찾기
			WebElement $travelGoalUl = driver.findElement(By.id("travel-purposes"));
			WebElement $travelGoalLabel = $travelGoalUl.findElement(By.xpath(".//label[normalize-space()='" + expectedTravelGoal + "']"));

			// 여행목적 클릭
			click($travelGoalLabel);

			// 실제 선택된 여행목적 값 읽어오기
			$travelGoalLabel = $travelGoalUl.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
			actualTravelGoal = $travelGoalLabel.getText();

			// 비교
			super.printLogAndCompare(title, expectedTravelGoal, actualTravelGoal);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_GOAL;
			throw new CommonCrawlerException(e, exceptionEnum.getMsg());
		}
	}
}