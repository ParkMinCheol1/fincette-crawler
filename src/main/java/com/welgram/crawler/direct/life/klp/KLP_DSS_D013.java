package com.welgram.crawler.direct.life.klp;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKLP;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingOption.BrowserType;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KLP_DSS_D013 extends CrawlingKLP {



    public static void main(String[] args) {
        executeCommand(new KLP_DSS_D013(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);

        return true;
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setBrowserType(BrowserType.Chrome);
        option.setImageLoad(true);
    }

    private void crawlFromHomepage(CrawlingProduct info) throws Exception {


            logger.info("생년월일");
            helper.sendKeys1_check(By.id("plnnrBrdt"), info.fullBirth);

            logger.info("성별");
            setGender(info.gender);

            logger.info("보험료 확인/가입");
            setConfirmPremium(By.id("fastPayCalc"));

            logger.info("보험료 확인/가입");

            int assureMoney = info.treatyList.get(0).assureMoney/10000000;
            String assureMoneyStr = assureMoney+"천만원";

            String userPageAssureMoney1 = driver.findElement(By.cssSelector("#iamtArea > span.rdo_m._enabled._checked > label")).getText().trim();
            String userPageAssureMoney2 = driver.findElement(By.cssSelector("#iamtArea > span.rdo_m._enabled._unChecked > label")).getText().trim();


            if(userPageAssureMoney1.equals(assureMoneyStr)){
                driver.findElement(By.cssSelector("#iamtArea > span.rdo_m._enabled._checked > label")).click();
            }

            else if(userPageAssureMoney2.equals(assureMoneyStr)){
                driver.findElement(By.cssSelector("#iamtArea > span.rdo_m._enabled._unChecked > label")).click();
            }
            WaitUtil.waitFor(1);

            logger.info("보험기간");
            setInsTerm(By.id("inspdContents"), info.insTerm);

            logger.info("납입기간");
            listSetNapTerm(info.napTerm);

            logger.info("결과 확인하기");
            confirmResult();

            logger.info("보험료");
            getPremium("#premiumLabel2", info);

            logger.info("해약환급금 조회");
            getReturns("cancel1", info);

    }

    protected void setConfirmPremium(By by) throws Exception {

        // logger.debug(driver.findElement(By.cssSelector(".btn_talk_close")).isDisplayed());
        // 해당 클래스명이 보이면 true
        WaitUtil.loading(6);
        if (driver.findElement(By.cssSelector(".btn_talk_close")).isDisplayed()) {
            Thread.sleep(1000);
            element = driver.findElement(By.className("btn_talk_close"));
            element.click();

            logger.debug("상담톡 닫기!");
            Thread.sleep(1000);
        }
        logger.debug("step1");
        WaitUtil.loading(1);
        //((JavascriptExecutor) driver).executeScript("scroll(0,1000);");
        element = driver.findElement(by);
        element.click();
        logger.debug("step2");

        helper.waitForCSSElement("#loadingArea");
    }

    protected void setDiagnosticBenefit(String value) throws Exception {
        boolean result = true;
        String premium = value;
        for (int i = 0; i < 3; i++) {
            WaitUtil.waitFor(2);
            if (!(driver.findElements(By.className("tab_rdo")).get(0).isDisplayed())) {
                logger.debug("로딩 중....");
            } else {
                logger.debug("로딩 끝....");
                break;
            }
        }
        element = driver.findElements(By.className("tab_rdo")).get(0);
        elements = element.findElements(By.tagName("li"));

        while (result) {
            for (int i = 0; i < elements.size(); i++) {
                element = elements.get(i);
                element = element.findElement(By.tagName("input"));
                if (element.getAttribute("value").equals(premium)) {
                    elements.get(i).click();
                    helper.waitForCSSElement("#loadingArea");
                    // 체크되지 않은 경우에 다시 클릭
                    if (elements.get(i).getAttribute("class").contains("_unChecked")) {
                        logger.debug("####### 다시 클릭!!!");
                        elements.get(i).click();
                        helper.waitForCSSElement("#loadingArea");
                    } else {
                        logger.debug("######## 가입금액: " + premium + "원 선택완료!");
                        result = false;
                        break;
                    }
                }
            }
        }
    }


    protected void listSetNapTerm(String values) throws Exception {
        Boolean result = false;
        String value = values.replace("년", "").replace("세", "");
        // 월납클릭
        /*element = driver.findElement(By.id("insuTermHeader")).findElement(By.className("tab_rdo"));
        element = element.findElements(By.tagName("li")).get(0); // 0 : 월납, 1 : 년납, 2 : 일시납
        element.click();*/
        logger.info("납기 : "+value);

        helper.waitForCSSElement("#loadingArea");
        //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading


        //요기가 문제제
      /* element = driver.findElement(By.id("insuTermContents")).findElement(By.tagName("div"))
                .findElement(By.className("box_rdo"));*/

        element = driver.findElement(By.cssSelector("#insuTermContents > div.box_rdo"));

        elements = element.findElements(By.className("rdo_m"));

        logger.info("반복 사이즈 : "+elements.size());

        for (WebElement el : elements) {
            element = el.findElement(By.tagName("input"));
            if (element.getAttribute("value").equals("01|" + value)
                    || element.getAttribute("value").equals("02|" + value)) {
                logger.info("넘어오는지 확인");
                el.findElement(By.tagName("label")).click();
                helper.waitForCSSElement("#loadingArea");
                //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingArea"))); // imgLoading
//				waitFor();
                logger.info("######## 납입기간 " + values + " 선택완료");
                result = true;
                break;
            }
        }
        if (!result) {
            throw new Exception("해당 나이에서는 납입기간을 선택할수 없음 : " + value);
        }

    }

}
