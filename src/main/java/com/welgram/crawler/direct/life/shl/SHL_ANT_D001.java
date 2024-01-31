package com.welgram.crawler.direct.life.shl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class SHL_ANT_D001 extends CrawlingSHLAnnounce {

    public static void main(String[] args) { executeCommand(new SHL_ANT_D001(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        int unitAmt = 1;
        String driveType = "승용차(자가용)";
        String prodType = "기본형";
        String[] arrTextType = info.getTextType().split("#");
        String refundOption = "FULL";
//        String exceptionalTreatyName = "간편첫날부터플러스입원특약B15(무배당, 갱신형)(1형)";

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 상품검색 ] ▉▉▉▉");
        initSHL_wj(info);

        logger.info("▉▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉▉");
        setBirthday(By.xpath("//input[@title='생년월일']"), info.getFullBirth());
        setVehicle(driveType);
        setGender(info.getGender());
        setJob(Job.MANAGER.getCodeValue());
        pushButton(driver.findElement(By.xpath("//*[@id='csinDivision']/div[2]/span/button")), 3);

        logger.info("▉▉▉▉ STEP02 [ '주계약계산'입력 ] ▉▉▉▉");

        logger.info("▉▉▉▉ 보험종류, 납입주기, 보험기간, 납입기간, 가입금액, 보험료 ▉▉▉▉");
        setProductType(By.xpath("//select[@title='보험종류']"), prodType);
        setNapCycle(By.xpath("//select[@title='납입주기']"), info.getNapCycleName());
        setAnnuityAge(By.xpath("//select[@title='보험기간']"), info.getAnnuityAge());
        setNapTerm(By.xpath("//select[@title='납입기간']"), info.getNapTerm());
        setAssureMoney(By.xpath("//input[@title='보험료(원)']"), info.getAssureMoney(), unitAmt);
        helper.findFirstDisplayedElement(By.xpath("//button[contains(.,'확인')]")).get().click();
        helper.findFirstDisplayedElement(By.xpath("//button[contains(.,'보험료계산') and @class='btn_p btnInpFeCal']")).get().click();

        logger.info("▉▉▉▉ STEP04 [ 결과확인 ] ▉▉▉▉");
        // 2024.01.16 노우정
        // 보험사에서 보답에서 연금수령액 게시하는 기준인 종신 10년, 확정 10년 연금수령액을 제공하지 않아서 대상아님 상태로 변경하게 되었습니다.
//        crawlPremium(driver.findElement(By.xpath("//*[@id='cdhi0050p']/div[2]/div/div/div[2]/section[2]/div[3]/div[2]/section/div[4]/em")), info);
//        snapScreenShot(info);
//        pushButton(helper.getWebElement(By.xpath("//a/span[text()='해약환급금 예시']")), 1); // 해약환급금 예시
//        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='tblInmRtFxty01']/tbody/tr")), info, refundOption);

        return true;
    }
}
