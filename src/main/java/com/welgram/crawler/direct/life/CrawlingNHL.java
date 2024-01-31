package com.welgram.crawler.direct.life;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingNHL extends SeleniumCrawler {



    //element 클릭 명시적 대기
    protected WebElement waitElementToBeClickable(WebElement element) throws Exception {
        WebElement returnElement = null;
        boolean isClickable = element.isDisplayed() && element.isEnabled();

        if(isClickable) {
            //element가 화면상으로 보이며 활성화 되어있을 때만 클릭 가능함
            returnElement = wait.until(ExpectedConditions.elementToBeClickable(element));
        } else {
            throw new Exception("element가 클릭 불가능한 상태입니다.");
        }

        return returnElement;
    }


    //element 보일때까지 명시적 대기
    protected WebElement waitPresenceOfElementLocated(By by) throws Exception {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    //element 보일때까지 명시적 대기
    protected List<WebElement> waitPresenceOfAllElementsLocatedBy(By by) throws Exception {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    //element 보일때까지 명시적 대기
    protected WebElement waitVisibilityOfElementLocated(By by) throws Exception {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    //element 보일때까지 명시적 대기
    protected List<WebElement> waitVisibilityOfAllElementsLocatedBy(By by) throws Exception {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }


    //element 보일때까지 명시적 대기
    protected List<WebElement> waitVisibilityOfAllElements(By by) throws Exception {
        List<WebElement> elements = driver.findElements(by);
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }


    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(By by) throws Exception {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }



    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }


    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(WebElement element, String text) {
        element.click();
        element.clear();
        element.sendKeys(text);
    }



    //select box에서 text와 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(By by, String text) throws Exception{
        Select select = new Select(driver.findElement(by));

        try {
            select.selectByVisibleText(text);
        }catch (NoSuchElementException e) {
            throw new NoSuchElementException("selectbox에서 해당 text(" + text + ")를 찾을 수 없습니다");
        }
    }


    //select box에서 text와 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(WebElement element, String text) throws Exception{
        Select select = new Select(element);

        try {
            select.selectByVisibleText(text);
        }catch (NoSuchElementException e) {
            throw new NoSuchElementException("selectbox에서 해당 text(" + text + ")를 찾을 수 없습니다");
        }
    }


    //select box에서 value값이 일치하는 option 클릭하는 메서드
    protected void selectOptionByValue(By by, String value) throws Exception{
        Select select = new Select(driver.findElement(by));

        try {
            select.selectByValue(value);
        }catch (NoSuchElementException e) {
            throw new NoSuchElementException("selectbox에서 해당 value(" + value + ")를 찾을 수 없습니다");
        }
    }


    //select box에서 value값이 일치하는 option 클릭하는 메서드
    protected void selectOptionByValue(WebElement element, String value) throws Exception{
        Select select = new Select(element);

        try {
            select.selectByValue(value);
        }catch (NoSuchElementException e) {
            throw new NoSuchElementException("selectbox에서 해당 value(" + value + ")를 찾을 수 없습니다");
        }
    }

    // 크롤링시 필요한 옵션 정의
//    protected void setChromeOptionNHL(CrawlingProduct info) {
//        CrawlingOption co = info.getCrawlingOption();
//        co.setRandomUserAgent(false);
//        info.setCrawlingOption(co);
//    }

    // 생년월일 set
    protected void setBirth(String id, String birth) throws InterruptedException {
        element = driver.findElement(By.id(id));
        element.clear();
        element.sendKeys(birth);
        logger.info(birth);
        WaitUtil.loading(2);
    }

    // 성별 set
    protected void setGender(int gender) throws Exception {
        String genderStr = (gender == MALE) ? "남자" : "여자";
        driver.findElement(By.xpath("//span[contains(., '" + genderStr + "')]")).click();
        WaitUtil.loading(2);
    }

    // 공시실 성별 set
    protected void setGenderAnnounce(int gender) throws Exception {
        if(MALE == gender) {
            element = driver.findElement(By.id("sex_m"));
            logger.info("남");
        }else {
            element = driver.findElement(By.id("sex_fm"));
            logger.info("여");
        }
        element.click();
        WaitUtil.waitFor(2);
    }

    // 플랜 선택
    protected void setPlanType(String textType) throws Exception {
        String[] planStandard = textType.split(" ");

        List<WebElement> options = driver.findElement(By.id("selPlan")).findElements(By.tagName("option"));
        for(WebElement option : options){
            String optionText = option.getText();
            boolean isOk = true;

            for(int i = 0; i<planStandard.length; i++){
                if(!optionText.contains(planStandard[i])){
                  isOk = false;
                  break;
                }
            }

            if(isOk) {
                option.click();
                break;
            }
        }
        WaitUtil.waitFor(2);
    }

    //오버로딩
    protected void setPlanType(String id, String textType)throws Exception{
        List<WebElement> options = driver.findElement(By.id(id)).findElements(By.tagName("option"));
        boolean isOk = false;

        for(WebElement option : options){
            String optionText = option.getText();

            if(optionText.contains(textType)) {
                option.click();
                isOk = true;
                break;
            }
        }
        if(!isOk){
            throw new Exception(textType + " :: 상품유형이 존재하지않습니다.");
        }
        WaitUtil.waitFor(2);
    }

    // 납입 주기 선택
    protected void setNapCycle(String napCycle) throws Exception {
        Select select = new Select(driver.findElement(By.id("strNabMthCod_1_0")));
        select.selectByVisibleText(napCycle);
        logger.info("납입 주기 :: {}", napCycle);
    }


    // 특약 선택
    protected void setSubTreaty(By id) throws Exception {
        element = driver.findElement(id);        // 비급여 도수·체외충격파·증식
        String tag = element.getAttribute("id");
        String selected = driver.findElement(By.cssSelector("label[for='" + tag + "']")).getText();

        if (!element.isSelected()) {
            element.click();
            logger.info(selected);
            WaitUtil.waitFor(2);
        }
    }


    // 보험료 계산하기
    protected void calcPremium(By id) throws Exception {
        element = driver.findElement(id);
        element.click();
        helper.waitForCSSElement("#uiPOPLoading1");
    }

    // 보험료 계산하기
    protected void calcPremiumAnnounce(By cssSelect) throws Exception {
        element = driver.findElement(cssSelect);
        element.click();
        WaitUtil.waitFor(4);
    }


    // 월보험료(연금)
    protected void setPremium(String premium) throws InterruptedException {
        element = driver.findElement(By.id("premiumMy"));
        element.clear();
        element.sendKeys(premium);
        WaitUtil.loading(2);
    }

    // 월보험료 가져오기
    protected void getPremium(By id, CrawlingProduct info) throws Exception {
        String premium = "";
        element = driver.findElement(id);
        premium = element.getAttribute("value").replaceAll("[^0-9]", "");
        logger.info("premium :: " + premium);
        info.treatyList.get(0).monthlyPremium = premium;
    }

    // 연금개시나이 set
    protected void setAnnuityAgeNm(String annAge) throws InterruptedException {
        element = driver.findElement(By.id("annuityAgeMy"));
        elements = element.findElements(By.tagName("option"));
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(annAge)) {
                option.click();
                WaitUtil.waitFor(2);
                break;
            }
        }
    }

    // 납입기간 set
    protected void setNapterm(CrawlingProduct info) throws Exception {
        boolean isClicked = false;
        element = driver.findElement(By.id("napTermMy"));
        elements = element.findElements(By.tagName("option"));
        String napTerm = info.napTerm.replace("년", "").replace("세", "");
        String nap = "";

        if (napTerm.equals("전기납")) {
            nap = "S0";
            // info.napTerm = info.annAge;
        } else {
            if (Integer.parseInt(napTerm) < 10) {
                nap = "Y00";
            } else {
                nap = "Y0";
            }
        }

        for (WebElement option : elements) {
            String optionValue = option.getAttribute("value");
            if (optionValue.equals(nap + napTerm)) {
                option.click();
                isClicked = true;
                WaitUtil.loading(2);
                break;
            }
        }

        if (!isClicked) {
            throw new Exception("납입기간: 선택할 요소가 없습니다.");
        }
    }

    // 가입금액 set
    protected void setInsuredAmount(By id, CrawlingProduct info) throws InterruptedException {
        Select assureMoney = new Select(driver.findElement(id));
        assureMoney.selectByValue(String.valueOf(Integer.parseInt(info.assureMoney) / 10000));
        WaitUtil.loading(2);
    }

    // 연금지급형태 set
    protected void setAnnuityPayType(String annuityPayType) throws Exception {
        driver.findElement(By.id("annuityPayTypeMy")).click();
        WaitUtil.waitFor(2);

        boolean exist = false;
        if(annuityPayType.contains("종신 10년")){
            driver.findElement(By.xpath("//select[@id='annuityPayTypeMy']//option[contains(.,'종신연금형(10년보증)')]")).click();
            exist = true;
        } else if(annuityPayType.contains("종신 20년")){
            driver.findElement(By.xpath("//select[@id='annuityPayTypeMy']//option[contains(.,'종신연금형(20년보증)')]")).click();
            exist = true;
        } else if(annuityPayType.contains("종신 30년")){
            driver.findElement(By.xpath("//select[@id='annuityPayTypeMy']//option[contains(.,'종신연금형(30년보증)')]")).click();
            exist = true;
        }

        if(exist = false){
            throw new Exception();
        }
        WaitUtil.waitFor(1);
    }

    // 연금지급주기 set
    protected void setAnnuityPayPeriod() throws InterruptedException {
        element = driver.findElement(By.id("annuityPayPeriodMy"));
        // 매년 고정
        element = element.findElements(By.tagName("option")).get(4);
        element.click();
        WaitUtil.waitFor(2);
    }

    // 다시 보험료 계산하기
    protected void reCalcPremium(By id) throws Exception {
        element = driver.findElement(id);
        element.click();
        helper.waitForCSSElement("#uiPOPLoading1");
    }

    protected void checkStatus() {
        try {
            if (driver.findElement(By.cssSelector(".pop-modal2.mes.open")).isDisplayed()) {
                logger.debug("[변경된 조건이 없습니다] 알럿 확인!!!");
                helper.click(By.id("popCloseBtn"));
            }
        } catch (Exception e) {
            logger.info("알럿표시 없음!!!");
        }
    }

    //연금 예상 수령액 설정
    protected void setAnnuityPremium(CrawlingProduct info) throws Exception {
        driver.findElement(By.id("annuityPayTypeMy")).click();
        WaitUtil.waitFor(1);
        driver.findElement(By.xpath("//select[@id='annuityPayTypeMy']//option[contains(.,'종신연금형(10년보증)')]")).click();
        reCalcPremium(By.id("reCalcPremium"));
        checkStatus();
        String annuityPremium = driver.findElement(By.id("returnPremiumMy")).getText().replace(",", "");
        String whl10y = String.valueOf(MoneyUtil.toDigitMoney(annuityPremium));

        driver.findElement(By.id("annuityPayTypeMy")).click();
        WaitUtil.waitFor(1);
        driver.findElement(By.xpath("//select[@id='annuityPayTypeMy']//option[contains(.,'종신연금형(20년보증)')]")).click();
        reCalcPremium(By.id("reCalcPremium"));
        checkStatus();
        annuityPremium = driver.findElement(By.id("returnPremiumMy")).getText().replace(",", "");
        String whl20y = String.valueOf(MoneyUtil.toDigitMoney(annuityPremium));

        driver.findElement(By.id("annuityPayTypeMy")).click();
        WaitUtil.waitFor(1);
        driver.findElement(By.xpath("//select[@id='annuityPayTypeMy']//option[contains(.,'종신연금형(30년보증)')]")).click();
        reCalcPremium(By.id("reCalcPremium"));
        checkStatus();
        annuityPremium = driver.findElement(By.id("returnPremiumMy")).getText().replace(",", "");
        String whl30y = String.valueOf(MoneyUtil.toDigitMoney(annuityPremium));

        if(info.annuityType.contains("종신 10년")){
            info.annuityPremium = whl10y;
        } else if(info.annuityType.contains("종신 20년")) {
            info.annuityPremium = whl20y;
        } else if(info.annuityType.contains("종신 30년")) {
            info.annuityPremium = whl30y;
        }

        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        planAnnuityMoney.setWhl10Y(whl10y);
        planAnnuityMoney.setWhl20Y(whl20y);
        planAnnuityMoney.setWhl30Y(whl30y);
        info.planAnnuityMoney = planAnnuityMoney ;

        if(whl10y.equals("") || whl10y.equals("0")){
            throw new Exception("종신 10년의 금액이 null이거나 0원입니다.");
        } else if(whl20y.equals("") || whl20y.equals("0")) {
            throw new Exception("종신 20년의 금액이 null이거나 0원입니다.");
        } else if(whl30y.equals("") || whl30y.equals("0")) {
            throw new Exception("종신 30년의 금액이 null이거나 0원입니다.");
        }

    }

        // 해약 환급금
    //해약환급금 - 납입 기간과 일치하는 경과기간을 찾아 예상 해약환급금 크롤링
    protected void getReturnPremium(CrawlingProduct info, By trList) throws Exception {
        element = driver.findElement(By.cssSelector("#showReturn1"));    // 해약환급금 버튼
        element.click();
        WaitUtil.waitFor(3);

        helper.waitVisibilityOfElementLocated(By.cssSelector("#selUiTabCon"));
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        elements = driver.findElements(By.cssSelector("#selUiTabCon > option"));
        boolean isContain = false;

        for (WebElement option : elements) {
            option.click();
            WaitUtil.waitFor(2);

            elements = driver.findElements(trList);

            for (int i = 0; i < elements.size(); i++) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                WebElement tr = elements.get(i);

                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);

                logger.info("경과기간 :: {}", term);
                logger.info("납입보혐료 누계 :: {}", premiumSum);

                if (isContain) {
                    planReturnMoney = planReturnMoneyList.get(i);
                }

                if (option.getText().contains("최저")) {
                    String return_money_min = tr.findElements(By.tagName("td")).get(2).getText();
                    String return_rate_min = tr.findElements(By.tagName("td")).get(3).getText();

                    planReturnMoney.setReturnMoneyMin(return_money_min);
                    planReturnMoney.setReturnRateMin(return_rate_min);

                    logger.info("예상 최저 해약환급금 :: {}", return_money_min);
                    logger.info("예상 최저 환급률 :: {}", return_rate_min);
                    logger.info("------------------------------------");


                } else if (option.getText().contains("평균")) {
                    String return_money_avg = tr.findElements(By.tagName("td")).get(2).getText();
                    String return_rate_avg = tr.findElements(By.tagName("td")).get(3).getText();

                    planReturnMoney.setReturnMoneyAvg(return_money_avg);
                    planReturnMoney.setReturnRateAvg(return_rate_avg);

                    logger.info("예상 평균 해약환급금 :: {}", return_money_avg);
                    logger.info("예상 평균 환급률 :: {}", return_rate_avg);
                    logger.info("------------------------------------");

                } else {
                    returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
                    returnRate = tr.findElements(By.tagName("td")).get(3).getText();

                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);

                    logger.info("예상 공시이율 해약환급금 :: {}", returnMoney);
                    logger.info("예상 공시이율 환급률 :: {}", returnRate);
                    logger.info("------------------------------------");
                }

                if (!isContain) {
                    planReturnMoneyList.add(planReturnMoney);
                }
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
            isContain = true;
        }

