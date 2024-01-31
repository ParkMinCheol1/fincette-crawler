package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * 2020.11.23
 * @author 조하연
 * TYL 상품 홈페이지용 클래스
 */



//동양생명 상품 중 홈페이지에서 크롤링해오는 상품에 대해서는 HomepageCrawlingTYL을 상속받는다.
public abstract class HomepageCrawlingTYL extends SeleniumCrawler {

//	//크롤링 옵션 정의 메서드(홈페이지, 공시실 공용)
//	protected void setChromeOptionTYL(CrawlingProduct info) {
//		CrawlingOption option = info.getCrawlingOption();
//
//		option.setBrowserType(CrawlingOption.BrowserType.Chrome);
//		option.setImageLoad(false);
//		option.setUserData(false);
//
//		info.setCrawlingOption(option);
//	}

	//특정 element가 존재하는지 여부를 리턴하는 메서드
	protected boolean existElement(By element) {
		try{
			driver.findElement(element);
		}catch(NoSuchElementException e) {
			return false;
		}
		return true;
	}

	//li태그 중 text가 일치하는 li 선택하는 메서드
	protected void selectLi(By liList, String text) throws Exception{
		List<WebElement> elements = driver.findElements(liList);
		boolean isSelected = false;

		for(WebElement li : elements) {
			if(li.getText().trim().equals(text)) {
				li.click();
				isSelected = true;
				li.click();
				break;
			}
		}

		if(!isSelected) {
			throw new Exception("목록 중 " + text + "이(가) 존재하지 않습니다.");
		}
	}

	//select 박스에서 text로 option 선택하는 메서드
	protected void selectOption(By element, String text) {
		WebElement selectEl =  driver.findElement(element);
		selectOption(selectEl, text);
	}

	//select 박스에서 text로 option 선택하는 메서드
	protected void selectOption(WebElement selectEl, String text) {
		Select select = new Select(selectEl);
		select.selectByVisibleText(text);
	}

	//버튼 클릭 메서드(홈페이지, 공시실 공용)
	protected void btnClick(By element) throws  Exception {
		driver.findElement(element).click();
	}

	//홈페이지용 버튼 클릭 메서드(홈페이지용 명시적 대기 코드가 추가돼있음)
	protected void homepageBtnClick(By element) throws Exception{
		btnClick(element);
		waitHomepageLoadingImg();
		WaitUtil.waitFor(2);
	}

	//홈페이지용 버튼 클릭 메서드(홈페이지용 명시적 대기 코드가 추가돼있음)
	protected void homepageBtnClick(WebElement element) throws Exception{
		element.click();
		waitHomepageLoadingImg();
		WaitUtil.waitFor(2);
	}

	//홈페이지용 로딩이미지 명시적 대기
	protected void waitHomepageLoadingImg() {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("prograss-document-bar")));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("prograss-bar")));
	}

	//inputBox에 텍스트를 입력하는 메서드
	protected void setTextToInputBox(By element, String text) {
		WebElement inputBox = driver.findElement(element);
		inputBox.click();
		inputBox.clear();
		inputBox.sendKeys(text);
	}

	//홈페이지용 성별 설정 메서드
	protected void setHomepageGender(int gender) throws Exception{
		String genderText = (gender == MALE) ? "남자" : "여자";
		btnClick(By.xpath("//label[contains(., '" + genderText + "')]"));
//		homepageBtnClick(waitPresenceOfElementLocated(By.xpath("//label[contains(., '" + genderText + "')]")));
	}

	//홈페이지용 성별 설정 메서드(부모, 자녀 2개 성별 입력)
	protected void setHomepageGender(int childGender, int parentGender) throws Exception{
		String childGenderText = (childGender == MALE) ? "남아" : "여아";

		logger.info("부모 성별 설정");
		setHomepageGender(parentGender);

		logger.info("자녀 성별 설정");
		btnClick(By.xpath("//label[contains(., '" + childGenderText + "')]"));
//		homepageBtnClick(By.xpath("//label[contains(., '" + childGenderText + "')]"));
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
		String _assureMoney = String.valueOf(Integer.parseInt(info.assureMoney) / 10000);
		info.treatyList.get(0).monthlyPremium = info.assureMoney;
		setTextToInputBox(element, _assureMoney);
	}


	//홈페이지용 보험기간 설정 메서드
	protected void setHomepageInsTerm(String insTerm) throws Exception{
//		btnClick(By.id("TRMINS-button"));
//		selectLi(By.cssSelector("#ui-selectmenu-menu-TRMINS li"), insTerm);
		selectOption(By.id("TRMINS"), insTerm);
	}


	//홈페이지용 납입기간 설정 메서드
	protected void setHomepageNapTerm(String napTerm) throws Exception{
//		homepageBtnClick(By.id("PYPD-button"));
//		selectLi(By.cssSelector("#ui-selectmenu-menu-PYPD li"), napTerm);
		selectOption(By.id("PYPD"), napTerm);
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

		btnClick(By.cssSelector(".ui-dialog-titlebar-close"));
	}


	//홈페이지용 해약환급금 조회 및 세팅 메서드(경과기간, 납입보험료, 최저.평균.공시 정보 모두 나온 경우 사용)
	protected void getHomepageFullReturnPremiums(CrawlingProduct info) throws Exception{
		homepageBtnClick(By.id("btn_srdrrf_popup"));		//해약환급금 버튼 클릭

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
}

