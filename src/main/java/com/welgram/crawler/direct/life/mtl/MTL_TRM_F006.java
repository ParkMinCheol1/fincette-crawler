package com.welgram.crawler.direct.life.mtl;

import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MTL_TRM_F006 extends CrawlingMTLAnnounce {

    public static void main(String[] args) {
        executeCommand(new MTL_TRM_F006(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $element = null;

        driver.manage().window().maximize();

        logger.info("[STEP 1] 사용자 정보 입력");
        setUserInfo(info);

        logger.info("[STEP 2] 주계약 정보 입력");
        setMainTreatyInfo(info);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goNext"));
        click($element);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goCoverage"));
        click($element);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        ReturnMoneyIdx returnMoneyIdx = new ReturnMoneyIdx();
        returnMoneyIdx.setPremiumSumIdx(2);
        returnMoneyIdx.setReturnMoneyIdx(3);
        returnMoneyIdx.setReturnRateIdx(4);
        crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.만원);

        return true;
    }
}