package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
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
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;



public abstract class CrawlingDBFDirect extends CrawlingDBFNew {

    /**
     * DBF 다이렉트 생년월일 셋팅
     * @param obj
     * obj[0] : By (생년월일 입력 input 위치) (필수)
     * obj[1] : info.fullBirth (필수, 고정값)
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
     * DBF 다이렉트 성별 셋팅
     * @param obj
     * obj[0] : 성별 input name (필수)
     * obj[1] : info.gender (필수, 고정값)
     * @throws SetGenderException
     */
    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";
        String tagName = (String) obj[0];
        int gender = (int) obj[1];
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String script = "return $('input[name=" + tagName + "]:checked').attr('id');";

        try {
            WebElement $genderLabel = driver.findElement(By.xpath("//span[contains(.,'" + expectedGenderText + "')]"));
            helper.waitElementToBeClickable($genderLabel).click();

            String actualGenderId = String.valueOf(helper.executeJavascript(script, expectedGenderText));
            String actualGenderText = driver.findElement(By.xpath("//label[@for='" + actualGenderId + "']")).getText();

            super.printLogAndCompare(title, expectedGenderText, actualGenderText);
            WaitUtil.loading(1);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 다이렉트 운전형태 선택
     * @param obj
     * obj[0] : 운전 형태 input tagName (필수)
     * obj[1] : 운전 형태 값 (필수)
     * @throws SetVehicleException
     */
    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {
        String title = "운전 형태";

        String tagName = (String) obj[0];
        String expectedVehicleText = (String) obj[1];
        String script = "return $('input[name=" + tagName + "]:checked').attr('id');";

        try {
            WaitUtil.waitFor(1);

            WebElement $VehicleLabel = driver.findElement(By.xpath("//span[contains(.,'" + expectedVehicleText + "')]"));
            helper.waitElementToBeClickable($VehicleLabel).click();

            String actualVehicleId = String.valueOf(helper.executeJavascript(script, expectedVehicleText));
            String actualVehicleText = driver.findElement(By.xpath("//label[@for='" + actualVehicleId + "']")).getText();

            super.printLogAndCompare(title, expectedVehicleText, actualVehicleText);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 공시실 직업 선택
     * @param obj
     * obj[0] : 선택 할 직업명
     * @throws SetJobException
     */
    @Override
    public void setJob(Object... obj) throws SetJobException {
        String title = "직업";
        String expectedJobText = (String) obj[0];
        try {
            // 1. 직업 검색창 클릭
            logger.info("직업 검색창 클릭");
            helper.waitElementToBeClickable(driver.findElement(By.cssSelector("#jobNm"))).click();
            WaitUtil.waitFor(1);

            // 2. 직업 입력
            logger.info("직업 입력");
            driver.findElement(By.cssSelector("#searchJobNm")).sendKeys(expectedJobText);
            WaitUtil.waitFor(1);

            // 3. 검색
            logger.info("검색 클릭");
            helper.waitElementToBeClickable(driver.findElement(By.linkText("검색"))).click();
            WaitUtil.waitFor(1);

            // 4. 직업관련 동의 버튼 클릭
            logger.info("직업관련 확인버튼 클릭");
            driver.findElement(By.cssSelector("label[for='ltmJobNmChk']")).click();
            WaitUtil.waitFor(1);

            // 5. 직업 선택 완료 클릭
            logger.info("직업 선택 완료 클릭");
            driver.findElement(By.cssSelector("#_btn_job_complete_ > a")).click();
            WaitUtil.waitFor(1);

            String actualJobText = driver.findElement(By.id("jobNm")).getAttribute("value");
            super.printLogAndCompare(title, expectedJobText, actualJobText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 다이렉트 가입 유형 선택
     * @param obj
     * obj[0] : 가입 유형 input tagName (필수)
     * obj[1] : 선택할 가입 유형 (필수)
     * @throws SetProductTypeException
     */
    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        String title = "가입 유형";

        String tagName = (String) obj[0];
        String expectedProductTypeText = (String) obj[1];
        String script = "return $('input[name=" + tagName + "]:checked').attr('id');";

        try {
            WebElement $productTypeLabel = driver.findElement(By.xpath("//span[contains(.,'" + expectedProductTypeText + "')]"));
            helper.waitElementToBeClickable($productTypeLabel).click();

            String actualProductTypeId = String.valueOf(helper.executeJavascript(script, expectedProductTypeText));
            String actualProductTypeText = driver.findElement(By.xpath("//input[@id='" + actualProductTypeId + "']/parent::label/span")).getText();

            super.printLogAndCompare(title, expectedProductTypeText, actualProductTypeText);
            waitDirectLoadingImg();
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 다이렉트 보험기간 셋팅
     * @param obj
     * obj[0] : 보험 기간 input tagName (필수)
     * obj[1] : 보험 기간 값 (필수)
     * @throws SetInsTermException
     */
    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";

        String tagName = (String) obj[0];
        String expectedTermText = (String) obj[1];
        String actualTermText = "";

        try {
            List<WebElement> $termList = driver.findElements(By.xpath("//a[contains(@name, '" + tagName + "')]"));
            for (WebElement $term : $termList) {
                String termText = $term.getText().replaceAll("선택", "").trim();
                if (termText.equals(expectedTermText)) {
                    helper.waitElementToBeClickable($term).click();
                    actualTermText = termText;
                    break;
                }
            }

            super.printLogAndCompare(title, expectedTermText, actualTermText);
            waitDirectLoadingImg();
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 다이렉트 납입기간 셋팅
     * @param obj
     * obj[0] : 납입 기간 input tagName (필수)
     * obj[1] : 납입 기간 값 (필수)
     * @throws SetNapTermException
     */
    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";

        String tagName = (String) obj[0];
        String expectedTermText = (String) obj[1];
        String actualTermText = "";

        try {
            List<WebElement> $termList = driver.findElements(By.xpath("//a[contains(@name, '" + tagName + "')]"));
            for (WebElement $term : $termList) {
                String termText = $term.getText().trim();
                if (termText.equals(expectedTermText)) {
                    $term.click();
                    actualTermText = termText;
                    break;
                }
            }

            super.printLogAndCompare(title, expectedTermText, actualTermText);
            waitDirectLoadingImg();
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 다이렉트 납입주기 셋팅
     * @param obj
     * obj[0] : 납입 주기 input tagName (필수)
     * obj[1] : 납입 주기 값 (필수)
     * @throws SetNapCycleException
     */
    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";

        String tagName = (String) obj[0];
        String napCycle = (String) obj[1];
        String expectedCycleText = napCycle.equals("01") ? "월납" : "연납";

        String script = "return $('input[name=" + tagName + "]:checked').attr('id');";

        String actualCycleText = "";

        try {
            WebElement $cycleLabel = driver.findElement(By.xpath("//span[contains(.,'" + expectedCycleText + "')]"));
            helper.waitElementToBeClickable($cycleLabel).click();

            String actualCycleId = String.valueOf(helper.executeJavascript(script, expectedCycleText));
            actualCycleText = driver.findElement(By.xpath("//input[@id='" + actualCycleId + "']/parent::label/span")).getText();

            super.printLogAndCompare(title, expectedCycleText, actualCycleText);
            waitDirectLoadingImg();
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 다이렉트 보장유형 셋팅
     * @param obj
     * obj[0] : 보장내용 input tagName (필수)
     * obj[1] : 보장내용 값 (필수)
     * @throws Exception
     */
    public void setWarranty(Object...obj) throws Exception {
        String title = "보장내용";

        String tagName = (String) obj[0];
        String expectedWarrantyText = (String) obj[1];
        String script = "return $('input[name=" + tagName + "]:checked').attr('id');";

        try {
            WebElement $productTypeLabel = driver.findElement(By.xpath("//span[contains(.,'" + expectedWarrantyText + "')]"));
            helper.waitElementToBeClickable($productTypeLabel).click();

            String actualWarrantyId = String.valueOf(helper.executeJavascript(script, expectedWarrantyText));
            String actualWarrantyText = driver.findElement(By.xpath("//label[@for='" + actualWarrantyId + "']")).getText();

            if (actualWarrantyText.contains(expectedWarrantyText)) {
               logger.info("선택한 보장내용 일치: {}", actualWarrantyText);
            } else {
                throw new Exception("선택한 보장내용이 불일치 합니다!");
            }
            waitDirectLoadingImg();
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new Exception("보장내용 선택과정 오류입니다");
        }

    }



    /**
     * DBF 다이렉트 특약 셋팅
     * @param obj
     * obj[0] : 해당 가입설계 특약리스트
     * TODO: 현재 기존에 사용하던 코드 그대로 가져온거라 후에 수정요망(셋팅 구조 변경 및 검증 로직 추가)
     * @throws SetTreatyException
     */
    public void setTreaties(Object... obj) throws Exception {
        WaitUtil.waitFor(1);

        List<CrawlingTreaty> treatyList = (List<CrawlingTreaty>) obj[0];

        List<WebElement> targetTreatyEl = driver.findElements(By.cssSelector(".plan_select.ui_plan_select dd div strong"));
        List<String> targetTreatyList = new ArrayList<>();	// web 특약
        List<String> myTreatyList = new ArrayList<>();		// 내 특약

        for (WebElement target : targetTreatyEl) {
            targetTreatyList.add(target.getText());
        }

        for (CrawlingTreaty myTreaty : treatyList) {
            myTreatyList.add(myTreaty.treatyName);
        }

        targetTreatyList.removeAll(myTreatyList);

        //미가입 처리할 특약들만 돈다.
        for (int i=0; i<targetTreatyList.size(); i++) {
            boolean hasJoinBtn = true;			// 가입여부버튼
            String treatyName = targetTreatyList.get(i);	//미가입시킬 특약명
            WebElement findEl = driver.findElement(By.xpath("//strong[contains(text(), '" + treatyName + "')]"));
            List<WebElement> ddEl = findEl.findElement(By.xpath("ancestor::dl")).findElements(By.tagName("dd"));

            int ddIdx = 0;
            for (WebElement dd : ddEl) {
                if (dd.findElement(By.cssSelector("strong")).getText().equals(treatyName)) {
                    break;
                }
                ddIdx++;
            }

            WebElement joinEl = findEl.findElements(By.xpath("ancestor::ul[@class='plan_select ui_plan_select']/li")).get(2);
            WebElement joinBtn = null;
            String signOnOff = "";

            try {
                joinBtn = joinEl.findElements(By.tagName("dd")).get(ddIdx).findElement(By.cssSelector(".signup a"));

                signOnOff = joinEl.findElements(By.tagName("dd")).get(ddIdx).findElement(By.cssSelector("em[class=hide_txt]")).getText();
            } catch (NoSuchElementException e) {
                hasJoinBtn = false;
                logger.info("해당 특약은 필수가입 특약입니다.");
            }

            // 가입여부버튼이 존재하고, 가입으로 세팅되어있을 경우에만 미가입으로 변경이 가능하다.
            if (hasJoinBtn && signOnOff.equals("ON")) {
                joinBtn.click();
                WaitUtil.waitFor(1);
            }

        }
    }



    /**
     * DBF 다이렉트 월 납입보험료 셋팅
     * @param obj
     * obj[0] : 산출된 합계보험료 element
     * obj[1] : CrawlingProduct info
     * @throws PremiumCrawlerException
     */
    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            WebElement $monthlyPremiumTd = (WebElement) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            String premium = $monthlyPremiumTd.getText().replaceAll("[^0-9]", "");
            info.treatyList.get(0).monthlyPremium = premium;
            logger.info("월 보험료 확인 : " + premium);

            if ("0".equals(info.treatyList.get(0).monthlyPremium)) {
                throw new Exception("주계약 보험료는 0원일 수 없습니다");
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 다이렉트 해약환급금 크롤링
     * @param obj
     * obj[0] : CrawlingProduct info (필수)
     */
    public void getReturnPremium(Object...obj) throws Exception {
        CrawlingProduct info = (CrawlingProduct) obj[0];

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> $refundTrList = helper.waitPresenceOfElementLocated(By.id("tbodyExCancel")).findElements(By.tagName("tr"));

        for (WebElement tr : $refundTrList) {

            String term = helper.waitVisibilityOf(tr.findElement(By.tagName("th"))).getText();
            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
            String premiumSum = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(0)).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

            String returnMoneyMin = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(1)).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
            String returnRateMin = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(2)).getText();
            logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

            String returnMoneyAvg = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(3)).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
            String returnRateAvg = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(4)).getText();
            logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

            String returnMoney = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(5)).getText().replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
            String returnRate = helper.waitVisibilityOf(tr.findElements(By.tagName("td")).get(6)).getText();
            logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);
            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoneyAvg;
        }

        info.planReturnMoneyList = planReturnMoneyList;
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {}

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {}

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {}

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {}

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {}

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {}

    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {}

    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {}

    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {}

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {}

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {}

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {}

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}



    // 로딩이미지 명시적 대기
    protected void waitDirectLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading")));
    }



    // 다시계산하기 버튼이 있는경우 클릭 그외 바로 넘김
    protected void reComputeCssSelect(By cssSelect) throws Exception {
        element = driver.findElement(cssSelect);

        if (element.isDisplayed()) {
            logger.info("다시계산 버튼 클릭");
            element.click();
            waitDirectLoadingImg();
        } else {
            logger.info("다시계산 버튼 없음");
        }
        WaitUtil.waitFor(1);
    }



    // 이벤트 팝업 체크
    public void checkPopUp() {

        By popUpDivBy = By.xpath("//div[@id='popEventChildNoti']");
        if (helper.existElement(popUpDivBy)) {
            WebElement buttonDiv = driver.findElement(popUpDivBy);
            WebElement button = buttonDiv.findElement(By.xpath(".//span[text()='확인']"));

            String script = "arguments[0].click();";
            helper.executeJavascript(script, button);
        }
    }

}