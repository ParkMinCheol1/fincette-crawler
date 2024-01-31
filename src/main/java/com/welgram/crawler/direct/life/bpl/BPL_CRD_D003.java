package com.welgram.crawler.direct.life.bpl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;



// 2023.05.09 | 최우진 | (무) (e)대출안심 보장보험(플랜선택형) 선택부가형
// 2023.08.25 | 공시실 크롤링으로 변경
public class BPL_CRD_D003 extends SeleniumCrawler {

    public static void main(String[] args) {
        executeCommand(new BPL_CRD_D003(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.findElement(By.xpath("//label[text()='보장성 보험']//preceding-sibling::input[@id='case01']//parent::li")).click();

        String[] textType = info.getTextType().split("#");
        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            String alertMessage = alert.getText();
            logger.debug(alertMessage);
            alert.accept();
        }

        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath(
                "//td[text()='" + info.getProductNamePublic() + "']//following-sibling::td//a[text()='보험료 계산']"
        ))));

        driver.findElement(By.xpath(
                "//td[text()='" + info.getProductNamePublic() + "']//following-sibling::td//a[text()='보험료 계산']"
        )).click();

        // 상품선택(대출안심보장보험(선택부가형)
        logger.info("상품선택 : " + info.productNamePublic);
        WaitUtil.waitFor(2);

        try {
            if (driver.findElements(By.xpath("/html/body/div[1]/div[2]/div/mat-dialog-container/iesub-prd-f0220/div/div")).size() > 0) {
                logger.info("팝업 있음");
                driver.findElement(By.xpath("//*[@id='wrap']/div/div/button")).click();
                WaitUtil.waitFor(1);
            }

        } catch (Exception e) {
            logger.info("이벤트 팝업 변경 or 없음");
        }

        logger.info("생년월일 : " + info.getFullBirth());
        WebElement e = driver.findElement(By.id("P_INDI_CPI_PL_jumin_no1"));
        wait.until(ExpectedConditions.elementToBeClickable(e));
        e.click();
        e.sendKeys(info.getFullBirth());

        logger.info("성별선택 : {}", info.gender == 0 ? "남자" : "여자");
        String gender = info.gender == 0 ? "P_INDI_CPI_PL_male" : "P_INDI_CPI_PL_female";
        driver.findElement(By.xpath("//input[@id='" + gender +"']//following-sibling::label")).click();

        logger.info("상품 유형 : {}", textType[0]);
        String productType = textType[0];
        if ("선택부가형".equals(productType)) {
            productType = "P_INDI_CPI_PL_select";
        } else {
            productType = "P_INDI_CPI_PL_fixed";
        }
        e = driver.findElement(By.xpath("//label[@for='" + productType + "']"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", e);
        e.click();

        WaitUtil.waitFor(1);
        e = driver.findElement(By.id("P_INDI_CPI_PL_planType"));
        e.click();

        WaitUtil.waitFor(1);
        String productType2 = textType[1];
        e.findElement(By.xpath("option[. = '" + productType2 + "']")).click();

        logger.info("가입금액 : {}", info.getAssureMoney());
        String strUnitTeller = driver.findElement(By.xpath(
                "//*[@id='layer_calc01_P_INDI_CPI_PL']/div/div[1]/div/div[2]/div/div[2]/div/div[3]/div[1]/div/span"
        )).getText();
        int unit = 1;
        if (strUnitTeller.contains("천원")) {
            unit = 1000;
        } else if (strUnitTeller.contains("만원")) {
            unit = 10_000;
        }
        String assureMoney = String.valueOf(Integer.parseInt(info.getAssureMoney()) / unit);
        driver.findElement(By.id("P_INDI_CPI_PL_specialSignPay0")).sendKeys(assureMoney);

        logger.debug("보험기간: {}", info.getInsTerm());
        e = driver.findElement(By.id("P_INDI_CPI_PL_insurance_term"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", e);
        e.click();
        WaitUtil.waitFor(1);
        e.findElement(By.xpath("./option[. = '" + info.getInsTerm() + "']")).click();

        logger.debug("납입기간: {}", info.getNapTerm());
        e = driver.findElement(By.id("P_INDI_CPI_PL_payment_term"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", e);
        e.click();
        WaitUtil.waitFor(1);
        e.findElement(By.xpath("option[. = '" + info.getNapTerm()+"납']")).click();

        logger.info("보험료 계산하기 버튼 클릭");
        driver.findElement(By.xpath("//*[@id='layer_calc01_P_INDI_CPI_PL']/div/div[2]/a[text()='보험료 계산하기']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//tbody[@id='A_result1']//tr//th[text()='총 보험료']//following-sibling::td"
        )));

        // 보험료조회
        String _premium = driver
                .findElement(By.xpath("//tbody[@id='A_result1']//tr//th[text()='총 보험료']//following-sibling::td"))
                .getText();
        logger.debug("보험료: {}", _premium);

        _premium = MoneyUtil.toDigitMoney(_premium).toString();
        info.treatyList.get(0).monthlyPremium = _premium;

        // 스크린샷
        takeScreenShot(info);
        WaitUtil.waitFor(2);

        logger.info("보험료/해약환급금 조회");

        List<WebElement> trElements = driver.findElements(By.xpath("//tbody[@id='A_result5_2']/tr"));
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (WebElement trElement : trElements) {
            String term = trElement.findElement(By.xpath("./td[1]"))
                .getAttribute("innerText");
            String premiumSum = trElement.findElement(By.xpath("./td[2]"))
                .getAttribute("innerText")
                .replaceAll("[^0-9]", "");
            String returnMoney = trElement.findElement(By.xpath("./td[3]"))
                .getAttribute("innerText")
                .replaceAll("[^0-9]", "");
            String returnRate = trElement.findElement(By.xpath("./td[4]"))
                .getAttribute("innerText")
                .replaceAll("%", "");

            logger.debug("납입기간: {},  납입보험료: {}, 해약환급금:{}, 해약환급률:{}", term, premiumSum, returnMoney, returnRate);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            // logger.info(term + " :: " + premiumSum);
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            // 해약환급금 마지막 값 저장
            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);

        return true;

    }



    private void loading() {

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#loder_wrap")));

    }

}
