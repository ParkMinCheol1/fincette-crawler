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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CrawlReturnMoneyList3 extends CrawlReturnMoneyListBehavior {

    public CrawlReturnMoneyList3(SeleniumCrawlingHelper helper,
        Class<? extends AbstractCrawler> productClass) {
        super(helper, productClass);
    }

    @Override
    public void crawlReturnMoneyList(Object obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingInfo crawlingInfo = (CrawlingInfo) obj;
            CrawlingProduct info = crawlingInfo.getInfo();

            // 해약환급금 탭 클릭
            clickReturnMoneyTab();

            // 해약환급금 tbody > tr List 불러오기, 공시
            List<WebElement> trs_공시 = getTrs(By.xpath("//tbody[@id='returnCancel1']//tr"));
            List<WebElement> trs_평균 = getTrs(By.xpath("//tbody[@id='returnCancel2']//tr"));
            List<WebElement> trs_최저 = getTrs(By.xpath("//tbody[@id='returnCancel3']//tr"));

            // tr List 순회하면서 PlanReturnMoneyList 스크랩
            setPlanReturnMoneyList(info, trs_공시,
                () -> new PlanReturnMoneyTdIdx(0, 1, 2, 3));

            setPlanReturnMoneyList(info, trs_평균,
                () -> new PlanReturnMoneyTdIdx(
                    0, 1,
                    -1, -1,
                    -1, -1,
                    2, 3));

            setPlanReturnMoneyList(info, trs_최저,
                () -> new PlanReturnMoneyTdIdx(
                    0, 1,
                    -1, -1,
                    2, 3,
                    -1, -1));

            LinkedHashMap<String, PlanReturnMoney> map = new LinkedHashMap<>();
            List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
            for(PlanReturnMoney p : planReturnMoneyList) {
                String term = p.getTerm();

                if(!map.containsKey(term)) {
                    map.put(term, p);
                }
            }

            List<PlanReturnMoney> newPlanReturnMoneyList = new ArrayList<>();
            for(String key : map.keySet()) {
                newPlanReturnMoneyList.add(map.get(key));
            }

            info.setPlanReturnMoneyList(newPlanReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }
}
