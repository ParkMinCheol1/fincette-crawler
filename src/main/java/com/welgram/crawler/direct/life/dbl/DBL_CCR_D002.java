package com.welgram.crawler.direct.life.dbl;

import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class DBL_CCR_D002 extends CrawlingDBLDirect {

// (무) e로운 암보험(갱신형)(2311)

  public static void main(String[] args) {
    executeCommand(new DBL_CCR_D002(), args);
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
      try {
          driver.findElement(By.id("btnCal")).click();
      } catch (Exception e) {
          driver.findElement(By.id("btnBirth")).click();
      }
      WaitUtil.waitFor(5);

      logger.info("생년월일 입력");
      setBirthday(info.fullBirth);

      logger.info("성별 클릭");
      setGender(info.gender);
      driver.findElement(By.id("btnOk")).click();
      WaitUtil.waitFor(5);
      removeNextButton();

      // 원수사 홈페이지 오류로 새로고침 필요!
      logger.info("새로고침");
      driver.navigate().refresh();

      logger.info("플랜 :: 기본");
      setPlan(info.textType);
      WaitUtil.waitFor(3);

      logger.info("가입금액");
      moveToElementByScrollIntoView(driver.findElement(By.cssSelector("#planInfo > ul > li:nth-child(1)")));
      setAssureMoney(info.assureMoney);
      WaitUtil.waitFor(3);

      logger.info("보험기간");
      moveToElementByScrollIntoView(driver.findElement(By.cssSelector("#planInfo > ul > li:nth-child(2)")));
      setInsTerm(info.insTerm);
      WaitUtil.waitFor(3);

      logger.info("납입기간");
      moveToElementByScrollIntoView(driver.findElement(By.cssSelector("#planInfo > ul > li:nth-child(3)")));
      setNapTerm(info.napTerm);
      WaitUtil.waitFor(3);

      logger.info("특약 선택");
      moveToElementByScrollIntoView(driver.findElement(By.cssSelector("#planInfo > ul > li:nth-child(4)")));
      List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
          .filter(t -> t.productGubun == ProductGubun.선택특약)
          .collect(Collectors.toList());
      setTreaties(subTreatyList);

      logger.info("다시 계산하기 클릭");
      moveToElementByScrollIntoView(driver.findElement(By.cssSelector("#contents > div.ip-product-head")));
      driver.findElement(By.id("prodDimmed")).click();
      WaitUtil.waitFor(1);

      logger.info("월보험료 크롤링");
      moveToElementByScrollIntoView(driver.findElement(By.cssSelector("#contents > span")));
      crawlPremium(info);
      WaitUtil.waitFor(3);

      logger.info("해약환급금 크롤링");
      crawlReturnMoneyList(info);
      crawlReturnPremium(info);

  }


    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            logger.info("전체보기 클릭");
            helper.waitElementToBeClickable(By.className("more")).click();
        } catch (Exception e) {
            logger.info("전체보기가 없습니다");
        }

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            WebElement $tableLocation = driver.findElement(By.id("secondList"));
            WebElement $table = $tableLocation.findElement(By.tagName("tbody"));
            List<WebElement> $trList = $table.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                String term = $tr.findElement(By.xpath("./td[1]")).getText();
                String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();

                logger.info("경과기간 : {}", term);
                logger.info("납입보험료 : {}", premiumSum);
                logger.info("해약환급금 : {}", returnMoney);
                logger.info("환급률 : {}", returnRate);
                logger.info("==========================");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

            }

            info.planReturnMoneyList = planReturnMoneyList;

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
            throw new ReturnMoneyListCrawlerException("해약환급금 크롤링 오류\n" + e.getMessage());
        }
    }

}
