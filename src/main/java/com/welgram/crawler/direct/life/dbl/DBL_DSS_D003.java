package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class DBL_DSS_D003 extends CrawlingDBLDirect {

// (무) e로운 뇌혈관질환보장보험(해약환급금 미지급형)(2311)

  public static void main(String[] args) {
    executeCommand(new DBL_DSS_D003(), args);
  }

  @Override
  protected boolean scrap(CrawlingProduct info) throws Exception {

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
      logger.info("새로고침");
      driver.navigate().refresh();

      logger.info("플랜 :: 기본");
      driver.findElement(By.name("tabPlan")).click();
      WaitUtil.waitFor(5);

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

      return true;

  }
    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        driver.findElement(By.className("more")).click();
        driver.findElement(By.cssSelector("#secondList > div > ul > li.active > a")).click();
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            WebElement $table = driver.findElement(By.tagName("tbody"));
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
                planReturnMoney.setGender(CrawlingProduct.Gender.M.getDesc().equals(info.getGender()) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

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
