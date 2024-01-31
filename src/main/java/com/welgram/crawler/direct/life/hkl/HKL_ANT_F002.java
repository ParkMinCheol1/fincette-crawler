package com.welgram.crawler.direct.life.hkl;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingHKL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;



public class HKL_ANT_F002 extends CrawlingHKL {

    // (무)흥국생명 프리미엄드림연금보험
    public static void main(String[] args) {
        executeCommand(new HKL_ANT_F002(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        disclosureRoomCrawling(info);

        return true;
    }



    private void disclosureRoomCrawling(CrawlingProduct info) throws Exception {

        logger.info("================");
        logger.info("공시실 크롤링 시작!");
        logger.info("================");

        // 이름
        // id : linkText
        logger.info("이름");
        String name = PersonNameGenerator.generate();
        logger.debug("name: {}", name);
        helper.sendKeys3_check(By.id("custNm"), name);

        // 성별
        logger.info("성별");
        setGenderNew(info.gender);

        // Full_생년월일
        // id : birthday
        logger.info("생년월일");
        driver.findElement(By.xpath("//*[@id=\"birthday\"]")).sendKeys(info.fullBirth);

        // 보험종류 선택
        logger.info("보험종류 선택!");
        WebElement productKindEl = driver.findElement(By.xpath("//*[@id=\"bhCd\"]/option[contains(text(),'" + info.textType + "')]"));
        productKindEl.click();

        // 연금개시나이 선택
        logger.info("연금개시나이 선택!");
        WebElement productAnnuityAgeEl = driver.findElement(By.xpath("//*[@id=\"anGsiAge\"]/option[contains(text(),'" + info.annuityAge
            + "')]"));
        productAnnuityAgeEl.click();

        // 납입기간 선택
        logger.info("납입기간 선택!");
        WebElement productNaptermEl = driver.findElement(By.xpath("//*[@id=\"niTerm\"]/option[contains(text(),'" + info.napTerm + "')]"));
        productNaptermEl.click();

        // 납입기간 선택
        logger.info("연금지급주기 선택!");
        WebElement productAnnuityCycleEl = driver.findElement(By.xpath("//*[@id=\"anJiCycl\"]/option[contains(text(),'매년')]"));
        productAnnuityCycleEl.click();

        // 연금지급형태
        logger.info("연금지급형태 선택!");

        // 확정일때 종신과 동잏라게 보험기간으로 변경
        if (info.annuityType.contains("확정")) {
            info.insTerm = "종신";
        }

        String annuityPaymentString = info.insTerm.replace("보장", "") + "연금형(개인," + info.napTerm + "보증형)";
        WebElement annuityPaymentEl
            = driver.findElement(By.xpath("//*[@id=\"antyCd\"]/option[contains(text(),'" + annuityPaymentString + "')]"));
        annuityPaymentEl.click();

        // 보험료
        logger.info("보험료 입력!");
        driver.findElement(By.xpath("//*[@id=\"bhRyo\"]")).sendKeys(Integer.toString(Integer.parseInt(info.assureMoney) / 10000));
        info.treatyList.get(0).monthlyPremium = info.assureMoney;

        // 계산하기
        logger.info("계산하기");
        calculatePremium();

        // 스크린샷을 위한 스크롤 다운
        logger.info("스크린샷을 위한 스크롤 다운");
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,250)");

        logger.info("스크린샷");
        takeScreenShot(info);

        // 연금수령액
        logger.info("연금수령액");
        setDisclosureRoomAnnuityPremium(info, By.cssSelector("#frmPage > dd.dd_first > div:nth-child(10) > table > tbody > tr"));

        // 해약환급금
        logger.info("해약환급금보기 클릭!");
        elements = driver.findElements(By.cssSelector("#frmPage > dt.first > ul > li"));
        for (int i = 0; i < elements.size(); i++) {
            if (i == 2) {
                elements.get(i).findElement(By.tagName("p")).click();
            }
        }
        WaitUtil.loading(4);
        setReturnMoney(By.cssSelector("#frmPage > dd.dd_third > div.table_wrap.overflow > table > tbody > tr "), info);
    }



    // 해약환급금 공시실
    @Override
    protected void setReturnMoney(By by, CrawlingProduct info) {

        logger.info("해약환급금 테이블선택");
        logger.info("====================");

        elements = helper.waitPesenceOfAllElementsLocatedBy(by);

        String term;
        String premiumSum;
        String returnMoney;
        String returnRate;
        String returnMoneyMin;
        String returnRateMin;
        String returnMoneyAvg;
        String returnRateAvg;

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            term = tr.findElements(By.tagName("td")).get(0).getText();
            premiumSum = tr.findElements(By.tagName("td")).get(1).getText();

            returnMoneyAvg = tr.findElements(By.tagName("td")).get(2).getText();
            returnRateAvg = tr.findElements(By.tagName("td")).get(3).getText();

            returnMoney = tr.findElements(By.tagName("td")).get(4).getText();
            returnRate = tr.findElements(By.tagName("td")).get(5).getText();

            returnMoneyMin = tr.findElements(By.tagName("td")).get(6).getText();
            returnRateMin = tr.findElements(By.tagName("td")).get(7).getText();

            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("해약환급금 : {}", returnMoney);
            logger.info("해약환급률 : {}", returnRate);
            logger.info("평균 해약환급금 : {}", returnMoneyAvg);
            logger.info("평균 해약환급률 : {}", returnRateAvg);
            logger.info("최저 해약환급금 : {}", returnMoneyMin);
            logger.info("최저 해약환급률 : {}", returnRateMin);
            logger.info("==================================");

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);
            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");

        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
    }
}