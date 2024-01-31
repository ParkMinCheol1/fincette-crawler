package com.welgram.crawler.direct.life;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CrawlingHDL extends SeleniumCrawler  {

    public static final Logger logger = LoggerFactory.getLogger(CrawlingHDL.class);

    protected void selectDropBox(String selectTagAttribute, String visibleTxt, WebElement... from) throws Exception {

        if (visibleTxt.equals("")) return;

        try {
            WebElement parentDiv = (from.length == 0)?
                driver.findElements(By.xpath("//select" + selectTagAttribute + "/parent::div"))
                    .stream().filter(WebElement::isDisplayed).findFirst().get()
                : from[0].findElements(By.xpath(".//select" + selectTagAttribute + "/parent::div"))
                    .stream().filter(WebElement::isDisplayed).findFirst().get();

            // 화면에 보여지는 버튼
            WebElement button = parentDiv.findElement(By.cssSelector("button"));
//        if (button.getText().contains(visibleTxt)) return;

            int attempt = 2;
            // option(li)이 펼쳐질 때까지 클릭
            while (attempt > 0 && !parentDiv.getAttribute("class").contains("active")) {
                helper.click(button);
                helper.waitForLoading();;
                attempt--;
            }

            WebElement li = parentDiv.findElements(By.tagName("li")).stream().filter(
                el -> el.getAttribute("innerHTML").contains(visibleTxt)
            ).findFirst().orElseThrow(() -> new NoSuchElementException(visibleTxt + " 를 찾을 수 없습니다."));

            // 이유는 모르겠지만 HDL_TRM_D005 납입기간 선택시 아래와 같이 xpath로 요소를 잡았을 때 클릭을 할 수가 없었음
//        WebElement li = parentDiv.findElement(By.xpath("//li[contains(text(),'" + visibleTxt + "')]"));

            attempt = 2;
            while (attempt > 0 && !button.getText().contains(visibleTxt)) { // li가 제대로 선택될때까지 클릭
                helper.click(li);
                helper.waitForLoading();;
                attempt--;
            }

            if (!button.getText().contains(visibleTxt)) {
                throw new RuntimeException(visibleTxt + " 가 선택되지 않았습니다.");
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    protected void sendKeys(By by, String keys) throws Exception {
        WebElement el = helper.waitElementToBeClickable(by);

        helper.moveToElementByJavascriptExecutor(el);
        helper.sendKeys3_check(el, keys);
        helper.executeJavascript("arguments[0].blur();", el);

        helper.waitForLoading();
        WaitUtil.loading(1);
    }

    protected void sendKeys(WebElement el, String keys) throws InterruptedException {

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);

        new Actions(driver)
            .moveToElement(el)
            .pause(Duration.ofSeconds(1)).perform();

        for (int i = 0; i < el.getAttribute("value").length(); i++) {
            new Actions(driver).sendKeys(el, Keys.BACK_SPACE).perform();
        }
            new Actions(driver).sendKeys(el, keys).perform();

        helper.waitForLoading();;
        WaitUtil.loading(1);
    }

    protected void selectGenderLabel(CrawlingProduct info) throws InterruptedException {
        int attempt = 2;
        String gender = info.gender == 0 ? "남자" : "여자";

        WebElement genderLabel = driver.findElement(By.xpath(
            "//div[@aria-label='" + "성별" + "']//label[contains(text(),'" + gender + "')]"));
        WebElement genderInput = genderLabel.findElement(By.xpath("parent::div/input"));

        while (attempt > 0 && !Boolean.parseBoolean(genderInput.getAttribute("checked"))) {
            attempt--;
            helper.click(genderLabel);
            helper.waitForLoading();;
        }

    }

    protected void clickCalcBtn() throws Exception {

        WebElement 보험료계산하기버튼 = helper.waitElementToBeClickable(By.id("btnPremCacl"));

        WebElement 해약환급금버튼 = helper.waitPresenceOfElementLocated(
            By.xpath("//button[text()='해약환급금']"));

        int attempt = 2;
        // 해약환급금 버튼이 활성화 될 때까지 클릭
        while (attempt > 0 && Boolean.parseBoolean(해약환급금버튼.getAttribute("disabled"))) {
            helper.click(보험료계산하기버튼);
            helper.waitForLoading();;
            attempt--;
        }
        WaitUtil.loading(1);
    }


    protected void selectPlan(CrawlingProduct info, String... planTypes ) throws CommonCrawlerException {
        try {
            WaitUtil.loading(3);
            helper.waitForLoading();;

            helper.click(
                By.xpath("//div[@id='pContents']//table//td[contains(.,'" + info.getProductNamePublic() + "')]/ancestor::tr//td[3]"));

            ArrayList<String> handleList = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(handleList.get(1));
            WaitUtil.loading(3);
            helper.waitForLoading();;

            logger.info("설계유형 선택");
            String planType = (planTypes.length == 0) ? info.textType : planTypes[0];
            selectDropBox("[@id='prodSel03']", planType);

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.FAIL, e);
        }
    }

    protected void selectPlanName(CrawlingProduct info, String... planTypes ) throws CommonCrawlerException {
        try {
            WaitUtil.loading(3);
            helper.waitForLoading();;

            helper.click(
                By.xpath("//div[@id='pContents']//table//td[contains(.,'" + info.planName + "')]/ancestor::tr//td[3]"));

            ArrayList<String> handleList = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(handleList.get(1));
            WaitUtil.loading(3);
            helper.waitForLoading();;

            logger.info("설계유형 선택");
            String planType = (planTypes.length == 0) ? info.textType : planTypes[0];
            selectDropBox("[@id='prodSel03']", planType);

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.FAIL, e);
        }
    }

    protected void selectNotPublicPlan(CrawlingProduct info, String... planTypes ) throws CommonCrawlerException {
        try {
            WaitUtil.loading(3);
            helper.waitForLoading();;

            helper.click(
                By.xpath("//div[@id='pContents']//table//td[contains(.,'" + info.getProductName() + "')]/ancestor::tr//td[3]"));

            ArrayList<String> handleList = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(handleList.get(1));
            WaitUtil.loading(3);
            helper.waitForLoading();;

            logger.info("설계유형 선택");
            String planType = (planTypes.length == 0) ? info.textType : planTypes[0];
            selectDropBox("[@id='prodSel03']", planType);

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.FAIL, e);
        }
    }


    protected void setUserInfo(CrawlingProduct info) throws Exception {
        logger.info("생년월일");
        sendKeys(By.xpath("//label[text()='생년월일']/parent::div//input"), info.fullBirth);
        helper.waitForLoading();;

        logger.info("성별 - 라벨 클릭");
        selectGenderLabel(info);
    }


    protected void setMainTreaty(CrawlingProduct info) throws Exception {

        if (info.productCode.contains("ANT") || info.productCode.contains("ASV")) {

            logger.info("연금개시나이");
            selectDropBox("[@title[contains(., '연금개시나이')]]", info.annuityAge);

            logger.info("납입기간");
            selectDropBox("[@title[contains(., '납입기간')]]", info.napTerm);

            logger.info("납입주기 - 생략");

            logger.info("보험료");
            sendKeys(By.xpath("//input[@title='보험료 입력']"), info.assureMoney);

            logger.info("연금유형 - 종신형 생략");

            logger.info("보증기간");
            selectDropBox("[@title[contains(., '보증기간')]]", info.annuityType.replaceAll("\\D", ""));

        } else {

            logger.info("가입금액");
            String assureMoney = String.valueOf(
                Integer.parseInt(info.assureMoney) / 10000);
            sendKeys(By.xpath("//input[@title='가입금액 입력']"), assureMoney);

            logger.info("보험기간");
            String insTerm = info.insTerm.equals("종신보장") ? "99년" : info.insTerm;
            selectDropBox("[@title[contains(., '보험기간')]]", insTerm);

            logger.info("납입기간");
            selectDropBox("[@title[contains(., '납입기간')]]", info.napTerm);

            logger.info("납입주기 - 생략");
        }

    }

    protected void setOptionalTreaty(CrawlingProduct info) throws Exception {
        logger.info("특약계산");
        List<WebElement> treatyTrList = helper.waitPesenceOfAllElementsLocatedBy(
            By.cssSelector("#specialContractTable tbody tr"));

        for (CrawlingTreaty t : info.treatyList) {

            if (t.productGubun.equals(ProductGubun.주계약)) continue;

            // 특약명이 매치되는 tr 찾기
            WebElement matchedTr = treatyTrList.stream().filter(tr -> {
                String pageTreatyName = tr.findElement(By.cssSelector("td:nth-child(2)"))
                    .getText().trim();
                return pageTreatyName.contains(t.treatyName);
            }).findFirst().get();

            // 선택
            helper.click(matchedTr.findElement(
                By.cssSelector("td:nth-child(1) label")));

            // 가입금액
            logger.info("가입금액");
            String tAssureMoney = String.valueOf(t.assureMoney / 10000);
            sendKeys(matchedTr.findElement(By.cssSelector("input[data-title*='가입금액']")), tAssureMoney);

            // 보험기간
            logger.info("보험기간");
            String tInsTerm = info.insTerm.equals("종신보장") ? "99년" : t.insTerm;
            selectDropBox("[@title[contains(., '보험기간')]]", tInsTerm, matchedTr);

            // 납입기간
            logger.info("납입기간");
            selectDropBox("[@title[contains(., '납입기간')]]", t.napTerm, matchedTr);
        }
    }

    protected void getMonthlyPremium(CrawlingProduct info) throws Exception {
        logger.info("보험료 계산하기 클릭");
        clickCalcBtn();

        logger.info("보험료");
        info.treatyList.get(0).monthlyPremium =
            helper.waitPresenceOfElementLocated(By.id("dcAfterPrem"))
                .getText().replaceAll("[^\\d.]", "");

        logger.info("스크린샷 찍기");
        takeScreenShot(info);
    }

