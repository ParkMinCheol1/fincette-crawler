package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class DBF_DRV_D032 extends CrawlingDBFDirect {

	// 무배당 프로미라이프 다이렉트 참좋은운전생활 운전자보험2311(CM) 신규상품
	public static void main(String[] args) {
		executeCommand(new DBF_DRV_D032(), args);
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

		WebElement $button = driver.findElement(By.cssSelector("#wrap > div.wrap_float_container > div > a"));
		$button.click();

		logger.info("생일 입력: {}", info.fullBirth);
		setBirthday(By.id("birthday"), info.fullBirth);

		logger.info("성별선택");
		setGender("sxCd", info.gender);

		logger.info("운전형태 선택: 자가용 고정");
		setVehicle("oprtVhDvcd", "자가용");

		logger.info("보험료 확인하기 버튼 클릭");
		driver.findElement(By.xpath("//span[contains(.,'보험료 확인하기')]")).click();
		waitDirectLoadingImg();
		WaitUtil.waitFor(3);

		try {
			logger.info("팝업 있는지 확인");
			WaitUtil.waitFor(2);
			driver.findElement(By.id("calcDriverBannerCloseBtn")).click();
			logger.info("팝업 닫기");
		} catch (Exception e) {
			logger.info("팝업 없음");
		}

		waitDirectLoadingImg();
		WaitUtil.waitFor(3);


		logger.info("보험기간 선택: {}", info.insTerm);
		setInsTerm("selArcTrm", info.insTerm);

		logger.info("특약셋팅");
		setTreaties1(info, info.getTreatyList());

		logger.info("다시 계산하기 버튼 클릭");
		reComputeCssSelect(By.xpath("//span[contains(.,'다시 계산')]"));

		logger.info("월납입보험료 가져오기");
		crawlPremium1(info);

		logger.info("스크린샷 찍기");
		takeScreenShot(info);
		WaitUtil.waitFor(1);

		logger.info("해약환급금 예시 버튼 클릭");
		driver.findElement(By.xpath("//span[contains(.,'해약환급금 예시')]")).click();
		waitDirectLoadingImg();

		logger.info("해약환급금 저장");
		WaitUtil.waitFor(3);
		getReturnPremium(info);
	}

	@Override
	public void setGender(Object... obj) throws SetGenderException {
		String title = "성별";
		String tagName = (String) obj[0];
		int gender = (int) obj[1];
		String expectedGenderText = (gender == MALE) ? "남자" : "여자";
		String script = "return $('input[name=" + tagName + "]:checked').attr('id');";

		try {
			WebElement $genderLabel = driver.findElement(By.xpath("//span[@class='input_radio chk_orange']/input[@title='"+ expectedGenderText +"']/following-sibling::label"));

			helper.waitElementToBeClickable($genderLabel).click();

			String actualGenderId = String.valueOf(helper.executeJavascript(script, expectedGenderText));
			String actualGenderText = driver.findElement(By.xpath("//label[@for='" + actualGenderId + "']")).getText();

			super.printLogAndCompare(title, expectedGenderText, actualGenderText);
			WaitUtil.loading(1);
		} catch (Exception e) {
			throw new SetGenderException(e.getMessage());
		}
	}

	@Override
	public void setVehicle(Object... obj) throws SetVehicleException {
		String title = "운전 형태";
		String tagName = (String) obj[0];
		String expectedVehicleText = (String) obj[1];
		String script = "return $('input[name=" + tagName + "]:checked').attr('id');";

		try {
			WaitUtil.waitFor(2);

			WebElement $vehicleLabel = driver.findElement(By.xpath("//span[@class='input_radio chk_orange']/input[@name='oprtVhDvcd']/following-sibling::label"));
			helper.waitElementToBeClickable($vehicleLabel).click();

			String actualVehicleId = String.valueOf(helper.executeJavascript(script, expectedVehicleText));
			String actualVehicleText = driver.findElement(By.xpath("//label[@for='" + actualVehicleId + "']")).getText();

			super.printLogAndCompare(title, expectedVehicleText, actualVehicleText);


		} catch (Exception e) {
			throw new SetVehicleException(e.getMessage());
		}
	}

	@Override
	public void setInsTerm(Object... obj) throws SetInsTermException {
		String title = "보험기간";
		String tagName = (String) obj[0];
		String expectedTermText = (String) obj[1];
		String actualTermText = "";

		try {
			List<WebElement> $termList = driver.findElements(By.xpath(".//dl[@class='accodion_list']/dt/strong[@class='txt_green']"));

			String arcTrm = $termList.get(0).getText().trim();
			String pymTrm = $termList.get(1).getText().trim();
			String pymMtd = $termList.get(2).getText().trim();

			logger.info("보험기간 : {}, {}, {}", arcTrm, pymTrm, pymMtd);

		} catch (Exception e) {
			throw new SetInsTermException(e.getMessage());
		}
	}

	// 특약 미가입 처리부분 수정
	public void setTreaties1(CrawlingProduct info, List<CrawlingTreaty> welgramTreatyList) throws Exception {
		WaitUtil.waitFor(1);

		List<CrawlingTreaty> treatyList = info.getTreatyList();

		logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
		boolean result = advancedCompareTreaties(treatyList, welgramTreatyList , new CrawlingTreatyEqualStrategy2());
		if (result) {
			logger.info("특약 정보 모두 일치");
		} else {
			logger.info("특약 정보 불일치");
			throw new Exception();
		}
	}

	public void crawlPremium1(Object... obj) throws PremiumCrawlerException {

		try {
			CrawlingProduct info = (CrawlingProduct) obj[0];

			WebElement $monthlyPremiumElement = driver.findElement(By.xpath("//*[@id='sForm']/div/div[1]/div/div[1]/div[1]/div/dl[1]/dd/strong[1]"));
			String premium = $monthlyPremiumElement.getText().trim().replaceAll("[^0-9]", "");
			info.treatyList.get(0).monthlyPremium = premium;
			logger.info("월 보험료 확인 : " + premium);

			if ("0".equals(info.treatyList.get(0).monthlyPremium)) {
				throw new Exception("주계약 보험료는 0원일 수 없습니다");
			}
		} catch (Exception e) {
			throw new PremiumCrawlerException(e.getMessage());
		}
	}

	public void getReturnPremium(Object...obj) throws Exception {
		CrawlingProduct info = (CrawlingProduct) obj[0];

		List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
		driver.findElement(By.xpath("//*[@id='popExCancelLayer']/div[2]/div[1]/ul/li[2]/a")).click();
//		List<WebElement> $refundTrList = helper.waitPresenceOfElementLocated(By.id("tbodyExCancel1")).findElements(By.tagName("tr"));
		List<WebElement> $refundTrList = driver.findElements(By.xpath("//*[@id='tbodyExCancel2']//tr"));

		for (WebElement tr : $refundTrList) {

			String term = helper.waitVisibilityOf(tr.findElement(By.tagName("th"))).getText();
			logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
			String premiumSum = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(0)).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

			String returnMoney = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(1)).getText().replaceAll("[^0-9]", "");
			logger.info("해약환급금 크롤링:: 환급금 :: " + returnMoney);
			String returnRate = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(2)).getText();
			logger.info("해약환급금 크롤링:: 환급률 :: " + returnRate);

			PlanReturnMoney planReturnMoney = new PlanReturnMoney();
			planReturnMoney.setTerm(term);
			planReturnMoney.setPremiumSum(premiumSum);
			planReturnMoney.setReturnMoneyMin(returnMoney);
			planReturnMoney.setReturnRateMin(returnRate);
			planReturnMoneyList.add(planReturnMoney);

			info.returnPremium = returnMoney;
		}

		info.planReturnMoneyList = planReturnMoneyList;
	}
}
