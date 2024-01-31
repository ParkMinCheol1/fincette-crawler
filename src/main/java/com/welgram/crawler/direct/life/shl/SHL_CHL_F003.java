package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.08.23 | 최우진 | 신한우리아이교통보장보험M(무배당)
public class SHL_CHL_F003 extends CrawlingSHLAnnounce {

    public static void main(String[] args) { executeCommand(new SHL_CHL_F003(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String driveType = "비운전";
        String job = Job.CHILD.getCodeValue(); // 미취학아동
        String refundOption = "BASE";
        int unitAmt = 10_000;
        String motherBirth = "19930521";

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 상품검색 ]");
        initSHL(info, "06");

        logger.info("▉▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ]");
        setBirthday(driver.findElement(By.xpath("//*[@id='csinDivision']/div[1]/ul/li[1]/div[2]/div/input")), motherBirth);
        setGender2(1);
        setBirthday(driver.findElement(By.xpath("//*[@id='csinDivision']/div[2]/ul/li[1]/div[2]/div/input")), info.getFullBirth());
        setGender(info.getGender());
        setVehicle(driveType);
        setJob(job);
        pushButton(driver.findElement(By.xpath("//*[@id='csinDivision']/div[3]/span/button")), 3);

        logger.info("▉▉▉▉ STEP02 [ '주계약계산' 입력 ]");
        logger.info("▉▉▉▉ 납입주기, 보험기간, 납입기간, 가입금액");
        setNapCycle(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[4]/div[2]/select")), info.getNapCycleName());
        setInsTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[1]/div[2]/select")), info.getInsTerm());
        setNapTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[2]/div[2]/select")), info.getNapTerm());
        setAssureMoney(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[3]/div[2]/div/input")), info.getAssureMoney(), unitAmt);
            // [확인]
        pushButton(driver.findElement(By.xpath("//*[@id='mnprDivision']/div[1]/span/button")), 4);
            // [보험료계산]
        pushButton(driver.findElement(By.xpath("//*[@id='cdhi0050p']/div[2]/div/div/div[2]/section[2]/div[2]/span/button")), 4);

        logger.info("▉▉▉▉ STEP04 [ 결과확인 ] ▉▉▉▉");
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
