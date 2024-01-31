package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
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
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public abstract class CrawlingSFIDirect extends CrawlingSFINew {

    /**
     * 원수사 페이지 접속시 안내 모달창이 간혹 뜨기도 함.
     * 안내 모달창이 존재하는지 여부를 판단해서 처리를 진행한다.
     * @throws Exception
     */
    public void modalCheck() throws Exception {
        boolean isDomExist = false;
        By modalPosition = By.xpath("//div[@class='modal-dialog']");

        isDomExist = helper.existElement(modalPosition);
        if (isDomExist) {
            List<WebElement> $modals = driver.findElements(modalPosition);

            // 모달창 뒤에서부터 닫기. 맨 위에 떠 있는 모달창이 뒤에 위치한다.
            for (int i = $modals.size() - 1; i >= 0; i--) {
                logger.info("안내 모달창 뜸");
                WebElement $modal = $modals.get(i);
                WebElement $button = $modal.findElement(By.id("btn-confirm"));

                logger.info("안내 모달창 확인 버튼 클릭");
                click($button);
                WaitUtil.waitFor(1);
            }
        }

        popUpCheck();
        WaitUtil.waitFor(1);
    }



    /**
     * 원수사 페이지 접속시 이벤트 팝업이 간혹 뜨기도 함.
     * 안내 모달창이 존재하는지 여부를 판단해서 처리를 진행한다.
     * @throws Exception
     */
    public void popUpCheck() throws Exception {
        boolean isDomExist = false;
        By modalPosition = By.xpath("//div[@class='modal-content']");

        isDomExist = helper.existElement(modalPosition);
        if (isDomExist) {
            logger.info("이벤트 팝업 발생");
            WebElement $popUp = driver.findElement(modalPosition);
            WebElement $button = $popUp.findElement(By.id("btn-confirm"));

            logger.info("이벤트 팝업 확인 버튼 클릭");
            click($button);
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {

            WaitUtil.loading(1);
            By birthInputBy = obj.length > 1 ? (By) obj[1] : By.id("birthS-input");

            // 생년월일 element 찾기
            WebElement $birthInput = helper.waitElementToBeClickable(driver.findElement(birthInputBy));

            // 생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

            // 생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        String expectedGenderText = "";
        String actualGenderText = "";

        int gender = (int) obj[0];
        if (obj.length > 1){
            expectedGenderText = (String) obj[1];
        } else {
            expectedGenderText = (gender == MALE) ? "남" : "여";
        }

        try {
            // 성별 element 찾기
            By genderPath = By.id("gender-radio");
            if (!helper.existElement(genderPath)) {
                genderPath = By.xpath("//label[@for='gender-radio-" + gender + "'");
            }
            WebElement $genderDiv = driver.findElement(genderPath);
            WebElement $genderLabel = $genderDiv.findElement(By.xpath("./label[normalize-space()='" + expectedGenderText + "']"));

            // 성별 클릭
            click($genderLabel);

            // 실제 선택된 성별 값 읽어오기
            $genderLabel = $genderDiv.findElement(By.xpath("./label[@class[contains(., 'active')]]"));
            actualGenderText = $genderLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setJob(Object... obj) throws SetJobException {
        String title = "직업정보";

        String expectedJob = (String) obj[0];
        String actualJob = "";
        WebElement $button = null;
        WebElement $a = null;

        try {
            logger.info("직업 찾기 버튼 클릭");
            WebElement $jobFindButton = helper.waitElementToBeClickable(driver.findElement(By.id("job-button")));
            click($jobFindButton);

            logger.info("검색으로 찾기 탭 클릭");
            WebElement $jobDiv = null;

            try {
                $jobDiv = driver.findElement(By.id("CommonSearchJob"));
            } catch (NoSuchElementException ne){
                $jobDiv = driver.findElement(By.id("V3CommonSearchJob"));
            }
            $a = $jobDiv.findElement(By.linkText("검색으로 찾기"));
            click($a);

            logger.info("직업 입력");
            WebElement $jobInput = helper.waitElementToBeClickable(driver.findElement(By.id("sjob-search-text")));
            helper.sendKeys4_check($jobInput, expectedJob);
            WaitUtil.waitFor(5);

            logger.info("검색된 직업 목록 중 첫번째 직업 클릭");
            WebElement $jobSearchResultDiv = driver.findElement(By.id("sjob-search-result"));
            WebElement $jobEm = $jobSearchResultDiv.findElement(By.xpath("//em[normalize-space()='" + expectedJob + "']"));
            WebElement $jobButton = $jobEm.findElement(By.xpath("./ancestor::button[1]"));
            click($jobButton);

            // 실제 선택된 직업 값 읽어오기
            String script = "return $(arguments[0]).val();";
            $jobInput = driver.findElement(By.id("sjob-selected"));
            actualJob = String.valueOf(helper.executeJavascript(script, $jobInput));

            logger.info("직업 변경에 대한 알림 확인 체크박스 체크");
            WebElement $jobChangeAgreeInput = driver.findElement(By.id("sjob-select-agree"));
            WebElement $jobChangeAgreeLabel = $jobChangeAgreeInput.findElement(By.xpath("./parent::label"));
            click($jobChangeAgreeLabel);

            logger.info("직업 창 확인 버튼 클릭");
            WebElement $jobFooter = $jobDiv.findElement(By.xpath(".//div[contains(@class, 'modal-footer')]"));
            $button = $jobFooter.findElement(By.xpath(".//button[contains(., '확인')]"));
            click($button);

            // 비교
            super.printLogAndCompare(title, expectedJob, actualJob);

       } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입방법";
        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {
            /**
             * 어떤 다이렉트 상품의 경우 크롤링 서버를 통해 원수사 사이트에 접속하게 되면
             * 납입방법 ui가 깨져있다. 해당 경우에 selenium이 제공하는 click 기능이 작동하지 않는다.
             * 따라서 jquery 문법으로 클릭시켜야한다.
             */
            String script = "$(arguments[0]).click();";

            WebElement $napCycleAreaDd = driver.findElement(By.id("payment-method"));
            WebElement $napCycleLabel = $napCycleAreaDd.findElement(By.xpath(".//label[normalize-space()='" + expectedNapCycle + "']"));
            helper.executeJavascript(script, $napCycleLabel);

            // 실제 선택된 납입방법 값 읽어오기(원수사에서는 실제 선택된 납입방법 element 클래스 속성에 active를 준다)
            $napCycleLabel = $napCycleAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualNapCycle = $napCycleLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e, exceptionEnum.getMsg());
        }
    }



    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "플랜";
        String actualPlan = "";
        String[] textTypes = expectedPlan.split("\\|");
        String script = "";

        try {
            // 플랜 관련 element 찾기
            WebElement $planAreaThead = driver.findElement(By.id("coverage-header"));
            WebElement $planLabel = null;

            // 플랜 선택과 비교를 편하게 하기 위해 불필요한 element 삭제 처리
            script = "$(arguments[0]).find('th > label > span').remove();";
            helper.executeJavascript(script, $planAreaThead);

            for (String textType : textTypes) {
                try {
                    //플랜 클릭
                    textType = textType.trim();

                    $planLabel = $planAreaThead.findElement(By.xpath(".//label[normalize-space()='" + textType + "']"));
                    click($planLabel);
                    expectedPlan = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            // 플랜 선택과 비교를 편하게 하기 위해 불필요한 element 삭제 처리
            helper.executeJavascript(script, $planAreaThead);

            // 실제 선택된 플랜 값 읽어오기
            $planLabel = $planAreaThead.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualPlan = $planLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }



    public void setPlan2(String expectedPlan) throws CommonCrawlerException {
        String title = "플랜";
        String actualPlan = "";
        String[] textTypes = expectedPlan.split("\\|");
        String script = "";

        try {
            // 플랜 관련 element 찾기
            WebElement $planAreaThead = driver.findElement(By.cssSelector(".coverage-header"));
//            WebElement $planAreaThead = driver.findElement(By.xpath("//div[@id='plan-code']"));
            WebElement $planLabel = null;

            // 플랜 선택과 비교를 편하게 하기 위해 불필요한 element 삭제 처리
            script = "$(arguments[0]).find('label > div').remove();";
            helper.executeJavascript(script, $planAreaThead);

            for (String textType : textTypes) {
                try {
                    //플랜 클릭
                    textType = textType.trim();

                    $planLabel = $planAreaThead.findElement(By.xpath(".//label[contains(.,'" + textType + "')]"));
                    click($planLabel);
                    expectedPlan = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            // 플랜 선택과 비교를 편하게 하기 위해 불필요한 element 삭제 처리
            helper.executeJavascript(script, $planAreaThead);

            // 실제 선택된 플랜 값 읽어오기
            $planLabel = $planAreaThead.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualPlan = $planLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }


    /**
     * 영업용 자동차 운전 여부 설정
     * @param expectedVehicle
     * @throws SetVehicleException
     */
    public void setVehicle(String expectedVehicle) throws SetVehicleException {
        String title = "영업용 자동차 운전여부";
        String actualVehicle = "";

        try {
            // 영업용 자동차 운전여부 관련 element 찾기
            WebElement $vehicleDiv = driver.findElement(By.id("business-radio"));
            WebElement $vehicleLabel = $vehicleDiv.findElement(By.xpath("./label[normalize-space()='" + expectedVehicle + "']"));
            click($vehicleLabel);

            // 실제 선택된 운전여부 값 읽어오기
            $vehicleLabel = $vehicleDiv.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualVehicle = $vehicleLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedVehicle, actualVehicle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(e, exceptionEnum.getMsg());
        }
    }



    public void setJoinType(String expectedJoinType, By... by) throws CommonCrawlerException {
        String title = "가입형태";
        String actualJoinType = "";

        try {
            By divBy = by.length > 0 ? by[0] : By.id("target-radio");;

            // 가입형태 관련 element 찾기
            WebElement $joinTypeDiv = helper.waitElementToBeClickable(helper.waitVisibilityOf(driver.findElement(divBy)));
            WebElement $joinTypeSpan = $joinTypeDiv.findElement(By.xpath("//span[normalize-space()='" + expectedJoinType + "']"));
            WebElement $joinTypeLabel = $joinTypeSpan.findElement(By.xpath("./parent::label"));

            // 가입형태 클릭
            click($joinTypeLabel);

            // 실제 선택된 가입형태 값 읽어오기
            $joinTypeDiv = driver.findElement(divBy);
            $joinTypeLabel = $joinTypeDiv.findElement(By.xpath("./label[@class[contains(., 'active')]]"));
            $joinTypeSpan = $joinTypeLabel.findElement(By.xpath("./span"));
            actualJoinType = $joinTypeSpan.getText();

            // 비교
            super.printLogAndCompare(title, expectedJoinType, actualJoinType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_JOIN_TYPE;
            throw new CommonCrawlerException(e);
        }
    }



    public void setChildType(String expectedChildType) throws CommonCrawlerException {
        String title = "자녀정보";
        String actualChildType = "";

        try {
            // 자녀정보 관련 element 찾기
            WebElement $childTypeDiv = driver.findElement(By.id("job-radio"));
            WebElement $childTypeLabel = $childTypeDiv.findElement(By.xpath(".//label[normalize-space()='" + expectedChildType + "']"));

            // 자녀정보 클릭
            click($childTypeLabel);

            // 실제 선택된 자녀정보 값 읽어오기
            $childTypeLabel = $childTypeDiv.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualChildType = $childTypeLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedChildType, actualChildType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CHILD_TYPE;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {
        String title = "연금개시나이";
        String expectedAnnuityAge = (String) obj[0];
        String actualAnnuityAge = "";
        String script = "";

        try {
            // 연금개시나이를 원수사의 연금개시나이 포맷에 맞게 text값 수정
            expectedAnnuityAge = expectedAnnuityAge + "세부터";

            // 연금개시나이 세팅을 위해 버튼 클릭
            WebElement $annuityAgeSpan = driver.findElement(By.id("dropdown-receiveAge"));
            WebElement $annuityAgeButton = $annuityAgeSpan.findElement(By.xpath(".//button[@class[contains(., 'btn-adropdown')]]"));
            click($annuityAgeButton);

            /**
             * ul에서 연금개시나이를 선택해야 하는 구조다.
             * 여기서 연금개시나이에 해당하는 ul을 찾기위해서는 :visible 속성으로 찾아야한다.
             * :visible은 By.cssSelector()로는 동작하지 않음. executeScript()를 통해서 실행해야 함.
             */

            script = "return $('ul[id^=sfddropdown-menu]:visible')[0]";
            WebElement $annuityAgeUl = (WebElement) helper.executeJavascript(script);
            selectLiByTextFromUl($annuityAgeUl, expectedAnnuityAge);

            $annuityAgeSpan = $annuityAgeSpan.findElement(By.xpath(".//span[@class='label']"));
            actualAnnuityAge = $annuityAgeSpan.getText();

            // 비교
            super.printLogAndCompare(title, expectedAnnuityAge, actualAnnuityAge);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new SetAnnuityAgeException(e, exceptionEnum.getMsg());
        }
    }



    public void setAnnuityReceivePeriod(String expectedAnnuityReceivePeriod) throws CommonCrawlerException {
        String title = "연금수령기간";
        String actualAnnuityReceivePeriod = "";
        String script = "";

        try {
            // 연금수령기간 세팅을 위해 버튼 클릭
            WebElement $annuityReceivePeriodSpan = driver.findElement(By.id("dropdown-receivePeriod"));
            WebElement $annuityReceivePeriodButton = $annuityReceivePeriodSpan.findElement(By.xpath(".//button[@class[contains(., 'btn-adropdown')]]"));
            click($annuityReceivePeriodButton);

            /**
             * ul에서 연금수령기간을 선택해야 하는 구조다.
             * 여기서 연금수령기간에 해당하는 ul을 찾기위해서는 :visible 속성으로 찾아야한다.
             * :visible은 By.cssSelector()로는 동작하지 않음. executeScript()를 통해서 실행해야 함.
             */

            script = "return $('ul[id^=sfddropdown-menu]:visible')[0]";
            WebElement $annuityReceivePeriodUl = (WebElement) helper.executeJavascript(script);
            selectLiByTextFromUl($annuityReceivePeriodUl, expectedAnnuityReceivePeriod);

            $annuityReceivePeriodSpan = $annuityReceivePeriodSpan.findElement(By.xpath(".//span[@class='label']"));
            actualAnnuityReceivePeriod = $annuityReceivePeriodSpan.getText();

            // 비교
            super.printLogAndCompare(title, expectedAnnuityReceivePeriod, actualAnnuityReceivePeriod);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_PERIOD;
            throw new SetAnnuityAgeException(e, exceptionEnum.getMsg());
        }
    }



    public void setAnnuityReceiveCycle(String expectedAnnuityReceiveCycle) throws CommonCrawlerException {
        String title = "연금수령주기";
        String actualAnnuityReceiveCycle = "";
        String script = "";

        try {
            // 연금수령주기 세팅을 위해 버튼 클릭
            WebElement $annuityReceiveCycleSpan = driver.findElement(By.id("dropdown-receiveCycle"));
            WebElement $annuityReceiveCycleButton = $annuityReceiveCycleSpan.findElement(By.xpath(".//button[@class[contains(., 'btn-adropdown')]]"));
            click($annuityReceiveCycleButton);

            /**
             * ul에서 연금수령주기를 선택해야 하는 구조다.
             * 여기서 연금수령주기에 해당하는 ul을 찾기위해서는 :visible 속성으로 찾아야한다.
             * :visible은 By.cssSelector()로는 동작하지 않음. executeScript()를 통해서 실행해야 함.
             */

            script = "return $('ul[id^=sfddropdown-menu]:visible')[0]";
            WebElement $annuityReceiveCycleUl = (WebElement) helper.executeJavascript(script);
            selectLiByTextFromUl($annuityReceiveCycleUl, expectedAnnuityReceiveCycle);

            $annuityReceiveCycleSpan = $annuityReceiveCycleSpan.findElement(By.xpath(".//span[@class='label']"));
            actualAnnuityReceiveCycle = $annuityReceiveCycleSpan.getText();

            // 비교
            super.printLogAndCompare(title, expectedAnnuityReceiveCycle, actualAnnuityReceiveCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_CYCLE;
            throw new SetAnnuityAgeException(e, exceptionEnum.getMsg());
        }
    }



    public void crawlAnnuityPremium(CrawlingProduct info) throws CommonCrawlerException {
        String title = "연금수령액 크롤링";
        String annuityType = info.getAnnuityType();
        PlanAnnuityMoney planAnnuityMoney = info.getPlanAnnuityMoney();

        try {
            WaitUtil.waitFor(5);

            WebElement $annuityPremiumDiv = driver.findElement(By.id("rolling-number"));
            WebElement $annuityPremiumStrong = $annuityPremiumDiv.findElement(By.xpath(".//strong[@class='blind']"));
            String annuityPremium = $annuityPremiumStrong.getText();
            annuityPremium = annuityPremium.replaceAll("[^0-9]", "");

            if (annuityType.contains("종신")) {
                info.annuityPremium = annuityPremium;
                planAnnuityMoney.setWhl10Y(info.annuityPremium);
                logger.info("종신 연금수령액 : {}원", info.annuityPremium);
            } else if(annuityType.contains("확정")) {
                info.fixedAnnuityPremium = annuityPremium;
                planAnnuityMoney.setFxd10Y(info.fixedAnnuityPremium);
                logger.info("확정 연금수령액 : {}원", info.fixedAnnuityPremium);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {
        String title = "환급유형";
        String expectedRefundType = (String) obj[0];
        String actualRefundType = "";

        try {
            // 환급유형 관련 element 찾기
            WebElement $refundTypeDiv = driver.findElement(By.id("refundCls"));

            // 환급유형 버튼 중에서 해약환급금 미지급형밖에 없다면? -> 순수보장형과 매치시켜야 함. 2023-10-04 우정 추가
            List<String> typeList = $refundTypeDiv.findElements(By.tagName("button")).stream()
                .map(WebElement::getText).collect(Collectors.toList());
            if (typeList.size() == 1
                && typeList.get(0).equals("해지환급금 미지급형")
                && expectedRefundType.equals("순수보장형")
            ) {
                expectedRefundType = "해지환급금 미지급형";
            }

            WebElement $refundTypeButton = $refundTypeDiv.findElement(By.xpath("./button[normalize-space()='" + expectedRefundType + "']"));

            // 환급유형을 클릭하려고 하는데 해당 영역 위에 몇초간 말풍선 뜨면서 element를 가리는 문제가 있음.
            // 해당 말풍선 element 삭제 처리한 후에 클릭을 진행한다.
            String script = "$('div.info-balloon').remove();";
            helper.executeJavascript(script);
            click($refundTypeButton);

            // 실제 클릭된 환급유형 값 읽어오기
            $refundTypeButton = $refundTypeDiv.findElement(By.xpath("./button[@class[contains(., 'active')]]"));
            actualRefundType = $refundTypeButton.getText();

            // 비교
            super.printLogAndCompare(title, expectedRefundType, actualRefundType);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_REFUND_TYPE;
            throw new SetRefundTypeException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {
            String script = "return $('*[id*=insured-term]:visible')[0]";
            WebElement $insTermAreaDd = (WebElement) helper.executeJavascript(script);
            WebElement $insTermLabel = $insTermAreaDd.findElement(By.xpath(".//label[contains(.,'" + expectedInsTerm + "')]"));
            // 보험기간 라벨 텍스트 예:  "30세 만기(계약 전환시 최대 100세 보장)" -> 시작 텍스트가 보험기간 선택의 기준이 된다.
            click($insTermLabel);

            // 실제 선택된 보험기간 값 읽어오기(원수사에서는 실제 선택된 보험기간 element 클래스 속성에 active를 준다)
            $insTermLabel = $insTermAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            String activeLabelText = $insTermLabel.getText().trim();
            actualInsTerm = activeLabelText.contains("만기") ?
                activeLabelText.substring(0, activeLabelText.indexOf("만기")).trim(): activeLabelText;

            // 비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            String script = "return $('*[id*=payment-term]:visible')[0]";
            WebElement $napTermAreaDd = (WebElement) helper.executeJavascript(script);
            WebElement $napTermLabel = $napTermAreaDd.findElement(By.xpath(".//label[normalize-space()='" + expectedNapTerm + "']"));
            click($napTermLabel);

            // 실제 선택된 납입기간 값 읽어오기(원수사에서는 실제 선택된 납입기간 element 클래스 속성에 active를 준다)
            $napTermLabel = $napTermAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualNapTerm = $napTermLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        String title = "상품유형(=심사유형)";
        String expectedProductType = (String) obj[0];
        String actualProductType = "";
        String script = "";

        try {
            script = "return $('*[id*=product-cls]:visible')[0]";
            WebElement $productTypeAreaDd = (WebElement) helper.executeJavascript(script);
            WebElement $productTypeLabel = $productTypeAreaDd.findElement(By.xpath(".//label[normalize-space()='" + expectedProductType + "']"));
            click($productTypeLabel);

            // 실제 선택된 상품유형 값 읽어오기(원수사에서는 실제 선택된 상품유형 element 클래스 속성에 active를 준다)
            $productTypeLabel = $productTypeAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualProductType = $productTypeLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedProductType, actualProductType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREVALENCE_TYPE;
            throw new SetProductTypeException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {
        String title = "상품유형(=심사유형)";
        String expectedPrevalenceType = (String) obj[0];
        String actualPrevalenceType = "";
        String[] textTypes = expectedPrevalenceType.split("\\|");
        String script = "";

        try {
            script = "return $('*[id*=product-cls]:visible')[0]";
            WebElement $prevalenceTypeAreaDd = (WebElement) helper.executeJavascript(script);
            WebElement $prevalenceTypeLabel = null;

            for (String textType : textTypes) {
                try {
                    textType = textType.trim();

                    $prevalenceTypeLabel = $prevalenceTypeAreaDd.findElement(By.xpath(".//label[normalize-space()='" + textType + "']"));
                    click($prevalenceTypeLabel);
                    expectedPrevalenceType = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            // 실제 선택된 상품유형 값 읽어오기(원수사에서는 실제 선택된 상품유형 element 클래스 속성에 active를 준다)
            $prevalenceTypeLabel = $prevalenceTypeAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualPrevalenceType = $prevalenceTypeLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedPrevalenceType, actualPrevalenceType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREVALENCE_TYPE;
            throw new SetPrevalenceTypeException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {
        String title = "상품유형(=갱신유형)";
        String expectedRenewType = (String) obj[0];
        String actualRenewType = "";
        String script = "";

        try {
            script = "return $('*[id*=product-cls]:visible')[0]";

            // 갱신 유형 존재여부 체크
            Optional<WebElement> renewTypeAreaDdOpt =
                Optional.ofNullable((WebElement) helper.executeJavascript(script));
            if (!renewTypeAreaDdOpt.isPresent()) {
                logger.info("상품유형(=갱신유형) 요소를 찾을 수 없어 원수사에서 선택항목을 제공하지 않는 것으로 간주하고 넘어갑니다.");
                return;
            }

            WebElement $renewTypeAreaDd = renewTypeAreaDdOpt.get();
            WebElement $renewTypeLabel = $renewTypeAreaDd.findElement(By.xpath(".//label[normalize-space()='" + expectedRenewType + "']"));
            click($renewTypeLabel);

            // 실제 선택된 상품유형 값 읽어오기(원수사에서는 실제 선택된 상품유형 element 클래스 속성에 active를 준다)
            $renewTypeLabel = $renewTypeAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualRenewType = $renewTypeLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedRenewType, actualRenewType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RENEW_TYPE;
            throw new SetRenewTypeException(e, exceptionEnum.getMsg());
        }
    }



    public void setGuranteeType(String expectedGuranteeType) throws CommonCrawlerException {
        String title = "보장유형(일반형/체증형 등)";
        String actualGuranteeType = "";

        try {
            // 보장유형 관련 element 찾기
            String script = "return $('*[id*=insured-cls]:visible')[0]";
            WebElement $guranteeTypeAreaDd = (WebElement) helper.executeJavascript(script);
            WebElement $guranteeTypeLabel = $guranteeTypeAreaDd.findElement(By.xpath(".//label[normalize-space()='" + expectedGuranteeType + "']"));
            click($guranteeTypeLabel);

            // 실제 선택된 보장유형 값 읽어오기(원수사에서는 실제 선택된 보장유형 element 클래스 속성에 active를 준다)
            $guranteeTypeLabel = $guranteeTypeAreaDd.findElement(By.xpath(".//label[@class[contains(., 'active')]]"));
            actualGuranteeType = $guranteeTypeLabel.getText().trim();

            // 비교
            super.printLogAndCompare(title, expectedGuranteeType, actualGuranteeType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_GURANTEE_TYPE;
            throw new CommonCrawlerException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {
        String title = "출생예정일";
        String expectedDueDate = (String) obj[0];
        String actualDueDate = "";
        WebElement $button = null;

        // 예상 출생예정일 파싱 작업
        int expectedYear = Integer.parseInt(expectedDueDate.substring(0, 4));
        int expectedMonth = Integer.parseInt(expectedDueDate.substring(4, 6));
        int expectedDate = Integer.parseInt(expectedDueDate.substring(6));

        int calendarCurrentMonth = 0;

        try {
            // 출생예정일 선택을 위해 캘린더 버튼 클릭
            WebElement $calendarDiv = driver.findElement(By.id("childBirth-input"));
            click($calendarDiv);

            // 현재 캘린더에 default로 세팅된 년, 월 읽어오기
            $calendarDiv = driver.findElement(By.xpath("//div[@class='component-cal-container']"));
            WebElement $calendarCurrentYear = $calendarDiv.findElement(By.xpath(".//span[@class='cal-txt-year']"));
            WebElement $calendarCurrentMonth = $calendarDiv.findElement(By.xpath(".//span[@class='cal-txt-month']"));
            calendarCurrentMonth = Integer.parseInt($calendarCurrentMonth.getText().replaceAll("[^0-9]", ""));

            // 캘린더 월 조정(보통은 이전달로 돌아갈 일은 없으므로 이전달에 대한 처리는 하지 않는다)
            int clickCnt = 0;
            expectedMonth = (expectedMonth < calendarCurrentMonth) ? expectedMonth + 12 : expectedMonth;
            clickCnt = expectedMonth - calendarCurrentMonth;

            // 달의 차이만큼 다음달 이동 버튼을 클릭한다.
            for (int i = 0; i < clickCnt; i++) {
                logger.info("다음달 이동 버튼 클릭");
                $button = $calendarDiv.findElement(By.xpath(".//button[normalize-space()='다음달 이동']"));
                click($button);
            }

            // 캘린더 일 조정
            WebElement $calendarTable = $calendarDiv.findElement(By.xpath(".//table[@class='component-cal-calendar']"));
            WebElement $calendarTbody = $calendarTable.findElement(By.tagName("tbody"));

            // 출생예정일 선택을 위해 불필요한 element 제거
            String script = "$(arguments[0]).find('span.sr-only.blind').remove();";
            helper.executeJavascript(script, $calendarTbody);

            WebElement $calendarDate = $calendarTbody.findElement(By.xpath(".//td[not(@class)]//button[normalize-space()='" + expectedDate + "']"));
            click($calendarDate);

            // 실제 입력된 출생예정일 읽어오기
            $calendarDiv = driver.findElement(By.id("childBirth-input"));
            WebElement $calendarSpan = $calendarDiv.findElement(By.xpath("./span[@class='label-date']"));
            actualDueDate = $calendarSpan.getText().replaceAll("[\\.]", "");

            // 비교
            super.printLogAndCompare(title, expectedDueDate, actualDueDate);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_DUEDATE;
            throw new SetDueDateException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

    }



    protected void selectLiByTextFromUl(WebElement $ul, String text) {
        List<WebElement> $liList = $ul.findElements(By.tagName("li"));

        try {
            // text 선택과 비교를 위해 불필요한 element 삭제처리
            String script = "$(arguments[0]).find('span.sr-only').remove();";
            helper.executeJavascript(script, $ul);

            for (WebElement $li : $liList) {
                String targetText = $li.getText().trim();

                if (targetText.equals(text)) {
                    click($li);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // 로딩바 명시적 대기
    public void waitLoadingBar() {
        /**
         * TODO 로딩바 css selector 보기
         *
         * SFI_CCR_D005의 경우 #loading-message-box
         * SFI_AMD_D003의 경우 .ui-loading
         *
         * 통일 시킬 수 있는지 확인
         */
        try {
            helper.waitForCSSElement(".ui-loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * element에 &nbsp가 껴잇는 경우가 있다.
     * xpath에서 &nbsp에 대한 처리가 불가능함. 따라서 일단 jquery로 &npsp를 모두 제거시킨 다음에 element를 찾게한다.
     */
    protected void removeWhiteSpaceFromElement(WebElement $element) {
        String script = "$(arguments[0]).find('td:nth-child(1)').html(function (i, html) {"
            + "    return html.replace(/&nbsp;/g, '');"
            + "});";

        try {
            helper.executeJavascript(script, $element);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}