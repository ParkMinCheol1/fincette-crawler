package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import com.welgram.crawler.general.CrawlingProduct;

/**
 * @author aqua KB착한정기보험Ⅱ
 */
public class KBL_TRM_D001 extends CrawlingKBLDirect {

	public static void main(String[] args) {
		executeCommand(new KBL_TRM_D001(), args);
	}

	@Override
	protected boolean scrap(CrawlingProduct info) throws Exception {
		WebElement $button = null;
		WebElement $span = null;
		WebElement $div = null;

		waitLoadingBar();
		WaitUtil.loading(3);

		driver.findElement(By.xpath("//*[@id=\"btnCal_213910_1518175_2\"]")).click();

		logger.info("생년월일");
		WaitUtil.waitFor(3);
		setBirthday(info.getFullBirth());

		logger.info("성별");
		setGender(info.getGender());

		logger.info("보험료 계산하기");
		$button = driver.findElement(By.id("calculateResult"));
		click($button);

		logger.info("사전건강질문지");
		surveyAlert();

		logger.info("플랜 [{}] 선택", info.textType);
		By location = By.xpath("//div[@id='insurancePlanSubSlide']");
		setPlan(info, location);

		logger.info("납입 주기");
		location = By.id("paymentMethodsWrap");
		String script = "return $(\"dd[id='paymentMethodsWrap'] div.select-box a.anchor\").text()";
		setNapCycle(info.napCycle, location, script);

		logger.info("보험 기간");
		location = By.id("insuranceTermsWrap");
		script = "return $(\"dd[id='insuranceTermsWrap'] div.select-box a.anchor\").text()";
		setInsTerm(info.insTerm + " 만기", location, script);

		logger.info("납입기간");
		location = By.id("paymentTermsWrap");
		script = "return $(\"dd[id='paymentTermsWrap'] div.select-box a.anchor\").text()";
		info.napTerm = (info.napTerm.contains("년")) ? info.napTerm + " 납": info.napTerm + " 만기";
		setNapTerm(info.napTerm, location, script);

		logger.info("가입금액 입력");
		location = By.xpath("//*[@id='mainAmountLayer']//input");
		setInputAssureMoney(info, location);

		logger.info("보험료 계산하기 버튼 선택");
		$div = driver.findElement(By.id("insurancePlanCards"));
		click($div);

		logger.info("보험료 크롤링");
		By monthlyPremium = By.xpath("//div[@id='insurancePlanCards']//dl[1]//span");
		crawlPremium(info, monthlyPremium);

		logger.info("해약환급금 스크랩");
		crawlReturnMoneyListTwo(info);

		logger.info("스크린샷");
		takeScreenShot(info);

		return true;
	}


	public void setInputAssureMoney(Object... obj) throws SetAssureMoneyException {
		String title = "가입금액";
		CrawlingProduct info = (CrawlingProduct) obj[0];
		By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];

		String expectedAssureMoney = info.getAssureMoney();
		String actualAssureMoney = "";

		try {
			//가입금액을 원수사의 가입금액 포맷에 맞게 text값 수정
			expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000).replaceAll("[^0-9]", "");

			WebElement $assureMoneyInput = driver.findElement(location);

			actualAssureMoney = setTextToInputBox($assureMoneyInput, expectedAssureMoney).replaceAll("[^0-9]", "");

			//비교
			super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

		} catch (Exception e) {
			ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
			throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
		}
	}


	public String setTextToInputBox(WebElement $input, String text) throws Exception {
		String script = "return $(arguments[0]).val();";
		String actualValue = "";

		if ("input".equals($input.getTagName())) {
			//text 입력
			helper.waitElementToBeClickable($input);
			$input.click();
//            clickByJavascriptExecutor($input);
			$input.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
			$input.sendKeys(text);

			//실제 input에 입력된 value 읽어오기
			actualValue = String.valueOf(helper.executeJavascript(script, $input));
			logger.info("actual input value :: {}", actualValue);

		} else {
			logger.error("파라미터로 input element를 전달해주세요");
			throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ELEMENT);
		}

		return actualValue;
	}

}
