package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
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
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;



public abstract class CrawlingDBFAnnounce extends CrawlingDBFNew {



    /**
     * DBF 공시실 생년월일 셋팅
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
     * DBF 공시실 성별 셋팅
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
            WebElement $genderLabel = driver.findElement(By.xpath("//label[text()='" + expectedGenderText + "']"));
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
            // 1. 직업 찾아보기 클릭
            logger.info("직업 찾아보기 클릭");
            helper.waitElementToBeClickable(driver.findElement(By.linkText("찾아보기"))).click();
            WaitUtil.waitFor(1);

            // 2. 세팅 할 직업 입력
            logger.info("{} 입력", expectedJobText);
            driver.findElement(By.cssSelector("#srchJobName")).sendKeys(expectedJobText);
            WaitUtil.waitFor(1);

            // 3. 찾기
            logger.info("찾기 클릭");
            helper.waitElementToBeClickable(driver.findElement(By.linkText("찾기"))).click();
            WaitUtil.waitFor(1);

            // 4. 찾은 리스트 중 직업 선택
            logger.info("{} 선택", expectedJobText);
            List<WebElement> $jobList = driver.findElements(By.cssSelector("#divJobSelect > td > div > ul > li"));
            for (WebElement $job : $jobList) {
                WebElement $jobLabel = $job.findElement(By.cssSelector("label"));
                if ($jobLabel.getText().trim().contains(expectedJobText)) {
                    $jobLabel.click();
                    break;
                }
            }

            // 5. 확인 버튼 클릭
            logger.info("확인 버튼 클릭");
            helper.waitElementToBeClickable(driver.findElement(By.linkText("확인"))).click();
            WaitUtil.waitFor(1);

            try {
                String actualJobText = driver.findElement(By.id("job_nm")).getAttribute("value");
                super.printLogAndCompare(title, expectedJobText, actualJobText);
            } catch (Exception e) {
                String actualJobText = driver.findElement(By.id("JOB_NAME")).getAttribute("value");
                super.printLogAndCompare(title, expectedJobText, actualJobText);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 공시실 가입 유형 선택
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
            WebElement $productTypeLabel = driver.findElement(By.xpath("//label[contains(.,'" + expectedProductTypeText + "')]"));
            helper.waitElementToBeClickable($productTypeLabel).click();

            String actualProductTypeId = String.valueOf(helper.executeJavascript(script, expectedProductTypeText));
            String actualProductTypeText = driver.findElement(By.xpath("//label[@for='" + actualProductTypeId + "']")).getText();
            if (actualProductTypeText.contains("\n")){
                actualProductTypeText = actualProductTypeText.substring(0, actualProductTypeText.indexOf("\n"));
            }

            super.printLogAndCompare(title, expectedProductTypeText, actualProductTypeText);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 공시실 운전형태 선택
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
            WebElement $productTypeLabel = driver.findElement(By.xpath("//label[text()='" + expectedVehicleText + "']"));
            helper.waitElementToBeClickable($productTypeLabel).click();

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
     * DBF 공시실 보험기간 셋팅
     * @param obj
     * obj[0] : 보험 기간 element(By) (필수)
     * obj[1] : 보험 기간 값 (필수)
     * @throws SetInsTermException
     */
    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험 기간";

        By $insTermBy = (By) obj[0];
        String expectedInsTerm = (String) obj[1];
        String actualInsTerm = "";

