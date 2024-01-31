package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.NotFoundNapTermException;
import com.welgram.common.except.NotFoundPensionAgeException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @author SungEun Koo <aqua@welgram.com> 하나생명
 */

public abstract class CrawlingHNL extends SeleniumCrawler {

  protected void setGender(int gender) {
    String genderName = (gender == 0) ? "male" : "female";
    driver.findElement(By.cssSelector("#" + genderName)).click();
  }

  // 성별
  protected void setGender(By id, int gender) throws InterruptedException {
    element = driver.findElement(id);
    elements = element.findElements(By.tagName("label"));
    element = elements.get(gender);
    element.click();
    WaitUtil.waitFor(3);
  }

  // 성별
  protected void setGender2(int gender) throws InterruptedException {
    if (gender == 0) {
      helper.waitPresenceOfElementLocated(By.id("male02")).click();
    } else {
      helper.waitPresenceOfElementLocated(By.id("female02")).click();
    }
  }

  // 생년월일
  protected void setBirth(By id, String birth) throws InterruptedException {
    element = helper.waitPresenceOfElementLocated(id);
    element.click();
    logger.debug(birth);
    element.clear();
    element.sendKeys(birth);
  }

  // 생년월일 연금저축
  protected void setBirthYY(String birth) throws Exception {
    helper.waitForCSSElement(".blockOverlay");
    element = driver.findElement(By.id("birth"));
    element.click();
    WaitUtil.waitFor(3);
    element.clear();
    element.sendKeys(birth);
    WaitUtil.waitFor(3);
  }

  // 보험료 확인 어린이 (보험료 확인 클릭 시 팝업창 뜸)
  protected void calculatePremiumChild(By id) throws InterruptedException {
    element = driver.findElement(id);
    element = element.findElement(By.tagName("a"));
    element.click();
    WaitUtil.waitFor(3);
  }

  // 보험료 확인
  protected void calculatePremium(By id) throws Exception {
    wait.until(ExpectedConditions.elementToBeClickable(id));
    element = driver.findElement(id);
    //element = element.findElement(By.tagName("a"));
    element.click();

    helper.waitForCSSElement(".blockOverlay");
    WaitUtil.loading(2);
  }

  // 팝업창
  protected void popUp() throws Exception {
    try {
      // 알림창
      Alert alert = driver.switchTo().alert();
      @SuppressWarnings("unused")
      String text = alert.getText();
      alert.accept();

      // 사용자에게 올바른 메세지 전송됬는지 확인
      // Assert.assertEquals("17.4.1일 이후 개정 소득세법 비과세 요건 중 보험기간
      // 10년미만으로 보험차익과세(납입보험료≥해약환급금)
      // 계약으로 분류됩니다.", text);

      helper.waitForCSSElement(".blockOverlay");
      logger.debug("로딩끝");
    } catch (NoAlertPresentException e) {
      //e.printStackTrace();
    	logger.info("팝업창 없음");
    }
  }

  // 보험료 확인 알림 창
  protected void calculatePremiumPop(By id, String insTerm) throws Exception {
    // 보험기간
    if (insTerm.indexOf("5") > -1 || insTerm.indexOf("3") > -1 ) {
      try {
        element = driver.findElement(id);
        element = element.findElement(By.tagName("a"));
        element.click();
        WaitUtil.waitFor(3);
        // 알림창
        Alert alert = driver.switchTo().alert();
        // String text = alert.getText();
        alert.accept();

        // 사용자에게 올바른 메세지 전송됬는지 확인
        // Assert.assertEquals("17.4.1일 이후 개정 소득세법 비과세 요건 중 보험기간
        // 10년미만으로 보험차익과세(납입보험료≥해약환급금)
        // 계약으로 분류됩니다.", text);

        helper.waitForCSSElement(".blockOverlay");
        logger.debug("로딩끝");
      } catch (NoAlertPresentException e) {
    	  logger.info("팝업 없음");
    	  //e.printStackTrace();
      }
    } else {

      element = driver.findElement(id);
      element = element.findElement(By.tagName("a"));
      element.click();

      helper.waitForCSSElement(".blockOverlay");
      logger.debug("로딩끝");
      //waitFor();
    }
  }

  protected void setNapTermChild(By id, String napTerm) throws InterruptedException {
    element = driver.findElement(id);
    elements = element.findElements(By.tagName("input"));
    napTerm = napTerm.replace("년", "").replace("세", "");
    if (napTerm.equals("일시납")) {
      napTerm = "0";
    }
    for (WebElement input : elements) {
      if (input.getAttribute("value").equals(napTerm)) {
        input.click();
        WaitUtil.waitFor(3);
        break;
      }
    }
  }

