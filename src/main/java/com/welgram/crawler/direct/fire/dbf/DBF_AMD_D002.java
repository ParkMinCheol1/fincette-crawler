package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

/**
 * DB손해보험 - 무배당 프로미라이프 다이렉트 간편실손의료비(유병력자용)2101(CM)
 * @author user
 *
 */
public class DBF_AMD_D002 extends CrawlingDBF {

	

	public static void main(String[] args) {
		executeCommand(new DBF_AMD_D002(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		doCrawlInsurance(info);

		return true;
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) throws Exception {
		option.setUserData(true);
	}


	public void crawlFromHomepage(CrawlingProduct info) throws Exception {

			WaitUtil.waitFor(2);
			driver.findElement(By.cssSelector("#pc_contents > div > div.prod_content.ui_prod_pr.heal_simple > div.prod_top > div > a")).click();

			WaitUtil.waitFor(1);

			// 자녀형(18세까지) 탭 선택
			logger.info("자녀형(18세까지) 탭 선택");
			//doClick(By.cssSelector(".li01 > a:nth-child(1) > span:nth-child(1)"));
			helper.click(By.cssSelector(".li01 > a:nth-child(1)"));

			// 미취학 아동 라디오버튼 클릭
			logger.info("미취학 아동 라디오버튼 클릭");
			//doClick(By.id("jobCd1"));
			//doClick(By.cssSelector(".label_horizental > li:nth-child(1) > label:nth-child(1) > input"));

			List<WebElement> radioBtns = helper.waitPesenceOfAllElementsLocatedBy(By.name("jobCd"));
			for (WebElement radioBtn : radioBtns) {
				if (radioBtn.getAttribute("value").equals("B6100")) {
					radioBtn.findElement(By.xpath("ancestor::label")).click();
					logger.info("취학정보 라디오 선택 여부" + radioBtn.isSelected());
					break;
				}
			}

			logger.info("생년월일");
			setBirth(info.fullBirth);

			logger.info("성별");
			radioBtns = helper.waitPesenceOfAllElementsLocatedBy(By.name("sxCd"));
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
			WaitUtil.waitFor(2);

			loopTreatyList(info, "01");
			WaitUtil.waitFor(1);
			reCompute();

			helper.waitForCSSElement(".loadmask");

			String prm;
			element = driver.findElement(By.id("totFstiPrm"));
			prm = element.getText().replaceAll("[^0-9]", "");
			info.treatyList.get(0).monthlyPremium = prm;
			logger.info("보험료 : " + prm);

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

		elements = helper.waitPresenceOfElementLocated(By.cssSelector("#contents > div > div.plan_wrap > div.plan-fix > div.plan-fix-body > ul > li.plan" + textTypeNumber + ".on > dl")).findElements(By.cssSelector("dd"));

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
				}
			}
		}
	}

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
