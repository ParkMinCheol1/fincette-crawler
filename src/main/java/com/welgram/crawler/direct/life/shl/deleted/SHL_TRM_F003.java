package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.GenderMismatchException;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.common.except.NapCycleMismatchException;
import com.welgram.common.except.NapTermMismatchException;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


// 2022.05.27 		| 최우진 			| 대면_정기
// SHL_TRM_F003 	| 신한CEO정기보험(무배당, 보증비용부과형) 평준형 간편심사형
public class SHL_TRM_F003 extends CrawlingSHL {

	public static void main(String[] args) {
		executeCommand(new SHL_TRM_F003(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		logger.info("대면보험의 경우 공시실의 정보를 크롤링합니다. %TRM_F% ");
		logger.info("START | SHL_TRM_F003 :: 신한CEO정기보험(무배당, 보증비용부과형) 평준형 간편심사형");

		WaitUtil.waitFor(2);

		String tType = info.textType;
		String[] arrTType = tType.split("#");
		for(int i = 0; i < arrTType.length; i++) {
			arrTType[i] = arrTType[i].trim();
			logger.info(arrTType[i]);
			// 0 : 신한CEO정기보험(무배당, 보증비용부과형)
			// 1 : 평준형
			// 2 : 간편심사형
		}
		logger.info("검색창에서 상품명 : [ {} ]을 조회합니다", "신한CEO정기보험(무배당, 보증비용부과형)");
//		helper.doSendKeys(By.id("meta04"), "신한CEO정기보험(무배당, 보증비용부과형)");
		driver.findElement(By.id("meta04")).sendKeys("신한CEO정기보험(무배당, 보증비용부과형)");

//		helper.doClick(By.id("btnSearch"));
		driver.findElement(By.id("btnSearch")).click();
		WaitUtil.waitFor(1);

		logger.info("보험료계산 버튼 클릭");
//		helper.doClick(By.id("calc_0"));
		driver.findElement(By.id("calc_0")).click();
		WaitUtil.waitFor(3);

		logger.info("고객정보(피보험자) 내용 입력");
		logger.info("생년월일");
//		helper.doSendKeys(By.xpath("//input[@type='text'][@title='생년월일']"), info.getFullBirth());
		driver.findElement(By.xpath("//input[@type='text'][@title='생년월일']")).sendKeys(info.getFullBirth());

		logger.info("성별");
		String genderOpt = (info.getGender() == MALE) ? "filt1_1" : "filt1_2";
//		helper.doClick(By.xpath("//input[@id='" + genderOpt + "']//parent::li"));
		driver.findElement(By.xpath("//input[@id='" + genderOpt + "']//parent::li")).click();

		logger.info("운전");
		Select select = new Select(driver.findElement(By.id("vhclKdCd")));
		select.selectByVisibleText("승용차(자가용)");

		logger.info("직업 :: 사무직 - 경영지원 사무직 관리자");
		String jobOpt = "경영지원 사무직 관리자";
		helper.click(By.xpath("//span[text()='검색']//parent::button[@class='btn_t m btnJobPop']"));
		helper.sendKeys3_check(By.id("jobNmPop"), jobOpt);
		helper.click(By.id("btnJobSearch"));
		helper.click(By.xpath("//span[@class='infoCell'][text()='"+jobOpt+"']"));

		logger.info("확인 버튼 클릭");
		helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnCstCfn']"));
		WaitUtil.waitFor(2);

		logger.info("주계약계산 내용 입력");
		logger.info("보험형태 선택(1/6)");
		try {
			Select selctInsForm = new Select(driver.findElement(By.xpath("//select[@title='보험형태']")));
			selctInsForm.selectByVisibleText(arrTType[1]);
			WaitUtil.waitFor(2);
		} catch (Exception e) {
			throw new CommonCrawlerException("보험형태를 설정할 수 없습니다");
		}

		logger.info("보험종류 선택(2/6)");
		try{
			Select selctInsKind = new Select(driver.findElement(By.xpath("//select[@title='보험종류']")));
			selctInsKind.selectByVisibleText(arrTType[2]);                              // 간편심사형
			WaitUtil.waitFor(2);
		} catch(Exception e) {
			throw new CommonCrawlerException("(SELECTBOX) [ "+arrTType[2]+" ]를 선택할수 없습니다");
		}

		logger.info("납입주기 선택(3/6)");
		try{
			Select selctNapTerm = new Select(driver.findElement(By.xpath("//select[@title='납입주기']")));
			selctNapTerm.selectByVisibleText("월납");
			WaitUtil.waitFor(2);
		} catch(Exception e) {
			throw new CommonCrawlerException("(SELECTBOX) [ '월납' ]을 선택할수 없습니다");
		}

		logger.info("보험기간 선택(4/6)");
		try{
			Select selectInsPeriod = new Select(driver.findElement(By.xpath("//select[@title='보험기간']")));
			selectInsPeriod.selectByVisibleText(info.getInsTerm()+"만기");
			WaitUtil.waitFor(2);
		} catch(Exception e) {
			throw new CommonCrawlerException("(SELECTBOX) [ INSTERM : "+info.getInsTerm()+" ]를 선택할수 없습니다");
		}

		logger.info("납입기간 선택(5/6)");
		try{
			Select selectPayPeriod = new Select(driver.findElement(By.xpath("//select[@title='납입기간']")));
			selectPayPeriod.selectByVisibleText(info.getNapTerm()+"납");
			WaitUtil.waitFor(2);
		} catch(Exception e) {
			throw new CommonCrawlerException("(SELECTBOX) [ NAPTERM : "+info.getNapTerm()+"]를 선택할수 없습니다");
		}

		logger.info("가입금액 선택(6/6)");
		WaitUtil.waitFor(1);
		WebElement inputTextJoinFee = driver.findElement(By.xpath("//input[@title='가입금액']"));
		String joinFeeFormCutLast4 = info.assureMoney.substring(0, info.assureMoney.length() -4);
		logger.info("가입금액 확인 :: {}", joinFeeFormCutLast4);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.querySelector('#mnprDivision > ul:nth-child(2) > li:nth-child(3) > div.val > div > input').value = '';");

		WaitUtil.waitFor(1);
		inputTextJoinFee.click();
		inputTextJoinFee.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
		inputTextJoinFee.sendKeys(joinFeeFormCutLast4);
		WaitUtil.waitFor(1);

		logger.info("확인 버튼 클릭");
		helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnMnpr']"));
		WaitUtil.waitFor(1);

		logger.info("보험료계산 버튼 클릭");
		helper.click(By.xpath("//span[text()='보험료계산']//parent::button[@class='btn_p btnInpFeCal']"));
		WaitUtil.waitFor(3);

		// GET ======================================================================================================

		logger.info("보험료 확인");
		try {
			String monthlyPremium = driver.findElement(By.xpath("//em[@class='rlpaAm']")).getText().replaceAll("[^0-9]", "");
			logger.info("월 보험료 : " + monthlyPremium);
			info.treatyList.get(0).monthlyPremium = monthlyPremium;
		} catch(Exception e) {
			throw new CommonCrawlerException("보험료 확인중 에러가 발생하였습니다");
		}

		logger.info("스크린샷 찍기");
		try {
			takeScreenShot(info);
			logger.info("찰칵");
		} catch(Exception e) {
			logger.error("스크린 샷을 찍는데 실패하였습니다.");
		}

		logger.info("해약환급금예시 확인");
		helper.click(By.xpath("//span[@class='scriptCell'][text()='해약환급금 예시']//parent::a"));
		WaitUtil.waitFor(2);
		// (rBtn) 최저보증이율 / (rBtn) 평균공시이율 / (rBtn) 공시이율
		// 경과기간		- 나이 		- 납입모험료 누계 		- 해약환급금 		- 환급률
		// 3개월 		- 30세		- 279,000				- 0					- 0.0
		// 6개월 		- 30세 		- 558,000				- 0 				- 0.0
		// 9개월 		- 30세 		- 837,000				- 0 				- 0.0
		// 1년	 		- 31세 		- 1,116,000				- 0 				- 0.0
		// 2년	 		- 32세 		- 2,232,000				- 196,029			- 8.7
		try {
			helper.click(By.cssSelector("#btnSubCocaSlct1 > label"));			// 최저보증이율
			String[] arrReturnInfo = {"최저보증이율 가정시", "연  2.25%(평균공시이율)가정시", "연  2.34%(공시이율)가정시"};
			List<PlanReturnMoney> pRMList = new ArrayList<>();
			List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
			for(WebElement trMin : trReturnMinInfoList) {
				String term = trMin.findElement(By.xpath("./td[1]")).getText();
				String age = trMin.findElement(By.xpath("./td[2]")).getText();
				String premiumSum = trMin.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
				String returnMoney = trMin.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
				String returnRate = trMin.findElement(By.xpath("./td[5]")).getText();

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();

				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoneyMin(returnMoney);
				planReturnMoney.setReturnRateMin(returnRate);

				pRMList.add(planReturnMoney);
			}

			helper.click(By.cssSelector("#btnSubCocaSlct2 > label"));
			List<WebElement> trReturnAvgInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
			for(int idx = 0; idx < trReturnAvgInfoList.size(); idx++) {
				WebElement avgEl = trReturnAvgInfoList.get(idx);
				String returnMoneyAvg = avgEl.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
				String returnRateAvg = avgEl.findElement(By.xpath("./td[5]")).getText();

				pRMList.get(idx).setReturnMoneyAvg(returnMoneyAvg);
				pRMList.get(idx).setReturnRateAvg(returnRateAvg);
			}

			helper.click(By.cssSelector("#btnSubCocaSlct3 > label"));
			List<WebElement> trReturnInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
			for(int idx = 0; idx < trReturnInfoList.size(); idx++) {
				WebElement normEl = trReturnInfoList.get(idx);
				String returnMoney = normEl.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
				String returnRate = normEl.findElement(By.xpath("./td[5]")).getText();

				pRMList.get(idx).setReturnMoney(returnMoney);
				pRMList.get(idx).setReturnRate(returnRate);
			}

			logger.info("SIZE :: " + pRMList.size());
			pRMList.forEach(idx -> {
				logger.info("===================================");
				logger.info("TERM   : " + idx.getTerm());
				logger.info("SUM    : " + idx.getPremiumSum());
				logger.info("rmMMin : " + idx.getReturnMoneyMin());
				logger.info("rmRMin : " + idx.getReturnRateMin());
				logger.info("rmMAvg : " + idx.getReturnMoneyAvg());
				logger.info("rmRAvg : " + idx.getReturnRateAvg());
				logger.info("rmM    : " + idx.getReturnMoney());
				logger.info("rmR    : " + idx.getReturnRate());
			});

			logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
			info.setPlanReturnMoneyList(pRMList);

			// 해당 상품의 해약환급금 표의 가장 마지막 값은 보험기간 만기 하루 전날의 해약환급금을 명시하고 있음.
			// 따라서 순수보장형 상품의 경우 만기환급금이 0원이 되어야하는게 맞음.
			if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
				logger.info("보험형태 : {} 상품이므로 만기환급금을 0원으로 설정합니다", info.treatyList.get(0).productKind);
				info.returnPremium = "0";
			}
			logger.info("만기환급금 : {}원", info.returnPremium);

		} catch(Exception e) {
			throw new CommonCrawlerException("해약환급금 조회중 에러가 발생하였습니다");
		}

		logger.info("INNER PROCESS END");

		return true;
	}



	/**
	 * TODO 명시적 대기가 안먹는 케이스
	 * 1. 창 전환시
	 * 2. 성별 클릭 후
	 *
	 * 위의 두 케이스에 대해서는 일단 암묵적 대기 사용
	 * */
	private void waitLoadingImg() throws Exception {
		try {
			//1. class 속성값 중 loading이라는 문자를 포함하는 div 태그들을 모두 찾는다(=로딩바 관련 태그를 전부 찾는다)
			wait = new WebDriverWait(driver, 4);
			List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class[contains(., 'loading')]]")));

			//2. 로딩바가 모두 사라질 때까지 대기
			wait.until(ExpectedConditions.invisibilityOfAllElements(elements));
		} catch(TimeoutException e) {

		}
	}