  // 보험기간 어린이
  protected void setInsTermChild(By id, String insTerm) throws InterruptedException {

    elements = driver.findElements(By.cssSelector("#div_inspd > input"));

    insTerm = insTerm.replace("년", "");
    for (int i = 0; i < elements.size(); i++) {
      WebElement insTermEl = elements.get(i);
      if (insTermEl.getAttribute("value").equals(insTerm)) {
        element = element.findElements(By.tagName("strong")).get(i);
        if (element.getText().contains("어린이")) {
          element.click();
          WaitUtil.waitFor(3);
          break;
        }
      }
    }

  }

  // 보험기간
  protected void setInsTerm(By id, String insTerm) throws Exception {
    elements = helper.waitPesenceOfAllElementsLocatedBy(
        By.cssSelector("#div_inspd > input[name='insPd']"));

    insTerm = insTerm.replace("년", "").replace("세", "");

    for (WebElement input : elements) {
      if (input.getAttribute("value").equals(insTerm)) {
        input.click();
        break;
      }
    }
    WaitUtil.loading(2);
  }

  // 납입기간 연금저축
  protected void setNapTermYY(String napTerm) throws Exception {
    boolean result = false;
    // element = driver.findElement(By.id("div_paypd"));
    element = driver.findElement(By.id("selPayPd"));
    elements = element.findElements(By.tagName("option"));

    WaitUtil.waitFor(3);
    for (WebElement option : elements) {
      if (option.getAttribute("value").equals((napTerm).replace("년", ""))) {
        option.click();
        result = true;
        helper.waitForCSSElement(".blockOverlay");
        WaitUtil.waitFor(3);
        break;
      }
    }
    if (!result) {
      throw new NotFoundNapTermException("해당나이에서는 납입기간 " + napTerm + "을 선택할 수 없습니다.");
    }
  }

  // 납입기간
  protected void setNapTerm(By id, String napTerm) throws Exception {
    boolean result = false;

    elements = driver.findElements(By.cssSelector("#div_paypd > label"));

    napTerm = napTerm.replace("년", "").replace("세", "");

    for (WebElement napTermLabelEl : elements) {
      if (napTermLabelEl.getAttribute("innerText").indexOf(napTerm) > -1) {
        String napTerId = napTermLabelEl.getAttribute("for");
        driver.findElement(By.id(napTerId)).click();
//        napTermLabelEl.click();
        result = true;
        WaitUtil.waitFor(3);
        break;
      }
    }
    if (!result) {
      throw new Exception("해당나이에서는 납입기간 " + napTerm + "년을 선택할 수 없습니다.");
    }

  }

  // 계산구분
  protected void calculateSection() throws InterruptedException {
    element = driver.findElement(By.tagName("tbody"));
    elements = element.findElements(By.tagName("tr"));
    element = elements.get(5).findElement(By.tagName("td"));
    element = element.findElement(By.tagName("input"));

    element.click();
    WaitUtil.waitFor(3);
  }

  // 보험가입금액 직접계산
  protected void setPremiumYY(String premium) throws InterruptedException {
    element = driver.findElement(By.className("input_info"));
    element = element.findElement(By.id("directChk"));
    element.click();
    WaitUtil.waitFor(3);

    element = driver.findElement(By.id("freeNtryAmt"));
    element.clear();
    element.sendKeys(premium);
    WaitUtil.waitFor(3);
  }

  // 보험가입금액 직접계산
  protected void setPremium(By id, String premium) throws InterruptedException {
    element = driver.findElement(id);
    element.clear();
    element.sendKeys(premium);
    WaitUtil.waitFor(3);
  }

  // 적용 버튼
  protected void applyButton(By id) throws Exception {
    element = driver.findElement(id);
    element.click();
    helper.waitForCSSElement(".blockOverlay");
  }

  // 보험가입금액
  protected void getPremium(By id, String premium) throws InterruptedException {
    // 가입금액 2구좌
    int pre = Integer.parseInt(premium) / 1000;
    String pre2 = String.valueOf(pre) + "천만";

    {
      element = wait.until(ExpectedConditions.visibilityOfElementLocated(id));
      elements = element.findElements(By.tagName("li"));
      element = elements.get(0).findElement(By.tagName("input"));

      // li 요소가 분리되있음
      if (element.getAttribute("value").equals(pre2)) {
        WebElement element = elements.get(0).findElement(By.tagName("a"));
        element.click();
      } else {
        element = wait.until(ExpectedConditions.visibilityOfElementLocated(id));
        elements = element.findElements(By.tagName("li"));
        element = elements.get(1).findElement(By.tagName("input"));
        if (element.getAttribute("value").equals(pre2)) {
          WebElement element = elements.get(1).findElement(By.tagName("a"));
          element.click();
        }
      }
    }
    WaitUtil.waitFor(3);
  }

