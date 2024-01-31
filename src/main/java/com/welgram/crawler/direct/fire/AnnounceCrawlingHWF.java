package com.welgram.crawler.direct.fire;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;


/*
* 2020.11.20
* @author 조하연
* HWF 상품 공시실용 클래스
* */


//한화손해보험 상품 중 공시실에서 크롤링해오는 상품에 대해서는 AnnounceCrawlingHWF를 상속받는다.
public abstract class AnnounceCrawlingHWF extends SeleniumCrawler {
  /*
  * 크롤링 옵션 정의 메서드
  *  (여기서 예외가 발생한다면 이 메서드를 호출한 보험상품파일의 Exception catch block에서 예외를 처리하게 된다.)
  * */
  protected void setChromeOptionHWF(CrawlingProduct info) throws Exception{
    CrawlingOption option = info.getCrawlingOption();

    option.setBrowserType(CrawlingOption.BrowserType.Chrome);
    option.setImageLoad(false);
    option.setUserData(false);

    info.setCrawlingOption(option);
  }

  //가입연령 확인 메서드
  abstract protected void checkJoinAge(int age, String planId) throws Exception;

  /*
  * 생년월일 설정 메서드
  *  @param fullBirth : 생년월일    ex.19900606
  * */
  protected void setBirth(String fullBirth) {
    WebElement birthInputBox = driver.findElement(By.id("i_jumin"));
    birthInputBox.clear();
    birthInputBox.sendKeys(fullBirth);
  }

  /*
   * 성별 설정 메서드
   *  @param gender : 성별  (0 : 남성, 1 : 여성)
   * */
  protected void setGender(int gender) {
    String genderText  = (gender == MALE) ? "남성" : "여성";

    WebElement dropdown = driver.findElement(By.id("i_no"));
    dropdown.findElement(By.xpath("//option[contains(.,'" + genderText + "')]")).click();
  }

  //직업 설정 메서드
  protected void setJob() throws Exception{
    driver.findElement(By.cssSelector("#jobSearch")).click();

    currentHandle = driver.getWindowHandle();

    if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
      helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

      driver.findElement(By.linkText("분류대로 찾기")).click();
      waitLoadingImg();

      driver.findElement(By.xpath("//span[contains(.,'사무 종사자')]")).click();
      waitLoadingImg();

      driver.findElement(By.xpath("//span[contains(.,'행정 사무원')]")).click();
      waitLoadingImg();

      driver.findElement(By.xpath("//span[contains(.,'회사 사무직 종사자')]")).click();
      WaitUtil.loading(1);

      driver.findElement(By.id("btnOk")).click();
    }

