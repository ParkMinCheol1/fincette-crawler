package com.welgram.crawler.direct.life;


import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.*;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import java.text.DecimalFormat;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.*;
import org.openqa.selenium.support.ui.Select;

/**
 * @author SungEun Koo <aqua@welgram.com> 신한
 */
// 2022.04.01 | 최우진 | 신한라이프(SHL) 작업 및 관리자 변경 ..최우진
public abstract class CrawlingSHL extends SeleniumCrawler {

//    public static final Logger logger = LoggerFactory.getLogger(CrawlingSHL.class);

    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉               ▉▉▉▉▉                   ▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉    ▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉               ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉                    ▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉     ▉▉▉▉▉               ▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉


    /*
    DEPTH.00 - "UI 그대로 Follow" (UI를 그대로 따라가기) eff - 새로운 크롤링 담당자가 이해하기 편함, UI변경(흔한모달추가같은) 바로 확인 가능, 신규상품만들때 편함
    [ METHOD LIST ]


        todo | 우선은 모든 작업을 공시실 기준으로, 공통화는 섣부르게 하지 않음, annuity 내용에 대해 코드수정 필요
        - 공용 PROCESS
        initSHL()  ----------------------------------  초기화  ( 적당히 알림로깅, 필요한세팅, 카테고리추출, 타입추출, 텍스트타입확인 )
        finishSHL()  --------------------------------  Scrap Process 마지막 단계 ()

        - 공시실
        initShlAnnc() ----------------------------------  초기화  ( 적당히 알림로깅, 필요한세팅, 카테고리추출, 타입추출, 텍스트타입확인 )
        searchByProdInfo() -----------------------------  STEP00 ( 공시실에서 상품코드로 해당 상품 찾기 )
        fillInCustomerInfo() ---------------------------  STEP01 ( 고객정보 입력 )
        fillInMainTreatyInfo() -------------------------  STEP02 ( 주특약정보 입력 )
        fillInSubTreatiesInfo() ------------------------  STEP03 ( 일반특약정보 입력 )
        getResult() ------------------------------------  STEP04 ( 크롤링결과 가져오기 )
        finalize() -------------------------------------  마무리  ( 특약내용, 보험료, 해약환급금, 만기환급금, 환급률, TND정보 확인 )

        - 판매채널


    */


    
    // 공시실 | 초기화
    protected String[] initSHL(CrawlingProduct info) throws Exception {

        String category = "";
        String salesType = "";

        // 크롤링 시작 알림
        logger.info("대면보험의 경우 공시실의 정보를 크롤링합니다");

        // 카테고리 추출
        
        // 판매타입 추출
        
        // 텍스트타입 체크
        String[] tType = checkTextType(info);

        return tType;
    }

    // 공시실 | 초기화 | D00
    protected void initShlAnnc(CrawlingProduct info) {
        logger.info("대면보험의 경우 공시실의 정보를 크롤링합니다");
//        setCrawlingOption();
    }

    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉



    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(By element, String text) throws Exception {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }



    protected void waitMobileLoadingImg() throws Exception {
        wait.until(ExpectedConditions.attributeContains(By.id("mp_loading"), "style", "display: none;"));
//        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("mp_loading")));
    }



