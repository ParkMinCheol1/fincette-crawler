package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.08.03 | 최우진 | 신한큐브종합건강상해보험라이트(무배당, 갱신형)
public class SHL_ACD_F006 extends CrawlingSHLAnnounce {

    public static void main(String[] args) { executeCommand(new SHL_ACD_F006(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        int unitAmt = 10_000;
        String driveType = "승용차(자가용)";
        String prodType = "15년갱신형";
        String salesType = "일반";
        String[] arrTextType = info.getTextType().split("#");
        String refundOption = "BASE";

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 상품검색 ] ▉▉▉▉");
        initSHL(info, "05");

        logger.info("▉▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉▉");
        setBirthday(driver.findElement(By.xpath("//*[@id='csinDivision']/div[1]/ul/li[1]/div[2]/div/input")), info.getFullBirth());
        setVehicle(driveType);
        setGender(info.getGender());
        setJob(Job.MANAGER.getCodeValue());
        pushButton(driver.findElement(By.xpath("//*[@id='csinDivision']/div[2]/span/button")), 3);

        logger.info("▉▉▉▉ STEP02 [ '주계약계산'입력 ] ▉▉▉▉");
        logger.info("▉▉▉▉ 보험형태, 보험종류, 보험기간, 납입기간, 납입주기, 가입금액 ▉▉▉▉");
        setProductType(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[2]/div[2]/select")), prodType);
        setSalesType(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[3]/div[2]/select")),      salesType);
        setInsTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[1]/div[2]/select")), info.getInsTerm());
        setNapTerm(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[2]/div[2]/select")), info.getNapTerm());
        setNapCycle(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[4]/div[2]/select")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[3]/div[2]/div/input")), info.getAssureMoney(), unitAmt);
        pushButton(driver.findElement(By.xpath("//*[@id='mnprDivision']/div[1]/span/button")), 3);

        logger.info("▉▉▉▉ STEP03 [ '특약계산'입력 ] ▉▉▉▉");
        setTreatyList(driver.findElements(By.xpath("//*[@id='trtyDivision']/table/tbody/tr")), info.getTreatyList(), arrTextType);
        pushButton(driver.findElement(By.xpath("//*[@id='acco_3']/div[2]/span/button")), 2);                                        // 진행 버튼
        pushButton(driver.findElement(By.xpath("//*[@id='cdhi0050p']/div[2]/div/div/div[2]/section[2]/div[2]/span/button")), 4);    // 보험료 계산 버튼

        logger.info("▉▉▉▉ STEP04 [ 결과확인 ] ▉▉▉▉");
        pushButton(driver.findElement(By.xpath("//*[@id='tab_0_2']")),4);
        crawlPremium(driver.findElement(By.xpath("//*[@id='cdhi0050p']/div[2]/div/div/div[2]/section[2]/div[3]/div[2]/section/div[4]/em")), info);
        snapScreenShot(info);
        pushButton(driver.findElement(By.xpath("//*[@id='tab_0_2']/a/span[1]")), 1);
        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='tblInmRtFxty01']/tbody/tr")), info, refundOption);

        return true;
    }
}
