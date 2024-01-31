package com.welgram.crawler.direct.life.nhl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NHL_ACD_D002 extends CrawlingNHLDirect {

    // 로거
    public final static Logger logger = LoggerFactory.getLogger(NHL_ACD_D002.class);

    // 구동부
    public static void main(String[] args) {
        executeCommand(new NHL_ACD_D002(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        String genderOpt = (info.getGender() == MALE) ? "cal_gender1" : "cal_gender2";
        String genderText = (info.getGender() == MALE) ? "남자" : "여자";

        logger.info("NHL_ACD_D002 :: {}", info.getProductName());
        WaitUtil.waitFor(2);

        logger.info("생년월일 :: {}", info.getFullBirth());
        setBirthday(By.id("birth"), info.getFullBirth());

        logger.info("성별 :: {}", genderText);
        setGender(By.xpath("//input[@id='" + genderOpt + "']/parent::label"), genderText);

        logger.info("보험료 계산하기");
        calcBtnClickforPremium(By.id("calcPremium"));

        logger.info("특약 일치여부 확인");
        checkTreaties(info.getTreatyList());

        logger.info("월 보험료 가져오기");
        crawlPremium(By.id("premium"), info);

        logger.info("해약환급금 가져오기");
        crawlReturnMoneyList(info, By.cssSelector("#minilifeReturn_uiPOPRefund1 > table > tbody > tr"));

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }

}