    //select box에서 text와 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(By element, String text) throws Exception {
        Select select = new Select(driver.findElement(element));
        select.selectByVisibleText(text);
    }



    //select box에서 text와 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(WebElement element, String text) throws Exception {
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }



    protected boolean selectOptionContainsText(By element, String text) throws Exception {
        boolean isFound = false;

        Select select = new Select(driver.findElement(element));

        List<WebElement> options = select.getOptions();
        for(WebElement option : options) {
            if(option.getText().contains(text)) {
                isFound = true;
                option.click();
                break;
            }
        }

        if(!isFound) {
            throw new Exception("해당 text(" + text + ")를 포함하는 option태그가 존재하지 않습니다.");
        }

        return isFound;
    }



    //모바일 성별 설정
    protected void setMobileGender(int gender) throws Exception {
        String elementId = (gender == MALE) ? "gender-type01" : "gender-type02";
        driver.findElement(By.cssSelector("label[for='" + elementId + "']")).click();

        //클릭된 성별이 맞게 클릭됐는지 검사
        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='gender-type']:checked\").attr('id')").toString();
        String checkedGender = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();

        logger.info("클릭된 성별 : {}", checkedGender);

        if(!checkedElId.equals(elementId)) {
            logger.info("성별이 잘못 클릭됐습니다.");
            logger.info("@@@클릭된 성별 : {}", checkedGender);
            logger.info("@@@가입설계 성별 : {}", (gender == MALE) ? "남자" : "여자");

            throw new Exception("성별 불일치");
        }
    }



    //모바일 생년월일 설정
    protected void setMobileBirth(String fullBirth) throws Exception {
        setTextToInputBox(By.id("birth"), fullBirth);
    }



    //모바일 월 보험료 설정
    protected void setMobilePremium(CrawlingProduct info) {
        String monthlyPremium = driver.findElement(By.id("prmSamt")).getText().replaceAll("[^0-9]", "");

        info.treatyList.get(0).monthlyPremium = monthlyPremium;

        logger.info("년 보험료 : {}원", monthlyPremium);
    }



    // (구)보험종류
    protected void setInsuranceType(String productKind) throws Exception {

        element = driver.findElement(By.name("iItp.etplItpDTO.insFormCd"));
        elements = element.findElements(By.tagName("option"));

        for (WebElement el : elements) {
            if (productKind.equals(el.getText())) {
                el.click();
                WaitUtil.loading(1);
                break;
            }
        }
    }



    //납입기간 Select Box
    protected void doSelectBox(By by, String value) throws Exception {
//        int Replaybutton = 0;

        wait.until(ExpectedConditions.elementToBeClickable(by));
        element = driver.findElement(by);
        elements = element.findElements(By.tagName("option"));
        boolean selected = false;

        // 현재 api에는 선택 안함이 없고 0원으로 되어 있기 떄문에 선택 안함 if문 처리
        if (value == "0") {
            value = "선택안함";
        }
        value = value.replace("년", "").replace("세", "");
        for (WebElement option : elements) {
            if (option.getAttribute("value").contains(value)) {
                option.click();
                logger.info(option.getText());
                selected = true;
                break;
            }
        }
        if (selected = false) {
//            if (Replaybutton == 0) {
//            option.click();
//            Replaybutton++;
            throw new NotFoundTextInSelectBoxException("selectBox에 text가 존재하지 않습니다.");
        }
        WaitUtil.loading(2);
    }



    // 자기부담률
    protected void setProductType(By id, String insuName) throws Exception {
        String text = "";
        element = driver.findElement(id);
        text = element.getText();
        text = text.substring(text.indexOf("(") + 1, text.length() - 1);

        if (insuName.contains(text)) {
            WaitUtil.waitFor();
        } else {
            element = element.findElement(By.xpath("parent::*"));
            element.click();
            WaitUtil.waitFor();
            elements = element.findElement(By.tagName("ul")).findElements(By.tagName("a"));

            for (WebElement a : elements) {
                logger.debug(a.getText());
                text = a.getText();
                text = text.substring(text.indexOf("(") + 1, text.length() - 1);
                if (insuName.contains(text)) {
                    a.click();
                    WaitUtil.waitFor();
                    break;
                }
            }
        }
    }



    // 직업선택
    protected void setJob() throws Exception {
        driver.findElement(By.cssSelector(".btn.btnCalJob")).click();
        helper.waitForCSSElement(".blockOverlay");

        element = driver.findElement(By.cssSelector("ul.careerSelect")).findElements(By.tagName("li")).get(0);
        element.click();
        helper.waitForCSSElement(".blockOverlay");

        driver.findElement(By.cssSelector("label[for=chk14122]")).click();
        WaitUtil.waitFor();

        driver.findElement(By.cssSelector(".popCont.career .btn.mediumType3")).click();
        WaitUtil.waitFor();
    }



    // 연금개시나이
    protected void setAnnAge(String annAge) throws Exception {
        boolean result = false;
        WaitUtil.waitFor();
        ((JavascriptExecutor) driver).executeScript("scroll(0,250);");
        element = driver.findElements(By.className("sbHolder")).get(1);
        element.click();
        WaitUtil.waitFor();

        element = element.findElement(By.className("sbOptions"));
        elements = element.findElements(By.tagName("li"));

        for (WebElement li : elements) {
            element = li.findElement(By.tagName("a"));
            if (element.getAttribute("rel").equals(annAge)) {
                li.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!result) {
            throw new Exception("연금개시나이 " + annAge + "세를 찾을 수 없습니다.");
        }
    }



    // 월 보험료 설정 (연금저축)
    protected void setPreimumY(String premium, CrawlingProduct info) throws InterruptedException {
        element = driver.findElement(By.id("customEntAm1"));
        elements = element.findElements(By.tagName("option"));

        // 만원 단위
        for (WebElement option : elements) {
            if (option.getAttribute("value").equals(premium)) {
                option.click();
                WaitUtil.waitFor();
                break;
            }
        }
        // 천원 단위
        element = driver.findElement(By.id("customEntAm2"));
        element = element.findElements(By.tagName("option")).get(0);
        element.click();
        WaitUtil.waitFor();

        // info.monthlyPremium = Integer.parseInt(info.premium + "0000");
        info.treatyList.get(0).monthlyPremium = info.assureMoney;

    }



    protected boolean confirmReturnPremium(CrawlingProduct info) throws Exception {
        boolean isFound = false;

        String[] page = new String[]{"5", "6", "7","8","9","10","11","12"};
        for(int i = 0; i<page.length; i++){
            try {
                driver.findElement(By.id("crownix-toolbar-move")).click();
                WaitUtil.waitFor(1);

                setTextToInputBox(By.cssSelector(".aTextbox"), page[i]);
                driver.findElement(By.xpath("//button[text()='확인']")).click();
                driver.findElement(By.xpath("//*[@id='m2soft-crownix-text'][contains(., '해약환급금 예시')]"));
                isFound = true;
                break;
            } catch (NoSuchElementException exception) {
                logger.info("{}페이지는 해약환급금 예시 페이지가 아닙니다.", page[i]);
            }
        }

        return isFound;
    }


    // 해약환급금
    protected void returnPremium(CrawlingProduct info) throws Exception {
        element = driver.findElements(By.className("dcConsult")).get(1);
        element.click();
        WaitUtil.waitFor();

        Set<String> windowId = driver.getWindowHandles();
        Iterator<String> handles = windowId.iterator();
        String subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            WaitUtil.waitFor();
        }

        driver.switchTo().window(subHandle);
        helper.waitForCSSElement(".ers_progress");

        for (int i = 1; i < 9; i++) {
            element = driver.findElements(By.className("crownix-toolbar-button")).get(4);
            element.click();
            Thread.sleep(1000);
        }

        element = driver.findElement(By.id("m2soft-crownix-text"));
        elements = element.findElements(By.tagName("div"));

        String returnPremium = "";
        int i = 0;

        // 해약환급금
        for (WebElement div : elements) {
            i++;
            if (div.getText().equals(info.napTerm)) {
                element = driver.findElement(By.id("m2soft-crownix-text"));
                element = element.findElements(By.tagName("div")).get(i + 4);
                returnPremium = element.getText().replace(",", "");
                info.returnPremium = returnPremium;
                info.errorMsg = "";
                logger.debug("######### 해약환급금: " + returnPremium + "원");
                break;
            }
        }
    }

    // 성별
    protected void setGender(int gender) throws Exception {

        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("tempInpa.gndrCd")));
        elements = element.findElements(By.tagName("option"));
        for (WebElement el : elements) {
            if (el.getAttribute("value").equals(Integer.toString(gender + 1))) {
                el.click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
                break;
            }
        }
    }

    // 성별 (web크롤링)
    protected void setGenderWeb(int gender) throws Exception {
        if (gender == MALE) {
            driver.findElement(By.xpath("//ul[@class='iptFilt']//label[@for='gndrScCd01']")).click();
        } else {
            driver.findElement(By.xpath("//ul[@class='iptFilt']//label[@for='gndrScCd02']")).click();
        }
        helper.waitForCSSElement(".blockUI.blockMsg.blockPage");
    }

    protected void setChildGenderWeb(int gender) throws Exception {
        if(gender == 0) {
            helper.click(By.cssSelector("label[for='child1']"));
        } else {
            helper.click(By.cssSelector("label[for='child2']"));
        }
        helper.waitForCSSElement(".blockUI.blockMsg.blockPage");
    }

    protected void moveToPlanSetting() throws Exception {
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(By.xpath("//*[@id='calculatorBoxArea']"));
        actions.moveToElement(element);
        actions.perform();
        WaitUtil.loading(1);
    }

    protected void moveToElement(By location){
        WebElement element = driver.findElement(location);
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
        actions.perform();
    }

    // 생년월일
    protected void setBirth(String birth) throws Exception {

        element = driver.findElement(By.cssSelector(
                "#trtyInfoForm > table > tbody > tr:nth-child(1) > td:nth-child(3) > input[type=text]:nth-child(1)"));
        element.clear();
        element.sendKeys(birth);
        element.sendKeys(Keys.TAB);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
    }

    // 이름
    protected void setName(String name) throws Exception {

        // 주피보험자
        element = driver.findElement(
                By.cssSelector("#trtyInfoForm > table > tbody > tr:nth-child(1) > td:nth-child(2) > input[type=text]"));
        helper.waitElementToBeClickable(element);
        WaitUtil.waitFor(2);
        element.clear();
        element.sendKeys(name);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
    }

    // 보험료 계산하기 버튼
    protected void getPlans() throws Exception {

//		element = driver.findElement(By.cssSelector("#trtyInfoForm > div.ac.mt20 > a"));
//		element.click();

        helper.click(By.cssSelector("#trtyInfoForm > div.ac.mt20 > a"));

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));

    }

    // 보험료 계산하기
    protected void calculatePremium() throws Exception {

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
        element = driver.findElement(By.cssSelector("#calculateInpFeDiv > a"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
    }

    // 보장기간
    protected void setInsTerm(WebElement el, String insTerm) throws Exception {

        boolean result = false;

        element = el.findElement(By.cssSelector("td > select[title=보험기간]"));
        elements = element.findElements(By.tagName("option"));

        for (WebElement option : elements) {

            if ((option.getText().indexOf(insTerm)) > -1) {
                option.click();
                result = true;
                logger.info("보험기간 " + insTerm + "이(가) 선택되었습니다.");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
//				waitFor();
                break;
            }
        }

        WaitUtil.loading(2);

        if (!result) {
            logger.error("보험기간 " + insTerm + "을 선택할 수 없습니다.");
            throw new Exception("보험기간 " + insTerm + "을 선택할 수 없습니다.");
        }
    }

    // 납입기간
    protected void setNapTerm(String productCode, WebElement el, String napTerm, CrawlingProduct info) throws Exception {

        boolean result = false;
        int StandardInsTerm = 0;
        if(!(productCode.equals("SHL_ASV_D002"))){
            StandardInsTerm = Integer.parseInt(info.insTerm.replaceAll("[^0-9]", ""));
        }
        int StandardNapTerm = Integer.parseInt(info.napTerm.replaceAll("[^0-9]", ""));

        WebElement selEl = wait
//                .until(ExpectedConditions.visibilityOfElementLocated(el.findElement(By.cssSelector("td > select[title=납입기간]"))));
                .until(ExpectedConditions.visibilityOf(el.findElement(By.cssSelector("td > select[title=납입기간]"))));
        elements = selEl.findElements(By.tagName("option"));
        if (productCode.equals("SHL_CCR_D002") || productCode.equals("SHL_CHL_D002") || productCode.equals("SHL_ACD_D001") || productCode.equals("SHL_TRM_D003") || productCode.equals("SHL_TRM_D004")) {
            if ((info.insTerm.equals(info.napTerm))){
                napTerm = "전기납";
            }
        } else if(productCode.equals("SHL_ASV_D002")){
            int StandardAnnAge = Integer.parseInt(info.annuityAge);
            int StandardAge = StandardAnnAge - StandardNapTerm;

            if(Integer.parseInt(info.age) == StandardAge){
                napTerm = "전기납";
            }
        }
        for (WebElement option : elements) {

            String optionText = option.getText();

            if (optionText.indexOf(napTerm) > -1) {
                option.click();
                result = true;
                logger.info("납입기간 " + napTerm + "이(가) 선택되었습니다.");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
//				waitFor();
                break;
            }
        }

        WaitUtil.loading(2);

        if (!result) {
            logger.error("납입기간 " + napTerm + "을 선택할 수 없습니다.");
            throw new Exception("납입기간 " + napTerm + "을 선택할 수 없습니다.");
        }
    }

    // 납입주기
    protected void setNapCycle(CrawlingProduct info) throws Exception {

        int num = 0;
        if (info.productCode.equals("SHL_CCR_D006") || info.productCode.equals("SHL_CHL_D002")) {
            num = 3;
        } else {
            num = 4;
        }

        elements = driver.findElements(By.className("sbHolder"));
        element = elements.get(num).findElement(By.className("sbSelector"));
        element.click();
        elements = driver.findElements(By.className("sbOptions")).get(num).findElements(By.tagName("a"));

        WaitUtil.loading(2);

        for (WebElement option : elements) {
            if (option.getAttribute("rel").equals(info.napCycle)) {
                option.click();
//				waitFor();
                break;
            }
        }
    }

    // 어린이 특약 미포함
    @Deprecated
    protected void setSpecial() throws Exception {
        elements = driver.findElements(By.className("sbHolder"));
        element = elements.get(4).findElement(By.className("sbSelector"));
        element.click();
        WaitUtil.waitFor();
        elements = driver.findElements(By.className("sbOptions")).get(4).findElements(By.tagName("a"));

        for (WebElement option : elements) {
            String special = option.getAttribute("rel");
            if (special.equals("false")) {
                option.click();
                helper.waitForCSSElement(".blockOverlay");
                WaitUtil.waitFor();
                break;
            }
        }
    }

    // 어린이
    @Deprecated
    protected void getPremium2(String premium) throws Exception {
        WaitUtil.waitFor();
        ((JavascriptExecutor) driver).executeScript("scroll(0,1200);");
        element = driver.findElements(By.className("sbHolder")).get(5);
        element.click();
        WaitUtil.waitFor();
        elements = element.findElement(By.tagName("ul")).findElements(By.tagName("li"));

        // 첫번째 rel 선택 반복문
        for (WebElement el : elements) {
            WebElement ele = el.findElement(By.tagName("a"));
            ele.sendKeys(Keys.UP);
            String rel = ele.getAttribute("rel");

            if (rel.equals("7000000")) {
                break;
            }
        }

        element.click();
        WaitUtil.waitFor();
        element.click();

        int premiumNum = Integer.parseInt(premium);
        // 해당 premium 선택 반복문
        for (WebElement option : elements) {

            WebElement el = option.findElement(By.tagName("a"));

            // 가입금액 오백만원 초과일 때
            if (premiumNum > 5000000) {
                el.sendKeys(Keys.DOWN);

                String rel = el.getAttribute("rel");
                int num = Integer.parseInt(rel);
                num = num + 1000000;
                String numString = String.valueOf(num);
                if (numString.equals(premium)) {
                    el.sendKeys(Keys.ENTER);
                    helper.waitForCSSElement(".blockOverlay");
                    break;
                }
            } else { // 가입금액 오백만원
                if (el.getAttribute("rel").equals(premium)) {
                    el.sendKeys(Keys.ENTER);
                    helper.waitForCSSElement(".blockOverlay");
                    break;
                }
            }
        }
    }

    // 보험가입금액 (암)
    @Deprecated
    protected void getPremiumCancer(String premium) throws Exception {
        WaitUtil.waitFor();
        ((JavascriptExecutor) driver).executeScript("scroll(0,1200);");
        element = driver.findElements(By.className("sbHolder")).get(4);
        element.click();
        elements = element.findElement(By.tagName("ul")).findElements(By.tagName("li"));

        // 첫번째 rel 선택 반복문
        for (WebElement el : elements) {
            WebElement ele = el.findElement(By.tagName("a"));
            ele.sendKeys(Keys.UP);
            String rel = ele.getAttribute("rel");

            if (rel.equals("29000000")) {
                break;
            }
        }

        element.click();
        WaitUtil.waitFor();
        element.click();

        int premiumNum = Integer.parseInt(premium);
        // 해당 premium 선택 반복문
        for (WebElement option : elements) {

            WebElement el = option.findElement(By.tagName("a"));

            // 가입금액 오백만원 초과일 때
            if (premiumNum > 5000000) {
                el.sendKeys(Keys.DOWN);

                String rel = el.getAttribute("rel");
                int num = Integer.parseInt(rel);
                num = num + 1000000;
                String numString = String.valueOf(num);
                if (numString.equals(premium)) {
                    el.sendKeys(Keys.ENTER);
                    helper.waitForCSSElement(".blockOverlay");
                    break;
                }
            } else { // 가입금액 오백만원
                if (el.getAttribute("rel").equals(premium)) {
                    el.sendKeys(Keys.ENTER);
                    helper.waitForCSSElement(".blockOverlay");
                    break;
                }
            }
        }
    }

    protected void setPremium(CrawlingProduct info) throws Exception {

        int weight = 10000;
        switch (info.getProductCode()) {
            case "SHL_ASV_D002":
                weight = 1000;
                break;
        }

        for (CrawlingTreaty treaty : info.treatyList) {

            if (ProductGubun.주계약.equals(treaty.productGubun)) {

                setMainTreaty(info.productCode, treaty.treatyName, treaty.assureMoney, treaty.insTerm, treaty.napTerm,
                        weight, info);

            } else {
                setSubTreaty(info.productCode, treaty.treatyName, treaty.assureMoney, treaty.insTerm, treaty.napTerm,
                        weight, info);
            }

            WaitUtil.loading(1);
        }

    }

    /**
     * 전체 보험료 결과 조회
     */
    protected void getCrawlingResults(List<CrawlingTreaty> treatyList) throws Exception {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("savePlanDiv")));

        for (CrawlingTreaty treaty : treatyList) {

            getCrawlingResult(treaty);
        }
    }

    // 특약 설정
    protected void setSubTreaty(String productCode, String treatyName, int assureMoney, String insTerm, String napTerm,
                                int weight, CrawlingProduct info) {

        elements = driver.findElements(By.cssSelector("#tb_BasCtttInfoList > tbody > tr"));

        for (WebElement trEl : elements) {

            List<WebElement> tdEls = trEl.findElements(By.tagName("td"));

            if (tdEls.size() > 2) {

                By by = By.cssSelector("td > input[type=checkbox][name=trtyChk]");

                try {

                    element = trEl.findElement(by);
                    helper.click(element);

                    String subject = "";

                    try {
                        {
                            By by2 = By.cssSelector("td:nth-child(2)");
                            WebElement nameElement = trEl.findElement(by2);
                            subject = nameElement.getText(); // 특약명
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }

                    if (treatyName.equals(subject)) {

                        logger.info("[특약]보험가입금액 설정");
                        setPremium(trEl, assureMoney, weight);
                        WaitUtil.loading(2);

                        logger.info("[특약]보험기간 설정");
                        setInsTerm(trEl, insTerm);
                        WaitUtil.loading(2);

                        logger.info("[특약]납입기간 설정");
                        setNapTerm(productCode, trEl, napTerm, info);


                    }

                } catch (Exception e) {
                    logger.error("[특약]" + e.getMessage());
                }
            }
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));

    }

    protected void selectPlan(By id, String value) throws Exception{
        boolean result = false;
        elements = driver.findElement(id).findElements(By.tagName("option"));
        for (WebElement option : elements) {
            if (option.getText().trim().equals(value)) {
                option.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!result) {
            throw new Exception("selectBox 선택 오류!");
        }
    }

    // 주계약 설정

    /**
     * 주계약 설정
     *
     * @param productCode 상품명
     * @param treatyName  특약명
     * @param assureMoney 가입금액
     * @param insTerm     보험기간
     * @param napTerm     납입기간
     * @param weight      단위(천,만원)
     */
    protected void setMainTreaty(String productCode, String treatyName, int assureMoney, String insTerm, String napTerm,
                                 int weight, CrawlingProduct info) throws Exception {

        elements = driver.findElements(By.cssSelector("#tb_BasCtttInfoList > tbody > tr"));

        for (WebElement trEl : elements) {

            List<WebElement> tdEls = trEl.findElements(By.tagName("td"));

            if (tdEls.size() > 2) {

                By by = By.cssSelector("td.subject");
                element = trEl.findElement(by);

                String subject = "";

                try {
                    subject = element.getText(); // 특약명
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

                if (treatyName.equals(subject)) {

                    logger.info("[주계약]보험가입금액 설정");
                    setPremium(trEl, assureMoney, weight);
                    WaitUtil.loading(2);

                    logger.info("[주계약]보험기간 설정");
                    setInsTerm(trEl, insTerm);
                    WaitUtil.loading(2);

                    logger.info("[주계약]납입기간 설정");
                    setNapTerm(productCode, trEl, napTerm, info);
                    WaitUtil.loading(2);
                    break;
                }

            }
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
    }

    // 보험종류(순수보장형 등)
    protected void setInsuranceKind(String productKind) {

        try {
            element = driver.findElement(By.id("insFormCd"));
            elements = element.findElements(By.tagName("option"));

            for (WebElement option : elements) {
                logger.info("setInsuranceKind productKind : " + productKind);
                logger.info("setInsuranceKind option.getText() : " + option.getText());
                if (option.getText().equals(productKind)) {
                    logger.info("보험종류 선택");
                    option.click();
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
                    break;
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    // 보험주기(월납/일시납)
    protected void setNapCycle(String napCycle) {

        try {

            element = driver.findElement(By.id("pamCyclCd"));
            elements = element.findElements(By.tagName("option"));

            for (WebElement option : elements) {

                String napCycleText = "";

                if ("01".equals(napCycle)) {
                    // 월납
                    napCycleText = "월납";
                } else if ("02".equals(napCycle)) {
                    // 년납
                    napCycleText = "년납";
                } else if ("00".equals(napCycle)) {
                    // 일시납
                    napCycleText = "일시납";
                }

                if (option.getText().equals(napCycleText)) {

                    option.click();
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
                    break;
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    // 보험가입금액 (생활비주는암보험, 신한스포츠&레저보장보험)
    protected void setPremium(WebElement el, int assureMoney, int weight) throws Exception {

        element = el.findElement(By.cssSelector("td:nth-child(4) > input[type=text]:nth-child(1)"));

        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(String.valueOf(assureMoney / weight));

        WaitUtil.loading(3);

        /*
         *** onchange, onblur 이벤트 발생시켜야 함 => TAB 키!! ***
         */
        element.sendKeys(Keys.TAB);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));

    }

    // 보험가입금액 (정기)
    protected void getPremium(String premium) throws InterruptedException {

        element = driver.findElements(By.className("sbHolder")).get(5);
        element.findElement(By.className("sbSelector")).click();

        elements = element.findElement(By.className("sbOptions")).findElements(By.tagName("a"));

        for (WebElement option : elements) {
            if (option.getText().replaceAll("[^0-9]", "").concat("0000").equals(premium)) {
                option.click();
                WaitUtil.waitFor();
                break;
            }
        }

    }

    // 직접설계 계산
    @Deprecated
    protected void directPremium() throws Exception {
        element = driver.findElement(By.className("btnCaltxt"));
        element.click();
        helper.waitForCSSElement(".blockOverlay");
    }

    // 실손 보험료 조회
    @Deprecated
    protected void getPremium(CrawlingProduct info) {
        String premium = "";
        elements = driver.findElements(By.cssSelector(".resultWrap"));

        if (info.insuName.contains("상해보장형")) {
            premium = elements.get(0).findElement(By.cssSelector(".price em")).getText().replaceAll("[^0-9]", "");
        }
        if (info.insuName.contains("종합보장형")) {
            premium = elements.get(1).findElement(By.cssSelector(".price em")).getText().replaceAll("[^0-9]", "");
        }
        if (info.insuName.contains("질병보장형")) {
            premium = elements.get(2).findElement(By.cssSelector(".price em")).getText().replaceAll("[^0-9]", "");
        }

        logger.debug("월 보험료: " + premium);
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }

    /**
     * 특약별 보험료 조회
     */
    protected void getCrawlingResult(CrawlingTreaty treaty) throws Exception {

        elements = driver.findElements(By.cssSelector("#tb_BasCtttInfoList > tbody > tr"));

        for (WebElement el : elements) {

            List<WebElement> tdEls = el.findElements(By.tagName("td"));

            if (tdEls.size() > 2) {

                By by1 = By.cssSelector("td.ar > input[type=hidden]"); // 보험료 element 조회
                By by2 = By.cssSelector("td.subject"); // 특약 element 조회

                element = el.findElement(by2);
                String subject = "";

                try {
                    subject = element.getText();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

                if (treaty.treatyName.equals(subject)) {

                    WebElement pEl = el.findElement(by1);
                    String pValue = pEl.getAttribute("value");
                    treaty.monthlyPremium = pValue;
                    break;
                }

            }
        }

    }

    // 보험료조회
    @Deprecated
    protected void getCrawlingResult(By by, CrawlingProduct info) {
        String premium = "";

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("calculateInpFeRsltDiv")));

        element = driver.findElement(by);
        premium = element.getText().replace(",", "").replace("원", "");

        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
        info.returnPremium = "0";
        info.annuityPremium = "0";
    }

    @Deprecated
    protected void closePopUp() throws InterruptedException {
        ((JavascriptExecutor) driver).executeScript("javascript:$('#annuity_event').dialog('close'); return false;");
        WaitUtil.waitFor();
    }

    protected void openAnnouncePage(CrawlingProduct info) throws InterruptedException, Exception {

        helper.closeOtherWindows();

        WebElement annouceEl = driver.findElement(By.id("insu_menu_44"));

        annouceEl.click();

        String productName = "";
        elements = driver.findElements(By.cssSelector(".insurance li"));

        for (WebElement li : elements) {
            List<WebElement> children = li.findElements(By.cssSelector("*"));

            productName = children.get(0).getText();

            if (productName.equals(info.productName)) {
                logger.info("해당 보험상품 팝업 페이지 열기");

                element = children.get(1);
                //element.click();
                element.sendKeys(Keys.ENTER);
                WaitUtil.waitFor();
                break;

            }

        }

        Set<String> windowId = driver.getWindowHandles();
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();

            logger.info(subHandle);
            WaitUtil.waitFor();
        }

        driver.switchTo().window(subHandle);
    }

    //

    /**
     * 해약환급금 조회
     */
    protected void getReturns(CrawlingProduct info) throws Exception {

        element = driver.findElement(By.cssSelector("#menu_btn > a:nth-child(3)"));
        element.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));
        WaitUtil.waitFor(1);

        elements = driver.findElements(By.cssSelector("#tbl_KA111S1 > tbody > tr"));    // 연금저축 외 해약환급금 테이블 id

        if (elements.size() > 1) {
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
            for (int i = 0; i < elements.size(); i++) {

                WebElement tr = elements.get(i);
                List<WebElement> tdList = tr.findElements(By.tagName("td"));

                Actions action = new Actions(driver);
                action.moveToElement(tr);
                action.perform();

                logger.info("______해약환급급[{}]_______ ", i);
                String term, age, premiumSum, returnMoney, returnRate;

                term = tdList.get(0).getText(); // 경과기간
                age = tdList.get(1).getText(); // 나이
                premiumSum = tdList.get(2).getText(); // 납입보험료
                returnMoney = tdList.get(3).getText(); // 해약환급금
                returnRate = tdList.get(4).getText(); // 환급률


                logger.info("|--경과기간: {}", term);
                logger.info("|--나이: {}", age);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender(info.getGenderEnum().name());
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term); // 경과기간
                planReturnMoney.setPremiumSum(premiumSum); // 보험료 합계
                planReturnMoney.setReturnMoney(returnMoney); // 환급금
                planReturnMoney.setReturnRate(returnRate); // 환급률

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } else {
            logger.info("해약환급금 내역이 없습니다.");
        }
    }

    protected void getWebPageReturnPremium(CrawlingProduct info) throws Exception {
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#m2soft-crownix-text > div"));

        String term = "";
        String premiumSum = "";
        String returnMoneyMin = "";
        String returnRateMin = "";
        String returnMoneyAvg = "";
        String returnRateAvg = "";
        String returnMoney = "";
        String returnRate = "";

        boolean read = true;
        boolean isChecked = false;
        int i = 0;

        while (true) {
            try{
                moveToElement(By.cssSelector("#m2soft-crownix-text > div:nth-child("+(i+10)+")"));
            } catch (NoSuchElementException e){
                break;
            }
            logger.info(elements.get(i).getText());

            // 3개월이 처음으로 나올 때까지 div를 continue
            if (!elements.get(i).getText().equals("3개월") && isChecked == false ) {
                read = false;
            } else {
                read = true;
                isChecked = true;
            }

            if(!read){
                i++;
                continue;
            }

            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

            term 			= elements.get(i).getText();
            logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
            premiumSum 		= elements.get(i + 2).getText();
            logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

            returnMoneyMin 	= elements.get(i + 3).getText();
            logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
            returnRateMin 	= elements.get(i + 4).getText();
            logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

            returnMoneyAvg 	= elements.get(i + 5).getText();
            logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
            returnRateAvg 	= elements.get(i + 6).getText();
            logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

            returnMoney     = elements.get(i + 7).getText();
            logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
            returnRate      = elements.get(i + 8).getText();
            logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnRate);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);;
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);

            planReturnMoneyList.add(planReturnMoney);

            i = i + 9;
//            if(!(elements.get(i).getText().contains("년") || elements.get(i).getText().contains("개월"))){
//                break;
//            }
        }
        info.returnPremium = returnMoney;
        logger.info("만기환급급 :: " + returnMoney);
        info.setPlanReturnMoneyList(planReturnMoneyList);

        takeScreenShot(info);
    }

    /**
     * 주계약 상품Master정보 조회
     */
    protected void getMainTreaty(CrawlingProduct info) {

        WebElement tr = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("#tb_BasCtttInfoList > tbody > tr:nth-child(2)")));

        List<String> productTypes = new ArrayList<>(Arrays.asList("갱신형"));

        logger.info("모니터링인경우 담보명을 크롤링한다");

        // 주보험 상품명

        logger.info("[{}]:주계약상품명 조회", info.insuName);
        String prdtNm = tr.findElement(By.cssSelector("#tb_BasCtttInfoList > tbody > tr:nth-child(2) > td.subject"))
                .getText();
        logger.debug("prdtNm: {}", prdtNm);

        logger.info("[{}]보험기간 조회", info.insuName);
        element = tr.findElement(By.cssSelector("td:nth-child(6) > select[title=보험기간]"));
        elements = element.findElements(By.tagName("option"));
        List<String> insTerms = new ArrayList<>();
        for (WebElement el : elements) {
            String insTerm = el.getAttribute("innerText");
            insTerms.add(insTerm);
            logger.debug("insTerms: {}", insTerms);

        }

        logger.info("[{}]납입기간 조회", info.insuName);
        element = tr.findElement(By.cssSelector("td:nth-child(7) > select[title=납입기간]"));
        elements = element.findElements(By.tagName("option"));
        List<String> napTerms = new ArrayList<>();
        for (WebElement el : elements) {
            String napTerm = el.getAttribute("innerText");
            napTerms.add(napTerm);
            logger.debug("napTerm: {}", napTerm);
        }

        logger.info("[{}]납입주기 조회", info.insuName);
        element = driver.findElement(By.id("pamCyclCd"));
        elements = element.findElements(By.tagName("option"));
        List<String> napCycles = new ArrayList<>();
        for (WebElement el : elements) {
            String napCycle = el.getAttribute("innerText");
            napCycles.add(napCycle);
            logger.debug("napCycle: {}", napCycle);
        }

        logger.info("[{}]가입금액 조회", info.insuName);
        String minAssureMoney = tr.findElement(By.cssSelector("td:nth-child(5) > input[type=text]:nth-child(1)"))
                .getAttribute("value").replace(",", "");
        String maxAssureMoney = tr.findElement(By.cssSelector("td:nth-child(5) > input[type=text]:nth-child(2)"))
                .getAttribute("value").replace(",", "");

        List<String> assureMoneys = new ArrayList<String>();
        assureMoneys.add(minAssureMoney);
        assureMoneys.add(maxAssureMoney);

        ProductMasterVO productMasterVO = new ProductMasterVO();
        productMasterVO.setProductId(info.productCode);
        productMasterVO.setProductKinds(info.defaultProductKind); // 정확히 알면 표기
        productMasterVO.setProductTypes(productTypes); // 정확히 알면 표기
        productMasterVO.setProductGubuns("주계약");
        productMasterVO.setSaleChannel(info.getSaleChannel());
        productMasterVO.setProductName(prdtNm);
        productMasterVO.setInsTerms(insTerms);
        productMasterVO.setNapTerms(napTerms);
        productMasterVO.setAssureMoneys(assureMoneys);
        productMasterVO.setMinAssureMoney(minAssureMoney);
        productMasterVO.setMaxAssureMoney(maxAssureMoney);
        productMasterVO.setCompanyId(info.getCompanyId());

        logger.info("상품마스터 :: " + productMasterVO.toString());
        info.getProductMasterVOList().add(productMasterVO);

    }

    protected void getSubTreaty(CrawlingProduct info) {

        logger.info("상품마스터 특약보험 입력시작");

//		element = waitPresenceOfElementLocated(By.id("tb_BasCtttInfoList"));
        elements = driver.findElements(By.cssSelector("#tb_BasCtttInfoList > tbody > tr"));
//		elements = element.findElements(By.cssSelector("tbody > tr"));

        logger.debug("tr size: {}", elements.size());
        for (int i = 0; i < elements.size(); i++) {

            if (i > 2) {

                WebElement tr = elements.get(i);

                logger.info("[{}]:특약상품명 조회", info.insuName);
                String prdtNm = tr.findElement(By.cssSelector("td.subject")).getText();
                logger.debug("prdtNm: {}", prdtNm);

                logger.info("[{}]보험기간 조회", info.insuName);
                element = tr.findElement(By.cssSelector("td:nth-child(6) > select[title=보험기간]"));
                elements = element.findElements(By.tagName("option"));
                List<String> insTerms = new ArrayList<>();
                for (WebElement el : elements) {
                    String insTerm = el.getAttribute("innerText");
                    insTerms.add(insTerm);
                    logger.debug("insTerms: {}", insTerms);

                }

                logger.info("[{}]납입기간 조회", info.insuName);
                element = tr.findElement(By.cssSelector("td:nth-child(7) > select[title=납입기간]"));
                elements = element.findElements(By.tagName("option"));
                List<String> napTerms = new ArrayList<>();
                for (WebElement el : elements) {
                    String napTerm = el.getAttribute("innerText");
                    napTerms.add(napTerm);
                    logger.debug("napTerm: {}", napTerm);
                }

                logger.info("[{}]납입주기 조회", info.insuName);
                element = driver.findElement(By.id("pamCyclCd"));
                elements = element.findElements(By.tagName("option"));
                List<String> napCycles = new ArrayList<>();
                for (WebElement el : elements) {
                    String napCycle = el.getAttribute("innerText");
                    napCycles.add(napCycle);
                    logger.debug("napCycle: {}", napCycle);
                }

                logger.info("[{}]가입금액 조회", info.insuName);
                String minAssureMoney = tr
                        .findElement(By.cssSelector("td:nth-child(5) > input[type=text]:nth-child(1)"))
                        .getAttribute("value").replace(",", "");
                String maxAssureMoney = tr
                        .findElement(By.cssSelector("td:nth-child(5) > input[type=text]:nth-child(2)"))
                        .getAttribute("value").replace(",", "");

                List<String> assureMoneys = new ArrayList<String>();
                assureMoneys.add(minAssureMoney);
                assureMoneys.add(maxAssureMoney);

                ProductMasterVO productMasterVO = new ProductMasterVO();
                productMasterVO.setProductId(info.productCode);
                productMasterVO.setProductKinds(info.defaultProductKind); // 정확히 알면 표기
                productMasterVO.setProductTypes(info.defaultProductType); // 정확히 알면 표기
                productMasterVO.setProductGubuns("선택특약");
                productMasterVO.setSaleChannel(info.getSaleChannel());
                productMasterVO.setProductName(prdtNm);
                productMasterVO.setInsTerms(insTerms);
                productMasterVO.setNapTerms(napTerms);
                productMasterVO.setAssureMoneys(assureMoneys);
                productMasterVO.setMinAssureMoney(minAssureMoney);
                productMasterVO.setMaxAssureMoney(maxAssureMoney);
                productMasterVO.setCompanyId(info.getCompanyId());

                logger.info("상품마스터 :: " + productMasterVO.toString());
                info.getProductMasterVOList().add(productMasterVO);

            }
        }
    }

    //웹 크롤링, 해약환급금 크롤링 (보험가입문서를 통한 크롤링)
    protected void getReturnPremium(CrawlingProduct info) throws Exception {
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        driver.findElement(By.xpath("//div[@id='m2soft-crownix-text']//div[text()='3개월']"));

        List<WebElement> elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#m2soft-crownix-text > div"));

        int idx = 0;
        for(int i=0; i<elements.size(); i++) {
            if(elements.get(i).getText().equals("3개월")) {
                idx = i+1;
                break;
            }
        }

        boolean isEnd = false;
        while (!isEnd) {
            try {
                moveToElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + idx + ")"));
                String term = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + idx + ")")).getText();
                String premiumSum = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+2) + ")")).getText();
                String returnMoney = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+3) + ")")).getText();
                String returnRate = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+4) + ")")).getText();

                if(term.length() > 4) {
                    throw new NoSuchElementException("경과기간에 해당하는 div가 아닙니다.");
                }

                logger.info("================================");
                logger.info("경과기간 : {}", term);
                logger.info("납입보험료 : {}", premiumSum);
                logger.info("해약환급금 : {}", returnMoney);
                logger.info("환급률 : {}", returnRate);

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney;

                idx += 5;
            }catch(NoSuchElementException e) {
                isEnd = true;
            }
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);

        logger.info("만기환급급 :: {}원", info.returnPremium);
    }

    // textType 조회
    protected String[] getArrTextType(CrawlingProduct info) {
        logger.info("텍스트타입(textType)에 저장된 내용을 가져옵니다");
        String tType = info.textType;
        String[] arrTType = tType.split("#");
        for(int i = 0; i < arrTType.length; i++) {
            arrTType[i] = arrTType[i].trim();
            logger.info(i + " : " + arrTType[i]);
            // ex)
            // 0 : 신한CEO정기보험(무배당, 보증비용부과형)
            // 1 : (20%)체증형
            // 2 : 일반심사형
        }
        return arrTType;
    }

    // CLICK #001
    protected WebElement waitForClickable(WebElement element) throws Exception{
        WebElement returnEl = null;
        if(element.isDisplayed() && element.isEnabled()) {
            returnEl = wait.until(ExpectedConditions.elementToBeClickable(element));
        } else {
            throw new Exception("클릭할 수 없는 ELEMENT 혹은 CONDITION 입니다.");
        }

        return returnEl;

    }



    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉               ▉▉▉▉▉                   ▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉    ▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉               ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉                    ▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉     ▉▉▉▉▉               ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉



    // 공시실 | textType 확인
    protected String[] checkTextType(CrawlingProduct info) throws Exception {
        try {
            String[] arrTType = info.getTextType().split("#");
            for(int i = 0; i < arrTType.length; i++) {
                arrTType[i] = arrTType[i].trim();
                if(arrTType.length != 1) {
                    logger.info("[ TEXTTYPE " + i + "번 ] :: " + arrTType[i]);
                } else {
                    if(StringUtils.isEmpty(arrTType[0])) {
                        logger.info("텍스트 타입이 존재하지 않습니다");
                    } else {
                        logger.info("텍스트 타입이 '1개' tType :: {}", arrTType[0]);
                    }
                }
            }

            return arrTType;

        } catch(Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_TEXTTYPE);
        }
    }



    // 공시실, 원수사 | 상품 검색 (검색창 검색 / 상품리스트 검색)
    protected void searchProdByTitle(String type, String title) throws Exception {

        WaitUtil.waitFor(3);        // 종종 첫로딩이 길어지는 케이스 때문에 대기시간 추가
        
        // validator
        if(StringUtils.isEmpty(type)) {
            throw new CommonCrawlerException("검색중 판매타입(대면/다이렉트)이 존재하지 않습니다");
        }

        if(StringUtils.isEmpty(title)) {
            throw new CommonCrawlerException("검색할 내용이 존재하지 않습니다");
        }

        // 모달 창 (2023.05.08)
        logger.info("모달 창 닫기");
        try {
            WaitUtil.waitFor(2);
//			driver.findElement(By.xpath("//*[@id='popu_100000000000500']/div[2]/div/div/div[3]/button")).click();
            driver.findElement(By.xpath("//span[text()='팝업 닫기']/parent::button")).click();
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            logger.info("모달창이 있었는데 없어졌습니다");
        }

        helper.findExistentElement(
                By.xpath("//div[@class='popContain']//button[@class='close']"), 1L)
            .ifPresent(el -> helper.click(el, "연금 저축 가입 이벤트 팝업 클릭"));

        // 원수사 크롤링용
        if(type.equalsIgnoreCase("D")) {

            logger.info("디지털 보험 상품 전체 레이블 리스트 모달 업");
            try {
                //            helper.doClick(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']"));
                driver.findElement(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']")).click();
                WaitUtil.waitFor(3);
            } catch(NoSuchElementException nsee) {
                logger.error("그런 모달창이 있었는데 없어졌습니다.");
            }


            logger.info("상품리스트에서 해당상품'{}'을 검색합니다({})", title, type);
            try {
                logger.info("'{}' 선택", title);
                driver.findElement(By.xpath("//ul[@class='mainDigiPrd']//div[text()='" + title + "']//parent::a")).click();
//                helper.doClick(By.xpath("//ul[@class='mainDigiPrd']//div[text()='" + title + "']//parent::a"));
//                wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//ul[@class='mainDigiPrd']//div[text()='" + title + "']//parent::a"))));
//                driver.findElement(By.xpath("//ul[@class='mainDigiPrd']//div[text()='" + title + "']//parent::a")).click();
                WaitUtil.waitFor(2);

                // 자동 화면전환

            } catch (Exception e) {
                throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SHL_INPUT_MAIN_TRT, e.getCause());
            }
        } 
        
        // 공시실 크롤링용
        else if(type.equalsIgnoreCase("F")) {
            logger.info("검색창에서 해당상품'{}'을 검색합니다({})", title, type);
            try {
//                helper.doSendKeys(By.id("meta04"), title);
//                helper.doClick(By.id("btnSearch"));
                driver.findElement(By.id("meta04")).sendKeys(title);
                driver.findElement(By.id("btnSearch")).click();
                WaitUtil.waitFor(2);

                pushButton(By.id("calc_0"), "LONG");        // 버튼누르기 | 화면전환

            } catch(Exception e) {
                throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SHL_SEARCH_BOX, e.getCause());
            }
        }
    }

    // 공시실, 원수사 | 상품 검색 (검색창 검색 / 상품리스트 검색)
    protected void searchProdByTitleANNC(String type, String title) throws Exception {

        // validation check
        if(StringUtils.isEmpty(type)) {
            throw new CommonCrawlerException("검색중 타입옵션(대면/다이렉트)이 존재하지 않습니다");
        }

        if(StringUtils.isEmpty(title)) {
            throw new CommonCrawlerException("검색할 내용이 존재하지 않습니다");
        }

        // 공시실 크롤링용
        else if(type.equalsIgnoreCase("F")) {
            logger.info("검색창에서 해당상품'{}'을 검색합니다({})", title, type);
            try {
//                helper.doSendKeys(By.id("meta04"), title);
//                helper.doClick(By.id("btnSearch"));
                driver.findElement(By.id("meta04")).sendKeys(title);
                driver.findElement(By.id("btnSearch")).click();
                WaitUtil.waitFor(2);

                pushButton(By.id("calc_0"), "LONG");        // 버튼누르기 | 화면전환

            } catch(Exception e) {
                throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SHL_SEARCH_BOX, e.getCause());
            }
        }
    }


    // 공시실 | 고객정보(피보험자)
    protected void inputCustomerInfo(String birth, int gender, String driveYn, String job) throws Exception {
        logger.info("▉  생년월일 (1/4)  ▉");
        try {
//            helper.doSendKeys(By.xpath("//input[@type='text'][@title='생년월일']"), birth);
            driver.findElement(By.xpath("//input[@type='text'][@title='생년월일']")).sendKeys(birth);
            WaitUtil.waitFor(1);
        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_BIRTH, e.getCause());
        }

        logger.info("▉  성별 (2/4)  ▉");
        try {
            String genderOpt = (gender == MALE) ? "filt1_1" : "filt1_2";
//            helper.doClick(By.xpath("//input[@id='" + genderOpt + "']//parent::li"));
            driver.findElement(By.xpath("//input[@id='" + genderOpt + "']//parent::li")).click();
            logger.info("result : {}", (gender == MALE) ? "남" : "여");
            WaitUtil.waitFor(1);
        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.INVALID_GENDER, e.getCause());
        }

        logger.info("▉  운전 (3/4)  ▉");
        try {
            Select select = new Select(driver.findElement(By.id("vhclKdCd")));
            select.selectByVisibleText(driveYn);
            WaitUtil.waitFor(1);
        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_USER_INFO, e.getCause());
        }

        logger.info("▉  직업 :: 사무직 - 경영지원 사무직 관리자 (4/4)  ▉");
        try {
//            helper.doClick(By.xpath("//span[text()='검색']//parent::button[@class='btn_t m btnJobPop']"));
//            helper.doSendKeys(By.id("jobNmPop"), job);
////            helper.doClick(By.id("btnJobSearchbtnJobSearch"));    // todo | 예외처리 테스트용(삭제가능)
//            helper.doClick(By.id("btnJobSearch"));
//            helper.doClick(By.xpath("//span[@class='infoCell'][text()='" + job + "']"));

            // ==================================================================

            driver.findElement(By.xpath("//span[text()='검색']//parent::button[@class='btn_t m btnJobPop']")).click();
            WaitUtil.waitFor(2);
            helper.sendKeys3_check(By.id("jobNmPop"), job);
            WaitUtil.waitFor(2);
//            driver.findElement(By.xpath("jobNmPop")).click();
//            driver.findElement(By.xpath("jobNmPop")).sendKeys(job);
            helper.click(By.id("btnJobSearch"));
            WaitUtil.waitFor(2);
//            driver.findElement(By.xpath("btnJobSearch")).click();
            driver.findElement(By.xpath("//span[@class='infoCell'][text()='" + job + "']")).click();
            WaitUtil.waitFor(2);

        } catch (Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.INVALID_JOB, e.getMessage());
        }

        logger.info("▉ 확인 버튼 클릭  ▉");
        try {
//            helper.doClick(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnCstCfn']"));
            driver.findElement(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnCstCfn']")).click();

            // todo | 정확한 이유를 알수 없지만 로딩화면에 대한 제어가 될때도 있고, 안될때도 있다 (대체적으로 모달창화면에서는 로딩페이지 처리가 안되는 경우가 많다)
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));
//            wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.xpath("//div[@class[contains(., 'load')]]"))));
            WaitUtil.waitFor(8);

        } catch(Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ELEMENT, e.getCause());
        }
    }



    // 공시실 | 주계약계산
    // todo |
    protected void inputMainTreatyInfo(String...infos) throws Exception {
        // 공시실 UI상 세로우선입니다 - 순서가 달라지면 다른 selectbox, checkbox의 내용이 달라지는 경우가 있으니 주의가 필요합니다.
        logger.info("SHL 공시실 '주계약계산'은 주계약에 대한 설정입니다");
        if(infos.length != 0) {
            switch (infos[0]){

                case "SHL_DMN_F003":
                case "SHL_DMN_F004":
                case "SHL_DMN_F005":
                case "SHL_DMN_F006":
                    setInsForm(infos[1]);           // 1. 보험형태
                    setInsType(infos[3]);           // 3. 보험종류
                    setNapipCycle(infos[5]);        // 5. 납입주기
                    setInsTerm(infos[2]);           // 2. 보험기간
                    setNapDuration(infos[4]);       // 4. 납입기간
                    setInputAssureMoney(infos[6]);  // 6. 가입금액
                    break;

                case "SHL_CCR_F004":
                case "SHL_CCR_F003":
                    setInsType(infos[1]);           // 1. 보험종류
                    setInsTerm(infos[2]);           // 2. 보험기간
                    dividePlanStyle(infos[3]);      // 3. 직종구분
                    setNapDuration(infos[4]);       // 4. 납입기간
                    setNapipCycle(infos[5]);        // 5. 납입주기
                    setInputAssureMoney(infos[6]);  // 6. 가입금액
                    break;
                    
                case "SHL_DTL_F001":
                    setInsForm(infos[1]);           // 1. 보험형태
                    setInsTerm(infos[2]);           // 2. 보험기간
                    dividePlanStyle(infos[3]);      // 3. 직종구분
                    setNapDuration(infos[4]);       // 4. 납입기간
                    setNapipCycle(infos[5]);        // 5. 납입주기
                    setInputAssureMoney(infos[6]);  // 6. 가입금액
                    break;

                case "SHL_CCR_F009" :
                    logger.info("SHL_CCR_F009");
                    setInsType(infos[1]);           // 1. 보험종류
                    setInsTerm(infos[2]);           // 2. 보험기간
                    setNapDuration(infos[3]);       // 4. 납입기간
                    setNapipCycle(infos[4]);        // 5. 납입주기
                    setInputAssureMoney(infos[5]);  // 6. 가입금액
                    break;

                default :
                    logger.error("ERROR :: 메서드내에 등록된 상품코드가 아닙니다");
                    logger.error("ERROR :: inputMainTreatyInfo()의 내용을 다시한번 확인해주세요..");
                    logger.error("ERROR :: @@@ 상품코드가 없다면 원수사 제공 기본설정으로 진행됩니다 @@@ ");

//                    throw new CommonCrawlerException("infos(arr)의 상품코드가 잘못되었습니다");

// todo | case가 없는 경우에 예외처리로 변경할수 없나? 그냥 IF-ELSE 써야하나
            }
        } else {
// todo | 정상케이스1 | 이것도 제대로 작동함
//            CommonCrawlerException cce = new CommonCrawlerException(ExceptionEnum.ERROR_BY_SHL_INPUT_MAIN_TRT, new NullPointerException());
// todo | 정상케이스2 | 제대로 작동함.. 기본적인 if-else 예외시, 처리 방법
//            NullPointerException npe = new NullPointerException();
//            CommonCrawlerException cce = new CommonCrawlerException(ExceptionEnum.ERROR_BY_SHL_INPUT_MAIN_TRT);
//            cce.initCause(npe);
// todo | 비정상케이스1 | 아래방법은 cce가 아니라 NullPointException 으로 빠짐
//            CommonCrawlerException cce = new CommonCrawlerException(ExceptionEnum.ERROR_BY_SHL_INPUT_MAIN_TRT, new NullPointerException().getCause());
//            throw cce;
// todo | 우선은 아래 방법으로 하자...
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SHL_INPUT_MAIN_TRT, new NullPointerException());
        }

