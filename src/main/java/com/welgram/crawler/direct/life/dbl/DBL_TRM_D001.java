package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class DBL_TRM_D001 extends CrawlingDBLDirect {

// (무) e로운 장해Plus정기보험(2311)

  public static void main(String[] args) {
    executeCommand(new DBL_TRM_D001(), args);
  }

  @Override
  protected boolean scrap(CrawlingProduct info) throws Exception {
    crawlFromHomepage(info);
    return true;
  }

  public void crawlFromHomepage(CrawlingProduct info) throws Exception {

      driver.manage().window().maximize();
      WaitUtil.waitFor(10);

      logger.info("월보험료 계산하기 클릭");
      try{
          helper.waitElementToBeClickable(By.id("btnCal")).click();
      } catch (Exception e) {
          helper.waitElementToBeClickable(By.id("btnBirth")).click();
      }

      WaitUtil.waitFor(5);

      logger.info("생년월일 입력");
      setBirthday(info.fullBirth);

      logger.info("성별 클릭");
      setGender(info.gender);
      driver.findElement(By.id("btnOk")).click();
      WaitUtil.waitFor(3);

      // 원수사 홈페이지 오류로 새로고침 필요!
      logger.info("새로고침");
      driver.navigate().refresh();

      logger.info("플랜 :: 기본");
      driver.findElement(By.name("tabPlan")).click();
      WaitUtil.waitFor(10);

      logger.info("가입금액");
      setAssureMoney(info.assureMoney);
      WaitUtil.waitFor(3);

      logger.info("보험기간");
      helper.executeJavascript("window.scrollBy(0, 100)");
      setInsTerm(info.insTerm);
      WaitUtil.waitFor(3);

      logger.info("납입기간");
      helper.executeJavascript("window.scrollBy(0, 100)");
      setNapTerm(info.napTerm);
      WaitUtil.waitFor(3);

      logger.info("다시 계산하기 클릭");
      driver.findElement(By.id("btn01")).click();
      WaitUtil.waitFor(1);

      logger.info("월보험료 크롤링");
      crawlPremium(info);
      WaitUtil.waitFor(3);

      logger.info("해약환급금 크롤링");
      crawlReturnMoneyList(info);
      crawlReturnPremium(info);

  }


}
