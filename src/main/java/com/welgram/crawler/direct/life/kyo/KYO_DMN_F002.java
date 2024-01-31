package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



// 2023.04.26 | 최우진 | 교보치매보험(무배당) 1종(저해약환급금형)
public class KYO_DMN_F002 extends CrawlingKYOAnnounce {

	public static void main(String[] args) {
		executeCommand(new KYO_DMN_F002(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// INFORMATION
		String[] textType = info.getTextType().split("#");
		String refundOption = "BASE";

		// PROCESS
		logger.info("▉▉▉▉ 시작 ▉▉▉▉");
		initKYO(info, textType[1]);

		logger.info("▉▉▉▉ {} ▉▉▉▉", info.getProductNamePublic());
		setBirthday(driver.findElement(By.xpath("//*[@id='inpBhdt']")), info.getFullBirth(), 2);
		setGender(null, null, info.getGender(), 5);

		// 교보치매보험(무배당) 1종(저해약환급금형)
		logger.info("▉▉▉▉ 주계약 ▉▉▉▉");
		setProductKind(
			driver.findElement(By.xpath("//*[@id='sel_gdcl']")),
			textType[2],
			3
		);
		setInsTerm(
			driver.findElement(By.xpath("//*[@id='5193100_isPd']")),
			info.getInsTerm(),
			2
		);
		setAssureMoney(
			driver.findElement(By.xpath("//*[@id='5193100_sbcAmt']")),
			info.getAssureMoney(),
			2
		);
		setNapCycle(
			driver.findElement(By.xpath("//*[@id='pdtMcrnCd_paCyc']")),
			info.getNapCycleName(),
			2
		);
		setNapTerm(
			driver.findElement(By.xpath("//*[@id='5193100_paPd']")),
			info.getNapTerm(),
			info.getInsTerm(),
			2,
			false
		);

		logger.info("▉▉▉▉ 특약 ▉▉▉▉");
		submitTreatiesInfo(
			driver.findElements(By.xpath("//*[@id='scnList']/table/tbody/tr")),
			info
		);
		pushButton(
			driver.findElement(By.xpath("//*[@id='pop-calc']/div/div[3]/div/button")),
	4
		);

		logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
		crawlPremium(null, info, 2);
		pushButton(driver.findElement(By.xpath("//*[@id='areaPrm']/div[2]/button[1]")), 5);
		pushButton(driver.findElement(By.xpath("//*[@id='oPopHisMenu']/li[2]/a")), 3);
		crawlReturnMoneyList(
			driver.findElements(By.xpath("//*[@id='trmRview']/div[2]/table/tbody/tr")),
			info,
			refundOption
		);

		return true;
	}
}
//
//		logger.info("상품이름 확인 : "+info.productNamePublic);
//		helper.doClick(driver.findElement(By.cssSelector("#contents > div.ut-tabs.h > ul > li:nth-child(2) > a")));
//		WaitUtil.loading(1);
//
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//		WaitUtil.loading(1);
//		elements = driver.findElements(By.cssSelector("#prodList > tr"));
//		logger.info("플랜선택");
//
//		for (int i=0; i<elements.size(); i++) {
//			int y = 40 * i;
//			((JavascriptExecutor) driver).executeScript("scroll(0," + y + ");");
//			String siteProductName = elements.get(i).findElement(By.className("txt-l")).getText();
//
//			if (siteProductName.trim().equals(info.productNamePublic)) {
//				logger.info("페이지의 상품명 확인 : " + siteProductName);
//				elements.get(i).findElement(By.tagName("button")).click();;
//				break;
//			}
//		}
//
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
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
//		if (info.getGender() == 0) {
//			driver.findElement(By.xpath("//*[@id=\"userInfoType1\"]/label[1]")).click();
//		} else {
//			driver.findElement(By.xpath("//*[@id=\"userInfoType1\"]/label[2]")).click();
//		}
//
//		WaitUtil.loading(1);
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='ui-loading']")));
//
//		// 주계약 보험종류 셋팅
//		logger.info("주계약 보험종류 세팅");
//		WebElement select = driver.findElement(By.id("sel_gdcl"));
//		select.findElement(By.xpath(".//option[contains(., '" + info.textType + "')]")).click();
//
//		for (CrawlingTreaty item : info.treatyList) {
//			if (item.productGubun.equals(ProductGubun.주계약)) {
//				// 보험종류선택
////				String planName = item.treatyName;
////				Select selectPlan = new Select(driver.findElement(By.id("sel_gdcl")));
////				logger.info("1");
////				selectPlan.selectByVisibleText(planName);
////				logger.info("2");
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
//				if (item.insTerm.equals(item.napTerm)) {
//					item.napTerm = "전기납";
//				}
//				Select selectNapTerm = new Select(element.findElement(By.tagName("select")));
//				selectNapTerm.selectByVisibleText(item.napTerm + "납");
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
//		premium
//			= element
//				.getAttribute("value")
//				.replaceAll("[^0-9]","");
//
//		logger.info("월 보험료 스크랩 :: " + premium);
//		info.treatyList.get(0).monthlyPremium = premium;
//
//		//스크린샷
//		takeScreenShot(info);
//
//		logger.info("보장내용클릭");
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
//		getReturnMoneyNew(info);
//
//		return true;
//	}
//
//
//
//	protected void getReturnMoneyNew(CrawlingProduct info) throws Exception {
//
//		logger.info("해약환급금 테이블선택");
//
//		elements
//			= wait.until(
//				ExpectedConditions
//					.presenceOfAllElementsLocatedBy(
//						By.cssSelector("#trmRview > div.ut-tbl.a.mt20.dcs-tbl.scroll-x > table > tbody > tr")
//					)
//			);
//
//		// 주보험 영역 Tr 개수만큼 loop
//		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
////		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
//		for (WebElement tr : elements) {
//			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//			String term = tr.findElements(By.tagName("td")).get(0).getText();
//			String premiumSum
//				= tr.findElements(By.tagName("td"))
//					.get(1)
//					.getText()
//					.replaceAll("[^0-9]","");;
//			String returnMoney
//				= tr.findElements(By.tagName("td"))
//					.get(2)
//					.getText()
//					.replaceAll("[^0-9]","");;
//			String returnRate
//				= tr.findElements(By.tagName("td"))
//					.get(3)
//					.getText();
//
//			if (!term.isEmpty()) {
//
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
//
//				planReturnMoneyList.add(planReturnMoney);
//			}
//
//			info.returnPremium = returnMoney;
//		}
//
//		// 만기환급금
////		info.returnPremium = "0";
////		logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);
//
//		info.setPlanReturnMoneyList(planReturnMoneyList);
//		// 해약환급금 관련 End
//	}
//}
