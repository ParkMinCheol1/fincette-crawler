package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.AssureMoneyMismatchException;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.common.except.NapTermMismatchException;
import com.welgram.common.except.NotFoundAssureMoneyException;
import com.welgram.common.except.NotFoundInsTermException;
import com.welgram.common.except.NotFoundNapTermException;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * 2020.11.23
 * @author 조하연
 * MRA 상품 홈페이지용 클래스
 */



//미래에셋생명 상품 중 홈페이지에서 크롤링해오는 상품에 대해서는 HomepageCrawlingMRA 상속받는다.
public abstract class HomepageCrawlingMRA extends SeleniumCrawler {
//	//크롤링 옵션 정의 메서드
//	protected void setChromeOptionMRA(CrawlingProduct info) throws Exception{
//		CrawlingOption option = info.getCrawlingOption();
//
//		option.setBrowserType(CrawlingOption.BrowserType.Chrome);
//		option.setImageLoad(false);
//		option.setUserData(false);
//
//		info.setCrawlingOption(option);
//	}


	//버튼 클릭 메서드
	protected void btnClick(By element) {
		driver.findElement(element).click();
		waitLoadingImg();
	}

	//버튼 클릭 메서드
	protected void btnClick(WebElement element) {
		element.click();
		waitLoadingImg();
	}

	//적용 버튼 클릭 메서드
	protected void applyBtnClick() throws Exception{
		btnClick(By.id("modalCalcBtn"));		//적용 버튼 클릭
		waitLoadingImg();
	}

	//select 박스에서 text로 option 선택하는 메서드
	protected void selectOption(WebElement element, String text) throws Exception {
		Select select = new Select(element);

		try {
			select.selectByVisibleText(text);
		} catch (NoSuchElementException e) {
			throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')를 찾을 수 없습니다.");
		}
	}

	//select 박스에서 text로 option 선택하는 메서드
	protected void selectOption(By element, String text) throws Exception {
		Select select = new Select(driver.findElement(element));

		try {
			select.selectByVisibleText(text);
		} catch (NoSuchElementException e) {
			throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')를 찾을 수 없습니다.");
		}
	}

	//select 박스에서 text를 포함하는 option을 선택하는 메서드
	protected boolean selectOptionContainsText(WebElement element, String text) throws Exception {
		boolean isFound = false;

		Select select = new Select(element);

		List<WebElement> options = select.getOptions();
		for(WebElement option : options) {
			if(option.getText().contains(text)) {
				isFound = true;
				option.click();
				break;
			}
		}

		if(!isFound) {
			throw new Exception("해당 text(" + text + ")를 포함하는 option태그가 존재하지 않습니다.");
		}

		return isFound;
	}

	protected boolean selectOptionContainsText(By element, String text) throws Exception {
		boolean isFound = false;

		Select select = new Select(driver.findElement(element));

		List<WebElement> options = select.getOptions();
		for(WebElement option : options) {
			if(option.getText().contains(text)) {
				isFound = true;
				option.click();
				break;
			}
		}

		if(!isFound) {
			throw new Exception("해당 text(" + text + ")를 포함하는 option태그가 존재하지 않습니다.");
		}

		return isFound;
	}

	protected boolean existElement(By element) {
		boolean isExist = false;

		try {
			driver.findElement(element);
			isExist = true;
		} catch(NoSuchElementException e) {

		}

		return isExist;
	}


	//해당 element가 존재하는지 여부를 리턴
	protected boolean existElement(WebElement rootEl, By element) {

		boolean isExist = true;

		try {
			rootEl.findElement(element);
		}catch(NoSuchElementException e) {
			isExist = false;
		}

		return isExist;
	}


	protected void moveToElement(By location){
		Actions actions = new Actions(driver);
		WebElement element = driver.findElement(location);
		actions.moveToElement(element);
		actions.perform();
	}

	protected void moveToElement(WebElement location){
		Actions actions = new Actions(driver);
		actions.moveToElement(location);
		actions.perform();
	}



