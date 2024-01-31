package com.welgram.crawler.direct.life.kdb;

import com.welgram.crawler.direct.life.CrawlingKDB;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;



// 2023.12.07 | 최우진 | (무)다이렉트 미니독감치료보험
public class KDB_DSS_D007  extends CrawlingKDB {

    public static void main(String[] args) {
        executeCommand(new KDB_DSS_D007(), args);
    }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setMobile(true);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("'본인가입하기' - 클릭");
        driver.findElement(By.xpath("//a[text()='본인가입하기']")).click();

        logger.info("생년월일 설정 : {}", info.fullBirth);
        setBirth(info.fullBirth);

        logger.info("성별 설정 : {}", (info.gender == MALE) ? "남자" : "여자");
        setGender(info.gender);

        logger.info("'계산하기' - 클릭!");
        calcBtnClick();

        logger.info("보험료 갖고오기");
        setMainTreatyPremium(info, By.id("pInsAmt"));

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

                logger.info("__________해약환급급__________");
                logger.info("|-- 경과기간         : {}", term);
                logger.info("|-- 납입보험료       : {}", premiumSum);
                logger.info("|-- 해약환급금       : {}", returnMoney);
                logger.info("|-- 최저납입보험료    : {}", premiumSum);
                logger.info("|-- 환급률          : {}", returnRate);
                logger.info("____________________________");

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
