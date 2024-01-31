package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.WaitUtil;
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
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * 신한생명 - 무배당 신한인터넷 생활비주는암보험
 *
 * @author SungEun Koo <aqua@welgram.com>
 */
// 2022.12.23 				| 최우진 				| 대면_정기
// SHL_TRM_F007 			| 신한 스마트 정기보험 1종 순수보장형(무배당)
public class SHL_TRM_F007 extends CrawlingSHL {

	public static void main(String[] args) {
		executeCommand(new SHL_TRM_F007(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		logger.info("종신/정기보험 클릭");
		helper.waitElementToBeClickable(By.xpath("//label[@for[contains(., 'ipt_sub_tab2_')]]/span[contains(., '정기보험')]")).click();

		logger.info("보험료계산 버튼 클릭");
		helper.waitElementToBeClickable(By.xpath("//em[text()='신한 스마트 정기보험 1종 순수보장형(무배당)']/ancestor::li//button[text()='보험료계산']")).click();

		logger.info("창 전환");
		currentHandle = driver.getWindowHandle();
		if(wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
			helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
		}
		//창 전환시 명시적 대기 에러 발생. 일단 암묵적으로 5초 대기
		WaitUtil.waitFor(5);

		logger.info("생년월일 설정 : {}", info.fullBirth);
		setBirth(info.fullBirth);

		logger.info("성별 설정");
		setGender(info.gender);

		logger.info("주계약 가입금액 설정");
		setAssureMoney(info.treatyList.get(0).assureMoney);

		logger.info("주계약 보험기간 설정");
		setInsTerm(info.insTerm);

		logger.info("주계약 납입기간 설정");
		String napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm;
		setNapTerm(napTerm);

		logger.info("주계약 납입주기 설정");
		setNapCycle2(info.getNapCycleName());

		logger.info("보험료 계산하기 버튼 클릭");
		helper.waitElementToBeClickable(By.id("btnPrmCalc")).click();
//			waitLoadingImg();
		WaitUtil.waitFor(3);

		logger.info("주계약 보험료 설정");
		setMonthlyPremium(info.treatyList.get(0));

		logger.info("보장내용확인 버튼 클릭");
		helper.waitElementToBeClickable(By.id("btnCalResult")).click();
//			waitLoadingImg();
		WaitUtil.waitFor(3);

		logger.info("스크린샷 찍기");
		moveToElement(By.xpath("//td[text()='총 보험료']"));
		takeScreenShot(info);

		logger.info("해약환급금 예시 버튼 클릭");
		helper.waitElementToBeClickable(By.linkText("해약환급금 예시")).click();
		WaitUtil.waitFor(3);
		getReturnPremium(info);

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
		helper.waitElementToBeClickable(By.xpath("//div[@id='divMinsdInfo']//label[text()='" + genderText + "']")).click();
		WaitUtil.waitFor(3);
//		waitLoadingImg();

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
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#tblMncvInfo select[data-id='insPrd'] option:selected\").text()").toString();

		//2. 기존에 세팅되어진 값이랑 가입설계 보험기간이 다르면 직접 세팅하고, 일치하면 그냥 넘어간다
		if(!selectedOptionText.contains(insTerm)) {
			//보험기간 선택
			selectOptionContainsText(By.xpath("//select[@data-id='insPrd']"), insTerm);
//			waitLoadingImg();
			WaitUtil.waitFor(3);
		}

		//3. 실제 홈페이지에서 클릭된 보험기간 확인
		selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#tblMncvInfo select[data-id='insPrd'] option:selected\").text()").toString();
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
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#tblMncvInfo select[data-id='pymtPrd'] option:selected\").text()").toString();

		//2. 기존에 세팅되어진 값이랑 가입설계 납입기간이 다르면 직접 세팅하고, 일치하면 그냥 넘어간다
		if(!selectedOptionText.contains(napTerm)) {
			//납입기간 선택
			selectOptionContainsText(By.xpath("//select[@data-id='pymtPrd']"), napTerm);
//			waitLoadingImg();
			WaitUtil.waitFor(3);
		}

		//3. 실제 홈페이지에서 클릭된 납입기간 확인
		selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#tblMncvInfo select[data-id='pymtPrd'] option:selected\").text()").toString();
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
//			waitLoadingImg();
			WaitUtil.waitFor(3);
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
		unit = unit.substring(unit.indexOf("단위"));

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
			String premiumSum = tr.findElement(By.xpath("./td[5]")).getText().replaceAll("[^0-9]", "");
			String returnMoney = tr.findElement(By.xpath("./td[6]")).getText().replaceAll("[^0-9]", "");
			String returnRate = tr.findElement(By.xpath("./td[7]")).getText();

			premiumSum = String.valueOf((Integer.parseInt(premiumSum) * Integer.parseInt(unit)));
			returnMoney = String.valueOf((Integer.parseInt(returnMoney) * Integer.parseInt(unit)));

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);

			logger.info("***해약환급금***");
			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--환급률: {}", returnRate + "\n");

			planReturnMoneyList.add(planReturnMoney);
		}

		info.planReturnMoneyList = planReturnMoneyList;

		// 해당 상품의 해약환급금 표의 가장 마지막 값은 보험기간 만기 하루 전날의 해약환급금을 명시하고 있음.
		// 따라서 순수보장형 상품의 경우 만기환급금이 0원이 되어야하는게 맞음.
		if(info.treatyList.get(0).productKind == ProductKind.순수보장형) {
			info.returnPremium = "0";
		}

		logger.info("만기환급금 : {}원", info.returnPremium);
	}

}
