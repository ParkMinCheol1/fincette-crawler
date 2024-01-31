package com.welgram.crawler.direct.life.shl;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


// 2023.03.23 | 최우진 | 신한받고또받는생활비암보험(무배당, 갱신형) 생활비지급형
// 특이사항 :: '직종구분'있는 케이스 :: 실제로 크롤링시에는 무의미합니다
// 특이사항 :: SHL 공시실은 종종 태그의 xpath경로나 이름이 바뀌어서, 아래와 같이 상속을 받는 최종 자식클래스에서 xpath를 설정합니다
// 특이사항 :: setProductType() - SHL의 보험형태는 단순히 갱신/비갱신 구분만 하는것 외에 납입기간도 합쳐져 있습니다,
// 특이사항 :: setTreatyList() - Scrapable에 없는 내용입니다
public class SHL_CCR_F003 extends CrawlingSHLAnnounce {

    public static void main(String[] args) { executeCommand(new SHL_CCR_F003(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String birth = info.getFullBirth();     // 생년월일8자리
        String driveType = "승용차(자가용)";
        String salesType = "일반Plan";
        String job = Job.MANAGER.getCodeValue();
        String prodType = (info.getInsTerm().equals("15년")) ? "15년갱신형" : "30년갱신형";
        String refundOption = "BASE";
        String[] arrTextType = info.getTextType().split("#");
        int unitAmt = 10_000;

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 상품검색 ] ▉▉▉▉");
        initSHL(info, "04");

        logger.info("▉▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉▉");
        setBirthday(driver.findElement(By.xpath("//*[@id='csinDivision']/div[1]/ul/li[1]/div[2]/div/input")), birth);
        setVehicle(driveType);
        setGender(info.getGender());
        setJob(job);
        pushButton(driver.findElement(By.xpath("//*[@id='csinDivision']/div[2]/span/button")), 3);

        logger.info("▉▉▉▉ STEP02 [ '주계약계산' 입력 ] ▉▉▉▉");
        setProductType( driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[2]/div[2]/select")),      prodType);
        setInsTerm(     driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[1]/div[2]/select")),      info.getInsTerm());
        setSalesType(   driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[3]/div[2]/select")),      salesType);
        setNapTerm(     driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[2]/div[2]/select")),      info.getNapTerm());
        setNapCycle(    driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[1]/li[4]/div[2]/select")),      info.getNapCycleName());
        setAssureMoney( driver.findElement(By.xpath("//*[@id='mnprDivision']/ul[2]/li[3]/div[2]/div/input")),   info.getAssureMoney(), unitAmt);
        pushButton(     driver.findElement(By.xpath("//*[@id='mnprDivision']/div[1]/span/button")), 3);

        logger.info("▉▉▉▉ STEP03 [ '특약계산' 입력 ] ▉▉▉▉");
        setTreatyList(driver.findElements(By.xpath("//*[@id='trtyDivision']/table/tbody/tr")), info.getTreatyList(), arrTextType);
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

//        // PROCESS
//        logger.info("▉▉▉ STEP00 [ 검색창, '상품명:[{}] 입력 ] ▉▉▉", title);
//        searchProdByTitle(salesType, title);
//
//        logger.info("▉▉▉ STEP01 [ '고객정보(피보험자)' 입력 ] ▉▉▉");
//        inputCustomerInfo(birth, gender, driveType, strJob);
//
//        logger.info("▉▉▉ STEP02 [ '주계약계산' 입력 ] ▉▉▉ ");
//        logger.info("SHL 공시실 '주계약계산'은 주계약에 대한 설정입니다");
//        inputMainTreatyInfo (
//            prodCode,                       // 상품코드 (ex.SHL_CCR_F003)
//            insType,                        // 1. 보험종류
//            info.getInsTerm(),              // 2. 보험기간
//            planDistinguisher,              // 3. 직종구분
//            info.getNapTerm(),              // 4. 납입기간
//            info.getNapCycleName(),         // 5. 납입주기
//            info.getAssureMoney()           // 6. 가입금액
//        );
//
//        logger.info("▉▉▉ STEP03 [ '특약계산' 입력 ] ▉▉▉ ");
//        logger.info("SHL 공시실 '특약계산'은 선택특약에 대한 설정입니다");
//        inputSubTreatyInfo(info);
//
//        logger.info("▉▉▉ STEP04 [ '보험료계산' (크롤링 결과 확인)] ▉▉▉ ");
//        checkResult(info, "BASE", salesType, prodCode);      // 보험료 확인, 스크린샷, 환급금 확인