        try {
            actualInsTerm = helper.sendKeys4_check($insTermBy, expectedInsTerm);

            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

            WaitUtil.loading(1);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 공시실 보험기간 셋팅(보험기간, 납입기간 한번에 셋팅)
     * @param obj
     * obj[0] : 보험 기간 선택 select element (필수)
     * obj[1] : 보험 기간 값 (필수)
     * @throws Exception
     */
    public void setTerm(Object...obj) throws Exception {
        String title = "보험 기간";

        WebElement $termSelect = (WebElement) obj[0];
        String expectedTermText = (String) obj[1];
        String actualTermText = "";

        try {
            actualTermText = helper.selectByText_check($termSelect, expectedTermText);

            super.printLogAndCompare(title, expectedTermText, actualTermText);

            WaitUtil.waitFor(1);
        } catch (Exception e) {
            throw new Exception("보험기간 셋팅 오류입니다");
        }
    }



    /**
     * DBF 공시실 납입주기 셋팅
     * @param obj
     * obj[0] : 납입 주기 선택 select element(WebElement) (필수)
     * obj[1] : 납입 주기 값 (필수)
     * @throws SetNapCycleException
     */
    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";

        WebElement $cycleSelect = (WebElement) obj[0];
        String expectedCycleText = (String) obj[1];
        String actualCycleText = "";

        try {
            actualCycleText = helper.selectByText_check($cycleSelect, expectedCycleText);

            super.printLogAndCompare(title, expectedCycleText, actualCycleText);

            WaitUtil.waitFor(1);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 공시실 적립환급금지급시기 셋팅
     * @param obj
     * obj[0] : 지급시기 select element (필수)
     * obj[1] : 지급시기 셋팅 값 (필수)
     * @throws Exception
     */
    public void setRefundPeriod(Object...obj) throws Exception {
        String title = "적립환급금지급시기";

        WebElement $refundPeriodSelect = (WebElement) obj[0];
        String expectedRefundPeriodText = (String) obj[1];
        String actualRefundPeriodText = "";

        try {
            actualRefundPeriodText = helper.selectByText_check($refundPeriodSelect, expectedRefundPeriodText);

            super.printLogAndCompare(title, expectedRefundPeriodText, actualRefundPeriodText);

            WaitUtil.waitFor(1);
        } catch (Exception e) {
            throw new Exception("적립환급금지급시기 셋팅 오류입니다");
        }
    }



    /**
     * DBF 공시실 임신개월수 셋팅
     * @param obj
     * obj[0] : 임신개월수 select element (필수)
     * obj[1] : 임신개월수 셋팅 값 (필수)
     * @throws Exception
     */
    public void setPregnantMonth(Object...obj) throws Exception {
        String title = "임신개월수";

        WebElement $monthSelect = (WebElement) obj[0];
        String expectedMonthText = (String) obj[1];
        String actualMonthText = "";

        try {
            actualMonthText = helper.selectByText_check($monthSelect, expectedMonthText);

            super.printLogAndCompare(title, expectedMonthText, actualMonthText);

            WaitUtil.waitFor(1);
        } catch (Exception e) {
            throw new Exception("임신개월수 셋팅 오류입니다");
        }
    }



    /**
     * DBF 공시실 Radio Label 공용 선택 메서드
     * @param obj
     * obj[0] : Radio 태그 네임 (필수)
     * obj[1] : 선택할 값 (필수)
     * @throws Exception
     */
    public void setRadioLabel(Object... obj) throws Exception {
        String title = "라디오 타입 라벨";
        String tagName = (String) obj[0];
        String expectedLabelText = (String) obj[1];

        String script = "return $('input[name=" + tagName + "]:checked').attr('id');";

        try {
            WebElement $label = driver.findElement(By.xpath("//label[text()='" + expectedLabelText + "']"));
            helper.waitElementToBeClickable($label).click();

            String actualLabelId = String.valueOf(helper.executeJavascript(script, expectedLabelText));
            String actualLabelText = driver.findElement(By.xpath("//label[@for='" + actualLabelId + "']")).getText();

            super.printLogAndCompare(title, expectedLabelText, actualLabelText);
            WaitUtil.loading(1);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * DBF 공시실 특약 셋팅
     * @param obj
     * obj[0] : 비교할 특약이 있는 홈페이지 trList(List<WebElement>) (필수)
     * obj[1] : 해당 가입설계 특약리스트(List<CrawlingTreaty>) (필수)
     * @throws SetTreatyException
     */
    public void setTreaties(Object...obj) throws SetTreatyException {
        List<WebElement> $trList = (List<WebElement>) obj[0];
        List<CrawlingTreaty> treaties = (List<CrawlingTreaty>) obj[1];

        try {
            //원수사 특약명이 존재하는 tr만 조회
            List<CrawlingTreaty> targetTreaties = new ArrayList<>();

            for (WebElement $tr : $trList) {
                WebElement $treatyNameTd = $tr.findElement(By.cssSelector("td:nth-child(3)"));
                String targetTreatyName = $treatyNameTd.getText().trim();

                for (CrawlingTreaty treaty : treaties) {
                    String treatyName = treaty.treatyName;
                    String treatyAssureMoney = String.valueOf(treaty.assureMoney);

                    if (targetTreatyName.equals(treatyName)) {
                        String actualAssureMoney = "";

                        WebElement $treatyCheckBox = $tr.findElement(By.cssSelector("td:nth-child(1) > div"));
                        WebElement $treatyCheckText = $tr.findElement(By.cssSelector("td:nth-child(2)"));

                        logger.info("특약명 : {}", targetTreatyName);

                        if ($treatyCheckText.getText().trim().contains("필수")) {
                            logger.info("선택항목이 필수(주계약)인 경우 자동 체크되어 있어서 넘김.");
                        } else {
                            logger.info("가입버튼 클릭");
                            $treatyCheckBox.click();
                        }

                        logger.info("상품마스터에 있는 특약의 금액 입력 : {}", treaty.assureMoney);

                        try {
                            WebElement $treatyAssureMoney = $tr.findElement(By.cssSelector("td:nth-child(5) > div > input"));
                            actualAssureMoney = helper.sendKeys4_check($treatyAssureMoney, treatyAssureMoney);
                        } catch (Exception e) {
                            // 기본 5번째 td의 input 에 금액을 셋팅하지만 중간에 추가되어 위치가 바뀌는 경우가 있다
                            WebElement $treatyAssureMoney = $tr.findElement(By.cssSelector("td:nth-child(6) > div > input"));
                            actualAssureMoney = helper.sendKeys4_check($treatyAssureMoney, treatyAssureMoney);

                        }
                        logger.info("==================================================");

                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.setTreatyName(treatyName);
                        targetTreaty.setAssureMoney(Integer.parseInt(actualAssureMoney));

                        targetTreaties.add(targetTreaty);

                        break;
                    }
                }
            }

            boolean result = compareTreaties(targetTreaties, treaties);

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
     * DBF 공시실 ACD,OST,DMT 등 특이케이스 특약셋팅(테이블 구조가 다름)
     * @param obj
     * obj[0] : 비교할 특약이 있는 홈페이지 trList(List<WebElement>) (필수)
     * obj[1] : 해당 가입설계 특약리스트(List<CrawlingTreaty>) (필수)
     * @throws SetTreatyException
     */
    public void setTreatiesACD(Object...obj) throws SetTreatyException {
        List<WebElement> $trList = (List<WebElement>) obj[0];
        List<CrawlingTreaty> treaties = (List<CrawlingTreaty>) obj[1];

        try {
            List<CrawlingTreaty> targetTreaties = new ArrayList<>();

            for (WebElement element : $trList) {
                String targetTreatyName = element.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();

                for (CrawlingTreaty treaty : treaties) {
                    String treatyName = treaty.treatyName.trim();
                    String treatyAssureMoney = String.valueOf(treaty.assureMoney);

                    if (targetTreatyName.equals(treatyName) ) {
                        String actualAssureMoney = "";

                        logger.info("특약명 확인 : " + treatyName);
                        if (treaty.productGubun.equals(CrawlingTreaty.ProductGubun.주계약)) {
                            logger.info("선택항목이 필수(주계약)인 경우 자동 체크되어 있어서 넘김.");
                        } else {
                            logger.info("가입버튼 클릭");
                            element.findElement(By.cssSelector("td:nth-child(1) > div")).click();
                            WaitUtil.waitFor(1);
                        }
                        WebElement $treatyNameTd = element.findElement(By.xpath("./td[3]/ul/li"));
                        try {
                            WebElement $child = $treatyNameTd.findElement(By.xpath("./*[contains(@name, 'SCR_GAIP_AMT')]"));
                            logger.info("입력창 TagName : " + $child.getTagName());

                            if ("input".equals($child.getTagName())) {
                                actualAssureMoney = helper.sendKeys4_check($child, treatyAssureMoney);
                            } else if("select".equals($child.getTagName())) {
                                actualAssureMoney = helper.selectByValue_check($child, treatyAssureMoney);
                            }
                        } catch (Exception e) {
                            logger.info("가입 금액 입력란 없음. 가입 금액 셋팅 패스");
                            actualAssureMoney = treatyAssureMoney;
                        }

                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.setTreatyName(treatyName);
                        targetTreaty.setAssureMoney(Integer.parseInt(actualAssureMoney));

                        targetTreaties.add(targetTreaty);
                        break;
                    }
                }
            }
            boolean result = compareTreaties(targetTreaties, treaties);

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
     * DBF 공시실 희망보험료 입력을 통한 납입금액 찾기
     * @param obj
     * obj[0] : 희망보험료 input element (필수)
     * @throws Exception
     */
    public void setHopeAssureMoney(Object...obj) throws Exception {

        WebElement $hopeAssureMoneyInput = (WebElement) obj[0];
        String hopeAssureMoney = "";

        logger.info("희망보험료를 알기위해서는 가장 낮은 금액을 넣어서 경고창에서 확인해야함. (10원입력)");
        $hopeAssureMoneyInput.sendKeys("10");
        WaitUtil.waitFor(1);

        try {
            driver.findElement(By.linkText("보험료 산출")).click();
            waitAnnounceLoadingImg();
            try {
                while (true) {
                    WebElement $alertDialog = driver.findElement(By.cssSelector(".alert_dialog"));
                    hopeAssureMoney = $alertDialog.findElement(By.cssSelector(".message")).getText().trim();
                    int idx = hopeAssureMoney.indexOf("(");
                    String hopeAssureMoney2 = hopeAssureMoney.substring(idx+1);

                    String date[] = hopeAssureMoney2.split("\\)");

                    // 판매 종료 alert
                    if(hopeAssureMoney.contains("종료되었습니다")){
                        throw new CommonCrawlerException("해당 플랜은 판매 종료되었습니다.");
                    }

                    hopeAssureMoney = date[0].trim().replaceAll("[^0-9]", "");

                    logger.info("희망보험료 확인 : " + hopeAssureMoney + "원");

                    logger.info("확인버튼을 눌러 알럿창 닫기");
                    $alertDialog.findElement(By.cssSelector("div.btn_set > ul > li > a")).click();
                    WaitUtil.waitFor(1);

                    $hopeAssureMoneyInput.clear();
                    WaitUtil.waitFor(1);
                    $hopeAssureMoneyInput.sendKeys(hopeAssureMoney);
                    WaitUtil.waitFor(1);

                    logger.info("보험료 산출버튼 다시 클릭");
                    driver.findElement(By.linkText("보험료 산출")).click();
                    waitAnnounceLoadingImg();
                }
            } catch (NoSuchElementException e) {
                logger.info("alert 창 없음 -> 보험료 계산 정상 완료");
            }
        } catch (Exception e) {
            logger.info("기존 커스텀 Alert 창이 아닌 system Alert 창으로 여기서 처리");
            String alertMessage = e.getMessage();
            logger.info("alert message : {}", alertMessage);

            // 판매 종료 alert
            if(alertMessage.contains("종료")){
                throw new CommonCrawlerException(e.getMessage());
            }

            String[] alertTextList = alertMessage.split(" ");
            for (String text : alertTextList) {
                if (text.contains("원)")) {
                    hopeAssureMoney = text.replaceAll("[^0-9]", "");
                    logger.info("합계보혐료: {}" , hopeAssureMoney);
                    break;
                }
            }

            $hopeAssureMoneyInput.clear();
            WaitUtil.waitFor(1);
            $hopeAssureMoneyInput.sendKeys(hopeAssureMoney);
            WaitUtil.waitFor(1);

            logger.info("보험료 산출버튼 다시 클릭");
            driver.findElement(By.linkText("보험료 산출")).click();
            waitAnnounceLoadingImg();
        }
    }



    /**
     * DBF 공시실 월 납입보험료 셋팅
     * @param obj
     * obj[0] : 산출된 합계보험료 element(WebElement)
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
     * 태아보험용 보험료 크롤링(태아보험은 출생 전, 출생 후 보험료를 각각 크롤링)
     * @param obj
     * obj[0] : 산출된 출생 전 보험료 element
     * obj[1] : 산출된 출생 후 보험료 element
     * obj[2] : CrawlingProduct info
     * @throws PremiumCrawlerException
     */
    public void babyCrawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            WebElement $beforeBirthTd = (WebElement) obj[0];
            WebElement $afterBirthTd = (WebElement) obj[1];
            CrawlingProduct info = (CrawlingProduct) obj[2];

            String beforePremium = $beforeBirthTd.getText().replaceAll("[^0-9]", "");
            String afterPremium = $afterBirthTd.getText().replaceAll("[^0-9]", "");

            info.treatyList.get(0).monthlyPremium = beforePremium;
            logger.info("출산 전 보험료 확인 : " + beforePremium);

            info.nextMoney = afterPremium;
            logger.info("출산 후 보험료 확인 : " + afterPremium);

            if ("0".equals(info.treatyList.get(0).monthlyPremium)) {
                throw new Exception("주계약 보험료는 0원일 수 없습니다");
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    /**
     * 예상 총 환급금과 해약 환급금 표의 마지막 행의 값이 다른 경우 발생 >> 예상 총 환급금을 사용한다.
     * @param obj
     * @throws ReturnPremiumCrawlerException
     */
    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        try{
            WebElement $returnMoneyTd = (WebElement) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            String returnMoney = $returnMoneyTd.getText().replaceAll("[^0-9]", "");
            info.returnPremium = returnMoney;
            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_PREMIUM;
            throw new ReturnPremiumCrawlerException(exceptionEnum.getMsg());
        }

    }



    /**
     * 공시실 해약환급금 크롤링
     * @param obj
     * obj[0] : CrawlingProduct info (필수)
     */
    public void getReturnPremium(Object...obj) throws Exception {
        CrawlingProduct info = (CrawlingProduct) obj[0];

        logger.info("해약환급금 버튼 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.linkText("해약 환급금"))).click();
        waitAnnounceLoadingImg();

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        List<WebElement> $refundTrList = driver.findElements(By.cssSelector("#refundList > tr"));

        for (int i=0; i<$refundTrList.size(); i++) {
            WebElement $tr = $refundTrList.get(i);

            String term = $tr.findElement(By.cssSelector("td:nth-child(1)")).getText().trim();
            String premiumSum = $tr.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
            String returnMoneyMin = $tr.findElement(By.cssSelector("td:nth-child(3)")).getText()
                .trim();
            String returnRateMin = $tr.findElement(By.cssSelector("td:nth-child(4)")).getText()
                .trim();
            String returnMoneyAvg = $tr.findElement(By.cssSelector("td:nth-child(5)")).getText()
                .trim();
            String returnRateAvg = $tr.findElement(By.cssSelector("td:nth-child(6)")).getText()
                .trim();
            String returnMoney = $tr.findElement(By.cssSelector("td:nth-child(7)")).getText()
                .trim();
            String returnRate = $tr.findElement(By.cssSelector("td:nth-child(8)")).getText().trim();

            logger.info("------------------------------------");
            logger.info(term + " 경과기간 :: " + term);
            logger.info(term + " 납입보험료 :: " + premiumSum);
            logger.info(term + " 최저해약환급금 :: " + returnMoneyMin);
            logger.info(term + " 최저해약환급률 :: " + returnRateMin);
            logger.info(term + " 평균해약환급금 :: " + returnMoneyAvg);
            logger.info(term + " 평균해약환급률 :: " + returnRateAvg);
            logger.info(term + " 현재해약환급금 :: " + returnMoney);
            logger.info(term + " 현재해약환급률 :: " + returnRate);
            logger.info("------------------------------------");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            // 위치에 맞게 스크롤 내리기
            if ($refundTrList.size() != (i + 1)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
                    $tr.findElement(By.cssSelector("td:nth-child(1)")));
            }

            /*
            todo
            만기환급금 정보 크롤링 메서드 따로 추출하기 ->  crawlReturnPremium
            구현 CASE1 화면상에 만기환급금 이라고 명시적으로 게시하는 경우가 있다 그러면 그걸 크롤링하면 됨
            구현 CASE2 없는 경우엔 중도해약환급금(crawlReturnPremium) 목록에서 경과기간이 만기에 해당하는 해약환급금을 만기환급금으로 간주, 크롤링한다.
                   만기에 해당하는 해약환급금도 없을 경우엔 -1로 처리한다.
             */
            if(returnMoney.equals("")){
                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
                logger.info("만기환급금 : {}원", info.returnPremium);
            }
        }

        info.planReturnMoneyList = planReturnMoneyList;
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {}



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



    // 공시실 로딩이미지 명시적 대기
    protected void waitAnnounceLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading")));
    }

}