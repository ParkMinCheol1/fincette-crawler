package com.welgram.crawler.direct.life.pli;

import com.welgram.crawler.direct.life.CrawlingPLI;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class PLI_TRM_F001 extends CrawlingPLI {

    public static void main(String[] args) { executeCommand(new PLI_TRM_F001(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("iframe 진입");
        driver.switchTo().frame(
            helper.waitPresenceOfElementLocated(By.id("calculatorIframe")));

        logger.info("상품군 선택");
        selectByVisibleTxt("[@id='selCategory']", "사망보장");

        logger.info("상품명 선택");
        selectByVisibleTxt("[@id='selProductist']", "무배당 소득보장보험");

        logger.info("다음 클릭");
        helper.click(By.id("start"));



        logger.info("생년월일");
        helper.sendKeys3_check(By.id("strDateOfBirth"), info.fullBirth);

        logger.info("성별");
        selectByValue("[@id='strGender']", String.valueOf(info.gender + 1));

        logger.info("다음 클릭");
        helper.click(By.id("step1next"));



        logger.info("보험기간");
        selectByVisibleTxt("[@id='coveragePeriod']", (info.insTerm + "보장"));

        logger.info("납입기간");
        selectByVisibleTxt("[@id='paymentPeriod']",
            info.napTerm.equals(info.insTerm) ? "전기납" : info.napTerm + "납");

        logger.info("보험금액");
        helper.sendKeys3_check(By.id("faceAmount"), String.valueOf(Integer.parseInt(info.assureMoney) / 10000));

        logger.info("납입주기 월납 - 생략");

        logger.info("다음 클릭");
        helper.click(By.cssSelector("#step2next > div"));



        logger.info("특약 선택 - 생략");
        logger.info("특약 선택 - 페이지 전환될 때까지 대기");
        helper.waitVisibilityOfElementLocated(By.cssSelector("#step3 > div:nth-child(1) > h2"));

        logger.info("다음 클릭");
        helper.click(By.cssSelector("#step3next > div"));



        WebElement monthlyPremiumEl = helper.waitPresenceOfElementLocated(By.xpath("//*[@id='step4']/div[1]/b"));
        info.treatyList.get(0).monthlyPremium = monthlyPremiumEl.getText().replaceAll("[^\\d.]", "");
        logger.info("월 보험료 : " + info.treatyList.get(0).monthlyPremium);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금");
        getPlanReturnMoney(info);

        return true;
    }


}
