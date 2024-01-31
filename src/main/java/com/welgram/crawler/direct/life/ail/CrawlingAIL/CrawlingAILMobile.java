package com.welgram.crawler.direct.life.ail.CrawlingAIL;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetPrevalenceTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;



// 2023.05.19 | 최우진 | AIL 모바일 크롤링
public abstract class CrawlingAILMobile extends CrawlingAILNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String xpath1 = (String) obj[0];
        String xpath2 = (String) obj[1];
        CrawlingProduct info = (CrawlingProduct) obj[2];

        try {
            WebElement $inputBirth = driver.findElement(By.xpath((xpath1)));
            $inputBirth.sendKeys(info.getBirth());

            WebElement $inputGender = driver.findElement(By.xpath((xpath2)));
            $inputGender.sendKeys(String.valueOf(info.getGender()));

            logger.info("생년월일 설정 :: {}", info.getBirth());
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new SetBirthdayException(e.getMessage() + "생년월일/성별 입력중 에러 발생");
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        try {
            info.gender = (Integer.parseInt(info.getFullBirth().substring(0,4)) >= 2000)
                ? ((info.getGender() == MALE)? 3 : 4 )
                : ((info.getGender() == MALE)? 1 : 2);

            logger.info("GENDER :: {}", info.getGender());
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new SetGenderException(e.getMessage() + "성별 설정중 에러발생");
        }
    }



    @Override public void setInjuryLevel(Object... obj) throws SetInjuryLevelException { }
    @Override public void setJob(Object... obj) throws SetJobException { }
    @Override public void setInsTerm(Object... obj) throws SetInsTermException { }
    @Override public void setNapTerm(Object... obj) throws SetNapTermException { }
    @Override public void setNapCycle(Object... obj) throws SetNapCycleException { }
    @Override public void setRenewType(Object... obj) throws SetRenewTypeException { }
    @Override public void setAssureMoney(Object... obj) throws SetAssureMoneyException { }
    @Override public void setRefundType(Object... obj) throws SetRefundTypeException { }
    @Override public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException { }
    @Override public void setAnnuityType(Object... obj) throws SetAnnuityTypeException { }
    @Override public void setUserName(Object... obj) throws SetUserNameException { }
    @Override public void setDueDate(Object... obj) throws SetDueDateException { }
    @Override public void setTravelDate(Object... obj) throws SetTravelPeriodException { }
    @Override public void setProductType(Object... obj) throws SetProductTypeException { }
    @Override public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException { }
    @Override public void setVehicle(Object... obj) throws SetVehicleException { }
    @Override public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException { }
    @Override public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException { }
    @Override public void crawlPremium(Object... obj) throws PremiumCrawlerException { }
    @Override public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException { }

    // todo | AIL 모바일 공통내용

    // 01. 모바일 크롤링 옵션 설정
    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setMobile(true);
    }



    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉



    // AIL 공시실 에서 사용중인 event(01.버튼클릭)
    protected void pushButton(Object... obj) throws CommonCrawlerException {

        String xPath = (String) obj[0];
        int eventTerm = (int) obj[1];
        try {
            driver.findElement(By.xpath(xPath)).click();
            logger.info("BUTTON xPath :: [{}]", xPath);
            WaitUtil.loading(eventTerm);

        } catch(Exception e) {
            throw new CommonCrawlerException(e.getMessage() + "이벤트 처리중 에러발생");
        }
    }

    // 02. 모바일 사이트 로딩바 명시적 대기
    protected void waitMobileLoadingBar() throws Exception {

        wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.xpath("//div[@class[contains(., 'load')]]"))));
    }


    // 03. element 클릭 가능한 상태가 될 때까지 대기
    protected WebElement waitElementToBeClickable(By by) throws Exception {

        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }


    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // 아래에서 부터는 재확인 필요



    // 04. element 클릭 가능한 상태가 될 때까지 대기
    protected WebElement waitElementToBeClickable(WebElement element) throws Exception {

        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }



    // 05. input 태그에 text 입력
    protected void setTextToInputBox(WebElement inputBox, String text) throws Exception {

        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }



    // 06. input 태그에 text 입력
    protected void setTextToInputBox(By element, String text) throws Exception {

        WebElement inputBox = driver.findElement(element);
        setTextToInputBox(inputBox, text);
    }



    // 07. 해당 element가 존재하는지 여부를 리턴
    protected boolean existElement(By by) {

        boolean isExist = true;
        try {
            driver.findElement(by);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }



    // 07.02. 해당 element가 존재하는지 여부를 리턴
    protected boolean existElement(WebElement rootEl, By by) {

        boolean isExist = true;
        try {
            rootEl.findElement(by);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }



    // 08.
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {

        //element 좌표에서 70을 빼는 이유는 모바일 버전에서 헤더부분에 가려져 element 클릭이 안되기 때문이다.
        int posY = element.getLocation().getY() - 70;
        posY = (posY < 0) ? 0 : posY;
//        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + posY + ");");
        WaitUtil.waitFor(2);
    }



    protected void moveToElementByJavascriptExecutor(By by) throws Exception {

        WebElement element = driver.findElement(by);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
        WaitUtil.waitFor(2);
    }
}
