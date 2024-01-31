package com.welgram.crawler.direct.life.kyo;

import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanCalc;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;


// 2023.11.28 | 최우진 | 교보간편가입암보험(무배당)
public class KYO_CCR_F011 extends CrawlingKYOAnnounce {

    public static void main(String[] args) { executeCommand(new KYO_CCR_F011(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String[] textType = info.getTextType().split("#");
        String refundOption = "BASE"; // 1. 주계약 + 특약 | 2. 주계약 혼합 표기

        // PROCESS
        logger.info("▉▉▉▉ 시작 ▉▉▉▉");
        initKYO(info, textType[1]);

        logger.info("▉▉▉▉ {} ▉▉▉▉", info.getProductNamePublic());
        setBirthday(driver.findElement(By.xpath("//*[@id='inpBhdt']")), info.getFullBirth(), 2);
        setGender(null, null, info.getGender(), 5);

        logger.info("▉▉▉▉ 주계약 ▉▉▉▉");
        setProductKind(driver.findElement(By.xpath("//*[@id='sel_gdcl']")), textType[2], 3);
        setInsTerm(driver.findElement(By.xpath("//*[@id='5199600_isPd']")), info.getInsTerm(), 2);
        setAssureMoney(driver.findElement(By.xpath("//*[@id='5199600_sbcAmt']")), info.getAssureMoney(), 2);
        setNapCycle(driver.findElement(By.xpath("//*[@id='pdtMcrnCd_paCyc']")), info.getNapCycleName(), 2);
        setNapTerm(
            driver.findElement(By.xpath("//*[@id='5199600_paPd']")),
            info.getNapTerm(),
            info.getInsTerm(),
            2,
            false
        );

        logger.info("▉▉▉▉ 특약 ▉▉▉▉");
        submitTreatiesInfo(driver.findElements(By.xpath("//*[@id='scnList']/table/tbody/tr")), info);
        pushButton(driver.findElement(By.xpath("//*[@id='pop-calc']/div/div[3]/div/button")), 4);
        handleFloatingTreaty(info);

        logger.info("▉▉▉▉ 결과확인 ▉▉▉▉");
        crawlPremium(null, info, 2);
        pushButton(driver.findElement(By.xpath("//*[@id='areaPrm']/div[2]/button[1]")), 5);
        pushButton(driver.findElement(By.xpath("//*[@id='oPopHisMenu']/li[2]/a")), 3);
        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='trmRview']/div[2]/table/tbody/tr")), info, refundOption);

        return true;
    }



    private void handleFloatingTreaty(CrawlingProduct info) {

        logger.info("==============================");
        logger.info("가입금액 변동특약에 대한 처리 필요!!");
        List<WebElement> trList = driver.findElements(By.xpath("//div[@id='scnList']/table/tbody/tr"));
        for (WebElement tr : trList) {
            String assAmtTitle = tr.findElement(By.xpath(".//td[1]//label")).getText();

            // todo | get(4) 같은 방식아닌 가입금액 변동특약여부 알수있는 column값이 필요한 상황 (2023.11)
            if (info.getTreatyList().get(4).getTreatyName().equals(assAmtTitle)) {
                logger.info("assAmtTitle :: {}", assAmtTitle);
                String assAmt
                        = tr.findElement(By.xpath(".//td[4]//input"))
                        .getAttribute("value")
                        .replaceAll("[^0-9]", "");

                PlanCalc planCalc = new PlanCalc();
                planCalc.setMapperId(Integer.parseInt(info.treatyList.get(4).mapperId));
                planCalc.setGender((info.getGender() == MALE) ? "M" : "F");
                planCalc.setInsAge(Integer.parseInt(info.age));
                planCalc.setAssureMoney(assAmt);
                info.treatyList.get(4).setPlanCalc(planCalc);

                logger.info("CHECK FLOATING ASS AMT :: {}", info.treatyList.get(4).getPlanCalc().getAssureMoney());

                logger.info("가입금액 변동특약 처리 완료");
            }
        }
        logger.info("==============================");
    }
}
