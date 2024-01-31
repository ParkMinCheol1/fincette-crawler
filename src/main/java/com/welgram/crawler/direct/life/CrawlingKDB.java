package com.welgram.crawler.direct.life;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.*;
import com.welgram.util.InsuranceUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

//KDB생명 상품 중 홈페이지에서 크롤링해오는 상품에 대해서는 HomepageCrawlingKDB를 상속받는다.
public abstract class CrawlingKDB extends SeleniumCrawler {

    /*
     * 크롤링 옵션 정의 메서드
     *  => KDB생명 VPN 막혀있음.
     * @param info : 크롤링상품
     *
     * (여기서 예외가 발생한다면 이 메서드를 호출한 보험상품파일의 Exception catch block에서 예외를 처리하게 된다.)
     * */
//    protected void setChromeOptionKDB(CrawlingProduct info) {
//        CrawlingOption option = new CrawlingOption();
//
//        option.setBrowserType(CrawlingOption.BrowserType.Chrome);
//        option.setImageLoad(true);
//        option.setUserData(false);
        //option.setVpn(new ChromeMudfishVpn(HostUtil.getUsername()));

//        info.setCrawlingOption(option);
//        CrawlingOption option = info.getCrawlingOption();
//        option.setBrowserType(CrawlingOption.BrowserType.Chrome);
//        option.setImageLoad(false);
//        option.setUserData(false);
//        info.setCrawlingOption(option);
//    }

    /*
     * 버튼 클릭 메서드
     * @param element : 클릭하고 싶은 element
     * */
    protected void btnClick(By element) throws Exception {
        driver.findElement(element).click();
        waitLoadingImg();
        WaitUtil.loading(2);
    }

    //보험료 확인 버튼 클릭 메서드
    protected void calcBtnClick() throws Exception {
        btnClick(By.id("btnCal"));
    }

    //결과 확인하기 버튼 클릭 메서드
    protected void resultBtnClick() throws Exception {
        btnClick(By.id("btnRslt"));
    }

    //해약환급금 확인하기 버튼 클릭 메서드
    protected void returnPremiumBtnClick() throws Exception {
        btnClick(By.id("btnShowDetail"));
    }

    //해약환급금 테이블 다 조회 후 마지막에 확인 버튼 클릭 메서드
    protected void okBtnClick() throws Exception {
        btnClick(By.linkText("확인"));
    }


    /*
     * option태그들 중 하나를 선택하는 메서드
     *
     * @param1 element : option 태그들
     * @param2 text : 찾고자하는 text
     * */
    protected void selectOption(By element, String text) {
        List<WebElement> optionList = driver.findElements(element);
        String myMoney = text;

        /*
         * 가입금액이 10000이 넘어가는 금액에 대해서는 10000으로 나눠준다.
         * KDB생명 홈페이지의 경우 만약 가입금액이 10000000(천만원)인 경우 option 태그의 value속성을 보면 1000으로 세팅되어 있기때문에.
         * */
        if (Integer.parseInt(text) >= 10000) {
            myMoney = String.valueOf(Integer.parseInt(text) / 10000);
        }

        for (WebElement option : optionList) {
            String targetMoney = option.getAttribute("value");
            if (targetMoney.equals(myMoney)) {
                option.click();
                break;
            }
        }
    }

