package com.welgram.crawler.direct.life.kyo;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.direct.life.CrawlingKYO;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;



// 2323.07.31 | 조하연 |
public class KYO_CCR_F004 extends CrawlingKYO implements Scrapable{
	

	public static void main(String[] args) {
		executeCommand(new KYO_CCR_F004(), args);
	}

	@Override
	protected void configCrawlingOption(CrawlingOption option) {
//		option.setImageLoad(true);
	}

//	@Override
//	protected boolean preValidation(CrawlingProduct in fo) {
//
//		boolean result = true;
//		try {
//			int calcInsAge = Integer.parseInt(info.age);// 계산테이블에서 정의한 나이
//			int minInsAge = info.minInsAge;				// 가입설계에서 정의한 최소 나이
//			int maxInsAge = info.maxInsAge;				// 가입설계에서 정의한 최대 나이
//
//
//			if (calcInsAge >= minInsAge && calcInsAge <= maxInsAge){
//				logger.info("가입설계에서 정한 나이 확인!!");
//			}else{
//				throw new Exception("가입설계에서 정한 나이가 아닙니다.");
//			}
//		} catch (Exception e) {
//			result = false;
//			e.printStackTrace();
//		}
//		return result;
//	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
//		doCrawlInsurancePublic(info);
		crawlFromAnnounce(info);
		return true;
	}


	private boolean crawlFromAnnounce(CrawlingProduct info) throws Exception {
		boolean result = true;

		logger.info("공시실 진입 후 건강/암 버튼 클릭");
		element = driver.findElement(By.linkText("건강/암"));
		waitElementToBeClickable(element).click();
		WaitUtil.waitFor(2);


		logger.info("상품명 : {} 클릭", info.productNamePublic);
		element = driver.findElement(By.xpath("//td[text()='" + info.productNamePublic + "']/parent::tr//button"));
		waitElementToBeClickable(element).click();
		waitAnnouncePageLoadingBar();
		waitAnnouncePageLoadingBar();
		WaitUtil.waitFor(3);


		logger.info("생년월일 설정");
		setBirthdayNew(info.fullBirth);


		logger.info("성별 설정");
		setGenderNew(info.gender);


		logger.info("보험종류 설정");
		setPlanType(info.textType);


		logger.info("납입주기 설정");
		setNapCycleNew(info.getNapCycleName());


		logger.info("보험기간 설정");
		setInsTermNew(info.insTerm);


		logger.info("납입기간 설정");
		this.setNapTermNew(info.insTerm, info.napTerm);


		logger.info("가입금액 설정");
		setAssureMoneyNew(info.assureMoney);


		logger.info("특약 설정 및 비교");
		setTreaties(info);


		logger.info("보험료 크롤링");
		WebElement element = driver.findElement(By.xpath("//div[@id='totPrmTx']/strong"));
		String premium = element.getText().replaceAll("[^0-9]", "");
		info.treatyList.get(0).monthlyPremium = premium;


		logger.info("스크린샷찍기");
		moveToElementByJavascriptExecutor(element);
		takeScreenShot(info);


		logger.info("보장내용 버튼 클릭");
		element = driver.findElement(By.xpath("//button[text()='보장내용']"));
		waitElementToBeClickable(element).click();
		WaitUtil.waitFor(1);
		waitAnnouncePageLoadingBar();


		logger.info("해약환급금 탭 버튼 클릭");
		element = driver.findElement(By.linkText("해약환급금"));
		waitElementToBeClickable(element).click();


		logger.info("해약환급금 크롤링");
		this.crawlReturnMoneyListNew(info);


		return result;
	}


	private void setPlanType(Object obj) throws Exception {
		String title = "보험종류";
		String welgramPlanType = (String) obj;

		//보험종류 클릭
		WebElement select = driver.findElement(By.id("sel_gdcl"));
		selectOptionByText(select, welgramPlanType);


		//실제로 클릭된 보험종류 읽어오기
		String script = "return $(arguments[0]).find('option:selected').text();";
		String targetPlanType = String.valueOf(executeJavascript(script, select));

		//비교
		printAndCompare(title, welgramPlanType, targetPlanType);

	}


	public void setNapTermNew(Object obj, Object obj2) throws SetNapTermException {
		String title = "납입기간";
		String welgramInsTerm = (String) obj;
		String welgramNapTerm = (String) obj2;
		welgramNapTerm = (welgramInsTerm.equals(welgramNapTerm)) ? "전기납" : welgramNapTerm + "납";


		try {

			//납입기간 클릭
			WebElement select = driver.findElement(By.xpath("//span[@id='show_paPd']//select[@name='pdtScnCd_paPd']"));
			selectOptionByText(select, welgramNapTerm);


			//실제로 클릭된 납입기간 읽어오기
			String script = "return $(arguments[0]).find('option:selected').text();";
			String targetNapTerm = String.valueOf(executeJavascript(script, select));

			//비교
			printAndCompare(title, welgramNapTerm, targetNapTerm);

		} catch (Exception e) {
			throw new SetNapTermException(e.getMessage());
		}
	}


	public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
		CrawlingProduct info = (CrawlingProduct) obj;

		List<WebElement> $trList = driver.findElements(By.xpath("//div[@id='trmRview']//table/tbody/tr"));
		for(WebElement $tr : $trList) {
			String term = $tr.findElement(By.xpath("./td[1]")).getText();
			String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText();
			String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText();
			String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();
			returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));


			logger.info("경과기간 : {}", term);
			logger.info("납입보험료 : {}", premiumSum);
			logger.info("공시환급금 : {}", returnMoney);
			logger.info("공시환급률 : {}", returnRate);
			logger.info("==========================================");

			PlanReturnMoney p = new PlanReturnMoney();
			p.setTerm(term);
			p.setPremiumSum(premiumSum);
			p.setReturnMoney(returnMoney);
			p.setReturnRate(returnRate);


			//만기환급금 세팅
			info.returnPremium = returnMoney;
			info.planReturnMoneyList.add(p);
		}

		// 순수보장형 만기환급금 0
		info.returnPremium = 0+"";
		logger.info("만기환급금 : {}", info.returnPremium);

	}


