package com.welgram.crawler.direct.life.mtl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.ReturnMoneyIdx;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanCalc;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MTL_CHL_F001 extends CrawlingMTLAnnounce {

    public static void main(String[] args) {
        executeCommand(new MTL_CHL_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $element = null;

        driver.manage().window().maximize();

        logger.info("계약자 생년월일 설정(주민번호 앞자리)");
        setBirthday(info.getParent_Birth(), By.id("contract_jumin1"));

        logger.info("계약자 생년월일 설정(주민번호 뒷자리");
        setGender(info.getGender(), info.getParent_FullBirth(), By.xpath("//input[@name='contract_jumin2']"));

        logger.info("가입자녀 생년월일 설정(주민번호 앞자리)");
        setBirthday(info.getBirth(), By.id("jumin1"));

        logger.info("가입자녀 생년월일 설정(주민번호 뒷자리)");
        setGender(info.getGender(), info.getFullBirth(), By.xpath("//input[@name='jumin2']"));

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.linkText("다음"));
        click($element);

        logger.info("주계약 상품 입력");
        setMainTreatyInfo(info);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goNext"));
        click($element);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        $element = driver.findElement(By.id("productName"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.id("goCoverage"));
        click($element);

        logger.info("해약환급금 크롤링");
        ReturnMoneyIdx returnMoneyIdx = new ReturnMoneyIdx();
        returnMoneyIdx.setPremiumSumIdx(1);
        returnMoneyIdx.setReturnMoneyIdx(2);
        returnMoneyIdx.setReturnRateIdx(3);
        crawlReturnMoneyList(info, returnMoneyIdx, MoneyUnit.만원);

        return true;
    }



}