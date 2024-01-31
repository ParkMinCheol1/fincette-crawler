package com.welgram.crawler.direct.life;

import com.google.gson.Gson;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CrawlingPLI extends SeleniumCrawler {

    public static final Logger logger = LoggerFactory.getLogger(CrawlingPLI.class);

    protected void selectByVisibleTxt(String selectAttribute, String visibleTxtOfOption) throws Exception {
        helper.click(By.xpath(
            "//select" + selectAttribute + "//option[contains(text(),'" + visibleTxtOfOption + "')]"));
    }

    protected void selectByValue(String selectAttribute, String valueOfOption) throws Exception {
        helper.click(By.xpath(
            "//select" + selectAttribute + "//option[@value='" + valueOfOption + "']"));
    }

    protected void getPlanReturnMoney(CrawlingProduct info) {

        helper.waitVisibilityOfAllElementsLocatedBy(
                By.cssSelector(".accordion")).stream().filter(el -> el.getText().contains("해약환급금"))
            .findFirst().ifPresent(el -> {

                // 해약환급금 아코디언 펼치기
                el.findElement(By.tagName("button")).click();

                // 해약환급금 테이블이 있다면
                // 해약환급금 테이블 담기
                try {
                    List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

                    elements = helper.waitVisibilityOfAllElements(
                        driver.findElement(By.cssSelector("#step4  div.accordion.--is-active > div.accordion__contents table tbody"))
                            .findElements(By.tagName("tr")));

                    int elementsSize = elements.size();
                    for (int i=0; i<elementsSize; i++) {

                        String term = elements.get(i).findElement(By.cssSelector("th")).getText().trim();
                        logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                        logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
                        String premiumSum = elements.get(i).findElement(By.cssSelector("td:nth-child(2)")).getText().replaceAll("[^0-9]", "");
                        logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);
                        String returnMoney 	= elements.get(i).findElement(By.cssSelector("td:nth-child(3)")).getText().replaceAll("[^0-9]", "");
                        logger.info("해약환급금 크롤링:: 환급금 :: " + returnMoney);
                        String returnRate 	= elements.get(i).findElement(By.cssSelector("td:nth-child(4)")).getText();
                        logger.info("해약환급금 크롤링:: 환급률 :: " + returnRate);

                        PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                        planReturnMoney.setTerm(term);
                        planReturnMoney.setPremiumSum(premiumSum);
                        planReturnMoney.setReturnMoney(returnMoney);
                        planReturnMoney.setReturnRate(returnRate);
                        planReturnMoneyList.add(planReturnMoney);

                        info.setPlanReturnMoneyList(planReturnMoneyList);

                        if((i+1) < elementsSize){
                            WebElement element = elements.get(i+1);
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                        }
                    }

                    logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
                } catch (Exception e) {
                    logger.info("해약 환급금 정보가 없습니다.");
                    logger.info(e.getMessage());
                }

            });
    }
}
