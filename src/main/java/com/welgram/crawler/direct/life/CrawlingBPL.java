package com.welgram.crawler.direct.life;

import com.welgram.common.MoneyUtil;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.*;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanCalc;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public abstract class CrawlingBPL extends SeleniumCrawler implements Scrapable {

    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(WebElement element, String text) {
//		element.click();
//		element.clear();
//		element.sendKeys(text);
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        element.sendKeys(text);

    }

    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOption(By element, String text) throws Exception {
        WebElement selectEl =  driver.findElement(element);
        selectOption(selectEl, text);
    }

    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOption(WebElement selectEl, String text) throws Exception {
        Select select = new Select(selectEl);

        try {
            select.selectByVisibleText(text);
        }catch (NoSuchElementException e) {
            throw new NoSuchElementException("selectbox에서 해당 text(" + text + ")를 찾을 수 없습니다");
        }
    }

    //select box에서 text와 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(By element, String text) throws Exception{
//        Select select = new Select(driver.findElement(element));
//        select.selectByVisibleText(text);
        selectOption(element, text);

    }


    //select box에서 text와 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(WebElement element, String text) throws Exception{
//        Select select = new Select(driver.findElement(element));
//        select.selectByVisibleText(text);
        selectOption(element, text);

    }


    //select box에서 value값이 일치하는 option 클릭하는 메서드
    protected void selectOptionByValue(WebElement selectEl, String value) throws Exception{
        Select select = new Select(selectEl);
        select.selectByValue(value);
    }



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


    protected void waitAnnouncePageLoadingBar() throws Exception {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ui-loading")));
    }


    protected void printAndCompare(String title, String welgramData, String targetData) throws Exception {

        //가입설계 정보와 원수사 정보 출력
        logger.info("가입설계 {} : {}", title, welgramData);
        logger.info("홈페이지 {} : {}", title, targetData);
        logger.info("======================================================");

        if(!welgramData.equals(targetData)) {
            throw new Exception(title + " 불일치");
        }
    }

    public void loading() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".roadingPopup")));
    }


    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        String title = "생년월일";
        String welgramBirth = (String) obj;

        try {
            //생년월일 입력
            WebElement input = driver.findElement(By.id("brth"));
            waitElementToBeClickable(input).click();
            setTextToInputBox(input, welgramBirth);

            //실제로 입력된 생년월일 읽어오기
            String script = "return $(arguments[0]).val();";
            String targetBirth = String.valueOf(executeJavascript(script, input));

            //비교
            printAndCompare(title, welgramBirth, targetBirth);
        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }

    }

    @Override
    public void setGenderNew(Object obj) throws SetGenderException {
        String title = "성별";
        int welgramGender = (int) obj;
        String welgramGenderText = welgramGender == MALE ? "남자" : "여자";


        try {
            //성별 입력
            WebElement label = driver.findElement(By.xpath("//li[@class='gender']//span[text()='" + welgramGenderText + "']/parent::label"));
            waitElementToBeClickable(label).click();
            waitAnnouncePageLoadingBar();

        } catch (Exception e) {
            throw new SetGenderException(e.getMessage());
        }
    }

    @Override
    public void setJobNew(Object obj) throws SetJobException {

    }


    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {

    }

    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {

    }

    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException {
        String title = "납입주기";
        String welgramNapCycle = (String) obj;

        try {

            //납입주기 클릭
            WebElement select = driver.findElement(By.xpath("//select[@name='pmCyl']"));
            selectOptionByText(select, welgramNapCycle);


            //실제로 클릭된 납입주기 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetNapCycle = String.valueOf(executeJavascript(script, select));

            //비교
            printAndCompare(title, welgramNapCycle, targetNapCycle);

        } catch (Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }
    }

    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {

    }


    @Override
    public void setRefundTypeNew(Object obj) throws SetRefundTypeException {

    }

    @Override
    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException {

    }

    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj;

        List<WebElement> $trList = driver.findElements(By.xpath("//div[@id='trmRview']//table/tbody/tr"));
        for(WebElement $tr : $trList) {
            String term = $tr.findElement(By.xpath("./td[1]")).getText();
            String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText();
            String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText();
            String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();
            String returnMoneyAvg = $tr.findElement(By.xpath("./td[5]")).getText();
            String returnRateAvg = $tr.findElement(By.xpath("./td[6]")).getText();
            String returnMoneyMin = $tr.findElement(By.xpath("./td[7]")).getText();
            String returnRateMin = $tr.findElement(By.xpath("./td[8]")).getText();
            returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));
            returnMoneyAvg = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg));
            returnMoneyMin = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin));


            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("공시환급금 : {}", returnMoney);
            logger.info("공시환급률 : {}", returnRate);
            logger.info("평균환급금 : {}", returnMoneyAvg);
            logger.info("평균환급률 : {}", returnRateAvg);
            logger.info("최저환급금 : {}", returnMoneyMin);
            logger.info("최저환급률 : {}", returnRateMin);
            logger.info("==========================================");

            PlanReturnMoney p = new PlanReturnMoney();
            p.setTerm(term);
            p.setPremiumSum(premiumSum);
            p.setReturnMoney(returnMoney);
            p.setReturnRate(returnRate);
            p.setReturnMoneyAvg(returnMoneyAvg);
            p.setReturnRateAvg(returnRateAvg);
            p.setReturnMoneyMin(returnMoneyMin);
            p.setReturnRateMin(returnRateMin);


            //만기환급금 세팅
            info.returnPremium = returnMoney;
            info.planReturnMoneyList.add(p);
        }

        logger.info("만기환급금 : {}", info.returnPremium);
    }

    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {

    }



    //해당 element가 존재하는지 여부를 리턴
    private boolean existElement(By element) {

        boolean isExist = true;

        try {
            driver.findElement(element);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }


    //해당 element가 존재하는지 여부를 리턴
    protected boolean existElement(WebElement rootEl, By element) {

        boolean isExist = true;

        try {
            rootEl.findElement(element);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }

    protected void setTreaties(CrawlingProduct info) throws Exception {

        List<CrawlingTreaty> welgramTreaties = info.treatyList;
        CrawlingTreaty specialTreaty = null;

        for(CrawlingTreaty welgramTreaty : welgramTreaties) {

            //매번 가입금액이 바뀌는 특약에 대해서는 가설에서 0만원 특약으로 넘어온다.
            if(welgramTreaty.assureMoney == 0) {
                specialTreaty = welgramTreaty;
            }

            //선택특약일 경우에만 특약 세팅
            if(welgramTreaty.productGubun.equals(CrawlingTreaty.ProductGubun.선택특약)) {
                String treatyName = welgramTreaty.treatyName;
                int treatyAssureMoney = welgramTreaty.assureMoney;
                String treatyInsTerm = welgramTreaty.insTerm;
                String treatyNapTerm = welgramTreaty.napTerm;
                String unitText = "";
                String toSetAssureMoney = "";


                WebElement $tr = driver.findElement(By.xpath("//span[text()='" + treatyName +"']/ancestor::tr[1]"));
                WebElement $joinInput = $tr.findElement(By.xpath(".//input[@name='pdtScnCd']"));
                WebElement $insTerm = $tr.findElement(By.xpath(".//select[@name='pdtScnCd_isPd']"));
                WebElement $napTerm = $tr.findElement(By.xpath(".//select[@name='pdtScnCd_paPd']"));
                WebElement $assureMoneyInput = $tr.findElement(By.xpath(".//input[@name='pdtScnCd_sbcAmt']"));
                unitText = $assureMoneyInput.findElement(By.xpath("./following-sibling::i")).getText();


                //가입금액 단위 설정
                int unit = 1;
                switch (unitText) {
                    case "억원":
                        unit = 100000000;
                        break;
                    case "천만원":
                        unit = 10000000;
                        break;
                    case "백만원":
                        unit = 1000000;
                        break;
                    case "십만원":
                        unit = 100000;
                        break;
                    case "만원":
                        unit = 10000;
                        break;
                    case "천원":
                        unit = 1000;
                        break;
                    case "백원":
                        unit = 100;
                        break;
                    case "십원":
                        unit = 10;
                        break;
                    case "원":
                        unit = 1;
                        break;
                }
                toSetAssureMoney = String.valueOf(treatyAssureMoney / unit);


                //가입 체크박스 선택
                if(!$joinInput.isSelected()) {
//					WebElement label = $joinInput.findElement(By.xpath("./parent::label"));
//					waitElementToBeClickable(label).click();
//					waitElementToBeClickable($joinInput).click();
                    String script = "arguments[0].click();";
                    executeJavascript(script, $joinInput);


                    //특약 체크하면서 연계특약에 대한 alert 창이 뜨는 경우가 있음
                    boolean isExist = existElement(By.xpath("//button[@class='btn btn-confirm']"));
                    if(isExist) {
                        driver.findElement(By.xpath("//button[@class='btn btn-confirm']")).click();
                    }

                }

                //보험기간 선택
                String toSetInsTerm = treatyInsTerm + "만기";
                selectOptionByText($insTerm, toSetInsTerm);


                //납입기간 선택
                String toSetNapTerm = "";
                try {
                    //특약의 보험기간과 납입기간이 같으면 납입기간을 "전기납"으로 치환.
                    toSetNapTerm = (treatyInsTerm.equals(treatyNapTerm)) ? "전기납" : treatyNapTerm + "납";
                    selectOptionByText($napTerm, toSetNapTerm);
                } catch(Exception e) {
                    //특약별로 보험기간과 납입기간이 같은경우 전기납으로 표기된 경우도 있고 아닌 경우도 있음.
                    //전기납으로 납입기간을 못찾은 경우는 그냥 납입기간으로 찾아보기
                    toSetNapTerm = treatyNapTerm.contains("납") ? treatyNapTerm : treatyNapTerm + "납";
                    selectOptionByText($napTerm, toSetNapTerm);
                }


                //가입금액 설정
                setTextToInputBox($assureMoneyInput, toSetAssureMoney);


            }

        }



        logger.info("보험료 계산 버튼 클릭");
        element = driver.findElement(By.xpath("//div[@class='pbt']//button[text()='보험료 계산']"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();


        logger.info("매번 가입금액 바뀌는 특약 가입금액 크롤링");
        String treatyName = specialTreaty.treatyName;
        WebElement $tr = driver.findElement(By.xpath("//span[text()='" + treatyName + "']/ancestor::tr[1]"));
        WebElement $assureMoneyInput = $tr.findElement(By.xpath(".//input[@name='pdtScnCd_sbcAmt']"));
        String unitText = $assureMoneyInput.findElement(By.xpath("./following-sibling::i")).getText();

        //실제 가입금액 크롤링
        String script = "return $(arguments[0]).val();";
        String targetAssureMoney = String.valueOf(executeJavascript(script, $assureMoneyInput));
        targetAssureMoney = targetAssureMoney + unitText;
        targetAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetAssureMoney));


        //특약 계산테이블에 세팅
        PlanCalc planCalc = new PlanCalc();
        planCalc.setMapperId(Integer.parseInt(specialTreaty.mapperId));
        planCalc.setInsAge(Integer.parseInt(info.getAge()));
        planCalc.setGender(info.gender == MALE ? "M" : "F");
        planCalc.setAssureMoney(targetAssureMoney);
        specialTreaty.setPlanCalc(planCalc);



//		List<CrawlingTreaty> targetTreaties = new ArrayList<>();
//
//
//		//가입설계 특약정보와 원수사 특약정보 비교
//		logger.info("가입하는 특약은 총 {}개입니다.", targetTreaties.size());
//
//		boolean result = compareTreaties(targetTreaties, welgramTreaties);
//
//
//		if(result) {
//			logger.info("특약 정보 모두 일치 ^^");
//		} else {
//			throw new Exception("특약 불일치");
//		}

    }




    protected boolean compareTreaties(List<CrawlingTreaty> targetTreaties, List<CrawlingTreaty> welgramTreatyList) throws Exception {
        boolean result = true;

        List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
        List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트
        List<String> samedTreatyNameList = null;				//가입설계와 홈페이지 둘 다 일치하는 특약명 리스트


        //홈페이지 특약명 리스트
        List<String> targetTreatyNameList = new ArrayList<>();
        List<String> copiedTargetTreatyNameList = null;
        for(CrawlingTreaty t : targetTreaties) {
            targetTreatyNameList.add(t.treatyName);
        }
        copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);


        //가입설계 특약명 리스트
        List<String> welgramTreatyNameList = new ArrayList<>();
        List<String> copiedWelgramTreatyNameList = null;
        for(CrawlingTreaty t : welgramTreatyList) {
            welgramTreatyNameList.add(t.treatyName);
        }
        copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);




        //일치하는 특약명만 추림
        targetTreatyNameList.retainAll(welgramTreatyNameList);
        samedTreatyNameList = new ArrayList<>(targetTreatyNameList);
        targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);



        //가입설계에 추가해야하는 특약명만 추림
        targetTreatyNameList.removeAll(welgramTreatyNameList);
        toAddTreatyNameList = new ArrayList<>(targetTreatyNameList);
        targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);



        //가입설계에서 제거해야하는 특약명만 추림
        welgramTreatyNameList.removeAll(targetTreatyNameList);
        toRemoveTreatyNameList = new ArrayList<>(welgramTreatyNameList);
        welgramTreatyNameList = new ArrayList<>(copiedWelgramTreatyNameList);



        //특약명이 일치하는 경우에는 가입금액을 비교해준다.
        for(String treatyName : samedTreatyNameList) {
            CrawlingTreaty targetTreaty = getCrawlingTreaty(targetTreaties, treatyName);
            CrawlingTreaty welgramTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);

            int targetTreatyAssureMoney = targetTreaty.assureMoney;
            int welgramTreatyAssureMoney = welgramTreaty.assureMoney;


            //가입금액 비교
            if(targetTreatyAssureMoney == welgramTreatyAssureMoney) {
                //금액이 일치하는 경우
                logger.info("특약명 : {} | 가입금액 : {}원", treatyName, welgramTreatyAssureMoney);
            } else {
                //금액이 불일치하는 경우 특약정보 출력
                result = false;

                logger.info("[불일치 특약]");
                logger.info("특약명 : {}", treatyName);
                logger.info("가입설계 가입금액 : {}", welgramTreatyAssureMoney);
                logger.info("홈페이지 가입금액 : {}", targetTreatyAssureMoney);
                logger.info("==============================================================");
            }
        }


        //가입설계 추가해야하는 특약정보 출력
        if(toAddTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
            logger.info("==============================================================");

            for(int i=0; i<toAddTreatyNameList.size(); i++) {
                String treatyName = toAddTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(targetTreaties, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }

        }



        //가입설계 제거해야하는 특약정보 출력
        if(toRemoveTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
            logger.info("==============================================================");

            for(int i=0; i<toRemoveTreatyNameList.size(); i++) {
                String treatyName = toRemoveTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(welgramTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }
        }


        return result;
    }



    private CrawlingTreaty getCrawlingTreaty(List<CrawlingTreaty> treatyList, String treatyName) {
        CrawlingTreaty result = null;

        for(CrawlingTreaty treaty : treatyList) {
            if(treaty.treatyName.equals(treatyName)) {
                result = treaty;
            }
        }

        return result;
    }





    protected void setPlan(CrawlingProduct info) throws Exception {
        boolean result = false;
        String plan = info.textType;

        logger.info(plan + " 찾는 중...");

        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#insCltpCd option"));
        for (WebElement option : elements) {
            if (option.getText().equals(plan)) {
                logger.info(option.getText() + " 선택");
                option.click();
                result = true;
                break;
            }
        }

        if (!result) {
            throw new Exception("선택할 항목이 없습니다.");
        }
    }

    protected void setCycle() {
        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#iPayFreqN option"));
        for (WebElement option : elements) {
            if (option.getText().equals("월납")) {
                logger.info(option.getText() + " 선택");
                option.click();

                break;
            }
        }
    }

    protected void setOptions() {
        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("input[type='checkbox']"));
        for (WebElement option : elements) {

            option.click();

        }
    }


    protected void setName() {
        sendKeys(By.id("user_name"), "정교보");
    }

    protected void setBirth(CrawlingProduct info) {
        sendKeys(By.id("user_birth"), info.fullBirth);
    }

    protected void setGender(CrawlingProduct info) {

        if(info.getGender() == 0){
            driver.findElement(By.id("male")).click();
        }else{
            driver.findElement(By.id("female")).click();
        }
    }

    protected void sendKeys(By by, String keys) {
        element = helper.waitElementToBeClickable(by);
        element.click();
        element.clear();
        element.sendKeys(keys);
    }

    protected void sendKeys(WebElement webElement, String keys) {
        element = helper.waitElementToBeClickable(webElement);
        element.click();
        element.clear();
        element.sendKeys(keys);
    }

    protected void radioBtn(By by, String value) {
        elements = helper.waitVisibilityOfAllElementsLocatedBy(by);
        for (WebElement input : elements) {
            if (input.getAttribute("value").equals(value)) {
                input.click();
                break;
            }
        }
    }
}
