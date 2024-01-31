package com.welgram.crawler.direct.life.ail;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.Job;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;



// 2023.03.03 | 최우진 | 무배당 GOLDEN CHOICE 연금보험
// 2023.05.25 | 최우진 | 표준화 1차
public class AIL_ANT_F001 extends CrawlingAILAnnounce {

    public static void main(String[] args) {
        executeCommand(new AIL_ANT_F001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        int unitGJ = 10000; // 1만, 10k
        String tempJob = Job.MANAGER.getCodeValue();
        String drivingType = "승용차(자가용)";
//        String elTitleNapCycle = "주계약 납입주기";        // element 경로 찾기용 문자열
//        String elTitleAssureMoney = "주계약 납입주기";     // element 경로 찾기용 문자열
//        String refundOption = "FULL";
        String receiveCycle = "매년"; // 연금받는 방법
        String[] arrTextType = info.getTextType().split("#");

        initAIL(info, arrTextType);

        setUserName("custNm");
        setBirthday("brthDt", info.getFullBirth());
        setVehicle("drvgCd", drivingType);
        setSmokeOption("grp-rdo3");
        setJob(tempJob);

//        setInsTerm("//*[@id='polprd_O30100_03E']", info.getInsTerm());
        /** 전기납으로 선택할 경우 자기관리자금 수령시 해약환급금 및 연금수령액이 "-"로 표시되어 전기납이 아닌 10년납으로 선택하게 했습니다. */
        setNapTerm(driver.findElement(By.id("payprd_FM01H2_10F")), info.getNapTerm(), info.getInsTerm());
        setNapCycle(driver.findElement(By.id("payCyclCd")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.id("sprm_FM01H2_10F")), info.getAssureMoney(), unitGJ);

        setMainTreatyType(driver.findElement(By.id("antyTypCdInfo")), info.planSubName);

        setAnnuityAge(driver.findElement(By.id("antyStAge")), info.getAnnuityAge());

        setAnnuityReceiveCycle(driver.findElement(By.id("antyPayCyclCd")), receiveCycle);

        pushButton(By.xpath("/html/body/form[1]/div/div[1]/div[5]/button"), 2);
        pushButton(By.xpath("/html/body/form[1]/div/div[2]/div/div[2]/button"), 2);
        crawlPremium(helper.waitVisibilityOfElementLocated(By.xpath("//*[@id='layer1']/div[3]/table/tfoot/tr/td/strong")), info);
        takeScreenShot(info);
        pushButton(By.xpath("//*[@id='tabLISTBox']/div[1]/ul/li[3]/a"), 2);

//        crawlReturnMoneyList(driver.findElements(By.xpath("//*[@id='layer3']/div/div/table/tbody/tr")), info, refundOption);
//        logger.info("{}", driver.findElements(By.cssSelector("#layer3 > div:nth-child(3) > div > table > tbody > tr:nth-child(1)")).size());

        /** 자기관리자금 수령시 기준으로 크롤링 했습니다 */
        crawlRefundFFull(driver.findElements(By.xpath("//*[@id=\"layer3\"]/div[1]/div/table/tbody/tr")), info);
        pushButton(By.xpath("//*[@id='tabLISTBox']/div[1]/ul/li[4]/a"), 2);
        crawlExpectedSavePremium(driver.findElements(By.xpath("//*[@id=\"layer4\"]/div[1]/div/table/tbody/tr")), info);

        return true;

    }



    // 주계약형태 지정
    protected void setMainTreatyType(WebElement elment, String planSubName) throws CommonCrawlerException{
        try {
            // 주계약 형태 선택
            String[] $planSubName = planSubName.split("#");

            Select $selectTreatyType = new Select(elment);
            $selectTreatyType.selectByVisibleText($planSubName[2]);

            logger.info("주계약 형태 설정 :: {}", $planSubName[2]);

            // prove
            String strSelected =
                String.valueOf(
                    ((JavascriptExecutor)driver).executeScript("return $(arguments[0]).find('option:selected').text();", $selectTreatyType));
            printLogAndCompare("[검증] 주계약 형태", $planSubName[2], strSelected);

        } catch(Exception e) {
            throw new SetNapTermException("주계약 형태 설정 중 에러 발생\n" + e.getMessage());
        }

    }



    // 연금받는방법 설정
    protected void setAnnuityReceiveCycle(WebElement elment, String receiveCycle) throws CommonCrawlerException{

        try{
            Select $selectReceiveCycle = new Select(elment);
            $selectReceiveCycle.selectByVisibleText(receiveCycle);
            logger.info("연금받는방법 설정 :: {}", receiveCycle);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_CYCLE;
            throw new PremiumCrawlerException("연금받는방법 선택 중 에러가 발생\n" + exceptionEnum.getMsg());
        }

    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        WebElement el = (WebElement) obj[0];
        String napTerm = (String) obj[1];
        String insTerm = (String) obj[2];

        try {
            // 보험기간 선택
            napTerm = napTerm + "납";
            Select $selectNapTerm = new Select(el);
            $selectNapTerm.selectByVisibleText(napTerm);
            logger.info("납입기간 설정 :: {}", napTerm);

            // prove
            String strSelected =
                String.valueOf(((JavascriptExecutor)driver).executeScript("return $(arguments[0]).find('option:selected').text();", $selectNapTerm));
            printLogAndCompare("[검증] 납입기간", napTerm, strSelected);

        } catch(Exception e) {
            throw new SetNapTermException("납입기간 설정 에러 발생\n" + e.getMessage());
        }

    }



    @Override
    protected void crawlRefundFFull(Object... obj) throws Exception {

        List<WebElement> defaultElList = driver.findElements(By.xpath("//*[@id=\"layer3\"]/div[1]/div/table/tbody/tr"));
        List<WebElement> $elList = (obj[0] == null) ? defaultElList : (List<WebElement>) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

//        // 단위 확인
        int unit = 1;
//        WebElement $unit = driver.findElement(By.xpath("//*[@id='layer3']/p"));
//        String unitTester = $unit.getText();
//
//        if(unitTester.contains("만원")) {
//            unit = 10000;
//            logger.info("UNIT :: {}", unit);
//        }

        // 환급정보 크롤링
        try {
            logger.info("=========== REFUND INFO ===========");
            List<PlanReturnMoney> prmList = new ArrayList<>();

            for (int i = 0; i < $elList.size(); i++) {
//                if(i % 2 == 1) {

                    PlanReturnMoney prm = new PlanReturnMoney();
                    WebElement $trEl = $elList.get(i);
//                    WebElement $trEven = $elList.get(i);

                    String term = $trEl.findElement(By.xpath("./td[1]")).getText();
                    String premiumSum = String.valueOf(Integer.parseInt($trEl.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "")) * unit);
                    String returnMoney = String.valueOf(Integer.parseInt($trEl.findElement(By.xpath("./td[6]")).getText().replaceAll("[^0-9]", "")) * unit );
                    String returnRate = $trEl.findElement(By.xpath("./td[7]")).getText();
//                    String returnMoneyAvg = String.valueOf(Integer.parseInt($trEl.findElement(By.xpath("./td[8]")).getText().replaceAll("[^0-9]", "")) * unit);
//                    String returnRateAvg = $trEl.findElement(By.xpath("./td[9]")).getText();
//                    String returnMoneyMin = String.valueOf(Integer.parseInt($trEl.findElement(By.xpath("./td[11]")).getText().replaceAll("[^0-9]", "")) * unit);
//                    String returnRateMin = $trEl.findElement(By.xpath("./td[12]")).getText();

                    logger.info("=================================");
                    logger.info("TERM           :: {}", term);
                    logger.info("PREMIUM_SUM    :: {}", premiumSum);
                    logger.info("RETURN_MONEY   :: {}", returnMoney);
                    logger.info("RETURN_RATE    :: {}", returnRate);
                    logger.info("=================================");
//                    logger.info("RMONEY_AVG     :: {}", returnMoneyAvg);
//                    logger.info("RRATE_AVG      :: {}", returnRateAvg);
//                    logger.info("RMONEY_MIN     :: {}", returnMoneyMin);
//                    logger.info("RRATE_MIN      :: {}", returnRateMin);
//                    logger.info("====================================");

                    prm.setTerm(term);
                    prm.setPremiumSum(premiumSum);
                    prm.setReturnMoney(returnMoney);
                    prm.setReturnRate(returnRate);
//                    prm.setReturnMoneyAvg(returnMoneyAvg);
//                    prm.setReturnRateAvg(returnRateAvg);
//                    prm.setReturnMoneyMin(returnMoneyMin);
//                    prm.setReturnRateMin(returnRateMin);

                    prmList.add(prm);

                    info.setReturnPremium(returnMoney);
//                }
            }
            info.setPlanReturnMoneyList(prmList);

            logger.error("더이상 참조할 차트가 없습니다");
            logger.info("=============================");

//            if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
//                info.setReturnPremium("0");
//                logger.info("보험형태 : {} 상품이므로 만기환급금을 0원으로 설정합니다", info.treatyList.get(0).productKind);
//            }

        } catch(Exception e) {
            throw new CommonCrawlerException("해약환급정보(FULL) 크롤링중 에러발생\n" + e.getMessage());
        }

    }



