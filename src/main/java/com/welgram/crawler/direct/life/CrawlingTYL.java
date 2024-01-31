package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.GenderMismatchException;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.common.except.NapCycleMismatchException;
import com.welgram.common.except.NapTermMismatchException;
import com.welgram.common.except.NotFoundInsTermException;
import com.welgram.common.except.NotFoundNapTermException;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;



/**
 * 2021.02.02
 * @author 조하연
 * TYL 홈페이지, 공시실 코드
 */
// 2021.02.02 | TYL 담당 from 조하연
// 2022.04.01 | TYL 담당 to   최우진 (from.조하연)
public abstract class CrawlingTYL extends SeleniumCrawler {


// todo | todoList (...ing) + 6
// 2022.10.24 메모내용 옮김
// 1. Depth 설정
// 2. 공시실, 원수사 사용자 홈페이지 분리
// 3. 전체 메서드 체이닝 변경 가능유무


// todo | todoList (...ing)
// 1. 메서드명 변경 혹은 내용변경 필요
	// 	inputTreatiesInfoASV(), submitSubTreaties()

	//크롤링 옵션 정의 메서드(홈페이지, 공시실 공용)
	protected void setChromeOptionTYL(CrawlingProduct info) {
		CrawlingOption option = info.getCrawlingOption();

		option.setBrowserType(CrawlingOption.BrowserType.Chrome);
		option.setImageLoad(false);
		option.setUserData(false);

		info.setCrawlingOption(option);
	}



	//특정 element가 존재하는지 여부를 리턴하는 메서드
	protected boolean existElement(By element) {
		try{
			driver.findElement(element);
		}catch(NoSuchElementException e) {
			return false;
		}
		return true;
	}



	//element가 보이게끔 이동
	protected void moveToElement(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}



