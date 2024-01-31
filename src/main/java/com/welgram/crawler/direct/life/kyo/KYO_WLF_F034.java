package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;


// 2023.07.14 | 최우진 | 교보실속초간편가입종신보험(무배당,보증비용부과형) (일반형, 기본형플러스)
public class KYO_WLF_F034 extends CrawlingKYOAnnounce {

    public static void main(String[] args) { executeCommand(new KYO_WLF_F034(), args); }

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
        setInsTerm(driver.findElement(By.xpath("//*[@id='5190903_isPd']")), info.getInsTerm(), 2);
        setAssureMoney(driver.findElement(By.xpath("//*[@id='5190903_sbcAmt']")), info.getAssureMoney(), 2);
        setNapCycle(driver.findElement(By.xpath("//*[@id='pdtMcrnCd_paCyc']")), info.getNapCycleName(), 2);
        setNapTerm(driver.findElement(By.xpath("//*[@id='5190903_paPd']")), info.getNapTerm(), info.getInsTerm(), 2, false);

        logger.info("▉▉▉▉ 특약 ▉▉▉▉");
        // todo | 종신 보험의 특약구성은 주계약 밖에 없음 :: default 상태 유지 (스크립트상 할게 없음)
        // submitTreatiesInfo(trtyList, info);
        pushButton(driver.findElement(By.xpath("//*[@id='pop-calc']/div/div[3]/div/button")), 4);

        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
        crawlPremium(null, info, 2);
        pushButton(driver.findElement(By.xpath("//*[@id='areaPrm']/div[2]/button[1]")), 7);
        pushButton(driver.findElement(By.xpath("//*[@id='oPopHisMenu']/li[2]/a")), 3);
        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='trmRview']/div[2]/table/tbody/tr")), info, refundOption);

        return true;
    }
}