/*    protected void getPremium(CrawlingProduct info) throws Exception {
        logger.info("보험료 계산하기 클릭");
        driver.findElement(By.cssSelector("#btnPremCacl")).click();
        helper.waitForCSSElement("#loading > div.c-loading__bars > div.c-loading__bar.c-loading__bar--12");
        WaitUtil.waitFor(3);

        logger.info("보험료");
        info.treatyList.get(0).monthlyPremium = driver.findElement(By.cssSelector("#btnPremCacl")).getText().trim().replaceAll("[^0-9]", "");
        logger.info("스크린샷 찍기");
        takeScreenShot(info);
    }*/

    protected void getPlanReturnMoney(CrawlingProduct info) throws Exception {

        logger.info("해약환급금");

        boolean isTable; // 가끔씩 해약환급금 모달창이 열렸을 때 table 요소가 나타나지 않음. 나타날 때까지 닫았다 다시 켜기.
        int attempt = 2;

        do {
            attempt--;

            WebElement 해약환급금버튼 = helper.waitPresenceOfElementLocated(
                By.xpath("//button[text()='해약환급금']"));
            helper.click(해약환급금버튼);

            wait.until(webDriver -> webDriver.getWindowHandles().size() == 3);
            ArrayList<String> handleList = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(handleList.get(2));

            WaitUtil.loading(2);

            try {
                WebElement table = helper.waitPresenceOfElementLocated(
                    By.xpath("//h2[contains(text(),'해약환급금')]/parent::div//table"));
                isTable = true;
            } catch (Exception e) {
                isTable = false;
                driver.close();
            }

        } while (!isTable && attempt > 0);

        if (!isTable) {
            throw new Exception("해약환급금 정보가 없습니다");
        }

        List<WebElement> thList = helper.waitPesenceOfAllElementsLocatedBy(
            By.xpath("//h2[contains(text(),'해약환급금')]/parent::div//table//thead//th"));
        HashMap<String, Integer> tdNumMap = new HashMap<>();


        for (int i = 0; i < thList.size(); i++) {
            String thTxt = thList.get(i).getText();
            int tdIdx = i + 1;

            if (thTxt.contains("경과기간")) {
                tdNumMap.put("경과기간", tdIdx);
            } else if (thTxt.contains("납입보험료")) {
                tdNumMap.put("납입보험료", tdIdx);
            } else if (thTxt.contains("해약환급금")) {
                tdNumMap.put("해약환급금", tdIdx);
            } else if (thTxt.contains("환급률")) {
                tdNumMap.put("환급률", tdIdx);
                break;
            }
        }

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trList = helper.waitPesenceOfAllElementsLocatedBy(
            By.xpath("//h2[contains(text(),'해약환급금')]/parent::div//table//tbody/tr"));

        int trListSize = trList.size();
        for (int i = 0 ; i < trListSize ; i++) {

            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

            WebElement termEl = trList.get(i).findElement(By.cssSelector("td:nth-child(" + tdNumMap.get("경과기간") + ")"));
            new WebDriverWait(driver, 10).until(webDriver -> {
                return !termEl.getText().isEmpty();
            });
            String term = termEl.getText().trim();
            logger.info("해약환급금 크롤링:: 납입기간 :: " + term);

            String premiumSum = trList.get(i).findElement(By.cssSelector("td:nth-child(" + tdNumMap.get("납입보험료") + ")")).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

            Integer 해약환급금;
            Integer 환급률;
            switch (info.productCode) {
                case "HDL_WLF_F003" :
                    해약환급금 = 5;
                    환급률 = 6;
                    break;
                case "HDL_ANT_F001" :
                case "HDL_ANT_F002" :
                case "HDL_WLF_F018" :
                    해약환급금 = 3;
                    환급률 = 4;
                    break;
                default:
                    해약환급금 = tdNumMap.get("해약환급금");
                    환급률 = tdNumMap.get("환급률");
            }

            String returnMoney 	= trList.get(i).findElement(By.cssSelector("td:nth-child(" + 해약환급금 + ")")).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금 :: " + returnMoney);

            String returnRate 	= trList.get(i).findElement(By.cssSelector("td:nth-child(" + 환급률 + ")")).getText();
            logger.info("해약환급금 크롤링:: 환급률 :: " + returnRate);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);

            if((i+1) < trListSize){
                WebElement element = trList.get(i+1);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            }
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);

        PlanReturnMoney lastReturnMoney = planReturnMoneyList.get(planReturnMoneyList.size() - 1);

