package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.05.08 | 최우진 | 연금저축 교보연금보험
public class KYO_ASV_F001 extends CrawlingKYOAnnounce {

    public static void main(String[] args) {
        executeCommand(new KYO_ASV_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String[] textType = info.getTextType().split("#");
        String refundOption = "FULL";


        // PROCESS
        logger.info("▉▉▉▉ 시작 ▉▉▉▉");
        initKYO(info, textType[1]);

        logger.info("▉▉▉▉ {} ▉▉▉▉", info.getProductNamePublic());
        setBirthday(driver.findElement(By.xpath("//*[@id='inpBhdt']")), info.getFullBirth(), 2);
        setGender(null, null, info.getGender(), 5);

        logger.info("▉▉▉▉ 주계약 ▉▉▉▉");
        setProductKind(driver.findElement(By.xpath("//*[@id='sel_gdcl']")), textType[2], 3);
        setInsTerm(driver.findElement(By.xpath("//*[@id='5164900_isPd']")), info.getInsTerm(), 2);
        setNapCycle(driver.findElement(By.xpath("//*[@id='pdtMcrnCd_paCyc']")), info.getNapCycleName(), 2);
        setNapTerm(
            driver.findElement(By.xpath("//*[@id='5164900_paPd']")),
            info.getNapTerm(),
            info.getInsTerm(),
            2,
            false
        );
        setPremium(
            driver.findElement(By.xpath("//*[@id='5164900_prm']")),
            info.getAssureMoney(),
            2
        );
        setAnnuityAge(
            driver.findElement(By.xpath("//*[@id='anBgnAe']")),
//            driver.findElement(By.id("anBgnAe")),
            info.getAnnuityAge(),
            4
        );
        //todo | setAnnuityAge 페이지 에러로 인해 같은 작업 2번 반복
        setAnnuityAge(
            driver.findElement(By.xpath("//*[@id='anBgnAe']")),
            info.getAnnuityAge(),
            4
        );

        logger.info("▉▉▉▉ 특약 ▉▉▉▉");
        submitTreatiesInfo(driver.findElements(By.xpath("//*[@id='scnList']/table/tbody/tr")), info);
        pushButton(driver.findElement(By.xpath("//*[@id='pop-calc']/div/div[3]/div/button")), 4);

        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
        crawlPremium(null, info, 2);
        pushButton(driver.findElement(By.xpath("//*[@id='areaPrm']/div[2]/button[1]")), 5);
        pushButton(driver.findElement(By.xpath("//*[@id='oPopHisMenu']/li[2]/a")), 3);

        crawlReturnMoneyList(
            driver.findElements(By.xpath("//*[@id='trmRview']/div[2]/table/tbody/tr")),
            info,
            refundOption
        );
        pushButton(driver.findElement(By.xpath("//*[@id='oPopHisMenu']/li[3]/a")), 3);

        crawlAnnuityInfo(
            driver.findElement(By.xpath("//*[@id='anXmplRview']/div[1]/div[2]/span")).getText(),
            info
        );

        return true;
    }
}