    //로딩이미지 명시적 대기
    protected void waitLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("dialogProgress")));
    }

    /*
     * inputBox에 텍스트를 입력하는 메서드
     *
     * @param1 element : 입력할 inputBox
     * @param2 text : 입력할 text
     * */
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }

    protected void moveToElement(By location){
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(location);
        actions.moveToElement(element);
        actions.perform();
    }


    /*
     * 생년월일 설정 메서드(1개 입력)
     * @param fullBirth : 생년월일		ex.19900606
     * */
    protected void setBirth(String fullBirth) throws Exception{
        WaitUtil.waitFor(2);
        helper.waitElementToBeClickable(By.id("pYmd"));
        WaitUtil.waitFor(2);
        setTextToInputBox(By.id("pYmd"), fullBirth);
    }


    /*
     * 생년월일 설정 메서드(2개 입력)
     *  @param1 childBirth : 자녀 생년월일     ex.20050105
     *  @param2 parentBirth : 부모 생년월일    ex.19800606
     * */
    protected void setBirth(String childBirth, String parentBirth) throws Exception{
        logger.info("부모 생년월일 설정 : {}", parentBirth);
        setBirth(parentBirth);

        logger.info("자녀 생년월일 설정 : {}", childBirth);
        setTextToInputBox(By.id("pChildYmd"), childBirth);
    }


    //출산예정일 설정 메서드(태아보험일 경우에만 사용된다)
    protected void setDueDate() {
        String dueDate = InsuranceUtil.getDateOfBirth(12);
        //오늘 날짜로부터 12주후가 출산예정일이 된다.

        setTextToInputBox(By.id("pFetusYmd"), dueDate);
    }


    /*
     * 성별 설정 메서드
     * @param gender : 성별  (0 : 남성, 1 : 여성)
     * */
    protected void setGender(int gender) throws Exception {
        String genderTag = (gender == MALE) ? "pGenderM" : "pGenderF";

        btnClick(By.cssSelector("label[for='" + genderTag + "']"));
    }


    /*
     * 성별 설정 메서드(2개 입력)
     * @param1 childGender :  성별  (0 : 남성, 1 : 여성)
     * @param2 parentGender : 성별  (0 : 남성, 1 : 여성)
     * */
    protected void setGender(int childGender, int parentGender) throws Exception {
        String childGenderTag = (childGender == MALE) ? "pChildGenderM" : "pChildGenderF";

        logger.info("부모 성별 설정 : {}", (parentGender == MALE) ? "남자" : "여자");
        setGender(parentGender);

        logger.info("자녀 성별 설정 : {}", (childGender == MALE) ? "남자" : "여자");
        btnClick(By.cssSelector("label[for='" + childGenderTag + "']"));
    }


    /*
     * 자녀 유형 설정 메서드
     *
     * @param childType : 자녀유형(어린이, 태아 중 하나)
     * */
    protected void setChildType(String childType) throws Exception {
        String childTypeTag = "pChildYnC";        //기본은 어린이로 설정

        if (childType.equals("태아")) {
            childTypeTag = "pChildYnF";
        }

        logger.info("자녀유형 : {}", childType);
        btnClick(By.cssSelector("label[for='" + childTypeTag + "']"));
    }


    /*
     * 상품 유형 설정 메서드
     *
     * @param productType : 상품유형		ex. 갱신형, 순수형, 표준형, 해약환급금 미지급형...
     * */
    protected void setProductType(String productType) throws Exception {
//		List<WebElement> labelList = driver.findElements(By.cssSelector("#PINSTYPE_AREA label"));
        List<WebElement> labelList = driver.findElements(By.cssSelector("#PINSTYPE_AREA li label"));

        // 태그를 읽어오면서 유형명 뿐아니라 안내 팝업(ex.갱신형 안내 팝업으로 이동)까지 읽어오는 것을
        // 수정하기 위해 subString을 사용하여 문자열의 범위 설정
        for (WebElement label : labelList) {
            String str = label.getText();
            int idx = str.indexOf("\n");
            String targetType = str.substring(0, idx);
            if (targetType.equals(productType)) {
                label.click();
                break;
            }
        }
    }

    /*
     * dropdown에서 가입금액 설정 메서드
     *
     * @param assureMoney : 가입금액		ex. 1천만원, 5천만원...
     * */
    protected void setAssureMoneyFromDropdown(String assureMoney) throws Exception {
        selectOption(By.cssSelector("#pAssureAmt option"), assureMoney);
    }


    /*
     * 버튼으로 가입금액 설정 메서드
     *  => element를 매개변수로 추가한 이유는 나머지 상품들의 가입금액 태그는 #PASSUREAMT_AREA label지만,
     *      KDB_CCR_D003 상품의 경우 #ASSUREAMT_AREA label이기 때문이다.
     *
     * @param1 element : 가입금액 element
     * @param2 assureMoney : 가입금액		ex. 1천만원, 5천만원...
     * */
    protected void setAssureMoneyFromBtn(By element, String assureMoney) throws Exception {
        String _assureMoney = String.valueOf(Integer.parseInt(assureMoney) / 10000);

        List<WebElement> labelList = driver.findElements(element);

        for (WebElement label : labelList) {
            String labelNum = label.getText().replaceAll("[^0-9]", "");
            if (labelNum.equals(_assureMoney)) {
                label.click();
                break;
            }
        }
    }


    /*
     * 보험기간 설정 메서드
     *  => 매개변수로 tag값을 받는 이유는 나머지 상품의 경우 태그값이 pInsTerm지만,
     *      KDB_SAV_D001 상품의 경우 pInsAmt이기 때문이다.
     *
     * @param1 tag : 태그값
     * @param2 insTerm : 보험기간     ex.5년,10년,15년...
     * */
    protected void setInsTerm(String tag, String insTerm) throws Exception {
        String _insTerm = insTerm.replaceAll("[^0-9]", "");
        btnClick(By.cssSelector("label[for='" + tag + _insTerm + "']"));
    }


    /*
     * 버튼으로 납입기간 설정 메서드
     *
     * @param napTerm : 납입기간     ex.전기납...
     * */
    protected void setNapTermFromBtn(String napTerm) throws Exception {

        String _napTerm = napTerm.replaceAll("[^0-9]", "");
        btnClick(By.cssSelector("label[for='pNapTerm" + _napTerm + "']"));
    }



    /*
     * dropdown에서 납입기간 설정 메서드
     *
     * @param napTerm : 납입기간     ex.전기납...
     * */
    protected void setNapTermFromDropdown(String napTerm) throws Exception {

        String _napTerm = napTerm.replaceAll("[^0-9]", "");
        selectOption(By.cssSelector("#pNapTerm option"), _napTerm);
    }



    protected void setMainTreatyPremium(CrawlingProduct info, By element) {

        WebElement premiumEl = driver.findElement(element);
        String monthlyPremium = premiumEl.getText().replaceAll("[^0-9]", "");

        info.treatyList.get(0).monthlyPremium = monthlyPremium;
    }



    /*
     * 버튼으로 월 보험료 설정 메서드(연금보험, 저축보험의 경우에만 사용된다)
     *  => 연금보험, 저축보험의 경우 월 보험료(가입금액)가 주계약 보험료가 된다.
     * */
    protected void setMonthlyPremiumByBtn(CrawlingProduct info) throws Exception {

        String assureMoney = String.valueOf(Integer.parseInt(info.assureMoney) / 10000);

        try {
            btnClick(By.cssSelector("label[for='pPayAmt" + assureMoney + "']"));

        } catch (NoSuchElementException e) {
            //Radio Button이 없는 보험료를 입력하기 위해 직접입력 버튼을 클릭
            helper.click(By.cssSelector("#PPAYAMT_AREA > li:nth-child(5) > label"));
            WebElement element = driver.findElement(By.id("pPayAmtEtc"));
            element.click();
            element.sendKeys(assureMoney);
        }
        info.treatyList.get(0).monthlyPremium = info.assureMoney;
    }



    /*
     * inputBox로 월 보험료 설정 메서드(연금보험, 저축보험의 경우에만 사용된다)
     *  => 연금보험, 저축보험의 경우 월 보험료(가입금액)가 주계약 보험료가 된다.
     * */
    protected void setMonthlyPremiumByText(CrawlingProduct info) throws Exception {

        String assureMoney = String.valueOf(Integer.parseInt(info.assureMoney) / 10000);

        setTextToInputBox(By.id("pPayAmt"), assureMoney);
        info.treatyList.get(0).monthlyPremium = info.assureMoney;
    }


    /*
     * 연금수령액 설정 메서드
     *
     * @param info : 크롤링상품
     * */
    protected void setAnnuityPremium(CrawlingProduct info) throws Exception {

        WaitUtil.waitFor(2);

        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        String whl10y = driver.findElement(By.cssSelector("#L10")).getText();
        String whl20y = driver.findElement(By.cssSelector("#L20")).getText();
        String whl30y = driver.findElement(By.cssSelector("#L30")).getText();
        String whl100a = driver.findElement(By.cssSelector("#L100")).getText();

        String fxd10y = driver.findElement(By.cssSelector("#F10")).getText();
        String fxd15y = driver.findElement(By.cssSelector("#F15")).getText();
        String fxd20y = driver.findElement(By.cssSelector("#F20")).getText();
        String fxd25y = driver.findElement(By.cssSelector("#F25")).getText();
        String fxd30y = driver.findElement(By.cssSelector("#F30")).getText();

        planAnnuityMoney.setWhl10Y(String.valueOf(MoneyUtil.toDigitMoney(whl10y)));
        planAnnuityMoney.setWhl20Y(String.valueOf(MoneyUtil.toDigitMoney(whl20y)));
        planAnnuityMoney.setWhl30Y(String.valueOf(MoneyUtil.toDigitMoney(whl30y)));
        planAnnuityMoney.setWhl100A(String.valueOf(MoneyUtil.toDigitMoney(whl100a)));

        planAnnuityMoney.setFxd10Y(String.valueOf(MoneyUtil.toDigitMoney(fxd10y)));
        planAnnuityMoney.setFxd15Y(String.valueOf(MoneyUtil.toDigitMoney(fxd15y)));
        planAnnuityMoney.setFxd20Y(String.valueOf(MoneyUtil.toDigitMoney(fxd20y)));
        planAnnuityMoney.setFxd25Y(String.valueOf(MoneyUtil.toDigitMoney(fxd25y)));
        planAnnuityMoney.setFxd30Y(String.valueOf(MoneyUtil.toDigitMoney(fxd30y)));
        info.planAnnuityMoney = planAnnuityMoney;

        if (info.annuityType.contains("종신 10년")) {
            info.annuityPremium = planAnnuityMoney.getWhl10Y();

        } else if (info.annuityType.contains("종신 20년")) {
            info.annuityPremium = planAnnuityMoney.getWhl20Y();

        } else if (info.annuityType.contains("종신 30년")) {
            info.annuityPremium = planAnnuityMoney.getWhl30Y();

        } else if (info.annuityType.contains("종신 100세")) {
            info.annuityPremium = planAnnuityMoney.getWhl100A();

        } else if (info.annuityType.contains("확정 10년")) {
            info.annuityPremium = planAnnuityMoney.getWhl10Y();
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd10Y();

        } else if (info.annuityType.contains("확정 15년")) {
            info.annuityPremium = planAnnuityMoney.getWhl10Y();
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd15Y();

        } else if (info.annuityType.contains("확정 20년")) {
            info.annuityPremium = planAnnuityMoney.getWhl10Y();
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd20Y();

        } else if (info.annuityType.contains("확정 25년")) {
            info.annuityPremium = planAnnuityMoney.getWhl10Y();
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd25Y();

        } else if (info.annuityType.contains("확정 30년")) {
            info.annuityPremium = planAnnuityMoney.getWhl10Y();
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd30Y();

        } else {
            logger.info("{} 을 찾을 수 없습니다.", info.annuityType);
            throw new Exception();
        }

        logger.info("info.annuityPremium :: {}", info.annuityPremium);
        logger.info("info.fixedAnnuityPremium :: {}", info.fixedAnnuityPremium);
        logger.info("|---보증--------------------");
        logger.info("|-- 10년 보증 :: {}", planAnnuityMoney.getWhl10Y());
        logger.info("|-- 20년 보증 :: {}", planAnnuityMoney.getWhl20Y());
        logger.info("|-- 30년 보증 :: {}", planAnnuityMoney.getWhl30Y());
        logger.info("|-- 100세 보증 :: {}", planAnnuityMoney.getWhl100A());
        logger.info("|---확정--------------------");
        logger.info("|-- 10년 확정 :: {}", planAnnuityMoney.getFxd10Y());
        logger.info("|-- 15년 확정 :: {}", planAnnuityMoney.getFxd15Y());
        logger.info("|-- 20년 확정 :: {}", planAnnuityMoney.getFxd20Y());
        logger.info("|-- 25년 확정 :: {}", planAnnuityMoney.getFxd25Y());
        logger.info("|-- 30년 확정 :: {}", planAnnuityMoney.getFxd30Y());
        logger.info("--------------------------");
    }



    /*
     * 연금개시 나이 설정 메서드
     * @param annAge : 연금개시나이     ex.65세, 66세...
     * */
    protected void setAnnuityAge(String annAge) throws Exception {

        setTextToInputBox(By.id("pBeginAge"), annAge);
    }



    /*
     * 특약 선택 메서드
     *  => 내 상품의 특약리스트의 특약명과 홈페이지의 특약명이 일치하는 애들을 클릭한다.
     *      element를 매개변수로 받는 이유는 KDB_DSS_D003상품의 경우 표준형 레시피와 고급형 레시피별
     *      특약 테이블의 태그값이 다르기 때문이다.
     *
     * @param1 treatyList : 크롤링상품의 특약리스트
     * @param2 element : 특약이 적혀있는 테이블의 태그값
     * */
    protected void setSubTreaties(List<CrawlingTreaty> treatyList, By element) throws Exception {

        List<WebElement> trList = driver.findElements(element);

        //홈페이지에서 특약명 조회
        for (WebElement tr : trList) {
            String targetTreatyName = tr.findElements(By.tagName("td")).get(1).getText();

            //내 상품의 특약리스트에서 특약명 조회
            for (CrawlingTreaty treaty : treatyList) {
                String myTreatyName = treaty.treatyName;

                //홈페이지 특약명과 내 특약리스트의 특약명이 일치하면 체크박스 클릭!
                if (targetTreatyName.equals(myTreatyName)) {
                    WebElement checkBox = tr.findElement(By.cssSelector("input[type='checkbox']"));
                    String checkboxId = checkBox.getAttribute("id");
                    WebElement checkBoxLabel = driver.findElement(By.cssSelector("label[for='" + checkboxId + "']"));

                    if (!checkBox.isSelected()) {
                        checkBoxLabel.click();
                    }

                    break;
                }
            }
        }

        WaitUtil.loading(2);
    }



    /*
     * 해약환급금 테이블의 td가 총 4개 있는 경우(경과기간, 납입보험료, 해약환급금, 환급률만 나온 경우 사용)
     *
     * @param info : 크롤링상품
     * */
    protected void getReturnPremiumTd4(CrawlingProduct info, By element) throws Exception {

        returnPremiumBtnClick();

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        List<WebElement> trList = driver.findElements(element);
        for (WebElement tr : trList) {
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

            logger.info("______해약환급급__________ ");
            logger.info("|--경과기간: {}", term);
            logger.info("|--납입보험료: {}", premiumSum);
            logger.info("|--해약환급금: {}", returnMoney);
            logger.info("|--최저납입보험료: {}", premiumSum);
            logger.info("|--환급률: {}", returnRate);
            logger.info("|_______________________");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.age));

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);
            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        okBtnClick();
    }



    /*
     * 해약환급금 테이블의 td가 총 5개 있는 경우
     *  => 이 경우에는 사실상
     *      td(0) : 경과기간
     *      td(1) : 해약환급금
     *      td(2) : 환급률
     *     의 내용만 크롤링해오면 된다.
     * */
    protected void getReturnPremiumTd5(CrawlingProduct info, By element) throws Exception {

        returnPremiumBtnClick();

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        List<WebElement> trList = driver.findElements(element);
        for (WebElement tr : trList) {
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(1).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(2).getText();

            logger.info("______해약환급급__________ ");
            logger.info("|--경과기간: {}", term);
            logger.info("|--해약환급금: {}", returnMoney);
            logger.info("|--환급률: {}", returnRate);
            logger.info("|_______________________");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.age));

            planReturnMoney.setTerm(term);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);
            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        okBtnClick();
    }



    /*
     * 해약환급금 조회 및 세팅 메서드(경과기간, 납입보험료, 최저.평균.공시 정보 모두 나온 경우 사용)
     *
     * @param info : 크롤링상품
     * */
    protected void getReturnPremiumTd8(CrawlingProduct info, By element) throws Exception {

        returnPremiumBtnClick();

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> trList = driver.findElements(element);
        for (WebElement tr : trList) {
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText();
            String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText();
            String returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();
            String returnMoney = tr.findElements(By.tagName("td")).get(6).getText();
            String returnRate = tr.findElements(By.tagName("td")).get(7).getText();


            logger.info("______해약환급급__________ ");
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

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);

            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        okBtnClick();
    }

//    protected void getFixedAnnuityPremium(CrawlingProduct info) throws Exception {
//        element = driver.findElement(By.cssSelector("#section2 > section:nth-child(2) > div.ins_etc_info_grid.clear"));
//        elements = element.findElements(By.tagName("h4"));
//
//        for (WebElement title : elements) {
//            if (title.getText().contains("확정연금형")) {
//                element = title.findElement(By.xpath("parent::*"));
//
//                List<WebElement> thList = element.findElements(By.tagName("th"));
//                for (int i = 0; i < thList.size(); i++) {
//                    if (thList.get(i).getText().contains("10년")) {
//                        String fixedPremium = element.findElements(By.tagName("td")).get(i).getText();
//                        info.fixedAnnuityPremium = fixedPremium.replaceAll("[^0-9]", "") + "0000";
//                        logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");
//                    }
//                }
//            }
//        }
//    }
}

