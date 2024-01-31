package com.welgram.crawler.direct.life.hwl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.direct.life.CrawlingHWL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 한화생명 - e재테크저축보험(무)
 */
public class HWL_SAV_D001 extends CrawlingHWLDirect {

    // 한화생명 - e재테크저축보험(무)
    public static void main(String[] args) { executeCommand(new HWL_SAV_D001(), args); }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        webCrawling(info);
        return true;
    }

    // 사이트웹
    protected void webCrawling(CrawlingProduct info) throws Exception{

        setBirthday(By.xpath("//input[@id='birthdayDt01'][@type='text']"), info.getBirth());
        setGender(info.getGender(), By.xpath("//label[@for='gender010" + (info.getGender() + 1) + "']"));
        clickCalcButton(By.cssSelector("#calc_top_cont > div > a"));
        checkAgePopup();

        setAssureMoney(By.id("CM090401Dt_monthbill"), info);
        setNapTerm(By.xpath("//li[@class='pro_list04_wd02']//dd//span[normalize-space()='" + info.getNapTerm() + "']"), info.getNapTerm()); //
        clickCalcButton(By.cssSelector("div.btn_re.wd180 > a"));
        crawlPremium(By.id("resMonthbill"), info);
        takeScreenShot(info);
        crawlReturnMoneyList(By.cssSelector("#terminateTbl > tbody > tr"), info); //
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
            WaitUtil.loading(2);

            info.treatyList.get(0).monthlyPremium = info.assureMoney;

        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try {
            By position = (By) obj[0];

            driver.findElement(position).click();

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }
    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        try {
            By position = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];
            int unit = 10000;

            String premium = driver.findElement(position).getText().replaceAll("[^0-9]", "");
            premium = String.valueOf(Integer.parseInt(premium) * unit);
            logger.info("==================================");
            logger.info("월 보험료 : {}", premium);
            logger.info("==================================");

            info.treatyList.get(0).monthlyPremium = premium;

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            By by = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            WaitUtil.loading(2);
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
            elements = helper
                .waitPesenceOfAllElementsLocatedBy(by);

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