	//보험유형 설정
	private void setPlanType(String planType) throws Exception{
		//1. 보험유형 선택
		selectOptionByText(By.id("prodKndCd"), planType);

		//2. 실제 홈페이지에서 클릭된 보험유형 확인
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#prodKndCd option:selected\").text()").toString();
		logger.info("홈페이지에서 클릭된 보험유형 : {}", selectedOptionText);

		if(!selectedOptionText.equals(planType)) {
			logger.error("가입설계 보험유형 : {}", planType);
			logger.error("홈페이지에서 클릭된 보험유형 : {}", selectedOptionText);
			throw new Exception("보험유형 불일치");
		} else {
			logger.info("result :: 가입설계 보험유형 == 홈페이지에서 클릭된 보험유형 : {}", selectedOptionText);
		}
	}



	//생년월일 설정
	@Override
	protected void setBirth(String birth) throws Exception {
		//1. 생년월일 입력
		setTextToInputBox(By.xpath("//div[@id='divMinsdInfo']//input[@name='btdt']"), birth);
	}



	//성별 설정
	protected void setGender(int gender) throws Exception {
		String genderText = (gender == MALE) ? "남" : "여";

		//1. 성별 클릭
		helper.waitElementToBeClickable(By.xpath("//label[text()='" + genderText + "']")).click();

		//2. 실제 홈페이지에서 클릭된 성별 확인
		String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='minsdGenCd']:checked\").attr('id')").toString();
		String checkedGender = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();
		logger.info("============================================================================");
		logger.info("가입설계 성별 : {}", genderText);
		logger.info("홈페이지에서 클릭된 성별 : {}", checkedGender);
		logger.info("============================================================================");

		if(!checkedGender.equals(genderText)) {
			logger.error("가입설계 성별 : {}", genderText);
			logger.error("홈페이지에서 클릭된 성별 : {}", checkedGender);
			throw new GenderMismatchException("성별 불일치");
		} else {
			logger.info("result :: 가입설계 성별({}) == 홈페이지에서 클릭된 성별({})", genderText, checkedGender);
			logger.info("============================================================================");
		}
	}



	//주계약 가입금액 설정
	private void setAssureMoney(int assureMoney) throws Exception {
		//1. 가입금액 단위 읽어오기
		String unit = driver.findElement(By.id("fcamtUnit")).getText().trim();

		if("억원".equals(unit)) {
			unit = "100000000";
		} else if("천만원".equals(unit)) {
			unit = "10000000";
		} else if("백만원".equals(unit)) {
			unit = "1000000";
		} else if("십만원".equals(unit)) {
			unit = "100000";
		} else if("만원".equals(unit)) {
			unit = "10000";
		} else if("천원".equals(unit)) {
			unit = "1000";
		} else if("백원".equals(unit)) {
			unit = "100";
		} else if("십원".equals(unit)) {
			unit = "10";
		} else if("원".equals(unit)) {
			unit = "1";
		}
		assureMoney = assureMoney / Integer.parseInt(unit);

		//2. 가입금액 입력
		setTextToInputBox(By.xpath("//input[@data-id='fcamt']"), String.valueOf(assureMoney));
		logger.info("입력된 가입금액 : {}원", assureMoney);
	}



	//보험기간 설정 메서드
	private void setInsTerm(String insTerm) throws Exception{
		//1. 지금 현재 주계약 보험기간에 세팅되어진 값을 읽어옴.
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"select[data-id='insPrd'] option:selected\").text()").toString();

		//2. 기존에 세팅되어진 값이랑 가입설계 보험기간이 다르면 직접 세팅하고, 일치하면 그냥 넘어간다
		if(!selectedOptionText.contains(insTerm)) {
			//보험기간 선택
			selectOptionContainsText(By.xpath("//select[@data-id='insPrd']"), insTerm);
			waitLoadingImg();
		}

		//3. 실제 홈페이지에서 클릭된 보험기간 확인
		selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"select[data-id='insPrd'] option:selected\").text()").toString();
		logger.info("============================================================================");
		logger.info("가입설계 보험기간 : {}", insTerm);
		logger.info("홈페이지에서 클릭된 보험기간 : {}", selectedOptionText);
		logger.info("============================================================================");

		if(!selectedOptionText.contains(insTerm)) {
			logger.error("가입설계 보험기간 : {}", insTerm);
			logger.error("홈페이지에서 클릭된 보험기간 : {}", selectedOptionText);
			throw new InsTermMismatchException("보험기간 불일치");
		} else {
			logger.info("result :: 가입설계 보험기간({}) == 홈페이지에서 클릭된 보험기간({})", insTerm, selectedOptionText);
			logger.info("============================================================================");
		}
	}


	//납입기간 설정 메서드
	protected void setNapTerm(String napTerm) throws Exception{
		//1. 지금 현재 주계약 납입기간에 세팅되어진 값을 읽어옴.
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"select[data-id='pymtPrd'] option:selected\").text()").toString();

		//2. 기존에 세팅되어진 값이랑 가입설계 납입기간이 다르면 직접 세팅하고, 일치하면 그냥 넘어간다
		if(!selectedOptionText.contains(napTerm)) {
			//납입기간 선택
			selectOptionContainsText(By.xpath("//select[@data-id='pymtPrd']"), napTerm);
			WaitUtil.waitFor(3);
		}

		//3. 실제 홈페이지에서 클릭된 납입기간 확인
		selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"select[data-id='pymtPrd'] option:selected\").text()").toString();
		logger.info("============================================================================");
		logger.info("가입설계 납입기간 : {}", napTerm);
		logger.info("홈페이지에서 클릭된 납입기간 : {}", selectedOptionText);
		logger.info("============================================================================");

		if(!selectedOptionText.contains(napTerm)) {
			logger.error("가입설계 납입기간 : {}", napTerm);
			logger.error("홈페이지에서 클릭된 납입기간 : {}", selectedOptionText);
			throw new NapTermMismatchException("납입기간 불일치");
		} else {
			logger.info("result :: 가입설계 납입기간({}) == 홈페이지에서 클릭된 납입기간 : ({})", napTerm, selectedOptionText);
			logger.info("============================================================================");
		}
	}


	//납입주기 설정 메서드
	protected void setNapCycle2(String napCycle) throws Exception{
		//1. 지금 현재 주계약 납입주기에 세팅되어진 값을 읽어옴.
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#pmfqyCd option:selected\").text()").toString();

		//2. 기존에 세팅되어진 값이랑 가입설계 납입주기가 다르면 직접 세팅하고, 일치하면 그냥 넘어간다
		if(!selectedOptionText.contains(napCycle)) {
			//납입주기 선택
			selectOptionByText(By.id("pmfqyCd"), napCycle);
			waitLoadingImg();
		}

		//3. 실제 홈페이지에서 클릭된 납입주기 확인
		selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#pmfqyCd option:selected\").text()").toString();
		logger.info("============================================================================");
		logger.info("가입설계 납입주기 : {}", napCycle);
		logger.info("홈페이지에서 클릭된 납입주기 : {}", selectedOptionText);
		logger.info("============================================================================");

		if(!selectedOptionText.equals(napCycle)) {
			logger.error("가입설계 납입주기 : {}", napCycle);
			logger.error("홈페이지에서 클릭된 납입주기 : {}", selectedOptionText);
			throw new NapCycleMismatchException("납입주기 불일치");
		} else {
			logger.info("result :: 가입설계 납입주기({}) == 홈페이지에서 클릭된 납입주기({})", napCycle, selectedOptionText);
			logger.info("============================================================================");
		}
	}


	//주계약 보험료 세팅 메서드
	private void setMonthlyPremium(CrawlingTreaty mainTreaty) {
		WebElement element = driver.findElement(By.id("prmSamt"));

		//1. 총 보험료 가져오기
		String monthlyPremium = element.getText().replaceAll("[^0-9]", "");

		//2. 총 보험료 단위 가져오기
		String unit = element.findElement(By.xpath("./parent::td/label[@name='prmUnit']")).getText().trim();

		if("억원".equals(unit)) {
			unit = "100000000";
		} else if("천만원".equals(unit)) {
			unit = "10000000";
		} else if("백만원".equals(unit)) {
			unit = "1000000";
		} else if("십만원".equals(unit)) {
			unit = "100000";
		} else if("만원".equals(unit)) {
			unit = "10000";
		} else if("천원".equals(unit)) {
			unit = "1000";
		} else if("백원".equals(unit)) {
			unit = "100";
		} else if("십원".equals(unit)) {
			unit = "10";
		} else if("원".equals(unit)) {
			unit = "1";
		}

		//3. 보험료 = 총 보험료(숫자) * 단위
		monthlyPremium = String.valueOf(Integer.parseInt(monthlyPremium) * Integer.parseInt(unit));

		//4. 주계약 보험료 설정
		mainTreaty.monthlyPremium = monthlyPremium;

		logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
	}


	//해약환급금 조회
	@Override
	protected void getReturnPremium(CrawlingProduct info) throws Exception {
		//1. 해약환급금 금액 단위 가져오기
		String unit = driver.findElement(By.id("basTxt")).getText();

		if(unit.contains("억원")) {
			unit = "100000000";
		} else if(unit.contains("천만원")) {
			unit = "10000000";
		} else if(unit.contains("백만원")) {
			unit = "1000000";
		} else if(unit.contains("십만원")) {
			unit = "100000";
		} else if(unit.contains("만원")) {
			unit = "10000";
		} else if(unit.contains("천원")) {
			unit = "1000";
		} else if(unit.contains("백원")) {
			unit = "100";
		} else if(unit.contains("십원")) {
			unit = "10";
		} else if(unit.contains("원")) {
			unit = "1";
		}

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
		List<WebElement> trList = driver.findElements(By.xpath("//div[@id='divTab3']//div[@class='tableStyle01 mt0']//tbody/tr"));
		for(WebElement tr : trList) {
			String term = tr.findElement(By.xpath("./td[1]")).getText();
			String premiumSum = tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
			String returnMoneyMin = tr.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
			String returnRateMin = tr.findElement(By.xpath("./td[5]")).getText();
			String returnMoneyAvg = tr.findElement(By.xpath("./td[6]")).getText().replaceAll("[^0-9]", "");
			String returnRateAvg = tr.findElement(By.xpath("./td[7]")).getText();
			String returnMoney = tr.findElement(By.xpath("./td[8]")).getText().replaceAll("[^0-9]", "");
			String returnRate = tr.findElement(By.xpath("./td[9]")).getText();

			premiumSum = String.valueOf((Integer.parseInt(premiumSum) * Integer.parseInt(unit)));
			returnMoneyMin = String.valueOf((Integer.parseInt(returnMoneyMin) * Integer.parseInt(unit)));
			returnMoneyAvg = String.valueOf((Integer.parseInt(returnMoneyAvg) * Integer.parseInt(unit)));
			returnMoney = String.valueOf((Integer.parseInt(returnMoney) * Integer.parseInt(unit)));

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
			planReturnMoney.setReturnRateMin(returnRateMin);
			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
			planReturnMoney.setReturnRateAvg(returnRateAvg);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);

			logger.info("***해약환급금***");
			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--최저해약환급금: {}", returnMoneyMin);
			logger.info("|--최저환급률: {}", returnRateMin);
			logger.info("|--평균해약환급금: {}", returnMoneyAvg);
			logger.info("|--평균환급률: {}", returnRateAvg);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--환급률: {}", returnRate + "\n");

			planReturnMoneyList.add(planReturnMoney);
		}

		logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
		info.setPlanReturnMoneyList(planReturnMoneyList);

		// 해당 상품의 해약환급금 표의 가장 마지막 값은 보험기간 만기 하루 전날의 해약환급금을 명시하고 있음.
		// 따라서 순수보장형 상품의 경우 만기환급금이 0원이 되어야하는게 맞음.
		if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
			logger.info("보험형태 : {} 상품이므로 만기환급금을 0원으로 설정합니다", info.treatyList.get(0).productKind);
			info.returnPremium = "0";
		}
		logger.info("만기환급금 : {}원", info.returnPremium);
	}
}
