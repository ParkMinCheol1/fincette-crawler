package com.welgram.crawler.direct.life.dbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class DBL_DSS_F013 extends CrawlingDBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBL_DSS_F013(), args);
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
        setBirthday(info.birth);
        setGender(info);
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

        // 가입금액 변동 특약 처리
        setVariableTreatyAssureMoney(info);
    }
}