	//운전여부 설정 메서드
	protected void setCar(int age) {
		if(age <= 18) {
			//자녀보험료의 경우는 운전여부란에 운전이 체크가 안되기때문에 비운전으로 처리한다.
			logger.info("[Fixed]운전여부: 비운전 처리");
			btnClick(By.xpath("//label[contains(.,'비운전')]"));
		} else {
			//운전(자가용(승용차))처리
			logger.info("[Fixed]운전여부: 운전(승용차(자가용)) 처리");
			btnClick(By.xpath("//label[contains(.,'운전')]"));
		}
	}


	//inputBox에 텍스트를 입력하는 메서드
	protected void setTextToInputBox(By element, String text) {
		WebElement inputBox = driver.findElement(element);
		helper.waitElementToBeClickable(inputBox).click();
		inputBox.clear();
		inputBox.sendKeys(text);
		inputBox.sendKeys(Keys.ENTER);
	}

	//로딩이미지 명시적 대기
	protected void waitLoadingImg() {
		logger.info("로딩바 사라질때까지 대기 중...");
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ui-loading-wrap")));

//		List<WebElement> loadingElements = driver.findElements(By.xpath("//div[@class[contains(., 'loading')]]"));
		wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.xpath("//div[@class[contains(., 'loading')]]"))));
		logger.info("로딩바 사라짐");
	}



	//생년월일과 성별 설정 메서드
	protected void setBirthAndGender(CrawlingProduct info) throws Exception{
		int age = Integer.parseInt(info.age);

		if(info.productCode.equals("MRA_MDC_D001")) {
			//MRA_MDC_D001 보험의 경우 생년월일,성별을 세팅하기 위해 클릭하는 태그 값이 다르다.
			btnClick(By.id("insuAge"));
		} else {
			btnClick(By.id("insuAgeTxt"));
		}
		WaitUtil.loading(2);

		if (age <= 18) {
			//18세 이하의 경우 자녀보험료 계산(=부모계약자 세팅)
			btnClick(By.cssSelector("label[for='childrenCalc']"));	//자녀보험료 계산 버튼 클릭

			setBirth(info.fullBirth, info.parent_FullBirth);
			setGender(info.gender, info.gender);
		} else {
			setBirth(info.fullBirth);
			setGender(info.gender);
		}

//		btnClick(By.cssSelector(".btn-base.ui-fctab-e"));	//모두 입력 후 확인 버튼 클릭
		WaitUtil.loading(1);
		btnClick(By.xpath("//span[contains(., '확인')]"));	//ok
		WaitUtil.loading(1);
		waitLoadingImg();
	}


	//생년월일 설정 메서드
	protected void setBirth(String fullBirth) {
		setTextToInputBox(By.id("birthDays"), fullBirth);
	}


	//생년월일 설정 메서드
	protected void setBirth(String childBirth, String parentBirth) {
		logger.info("부모 생년월일 입력 : {}", parentBirth);
		setBirth(parentBirth);
		logger.info("자녀 생년월일 입력 : {}", childBirth);
		setTextToInputBox(By.id("chBirthDays"), childBirth);
	}


	//성별 설정 메서드
	protected void setGender(int gender) throws Exception{
		String genderText = (gender == MALE) ? "남" : "여";

		logger.info("성별 입력 : {}", genderText);
		btnClick(By.xpath("//label[contains(., '" + genderText + "')]"));
	}


	//성별 설정 메서드
	protected void setGender(int childGender, int parentGender) {
		int parentGenderTag = (parentGender == MALE) ? 1 : 2;
		int childGenderTag = (childGender == MALE) ? 4 : 5;

		logger.info("부모 성별 입력 : {}", (parentGender == MALE) ? "남자" : "여자");
		btnClick(By.cssSelector("label[for='gender_" + parentGenderTag + "']"));

		logger.info("자녀 성별 입력 : {}", (childGender == MALE) ? "남자" : "여자");
		btnClick(By.cssSelector("label[for='gender_" + childGenderTag + "']"));
	}


	//상품 유형 설정 메서드			ex. 기본형, 해약환급금이 적은 유형(보험기간 30%)
	protected void setProductType(String type) throws Exception{
		btnClick(By.xpath("//label[contains(., '" + type + "')]"));
	}


	//보험기간 설정 메서드
	protected void setInsTerm(String insTerm) throws Exception{
		try {
			WebElement label = driver.findElement(By.xpath("//input[@name='scrtPrid']/parent::div//label[contains(., '" + insTerm + "')]"));

			if(label.isEnabled()) {
				label.click();
			} else {
				throw new NoSuchElementException("");
			}

			//실제 홈페이지에서 클릭된 보험기간 확인
			String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='scrtPrid']:checked\").attr('id')").toString();
			String checkedInsTerm = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();

			logger.info("보험기간 : {} 클릭됨", checkedInsTerm);

			//선택된 홈페이지의 가입금액과 내 가입설계 가입금액 일치여부 비교
			if(!checkedInsTerm.contains(insTerm)) {
				logger.error("홈페이지 클릭된 보험기간 : {}", checkedInsTerm);
				logger.error("가입설계 보험기간 : {}", insTerm);
				throw new InsTermMismatchException("보험기간이 일치하지 않습니다.");
			}
		}catch(NoSuchElementException e) {
			logger.error("해당 보험기간({})이 존재하지 않습니다.", insTerm);
			throw new NotFoundInsTermException("해당 보험기간(" + insTerm + ")이 존재하지 않습니다.");
		}
	}


	//납입기간 설정 메서드
	protected void setNapTerm(String napTerm) throws Exception{
		try {
			WebElement label = driver.findElement(By.xpath("//input[@name='rvpd']/parent::div//label[contains(., '" + napTerm + "')]"));

			if(label.isEnabled()) {
				label.click();
			} else {
				throw new NoSuchElementException("");
			}

			//실제 홈페이지에서 클릭된 납입기간 확인
			String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='rvpd']:checked\").attr('id')").toString();
			String checkedNapTerm = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();

			logger.info("납입기간 : {} 클릭됨", checkedNapTerm);

			//선택된 홈페이지의 납입기간과 내 가입설계 납입기간 일치여부 비교
			if(!checkedNapTerm.contains(napTerm)) {
				logger.error("홈페이지 클릭된 납입기간 : {}", checkedNapTerm);
				logger.error("가입설계 납입기간 : {}", napTerm);
				throw new NapTermMismatchException("납입기간이 일치하지 않습니다.");
			}
		}catch(NoSuchElementException e) {
			logger.error("해당 납입기간({})이 존재하지 않습니다.", napTerm);
			throw new NotFoundNapTermException("해당 납입기간(" + napTerm + ")이 존재하지 않습니다.");
		}