//        if (info.productCode.contains("_ASV_") || info.productCode.contains("_ANT_")) { // 연금보험, 연금저축보험
//
//            // 경과기간
//            int term = Integer.parseInt(
//                lastReturnMoney.getTerm().replaceAll("\\D", ""));
//
//            int age = Integer.parseInt(info.age);
//            int annAge = Integer.parseInt(info.annAge);
//
//            if (term == (annAge - age)) {
//                info.returnPremium = lastReturnMoney.getReturnMoney();
//            }
//
//        } else if (lastReturnMoney.getTerm().equals(info.insTerm)) {
//            info.returnPremium = lastReturnMoney.getReturnMoney();
//        }

        if(info.productKind.contains("순수")){
            info.returnPremium = "0";
            logger.info("환급유형 : {} " ,info.productKind);
            logger.info("순수보장형의 경우는 만기시에 환급금이 0원으로 고정");
            logger.info("만기환급금 : {} " , info.returnPremium);
        }else{
            info.returnPremium = lastReturnMoney.getReturnMoney();
            logger.info("만기환급금 : {} " , info.returnPremium);
        }
    }

    static class TdElement {
        private String text;
        private int rowSpan;
        private int colSpan;

        public TdElement(WebElement element) {
            String rowspan = element.getAttribute("rowspan");
            String colspan = element.getAttribute("colspan");
            String text = element.getText();
            this.rowSpan = rowspan == null ? 0 : (Integer.parseInt(rowspan));
            this.colSpan = colspan == null ? 0 : (Integer.parseInt(colspan));
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public int getRowSpan() {
            return rowSpan;
        }

        public int getColSpan() {
            return colSpan;
        }
    }


    protected void setAnnuityMoney(CrawlingProduct info) throws Exception {

        helper.click(By.xpath("//button[contains(.,'연금액 예시')]"), "연금액 예시 탭");

        // 연금액 예시 tbody
        List<WebElement> trList = helper.waitPesenceOfAllElementsLocatedBy(
            By.cssSelector("tbody#antyExam tr"));

        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        int ColSize = 4; // col1 : 연금타입, col2 : 보증년수, col4: 연복리3.15% 가정 년 수령액
        int[] rowspanOfCols = new int[ColSize];
        String annuityType = "";
        String annuityYear = "";

        for (WebElement tr : trList) {

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

            int skipForRowspan = 0;
            int skipForColspan = 0;
            for (int i = 0; i < ColSize; i++) {

                if (rowspanOfCols[i] > 0) {
                    skipForRowspan++;
                    --rowspanOfCols[i];
                    continue;
                }

                int n = i + 1 - skipForRowspan - skipForColspan;
                TdElement td = new TdElement(tr.findElement(By.cssSelector("td:nth-child(" + n + ")")));
                rowspanOfCols[i] = td.getRowSpan() == 0 ?  0 : td.getRowSpan() - 1;

                if (i == 0) {
                    annuityType = td.getText();
                } else if (i == 1) {
                    annuityYear = td.getText();
                } else if (i == 3) {
                    String annuityMoney = td.getText().replaceAll("\\D","");
                    setAnnuityMoney(info, planAnnuityMoney, annuityType, annuityYear, annuityMoney);
                }

                if (skipForColspan == 0) {
                    skipForColspan = td.getColSpan() == 0 ?  0 : td.getColSpan() - 1;
                }
            }
        }

        info.planAnnuityMoney = planAnnuityMoney;
    }

    private static void setAnnuityMoney(CrawlingProduct info, PlanAnnuityMoney planAnnuityMoney,
        String annuityType, String annuityYear, String annuityMoney) {
        // 종신, 확정인지 확인
        if (annuityType.contains("종신연금형") && annuityType.contains("(기본형)")) {

            // 몇년 보증인지 확인하고 수령액 설정
            if (annuityYear.contains("10년")) {
                planAnnuityMoney.setWhl10Y(annuityMoney);
                info.annuityPremium = annuityMoney;
            } else if (annuityYear.contains("20년")) {
                planAnnuityMoney.setWhl20Y(annuityMoney);
            } else if (annuityYear.contains("30년")) {
                planAnnuityMoney.setWhl30Y(annuityMoney);
            } else if (annuityYear.contains("100세")) {
                planAnnuityMoney.setWhl100A(annuityMoney);
            }

        } else if (annuityType.contains("확정연금형")) {

            // 몇년 보증인지 확인
            if (annuityYear.contains("10년")) {
                planAnnuityMoney.setFxd10Y(annuityMoney);
                info.fixedAnnuityPremium = annuityMoney;
            } else if (annuityYear.contains("15년")) {
                planAnnuityMoney.setFxd15Y(annuityMoney);
            } else if (annuityYear.contains("20년")) {
                planAnnuityMoney.setFxd20Y(annuityMoney);
            } else if (annuityYear.contains("25년")) {
                planAnnuityMoney.setFxd25Y(annuityMoney);
            } else if (annuityYear.contains("30년")) {
                planAnnuityMoney.setFxd30Y(annuityMoney);
            }
        }
    }

    protected void planTypeSelect(CrawlingProduct info){

        logger.info("플랜타입 선택하기 : "+info.textType);

        elements = driver.findElements(By.cssSelector("div.listbox > select > option"));
        int size = elements.size();

        for(int i=0; i<size; i++){

            if(elements.get(i).getText().trim().contains(info.textType)){
                elements.get(i).click();
                break;
            }
        }

    }

}
