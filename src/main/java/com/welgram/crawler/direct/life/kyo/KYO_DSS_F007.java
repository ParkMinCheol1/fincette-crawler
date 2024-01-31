package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce2;
import com.welgram.crawler.general.CrawlingProduct;


public class KYO_DSS_F007 extends CrawlingKYOAnnounce2 {

    public static void main(String[] args) {
        executeCommand(new KYO_DSS_F007(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        //step1 : 공시실 상품명 찾기
        findProductName(info.getProductNamePublic());

        //step2 : 고객정보 입력
        setUserInfo(info);

        //step3 : 주계약정보 입력
        setMainTreatyInfo(info);

        //step4 : 선택특약정보 입력
        setSubTreatiesInfo(info);

        //stpe5 : 보험료 크롤링
        crawlPremium(info);

        //step6 : 해약환급금 크롤링
        crawlReturnMoneyList(info);

        return true;
    }

}


//package com.welgram.crawler.direct.life.kyo;
//
//import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce;
//import com.welgram.crawler.general.CrawlingProduct;
//import org.openqa.selenium.By;
//
//
//// 2023.07.05 | 최우진 | (무)교보내맘쏙건강보험
//public class KYO_DSS_F007 extends CrawlingKYOAnnounce {
//
//    public static void main(String[] args) { executeCommand(new KYO_DSS_F007(), args); }
//
//    @Override
//    protected boolean scrap(CrawlingProduct info) throws Exception {
//
//        // INFORMATION
//        String[] textType = info.getTextType().split("#");
//        String refundOption = "BASE";
//
//        // PROCESS
//        logger.info("▉▉▉▉ 시작 ▉▉▉▉");
//        initKYO(info, textType[1]);
//
//        logger.info("▉▉▉▉ {} ▉▉▉▉", info.getProductNamePublic());
//        setBirthday(driver.findElement(By.xpath("//*[@id='inpBhdt']")), info.getFullBirth(), 2);
//        setGender(null, null, info.getGender(), 5);
//
//        logger.info("▉▉▉▉ 주계약 ▉▉▉▉");
//        setProductKind(driver.findElement(By.xpath("//*[@id='sel_gdcl']")), info.getProductNamePublic(), 3);
//        setInsTerm(driver.findElement(By.xpath("//*[@id='5188000_isPd']")), info.getInsTerm(), 2);
//        setAssureMoney(driver.findElement(By.xpath("//*[@id='5188000_sbcAmt']")), info.getAssureMoney(), 2);
//        setNapCycle(driver.findElement(By.xpath("//*[@id='pdtMcrnCd_paCyc']")), info.getNapCycleName(), 2);
//        setNapTerm(driver.findElement(By.xpath("//*[@id='5188000_paPd']")), info.getNapTerm(), info.getInsTerm(), 2, false);
//
//        logger.info("▉▉▉▉ 특약 ▉▉▉▉");
//        submitTreatiesInfo(driver.findElements(By.xpath("//*[@id='scnList']/table/tbody/tr")), info);
//        pushButton(driver.findElement(By.xpath("//*[@id='pop-calc']/div/div[3]/div/button")), 4);
//
//        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
//        crawlPremium(null, info, 2);
//        pushButton(driver.findElement(By.xpath("//*[@id='areaPrm']/div[2]/button[1]")), 5);
//        pushButton(driver.findElement(By.xpath("//*[@id='oPopHisMenu']/li[2]/a")), 3);
//        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='trmRview']/div[2]/table/tbody/tr")), info, refundOption);
//
//        return true;
//    }
//}
