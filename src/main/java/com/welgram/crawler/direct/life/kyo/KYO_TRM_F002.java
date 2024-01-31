package com.welgram.crawler.direct.life.kyo;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKYO;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class KYO_TRM_F002 extends CrawlingKYO {

	

	public static void main(String[] args) {
		executeCommand(new KYO_TRM_F002(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) {
		option.setImageLoad(true);
	}

	@Override
	protected boolean preValidation(CrawlingProduct info) {

		boolean result = true;
		try {
			int calcInsAge = Integer.parseInt(info.age);// 계산테이블에서 정의한 나이
			int minInsAge = info.minInsAge;				// 가입설계에서 정의한 최소 나이
			int maxInsAge = info.maxInsAge;				// 가입설계에서 정의한 최대 나이


			if (calcInsAge >= minInsAge && calcInsAge <= maxInsAge){
				logger.info("가입설계에서 정한 나이 확인!!");
			}else{
				throw new Exception("가입설계에서 정한 나이가 아닙니다.");
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		doCrawlInsurancePublic(info);
		return true;
	}

	private void doCrawlInsurancePublic(CrawlingProduct info) throws Exception {

		logger.info("상품이름 확인 : "+info.productName);

		helper.click(driver.findElement(By.cssSelector("#clf0 > a")));
		WaitUtil.loading(2);

		elements = driver.findElements(By.className("cellText-Left"));
		logger.info("플랜선택");
		//setPlan(info);
		for(int i=0; i<elements.size(); i++){
			if(elements.get(i).getText().trim().equals(info.productName)){
				logger.info("페이지의 상품명 확인 : "+elements.get(i).getText().trim());
				driver.findElement(By.cssSelector("#smartUItable_RetrieveTable > tbody > tr:nth-child("+(i+1)+") > td.cellText-Center > a")).click();
				break;
			}
		}
		WaitUtil.loading(2);

		// 플랜선택
		String planName = "";
		for (CrawlingTreaty item : info.treatyList) {
			if (item.productGubun.equals(ProductGubun.주계약)){
				planName = item.treatyName;
			}
		}
		Select selectPlan = new Select(driver.findElement(By.id("insKind")));
		logger.info("1");
		selectPlan.selectByVisibleText(planName);
		logger.info("2");

		//이름
		logger.info("이름입력");
		setName();
		WaitUtil.loading(1);

		// 생년월일
		logger.info("생년월일 입력");
		setBirth(info);
		WaitUtil.loading(2);

		// 성별
		logger.info("성별선택");
		setGender(info);
		WaitUtil.loading(1);

		// 표준체, 건강체 선택
		driver.findElement(By.id("smokingY")).click();
		WaitUtil.loading(1);

		//다음누르기
		logger.info("다음누르기");
		helper.click(driver.findElement(By.id("btn_next_step2")));
		WaitUtil.loading(2);

//		//납입주기 선택
//		logger.info("납입주기 월납선택");
//		setCycle();

		//다음누르기
		logger.info("다음누르기");
		helper.click(driver.findElement(By.id("btn_next_step3")));
		WaitUtil.loading(2);

		//다음누르기
		logger.info("다음누르기");
		helper.click(driver.findElement(By.id("btn_next_step4")));
		WaitUtil.loading(2);
		// 특약선택

		for (CrawlingTreaty item : info.treatyList) {
			if (item.productGubun.equals(ProductGubun.주계약)){
				logger.info("주보험 보험기간 세팅");
				setInsTerm(item);
				WaitUtil.loading(1);
				logger.info("주보험 납입기간 세팅");

				// 보기 납기가 같은경우 납입기간 처리
				if (item.insTerm.equals(item.napTerm)){
					item.napTerm = "전기납";
				}

				setNapTerm(item);
				WaitUtil.loading(1);

				logger.info("가입금액 세팅");
				element = driver.findElement(By.cssSelector("#ioInsAmtM"));
				element.click();
				element.sendKeys(Keys.DELETE);
				element.sendKeys(Keys.DELETE);
				element.sendKeys(Keys.DELETE);
				element.sendKeys(Keys.DELETE);
				element.sendKeys(Keys.DELETE);
				element.sendKeys(item.assureMoney / 10000 + "");

			}
		}

		WaitUtil.waitFor(2);

		//보험료계산 버튼누르기
		logger.info("보험료계산버튼");
		helper.click(driver.findElement(By.id("calPremiumBtn")));
		WaitUtil.loading(5);

		//보험료 가져오기
		logger.info("보험료 가져오기");
		this.getPremiums(info);

		//스크린샷
		takeScreenShot(info);

		logger.info("보장내용클릭");
		driver.findElement(By.id("btn_confirm")).click();
		WaitUtil.loading(5);

		driver.switchTo().frame("btn_confirmiFrame");
		logger.info("해약환급금 탭 클릭 ");
		driver.findElement(By.cssSelector("#tab_02")).click();
		WaitUtil.loading(1);

		getReturnMoneyNew(info, By.cssSelector(""));

	}

	private void getPremiums(CrawlingProduct info) {
		String premium="";
		element = driver.findElement(By.id("totInsuAmt"));
		premium=element.getText().replaceAll(",","");
		
		logger.info("월 보험료 스크랩 :: " + premium);
		info.treatyList.get(0).monthlyPremium=premium;

	}

	protected void setInsTerm(CrawlingTreaty item) {
		elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#iInagPeM option"));
		for (WebElement option : elements) {
			logger.info("option.getText() :: " + option.getText());
			if (option.getText().indexOf(item.insTerm) > -1) {
				logger.info(option.getText() + " 선택");
				option.click();
				break;
			}
		}
	}

	protected void setNapTerm(CrawlingTreaty item) {
		elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#iPayPeM option"));
		for (WebElement option : elements) {
			logger.info("option.getText() :: " + option.getText());
			if (option.getText().indexOf(item.napTerm) > -1) {
				logger.info(option.getText() + " 선택");
				option.click();
				break;
			}
		}
	}

	protected void getReturnMoneyNew(CrawlingProduct info, By by) throws Exception {

		logger.info("해약환급금 테이블선택");
		elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#pop_contents2 > #tableDiv > table > tbody > tr")));
		// 주보험 영역 Tr 개수만큼 loop
		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		int scrollTop=0;
		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
		for (WebElement tr : elements) {
			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			logger.info(tr.findElements(By.tagName("td")).get(0).getText());
			String term = tr.findElements(By.tagName("td")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
			String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
			String returnRate = tr.findElements(By.tagName("td")).get(3).getText();
			logger.info(term + " :: " + premiumSum );

			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoney(returnMoney);
			planReturnMoney.setReturnRate(returnRate);
			planReturnMoneyList.add(planReturnMoney);



			//info.returnPremium = returnMoney.replace(",", "").replace("원", "");

//			scrollTop += 65;
//			WaitUtil.mSecLoading(300);
//			eventFiringWebDriver.executeScript("document.querySelector(\"div[class='section-main section-disclosure section-insurance-calculate']\").parentNode.scrollTop = " + scrollTop );
		}
		// 순수보장형 만기환급금 0
		info.returnPremium = 0+"";
		logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);

		info.setPlanReturnMoneyList(planReturnMoneyList);
		// 해약환급금 관련 End
	}
}
