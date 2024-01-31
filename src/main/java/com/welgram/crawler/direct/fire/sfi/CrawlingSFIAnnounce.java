package com.welgram.crawler.direct.fire.sfi;

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.*;
import com.welgram.common.except.crawler.setUserInfo.*;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class CrawlingSFIAnnounce extends CrawlingSFINew {

//    //계약정보 입력
//    public void setContractInfo(CrawlingProduct info) throws Exception {
//        boolean isExist = false;
//
//        logger.info("계약정보 입력");
//
//        By position = By.xpath("//label[normalize-space()='납입기간']");
//        isExist = helper.existElement(position);
//        if(isExist) {
//            logger.info("납입기간 설정");
//            setNapTerm(info.getNapTerm());
//        }
//
//        position = By.xpath("//label[normalize-space()='보험기간']");
//        isExist = helper.existElement(position);
//        if(isExist) {
//            logger.info("보험기간 설정");
//            setInsTerm(info.getInsTerm());
//        }
//
//        position = By.xpath("//label[normalize-space()='납입주기']");
//        isExist = helper.existElement(position);
//        if(isExist) {
//            logger.info("납입주기 설정");
//            setNapCycle(info.getNapCycleName());
//        }
//    }
//
//    //피보험자정보 입력
//    public void setUserInfo(CrawlingProduct info) throws Exception {
//        boolean isExist = false;
//
//        logger.info("피보험자정보 입력");
//
//        By position = By.xpath("//label[normalize-space()='생년월일']");
//        isExist = helper.existElement(position);
//        if(isExist) {
//            logger.info("생년월일 설정");
//            setBirthday(info.getFullBirth());
//        }
//
//        position = By.xpath("//label[normalize-space()='성별']");
//        isExist = helper.existElement(position);
//        if(isExist) {
//            logger.info("성별 설정");
//            setGender(info.getGender());
//        }
//
//        /**
//         * 세팅항목명 상해급수의 경우
//         * 상품별로 상해급수, 상해급수Ⅲ 등 표기명이 다르다.
//         * 따라서 normalize-space()가 아닌 contains()로 텍스트명을 찾는다.
//         */
//        position = By.xpath("//label[contains(., '상해급수')]");
//        isExist = helper.existElement(position);
//        if(isExist) {
//            logger.info("상해급수 설정");
//            setInjuryLevel("1급");
//        }
//
//        position = By.xpath("//label[normalize-space()='운전차용도']");
//        isExist = helper.existElement(position);
//        if(isExist) {
//            logger.info("운전차용도 설정");
//            setVehicle(info.getAge());
//        }
//
//        position = By.xpath("//label[normalize-space()='차량가입대수']");
//        isExist = helper.existElement(position);
//        if(isExist) {
//            logger.info("차량가입대수 설정");
//            setVehicleCnt("1");
//        }
//    }

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";

        WebElement $birthInput = (WebElement) obj[0];
        String expectedFullBirth = (String) obj[1];
        String actualFullBirth = "";

        try {
            if($birthInput.isEnabled()) {
                //생년월일 설정
                actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);
            } else {
                actualFullBirth = $birthInput.getAttribute("value");
                actualFullBirth = actualFullBirth.replaceAll("\\.", "");
            }

            //생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";

        WebElement $genderSelect = (WebElement) obj[0];
        int gender = (int) obj[1];
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {
            //성별 설정
            actualGenderText = helper.selectByText_check($genderSelect, expectedGenderText);

            //성별 비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {
        String title = "상해급수";


        WebElement $injuryLevelSelect = (WebElement) obj[0];
        String expectedInjuryLevel = (String) obj[1];
        String actualInjuryLevel = "";

        try {
            //상해급수Ⅲ 설정
            actualInjuryLevel = helper.selectByText_check($injuryLevelSelect, expectedInjuryLevel);

            //상해급수Ⅲ 비교
            super.printLogAndCompare(title, expectedInjuryLevel, actualInjuryLevel);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INJURY_LEVEL;
            throw new SetInjuryLevelException(e, exceptionEnum.getMsg());
        }
    }


    public void setHealthInsuranceYN(Object... obj) throws CommonCrawlerException {
        String title = "국민건강보험 가입여부";

        WebElement $healthSelect = (WebElement) obj[0];
        String expectedHealth = (String) obj[1];
        String actualHealth = "";

        try {

            //국민건강보험 가입여부 설정
            actualHealth = helper.selectByText_check($healthSelect, expectedHealth);

            //국민건강보험 가입여부 비교
            super.printLogAndCompare(title, expectedHealth, actualHealth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_NATIONAL_HEALTH_INSURANCE;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }




    @Override
    public void setJob(Object... obj) throws SetJobException {
        super.setJob(obj);
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";

        WebElement $insTermSelect = (WebElement) obj[0];
        String expectedInsTerm = (String) obj[1];
        String actualInsTerm = "";

        try {
            expectedInsTerm = expectedInsTerm + "만기";
            actualInsTerm = helper.selectByText_check($insTermSelect, expectedInsTerm);

            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";

        WebElement $napTermSelect = (WebElement) obj[0];
        String expectedNapTerm = (String) obj[1];
        String actualNapTerm = "";

        try {
            expectedNapTerm = (expectedNapTerm.contains("납")) ? expectedNapTerm : expectedNapTerm + "납";
            actualNapTerm = helper.selectByText_check($napTermSelect, expectedNapTerm);

            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";

        WebElement $napCycleSelect = (WebElement) obj[0];
        String expectedNapCycle = (String) obj[1];
        String actualNapCycle = "";

        try {
            //삼성화재 공시실의 경우 납입주기 "월납"을 "매월"로 표기함.
            expectedNapCycle = "월납".equals(expectedNapCycle) ? "매월" : expectedNapCycle;
            actualNapCycle = helper.selectByText_check($napCycleSelect, expectedNapCycle);

            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e, exceptionEnum.getMsg());
        }
    }


    public void setHealthType(Object... obj) throws SetNapCycleException {
        String title = "간편심사 유형";

        WebElement $napCycleSelect = (WebElement) obj[0];
        String expectedHealthType = (String) obj[1];
        String actualHealthType = "";

        try {
            actualHealthType = helper.selectByText_check($napCycleSelect, expectedHealthType);
            super.printLogAndCompare(title, expectedHealthType, actualHealthType);
        } catch (Exception e) {
            throw new SetNapCycleException(e);
        }
    }

    public void setAfterConversionInsTerm(String expectedInsTerm) throws CommonCrawlerException {
        String title = "전환후 보험기간";
        String actualInsTerm = "";

        try {
            WebElement $insTermSelect = driver.findElement(By.id("c_extnPrdCd"));
            actualInsTerm = helper.selectByText_check($insTermSelect, expectedInsTerm);

            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_AFTER_CONVERSION_INSTERM;
            throw new SetInsTermException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {
        super.setRenewType(obj);
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "1회 보험료";

        String expectedOneTimePremium = "1000000";
        String actualOneTimePremium = "";

        WebElement $oneTimePremiumInput = null;
        WebElement $calcBtn = null;
        WebElement $premiumSpan = null;
        String premium = "";

        try {

            //1회 보험료 설정
            logger.info("1회 보험료 임의 설정(실제 보장보험료를 구하기 위해 임의로 100만원을 세팅해본다)");
            $oneTimePremiumInput = driver.findElement(By.id("premaftertaxAm"));
            helper.sendKeys4_check($oneTimePremiumInput, expectedOneTimePremium);

            logger.info("보험료 계산 버튼 클릭");
            $calcBtn = driver.findElement(By.xpath("//span[normalize-space()='보험료 계산']/parent::button"));
            click($calcBtn);
            WaitUtil.waitFor(3);

            logger.info("보장보험료를 1회 보험료로 다시 설정한다(=적립보험료를 0원으로 만들기 위함)");
            $premiumSpan = driver.findElement(By.id("zaCovrPrm"));
            premium = $premiumSpan.getText();
            premium = premium.replaceAll("[^0-9]", "");
            expectedOneTimePremium = premium;

            // 최저 보험료를 맞춰주기 위함
            int expectedOneTimePremiumInteger = Integer.parseInt(expectedOneTimePremium);
            if (expectedOneTimePremiumInteger < 10000) {
                expectedOneTimePremium = String.valueOf(10000);
            }

            $oneTimePremiumInput = driver.findElement(By.id("premaftertaxAm"));
            actualOneTimePremium = helper.sendKeys4_check($oneTimePremiumInput, expectedOneTimePremium);
            actualOneTimePremium = actualOneTimePremium.replaceAll("[^0-9]", "");

            //비교
            super.printLogAndCompare(title, expectedOneTimePremium, actualOneTimePremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_ONE_TIME_PREMIUM;
            throw new SetAssureMoneyException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {
        super.setRefundType(obj);
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        String premium = "";
        String savePremium = "";
        String returnPremium = "";

        try {

            WebElement $premiumSpan = driver.findElement(By.id("zaCovrPrm"));
            WebElement $savePremiumSpan = driver.findElement(By.id("zaAcumPrm"));
            WebElement $returnPremiumStrong = driver.findElement(By.id("zz7ParngAmtAm"));

            premium = $premiumSpan.getText().replaceAll("[^0-9]", "");
            savePremium = $savePremiumSpan.getText().replaceAll("[^0-9]", "");
            returnPremium = $returnPremiumStrong.getText().replaceAll("[^0-9]", "");

            //보험료 정보 세팅
            info.savePremium = savePremium;
            info.returnPremium = returnPremium;
            mainTreaty.monthlyPremium = premium;

            if("태아보험".equals(info.getCategoryName())) {
                info.nextMoney = mainTreaty.monthlyPremium;
            }

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
                logger.info("적립보험료 : {}원", info.savePremium);
                logger.info("만기환급금 : {}원", info.returnPremium);
            }

        }  catch (Exception e) {
            throw new PremiumCrawlerException(e, exceptionEnum.getMsg());
        }
    }


    //TODO 다시 확인 필요
    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            // 해약환급금
            logger.info("해약환급금 테이블");
            WebElement $button = driver.findElement(By.xpath("//span[normalize-space()='상세보기']/parent::button"));
            click($button);

            // 해약환급금 창으로 전환
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            WaitUtil.loading(3);
            helper.switchToWindow(driver.getWindowHandle(), driver.getWindowHandles(), true);

            WebElement svg = new WebDriverWait(driver, 180).until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='oziviw_1']//*[local-name()='svg']")
                ));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String data = (String) js.executeScript("return xmlMsgAll");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(data));
            Document doc = builder.parse( is );
            NodeList sr = doc.getElementsByTagName("SR");

            for (int i = 0; i < sr.getLength(); i++) {
                Node item = sr.item(i);
                String term = ((DeferredElementImpl) item).getAttribute("sr0").toString();
                String premiumSum = ((DeferredElementImpl) item).getAttribute("sr1").toString();
                String returnMoneyMin = ((DeferredElementImpl) item).getAttribute("sr16").toString(); // 최저보증이율 예상해약환급금
                String returnRateMin = ((DeferredElementImpl) item).getAttribute("sr17").toString(); // 최저보증이율 예상해약환급률
                String returnMoney = ((DeferredElementImpl) item).getAttribute("sr29").toString(); // 공시이율 예상해약환급률
                String returnRate = ((DeferredElementImpl) item).getAttribute("sr30").toString(); // 공시이율 예상해약환급률
                String returnMoneyAvg = ((DeferredElementImpl) item).getAttribute("sr31").toString(); // 평균공시이율 예상해약환급률
                String returnRateAvg = ((DeferredElementImpl) item).getAttribute("sr32").toString(); // 평균공시이율 예상해약환급률

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);;
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                info.returnPremium = returnMoney.replaceAll("\\D","");;
                planReturnMoneyList.add(planReturnMoney);
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }


    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int planCalcAge = Integer.parseInt(info.age.replaceAll("\\D", ""));

            // 수집한 중도해약환급금 목록
            List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

            Optional<PlanReturnMoney> returnMoneyOptional = Optional.empty();
            for (int i = planReturnMoneyList.size() - 1; i > 0; i--) {

                PlanReturnMoney planReturnMoney = planReturnMoneyList.get(i);

                // termTxt: planReturnMoney 경과기간
                String termTxt = planReturnMoney.getTerm();

                // 경과기간이 개월단위인 경우는 일단 제외 // todo 개월단위도 포함하도록 수정
                if (termTxt.contains("개월")) {
                    continue;
                }

                // 나이로된 경과기간, 년으로 된 경과기간 추출
                String termUnit = termTxt.indexOf("년") > termTxt.indexOf("세") ? "년" : "세";
                int termUnitIndex = termTxt.indexOf(termUnit);
                int termNumberValue = Integer.parseInt(
                    termTxt.substring(0, termUnitIndex).replaceAll("\\D", ""));
                int termYear = -1;
                int termAge = -1;
                switch (termUnit) {
                    case "년":
                        termYear = termNumberValue;
                        termAge = planCalcAge + termYear;
                        break;
                    case "세":
                        termYear = termNumberValue - planCalcAge;
                        termAge = termNumberValue;
                        break;
                }

                // 해당 가설(info)의 보험기간 단위 추출 (세 or 년), 숫자 추출
                String insTermUnit = "";
                int insTermNumberValue = -1;
                if (info.categoryName.contains("종신")) {
                    String napTermUnit = info.napTerm.replaceAll("[0-9]", "");
                    int napTerm = Integer.parseInt(info.napTerm.replaceAll("[^0-9]", ""));
                    switch (napTermUnit) {
                        case "년":
                            insTermNumberValue = napTerm + 10;
                            break;
                        case "세":
                            insTermNumberValue = planCalcAge + napTerm;
                    }
                    insTermUnit = "년";
                } else if (info.categoryName.contains("연금")) { // 연금보험, 연금저축보험
                    insTermUnit = "세"; // 환급금 크롤링 시점은 개시나이
                    insTermNumberValue = Integer.parseInt(info.annuityAge.replaceAll("[^0-9]", ""));
                } else {
                    insTermUnit = info.insTerm.replaceAll("[0-9]", "");
                    insTermNumberValue = Integer.parseInt(info.insTerm.replaceAll("[^0-9]", ""));
                }

                // 보험기간 단위에 따라 비교: 경과기간이 만기에 해당하는지 여부 반환
                if ((insTermUnit.equals("세") && termAge == insTermNumberValue)
                    || (insTermUnit.equals("년") && termYear == insTermNumberValue)) {

                    logger.info("만기환급금 크롤링 :: 카테고리 :: {}", info.categoryName);
                    logger.info("만기환급금 크롤링 :: 가설 케이스 나이 :: {}세", planCalcAge);
                    logger.info("만기환급금 크롤링 :: 가설 보험기간 :: {}", info.insTerm);
                    logger.info("만기환급금 크롤링 :: 가설 납입기간 :: {}", info.napTerm);
                    logger.info("만기환급금 크롤링 :: 해약환급금 해당 경과기간 :: {}", planReturnMoney.getTerm());

                    returnMoneyOptional = Optional.of(planReturnMoney);
                }
            }

            if (returnMoneyOptional.isPresent()) {
                info.returnPremium = returnMoneyOptional.get().getReturnMoney();
            } else {
                info.returnPremium = "-1"; // 만기에 해당하는 중도해약환급금이 없을 경우
            }

            logger.info("만기환급금 크롤링 :: 만기환급금 :: {}", info.returnPremium);

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }


    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {
        String title = "연금개시연령";

        WebElement $annuityAgeSelect = (WebElement) obj[0];
        String expectedAnnuityAge = (String) obj[1];
        String actualAnnuityAge = "";

        try {

            expectedAnnuityAge = expectedAnnuityAge + "세";

            //연금개시연령 설정
            actualAnnuityAge = helper.selectByText_check($annuityAgeSelect, expectedAnnuityAge);

            //연금개시연령 비교
            super.printLogAndCompare(title, expectedAnnuityAge, actualAnnuityAge);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new SetAnnuityAgeException(e, exceptionEnum.getMsg());
        }
    }


    /**
     * 연금지급기간 설정
     * @param $annuityReceivePeriodSelect
     * @param expectedAnnuityReceivePeriod
     * @throws CommonCrawlerException
     */
    public void setAnnuityReceivePeriod(WebElement $annuityReceivePeriodSelect, String expectedAnnuityReceivePeriod) throws CommonCrawlerException {
        String title = "연금지급기간(연금수령형태의 년수?)";
        String actualAnnuityReceivePeriod = "";

        try {

            //연금지급기간 설정
            actualAnnuityReceivePeriod = helper.selectByText_check($annuityReceivePeriodSelect, expectedAnnuityReceivePeriod);

            //연금지급기간 비교
            super.printLogAndCompare(title, expectedAnnuityReceivePeriod, actualAnnuityReceivePeriod);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_PERIOD;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }


    /**
     * 연금수령방법 설정
     * @param $annuityReceiveCycleSelect
     * @param expectedAnnuityReceiveCycle
     * @throws CommonCrawlerException
     */
    public void setAnnuityReceiveCycle(WebElement $annuityReceiveCycleSelect, String expectedAnnuityReceiveCycle) throws CommonCrawlerException {
        String title = "연금수령방법(연금수령주기?)";
        String actualAnnuityReceiveCycle = "";

        try {

            //연금수령방법 설정
            actualAnnuityReceiveCycle = helper.selectByText_check($annuityReceiveCycleSelect, expectedAnnuityReceiveCycle);

            //연금수령방법 비교
            super.printLogAndCompare(title, expectedAnnuityReceiveCycle, actualAnnuityReceiveCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_CYCLE;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }


    /**
     * 연금지급형태 설정
     * @param $annuityGiveTypeSelect
     * @param expectedAnnuityGiveType
     * @throws CommonCrawlerException
     */
    public void setAnnuityGiveType(WebElement $annuityGiveTypeSelect, String expectedAnnuityGiveType) throws CommonCrawlerException {
        String title = "연금지급형태";
        String actualAnnuityGiveType = "";

        try {

            //연금지급형태 설정
            actualAnnuityGiveType = helper.selectByText_check($annuityGiveTypeSelect, expectedAnnuityGiveType);

            //연금지급형태 비교
            super.printLogAndCompare(title, expectedAnnuityGiveType, actualAnnuityGiveType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_GIVE_TYPE;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }


    public void crawlAnnuityPremium(CrawlingProduct info) throws CommonCrawlerException {
        String title = "연금수령액 크롤링";
        String annuityType = info.getAnnuityType();
        PlanAnnuityMoney planAnnuityMoney = info.getPlanAnnuityMoney();

        try {

            logger.info("예상환급 버튼 클릭");
            WebElement $button = driver.findElement(By.id("zzParngAmtAmDtl"));
            click($button);


            logger.info("연금수령액 크롤링");
            WebElement $tbody = driver.findElement(By.id("contArea"));
            WebElement $td = $tbody.findElement(By.xpath("./tr/td[4]"));
            String annuityPremium = $td.getText();
            annuityPremium = annuityPremium.replaceAll("[^0-9]", "");


            //연금수령액 정보 세팅팅
            if (annuityType.contains("종신")) {
                info.annuityPremium = annuityPremium;
                planAnnuityMoney.setWhl10Y(info.annuityPremium);
                logger.info("종신 연금수령액 : {}원", info.annuityPremium);
            } else if (annuityType.contains("확정")) {
                info.fixedAnnuityPremium = annuityPremium;
                planAnnuityMoney.setFxd10Y(info.annuityPremium);
                logger.info("확정 연금수령액 : {}원", info.fixedAnnuityPremium);
            }


            logger.info("연금수령액 팝업 닫기 위해 확인 버튼 클릭");
            $button = driver.findElement(By.xpath("//span[normalize-space()='확인']/parent::button"));
            click($button);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {
        super.crawlExpectedSavePremium(obj);
    }

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {
        String title = "피보험자명";

        WebElement $userNameInput = (WebElement) obj[0];
        String expectedUserName = (String) obj[1];
        String actualUserName = "";

        try {

            //피보험자명 설정
            actualUserName = helper.sendKeys4_check($userNameInput, expectedUserName);

            //피보험자명 비교
            super.printLogAndCompare(title, expectedUserName, actualUserName);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_USER_NAME;
            throw new SetUserNameException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {
        String title = "출생예정일";

        WebElement $dueDateInput = (WebElement) obj[0];
        String expectedDueDate = (String) obj[1];
        String actualDueDate = "";

        try {
            actualDueDate = helper.sendKeys4_check($dueDateInput, expectedDueDate);

            super.printLogAndCompare(title, expectedDueDate, actualDueDate);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_DUEDATE;
            throw new SetDueDateException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {
        super.setTravelDate(obj);
    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        super.setProductType(obj);
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {
        String title = "간편유형 구분";

        WebElement $prevalenceTypeSelect = (WebElement) obj[0];
        String expectedPrevalenceType = (String) obj[1];
        String actualPrevalenceType = "";

        try {

            //간편유형 구분 설정
            actualPrevalenceType = helper.selectByText_check($prevalenceTypeSelect, expectedPrevalenceType);

            //간편유형 구분 비교
            super.printLogAndCompare(title, expectedPrevalenceType, actualPrevalenceType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREVALENCE_TYPE;
            throw new SetPrevalenceTypeException(e, exceptionEnum.getMsg());
        }
    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {
        String title = "운전차용도";

        int age = Integer.parseInt(String.valueOf(obj[0]));
        WebElement $vehicleSelect = (WebElement) obj[1];
        String expectedVehicle = (age < 18) ? "운전안함" : "자가용";
        String actualVehicle = "";

        try {

            //운전차용도 설정
            actualVehicle = helper.selectByText_check($vehicleSelect, expectedVehicle);

            //운전차용도 비교
            super.printLogAndCompare(title, expectedVehicle, actualVehicle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(e, exceptionEnum.getMsg());
        }
    }


    public void setVehicleInjuryLevel(Object... obj) throws CommonCrawlerException {
        String title = "교통상해급수";

        WebElement $vehicleInjuryLevelSelect = (WebElement) obj[0];
        String expectedVehicleInjuryLevel = (String) obj[1];
        String actualVehicleInjuryLevel = "";

        try {

            //교통상해급수 설정
            actualVehicleInjuryLevel = helper.selectByText_check($vehicleInjuryLevelSelect, expectedVehicleInjuryLevel);

            //교통상해급수 비교
            super.printLogAndCompare(title, expectedVehicleInjuryLevel, actualVehicleInjuryLevel);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_VEHICLE_INJURY_LEVEL;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }


    public void setMedicalBeneficiary(Object... obj) throws CommonCrawlerException {
        String title = "의료수급권자여부";

        WebElement $medicalSelect = (WebElement) obj[0];
        String expectedMedical = (String) obj[1];
        String actualMedical = "";

        try {

            //의료수급권자여부 설정
            actualMedical = helper.selectByText_check($medicalSelect, expectedMedical);

            //의료수급권자여부 비교
            super.printLogAndCompare(title, expectedMedical, actualMedical);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_MEDICAL_BENEFICIARY;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }


    public void setTreatyAttribute(Object... obj) throws CommonCrawlerException {
        String title = "담보 속성";

        WebElement $treatyAttributeSelect = (WebElement) obj[0];
        String expectedAttribute = (String) obj[1];
        String actualAttribute = "";

        try {

            //담보 속성 설정
            actualAttribute = helper.selectByText_check($treatyAttributeSelect, expectedAttribute);

            //담보 속성 비교
            super.printLogAndCompare(title, expectedAttribute, actualAttribute);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TREATY_ATTRIBUTE;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }




    public void setWorkerInsuranceYN(Object... obj) throws CommonCrawlerException {
        String title = "산재보험가입유무";

        WebElement $vehicleSelect = (WebElement) obj[0];
        String expectedJoin = (String) obj[1];
        String actualJoin = "";

        try {

            //산재보험가입유무 설정
            actualJoin = helper.selectByText_check($vehicleSelect, expectedJoin);

            //산재보험가입유무 비교
            super.printLogAndCompare(title, expectedJoin, actualJoin);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_WORKER_INSURANCE_JOIN;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }


    public void setVehicleCnt(Object... obj) throws CommonCrawlerException {
        String title = "차량가입대수";

//        String expectedVehicleCnt = (String) obj[0];
//        String actualVehicleCnt = "";
//
//        try {
//            WebElement $titleLabel = driver.findElement(By.xpath("//label[normalize-space()='" + title + "']"));
//            WebElement $titleTh = $titleLabel.findElement(By.xpath("./parent::th"));
//            WebElement $vehicleCntTd = $titleTh.findElement(By.xpath("./following-sibling::td[1]"));
//            WebElement $vehicleCntInput = $vehicleCntTd.findElement(By.tagName("input"));
//
//            actualVehicleCnt = helper.setTextToInputBox($vehicleCntInput, expectedVehicleCnt);
//
//            super.printLogAndCompare(title, expectedVehicleCnt, actualVehicleCnt);
//        } catch (Exception e) {
//            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_VEHICLE_CNT;
//            throw new CommonCrawlerException(exceptionEnum, e);
//        }

        WebElement $vehicleCntInput = (WebElement) obj[0];
        String expectedVehicleCnt = (String) obj[1];
        String actualVehicleCnt = "";

        try {

            //차량가입대수 설정
            actualVehicleCnt = helper.sendKeys4_check($vehicleCntInput, expectedVehicleCnt);

            //차량가입대수 비교
            super.printLogAndCompare(title, expectedVehicleCnt, actualVehicleCnt);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_VEHICLE_CNT;
            throw new CommonCrawlerException(exceptionEnum, e);
        }
    }


    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        DecimalFormat df = new DecimalFormat("#.#");

        try {

            //가입설계 특약 정보 세팅
            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                String welgramTreatyName = welgramTreaty.getTreatyName().trim();
                String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());
                String welgramInsTerm = welgramTreaty.getInsTerm();
                String welgramNapTerm = welgramTreaty.getNapTerm();


                //특약명으로 element 찾기
                WebElement $treatyLabel = driver.findElement(By.xpath("//td[@name='p_zzobjtNmTt']/label[normalize-space()='" + welgramTreatyName + "']"));
                WebElement $treatyTr = $treatyLabel.findElement(By.xpath("./ancestor::tr[1][not(@style='display:none')]"));


                /**
                 * 특약 가입 체크박스 처리 영역
                 * */
                WebElement $treatyJoinArea = $treatyTr.findElement(By.xpath("./td[1]"));
                WebElement $treatyJoinInput = $treatyJoinArea.findElement(By.xpath("./input"));
                WebElement $treatyJoinLabel = $treatyJoinArea.findElement(By.xpath("./label"));


                if (!$treatyJoinInput.isSelected()) {
                    //특약이 미가입인 경우에만 가입처리 시킨다.
                    logger.info("특약명({}) 가입처리 완료", welgramTreatyName);
                    $treatyJoinLabel.click();
                } else {
                    logger.info("특약명({})은 필수가입 특약입니다.", welgramTreatyName);
                }


                /**
                 *
                 * 특약 가입금액 처리 영역
                 *
                 * 삼성화재 공시실의 경우 가입금액란에 input이 올 수도 있고, select가 올 수 있다.
                 * 가입금액 영역에 input, select element가 모두 존재하고 둘 중 하나의 element를 보여주고 있다.
                 */
                WebElement $treatyAssureMoneyArea = $treatyTr.findElement(By.xpath("./td[4]"));
                WebElement $treatyAssureMoneySelect = $treatyAssureMoneyArea.findElement(By.xpath("./select"));
                WebElement $treatyAssureMoneyInput = $treatyAssureMoneyArea.findElement(By.xpath("./input"));


                /**
                 * 어떤 특약은 12000원짜리도 있어 1.2에 해당하는 값을 찾아야 한다.
                 * 가입금액 소수점 처리를 위해 DecimalFormat 사용
                 */
                welgramTreatyAssureMoney = df.format(Double.parseDouble(welgramTreatyAssureMoney) / 10000);
                if($treatyAssureMoneyInput.isDisplayed()) {
                    //가입금액 세팅란이 input인 경우
                    helper.sendKeys4_check($treatyAssureMoneyInput, welgramTreatyAssureMoney);

                } else if($treatyAssureMoneySelect.isDisplayed()) {
                    //가입금액 세팅란이 select인 경우
                    helper.selectByValue_check($treatyAssureMoneySelect, welgramTreatyAssureMoney);
                }



                /**
                 * 특약 납입/보험기간 처리 영역
                 *
                 * 삼성화재 공시실의 경우 납입/보험기간란이 input이 올 수도 있고, select가 올 수 있다.
                 * 납입/보험기간 영역에 input, select element가 모두 존재하고 둘 중 하나의 element를 보여주고 있다.
                 * 세팅란이 input인 경우에는 값을 세팅할 수 없음. select인 경우에만 값을 선택할 수 있음.
                 */
                WebElement $treatyTermArea = $treatyTr.findElement(By.xpath("./td[5]"));
                WebElement $treatyNapTermSelect = $treatyTermArea.findElement(By.id("p_viewSelectZzcoltrPmprdVl"));
                WebElement $treatyInsTermSelect = $treatyTermArea.findElement(By.id("p_viewSelectZzcoltrInprdVl"));


                //특약 납입기간 처리
                welgramNapTerm = welgramNapTerm + "납";
                if($treatyNapTermSelect.isDisplayed() && $treatyNapTermSelect.isEnabled()) {
                    //납입기간 세팅란이 select이면서 활성화 되어있는 경우에만
                    helper.selectByText_check($treatyNapTermSelect, welgramNapTerm);
                }


                //특약 보험기간 처리
                welgramInsTerm = welgramInsTerm + "만기";
                if($treatyInsTermSelect.isDisplayed() && $treatyInsTermSelect.isEnabled()) {
                    //보험기간 세팅란이 select이면서 활성화 되어있는 경우에만
                    helper.selectByText_check($treatyInsTermSelect, welgramInsTerm);
                }

            }



            //원수사에서 실제 가입처리된 특약 정보 가져오기
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            List<WebElement> $checkedTreatyInputs = driver.findElements(By.cssSelector("input[name=p_chkCov]:checked"));
            for(WebElement $checkedTreatyInput : $checkedTreatyInputs) {
                String targetTreatyName = "";
                String targetTreatyAssureMoney = "";
                String targetTreatyNapTerm = "";
                String targetTreatyInsTerm = "";
                String script = "return $(arguments[0]).val();";
                int unit = 10000;


                WebElement $treatyTr = $checkedTreatyInput.findElement(By.xpath("./ancestor::tr[1]"));


                //특약명 영역
                WebElement $treatyNameArea = $treatyTr.findElement(By.xpath("./td[2]"));
                WebElement $treatyNameLabel = $treatyNameArea.findElement(By.xpath("./label"));
                targetTreatyName = $treatyNameLabel.getText();


                //특약 가입금액 영역
                WebElement $treatyAssureMoneyArea = $treatyTr.findElement(By.xpath("./td[4]"));
                WebElement $treatyAssureMoneySelect = $treatyAssureMoneyArea.findElement(By.xpath("./select"));
                WebElement $treatyAssureMoneyInput = $treatyAssureMoneyArea.findElement(By.xpath("./input"));

                if($treatyAssureMoneySelect.isDisplayed()) {
                    script = "return $(arguments[0]).find('option:selected').val();";
                    targetTreatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneySelect));
                } else if($treatyAssureMoneyInput.isDisplayed()) {
                    script = "return $(arguments[0]).val();";
                    targetTreatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneyInput));
                }
                targetTreatyAssureMoney = targetTreatyAssureMoney.replaceAll("[^0-9.]", "");
                targetTreatyAssureMoney = df.format(Double.parseDouble(targetTreatyAssureMoney) * unit);



                //특약 납입/보험기간 영역
                WebElement $treatyTermArea = $treatyTr.findElement(By.xpath("./td[5]"));
                WebElement $treatyNapTermSelect = $treatyTermArea.findElement(By.id("p_viewSelectZzcoltrPmprdVl"));
                WebElement $treatyNapTermInput = $treatyTermArea.findElement(By.id("p_viewInputZzcoltrPmprdVl"));
                WebElement $treatyInsTermSelect = $treatyTermArea.findElement(By.id("p_viewSelectZzcoltrInprdVl"));
                WebElement $treatyInsTermInput = $treatyTermArea.findElement(By.id("p_viewInputZzcoltrInprdVl"));

                //실제 납입기간 값 읽어오기
                if($treatyNapTermInput.isDisplayed()) {
                    //납입기간 세팅란이 input인 경우
                    script = "return $(arguments[0]).val();";
                    targetTreatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermInput));
                } else if($treatyNapTermSelect.isDisplayed()) {
                    //납입기간 세팅란이 select인 경우
                    script = "return $(arguments[0]).find('option:selected').text();";
                    targetTreatyNapTerm = String.valueOf(helper.executeJavascript(script, $treatyNapTermSelect));
                }
                targetTreatyNapTerm = targetTreatyNapTerm.replace("납", "");


                //실제 보험기간 값 읽어오기
                if($treatyInsTermInput.isDisplayed()) {
                    //보험기간 세팅란이 input인 경우
                    script = "return $(arguments[0]).val();";
                    targetTreatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermInput));
                } else if($treatyInsTermSelect.isDisplayed()) {
                    //보험기간 세팅란이 select인 경우
                    script = "return $(arguments[0]).find('option:selected').text();";
                    targetTreatyInsTerm = String.valueOf(helper.executeJavascript(script, $treatyInsTermSelect));
                }
                targetTreatyInsTerm = targetTreatyInsTerm.replace("만기", "");


                //원수사 특약 정보 적재
                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                targetTreaty.setTreatyName(targetTreatyName);
                targetTreaty.setAssureMoney(Integer.parseInt(targetTreatyAssureMoney));
                targetTreaty.setNapTerm(targetTreatyNapTerm);
                targetTreaty.setInsTerm(targetTreatyInsTerm);

                targetTreatyList.add(targetTreaty);
            }

            //비교
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());

            if(result) {
                logger.info("특약 정보가 모두 일치합니다~~~");
            } else {
                logger.error("특약 정보 불일치~~~");
                throw new Exception();
            }


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e, exceptionEnum.getMsg());
        }

    }


    /**
     * 가입금액 단위가 KRW인 특약을 세팅할 때 사용하는 메서드
     * TODO 예쁘게 다듬기 ^^;;;
     * @param welgramTreatyList
     * @throws SetTreatyException
     */
    public void setTreatiesTypeKRW(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        //그룹특약에 해당하는 tr 목록을 얻어오기 위한 jquery 문법
        String script = "var $tr = $(arguments[0]); "
            + "return $tr.nextAll('tr').addBack().slice(0, arguments[1]).get();";

        try {

            WebElement $tbody = driver.findElement(By.xpath("//div[@id='personCoverageInfo']//tbody"));


            //가입설계 특약 정보대로 원수사에 세팅하기
            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                String treatyName = welgramTreaty.treatyName;

                WebElement $treatyNameTd = $tbody.findElement(By.xpath(".//td[@id='p_objtNm'][normalize-space()='" + treatyName + "']"));
                WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

                List<WebElement> $treatyTrList = new ArrayList<>();
                String rowspanAttr = $treatyNameTd.getAttribute("rowspan");

                if(StringUtils.isNotEmpty(rowspanAttr)) {
                    //rowspan 속성이 있는 경우에만

                    //특약 그룹에 해당하는 tr 목록 얻어오기(그룹특약인 경우에는 복수개의 tr을, 단일특약인 경우에는 1개의 tr을 얻어올 것이다)
                    int rowspan = Integer.parseInt(rowspanAttr);
                    $treatyTrList = (List<WebElement>) helper.executeJavascript(script, $treatyTr, rowspan);

                } else {
                    $treatyTrList.add($treatyTr);
                }


                for(WebElement $tr : $treatyTrList) {
                    setTreatyInfoFromTr($tr, welgramTreaty);
                }

            }


            //원수사에서 실제 가입처리된 특약 정보 읽어오기
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

            List<WebElement> $treatyNameTdList = $tbody.findElements(By.xpath(".//td[@id='p_objtNm']"));
            for(WebElement $treatyNameTd : $treatyNameTdList) {
                WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
                String treatyName = $treatyNameTd.getText().trim();
                String rowspanAttr = $treatyNameTd.getAttribute("rowspan");

                if(StringUtils.isNotEmpty(rowspanAttr)) {
                    //rowspan 속성이 있는 경우에만
                    int rowspan = Integer.parseInt($treatyNameTd.getAttribute("rowspan"));

                    if(rowspan > 1) {

                        //특약 그룹에 해당하는 tr 목록 얻어오기
                        List<WebElement> $treatyTrList = (List<WebElement>) helper.executeJavascript(script, $treatyTr, rowspan);
                        List<Boolean> joinYNList = new ArrayList<>();   //하위 보장 tr의 모든 가입여부를 담는 리스트

                        for(WebElement $tr : $treatyTrList) {

                            //특약 가입체크 영역
                            WebElement $treatyJoinTd = $tr.findElement(By.xpath("./td[1]"));
                            WebElement $treatyJoinInput = $tr.findElement(By.tagName("input"));

                            //가입여부 담기
                            joinYNList.add($treatyJoinInput.isSelected());
                        }

                        //하위의 모든 보장까지 가입처리 되어 있어야만 해당 특약을 가입으로 간주한다.
                        if(!joinYNList.contains(false)) {

                            //그룹특약인 경우 - 가입설계 특약의 가입금액을 세팅한다
                            CrawlingTreaty welgramTreaty = welgramTreatyList.stream()
                                .filter(t -> t.getTreatyName().equals(treatyName))
                                .findFirst()
                                .orElseThrow(SetTreatyException::new);

                            CrawlingTreaty targetTreaty = new CrawlingTreaty();
                            targetTreaty.setTreatyName(treatyName);
                            targetTreaty.setAssureMoney(welgramTreaty.getAssureMoney());

                            targetTreatyList.add(targetTreaty);
                        }

                    }
                    else {

                        //단일특약인 경우 - 원수사에서 특약 가입금액을 읽어 세팅한다.

                        //특약 가입체크 영역
                        WebElement $treatyJoinTd = $treatyTr.findElement(By.xpath("./td[1]"));
                        WebElement $treatyJoinInput = $treatyTr.findElement(By.tagName("input"));

                        if($treatyJoinInput.isSelected()) {
                            String treatyAssureMoney = "";

                            WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[last()]"));
                            WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));

                            script = "return $(arguments[0]).val();";
                            treatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneyInput));
                            treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");

                            CrawlingTreaty targetTreaty = new CrawlingTreaty();
                            targetTreaty.setTreatyName(treatyName);
                            targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

                            targetTreatyList.add(targetTreaty);
                        }

                    }


                } else {

                    //단일특약인 경우 - 원수사에서 특약 가입금액을 읽어 세팅한다.

                    //특약 가입체크 영역
                    WebElement $treatyJoinTd = $treatyTr.findElement(By.xpath("./td[1]"));
                    WebElement $treatyJoinInput = $treatyTr.findElement(By.tagName("input"));

                    if($treatyJoinInput.isSelected()) {
                        String treatyAssureMoney = "";

                        WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[last()]"));
                        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));

                        script = "return $(arguments[0]).val();";
                        treatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneyInput));
                        treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "");

                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.setTreatyName(treatyName);
                        targetTreaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));

                        targetTreatyList.add(targetTreaty);
                    }



                }


            }


            //원수사 특약 정보 vs 가입설계 특약 정보 비교
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
            if (result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e, exceptionEnum.getMsg());
        }
    }


    /**
     * 공시실 특약 가입금액 설정 단위가 KRW인 상품 중에서
     * 특약 tr마다 가입여부, 가입금액 세팅을 할 때 사용하는 메서드입니다.
     * @param $tr
     * @param treatyInfo
     */
    public void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {

        //특약 가입체크 영역
        WebElement $treatyJoinTd = $tr.findElement(By.xpath("./td[1]"));
        WebElement $treatyJoinInput = $treatyJoinTd.findElement(By.tagName("input"));
        WebElement $treatyJoinLabel = $treatyJoinTd.findElement(By.tagName("label"));

        //특약명 영역
        WebElement $treatyNameTd = $tr.findElement(By.xpath("./td[2]"));


        /**
         * 특약 가입금액 영역
         *
         * [주의!!!]
         * 특약 가입금액 영역 td를 찾을 때는 현재 tr 기준으로 ./td[4]로 찾으면 안됨.
         * 단일특약이면 상관없으나 그룹특약의 경우 tr안에 td가 3개만 있는 경우가 있기 때문에.
         * last()로 마지막 td를 지칭해 특약 가입금액 영역을 지칭해줘야 한다.
         */
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[last()]"));
        WebElement $treatyAssureMoneyInput = $treatyAssureMoneyTd.findElement(By.tagName("input"));


        //"마가입"인 경우에만 "가입"으로 체크한다
        if(!$treatyJoinInput.isSelected()) {
            click($treatyJoinLabel);
        }

        //특약 가입금액 세팅
        if($treatyAssureMoneyInput.isEnabled()) {
            helper.sendKeys4_check($treatyAssureMoneyInput, String.valueOf(treatyInfo.getAssureMoney()));
        }

    }



    //로딩바 명시적 대기
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement(".ui-loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // 최소 보험료 미충족으로 인한 alert
    public void invalidPremiumPopup() {
        WebElement popUp = driver.findElement(By.xpath("div[@class='ui-modal-wrap type-alert']"));
        if (popUp.isDisplayed()) {
            popUp.findElement(By.xpath(".//button[@id='btn-main']"));
        }
    }

}