package com.welgram.crawler.direct.fire.axf;

import com.welgram.crawler.direct.fire.CrawlingAXF;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class AXF_DTL_F001 extends CrawlingAXF { // (무)AXA치아보험2301
    public static void main(String[] args) {
        executeCommand(new com.welgram.crawler.direct.fire.axf.AXF_DTL_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        disClosureRoomCrawling(info);
        return true;
    }

    private void disClosureRoomCrawling(CrawlingProduct info) throws Exception {

        // 공시실
        logger.info("=============================");
        logger.info("공시실열기");
        logger.info("=============================");

        // 생년월일 세팅
        setBirthdayNew(info);

        // 성별 세팅
        String gender = info.getGender() == MALE ? "1" : "2";
        setGenderNew(gender);

        // 보험료계산 클릭
        logger.info("=============================");
        logger.info("다음 누르기 클릭!");
        helper.click(By.cssSelector("#frmStep01 > div.btn_area.mgt30 > div > a"));

        // 로딩바...
        logger.info("=============================");
        logger.info("로딩바처리");
        helper.waitForCSSElement("#wrap > div.overlay.overlay_loading > div.calboxp > div");

        // 월 보험료 세팅
        crawlPremiumNew(info);

        logger.info("스크린샷!");
        takeScreenShot(info);
    }

    private boolean Webcrawling(CrawlingProduct info) throws Exception {

        boolean result = false;

        result = true;

        return result;
    }

    private boolean Mobilecrawling(CrawlingProduct info) throws Exception {

        boolean result = false;

        result = true;

        return result;
    }
}
