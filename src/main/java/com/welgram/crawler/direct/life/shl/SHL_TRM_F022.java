package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.08.23 | 최우진 | 신한CEO정기보험II(무배당, 보증비용부과형) 2종(일반심사형)(15%)체증형
public class SHL_TRM_F022 extends CrawlingSHLAnnounce {

    public static void main(String[] args) { executeCommand(new SHL_TRM_F022(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String prodKind = "(15%)체증형";
        String prodType = "일반심사형";
        String driveType = "승용차(자가용)";
        String job = Job.MANAGER.getCodeValue();
        String refundOption = "FULL";
        int unitAmt = 10_000;

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 검색창, '상품명 입력 ] ▉▉▉▉");
        initSHL(info, "01");

        logger.info("▉▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉▉");
        setBirthday(driver.findElement(By.xpath("//*[@id='csinDivision']/div[1]/ul/li[1]/div[2]/div/input")), info.getFullBirth());
        setVehicle(driveType);
        setGender(info.getGender());
        setJob(job);
        pushButton(driver.findElement(By.xpath("//*[@id='csinDivision']/div[2]/span/button")), 3);

        logger.info("▉▉▉▉ STEP02 [ '주계약계산' 입력 ] ▉▉▉▉");
        logger.info("▉▉▉▉ 보험형태, 보험종류, 보험기간, 납입기간, 납입주기, 가입금액 ▉▉▉▉");
        setProductKind(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[1]/div[2]/select")), prodKind);
        setProductType(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[2]/div[2]/select")), prodType);
        setInsTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[1]/div[2]/select")), info.getInsTerm());
        setNapTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[2]/div[2]/select")), info.getNapTerm());
        setNapCycle(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[4]/div[2]/select")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[3]/div[2]/div/input")), info.getAssureMoney(), unitAmt);
        pushButton(driver.findElement(By.xpath("//*[@id='mnprDivision']/div[1]/span/button")), 3);

        logger.info("▉▉▉▉ STEP03 [ '특약계산' 입력 ] ▉▉▉▉");
        // 추가 특약 설정 없음 (주계약 1개)
        pushButton(driver.findElement(By.xpath("//*[@id='acco_3']/div[2]/span/button")), 2);
        pushButton(driver.findElement(By.xpath("//*[@id='cdhi0050p']/div[2]/div/div/div[2]/section[2]/div[2]/span/button")), 4);    // 보험료 계산 버튼

        logger.info("▉▉▉▉ STEP04 [ 결과확인 ] ▉▉▉▉");
        pushButton(driver.findElement(By.xpath("//*[@id='tab_0_2']")),4);
        crawlPremium(
            driver.findElement(By.xpath("//*[@id='cdhi0050p']/div[2]/div/div/div[2]/section[2]/div[3]/div[2]/section/div[4]/em")),
            info
        );
        snapScreenShot(info);
        pushButton(driver.findElement(By.xpath("//*[@id='tab_0_2']/a/span[1]")), 1);
        crawlReturnMoneyList(
            driver.findElements(By.xpath("//*[@id='tblInmRtFxty01']/tbody/tr")),
            info,
            refundOption
        );

        return true;
    }
}
