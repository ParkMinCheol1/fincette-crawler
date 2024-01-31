package com.welgram.crawler.direct.life.hwl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 한화생명 - 한화생명 - e연금보험(무)
 */
public class HWL_ANT_D002 extends CrawlingHWLDirect {

    // 한화생명 - e연금보험(무)
    public static void main(String[] args) { executeCommand(new HWL_ANT_D002(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        webCrawling(info);
        return true;
    }

    @Override
    protected boolean preValidation(CrawlingProduct info) {

        boolean result = true;
        try {
            logger.info("나이체크");
            ageChk(info);
        } catch (Exception e) {

            result = false;
            e.printStackTrace();
        }
        return result;
    }
    // 사이트웹
    private void webCrawling(CrawlingProduct info) throws Exception {

        setBirthday(By.xpath("//input[@id='birthdayDt01'][@type='text']"), info.getBirth());
        setGender(info.getGender(), By.xpath("//label[@for='gender010" + (info.getGender() + 1) + "']"));
        clickCalcButton(By.cssSelector("#calc_top_cont > div > a"));
        setAnnuityAge(By.id("CM090901Dt_gstartage"), info.getAnnuityAge() + "세");
        setAnnuityType(By.cssSelector("#CM090901Dt_gbjCode > option"), info.getAnnuityType());
        setNapTerm(By.id("CM090901Dt_gpayment"), info.getNapTerm());
        setAssureMoney(By.id("CM090901Dt_gmonthbill"), info);
        clickCalcButton(By.cssSelector("div.btn_re.wd180 > a"));
        WaitUtil.loading(5);
        helper.click(By.cssSelector("#proPop_btn01"));
        takeScreenShot(info);
        crawlAnnuityPremium(By.id("amountT001Tbl"), info);
        helper.click(By.cssSelector("#proPop_close01"));
        crawlReturnMoneyList(By.cssSelector("#terminateTbl > tbody > tr"), info);
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try {
            By position = (By) obj[0];
            String napTerm = (String) obj[1];
            String script = "return $(arguments[0]).find('option:selected').text();";
            String selectedValue = "";

            helper.waitElementToBeClickable(position);
            helper.selectOptionContainsText(driver.findElement(position), napTerm);
            WaitUtil.waitFor(1);

            selectedValue = String.valueOf(helper.executeJavascript(script, driver.findElement(position)));
            logger.info("selected value :: {}", selectedValue);

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        try {
            By position = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];
            int unit = 10000;
            int assureMoney = Integer.parseInt(info.getAssureMoney()) / unit;

            WebElement $input = driver.findElement(position);
            $input.clear();
            $input.sendKeys(String.valueOf(assureMoney));
            WaitUtil.loading(3);

            info.treatyList.get(0).monthlyPremium = info.assureMoney;

        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try{
            By by = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            WaitUtil.loading(5);
            logger.info("==================");
            logger.info("해약환급금 보기클릭");
            logger.info("==================");
            element = helper.waitPresenceOfElementLocated(By.id("proPop_btn02"));
            element.click();

            WaitUtil.loading(2);
            element = helper.waitPresenceOfElementLocated(By.linkText("전체기간 보기"));
            element.click();

            logger.info("==================");
            logger.info("해약환급금 테이블선택");
            logger.info("==================");
            elements = helper.waitPesenceOfAllElementsLocatedBy(by);

            // 주보험 영역 Tr 개수만큼 loop
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            for (WebElement tr : elements) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                String term = tr.findElements(By.tagName("th")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
                String returnMoneyMin = tr.findElements(By.tagName("td")).get(1).getText();
                String returnRateMin = tr.findElements(By.tagName("td")).get(2).getText();
                String returnMoneyAvg = tr.findElements(By.tagName("td")).get(3).getText();
                String returnRateAvg = tr.findElements(By.tagName("td")).get(4).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(5).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(6).getText();

                logger.info("경과기간 : {} ", term);
                logger.info("납입보험료 : {} ", premiumSum);
                logger.info("현공시이율 환급금 : {} ", returnMoney);
                logger.info("현공시이율 환급률 : {} ", returnRate);
                logger.info("평균공시이율 환급금 : {} ", returnMoneyAvg);
                logger.info("평균공시이율 환급률 : {} ", returnRateAvg);
                logger.info("최저보증이율 환급금 : {} ", returnMoneyMin);
                logger.info("최저보증이율 환급률 : {} ", returnRateMin);
                logger.info("===========================================");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                planReturnMoney.setReturnRateAvg(returnRateAvg);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                // 납입기간에 따른 해약환급금 금액을 calc에 저장
                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
            // 해약환급금 관련 End
        } catch (Exception e){
            throw new ReturnMoneyListCrawlerException(e);
        }
    }


}
