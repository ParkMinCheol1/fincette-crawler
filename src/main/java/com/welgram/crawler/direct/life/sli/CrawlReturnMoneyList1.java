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
import java.util.Optional;
import java.util.function.Supplier;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class CrawlReturnMoneyList1 extends CrawlReturnMoneyListBehavior {

    public CrawlReturnMoneyList1(SeleniumCrawlingHelper helper, Class<? extends AbstractCrawler> productClass) {
        super(helper, productClass);
    }

    @Override
    public void crawlReturnMoneyList(Object obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();

            // 해약환급금 탭 클릭
            clickReturnMoneyTab();

            // 해약환급금 tbody > tr List 불러오기
            List<WebElement> trs = new ArrayList<>();
            WebElement $tbody = null;
            try{
                trs = getTrs(By.xpath("//tbody[@id='pReturnCancel']//tr"));
            } catch (TimeoutException e){
                trs = getTrs(By.xpath("//tbody[contains(@id, 'returnCancel')]//tr"));
            }

            // tr List 순회하면서 PlanReturnMoneyList 스크랩
            setPlanReturnMoneyList(
                info,
                trs,
                () -> new PlanReturnMoneyTdIdx(0,1,2,3));

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }
}
