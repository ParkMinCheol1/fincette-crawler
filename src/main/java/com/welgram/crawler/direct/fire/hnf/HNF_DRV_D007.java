package com.welgram.crawler.direct.fire.hnf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class HNF_DRV_D007 extends CrawlingHNFMobile {

	// 하나 가득담은 운전자보험(다이렉트)(2306)
	public static void main(String[] args) {
		executeCommand(new HNF_DRV_D007(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		WebElement $button = null;

		// STEP 1 : 유저 세팅
		setUserInfo(info);

		// STEP 2 : 가입조건 세팅
		logger.info("조건변경 버튼 클릭");
		$button = driver.findElement(By.id("btnCondionModify"));
		click($button);

		logger.info("보험기간/납입기간 설정");
		setTerm(info.getInsTerm(), info.getNapTerm());

		logger.info("선택완료 버튼 클릭");
		$button = driver.findElement(By.xpath("//*[@id=\"toastPop\"]/div[1]/div[3]/div/button"));
		click($button);

		logger.info("플랜명 선택");
		setPlan(info.getTextType());

		logger.info("보험료 크롤링");
		crawlPremium(info);

		logger.info("스크린샷 찍기");
		helper.executeJavascript("window.scrollTo(0,0);");
		takeScreenShot(info);

		logger.info("해약환급금 크롤링");
		crawlReturnMoneyList(info);

		return true;

	}



	public void setTerm(String expectInsTerm, String expectedNapTerm) throws SetInsTermException {

		String title = "보험기간";
		String actualInsTerm = "";

		try {
			if (!expectInsTerm.equals(expectedNapTerm)) {
				throw new SetNapTermException("보험기간과 납입기간이 일치하지 않습니다.");
			}

			WebElement $insTermDiv = driver.findElement(By.id("divGiganInsWrapper"));
			WebElement $insTermLabel = driver.findElement(By.xpath(".//label[normalize-space()='" + expectInsTerm + "']"));

			// 보험기간 설정
			click($insTermLabel);

			// 실제 클릭된 보험기간 값 읽어오기
			String script = "return $('input[name=rdoInsPrd]:checked').attr('id');";
			String id = String.valueOf(helper.executeJavascript(script));
			$insTermLabel = driver.findElement(By.xpath("//label[@for='" + id + "']"));
			actualInsTerm = $insTermLabel.getText().trim();

			// 비교
			super.printLogAndCompare(title, expectInsTerm, actualInsTerm);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
			throw new SetInsTermException(e.getMessage());
		}

	}



	@Override
	public void setPlan(String expectedPlan) throws CommonCrawlerException {

		String title = "플랜명";
		String actualPlan = "";

		try {
			WebElement $planDiv = driver.findElement(By.id("ulPlncodWrapper"));
			WebElement $planInput = $planDiv.findElement(By.xpath(".//input[@data-lgtmplannm='" + expectedPlan + "']"));
			WebElement $planLabel = $planDiv.findElement(By.xpath(".//label[@for='" + $planInput.getAttribute("id") + "']"));

			// 플랜 선택
			click($planLabel);

			// 실제 클릭된 플랜 읽어오기
			String script = "return $('input[name=rdoPlnCod]:checked').data('lgtmplannm');";
			actualPlan = String.valueOf(helper.executeJavascript(script));

			// 비교
			super.printLogAndCompare(title, expectedPlan, actualPlan);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
			throw new SetInsTermException(exceptionEnum.getMsg());
		}

	}



	/**
	 * 특약 상세 모달창에서 가입금액을 세팅한다.
	 *
	 * @param treatyAssureMoney 세팅할 가입금액(미가입인 경우에는 null, 가입인 경우에는 세팅할 가입금액이 넘어온다)
	 * @throws Exception
	 */
	private void setTreatyAssureMoneyFromModal(String treatyAssureMoney) throws Exception {

		//특약 가입 여부
		boolean toJoin = StringUtils.isNotEmpty(treatyAssureMoney);

		WebElement $modal = driver.findElement(By.id("popLongtermSelectPlan"));
		WebElement $treatyAssureMoneyDiv = $modal.findElement(By.id("divCoverButtionList"));
		WebElement $treatyAssureMoneyLabel = null;

		// 가입금액 선택을 쉽게 하기 위해 불필요한 element 삭제
		String script = "$(arguments[0]).find('span.icon_check').remove();" +
				"$(arguments[0]).find('span.icon_check').remove();" ;
		helper.executeJavascript(script, $treatyAssureMoneyDiv);

		// 클릭해야하는 가입금액 label 찾기
		if (toJoin) {
			WebElement $treatyAssureMoneyInput = $treatyAssureMoneyDiv.findElement(By.xpath(".//input[@value='" + treatyAssureMoney + "']"));
			String id = $treatyAssureMoneyInput.getAttribute("id");
			$treatyAssureMoneyLabel = $treatyAssureMoneyDiv.findElement(By.xpath(".//label[@for='" + id + "']"));
		} else {
			WebElement $treatyAssureMoneySapn = $treatyAssureMoneyDiv.findElement(By.xpath(".//span[@class='back'][normalize-space()='미가입']"));
			$treatyAssureMoneyLabel = $treatyAssureMoneySapn.findElement(By.xpath("./parent::label"));
		}

		// 가입금액 클릭
		click($treatyAssureMoneyLabel);

		// 모달창 닫기 위해 확인 버튼 클릭
		WebElement $button = $modal.findElement(By.xpath(".//button[normalize-space()='확인']"));
		click($button);

		// alret 발생 가능성 있음
		By alertPosition = By.xpath("//div[@data-msgtype='alert']");
		boolean isExist = helper.existElement(alertPosition);
		if (isExist) {
			WebElement $alert = driver.findElement(alertPosition);
			$button = $alert.findElement(By.xpath(".//button[normalize-spce()='확인']"));
			click($button);
		}

	}



	/**
	 * 특약 li로부터 세팅되어진 특약정보를 읽어온다.
	 * 특약정보에는 특약명, 특약가입금액이 있다.
	 * <p>
	 * 가입(보장금액이 미가입이 아닌경우) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고,
	 * 미가입(보장금액이 미가입인 경우) 특약인 경우 null을 리턴한다.
	 *
	 * @param $li
	 * @return
	 * @throws Exception
	 */
	private CrawlingTreaty getTreatyInfoFromLi(WebElement $li) {

		CrawlingTreaty treaty = null;

		// 특약명 영역
		WebElement $treatyNameSpan = $li.findElement(By.xpath("./span"));

		// 특약 가입금액 영역
		WebElement $treatyAssureMoneyDiv = $li.findElement(By.xpath(".//div[@class[contains(.,'on')]]"));
		WebElement $treatyAssureMoneySpan = $treatyAssureMoneyDiv.findElement(By.tagName("span"));
		String targetTreatyAssureMoney = $treatyNameSpan.getText();

		// 현재 원수사에서 특약 가입여부 상태
		boolean isJoin = !"미가입".equals(targetTreatyAssureMoney) && !"-".equals(targetTreatyAssureMoney);

		// 특약이 가입인 경우에만 객체 생성
		if (isJoin) {
			String treatyName = $treatyNameSpan.getText().trim();
			String treatyAssureMoney = $treatyNameSpan.getAttribute("data-scd");

			treaty = new CrawlingTreaty();
			treaty.setTreatyName(treatyName);
			treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
		}

		return treaty;

	}



	/**
	 * 특약 li에 대해 가입금액을 세팅한다.
	 *
	 * @param $li 입력 대상이 되는 li element
	 * @param treatyAssureMoney 세팅할 가입금액(가입인 경우에는 세팅할 가입금액이 넘어오고, 미가입인 경우에는 null이 넘어온다)
	 * @throws SetTreatyException
	 */
	private void setTreatyInfoFromLi(WebElement $li, String treatyAssureMoney) throws Exception {

		// 특약명 영역
		WebElement $treatyNameSpan = $li.findElement(By.xpath("./span"));

		// 특약 가입금액 영역
		WebElement $treatyAssureMoneyDiv = $li.findElement(By.xpath(".//div[@class[contains(., 'on')]]"));
		WebElement $treatyAssureMoneySpan = $treatyAssureMoneyDiv.findElement(By.tagName("span"));
		String targetTreatyAssureMoney = $treatyAssureMoneySpan.getText().trim();

		// 특약 가입여부
		boolean toJoin = StringUtils.isNotEmpty(treatyAssureMoney);

		// 현재 원수사에서 특약 가입여부 상태
		boolean isJoin = !"미가입".equals(targetTreatyAssureMoney) && !"-".equals(targetTreatyAssureMoney);

		if (toJoin != isJoin) {
			logger.info("특약명 : {} 처리 진행중...", $treatyNameSpan.getText());

			helper.moveToElementByJavascriptExecutor($treatyAssureMoneyDiv);
			helper.executeJavascript("window.scrollBy(0, -200)");
			click($treatyAssureMoneyDiv);

			setTreatyAssureMoneyFromModal(treatyAssureMoney);
		}

	}



	public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

		try {
			WebElement $treatyUl = driver.findElement(By.id("tbdGuaranteeList"));
			List<WebElement> $treatyLiList = $treatyUl.findElements(By.xpath("./li//div[@class[contains(., 'on')]]/span/ancestor::li[1]"));

			List<String> targetTreatyNameList = new ArrayList<>();
			List<String> welgramTreatyNameList = new ArrayList<>();

			// 원수사 특약명 수집
			for(WebElement $treatyLi : $treatyLiList) {
				WebElement $treatyNameSpan = $treatyLi.findElement(By.xpath("./span"));

				// 원수사 특약명을 가져오기 위해 특약명이 보이도록 스크롤 처리를 해야함.
				helper.moveToElementByJavascriptExecutor($treatyNameSpan);
				String targetTreatyName = $treatyNameSpan.getText().trim();

				targetTreatyNameList.add(targetTreatyName);
			}

			// 가입설계 특약명 수집
			welgramTreatyNameList = welgramTreatyList.stream().map(CrawlingTreaty::getTreatyName).collect(Collectors.toList());

			// 원수사와 가입설계 특약명을 비교해서 일치, 불일치 특약 추려내기
			List<String> matchedTreatyNameList = getMatchedTreatyNameList(targetTreatyNameList, welgramTreatyNameList);         // 원수사에서 가입처리 해야할 특약명 리스트
			List<String> misMatchedTreatyNameList = getMisMatchedTreatyNameList(targetTreatyNameList, welgramTreatyNameList);   // 원수사에서 미가입처리 해야할 특약명 리스트

			logger.info("해당 특약들을 가입 처리합니다.");
			for (String treatyName : matchedTreatyNameList) {
				logger.info("특약 : {} 가입 처리중...", treatyName);

				WebElement $treatyLi = $treatyUl.findElement(By.xpath("./li[@data-trtyuserdefnname='" + treatyName + "']"));

				CrawlingTreaty welgramTreaty = welgramTreatyList.stream()
						.filter(t -> t.getTreatyName().equals(treatyName))
						.findFirst()
						.orElseThrow(SetTreatyException::new);

				setTreatyInfoFromLi($treatyLi, String.valueOf(welgramTreaty.getAssureMoney()));
			}

			logger.info("해당 특약들을 미가입 처리합니다.");
			for (String treatyName : misMatchedTreatyNameList) {
				logger.info("특약 : {} 미가입 처리중...", treatyName);

				WebElement $treatyLi = $treatyUl.findElement(By.xpath("./li[@data-trtyuserdefnname='" + treatyName + "']"));
				setTreatyInfoFromLi($treatyLi, null);
			}

			logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
			$treatyLiList = $treatyUl.findElements(By.xpath("./li//div[@class[contains(., 'on')]]/span/ancestor::li[1]"));
			List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
			for (WebElement $treatyLi : $treatyLiList) {

				//li로부터 특약정보 읽어오기
				CrawlingTreaty targetTreaty = getTreatyInfoFromLi($treatyLi);

				if (targetTreaty != null) {
					targetTreatyList.add(targetTreaty);
				}
			}

			logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
			boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
			if (result) {
				logger.info("특약 정보 모두 일치");
			} else {
				logger.info("특약 정보 불일치");
				throw new Exception();
			}

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
			throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
		}

	}



	@Override
	public void crawlPremium(Object... obj) throws PremiumCrawlerException {

		CrawlingProduct info = (CrawlingProduct) obj[0];
		CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
		ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

		try {
			//보험료 크롤링 전에는 대기시간을 넉넉히 준다
			WaitUtil.waitFor(5);

			WebElement $premiumSpan = driver.findElement(By.cssSelector("#calcResult > div.top_con > dl:nth-child(1) > dd > p:nth-child(2) > b > span:nth-child(1)"));
			String premium = $premiumSpan.getText();
			premium = String.valueOf(MoneyUtil.toDigitMoney(premium));
			mainTreaty.monthlyPremium = premium;

			if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
				logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
				throw new PremiumCrawlerException(exceptionEnum.getMsg());
			} else {
				logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
			}
		} catch (Exception e) {
			throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
		}

	}

}
