package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLDirect;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.01.26       | 최우진           | 다이렉트_암보험
// SHL_CCR_D011     | 신한인터넷암보험(무배당,해약환급금 미지급형)
public class SHL_CCR_D011 extends CrawlingSHLDirect {

    public static void main(String[] args) {
        executeCommand(new SHL_CCR_D011(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String birth = info.getFullBirth();
        String[] arrTextType = getTextType(info);
        String refundType = arrTextType[1];
        String refundOption = "BASE";

        // PROCESS
        logger.info("▉▉▉ STEP00 [ 상품 검색 ] ▉▉▉");
        initSHL(info);

        logger.info("▉▉▉ STEP01 [ 기본정보 입력 :: 성별, 생년월일(8) ] ▉▉▉");
        setGender(info.getGender());
        setBirthday(driver.findElement(By.id("birymd")), birth);
        pushButton(driver.findElement(By.id("btnCalInpFe")), 3);

        logger.info("▉▉▉ STEP02 [ 주계약 내용 입력01 :: 보험형태, 납입주기, 보험기간, 납입기간 ] ▉▉▉");
        setRefundType(driver.findElement(By.id("selInsFormCd")), refundType);
        setNapCycle(driver.findElement(By.id("selPamCyclCd")), info.getNapCycleName());
        setInsTerm(driver.findElement(By.id("selectMnprIsteCn")), info.getInsTerm());
        setNapTerm(driver.findElement(By.id("selectMnprPmpeTc")), info.getNapTerm());

        logger.info("▉▉▉ STEP03 [ 주계약 내용 입력02 :: 보험가입금액] ▉▉▉"); // todo 다시 짜야함
//        setAssureMoney(driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select")), info, true);

        // 주계약 가입금액 설정
        helper.selectByValue_check(
            By.xpath("//select[@class='ipt' and @title[contains(.,'보험가입금액')]]"), info.assureMoney);

        pushButton(driver.findElement(By.xpath("//button[text()='다시 계산하기']")), 3);

        logger.info("▉▉▉ STEP04 [ 결과가져오기 :: 보험료, 스크린샷, 해약정보] ▉▉▉");
        crawlPremium(driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[3]/div[1]/div[2]/em")), info);
        snapScreenShot(info);
        crawlReturnMoneyList(info, refundOption);

        return true;
    }
}
