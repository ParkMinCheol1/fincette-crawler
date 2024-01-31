package com.welgram.crawler.direct.life.shl;

import com.welgram.crawler.direct.life.shl.CrawlingSHL.CrawlingSHLDirect;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2022.10.06       | 최우진           | 다이렉트_상해
// SHL_ACD_D007     | 신한 MZ상해보험M(무배당)
// todo | 해당상품은 가입금액 설정도 불가능, 그냥 1천 단순 고정
// todo | 단순고정에 default처리인지, 작동을 기본값으로 하는지 확인필요
public class SHL_ACD_D007 extends CrawlingSHLDirect {

    public static void main(String[] args) { executeCommand(new SHL_ACD_D007(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String birth = info.getFullBirth(); // 생년월일8자리
        String refundOption = "BASE";
        boolean isAnnuuity = false;         // 연금계열 여부

        // PROCESS
        logger.info("▉▉▉▉ STEP00 [ 상품 검색 ] ▉▉▉▉");
        initSHL(info);

        logger.info("▉▉▉▉ STEP01 [ 기본정보 입력 :: 성별, 생년월일(8) ] ▉▉▉▉");
        setGender(info.getGender());
        setBirthday(driver.findElement(By.id("birymd")), birth);
        pushButton(driver.findElement(By.id("btnCalInpFe")), 3);

        logger.info("▉▉▉▉ STEP02 [ 주계약 내용 입력01 :: 납이주기, 보험기간, 납입기간 ] ▉▉▉▉");
        setNapCycle(driver.findElement(By.id("selPamCyclCd")), info.getNapCycleName());
        setInsTerm(driver.findElement(By.id("selectMnprIsteCn")), info.getInsTerm());
        setNapTerm(driver.findElement(By.id("selectMnprPmpeTc")), info.getNapTerm());

        logger.info("▉▉▉▉ STEP03 [ 주계약 내용 입력02 :: 보험가입금액] ▉▉▉▉");
        setAssureMoney(
            driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select")),
            info,
            isAnnuuity
        );
        pushReCalc();

        logger.info("▉▉▉▉ STEP04 [ 결과가져오기 :: 보험료, 스크린샷, 해약정보] ▉▉▉▉");
        crawlPremium(driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[2]/div[1]/div[2]/em")), info);
        snapScreenShot(info);
        crawlReturnMoneyList(info, refundOption);

        return true;
    }
}
