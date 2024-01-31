package com.welgram.crawler.direct.life.cbl;

import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class CBL_TRM_F002 extends CrawlingCBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new CBL_TRM_F002(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        driver.manage().window().maximize();

        logger.info("상품명 찾기");
        findProductName(info.getProductNamePublic());

        logger.info("고객정보 입력");
        setUserInfo(info);

        logger.info("설계정보 입력");
        setJoinCondition(info);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }



    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
        int unit = MoneyUnit.만원.getValue();

        try {
            logger.info("해약환급금 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//a[normalize-space()='해약환급금']"));
            click($button);

            WebElement $table = driver.findElement(By.cssSelector("table[id^=surr]"));
            WebElement $tbody = $table.findElement(By.tagName("tbody"));
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $thList = $tr.findElements(By.tagName("th"));
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $thList.get(0).getText();
                String premiumSum = $tdList.get(1).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(3).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(4).getText();

                premiumSum = String.valueOf(Long.parseLong(premiumSum) * unit);
                returnMoney = String.valueOf(Long.parseLong(returnMoney) * unit);

                //해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                planReturnMoneyList.add(p);

                logger.info(
                "경과기간 : {} | 납입보험료 : {} | 환급금 : {} | 환급률 : {}",
                    term, premiumSum, returnMoney, returnRate
                );

                //만기환급금 세팅
                info.returnPremium = returnMoney;
            }

            // todo | 수정필요
            if (ProductKind.순수보장형.equals(info.getTreatyList().get(0).productKind)) {

                info.returnPremium = "0";
            }

            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e) {

            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}
