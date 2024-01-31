package com.welgram.crawler.direct.life;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.GenderMismatchException;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
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

public abstract class CrawlingDGL extends SeleniumCrawler {

    public static final Logger logger = LoggerFactory.getLogger(CrawlingDGL.class);


    //inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void setTextToInputBox(By id, String text) {
        WebElement element = driver.findElement(id);
        element.clear();
        element.sendKeys(text);
    }

    //inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void setTextToInputBox(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }


    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOption(By element, String text) throws Exception {
        WebElement selectEl =  driver.findElement(element);
        selectOption(selectEl, text);
    }

    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOption(WebElement selectEl, String text) throws Exception{
        Select select = new Select(selectEl);

        try {
            select.selectByVisibleText(text);
        }catch (NoSuchElementException e) {
            throw new NoSuchElementException("selectbox에서 해당 text(" + text + ")를 찾을 수 없습니다");
        }
    }

    //버튼 클릭 메서드(홈페이지, 공시실 공용)
    protected void btnClick(By element) throws  Exception {
        driver.findElement(element).click();
    }

    //버튼 클릭 메서드(홈페이지, 공시실 공용)
    protected void btnClick(WebElement element) throws  Exception {
        element.click();
    }


    //홈페이지 생년월일 설정 메서드
    protected void setHomepageBirth(String birth) {
        setTextToInputBox(By.id("RRN"), birth);
    }


    //홈페이지 성별 설정 메서드
    protected void setHomepageGender(int gender) throws Exception{
        String genderId = (gender == MALE) ? "male" : "female";
        driver.findElement(By.id(genderId)).click();

        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='GNDR']:checked\").attr('id')").toString();
        String checkedGenderText = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();

        logger.info("성별 : {} 선택됨", checkedGenderText);

        if(!checkedElId.equals(genderId)) {
            logger.error("홈페이지 클릭된 성별 : {}", checkedGenderText);
            logger.error("가입설계 성별 : {}", (gender == MALE) ? "남자" : "여자");
            throw new GenderMismatchException("성별이 일치하지 않습니다.");
        }
    }


    //홈페이지 보험료 계산하기 버튼클릭 메서드
    protected void homepageCalcBtnClick() throws Exception{
        driver.findElement(By.id("PREM_CALC")).click();
        waitHomepageLoadingImg();
    }


    //todo 로딩바 명시적 대기 안먹힘...a
    //홈페이지 로딩이미지 명시적 대기
    protected void waitHomepageLoadingImg() throws Exception{


//        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("ly-loading")));
        helper.waitForCSSElement("ly-loading");
    }


    //홈페이지 보험기간 설정 메서드
    protected void setHomepageInsTerm(String insTerm) throws Exception {
        //TODO 보장기간이 아닌 가입하기 버튼이 보일때까지로 대기...
//        WebElement targetInsTermEl = helper.waitPresenceOfElementLocated(By.xpath("//div[text()='보장기간']/../div[@class='price']"));
        WebElement targetInsTermEl = waitPresenceOfElementLocated(By.xpath("//div[text()='보장기간']/../div[@class='price']"));

        String targetInsTerm = targetInsTermEl.getText().trim();

        logger.info("보험기간 : {} 표기됨", targetInsTerm);

        if(!insTerm.equals(targetInsTerm)) {
            logger.error("홈페이지에 표기된 보험기간 : {}", targetInsTerm);
            logger.error("가입설계 보험기간 : {}", insTerm);
            throw new InsTermMismatchException("보험기간이 일치하지 않습니다.");
        }
    }


    //홈페이지 납입기간 설정 메서드
    protected void setHomepageNapTerm(String insTerm) throws Exception {

    }

    //홈페이지 납입주기 설정 메서드
    protected void setHomepageNapCycle(String napCycle) throws Exception {

    }


    //홈페이지 주계약 보험료 설정 메서드
    protected void setHomepagePremium(CrawlingProduct info) throws Exception{
//        String monthlyPremium = helper.waitPresenceOfElementLocated(By.xpath("//div[contains(., '보험료')]/../div[@class='price']")).getText().replaceAll("[^0-9]", "").trim();
        String monthlyPremium = waitPresenceOfElementLocated(By.xpath("//div[contains(., '보험료')]/../div[@class='price']")).getText().replaceAll("[^0-9]", "").trim();

        logger.info("보험료 : {}원", monthlyPremium);

        info.treatyList.get(0).monthlyPremium = monthlyPremium;

        WaitUtil.waitFor(2);
    }


    //홈페이지 해약환급금 조회
    protected void getHomepageReturnPremiums(CrawlingProduct info) throws Exception{
        logger.info("해약환급금 버튼 클릭!!");
        WebElement element = driver.findElement(By.linkText("해약환급금"));
        waitElementToBeClickable(element).click();
        waitHomepageLoadingImg();

        List<WebElement> trList = waitVisibilityOfAllElements(By.cssSelector("#rateTbody tr"));
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        String unit = driver.findElement(By.cssSelector(".tbl-footnote .align-r")).getText().trim();
        String unitText = "";
        if(unit.contains("만원")) {
            unitText = "0000";
        }

        for(WebElement tr : trList) {
            String term = tr.findElements(By.tagName("td")).get(0).getText().trim();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "") + unitText;
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "") + unitText;
            String returnRate = tr.findElements(By.tagName("td")).get(3).getText().trim();

            logger.info("***해약환급금***");
            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);
            logger.info("|--해약환급금: {}", returnMoney);
            logger.info("|--환급률: {}", returnRate + "\n");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.age));

            planReturnMoney.setTerm(term); // 경과기간
            planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계(납입보험료)
            planReturnMoney.setReturnMoney(returnMoney); // 환급금
            planReturnMoney.setReturnRate(returnRate); // 환급률

            planReturnMoneyList.add(planReturnMoney);
            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }

        info.planReturnMoneyList = planReturnMoneyList;

        logger.info("보험기간 만료 시({}) 만기환급금 : {}원", info.insTerm, info.returnPremium);
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


    //공시실 페이지 로딩바 대기
    protected void waitAnnouncePageLoadingBar() throws Exception {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("commonProgressBar")));
    }


    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }


    //공시실 생년월일 설정
    protected void setAnnounceBirth(String fullBirth) throws Exception {
        WebElement input = driver.findElement(By.id("custBirth21"));
        setTextToInputBox(input, fullBirth);
    }



    //공시실 성별 설정
    protected void setAnnounceGender(int gender) throws Exception {
        String genderText = (gender == MALE) ? "남" : "여";


        //성별 클릭
        WebElement select = driver.findElement(By.id("gender21"));
        selectOption(select, genderText);


        //맞게 클릭됐는지 검사
        String script = "return $('#gender21 option:selected').text();";
        String checkedGender = executeJavascript(script).toString();

        logger.info("=========================================");
        logger.info("가입설계 성별 : {}", genderText);
        logger.info("홈페이지 클릭된 성별 : {}", checkedGender);
        logger.info("=========================================");

        if(genderText.equals(checkedGender)) {
            logger.info("가입설계 성별 : {} == 홈페이지 클릭된 성별 : {}", genderText, checkedGender);
        } else {
            logger.info("가입설계 성별 : {} ≠ 홈페이지 클릭된 성별 : {}", genderText, checkedGender);
            throw new Exception("성별 불일치");
        }
    }



    //공시실 주계약 종류 설정
    protected void setAnnounceMainTreaty(String mainTreatyName) throws Exception {

        //주계약 종류 선택
        WebElement select = driver.findElement(By.id("pdtMaiSelect"));
        selectOption(select, mainTreatyName);


        //맞게 클릭됐는지 검사
        String script = "return $('#pdtMaiSelect option:selected').text();";
        String checkedMainTreatyName = executeJavascript(script).toString();

        logger.info("=========================================");
        logger.info("가입설계 주계약 종류 : {}", mainTreatyName);
        logger.info("홈페이지 클릭된 주계약 종류 : {}", checkedMainTreatyName);
        logger.info("=========================================");

        if(mainTreatyName.equals(checkedMainTreatyName)) {
            logger.info("가입설계 주계약 종류 : {} == 홈페이지 클릭된 주계약 종류 : {}", mainTreatyName, checkedMainTreatyName);
        } else {
            logger.info("가입설계 주계약 종류 : {} ≠ 홈페이지 클릭된 주계약 종류 : {}", mainTreatyName, checkedMainTreatyName);
            throw new Exception("주계약 종류 불일치");
        }
    }



    //공시실 보험기간 설정
    protected void setAnnounceInsTerm(String insTerm) throws Exception {

        //보험기간 클릭
        WebElement select = driver.findElement(By.id("GP1001_TRMINS"));
        selectOption(select, insTerm);
        waitAnnouncePageLoadingBar();


        //맞게 클릭됐는지 검사
        String script = "return $('#GP1001_TRMINS option:selected').text();";
        String checkedInsTerm = executeJavascript(script).toString();

        logger.info("=========================================");
        logger.info("가입설계 보험기간 : {}", insTerm);
        logger.info("홈페이지 클릭된 보험기간 : {}", checkedInsTerm);
        logger.info("=========================================");

        if(insTerm.equals(checkedInsTerm)) {
            logger.info("가입설계 보험기간 : {} == 홈페이지 클릭된 보험기간 : {}", insTerm, checkedInsTerm);
        } else {
            logger.info("가입설계 보험기간 : {} ≠ 홈페이지 클릭된 보험기간 : {}", insTerm, checkedInsTerm);
            throw new Exception("보험기간 불일치");
        }
    }


    //공시실 납입기간 설정
    protected void setAnnounceNapTerm(String napTerm) throws Exception {

        //납입기간 클릭
        WebElement select = driver.findElement(By.id("GP1001_pypd"));
        selectOption(select, napTerm);


        //맞게 클릭됐는지 검사
        String script = "return $('#GP1001_pypd option:selected').text();";
        String checkedNapTerm = executeJavascript(script).toString();

        logger.info("=========================================");
        logger.info("가입설계 납입기간 : {}", napTerm);
        logger.info("홈페이지 클릭된 납입기간 : {}", checkedNapTerm);
        logger.info("=========================================");

        if(napTerm.equals(checkedNapTerm)) {
            logger.info("가입설계 납입기간 : {} == 홈페이지 클릭된 납입기간 : {}", napTerm, checkedNapTerm);
        } else {
            logger.info("가입설계 납입기간 : {} ≠ 홈페이지 클릭된 납입기간 : {}", napTerm, checkedNapTerm);
            throw new Exception("납입기간 불일치");
        }
    }


    //공시실 납입주기 설정
    protected void setAnnounceNapCycle(String napCycle) throws Exception {

        //납입주기 클릭
        WebElement select = driver.findElement(By.id("GP1001_pycyc"));
        selectOption(select, napCycle);


        //맞게 클릭됐는지 검사
        String script = "return $('#GP1001_pycyc option:selected').text();";
        String checkedNapCycle = executeJavascript(script).toString();

        logger.info("=========================================");
        logger.info("가입설계 납입주기 : {}", napCycle);
        logger.info("홈페이지 클릭된 납입주기 : {}", checkedNapCycle);
        logger.info("=========================================");

        if(napCycle.equals(checkedNapCycle)) {
            logger.info("가입설계 납입주기 : {} == 홈페이지 클릭된 납입주기 : {}", napCycle, checkedNapCycle);
        } else {
            logger.info("가입설계 납입주기 : {} ≠ 홈페이지 클릭된 납입주기 : {}", napCycle, checkedNapCycle);
            throw new Exception("납입주기 불일치");
        }
    }




    //공시실 가입금액 설정
    protected void setAnnounceAssureMoney(int assureMoney) throws Exception {

        //가입금액 설정
        WebElement input = driver.findElement(By.id("GP1001_SMSU_INPUT"));
        setTextToInputBox(input, String.valueOf(assureMoney));


        //맞게 설정됐는지 검사
        WebElement td = driver.findElement(By.xpath("//span[@id='#tr_GP1001 > td:nth-child(8)']/parent::td"));
        String checkedAssureMoney = td.getText();
        Long checkedAssureMoneyNum = MoneyUtil.toDigitMoney(checkedAssureMoney);


        logger.info("=========================================");
        logger.info("가입설계 가입금액 : {}", assureMoney);
        logger.info("홈페이지 클릭된 가입금액 : {}", checkedAssureMoneyNum);
        logger.info("=========================================");

        if(assureMoney == checkedAssureMoneyNum) {
            logger.info("가입설계 가입금액 : {} == 홈페이지 설정된 가입금액 : {}", assureMoney, checkedAssureMoneyNum);
        } else {
            logger.info("가입설계 가입금액 : {} ≠ 홈페이지 설정된 가입금액 : {}", assureMoney, checkedAssureMoneyNum);
            throw new Exception("가입금액 불일치");
        }
    }



    //공시실 주계약 보험료 설정
    protected void setAnnounceMonthlyPremium(CrawlingTreaty mainTreaty) throws Exception {
        WebElement td = driver.findElement(By.xpath("//span[@id='GP1001_PREM']/parent::td"));
        String monthlyPremium = td.getText().replaceAll("[^0-9]", "");

        mainTreaty.monthlyPremium = monthlyPremium;

        if("0".equals(mainTreaty.monthlyPremium)) {
            throw new Exception("주계약 보험료 설정을 필수입니다.");
        } else {
            logger.info("보험료 : {}원", mainTreaty.monthlyPremium);
        }
    }



    //공시실 해약환급금 조회
    protected void getAnnounceReturnPremium(CrawlingProduct info) throws Exception {
        logger.info("해약환급금 버튼 클릭");
        WebElement element = driver.findElement(By.id("calcTab_2"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(6);

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trList = driver.findElements(By.xpath("//div[@id='srdfbasicDiv']//tr[position() > 1]"));
        for(WebElement tr : trList) {
            moveToElementByJavascriptExecutor(tr);

            String term = tr.findElement(By.xpath("./td[1]")).getText();
            String premiumSum = tr.findElement(By.xpath("./td[3]")).getText();
            String returnMoney = tr.findElement(By.xpath("./td[4]")).getText();
            String returnRate = tr.findElement(By.xpath("./td[5]")).getText();

            logger.info("======해약환급금=======");
            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("해약환급금 : {}", returnMoney);
            logger.info("해약환급률 : {}", returnRate);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney;
        }

        info.planReturnMoneyList = planReturnMoneyList;

        logger.info("만기환급금 : {}", info.returnPremium);
    }

}
