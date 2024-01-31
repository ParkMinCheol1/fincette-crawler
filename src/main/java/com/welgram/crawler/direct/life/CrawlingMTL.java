package com.welgram.crawler.direct.life;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.NotFoundValueInSelectBoxException;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CrawlingMTL extends SeleniumCrawler implements Scrapable {

    public static final Logger logger = LoggerFactory.getLogger(CrawlingMTL.class);


    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        String title = "생년월일";
        String welgramBirth = (String) obj;

        try {
            logger.info("{} 입력", title);
            WebElement input = driver.findElement(By.id("default_jumin1"));
            setTextToInputBox(input, welgramBirth);


            logger.info("현재 입력된 {} 값 조회", title);
            String script = "return $(arguments[0]).val();";
            String targetBirth = String.valueOf(executeJavascript(script, input));


            logger.info("{} 비교", title);
            printAndCompare(title, welgramBirth, targetBirth);
        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }
    }

    @Override
    public void setGenderNew(Object obj) throws SetGenderException {

        String title = "성별";
        String welgramGender = (String) obj;

        try {
            logger.info("{} 입력", title);
            WebElement input = driver.findElement(By.name("jumin2"));
            setTextToInputBox(input, welgramGender);


            logger.info("현재 입력된 {} 값 조회", title);
            String script = "return $(arguments[0]).val();";
            String targetGender = String.valueOf(executeJavascript(script, input));


            logger.info("{} 비교", title);
            printAndCompare(title, welgramGender, targetGender);
        } catch (Exception e) {
            throw new SetGenderException(e.getMessage());
        }
;
    }

    @Override
    public void setJobNew(Object obj) throws SetJobException {

    }

    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {
        String title = "보험기간";
        String welgramInsTerm = (String) obj;

        try {

            logger.info("{} 입력", title);
            WebElement select = driver.findElement(By.id("compIsprd"));
            selectOptionByText(select, welgramInsTerm);


            logger.info("현재 선택된 {} 값 조회", title);
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetInsTerm = String.valueOf(executeJavascript(script, select));


            logger.info("{} 비교", title);
            printAndCompare(title, welgramInsTerm, targetInsTerm);

        } catch(Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }

    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {
        String title = "납입기간";
        String welgramNapTerm = (String) obj;
        welgramNapTerm = (welgramNapTerm.contains("납")) ? welgramNapTerm : welgramNapTerm + "납";

        try {

            logger.info("{} 입력", title);
            WebElement select = driver.findElement(By.id("compRvpd"));
            selectOptionByText(select, welgramNapTerm);


            logger.info("현재 선택된 {} 값 조회", title);
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetNapTerm = String.valueOf(executeJavascript(script, select));


            logger.info("{} 비교", title);
            printAndCompare(title, welgramNapTerm, targetNapTerm);

        } catch(Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }

    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException {
        String title = "납입주기";
        String welgramNapCycle = (String) obj;

        try {

            logger.info("{} 입력", title);
            WebElement select = driver.findElement(By.id("compProdRvcy"));
            selectOptionByText(select, welgramNapCycle);


            logger.info("현재 선택된 {} 값 조회", title);
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetNapCycle = String.valueOf(executeJavascript(script, select));


            logger.info("{} 비교", title);
            printAndCompare(title, welgramNapCycle, targetNapCycle);

        } catch(Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }
    }

    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {
        String title = "주계약 가입금액";
        String welgramAssureMoney = (String) obj;
        String toSetAssureMoney = "";

        try {

            WebElement input = driver.findElement(By.id("compPaymentPrice"));
            WebElement span = input.findElement(By.xpath("./parent::span/following-sibling::span"));
            String unitText = span.getText().trim();

            int unit = 0;
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

            toSetAssureMoney = String.valueOf((Integer.parseInt(welgramAssureMoney) / unit));


            logger.info("{} 입력", title);
            setTextToInputBox(input, toSetAssureMoney);


            logger.info("현재 선택된 {} 값 조회", title);
            String script = "return $(arguments[0]).val();";
            String targetAssureMoney = String.valueOf(executeJavascript(script, input));
            targetAssureMoney = targetAssureMoney.replaceAll("[^0-9]", "");
            targetAssureMoney = String.valueOf(Integer.parseInt(targetAssureMoney) * unit);


            logger.info("{} 비교", title);
            printAndCompare(title, welgramAssureMoney, targetAssureMoney);

        } catch(Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }

    @Override
    public void setRefundTypeNew(Object obj) throws SetRefundTypeException {

    }

    @Override
    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException {
        WebElement element = null;
        CrawlingTreaty mainTreaty = (CrawlingTreaty) obj;

        try {
            element = waitPresenceOfElementLocated(By.cssSelector("#step3 > div:nth-child(7) > div > p"));

            String monthlyPremium = element.getText().replaceAll("[^0-9]", "");
            mainTreaty.monthlyPremium = monthlyPremium;

            if("0".equals(mainTreaty.monthlyPremium)) {
                throw new Exception("주계약 보험료 세팅은 필수입니다.");
            } else {
                logger.info("보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }

    }

    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj;

        try {
            logger.info("해약환급금 보기 버튼 클릭");
            WebElement element = driver.findElement(By.id("openSurr"));
            waitElementToBeClickable(element).click();


            //창 전환
            currentHandle = driver.getWindowHandle();
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);


            //홈페이지 해약환급금 금액표기 단위 읽기
            element = waitPresenceOfElementLocated(By.xpath("//p[@class='txtPos']"));
            String unit = element.getText();
            int unitNum = 1;
            int startIdx = unit.indexOf("단위");
            int endIdx = unit.lastIndexOf(",");
            unit = unit.substring(startIdx, endIdx);


            if(unit.contains("억원")) {
                unitNum = 100000000;
            } else if(unit.contains("천만원")) {
                unitNum = 10000000;
            } else if(unit.contains("만원")) {
                unitNum = 10000;
            }



            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            List<WebElement> $trList = driver.findElements(By.xpath("//tbody//tr"));
            for(WebElement $tr : $trList) {
                String term = $tr.findElement(By.xpath("./th[1]")).getText();
                String premiumSum = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tr.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
                String returnRate = $tr.findElement(By.xpath("./td[5]")).getText();

                premiumSum = String.valueOf(Long.parseLong(premiumSum) * unitNum);
                returnMoney = String.valueOf(Long.parseLong(returnMoney) * unitNum);

                logger.info("====================");
                logger.info("경과기간 : {}", term);
                logger.info("합계보험료 : {}", premiumSum);
                logger.info("해약환급금 : {}", returnMoney);
                logger.info("환급률 : {}", returnRate);

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney;
            }

            info.planReturnMoneyList = planReturnMoneyList;

            if(info.treatyList.get(0).productKind == ProductKind.순수보장형) {
                info.returnPremium = "0";
            }

            logger.info("보험기간 만료시 만기환급금 : {}원", info.returnPremium);
        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }

    }

    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {

    }

    //inputBox에 text 입력하는 메서드
    protected void setTextToInputBox(By id, String text) {
        WebElement element = driver.findElement(id);
        element.clear();
        element.sendKeys(text);
    }

    //inputBox에 text 입력하는 메서드
    protected void setTextToInputBox(WebElement element, String text) {
        element.click();
        element.clear();
        element.sendKeys(text);
    }


    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOptionByText(By element, String text) throws Exception {
        WebElement selectEl =  driver.findElement(element);
        selectOptionByText(selectEl, text);
    }

    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOptionByText(WebElement selectEl, String text) throws Exception{
        Select select = new Select(selectEl);

        try {
            select.selectByVisibleText(text);
        }catch (NoSuchElementException e) {
            throw new NoSuchElementException("selectbox에서 해당 text(" + text + ")를 찾을 수 없습니다");
        }
    }


    //select 박스에서 value로 option을 선택하는 메서드
    protected void selectOptionByValue(WebElement selectEl, String value) throws Exception {
        Select select = new Select(selectEl);

        try {
            select.selectByValue(value);
        } catch (NoSuchElementException e) {
            throw new NotFoundValueInSelectBoxException("selectBox에서 해당 value('" + value + "')값을 찾을 수 없습니다.");
        }

    }


    //홈페이지 성별 설정 메서드
    protected void setHomepageGender(int gender) throws Exception{
        String genderText = (gender == MALE) ? "남" : "여";

        //1. 성별 클릭
        element = driver.findElement(By.xpath("//label[text()='" + genderText + "']"));
        waitElementToBeClickable(element).click();


        //2. 실제 홈페이지에서 클릭된 성별 확인
        String script = "return $(\"input[name='gender']:checked\").attr('id')";
        String checkedElId = String.valueOf(executeJavascript(script));
        String checkedGender = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();
        logger.info("============================================================================");
        logger.info("가입설계 성별 : {}", genderText);
        logger.info("홈페이지에서 클릭된 성별 : {}", checkedGender);
        logger.info("============================================================================");

        if(!checkedGender.equals(genderText)) {
            logger.error("가입설계 성별 : {}", genderText);
            logger.error("홈페이지에서 클릭된 성별 : {}", checkedGender);
            throw new Exception("성별 불일치");
        } else {
            logger.info("result :: 가입설계 성별({}) == 홈페이지에서 클릭된 성별({})", genderText, checkedGender);
            logger.info("============================================================================");
        }
    }


    //공시실 생년월일 및 성별 설정
    protected void setAnnounceBirthAndGender(String fullBirth, int gender) throws Exception {
        logger.info("주민등록번호 설정");
        int year = Integer.parseInt(fullBirth.substring(0, 4));
        String genderNum = "";
        if(year >= 2000) {
            genderNum = (gender == MALE) ? "3" : "4";
        } else {
            genderNum = (gender == MALE) ? "1" : "2";
        }

        setTextToInputBox(By.cssSelector("input[name='jumin1']"), fullBirth.substring(2));
        setTextToInputBox(By.cssSelector("input[name='jumin2']"), genderNum);
    }

    //공시실 직업 설정
    protected void setAnnounceJob() throws Exception {
        logger.info("검색 버튼 클릭");

        //검색 버튼이 클릭 가능한 상태가 될 때까지 대기한 후에 클릭
        WebElement element = driver.findElement(By.linkText("검색"));
        waitElementToBeClickable(element).click();

        //창 전환
        currentHandle = driver.getWindowHandle();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);


        //직업을 직접 검색해서 찾는 방식은 selenium으로 돌릴때 에러가 발생함.
        //직접 대분류와 중분류를 클릭해 직업을 탐색 시켜야 함
        logger.info("대분류 : 전문가 및 관련 종사자(고정) 클릭");
        selectOptionByText(By.id("jobCode1"), "전문가 및 관련 종사자");
        WaitUtil.waitFor(2);

        logger.info("중분류 : 초등학교 교사(고정) 클릭");
        selectOptionByText(By.id("jobCode2"), "초등학교 교사");
        WaitUtil.waitFor(3);


        //직업 코드가 나와있는 란은 iframe으로 되어있기 때문에 해당 frame으로 전환한 후에 직업 코드를 클릭해야 함
        logger.info("직업코드 버튼 클릭");
        driver.switchTo().frame(0);

        //반드시 iframe에서 body 태그로 포커스를 맞춘 상태에서 하위 a태그를 찾아야함. 안그러면 예외 발생
        element = driver.findElement(By.xpath("//body"));
        element = element.findElement(By.xpath("//tbody[@id='jobDtl']//a"));
        waitElementToBeClickable(element).click();

        //창 전환
        driver.switchTo().window(currentHandle);
    }

    //공시실 보험기간 설정 메서드
    protected void setAnnounceInsTerm(String insTerm) throws Exception {
        selectOptionByText(By.id("compIsprd"), insTerm);
        waitAnnouncePageLoadingBar();

        String script = "return $('#compIsprd option:selected').text();";
        String checkedInsTerm = executeJavascript(script).toString();

        logger.info("======================================================");
        logger.info("가입설계 보험기간 : {}", insTerm);
        logger.info("홈페이지 클릭된 보험기간 : {}", checkedInsTerm);
        logger.info("======================================================");

        if(insTerm.equals(checkedInsTerm)) {
            logger.info("가입설계 보험기간 : {} == 홈페이지 클릭된 보험기간 : {}", insTerm, checkedInsTerm);
        } else {
            logger.info("가입설계 보험기간 : {} ≠ 홈페이지 클릭된 보험기간 : {}", insTerm, checkedInsTerm);
            throw new Exception("보험기간 불일치");
        }
    }

    //공시실 납입기간 설정 메서드
    protected void setAnnounceNapTerm(String napTerm) throws Exception {
        napTerm = napTerm.contains("납") ? napTerm : napTerm + "납";

        selectOptionByText(By.id("compRvpd"), napTerm);
        waitAnnouncePageLoadingBar();

        String script = "return $('#compRvpd option:selected').text();";
        String checkedNapTerm = executeJavascript(script).toString();

        logger.info("======================================================");
        logger.info("가입설계 납입기간 : {}", napTerm);
        logger.info("홈페이지 클릭된 납입기간 : {}", checkedNapTerm);
        logger.info("======================================================");

        if(napTerm.equals(checkedNapTerm)) {
            logger.info("가입설계 납입기간 : {} == 홈페이지 클릭된 납입기간 : {}", napTerm, checkedNapTerm);
        } else {
            logger.info("가입설계 납입기간 : {} ≠ 홈페이지 클릭된 납입기간 : {}", napTerm, checkedNapTerm);
            throw new Exception("납입기간 불일치");
        }
    }

    //공시실 납입주기 설정 메서드
    protected void setAnnounceNapCycle(String napCycle) throws Exception {
        selectOptionByText(By.id("compProdRvcy"), napCycle);
        waitAnnouncePageLoadingBar();

        String script = "return $('#compProdRvcy option:selected').text();";
        String checkedNapCycle = executeJavascript(script).toString();

        logger.info("======================================================");
        logger.info("가입설계 납입주기 : {}", napCycle);
        logger.info("홈페이지 클릭된 납입주기 : {}", checkedNapCycle);
        logger.info("======================================================");

        if(napCycle.equals(checkedNapCycle)) {
            logger.info("가입설계 납입주기 : {} == 홈페이지 클릭된 납입주기 : {}", napCycle, checkedNapCycle);
        } else {
            logger.info("가입설계 납입주기 : {} ≠ 홈페이지 클릭된 납입주기 : {}", napCycle, checkedNapCycle);
            throw new Exception("납입주기 불일치");
        }
    }

    //공시실 납입기간 설정 메서드
    protected void setAnnounceAnnuityAge(String annAge) throws CommonCrawlerException {
        String title = "연금개시나이";
        annAge = (annAge.contains("세")) ? annAge : annAge + "세";

        try {

            WebElement $select = driver.findElement(By.id("compIsprd"));
            selectOptionByText($select, annAge);
            waitAnnouncePageLoadingBar();

            String script = "return $(arguments[0]).find('option:selected').text();";
            String selectedAnnAge = executeJavascript(script, $select).toString();

            //비교
            printLogAndCompare(title, annAge, selectedAnnAge);

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ANNUITY_AGE);
        }
    }

    //공시실 가입금액 설정 메서드
    protected void setAnnounceAssureMoney(int assureMoney) throws Exception {
        Long _assureMoney = MoneyUtil.toDigitMoney(String.valueOf(assureMoney));

        WebElement input = driver.findElement(By.id("compPaymentPrice"));
        WebElement span = input.findElement(By.xpath("./ancestor::div[@class='inputTxt']/span[position()='2']"));

        String unit = span.getText();
        if("억원".equals(unit)) {
            _assureMoney = _assureMoney / 100000000;
        } else if("천만원".equals(unit)) {
            _assureMoney = _assureMoney / 10000000;
        } else if("만원".equals(unit)) {
            _assureMoney = _assureMoney / 10000;
        }

        logger.info("가입금액 : {}{}", _assureMoney, unit);
        setTextToInputBox(input, String.valueOf(_assureMoney));
        WaitUtil.waitFor(3);

    }

    //공시실 주계약 보험료 설정
    protected void setAnnounceMonthlyPremium(CrawlingTreaty mainTreaty) throws Exception {
        WebElement element = waitPresenceOfElementLocated(By.cssSelector("#step3 > table > tbody > tr > td:nth-child(5)"));

        String monthlyPremium = element.getText().replaceAll("[^0-9]", "");
        mainTreaty.monthlyPremium = monthlyPremium;

        if("0".equals(mainTreaty.monthlyPremium)) {
            throw new Exception("주계약 보험료 세팅은 필수입니다.");
        } else {
            logger.info("보험료 : {}원", mainTreaty.monthlyPremium);
        }
    }

    //공시실 해약환급금 조회
    protected void crawlReturnPremiums(CrawlingProduct info) throws Exception {
        logger.info("해약환급금 보기 버튼 클릭");
        WebElement element = driver.findElement(By.id("openSurr"));
        waitElementToBeClickable(element).click();


        //창 전환
        currentHandle = driver.getWindowHandle();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);


        //홈페이지 해약환급금 금액표기 단위 읽기
        element = waitPresenceOfElementLocated(By.xpath("//p[@class='txtPos']"));
        String unit = element.getText();
        int unitNum = 1;
        int startIdx = unit.indexOf("단위");
        unit = unit.substring(startIdx);

        if(unit.contains("억원")) {
            unitNum = 100000000;
        } else if(unit.contains("천만원")) {
            unitNum = 10000000;
        } else if(unit.contains("만원")) {
            unitNum = 10000;
        }



        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trList = driver.findElements(By.xpath("//tbody//tr"));
        for(WebElement tr : trList) {
            String term = tr.findElement(By.xpath("./th[1]")).getText();
            String premiumSum = tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
            String returnMoney = tr.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
            String returnRate = tr.findElement(By.xpath("./td[5]")).getText();

            premiumSum = String.valueOf(Integer.parseInt(premiumSum) * unitNum);
            returnMoney = String.valueOf(Integer.parseInt(returnMoney) * unitNum);

            logger.info("====================");
            logger.info("경과기간 : {}", term);
            logger.info("합계보험료 : {}", premiumSum);
            logger.info("해약환급금 : {}", returnMoney);
            logger.info("환급률 : {}", returnRate);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney;
        }

        info.planReturnMoneyList = planReturnMoneyList;

        logger.info("보험기간 만료시 만기환급금 : {}원", info.returnPremium);
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


    //공시실 페이지 로딩바 대기
    protected void waitAnnouncePageLoadingBar() throws Exception {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("wrapper_loading")));
    }


    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }


    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
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




    protected boolean compareTreaties(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList) throws Exception {
        boolean result = true;

        List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
        List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트
        List<String> samedTreatyNameList = null;				//가입설계와 홈페이지 둘 다 일치하는 특약명 리스트


        //홈페이지 특약명 리스트
        List<String> homepageTreatyNameList = new ArrayList<>();
        List<String> copiedHomepageTreatyNameList = null;
        for(CrawlingTreaty t : homepageTreatyList) {
            homepageTreatyNameList.add(t.treatyName);
        }
        copiedHomepageTreatyNameList = new ArrayList<>(homepageTreatyNameList);


        //가입설계 특약명 리스트
        List<String> myTreatyNameList = new ArrayList<>();
        List<String> copiedMyTreatyNameList = null;
        for(CrawlingTreaty t : welgramTreatyList) {
            myTreatyNameList.add(t.treatyName);
        }
        copiedMyTreatyNameList = new ArrayList<>(myTreatyNameList);




        //일치하는 특약명만 추림
        homepageTreatyNameList.retainAll(myTreatyNameList);
        samedTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);



        //가입설계에 추가해야하는 특약명만 추림
        homepageTreatyNameList.removeAll(myTreatyNameList);
        toAddTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);



        //가입설계에서 제거해야하는 특약명만 추림
        myTreatyNameList.removeAll(homepageTreatyNameList);
        toRemoveTreatyNameList = new ArrayList<>(myTreatyNameList);
        myTreatyNameList = new ArrayList<>(copiedMyTreatyNameList);



        //특약명이 일치하는 경우에는 가입금액을 비교해준다.
        for(String treatyName : samedTreatyNameList) {
            CrawlingTreaty homepageTreaty = getCrawlingTreaty(homepageTreatyList, treatyName);
            CrawlingTreaty myTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);

            int homepageTreatyAssureMoney = homepageTreaty.assureMoney;
            int myTreatyAssureMoney = myTreaty.assureMoney;


            //가입금액 비교
            if(homepageTreatyAssureMoney == myTreatyAssureMoney) {
                //금액이 일치하는 경우
                logger.info("특약명 : {} | 가입금액 : {}원", treatyName, myTreatyAssureMoney);
            } else {
                //금액이 불일치하는 경우 특약정보 출력
                result = false;

                logger.info("[불일치 특약]");
                logger.info("특약명 : {}", treatyName);
                logger.info("가입설계 가입금액 : {}", myTreatyAssureMoney);
                logger.info("홈페이지 가입금액 : {}", homepageTreatyAssureMoney);
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

                CrawlingTreaty treaty = getCrawlingTreaty(homepageTreatyList, treatyName);
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




    protected int getUnit(String unitText) throws Exception {
        int unit = 0;

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
            default:
                throw new Exception(unitText + " is invalid.");
        }

        return unit;
    }
}
