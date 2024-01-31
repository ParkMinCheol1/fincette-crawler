package com.welgram.crawler.direct.life.pst;

import com.welgram.common.enums.MoneyUnit;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class PST_CCR_F005 extends CrawlingPSTAnnounce {

    public static void main(String[] args) {
        executeCommand(new PST_CCR_F005(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $element = null;

        driver.manage().window().maximize();

        logger.info("공시실에서 상품명 찾기");
        findProductName(info.getProductNamePublic());

        logger.info("사용자 정보 입력");
        setUserInfo(info);

        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        $element = driver.findElement(By.xpath("//h3[normalize-space()='피보험자 기본정보']"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        //step5 : 해약환급금 크롤링
        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info, MoneyUnit.원);

        crawlReturnPremium(info);

       return true;
    }
}