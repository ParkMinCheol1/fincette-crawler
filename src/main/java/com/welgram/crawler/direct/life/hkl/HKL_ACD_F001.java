package com.welgram.crawler.direct.life.hkl;

import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class HKL_ACD_F001 extends CrawlingHKLAnnounce {

    // (무)처음만난흥국생명보험
    public static void main(String[] args) {
        executeCommand(new HKL_ACD_F001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        takeScreenShot(info);

        crawlPremium(
            info,
            By.cssSelector("#frmPage > dd.dd_first > div:nth-child(4) > table > tbody > tr:nth-child(1) > td.fi_cost")
        );

        crawlReturnMoneyList(info);

        return true;
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            By by = (By) obj[1];

            element = driver.findElement(by);
            //  premium에 '원'이 붙어서 원을 제거해 주었습니다.
            String premium = element.getText().replace(",", "").replace("원", "").replace("만", "");
            logger.info("월 보험료: " + premium + "원");
            logger.info("====================");
            info.treatyList.get(0).monthlyPremium = premium;

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        try {
            // 해약환급금보기 창변환
            logger.info("해약환급금보기 창변환");
            logger.info("====================");
            helper.click(By.cssSelector(".first li:nth-child(3) strong"));

            logger.info("해약환급금 가져오기");
            logger.info("====================");

            //elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#frmPage > dd.dd_third > div.table_wrap.overflow > table > tbody > tr"));

            List<WebElement> planTypeElement = driver.findElements(By.cssSelector("#frmPage > dd.dd_third > h4"));

            for (int i = 0; i < planTypeElement.size(); i++) {

                logger.info("페이지 확인 : " + planTypeElement.get(i).getText() + " / 텍스트타입 확인 : " + info.textType);

                if (planTypeElement.get(i).getText().contains(info.textType)) {

                    String gen = "";
                    if (info.gender == MALE) {
                        gen = "남자";
                    }
                    if (info.gender == FEMALE) {
                        gen = "여자";
                    }

                    if (planTypeElement.get(i).findElement(By.cssSelector("p")).getText().contains(gen)) {

                        logger.info("플랜선택 : " + planTypeElement.get(i).getText());

                        int child = (i * 2) + 3;

                        elements = driver.findElements(By.cssSelector("#frmPage > dd.dd_third > div:nth-child(" + child + ") > table > tbody > tr"));
                        int elementsSize = elements.size();

                        logger.info("tr사이즈 : " + elementsSize);

                        String term;
                        String premiumSum;
                        String returnMoney;
                        String returnRate;

                        // 주보험 영역 Tr 개수만큼 loop
                        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

                        for (int j = 0; j < elementsSize; j++) {
                            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                            term = elements.get(j).findElements(By.tagName("td")).get(0).getText();
                            premiumSum = elements.get(j).findElements(By.tagName("td")).get(1).getText();
                            returnMoney = elements.get(j).findElements(By.tagName("td")).get(2).getText();
                            returnRate = elements.get(j).findElements(By.tagName("td")).get(3).getText();

                            logger.info(term + " :: 납입보험료 :: " + premiumSum + " :: 해약환급금 :: " + returnMoney);
                            logger.info("========================================================================");
                            planReturnMoney.setTerm(term);
                            planReturnMoney.setPremiumSum(premiumSum);
                            planReturnMoney.setReturnMoney(returnMoney);
                            planReturnMoney.setReturnRate(returnRate);
                            planReturnMoneyList.add(planReturnMoney);

                            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
                        }
                        info.setPlanReturnMoneyList(planReturnMoneyList);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }
    }
}
