package com.welgram.crawler.direct.fire.hwf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.NotFoundInsTermException;
import com.welgram.common.except.NotFoundNapTermException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.*;
import com.welgram.common.except.crawler.setUserInfo.*;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public abstract class CrawlingHWFMobile extends CrawlingHWFNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";

        WebElement $birthInput = (WebElement) obj[0];
        String expectedFullBirth = (String) obj[1];
        String actualFullBirth = "";

        try {
            //생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

            WaitUtil.waitFor(1);

            //생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";
        try {
            WebElement $element = (WebElement) obj[0];

            String expectedGenderText = (String) obj[1];
            String actualGenderText = "";

            // 1. 성별 클릭
            helper.waitElementToBeClickable($element).click();

            // 2. 실제 홈페이지에서 클릭된 성별 확인
            String script = "return $(\"input[name='ctrctGender']:checked\").attr('id')";
            String checkedElId = String.valueOf(executeJavascript(script));
            actualGenderText = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();

            // 3. 성별 비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {

    }

    @Override
    public void setJob(Object... obj) throws SetJobException {
        String title = "직업";
        String script = "arguments[0].click();";
        String expectedJobText = (String) obj[0];

        try {
            // 1. 분류대로 찾기 버튼 클릭
            logger.info("1. 분류대로 찾기 버튼 클릭");
            helper.waitElementToBeClickable(By.id("jobSet")).click();
            WaitUtil.waitFor(2);

            // 2. 사무종사자 클릭
            logger.info("2. 사무 종사자 클릭");
            element = driver.findElement(By.xpath("//ul[@id='jobCtg1Area']//a[text()='사무 종사자']"));
            executeJavascript(script, element);
            WaitUtil.waitFor(1);

            logger.info("3. 보험 사무원 클릭");
            element = driver.findElement(By.xpath("//ul[@id='jobCtg2Area']//a[text()='"+ expectedJobText + "']"));
            executeJavascript(script, element);
            WaitUtil.waitFor(1);

            logger.info("4. 보험 사무원 클릭");
            element = driver.findElement(By.xpath("//ul[@id='jobCtg3Area']//a[text()='"+ expectedJobText + "']"));
            executeJavascript(script, element);
            WaitUtil.waitFor(1);

            logger.info("확인 버튼 클릭");
            helper.waitElementToBeClickable(By.xpath("//div[@id='jobBtnArea']/a[text()='확인']")).click();
            WaitUtil.waitFor(2);

            String actualJobText =  driver.findElement(By.id("selectJobName")).getText();

            super.printLogAndCompare(title, expectedJobText, actualJobText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";

        String expectedInsTermText = (String) obj[0];
        String actualInsTermText = "";

        try {
            // 1.보험기간 클릭
            WebElement element = driver.findElement(By.xpath("//label[text()='" + expectedInsTermText + "']"));
            String elementId = element.getAttribute("for");
            WebElement inputEl = driver.findElement(By.id(elementId));

            if (inputEl.isEnabled()) {
                helper.waitElementToBeClickable(element).click();
            } else {
                String errorMsg = "해당 보험기간(" + expectedInsTermText + ")은 비활성화 되어있습니다.";
                logger.info(errorMsg);
                throw new NotFoundInsTermException(errorMsg);
            }

            // 2.실제 홈페이지에서 클릭된 보험기간 확인
            String checkedElId = ((JavascriptExecutor)driver).executeScript("return $('input[name=\"insTerms\"]:checked').attr('id');").toString();
            actualInsTermText = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();

            // 3.보험기간 비교
            super.printLogAndCompare(title, expectedInsTermText, actualInsTermText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";

        String expectedNapTermText = (String) obj[0];
        String actualNapTermText = "";

        try {
            // 1.납입기간 클릭
            WebElement element = driver.findElement(By.xpath("//label[text()='" + expectedNapTermText + "']"));
            String elementId = element.getAttribute("for");
            WebElement inputEl = driver.findElement(By.id(elementId));
            if (inputEl.isEnabled()) {
                helper.waitElementToBeClickable(element).click();
            } else {
                String errorMsg = "해당 납입기간(" + expectedNapTermText + ")은 비활성화 되어있습니다.";
                logger.info(errorMsg);
                throw new NotFoundNapTermException(errorMsg);
            }

            // 2.실제 홈페이지에서 클릭된 납입기간 확인
            String checkedElId = ((JavascriptExecutor)driver).executeScript("return $('input[name=\"payTerms\"]:checked').attr('id');").toString();
            actualNapTermText = driver.findElement(By.xpath("//label[@for='" + checkedElId + "']")).getText();

            // 3.납입기간 비교
            super.printLogAndCompare(title, expectedNapTermText, actualNapTermText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {

    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

    }

    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

    }

    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {

    }

    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {

    }

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {

    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {

    }

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {

    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        String title = "가입구분";

        String expectedProductTypeText = (String) obj[0];
        String actualProductTypeText = "";

        try {
            //1. 플랜 클릭
            helper.waitElementToBeClickable(By.xpath("//div[@class='section plan']/ul[@class='tabs']//span[text()='" + expectedProductTypeText + "']")).click();
            waitMobileLoadingImg();

            //2. 실제 홈페이지에서 클릭된 플랜 확인
            actualProductTypeText = driver.findElement(By.xpath("//div[@class='section plan']/ul[@class='tabs']/li[@class[contains(., 'active')]]/a")).getAttribute("title");
            logger.info("클릭된 플랜유형 : {}", actualProductTypeText);

            // 가입구분 비교
            super.printLogAndCompare(title, expectedProductTypeText, actualProductTypeText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }

    protected void getMobilePremiums(Object...obj) throws Exception {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        WebElement $element = (WebElement) obj[1];

        CrawlingTreaty mainTreaty = info.treatyList.get(0);

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");

        String monthlyPremium = $element.getText().replaceAll("[^0-9]", "");
        mainTreaty.monthlyPremium = monthlyPremium;

        if(!"0".equals(mainTreaty.monthlyPremium)) {
            logger.info("주계약 보험료 : {}원", monthlyPremium);
        } else {
            throw new Exception("주계약에 보험료를 세팅해주세요. 보험료는 0원일 수 없습니다.");
        }
    }

    // 모바일 환급금 크롤링
    protected void getMobileReturnPremiums(Object...obj) {

        List<WebElement> $trList = (List<WebElement>) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        for (WebElement $tr : $trList) {
            String term = $tr.findElement(By.xpath(".//td[1]")).getText();               //경과기간
            String premiumSum = $tr.findElement(By.xpath(".//td[2]")).getText();         //납입보험료
            String returnMoney = $tr.findElement(By.xpath(".//td[3]")).getText();     //환급금
            String returnRate = $tr.findElement(By.xpath(".//td[4]")).getText();      //환급률

            logger.info("***해약환급금***");
            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);
            logger.info("|--해약환급금: {}", returnMoney);
            logger.info("|--환급률: {}", returnRate + "\n");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }
        info.planReturnMoneyList = planReturnMoneyList;

        logger.info("만기환급금 : {}", info.returnPremium);
    }

    // 팝업 체크
    protected void checkPopup() throws InterruptedException {
        try {
            element = driver.findElement(By.xpath("//div[@class='new_tostpopup_wrap active']//div[text()='확인']"));
            helper.waitElementToBeClickable(element).click();
            logger.info("팝업 확인 버튼 클릭");
            WaitUtil.waitFor(1);
        } catch (Exception e) {
            logger.info("팝업 존재하지 않음");
            WaitUtil.waitFor(1);
        }
    }

    // 버튼 클릭
    protected void clickConfirmButton(Object...obj) throws Exception {
        WebElement $element = (WebElement) obj[0];
        try {
            helper.waitElementToBeClickable($element).click();
            waitMobileLoadingImg();
        } catch (Exception e) {
            logger.info("버튼 클릭 오류입니다.");
        }
    }

    protected void reCalcButtonClick() {
        //보기,납기 세팅을 변경하면 다시계산버튼이 나올 때가 있음.
        WebElement $element = driver.findElement(By.xpath("//a[text()='다시계산']"));
        boolean isExistReCalcBtn = $element.findElement(By.xpath("./ancestor::div[1]")).isDisplayed();
        if (isExistReCalcBtn) {
            executeJavascript("window.scrollTo(0, 0);");
            logger.info("다시계산 버튼 클릭");
            helper.waitElementToBeClickable($element).click();

            waitMobileLoadingImg();
        }
    }
    //모바일용 로딩바 대기
    protected void waitMobileLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.xpath("//div[@class[contains(., 'load')]]"))));
    }

    // script 실행
    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }
}
