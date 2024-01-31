package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class SFI_AMD_D003 extends CrawlingSFIDirect {

	public static void main(String[] args) {
		executeCommand(new SFI_AMD_D003(), args);
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

		logger.info("직업정보 설정");
		setJob("중고등학교교사");

		logger.info("보험료 계산하기 버튼 클릭");
		$button = driver.findElement(By.id("btn-next-step"));
		click($button);

		logger.info("모달창이 뜨는지를 확인합니다");
		modalCheck();

		logger.info("납입방법 설정");
		setNapCycle(info.getNapCycleName());

		/**
		 * 해당 보험은 20세부터 고객이 플랜을 자유롭게 설정할 수 있다.
		 * 19세는 플랜 선택없이 고정된 플랜으로 가입하게 된다.
		 */
		boolean isFixedPlan = false;
		String script = "return $('div.ne-plan-front:visible')[0]";
		helper.executeJavascript(script);
		WebElement $displayedDiv = (WebElement) helper.executeJavascript(script);
		WebElement $planAreaThead = $displayedDiv.findElement(By.id("coverage-header"));
		isFixedPlan = helper.existElement($planAreaThead, By.xpath(".//th[text()='가입금액']"));

		if (isFixedPlan) {
			logger.info("고정된 플랜으로 가입됩니다.");
		} else {
			logger.info("플랜을 자유롭게 선택할 수 있습니다.");
			logger.info("플랜 설정");
			setPlan(info.getTextType());
		}

		logger.info("특약 설정");
		setTreaties(isFixedPlan, info.getTreatyList());

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);

		return true;
	}



	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {
		String title = "보험료 크롤링";
		String script = "";

		CrawlingProduct info = (CrawlingProduct) obj[0];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
		ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

		try {
			// 보험료 크롤링 전에는 대기시간을 넉넉히 준다
			WaitUtil.waitFor(5);

			/**
			 * 보험료 element 찾기
			 *
			 * 보험료 영역($premiumAreaDiv 변수)을 찾을 때 id값이 고유하지 않음(동일한 id가 가입유형별로 존재함)
			 * 따라서 현재 보여지는(활성화된) 가입유형(태아가입형/일반가입형/소아가입형 등) div 영역을 알아낼 필요가 있음.
			 * div.ne-plan-front:visible 문법으로 찾는데, :visible때문인지 By.cssSelector()로는 동작하지 않음.
			 * 따라서 executeScript()를 통해 실행시켜야함.
			 *
			 */
			script = "return $('div.ne-plan-front:visible')[0]";
			helper.executeJavascript(script);
			WebElement $displayedDiv = (WebElement) helper.executeJavascript(script);

			WebElement $premiumAreaDiv = $displayedDiv.findElement(By.id("coverage-premium"));
			WebElement $activedPlanDiv = $premiumAreaDiv.findElement(By.xpath(".//div[@class[contains(., 'total-current')]]"));
			WebElement $premiumStrong = $activedPlanDiv.findElement(By.xpath("./strong[@class='blind']"));
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



	public void setPlan(String expectedPlan) throws CommonCrawlerException {
		String title = "플랜";
		String actualPlan = "";
		String script = "";

		try {
			/**
			 * 플랜 element 찾기
			 *
			 * 플랜 영역($planAreaThead 변수)을 찾을 때 id값이 고유하지 않음(동일한 id가 가입유형별로 존재함)
			 * 따라서 현재 보여지는(활성화된) 가입유형(태아가입형/일반가입형/소아가입형 등) div 영역을 알아낼 필요가 있음.
			 * div.ne-plan-front:visible 문법으로 찾는데, :visible때문인지 By.cssSelector()로는 동작하지 않음.
			 * 따라서 executeScript()를 통해 실행시켜야함.
			 *
			 */
			script = "return $('div.ne-plan-front:visible')[0]";
			helper.executeJavascript(script);
			WebElement $displayedDiv = (WebElement) helper.executeJavascript(script);
			WebElement $planAreaThead = $displayedDiv.findElement(By.id("coverage-header"));

			/**
			 * 플랜명 선택과, 플랜명 비교를 수월하게 하기 위해 소괄호 안의 작은 글씨 element 삭제처리
			 *
			 * ex)
			 * 단독 실손보험(실손만 가입) -> 단독 실손보험
			 * 실손+간편보험(함께 가입) -> 실손+간편보험
			 */
			script = "$(arguments[0]).find('span.txt-sm').remove();";
			helper.executeJavascript(script, $planAreaThead);

			WebElement $planSpan = $planAreaThead.findElement(By.xpath(".//span[@class='inner'][text()='" + expectedPlan + "']"));
			WebElement $planLabel = $planSpan.findElement(By.xpath("./parent::label"));
			click($planLabel);

			// 실제 선택된 플랜 값 읽어오기
			$planLabel = $planAreaThead.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
			actualPlan = $planLabel.getText().trim();

			// 비교
			super.printLogAndCompare(title, expectedPlan, actualPlan);


		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
			throw new CommonCrawlerException(e, exceptionEnum.getMsg());
		}
	}



	@Override
	public void setNapCycle(Object... obj) throws SetNapCycleException {
		String title = "납입방법";
		String expectedNapCycle = (String) obj[0];
		String actualNapCycle = "";
		String script = "";

		try {
			/**
			 * 납입방법 element 찾기
			 *
			 * 납입방법 영역($napCycleAreaDd 변수)을 찾을 때 id값이 고유하지 않음(동일한 id가 가입유형별로 존재함)
			 * 따라서 현재 보여지는(활성화된) 가입유형(태아가입형/일반가입형/소아가입형 등) div 영역을 알아낼 필요가 있음.
			 * div.ne-plan-front:visible 문법으로 찾는데, :visible때문인지 By.cssSelector()로는 동작하지 않음.
			 * 따라서 executeScript()를 통해 실행시켜야함.
			 *
			 */
			script = "return $('div.ne-plan-front:visible')[0]";
			helper.executeJavascript(script);
			WebElement $displayedDiv = (WebElement) helper.executeJavascript(script);
			WebElement $napCycleAreaDd = $displayedDiv.findElement(By.id("payment-method"));
			WebElement $napCycleLabel = $napCycleAreaDd.findElement(By.xpath("./label[normalize-space()='" + expectedNapCycle + "']"));
			click($napCycleLabel);

			// 실제 선택된 납입방법 값 읽어오기(원수사에서는 실제 선택된 납입방법 element 클래스 속성에 active를 준다)
			$napCycleLabel = $napCycleAreaDd.findElement(By.xpath("./label[@class[contains(., 'active')]]"));
			actualNapCycle = $napCycleLabel.getText().trim();

			// 비교
			super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
			throw new SetNapCycleException(e, exceptionEnum.getMsg());
		}
	}



	//TODO SFI_DMT_D001 특약 설정 로직처럼 수정 필요
	public void setTreaties(boolean isFixedPlan, List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
		String script = "";
		ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;

		try {
			/**
			 *
			 * 특약 리스트 영역을 찾을 때 id값이 고유하지 않음(동일한 id가 가입유형별로 존재함)
			 * 따라서 현재 보여지는(활성화된) 가입유형(태아가입형/일반가입형/소아가입형 등) div 영역을 알아낼 필요가 있음.
			 * div.ne-plan-front:visible 문법으로 찾는데, :visible때문인지 By.cssSelector()로는 동작하지 않음.
			 * 따라서 executeScript()를 통해 실행시켜야함.
			 *
			 */
			script = "return $('div.ne-plan-front:visible')[0]";
			helper.executeJavascript(script);
			WebElement $displayedDiv = (WebElement) helper.executeJavascript(script);

			// 특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(span 삭제)
			script = "$(arguments[0]).find('span.ne-hidden').remove();";
			helper.executeJavascript(script, $displayedDiv);

			// 특약명 선택과, 특약명 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(더보기 버튼 삭제)
			script = "$(arguments[0]).find('button.ne-bt-more').remove();";
			helper.executeJavascript(script, $displayedDiv);

			// 가입설계 정보대로 특약 설정
			for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
				String welgramTreatyName = welgramTreaty.treatyName;
				String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.assureMoney);
				WebElement $treatyNameTh = null;
				WebElement $treatyTr = null;
				WebElement $treatyAssureMoneyTd = null;

				logger.info("특약명 : {} 관련 처리중...", welgramTreatyName);

				// 특약 관련 element 찾기
				$treatyNameTh = $displayedDiv.findElement(By.xpath(".//th[text()='" + welgramTreatyName + "']"));
				$treatyTr = $treatyNameTh.findElement(By.xpath("./parent::tr"));

				if (isFixedPlan) {
					// 플랜 설정이 고정인 경우
					$treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[1]"));
				} else {
					// 자유롭게 플랜 설정이 가능한 경우
					$treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[@class[contains(., 'select')]]"));
				}

				// 원수사에 명시된 특약 가입금액값 읽어오기
				String targetTreatyAssureMoney = $treatyAssureMoneyTd.getText();
				targetTreatyAssureMoney = targetTreatyAssureMoney.replace("한도", "");
				targetTreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));

				// 원수사 특약 가입금액과 가입설계 특약 가입금액이 다를 경우
				if (!welgramTreatyAssureMoney.equals(targetTreatyAssureMoney)) {

					// 해당 특약을 클릭해서 가입금액을 조정한다.
					click($treatyTr);

					// 특약 가입금액 관련 element 찾기
					WebElement $treatyAssureMoneyAreaDiv = driver.findElement(By.id("coverage-value-picker"));
					WebElement $treatyAssureMoneyUl = $treatyAssureMoneyAreaDiv.findElement(By.xpath(".//ul[@class='slider-list']"));
					List<WebElement> $treatyAssureMoneyLiList = $treatyAssureMoneyUl.findElements(By.xpath("./li[@class='slider-item']"));

					boolean isExistAssureMoney = false;
					for (WebElement $li : $treatyAssureMoneyLiList) {
						// 특약 가입금액 선택과 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(span 삭제)
						script = "$(arguments[0]).find('span.blind').remove();";
						helper.executeJavascript(script, $li);

						// 특약 가입금액 선택과 비교를 수월하게 하기 위해 불필요한 element 삭제 처리(span 삭제)
						script = "$(arguments[0]).find('span.sr-only').remove();";
						helper.executeJavascript(script, $li);

						// 일치하는 특약 가입금액이 있는지 확인하는 작업
						WebElement $treatyAssureMoneyP = $li.findElement(By.xpath(".//p[@class='label']"));
						WebElement $treatyAssureMoneyA = null;
						WebElement $treatyAssureMoneyConfirmBtn = null;

						targetTreatyAssureMoney = $treatyAssureMoneyP.getText();
						if ("미가입".equals(targetTreatyAssureMoney)) {
							targetTreatyAssureMoney = "0";
						} else {
							targetTreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));
						}

						if (welgramTreatyAssureMoney.equals(targetTreatyAssureMoney)) {
							isExistAssureMoney = true;

							// 가입금액 조정
							$treatyAssureMoneyA = $treatyAssureMoneyP.findElement(By.xpath("./parent::a"));
							click($treatyAssureMoneyA);

							// 가입금액 조정 팝업 확인 버튼 클릭
							$treatyAssureMoneyConfirmBtn = driver.findElement(By.id("btn-confirm"));
							click($treatyAssureMoneyConfirmBtn);

							break;
						}
					}

					// 일치하는 특약 가입금액이 없는 경우 예외 발생
					if (!isExistAssureMoney) {
						logger.info("원수사에 해당 가입금액({}) 없음", welgramTreatyAssureMoney);
						throw new SetTreatyException(exceptionEnum.getMsg());
					}
				}
			}

			// 실제로 세팅된 원수사 특약 정보 읽어오기
			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

			// 원수사에서 가입처리된 특약 정보만 불러오기(가입금액이 '-' 인(=미가입인) 특약은 제외)
			List<WebElement> $tdList = null;
			if (isFixedPlan) {
				$tdList = $displayedDiv.findElements(By.xpath(".//tr[@class='coverage-item']/td[not(text()='-')]"));
			} else {
				$tdList = $displayedDiv.findElements(By.xpath(".//tr[@class='coverage-item']/td[@class[contains(., 'select')]][not(text()='-')]"));
			}

			for (WebElement $td : $tdList) {
				WebElement $treatyTr = $td.findElement(By.xpath("./parent::tr"));
				WebElement $treatyNameTh = $treatyTr.findElement(By.xpath("./th[1]"));
				String treatyName = "";
				String treatyAssureMoney = "";

				// 원수사 사이트의 특약명과 특약 가입금액 값 읽어오기
				treatyName = $treatyNameTh.getText();
				treatyAssureMoney = $td.getText();
				treatyAssureMoney = treatyAssureMoney.replace("한도", "");
				treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

				// 원수사 특약 정보 적재
				CrawlingTreaty targetTreaty = new CrawlingTreaty();
				targetTreaty.setTreatyName(treatyName);
				targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

				targetTreatyList.add(targetTreaty);
			}

			// 원수사 특약 vs 가입설계 특약 비교
			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

			if (result) {
				logger.info("특약 정보 일치");
			} else {
				logger.info("특약 정보 불일치");
				throw new Exception();
			}

		} catch (Exception e) {
			throw new SetTreatyException(e, exceptionEnum.getMsg());
		}
	}
}