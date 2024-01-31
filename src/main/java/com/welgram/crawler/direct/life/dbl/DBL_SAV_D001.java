package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DBL_SAV_D001 extends CrawlingDBLDirect {

// (무) e로운 DB저축보험(2311)

  public static void main(String[] args) {
    executeCommand(new DBL_SAV_D001(), args);
  }

  @Override
  protected boolean scrap(CrawlingProduct info) throws Exception {
    crawlFromHomepage(info);
    return true;
  }

  @Override
  protected void configCrawlingOption(CrawlingOption option) throws Exception {
    option.setBrowserType(BrowserType.Chrome);
    option.setImageLoad(true);
    option.setUserData(false);
  }

  public void crawlFromHomepage(CrawlingProduct info) throws Exception {

    driver.manage().window().maximize();
    WaitUtil.waitFor(3);

    logger.info("월보험료 계산하기 클릭");
    WaitUtil.waitFor(5);
    driver.findElement(By.id("btnCal")).click();
    WaitUtil.waitFor(3);

    logger.info("생년월일 입력");
    setBirthday(info.fullBirth);

    logger.info("성별 클릭");
    setGender(info.gender);
    driver.findElement(By.id("btnOk")).click();
    WaitUtil.waitFor(1);

    // 원수사 홈페이지 오류로 새로고침 필요!
    logger.info("새로고침");
    driver.navigate().refresh();

    logger.info("가입금액");
    setAssureMoney(info.assureMoney);
    WaitUtil.waitFor(3);

    logger.info("보험기간");
    helper.executeJavascript("window.scrollBy(0, 100)");
    WaitUtil.waitFor(3);
    setInsTerm(info.insTerm);
    WaitUtil.waitFor(3);

    logger.info("납입기간");
    helper.executeJavascript("window.scrollBy(0, 100)");
    WaitUtil.waitFor(3);
    setNapTerm(info.napTerm);
    WaitUtil.waitFor(3);

    logger.info("다시 계산하기 클릭");
    driver.findElement(By.id("btn01")).click();
    WaitUtil.waitFor(3);

    logger.info("월보험료 크롤링");
    crawlPremium(info);
    WaitUtil.waitFor(3);

    logger.info("해약환급금 크롤링");
    crawlReturnPremiumOne(info);
    WaitUtil.waitFor(3);
    crawlReturnMoneyListOne(info);
    WaitUtil.waitFor(3);

  }



  @Override
  public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

    String title = "가입금액";
    String expectedAssureMoney = (String) obj[0];
    expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000);

    try {

      WebElement $inputBox = driver.findElement(By.id("insuredAmt"));
      String actualAssureMoney = helper.sendKeys4_check($inputBox, expectedAssureMoney);

      super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

    } catch (Exception e) {
      ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
      throw new SetAssureMoneyException(e.getCause(), e.getMessage());
    }
  }



  public void crawlReturnPremiumOne (CrawlingProduct info) throws ReturnPremiumCrawlerException {

    try {
      String $returmPremium = driver.findElement(By.className("txtPremSum")).getText().replaceAll("[^0-9]", "");
      info.returnPremium = $returmPremium;

      logger.info("만기환급금 :: {}", info.returnPremium);

    } catch (Exception e) {
      throw new ReturnPremiumCrawlerException(e);
    }
  }



  // 최저보증이율, Min (평균,현재 공시이율), 현재공시이율
  public void crawlReturnMoneyListOne(Object... obj) throws ReturnMoneyListCrawlerException {

    try {
      CrawlingProduct info = (CrawlingProduct) obj[0];
      List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

      WebElement $divClass = driver.findElement(By.className("tabWrap"));
      WebElement $tabList = $divClass.findElement(By.tagName("ul"));

      String[] liName = new String[]{"최저보증이율", "Min", "현재공시이율"};

      for(int i = 0; i < liName.length; i++) {

        PlanReturnMoney planReturnMoney = null;
//        $tabList.findElement(By.xpath("//a[text()='" + liName[i] + "']")).click();        // todo :: xpath 안먹힌다... 탭 이동하는 다른 좋은 방법 없을까...

        if(i == 0) {
          $tabList.findElement(By.linkText("최저보증이율")).click();
          driver.findElement(By.className("more")).click();
        } else if (i == 1) {
          $tabList.findElement(By.tagName("small")).click();
          driver.findElement(By.cssSelector("#secondList > div > div > div:nth-child(2) > dl > a")).click();
        } else {
          $tabList.findElement(By.linkText("현재공시이율")).click();
          driver.findElement(By.cssSelector("#secondList > div > div > div:nth-child(3) > dl > a")).click();
        }

        WebElement $tBody = driver.findElement(By.cssSelector("#secondList > div > div > div.active > dl > dd > table > tbody"));
        List<WebElement> $trList = $tBody.findElements(By.tagName("tr"));

        for(int j = 0; j < $trList.size(); j++) {

          List<WebElement> $tdList = $trList.get(j).findElements(By.tagName("td"));

          if(i == 0){

            String term = $tdList.get(0).getText();
            String premiumSum = $tdList.get(1).getText().replaceAll("[^0-9]", "");
            String returnMoneyMin = $tdList.get(2).getText().replaceAll("[^0-9]", "");
            String returnRateMin = $tdList.get(3).getText();

            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("최저보증 해약환급금 : {}", returnMoneyMin);
            logger.info("최저보증 환급률 : {}", returnRateMin);
            logger.info("==========================");

            planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.age));

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);

            planReturnMoneyList.add(planReturnMoney);

          } else if (i == 1) {

            planReturnMoney = planReturnMoneyList.get(j);

            String returnMoneyAvg = $tdList.get(2).getText().replaceAll("[^0-9]", "");
            String returnRateAvg = $tdList.get(3).getText();

            logger.info("평균공시 해약환급금 : {}", returnMoneyAvg);
            logger.info("평균공시 환급률 : {}", returnRateAvg);
            logger.info("==========================");

            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);

          } else {

            planReturnMoney = planReturnMoneyList.get(j);

            String returnMoney =  $tdList.get(2).getText().replaceAll("[^0-9]", "");
            String returnRate = $tdList.get(3).getText();

            logger.info("공시 해약환급금 : {}", returnMoney);
            logger.info("공시 환급률 : {}", returnRate);
            logger.info("==========================");

            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

          }
        }

        info.planReturnMoneyList = planReturnMoneyList;

      }

    } catch (Exception e) {
      ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
      throw new ReturnMoneyListCrawlerException("해약환급금 크롤링 오류\n" + e.getMessage());
    }
  }

}
