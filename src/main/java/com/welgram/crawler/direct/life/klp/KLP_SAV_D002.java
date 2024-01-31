package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.common.except.CannotBeSelectedException;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.ProductMasterVO;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * 교보라이프플래닛 - (무)라이프플래닛e에듀케어저축보험
 *
 */
// 2023.11.20 | 최우진 | 교보라이프플래닛 - (무)라이프플래닛e에듀케어저축보험
public class KLP_SAV_D002 extends CrawlingKLP {

	public static void main(String[] args) {
		executeCommand(new KLP_SAV_D002(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		// 부모 생년월일
		logger.info("부모 생년월일 설정 :: {}", info.getParent_FullBirth());
		helper.sendKeys1_check(By.id("plnnrBrdt"), info.getParent_FullBirth());

		// 부모 성별
		logger.info("부모 성별 설정: 남자로 고정");
		setGender(0); // 부모성별 여자(어머니)로 고정

		// 자녀 생년월일
		logger.info("자녀 생년월일 설정 :: {}", info.getFullBirth());
		helper.sendKeys1_check(By.id("childBrdt"), info.getFullBirth());

		// 자녀 성별
		logger.info("자녀 성별 설정");
		setChildGender(info.getGender());

		// 보험료 확인/가입
		logger.info("보험료 확인/가입");
		setConfirmPremium(By.id("fastPayCalc"));

//			logger.info("상품마스터 조회");
//			getTreaties(info, exeType);

		// 월보험료
		logger.info("월보험료 설정");
		helper.sendKeys1_check(By.id("monthPrm"), info.getAssureMoney());

		// 보험기간
		logger.info("보험기간 설정");
		setInsTerm2(By.id("inspd"), info.getInsTerm());

		// 납입기간
		logger.info("납입기간 설정");
		try {
			setNapTerm2(By.id("insuTermRadioArea"), info.getNapTerm());

		} catch(CannotBeSelectedException e) {
			throw new CannotBeSelectedException(e.getMessage());
		}

		//보험료 납입면제 특약
		logger.info("보험료 납입면제 특약 : 가입안함으로 세팅");
		driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(4) > div.box_middle.type_4 > div.box_rdo.type_4 > span.rdo_m.r._enabled._unChecked > label")).click();
		WaitUtil.waitFor(1);

		// 결과 확인하기
		logger.info("결과 확인하기");
		confirmResult();
		info.treatyList.get(0).monthlyPremium = info.assureMoney;

		WaitUtil.waitFor(1);
		logger.info("스크린샷 찍기");
		takeScreenShot(info);
		WaitUtil.waitFor(1);

		// 해약환급금 보기
		//getReturnPremium("#cal1Cancel1", info);

		element = driver.findElement(By.xpath("//*[@id='cal1MainGraph3']/div[1]/div/strong"));
		String returnMoney = element.getText().replaceAll("[^0-9]", "");
		info.setReturnPremium(returnMoney);
		logger.info("만기환급금 :: {}", info.getReturnPremium());

		logger.info("해약환급금(예시표) 조회");
		getReturns("cal1Cancel1", info);

		return true;
	}



	@Override
	protected void getMainTreaty(CrawlingProduct info) {

		List<String> assureMoneys = new ArrayList<>(); // 진단보험금
		List<String> insTerms = new ArrayList<>(); // 보험기간

		// 월보험료
		WebElement insuMoneyElement = driver.findElement(By.cssSelector("#monthPrm"));

		String assureMoney = insuMoneyElement.getAttribute("value");
		logger.debug("assureMoney: " + assureMoney);
		assureMoneys.add(assureMoney);

		WebElement minMaxAssureMoneyElement =
			driver
				.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_4 > div > p"));
		String minMaxAssureMoney = minMaxAssureMoneyElement.getText();

		Pattern p = Pattern.compile("\\((.*?)\\)");
		Matcher m = p.matcher(minMaxAssureMoney);
		minMaxAssureMoney = "";

		while (m.find()) {
			minMaxAssureMoney = m.group(1);
		}

		logger.info("minMaxAssureMoney: {}", minMaxAssureMoney);

		String[] minMaxAssureMoneyArr = minMaxAssureMoney.split(" ~ ");
		String minAssureMoney = minMaxAssureMoneyArr[0].split(" ")[1];
		String maxAssureMoney = minMaxAssureMoneyArr[1].split(" ")[1];

		assureMoneys.add(minAssureMoney);
		assureMoneys.add(maxAssureMoney);

		// 보험기간
		WebElement insuTermElement = driver.findElement(By.cssSelector("#inspd"));

		String insTerm = insuTermElement.getAttribute("value");
		logger.debug("insTerm: " + insTerm);
		insTerms.add(insTerm);

		WebElement minMaxInsTermElement = driver
				.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(2) > div.box_middle.type_4 > div > p"));
		String minMaxInsTerm = minMaxInsTermElement.getText();

		Pattern p1 = Pattern.compile("\\((.*?)\\)");
		Matcher m1 = p1.matcher(minMaxInsTerm);
		minMaxInsTerm = "";

		while (m1.find()) {
			minMaxInsTerm = m1.group(1);
		}

		logger.info("minMaxInsTerm: {}", minMaxInsTerm);

		String[] minMaxInsTermArr = minMaxInsTerm.split(" ~ ");
		String minInsTerm = minMaxInsTermArr[0].split(" ")[1];
		String maxInsTerm = minMaxInsTermArr[1].split(" ")[1];

		insTerms.add(minInsTerm);
		insTerms.add(maxInsTerm);

		// 납입기간(5, 7, 10, 11, ..., 30년)
		List<String> napTerms = new ArrayList<>() ;
		napTerms.add("5년");
		napTerms.add("7년");
		napTerms.add("10년");

		for(int i=11;i<=30;i++) {
			napTerms.add(i + "년");
		}

		ProductMasterVO productMasterVO = new ProductMasterVO();
		productMasterVO.setProductId(info.productCode);
		productMasterVO.setProductKinds(info.defaultProductKind); // 정확히 알면 표기
		productMasterVO.setProductTypes(info.defaultProductType); // 정확히 알면 표기
		productMasterVO.setProductGubuns("주계약");
		productMasterVO.setSaleChannel(info.getSaleChannel());
		productMasterVO.setProductName(info.productName);
		productMasterVO.setInsTerms(insTerms);
		productMasterVO.setNapTerms(napTerms);
		productMasterVO.setAssureMoneys(assureMoneys);
//		productMasterVO.setAnnuityAges(annuityAges);
//		productMasterVO.setAnnuityTypes(annuityTypes);
		productMasterVO.setMinAssureMoney(minAssureMoney);
		productMasterVO.setMaxAssureMoney(maxAssureMoney);
		productMasterVO.setCompanyId(info.getCompanyId());

		logger.info("상품마스터 :: " + productMasterVO.toString());
		info.getProductMasterVOList().add(productMasterVO);
	}



	protected void getReturns(String id, CrawlingProduct info) throws Exception {
		WaitUtil.loading(2);
		helper.waitForCSSElement("#loadingArea");

		WaitUtil.loading(2);
		element = driver.findElement(By.id(id));
		element.sendKeys(Keys.ENTER);
		element.click();
		WaitUtil.loading(1);

		Set<String> windowId = driver.getWindowHandles();
		Iterator<String> handles = windowId.iterator();
		// 메인 윈도우 창 확인
		subHandle = null;
		while (handles.hasNext()) {
			subHandle = handles.next();
			logger.debug(subHandle);
			WaitUtil.loading(1);
		}
		driver.switchTo().window(subHandle);
		elements = driver.findElements(By.cssSelector("#listArea > tr"));

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
		if (elements.size() > 0) {

			for (int i = 0; i < elements.size(); i++) {

				WebElement tr = elements.get(i);
				List<WebElement> tdList = tr.findElements(By.tagName("td"));

				logger.info("______해약환급급[{}]_______ ", i);

				String term = tdList.get(0).getAttribute("innerText"); // 경과기간
				String premiumSum = tdList.get(1).getAttribute("innerText"); // 납입보험료
				String minReturnMoney = tdList.get(2).getAttribute("innerText"); // 최저해약환급금
				String minReturnRate = tdList.get(3).getAttribute("innerText"); // 최저환급률
				String avgReturnMoney = tdList.get(4).getAttribute("innerText"); // 평균해약환급금
				String avgReturnRate = tdList.get(5).getAttribute("innerText"); // 평균환급률
				String returnMoney = tdList.get(6).getAttribute("innerText"); // 현재해약환급금
				String returnRate = tdList.get(7).getAttribute("innerText"); // 현재환급률

				logger.info("|--경과기간: {}", term);
				logger.info("|--납입보험료: {}", premiumSum);
				logger.info("|--해약환급금: {}", returnMoney);
				logger.info("|--환급률: {}", returnRate);
				logger.info("|_______________________");

				PlanReturnMoney planReturnMoney = new PlanReturnMoney();
				planReturnMoney.setPlanId(Integer.parseInt(info.planId));
				planReturnMoney.setGender(info.getGenderEnum().name());
				planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

				planReturnMoney.setTerm(term); // 경과기간
				planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계

				planReturnMoney.setReturnMoneyMin(minReturnMoney); // 최저해약환급금
				planReturnMoney.setReturnRateMin(minReturnRate); // 최저해약환급률

				planReturnMoney.setReturnMoneyAvg(avgReturnMoney); // 평균해약환급금
				planReturnMoney.setReturnRateAvg(avgReturnRate); // 평균해약환급률

				planReturnMoney.setReturnMoney(returnMoney); // 환급금
				planReturnMoney.setReturnRate(returnRate); // 환급률


				planReturnMoneyList.add(planReturnMoney);

//				info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
			}

			info.setPlanReturnMoneyList(planReturnMoneyList);

		} else {

			logger.info("해약환급금 내역이 없습니다.");
		}
	}



	public  void switchToNewWindow(WebDriver driver) {

		String mainWindow = driver.getWindowHandle();
		Set<String> openedWindows = driver.getWindowHandles();

		if(openedWindows.size() > 1) {
			for(String newWindow : openedWindows) {
				driver.switchTo().window(newWindow);
			}
			driver.close();
			driver.switchTo().window(mainWindow);
		}
	}

}
