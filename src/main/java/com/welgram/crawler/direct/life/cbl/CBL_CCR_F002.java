package com.welgram.crawler.direct.life.cbl;

import com.welgram.crawler.general.CrawlingProduct;



public class CBL_CCR_F002 extends CrawlingCBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new CBL_CCR_F002(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("상품명 찾기");
        findProductName(info.getProductNamePublic());

        logger.info("고객정보 입력");
        setUserInfo(info);

        logger.info("설계정보 입력");
        setJoinCondition(info);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}