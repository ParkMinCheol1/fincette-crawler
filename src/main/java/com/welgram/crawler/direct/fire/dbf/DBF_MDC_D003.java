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

public class DBF_MDC_D003 extends CrawlingDBF {		// 무배당 프로미라이프 다이렉트 실손의료비보험1904(CM)

	

	public static void main(String[] args) {
		executeCommand(new DBF_MDC_D003(), args);
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


	public void crawlFromHomepage(CrawlingProduct info) throws Exception {

			WaitUtil.waitFor(1);

			if(ExpectedConditions.alertIsPresent().apply(driver)!=null) {
				driver.switchTo().alert().accept();
			}
			WaitUtil.waitFor(1);

			logger.info("생년월일");
			setBirth(info.fullBirth);

			logger.info("성별");
			List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(By.name("sxCd"));
			for (WebElement radioBtn : radioBtns) {
				if (radioBtn.getAttribute("value")
						.equals(Integer.toString(info.getGender() == MALE ? 1 : 2))) {
					radioBtn.findElement(By.xpath("ancestor::label")).click();
					logger.info("성별 라디오 선택 여부" + radioBtn.isSelected());
					break;
				}
			}

			logger.info("보험료 확인하기 버튼 클릭");
			clickByLinkText("보험료 확인하기");

			// 2번째 페이지로 이동했음
			logger.info("라디오 버튼 클릭을 방해하는 요소 정리");
			helper.executeJavascript("$(\".wrap_quick\").hide()");

			// 운전형태 및 직업 선택
			element = driver.findElement(By.cssSelector("#oprtVhDvcd1"));
			element.click();
			logger.debug("운전형태 클릭");
			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(1);

			// 이륜자동차 선택
			element = driver.findElement(By.cssSelector("#mtccDrveYn2"));
			element.click();
			logger.debug("이륜차형태 클릭");
			helper.waitForCSSElement(".loadmask");
			WaitUtil.waitFor(1);


			// 직업 선택
			element = driver.findElement(By.cssSelector("#jobNm"));
			element.click();
			WaitUtil.waitFor(2);

			helper.waitPresenceOfElementLocated(By.cssSelector("#_name_job_tab_ > div:nth-child(1) > div.wrap_inp.clear_input_box > input")).sendKeys("사무원");
			helper.click(By.cssSelector("#_name_job_tab_ > div:nth-child(1) > a > span"));
			helper.click(By.cssSelector("#__job_codes_area__ > li:nth-child(1) > label > span > em"));
			WaitUtil.waitFor(1);

			// 완료버튼
			helper.click(By.cssSelector("#_btn_job_complete_ > a"));

			// 동의 버튼
			WaitUtil.waitFor(1);

			element = driver.findElement(By.cssSelector("#privateAgree"));
			element.click();

			//개인형 이동장치 선택
			logger.info("개인형 이동장치 사용안함 클릭");
			driver.findElement(By.cssSelector("#personalMobYn2")).click();


			//개인형 이동장치 선택 후, 동의 버튼 다시클릭
			WaitUtil.waitFor(1);
			driver.findElement(By.cssSelector("#privateAgree")).click();


			logger.info("다음");
			clickByLinkText("다음");

			logger.info("플랜 : 기본형 고정");
			// 가입유형 선택 (실속/일반/고급형, 보험기간) 예: 고급형플랜(7년갱신)
			String planType = "#pdcPanCd1";
			String textType = "01";
			if (info.textType.contains("표준형")) {
				logger.info("표준형");
			} else if (info.textType.contains("선택형II")) {
				logger.info("선택형Ⅱ");
				planType = "#pdcPanCd2";
				textType = "02";
			}

			if(planType.equals("#pdcPanCd2")) {
				element = driver.findElement(By.cssSelector(planType));
				element.sendKeys(Keys.ENTER);
				element.click();
				helper.waitForCSSElement(".loadmask");
			}



			loopTreatyList(info, textType);

			reCompute();


			String prm;
			element = driver.findElement(By.id("totFstiPrm"));
			prm = element.getText().replaceAll("[^0-9]", "");
			info.treatyList.get(0).monthlyPremium = prm;
			logger.info("금액 : "+prm);

			WaitUtil.waitFor(1);
			logger.info("스크린샷 찍기");
			takeScreenShot(info);

	}

	protected void setBirth(String birth) throws InterruptedException {
		element = helper.waitPresenceOfElementLocated(By.id("birthday"));
		element.clear();
		element.sendKeys(birth);
	}



	//특약 loop
	protected void loopTreatyList(CrawlingProduct info,String textTypeNumber) throws InterruptedException {

		List<String> webTreatyList = new ArrayList<>();
		List<String> apiTreatyList = new ArrayList<>();
		List<String> webText = new ArrayList<>();

		String arr;
		elements = helper.waitPresenceOfElementLocated(By.cssSelector("#contents > div > div.plan_wrap > div.plan-fix > div.plan-fix-body > ul > li.plan_name > dl")).findElements(By.cssSelector("dd"));

		for (WebElement treatyList : elements) {
			arr = treatyList.findElement(By.cssSelector("div > strong")).getText();
			arr = arr.replaceAll("\\p{Z}", "");
			webTreatyList.add(arr);
			webText.add(arr);
		}


		elements = helper.waitPresenceOfElementLocated(By.cssSelector("#contents > div > div.plan_wrap > div.plan-fix > div.plan-fix-body > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));



		for(CrawlingTreaty treatyList : info.treatyList){
			apiTreatyList.add(treatyList.treatyName);
		}

		for(CrawlingTreaty name : info.treatyList){
			String treatyNameSave = name.treatyName;
			String[] treatyNameSplitSave = treatyNameSave.split("- ");
		}


		webTreatyList.removeAll(apiTreatyList);

		// 특약 loop
		int loop = 0;
		for (WebElement selectSignup : elements) {

			logger.info("특약체크 중....");

			if (selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("필수가입") || selectSignup.findElement(By.cssSelector("ul > li.signup")).getText().equals("")) {
				continue;
			}

			for (String treatyName : webTreatyList) {
				if (webText.get(loop).equals(treatyName)) {
					if (selectSignup.findElement(By.cssSelector("ul > li.signup > a")).getAttribute("class").equals("signup_box on")) {
						WebElement signupClick = selectSignup.findElement(By.cssSelector("ul > li.signup > a"));
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", signupClick);
						webTreatyList.remove(treatyName);
						WaitUtil.waitFor(1);
						break;
					}
				}
			}
			loop++;
		}
	}

	//다시계산하기 버튼이 있는경우 클릭 그외 바로 넘김
	protected void reCompute() throws Exception {
		element = driver.findElement(By.cssSelector("#contents > div > div.plan_total.type02 > div.right_plan_again > a"));

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
}
