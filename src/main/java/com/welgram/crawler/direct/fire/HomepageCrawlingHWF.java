package com.welgram.crawler.direct.fire;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;


/*
* 2020.11.20
* @author 조하연
* HWF 상품 홈페이지용 클래스
* */


//한화손해보험 상품 중 홈페이지에서 크롤링해오는 상품에 대해서는 HomepageCrawlingHWF 상속받는다.
public abstract class HomepageCrawlingHWF extends SeleniumCrawler {

  //inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
  protected void setTextToInputBox(By id, String text) {
    WebElement element = driver.findElement(id);
    element.clear();
    element.sendKeys(text);
  }


  // select 태그에서 해당 text의 option을 클릭한다(홈페이지, 공시실 둘 다 사용 가능한 메서드)
  protected void selectOption(By element, String text) {
    Select select = new Select(helper.waitElementToBeClickable(element));
    select.selectByVisibleText(text);
  }


  // select 태그에서 해당 text의 option을 클릭한다(홈페이지, 공시실 둘 다 사용 가능한 메서드)
  protected void selectOption(WebElement selectEl, String text) {
    Select select = new Select(selectEl);
    select.selectByVisibleText(text);
  }


  //버튼 클릭 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
  protected void btnClick(By element) {
    driver.findElement(element).click();
    waitHomepageLoadingImg();
  }


  //크롤링 옵션 정의 메서드
  protected void setChromeOptionHWF(CrawlingProduct info) throws Exception{
    CrawlingOption option = info.getCrawlingOption();

    option.setBrowserType(CrawlingOption.BrowserType.Chrome);
    option.setImageLoad(false);
    option.setUserData(false);

    info.setCrawlingOption(option);
  }


  //가입연령 확인 메서드
  abstract protected void checkJoinAge(int age, String planId) throws Exception;


  //홈페이지 생년월일 설정 메서드(1개 입력)
  protected void setHomepageBirth(String fullBirth) {
    setTextToInputBox(By.id("ctrctBirthday"), fullBirth);
  }


  //홈페이지 생년월일 설정 메서드(2개 입력)
  protected void setHomepageBirth(String childBirth, String parentBirth) {
    logger.info("자녀 생년월일 설정");
    setTextToInputBox(By.id("relpcBirthday"), childBirth);

    logger.info("부모 생년월일 설정");
    setTextToInputBox(By.id("ctrctBirthday"), parentBirth);
  }


  //홈페이지 성별 설정 메서드(1개 입력)
  protected void setHomepageGender(int gender) {
    String genderTag  = (gender == MALE) ? "gender_man" : "gender_woman";
    driver.findElement(By.cssSelector("label[for='" + genderTag + "']")).click();
  }


  //홈페이지 성별 설정 메서드(2개 입력)
  protected void setHomepageGender(int childGender, int parentGender) {
    String childGenderTag  = (childGender == MALE) ? "gender_man" : "gender_woman";
    String parentGenderTag = (parentGender == MALE) ? "ctrGender_man" : "ctrGender_woman";

    logger.info("자녀 성별 설정");
    driver.findElement(By.cssSelector("label[for='" + childGenderTag + "']")).click();

    logger.info("부모 성별 설정");
    driver.findElement(By.cssSelector("label[for='" + parentGenderTag + "']")).click();
  }


  //홈페이지 차량 설정 메서드(자가용으로 고정)
  protected void setHomepageCar() {
    btnClick(By.xpath("//dt[contains(text(), '자가용')]"));
  }


  //홈페이지 보험기간 설정 메서드
  protected void setHomepageInsTerm(String insTerm) {
//    WebElement dropdown = driver.findElement(By.name("insTerms"));
//    dropdown.findElement(By.xpath("//option[contains(.,'" + insTerm + "')]")).click();
    selectOption(By.name("insTerms"), insTerm);
  }


  //홈페이지 납입기간 설정 메서드
  protected void setHomepageNapTerm(String napTerm) {
//    WebElement dropdown = driver.findElement(By.name("payTerms"));
//    dropdown.findElement(By.xpath("//option[contains(.,'" + napTerm + "')]")).click();
    selectOption(By.name("payTerms"), napTerm);
  }


  //홈페이지 1개의 드롭박스 내에서 보험기간, 납입기간을 동시에 세팅하는 경우에 사용하는 메서드
  protected void setHomepageTerms(CrawlingProduct info) {
    String insTerm = info.insTerm;
    String napTerm = info.napTerm;
    String productCode = info.productCode;
    String terms = insTerm + "/" + napTerm;

    if(productCode.equals("HWF_CHL_D004")) {
      //HWF_CHL_D004 경우 보험기간 "년" -> "세"로 바꿔야함.
      terms = insTerm.replaceAll("년", "세") + "/" + napTerm;
    }

    logger.info("보험기간,납입기간 : {}", terms);
    setHomepageInsTerm(terms);
  }


