package com.welgram.crawler.direct.life.kdb;

import com.welgram.crawler.direct.life.CrawlingKDB;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



// 온택트(무)스마트폰질환보장보험
public class KDB_DSS_D005 extends CrawlingKDB {

    public static void main(String[] args) {
        executeCommand(new KDB_DSS_D005(), args);
    }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("생년월일 설정 : {}", info.fullBirth);
        setBirth(info.fullBirth);

        logger.info("성별 설정 : {}", (info.gender == MALE) ? "남자" : "여자");
        setGender(info.gender);

        logger.info("보험료 확인 버튼 클릭!");
        calcBtnClick();

        logger.info("주계약 보험료 설정");
        setMainTreatyPremium(info, By.id("mInsAmt"));

        logger.info("보험료 :: {}",info.treatyList.get(0).monthlyPremium);
        takeScreenShot(info);

        logger.info("해약환급금 조회");
        getReturnPremiumTd4(info, By.cssSelector("#cancelRefund1 tr"));

        return true;
    }



    @Override
    protected void getReturnPremiumTd4(CrawlingProduct info, By element) throws Exception {

        returnPremiumBtnClick();

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> trList = driver.findElements(element);
        for (WebElement tr : trList) {
            try {
                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

                logger.info("______해약환급급__________ ");
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--최저납입보험료: {}", premiumSum);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");

            } catch (IndexOutOfBoundsException e) {
                continue; // todo | 이게 왜 그냥 continue임??
            }
        }
            info.setPlanReturnMoneyList(planReturnMoneyList);
    }
}
