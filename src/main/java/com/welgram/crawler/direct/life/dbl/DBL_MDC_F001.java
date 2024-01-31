package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class DBL_MDC_F001 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_MDC_F001(), args);
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

        btnClick(By.cssSelector("#detailcal"));
        setBirthday(info.birth);
        setGender(info);
        setJob(); // 직업 교사 불가
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

        crawlReturnMoneyList(info);
        crawlReturnPremium(info);
    }



    @Override
    public void setJob(Object... obj) throws SetJobException {

        try {
            logger.info("직업선택클릭 (대분류)");
            elements = driver.findElements(By.cssSelector("#jobcode > option"));
            int jobSize = elements.size();

            for (int i = 0; i < jobSize; i++) {

                if (elements.get(i).getText().trim().contains("고위공무원, 기업체 및 비영리단체 임원")) {
                    elements.get(i).click();
                    break;
                }
            }
            WaitUtil.waitFor(1);

            logger.info("직업선택 (소분류)");
            elements = driver.findElements(By.cssSelector("#jobcodeDetail > option"));
            int job2Size = elements.size();

            for (int i = 0; i < job2Size; i++) {

                if (elements.get(i).getText().trim().contains("기업체임원")) {
                    elements.get(i).click();
                    break;
                }
            }
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetJobException(e);
        }
    }


    // 현심
    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            info.returnPremium = "0";

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }
}
