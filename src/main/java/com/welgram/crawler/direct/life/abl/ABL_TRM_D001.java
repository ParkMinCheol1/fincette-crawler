package com.welgram.crawler.direct.life.abl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;



public class ABL_TRM_D001 extends CrawlingABLDirect {

    public static void main(String[] args) {
        executeCommand(new ABL_TRM_D001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("생년월일 입력");
        setBirthday(info.fullBirth);

        logger.info("성별 선택");
        setGender(info.gender);

        logger.info("흡연 여부");
        driver.findElement(By.id("prdAdtnOtpNm01")).click();

        logger.info("보험료 확인하기 클릭");
        driver.findElement(By.id("calcStrBtn")).click();
        waitLoadingImg();

        logger.info("내맘대로 설계하기 클릭");
        helper.waitElementToBeClickable(By.id("direcEntplTab")).click();

        logger.info("가입금액 확인");
        setAssureMoney(info.assureMoney);

        logger.info("보험기간 클릭");
        setInsTerm(info.insTerm);

        logger.info("납입기간 클릭");
        setNapTerm(info.napTerm);

        logger.info("보험료 계산");
        crawlPremium(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금 클릭");
        driver.findElement(By.id("srdvl00Btn")).click();
        WaitUtil.waitFor(3);
        crawlReturnMoneyListTwo(info);

        return true;
    }
}