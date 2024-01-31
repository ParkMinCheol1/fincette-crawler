package com.welgram.crawler.direct.life.hwl;

import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HWL_MDC_D003 extends CrawlingHWLDirect {

    // 한화생명 기본형 급여 e실손의료비보장보험(갱신형)(무)
    public static void main(String[] args) {
        executeCommand(new HWL_MDC_D003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        webCrawling(info);
        return true;
    }

    // 사이트웹
    private void webCrawling(CrawlingProduct info) throws Exception {

        setBirthday(By.xpath("//input[@id='birthdayDt01'][@type='text']"), info.getBirth());
        setGender(info.getGender(), By.xpath("//label[@for='gender010" + (info.getGender() + 1) + "']"));
        setJob();
        clickCalcButton(By.cssSelector("#calc_top_cont > div > a"));

        crawlPremium(By.id("resMonthbill"), info);
        takeScreenShot(info);
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        try {
            By position = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];
            WebElement premiumEl = driver.findElement(position);

            helper.waitVisibilityOfElementLocated(position);
            String premium = premiumEl.getText().replaceAll("[^0-9]", "");

            logger.info("==================================");
            logger.info("월 보험료 : {}", premium);
            logger.info("==================================");

            info.treatyList.get(0).monthlyPremium = premium;

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

}