//      만기환급금
        info.returnPremium = planReturnMoneyList.get(planReturnMoneyList.size() - 1)
            .getReturnMoney().replace(",", "").replace("원", "");

        logger.info("만기환급금 :: " + info.returnPremium);
    }

    // 해약 환급금
    protected void getReturnPremium(CrawlingProduct info, String returnId, String returnLayer)
        throws
        InterruptedException {

        element = driver.findElement(By.id(returnId));
        element = element.findElement(By.xpath("parent::*"));
        try{
            element = element.findElement(By.linkText("해약환급금"));
        } catch (NoSuchElementException e){
            element = element.findElement(By.linkText("해약환급금"));
        }
        element.click();
        WaitUtil.loading(3);

        elements = driver.findElements(By.cssSelector(returnLayer + " > table > tbody > tr"));

        // 주보험 영역 Tr 개수만큼 loop
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        for (WebElement tr : elements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

            logger.info("경과기간 :: " + term);
            logger.info("납입보험료 누계:: " + premiumSum);
            logger.info("해약환급금 :: " + returnMoney);
            logger.info("환급률 :: " + premiumSum);
            logger.info("==================================");

            planReturnMoney.setTerm(term);    // 경과기간
            planReturnMoney.setPremiumSum(premiumSum);    // 납입보험료 누계
            planReturnMoney.setReturnMoney(returnMoney);    // 예상해약환급금
            planReturnMoney.setReturnRate(returnRate);    // 예상 환급률

            planReturnMoneyList.add(planReturnMoney);
            info.returnPremium = returnMoney.replace(",", "").replace("원", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        // 해약환급금 관련 End
    }

    // 월보험료 가져오기		DSS_D001
    protected void getPremium(CrawlingProduct info, By id) throws InterruptedException {
        String premium = "";
        element = driver.findElement(id);
        premium = element.getText().replace(",", "").replace("원", "");
        logger.info("월보험료 : " + premium);
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }

    // 보장기간
    protected void setInsterm(CrawlingProduct info, By id, By tagName) throws Exception {
        boolean isClicked = false;
        element = driver.findElement(id);
        elements = element.findElements(tagName);
        String insTerm = info.insTerm.replace("년", "").replace("세", "");
        String ins = "";

        if (insTerm.equals("전기납")) {
            ins = "S0";
            // info.insTerm = info.annAge;
        } else {
            if (Integer.parseInt(insTerm) < 10) {
                ins = "Y00";
            } else {
                ins = "Y0";
            }
        }

        for (WebElement option : elements) {
            String optionValue = option.getAttribute("value");
            if (optionValue.equals(ins + insTerm)) {
                option.click();
                isClicked = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!isClicked) {
            throw new Exception("보장기간: 선택할 요소가 없습니다.");
        }
    }

    protected void moveToElement(By location){
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(location);
        actions.moveToElement(element);
        actions.perform();
    }


    //확정 기간 연금 조회 (보류)
//    protected void getFixedAnnuityPremium(CrawlingProduct info) throws Exception {
//        doClick(By.xpath("//button[contains(text(), '확인')]"));    // 해약환급금 창 닫기
//        doClick(By.linkText("상품정보"));
//        WaitUtil.waitFor(1);
//        doClick(By.linkText("보장내용"));
//        WaitUtil.waitFor(1);
//
//        Actions actions = new Actions(driver);
//        WebElement element = driver.findElement(By.id("TbAntyExmf"));
//        actions.moveToElement(element);
//        actions.perform();
//        WaitUtil.loading(1);

    // 확정기간연금에서 10년 지급 금액을 공시이율 가정으로 설정했을 때
    // 1. 구분란에 공시이율 가정의 index값을 구하여 i에 저장 = 현재는 3
//        List<WebElement> thead = driver.findElements(By.cssSelector("#uiTabSection3_2 > div.wrap-scroll1 > table > thead > tr > th"));
//        int i = 0;
//        for (i = 0; i < thead.size(); i++) {
//            if (thead.get(i).getText().contains("공시이율 가정")) {
//                break;
//            }
//        }
    // 2. tbody 부분에서 확정기간연금과 10년지급을 만족하는 수령액들이 td로 되어있는데,
    //    필요한 공시이율에 대한 수령액은 세 번째칸(index = 0,1,2)에 있다.
    //    그렇기에 위에서 구한 i에 -1를 하여 index(i-1 = 2)를 구하여 값을 구하도록 설정      -mincheol
//        WebElement tbody = driver.findElement(By.cssSelector("#TbAntyExmf"));
//        List<WebElement> tbodyTr = tbody.findElements(By.tagName("tr"));
//        for (WebElement tbodyTd : tbodyTr) {
//            List<WebElement> th = tbodyTd.findElements(By.tagName("th"));
//            List<WebElement> td = tbodyTd.findElements(By.tagName("td"));
//            for (int idx = 0; idx <= th.size(); idx++) {
//                try {
//                    if (th.get(idx).getText().contains("확정기간연금") && th.get(idx+1).getText().contains("10년지급")) {
//                        info.fixedAnnuityPremium = td.get(i-1).getText().replaceAll("[^0-9]","") + "000";
//                    }
//
//                } catch (IndexOutOfBoundsException e){
//                    continue;
//                }
//            }
//        }
//        logger.info("확정기간연금 수령액 (10년) :: " +info.fixedAnnuityPremium+"원") ;
//    }

//	// 납입주기
//	protected void setNapPeriod(String napPeriod) throws InterruptedException {
//		element = driver.findElement(By.id("napPeriod"));
//		elements = element.findElements(By.tagName("option"));
//
//		for (WebElement option : elements) {
//			if (option.getAttribute("value").equals(napPeriod)) {
//				option.click();
//				WaitUtil.waitFor();
//				break;
//			}
//		}
//	}
//
//	// 실손계약 종류
//	protected void setMedicalType(String insuName) throws InterruptedException {
//		String type = "";
//
//		if (insuName.contains("표준형")) {
//			type = "A";
//		} else {
//			type = "B";
//		}
//
//		element = driver.findElement(By.id("medicalType"));
//		elements = element.findElements(By.tagName("option"));
//
//		for (WebElement option : elements) {
//			if (option.getAttribute("value").equals(type)) {
//				option.click();
//				WaitUtil.waitFor();
//				break;
//			}
//		}
//	}
//
//	// 실손플랜 종류
//	protected void setPlanType(String insuName) throws InterruptedException {
//		String typeValue = "";
//
//		if (insuName.contains("종합플랜")) {
//			typeValue = "3";
//		} else if (insuName.contains("질병플랜")) {
//			typeValue = "1";
//		} else if (insuName.contains("상해플랜")) {
//			typeValue = "2";
//		}
//
//		element = driver.findElement(By.id("planType"));
//		elements = element.findElements(By.tagName("option"));
//		for (WebElement option : elements) {
//			if (option.getAttribute("value").equals(typeValue)) {
//				option.click();
//				WaitUtil.waitFor();
//				break;
//			}
//		}
//	}
//
//	// 해약환급금 조회
//	protected void showReturn(CrawlingProduct info) throws InterruptedException {
//		String returnPremium = "";
//		String returnPremiumMy = "";
//		String year = "";
//		int getReturnYear = 0;
//
//		getReturnYear = Integer.parseInt(info.annAge) - Integer.parseInt(info.age);
//
//		returnPremiumMy = driver.findElement(By.id("returnPremiumMy")).getText();
//		returnPremiumMy = returnPremiumMy.replace(",", "").replace("원", "");
//
//		element = driver.findElement(By.id("showReturn1"));
//		element.click();
//		WaitUtil.waitFor();
//
//		element = driver.findElement(By.id("uiTabCon2-1"));
//		element = element.findElement(By.tagName("tbody"));
//		elements = element.findElements(By.cssSelector("tr"));
//
//		String height = "0";
//		int num = elements.size();
//
//		for (int i = 0; i < num; i++) {
//			year = elements.get(i).findElements(By.tagName("td")).get(0).getText();
//			if ("".equals(year)) {
//				i = i - 1;
//				height = String.valueOf(Integer.parseInt(height) + 250);
//				((JavascriptExecutor) driver).executeScript("$('#mCSB_4_container').attr('style', 'position: relative; top:-" + height + "px; left:0px;');");
//				Thread.sleep(200);
//			}
//			if (year.equals(String.valueOf(getReturnYear) + "년")) {
//				returnPremium = elements.get(i).findElements(By.tagName("td")).get(2).getText().replace(",", "").replace("원", "");
//				break;
//			}
//		}
//		info.savePremium = info.assureMoney;
//		info.treatyList.get(0).monthlyPremium = "0";
//		info.errorMsg = "";
//		info.returnPremium = returnPremium;
//		info.annuityPremium = returnPremiumMy;
//	}
}
