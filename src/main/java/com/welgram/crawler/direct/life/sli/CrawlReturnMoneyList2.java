package com.welgram.crawler.direct.life.sli;

import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.AbstractCrawler;
import com.welgram.crawler.direct.life.sli.CrawlReturnMoneyListBehavior;
import com.welgram.crawler.direct.life.sli.CrawlingInfo;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.PlanReturnMoneyTdIdx;
import com.welgram.crawler.helper.SeleniumCrawlingHelper;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CrawlReturnMoneyList2 extends CrawlReturnMoneyListBehavior {

    public CrawlReturnMoneyList2(SeleniumCrawlingHelper helper, Class<? extends AbstractCrawler> productClass) {
        super(helper, productClass);
    }

    @Override
    public void crawlReturnMoneyList(Object obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();
            String planNum = crawlingInfo.getValue();

            clickReturnMoneyTab();

            List<WebElement> trs = getTrs(By.xpath("//tbody[@id='returnCancel']//tr"));


            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            for (WebElement tr : trs) {

                helper.moveToElementByJavascriptExecutor(tr);

                List<WebElement> tds = tr.findElements(By.tagName("td"));
                List<WebElement> tds2 = tr.findElements(By.cssSelector("td.data" + planNum));
                WebElement term = tds.get(0);
                WebElement premiumSum = tds2.get(0);
                WebElement returnMoney = tds2.get(1);
                WebElement returnRate = tds2.get(2);

                planReturnMoneyList.add(
                    new PlanReturnMoney(
                        term.getAttribute("innerHTML"),
                        premiumSum.getAttribute("innerHTML"),
                        returnMoney.getAttribute("innerHTML"),
                        returnRate.getAttribute("innerHTML")
                    )
                );
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.info("해약환급금 테이블 스크랩 : " + planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }

}