  // 해약환급금 조회
  protected void getreturn(By id, CrawlingProduct info) throws Exception {
    element = driver.findElement(id);
    elements = element.findElements(By.tagName("li"));
    element = elements.get(1);
    element.click();

    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".blockOverlay")));
//    waitForCSSElement(".blockOverlay");
    WaitUtil.waitFor(3);

    // 해약환급금 iframe
    driver.switchTo().frame("wp-epd-012Iframe");

    String year = "";
    boolean result = true;

    while (result) {
      element = driver.findElement(By.id("listTable"));
      element = element.findElement(By.tagName("tbody"));
      elements = element.findElements(By.tagName("tr"));
      for (WebElement tr : elements) {
        year = tr.findElements(By.tagName("td")).get(0).getText();
        if (year.equals(info.napTerm)) {
          info.returnPremium = tr.findElements(By.tagName("td")).get(6).getText().replace(",", "");
          result = false;
          break;
        }
      }
      element = driver.findElement(By.className("paging-wrap"));
      element = element.findElement(By.tagName("span"));
      elements = element.findElements(By.tagName("a"));
      element = elements.get(1);
      element.click();
      WaitUtil.waitFor(3);
    }

    //info.savePremium = Integer.parseInt(info.premium + "0000");
    //info.treatyList.get(0).monthlyPremium = "0";
    info.errorMsg = "";

    driver.findElement(By.cssSelector("#btnClose2")).click();
  }

  // 연금수령액확인
  protected void getCrawlingResultYY(CrawlingProduct info, String iframeId, String fixedAnnuityYear, String fixedAPremium) throws Exception {
    element = driver.findElement(By.id("btnFreeCalc"));
    element.click();
    helper.waitForCSSElement(".blockOverlay");

    element = driver.findElement(By.id("txtAntyRecvYear"));
    info.annuityPremium = element.getText().replace(",", "");

    driver.findElement(By.cssSelector("#result > div.button_area.external > ul > li:nth-child(3) > a")).click();
    helper.waitForCSSElement(".blockOverlay");
    WaitUtil.waitFor(1);

    driver.switchTo().frame(iframeId);




    String fixedAnnuityPremium;

    if(driver.findElement(By.cssSelector(fixedAnnuityYear)).getAttribute("textContent").equals("10년확정")){
      WaitUtil.waitFor(1);
      fixedAnnuityPremium = driver.findElement(By.cssSelector(fixedAPremium)).getAttribute("textContent").replaceAll("[^0-9]", "");
      info.fixedAnnuityPremium = fixedAnnuityPremium + "0000";
      logger.info("10년 확정형 연금수령액 : "+info.fixedAnnuityPremium);
      WaitUtil.waitFor(1);
      driver.findElement(By.cssSelector("#btnClose2 > img")).click();
    }

  }

  // 보험료 조회
  protected void getCrawlingResult(By id, CrawlingProduct info) {

    String premium = "";
    String returnPremium = "";
    element = driver.findElement(id);

    premium = element.getText();
    premium = premium.replace(",", "");

    info.treatyList.get(0).monthlyPremium = premium;
    info.errorMsg = "";

    if (info.productCode.equals("HNL_DSS_D001")) {
      element = driver.findElement(By.id("bestNtryAmt"));
      returnPremium = element.getText().replaceAll("[^0-9]", "");
      info.returnPremium = returnPremium;
    }
  }

  // 연금개시나이
  protected void setPensionAge(String annAge, String age) throws Exception {
    boolean result = false;
    element = driver.findElement(By.id("div_inspd"));
    elements = element.findElement(By.id("selInsPd")).findElements(By.tagName("option"));

    WaitUtil.waitFor(3);
    for (WebElement option : elements) {
      if (option.getAttribute("value").equals(annAge)) {
        option.click();
        result = true;
        helper.waitForCSSElement(".blockOverlay");
        WaitUtil.waitFor(3);
        break;
      }
    }

    if (!result) {
      throw new NotFoundPensionAgeException(age + "세에서는 연금개시나이" + annAge + "세를 선택할 수 없습니다.");
    }
  }

  protected void getTreaties(CrawlingProduct info) throws Exception {

    getMainTreaty(info);

  }

  protected void getMainTreaty(CrawlingProduct info) throws Exception {

  }

  protected void getSubTreaties(CrawlingProduct info) {
  }

	protected void checkProductMaster(CrawlingProduct info, String el) {
		try {
			for (CrawlingTreaty item : info.treatyList) {
				String treatyName = item.treatyName;

				element = driver.findElement(By.cssSelector(el));
				element.click();
				String prdtName = element.getText();

				if (treatyName.indexOf(prdtName) > -1){
					info.siteProductMasterCount ++;
					logger.info("담보명 확인 완료 !! ");
				}
			}
		}catch(Exception e){
			logger.info("담보명 확인 에러 발생 !!");
		}

	}
}
