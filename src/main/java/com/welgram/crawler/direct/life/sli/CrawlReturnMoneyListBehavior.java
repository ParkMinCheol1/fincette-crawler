package com.welgram.crawler.direct.life.sli;

import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.AbstractCrawler;
import com.welgram.crawler.FinLogger;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.PlanReturnMoneyTdIdx;
import com.welgram.crawler.helper.SeleniumCrawlingHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class CrawlReturnMoneyListBehavior {

    protected SeleniumCrawlingHelper helper;
    protected FinLogger logger;

    public CrawlReturnMoneyListBehavior(SeleniumCrawlingHelper helper, Class<? extends AbstractCrawler> productClass) {
        this.helper = helper;
        this.logger = FinLogger.getFinLogger(productClass);
    }

    public abstract void crawlReturnMoneyList(Object obj) throws ReturnMoneyListCrawlerException;

    protected void clickReturnMoneyTab() {
        helper.click(
            helper.waitVisibilityOfElementLocated(By.xpath("//a[text()='해약환급금']"))
            , "해약환급금 탭"
        );
    }

    protected List<WebElement> getTrs(By position) {
        return helper.waitPesenceOfAllElementsLocatedBy(position);
    }

    protected void setPlanReturnMoneyList(CrawlingProduct info, List<WebElement> trs, Supplier<PlanReturnMoneyTdIdx> getTdIdx) {

        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        PlanReturnMoneyTdIdx tdIdx = getTdIdx.get();
        int termIdx = tdIdx.getTerm();                  // 납입기간
        int premiumSumIdx = tdIdx.getPremiumSum();      // 합계 보험료
        int rMoneyIdx = tdIdx.getReturnMoney();         // 환급금(공시)
        int rRateIdx = tdIdx.getReturnRate();           // 환급률(공시)
        int rMoneyMinIdx = tdIdx.getReturnMoneyMin();   // 환급금(최저)
        int rRateMinIdx = tdIdx.getReturnRateMin();     // 환급률(최저)
        int rMoneyAvgIdx = tdIdx.getReturnMoneyAvg();   // 환급금(평균)
        int rRateAvgIdx = tdIdx.getReturnRateAvg();     // 환급률(평균)

        Map<String, PlanReturnMoney> planReturnMoneyMap = new HashMap<>();
        planReturnMoneyList.forEach( i -> planReturnMoneyMap.put(i.getTerm(), i));

        for (WebElement tr : trs) {

            helper.moveToElementByJavascriptExecutor(tr);

            List<WebElement> tds = tr.findElements(By.tagName("td"));

            String term = tds.get(termIdx).getAttribute("innerHTML");
            PlanReturnMoney planReturnMoney = planReturnMoneyMap.getOrDefault(term, new PlanReturnMoney(term));

            if (premiumSumIdx != -1) planReturnMoney.setPremiumSum(tds.get(premiumSumIdx).getAttribute("innerHTML"));
            if (rMoneyIdx != -1) planReturnMoney.setReturnMoney(tds.get(rMoneyIdx).getAttribute("innerHTML"));
            if (rRateIdx != -1) planReturnMoney.setReturnRate(tds.get(rRateIdx).getAttribute("innerHTML"));
            if (rMoneyMinIdx != -1) planReturnMoney.setReturnMoneyMin(tds.get(rMoneyMinIdx).getAttribute("innerHTML"));
            if (rRateMinIdx != -1) planReturnMoney.setReturnRateMin(tds.get(rRateMinIdx).getAttribute("innerHTML"));
            if (rMoneyAvgIdx != -1) planReturnMoney.setReturnMoneyAvg(tds.get(rMoneyAvgIdx).getAttribute("innerHTML"));
            if (rRateAvgIdx != -1) planReturnMoney.setReturnRateAvg(tds.get(rRateAvgIdx).getAttribute("innerHTML"));

            planReturnMoneyList.add(planReturnMoney);
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        logger.info("해약환급금 테이블 스크랩 : " +
            planReturnMoneyList);
    }

    static class TdElement {
        private String text;
        private int rowSpan;
        private int colSpan;

        public TdElement(WebElement element) {
            String rowspan = element.getAttribute("rowspan");
            String colspan = element.getAttribute("colspan");
            String text = element.getText();
            this.rowSpan = rowspan == null ? 0 : (Integer.parseInt(rowspan));
            this.colSpan = colspan == null ? 0 : (Integer.parseInt(colspan));
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public int getRowSpan() {
            return rowSpan;
        }

        public int getColSpan() {
            return colSpan;
        }
    }

}
