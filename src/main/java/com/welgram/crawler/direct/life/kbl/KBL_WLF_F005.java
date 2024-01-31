package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKBL;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class KBL_WLF_F005 extends CrawlingKBL {



    public static void main(String[] args) {
        executeCommand(new KBL_WLF_F005(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setImageLoad(true);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        //웹 크롤링
        webCrawling(info);

        //모바일 크롤링
//            mobileCrawling(info);

        return true;
    }

    protected void webCrawling(CrawlingProduct info) throws Exception{
        WaitUtil.waitFor(3);

        logger.info("로딩 기다리기");
        helper.waitForCSSElement("div.loading");

        logger.info("생년월일");
        helper.sendKeys3_check(By.id("birthday"), info.fullBirth);

        logger.info("성별 :" + info.gender);
        selectGender_front(info.gender);

        logger.info("보험료 계산하기");
        helper.click(By.id("calculateResult"));

        logger.info("로딩 기다리기");
        helper.waitForCSSElement("div.loading");

        try {
            logger.info("홈페이지 무조건 오류로 시스템 알럿 발생");
            driver.findElement(By.cssSelector("#systemAlert1 > div > div.modal-footer > button")).click();
        } catch (Exception e) {
            logger.info("시스템 알럿 없음");
        }

        logger.info("납입기간: " + info.napTerm);
        selectDropDown_front(By.id("paymentTermsWrap"), info.napTerm.replace("년", "").replace("세", ""));

        logger.info("가입금액: " + info.assureMoney);
        WaitUtil.waitFor(3);
        String assureMoney = String.valueOf(Integer.parseInt(info.assureMoney) / 10000);
        WebElement element = driver.findElement(By.id("mainAmount"));
        element.click();
        element.sendKeys(Keys.DELETE);
        element.sendKeys(assureMoney);

        logger.info("보험료 계산하기");
        helper.click(By.xpath("//button[@class='btn-reset']"));

        logger.info("로딩 기다리기");
        helper.waitForCSSElement("div.loading");

        try{
            if(driver.findElement(By.id("systemAlert1")).isDisplayed()){
                throw new ElementClickInterceptedException("가입 불가 조건");
            }
        } catch (NoSuchElementException e){
            logger.info("가입가능");
        }

        WaitUtil.waitFor(2);
        String premium = driver.findElement(By.cssSelector("#insurancePlanCards > div > div.item-box > dl:nth-child(1) > dd > span")).getText();
        WaitUtil.waitFor(2);
        info.treatyList.get(0).monthlyPremium = premium.replaceAll("[^0-9]", "");
        logger.info("월 보험료 스크랩 " + (info.getTreatyList().get(0)).monthlyPremium);

        logger.info("스크린샷");
        takeScreenShot(info);
        WaitUtil.waitFor(1);

//        logger.info("보장내역보기 버튼 클릭");
//        helper.doClick(By.id("buttonResultDocumentView"));
//        helper.waitForCSSElement("div.loading");
//        WaitUtil.waitFor(1);

        logger.info("해약환급금");
        String[] returnMoneyPageList = new String[]{"2","3","4","5","6"};
        String moneyUtilLocation = "8";
        getViewerReturnPremium(info, returnMoneyPageList, moneyUtilLocation);

    }

//    protected void mobileCrawling(CrawlingProduct info) throws Exception{
//        // 크롤링시 필요한 옵션정의
//        CrawlingOption option = info.getCrawlingOption();
//        option.setBrowserType(BrowserType.Chrome);
//        option.setMobile(true);
//        option.setImageLoad(false);
//        info.setCrawlingOption(option);
//
//        startDriver(info);
//        String currentHandle = driver.getWindowHandle();
//
//        logger.info("바로 가입하기 버튼 클릭");
//        helper.doClick(driver.findElement(By.id("entry")));
//
//        logger.info("생년월일 :: {}", info.fullBirth);
//        WaitUtil.waitFor(5);
//        driver.switchTo().frame(0);
//        helper.doSendKeys(By.id("custBirth"), info.fullBirth);
//
//        logger.info("성별 :: {}", info.gender == 0 ? "남자" : "여자");
//        if(info.gender == 0){
//            logger.info("남자 선택");
//            driver.findElement(By.xpath("//label[@for='lbl_male']")).click();
//        } else {
//            logger.info("여자 선택");
//            driver.findElement(By.xpath("//label[@for='lbl_female']")).click();
//        }
//
//        logger.info("적용 버튼 클릭");
//        driver.findElement(By.id("confirm")).click();
//
//        logger.info("베타추구형 선택");
//        WaitUtil.waitFor(5);
//        driver.switchTo().window(currentHandle);
//        driver.switchTo().frame(1);
//        driver.findElement(By.xpath("//label[@for='lbl_insuranceChoice01']")).click();
//        WaitUtil.waitFor(5);
//
//        int iframe = 0;
//        logger.info("질병여부 선택 :: {}", info.textType);
//        driver.switchTo().window(currentHandle);
//        iframe = 2;
//        driver.switchTo().frame(iframe);
//        try{
//            if(info.textType.contains("간편심사형")){
//                logger.info("예.");
//                logger.info("간편심사형 선택");
//                driver.findElement(By.xpath("//label[@for='lbl_insuranceChoice02']")).click();
//                iframe += 1;
//            } else {
//                logger.info("아니오.");
//                logger.info("일반심사형 선택");
//                driver.findElement(By.xpath("//label[@for='lbl_insuranceChoice01']")).click();
//                iframe += 1;
//            }
//        }catch (Exception e){
//            logger.info("질병여부 선택 창 없음");
//        }
//        WaitUtil.waitFor(3);
//
//        String str = info.annuityType;
//        String[] strAry = str.split(" ");
//        logger.info("보험기간 :: {}", strAry[0]);
//        setSelectBox("#lbl_term-ins", strAry[0], iframe);
//
//        logger.info("납입기간 :: {}", info.napTerm);
//        setSelectBox("#lbl_term-pay", info.napTerm, iframe);
//
//        logger.info("가입금액 :: {}", info.assureMoney);
//        String str2 = info.assureMoney;
//        String assureMoney = str2.substring(0, str2.length()-4);
//        setSelectBox("#juJAmt", assureMoney, iframe);
//
//        String annuityChangeAge = String.valueOf(Integer.parseInt(info.age) + 10);
//        logger.info("연금 전환 신청 나이 :: {} 세", annuityChangeAge);
//        setSelectBox("#pensTrans", annuityChangeAge, iframe);
//
//        logger.info("연금개시 나이 :: {} 세", info.annAge);
//        setSelectBox("#pensStAge", info.annAge, iframe);
//
//        logger.info("적용 버튼 선택");
//        driver.findElement(By.id("mainBtn")).click();
//        WaitUtil.waitFor(2);
//
//        logger.info("로딩 기다리기");
//        driver.switchTo().window(currentHandle);
//        List<WebElement> elements = driver.findElements(By.xpath("//p[@id[contains(., 'loadionLayer01Txt')]]"));
//        wait.until(ExpectedConditions.invisibilityOfAllElements(elements));
//        WaitUtil.waitFor(2);
//
//        iframe = iframe + 2;
//        driver.switchTo().window(currentHandle);
//        driver.switchTo().frame(iframe);
//        String premium = driver.findElement(By.xpath("(//table[@class='tbl_plan_A']//td[@class='ftBCol01'])[1]")).getText();
//        info.treatyList.get(0).monthlyPremium = premium.replaceAll("[^0-9]", "");
//        WaitUtil.waitFor(3);
//        logger.info("월 보험료 :: {}", info.treatyList.get(0).monthlyPremium);
//
//        logger.info("해약환급금 스크랩");
//        mobileReturnPremium(info);
//
//        logger.info("스크린샷");
//        takeScreenShot(info);

//        driver.switchTo().window(currentHandle);
//        driver.switchTo().frame(iframe);

        // 종신보험의 취지에 맞게 연금테이블은 쌓지않도록 설정
//        logger.info("연금수령액 스크랩");
//        mobileAnnuityPremium(info);

//    }


    protected void setSelectBox(String cssValue, String value, int frame) throws Exception {
        boolean result = false;
        driver.switchTo().window(currentHandle);
        driver.switchTo().frame(frame);

        elements = driver.findElement(By.cssSelector(cssValue)).findElements(By.tagName("option"));

        if(cssValue.equals("#juJAmt")){
            for (WebElement option : elements) {
                logger.info(option.getText());
                if (option.getAttribute("value").contains(value)) {
                    option.click();
                    result = true;
                    break;
                }
            }
        } else {
            for (WebElement option : elements) {
                driver.switchTo().window(currentHandle);
                driver.switchTo().frame(frame);
                logger.info(option.getText());
                if (option.getText().contains(value)) {
                    option.click();
                    result = true;
                    break;
                }
            }
        }

        if (!result) {
            throw new Exception("selectBox 선택 오류!");
        }

        WaitUtil.waitFor(1);
    }

    protected void mobileReturnPremium(CrawlingProduct info) throws Exception{
        helper.click(By.xpath("(//table[@class='tbl_plan_A']//button[@name='btnRefundConfirm'])[1]"));
        WaitUtil.waitFor(2);

        driver.switchTo().window(currentHandle);

        String[] buttons = new String[]{"최저보증이율 기준", "평균공시이율 가정", "공시이율 가정"};

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        for(int i=0; i<buttons.length; i++) {
            WebElement element = driver.findElement(By.xpath("//*[@id='Popup01']//*[@id='Popup01_select']//option[text()='"+buttons[i]+"']"));
            element.click();
            logger.info( "{} 버튼을 클릭!", buttons[i]);
            PlanReturnMoney planReturnMoney = null;

            driver.switchTo().window(currentHandle);
//            driver.switchTo().frame(iframe);

            List<WebElement> trList = element.findElements(By.xpath("//tbody[@id='Popup01_tbody0"+(i+1)+"']//tr"));

            for(int j = 0; j < trList.size(); j++) {
                WebElement tr = trList.get(j);

                String term = tr.findElements(By.tagName("th")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(0).getText().replaceAll("[^0-9]", "") + "0000";
                String returnMoney = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "") + "0000";
                String returnRate = tr.findElements(By.tagName("td")).get(2).getText().trim();

                logger.info("{} 해약환급금", buttons[i]);
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--{} 해약환급금: {}", buttons[i], returnMoney);
                logger.info("|--{} 해약환급률: {}", buttons[i], returnRate);
                logger.info("|_______________________");

                if(i == 0) {
                    //최저보증이율
                    planReturnMoney = new PlanReturnMoney();

                    planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                    planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.age));

                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoneyMin(returnMoney);
                    planReturnMoney.setReturnRateMin(returnRate);

                    planReturnMoneyList.add(planReturnMoney);
                } else if(i == 1) {
                    //평균공시이율
                    planReturnMoney = planReturnMoneyList.get(j);

                    planReturnMoney.setReturnMoneyAvg(returnMoney);
                    planReturnMoney.setReturnRateAvg(returnRate);

                } else {
                    //공시이율
                    planReturnMoney = planReturnMoneyList.get(j);

                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);

                    info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
                }
            }
            info.planReturnMoneyList = planReturnMoneyList;
        }
        WaitUtil.waitFor(1);
        helper.click(By.xpath("//*[@id='Popup01']//button"));
    }

    protected void mobileAnnuityPremium(CrawlingProduct info) throws Exception {
        helper.click(By.xpath("(//table[@class='tbl_plan_A']//button[@name='btnAnnuityConfirm'])[1]"));
        driver.switchTo().window(currentHandle);
        WaitUtil.waitFor(2);

        String moneyUnit = driver.findElement(By.xpath("//*[@id='Popup12_dev01']")).getText();
        int start = moneyUnit.indexOf(":");
        int end = moneyUnit.indexOf("]");
        String unit = moneyUnit.substring(start + 1, end);

        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        String whl100a = driver.findElement(By.xpath("//*[@id=\"Popup12_tbody02\"]/tr[2]/td[1]")).getText();
        String whl20y = driver.findElement(By.xpath("//*[@id=\"Popup12_tbody02\"]/tr[2]/td[2]")).getText();
        String fxd20y = driver.findElement(By.xpath("//*[@id=\"Popup12_tbody02\"]/tr[2]/td[3]")).getText();

        planAnnuityMoney.setWhl100A(String.valueOf(MoneyUtil.toDigitMoney(whl100a + unit)));
        planAnnuityMoney.setWhl20Y(String.valueOf(MoneyUtil.toDigitMoney(whl20y + unit)));
        planAnnuityMoney.setFxd20Y(String.valueOf(MoneyUtil.toDigitMoney(fxd20y + unit)));

        info.planAnnuityMoney = planAnnuityMoney;

        if(info.annuityType.contains("종신 20년")) {
            info.annuityPremium = planAnnuityMoney.getWhl20Y();
        } else if(info.annuityType.contains("종신 100세")) {
            info.annuityPremium = planAnnuityMoney.getWhl100A();
        } else if(info.annuityType.contains("확정 20년")) {
            info.annuityPremium = planAnnuityMoney.getFxd20Y();
        } else {
            logger.info("{} 을 찾을 수 없습니다.", info.annuityType);
            throw new Exception();
        }
    }
}



