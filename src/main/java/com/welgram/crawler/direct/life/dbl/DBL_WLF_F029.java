package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

// (무) 10년 더드림플러스 유니버셜종신보험(보증비용부과형)(2301)(해약환급금보증형,체증형,가입금액형)

public class DBL_WLF_F029 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_WLF_F029(), args);
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

        driver.manage().window().maximize();
        btnClick(By.cssSelector("#detailcal"));
        setBirthday(info.birth);
        setGender(info);
        WaitUtil.waitFor(3);
        setTranceAge("55세");
        setJob();
        setProductType(info.textType);
        setAssureMoney(info.assureMoney);
        setInsTerm(info.insTerm);
        setNapTerm(info);
        WaitUtil.waitFor(1);
        calculate();

//        setTreaties(info.treatyList);
        btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));

//        setSubTreatyConditions(info);
        btnClick(driver.findElement(By.xpath("//a[@class='btnB']")));
        crawlPremium(info);
        takeScreenShot(info);
        moveToElement(driver.findElement(By.cssSelector("#direct_result > div > a.btnB.greenType")));

        crawlReturnMoneyListAll(info);

        crawlReturnPremium(info);
    }
}