	//li태그 중 text가 일치하는 li 선택하는 메서드
	protected void selectLi(By liList, String text) throws Exception{
		List<WebElement> elements = driver.findElements(liList);
		boolean isSelected = false;

		for(WebElement li : elements) {
			if(li.getText().trim().equals(text)) {
				li.click();
				isSelected = true;
				break;
			}
		}

		if(!isSelected) {
			throw new NotFoundTextInSelectBoxException("selectBox 안에 해당 text(" + text + ")가 존재하지 않습니다.");
		}
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



	//알럿창 존재여부 리턴 메서드
	protected boolean existAlert() {
		return ExpectedConditions.alertIsPresent().apply(driver) != null;
	}



	//버튼 클릭 메서드(홈페이지, 공시실 공용)
	protected void btnClick(By element) throws  Exception {
		driver.findElement(element).click();
	}



	//버튼 클릭 메서드(홈페이지, 공시실 공용)
	protected void btnClick(WebElement element) throws  Exception {
		element.click();
	}



	//공시실용 버튼 클릭 메서드(공시실용 명시적 대기 코드가 추가돼있음)
	protected void announceBtnClick(By element) throws Exception {
		helper.waitElementToBeClickable(element).click();
		waitAnnounceLoadingImg();
		WaitUtil.waitFor(2);
	}



	//공시실용 버튼 클릭 메서드(공시실용 명시적 대기 코드가 추가돼있음)
	protected void announceBtnClick(WebElement element) throws Exception {
		element.click();
		waitAnnounceLoadingImg();
		WaitUtil.waitFor(2);
	}



	//홈페이지용 버튼 클릭 메서드(홈페이지용 명시적 대기 코드가 추가돼있음)
	protected void homepageBtnClick(By element) throws Exception {
		btnClick(element);
		waitHomepageLoadingImg();
		WaitUtil.waitFor(2);
	}



	//홈페이지용 버튼 클릭 메서드(홈페이지용 명시적 대기 코드가 추가돼있음)
	protected void homepageBtnClick(WebElement element) throws Exception {
		element.click();
		waitHomepageLoadingImg();
		WaitUtil.waitFor(2);
	}



	// todo 2022.04.14 | 최우진 | 크롤링 수정
	//홈페이지용 확인하기 버튼 클릭 메서드
	protected void homepageOkBtnClick() throws Exception {
		//homepageBtnClick(By.cssSelector("img[alt='확인하기']"));
		homepageBtnClick(By.cssSelector("img[alt='수령액 확인하기']"));
	}



	//홈페이지용 직접설계 버튼 클릭 메서드
	protected void homepageDirectDesignBtnClick() throws Exception {
		homepageBtnClick(By.cssSelector("img[alt='직접설계']"));
	}



	//홈페이지용 계산하기 버튼 클릭 메서드
	protected void homepageCalcBtnClick() throws Exception {
		homepageBtnClick(By.id("btn_free_calc"));
	}


	//홈페이지용 로딩이미지 명시적 대기
	protected void waitHomepageLoadingImg() {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("prograss-document-bar")));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("prograss-bar")));
	}



	//공시실용 로딩이미지 명시적 대기
	protected void waitAnnounceLoadingImg() {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBar")));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("img[alt='loading']")));
	}



	//inputBox에 텍스트를 입력하는 메서드
	protected void setTextToInputBox(By element, String text) {
		WebElement inputBox = driver.findElement(element);
		inputBox.click();
		Actions builder = new Actions(driver);
		builder.keyDown(Keys.CONTROL).sendKeys("a").sendKeys(Keys.DELETE).keyUp(Keys.CONTROL).build().perform();
		inputBox.sendKeys(text);
	}



	protected void setTextToInputBox(WebElement inputBox, String text) {
		inputBox.click();
		Actions builder = new Actions(driver);
		builder.keyDown(Keys.CONTROL).sendKeys("a").sendKeys(Keys.DELETE).keyUp(Keys.CONTROL).build().perform();
		inputBox.sendKeys(text);
	}



	//홈페이지용 성별 설정 메서드
	protected void setHomepageGender(int gender) throws Exception{
		String genderText = (gender == MALE) ? "남자" : "여자";
		btnClick(By.xpath("//label[contains(., '" + genderText + "')]"));
	}



	//홈페이지용 성별 설정 메서드(부모, 자녀 2개 성별 입력)
	protected void setHomepageGender(int childGender, int parentGender) throws Exception{
		String childGenderText = (childGender == MALE) ? "남아" : "여아";

		logger.info("부모 성별 설정");
		setHomepageGender(parentGender);

		logger.info("자녀 성별 설정");
		btnClick(By.xpath("//label[contains(., '" + childGenderText + "')]"));
	}



	//홈페이지용 생년월일 설정 메서드
	protected void setHomepageBirth(String fullBirth) {
		setTextToInputBox(By.id("input01"), fullBirth);
	}



	//홈페이지용 생년월일 설정 메서드(부모, 자녀 2개 생년월일 입력)
	protected void setHomepageBirth(String childBirth, String parentBirth) {
		logger.info("부모 생년월일 설정 : {}", parentBirth);
		setHomepageBirth(parentBirth);

		logger.info("자녀 생년월일 설정 : {}", childBirth);
		setTextToInputBox(By.id("input02"), childBirth);
	}



	/*
	 * 홈페이지용 가입금액 설정 메서드
	 *  => 가입금액이 주계약 보험료가 된다(연금보험, 저축보험에 사용됨)
	 * */
	protected void setHomepageAssureMoney(CrawlingProduct info, By element) {
		// todo 2022.04.14 | 최우진 | 크롤링 수정
		//String unit = driver.findElement(By.cssSelector("#yymmdd-area .txt_right")).getText().trim();
		String cssPath = "#body > div.my-calc-area > div > div.input-area > div > div:nth-child(2) > div.cal_4_1 > div > div";
		String unit = driver.findElement(By.cssSelector(cssPath)).getText().trim();

		String _assureMoney = info.assureMoney;

		if("만원".equals(unit)) {
			_assureMoney = String.valueOf(Integer.parseInt(info.assureMoney) / 10000);
		}

		info.treatyList.get(0).monthlyPremium = info.assureMoney;
		setTextToInputBox(element, _assureMoney);
	}



	//홈페이지용 보험기간 설정 메서드
	protected void setHomepageInsTerm(String insTerm) throws Exception{
		btnClick(By.id("TRMINS-button"));

		try {
 			selectLi(By.cssSelector("#ui-selectmenu-menu-TRMINS li"), insTerm);

			String checkedInsTerm = driver.findElement(By.cssSelector("#TRMINS-button .ui-selectmenu-text")).getText();

			logger.info("클릭된 보기 : {}", checkedInsTerm);

			if(!checkedInsTerm.equals(insTerm)) {
				logger.info("===============================");
				logger.info("홈페이지 클릭된 보기 : {}", checkedInsTerm);
				logger.info("가입설계 보기 : {}", insTerm);
				logger.info("===============================");
				throw new InsTermMismatchException("보험기간이 일치하지 않습니다.");
			}
		}catch(NotFoundTextInSelectBoxException e) {
			throw new NotFoundInsTermException("보험기간(" + insTerm + ")이 존재하지 않습니다.");
		}

	}



	// 2022.04.04 | 최우진 | '전기납' 케이스 수정
	//홈페이지용 납입기간 설정 메서드
	protected void setHomepageNapTerm(String napTerm, String insTerm) throws Exception{
		btnClick(By.id("PYPD-button"));

		try {
			String checkedNapTerm;

			// '전기납' 케이스 추가분
			if((napTerm.equals(insTerm))
				&&(existElement(By.xpath("//ul[@id='PYPD-menu']//li[text()='전기납']"))) ) {
				selectLi(By.cssSelector("#ui-selectmenu-menu-PYPD li"), "전기납");
				checkedNapTerm = driver.findElement(By.cssSelector("#PYPD-button .ui-selectmenu-text")).getText();
				if("전기납".equals(checkedNapTerm)) { logger.info("홈페이지 클릭된 납기 : {}", checkedNapTerm); }
			}
			// 기존 케이스
			else {
				selectLi(By.cssSelector("#ui-selectmenu-menu-PYPD li"), napTerm);
				checkedNapTerm = driver.findElement(By.cssSelector("#PYPD-button .ui-selectmenu-text")).getText();
				if(!checkedNapTerm.equals(napTerm)) {
					logger.info("===============================");
					logger.info("홈페이지 클릭된 납기 : {}", checkedNapTerm);
					logger.info("가입설계 납기 : {}", napTerm);
					logger.info("===============================");

					throw new NapTermMismatchException("납입기간이 일치하지 않습니다.");
				}
			}
			logger.info("클릭된 납기 : {}", checkedNapTerm);

		} catch(NotFoundTextInSelectBoxException e) {
			throw new NotFoundNapTermException("납입기간(" + napTerm + ")이 존재하지 않습니다.");
		}
	}



	//홈페이지용 연금개시 나이 설정 메서드
	protected void setAnnuityAge(String annAge) throws Exception{
		btnClick(By.id("TRMINS-button"));
		selectLi(By.cssSelector("#ui-selectmenu-menu-TRMINS li"), annAge + "세");
	}



	//홈페이지용 연금수령타입 설정 메서드
	protected void setHomepageAnnuityType(String annuityType) throws Exception{
		String _annuityType = annuityType.substring(2, 5);
		String text = "종신(" + _annuityType + " 보증)";

		btnClick(By.id("ANTY_GUA_PRID_COD-button"));
		selectLi(By.cssSelector("#ui-selectmenu-menu-ANTY_GUA_PRID_COD li"), text);
	}



	//홈페이지용 연금수령액 설정 메서드
	protected void setHomepageAnnuityPremium(CrawlingProduct info) {
		WebElement premiumEl = driver.findElement(By.cssSelector("div.text-right.BTYP_FRST_PENAMT_BAS"));
		String annuityPremium = premiumEl.getText().replaceAll("[^0-9]", "");

		info.annuityPremium = annuityPremium;
	}



	//홈페이지용 주계약 보험료 설정 메서드
	protected void setHomepagePremiums(CrawlingProduct info, By element) {
		WebElement premiumEl = driver.findElement(element);
		String monthlyPremium = premiumEl.getText().replaceAll("[^0-9]", "");

		logger.info("월 보험료 : {}", monthlyPremium + "원");

		info.treatyList.get(0).monthlyPremium = monthlyPremium;
	}



	//홈페이지용 해약환급금 조회 및 세팅 메서드(경과기간, 납입보험료, 해약환급금, 환급률 정보만 나온 경우 사용)
	protected void getHomepageShortReturnPremiums(CrawlingProduct info, By element) throws Exception{
		homepageBtnClick(element);		//해약환급금 버튼 클릭

		boolean isExistBtn = existElement(By.id("btn-trem-data"));	//전체기간 보기 버튼의 존재여부

		if(isExistBtn) {
			homepageBtnClick(By.id("btn-trem-data"));
		}

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		List<WebElement> trList = driver.findElements(By.cssSelector(".table.text-center tbody tr"));
		for (WebElement tr : trList) {
			try {
				tr.findElements(By.tagName("td"));
				/*
				 * 전체기간 보기 버튼을 클릭하게되면 간혹 빈 tr이 존재하는 경우가 있다.
				 * 빈 tr에서 td라는 태그를 가져오려하면 IndexOutOfBoundsException 예외가 발생한다.
				 * 그럴 경우 빈 tr문을 그냥 넘어가게끔 catch문 안에 continue를 적어준다.
				 * */

				String term = tr.findElements(By.tagName("td")).get(0).getText();
				String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
				String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
				String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

				logger.info("______해약환급급__________ ");
				logger.info("|--경과기간: {}", term);
				logger.info("|--납입보험료: {}", premiumSum);
				logger.info("|--해약환급금: {}", returnMoney);
				logger.info("|--최저납입보험료: {}", premiumSum);
				logger.info("|--환급률: {}", returnRate);
				logger.info("|_______________________");

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();

				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
				planReturnMoney.setInsAge(Integer.parseInt(info.age));

				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);

				planReturnMoneyList.add(planReturnMoney);
				info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
			}catch(IndexOutOfBoundsException e) {
				continue;
			}
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

		logger.info("보험기간({}) 만료시 만기환급금 : {}원", info.insTerm, info.returnPremium);
	}



	//홈페이지용 해약환급금 조회 및 세팅 메서드(경과기간, 납입보험료, 최저.평균.공시 정보 모두 나온 경우 사용)
	protected void getHomepageFullReturnPremiums(CrawlingProduct info, WebElement element) throws Exception{
		homepageBtnClick(element);		// 해약환급금 버튼 클릭

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

		List<WebElement> trList = driver.findElements(By.cssSelector(".table.text-center tbody tr"));
		for (WebElement tr : trList) {
			String term = tr.findElements(By.tagName("td")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
			String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText();
			String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
			String returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText();
			String returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();
			String returnMoney = tr.findElements(By.tagName("td")).get(6).getText();
			String returnRate = tr.findElements(By.tagName("td")).get(7).getText();

			logger.info("______해약환급급__________ ");

			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--최저납입보험료: {}", premiumSum);
			logger.info("|--최저해약환급금: {}", returnMoneyMin);
			logger.info("|--최저해약환급률: {}", returnRateMin);
			logger.info("|--평균해약환급금: {}", returnMoneyAvg);
			logger.info("|--평균해약환급률: {}", returnRateAvg);
			logger.info("|--환급률: {}", returnRate);
			logger.info("|_______________________");

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();

			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
			planReturnMoney.setInsAge(Integer.parseInt(info.age));

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
			planReturnMoney.setReturnRateMin(returnRateMin);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
			planReturnMoney.setReturnRateAvg(returnRateAvg);

			planReturnMoneyList.add(planReturnMoney);
			info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);

		btnClick(By.cssSelector(".ui-dialog-titlebar-close"));
	}



	//공시실에서 보험 찾기 메서드
	protected void findInsuFromAnnounce(String insuName) throws Exception {

		driver.findElement(By.xpath("//span[text()='전체열기']")).click();

		String currentHandle = driver.getWindowHandle();

		try {

			driver.findElement(By.xpath("//span[text()='" + insuName + "']/ancestor::tr//button")).click();
			WaitUtil.loading(4);

			helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

		} catch(NoSuchElementException e) {

			logger.info("{} 이 존재하지 않습니다. 보험명을 다시 확인해주세요.", insuName);
		}

		WaitUtil.waitFor(4);
	}



	//공시실용 이름 설정 메서드
	protected void setAnnounceName(String name) {
		setTextToInputBox(By.id("name_21"), name);
	}



	//공시실용 생년월일 설정 메서드
	protected void setAnnounceBirth(String fullBirth) {
		String year = fullBirth.substring(0, 4);
		String month = fullBirth.substring(4, 6);
		String date = fullBirth.substring(6, 8);

		setTextToInputBox(By.id("birthday_Y_21"), year);
		setTextToInputBox(By.id("birthday_M_21"), month);
		setTextToInputBox(By.id("birthday_D_21"), date);
	}



	//공시실용 성별 설정 메서드
	protected void setAnnounceGender(int gender) throws Exception {
		String genderText = (gender == MALE) ? "남자" : "여자";

		helper.waitElementToBeClickable(By.xpath("//label[contains(., '" + genderText + "')]")).click();

		//실제 홈페이지에서 클릭된 성별 확인
		String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='sex_21']:checked\").attr('id')").toString();
		String checkedGender = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();
		logger.info("클릭된 성별 : {}", checkedGender);

		if(!checkedGender.equals(genderText)) {
			logger.error("가입설계 성별 : {}", genderText);
			logger.error("홈페이지에서 클릭된 성별 : {}", checkedGender);
			throw new GenderMismatchException("성별 불일치");
		}
	}



	//공시실용 주계약 선택 메서드
	protected void setPlanType(String planType) throws Exception {
		selectOption(By.cssSelector("#policycd_l"), planType);

		//실제 홈페이지에서 클릭된 주상품 유형 확인
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#policycd_l option:selected\").text()").toString();
		logger.info("클릭된 주상품 유형 : {}", selectedOptionText);

		if(!selectedOptionText.equals(planType)) {
			logger.error("가입설계 주상품 유형 : {}", planType);
			logger.error("홈페이지에서 클릭된 주상품 유형 : {}", selectedOptionText);
			throw new Exception("주상품 유형 불일치");
		}
	}



	//공시실용 보험기간 설정 메서드
	protected void setAnnounceInsTerm(String insTerm) throws Exception {
		//1. 주계약 tr을 찾는다(해당 상품은 주계약의 경우 default로 체크가 되어 있음.
		WebElement mainTreatyTr = driver.findElement(By.xpath("//tbody[@id='step3_tbody1']//input[@type='checkbox'][@checked='checked']"));

		//2. 주계약의 보험기간 selectbox를 찾는다.
		WebElement mainTreatyInsTermEl = mainTreatyTr.findElement(By.xpath("./ancestor::tr//select[@title[contains(., '보험기간')]]"));
		String mainTreatyInsTermElId = mainTreatyInsTermEl.getAttribute("id");

		//3. 보험기간 세팅
		selectOption(mainTreatyInsTermEl, insTerm);

		//실제 홈페이지에서 클릭된 보험기간 확인
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#" + mainTreatyInsTermElId + " option:selected\").text()").toString();
		logger.info("클릭된 보험기간 유형 : {}", selectedOptionText);

		if(!selectedOptionText.equals(insTerm)) {
			logger.error("가입설계 보험기간 유형 : {}", insTerm);
			logger.error("홈페이지에서 클릭된 보험기간 유형 : {}", selectedOptionText);
			throw new InsTermMismatchException("보험기간 불일치");
		}
	}



	//공시실용 납입기간 설정 메서드
	protected void setAnnounceNapTerm(String napTerm) throws Exception {
		//1. 주계약 tr을 찾는다(해당 상품은 주계약의 경우 default로 체크가 되어 있음.
		WebElement mainTreatyTr = driver.findElement(By.xpath("//tbody[@id='step3_tbody1']//input[@type='checkbox'][@checked='checked']"));

		//2. 주계약의 납입기간 selectbox를 찾는다.
		WebElement mainTreatyNapTermEl = mainTreatyTr.findElement(By.xpath("./ancestor::tr//select[@title[contains(., '납입기간')]]"));
		String mainTreatyNapTermElId = mainTreatyNapTermEl.getAttribute("id");

		//3. 납입기간 세팅
		selectOption(mainTreatyNapTermEl, napTerm);

		//실제 홈페이지에서 클릭된 납입기간 확인
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#" + mainTreatyNapTermElId + " option:selected\").text()").toString();
		logger.info("클릭된 납입기간 유형 : {}", selectedOptionText);

		if(!selectedOptionText.equals(napTerm)) {
			logger.error("가입설계 납입기간 유형 : {}", napTerm);
			logger.error("홈페이지에서 클릭된 납입기간 유형 : {}", selectedOptionText);
			throw new NapTermMismatchException("납입기간 불일치");
		}
	}



	//공시실용 납입주기 설정 메서드
	protected void setAnnounceNapCycle(String napCycle) throws Exception {
		//1. 주계약 tr을 찾는다(해당 상품은 주계약의 경우 default로 체크가 되어 있음.
		WebElement mainTreatyTr = driver.findElement(By.xpath("//tbody[@id='step3_tbody1']//input[@type='checkbox'][@checked='checked']"));

		//2. 주계약의 납입주기 selectbox를 찾는다.
		WebElement mainTreatyNapCycleEl = mainTreatyTr.findElement(By.xpath("./ancestor::tr//select[@title[contains(., '납입주기')]]"));
		String mainTreatyNapCycleElId = mainTreatyNapCycleEl.getAttribute("id");

		//3. 납입주기 세팅
		selectOption(mainTreatyNapCycleEl, napCycle);

		//실제 홈페이지에서 클릭된 납입주기 확인
		String selectedOptionText = ((JavascriptExecutor)driver).executeScript("return $(\"#" + mainTreatyNapCycleElId + " option:selected\").text()").toString();
		logger.info("클릭된 납입주기 : {}", selectedOptionText);

		if(!selectedOptionText.equals(napCycle)) {
			logger.error("가입설계 납입주기 : {}", napCycle);
			logger.error("홈페이지에서 클릭된 납입주기 : {}", selectedOptionText);
			throw new NapCycleMismatchException("납입주기 불일치");
		}
	}



	//공시실용 가입금액 설정 메서드
	protected void setAnnounceAssureMoney(int assureMoney) {
		//1. 주계약 tr을 찾는다(해당 상품은 주계약의 경우 default로 체크가 되어 있음.
		WebElement mainTreatyTr = driver.findElement(By.xpath("//tbody[@id='step3_tbody1']//input[@type='checkbox'][@checked='checked']"));

		//2. 주계약의 가입금액 inputBox를 찾는다.
		WebElement mainTreatyAssureMoneyEl = mainTreatyTr.findElement(By.xpath("./ancestor::tr//input[@title[contains(., '가입금액')]]"));

		//3. 주계약의 가입금액 inputBox를 클릭한다.
		mainTreatyAssureMoneyEl.click();

		//4. 툴팁창이 뜨면 가입금액의 단위를 읽어온다.
		String unit = mainTreatyAssureMoneyEl.findElement(By.xpath("(./parent::div//p[@class='inner_ballon']//b[@class='red'])[1]")).getText().trim();

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

		//5. 주계약 가입금액을 세팅한다.
		setTextToInputBox(mainTreatyAssureMoneyEl, String.valueOf(assureMoney));
	}



	//공시실용 주계약 보험료 세팅 메서드
	protected void setAnnounceMonthlyPremium(CrawlingTreaty mainTreaty) {
		String monthlyPremium = null;
		try {
			// 가입금액이 높아 고액 할인이 들어가는 경우에는
			monthlyPremium = driver.findElement(By.xpath("//th[text()='최종 합계보험료']/parent::tr//span[@class='point1']")).getText().replaceAll("[^0-9]", "");
			logger.info("보험료 :: {}", monthlyPremium);

		} catch(NoSuchElementException e) {
			// 가입금액이 낮아 고액 할인이 안들어가는 경우에는 합계보험료의 값을 크롤링해야함.
			monthlyPremium = driver.findElement(By.xpath("//th[text()='합계보험료']/parent::tr//span[@class='point1']")).getText().replaceAll("[^0-9]", "");
			logger.info("보험료 :: {}", monthlyPremium);
		}
		mainTreaty.monthlyPremium = monthlyPremium;

		logger.info("합계보험료 : {}원", mainTreaty.monthlyPremium);
	}



	//공시실용 해약환급금 조회 메서드(경과기간, 납입보험료, 해약환급금, 환급률 정보만 나온 경우 사용)
	protected void getAnnounceShortReturnPremiums(CrawlingProduct info) {
		int unit = 1;
		String unitText = driver.findElement(By.cssSelector(".mb5 .t_right")).getText();

		if (unitText.contains("만원")) {
			unit = 10000;
		}

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		List<WebElement> trList = driver.findElements(By.cssSelector(".tblCol.tableCyber tbody tr"));
		for (WebElement tr : trList) {
			String term = tr.findElements(By.tagName("td")).get(0).getText().trim();
			String premiumSum = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "").trim();
			String returnMoney = tr.findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]", "").trim();
			String returnRate = tr.findElements(By.tagName("td")).get(4).getText().trim();


			//공시실 해약환급금 테이블의 단위가 만원일 경우 단위를 맞춰준다.
			premiumSum = String.valueOf(Long.parseLong(premiumSum) * unit);
			returnMoney = String.valueOf(Long.parseLong(returnMoney) * unit);

			logger.info("______해약환급급__________ ");
			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--환급률: {}", returnRate);
			logger.info("|_______________________");

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();

			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
			planReturnMoney.setInsAge(Integer.parseInt(info.age));

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);

			planReturnMoneyList.add(planReturnMoney);

			info.returnPremium = returnMoney;			// 만기 환급금
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);		// 해약환급금
	}



	//공시실용 해약환급금 조회 메서드(경과기간, 납입보험료, 최저.평균.공시 정보 모두 나온 경우 사용)
	protected void getAnnounceFullReturnPremiums(CrawlingProduct info) {
		int unit = 1;
		String unitText = driver.findElement(By.cssSelector(".mb5 .t_right")).getText();

		if (unitText.contains("만원")) {
			unit = 10000;
		}

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		List<WebElement> trList = driver.findElements(By.cssSelector(".tblCol.tableCyber tbody tr"));

		for (WebElement tr : trList) {
			String term = tr.findElements(By.tagName("td")).get(0).getText().trim();
			String premiumSum = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "").trim();

			String returnMoneyMin = tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "").trim();
			String returnRateMin = tr.findElements(By.tagName("td")).get(5).getText().trim();

			String returnMoneyAvg = tr.findElements(By.tagName("td")).get(7).getText().replaceAll("[^0-9]", "").trim();
			String returnRateAvg = tr.findElements(By.tagName("td")).get(8).getText().trim();

			String returnMoney = tr.findElements(By.tagName("td")).get(10).getText().replaceAll("[^0-9]", "").trim();
			String returnRate = tr.findElements(By.tagName("td")).get(11).getText().trim();

			//공시실 해약환급금 테이블의 단위가 만원일 경우 단위를 맞춰준다.
			premiumSum = String.valueOf(Long.parseLong(premiumSum) * unit);
			returnMoney = String.valueOf(Long.parseLong(returnMoney) * unit);
			returnMoneyMin = String.valueOf(Long.parseLong(returnMoneyMin) * unit);
			returnMoneyAvg = String.valueOf(Long.parseLong(returnMoneyAvg) * unit);

			logger.info("______해약환급급__________ ");
			logger.info("|--경과기간: {}", term);
			logger.info("|--납입보험료: {}", premiumSum);
			logger.info("|--해약환급금: {}", returnMoney);
			logger.info("|--최저납입보험료: {}", premiumSum);
			logger.info("|--최저해약환급금: {}", returnMoneyMin);
			logger.info("|--최저해약환급률: {}", returnRateMin);
			logger.info("|--평균해약환급금: {}", returnMoneyAvg);
			logger.info("|--평균해약환급률: {}", returnRateAvg);
			logger.info("|--환급률: {}", returnRate);
			logger.info("|_______________________");

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();

			planReturnMoney.setPlanId(Integer.parseInt(info.planId));
			planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
			planReturnMoney.setInsAge(Integer.parseInt(info.age));

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoneyMin);
			planReturnMoney.setReturnRateMin(returnRateMin);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
			planReturnMoney.setReturnRateAvg(returnRateAvg);

			planReturnMoneyList.add(planReturnMoney);

			info.returnPremium = returnMoney;					// 만기환급금
		}

		info.setPlanReturnMoneyList(planReturnMoneyList);		// 해약환급금
	}



	// 2022.08.03 | 최우진 |

	// (공통) step1 - 고객정보를 입력해 주세요
	protected void submitCustomerInfo(String name, String birth, int gender) throws Exception {

		logger.info("이름 설정 : {}", name);
		setAnnounceName(name);

		logger.info("생년월일 설정 : {}", birth);
		setAnnounceBirth(birth);

		logger.info("성별 설정 : {}", gender);
		setAnnounceGender(gender);

		logger.info("주상품 조회 버튼 클릭!");
		announceBtnClick(By.xpath("//span[contains(., '주상품 조회')]"));

	}



	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉          ▉▉▉▉▉▉▉              ▉▉▉▉           ▉▉▉▉▉             ▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉             ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉             ▉▉▉▉▉           ▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉               ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉    ▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉  ▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉          ▉▉▉▉▉▉▉              ▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉  ▉▉▉             ▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉

	// 각종 프로세스, 고정경로 등 지정
	// UI와 가능한 동일하게 진행되는 것이원칙



	// 공시실 | 상품검색
	protected void searchProdByTitle(String title) throws Exception  {
// todo | window 창 전환에 대한 스터디(?) 필요...
		try {
			driver.findElement(By.xpath("//span[text()='전체열기']")).click();
			String currentHandle = driver.getWindowHandle();

			driver.findElement(By.xpath("//span[text()='" + title + "']/ancestor::tr//button")).click();
			WaitUtil.waitFor(3);
			helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
			logger.info("▉▉ 상품({}) 검색 완료 ", title);
			WaitUtil.waitFor(2);

		} catch(NoSuchElementException e) {
			throw new CommonCrawlerException("해당 보험이 존재하지 않습니다");
		}
	}



	// 공시실 | 고객정보를 입력해주세요
	protected void inputCustomerInfo(String name, String birth, String gender) throws Exception {

		logger.info("▉▉ 이름 입력");
		inputCustomerName(name);

		logger.info("▉▉ 생년월일 입력");
		inputCustomerBirthday(birth);

		logger.info("▉▉ 성별 입력");
		inputCustomerGender(gender);

		logger.info("▉▉ '주상품 조회' 버튼 클릭");
		pushButton("주상품 조회");
	}



	// 공시실 | 주상품을 선택해주세요
	protected void inputMainTretyInfo(String mainTreatyName) throws Exception {
		logger.info("▉▉ ========================================================================== ");
		logger.info("▉▉ 보험 종류 선택");
		logger.info("▉▉ CrwalingProduct의 insuName을 참조합니다 ");
		logger.info("▉▉ CrwalingProduct의 insuName과 다른경우, 지정 이름으로 검색합니다(hardcoding).. ");
		logger.info("▉▉ ========================================================================== ");
		WaitUtil.waitFor(3);
		selectMainPlanType(mainTreatyName);

		logger.info("▉▉ '특약 조회' 버튼 클릭");
		pushButton("특약 조회");
	}



	// 공시실 | 특약을 선택해주세요
	protected void inputSubTreatyInfo(List<CrawlingTreaty> trtList) throws Exception {

		// 특약개수 1개 == 주계약만 있는 경우
		try {
			// 주계약만 있는 경우
			if(trtList.size() == 1) {
// todo | 미구현.. test이후 수정필요
				List<WebElement> firstTds = driver.findElements(By.xpath("//tbody[@id='step3_tbody1']//tr[1]//td"));

				for(WebElement eachTd : firstTds) {
					if(trtList.get(0).productGubun.equals(ProductGubun.주계약)) {
						logger.info("MAIN_TREATY_NAME :: {}", trtList.get(0).treatyName);

						submitSubTreaties(eachTd, trtList.get(0));

						logger.info("주계약 내용 설정 완료");
					}
				}
			}
		} catch(Exception e) {
			throw new CommonCrawlerException("주계약 설정 중 에러가 발생하였습니다");
		}

		// 특약개수 여러개 == 선택특약이 있는 경우
		try {
			if(trtList.size() > 1) {
				logger.info("▉▉ ===================================== ");
				logger.info("▉▉ ===================================== ");
				logger.info("▉▉ 특약이 2개이상인 경우입니다 :: {}", trtList.size());
				logger.info("▉▉ ===================================== ");
				logger.info("▉▉ ===================================== ");

				List<WebElement> trList = driver.findElements(By.xpath("//tbody[@id='step3_tbody1']//tr"));
				for(WebElement eachTr : trList) {
					String eachTrName = eachTr.findElement(By.xpath(".//label")).getText();
					logger.info("▉▉ TR NAME 	:: {}", eachTrName);
					for(CrawlingTreaty eachTrt : trtList) {
						String trtName = eachTrt.treatyName;

						if(eachTrt.productGubun.equals(ProductGubun.주계약)) {
							if(eachTrName.contains(trtName)) {
								logger.info("▉▉ MAIN TREATY NAME	:: {}", eachTrName);
								logger.info("▉▉ ========================================================================== ");
								logger.info("▉▉ 주계약에 대한 작업을 진행합니다");
// todo | 사용여부 선택상태를 조정할수 없는 경우(disabled) > 확인후 > 어떻게 처리할지 정할 필요 있음
								logger.info("▉▉ 사용여부를 수정할 수 없습니다 (default :: 선택상태)");

								submitSubTreaties(eachTr, eachTrt);	// 보험기간, 납입기간, 납이주기, 가입금액 설정
								logger.info("▉▉ 주계약 내용 설정완료");
							}
						}

						if(eachTrt.productGubun.equals(ProductGubun.선택특약)) {
							if(eachTrName.contains(trtName)) {
								logger.info("▉▉ SUB TREATY NAME 	:: {}", eachTrName);
								logger.info("▉▉ ========================================================================== ");
								logger.info("▉▉ 선택특약에 대한 작업을 진행합니다");

								eachTr.findElement(By.xpath(".//input[1]")).click();
								logger.info("▉▉ '" + trtName + "'를 체크하였습니다 (선택완료)");

								submitSubTreaties(eachTr, eachTrt);	// 보험기간, 납입기간, 납이주기, 가입금액 설정
								logger.info("▉▉ 선택특약 내용 설정완료");
							}
						}
					}
					logger.info("▉▉ ========================================================================== ");
				}
			}
		} catch(Exception e) {
			throw new CommonCrawlerException("특약 설정 중 에러가 발생하였습니다");
		}
	}



	// 공시실 | 특수(연금_저축)
	protected void inputTreatiesInfoASV(CrawlingProduct info) throws Exception {
		try {
			CrawlingTreaty treaty = info.getTreatyList().get(0);
			WebElement tr = driver.findElement(By.xpath("//tbody[@id='step3_tbody1']//tr"));
			if(ProductGubun.주계약.equals(treaty.productGubun)) {
				String prdName = tr.findElement(By.xpath(".//label")).getText().trim();
				if(prdName.contains(treaty.treatyName)) {
					submitSubTreaties(tr, treaty);
				} else {
					throw new CommonCrawlerException("주계약 명이 잘못 설정되었습니다. 확인이 필요합니다");		// >> todo | 차후 '규칙'통한 코드 줄일수 있을듯..
				}

			} else {
				throw new CommonCrawlerException("첫번째 계약이 주계약이 아닙니다. 확인이 필요합니다");
			}

		} catch(Exception e) {
			throw new CommonCrawlerException("특약내용 설정중 에러가 발생하였습니다");
		}
	}



	// 공시실 | 보험료 계산
	protected void calculatePremium() throws Exception {
		try {
			driver.findElement(By.xpath("//span[text()='보험료 계산']//parent::button")).click();
			logger.info("▉▉ '보험료 계산' 버튼을 클릭하였습니다");
			WaitUtil.waitFor(3);

		} catch(Exception e) {
			throw new CommonCrawlerException("'보험료 계산' 버튼 클릭시 에러가 발생하였습니다");
		}
	}



	// 공시실 | 보험료계산 결과
	protected  void checkMonthlyPremium(CrawlingProduct info) throws Exception {

		CrawlingTreaty mainTreaty = info.getTreatyList().get(0);

		// 보험료계산 버튼
		logger.info("▉▉ '보험료 계산'버튼을 클릭합니다(1/3)");
		pushButton("보험료 계산");

		// 합계보험료
		logger.info("▉▉ 합계보험료를 확인합니다(2/3)");
		noteMonthlyPremium(mainTreaty);

		// 화면이동 & 스크린 샷
		logger.info("▉▉ 스크린샷을 촬영합니다(3/3)");
		twinkleScreenShot(info);
	}



	// 공시실 | 보장내용 상세보기 (일반)
	protected void checkDetails(CrawlingProduct info, String opt) throws Exception {

		logger.info("▉▉ 보장내용상세보기 버튼 클릭");
		pushButton("보장내용상세보기");

		logger.info("▉▉ 해약환급금을 확인합니다");
		String key = info.productKind + "_" + opt;
		switch (key) {
			case "순수보장형_BASE" :
				logger.info("순수보장형_BASE 해약환급금 스크래핑 시작");
				checkReturnMoneyPGT(info);
				break;

			case "순수보장형_FULL" :
				logger.info("순수보장형_FULL 해약환급금 스크래핑 시작");
				checkReturnMoneyPGTFull(info);
				break;

			case "만기환급형_BASE":
				logger.info("만기환급형_BASE 해약환급금 스크래핑 시작");
// todo | 만기환급형_BASE 해약환급금 크롤링코드 추가필요
				checkReturnMoneyMRT(info);
				break;

			case "만기환급형_FULL":
				logger.info("만기환급형_FULL 해약환급금 스크래핑 시작");
// todo | 만기환급형_FULL 해약환급금 크롤링코드 추가필요
				checkReturnMoneyMRTFUll(info);
				break;

			case "순수보장형_SPCL01":
				logger.info("해약환급금 특수케이스");
				checkReturnMoneySPCL01(info);
		}

		logger.info("getAnnuityType :: {}", info.getAnnuityType());
		if(!info.getAnnuityType().equals("")) {
			logger.info("▉▉ 연금_저축보험의 경우, 연금에 대한 내용을 스크래핑합니다.");
			if("종신 10년".equals(info.getAnnuityType())) {
				checkAnnuityMoney(info);
			} else if("확정 10년".equals(info.getAnnuityType())) {
				checkAnnuityMoney(info);
			}
		} else {
			logger.info("checkDetails()를 마무리합니다");
		}

	}



	// 공시실 | 보장내용 상세보기 (연금저축보험)
	protected void checkDetailsASV(CrawlingProduct info, String opt) throws Exception {
		logger.info("▉▉ 보장내용상세보기 버튼 클릭");
		pushButton("보장내용상세보기");

		if("FULL".equals(opt)) {

		}
	}




	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉          ▉▉▉▉▉▉▉              ▉▉▉▉           ▉▉▉▉▉             ▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉             ▉▉▉▉▉           ▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉               ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉▉   ▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉▉    ▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉   ▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉  ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉          ▉▉▉▉▉▉▉              ▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉   ▉▉▉▉  ▉▉▉▉       ▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉


	// 바뀔필요없으면 여기서도 고정경로 사용, 아닌 경우 공통화위해서 params으로 받는 식으로 진행



	// DEPTH.1 공시실 | 고객정보를 입력해주세요 | 이름 입력
	protected void inputCustomerName(String name) throws Exception {
		try {
			driver.findElement(By.xpath("//input[@id='name_21']")).sendKeys(name);
			logger.info("▉ 이름 :: '{}'을 입력하였습니다", name);
			WaitUtil.waitFor(2);

		} catch(Exception e) {
			throw new CommonCrawlerException("▉ 고객정보(이름)입력중 에러가 발생하였습니다");
		}
	}



	// DEPTH.1 공시실 | 고객정보를 입력해주세요 | 생년월일 입력
	protected void inputCustomerBirthday(String birth) throws Exception {
		try {
			String strYear = birth.substring(0,4);
			String strMonth = birth.substring(4, 6);
			String strDay = birth.substring(6, 8);
			driver.findElement(By.xpath("//input[@id='birthday_Y_21']")).sendKeys(strYear);
			driver.findElement(By.xpath("//input[@id='birthday_M_21']")).sendKeys(strMonth);
			driver.findElement(By.xpath("//input[@id='birthday_D_21']")).sendKeys(strDay);
			logger.info("▉ 생년월일 :: {}년 {}월 {}일", strYear, strMonth, strDay);
			WaitUtil.waitFor(2);

		} catch(Exception e) {
			throw new CommonCrawlerException("▉ 고객정보(생년월일)입력중 에러가 발생하였습니다");
		}
	}



	// DEPTH.1 공시실 | 고객정보를 입력해주세요 | 성별 선택
	protected void inputCustomerGender(String gender) throws Exception {
		try {
			if(gender.equals("남자")) {
				driver.findElement(By.xpath("//input[@id='sexM_21']")).click();
			} else {
				driver.findElement(By.xpath("//input[@id='sexF_21']")).click();
			}
			logger.info("▉ 성별 :: '{}'를 설정하였습니다", gender);
			WaitUtil.waitFor(2);

		} catch(Exception e) {
			throw new CommonCrawlerException("▉ 고객정보(성별)입력중 에러가 발생하였습니다");
		}
	}



	// DEPTH.1 공시실 | 고객정보를 입력해 주세요 | 버튼클릭(공통)
	protected void pushButton(String btnName) throws Exception {
		try {
			driver.findElement(By.xpath("//span[text()='" + btnName + "']//parent::button")).click();
			logger.info("▉ '{}' 버튼 클릭!", btnName);
			WaitUtil.waitFor(3);

		} catch(Exception e) {
			throw new CommonCrawlerException("▉ 버튼클릭시 에러 :: ");
		}
	}



	// DEPTH.1 공시실 | 주상품을 선택해 주세요 | 보험종류 선택
	protected void selectMainPlanType(String mainTreatyName) throws Exception {
		try {
			Select selectMainTreaty = new Select(driver.findElement(By.xpath("//select[@id='policycd_l']")));
			selectMainTreaty.selectByVisibleText(mainTreatyName);
			logger.info("▉ 보험종류 :: '{}'를 선택하였습니다", mainTreatyName);
			WaitUtil.waitFor(2);

		} catch(Exception e) {
			String msg= "▉ SELECT(" + mainTreatyName + ")시 에러 발생";
			throw new CommonCrawlerException(msg);
		}
	}


	//  DEPTH.1 공시실 | 특약을 선택해주세요 | 특약들에 대한 조건 설정
	protected void submitSubTreaties(WebElement eachTr, CrawlingTreaty eachTrt) throws Exception {
		String trtInsTerm = eachTrt.getInsTerm();
		String trtNapTerm = eachTrt.getNapTerm();
		String trtNapTermUnit = eachTrt.getNapCycleName();
		String trtAssureAmt = String.valueOf(eachTrt.assureMoney / 1_0000);
		String trtAnnAge = eachTrt.annAge;

		logger.info("trtInsTerm 		:: {}", trtInsTerm);
		logger.info("trtNapTerm 		:: {}", trtNapTerm);
		logger.info("trtNapTermUnit 	:: {}", trtNapTermUnit);
		logger.info("trtAssureAmt 		:: {}", trtAssureAmt);
		logger.info("trtAnnAge 		:: {}", trtAnnAge);

		try {
			// 보험기간

			Select selectInsTerm = new Select(eachTr.findElement(By.xpath(".//td[4]//select")));
			// 2022.11.25 | 최우진 | 동양생명 대면연금저축의 보험기간과 연금개시나이 관계
			// 동양생명 대면_연금-저축보험에서 '종신(확정)xx년'을 크롤링하는 경우, ui상 '보험기간'입력칸에 '연금개시나이'를 지정합니다
			// 해당내용은 동양생명 연금-저축보험의 특이사항으로 변동시 확인이 필요합니다.
// todo | 수정필수 annuitytype 으로 수정필수
//			selectInsTerm.selectByVisibleText(trtAnnAge + "세");


			if(!trtInsTerm.equals("종신보장")) {
				selectInsTerm.selectByVisibleText(trtInsTerm);

			} else {
				selectInsTerm.selectByVisibleText(trtAnnAge + "세");
			}


			logger.info("▉ 보험기간 :: '{}'(으)로 설정하였습니다", trtInsTerm);

			// 납입기간
			Select selectNapTerm = new Select(eachTr.findElement(By.xpath(".//td[5]//select")));
			selectNapTerm.selectByVisibleText(trtNapTerm);
			logger.info("▉ 납입기간 :: '{}'(으)로 설정하였습니다", trtNapTerm);

			// 납입주기
			Select selctNapCycleName = new Select(eachTr.findElement(By.xpath(".//td[6]//select")));
			selctNapCycleName.selectByVisibleText(trtNapTermUnit);
			logger.info("▉ 납입주기 :: '{}'(으)로 설정하였습니다", trtNapTermUnit);

			// 가입금액
// todo | 심각한 수정필요 TYL 공통화는 모듈화 다하고서 할 것
			if(!trtInsTerm.equals("종신보장") && StringUtils.isEmpty(trtAnnAge)) {
				WebElement inputAssureAmt = eachTr.findElement(By.xpath(".//td[7]//input"));
				inputAssureAmt.click();
				inputAssureAmt.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
				inputAssureAmt.sendKeys(trtAssureAmt);
				logger.info("▉ 가입금액 :: '{}만원' 으로 설정하였습니다", trtAssureAmt);
				
			} else {
				trtAssureAmt = String.valueOf(Integer.parseInt(trtAssureAmt) * 1_0000);
				WebElement inputPremium = eachTr.findElement(By.xpath(".//td[8]//input"));
				inputPremium.click();
				inputPremium.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
				inputPremium.sendKeys(trtAssureAmt);
				logger.info("▉ 보험료 :: '{}원' 으로 설정하였습니다", trtAssureAmt);
				logger.info("▉ 연금_저축의 경우, 현 구조상 가입금액(input)을 보험료(output)를 대신해 해당되는 내용을 입력(input)합니다.");
			}
			WaitUtil.waitFor(1);

		} catch(Exception e) {
			throw new CommonCrawlerException("▉ 특약 옵션 설정중 에러가 발생하였습니다");
		}
	}


	
	// D1 공시실 | 보험료 계산결과 | 보험료 결과 확인
	protected void noteMonthlyPremium(CrawlingTreaty mainTreaty) throws  Exception {
		String monthlyPremium;
		try {
			WaitUtil.waitFor(3);
			((JavascriptExecutor) driver).executeScript("scrollTo(0, document.body.scrollHeight);");
			element = driver.findElement(By.xpath("//th[contains(., '합계보험료')]/parent::tr//span[@class='point1']"));
			monthlyPremium = element.getText().replaceAll("[^0-9]", "");

			logger.info("▉ '합계보험료'로 월보험료 스크래핑");

		} catch(Exception e) {
			throw new CommonCrawlerException("합계보험료 확인중 에러가 발생하였습니다");
		}

		mainTreaty.monthlyPremium = monthlyPremium;
		logger.info("▉ 합계보험료 : {}원", mainTreaty.monthlyPremium);

		WaitUtil.waitFor(2);
	}



	// D1 공시실 | 화면이동 & 스크린 샷 | 스크린샷 찍을 위치로 화면 이동 & 스크린샷
	protected void twinkleScreenShot(CrawlingProduct info) throws Exception {
// todo | 필요한 경우 스크롤위치 정할수 있도록 (현재는 무조건 fulldown)
		try {
			((JavascriptExecutor) driver).executeScript("scrollTo(0, document.body.scrollHeight);");
			logger.info("▉ 촬영위한 화면이동");

			takeScreenShot(info);
			logger.info("▉ 스크린샷 완료");

		} catch(Exception e) {
			throw new CommonCrawlerException("스크린 샷 촬영중 에러가 발생하였습니다");
		}
	}



	// D1 공시실 | 보장내용 상세보기 | [순수보장형]_[BASE] 해약환급금 예시표 확인
	protected void checkReturnMoneyPGT(CrawlingProduct info) throws Exception {
		int unit = 1;
		try {
			String unitText = driver.findElement(By.cssSelector(".mb5 .t_right")).getText();
			if (unitText.contains("만원")) {
				unit = 1_0000;
			}
		} catch(Exception e) {
			throw new CommonCrawlerException("잘못된 금액단위설정입니다");
		}

		try {
			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
			List<WebElement> trList = driver.findElements(By.cssSelector(".tblCol.tableCyber tbody tr"));
			for (WebElement tr : trList) {
				String term = tr.findElements(By.tagName("td")).get(0).getText().trim();
				String premiumSum = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "").trim();
				String returnMoney = tr.findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]", "").trim();
				String returnRate = tr.findElements(By.tagName("td")).get(4).getText().trim();

				//공시실 해약환급금 테이블의 단위가 만원일 경우 단위를 맞춰준다.
				premiumSum = String.valueOf(Integer.parseInt(premiumSum) * unit);
				returnMoney = String.valueOf(Integer.parseInt(returnMoney) * unit);

				logger.info("경과기간: {}", term);
				logger.info("납입보험료: {}", premiumSum);
				logger.info("해약환급금: {}", returnMoney);
//				logger.info("최저납입보험료: {}", premiumSum);
				logger.info("환급률: {}", returnRate);
				logger.info("_____________________");

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();

				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
				planReturnMoney.setInsAge(Integer.parseInt(info.age));
				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);

				planReturnMoneyList.add(planReturnMoney);

				info.setReturnPremium(returnMoney);
			}
			logger.info("▉ 더이상 참조할 차트가 존재하지 않습니다");
			logger.info("_____________________");

			if(ProductKind.순수보장형 == info.getTreatyList().get(0).productKind) {
				logger.info("▉ 순수보장형 상품의 경우 만기환급금이 존재하지 않습니다");
				info.returnPremium = "0";
			}
			logger.info("▉ 만기환급금 :: '{}원'", info.returnPremium);

			info.setPlanReturnMoneyList(planReturnMoneyList);		// 해약환급금
			logger.info("▉ 해약환급금 스크래핑 완료");

		} catch(Exception e) {
			throw new CommonCrawlerException("해약환급금 크롤링 중 에러가 발생하였습니다");
		}

	}



	protected void checkReturnMoneySPCL01(CrawlingProduct info) throws Exception {
		int unit = 1;
		try {
			String unitText = driver.findElement(By.cssSelector(".mb5 .t_right")).getText();
			if (unitText.contains("만원")) {
				unit = 1_0000;
			}
		} catch(Exception e) {
			throw new CommonCrawlerException("잘못된 금액단위설정입니다");
		}

		try {
			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
			List<WebElement> trList = driver.findElements(By.cssSelector(".tblCol.tableCyber tbody tr"));
			for (WebElement tr : trList) {
				String term = tr.findElements(By.tagName("td")).get(0).getText().trim();
				String premiumSum = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "").trim();
				String returnMoney = tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "").trim();
				String returnRate = tr.findElements(By.tagName("td")).get(7).getText().trim();

				//공시실 해약환급금 테이블의 단위가 만원일 경우 단위를 맞춰준다.
				premiumSum = String.valueOf(Integer.parseInt(premiumSum) * unit);
				returnMoney = String.valueOf(Integer.parseInt(returnMoney) * unit);

				logger.info("경과기간: {}", term);
				logger.info("납입보험료: {}", premiumSum);
				logger.info("해약환급금: {}", returnMoney);
				logger.info("환급률: {}", returnRate);
				logger.info("_____________________");

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();

				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
				planReturnMoney.setInsAge(Integer.parseInt(info.age));
				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);

				planReturnMoneyList.add(planReturnMoney);

				info.setReturnPremium(returnMoney);
			}
			logger.info("▉ 더이상 참조할 차트가 존재하지 않습니다");
			logger.info("_____________________");

			if(ProductKind.순수보장형 == info.getTreatyList().get(0).productKind) {
				logger.info("▉ 순수보장형 상품의 경우 만기환급금이 존재하지 않습니다");
				info.returnPremium = "0";
			}
			logger.info("▉ 만기환급금 :: '{}원'", info.returnPremium);

			info.setPlanReturnMoneyList(planReturnMoneyList);		// 해약환급금
			logger.info("▉ 해약환급금 스크래핑 완료");

		} catch(Exception e) {
			throw new CommonCrawlerException("해약환급금 크롤링 중 에러가 발생하였습니다");
		}

	}



	// D! 공시실 | 보장내용 상세보기 | [순수보장형]_[BASE] 해약환급금 예시표 확인
	protected void checkReturnMoneyPGTFull(CrawlingProduct info) {

	}



	// 공시실 | 보장내용 상세보기 | 만기환급형_BASE 해약환급금 예시표 확인
	protected void checkReturnMoneyMRT(CrawlingProduct info) {

	}



	// 공시실 | 보장내용 상세보기 | 만기환급형_FULL 해약환급금 예시표 확인
	protected void checkReturnMoneyMRTFUll(CrawlingProduct info) throws Exception {
		int unit = 1;
		try {
			String unitText = driver.findElement(By.cssSelector(".mb5 .t_right")).getText();
			if (unitText.contains("만원")) {
				unit = 1_0000;
			}
		} catch(Exception e) {
			throw new CommonCrawlerException("잘못된 금액단위설정입니다");
		}

		try {
			List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
			List<WebElement> trList = driver.findElements(By.xpath("//caption[text()='해약환급금 예시표']//parent::table/tbody/tr"));
			for (WebElement tr : trList) {
				String term = tr.findElements(By.tagName("td")).get(0).getText().trim();
				String premiumSum = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "").trim();
				String minReturnMoney = tr.findElements(By.tagName("td")).get(3).getText().replaceAll("[^0-9]", "").trim();
				String minReturnRate = tr.findElements(By.tagName("td")).get(4).getText().trim();
				String avgReturnMoney = tr.findElements(By.tagName("td")).get(5).getText().replaceAll("[^0-9]", "").trim();
				String avgReturnRate = tr.findElements(By.tagName("td")).get(6).getText().trim();
				String returnMoney = tr.findElements(By.tagName("td")).get(7).getText().replaceAll("[^0-9]", "").trim();
				String returnRate = tr.findElements(By.tagName("td")).get(8).getText().trim();

				//공시실 해약환급금 테이블의 단위가 만원일 경우 단위를 맞춰준다.
				premiumSum = String.valueOf(Integer.parseInt(premiumSum) * unit);
				returnMoney = String.valueOf(Integer.parseInt(returnMoney) * unit);
				minReturnMoney = String.valueOf(Integer.parseInt(minReturnMoney) * unit);
				avgReturnMoney = String.valueOf(Integer.parseInt(avgReturnMoney) * unit);

				logger.info("경과기간      : {}", term);
				logger.info("납입보험료    : {}", premiumSum);
				logger.info("일반해약환급금 : {}", returnMoney);
				logger.info("일반환급률    : {}", returnRate);
				logger.info("최소해약환급금 : {}", minReturnMoney);
				logger.info("최소해약환금률 : {}", minReturnRate);
				logger.info("평균해약환급금 : {}", avgReturnMoney);
				logger.info("평균해약환급률 : {}", avgReturnRate);
				logger.info("====================================");

				PlanReturnMoney prm = new PlanReturnMoney();
				prm.setPlanId(Integer.parseInt(info.planId));
				prm.setGender((info.gender == MALE) ? "M" : "F");
				prm.setInsAge(Integer.parseInt(info.age));
				prm.setTerm(term);
				prm.setPremiumSum(premiumSum);
				prm.setReturnMoney(returnMoney);
				prm.setReturnRate(returnRate);
				prm.setReturnMoneyMin(minReturnMoney);
				prm.setReturnRateMin(minReturnRate);
				prm.setReturnMoneyAvg(avgReturnMoney);
				prm.setReturnRateAvg(avgReturnRate);

				planReturnMoneyList.add(prm);

				info.setReturnPremium(returnMoney);
			}
			logger.info("▉ 더이상 참조할 차트가 존재하지 않습니다");
			logger.info("▉ _____________________");

			logger.info("▉ 해약환급금 스크래핑 완료");
			info.setPlanReturnMoneyList(planReturnMoneyList);		// 해약환급금

			logger.info("▉ 만기환급금 확인 :: {}", info.getReturnPremium());

		} catch(Exception e) {
			throw new CommonCrawlerException("해약환급금 크롤링 중 에러가 발생하였습니다");
		}
	}



	protected void checkAnnuityMoney(CrawlingProduct info) throws Exception {
		try {
			PlanAnnuityMoney pam = new PlanAnnuityMoney();
			List<WebElement> trList = driver.findElements(By.xpath("//caption[text()='연금 예시표 - 정액형']//parent::table/tbody/tr"));
			String fxdClue = "년형";
			String whlClue = "년보증";
			for(int i = 1; i < trList.size(); i++) {
				WebElement tr = trList.get(i);
				String tempAmt = String.valueOf(Integer.parseInt(tr.findElement(By.xpath(".//td[10]")).getText().replaceAll("[^0-9]", "")) * 1_0000);
				String tempTitle =  "";
				if(i <= 6) {
					tempTitle = tr.findElement(By.xpath(".//th[contains(., '" + fxdClue + "')]")).getText();
				} else {
					tempTitle = tr.findElement(By.xpath(".//th[contains(., '" + whlClue + "')]")).getText();
				}

				// 확정 FXD
				if(tempTitle.equals("10년형")) {
					pam.setFxd10Y(tempAmt);
				}
//				else if(tempTitle.equals("15년형")) {
//					pam.setFxd15Y(tempAmt);
//				} else if(tempTitle.equals("20년형")) {
//					pam.setFxd20Y(tempAmt);
//				} else if(tempTitle.equals("25년형")) {
//					pam.setFxd25Y(tempAmt);
//				} else if(tempTitle.equals("30년형")) {
//					pam.setFxd30Y(tempAmt);
//				}
				// 종신 WHL
				if(tempTitle.equals("10년보증")) {
					pam.setWhl10Y(tempAmt);
				}
//				else if(tempTitle.equals("20년보증")) {
//					pam.setWhl20Y(tempAmt);
//				} else if(tempTitle.equals("30년보증")) {
//					pam.setWhl30Y(tempAmt);
//				}

				if("확정 10년".equals(info.getAnnuityType())) {
					info.setFixedAnnuityPremium(pam.getFxd10Y());
				} else {
					info.setAnnuityPremium(pam.getWhl10Y());
				}
			}
			info.setPlanAnnuityMoney(pam);

			logger.info("▉ < Annuity Info >");
			logger.info("▉ INFO WHL ANNUITY 종신 :: {}", (!StringUtils.isEmpty(info.getAnnuityPremium())) ? info.getAnnuityPremium() : "0");
			logger.info("▉ INFO FXD ANNUITY 확정 :: {}", (!StringUtils.isEmpty(info.getFixedAnnuityPremium())) ? info.getFixedAnnuityPremium() : "0");

			logger.info("▉ PAM WHL 10 :: {}", info.getPlanAnnuityMoney().getWhl10Y());
//			logger.info("▉ PAM WHL 20 :: {}", info.getPlanAnnuityMoney().getWhl20Y());
//			logger.info("▉ PAM WHL 30 :: {}", info.getPlanAnnuityMoney().getWhl30Y());

			logger.info("▉ PAM FXD 10 :: {}", info.getPlanAnnuityMoney().getFxd10Y());
//			logger.info("▉ PAM FXD 15 :: {}", info.getPlanAnnuityMoney().getFxd15Y());
//			logger.info("▉ PAM FXD 20 :: {}", info.getPlanAnnuityMoney().getFxd20Y());
//			logger.info("▉ PAM FXD 25 :: {}", info.getPlanAnnuityMoney().getFxd25Y());
//			logger.info("▉ PAM FXD 30 :: {}", info.getPlanAnnuityMoney().getFxd30Y());

			logger.info("===========================================");

		} catch(Exception e) {
			throw new CommonCrawlerException("연금확인중 에러가 발생하였습니다");
		}
	}
}