  //홈페이지 납입주기 설정 메서드
  protected void setHomepageNapCycle(String napCycle) throws Exception{
    if(napCycle.equals("01")) {
      napCycle = "월납";
    } else if(napCycle.equals("02")) {
      napCycle = "연납";
    } else if(napCycle.equals("00")){
      napCycle = "일시납";
    }

//    WebElement dropdown = driver.findElement(By.id("pymMtd"));
//    dropdown.findElement(By.xpath("//option[. = '" + napCycle + "']")).click();
    selectOption(By.id("pymMtd"), napCycle);
    WaitUtil.waitFor(1);
  }


  //홈페이지 계산 버튼 클릭 메서드
  protected void homepageCalcBtnClick(By element) throws Exception{
    btnClick(element);
    waitHomepageLoadingImg();
  }


  //다시 계산 버튼 클릭 메서드
  protected void homepageReCalcBtnClick() throws Exception {
    btnClick(By.id("btnReCalcAllPlan"));
  }


  /*
  * 주계약 보험료 세팅 메서드
  *
  * 보통 treatyList의 첫번째에 주계약이 위치한다.
  * 하지만 가끔 주계약이 첫번째에 위치하지 않은 경우도 있는데 그럴 때는 상품마스터에서 주계약을 맨 위에 위치하도록 수정한다.
  * 그래야만 마지막에 제대로 보혐료를 크롤링해 온다.)
  * */
  protected void setHomepagePremiums(CrawlingProduct info) {
    String[] premiums = driver.findElement(By.id("selPlanInsarc")).getText().split("/");

    String insMoney = premiums[0].replaceAll("[^0-9]", "");
    String saveMoney = premiums[1].replaceAll("[^0-9]", "");

    logger.info("보장보험료 : {}", insMoney);
    logger.info("적립보험료 : {}", saveMoney);
    logger.info("월 보험료(total 보험료) : {}", Integer.valueOf(insMoney) + Integer.valueOf(saveMoney));

    info.treatyList.get(0).monthlyPremium = insMoney;
    info.savePremium = saveMoney;
  }


  //해약환급금 조회 메서드
  protected void getHomepageReturnPremiums(CrawlingProduct info) throws Exception {
    btnClick(By.id("btnPopRefund"));

    List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

    List<WebElement> trList = driver.findElements(By.cssSelector("#refundTbodyArea > tr"));
    for (int i = 0; i < trList.size(); i++) {
      WebElement tr = trList.get(i);

      String term = tr.findElement(By.xpath(".//th")).getText();     //경과기간
      String premiumSum = tr.findElement(By.xpath(".//td[1]")).getText();  // 납입보험료

      String returnMoneyMin = tr.findElement(By.xpath(".//td[2]")).getText();  // 최저 환급금
      String returnRateMin = tr.findElement(By.xpath(".//td[3]")).getText();  // 최저 환급률

      String returnMoney = tr.findElement(By.xpath(".//td[4]")).getText();  // 공시이율 환급금
      String returnRate = tr.findElement(By.xpath(".//td[5]")).getText();  // 공시이율 환급률

      String returnMoneyAvg = tr.findElement(By.xpath(".//td[6]")).getText();  // 평균공시이율 환급금
      String returnRateAvg = tr.findElement(By.xpath(".//td[7]")).getText();  // 평균공시이율 환급률


      logger.info("______해약환급급[{}]_______ ", i);
      logger.info("|--경과기간: {}", term);
      logger.info("|--납입보험료: {}", premiumSum);
      logger.info("|--해약환급금: {}", returnMoney);
      logger.info("|--최저납입보험료: {}", premiumSum);
      logger.info("|--최저해약환급금: {}", returnMoneyMin);
      logger.info("|--최저해약환급률: {}", returnRateMin);
      logger.info("|--평균해약환급금: {}", returnMoneyAvg);
      logger.info("|--평균해약환급률: {}", returnRateAvg);
      logger.info("|--환급률: {}", returnRate);
      logger.info("|_______________________");

      PlanReturnMoney planReturnMoney = new PlanReturnMoney();

      planReturnMoney.setPlanId(Integer.parseInt(info.planId));
      planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
      planReturnMoney.setInsAge(Integer.parseInt(info.age));

      planReturnMoney.setTerm(term); // 경과기간
      planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계(납입보험료)

      planReturnMoney.setReturnMoneyMin(returnMoneyMin); // 최저해약환급금
      planReturnMoney.setReturnRateMin(returnRateMin); // 최저환급률

      planReturnMoney.setReturnMoney(returnMoney); // 환급금
      planReturnMoney.setReturnRate(returnRate); // 환급률

      planReturnMoney.setReturnMoneyAvg(returnMoneyAvg); // 평균해약환급금
      planReturnMoney.setReturnRateAvg(returnRateAvg); // 평균환급률

      planReturnMoneyList.add(planReturnMoney);
      info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
    }

    info.planReturnMoneyList = planReturnMoneyList;

    logger.info("만기환급금 : {}", info.returnPremium);

    btnClick(By.id("btnCloseRefund"));
    WaitUtil.waitFor(2);
  }

  //로딩이미지 명시적 대기
  protected void waitHomepageLoadingImg() {
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadCont")));
  }
}
