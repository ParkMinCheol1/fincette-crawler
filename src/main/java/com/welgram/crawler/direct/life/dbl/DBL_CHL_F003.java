package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DBL_CHL_F003 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_CHL_F003(), args);
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

        logger.info("상단 보험료계산탭 선택");
        helper.findFirstDisplayedElement(By.xpath("//a[contains(.,'보험료계산')]"), 3L).get().click();

        btnClick(By.cssSelector("#detailcal"));
        setBirthday(info.birth);
        setGender(info);
        setProductType(info.textType);
        setAssureMoney(info.assureMoney);
        setInsTerm(info.insTerm);
        setNapTerm(info);
        calculate();

        setTreaties(info.treatyList);
        btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));

        setSubTreatyConditions(info);
        btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));
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

            elements = driver.findElements(By.xpath("//*[@id='refund_result']/div[2]/table/tbody/tr"));
            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                String term = "";
                String premiumSum = "";
                String returnMoney = "";
                String returnRate = "";

                term = tr.findElements(By.tagName("th")).get(0).getText();
                premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                returnMoney = tr.findElements(By.tagName("td")).get(3).getText();
                returnRate = tr.findElements(By.tagName("td")).get(4).getText();

                logger.info("경과기간   :: {}", term);
                logger.info("납입보험료 :: {}", premiumSum);
                logger.info("해약환급금 :: {}", returnMoney);
                logger.info("환급률    :: {}", returnRate);
                logger.info("=================================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);

            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e){
            throw new ReturnMoneyListCrawlerException(e);
        }
    }
}
