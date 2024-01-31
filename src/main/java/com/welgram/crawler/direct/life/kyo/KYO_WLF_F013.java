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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class KYO_WLF_F013 extends CrawlingKYO {

	

	public static void main(String[] args) {
		executeCommand(new KYO_WLF_F013(), args);
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

		helper.click(driver.findElement(By.cssSelector("#contents > div.ut-tabs.h > ul > li:nth-child(1) > a")));

		WaitUtil.loading(1);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
		WaitUtil.loading(1);
		elements = driver.findElements(By.cssSelector("#prodList > tr"));
		logger.info("플랜선택");

		for(int i=0; i<elements.size(); i++){
			int y = 40 *i;
			((JavascriptExecutor) driver).executeScript("scroll(0,"+y+");");
			String siteProductName = elements.get(i).findElement(By.className("txt-l")).getText();

			if(siteProductName.trim().equals(info.productName)){
				logger.info("페이지의 상품명 확인 : "+siteProductName);
				elements.get(i).findElement(By.tagName("button")).click();;
				break;
			}
		}
		WaitUtil.loading(1);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

		// 생년월일
		logger.info("생년월일 입력");
		element = helper.waitElementToBeClickable(By.cssSelector("#userInfoType1 > span"));
		element.click();
		//element.clear();
		element.findElement(By.tagName("input")).sendKeys(info.fullBirth);

		WaitUtil.loading(1);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

		// 성별
		logger.info("성별선택");
		if(info.getGender() == 0){
			driver.findElement(By.xpath("//*[@id=\"userInfoType1\"]/label[1]")).click();
		}else{
			driver.findElement(By.xpath("//*[@id=\"userInfoType1\"]/label[2]")).click();
		}

		WaitUtil.loading(1);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));


		for (CrawlingTreaty item : info.treatyList) {
			if (item.productGubun.equals(ProductGubun.주계약)){


				// 표준체 선택
//				driver.findElement(By.xpath("//*[@id=\"disHealthYn\"]/td/label[1]")).click();
//				WaitUtil.loading(1);
//				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));


				// 보험종류선택
				String planName = item.treatyName;
				Select selectPlan = new Select(driver.findElement(By.id("sel_gdcl")));
				logger.info("1");
				selectPlan.selectByVisibleText(planName);
				logger.info("2");
				WaitUtil.loading(1);
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

				logger.info("주보험 보험기간 세팅");
				if (item.insTerm.equals("종신보장")){
					item.insTerm = "종신";
				}
				element = driver.findElement(By.cssSelector("#show_isPd"));
				Select selectInsTerm = new Select(element.findElement(By.tagName("select")));
				selectInsTerm.selectByVisibleText(item.insTerm);

				WaitUtil.loading(1);
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

				logger.info("주보험 납입기간 세팅");
				element = driver.findElement(By.cssSelector("#show_paPd"));
				// 보기 납기가 같은경우 납입기간 처리
				if (item.insTerm.equals(item.napTerm)){
					item.napTerm = "전기납";
				}
				Select selectNapTerm = new Select(element.findElement(By.tagName("select")));
				selectNapTerm.selectByVisibleText(item.napTerm+"납");
				WaitUtil.loading(1);
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

				logger.info("가입금액 세팅");
				element = driver.findElement(By.cssSelector("#sbcAmtView"));
				element.click();

				element = element.findElement(By.tagName("input"));
				element.sendKeys(Keys.DELETE);
				element.sendKeys(Keys.DELETE);
				element.sendKeys(Keys.DELETE);
				element.sendKeys(Keys.DELETE);
				element.sendKeys(Keys.DELETE);
				element.sendKeys(item.assureMoney / 10000 + "");
			}
		}


		WaitUtil.loading(1);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

		//보험료계산 버튼누르기
		logger.info("보험료계산버튼");
		helper.click(driver.findElement(By.cssSelector("#pop-calc > div > div.pbt > div > button")));

		WaitUtil.loading(1);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

		//보험료 가져오기
		logger.info("보험료 가져오기");
		//this.getPremiums(info);
		String premium="";
		element = driver.findElement(By.cssSelector("#show_pdtPrm > span > input:nth-child(3)"));

		premium=element.getAttribute("value").replaceAll(",","");

		logger.info("월 보험료 스크랩 :: " + premium);
		info.treatyList.get(0).monthlyPremium=premium;

		//스크린샷
		takeScreenShot(info);

		logger.info("보장내용클릭");

		driver.findElement(By.cssSelector("#areaPrm > div.btn-set.mt20 > button.btn.b.md")).click();
		WaitUtil.loading(1);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));

		logger.info("해약환급금 탭 클릭 ");
		driver.findElement(By.cssSelector("#oPopHisMenu > li:nth-child(2) > a")).click();
		WaitUtil.loading(1);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));


		getReturnMoneyNew(info, By.cssSelector(""));

	}

	protected void getReturnMoneyNew(CrawlingProduct info, By by) throws Exception {

		logger.info("해약환급금 테이블선택");

		elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#trmRview > div.ut-tbl.a.mt20.dcs-tbl.scroll-x > table > tbody > tr")));

		// 주보험 영역 Tr 개수만큼 loop
		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		int scrollTop=0;
		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
		for (WebElement tr : elements) {
			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			String term = tr.findElements(By.tagName("td")).get(0).getText();
			String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
			String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
			String returnRate = tr.findElements(By.tagName("td")).get(3).getText();
			String returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText();
			String returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();
			String returnMoneyMin = tr.findElements(By.tagName("td")).get(6).getText();
			String returnRateMin = tr.findElements(By.tagName("td")).get(7).getText();

			if (!term.isEmpty()){
				logger.info("term :: " + term );
				logger.info("premiumSum :: " + premiumSum );
				logger.info("returnMoney :: " + returnMoney );
				logger.info("returnRate :: " + returnRate );
				logger.info("returnMoneyAvg :: " + returnMoneyAvg );
				logger.info("returnRateAvg :: " + returnRateAvg );
				logger.info("returnMoneyMin :: " + returnMoneyMin );
				logger.info("returnRateMin :: " + returnRateMin );
				logger.info(" ============================= ");

				planReturnMoney.setTerm(term);
				planReturnMoney.setPremiumSum(premiumSum);
				planReturnMoney.setReturnMoney(returnMoney);
				planReturnMoney.setReturnRate(returnRate);
				planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
				planReturnMoney.setReturnRateAvg(returnRateAvg);
				planReturnMoney.setReturnMoneyMin(returnMoneyMin);
				planReturnMoney.setReturnRateMin(returnRateMin);
				planReturnMoneyList.add(planReturnMoney);

			}

			info.returnPremium = returnMoney.replace(",", "").replace("원", "");

		}
		// 순수보장형 만기환급금 0
		info.returnPremium = 0+"";
		logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);

		info.setPlanReturnMoneyList(planReturnMoneyList);
	}
}
