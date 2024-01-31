package com.welgram.crawler.direct.fire.axf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.09.21 | 노우정 | 온라인스마트운전자보험
public class AXF_DRV_D001 extends CrawlingAXFAnnounce {

    public static void main(String[] args) {
        executeCommand(new com.welgram.crawler.direct.fire.axf.AXF_DRV_D001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        initAXF(info);        // AXF 공시실 검색 초기화

        setBirthday(By.xpath("//input[@id='if_02']"), info);        // 주민등록 번호 - 생년월일 입력

        setGender(By.xpath("//input[@id='if_02']/parent::td/input[2]"), info);      // 주민등록번호 - 성별 입력

        pushButton(By.xpath("//span[text()='다음 단계']/parent::a"), 7);        // 다음단계 버튼 클릭

        // 대기시간으로 일단 해결가능해서 주석으로 막았습니다 필요하면 수정
        //        innerPeace(By.xpath("로딩바xpath경로"));     // 로딩바...
        //        logger.info("=============================");
        //        logger.info("로딩바처리");
        //        helper.waitForCSSElement("#wrap > div.overlay.overlay_loading > div.calboxp > div");

        crawlPremium(By.xpath("//p[text()='월 보험료']/parent::div//span"), info);

        takeScreenShot(info);

        crawlReturnMoneyList(info);

        crawlReturnPremium(info);

        return true;
    }


}