    helper.switchToWindow("", driver.getWindowHandles(), true);
  }

  //차량용도 설정 메서드
  protected void setCar() throws Exception{
    WebElement dropdown = driver.findElement(By.id("i_car"));
    dropdown.findElement(By.xpath("//option[. = '비운전자']")).click();

    WaitUtil.loading(1);
  }

  /*
   * 가입구분 설정 메서드
   *  @param textType     ex.표준형, 선택형Ⅱ
   * */
  protected void setType(String textType) throws Exception{
    WebElement dropdown = driver.findElement(By.id("gubun"));

    if(textType.equals("표준형")){
      dropdown.findElement(By.xpath("//option[. = '1종(기본납입형)(표준형)']")).click();
    }else{
      dropdown.findElement(By.xpath("//option[. = '2종(기본납입형)(선택형Ⅱ)']")).click();
    }

    WaitUtil.loading(1);
  }

  /*
   * 보험기간 설정 메서드
   *  @param insTerm : 보험기간     ex.5년,10년,15년...
   * */
  protected void setInsTerm(String insTerm) {
    WebElement dropdown = driver.findElement(By.name("bogi"));
    dropdown.findElement(By.xpath("//option[contains(.,'" + insTerm + "')]")).click();
  }

  /*
   * 납입기간 설정 메서드
   *  @param napTerm : 납입기간     ex.전기납...
   * */
  protected void setNapTerm(String napTerm) {
    WebElement dropdown = driver.findElement(By.name("napgi"));
    dropdown.findElement(By.xpath("//option[. = '" + napTerm + "']")).click();
  }

  /*
   * 납입주기 설정 메서드
   *  @param napCycle : 납입주기  (01 : 월납, 02 : 연납, 00 : 일시납)
   * */
  protected void setNapCycle(String napCycle) throws Exception{
    if(napCycle.equals("01")) {
      napCycle = "월납";
    } else if(napCycle.equals("02")) {
      napCycle = "연납";
    } else if(napCycle.equals("00")){
      napCycle = "일시납";
    }

    WebElement dropdown = driver.findElement(By.id("cycle"));
    dropdown.findElement(By.xpath("//option[. = '" + napCycle + "']")).click();

    WaitUtil.loading(1);
  }

  /*
   * 특약 설정 메서드
   *  @param1 info : 크롤링상품
   *  @param2 item : 특약 1개
   * */
  protected void setTreaty(CrawlingProduct info, CrawlingTreaty item) throws Exception {
    String treatyName = item.treatyName;  //특약명
    String assureMoney = String.valueOf(item.assureMoney/10000);  //가입금액

    // 담보
    List<WebElement> elements = driver.findElements(By.xpath("//div[@id='content']/div[3]/form/div[6]/table/tbody/tr"));

    for (WebElement trEl : elements) {
      int tdSize = trEl.findElements(By.tagName("td")).size();

      int premiumNth = tdSize > 2 ? 5 : 3;
      int assureMoneyNth = tdSize > 2 ? 4 : 2;

      //WebElement titleEl = trEl.findElement(By.cssSelector("th"));
      WebElement titleEl = trEl.findElement(By.tagName("th"));
      WebElement premiumEl = trEl.findElement(By.cssSelector("td:nth-child(" + premiumNth + ")"));
      WebElement assureMoneyEl = trEl.findElement(By.cssSelector("td:nth-child(" + assureMoneyNth + ")"));

      String productName = titleEl.getText();
      String premium = premiumEl.getText();

      // 가입금액을 세팅한다
      if (treatyName.equals(productName)){
        logger.info("같은 상품명 찾음!! :: {}", productName);

        List<WebElement> options = assureMoneyEl.findElements(By.tagName("option"));
        for (WebElement option : options) {
          String optionVal = option.getAttribute("value");
          String optionTxt = option.getText();
          if(optionVal.equals(assureMoney)) {
            helper.click(option);
            break;
          }
          if (assureMoney.equals("30") && optionTxt.equals("30만원(외래25,약제5)")){
            helper.click(option);
            break;
          }
        }
      }
    } // for: tr
  }

  /*
   * 버튼 클릭 메서드
   *  @param id : 클릭할 element
   * */
  protected void calcBtnClick(By id) {
    driver.findElement(id).click();
    waitLoadingImg();
  }

  //다시 계산 버튼 클릭 메서드
  protected void reCalcBtnClick() throws Exception {
    calcBtnClick(By.id("btnReCalc"));
  }

  /*
  * 주계약 보험료 세팅 메서드
  * @param info : 크롤링상품
  *
  * 보통 treatyList의 첫번째에 주계약이 위치한다.
  * 하지만 가끔 주계약이 첫번째에 위치하지 않은 경우도 있는데 그럴 때는 상품마스터에서 주계약을 맨 위에 위치하도록 수정한다.
  * 그래야만 마지막에 제대로 보혐료를 크롤링해 온다.)
  * */
  protected void setPremiums(CrawlingProduct info) {
    String premium = driver.findElement(By.id("gnPrm")).getText();
    info.treatyList.get(0).monthlyPremium = premium.replaceAll("[^0-9]", "");
  }

  /*
   * 해약환급금 조회 및 세팅 메서드
   * @param info : 크롤링상품
   * */
  protected void getReturnPremiums(CrawlingProduct info) {
    //해약환급금 버튼 클릭을 위해 해당 엘리먼트가 있는 곳으로 스크롤을 조정한다.
    Actions actions = new Actions(driver);
    WebElement element = driver.findElement(By.id("btnPopCancel"));
    actions.moveToElement(element);
    actions.perform();

    driver.findElement(By.id("btnPopCancel")).click();

    currentHandle = driver.getWindowHandle();

    if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
      helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

      List<WebElement> elements = driver.findElements(By.xpath("//tbody/tr"));
      List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

      for (int i = 0; i < elements.size(); i++) {
        WebElement trEl = elements.get(i);

        String term = trEl.findElement(By.xpath(".//th")).getText();  //경과기간
        String premiumSum = trEl.findElement(By.xpath(".//td[4]")).getText(); //납입보험료
        String returnPremium = trEl.findElement(By.xpath(".//td[5]")).getText(); //환급금

        logger.debug("기간: {}", term);
        logger.debug("납입보험료: {}", premiumSum);

        logger.info("______해약환급급[{}]_______ ", i);
        logger.info("|--경과기간: {}", term);
        logger.info("|--납입보험료: {}", premiumSum);
        logger.info("|--최저납입보험료: {}", premiumSum);
        logger.info("|--환급금: {}", returnPremium);
        logger.info("|_______________________");

        PlanReturnMoney planReturnMoney = new PlanReturnMoney();

        planReturnMoney.setPlanId(Integer.parseInt(info.planId));
        planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
        planReturnMoney.setInsAge(Integer.parseInt(info.age));

        planReturnMoney.setTerm(term);
        planReturnMoney.setPremiumSum(premiumSum);

        planReturnMoneyList.add(planReturnMoney);
        info.returnPremium = returnPremium.replaceAll("[^0-9]", "");
      }

      info.planReturnMoneyList = planReturnMoneyList;
    }

    driver.findElement(By.linkText("닫기")).click();

    helper.switchToWindow("", driver.getWindowHandles(), true);
  }

  //로딩이미지 명시적 대기
  protected void waitLoadingImg() {
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("popLoading")));
  }
}
