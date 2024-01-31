package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.07.19 | 최우진 | 신한든든한실속종신보험(무배당, 보증비용부과형)[해약환급금 일부지급형](간편심사형)
public class SHL_WLF_F042 extends CrawlingSHLAnnounce {

    public static void main(String[] args) { executeCommand(new SHL_WLF_F042(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String driveType = "승용차(자가용)";
        String job = Job.MANAGER.getCodeValue(); // 경영지원 사무직 관리자
        String prodKind = "일반";
        String prodType = "간편심사형";
        String salesType = "일반";
        int unitAmt = 10_000;
        String refundOption = "FULL";

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 상품검색 ]");
        initSHL2(info);

        logger.info("▉▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ]");
        setBirthday(By.xpath("//input[@title='생년월일']"), info.getFullBirth());
        setGender(info.getGender());
        setVehicle(driveType);
        setJob(job);
        pushButton(driver.findElement(By.xpath("//*[@id='csinDivision']/div[2]/span/button")), 3);

        logger.info("▉▉▉▉ STEP02 [ '주계약계산' 입력 ]");
        logger.info("▉▉▉▉ 보험형태, 보험종류, 직종구분, 보험기간, 납입기간, 납입주기, 가입금액");

        setProductKind(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[1]/div[2]/select")), prodKind);
        setProductType(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[2]/div[2]/select")), prodType);
        setSalesType(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[3]/div[2]/select")), salesType);
        setInsTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[1]/div[2]/select")), info.getInsTerm());
        setNapTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[2]/div[2]/select")), info.getNapTerm());
        setNapCycle(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[4]/div[2]/select")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[3]/div[2]/div/input")), info.getAssureMoney(), unitAmt);
        pushButton(driver.findElement(By.xpath("//*[@id='mnprDivision']/div[1]/span/button")), 3);

        logger.info("▉▉▉▉ STEP03 [ '특약계산' 입력 ]");
        // 특약1개라 내용없음 (default유지)
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
            driver.findElements(By.xpath("//*[@id='tblRttrGood01']/tbody/tr")),
            info,
            refundOption
        );

        crawlReturnPremium(info);

        return true;
    }
}
