package com.welgram.crawler.direct.fire.aig;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class CrawlingAIGMobile extends CrawlingAIGNew {

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setMobile(true);
    }

    public void setTravelType(String expectedTravelType) throws CommonCrawlerException {
        String title = "여행유형";
        String actualTravelType = "";

        try {

            //여행유형 관련 element 찾기
            WebElement $travelTypeUl = driver.findElement(By.xpath("//ul[@class[contains(., 'pageTab__list')]]"));
            WebElement $travelTypeSpan = $travelTypeUl.findElement(By.xpath(".//span[normalize-space()='" + expectedTravelType + "']"));
            WebElement $travelTypeButton = $travelTypeSpan.findElement(By.xpath("./parent::button"));

            //여행유형 클릭
            clickByJavascriptExecutor($travelTypeButton);

            //실제 선택된 여행유형 값 읽어오기
            $travelTypeSpan = $travelTypeUl.findElement(By.xpath("./li[@data-on='active']//span"));
            actualTravelType = $travelTypeSpan.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedTravelType, actualTravelType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_TYPE;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    public void setForeignType(String expectedForeignType) throws CommonCrawlerException {
        String title = "내국인/외국인 설정";
        String actualForeignType = "";

        try {

            //외국인 유형 관련 element 찾기
            WebElement $genderLabel = driver.findElement(By.xpath("//label[normalize-space()='" + expectedForeignType + "']"));

            //외국인 유형 클릭
            clickByJavascriptExecutor($genderLabel);

            //실제 클릭된 외국인 유형 읽어오기
            String script = "return $('input[name=insaRnrsClcd1]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $genderLabel = driver.findElement(By.xpath(".//label[@for='" + id + "']"));
            actualForeignType = $genderLabel.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedForeignType, actualForeignType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_FOREIGN_TYPE;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setTravelDepartureDate(String expectedDepartureDate) throws SetTravelPeriodException {
        String title = "여행 출발날짜";
        String actualDepartureDate = "";

        try {

            //여행 출발날짜 관련 element 찾기
            WebElement $departureDateInput = driver.findElement(By.id("insStDt"));
            helper.moveToElementByJavascriptExecutor($departureDateInput);

            //여행 출발날짜 세팅
            actualDepartureDate = helper.sendKeys4_check($departureDateInput, expectedDepartureDate);

            //비교
            super.printLogAndCompare(title, expectedDepartureDate, actualDepartureDate);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    public void setTravelDepartureTime(String expectedDepartureTime) throws SetTravelPeriodException {
        String title = "여행 출발시간";
        String actualDepartureTime = "";

        try {

            //여행 출발시간 관련 element 찾기
            WebElement $departureTimeSelect = driver.findElement(By.id("startTime"));

            //여행 출발시간 세팅
            actualDepartureTime = helper.selectByText_check($departureTimeSelect, expectedDepartureTime);

            //비교
            super.printLogAndCompare(title, expectedDepartureTime, actualDepartureTime);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTravelArrivalDate(String expectedArrivalDate) throws SetTravelPeriodException {
        String title = "여행 도착날짜";
        String actualArrivalDate = "";

        try {

            //여행 도착날짜 관련 element 찾기
            WebElement $arrivalDateInput = driver.findElement(By.id("insEndt"));
            helper.moveToElementByJavascriptExecutor($arrivalDateInput);

            //여행 도착날짜 세팅
            actualArrivalDate = helper.sendKeys4_check($arrivalDateInput, expectedArrivalDate);

            //비교
            super.printLogAndCompare(title, expectedArrivalDate, actualArrivalDate);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTravelArrivalTime(String expectedArrivalTime) throws SetTravelPeriodException {
        String title = "여행 도착시간";
        String actualArrivalTime = "";

        try {

            //여행 도착시간 관련 element 찾기
            WebElement $arrivalTimeSelect = driver.findElement(By.id("endTime"));

            //여행 도착시간 세팅
            actualArrivalTime = helper.selectByText_check($arrivalTimeSelect, expectedArrivalTime);

            //비교
            super.printLogAndCompare(title, expectedArrivalTime, actualArrivalTime);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {

            //생년월일 관련 element 찾기
            WebElement $birthInput = driver.findElement(By.id("birth"));

            //생년월일 설정
            actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

            //비교
            super.printLogAndCompare(title, expectedBirth, actualBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";
        int gender = (int) obj[0];
        String expectedGender = (gender == MALE) ? "man" : "woman";
        String actualGender = "";
        String script = "";

        try {

            //성별 관련 element 찾기
            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='reAigProductButtonBox']"));
            WebElement $genderInput = $genderDiv.findElement(By.id(expectedGender));

            //성별 클릭
            clickByJavascriptExecutor($genderInput);

            /**
             * 실제 클릭된 성별 읽어오기
             *
             * [주의사항]
             * :checked된 input의 id 값을 읽어 label 태그를 찾으려고 하는데
             * 원수사 html 태그 구조가 조금 이상한듯함. input과 label이 이어지지 않음
             * 따라서 xpath로 계층구조로 타고 들어가 실제 선택된 label 태그를 찾아야 한다.
             */
            script = "return $('input[name=gender]:checked')[0];";
            $genderInput = (WebElement) helper.executeJavascript(script);
            actualGender = $genderInput.getAttribute("id");

            //비교
            super.printLogAndCompare(title, expectedGender, actualGender);

;        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "판매플랜";
        String actualPlan = "";

        try {

            //판매플랜 관련 element 찾기
            WebElement $planFieldSet = driver.findElement(By.id("premium__guaranteeList"));
            WebElement $planSpan = $planFieldSet.findElement(By.xpath(".//span[@class='text']/span[normalize-space()='" + expectedPlan + "']"));
            WebElement $planLabel = $planSpan.findElement(By.xpath("./ancestor::label[1]"));

            //판매플랜 선택
            clickByJavascriptExecutor($planLabel);

            //실제 클릭된 판매플랜 읽어오기
            String script = "return $('input[name=plan]:checked').attr('id');";
            String id = String.valueOf(helper.executeJavascript(script));
            $planLabel = $planFieldSet.findElement(By.xpath(".//label[@for='" + id + "']"));
            $planSpan = $planLabel.findElement(By.xpath(".//span[@class='text']/span"));
            actualPlan = $planSpan.getText();

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "납입기간/보험기간";
        String expectedInsTerm = (String) obj[0];
        String expectedNapTerm = (String) obj[1];
        String expectedTerm = "";
        String actualTerm = "";

        try {
            expectedTerm = expectedNapTerm + "납·" + expectedInsTerm + "만기";

            //납입/보험기간 관련 element 찾기
            WebElement $termUl = driver.findElement(By.id("premium__termList"));
            WebElement $termSpan = $termUl.findElement(By.xpath(".//span[normalize-space()='" + expectedTerm + "']"));
            WebElement $termButton = $termSpan.findElement(By.xpath("./parent::button"));

            //납입/보험기간 선택
            clickByJavascriptExecutor($termButton);

            //실제 클릭된 납입/보험기간 읽어오기
            WebElement $termLi = $termUl.findElement(By.xpath("./li[@data-on='active']"));
            $termSpan = $termLi.findElement(By.xpath(".//span"));
            actualTerm = $termSpan.getText();

            //비교
            super.printLogAndCompare(title, expectedTerm, actualTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    //특약 모달창에서 가입금액 처리
    protected void setAssureMoneyFromTreatyModal(String assureMoney) throws Exception {
        boolean isJoin = !StringUtil.isEmpty(assureMoney);
        String joinText = isJoin ? "가입" : "미가입";
        String script = "arguments[0].click();";


        WebElement $modal = driver.findElement(By.id("details"));
        WebElement $joinArea = $modal.findElement(By.id("covJoinYnArea"));
        WebElement $joinLabel = $joinArea.findElement(By.xpath(".//label[normalize-space()='" + joinText + "']"));

        //가입여부 클릭
        clickByJavascriptExecutor($joinLabel);

        //가입인 경우에는 가입금액까지 클릭
        if(isJoin) {
            WebElement $assureMoneyArea = $modal.findElement(By.id("covAmtArea"));
            WebElement $assureMoneyInput = $assureMoneyArea.findElement(By.xpath(".//input[@value='" + assureMoney + "']"));
            WebElement $assureMoneyLabel = $assureMoneyInput.findElement(By.xpath("./parent::div/label"));

            /**
             * 가입금액 클릭
             * javascript executor로 click 시켜야만 함
             */
            clickByJavascriptExecutor($assureMoneyLabel);
        }

        //모달 창을 닫기 위해 확인 버튼 클릭
        WebElement $button = $modal.findElement(By.id("btnOk"));
        click($button);

    }


    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        try {

            /**
             * ===========================================================================================
             * [STEP 1]
             * 원수사 특약명, 가입설계 특약명 수집 진행하기
             * ===========================================================================================
             */

            //특약 관련 element 찾기
            WebElement $treatyTable = driver.findElement(By.xpath("//div[@class='premium__result']//table"));
            WebElement $treatyTbody = $treatyTable.findElement(By.tagName("tbody"));
            List<WebElement> $treatyTrList = $treatyTbody.findElements(By.tagName("tr"));
            List<String> targetTreatyNameList = new ArrayList<>();
            List<String> welgramTreatyNameList = new ArrayList<>();


            //원수사 특약명 수집
            for(WebElement $treatyTr : $treatyTrList) {
                WebElement $treatyNameTh = $treatyTr.findElement(By.xpath("./th[1]"));
                WebElement $treatyNameSpan = $treatyNameTh.findElement(By.xpath(".//span"));

                helper.moveToElementByJavascriptExecutor($treatyNameTh);
                String targetTreatyName = $treatyNameSpan.getText().trim();

                targetTreatyNameList.add(targetTreatyName);
            }


            //가입설계 특약명 수집
            welgramTreatyNameList = welgramTreatyList.stream().map(CrawlingTreaty::getTreatyName).collect(Collectors.toList());


            //원수사 특약명 vs 가입설계 특약명 비교 처리(유지, 삭제, 추가돼야할 특약명 분간하는 작업)
            List<String> copiedTargetTreatyNameList = new ArrayList<>(targetTreatyNameList);       //원본 리스트가 훼손되므로 복사본 떠두기
            List<String> copiedWelgramTreatyNameList = new ArrayList<>(welgramTreatyNameList);     //원본 리스트가 훼손되므로 복사본 떠두기
            List<String> matchedTreatyNameList = new ArrayList<>();                                //원수사와 가입설계 특약명 비교시 일치하는 특약명 리스트
            List<String> dismatchedTreatyNameList = new ArrayList<>();                             //원수사에서 미가입 처리해야하는 특약명 리스트


            //(원수사와 가입설계 특약 비교해서)공통된 특약명 찾기
            targetTreatyNameList.retainAll(welgramTreatyNameList);                      //원본 리스트 훼손됨
            matchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
            targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구


            //(원수사와 가입설계 특약 비교해서)불일치 특약명 찾기(원수사에서 미가입처리 해줄 특약명들)
            targetTreatyNameList.removeAll(matchedTreatyNameList);                      //원본 리스트 훼손됨
            dismatchedTreatyNameList = new ArrayList<>(targetTreatyNameList);
            targetTreatyNameList = new ArrayList<>(copiedTargetTreatyNameList);         //훼손된 리스트 원상복구




            /**
             * ===========================================================================================
             * [STEP 2]
             * 특약 가입/미가입 처리 진행하기
             * ===========================================================================================
             */

            //불일치 특약들에 대해서 원수사에서 미가입 처리 진행
            for(String treatyName : dismatchedTreatyNameList) {
                logger.info("미가입 특약 확인중... : {}", treatyName);

                WebElement $treatyNameSpan = $treatyTbody.findElement(By.xpath(".//span[normalize-space()='" + treatyName + "']"));
                WebElement $treatyNameTh = $treatyNameSpan.findElement(By.xpath("./ancestor::th[1]"));
                WebElement $treatyNameButton = $treatyNameTh.findElement(By.tagName("button"));
                WebElement $treatyTr = $treatyNameTh.findElement(By.xpath("./parent::tr"));
                WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[1]"));
                WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.tagName("span"));

                String treatyAssureMoney = $treatyAssureMoneySpan.getText();

                //"가입"인 경우에만 "미가입" 처리를 진행한다
                boolean isJoin = !"-".equals(treatyAssureMoney) && !"미가입".equals(treatyAssureMoney);
                if(isJoin) {
                    logger.info("특약명 : {} 미가입 처리를 진행합니다.", treatyName);

                    //특약 클릭
                    click($treatyNameButton);

                    //특약 모달창에서 가입여부 처리
                    setAssureMoneyFromTreatyModal(null);
                }
            }


            //공통된 특약들에 대해 원수사에서 가입 처리 진행
            for(String treatyName : matchedTreatyNameList) {
                logger.info("가입 특약 확인중... : {}", treatyName);

                WebElement $treatyNameSpan = $treatyTbody.findElement(By.xpath(".//span[normalize-space()='" + treatyName + "']"));
                WebElement $treatyNameTh = $treatyNameSpan.findElement(By.xpath("./ancestor::th[1]"));
                WebElement $treatyNameButton = $treatyNameTh.findElement(By.tagName("button"));
                WebElement $treatyTr = $treatyNameTh.findElement(By.xpath("./parent::tr"));
                WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[1]"));
                WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.tagName("span"));

                String treatyAssureMoney = $treatyAssureMoneySpan.getText();

                //"미가입"인 경우에만 "가입" 처리를 진행한다
                boolean isJoin = !"-".equals(treatyAssureMoney) && !"미가입".equals(treatyAssureMoney);
                if(!isJoin) {
                    logger.info("특약명 : {} 가입 처리를 진행합니다.", treatyName);

                    CrawlingTreaty welgramTreaty = welgramTreatyList.stream()
                        .filter(t -> t.getTreatyName().equals(treatyName))
                        .findFirst()
                        .orElseThrow(SetTreatyException::new);

                    //특약 클릭
                    click($treatyNameButton);

                    //특약 모달창에서 가입여부 처리
                    setAssureMoneyFromTreatyModal(String.valueOf(welgramTreaty.getAssureMoney()));
                }
            }


            /**
             * ===========================================================================================
             * [STEP 3]
             * 실제 가입처리된 원수사 특약 정보를 수집한다(유효성 검사를 하기 위함)
             * ===========================================================================================
             */
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            $treatyTrList = $treatyTbody.findElements(By.tagName("tr"));
            for(WebElement $treatyTr : $treatyTrList) {
                WebElement $treatyNameTh = $treatyTr.findElement(By.xpath("./th[1]"));
                WebElement $treatyNameSpan = $treatyNameTh.findElement(By.xpath(".//span"));
                WebElement $treatyAssureMoneyTd = $treatyTr.findElement(By.xpath("./td[1]"));
                WebElement $treatyAssureMoneySpan = $treatyAssureMoneyTd.findElement(By.tagName("span"));
                String treatyName = $treatyNameSpan.getText();
                String treatyAssureMoney = $treatyAssureMoneySpan.getText();

                boolean isJoin = !"-".equals(treatyAssureMoney) && !"미가입".equals(treatyAssureMoney);

                //가입처리된 특약 정보만 적재
                if(isJoin) {
                    treatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(treatyAssureMoney));

                    CrawlingTreaty t = new CrawlingTreaty();
                    t.setTreatyName(treatyName);
                    t.setAssureMoney(Integer.parseInt(treatyAssureMoney));
                    targetTreatyList.add(t);
                }
            }


            /**
             * ===========================================================================================
             * [STEP 4]
             * 원수사 특약 정보 vs 가입설계 특약 정보 비교
             * ===========================================================================================
             */
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());
            if(result) {
                logger.info("특약 정보 모두 일치");
            } else {
                logger.info("특약 정보 불일치");
                throw new Exception();
            }


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {

            //보험료 크롤링 전에는 대기시간을 넉넉히 준다.
            WaitUtil.waitFor(5);


            WebElement $premiumSpan = driver.findElement(By.id("totMoney"));
            String premium = $premiumSpan.getText();
            premium = premium.replaceAll("[^0-9]", "");

            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }


        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    @Override
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("#loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void clickByJavascriptExecutor(WebElement element) {
        String script = "arguments[0].click();";

        try {
            helper.executeJavascript(script, element);
            waitLoadingBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
