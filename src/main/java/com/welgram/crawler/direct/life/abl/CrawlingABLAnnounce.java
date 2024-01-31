package com.welgram.crawler.direct.life.abl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.NotFoundTextInSelectBoxException;
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
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.util.InsuranceUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;



// 2023.12.11 | 서용호 |
public abstract class CrawlingABLAnnounce extends CrawlingABLNew {

    private List<WebElement> eles;

    /**
     * ABL 공시실 성별 셋팅
     * @param obj
     * obj[0] : 성별 element tag NAME (필수)
     * obj[1] : 셋팅할 성별(info.gender) (필수)
     * @throws SetGenderException
     */
    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        String tagId = (String) obj[0];
        By $genderOption = By.cssSelector("#" + tagId +" > option");
        int gender = (int) obj[1];
        String expectedGenderText = (gender == MALE) ? "남" : "여";
        String actualGenderText = "";

        try {
            elements = helper.waitPesenceOfAllElementsLocatedBy($genderOption);
            for (WebElement el : elements) {
                if (el.getAttribute("value").equals(Integer.toString(gender + 1))) {
                    logger.info(el.getText());
                    el.click();
                    break;
                }
            }
            actualGenderText = ((JavascriptExecutor)driver).executeScript("return $(\"#" + tagId + " option:selected\").text()").toString();
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

            WaitUtil.loading(2);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * ABL 공시실 생년월일 셋팅
     * @param obj
     * obj[0] : 생년월일 입력 element(By) (필수)
     * obj[1] : 생년월일 (info.fullBirth) (필수)
     * @throws SetBirthdayException
     */
    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";

        By $birthBy = (By) obj[0];
        String expectedFullBirth = (String) obj[1];
        String actualFullBirth = "";

        try {
            actualFullBirth = helper.sendKeys4_check($birthBy, expectedFullBirth);

            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

            WaitUtil.loading(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * ABL 공시실 주보험 비교 메서드
     * @param obj
     * obj[0] : 주보험비교 select tagId (필수)
     * obj[1] : 주보험 셋팅하기 위해 비교(contains)할 단어(필수)
     * obj[2] : obj[1] 비교 조건 설정이나 추가 비교 할 단어(선택)
     * obj[2] -> "equals" : obj[1]번 문자와 equals 비교를 할 때 사용
     * obj[2] -> 그 외 : obj[1]외에 추가로 비교해야할 단어가 있을 때 사용 (필수아님)
     * @throws SetProductTypeException
     */
    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        try {
            // 가입구분 설정
            String tagId =(String) obj[0];
            elements = driver.findElements(By.cssSelector("#" + tagId +" > option"));
            String textType = (String) obj[1];

            try {
                // 가입구분을 선택하기 위한 비교 문자가 2개 들어오면 해당 로직 수행
                String addText = (String) obj[2];

                if (addText.equals("equals")) {
                    logger.info("{}로 가입구분 {} 비교", textType, addText);
                    for (WebElement option : elements) {
                        if (option.getText().equals(textType)) {
                            option.click();
                            logger.info("{} 클릭!", textType);
                            break;
                        }
                    }
                } else {
                    logger.info("{}, {}로 가입구분 비교", textType, addText);
                    for (WebElement $option : elements) {
                        String optionText = $option.getText();
                        if (optionText.contains(textType)) {
                            if (StringUtils.isNotEmpty(addText)) {
                                if (optionText.contains(addText)) {
                                    logger.info(optionText + "선택!");
                                    $option.click();
                                    break;
                                }
                            } else {
                                logger.info(optionText + "선택!");
                                $option.click();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.info("{}로 가입구분 비교", textType);
                for (WebElement $option : elements) {
                    String optionText = $option.getText();
                    if (optionText.contains(textType)) {
                        logger.info(optionText + "선택!");
                        $option.click();
                        break;
                    }
                }
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * ABL 공시실 상품 선택 메서드
     * @param obj
     * obj[0] : 상품 element tag ID (필수)
     * obj[1] : 셋팅할 상품명(String) (필수)
     * @throws Exception
     */
    public void setProduct(Object... obj) throws Exception {

        String tagId = (String) obj[0];
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(tagId)));
        elements = element.findElements(By.tagName("option"));

        String expectedProductName = (String) obj[1];
        String actualProductName = "";

        for (WebElement $option : elements) {
            String optionText = $option.getText();
            if (expectedProductName.contains(optionText)) {
                logger.info(optionText + "선택!");
                $option.click();
                break;
            }
        }
        actualProductName = ((JavascriptExecutor)driver).executeScript("return $(\"#" + tagId + " option:selected\").text()").toString();

        if (!expectedProductName.contains(actualProductName)) {
            throw new Exception("상품이 존재하지 않습니다.");
        }
    }



    /**
     * ABL 공시실 보험기간 셋팅
     * @param obj
     * obj[0] : 보험기간 element tag ID (필수)
     * obj[1] : 셋팅할 보험기간 (String) (필수)
     * @throws SetInsTermException
     */
    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";

        String tagId = (String) obj[0];
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(tagId)));
        elements = element.findElements(By.tagName("option"));

        String expectedInsTermText = (String) obj[1];
        if (expectedInsTermText.contains("종신보장")) { expectedInsTermText = "종신"; }
        String actualInsTermText = "";

        try {
            boolean chk = false;
            for (WebElement el : elements) {
                if (expectedInsTermText.equals(el.getText().trim())) {
                    chk = true;
                    logger.info(el.getText() + "클릭!");
                    el.click();
                    helper.waitForCSSElement(".state-load-data");
                    break;
                }
            }
            if (!chk) {
                logger.debug("선택할 보험기간이 없습니다!!!");
                throw new Exception("선택할 보험기간이 없습니다!!!");
            }
            actualInsTermText = ((JavascriptExecutor)driver).executeScript("return $(\"#" + tagId + " option:selected\").text()").toString();
            super.printLogAndCompare(title, expectedInsTermText, actualInsTermText);

            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }

    }



    /**
     * ABL 공시실 납입기간 셋팅
     * @param obj
     * obj[0] : 납입기간 element tag ID (필수)
     * obj[1] : 셋팅할 납입기간 (String) (필수)
     * @throws SetNapTermException
     */
    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        String title = "납입기간";
        String tagId = (String) obj[0];
        element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(tagId)));
        elements = element.findElements(By.tagName("option"));

        String expectedNapTermText = (String) obj[1];
        if (expectedNapTermText.contains("종신보장")) expectedNapTermText = "종신";
        String actualNapTermText = "";

        try {
            boolean chk = false;
            for (WebElement el : elements) {
                if (expectedNapTermText.equals(el.getText().trim())) {
                    chk = true;
                    logger.info(el.getText() + "클릭!");
                    el.click();
                    helper.waitForCSSElement(".state-load-data");
                    break;
                }
            }
            if (!chk) {
                logger.debug("선택할 납입기간이 없습니다!!!");
                throw new Exception("선택할 납입기간이 없습니다!!!");
            }
            actualNapTermText = ((JavascriptExecutor)driver).executeScript("return $(\"#" + tagId + " option:selected\").text()").toString();
            super.printLogAndCompare(title, expectedNapTermText, actualNapTermText);

            WaitUtil.loading(2);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * ABL 공시실 가입금액 셋팅
     * @param obj
     * obj[0] : 가입금액 element tag ID (필수)
     * obj[1] : CrawlingProduct (필수)
     * @throws SetNapTermException
     */
    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "가입금액";
        String tagId = (String) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        String expectedAssureMoney = info.assureMoney;
        String actualAssureMoney = "";
        try {
            // 대면연금저축을 제외한 상품
            if (!info.productCode.contains("ABL_ASV_F")){
                logger.info("대면연금저축 제외 상품 가입금액 세팅");
                WebElement $assureMoneySelect = driver.findElement(By.id(tagId));

                // 가입금액 단위 찾기
                String unitText = driver.findElement(By.xpath("//th[@id='entAmtTh']")).getText();
                int unit = 1;
                if (unitText.contains("만원")) {
                    logger.info("공시실 가입금액 단위: 만원");
                    unit = 10000;
                } else if (unitText.contains("천원")) {
                    logger.info("공시실 가입금액 단위: 천원");
                    unit = 1000;
                }

                int targetAssureMoney = Integer.parseInt(expectedAssureMoney) / unit;
                actualAssureMoney = helper.selectByText_check($assureMoneySelect, String.valueOf(targetAssureMoney));

                actualAssureMoney = String.valueOf(Integer.parseInt(actualAssureMoney) * unit);

            } else {
                // 대면연금저축일 경우
                logger.info("대면연금저축 상품 가입금액 세팅");
                WebElement $input = driver.findElement(By.cssSelector("#"+tagId));
                helper.sendKeys2_check($input, info.assureMoney);
                actualAssureMoney = $input.getAttribute("value");
            }

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);
            WaitUtil.loading(2);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * ABL 공시실 출생예정일 선택
     * @param obj
     * obj[0] : 출생 예정일 element(By) (핋수)
     * @throws Exception
     * todo | 출산예정일 입력
     */
    public void setExpectedDateBirth(Object... obj) throws Exception {

        String title = "출생 예정일";
        By $dateBirthBy = (By) obj[0];

        // 출생예정일 설정(고정)
        String expectedDateBirth = InsuranceUtil.getDateOfBirth(12);
        String actualDateBirth = "";

        try {
            actualDateBirth = helper.sendKeys4_check($dateBirthBy, expectedDateBirth);

            super.printLogAndCompare(title, expectedDateBirth, actualDateBirth);

            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new Exception(title + "이 일치하지 않습니다");
        }

    }


    /**
     * ABL 공시실 월 보험료 설정
     * @param value
     * value : 보험료
     * @throws CommonCrawlerException
     */
    protected void setMonthlyPremium(String value) throws CommonCrawlerException {

        try {
            element = driver.findElement(By.id("mnContPrm"));

            logger.info("월 보험료 : {} " , value);
            logger.debug("step1");
            element.click();
            Thread.sleep(1000);

            logger.debug("step2");
            element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            Thread.sleep(300);

            logger.debug("step3");
            element.sendKeys(value);
            WaitUtil.loading(1);

        } catch (Exception e) {
            throw new CommonCrawlerException("월 보험료 설정 중 오류가 발생했습니다.\n" + e.getMessage());
        }

    }



    /**
     * ABL 공시실 보험료 크롤링 메서드
     * @param obj
     * obj[0] : 보험료 element tag ID(필수)
     * obj[1] : CrawlingProduct (필수)
     * @throws PremiumCrawlerException
     */
    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        String tagId = (String) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        try {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(tagId)));
            info.treatyList.get(0).monthlyPremium = element.getAttribute("value")
                    .replaceAll(",", "");
            logger.info("============================");
            logger.debug("보험료 : {}", info.treatyList.get(0).monthlyPremium);
            logger.info("============================");
            info.siteProductMasterCount++;
            info.errorMsg = "";

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg());
        }
    }



    /**
     * 태아보험 전용 보험료 크롤링(출생전, 출생후 두가지 보험료를 저장)
     * @param obj
     * obj[0] : 출생 전 보험료
     * obj[1] : 출생 후 보험료
     * obj[2] : CrawlingProduct info
     * @throws PremiumCrawlerException
     */
    public void babyCrawlPremium(Object... obj) throws PremiumCrawlerException {

        String beforeTagId = (String) obj[0];
        String afterTagId = (String) obj[1];
        CrawlingProduct info = (CrawlingProduct) obj[2];

        try {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(beforeTagId)));
            info.treatyList.get(0).monthlyPremium = element.getAttribute("value").replaceAll(",", "");

            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(afterTagId)));
            info.nextMoney = element.getAttribute("value").replaceAll(",", "");

            logger.info("============================");
            logger.debug("출생 전 보험료 : {}", info.treatyList.get(0).monthlyPremium);
            logger.debug("출생 후 보험료 : {}", info.nextMoney);
            logger.info("============================");
            info.siteProductMasterCount++;
            info.errorMsg = "";

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg());
        }
    }



    /*********************************************************
     * <해약환급금 세팅 메소드>
     * @param  obj
     * obj[0] -> CrawlingProduct (필수)
     * obj[1] -> type 정리(필수 아님, 없을시에는 예외처리로 0,2,3,4번 td 크롤링)
     *           1. "TRM" -> 해약환급금 표에 사망보험금이 포함되어있어 테이블의 4(해약환급금), 5(환급률)인 경우
     *           2. "ALL,ASV" -> 해약환급금에 최저 공시 일반 모두 표기되어있어 전부 크롤링("ASV"인 경우 연금수령액까지)
     * @throws Exception - 해약환급금 세팅시 예외처리
     *********************************************************/
    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        try {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnEntplRprtPbl")));
            element.click();
            WaitUtil.loading(3);

            Set<String> windowId = driver.getWindowHandles();
            Iterator<String> handles = windowId.iterator();

            String currentHandle = driver.getWindowHandle();
            String nextHandle = null;

            while (handles.hasNext()) {
                nextHandle = handles.next();
                WaitUtil.loading(2);
            }

            driver.switchTo().window(nextHandle);

            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tabRefnd")));
            element.click();
            helper.waitForCSSElement(".state-load-data");

            WaitUtil.loading(3);
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("refndTab")));

            element = element.findElement(By.tagName("table")).findElement(By.tagName("tbody"));
            elements = element.findElements(By.tagName("tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            try {
                String type = (String) obj[1];
                logger.info("특이 케이스 {} TYPE 해약환급금 크롤링", type);
                if (type.equals("TRM")) {
                    for (WebElement tr : elements) {
                        PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                        List<WebElement> tdList = tr.findElements(By.tagName("td"));

                        String term = tdList.get(0).getText();
                        String premiumSum = tdList.get(2).getText();
                        String returnMoney = tdList.get(4).getText();
                        String returnRate = tdList.get(5).getText();

                        logger.info("경과기간   :: {}", term);
                        logger.info("납입보험료 :: {}", premiumSum);
                        logger.info("해약환급금 :: {}", returnMoney);
                        logger.info("환급률    :: {}", returnRate);
                        logger.info("=================================");

                        planReturnMoney.setTerm(term);
                        planReturnMoney.setPremiumSum(premiumSum);
                        planReturnMoney.setReturnMoney(returnMoney);
                        planReturnMoney.setReturnRate(returnRate);
                        planReturnMoneyList.add(planReturnMoney);

                        // 기본 해약환급금 세팅
                        info.returnPremium = returnMoney.replace(",", "");
                        logger.info("만기환급금 세팅 : {} ", info.returnPremium);
                    }

                } else if (type.equals("ALL") || type.equals("ASV")) {
                    for (WebElement tr : elements) {
                        PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                        List<WebElement> tdList = tr.findElements(By.tagName("td"));

                        String term = tdList.get(0).getText();
                        String premiumSum = tdList.get(2).getText();

                        String returnMoneyMin = tdList.get(3).getText();
                        String returnRateMin = tdList.get(4).getText();

                        String returnMoneyAvg = tdList.get(5).getText();
                        String returnRateAvg = tdList.get(6).getText();

                        String returnMoney = tdList.get(7).getText();
                        String returnRate = tdList.get(8).getText();

                        logger.info("경과기간   :: {}", term);
                        logger.info("납입보험료 :: {}", premiumSum);
                        logger.info("해약환급금 :: {}", returnMoney);
                        logger.info("환급률    :: {}", returnRate);
                        logger.info("최저해약환급금 :: {}", returnMoneyMin);
                        logger.info("최저해약환급률 :: {}", returnRateMin);
                        logger.info("평균해약환급금 :: {}", returnMoneyAvg);
                        logger.info("평균해약환급률 :: {}", returnRateAvg);
                        logger.info("=================================");

                        planReturnMoney.setTerm(term);
                        planReturnMoney.setPremiumSum(premiumSum);
                        planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                        planReturnMoney.setReturnRateMin(returnRateMin);
                        planReturnMoney.setReturnMoney(returnMoney);
                        planReturnMoney.setReturnRate(returnRate);
                        planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                        planReturnMoney.setReturnRateAvg(returnRateAvg);

                        planReturnMoneyList.add(planReturnMoney);

                        // 기본 해약환급금 세팅
                        info.returnPremium = tr.findElements(By.tagName("td")).get(7).getText()
                                .replace(",", "");
                    }

                    // 연금수령액
                    if (type.equals("ASV")) {
                        getAnnuityPremium(info);
                    }

                } else if (type.equals("WLF")){
                    for (WebElement tr : elements) {
                        PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                        List<WebElement> tdList = tr.findElements(By.tagName("td"));

                        String term = tdList.get(0).getText();
                        String premiumSum = tdList.get(2).getText();

                        String returnMoneyMin = tdList.get(3).getText();
                        String returnRateMin = tdList.get(4).getText();

                        String returnMoneyAvg = tdList.get(5).getText();
                        String returnRateAvg = tdList.get(6).getText();

                        String returnMoney = tdList.get(7).getText();
                        String returnRate = tdList.get(8).getText();

                        logger.info("경과기간   :: {}", term);
                        logger.info("납입보험료 :: {}", premiumSum);
                        logger.info("해약환급금 :: {}", returnMoney);
                        logger.info("환급률    :: {}", returnRate);
                        logger.info("최저해약환급금 :: {}", returnMoneyMin);
                        logger.info("최저해약환급률 :: {}", returnRateMin);
                        logger.info("평균해약환급금 :: {}", returnMoneyAvg);
                        logger.info("평균해약환급률 :: {}", returnRateAvg);
                        logger.info("=================================");

                        planReturnMoney.setTerm(term);
                        planReturnMoney.setPremiumSum(premiumSum);
                        planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                        planReturnMoney.setReturnRateMin(returnRateMin);
                        planReturnMoney.setReturnMoney(returnMoney);
                        planReturnMoney.setReturnRate(returnRate);
                        planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                        planReturnMoney.setReturnRateAvg(returnRateAvg);

                        planReturnMoneyList.add(planReturnMoney);

                        //만기환급금 세팅. 종신(WLF)의 경우 만기환급시점 = 납입기간 + 10년

                        String maturityDate = (Integer.parseInt(info.napTerm.replaceAll("[^0-9]", "")) + 10) + "년";

                        if(term.equals(maturityDate)){
                            info.returnPremium = returnMoney;
                        }
                    }
                }

            } catch (Exception e) {
                logger.info("일반 해약환급금 크롤링");
                // 특이 케이스 (type)이 없다면 예외처리
                for (WebElement tr : elements) {
                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                    List<WebElement> tdList = tr.findElements(By.tagName("td"));

                    String term = tdList.get(0).getText();
                    String premiumSum = tdList.get(2).getText();
                    String returnMoney = tdList.get(3).getText();
                    String returnRate = tdList.get(4).getText();
                    logger.info(term + " :: " + premiumSum);
                    logger.info(term + " :: 해약환급금 :: " + returnMoney);
                    logger.info(term + " :: 환급률    :: " + returnRate);

                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);
                    planReturnMoneyList.add(planReturnMoney);

                    // 기본 해약환급금 세팅
                    info.returnPremium = returnMoney.replace(",", "");
                    logger.info("만기환급금 세팅 : {} ", info.returnPremium);
                }
            }
            logger.info("==================================================");

            info.setPlanReturnMoneyList(planReturnMoneyList);
            info.savePremium = "0"; // 적립보험료
            info.errorMsg = "";

            driver.close();
            driver.switchTo().window(currentHandle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(),exceptionEnum.getMsg());
        }
    }



    /**
     * ABL 공시실 연금개시연령 셋팅 메서드
     * @param obj
     * obj[0] : 연금개시연령 element tag ID(필수)
     * obj[1] : 연금개시나이(필수)
     * @throws SetAnnuityAgeException
     */
    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        String tagId = (String) obj[0];
        String annAge = String.valueOf(obj[1]);

        try {
            elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#" + tagId + " > option"));
            boolean chk = false;
            for (WebElement option : elements) {
                if (option.getAttribute("value").equals(annAge)) {
                    logger.info("연금개시연령 :: {} +클릭!" , annAge);
                    chk = true;
                    option.click();
                    helper.waitForCSSElement(".state-load-data");
                    break;
                }
            }
            if (!chk){
                logger.debug("선택할 연금개시연령이 없습니다!!!");
                throw new SetAnnuityAgeException("선택할 연금개시연령이 없습니다!!!");
            }
            WaitUtil.loading(2);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new SetAnnuityAgeException(e.getCause(),exceptionEnum.getMsg());
        }

    }



    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {

    }



    @Override
    public void setJob(Object... obj) throws SetJobException {

    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

    }



    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {

    }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {

    }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

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
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }



    /*********************************************************
     * <공시실 세팅 메소드>
     * @param  obj {CrawlingProduct, type} - obi[0]: 크롤링 상품 객체, obj[1]: type
     * obj[1] -> type(필수 아님, 없을시에는 예외처리로 일반적인 상품찾기)
     * 1. "unique"
     * 2. "disclosure"
     * @throws InterruptedException - 공시실 세팅 시 예외처리
     *********************************************************/
    protected void openAnnouncePage(Object...obj) throws InterruptedException {

        logger.info("================");
        logger.info("공시실 크롤링 시작!");
        logger.info("================");

        String productName = "";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        elements = driver.findElements(By.className("pd_prod_list"));

        try {
            String type = (String) obj[1];
            // type 이 파라미터로 들어왔음
            logger.info("unique case 공시실 찾기");
            if (type.equals("unique")) {
                for (WebElement ul : elements) {
                    eles = ul.findElements(By.cssSelector("span.link"));
                    for (WebElement span : eles) {
                        element = span.findElement(By.tagName("a"));
                        productName = element.getAttribute("title").replace("가입설계", "").trim();

                        if (productName
                                .contains(info.productName.substring(0, info.productName.indexOf("보험")))) {
                            span.click();
                            WaitUtil.loading(3);
                            break;
                        }
                    }
                }

            } else if (type.equals("disclosure")) {
                for (WebElement ul : elements) {
                    eles = ul.findElements(By.cssSelector("span.link"));
                    for (WebElement span : eles) {
                        element = span.findElement(By.tagName("a"));
                        productName = element.getAttribute("title").replace("가입설계", "").trim().replace("새창열림","").replace(" ","").replace(",","");
                        if (info.productName.replace(" ","").indexOf(productName) > -1) {
                            span.click();
                            WaitUtil.loading(3);
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.info("일반 공시실 찾기");
            // type 파라미터가 들어오지 않아 예외로 넘어옴
            for (WebElement ul : elements) {
                eles = ul.findElements(By.cssSelector("span.link"));
                for (WebElement span : eles) {
                    element = span.findElement(By.tagName("a"));
                    productName = element.getAttribute("title").replace("가입설계", "").replace("새창열림","").replace(",","").trim();
                    productName = productName.replace("(일반심사형)","");
                    if (info.productName.indexOf(productName) > -1) {
                        span.click();
                        WaitUtil.loading(3);
                        break;
                    }
                }
            }
        }

        Set<String> windowId = driver.getWindowHandles();
        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            WaitUtil.loading(2);
        }
        driver.switchTo().window(subHandle);
    }



    /*********************************************************
     * <계약관계정보 적용 세팅 메소드>
     * @param  by {By} - id
     * @throws Exception - 계약관계정보 적용 세팅 예외처리
     *********************************************************/
    protected void doClickButton(By by) throws Exception {

        try {
            element = wait.until(ExpectedConditions.elementToBeClickable(by));
            element.click();
            helper.waitForCSSElement(".state-load-data");
            helper.waitForCSSElement(".state-load-data");

        } catch (Exception e){
            throw new Exception("계약관계정보 적용 세팅 시 예외처리 발생");
        }
    }



    /*********************************************************
     * <특약리스트를 돌면서 특약의 가입조건을 세팅 메소드>
     * @param  treatyList {CrawlingTreaty} - 특약 리스트
     * @throws Exception - 특약 세팅시 예외처리
     *********************************************************/
    protected void setTreaty(List<CrawlingTreaty> treatyList) throws SetTreatyException {

        try {
            List<CrawlingTreaty> homepageTreaties = new ArrayList<>();

            for (CrawlingTreaty treaty : treatyList) {
                String treatyName = treaty.treatyName;
                String treatyAssureMoney = String.valueOf(treaty.assureMoney);
                String insTerm = treaty.insTerm;
                String napTerm = treaty.napTerm;

                if (treaty.productGubun.equals(CrawlingTreaty.ProductGubun.선택특약)) {
                    try {
                        WebElement td = driver.findElement(By.xpath("//table[@id='trtyTable']//td[normalize-space()='" + treatyName + "']"));
                        WebElement tr = td.findElement(By.xpath("./parent::tr"));
                        WebElement joinInput = tr.findElement(By.xpath("./td[1]/input"));
                        WebElement insTermSelect = tr.findElement(By.xpath("./td[3]/select"));
                        WebElement napTermSelect = tr.findElement(By.xpath("./td[4]/select"));
                        WebElement assureMoneyInput = tr.findElement(By.xpath("./td[5]/input"));

                        if (treaty.productGubun.equals(CrawlingTreaty.ProductGubun.선택특약) && !joinInput.isSelected()) {
                            String script = "arguments[0].click()";
                            executeJavascript(script, joinInput);
                        }

                        // 보기 클릭
                        List<WebElement> insTermOptionList = insTermSelect.findElements(By.xpath(".//option"));
                        if (insTermOptionList.size() == 1) {
                            String optionText = insTermOptionList.get(0).getText().trim();
                            if (!optionText.equals(insTerm)) {
                                insTerm = "종신";
                                treaty.insTerm = "종신";
                            }
                        }
                        selectOptionByText(insTermSelect, insTerm);

                        // 납기 클릭
                        if (!napTerm.equals("전기납") && (insTerm.equals(napTerm))) {
                            napTerm = "전기납";
                            treaty.napTerm = "전기납";
                        }
                        selectOptionByText(napTermSelect, napTerm);

                        // 가입금액 설정
                        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / 10000);
                        setTextToInputBox(assureMoneyInput, treatyAssureMoney);

                    } catch (NoSuchElementException e) {
                        logger.info("해당 특약({})에 입력할 수 있는 element가 없습니다.", treatyName);
                    }
                }
            }

            // 홈페이지에서 선택된 특약리스트
            homepageTreaties = getHomepageTreaties(treatyList);

            boolean result = advancedCompareTreaties(homepageTreaties, treatyList, new CrawlingTreatyEqualStrategy2());

            if (result) {
                logger.info("특약 정보 모두 일치 ^^");
            } else {
                throw new Exception("특약 불일치");
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    /**
     * 홈페이지에서 선택된 특약리스트 세팅
     * @param treatyList        가설 특약리스트
     * @return homepageTreaties 홈페이지에서 선택된 특약리스트
     * @throws CommonCrawlerException
     */
    protected List<CrawlingTreaty> getHomepageTreaties(List<CrawlingTreaty> treatyList) throws CommonCrawlerException {

        List<CrawlingTreaty> homepageTreaties = new ArrayList<>();

        try {
            // 셋팅 된 특약들 가져오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            List<WebElement> $trList = driver.findElements(By.xpath("//table[@id='trtyTable']/tbody/tr"));

            // 주계약 셋팅
            WebElement $mainAssureMoneySelect = driver.findElement(By.id("mnContEntAmt"));
            WebElement $mainInsTermSelect = driver.findElement(By.id("mnInsrPrdYys"));
            WebElement $mainNapTermSelect = driver.findElement(By.id("mnInsrPadPrdYys"));

            String mainAssureMoney = String.valueOf(helper.executeJavascript(script, $mainAssureMoneySelect));
            String mainInsTerm = String.valueOf(helper.executeJavascript(script, $mainInsTermSelect));
            String mainNapTerm = String.valueOf(helper.executeJavascript(script, $mainNapTermSelect));

            CrawlingTreaty mainTreaty = new CrawlingTreaty();
            mainTreaty.treatyName = treatyList.get(0).treatyName;
            mainTreaty.assureMoney = Integer.parseInt(mainAssureMoney) * 10000;
            mainTreaty.insTerm = mainInsTerm;
            mainTreaty.napTerm = mainNapTerm;

            homepageTreaties.add(mainTreaty);

            for (WebElement $tr : $trList) {
                WebElement $joinInput = $tr.findElement(By.xpath("./td[1]/input"));

                if ($joinInput.isSelected()) {
                    WebElement $targetTreatyName = $tr.findElement(By.xpath("./td[2]"));
                    String targetTreatyName = $targetTreatyName.getText();
                    WebElement $insTermSelect = $tr.findElement(By.xpath("./td[3]/select"));
                    WebElement $napTermSelect = $tr.findElement(By.xpath("./td[4]/select"));
                    WebElement $assureMoneyInput = $tr.findElement(By.xpath("./td[5]/input"));

                    String targetInsTerm = String.valueOf(helper.executeJavascript(script, $insTermSelect));
                    String targetNapTerm = String.valueOf(helper.executeJavascript(script, $napTermSelect));
                    int targetAssureMoney = Integer.parseInt($assureMoneyInput.getAttribute("value")) * 10000;

                    CrawlingTreaty targetTreaty = new CrawlingTreaty();
                    targetTreaty.treatyName = targetTreatyName;
                    targetTreaty.insTerm = targetInsTerm;
                    targetTreaty.napTerm = targetNapTerm;
                    targetTreaty.assureMoney = targetAssureMoney;

                    homepageTreaties.add(targetTreaty);
                }
            }

        } catch (Exception e) {
            throw new CommonCrawlerException("홈페이지에서 선택된 특약을 세팅하던 중 오류가 발생했습니다.\n" + e.getMessage());
        }

        return homepageTreaties;
    }



    /*
        setTreaty와 동일하나 특약비교할 때 보가/납기도 비교하기 위해 생성
        todo ::
         compareTreaties()를 advancedCompareTreaties()로 변경해도 다른 상품들에 영향없는지 확인 후 setTreaty2()를 지우고
         특약비교부분을 분리할 것
     */
    protected void setTreaty2(List<CrawlingTreaty> treatyList) throws SetTreatyException {

        try {
            List<CrawlingTreaty> homepageTreaties = new ArrayList<>();

            for (CrawlingTreaty treaty : treatyList) {
                String treatyName = treaty.treatyName;
                String treatyAssureMoney = String.valueOf(treaty.assureMoney);
                String insTerm = treaty.insTerm;
                String napTerm = treaty.napTerm;

                if (treaty.productGubun.equals(CrawlingTreaty.ProductGubun.선택특약)) {
                    try {
                        WebElement td = driver.findElement(By.xpath("//table[@id='trtyTable']//td[text()[contains(.,'" + treatyName + "')]]"));
                        WebElement tr = td.findElement(By.xpath("./parent::tr"));
                        WebElement joinInput = tr.findElement(By.xpath("./td[1]/input"));
                        WebElement insTermSelect = tr.findElement(By.xpath("./td[3]/select"));
                        WebElement napTermSelect = tr.findElement(By.xpath("./td[4]/select"));
                        WebElement assureMoneyInput = tr.findElement(By.xpath("./td[5]/input"));

                        if (treaty.productGubun.equals(CrawlingTreaty.ProductGubun.선택특약) && !joinInput.isSelected()) {
                            String script = "arguments[0].click()";
                            executeJavascript(script, joinInput);
                        }

                        //보기 클릭
                        selectOptionByText(insTermSelect, insTerm);

                        //납기 클릭
                        if(!napTerm.equals("전기납")){
                            napTerm = (insTerm.equals(napTerm)) ? "전기납" : napTerm;
                        }
                        selectOptionByText(napTermSelect, napTerm);

                        //가입금액 설정
                        treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / 10000);
                        setTextToInputBox(assureMoneyInput, treatyAssureMoney);

                    } catch (NoSuchElementException e) {
                        logger.info("해당 특약({})에 입력할 수 있는 element가 없습니다.", treatyName);
                    }
                }
            }

            // 위를 통해 셋팅 된 특약들 가져오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            List<WebElement> $trList = driver.findElements(By.xpath("//table[@id='trtyTable']/tbody/tr"));

            // 주계약 셋팅
            WebElement $mainAssureMoneySelect = driver.findElement(By.id("mnContEntAmt"));
            String mainAssureMoney = String.valueOf(helper.executeJavascript(script, $mainAssureMoneySelect));
            CrawlingTreaty mainTreaty = new CrawlingTreaty();
            mainTreaty.treatyName = treatyList.get(0).treatyName;
            mainTreaty.insTerm = treatyList.get(0).insTerm;
            mainTreaty.napTerm = treatyList.get(0).napTerm;
            mainTreaty.assureMoney = Integer.parseInt(mainAssureMoney) * 10000;
            homepageTreaties.add(mainTreaty);

            for (WebElement $tr : $trList) {
                try {
                    WebElement $joinInput = $tr.findElement(By.xpath("./td[1]/input"));
                    if ($joinInput.isSelected()) {
                        WebElement $targetTreatyName = $tr.findElement(By.xpath("./td[2]"));
                        String targetTreatyName = $targetTreatyName.getText();
                        WebElement $insTermSelect = $tr.findElement(By.xpath("./td[3]/select"));
                        WebElement $napTermSelect = $tr.findElement(By.xpath("./td[4]/select"));
                        WebElement $assureMoneyInput = $tr.findElement(By.xpath("./td[5]/input"));

                        String targetInsTerm = String.valueOf(helper.executeJavascript(script, $insTermSelect));
                        String targetNapTerm = String.valueOf(helper.executeJavascript(script, $napTermSelect));
                        int targetAssureMoney = Integer.parseInt($assureMoneyInput.getAttribute("value")) * 10000;

                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.treatyName = targetTreatyName;
                        targetTreaty.insTerm = targetInsTerm;
                        targetTreaty.napTerm = targetNapTerm;
                        targetTreaty.assureMoney = targetAssureMoney;

                        homepageTreaties.add(targetTreaty);
                    }
                } catch (Exception e) {
                    logger.info("홈페이지에 셋팅된 특약 가져오는 부분에서 에러 발생");
                }
            }

            boolean result = advancedCompareTreaties(homepageTreaties, treatyList, new CrawlingTreatyEqualStrategy2());

            if (result) {
                logger.info("특약 정보 모두 일치 ^^");
            } else {
                throw new Exception("특약 불일치");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /*********************************************************
     * <특약리스트를 돌면서 특약의 가입조건(금액만, 나머지는 고정)을 세팅 메소드>
     * @param  treatyList {CrawlingTreaty} - 특약 리스트
     * @throws Exception - 특약 세팅시 예외처리
     *********************************************************/
    protected void setTreatyAssureMoney(List<CrawlingTreaty> treatyList) throws SetTreatyException {

        try {
            List<CrawlingTreaty> homepageTreaties = new ArrayList<>();

            for (CrawlingTreaty treaty : treatyList) {
                String treatyName = treaty.treatyName;
                String treatyAssureMoney = String.valueOf(treaty.assureMoney);

                if (treaty.productGubun.equals(CrawlingTreaty.ProductGubun.선택특약)) {
                    try {
                        List<WebElement> $tdList = driver.findElements(By.xpath("//table[@id='trtyTable']//td[text()[contains(.,'" + treatyName + "')]]"));
                        for (WebElement $td : $tdList) {
                            WebElement $tr = $td.findElement(By.xpath("./parent::tr"));
                            WebElement $joinInput = $tr.findElement(By.xpath("./td[1]/input"));
                            WebElement $assureMoneyInput = $tr.findElement(By.xpath("./td[5]/input"));

                            helper.moveToElementByJavascriptExecutor($joinInput);

                            if (!$joinInput.isSelected()) {
                                String script = "arguments[0].click()";
                                executeJavascript(script, $joinInput);
                            }

                            //가입금액 설정
                            String targetAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / 10000);
                            setTextToInputBox($assureMoneyInput, targetAssureMoney);
                        }

                    } catch (NoSuchElementException e) {
                        logger.info("해당 특약({})에 입력할 수 있는 element가 없습니다.", treatyName);
                    }
                }
            }

            // 위를 통해 셋팅 된 특약들 가져오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            List<WebElement> $trList = driver.findElements(By.xpath("//table[@id='trtyTable']/tbody/tr"));

            // 주계약 셋팅
            WebElement $mainAssureMoneySelect = driver.findElement(By.id("mnContEntAmt"));
            String mainAssureMoney = String.valueOf(helper.executeJavascript(script, $mainAssureMoneySelect));
            CrawlingTreaty mainTreaty = new CrawlingTreaty();
            mainTreaty.treatyName = treatyList.get(0).treatyName;
            mainTreaty.assureMoney = Integer.parseInt(mainAssureMoney) * 10000;
            homepageTreaties.add(mainTreaty);

            for (WebElement $tr : $trList) {
                try {
                    WebElement $joinInput = $tr.findElement(By.xpath("./td[1]/input"));
                    if ($joinInput.isSelected()) {
                        WebElement $targetTreatyName = $tr.findElement(By.xpath("./td[2]"));
                        String targetTreatyName = $targetTreatyName.getText();
                        WebElement $assureMoneyInput = $tr.findElement(By.xpath("./td[5]/input"));

                        int targetAssureMoney = Integer.parseInt($assureMoneyInput.getAttribute("value")) * 10000;

                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.treatyName = targetTreatyName;
                        targetTreaty.assureMoney = targetAssureMoney;

                        homepageTreaties.add(targetTreaty);
                    }
                } catch (Exception e) {
                    logger.info("홈페이지에 셋팅된 특약 가져오는 부분에서 에러 발생");
                }
            }

            for (CrawlingTreaty homepageTreaty : homepageTreaties) {
                boolean check = false;
                for (CrawlingTreaty treaty : treatyList) {
                    if (homepageTreaty.treatyName.contains(treaty.treatyName)) {
                        if (homepageTreaty.assureMoney == treaty.assureMoney) {
                            logger.info("{} 해당 특약 확인 완료", homepageTreaty.treatyName);
                            check = true;
                            break;
                        }
                    }
                }
                if (!check) {
                    throw new Exception("특약정보가 불일치합니다. 해당 특약명 : " + homepageTreaty.treatyName);
                }
            }
            logger.info("모든 특약이 일치합니다! 가입을 진행합니다.");

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /*********************************************************
     * <보험료 계산 메소드>
     * @param  id {String} - id
     * @throws Exception - 보험료 계산시 예외처리
     *********************************************************/
    protected void calculation(String id) throws Exception {

        String alertMessage = "";

        WaitUtil.loading(2);
        element = driver.findElement(By.id(id));

        try {
            element.click();
        } catch (Exception e) {
            logger.info("다시계산하기 버튼이 없습니다!");
        }

        if (helper.isAlertShowed()) {

            Alert alert = driver.switchTo().alert();
            WaitUtil.loading(3);
            alertMessage = alert.getText();
            alert.accept();
            WaitUtil.loading(3);
            throw new Exception(alertMessage);
        }

        WaitUtil.waitFor(3);
    }



    // ABL생명 공시실 스크롤 맨 밑으로 내리기
    protected void discusroomscrollbottom(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
            ""
                + " var $div = $('#mainDiv');"
                + " $div.scrollTop($div[0].scrollHeight);");
    }



    // javascript 실행
    protected Object executeJavascript(String script, WebElement element) {

        return ((JavascriptExecutor)driver).executeScript(script, element);
    }



    // select 태그에서 해당 text의 option을 클릭한다(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void selectOptionByText(WebElement selectEl, String text) throws NotFoundTextInSelectBoxException {

        Select select = new Select(selectEl);

        try {
            select.selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            throw new NotFoundTextInSelectBoxException("selectBox에서 해당 text('" + text + "')값을 찾을 수 없습니다.");
        }
    }

    //inputBox에 text 입력하는 메서드(홈페이지, 공시실 둘 다 사용 가능한 메서드)
    protected void setTextToInputBox(WebElement element, String text) {

        element.clear();
        element.sendKeys(text);
    }



    /*********************************************************
     * <연금수령액 가져오기 메소드>
     * @param  info {CrawlingProduct} - 크롤링 상품 객체
     * @throws Exception - 연금수령액 세팅시 예외처리
     *********************************************************/
    protected void getAnnuityPremium(CrawlingProduct info) throws Exception {

        String annuityPremium = "";
        String fixedAnnuityPremium = "";
        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        driver.findElement(By.id("tabAnut")).click();
        WaitUtil.loading(3);

        try {
            element = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8)"));
            elements = element.findElements(By.tagName("td"));

            if (info.annuityType.contains("종신")) { // 종신연금형일 경우
                if (info.annuityType.contains("10년")) { // 종신 10년일 경우
                    annuityPremium = driver.findElement(By.cssSelector(
                                    "#anutTab > table:nth-child(8) > tbody > tr:nth-child(1) > td:nth-child(4)"))
                            .getText().replaceAll("10년", "").replaceAll("[^0-9]", "");
                    info.annuityPremium = annuityPremium + "0000"; // 매년
                    logger.info("종신연금수령액: " + info.annuityPremium + "원");

                } else if (info.annuityType.contains("20년")) { // 종신 20년일 경우
                    annuityPremium = driver.findElement(By.cssSelector(
                                    "#anutTab > table:nth-child(8) > tbody > tr:nth-child(2) > td:nth-child(2)"))
                            .getText().replaceAll("20년", "").replaceAll("[^0-9]", "");
                    info.annuityPremium = annuityPremium + "0000"; // 매년
                    logger.info("종신연금수령액: " + info.annuityPremium + "원");
                }

            } else if (info.annuityType.contains("확정")) { // 확정연금형일 경우

                if (info.annuityType.contains("10년")) { // 확정 10년일 경우

                    fixedAnnuityPremium = driver.findElement(By.cssSelector(
                                    "#anutTab > table:nth-child(8) > tbody > tr:nth-child(3) > td:nth-child(3)"))
                            .getText().replaceAll("10년", "").replaceAll("[^0-9]", "");
                    info.fixedAnnuityPremium = fixedAnnuityPremium + "0000";
                    ; // 매년
                    logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");
                } else if (info.annuityType.contains("15년")) { // 확정 15년일 경우
                    fixedAnnuityPremium = driver.findElement(By.cssSelector(
                                    "#anutTab > table:nth-child(8) > tbody > tr:nth-child(4) > td:nth-child(2)"))
                            .getText().replaceAll("15년", "").replaceAll("[^0-9]", "");
                    info.fixedAnnuityPremium = fixedAnnuityPremium + "0000";
                    ; // 매년
                    logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");
                } else if (info.annuityType.contains("20년")) { // 확정 20년일 경우

                    fixedAnnuityPremium = driver.findElement(By.cssSelector(
                                    "#anutTab > table:nth-child(8) > tbody > tr:nth-child(5) > td:nth-child(2)"))
                            .getText().replaceAll("20년", "").replaceAll("[^0-9]", "");
                    info.fixedAnnuityPremium = fixedAnnuityPremium + "0000"; // 매년
                    logger.info("확정연금수령액: " + info.fixedAnnuityPremium + "원");
                }
            }

            // 종신형
            planAnnuityMoney
                .setWhl10Y(
                    driver
                        .findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(1) > td:nth-child(4)"))
                        .getText()
                        .replaceAll("[^0-9]", "") + "0000"
                );    //종신 10년

            planAnnuityMoney
                .setWhl20Y(
                    driver
                        .findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(2) > td:nth-child(2)"))
                        .getText()
                        .replaceAll("[^0-9]", "") + "0000"
                );    //종신 20년

            // 확정형
            String Fxd10
                = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(3) > td:nth-child(3)"))
                    .getText();  // 확정 10년
            int Fxd10last = Fxd10.indexOf("씩"); // 매년기준이기 때문에 문자열 "씩" 앞까지만

            String Fxd15
                = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(4) > td:nth-child(2)"))
                    .getText();  // 확정 15년
            int Fxd15last = Fxd15.indexOf("씩"); // 매년기준이기 때문에 문자열 "씩" 앞까지만

            String Fxd20y
                = driver.findElement(By.cssSelector("#anutTab > table:nth-child(8) > tbody > tr:nth-child(5) > td:nth-child(2)"))
                    .getText();  // 확정 20년
            int Fxd20last = Fxd20y.indexOf("씩"); // 매년기준이기 때문에 문자열 "씩" 앞까지만

            planAnnuityMoney.setFxd10Y(
                Fxd10.substring(0, Fxd10last + 1).replaceAll("[^0-9]", "") + "0000"
            );    //확정 10년
            planAnnuityMoney.setFxd15Y(
                Fxd15.substring(0, Fxd15last + 1).replaceAll("[^0-9]", "") + "0000"
            );    //확정 15년
            planAnnuityMoney.setFxd20Y(
                Fxd20y.substring(0, Fxd20last + 1).replaceAll("[^0-9]", "") + "0000"
            );    //확정 20년

            logger.info("종신10년 :: " + planAnnuityMoney.getWhl10Y());
            logger.info("종신20년 :: " + planAnnuityMoney.getWhl20Y());
            logger.info("확정10년 :: " + planAnnuityMoney.getFxd10Y());
            logger.info("확정15년 :: " + planAnnuityMoney.getFxd15Y());
            logger.info("확정20년 :: " + planAnnuityMoney.getFxd20Y());

            info.planAnnuityMoney = planAnnuityMoney;

        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }



    // 연금지급방법(매월, 매년 등)
    protected void setAnnPayment(Object...obj) throws Exception {

        String title = "연금지급방법";
        String tagId = (String) obj[0];
        String expectAnnPayment = (((String) obj[1]).contains("매년")) ? "12" : "01"; // 매년 = 12 (default), 매월 = 01
        String actualAnnPayment = "";
        try {
            elements = driver.findElements(By.cssSelector("#" + tagId + " > option"));

            WebElement $selectOption = null;
            for (WebElement $option : elements) {
                if ($option.getAttribute("value").contains(expectAnnPayment)) {
                    $selectOption = $option;
                    $option.click();
                    logger.info($option.getText() + "클릭!");
                    break;
                }
            }
            actualAnnPayment = $selectOption.getAttribute("value").trim();
            super.printLogAndCompare(title, expectAnnPayment, actualAnnPayment);

            WaitUtil.loading(4);

        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }



    /**
     * 납입주기를 한글 형태의 문자열로 리턴한다.
     *  => 01을 전달하면 "월납"이라는 문자열을 리턴한다.
     *  @param napCycle : 납입주기       ex.01, 00, ...
     *  @return napCycleName : 납입주기의 한글 형태       ex.월납, 연납, ...
     */
    protected String getNapCycleName(String napCycle) {
        String napCycleText = "";

        if (napCycle.equals("01")) {
            napCycleText = "월납";
        } else if (napCycle.equals("02")) {
            napCycleText = "년납";
        } else if (napCycle.equals("00")) {
            napCycleText = "일시납";
        }

        return napCycleText;
    }
}