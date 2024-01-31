package com.welgram.crawler.direct.life.dbl;

import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class DBL_ACD_F001 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_ACD_F001(), args);
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

        calculate();
        setBirthday(info.birth);
        setGender(info);
        setJob();
        setProductType(info.planSubName);
        setAssureMoney(info.assureMoney);
        setInsTerm(info.insTerm);
        setNapTerm(info);
        calculate();
        crawlPremium(info);
        takeScreenShot(info);
        moveToElement(driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")));
        crawlReturnMoneyList(info);
        crawlReturnPremium(info);
    }
}