// todo | 검증메서드 추가구역

        logger.info("확인 버튼 클릭");        // todo | 엘리먼트별 예외처리 필요
        helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnMnpr']"));
        WaitUtil.waitFor(4);
    }



    // 공시실 | 특약계산
//    protected void inputSubTreatyInfo(List<CrawlingTreaty> treaties) throws Exception {
    protected void inputSubTreatyInfo(CrawlingProduct info) throws Exception {
//        logger.info("선택특약 SIZE :: {}", treaties.size()); // todo | 선택특약 구분안되어있음
        List<CrawlingTreaty> treaties = info.getTreatyList();
        for(CrawlingTreaty eachTreaty : treaties) {
            String optTreatName = eachTreaty.treatyName;
            String optAmt = String.valueOf(eachTreaty.assureMoney/1_0000);
            String tempInsTerm = eachTreaty.getInsTerm();
            String tempNapterm = eachTreaty.getNapTerm();
            try {
                if(ProductGubun.선택특약.equals(eachTreaty.productGubun)) {

                    logger.info("=================================");
                    logger.info("▉ 선택특약 명 : " + optTreatName);
                    logger.info("▉ 선택특약 금액 : " + optAmt);
                    logger.info("▉ 선택특약 보험기간 : " + tempInsTerm);
                    logger.info("▉ 선택특약 납입기간 : " + tempNapterm);

                    String xpath = "//td[text()='" + optTreatName + "']/parent::tr";

                    WebElement inputChecker = driver.findElement(By.xpath(xpath + "/td[1]//input"));
                    WebElement chkBox = driver.findElement(By.xpath(xpath + "/td[1]//label"));
                    if(inputChecker.isEnabled() && !inputChecker.isSelected()) {
                        chkBox.click();
                        WaitUtil.waitFor(1);
                    } else {
                        logger.info("해당 선택특약의 체크박스를 체크할수 없는 상태(disabled||already selected)입니다");
                    }

                    WebElement elInput = driver.findElement(By.xpath(xpath + "/td[4]//input"));
                    if(elInput.isEnabled()) {
                        elInput.click();
                        elInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                        elInput.sendKeys(optAmt);
                        WaitUtil.waitFor(2);
                    } else {
                        logger.info("해당 선택특약의 가입금액 설정칸이 막혀있습니다(disabled)");
                    }

                    // 검증용 스크립트(작업단위내 공용)
                    String script = "return $(arguments[0]).find('option:selected').text();";
                    Select selectSubtrtInsTerm = new Select(driver.findElement(By.xpath(xpath + "//select[@title='보험기간']")));
                    selectSubtrtInsTerm.selectByVisibleText(tempInsTerm + "만기");

                    // 검증
                    String resultCheckSubInsterm = (String) ((JavascriptExecutor)driver).executeScript(script, selectSubtrtInsTerm);
                    if(resultCheckSubInsterm.equals(tempInsTerm + "만기")) {
                        logger.info("Select(보험기간)의 설정과 입력값이 일치합니다");
                    } else {
                        throw new CommonCrawlerException("Select(보험기간)의 설정과 입력값이 일치하지 않습니다");
                    }
                    WaitUtil.waitFor(2);

                    Select selectSubtrtNapTerm = new Select(driver.findElement(By.xpath(xpath + "//select[@title='납입기간']")));
                    selectSubtrtNapTerm.selectByVisibleText(tempNapterm + "납");
                    String resultCheckSubNapterm = (String) ((JavascriptExecutor)driver).executeScript(script, selectSubtrtNapTerm);
                    if(resultCheckSubNapterm.equals(tempNapterm + "납")) {
                        logger.info("Select(납입기간)의 설정과 입력값이 일치합니다");
                    } else {
                        throw new CommonCrawlerException("Select(납입기간)의 설정과 입력값이 일치하지 않습니다");
                    }
                    WaitUtil.waitFor(2);
                }
            } catch(Exception e) {
                throw new CommonCrawlerException("선택특약(" + optTreatName + ")의 설정시 에러가 발생하였습니다");
            }
        }
        WaitUtil.waitFor(1);

        try {
            logger.info("확인 버튼 클릭");
            helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnTrty']"));
            WaitUtil.waitFor(4);
        } catch(Exception e) {
            throw new CommonCrawlerException("특약계산(선택특약옵션설정)후 확인 버튼 클릭시 에러가 발생하였습니다");
        }
        WaitUtil.waitFor(3);
    }



    // 공시실, 원수사 | 크롤링 결과 확인 ([1]보험료확인, [2]스크린샷, [3]환급금)
    protected void checkResult(CrawlingProduct info, String returnMoneyOpt, String salesType, String prodCode) throws Exception {

        // 보험료 확인 (원수사)
        if(salesType.equalsIgnoreCase("D")) {
            try {
                String monthlyPremium  = driver.findElement(By.xpath("//em [@class='pointC5 sumInpFe']")).getText().replaceAll("[^0-9]", "");
                info.getTreatyList().get(0).monthlyPremium = monthlyPremium;
                logger.info("월 보험료 :: {}", monthlyPremium);

            } catch(Exception e) {
                throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM, e.getCause());
            }
        }

        // 보험료 확인 (공시실)휴게실이신ㄱ가
        else {
            try {
                pushButton(By.xpath("//span[text()='보험료계산']//parent::button[@class='btn_p btnInpFeCal']"),"LONG");

            } catch(Exception e) {
                throw new CommonCrawlerException(ExceptionEnum.ERR_BY_BUTTON , e.getCause());
            }

            logger.info("보험료 확인");
            try {
                String monthlyPremium = driver.findElement(By.xpath("//em[@class='rlpaAm']")).getText().replaceAll("[^0-9]", "");

// todo | 코드리뷰 필요(꼭 해야함)
                if(monthlyPremium.equals("")) {
                    logger.info("ERROR :: 보험료계산 이후 변화가 감지 되지않습니다");
                    logger.info("ERROR :: SHL_CCR_F004의 경우라면 보험금액미달로 공시실 계산이 불가능한 케이스로 의심됩니다");

                    if(prodCode.equals("SHL_CCR_F004")) {
                        throw new CommonCrawlerException("기준 보험료 미달..");
                    }
                }

                logger.info("월 보험료 : " + monthlyPremium);
                info.treatyList.get(0).monthlyPremium = monthlyPremium;
                WaitUtil.waitFor(1);
                // todo | '값없는 경우' 예외 필요
            } catch(Exception e) {
                throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM, e.getCause());
            }
        }

        // 스크린샷
        logger.info("스크린샷 찍기");
        try {
            takeScreenShot(info);
            logger.info("찰칵");
        } catch(Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_SCREENSHOT, e.getCause());
        }

        // 환급금 확인
