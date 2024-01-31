package com.welgram.crawler.direct.life.ibk;

import com.welgram.common.CrawlerSlackClient;
import com.welgram.common.HostUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingIBK;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class IBK_ANT_D004 extends CrawlingIBK {

    public static void main(String[] args) {
        executeCommand(new IBK_ANT_D004(), args);
    }

    @Override
    public int doCrawlInsurance(CrawlingProduct info) {

        return true ? userSite(info) : publicSite(info);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        return false;
    }

    private Map<String, Object> vars = new HashMap<>();

    public String waitForWindow(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<String> whNow = driver.getWindowHandles();
        Set<String> whThen = (Set<String>) vars.get("window_handles");
        if (whNow.size() > whThen.size()) {
            whNow.removeAll(whThen);
        }
        return whNow.iterator().next();
    }

    private int userSite(CrawlingProduct info) {

        int _result = 1;

        try {

            startDriver(info);

            wait = new WebDriverWait(driver, 120);

            wait.until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBackground")));

            wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("birth")));

            logger.info("생년월일: {}", info.fullBirth);
            driver.findElement(By.id("birth")).sendKeys(info.fullBirth);

            String _gender = info.gender == 0 ? "남" : "여";
            logger.info("성별: {}", _gender);
            driver.findElement(By.xpath("//label[contains(.,'" + _gender + "')]")).click();

            // 연금액 알아보기 클릭
            logger.info("연금액 알아보기");
            driver.findElement(By.id("btnInsuInfo")).click();

            WaitUtil.waitFor(2);
            wait.until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBackground")));

            // 월보험료
            logger.info("월보험료: {}", info.assureMoney);
            WebElement moneyElement = driver.findElement(By.id("txtPayMoney02"));

            moneyElement.clear();

            WaitUtil.waitFor(2);
            moneyElement
                .sendKeys(String.valueOf(Integer.valueOf(info.assureMoney) / 10000));

            WaitUtil.loading(1);
            // 연금개시나이
            logger.info("연금개시나이: {}", info.annuityAge);
            {

                WebElement dropdown = driver.findElement(By.id("insurance_term"));
                WaitUtil.loading(1);
                dropdown.findElement(By.xpath("//option[. = '" + info.annuityAge + "']")).click();

            }

            wait.until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBackground")));

            // 납입기간
            logger.info("납입기간: {}", info.napTerm);
            {

                WebElement dropdown = driver.findElement(By.id("pay_term"));
                WaitUtil.loading(1);
                String _napTerm = info.napTerm.replaceAll("년", "");
                dropdown.findElement(
                    By.xpath("./option[. = '" + _napTerm + "']")).click();

            }

            wait.until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBackground")));

            // 종신연금 보증기간
            String annuityType = info.annuityType.replaceAll("[^0-9]", "") + "년";


            logger.info("종신연금 보증기간: {}", annuityType);
            {
                WebElement dropdown = driver.findElement(By.id("anty_dfr_form"));
                WaitUtil.loading(1);
                dropdown.click();
                dropdown.findElement(By.xpath("./option[contains(.,'" + annuityType + "')]")).click();

            }

            // 보험료 할인방법(50만원 이하인 경우 해당안됨)
            {

                if (Integer.valueOf(info.assureMoney) > 500000) {
                    logger.info("보험료 할인방법: {}", "적립액가산형");
                    WebElement dropdown = driver.findElement(By.id("premDcSlc"));
                    WaitUtil.loading(1);
                    dropdown.findElement(By.xpath("./option[. = '적립액가산형']")).click();

                }
            }

            WaitUtil.loading(2);

            driver.findElement(By.xpath("//a[contains(.,'계산하기')]")).click();

            WaitUtil.loading(2);

            wait.until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBackground")));

            takeScreenShot(info);

            logger.info("연금액 예시");
            getPension(info);

            // 해약환급금 예시
            logger.info("해약환급금 예시");
            driver.findElement(By.id("premcalc2")).click();
            driver.switchTo().frame(13);
//
            List<WebElement> trElements = driver
                .findElements(By.cssSelector("#b2 tbody > tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            for (int i = 0; i < trElements.size(); i++) {

                WebElement trElement = trElements.get(i);

                PlanReturnMoney _planReturnMoney = new PlanReturnMoney();

                String _term = trElement.findElement(By.cssSelector("td:nth-child(1)")).getText();
                String _premium = trElement.findElement(By.cssSelector("td:nth-child(2)"))
                    .getText();

                String _returnMoneyMin = trElement
                    .findElement(By.cssSelector("td:nth-child(3)"))
                    .getText();
                String _returnRateMin = trElement
                    .findElement(By.cssSelector("td:nth-child(4)"))
                    .getText();

                String _returnMoneyAvg = trElement
                    .findElement(By.cssSelector("td:nth-child(5)"))
                    .getText();
                String _returnRateAvg = trElement
                    .findElement(By.cssSelector("td:nth-child(6)"))
                    .getText();

                String _returnMoney = trElement.findElement(By.cssSelector("td:nth-child(7)"))
                    .getText();
                String _returnRate = trElement.findElement(By.cssSelector("td:nth-child(8)"))
                    .getText();

                _planReturnMoney.setTerm(_term);
                _planReturnMoney.setPremiumSum(_premium);
                _planReturnMoney.setReturnMoney(_returnMoney);
                _planReturnMoney.setReturnRate(_returnRate);

                _planReturnMoney.setReturnMoneyAvg(_returnMoneyAvg);
                _planReturnMoney.setReturnRateAvg(_returnRateAvg);

                _planReturnMoney.setReturnMoneyMin(_returnMoneyMin);
                _planReturnMoney.setReturnRateMin(_returnRateMin);

                planReturnMoneyList.add(_planReturnMoney);
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

            logger.info("월보험료 세팅");
            info.treatyList.get(0).monthlyPremium = info.assureMoney;

            int _planReturnMoneySize = info.getPlanReturnMoneyList().size();

            PlanReturnMoney _fullPlanReturnMoney = info.getPlanReturnMoneyList()
                .get(_planReturnMoneySize - 1);

            logger.info("만기환급금");
            info.returnPremium = _fullPlanReturnMoney.getReturnMoney();

            _result = 0;
        } catch (Exception e) {

            e.printStackTrace();
            logger.debug("크롤링 에러: " + e.getMessage());

            info.totPremium = "0";
            info.treatyList.get(0).monthlyPremium = "0";
            info.savePremium = "0";
            info.returnPremium = "0";
            info.annuityPremium = "0";
            info.errorMsg = e.getMessage();

            CrawlerSlackClient.errorPost(HostUtil.getUsername(), info.productCode, e.getMessage());

        } finally {
            stopDriver(info);
        }

        return _result;
    }

    // 연금수령액 조회(사용자)
    protected void getPension(CrawlingProduct info) throws Exception {
        String annuityType = info.annuityType;

        // 연금액 예시 버튼 클릭
        driver.findElement(By.cssSelector("#premcalc1")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBackground")));
        WaitUtil.loading(2);

        driver.switchTo().frame(13);

        //연금수령액 테이블 추가
        String whl10yPremium = driver.findElement(
                By.cssSelector("#b1 > table > tbody > tr:nth-child(1) > td:nth-child(5)")).getText()
            .replaceAll("[^0-9]", "");      //종신 10년 연금수령액
        String whl20yPremium = driver.findElement(
                By.cssSelector("#b1 > table > tbody > tr:nth-child(5) > td:nth-child(5)")).getText()
            .replaceAll("[^0-9]", "");      //종신 20년 연금수령액
        String whl30yPremium = driver.findElement(
                By.cssSelector("#b1 > table > tbody > tr:nth-child(9) > td:nth-child(5)")).getText()
            .replaceAll("[^0-9]", "");      //종신 30년 연금수령액
        String whl100aPremium = driver.findElement(
                By.cssSelector("#b1 > table > tbody > tr:nth-child(13) > td:nth-child(5)")).getText()
            .replaceAll("[^0-9]", "");    //종신 100세 연금수령액
        String fxd10yPremium = driver.findElement(
                By.cssSelector("#b1 > table > tbody > tr:nth-child(19) > td:nth-child(4)")).getText()
            .replaceAll("[^0-9]", "");     //확정 10년 연금수령액
        String fxd15yPremium = driver.findElement(
                By.cssSelector("#b1 > table > tbody > tr:nth-child(20) > td:nth-child(4)")).getText()
            .replaceAll("[^0-9]", "");     //확정 15년 연금수령액
        String fxd20yPremium = driver.findElement(
                By.cssSelector("#b1 > table > tbody > tr:nth-child(21) > td:nth-child(4)")).getText()
            .replaceAll("[^0-9]", "");     //확정 20년 연금수령액
        String fxd25yPremium = driver.findElement(
                By.cssSelector("#b1 > table > tbody > tr:nth-child(22) > td:nth-child(4)")).getText()
            .replaceAll("[^0-9]", "");     //확정 25년 연금수령액
        String fxd30yPremium = driver.findElement(
                By.cssSelector("#b1 > table > tbody > tr:nth-child(23) > td:nth-child(4)")).getText()
            .replaceAll("[^0-9]", "");     //확정 30년 연금수령액

        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        planAnnuityMoney.setWhl10Y(whl10yPremium);
        planAnnuityMoney.setWhl20Y(whl20yPremium);
        planAnnuityMoney.setWhl30Y(whl30yPremium);
        planAnnuityMoney.setWhl100A(whl100aPremium);
        planAnnuityMoney.setFxd10Y(fxd10yPremium);
        planAnnuityMoney.setFxd15Y(fxd15yPremium);
        planAnnuityMoney.setFxd20Y(fxd20yPremium);
        planAnnuityMoney.setFxd25Y(fxd25yPremium);
        planAnnuityMoney.setFxd30Y(fxd30yPremium);

        info.planAnnuityMoney = planAnnuityMoney;

        String annuityPremium = "";
        String fixedAnnuityPremium = "";
        if ("종신 10년".equals(annuityType)) {
            annuityPremium = whl10yPremium;
        } else if ("종신 20년".equals(annuityType)) {
            annuityPremium = whl20yPremium;
        } else if ("종신 30년".equals(annuityType)) {
            annuityPremium = whl30yPremium;
        } else if ("종신 100세".equals(annuityType)) {
            annuityPremium = whl100aPremium;
        } else if ("확정 10년".equals(annuityType)) {
            annuityPremium = fxd10yPremium;
        } else if ("확정 15년".equals(annuityType)) {
            annuityPremium = fxd15yPremium;
        } else if ("확정 20년".equals(annuityType)) {
            annuityPremium = fxd20yPremium;
        } else if ("확정 25년".equals(annuityType)) {
            annuityPremium = fxd25yPremium;
        } else if ("확정 30년".equals(annuityType)) {
            annuityPremium = fxd30yPremium;
        }

        logger.info("연금타입 : {}", annuityType);
        logger.info(" 연금수령액 : {}원", annuityPremium);

        if(annuityType.contains("종신")) {
            info.annuityPremium = annuityPremium;
        } else {
            info.fixedAnnuityPremium = annuityPremium;
        }

        WaitUtil.loading(4);
        driver.switchTo().defaultContent();
        driver.findElement(By.linkText("닫기")).click();
    }

    private int publicSite(CrawlingProduct info) {
        int _result = 1;

        try {

            startDriver(info);

            logger.info("공시실 보험상품 찾기");
            searchProduct(info);

            logger.info("피보험자 성명");
            setName();

            logger.info("생년월일");
            setBirth(info.fullBirth, By.id("birth"));

            logger.info("성별");
            setGender(info.gender, By.className("pdr10"));

            logger.info("보험종류");
            kind(By.id("prod_type"));

            logger.info("연금개시나이");
            setPensionAge(info.annuityAge);

            logger.info("종신연금 보증기간");
            WebElement dropdown = driver.findElement(By.id("anty_dfr_form"));
            dropdown.findElement(By.xpath("//option[. = '10년']")).click();

            logger.info("납입기간");
            setNapTerm(info.napTerm);

            logger.info("납입주기");
            setNapCycle(info.napCycle);

            logger.info("납입보험료");
            setPremium(info.assureMoney, info);

            logger.info("보험료할인 여부: 적립가산형 선택(30만원초과)");
            setDiscount(300000, Integer.valueOf(info.assureMoney));

            logger.info("월보험료 세팅");
            info.treatyList.get(0).monthlyPremium = info.assureMoney;

            logger.info("연금수령액 조회");
            getPension(info);

            logger.info("해약환급금 조회");
            returnPremium(info);

            _result = 0;
        } catch (Exception e) {

            e.printStackTrace();
            logger.debug("크롤링 에러: " + e.getMessage());

            info.totPremium = "0";
            info.treatyList.get(0).monthlyPremium = "0";
            info.savePremium = "0";
            info.returnPremium = "0";
            info.annuityPremium = "0";
            info.errorMsg = e.getMessage();

            CrawlerSlackClient.errorPost(HostUtil.getUsername(), info.productCode, e.getMessage());

        } finally {
            //CrawlerSlackClient.errorPost(HostUtil.getUsername(),info.productCode, "테스트");
            stopDriver(info);

        }
        return _result;
    }


}
