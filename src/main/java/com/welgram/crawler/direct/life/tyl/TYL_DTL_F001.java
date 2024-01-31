package com.welgram.crawler.direct.life.tyl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.FinLogger;
import com.welgram.crawler.direct.life.CrawlingTYL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// 2022.07.29 | 최우진 | 대면_치아보험
// TYL_DTL_F001 :: 무배당수호천사꼭필요한치아보험(갱신형) 프리미엄
public class TYL_DTL_F001 extends CrawlingTYL {

    public static void main(String[] args) {
        executeCommand(new TYL_DTL_F001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // FIEDLS
        CrawlingTreaty mainTreaty = info.treatyList.get(0);
        String tempName = PersonNameGenerator.generate();

        // SETUP
        findInsuFromAnnounce("무배당수호천사꼭필요한치아보험(갱신형)");

        // [ STEP1 - 고객정보를 입력해 주세요 ]
        submitCustomerInfo(tempName, info.getFullBirth(), info.getGender());

        // [ STEP2 - 주상품을 선택해 주세요 ]
        setPlanType("무배당수호천사 꼭필요한치아보험(갱신형)프리미엄형-최초계약");
        announceBtnClick(By.xpath("//span[contains(., '특약 조회')]"));

        // STEP3 - 특약을 선택해 주세요
        logger.info(":: 특약개수 :: {}", info.getTreatyList().size());
        for (int i = 0; i < info.getTreatyList().size(); i++) {

            String trtName = info.getTreatyList().get(i).treatyName;
            String trtAssureMoney = String.valueOf(info.getTreatyList().get(i).assureMoney / 10000);

            if ("무배당수호천사 꼭필요한치아보험(갱신형)프리미엄형-최초계약".contains(trtName)) {
                logger.info("주계약 :: 무배당수호천사 꼭필요한치아보험(갱신형)프리미엄형-최초계약");
                WebElement tr1 = driver.findElement(By.xpath("//label[contains(., '무배당수호천사 꼭필요한치아보험(갱신형)프리미엄형-최초계약')]/ancestor::tr"));
                // helper.doClick(By.xpath("//label[contains(., '무배당수호천사 꼭필요한치아보험(갱신형)프리미엄형-최초계약')]/ancestor::tr//td[1]//input[1]"));
                WebElement inputAmt1 = tr1.findElement(By.xpath(".//td[7]/div/input"));
                inputAmt1.click();
                inputAmt1.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                inputAmt1.sendKeys(trtAssureMoney);
                logger.info("주계약 금액 입력 완료 :: ");
                WaitUtil.waitFor(2);

            } else if ("무배당꼭필요한 보철치료특약(갱신형)-프리미엄형-최초계약".contains(trtName)) {
                logger.info("선택특약 :: 무배당꼭필요한 보철치료특약(갱신형)-프리미엄형-최초계약");
                WebElement tr1 = driver.findElement(By.xpath("//label[contains(., '무배당꼭필요한 보철치료특약(갱신형)-프리미엄형-최초계약')]/ancestor::tr"));

                // helper.doClick(By.xpath("//label[contains(., '무배당꼭필요한 보철치료특약(갱신형)-프리미엄형-최초계약')]/ancestor::tr//td[1]//input[1]"));
                driver.findElement(By.xpath("//label[contains(., '무배당꼭필요한 보철치료특약(갱신형)-프리미엄형-최초계약')]/ancestor::tr//td[1]//input[1]")).click();

                WebElement inputAmt1 = tr1.findElement(By.xpath(".//td[7]/div/input"));
                inputAmt1.click();
                inputAmt1.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                inputAmt1.sendKeys(trtAssureMoney);
                logger.info("선택특약[1] 금액 입력 완료 :: ");
                WaitUtil.waitFor(2);

            } else if ("무배당꼭필요한 보존치료특약(갱신형)-최초계약".contains(trtName)) {
                logger.info("선택특약 :: 보존치료특약(갱신형)-최초계약 :: ");
                WebElement tr1 = driver.findElement(By.xpath("//label[contains(., '무배당꼭필요한 보존치료특약(갱신형)-최초계약')]/ancestor::tr"));

                // helper.doClick(By.xpath("//label[contains(., '무배당꼭필요한 보존치료특약(갱신형)-최초계약')]/ancestor::tr//td[1]//input[1]"));
                driver.findElement(By.xpath("//label[contains(., '무배당꼭필요한 보존치료특약(갱신형)-최초계약')]/ancestor::tr//td[1]//input[1]")).click();

                // 그외 변경없음
                WebElement inputAmt1 = tr1.findElement(By.xpath(".//td[7]/div/input"));
                inputAmt1.click();
                inputAmt1.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                inputAmt1.sendKeys(trtAssureMoney);
                logger.info("선택특약[2] 금액 입력 완료 :: ");
                WaitUtil.waitFor(2);

            } else if ("무배당꼭필요한 치아치료특약(갱신형)-최초계약".contains(trtName)) {
                logger.info("선택특약 :: 치아치료특약(갱신형)-최초계약 :: ");
                WebElement tr2 = driver.findElement(By.xpath("//label[contains(., '무배당꼭필요한 치아치료특약(갱신형)-최초계약')]/ancestor::tr"));

                // helper.doClick(By.xpath("//label[contains(., '무배당꼭필요한 치아치료특약(갱신형)-최초계약')]/ancestor::tr//td[1]//input[1]"));
                driver.findElement(By.xpath("//label[contains(., '무배당꼭필요한 치아치료특약(갱신형)-최초계약')]/ancestor::tr//td[1]//input[1]")).click();

                // 그외 변경없음
                WebElement inputAmt2 = tr2.findElement(By.xpath(".//td[7]/div/input"));
                inputAmt2.click();
                inputAmt2.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                inputAmt2.sendKeys(trtAssureMoney);
                logger.info("선택특약[3] 금액 입력 완료 :: ");
                WaitUtil.waitFor(2);

            } else {
                logger.info("ERROR :: 알수없는 특약리스트 :: {}", trtName);
            }
        }

        WebElement tr0 = driver.findElement(By.xpath("//label[contains(., '무배당수호천사 꼭필요한치아보험(갱신형)프리미엄형-최초계약')]/ancestor::tr"));
        WebElement inputAmt0 = tr0.findElement(By.xpath(".//td[7]/div/input"));
        inputAmt0.click();
        inputAmt0.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        inputAmt0.sendKeys(String.valueOf(info.getTreatyList().get(0).assureMoney/10000));

        // CALCULATE
        logger.info("보험료 계산 버튼 클릭!");
        announceBtnClick(By.xpath("//span[contains(., '보험료 계산')]"));

        // 화면전환) 보험료계산 결과
        logger.info("주계약 보험료 설정");
        setAnnounceMonthlyPremium(mainTreaty);

        logger.info("스크린샷 찍기");
        WebElement element = driver.findElement(By.xpath("//th[text()='합계보험료']/parent::tr//span[@class='point1']"));
        moveToElement(element);
        takeScreenShot(info);

        logger.info("보장내용 상세보기 버튼 클릭!");
        announceBtnClick(By.xpath("//span[contains(., '보장내용상세보기')]"));
        logger.info("해약환급금 조회");
        getAnnounceShortReturnPremiums(info);

        return true;

    }

}
