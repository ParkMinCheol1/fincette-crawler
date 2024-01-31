package com.welgram.crawler.direct.life.abl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


public class ABL_CCR_D003 extends CrawlingABLDirect {

    public static void main(String[] args) {
        executeCommand(new ABL_CCR_D003(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("성별 클릭");
        setGender(info.gender);

        logger.info("생년월일 입력");
        setBirthday(info.fullBirth);

        logger.info("보험료 확인하기 클릭");
        driver.findElement(By.id("calcStrBtn")).click();
        waitLoadingImg();

        logger.info("내맘대로 설계하기 클릭");
        driver.findElement(By.id("direcEntplTab")).click();

        logger.info("암진단시 최대보험금 설정");
        setAssureMoney(info.assureMoney);

        logger.info("보험료 계산");
        crawlPremium(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금 클릭");
        driver.findElement(By.id("srdvl00Btn")).click();
        WaitUtil.waitFor(3);
        crawlReturnMoneyList(info);

        return true;
    }
}