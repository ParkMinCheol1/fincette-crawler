package com.welgram.crawler.direct.life.dgl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DGL_WLF_F020 extends CrawlingDGLAnnounce {

    public static void main(String[] args) {
        executeCommand(new DGL_WLF_F020(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;

        driver.manage().window().maximize();

        logger.info("고객명 설정");
        setUserName(PersonNameGenerator.generate());

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("주계약 종류 설정");
        setProductType(info.getTextType());

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.id("pdtMaiBt"));
        click($button);

        logger.info("특약 설정");
        driver.findElement(By.xpath("//*[@id='H70301_pypd']/option[5]")).click();

        int assureMoney = Integer.parseInt(info.assureMoney) / 10000;
        setTextToInputBox(By.xpath("//*[@id='H70301_SMSU_INPUT']"), String.valueOf(assureMoney));

        logger.info("보험료계산 버튼 클릭");
        $button = driver.findElement(By.id("calcPrembt"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyListShort(info);

        //가입자 정보와 특약, 보험료가 모두 보이게하기 위해 적절한 위치로 스크롤 이동
        logger.info("스크린샷 찍기");
        WebElement $element = driver.findElement(By.id("memoStr"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        return true;
    }
}