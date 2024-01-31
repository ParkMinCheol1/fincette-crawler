package com.welgram.crawler.direct.life.kyo;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.kyo.CrawlingKYO.CrawlingKYOAnnounce2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


// 2023.12.04 | 최우진 | 교보뇌·심장케어보험(무배당, 서비스선택형)
public class KYO_DSS_F012 extends CrawlingKYOAnnounce2 {

    public static void main(String[] args) {
        executeCommand(new KYO_DSS_F012(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // todo | 공시실내 간편케이스 !!

        // 가격공시실 상품명 찾기
        findProductName(info.getProductNamePublic());

        // 생년월일
        WebElement $inputBirth = driver.findElement(By.xpath("//input[@id='ins0_id0']"));
//        $inputBirth.click();
        $inputBirth.sendKeys(info.getFullBirth());

        // 성별
        if (info.getGender() == MALE) {
            driver.findElement(By.xpath("//input[@id='sdt1']/parent::label")).click();
        } else {
            driver.findElement(By.xpath("//input[@id='sdt2']/parent::label")).click();
        }
        WaitUtil.waitFor(4);

        // 보험료 계산
//        driver.findElement(By.xpath("//button[text()='보험료 계산']")).click();
        driver.findElement(By.xpath("//button[@id='isPrcClc0']")).click();
        WaitUtil.waitFor(4);

        // 주계약 내용 확인
        List<WebElement> provementList = driver.findElements(By.xpath("//*[@id='tabsld_calc']/div/ul/li[2]/div[1]/div[2]/article/div[2]/ul/li"));
        for (WebElement provementee : provementList) {
            String provementeeTypeString = provementee.findElement(By.xpath(".//div/div[1]")).getText();
            if ("보험종류".equals(provementeeTypeString)) {
                String provementeeString
                    = provementee.findElement(By.xpath(".//div/div[2]"))
                        .getText();
                if (provementeeString.equals(info.getProductNamePublic())) {
                    logger.info("TEST 1 : OK ({})", provementeeString);
                }
            }

            if ("가입금액".equals(provementeeTypeString)) {
                String provementeeString
                    = provementee.findElement(By.xpath(".//div/div[2]"))
                        .getText()
                        .replaceAll("[^0-9]", "");

//                String assAmt = String.valueOf(info.getTreatyList().get(0).assureMoney / 1_0000);
                String assAmt = String.valueOf(info.getTreatyList().get(0).assureMoney);

                if (provementeeString.equals(assAmt)) {
                    logger.info("TEST 2 : OK ({})", assAmt);
                }
            }

            if ("보험기간".equals(provementeeTypeString)) {
                String provementeeString
                    = provementee.findElement(By.xpath(".//div/div[2]"))
                        .getText();

                String insTermComparor = info.getTreatyList().get(0).insTerm + "만기";
                if (provementeeString.equals(insTermComparor)) {
                    logger.info("TEST 3 : OK ({})", insTermComparor);
                }
            }

            if ("납입기간".equals(provementeeTypeString)) {
                String tempText = provementee.findElement(By.xpath(".//div/div[2]")).getText();
                String provementeeString
                    = (tempText.equals("전기납"))
                        ? tempText
                        : tempText + "납";

                CrawlingTreaty mainTreaty = info.getTreatyList().get(0);
                String napTermComparor = mainTreaty.napTerm;

                if (mainTreaty.insTerm.equals(mainTreaty.napTerm)) {
                    mainTreaty.napTerm = "전기납";
                }

                if (provementeeString.equals(napTermComparor)) {
                    logger.info("TEST 4 : OK ({})", napTermComparor);
                }
            }
        }

        // 보험료 크롤링
        String premium
            = driver.findElement(
                    By.xpath("//*[@id='tabsld_calc']/div/ul/li[2]/div[1]/div[1]/article/div/ul/li/div/div[2]/span/em"))
                .getText()
                .replaceAll("[^0-9]", "");

        info.getTreatyList().get(0).monthlyPremium = premium;
        logger.info("보험료 :: {}", info.getTreatyList().get(0).monthlyPremium + "원");

        // 스크린샷
        takeScreenShot(info);

        // 해약환급정보
        driver.findElement(By.xpath("//button[text()='해약환급금']")).click();
        WaitUtil.waitFor(3);

        List<WebElement> trList = driver.findElements(By.xpath("//tbody[@id='trmRview_1']/tr"));
        List<PlanReturnMoney> prmList = new ArrayList<>();
        for (WebElement tr : trList) {
            String term = tr.findElement(By.xpath(".//td[1]")).getText();
            String premiumSum = tr.findElement(By.xpath(".//td[2]")).getText().replaceAll("[^0-9]","");
            String returnMoney = tr.findElement(By.xpath(".//td[3]")).getText().replaceAll("[^0-9]", "");
            String returnRate = tr.findElement(By.xpath(".//td[4]")).getText();

            PlanReturnMoney prm = new PlanReturnMoney();
            prm.setTerm(term);
            prm.setPremiumSum(premiumSum);
            prm.setReturnMoney(returnMoney);
            prm.setReturnRate(returnRate);

            logger.info("====================================");
            logger.info("TERM :: {}", term);
            logger.info("SUM :: {}", premiumSum);
            logger.info("AMT :: {}", returnMoney);
            logger.info("RATE :: {}", returnRate);

            prmList.add(prm);

            if(term.equals(info.napTerm)) {
                info.returnPremium = returnMoney;
                logger.info("====================================");
                logger.info("@ 만기환급금@@ :: {}", returnMoney);
            }
        }
        logger.info("====================================");

        return true;
    }

}