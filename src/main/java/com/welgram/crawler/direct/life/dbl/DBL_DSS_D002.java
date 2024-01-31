package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class DBL_DSS_D002 extends CrawlingDBLDirect {

// (무) e로운 건강검진 주요폴립 수술보험(2311)

  public static void main(String[] args) {
    executeCommand(new DBL_DSS_D002(), args);
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
      WaitUtil.waitFor(10);

      logger.info("월보험료 계산하기 클릭");
      driver.findElement(By.id("btnCal")).click();
      WaitUtil.waitFor(5);

      logger.info("생년월일 입력");
      setBirthday(info.fullBirth);

      logger.info("성별 클릭");
      setGender(info.gender);
      driver.findElement(By.id("btnOk")).click();
      WaitUtil.waitFor(5);

      // 원수사 홈페이지 오류로 새로고침 필요!
      driver.navigate().refresh();

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
