package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DBF_OST_D003 extends CrawlingDBF {

	

	public static void main(String[] args) {
		executeCommand(new DBF_OST_D003(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		crawlFromHomepage(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setUserData(true);
	}


	private void crawlFromHomepage(CrawlingProduct info) throws Exception {

			// 개인형, 가족형 선택: 개인형 기본 선택됨

			// 생년월일
			helper.sendKeys3_check(By.id("birthday"), info.fullBirth);

			// 성별
			List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(By.name("sxCd"));
			for (WebElement radioBtn : radioBtns) {
				if (radioBtn.getAttribute("value")
						.equals(Integer.toString(info.getGender() == MALE ? 1 : 2))) {
					radioBtn.findElement(By.xpath("ancestor::label")).click();
					logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
					break;
				}
			}

			//	보험료 확인하기 버튼 클릭
			helper.click(By.linkText("보험료 확인하기"));
			logger.info("보험료 확인하기 클릭");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loadmask")));

			//$(".wrap_quick").hide()
			helper.executeJavascript("$(\".wrap_quick\").hide()");

			// 보험가입전 체크 3가지
			//helper.doClick(By.cssSelector("label[for='trvlChk1']"));
			//helper.doClick(By.cssSelector("label[for='trvlChk2']"));

			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("label[for='trvlChk1']"))).sendKeys(Keys.ENTER);
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("label[for='trvlChk1']"))).click();
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("label[for='trvlChk2']"))).sendKeys(Keys.ENTER);
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("label[for='trvlChk2']"))).click();


			// 다음버튼
			helper.click(By.id("btnNext"));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loadmask")));

			try {
				// <꼭 알아두세요> 모달창 확인 버튼 누르기
				helper.waitVisibilityOfElementLocated(By.id("popNoteJoinOvTrvl"));
				//helper.doClick(By.cssSelector("label[for='noteJoinChk']"));

				wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("label[for='noteJoinChk']"))).sendKeys(Keys.ENTER);
				wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("label[for='noteJoinChk']"))).click();

				//helper.doClick(By.id("btnNoteJoinChk"));
				wait.until(ExpectedConditions.elementToBeClickable(By.id("btnNoteJoinChk"))).click();
			} catch (Exception e){
				logger.info("모달창 확인 버튼 누르기 에러");
			}

			// 화면 하단에 설정 기본으로 놔두기.
			// 자기부담금 : 선택형으로 되어있음. (나중에 표준형 가입설계시 이부분 선택 가능하도록 추가 코딩해야합니다..^^; from 우정)
			// 의료수급권자 : 비대상으로 되어있음.

			OSTloopTreatyList(info, "01");
			WaitUtil.waitFor(1);
			reCompute();
			WaitUtil.waitFor(1);


			logger.info("보험료 가져오기");
			String premium = driver.findElement(By.id("totPrm")).getText().replaceAll("[^0-9]", "");
			info.treatyList.get(0).monthlyPremium = premium;
			logger.info("금액 : "+premium);

			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);
	}


	//다시계산하기 버튼이 있는경우 클릭 그외 바로 넘김
	protected void reCompute() throws Exception {
		element = driver.findElement(By.cssSelector("#sForm > div.wrap_contents > div.plan_total.type02 > div.right_plan_again > a"));

		if(element.isDisplayed()){
			logger.info("다시계산버튼클릭");
			element.click();
			helper.waitForCSSElement(".loadmask");
		}
		else{
			logger.info("다시계산 버튼 없음");
		}
		WaitUtil.waitFor(1);
	}



	//특약 loop
	protected void OSTloopTreatyList(CrawlingProduct info,String textTypeNumber) throws InterruptedException {

		List<String> webTreatyList = new ArrayList<>();
		List<String> apiTreatyList = new ArrayList<>();

		elements = helper.waitPresenceOfElementLocated(By.cssSelector("#sForm > div.wrap_contents > div.plan_wrap > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));
		for (WebElement treatyList : elements) {
			webTreatyList.add(treatyList.findElement(By.cssSelector("span")).getText());
		}

		for(CrawlingTreaty treatyList : info.treatyList){
			apiTreatyList.add(treatyList.treatyName);
		}

		for(CrawlingTreaty name : info.treatyList){
			String treatyNameSave = name.treatyName;
			String[] treatyNameSplitSave = treatyNameSave.split("- ");
		}


		webTreatyList.removeAll(apiTreatyList);

		// 특약 loop
		for (WebElement selectSignup : elements) {

			logger.info("특약체크 중....");

			if (selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("필수가입") || selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("")) {
				continue;
			}

			for (String treatyName : webTreatyList) {
				if (selectSignup.findElement(By.cssSelector("span")).getText().equals(treatyName)) {
					if (selectSignup.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box on")) {
						WebElement signupClick = selectSignup.findElement(By.cssSelector("ul > li.signup > a"));
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupClick);
						webTreatyList.remove(treatyName);
						WaitUtil.waitFor(1);
						break;
					}
				}else{
					/*if (selectSignup.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box")) {
						WebElement signupClick = selectSignup.findElement(By.cssSelector("ul > li.signup > a"));
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupClick);
					}*/
				}
			}
		}
	}
}