//	private void doCrawlInsurancePublic(CrawlingProduct info) throws Exception {
//
//		logger.info("상품이름 확인 : "+info.productName);
//
//		helper.doClick(driver.findElement(By.cssSelector("#contents > div.ut-tabs.h > ul > li:nth-child(2) > a")));
//
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//		WaitUtil.loading(1);
//		elements = driver.findElements(By.cssSelector("#prodList > tr"));
//		logger.info("플랜선택");
//
//		for(int i=0; i<elements.size(); i++){
//			int y = 40 *i;
//			((JavascriptExecutor) driver).executeScript("scroll(0,"+y+");");
//			String siteProductName = elements.get(i).findElement(By.className("txt-l")).getText();
//
//			if(siteProductName.trim().equals(info.productNamePublic)){
//				logger.info("페이지의 상품명 확인 : "+siteProductName);
//				elements.get(i).findElement(By.tagName("button")).click();;
//				break;
//			}
//		}
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//
//		// 생년월일
//		logger.info("생년월일 입력");
//		element = helper.waitElementToBeClickable(By.cssSelector("#userInfoType1 > span"));
//		element.click();
//		//element.clear();
//		element.findElement(By.tagName("input")).sendKeys(info.fullBirth);
//
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//		// 성별
//		logger.info("성별선택");
//		if(info.getGender() == 0){
//			driver.findElement(By.xpath("//*[@id=\"userInfoType1\"]/label[1]")).click();
//		}else{
//			driver.findElement(By.xpath("//*[@id=\"userInfoType1\"]/label[2]")).click();
//		}
//
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//
//		for (CrawlingTreaty item : info.treatyList) {
//			if (item.productGubun.equals(ProductGubun.주계약)){
//				// 보험종류선택
//				String planName = item.treatyName;
//				Select selectPlan = new Select(driver.findElement(By.id("sel_gdcl")));
//				logger.info("1");
//				selectPlan.selectByVisibleText(planName);
//				logger.info("2");
//
//				WaitUtil.loading(1);
//				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//				logger.info("주보험 보험기간 세팅");
//				element = driver.findElement(By.cssSelector("#show_isPd"));
//				Select selectInsTerm = new Select(element.findElement(By.tagName("select")));
//				selectInsTerm.selectByVisibleText(item.insTerm+"만기");
//
//				WaitUtil.loading(1);
//				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//				logger.info("주보험 납입기간 세팅");
//
//				element = driver.findElement(By.cssSelector("#show_paPd"));
//
//				// 보기 납기가 같은경우 납입기간 처리
//				if (item.insTerm.equals(item.napTerm)){
//					item.napTerm = "전기납";
//				}
//				Select selectNapTerm = new Select(element.findElement(By.tagName("select")));
//				selectNapTerm.selectByVisibleText(item.napTerm);
//
//				WaitUtil.loading(1);
//				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//				logger.info("가입금액 세팅");
//				element = driver.findElement(By.cssSelector("#sbcAmtView"));
//				element.click();
//
//				element = element.findElement(By.tagName("input"));
//				element.sendKeys(Keys.DELETE);
//				element.sendKeys(Keys.DELETE);
//				element.sendKeys(Keys.DELETE);
//				element.sendKeys(Keys.DELETE);
//				element.sendKeys(Keys.DELETE);
//				element.sendKeys(item.assureMoney / 10000 + "");
//
//
//			}else{
//
//				element = driver.findElement(By.cssSelector("#areaScn"));
//				elements = element.findElements(By.tagName("tr"));
//
//				for(int i=0; i<elements.size(); i++){
//					try{
//						int y = 40 *i;
//						((JavascriptExecutor) driver).executeScript("scroll(0,"+y+");");
//
//						element = elements.get(i).findElement(By.cssSelector("td:nth-child(1) > label > span"));
//
//						String treatyName = elements.get(i).findElement(By.cssSelector("td > label > span")).getText();
//						logger.info(treatyName);
//
//						if (item.treatyName.equals(treatyName)){
//							logger.info("가입설계 특약 찾음!!");
//
//							if (item.insTerm.equals(item.napTerm)){
//								item.napTerm = "전기납";
//							}
//							element = elements.get(i).findElement(By.cssSelector("td:nth-child(2) > span"));
//
//							Select selectInsTerm = new Select(element.findElement(By.tagName("select")));
//							selectInsTerm.selectByVisibleText(item.insTerm);
//
//
//						}
//
//					}catch (Exception e){
//						logger.info("특약 영영이 없음");
//
//					}
//
//
//				}
//			}
//		}
//
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//		//보험료계산 버튼누르기
//		logger.info("보험료계산버튼");
//		helper.doClick(driver.findElement(By.cssSelector("#pop-calc > div > div.pbt > div > button")));
//
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//		//보험료 가져오기
//		logger.info("보험료 가져오기");
//		//this.getPremiums(info);
//		String premium="";
//		element = driver.findElement(By.cssSelector("#show_pdtPrm > span > input:nth-child(3)"));
//
//		premium=element.getAttribute("value").replaceAll(",","");
//
//		logger.info("월 보험료 스크랩 :: " + premium);
//		info.treatyList.get(0).monthlyPremium=premium;
//
//		//스크린샷
//		takeScreenShot(info);
//
//		logger.info("보장내용클릭");
//
//		driver.findElement(By.cssSelector("#areaPrm > div.btn-set.mt20 > button.btn.b.md")).click();
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//		//driver.switchTo().frame("btn_confirmiFrame");
//		logger.info("해약환급금 탭 클릭 ");
//
//		driver.findElement(By.cssSelector("#oPopHisMenu > li:nth-child(2) > a")).click();
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//		getReturnMoneyNew(info, By.cssSelector(""));
//	}
//
//	protected void getReturnMoneyNew(CrawlingProduct info, By by) throws Exception {
//
//		logger.info("해약환급금 테이블선택");
//
//		elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#trmRview > div.ut-tbl.a.mt20.dcs-tbl.scroll-x > table > tbody > tr")));
//
//		// 주보험 영역 Tr 개수만큼 loop
//		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
//		int scrollTop=0;
//		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
//		for (WebElement tr : elements) {
//			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//			String term = tr.findElements(By.tagName("td")).get(0).getText();
//			String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
//			String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
//			String returnRate = tr.findElements(By.tagName("td")).get(3).getText();
//
//			if (!term.isEmpty()){
//				logger.info("term :: " + term );
//				logger.info("premiumSum :: " + premiumSum );
//				logger.info("returnMoney :: " + returnMoney );
//				logger.info("returnRate :: " + returnRate );
//				logger.info(" ============================= ");
//
//				planReturnMoney.setTerm(term);
//				planReturnMoney.setPremiumSum(premiumSum);
//				planReturnMoney.setReturnMoney(returnMoney);
//				planReturnMoney.setReturnRate(returnRate);
//				planReturnMoneyList.add(planReturnMoney);
//
//			}
//
//			info.returnPremium = returnMoney.replace(",", "").replace("원", "");
//
//		}
//		// 순수보장형 만기환급금 0
//		info.returnPremium = 0+"";
//		logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);
//
//		info.setPlanReturnMoneyList(planReturnMoneyList);
//		// 해약환급금 관련 End
//	}


	// 2022.11.03 | 최우진 | 아래 오버라이드 되는 메서드명만 변경하였습니다. ex. setBirthDay() -> setBirthDayNew()

}
