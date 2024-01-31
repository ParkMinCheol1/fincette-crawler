package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.common.except.CannotBeSelectedException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.DisCount;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.ProductMasterVO;
import com.welgram.util.StringUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;



// KLP 공통클래스 - scrapable 미구현
public abstract class CrawlingKLP extends SeleniumCrawler {

    // 입원비 보험
    protected void setPremium(CrawlingProduct info) throws Exception {

        element = driver.findElements(By.cssSelector(".box_middle.type_9")).get(0);
        String premium = String.valueOf(Integer.parseInt(info.assureMoney) / 10000);

        switch (premium) {
            case "3":
                element = element.findElements(By.className("rdo_m2")).get(0);
                break;

            case "6":
                element = element.findElements(By.className("rdo_m2")).get(1);
                break;

            case "9":
                element = element.findElements(By.className("rdo_m2")).get(2);
                break;

            default:
                throw new Exception("가입금액" + premium + "만원을 선택할 수 없습니다.");
        }

        element.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
    }



    public static String getDateOfBirth(int pregnancyWeek) {

        int DAYSPREGNANT = 280;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, (DAYSPREGNANT - 7 * pregnancyWeek));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

        return dateFormat.format(calendar.getTime());
    }



    public static String getDateOfFullBirth(int pregnancyWeek) {

        int DAYSPREGNANT = 280;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, (DAYSPREGNANT - 7 * pregnancyWeek));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        return dateFormat.format(calendar.getTime());
    }



    // 부모 나이 입력 | todo | 이건 또 뭐야??...
    protected void parentBirth() throws InterruptedException {

        String birth = "19900101";
        driver.findElement(By.cssSelector("#plnnrBrdt")).clear();
        driver.findElement(By.cssSelector("#plnnrBrdt")).sendKeys(birth);
        WaitUtil.waitFor(2);
    }



    // 부모 나이 입력
    protected void parentChangeBirth(String birth) throws InterruptedException {

        element = driver.findElement(By.id("plnnrBrdt"));
        element.clear();
        element.sendKeys(birth);
        WaitUtil.waitFor(2);
    }



    // 부모 성별 여자 고정
    protected void parentGender() throws InterruptedException {

        element = driver.findElements(By.className("form_calc")).get(1);
        element = element.findElements(By.tagName("label")).get(1);
        element.click();
        WaitUtil.waitFor(2);
    }



    // 어린이/태아 중 어린이 고정
    protected void childSet(String code) throws InterruptedException {

        // 어린이
        if (code.indexOf("CHL") > -1) {
            element = driver.findElements(By.className("form_calc")).get(2);
            element = element.findElements(By.tagName("label")).get(0);
        }

        // 태아
        if (code.indexOf("BAB") > -1) {
            element = driver.findElements(By.className("form_calc")).get(2);
            element = element.findElements(By.tagName("label")).get(1);
        }

        element.click();
        WaitUtil.waitFor(2);
    }



    // 자녀성별
    protected void childGender(int gender) throws InterruptedException {

        element = driver.findElements(By.className("form_calc")).get(4);
        elements = element.findElements(By.tagName("span"));
        element = elements.get(gender);
        element.click();
        WaitUtil.waitFor(2);
    }



    // 자녀 보험기간
    protected void childInsTerm(String insTerm) throws Exception {

        // ((JavascriptExecutor) driver).executeScript("scroll(0,900);");
        boolean result = false;

        helper.oppositionWaitForLoading("inspdContents");
        element = driver.findElement(By.id("inspdContents"));
        elements = element.findElements(By.tagName("label"));

        for (WebElement label : elements) {
            if (label.getText().equals(insTerm)) {
                label.click();
                result = true;
                helper.waitForCSSElement("#loadingArea");
                break;
            }
        }

        if (!result) {
            throw new Exception("보험기간을 선택할 수 없습니다.");
        }
    }



    // 자녀 납입기간
    protected void childNapTerm(String napCycle, String napTerm) throws Exception {

        boolean result = false;
        elements = element.findElements(By.cssSelector("#insuTermContents > div > span > label"));

        // 납입주기
        List<WebElement> napCycleElements = driver.findElements(By.cssSelector("#insuTermHeader > li > label"));

        for (WebElement napCycleEl : napCycleElements) {

            String prdtNapCycleStr = getNapCycle2Str(napCycle);
            String napCycleText = napCycleEl.getText();

            if (napCycleText.equals(prdtNapCycleStr)) {

                napCycleEl.click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("imgLoading"))); // imgLoading

                // 납입기간
                List<WebElement> napTermElements =
                    driver.findElements(By.cssSelector("#insuTermContents > div > span > label"));

                for (WebElement napTermEl : napTermElements) {
                    String napTermText = napTermEl.getText();

                    if (napTermText.equals(napTerm)) {

                        logger.debug("napTermText: " + napTermText);
                        result = true;

                        napTermEl = wait.until(ExpectedConditions.elementToBeClickable(napTermEl));
                        napTermEl.click();
                        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("imgLoading"))); // imgLoading
                    }

                }
            }
        }

        if (!result) {
            throw new Exception("납입기간을 선택할 수 없습니다.");
        }
    }



    protected void setBirth(By id, String birth) throws Exception {

        element = driver.findElement(id);
        element.clear();
        element.sendKeys(birth);
    }



    protected void setGender(int value) throws InterruptedException {

        elements = driver.findElements(By.className("form_calc"));
        elements = elements.get(1).findElements(By.tagName("span"));
        element = elements.get(value);
        element.click();
        WaitUtil.waitFor(2);
    }



    protected void setChildGender(int value) throws InterruptedException {

        elements = driver.findElements(By.className("form_calc"));
        elements = elements.get(3).findElements(By.tagName("span"));
        element = elements.get(value);
        element.click();
        WaitUtil.waitFor(2);
    }



    protected void setSmoke(DisCount discount) throws InterruptedException {

        if (discount.equals(DisCount.일반)) {
            driver.findElement(By.cssSelector("label[for='smokY']")).click();
        }

        if (discount.equals(DisCount.비흡연)) {
            driver.findElement(By.cssSelector("label[for='smokN']")).click();
        }
        WaitUtil.waitFor(2);
    }



    protected void setConfirmPremium(By by) throws Exception {

        WaitUtil.loading(3);

        if (driver.findElement(By.cssSelector(".btn_talk_close")).isDisplayed()) {
            Thread.sleep(1000);
            element = driver.findElement(By.className("btn_talk_close"));
            element.click();
            logger.debug("상담톡 닫기!");
            Thread.sleep(1000);
        }

        logger.debug("step1");
        WaitUtil.loading(1);

        element = driver.findElement(by);
        element.click();
        logger.debug("step2");
        WaitUtil.loading(2);

        logger.debug("step3");
        helper.waitForCSSElement("#loadingArea");
    }



    protected void setConfirmPremium2(By by) throws Exception {

        element = driver.findElement(by);
        element.click();
        WaitUtil.waitFor(2);

        if (isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            WaitUtil.waitFor(2);
        }

        // 로딩 대기
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
    }



    protected void setAmountInsured(String value) throws Exception {

        value = String.valueOf(Integer.parseInt(value) / 10000);
        int value1 = (Integer.parseInt(value) / 1000) / 10; // 억원
        int value2 = (Integer.parseInt(value) / 1000) % 10; // 천원

        WebElement menu = driver.findElement(By.className("list_sel"));

        Actions build = new Actions(driver); // heare you state ActionBuider
        build.moveToElement(menu).build().perform(); // Here you perform hover mouse over the needed elemnt to triger
        // the visibility of the hidden
        WaitUtil.loading(2);
        logger.debug("step B");

        if (value1 != 0) {
            // 억원
            // 억원 세팅
            element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(0);
            element.click();
            elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));
            for (WebElement li : elements) {
                // 보험가입 금액 선택하면 해당
                if ((value1 + "억").equals(li.findElement(By.tagName("span")).getText().trim())) {
                    li.click();
                    break;
                }
            }

            // 로딩 대기
            helper.waitForCSSElement("#loadingArea");

            // 천만원 세팅
            // 계산했을 때 보험가입금액 부분
            if (value2 != 0) {
                element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(1);
                element.click();
                elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));
                for (WebElement li : elements) {
                    if ((value2 + "천만원").equals(li.findElement(By.tagName("span")).getText().trim())) {
                        li.click();
                        break;
                    }
                }

            } else {
                element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(1);
                element.click();
                element = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li")).get(0);
                element.click();
            }
        } else {

            // 천만원
            // 천만원대 먼저 세팅
            element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(1);
            element.click();
            WaitUtil.loading(2);
            elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));

            for (WebElement li : elements) {
                if ((value2 + "천만원").equals(li.findElement(By.tagName("span")).getText().trim())) {
                    li.click();
                    break;
                }
            }

            // 알럿이 있는지 확인해서 있으면 Exception 처리를 해야한다.
            if (isAlertShowed()) {
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                logger.info("alertText :: " + alertText);
                alert.accept();
                throw new Exception(alertText);
            }

            // 로딩 대기
            helper.waitForCSSElement("#loadingArea");
            WaitUtil.loading(2);

            // 억원대 초기화
            element = driver.findElement(By.className("list_sel")).findElements(By.className("li2")).get(0);
            element.click();
            WaitUtil.loading(2);
            element = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li")).get(0);
            element.click();
        }

        // 로딩 대기
        helper.waitForCSSElement("#loadingArea");
    }



    protected void setInsuType(String value) throws Exception {

        elements = driver.findElements(By.cssSelector("#prodGubunArea > span"));

        if (value.contains("체감형")) {
            element = elements.get(1).findElement(By.tagName("label"));
        } else {
            element = elements.get(0).findElement(By.tagName("label"));
        }
        element.click();
        // 로딩 대기
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
    }



    protected void setInsTerm(By by, String values) throws Exception {

        String value = values.replace("년", "").replace("세", "");
        boolean result = false;

        element = driver.findElement(by);
        elements = element.findElements(By.className("rdo_m"));

        for (WebElement el : elements) {
            element = el.findElement(By.tagName("input"));

            if (element.getAttribute("value").equals("01|" + value)
                || element.getAttribute("value").equals("02|" + value)) {

                el.findElement(By.tagName("label")).click();
                result = true;
                helper.waitForCSSElement("#loadingArea");
                logger.info("######## 보험기간 " + values + " 선택완료");
                WaitUtil.loading(2);
                break;
            }
        }

        if (!result) {
            throw new CannotBeSelectedException("보험기간을 선택할 수 없습니다.");
        }
    }



    protected void setInsTerm2(By by, String value) throws SetInsTermException {

        try {
            helper.sendKeys1_check(by, value);
            element = driver.findElement(By.className("box_top"));
            element.click();
            WaitUtil.waitFor(2);
        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }



    protected void setNapTerm(By by, String value) throws Exception {

        Boolean result = false;
        value = value.replace("년", "").replace("세", "");
        elements = driver.findElement(by).findElements(By.className("rdo_m"));

        for (WebElement el : elements) {
            element = el.findElement(By.tagName("input"));

            if (element.getAttribute("value").equals("01|" + value)
                || element.getAttribute("value").equals("02|" + value)) {

                el.findElement(By.tagName("label")).click();
                result = true;
                helper.waitForCSSElement("#loadingArea");
                logger.debug("######## 납입기간 " + value + " 선택완료");
                WaitUtil.loading(2);
                break;
            }
        }

        WaitUtil.waitFor(2);
        if (!result) {
            throw new CannotBeSelectedException("해당 나이에서는 납입기간을 선택할수 없음 : " + value);
        }
    }



    protected void updateSetNapTerm(By by, String value) throws Exception {

        Boolean result = false;
        value = value.replace("년", "").replace("세", "");
        elements = driver.findElement(by).findElements(By.className("rdo_m"));

        for (WebElement el : elements) {

            element = el.findElement(By.tagName("input"));

            if (element.getAttribute("value").equals(value)
                || element.getAttribute("value").equals(value)) {

                el.findElement(By.tagName("label")).click();
                result = true;
                helper.waitForCSSElement("#loadingArea");
                logger.debug("######## 납입기간 " + value + " 선택완료");
                WaitUtil.loading(2);
                break;
            }
        }

        WaitUtil.waitFor(2);
        if (!result) {
            throw new CannotBeSelectedException("해당 나이에서는 납입기간을 선택할수 없음 : " + value);
        }
    }



    protected void setProductKind(CrawlingProduct info) throws Exception {

        if (info.productKind.equals("순수보장형")) {
            driver.findElement(By.cssSelector("#eprtRfdrtHeader > li.rdo_m3._enabled._unChecked > label")).click();
        } else {
            driver.findElement(By.cssSelector("#eprtRfdrtHeader > li.rdo_m3._enabled._checked > label")).click();
        }
        WaitUtil.waitFor(1);
    }



    protected void setNapTerm(String values, CrawlingProduct info) throws Exception {

        Boolean result = false;
        Boolean result2 = false;
        String value = values.replace("년", "").replace("세", "");

        // 월납클릭
        elements = driver.findElements(By.cssSelector("#insuTermHeader > ul > li"));
        int napCycleSize = elements.size();
        //납입주기 : 01 월납 / 02 연납 / 00 일시납
        String napCycleValue = "";

        logger.info("현재 납입주기 : "+info.napCycle);

        if (info.napCycle.equals("01")) {
            napCycleValue = "월납";

        } else if (info.napCycle.equals("02")) {
            napCycleValue = "연납";

        } else if (info.napCycle.equals("00")) {
            napCycleValue = "일시납";
            result = true;
        }

        logger.info("클릭해야 하는 납입주기 : "+napCycleValue);

        for (int i=0; i<napCycleSize; i++) {
            if (elements.get(i).findElement(By.cssSelector("label")).getText().trim().equals(napCycleValue)) {
                if (elements.get(i).getAttribute("class").contains("_disabled")) {
                    logger.info("납입주기 : 일시납 버튼을 선택할 수 없음");
                    break;
                }
                elements.get(i).click();
                logger.info(elements.get(i).findElement(By.cssSelector("label")).getText().trim()+"클릭됨");
                result2 = true;
                break;
            }
        }

        if (!result2) {
            throw new Exception("해당 나이에서는 납입주기를 선택할수 없음 : " + napCycleValue);
        }

        helper.waitForCSSElement("#loadingArea");
        WaitUtil.waitFor(2);

        if (!info.napCycle.equals("00")) {
            element =
                driver
                    .findElement(By.id("insuTermContents"))
                    .findElement(By.tagName("div"))
                    .findElement(By.className("box_rdo"));
            elements = element.findElements(By.className("rdo_m"));

            for (WebElement el : elements) {
                element = el.findElement(By.tagName("input"));

                if (element.getAttribute("value").equals("01|" + value)
                    || element.getAttribute("value").equals("02|" + value)) {

                    el.findElement(By.tagName("label")).click();
                    helper.waitForCSSElement("#loadingArea");
                    //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
//				waitFor();
                    logger.info("######## 납입기간 " + values + " 선택완료");
                    result = true;
                    break;
                }
            }
        }

        if (!result2) {
            throw new Exception("해당 나이에서는 납입주기를 선택할수 없음 : " + napCycleValue);

        } else if (!result) {
            throw new Exception("해당 나이에서는 납입기간을 선택할수 없음 : " + value);
        }
    }



    protected void setReturnType(CrawlingProduct info) throws Exception {

        Boolean result = false;

        elements = driver.findElements(By.cssSelector("#eprtRfdrtHeader > li"));
        int returnTypeSize = elements.size();
        String returnPercent = "";

        logger.info("환급타입 : "+info.productKind);

        if (info.productKind.equals("만기환급형")) {
            returnPercent = "100%";
        } else if (info.productKind.equals("순수보장형")) {
            returnPercent = "0%";
        }

        for (int i=0; i<returnTypeSize; i++) {
            if (elements.get(i).findElement(By.cssSelector("label > span.box_percentage")).getText().trim().equals(returnPercent)) {
                elements.get(i).click();
                result = true;
                break;
            }
        }

        WaitUtil.waitFor(2);
        if (!result){
            throw new Exception("환급금타입을 선택할 수 없습니다. : "+info.productKind);
        }
    }



    protected void setNapTerm2(By by, String value) throws Exception {

        Boolean result = false;
        elements = driver.findElement(by).findElements(By.tagName("span"));
        for (WebElement el : elements) {

            value = value.replace("년", "").replace("세", "");
            if (value.equals(el.findElement(By.tagName("input")).getAttribute("value"))) {
                el.findElement(By.tagName("label")).click();
                result = true;
                break;
            }
        }

        helper.waitForCSSElement("#loadingArea");
        if (!result) {
            throw new CannotBeSelectedException("해당 나이에서는 납입기간을 선택할수 없음 : " + value);
        }
    }



    protected void setNapTerm3(String value, CrawlingProduct info) throws Exception {

        Boolean result = false;
        int birth = Integer.parseInt((info.fullBirth).substring(0, 4));
        logger.info("출생년도 : "+birth);
        Date date = new Date();
        SimpleDateFormat day = new SimpleDateFormat("yyyy");
        int curDate = Integer.parseInt(day.format(date));
        logger.info("현재 년 : "+curDate);
        //보험나이로 계산하기때문에 실 출생년도보다 1년을 빼줌
        int curAge = curDate - birth -1;

        logger.info("출생년도 - 현재 년 - 1: "+curAge);

        int napTerm = Integer.parseInt(value);

        logger.info("납입기간 : "+napTerm);

        int napTerm2 = Integer.parseInt(info.annuityAge) - curAge;

        logger.info("계산하고 뺀 년도에서 납입기간 뺀 년도 : "+napTerm2);

        element = driver.findElements(By.className("box_sel")).get(1);
        element.click();
        elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));

        for (WebElement li : elements) {
            if ((value + "년납").equals(li.findElement(By.tagName("span")).getText().trim())) {
                li.click();
                result = true;
                break;
                // 연금개시나이 : 현재나이 == 납입기간 이면 전기납 선택
            } else if (napTerm == napTerm2 && ("전기납").equals(li.findElement(By.tagName("span")).getText().trim())) {
                li.click();
                result = true;
                break;
            }
            /*
            else if (napTerm == napTerm2 || ("전기납").equals(li.findElement(By.tagName("span")).getText().trim())) {
                li.findElement(By.tagName("span")).click();
                result = true;
                break;
            }
            */
        }

        WaitUtil.waitFor(2);
        if (!result) {
            throw new CannotBeSelectedException("해당 나이에서는 납입기간을 선택할수 없음 : " + value);
        }
    }


    
    protected void setNapTerm4(By by, String value) throws Exception {

        Boolean result = false;
        value = value.replace("년", "").replace("세", "");
        elements = driver.findElement(by).findElements(By.className("rdo_m"));
        for (WebElement el : elements) {
            // el : div
            String val = el.findElement(By.tagName("input")).getAttribute("value");

            if (value.equals(val)) {
                el.findElement(By.tagName("label")).click();
                result = true;
                break;
            }
        }

        WaitUtil.waitFor(2);
        if (!result) {
            throw new CannotBeSelectedException("해당 나이에서는 납입기간을 선택할수 없음 : " + value);
        }
    }



    protected void setReturnRatio() throws InterruptedException {

        element = driver.findElement(By.id("eprtRfdrtHeader"));
        element = element.findElements(By.className("rdo_m3")).get(0); // 0 : 순수보장형, 1 : 일부환급(혼합형), 2 : 만기보장형
        element.click();
        WaitUtil.waitFor(2);
    }



    protected void setDiagnosticBenefit(String value) throws Exception {

        boolean result = false;
        String money = null;
        String premium = value;
        for (int i = 0; i < 3; i++) {
            WaitUtil.waitFor(2);
            if (!(driver.findElements(By.className("tab_rdo")).get(0).isDisplayed())) {
                logger.info("로딩 중....");

            } else {
                logger.info("로딩 끝....");
                break;
            }
        }
        element = driver.findElements(By.className("tab_rdo")).get(0);
        elements = element.findElements(By.tagName("li"));

        String assureMoney = driver.findElement(By.cssSelector("#lblPltcEntAmt")).getText();
        logger.info("stlye 확인 : "+driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_8 > ul")).getAttribute("style"));

        if (!driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_8 > ul")).getAttribute("style").contains("none")) {

            for (int i = 0; i < elements.size(); i++) {
                element = elements.get(i);
                element = element.findElement(By.tagName("input"));
                if (element.getAttribute("value").equals(premium)) {
                    elements.get(i).click();
                    WaitUtil.loading(1);
                    helper.waitForCSSElement("#loadingArea");
                    result = true;
                }
            }

        } else {
            
            if (value.equals("10000000")) {
                money = "1천만원";
            } else if (value.equals("20000000")) {
                money = "2천만원";
            }
            result = false;
        }

        if (!result) {
            if (assureMoney.contains(money)) {
                logger.info("가입금액 1천만원의 경우, 크롤링 진행");

            } else {
                throw new CannotBeSelectedException("해당 나이에서는 가입금액을 선택할수 없음 : " + value);
            }
        }
    }



    protected void confirmResult() throws Exception {

        ((JavascriptExecutor) driver).executeScript("scroll(0,1150);");
        element = driver.findElement(By.id("btnExpectInsuPay"));
        element.click();
    }



    protected void setCancerReturnPremium(String value) throws Exception {

        int premium = 0;
        // elements = driver.findElements(By.className("tab_rdo"));
        element = driver.findElement(By.cssSelector("#iamtArea"));
        elements = element.findElements(By.tagName("span"));

        boolean isSuccess = false;
        for (WebElement el : elements) {
            premium = Integer.parseInt(el.findElement(By.tagName("input")).getAttribute("value"));
            //premium = premium / 10000;

            if (value.equals(Integer.toString(premium))) {
                el.findElement(By.tagName("label")).click();
                isSuccess = true;
                break;
            }
        }

        helper.waitForCSSElement("#loadingArea");

        if (!isSuccess) {
            throw new Exception("가입금액을 선택할수 없습니다 !!");
        }
        //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
    }



    protected void getPremium(String by, CrawlingProduct info) throws Exception {

        // 로딩 대기
        helper.waitForCSSElement("#loadingArea");
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading

        String premium = "";
        helper.elementWaitFor(by);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(by)));
        element = driver.findElement(By.cssSelector(by));

        WaitUtil.waitFor(1);
        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        premium = element.getText().replaceAll("[^0-9]", "");
        logger.debug("###### 월보험료: " + premium);
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }



    protected void setMonthlyPremium(By by, String value) throws PremiumCrawlerException {

        try {
            helper.sendKeys1_check(by, value);
            element = driver.findElement(By.id("insuMonthPayChunwon"));
            element.clear();
            WaitUtil.waitFor("setMonthlyPremium");
        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }



    // 가입금액
    protected void setPremium(String premium) throws Exception {

        boolean isPremium = false;
        elements = driver.findElements(By.name("pltcEntAmt"));

        for (WebElement element : elements) {
            if (premium.equals(element.getAttribute("value"))) {
                element.findElement(By.xpath("ancestor::span/label")).click();
                isPremium = true;
                break;
            }
        }

        if (!isPremium) {
            throw new Exception("가입금액을 선택할수 없습니다!!!");
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
    }



    protected void setPremium(String by, CrawlingProduct info) throws Exception {

        /* 가입금액 120만원 이상일 때 조건 */
        int monthPremium = 120;
        int premium = 0;

        premium = Integer.parseInt(info.assureMoney) / 100;

        helper.elementWaitFor(by);
        element = driver.findElement(By.cssSelector(by));
        element.clear();
        element.sendKeys(String.valueOf(premium));

        /* 금액 설정 로딩을 위해 클릭 동작 */
        element = driver.findElement(By.id("inspdContents"));
        element = element.findElements(By.tagName("span")).get(0);
        element = element.findElement(By.tagName("label"));
        element.click();

        if (premium >= monthPremium) {
            if (isAlertShowed()) {
                Alert alert = driver.switchTo().alert();
                alert.accept();
                logger.debug("alert check");
            }
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
        WaitUtil.waitFor("setPremium");
    }



    protected void setDeathPremium(By by, String value) throws Exception {

        element = driver.findElements(By.className("tab_rdo")).get(0);
        elements = element.findElements(By.tagName("li"));

        for (WebElement el : elements) {
            // el : li
            if (el.findElement(By.tagName("input")).getAttribute("value").equals(value)) {
                el.findElement(By.tagName("label")).click();
                break;
            }
        }

        // 로딩대기
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
    }



    protected void setDeathPremium2(By by, String value) throws Exception {

        helper.sendKeys1_check(by, value);
        element.findElement(by).sendKeys(Keys.TAB);

        if (isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        }

        // 로딩대기
        helper.waitForCSSElement("#loadingArea");
    }



    protected void setAnnuityAge(String value) throws Exception {

        Boolean result = false;
        // 연금개시나이
        element = driver.findElements(By.className("box_sel")).get(0);
        logger.debug("element1 >> " + element);
        element.click();

        logger.debug("click 1");
        elements = driver.findElement(By.className("_sel_option")).findElements(By.tagName("li"));

        logger.debug("element2 >> " + element);
        for (WebElement li : elements) {
            WaitUtil.waitFor(1);
            logger.debug(li.findElement(By.tagName("span")).getText().trim());
            if ((value + "세").equals(li.findElement(By.tagName("span")).getText().trim())) {
                li.click();
                result = true;
                break;
            }
        }

        WaitUtil.waitFor("setAnnuityAge");
        if (!result) {
            throw new CannotBeSelectedException("해당 나이에서는 연금개시나이를 선택할수 없음 : " + value);
        }
    }



    protected void getAnnuityPremiumAndFixedAnnuityPremium(String id, CrawlingProduct info) throws Exception {

        helper.waitForCSSElement("#loadingArea");

        element = driver.findElements(By.className("bar")).get(4).findElement(By.tagName("strong"));
        WaitUtil.waitFor(2);
        info.annuityPremium = element.getText().replace(",", "");
        logger.debug("연금수령액: " + info.annuityPremium);

        WaitUtil.loading(2);

        helper.waitForCSSElement("#loadingArea");

        ((JavascriptExecutor) driver).executeScript("scroll(0,2000);");

        WaitUtil.loading(2);
        element = driver.findElement(By.id(id));
        element.click();
        WaitUtil.loading(2);

        Set<String> windowId = driver.getWindowHandles();
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;
        while (handles.hasNext()) {
            subHandle = handles.next();

            logger.debug(subHandle);
            WaitUtil.loading(2);
        }
        driver.switchTo().window(subHandle);
        WaitUtil.loading(1);

        String fixedAnnuityPremium;

        if (driver.findElement(By.cssSelector("#table2 > tbody > tr:nth-child(5) > th.bl1")).getAttribute("textContent").equals("10년(10회)")) {
            fixedAnnuityPremium = driver.findElement(By.cssSelector("#crtnAnnYy10Anam1")).getAttribute("textContent").replaceAll("[^0-9]", "");
            info.fixedAnnuityPremium = fixedAnnuityPremium + "000";
            logger.info("예상연금수령액 : "+info.fixedAnnuityPremium);
        }

        WaitUtil.loading(1);
    }



    protected void getAnnuityPremium(CrawlingProduct info) throws Exception {

        helper.waitForCSSElement("#loadingArea");
        element = driver.findElements(By.className("bar")).get(4).findElement(By.tagName("strong"));
        WaitUtil.waitFor(2);
        info.annuityPremium = element.getText().replace(",", "");
        logger.debug("연금수령액: " + info.annuityPremium);
    }



    protected void getReturnPremium(String by, CrawlingProduct info) throws Exception {

        helper.waitForCSSElement("#loadingArea");
        //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
//		elementWaitFor(by);

//		element = driver.findElement(By.cssSelector(by));

        ((JavascriptExecutor) driver).executeScript("scroll(0,2000);");
        WaitUtil.waitFor("getReturnPremium");
        // 포커싱
//		new Actions(driver).moveToElement(driver.findElement(By.cssSelector(by))).perform();
//
//		element = driver.findElement(By.cssSelector(by));
//
//		// 포커스 이동을 안하면 클릭시 에러발생
//		JavascriptExecutor jse = (JavascriptExecutor) driver;
//		jse.executeScript("arguments[0].scrollIntoView()", element);
        // new Actions(driver).moveToElement(element).perform();
        // Actions build = new Actions(driver); // heare you state ActionBuider
        // build.moveToElement(element).build().perform(); // Here you perform hover
        // mouse over the needed elemnt to triger the visibility of the hidden

        element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(by)));
        element.sendKeys(Keys.ENTER);
        element.click();
        WaitUtil.waitFor(2);
        Set<String> windowId = driver.getWindowHandles();
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        String mainHandle = driver.getWindowHandle();
        String subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            WaitUtil.waitFor(2);
        }

        driver.switchTo().window(subHandle);
        helper.oppositionWaitForLoading("listArea");
        elements = driver.findElement(By.id("listArea")).findElements(By.tagName("tr"));
        String nap = "";

        int num = 0;
        switch (info.productCode) {
            case "KLP_ASV_D001":
                num = 6;
                break;

            case "KLP_ANT_D001":
                num = 6;
                break;

            case "KLP00014":
                num = 6;
                break;

            case "KLP_SAV_D001":
                num = 6;
                break;

            case "KLP_DSS_D003":
                num = 2;
                break;

            case "KLP00013":
                num = 2;
                break;

            case "KLP_DSS_D002":
                num = 2;
                break;

            case "KLP_DSS_D001":
                num = 2;
                break;

            case "KLP_DTL_D001":
                num = 2;
                break;

            case "KLP00176":
                num = 2;
                break;
        }

        for (WebElement el : elements) {
            if (info.napTerm.equals("전기납")) {
                nap = info.napTerm.replace("년", "").replace("세", "");
                nap = String.valueOf(Integer.parseInt(info.annuityAge.replace("세", "")) - Integer.parseInt(info.age));
            } else {
                nap = info.napTerm.replaceAll("[^0-9]", "");
            }

            if (el.findElements(By.tagName("td")).get(0).getText().equals(nap + "년")) {

// todo 확인 및 수정 필요
//                info.returnPremium = el.findElements(By.tagName("td")).get(num).getText().replace(",", "");

                break;
            }
        }

        if (info.productCode.equals("KLP_ANT_D001") || info.productCode.equals("KLP00011")
                || info.productCode.equals("KLP00014") || info.productCode.equals("KLP00082")) {
            //info.savePremium = info.assureMoney;
        }

        info.errorMsg = "";
        logger.debug("해약환급금: " + info.returnPremium);

        driver.close();
        driver.switchTo().window(mainHandle);
    }



    protected void setNapTerm2(String value) throws Exception {

        Boolean result = false;
        elements = driver.findElements(By.className("box_rdo")).get(1).findElements(By.className("rdo_m"));

        for (WebElement el : elements) {

            if (value.equals(el.findElement(By.tagName("input")).getAttribute("value"))) {
                el.findElement(By.tagName("label")).click();
                result = true;
                break;
            }
        }

        WaitUtil.waitFor(2);
        if (!result) {
            throw new CannotBeSelectedException("해당 나이에서는 납입기간을 선택할수 없음 : " + value);
        }
    }



    protected void setNapTerm3(String napTerm, String insTerm) throws Exception {

        String value = napTerm.replace("년", "").replace("세", "");
        boolean result = false;

        if (napTerm.equals("전기납")) {
            value = insTerm.replaceAll("[^0-9]", "");
        }

        elements = driver.findElement(By.id("insuTermContents")).findElements(By.className("rdo_m"));

        for (WebElement el : elements) {
            element = el.findElement(By.tagName("input"));

            if (element.getAttribute("value").equals("01|" + value)
                    || element.getAttribute("value").equals("02|" + value)) {

                el.findElement(By.tagName("label")).click();
                result = true;
                helper.waitForCSSElement("#loadingArea");
                logger.debug("######## 납입기간 " + napTerm + " 선택완료");
                WaitUtil.waitFor(2);
                break;
            }
        }

        if (!result) {
            throw new CannotBeSelectedException("납입기간을 선택할 수 없습니다.");
        }
    }



    protected void doGetPremium2(By by, CrawlingProduct info) {

        String premium = "";
        element = driver.findElements(by).get(0);
        elements = element.findElements(By.tagName("em"));
        for (WebElement el : elements) {
            premium = premium + el.getText();
        }
        info.totPremium = premium;
        info.returnPremium = "0";
        info.annuityPremium = "0";
    }



    protected void iFrameCheck() throws Exception {

        try {
            WaitUtil.loading(3);
            String currentWindow = driver.getWindowHandle();

            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("modalIfm")));
            driver.switchTo().defaultContent();
            driver.switchTo().frame(driver.findElement(By.id("modalIfm")));
            element = driver.findElement(By.id("btn_close"));
            element.click();

            driver.switchTo().window(currentWindow);
            logger.debug("iFrameCheck 실행 !!");

        } catch(Exception e) {
            logger.debug("iFrameCheck No !!!");
        }
    }



    // 환급형
    protected void maturityReturnPerCent(String insuName) throws Exception {

        element = driver.findElement(By.id("eprtRfdrtHeader"));
        elements = element.findElements(By.tagName("li"));
        String KindNum = "";

        if (insuName.contains("50%")) {
            KindNum = "50";

        } else if (insuName.contains("100%")) {
            KindNum = "100";

        } else {
            KindNum = "0";
        }

        for (WebElement li : elements) {
            if (li.findElement(By.tagName("input")).getAttribute("value").equals(KindNum)) {
                li.click();
//				waitForCSSElement("#loadingArea");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
                break;
            }
        }
    }



    protected void childPremium(String premium) throws Exception {

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
                driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1)")));
        Thread.sleep(500);

        String getPremium = "";

