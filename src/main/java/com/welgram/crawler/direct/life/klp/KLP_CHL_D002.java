package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;


/**
 * 교보라이프플래닛 - (무)라이프플래닛e플러스어린이보험(어린이)
 */
// 2023.11.20 | 최우진 | (무)라이프플래닛e플러스어린이보험(어린이)
public class KLP_CHL_D002 extends CrawlingKLP {

	public static void main(String[] args) {
		executeCommand(new KLP_CHL_D002(), args);
	}



	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {

		driver.manage().window().maximize();

		helper.oppositionWaitForLoading("plnnrBrdt");

		// 부모나이
		logger.info("부모생일 : " + info.getParent_FullBirth());
		parentChangeBirth(info.getParent_FullBirth());

		// 부모성별
		parentGender();

		// 자녀 - 어린이/태아 중 선택
		childSet(info.getProductCode());

		// 자녀 생년월일
		logger.info(info.getFullBirth());
		helper.sendKeys1_check(By.id("childBrdt"), info.getFullBirth());

		// 자녀성별
		childGender(info.getGender());

		// 보험료 확인/가입
		setConfirmPremium(By.id("btnCalculMyInsuPay"));

		// 가입금액	| todo | 금액계산에 대한 내용 확인 필요
		logger.info("가입금액 입력란이 사실 진단보험금 금액으로 표기되고 있음 "
			+ "실제 가입금액의 경우 드랍다운 아래 작은 회색글씨로 표기중"
			+ "진짜 보험가입금액은 해당위치의 금액을 확인해야함ㄴ");
		// todo | 아래 '* 5' 의 경우, 가입금액과 보험가입금액의 내용이 달라서 추가한 계산내용입니다
		logger.info("가입금액 확인 : " + Integer.parseInt(info.getAssureMoney()) * 5 / 10000 + "만원");
		childPremium(String.valueOf(Integer.parseInt(info.getAssureMoney()) * 5 / 10000));

		// 자녀 보험기간
		childInsTerm(info.getInsTerm());

		// 자녀 납입기간
		childNapTerm(info.getNapCycle(), info.getNapTerm());

		// 환급률
		maturityReturnPerCent(info.getInsuName());

		// 결과 확인하기
		confirmResult();

		// 보험료
		getPremium("#premiumLabel2", info);

		// 해약환급금(예시표)
		getReturns("cancel1", info);

		return true;
	}



	protected void childPremium(String premium) throws Exception {

		((JavascriptExecutor) driver).executeScript(
			"arguments[0].scrollIntoView(true);",
			driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1)"))
		);

		Thread.sleep(500);

		new Actions(driver).moveToElement(driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1)"))).perform();
		element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_3 > div.box_sel > span")));
		WaitUtil.waitFor("childPremium");
		element.click();
		element = driver.findElement(By.cssSelector("._sel_option.sel_m"));
		elements = element.findElements(By.tagName("li"));

		String getPremium;
		int premiumSum = Integer.parseInt(premium);
		logger.info("가입금액 :: " + premiumSum);

		for(WebElement li : elements) {
			element = li.findElement(By.tagName("span"));
			getPremium = element.getText().replace(",", "").replace("만원", "");

			if(getPremium.contains("진단보험금")) {
				continue;
			}

			// 실제 가입금액
			// getPremium = String.valueOf(Integer.parseInt(getPremium) / 5);
			logger.info("getPremium :: " + getPremium);
			if(getPremium.equals(Integer.toString(premiumSum))) {
				li.click();
				WaitUtil.waitFor(2);
				break;
			}
		}
	}
}
