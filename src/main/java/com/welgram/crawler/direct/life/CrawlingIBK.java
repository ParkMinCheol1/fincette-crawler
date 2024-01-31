package com.welgram.crawler.direct.life;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * @author aqua
 */
public abstract class CrawlingIBK extends SeleniumCrawler {

    protected void waitForLoadingEx() {
        int time = 0;
        try {
            while (true) {
                WaitUtil.loading(2);
                logger.debug("로딩 : " + driver.findElement(By.id("dataLoadingBackground")).isDisplayed());
                if (driver.findElement(By.id("dataLoadingBackground")).isDisplayed()) {
                    logger.debug("로딩 중....");
                    time += SLEEP_TIME;
                    if (time > 60000) {
                        throw new Exception("무한루프 오류 입니다.");
                    }
                } else {
                    logger.debug("로딩 끝....");
                    break;
                }

            }

            WaitUtil.waitFor();

        } catch (Exception e) {
            logger.info("####### 로딩 끝....");
        }
    }

    protected void searchProduct(CrawlingProduct info) throws Exception {
        String insuName = "";
        boolean result = false;
        logger.info(info.insuName + "찾는중...");

        element = driver.findElement(By.className("tbl_style01"));
        elements = element.findElements(By.cssSelector("tbody tr"));
        for (WebElement tr : elements) {
            element = tr.findElements(By.tagName("td")).get(0);

            // 공시실 보험명
            insuName = element.getText().trim();
            logger.info(insuName);

            if (insuName.contains(info.insuName)) {
                info.siteProductMasterCount++;
                logger.info("담보명 확인 완료 !! ");

                // 계산하기 버튼
                element = tr.findElements(By.tagName("td")).get(1);
                element = element.findElement(By.tagName("a"));
                element.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!result) {
            throw new Exception(info.insuName + " 을 찾을 수 없습니다.");
        }
    }

    // 피보험자 성명
    protected void setName() throws Exception {
//		String name = "김철권";
        String name = PersonNameGenerator.generate();

        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
        //wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("pop_wrap")));
        // element = driver.findElement(By.id("frmInsu"));

        element = driver.findElement(By.id("jname"));
        element.sendKeys(name);
        logger.info("###### 피보험자 성명 : " + name);
        WaitUtil.loading(2);
    }

    // 생년월일
    protected void setBirth(String birth, By id) throws Exception {
        element = driver.findElement(id);
        element.clear();
        element.sendKeys(birth);
        logger.info("###### 생년월일 : " + birth);
        WaitUtil.loading(2);
    }

    // 성별
    protected void setGender(int gender, By id) throws Exception {
        element = driver.findElement(id);
        elements = element.findElements(By.tagName("label"));
        element = elements.get(gender);
        element.click();
        String gen = gender == 0 ? "남자" : "여자";
        logger.info("###### 성별 : " + gen);
        waitForLoadingEx();
    }

    // 보험종류 선택
    protected void kind(By id) throws Exception {
        element = driver.findElement(id);
        // element.click();
        Thread.sleep(1000);

        element = element.findElements(By.tagName("option")).get(1);
        element.click();
        logger.info("###### 보험종류 선택 완료");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBackground")));
    }

    // 연금개시나이
    protected void setPensionAge(String annAge) throws Exception {
        boolean result = false;
        element = driver.findElement(By.id("insurance_term"));
        elements = element.findElements(By.tagName("option"));

        for (WebElement option : elements) {
            if (option.getText().replace("세", "").equals(annAge)) {
                option.click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBackground")));
                result = true;
                logger.info("###### 연금개시나이: " + annAge + "세 선택 완료");


                break;
            }
        }
        if (!result) {
            throw new Exception("해당나이에서는 연금개시나이 " + annAge + "세를 선택할 수 없습니다.");
        }
    }

    // 납입기간
    protected void setNapTerm(String napTerm) throws Exception {
        boolean result = false;
        element = driver.findElement(By.id("pay_term"));
        elements = element.findElements(By.tagName("option"));

        for (WebElement option : elements) {
            if (option.getText().equals(napTerm)) {
                option.click();
                result = true;
                logger.info("###### 납입기간: " + napTerm + " 선택 완료");
                WaitUtil.waitFor();
                break;
            }
        }
        if (!result) {
            throw new Exception("납입기간 " + napTerm + "을 선택할 수 없습니다.");
        }
    }

    // 납입주기
    protected void setNapCycle(String napCycle) throws Exception {
        element = driver.findElement(By.id("pay_period"));
        element = element.findElements(By.tagName("option")).get(1);
        element.click();
        String napCy = napCycle.equals("01") ? "월납" : "년납";

        logger.info("###### 납입주기 : " + napCy);
        WaitUtil.loading(2);
    }

    // 납입보험료
    protected void setPremium(String premium, CrawlingProduct info) throws Exception {
        element = driver.findElement(By.id("paymoney"));
        element.sendKeys(premium);
        WaitUtil.loading(2);
        logger.info("###### 납입보험료: " + premium + "원");
    }

    protected void setDiscount(int defaultPremium, int premium) {

        if (premium > defaultPremium) {
            WebElement dropdown = driver.findElement(By.id("premDcSlc"));
            dropdown.findElement(By.xpath("//option[. = '적립액가산형']")).click();
        }
    }

    // 연금수령액 조회
    protected void getPension(CrawlingProduct info) throws Exception {
        {
            WebElement element = driver.findElements(By.className("btn_mbox")).get(2);
            element.click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dataLoadingBackground")));
        }

        {
            WebElement element = driver.findElements(By.className("open_btn")).get(0);
            element.click();
            WaitUtil.loading(2);
        }
        {
            String getMoney = "";
            WebElement element = driver.findElement(By.cssSelector("#pop_container > div > div:nth-child(10) > dl > dd > div.pension_cont > table.tbl_style02.mgb5 > tbody > tr:nth-child(2) > td:nth-child(5)"));
            getMoney = element.getText().replace(",", "");
            // TODO: 연금수령역 조회 변경!!
            logger.info("###### 연금수령액: " + getMoney + "원");
            info.annuityPremium = getMoney;

        }

    }

    // 해약환급금 조회
    protected void returnPremium(CrawlingProduct info) throws Exception {

        element = driver.findElements(By.className("open_btn")).get(2);
        element.click();
        WaitUtil.loading(2);

        element = driver.findElements(By.className("pension_cont")).get(2);
        element = element.findElement(By.tagName("tbody"));
        elements = element.findElements(By.tagName("tr"));

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        int cnt = 0;
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            String term = "";
            String premiumSum = "";
            String returnMoneyMin = "";
            String returnRateMin = "";
            String returnMoneyAvg = "";
            String returnRateAvg = "";
            String returnMoney = "";
            String returnRate = "";

            if (cnt < 3) {
                term = tr.findElements(By.tagName("td")).get(0).getText();
                premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText();
                returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
                returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText();
                returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();
                returnMoney = tr.findElements(By.tagName("td")).get(6).getText();
                returnRate = tr.findElements(By.tagName("td")).get(7).getText();
            } else {
                term = tr.findElements(By.tagName("th")).get(0).getText();
                premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
                returnMoneyMin = tr.findElements(By.tagName("td")).get(1).getText();
                returnRateMin = tr.findElements(By.tagName("td")).get(2).getText();
                returnMoneyAvg = tr.findElements(By.tagName("td")).get(3).getText();
                returnRateAvg = tr.findElements(By.tagName("td")).get(4).getText();
                returnMoney = tr.findElements(By.tagName("td")).get(5).getText();
                returnRate = tr.findElements(By.tagName("td")).get(6).getText();
            }

            logger.info(term + " :: " + premiumSum);
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            cnt++;
        }


        info.returnPremium = elements.get(elements.size() - 1).findElements(By.cssSelector("td")).get(5).getText().replace(",", "").replace("원", "");
        logger.info("만기환급금 :: " + info.returnPremium);
        info.setPlanReturnMoneyList(planReturnMoneyList);
    }
}