//		element = driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_3 > div.box_sel > span"));
//		element = wait.until(ExpectedConditions.elementToBeClickable(element));

        new Actions(driver)
            .moveToElement(driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1)")))
            .perform();

        element = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_3 > div.box_sel > span")));
        WaitUtil.waitFor("childPremium");
        element.click();
        element = driver.findElement(By.cssSelector("._sel_option.sel_m"));
        elements = element.findElements(By.tagName("li"));

        for (WebElement li : elements) {

            element = li.findElement(By.tagName("span"));

            getPremium = element.getText().replace(",", "").replace("만원", "");

            if (getPremium.contains("진단보험금")) {
                continue;
            }

            logger.info("비교가 필요한 값 : "+premium);
            logger.info("비교 대상인 값   : "+getPremium);

            if (getPremium.equals(premium)) {
                li.findElement(By.tagName("span")).click();
                WaitUtil.waitFor(2);
                break;
            }
        }
    }



    /**
     * 해약환급금(예시표) 조회
     *
     * @param info
     * @throws InterruptedException
     */
    protected void getReturns(String id, CrawlingProduct info) throws Exception {

        WaitUtil.loading(2);
        helper.waitForCSSElement("#loadingArea");

        int num = 2;
        switch(info.productCode) {
            case "KLP_ASV_D001":
                num = 6;
                break;

            case "KLP_ANT_D001":
                num = 6;
                break;

            case "KLP_SAV_D001":
                num = 6;
                break;

            case "KLP_DSS_D003":
                num = 2;
                break;

            case "KLP_DSS_D002":
                num = 2;
                break;

            case "KLP_DSS_D001":
                num = 2;
                break;

            case "KLP_DTL_D001":
                num = 2;
                break;

            case "KLP_BAB_D001":
                num = 2;
                break;

            case "KLP_SAV_D002":
                num = 6;
                break;

            case "KLP_DSS_D010":
                num = 2;
                break;
        }

        WaitUtil.loading(2);
        element = driver.findElement(By.id(id));
        element.sendKeys(Keys.ENTER);
        element.click();
        WaitUtil.loading(2);

        Set<String> windowId = driver.getWindowHandles();
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;
        while (handles.hasNext()) {
            subHandle = handles.next();

            logger.debug(subHandle);
            WaitUtil.loading(2);
        }
        driver.switchTo().window(subHandle);
        elements = driver.findElements(By.cssSelector("#listArea > tr"));

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        if (elements.size() > 0) {

            for (int i = 0; i < elements.size(); i++) {

                WebElement tr = elements.get(i);
                List<WebElement> tdList = tr.findElements(By.tagName("td"));

                logger.info("______해약환급급[{}]_______ ", i);

                String term = tdList.get(0).getAttribute("innerText"); // 경과기간
                String premiumSum = tdList.get(1).getAttribute("innerText"); // 납입보험료
                String returnMoney = tdList.get(num).getAttribute("innerText"); // 해약환급금
                String returnRate = tdList.get(num + 1).getAttribute("innerText"); // 환급률

                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender(info.getGenderEnum().name());
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term); // 경과기간
                planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
                planReturnMoney.setReturnMoney(returnMoney); // 환급금
                planReturnMoney.setReturnRate(returnRate); // 환급률

                planReturnMoneyList.add(planReturnMoney);

// todo | 확인 및 수정 필요
//                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } else {
            logger.info("해약환급금 내역이 없습니다.");
        }
    }

    public  void switchToNewWindow(WebDriver driver) {

        String mainWindow = driver.getWindowHandle();
        Set<String> openedWindows = driver.getWindowHandles();

        if (openedWindows.size() > 1) {
            for (String newWindow : openedWindows) {
                driver.switchTo().window(newWindow);
            }
            driver.close();
            driver.switchTo().window(mainWindow);
        }
    }



    protected void getSubTreaty(CrawlingProduct info) {

        logger.debug("상품마스터 특약보험 입력시작");

        element = driver.findElement(By.cssSelector(
            "#content > div.box_product_plan > div:nth-child(2) > div.section_plan_result > div > div.clearfix > div.area_l > div.box_graph > div.insurance_info.type2 > div.area_txt"
        ));

        String productName = element.getText();
        logger.debug("[{}]:특약상품명 조회", productName);

        ProductMasterVO productMasterVO = new ProductMasterVO();
        productMasterVO.setProductId(info.productCode);
        productMasterVO.setProductKinds(info.defaultProductKind); // 정확히 알면 표기
        productMasterVO.setProductTypes(info.defaultProductType); // 정확히 알면 표기
        productMasterVO.setProductGubuns("선택특약");
        productMasterVO.setSaleChannel(info.getSaleChannel());
        productMasterVO.setProductName(productName);
//				productMasterVO.setInsTerms(insTerms);
//				productMasterVO.setNapTerms(napTerms);
//				productMasterVO.setAssureMoneys(assureMoneys);
//				productMasterVO.setMinAssureMoney(minAssureMoney);
//				productMasterVO.setMaxAssureMoney(maxAssureMoney);
        productMasterVO.setCompanyId(info.getCompanyId());

        logger.info("상품마스터 :: " + productMasterVO.toString());
        info.getProductMasterVOList().add(productMasterVO);
    }

    protected void getMainTreaty(CrawlingProduct info) throws InterruptedException {

        List<String> assureMoneys = new ArrayList<>(); // 진단보험금

        // 진단보험금
        List<WebElement> insuMoneyElements = driver.findElements(By.cssSelector("#iamtArea > span > label"));

        for (WebElement insuMoneyEl : insuMoneyElements) {
            String assureMoney = insuMoneyEl.getText();
            assureMoneys.add(assureMoney);
            logger.info("assureMoney: " + assureMoney);
        }

        String minAssureMoney = assureMoneys.get(0);
        String maxAssureMoney = assureMoneys.get(assureMoneys.size() - 1);

        // 보험기간
        List<WebElement> insuTermElements = driver.findElements(By.cssSelector("#inspdContents > span > label"));

        insuTermElements = wait.until(ExpectedConditions.visibilityOfAllElements(insuTermElements));
        List<String> insTerms = new ArrayList<>(); // 보험기간
        List<String> napTerms = null; // 납입기간
        List<String> napCycles = null; // 납입주기
        Set<String> napCyclesSet = new HashSet<>(); // 납입주기Set
        Set<String> napTermsSet = new HashSet<>(); // 납입기간

        for (WebElement insuTermEl : insuTermElements) {

            String insTerm = insuTermEl.getText();
            logger.debug("insTerm: " + insTerm);

            insTerms.add(insTerm);
            insuTermEl.click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("imgLoading"))); // imgLoading

            // 납입주기
            List<WebElement> napCycleElements = driver
                    .findElements(By.cssSelector("#insuTermHeader > ul > li > label"));

            for (WebElement napCycleEl : napCycleElements) {

                String napCycle = napCycleEl.getText();
                napCyclesSet.add(napCycle);
                logger.debug("napCycle: " + napCycle);

                napCycleEl.click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("imgLoading"))); // imgLoading

                // 납입기간
                List<WebElement> napTermElements = driver
                        .findElements(By.cssSelector("#insuTermContents > div > div > span > label"));

                for (WebElement napTermEl : napTermElements) {
                    String napTerm = napTermEl.getText();
                    if (!StringUtil.isEmpty(napTerm)) {
                        napTermsSet.add(napTerm);
                        logger.debug("napTerm: " + napTerm);
                    }
                }
            }
        }

        napTerms = new ArrayList<>(napTermsSet);
        napCycles = new ArrayList<>(napCyclesSet);

        ProductMasterVO productMasterVO = new ProductMasterVO();
        productMasterVO.setProductId(info.productCode);
        productMasterVO.setProductKinds(info.defaultProductKind); // 정확히 알면 표기
        productMasterVO.setProductTypes(info.defaultProductType); // 정확히 알면 표기
        productMasterVO.setProductGubuns("주계약");
        productMasterVO.setSaleChannel(info.getSaleChannel());
        productMasterVO.setProductName(info.productName);
        productMasterVO.setInsTerms(insTerms);
        productMasterVO.setNapTerms(napTerms);
        productMasterVO.setNapCycles(napCycles);
        productMasterVO.setAssureMoneys(assureMoneys);
        productMasterVO.setMinAssureMoney(minAssureMoney);
        productMasterVO.setMaxAssureMoney(maxAssureMoney);
        productMasterVO.setCompanyId(info.getCompanyId());

        logger.debug("상품마스터 :: " + productMasterVO.toString());
        info.getProductMasterVOList().add(productMasterVO);
    }



    protected String getNapCycle2Str(String napCycleCd) {

        Map<String, String> map = new HashMap<>();
        map.put("01", "월납");
        map.put("02", "연납");
        map.put("03", "일시납");

        return map.get(napCycleCd);
    }



    // 자녀 생일 설정
    protected void childBirth(String birth) {

        element = driver.findElement(By.id("brParYmd"));
        element.clear();
        element.sendKeys(birth.substring(2));

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("imgLoading"))); // imgLoading
    }



    protected void checkProductMaster(CrawlingProduct info, String el) {

        try {
            WaitUtil.waitFor(1);
            for (CrawlingTreaty item : info.treatyList) {
                String treatyName = item.treatyName;
                String prdtName = driver.findElement(By.cssSelector(el)).getAttribute("innerHTML");

                prdtName = prdtName.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");

                if (treatyName.indexOf(prdtName) > -1) {
                    info.siteProductMasterCount++;
                    logger.info("담보명 확인 완료 !! ");
                }
            }

        } catch (Exception e) {
            logger.info("담보명 확인 에러 발생 !!");
        }
    }



    protected void setAssureMoney(String premium) throws Exception {

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView(true);",
            driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1)"))
        );

        Thread.sleep(500);

        premium = (Integer.parseInt(premium) / 10000) + "";
        String getPremium = "";

        new Actions(driver)
            .moveToElement(driver.findElement(By.cssSelector("#frmSelfInfo > ul > li:nth-child(1)")))
            .perform();

        element = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#frmSelfInfo > ul > li:nth-child(1) > div.box_middle.type_12 > div.box_sel > span")));

        WaitUtil.waitFor(2);
        element.click();
        element = driver.findElement(By.cssSelector("._sel_option.sel_m"));
        elements = element.findElements(By.tagName("li"));
        for (WebElement li : elements) {
            element = li.findElement(By.tagName("span"));
            getPremium = element.getText().replace(",", "").replace("만원", "");

            if (getPremium.contains("진단보험금")) {
                continue;
            }

            // 실제 가입금액
            //getPremium = String.valueOf(Integer.parseInt(getPremium) / 5);

            if (getPremium.equals(premium)) {
                li.click();
                WaitUtil.waitFor(2);
                break;
            }
        }
    }



    protected boolean isAlertShowed() {

        try {
            Alert alert = new WebDriverWait(driver, 2).until(ExpectedConditions.alertIsPresent());

            if (alert != null) {
                return true;
            } else {
                throw new Throwable();
            }

        } catch(Throwable e) {

            return false;
        }
    }
}