    @Override
    protected void initAIL(CrawlingProduct info, String[] arrTextType) throws Exception {

        // Scrap 시작 지점
        logger.info("START [ {} :: {} ]",info.getProductCode(), info.getProductNamePublic());
        logger.info("AIL은 '구좌'키워드 사용중입니다 변수명unitGJ의 단위에 주의하세요");
        logger.info("AIL은 '전기납'키워드 사용중입니다 setNapTerm()시 파라미터에 보험기간, 납입기간 둘다 필요합니다");
        // textType 표시
        // todo | textType 확인

        // 상품분류 선택
        try {
            String key = (arrTextType.length > 1) ? arrTextType[1] : arrTextType[0]; // ex.AIA Vitality 다이렉트 / 건강·상해보험
//            String key = arrTextType[1];        // ex.AIA Vitality 다이렉트 / 건강·상해보험
            logger.info("상품분류를 선택합니다 :: {}", key);
            Select $selectProductDivision = new Select(driver.findElement(By.id("product_kind")));
            $selectProductDivision.selectByVisibleText(key);
            WaitUtil.waitFor(4);

            // 확인 클릭!
            helper.click(By.cssSelector("body > form:nth-child(9) > div > div > ul > li:nth-child(1) > button"));
            WaitUtil.waitFor(4);

            //prove

        } catch (Exception e) {
            throw new CommonCrawlerException("상품분류 선택중 에러발생\n" + e.getMessage());
        }

        // 상품명 선택
        try {
            String key = info.getPlanSubName();
            String[] productNm = key.split("#");
            int age = Integer.parseInt(info.getAge().replaceAll("[^0-9]", ""));
            if (age > 15) {
                key = productNm[1];
            } else if (age <= 15) {
                key = productNm[0];
            }
            Select $selectProductName = new Select(driver.findElement(By.id("planNo")));
            $selectProductName.selectByVisibleText(key);
            logger.info("상품명을 선택합니다 :: {}", key);
            WaitUtil.waitFor(2);

            // 확인 클릭!
            helper.click(By.cssSelector("body > form:nth-child(9) > div > div > ul > li:nth-child(2) > button"));
            WaitUtil.waitFor(4);

        } catch (Exception e) {
            throw new CommonCrawlerException(e.getMessage() + "상품명 선택중 에러발생");
        }

        // 새로운 팝업창 핸들러
        try {
            String lastWindow = null;
            Set<String> handles = driver.getWindowHandles();
            for (String aux : handles) {
                lastWindow = aux;
            }
            logger.info("IFRAME 창 전환");
            driver.switchTo().window(lastWindow);
            driver.switchTo().frame(1); // frame 전환
            WaitUtil.waitFor(4);

        } catch (Exception e) {
            throw new CommonCrawlerException(e.getCause(), "FRAME 전환중 에러 발생");
        }

    }

}