package com.welgram.crawler.direct.fire;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;




public abstract class CrawlingCRF extends SeleniumCrawler {

	// 생년월일
	protected void setBirth(CrawlingProduct info) throws Exception {
		String yyyy = info.getFullBirth().substring(0,4);
		String mm = info.getFullBirth().substring(4,6);
		String dd = info.getFullBirth().substring(6,8);
		
		// 년도 select tag
		Select year = new Select(helper.waitPresenceOfElementLocated(By.cssSelector("select[id^='Year']")));
		year.selectByVisibleText(yyyy);
		
		// 월 select tag
		Select month = new Select(helper.waitPresenceOfElementLocated(By.cssSelector("select[id^='Month'")));
		month.selectByVisibleText(mm);
		
		// 일 select tag
		Select day = new Select(helper.waitPresenceOfElementLocated(By.cssSelector("select[id^='Day'")));
		day.selectByVisibleText(dd);

	}

	// 성별
	protected void setGender(By id, int gender) throws Exception {
		logger.info("성별선택");
		List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(id);
		for (WebElement radioBtn : radioBtns) {
			if (radioBtn.getAttribute("value").equals(Integer.toString(gender == MALE ? 1 : 2))) {
				radioBtn.click();
				logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
				helper.waitForCSSElement("#divFloatLoading");
			}
		}
	}

	//
	protected void selectBox(By id, String value) throws Exception {
		boolean result = false;
		elements = driver.findElement(id).findElements(By.tagName("option"));
		for (WebElement option : elements) {
			if (option.getText().trim().equals(value)) {
				option.click();
				result = true;
				WaitUtil.loading(2);
				break;
			}
		}
		if (!result) {
			throw new Exception("selectBox 선택 오류!");
		}
	}

	//
    protected void findForCssElement(String css) throws Exception {
		WaitUtil.loading(1);
    	for (int i = 0; i < 30; i++) {
			WaitUtil.loading(1);
			try {
				driver.findElement(By.cssSelector(".blockUI"));
				logger.info("로딩창이 있어요..");
			} catch (Exception e) {
				logger.info("####### 로딩 끝....");
				break;
			}
		}        
    }
    
	// 달력에서 날짜 클릭하기
	protected void selectDay(String day, String cssEl) throws Exception {
		elements = helper.waitVisibilityOfAllElementsLocatedBy((By.cssSelector(cssEl)));
		boolean isClicked = false;
		for (WebElement tr : elements) {
//			List<WebElement> buttons = tr.findElements(By.cssSelector("td > button"));
			List<WebElement> buttons = tr.findElements(By.xpath("//td[not(contains(@class, 'empty'))]/button")); // em 가능 여부
			for (WebElement button : buttons) {
				String buttonTxt = button.getText();
				if (buttonTxt.equals(day)) {

					logger.info("BUTTON TXT :: {}", buttonTxt);
					logger.info("DAY TXT :: {}", day);

					((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
					isClicked = true;
					break;
				}
			}
			if (isClicked == true) {
				break;
			}
		}
	}

	//특약 확인 (CRF_OST_D001)
	protected void treatyCheck(CrawlingProduct info) throws Exception {
		WaitUtil.waitFor(2);
		List<WebElement> homepageTreatyList = new ArrayList<>();
		homepageTreatyList = driver.findElements(By.xpath("//tbody//tr"));

		int scrollTop = 0;
		for(int i = 0; i<homepageTreatyList.size(); i++){
			boolean exist = false;
			String homepageTreaty = homepageTreatyList.get(i).findElement(By.cssSelector("th")).getText();
			element = driver.findElement(By.xpath("//*[text()='"+homepageTreaty+"']"));

			//((JavascriptExecutor) driver). executeScript("arguments[0].scrollIntoView(true);", element);
			WaitUtil.waitFor(1);

			for(int j = 0; j<info.treatyList.size(); j++){
				String planTreaty = info.treatyList.get(j).treatyName;
				if(homepageTreaty.contains(planTreaty)){
					exist = true;
					break;
				}
			}

			if(!exist){
				String unSubscribed = homepageTreatyList.get(i).findElement(By.cssSelector("td")).getText();

				if(!(unSubscribed.equals("미가입"))){
					homepageTreatyList.get(i).findElement(By.cssSelector("td > button")).click();
					WaitUtil.waitFor(2);
					driver.findElement(By.xpath("//*[@id='tooltipSideBar']//span[text()='미가입']")).click();
					WaitUtil.waitFor(2);
					checkPopup();
					//닫기
					driver.findElement(By.xpath("//*[@id='tooltipSideBar']/div/button")).click();
					WaitUtil.waitFor(2);
				}
			}

			scrollTop += 70;
			((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + scrollTop + ");");

		}
	}

	//알럿 표시
	protected void checkPopup() {
		try {
			if (driver.findElement(By.cssSelector(".ui-modal-wrap")).isDisplayed()) {
				logger.debug("알럿표시 확인!!!");
				driver.findElement(By.xpath("//div[@class='ui-modal-wrap']//button[contains(., '확인')]")).click();
				WaitUtil.waitFor(2);
			}
		} catch (Exception e) {
			logger.info("알럿표시 없음!!!");
		}
	}




	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉            ▉▉▉▉▉▉▉▉                ▉▉▉▉               ▉▉▉▉▉                   ▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉              ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉                    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉                ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉                 ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉

	// 각종 프로세스, 고정경로 등 지정
	// UI와 가능한 동일하게 진행되는 것이원칙
	// CRF의 경우, 화면의 전환을 단위로 scrap()프로세스를 구성합니다



	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉               ▉▉▉▉▉                   ▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉               ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉                    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉     ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
	// ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉

	// 공통역역 - 바뀔필요없으면 여기서도 고정경로 사용, 아닌 경우 공통화위해서 params으로 받는 식으로 진행



}
