package com.welgram.crawler.direct.fire.hwf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.AssureMoneyMismatchException;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.common.except.NapCycleMismatchException;
import com.welgram.common.except.NapTermMismatchException;
import com.welgram.common.except.NotFoundInsTermException;
import com.welgram.common.except.NotFoundNapCycleException;
import com.welgram.common.except.NotFoundNapTermException;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
import com.welgram.common.except.NotFoundTreatyException;
import com.welgram.common.except.NotFoundValueInSelectBoxException;
import com.welgram.common.except.TreatyMisMatchException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;


/**
 * 2021.02.02
 *
 * @author 조하연
 * HWF 홈페이지, 공시실 코드
 */

public abstract class CrawlingHWF extends SeleniumCrawler {


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



    //element 클릭 명시적 대기
    protected WebElement waitElementToBeClickable(By by) throws Exception {
        WebElement returnElement = null;
        WebElement element = driver.findElement(by);

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



    //inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void setTextToInputBox(By el, String text) {
        WebElement element = driver.findElement(el);
        element.clear();
        element.sendKeys(text);
    }


    //inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void setTextToInputBox(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }


    // select 태그에서 해당 text의 option을 클릭한다(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void selectOptionByText(By element, String text) throws NotFoundTextInSelectBoxException {
        WebElement selectEl = driver.findElement(element);
        selectOptionByText(selectEl, text);
    }


    // select 태그에서 해당 text의 option을 클릭한다(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void selectOptionByText(WebElement selectEl, String text) throws NotFoundTextInSelectBoxException{
        Select select = new Select(selectEl);

        try {
            select.selectByVisibleText(text);
        }catch (NoSuchElementException e) {
            throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')값을 찾을 수 없습니다.");
        }
    }


    //select 박스에서 value로 option을 선택하는 메서드
    protected void selectOptionByValue(WebElement selectEl, String value) throws NotFoundValueInSelectBoxException {
        Select select = new Select(selectEl);

        try {
            select.selectByValue(value);
        } catch (NoSuchElementException e) {
            throw new NotFoundValueInSelectBoxException("selectBox에서 해당 value('" + value + "')값을 찾을 수 없습니다.");
        }

    }


    //select박스의 option들을 for문으로 돌면서 text를 포함하고 있는 option을 클릭한다.
    protected void selectOptionByFor(WebElement selectEl, String text) throws NotFoundTextInSelectBoxException{
        boolean isFound = false;

        List<WebElement> options = selectEl.findElements(By.tagName("option"));

        for(WebElement option : options) {
            if(option.getText().contains(text)) {
                isFound = true;
                option.click();
                break;
            }
        }

        if(!isFound) {
            throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')값을 찾을 수 없습니다.");
        }
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

    //해당 element가 존재하는지 여부를 리턴
    protected boolean existElement(By element) {

        boolean isExist = true;

        try {
            driver.findElement(element);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }


    //홈페이지 버튼 클릭 메서드(홈페이지용 명시적 대기가 포함된 코드)
    protected void homepageBtnClick(By element) {
        driver.findElement(element).click();
        waitHomepageLoadingImg();
    }

    //공시실 버튼 클릭 메서드(공시실용 명시적 대기가 포함된 코드)
    protected void announceBtnClick(By element) {
        driver.findElement(element).click();
        waitAnnounceLoadingImg();
    }

    //모바일 버튼 클릭 메서드(모바일용 명시적 대기가 포함된 코드)
    protected void mobileBtnClick(By element) {
        driver.findElement(element).click();
        waitMobileLoadingImg();
    }

    //일반 버튼 클릭 메서드
    protected void btnClick(By element) {
        driver.findElement(element).click();
    }



    //홈페이지 생년월일 설정 메서드(1개 입력)
    protected void setHomepageBirth(String fullBirth) {
        setTextToInputBox(By.id("ctrctBirthday"), fullBirth);
    }


    //홈페이지 생년월일 설정 메서드(2개 입력)
    protected void setHomepageBirth(String childBirth, String parentBirth) {
        logger.info("자녀 생년월일 설정");
        setTextToInputBox(By.id("relpcBirthday"), childBirth);

        logger.info("부모 생년월일 설정");
        setHomepageBirth(parentBirth);
    }


    //홈페이지 성별 설정 메서드(1개 입력)
    protected void setHomepageGender(int gender) {
        String genderTag = (gender == MALE) ? "gender_man" : "gender_woman";
        driver.findElement(By.cssSelector("label[for='" + genderTag + "']")).click();
    }


    //홈페이지 성별 설정 메서드(2개 입력)
    protected void setHomepageGender(int childGender, int parentGender) {
        String childGenderTag = (childGender == MALE) ? "gender_man" : "gender_woman";
        String parentGenderTag = (parentGender == MALE) ? "ctrGender_man" : "ctrGender_woman";

        logger.info("자녀 성별 설정");
        driver.findElement(By.cssSelector("label[for='" + childGenderTag + "']")).click();

        logger.info("부모 성별 설정");
        driver.findElement(By.cssSelector("label[for='" + parentGenderTag + "']")).click();
    }


    //홈페이지 차량 설정 메서드(자가용으로 고정)
    protected void setHomepageCar() {
        homepageBtnClick(By.xpath("//dt[contains(text(), '자가용')]"));
    }


    //홈페이지 보험기간 설정 메서드
    protected void setHomepageInsTerm(String insTerm) throws Exception{
        try {
            insTerm = insTerm + "만기";
            selectOptionByText(By.id("insTerms"), insTerm);
        } catch (NotFoundTextInSelectBoxException e) {
            throw new NotFoundInsTermException("보험기간(" + insTerm + ")이 존재하지 않습니다.");
        }

        //클릭된 보험기간 제대로 클릭된게 맞는지 검사.
        String checkedInsTerm = ((JavascriptExecutor)driver).executeScript("return $(\"#insTerms option:checked\").text()").toString();
        logger.info("클릭된 보험기간 : {}", checkedInsTerm);

        if(!checkedInsTerm.equals(insTerm)) {
            logger.info("홈페이지 클릭된 보험기간 : {}", checkedInsTerm);
            logger.info("가입설계 보험기간 : {}", insTerm);
            throw new InsTermMismatchException("보험기간 불일치.");
        }
    }


    //홈페이지 납입기간 설정 메서드
    protected void setHomepageNapTerm(String napTerm) throws Exception{
        try {
            napTerm = napTerm + "납";
            selectOptionByText(By.id("payTerms"), napTerm);
        } catch (NotFoundTextInSelectBoxException e) {
            throw new NotFoundNapTermException("납입기간(" + napTerm + ")이 존재하지 않습니다.");
        }

        //클릭된 납입기간 제대로 클릭된게 맞는지 검사.
        String checkedNapTerm = ((JavascriptExecutor)driver).executeScript("return $(\"#payTerms option:checked\").text()").toString();
        logger.info("클릭된 납입기간 : {}", checkedNapTerm);

        if(!checkedNapTerm.equals(napTerm)) {
            logger.info("홈페이지 클릭된 납입기간 : {}", checkedNapTerm);
            logger.info("가입설계 납입기간 : {}", napTerm);
            throw new NapTermMismatchException("납입기간 불일치.");
        }
    }


    //홈페이지 보험기간, 납입기간을 동시에 세팅하는 경우에 사용하는 메서드
    protected void setHomepageTerms(String insTerm, String napTerm) throws Exception{
        napTerm = (napTerm.equals(insTerm)) ? "전기납" : napTerm;
        String terms = insTerm + "/" + napTerm;

        selectOptionByText(By.id("insTerms"), terms);

        //클릭된 보기,납기가 제대로 클릭된게 맞는지 검사.
        String checkedTerms = ((JavascriptExecutor)driver).executeScript("return $(\"#insTerms option:checked\").text()").toString();
        logger.info("클릭된 보기,납기 : {}", checkedTerms);

        if(!checkedTerms.equals(terms)) {
            logger.info("홈페이지 클릭된 보기,납기 : {}", checkedTerms);
            logger.info("가입설계 보기,납기 : {}", terms);
            throw new Exception("보기, 납기 불일치");
        }
    }


    //홈페이지 납입주기 설정 메서드
    protected void setHomepageNapCycle(String napCycle) throws Exception {
        try {
            selectOptionByText(By.id("pymMtd"), napCycle);
        } catch (NotFoundTextInSelectBoxException e) {
            throw new NotFoundNapCycleException("납입주기(" + napCycle + ")이 존재하지 않습니다.");
        }

        //클릭된 납입주기 제대로 클릭된게 맞는지 검사.
        String checkedNapCycle = ((JavascriptExecutor)driver).executeScript("return $(\"#pymMtd option:checked\").text()").toString();
        logger.info("클릭된 납입주기 : {}", checkedNapCycle);

        if(!checkedNapCycle.equals(napCycle)) {
            logger.info("홈페이지 클릭된 납입주기 : {}", checkedNapCycle);
            logger.info("가입설계 납입주기 : {}", napCycle);
            throw new NapCycleMismatchException("납입주기 불일치.");
        }
    }


    //홈페이지 다시 계산 버튼 클릭 메서드
    protected void homepageReCalcBtnClick() throws Exception {
        homepageBtnClick(By.id("btnReCalcAllPlan"));
    }


    /*
     * 홈페이지 주계약 보험료 세팅 메서드
     *
     * 보통 treatyList의 첫번째에 주계약이 위치한다.
     * 하지만 가끔 주계약이 첫번째에 위치하지 않은 경우도 있는데 그럴 때는 상품마스터에서 주계약을 맨 위에 위치하도록 수정한다.
     * 그래야만 마지막에 제대로 보혐료를 크롤링해 온다.)
     * */
    protected void setHomepagePremiums(CrawlingProduct info) throws Exception{
        String[] premiums = driver.findElement(By.id("selPlanInsarc")).getText().split("/");

        String insMoney = premiums[0].replaceAll("[^0-9]", "");
        String saveMoney = premiums[1].replaceAll("[^0-9]", "");

        logger.info("보장보험료 : {}", insMoney);
        logger.info("적립보험료 : {}", saveMoney);
        logger.info("월 보험료(total 보험료) : {}", Integer.valueOf(insMoney) + Integer.valueOf(saveMoney));

        info.treatyList.get(0).monthlyPremium = insMoney;
        info.savePremium = saveMoney;

        if("0".equals(info.treatyList.get(0).monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수 없습니다.");
        }
    }


    //홈페이지 해약환급금 조회 메서드
    protected void getHomepageReturnPremiums(CrawlingProduct info) throws Exception {
//        homepageBtnClick(By.id("btnPopRefund"));
        logger.info("환급금 버튼 클릭");
        element = driver.findElement(By.id("btnPopRefund"));
        waitElementToBeClickable(element).click();
        waitHomepageLoadingImg();

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> trList = driver.findElements(By.cssSelector("#refundTbodyArea > tr"));
        for(WebElement tr : trList) {
            String term = tr.findElement(By.xpath("./th[1]")).getText();
            String premiumSum = tr.findElement(By.xpath("./td[1]")).getText().replaceAll("[^0-9]", "");
            String returnMoneyMin = tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
            String returnRateMin = tr.findElement(By.xpath("./td[3]")).getText();
            String returnMoney = tr.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
            String returnRate = tr.findElement(By.xpath("./td[5]")).getText();
            String returnMoneyAvg = tr.findElement(By.xpath("./td[6]")).getText().replaceAll("[^0-9]", "");
            String returnRateAvg = tr.findElement(By.xpath("./td[7]")).getText();


            logger.info("====== 해약환급금 ======");
            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("최저환급금 : {}", returnMoneyMin);
            logger.info("최저환급률 : {}", returnRateMin);
            logger.info("환급금 : {}", returnMoney);
            logger.info("환급률 : {}", returnRate);
            logger.info("평균환급금 : {}", returnMoneyAvg);
            logger.info("평균환급률 : {}", returnRateAvg);


            PlanReturnMoney p = new PlanReturnMoney();
            p.setTerm(term);
            p.setPremiumSum(premiumSum);
            p.setReturnMoneyMin(returnMoneyMin);
            p.setReturnRateMin(returnRateMin);
            p.setReturnMoney(returnMoney);
            p.setReturnRate(returnRate);
            p.setReturnMoneyAvg(returnMoneyAvg);
            p.setReturnRateAvg(returnRateAvg);


            planReturnMoneyList.add(p);

            info.returnPremium = returnMoney;
        }

        info.planReturnMoneyList = planReturnMoneyList;

        logger.info("만기환급금 : {}", info.returnPremium);
    }

    //홈페이지 로딩이미지 명시적 대기
    protected void waitHomepageLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadCont")));
    }


    //공시실 생년월일 설정 메서드
    protected void setAnnounceBirth(String fullBirth) {
        WebElement $element = driver.findElement(By.id("i_jumin"));

        setTextToInputBox(By.id("i_jumin"), fullBirth);
        $element.sendKeys(Keys.ENTER);
    }


    //공시실 성별 설정 메서드
    protected void setAnnounceGender(int gender) throws NotFoundTextInSelectBoxException{
        String genderText = (gender == MALE) ? "남성" : "여성";

        selectOptionByText(By.id("i_no"), genderText);
    }


    //공시실 직업 설정 메서드(보험 사무원으로 고정)
    protected void setAnnounceJob() throws Exception {
        announceBtnClick(By.cssSelector("#jobSearch"));

        currentHandle = driver.getWindowHandle();

        if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

            announceBtnClick(By.linkText("분류대로 찾기"));
            announceBtnClick(By.xpath("//span[contains(.,'사무 종사자')]"));
            announceBtnClick(By.xpath("//span[contains(.,'행정 사무원')]"));
            btnClick(By.xpath("//span[contains(.,'회사 사무직 종사자')]"));
            WaitUtil.waitFor(1);

            btnClick(By.id("btnOk"));
        }

        helper.switchToWindow("", driver.getWindowHandles(), true);
    }


    //공시실 차량용도 설정 메서드(자가용 고정)
    protected void setAnnounceCar() throws Exception {
        try {
            selectOptionByText(By.id("i_car"), "자가용");
        } catch (NoSuchElementException e) {
            selectOptionByText(By.cssSelector("select[name=cha]"), "자가용");
        }
    }


    //공시실 가입구분 설정 메서드
    protected void setAnnounceType(String textType) throws Exception {
        String text = "";
        if (textType.equals("표준형")) {
            text = "1종(기본납입형)(표준형)";
        } else if (textType.equals("선택형Ⅱ")) {
            text = "2종(기본납입형)(선택형Ⅱ)";
        }

        selectOptionByText(By.id("gubun"), text);
        WaitUtil.waitFor(1);
    }


    //공시실 특약 설정 메서드
    protected void setAnnounceTreaties(CrawlingProduct info) throws Exception {
        for(CrawlingTreaty treaty : info.treatyList) {
            String treatyName = treaty.treatyName;
            int assureMoney = treaty.assureMoney;

            String _assureMoney = "";
            if (assureMoney / 10000000 != 0) {
                _assureMoney = (assureMoney / 10000000) + "천만원";
            } else if (assureMoney / 10000 != 0) {
                _assureMoney = (assureMoney / 10000) + " 만원";
            }

            try {
                WebElement selectEl = driver.findElement(By.xpath("//th[text()='" + treatyName + "']/..//select"));
                selectEl.findElement(By.xpath(".//option[contains(., '" + _assureMoney + "')]")).click();
            }catch(NoSuchElementException e) {
                //특약 가입금액을 세팅하지 않는 케이스. 이 때는 세팅된 가격이랑 비교만 해주면 된다.
                WebElement assureMoneyEl = driver.findElement(By.xpath("//th[text()='" + treatyName + "']/..//td[@id[contains(., 'ainsure')]]"));

                if(!assureMoneyEl.getText().contains(_assureMoney)) {
                    logger.info("특약명 : {} 가입금액 불일치", treatyName);
                    logger.info("가입설계 가입금액 : {}", _assureMoney);
                    logger.info("홈페이지 가입금액 : {}", assureMoneyEl.getText());
                    throw new AssureMoneyMismatchException("가입금액 불일치");
                }
            }
        }
    }

    //공시실 로딩이미지 명시적 대기
    protected void waitAnnounceLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("popLoading")));
    }

    protected void waitMobileLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading_wrap")));
    }


    //공시실 플랜선택 메서드
    protected void setAnnounceProductType(String textType) throws Exception{
        if (textType.equals("종합보장플랜")) {
            textType = "2종(세만기형)";
        } else if (textType.equals("미니진단비플랜")) {
            textType = "1종(연만기형)";
        }

        selectOptionByText(By.cssSelector("select[name=gubun]"), textType);
    }


    //공시실 보험기간 설정 메서드
    protected void setAnnounceInsTerm(String insTerm) throws Exception{
        try {
            selectOptionByText(By.cssSelector("select[name=bogi]"), insTerm);
        }catch(NotFoundTextInSelectBoxException e) {
            throw new NotFoundInsTermException("보험기간(" + insTerm + ")이 존재하지 않습니다.");
        }
    }


    //공시실 납입기간 설정 메서드
    protected void setAnnounceNapTerm(String napTerm) throws Exception{
        try {
            selectOptionByText(By.cssSelector("select[name=napgi]"), napTerm);
        }catch(NotFoundTextInSelectBoxException e) {
            throw new NotFoundNapTermException("납입기간(" + napTerm + ")이 존재하지 않습니다.");
        }
    }


    //공시실 납입주기 설정 메서드
    protected void setAnnounceNapCycle(String napCycle) throws Exception{
        try {
            selectOptionByText(By.cssSelector("select[name=napbang]"), napCycle);
        } catch (NoSuchElementException e) {
            selectOptionByText(By.id("cycle"), napCycle);
        } catch (NotFoundTextInSelectBoxException e) {
            throw new NotFoundNapCycleException("납입기간(" + napCycle + ")이 존재하지 않습니다.");
        }
    }


    //공시실 보험료 계산 버튼 클릭 메서드
    protected void announceCalcBtnClick() throws Exception {
        announceBtnClick(By.id("btnCalc"));
        WaitUtil.waitFor(2);
    }



    //element가 보이게끔 이동
    private void moveToElement(By location) {
        Actions action = new Actions(driver);

        WebElement element = driver.findElement(location);
        action.moveToElement(element);
        action.perform();
    }



    protected void executeJavascriptExecute(String script) {
        ((JavascriptExecutor)driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }



    //공시실 주계약 보험료 설정 메서드
    protected void setAnnouncePremiums(CrawlingProduct info) {
        moveToElement(By.id("btnReCalc"));
        String insMoney = driver.findElement(By.id("gnPrm")).getText().replaceAll("[^0-9]", "");

        try {
            String saveMoney = driver.findElement(By.id("cuPrm")).getText().replaceAll("[^0-9]", "");
            logger.info("보장보험료 : {}", insMoney + "원");
            logger.info("적립보험료 : {}", saveMoney + "원");
            logger.info("월 보험료(total 보험료) : {}", Integer.valueOf(insMoney) + Integer.valueOf(saveMoney));
            info.savePremium = saveMoney;
        } catch (NoSuchElementException e) {
            logger.info("이 상품은 적립보험료가 없는 상품입니다!!!");
            logger.info("월 보험료 : {}", insMoney + "원");
        }

        info.treatyList.get(0).monthlyPremium = insMoney;
    }


    //공시실 해약환급금 조회 메서드
    protected void getAnnounceFullReturnPremiums(CrawlingProduct info) throws Exception {
        announceBtnClick(By.id("btnPopCancel"));

        currentHandle = driver.getWindowHandle();

        if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            List<WebElement> trList = driver.findElements(By.cssSelector("tbody tr"));
            for (int i = 0; i < trList.size(); i++) {
                WebElement tr = trList.get(i);

                String term = tr.findElement(By.tagName("th")).getText();     //경과기간
                String premiumSum = tr.findElement(By.cssSelector("td:nth-child(2)")).getText();  // 납입보험료

                String returnMoneyMin = tr.findElement(By.cssSelector("td:nth-child(3)")).getText();  // 최저 환급금
                String returnRateMin = tr.findElement(By.cssSelector("td:nth-child(4)")).getText();  // 최저 환급률

                String returnMoneyAvg = tr.findElement(By.cssSelector("td:nth-child(5)")).getText();  // 평균공시이율 환급금
                String returnRateAvg = tr.findElement(By.cssSelector("td:nth-child(6)")).getText();  // 평균공시이율 환급률

                String returnMoney = tr.findElement(By.cssSelector("td:nth-child(7)")).getText();  // 공시이율 환급금
                String returnRate = tr.findElement(By.cssSelector("td:nth-child(8)")).getText();  // 공시이율 환급률


                logger.info("______해약환급급[{}]_______ ", i);
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--최저납입보험료: {}", premiumSum);
                logger.info("|--최저해약환급금: {}", returnMoneyMin);
                logger.info("|--최저해약환급률: {}", returnRateMin);
                logger.info("|--평균해약환급금: {}", returnMoneyAvg);
                logger.info("|--평균해약환급률: {}", returnRateAvg);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));

                planReturnMoney.setTerm(term); // 경과기간
                planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계(납입보험료)

                planReturnMoney.setReturnMoneyMin(returnMoneyMin); // 최저해약환급금
                planReturnMoney.setReturnRateMin(returnRateMin); // 최저환급률

                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg); // 평균해약환급금
                planReturnMoney.setReturnRateAvg(returnRateAvg); // 평균환급률

                planReturnMoney.setReturnMoney(returnMoney); // 환급금
                planReturnMoney.setReturnRate(returnRate); // 환급률


                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }

            info.planReturnMoneyList = planReturnMoneyList;
            logger.info("만기환급금 : {}", info.returnPremium);
            btnClick(By.linkText("닫기"));
            WaitUtil.waitFor(2);
        }

        helper.switchToWindow("", driver.getWindowHandles(), true);
    }


    //공시실 해약환급금 조회 메서드
    protected void getAnnounceShortReturnPremiums(CrawlingProduct info) {
        //해약환급금 버튼 클릭을 위해 해당 엘리먼트가 있는 곳으로 스크롤을 조정한다.
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(By.id("cycle"));
        actions.moveToElement(element);
        actions.perform();

        announceBtnClick(By.id("btnPopCancel"));

        currentHandle = driver.getWindowHandle();

        if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

            List<WebElement> trList = driver.findElements(By.xpath("//tbody/tr"));
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            for (int i = 0; i < trList.size(); i++) {
                WebElement tr = trList.get(i);

                String term = tr.findElement(By.xpath(".//th")).getText().replaceAll(" ", "");  //경과기간
                String premiumSum = tr.findElement(By.xpath(".//td[4]")).getText(); //납입보험료
                String returnPremium = tr.findElement(By.xpath(".//td[5]")).getText(); //환급금

                logger.info("______해약환급급[{}]_______ ", i);
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--최저납입보험료: {}", premiumSum);
                logger.info("|--환급금: {}", returnPremium);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnPremium);

                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnPremium.replaceAll("[^0-9]", "");
            }

            info.planReturnMoneyList = planReturnMoneyList;
        }
    }


    //공시실 다시계산하기 버튼 클릭 메서드
    protected void announceReCalcBtnClick() {
        announceBtnClick(By.id("btnReCalc"));
    }


    //홈페이지용 특약정보 비교 메서드
    protected void compareTreaties(CrawlingProduct info) throws Exception {
        HashMap<String, String> hTreatyMap = new HashMap<>();

        List<WebElement> trList = driver.findElements(By.xpath("//tbody[@id='tbodyArea']/tr"));

        //홈페이지 특약명과 특약금액을 hTreatyMap에 담는다.
        for(WebElement tr : trList) {
            String hTreatyName = tr.findElement(By.xpath("./td[@class='flan_title']//p[position()=1]")).getText();
            String hTreatyMoney = tr.findElement(By.xpath("./td[@class='reomnnend']")).getText();
            hTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney(hTreatyMoney));

            hTreatyMap.put(hTreatyName, hTreatyMoney);
        }

        if(hTreatyMap.size() == info.treatyList.size()) {
            //Good Case :: 홈페이지와 가입설계 특약 수가 일치할 때. 이 경우는 특약명이 일치하는지, 특약 가입금액이 일치하는지 비교해줘야 함.

            for(CrawlingTreaty myTreaty : info.treatyList) {
                String myTreatyName = myTreaty.treatyName;
                String myTreatyMoney = String.valueOf(myTreaty.assureMoney);

                //특약명이 불일치할 경우
                if(!hTreatyMap.containsKey(myTreatyName)) {
                    throw new NotFoundTreatyException("특약명(" + myTreatyName + ")은 존재하지 않는 특약입니다.");
                } else {
                    //특약명은 일치하지만, 금액이 다른경우
                    if(!hTreatyMap.get(myTreatyName).equals(myTreatyMoney)) {
                        logger.info("특약명 : {}", myTreatyName);
                        logger.info("홈페이지 금액 : {}원", hTreatyMap.get(myTreatyName));
                        logger.info("가입설계 금액 : {}원", myTreatyMoney);

                        throw new TreatyMisMatchException("특약명(" + myTreatyName + ")의 가입금액이 일치하지 않습니다.");
                    }
                }
            }

        } else if(hTreatyMap.size() > info.treatyList.size()) {
            //Wrong Case :: 홈페이지의 특약 개수가 더 많을 때. 이 경우 가입설계에 어떤 특약을 추가해야 하는지 알려야 함.

            List<String> myTreatyNameList = new ArrayList<>();
            for(CrawlingTreaty myTreaty : info.treatyList) {
                myTreatyNameList.add(myTreaty.treatyName);
            }

            List<String> targetTreatyList = new ArrayList<>(hTreatyMap.keySet());
            targetTreatyList.removeAll(myTreatyNameList);

            logger.info("가입설계에 추가해야 할 특약 리스트 :: {}", targetTreatyList);

            throw new TreatyMisMatchException(targetTreatyList + "의 특약들을 추가해야 합니다.");

        } else {
            //Wrong Case : 가입설계의 특약 개수가 더 많을 때. 이 경우 가입설계에서 어떤 특약이 제거돼야 한다고 알려야 함.

            List<String> myTreatyNameList = new ArrayList<>();
            for(CrawlingTreaty myTreaty : info.treatyList) {
                myTreatyNameList.add(myTreaty.treatyName);
            }

            List<String> targetTreatyList = new ArrayList<>(hTreatyMap.keySet());
            myTreatyNameList.removeAll(targetTreatyList);

            logger.info("가입설계에서 제거돼야 할 특약 리스트 :: {}", myTreatyNameList);

            throw new TreatyMisMatchException(myTreatyNameList + "의 특약들을 제거해야 합니다.");

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
}