// todo | 아래의 방법은 임의 수정입니다 형식에 맞게끔 알맞은 코드로 수정해야합니다
// todo | 만기환급/순수보장의 케이스도 구분해야 합니다
        String returnOpt = salesType + "_" + returnMoneyOpt;
        logger.info("KEY :: {}", returnOpt);

        try {
            switch (returnOpt.toUpperCase()) {
                case "D_FULL":
// todo | 다이렉트 풀 해약환급금 케이스
                    break;

                case "D_BASE":
                    checkReturnMoneyD(info);
                    break;

                case "F_FULL":
                    checkReturnMoneyFull(info);
                    break;

                case "F_BASE":
                    checkReturnMoney(info);
                    break;
            }

        } catch(Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY, e.getCause());
        }
    }



    protected void inputCustomerInfo(String gender, String birth) throws Exception {

        logger.info("성별 : {}", gender);
        try {
//            setGenderWeb(gender);
            driver.findElement(By.xpath("//label[text()='" + gender + "']")).click();
            WaitUtil.waitFor(1);
        } catch(Exception e) {
            throw new CommonCrawlerException("성별을 설정중 에러가 발생하였습니다");
        }

// todo | CCE 예외처리 변경필요
        logger.info("생년월일 : {}", birth);
//        helper.doSendKeys(By.xpath("//div[@class='iptWrap']//input[@name='birymd']"), birth);
        driver.findElement(By.xpath("//div[@class='iptWrap']//input[@name='birymd']")).sendKeys(birth);
        WaitUtil.waitFor(1);

// todo | CCE 예외처리 변경필요
        logger.info("'보험료 확인' 버튼 클릭");
        helper.click(By.id("btnCalInpFe"));
        WaitUtil.waitFor(4);
    }



    // 다이렉트 | SHL_CCR_D010 에서만 사용중
    protected void checkOptions(CrawlingProduct info) throws Exception {

        // 1. 보험형태
        Select selectInsForm = new Select(driver.findElement(By.id("selInsFormCd")));
        selectInsForm.selectByVisibleText("일반형");
        logger.info("보험형태 :: {}", "일반형");

        // 2. 납입주기
        // disabled 처리된 상태

        // 3. 보장기간 (보험기간)
        Select selectInsTerm = new Select(driver.findElement(By.id("selectMnprIsteCn")));
        selectInsTerm.selectByVisibleText(info.getInsTerm());
        logger.info("보장기간 :: {}", info.getInsTerm());

        // 4. 납입기간
        Select selectNapTerm = new Select(driver.findElement(By.id("selectMnprPmpeTc")));
        selectNapTerm.selectByVisibleText(info.getNapTerm());
        logger.info("납입기간 :: {}", info.getNapTerm());

        // 5. 보험가입금액
        DecimalFormat decForm = new DecimalFormat("###,###");
        String strAssureAmt = decForm.format(Integer.valueOf(info.getAssureMoney())) + "원";
        logger.info("strAssureAmt :: {}", strAssureAmt);
        Select selectInsAmt = new Select(driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select")));
        selectInsAmt.selectByVisibleText(strAssureAmt);
        logger.info("주계약 가입금액 :: {}", strAssureAmt);

        // 사용 버튼 추가
        WebElement $chk = driver.findElement(By.id("switch1"));
        logger.info("CHK EN:: {}", $chk.isEnabled());
        logger.info("CHK DP:: {}", $chk.isDisplayed());
        logger.info("CHK SL:: {}", $chk.isSelected());
        if(!$chk.isSelected()) {
            $chk.click();
            logger.info("CHK SL:: {}", $chk.isSelected());
        }
        
        // 6. 암사망특약(무배당)가입금액
        String strOptinalAssureAmt = decForm.format(info.getTreatyList().get(4).getAssureMoney()) + "원";
        logger.info("strOptinalAssureAmt :: {}", strOptinalAssureAmt);
        Select selectOptionalInsAmt = new Select(driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[2]/div/div[1]/select")));
        selectOptionalInsAmt.selectByVisibleText(strOptinalAssureAmt);

        logger.info("선택특약 가입금액 :: {}", strOptinalAssureAmt);

        try {
//            driver.findElement(By.id("btnGoSuco")).click();
            driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            logger.error("다시 계산하기 버튼이 없습니다");
            //throw new CommonCrawlerException("버튼 클릭중 에러 발생 [다시 계산하기]");
        }
    }



    // SHL_DSS_D006 에서만 사용중
    protected void checkOptions2(CrawlingProduct info) throws Exception {

        // 1. 보험종류
        Select selectInsForm = new Select(driver.findElement(By.id("selInsKdCd")));
        selectInsForm.selectByVisibleText("실속형");
        WaitUtil.waitFor(1);

        // 2. 납입주기
        // disabled 처리된 상태

        // 3. 보장기간 (보험기간)
        Select selectInsTerm = new Select(driver.findElement(By.id("selectMnprIsteCn")));
        selectInsTerm.selectByVisibleText(info.getInsTerm());
        WaitUtil.waitFor(1);

        // 4. 납입기간
        Select selectNapTerm = new Select(driver.findElement(By.id("selectMnprPmpeTc")));
        selectNapTerm.selectByVisibleText(info.getNapTerm());
        WaitUtil.waitFor(1);

        // 5. 보험가입금액
        DecimalFormat decForm = new DecimalFormat("###,###");
        String strAssureAmt = decForm.format(Integer.valueOf(info.getAssureMoney())) + "원";
        logger.info("strAssureAmt :: {}", strAssureAmt);
        Select selectInsAmt = new Select(driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select")));
        selectInsAmt.selectByVisibleText(strAssureAmt);
        WaitUtil.waitFor(1);

        try {
            driver.findElement(By.id("btnGoSuco")).click();
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            throw new CommonCrawlerException("[다시 계산하기] 버튼 클릭중 에러 발생");
        }
    }

    // SHL_DSS_D007 에서만 사용
    protected void checkOptions3(CrawlingProduct info)throws Exception {

        // 납입주기
        Select selNapCycle = new Select(driver.findElement(By.id("selPamCyclCd")));
        selNapCycle.selectByVisibleText(info.getNapCycleName());
        logger.info("납입주기 :: {}", info.getNapCycleName());

        // 보장기간
        Select selInsTerm = new Select(driver.findElement(By.id("selectMnprIsteCn")));
        selInsTerm.selectByVisibleText(info.getInsTerm());
        logger.info("보험기간 :: {}", info.getInsTerm());

        // 납입기간
        Select selNapTerm = new Select(driver.findElement(By.id("selectMnprPmpeTc")));
        selNapTerm.selectByVisibleText(info.getNapTerm());
        logger.info("납입기간 :: {}", info.getNapTerm());

        // 가입금액
        Select selAssAmt = new Select(driver.findElement(By.xpath("//select[@title='보험가입금액 '][@name='selectEntAm']")));
        selAssAmt.selectByValue(info.getAssureMoney());
        logger.info("가입금액 :: {}", info.getAssureMoney());

        // 다시계산하기
        try {
//            driver.findElement(By.id("btnGoSuco")).click();
            driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            logger.error("[다시 계산하기] 버튼이 있었는데 없어졌습니다.");
//            throw new CommonCrawlerException("[다시 계산하기] 버튼 클릭중 에러 발생");
        }
    }


    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉


//    protected void initSHL(CrawlingProduct info) throws Exception {
//
//// todo | substring으로 잘라야할듯.. String optFullString = (opt.equals("D"))? "direct" : "face";
//        String optFullString = "direct";
//
//        logger.info("textType 확인");
//        String[] arrTextType = info.getTextType().split("#");
//        for(int i = 0; i < arrTextType.length; i++ ) {
//            arrTextType[i] = arrTextType[i].trim();
//            logger.info("TEXT_TYPE :: {}", arrTextType[i]);
//            // 0 :
//            // 1 :
//        }
//
//        logger.info("상품 유형은 {} 입니다", optFullString);
//        if(optFullString.equals("direct")) {
//            logger.info("원수사 홈페이지 크롤링을 위한 초기화를 진행합니다");
//
//            // ======== 원수사 홈페이지 초기화 작업 : 1. 우영우 모달, 2. 전체상품리스트 켜기
//            // 1. 우영우 모달 끄기
//            try {
//                driver.findElement(By.xpath("//*[@id='popu_100000000000500']/div[2]/div/div/div[3]/button")).click();
//                WaitUtil.waitFor(1);
//            } catch(Exception e) {
//                logger.info("모달창이 있었는데 없어졌습니다");        // 모달이 안뜨는 경우가 있는데 정확한 이유를 잘 모르겠음
////            throw new CommonCrawlerException("모달에 대한 경로(xpath) 지정이 잘못되었습니다");
//            }
//
//            // 전체 상품 리스트
//            logger.info("디지털 보험 상품 전체 레이블 리스트 모달 업");
//            helper.doClick(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']"));
//            WaitUtil.loading(1);
//
//        } else {
//            logger.info("공시실 크롤링을 위한 초기화를 진행합니다");
//
//            // ======== 공시실 크롤링 초기화 작업 : 현재 없음 (2022.10.06)
//
//        }
//    }


    // DEPTH 0
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉            ▉▉▉▉▉▉▉                ▉▉▉▉               ▉▉▉▉▉                   ▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉   ▉▉ ▉▉▉▉   ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉       ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉    ▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉               ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉                    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉   ▉▉▉▉▉▉▉▉   ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉   ▉▉▉▉▉▉   ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉            ▉▉▉▉▉▉▉                ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉▉     ▉▉▉               ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉



    // 공시실 | 버튼누르기
    protected void pushButton(By by, String opt) throws Exception {

        // push button
        if(ObjectUtils.isEmpty(by)) {
            throw new CommonCrawlerException("By값 을 설정해주세요");
        }
        logger.info("버튼클릭!!");
        helper.click(by);

        // 대기시간 설정
        opt = opt.toUpperCase();
        if(opt.equals("LONG") || ObjectUtils.isEmpty(opt)) {
            WaitUtil.waitFor(6);
        } else {
            WaitUtil.waitFor(2);
        }
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));
    }



    // 공시실 | opt없는 버튼 누르기
    protected void pushButton(By by) throws Exception {
        pushButton(by, null);
    }



    // 공시실 | 주계약계산 | 보험형태
    private void setInsForm(String insForm) throws Exception {
        logger.info("보험형태 선택 :: {}", insForm);      // todo | null체크 필요 >> 예외처리 세분화
        try {
            Select selctInsForm = new Select(driver.findElement(By.xpath("//select[@title='보험형태']")));
            selctInsForm.selectByVisibleText(insForm);
            WaitUtil.waitFor(2);
        } catch (Exception e) {
            throw new CommonCrawlerException("(SELECTBOX) 보험형태[" + insForm + "]를 설정할 수 없습니다");
        }
    }



    // 공시실 | 주계약계산 | 보험기간
    private void setInsTerm(String tempInsTerm) throws Exception {
        logger.info("보험기간 선택(insTerm) :: {}", tempInsTerm);
        String strInsTerm = tempInsTerm + "만기";
        logger.info("insTerm :: {} >>> {}", tempInsTerm, strInsTerm);   // todo | null체크 필요 >> 예외처리 세분화
        try {
            Select selectInsPeriod = new Select(driver.findElement(By.xpath("//select[@title='보험기간']")));
            // todo | test후 삭제
            selectInsPeriod.selectByVisibleText(strInsTerm);
            logger.info("종신(##세형)  - default선택 >> 변경 없음");
            WaitUtil.waitFor(2);
        } catch(Exception e) {
//            throw new CommonCrawlerException("(SELECTBOX) [ INSTERM : "+ strInsTerm +" ]를 선택할수 없습니다");
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_INSTERM, e.getCause());
        }
    }



    // 공시실 | 주계약계산 | 보험종류
    private void setInsType(String insType) throws Exception {
        logger.info("보험종류 설정 :: {}", insType);  // todo | null체크 필요 >> 예외처리 세분화
        try {
            Select selctInsKind = new Select(driver.findElement(By.xpath("//select[@title='보험종류']")));
            selctInsKind.selectByVisibleText(insType);
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            throw new CommonCrawlerException("보험종류의 설정에 실패하였습니다");
        }
    }



    // 공시실 | 주계약계산 | 직종구분
    private void dividePlanStyle(String planStyle) throws Exception {
        logger.info("직종구분 선택 :: {}", planStyle);    // todo | null체크 필요 >> 예외처리 세분화
        try {
            Select selctPlanStyle = new Select(driver.findElement(By.xpath("//select[@title='직종구분']")));
            selctPlanStyle.selectByVisibleText(planStyle);
            WaitUtil.waitFor(2);
        } catch (Exception e) {
            throw new CommonCrawlerException("(SELECTBOX) 직종구분[" + planStyle + "]을 설정할 수 없습니다");
        }
    }



    // 공시실 | 주계약계산 | 납입기간
    private void setNapDuration(String tempNapDuration) throws Exception {
        logger.info("납입기간 설정 :: {}", tempNapDuration);
        String strNapDuration = tempNapDuration + "납";
        logger.info("napTerm :: {} >>> {}", tempNapDuration, strNapDuration);       // todo | null체크 필요 >> 예외처리 세분화
        try {
            Select selectNapDutaiopn = new Select(driver.findElement(By.xpath("//select[@title='납입기간']")));
            selectNapDutaiopn.selectByVisibleText(strNapDuration);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            throw new CommonCrawlerException("납입기간의 설정에 실패하였습니다");
        }

    }



    // 공시실 | 주계약계산 | 납입주기
    private void setNapipCycle(String napCycle) throws Exception {
        logger.info("납입주기 선택 :: {}", napCycle);     // todo | null체크 필요 >> 예외처리 세분화
        try {
            Select selctNapipCylce = new Select(driver.findElement(By.xpath("//select[@title='납입주기']")));
            selctNapipCylce.selectByVisibleText(napCycle);
            WaitUtil.waitFor(2);
        } catch(Exception e) {
//            throw new CommonCrawlerException("(SELECTBOX) 납입주기[" + napCycle + "]를 선택할수 없습니다");
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_NAPTERM, e.getCause());
        }
    }



    // 공시실 | 주계약계산 | 가입금액
    private void setInputAssureMoney(String assureMoney) throws Exception {
        logger.info("가입금액을 설정 :: {}", assureMoney);
        String strAssureMoney = String.valueOf(Integer.parseInt(assureMoney) / 10000);
        logger.info("assureMoney :: {} >>> {}", assureMoney, strAssureMoney);
        WebElement inputAssureMoney = null;
        String tempResult = "";
        try {
            inputAssureMoney = driver.findElement(By.xpath("//input[@title='가입금액']"));
            inputAssureMoney.click();
            inputAssureMoney.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            inputAssureMoney.sendKeys(strAssureMoney);

            WebElement nothing = driver.findElement(By.xpath("//h1[text()='보험료계산']"));
            nothing.click();

            tempResult = inputAssureMoney.getAttribute("value").replaceAll("[^0-9]", "");

            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_ASSUREMONEY, e.getCause());
        }

        // 임시 가격검증
        logger.info("검증 TEST strAssureMoney :: {}", strAssureMoney);
        logger.info("검증 TEST tempResult     :: {}", tempResult);
        if(!strAssureMoney.equals(tempResult) || tempResult.equals("")) {
            logger.info("input 값이 변질되었습니다");
            throw new CommonCrawlerException("알수없는 조건에 의한 값 변질");
        }
        
        // 검증 :: verify 추가 필요

    }



    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉               ▉▉▉▉▉                   ▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉       ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉    ▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉▉▉      ▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉   ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉               ▉▉▉▉▉              ▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉                    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉▉    ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉▉     ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉    ▉▉▉▉▉    ▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉     ▉▉▉▉▉    ▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉             ▉▉▉▉▉▉▉                ▉▉▉▉    ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉    ▉▉▉▉▉▉▉▉▉     ▉▉▉▉▉▉▉▉    ▉▉▉▉     ▉▉▉▉               ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉
    // ▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉▉


    // 공시실 | verifySHL






    // SHL | 공시실 | 크롤링 결과 확이 | 해약환급금확인 FULL (DEPTH:1)
    private void checkReturnMoneyFull(CrawlingProduct info) throws Exception {
        logger.info("해약환급금예시 확인");
        helper.click(By.xpath("//span[@class='scriptCell'][text()='해약환급금 예시']//parent::a"));
        WaitUtil.waitFor(2);
        // UI 형턔
        // (radioBtn) 최저보증이율 / (rBtn) 평균공시이율 / (rBtn) 공시이율
        // 경과기간		- 나이 		- 납입모험료 누계 		- 해약환급금 		- 환급률
        // 3개월 		- 30세		- 279,000				- 0					- 0.0
        // 6개월 		- 30세 		- 558,000				- 0 				- 0.0
        // 9개월 		- 30세 		- 837,000				- 0 				- 0.0
        // 1년	 		- 31세 		- 1,116,000				- 0 				- 0.0
        // 2년	 		- 32세 		- 2,232,000				- 196,029			- 8.7
        helper.click(By.cssSelector("#btnSubCocaSlct1 > label"));			// 최저보증이율
        List<PlanReturnMoney> pRMList = new ArrayList<>();
        List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
        for(WebElement trMin : trReturnMinInfoList) {
            String term = trMin.findElement(By.xpath("./td[1]")).getText();
//            String age = trMin.findElement(By.xpath("./td[2]")).getText();
            String premiumSum = trMin.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
            String returnMoneyMin = trMin.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
            String returnRateMin = trMin.findElement(By.xpath("./td[5]")).getText();

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);

            pRMList.add(planReturnMoney);
        }

        helper.click(By.cssSelector("#btnSubCocaSlct2 > label"));
        List<WebElement> trReturnAvgInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
        for(int idx = 0; idx < trReturnAvgInfoList.size(); idx++) {
            WebElement avgEl = trReturnAvgInfoList.get(idx);
            String returnMoneyAvg = avgEl.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
            String returnRateAvg = avgEl.findElement(By.xpath("./td[5]")).getText();

            pRMList.get(idx).setReturnMoneyAvg(returnMoneyAvg);
            pRMList.get(idx).setReturnRateAvg(returnRateAvg);
        }

        helper.click(By.cssSelector("#btnSubCocaSlct3 > label"));
        List<WebElement> trReturnInfoList = driver.findElements(By.xpath("//table[@id='tblRttrGood01']/tbody/tr"));
        for(int idx = 0; idx < trReturnInfoList.size(); idx++) {
            WebElement normEl = trReturnInfoList.get(idx);
            String returnMoney = normEl.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
            String returnRate = normEl.findElement(By.xpath("./td[5]")).getText();

            pRMList.get(idx).setReturnMoney(returnMoney);
            pRMList.get(idx).setReturnRate(returnRate);
        }

        logger.info("SIZE :: " + pRMList.size());
        pRMList.forEach(idx -> {
            logger.info("===================================");
            logger.info("TERM   : " + idx.getTerm());
            logger.info("SUM    : " + idx.getPremiumSum());
            logger.info("rmAMin : " + idx.getReturnMoneyMin());
            logger.info("rmRMin : " + idx.getReturnRateMin());
            logger.info("rmAAvg : " + idx.getReturnMoneyAvg());
            logger.info("rmRAvg : " + idx.getReturnRateAvg());
            logger.info("rmA    : " + idx.getReturnMoney());
            logger.info("rmR    : " + idx.getReturnRate());
            // rmA : returnmoneyAmount , rmR : returnmoneyRate
        });

        info.setPlanReturnMoneyList(pRMList);

        logger.info("===================================");
        logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
        logger.info("===================================");

    }



    // SHL | 공시실 | 크롤링 결과 확인 | 해약환급금확인 BASE (DEPTH:1)
    private void checkReturnMoney(CrawlingProduct info) throws Exception {
        logger.info("해약환급금예시 확인");
        helper.click(By.xpath("//span[@class='scriptCell'][text()='해약환급금 예시']//parent::a"));
        WaitUtil.waitFor(6);
//        wait.until();
        // (rBtn) 최저보증이율 / (rBtn) 평균공시이율 / (rBtn) 공시이율
        // 경과기간		- 나이 		- 납입모험료 누계 		- 해약환급금 		- 환급률
        // 3개월 		- 30세		- 279,000				- 0					- 0.0
        // 6개월 		- 30세 		- 558,000				- 0 				- 0.0
        // 9개월 		- 30세 		- 837,000				- 0 				- 0.0
        // 1년	 		- 31세 		- 1,116,000				- 0 				- 0.0
        // 2년	 		- 32세 		- 2,232,000				- 196,029			- 8.7
        List<PlanReturnMoney> pRMList = new ArrayList<>();
        List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//table[@id='tblInmRtFxty01']/tbody/tr"));
        for (WebElement trMin : trReturnMinInfoList) {
            String term = trMin.findElement(By.xpath("./td[1]"))
                .getText();
            String premiumSum = trMin.findElement(By.xpath("./td[3]"))
                .getText()
                .replaceAll("[^0-9]", "");
            String returnMoney = trMin.findElement(By.xpath("./td[4]"))
                .getText()
                .replaceAll("[^0-9]", "");
            String returnRate = trMin.findElement(By.xpath("./td[5]"))
                .getText();

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            pRMList.add(planReturnMoney);

            logger.info("===================================");
            logger.info("기간         : " + term);
            logger.info("납입보험료누계 : " + premiumSum);
            logger.info("해약환급금    : " + returnMoney);
            logger.info("환급률       : " + returnRate);
        }
        info.setPlanReturnMoneyList(pRMList);

        logger.info("===================================");
        logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
        logger.info("===================================");
    }

    
    
    // 원수사 | 해약환급금 확인 BASE
    private void checkReturnMoneyD(CrawlingProduct info) throws Exception {
        logger.info("해약환급금 조회 :: D_BASE");

        try {
//            ((JavascriptExecutor) driver).executeScript("scrollTo(0, 0);");
            driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
            WaitUtil.waitFor(2);
            // ex1)    	경과 	- 납입보험료 	- 해약환급금 	- 환급률
            //  		3개월 	- 15000원 		- 0원 			- 0.0%
            //			6개월	- 189,000원		- 0원			- 0.0%
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            int rowIndex = 1;
            boolean isValubale = true;
            while (isValubale) {
                try {
                    int colIndex = 1;
                    String term = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/th"))
                        .getText();
                    String premiumSum = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span"))
                        .getText()
                        .replaceAll("[^0-9]", "");
                    String returnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span"))
                        .getText()
                        .replaceAll("[^0-9]", "");
                    String returnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex) + "]"))
                        .getText();
                    rowIndex++;
//                    info.setReturnPremium(returnMoney);

                    logger.info("================================");
                    logger.info("경과기간 : {}", term);
                    logger.info("납입보험료 : {}", premiumSum);
                    logger.info("해약환급금 : {}", returnMoney);
                    logger.info("환급률 : {}", returnRate);

                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                    planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                    planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);
                    planReturnMoneyList.add(planReturnMoney);

                } catch (NoSuchElementException nsee) {
                    isValubale = false;
                    logger.info("=================================");
                    logger.error("더 이상 참조할 차트가 존재하지 않습니다");
                    logger.info("=================================");
                }
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
            if(info.getTreatyList().get(0).productKind == ProductKind.순수보장형) {
                info.setReturnPremium("0");
                logger.info("순수보장형 상품의 경우, 만기환급금이 존재하지 않습니다");
                logger.info("만기환급금 : {}", info.getReturnPremium());
            }
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new CommonCrawlerException("해약 환급금이 존재하지 않습니다.");
        }
    }

    
    
    // 특수 메서드 SHL_CCR_F004 | 해당 상품의 경우, 유동가입금액이 발생하여 관련한 내용을 plan_calc에 적재합니다
    protected void exceptionalFunc_shl_ccr_f004(CrawlingProduct info) throws Exception {
        logger.info("specialized :: SHL_CCR_F004");
        logger.info("계산결과 확인");
        helper.click(By.xpath("//span[@class='scriptCell'][text()='계산결과']//parent::a"));
        try {
            logger.info("유동 가입금액 확인");
            List<WebElement> trList = driver.findElements(By.xpath("//table[@class='tblX calRsltList']//tbody//tr"));
            for(WebElement eachTr : trList) {
                WebElement td1 = eachTr.findElement(By.xpath(".//td[1]"));
                for(CrawlingTreaty eachTrt: info.getTreatyList()) {
                    if(td1.getText().contains(eachTrt.treatyName) && eachTrt.treatyName.equals("진심을품은일반암진단특약A15(무배당, 갱신형)")) {
                        String joinAmt = eachTr.findElement(By.xpath(".//td[2]")).getText().replaceAll("[^0-9]", "").trim();
                        logger.info("JoinAmount :: {}", joinAmt);

                        PlanCalc pCalc = new PlanCalc();
                        pCalc.setMapperId(Integer.parseInt(eachTrt.mapperId));
                        pCalc.setGender((info.getGender() == MALE) ? "M" : "F");
                        pCalc.setInsAge(Integer.parseInt(info.age));
                        pCalc.setAssureMoney(joinAmt);
                        eachTrt.setPlanCalc(pCalc);
                    }
                }
            }
//            logger.info("=========  TOTAL  =========");
        } catch(Exception e) {
            throw new CommonCrawlerException("SHL_CCR_F004 | UNIQUE 에러 발생");
        }
        WaitUtil.waitFor(2);
    }

    protected void verifyInpuAssureMoney(String one, String two) {

        logger.info("TEST11 :: {}", one);
        logger.info("TEST2 :: {}", two);

    }
}

