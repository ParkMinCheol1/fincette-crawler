package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLMobile;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.08.04 | 최우진 | 신한독감케어건강보험(무배당)
// todo 모바일에서 보험료계산 가능한 상품
public class SHL_DSS_D009 extends CrawlingSHLMobile {

    public static void main(String[] args) { executeCommand(new SHL_DSS_D009(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String birth = info.getFullBirth(); // 생년월일8자리
        String refundOption = "mob_base";

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 상품 검색 ] ▉▉▉▉");
        initSHL(info);

        logger.info("▉▉▉▉ STEP01 [ 기본정보 입력 :: 성별, 생년월일 ] ▉▉▉▉");
        setGender(info.getGender());
        setBirthday(driver.findElement(By.id("birymd")), birth);
        pushButton(driver.findElement(By.xpath("//*[@id='app']/div[1]/section/div[1]/div[2]/div/button")), 5);

        logger.info("▉▉▉▉ STEP04 [ 결과가져오기 :: 보험료, 스크린샷, 해약정보] ▉▉▉▉");
        crawlPremium(driver.findElement(By.xpath("//*[@id='app']/div[1]/section/div[3]/div[1]/ul[2]/li/div[2]/em")), info);
        snapScreenShot(info);
        pushButton(driver.findElement(By.xpath("//*[@id='app']/div[1]/section/div[3]/div[1]/div[2]/button[1]")), 5);
        crawlReturnMoneyList(info, refundOption);

        return true;
    }
}