//		WebElement element = null;
//
//		// 납입기간(napTerm)에 납이라는 글자가 없을경우, 납이라는 글자를 추가해 버튼을 클릭하게 한다.
//		if(napTerm.contains("납")) {
//			element = driver.findElement(By.xpath("//label[contains(., '" + napTerm + "')]"));
//		}else {
//			element = driver.findElement(By.xpath("//label[contains(., '" + napTerm + "납')]"));
//		}
//
//		if(element.isEnabled()) {
//			btnClick(element);
//		} else {
//			logger.error("해당 납입기간({})이 존재하지 않습니다.");
//			throw new NotFoundNapTermException("해당 납입기간(" + napTerm + ")을 선택할 수 없습니다.");
//		}

	}

	//납입주기 설정 메서드
	protected void setNapCycle(String napCycle) throws Exception{
		btnClick(By.xpath("//label[contains(., '" + napCycle + "')]"));
	}


	//주보험 유형 설정 메서드
	protected void setMainTreatyType(String textType, String planName) {
		btnClick(By.xpath("//label[contains(., '" + textType + "')]"));

		String type = planName.substring(planName.length()-6, planName.length()-1);
		//planName의 뒤에서부터 6번째 ~ 뒤에서부터 1번째 인덱스까지 자른다.
		//ex) planName이 실손의료비보험(갱신형)(무) (선택형)(종합보장형)의 경우 종합보장형을 추출해온다.

		int clickCnt = 0;

		if(type.equals("질병보장형")) {
			clickCnt = 1;
		} else if(type.equals("상해보장형")) {
			clickCnt = 2;
		}

		// clickCnt 횟수만큼 next 버튼(=  > 화살표 버튼) 클릭
		for(int i=0; i<clickCnt; i++) {
			btnClick(By.cssSelector("#simple-tab5 > div.tab-cont-wrap > div > div > div:nth-child(8) > div > button.slick-next.slick-arrow"));
		}
	}

	//가입금액 설정 메서드
	protected void setAssureMoney(String assureMoney) throws Exception{
		int _assureMoney = Integer.parseInt(assureMoney) / 10000000;

		btnClick(By.cssSelector("label[for='ntryAmt" + _assureMoney + "']"));
	}


	//사망보험금 설정 메서드
	protected void setDeathBenefit(String assureMoney) throws Exception {
		String _assureMoney = String.valueOf(Integer.parseInt(assureMoney) / 10000);

		try {
			String id = driver.findElement(
				By.cssSelector("input[type='radio'][name='ntryAmt'][value='" + _assureMoney + "']"))
				.getAttribute("id");
			driver.findElement(By.cssSelector("label[for='" + id + "']")).click();

			//실제 홈페이지에서 클릭된 가입금액(=사망보험금) 확인
			String checkedElId = ((JavascriptExecutor) driver)
				.executeScript("return $(\"input[name='ntryAmt']:checked\").attr('id')").toString();
			String checkedAssureMoney = driver
				.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();

			logger.info("사망보험금 : {} 클릭됨", checkedAssureMoney);

			checkedAssureMoney = driver.findElement(By.id(checkedElId)).getAttribute("value");
			//선택된 홈페이지의 가입금액(=사망보험금)과 내 가입설계 가입금액 일치여부 비교
			if (!checkedAssureMoney.equals(_assureMoney)) {
				logger.error("홈페이지 클릭된 사망보험금 : {}만원", checkedAssureMoney);
				logger.error("가입설계 사망보험금 : {}만원", _assureMoney);
				throw new AssureMoneyMismatchException("가입금액이 일치하지 않습니다.");
			}
		} catch (NoSuchElementException e) {
			logger.error("해당 사망보험금({})이 존재하지 않습니다.", assureMoney);
			throw new NotFoundAssureMoneyException("해당 사망보험금(" + assureMoney + ")이 존재하지 않습니다.");
		}
	}


	//주계약 보험료 세팅 메서드
	protected void setPremiums(CrawlingProduct info) {
		String productCode = info.productCode;
		String premiumTag = null;

		//보험상품별 월 보험료 태그값이 다르다.
		if(productCode.equals("MRA_MDC_D001")) {
			premiumTag = driver.findElement(By.cssSelector("strong[class='num monAmt']")).getText();
		} else if(productCode.equals("MRA_TRM_D002"))  {
			premiumTag = driver.findElement(By.cssSelector("strong[data-id='prmTxt']")).getText();
		} else {
			premiumTag = driver.findElement(By.cssSelector("strong[data-id='totDcPrmTxt']")).getText();
		}

		String monthlyPremium = premiumTag.replaceAll("[^0-9]", "");

		info.treatyList.get(0).monthlyPremium = monthlyPremium;
	}


	//해약환급금 조회 메서드
	protected void getReturnPremiums(CrawlingProduct info) throws Exception{
		helper.waitElementToBeClickable(By.id("surrBtn")).click();		//해약환급금 버튼 클릭
		WaitUtil.waitFor(2);

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

		List<WebElement> trList = driver.findElements(By.cssSelector("#srdrList > tr"));

		for (WebElement tr : trList) {
			String term = tr.findElement(By.cssSelector("th:nth-child(1)")).getText(); //경과기간
			String premiumSum = tr.findElement(By.cssSelector("td:nth-child(2)")).getText(); //납입보험료
			String returnMoney = tr.findElement(By.cssSelector("td:nth-child(3)")).getText(); //해약환급금
			String returnRate = tr.findElement(By.cssSelector("td:nth-child(4)")).getText(); //환급률


			logger.info("|_______________________");
			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--환급률: {}", returnRate);
			logger.info("|_______________________");

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);

			planReturnMoneyList.add(planReturnMoney);
			info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

		logger.info("보험기간({}) 만료시 만기환급금 : {}원", info.insTerm, info.returnPremium);
	}
}

