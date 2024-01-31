package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DBL_TRM_F018 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_TRM_F018(), args);
    }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setBrowserType(BrowserType.Chrome);
        option.setImageLoad(true);
        option.setUserData(false);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromHomepage(info);

        return true;
    }


    public void crawlFromHomepage(CrawlingProduct info) throws Exception {

        WaitUtil.loading();
        calculate();
        setBirthday(info.birth);
        setGender(info);
        setProductType(info.textType);
        setAssureMoney(info.assureMoney);
        setInsTerm(info.insTerm);
        setNapTerm(info);
        calculate();

//      setTreaties(info.treatyList);
//      btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));
//
//      setSubTreatyConditions(info);
//      btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));
        crawlPremium(info);
        takeScreenShot(info);
        moveToElement(driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")));

        crawlReturnMoneyListAll(info);
        crawlReturnPremium(info);
    }



    public void crawlReturnMoneyListAll(Object... obj) throws ReturnMoneyListCrawlerException {

        try {

            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            logger.info("해약환급금예시 클릭");
            driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")).click();
            WaitUtil.waitFor(3);

            elements = driver.findElements(By.xpath("//*[@id=\"refund_result\"]/div[2]/table/tbody/tr"));
            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                String term = "";
                long premiumSum = 0;
                long returnMoneyMin = 0;
                String returnRateMin = "";
                long returnMoneyAvg = 0;
                String returnRateAvg = "";
                long returnMoney = 0;
                String returnRate = "";

                int unit = 10000;
                term = tr.findElements(By.tagName("th")).get(0).getText();
                premiumSum = Long.parseLong(tr.findElements(By.tagName("td")).get(5).getText().replaceAll("\\D", "")) * unit;

                returnMoneyMin = Long.parseLong(tr.findElements(By.tagName("td")).get(6).getText().replaceAll("\\D", "")) * unit;
                returnRateMin = tr.findElements(By.tagName("td")).get(7).getText();

                returnMoneyAvg = Long.parseLong(tr.findElements(By.tagName("td")).get(8).getText().replaceAll("\\D", "")) * unit;
                returnRateAvg = tr.findElements(By.tagName("td")).get(9).getText();

                returnMoney = Long.parseLong(tr.findElements(By.tagName("td")).get(10).getText().replaceAll("\\D", "")) * unit;
                returnRate = tr.findElements(By.tagName("td")).get(11).getText();

                logger.info("경과기간   :: {}", term);
                logger.info("납입보험료 :: {}", premiumSum);
                logger.info("해약환급금 :: {}", returnMoney);
                logger.info("환급률    :: {}", returnRate);
                logger.info("최저해약환급금 :: {}", returnMoneyMin);
                logger.info("최저해약환급률 :: {}", returnRateMin);
                logger.info("평균해약환급금 :: {}", returnMoneyAvg);
                logger.info("평균해약환급률 :: {}", returnRateAvg);
                logger.info("=================================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(String.valueOf(premiumSum));
                planReturnMoney.setReturnMoneyMin(String.valueOf(returnMoneyMin));
                planReturnMoney.setReturnRateMin(returnRateMin);
                planReturnMoney.setReturnMoney(String.valueOf(returnMoney));
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoney.setReturnMoneyAvg(String.valueOf(returnMoneyAvg));
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                planReturnMoneyList.add(planReturnMoney);
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }
}
