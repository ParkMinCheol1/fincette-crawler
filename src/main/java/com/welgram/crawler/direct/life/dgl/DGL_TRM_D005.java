package com.welgram.crawler.direct.life.dgl;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DGL_TRM_D005 extends CrawlingDGLMobile {

    public static void main(String[] args) {
        executeCommand(new DGL_TRM_D005(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;

        logger.info("보험료 알아보기 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[normalize-space()='보험료 알아보기']"));
        click($button);

        logger.info("생년월일 설정");
        setBirthday(info.getBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[normalize-space()='보험료 계산']"));
        click($button);

        logger.info("나만의 플랜 설계하기 버튼 클릭");
        $button = driver.findElement(By.className("btn-myplan"));
        click($button);

        logger.info("보험종류 설정");
        setProductType(info.getTextType());

        logger.info("보장금액 설정");
        setAssureMoney(info.getAssureMoney());

        logger.info("보험기간 설정");
        setInsTermTrm(info.getInsTerm());

        logger.info("납입기간 설정");
        setNapTerm(info.getNapTerm());

        logger.info("납입방법 설정");
        setNapCycle(info.getNapCycleName());

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[@class='btn ty1 round']"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }
}