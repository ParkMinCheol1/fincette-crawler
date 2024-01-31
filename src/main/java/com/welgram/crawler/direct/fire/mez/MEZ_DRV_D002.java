package com.welgram.crawler.direct.fire.mez;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class MEZ_DRV_D002 extends CrawlingMEZMobile {

    public static void main(String[] args) {
        executeCommand(new MEZ_DRV_D002(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $element = null;
        waitLoadingBar();

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("운전용도 설정");
        setVehicle("자가용");

        logger.info("직업 설정");
        setJob("초등학교 교사");

        logger.info("내 보험료 계산 버튼 클릭");
        $element = driver.findElement(By.linkText("내 보험료 계산"));
        click($element);
        waitLoadingBar();

        logger.info("기간변경 버튼 클릭");
        $element = driver.findElement(By.xpath("//button[normalize-space()='기간변경']"));
        click($element);

        logger.info("보험기간 / 납입기간 설정");
        setInsTerm(info.getInsTerm());

        logger.info("다음 버튼 클릭");
        $element = driver.findElement(By.xpath("//div[@id='prdLayerPopup']//a[normalize-space()='다음']"));
        click($element);

        logger.info("플랜 설정");
        setPlan(info.planSubName);

        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}