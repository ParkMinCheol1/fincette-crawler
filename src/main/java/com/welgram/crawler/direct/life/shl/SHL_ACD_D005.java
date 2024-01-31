package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLDirect;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2022.07.05 | 최우진 | 신한대중교통보장보험(무배당)
public class SHL_ACD_D005 extends CrawlingSHLDirect {

    public static void main(String[] args) {
        executeCommand(new SHL_ACD_D005(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String birth = info.getFullBirth(); // 생년월일8자리
        String refundOption = "BASE";
        boolean isAnnuuity = false;         // 연금계열 여부


        // Crawling Process
        logger.info("▉▉▉▉ STEP00 [ 상품 검색 ] ▉▉▉▉");
        initSHL(info);


        logger.info("▉▉▉▉ STEP01 [ 기본정보 입력 :: 성별, 생년월일(8) ] ▉▉▉▉");
        setGender(info.getGender());
        setBirthday(driver.findElement(By.id("birymd")), birth);
        pushButton(driver.findElement(By.id("btnCalInpFe")), 3);


        logger.info("▉▉▉▉ STEP02 [ 주계약 내용 입력01 :: 납입주기, 보험기간, 납입기간 ] ▉▉▉▉");
        setNapCycle(driver.findElement(By.id("selPamCyclCd")), info.getNapCycleName());
        setInsTerm(driver.findElement(By.id("selectMnprIsteCn")), info.getInsTerm());
        setNapTerm(driver.findElement(By.id("selectMnprPmpeTc")), info.getNapTerm());


        logger.info("▉▉▉▉ STEP03 [ 주계약 내용 입력02 :: 보험가입금액] ▉▉▉▉");
        setAssureMoney(
            driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select")),
            info,
            isAnnuuity
        );
        pushButton(driver.findElement(By.xpath("//button[text()='다시 계산하기']")), 3);


        logger.info("▉▉▉▉ STEP04 [ 결과가져오기 :: 보험료, 스크린샷, 해약정보] ▉▉▉▉");
        crawlPremium(driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[2]/div[1]/div[2]/em")), info);
        snapScreenShot(info);
        crawlReturnMoneyList(info, refundOption);

        return true;
    }
}
