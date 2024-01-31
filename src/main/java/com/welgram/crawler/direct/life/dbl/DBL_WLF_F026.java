package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingDBL;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DBL_WLF_F026 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_WLF_F026(), args);
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
        setTranceAge("55세");
        setJob();
        setProductType(info.textType);
        setAssureMoney(info.assureMoney);
        setInsTerm(info.insTerm);
        setNapTerm(info);
        calculate();

        btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));

        btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));
        crawlPremium(info);
        takeScreenShot(info);
        moveToElement(driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")));

        crawlReturnMoneyListAll(info);
        crawlReturnPremium(info);

    }
}
