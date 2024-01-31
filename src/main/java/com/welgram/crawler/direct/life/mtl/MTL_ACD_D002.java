package com.welgram.crawler.direct.life.mtl;

import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MTL_ACD_D002 extends CrawlingMTLAnnounce {

    public static void main(String[] args) {
        executeCommand(new MTL_ACD_D002(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        /**
         * 원래라면 다이렉트 사이트에서 크롤링 해야하지만 다이렉트 사이트에는 해약환급금 정보가 없고
         * 공시실에는 해약환급금 정보를 제공하므로 공시실에서 크롤링을 진행한다.
         */
        WebElement $element = null;

        driver.manage().window().maximize();

        logger.info("[STEP 1] 사용자 정보 입력");
        setUserInfo(info);

        logger.info("[STEP 2] 주계약 정보 입력");
        setMainTreatyInfo(info);

        // 빈공간 클릭
        $element = driver.findElement(By.cssSelector("#mainForm > div"));
        click($element);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goNext"));
//        helper.executeJavascript("arguments[0].click();", $element);
//        helper.executeJavascript("$(arguments[0])[0].click();", $element);
//        waitLoadingBar();
        click($element);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goCoverage"));
        click($element);

        logger.info("스크린샷 찍기");
        $element = driver.findElement(By.id("productName"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        ReturnMoneyIdx returnMoneyIdx = new ReturnMoneyIdx();
        returnMoneyIdx.setPremiumSumIdx(2);
        returnMoneyIdx.setReturnMoneyIdx(3);
        returnMoneyIdx.setReturnRateIdx(4);
        crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.원);

        return true;
    }
}