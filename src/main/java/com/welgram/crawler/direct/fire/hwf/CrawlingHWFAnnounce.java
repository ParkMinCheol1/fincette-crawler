package com.welgram.crawler.direct.fire.hwf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.common.except.NapTermMismatchException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.*;
import com.welgram.common.except.crawler.setUserInfo.*;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public abstract class CrawlingHWFAnnounce extends CrawlingHWFNew {

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

        WebElement $genderSelect = (WebElement) obj[0];
        int gender = (int) obj[1];
        String expectedGenderText = (gender == MALE) ? "남성" : "여성";
        String actualGenderText = "";

        try {
            //성별 설정
            actualGenderText = helper.selectByText_check($genderSelect, expectedGenderText);

            WaitUtil.waitFor(1);

            //성별 비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    @Override
    public void setJob(Object... obj) throws SetJobException {
        String title = "직업";
        String currentHandle = driver.getWindowHandle();

        WebElement $a = (WebElement) obj[0];

        try {
            logger.info("직업검색 버튼 클릭");
            helper.waitElementToBeClickable($a).click();

            logger.info("기존창 -> 직업창으로 전환");
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            waitAnnounceLoadingImg();
            currentHandle = driver.getWindowHandle();

            logger.info("분류대로 찾기 버튼 클릭");
            $a = driver.findElement(By.linkText("분류대로 찾기"));
            helper.waitElementToBeClickable($a).click();
            waitAnnounceLoadingImg();

            String[] titles = {"대분류", "중분류", "소분류"};
            String[] ids = {"uiListCate1", "uiListCate2", "uiListCate3"};
            String[] jobs = {"전문가 및 관련 종사자", "생명과학 연구원", "생명과학 연구원"};

            //직업 선택
            for (int i = 0; i < titles.length; i++) {
                logger.info("{} 설정", titles[i]);
                String selectedJob = "";

                //element 찾기
                WebElement $ul = driver.findElement(By.id(ids[i]));
                $a = $ul.findElement(By.xpath(".//span[text()='" + jobs[i] + "']/parent::a"));

                //클릭
                helper.waitElementToBeClickable($a).click();
                waitAnnounceLoadingImg();

                //실제 클릭된 값 읽어오기
                $a = $ul.findElement(By.xpath(".//a[@class='selected']"));
                selectedJob = $a.getText();

                //비교
                printLogAndCompare(titles[i], jobs[i], selectedJob);
            }
            WaitUtil.waitFor(1);

            logger.info("확인 버튼 클릭");
            $a = driver.findElement(By.id("btnOk"));
            helper.waitElementToBeClickable($a).click();
            WaitUtil.waitFor(1);

            logger.info("직업창 -> 기존창으로 전환");
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            currentHandle = driver.getWindowHandle();

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {
        String title = "차량";
        WebElement $vehicleSelect = (WebElement) obj[0];
        String expectedJobText = (String) obj[1];
        String actualJobText = "";

        try {
            // 차량 설정
            actualJobText = helper.selectByText_check($vehicleSelect, expectedJobText);

            // 차량 비교
            super.printLogAndCompare(title, expectedJobText, actualJobText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        String title = "가입구분";
        WebElement $productTypeSelect = (WebElement) obj[0];
        String expectedProductTypeText = (String) obj[1];
        String actualProductTypeText = "";

        try {
            // 가입구분 설정
            actualProductTypeText = helper.selectByText_check($productTypeSelect, expectedProductTypeText);

            // 가입구분 비교
            super.printLogAndCompare(title, expectedProductTypeText, actualProductTypeText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";

        WebElement $insTermSelect = (WebElement) obj[0];
        String expectedInsTermText = (String) obj[1];
        String actualInsTermText = "";

        try {
            // 보험기간 설정
            actualInsTermText = helper.selectByText_check($insTermSelect, expectedInsTermText);

            // 보험기간 비교
            super.printLogAndCompare(title, expectedInsTermText, actualInsTermText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";

        WebElement $napTermSelect = (WebElement) obj[0];
        String expectedNapTermText = (String) obj[1];
        String actualNapTermText = "";

        try {
            // 납입기간 설정
            actualNapTermText = helper.selectByText_check($napTermSelect, expectedNapTermText);

            // 납입기간 비교
            super.printLogAndCompare(title, expectedNapTermText, actualNapTermText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";

        WebElement $cycleSelect = (WebElement) obj[0];
        String expectedCycleText = (String) obj[1];
        String actualCycleText = "";

        try {
            // 납입주기 설정
            actualCycleText = helper.selectByText_check($cycleSelect, expectedCycleText);

            // 납입주기 비교
            super.printLogAndCompare(title, expectedCycleText, actualCycleText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    // 가입금액
    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "납입보험료";

        WebElement $assureMoneyInput = (WebElement) obj[0];
        String expectedAssureMoney = (String) obj[1];
        String actualAssureMoney = "";

        try {
            moveToElementByJavascriptExecutor($assureMoneyInput);
            helper.executeJavascript("window.scrollBy(0, -50)");
            $assureMoneyInput.clear();

            actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney);

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        super.crawlPremium(obj);
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {
        super.setInjuryLevel(obj);
    }


    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {
        super.setRenewType(obj);
    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {
        super.setRefundType(obj);
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        super.crawlReturnMoneyList(obj);
    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {
        super.crawlReturnPremium(obj);
    }

    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {
        super.setAnnuityAge(obj);
    }

    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {
        super.setAnnuityType(obj);
    }

    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {
        super.crawlExpectedSavePremium(obj);
    }

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {
        super.setUserName(obj);
    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {
        super.setDueDate(obj);
    }

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {
        super.setTravelDate(obj);
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {
        super.setPrevalenceType(obj);
    }

    /**
     * 특약셋팅
     * @param obj
     * obj[0] - List<CrawlingTreaty> (해당 가입설계의 특약 - 필수)
     * obj[1] - List<WebElement> (원수사 특약명이 존재하는 trList - 필수)
     * obj[2] - treatyNameTd Tag (필수) ex: "th[1]" , "td[1]" 등
     * @throws SetTreatyException
     */
    protected void setTreaties(Object...obj) throws SetTreatyException {

        List<CrawlingTreaty> treaties = (List<CrawlingTreaty>) obj[0];
        List<WebElement> $trList = (List<WebElement>) obj[1];
        String treatyNameTdTag = (String) obj[2];

        String script = "";

        try {
            /**
             * 한화손해보험 공시실 대면상품의 경우 ui가 굉장히 번거롭다.
             * 미가입하는 특약들에 대해서 전부 0만원으로 세팅해줘야하는 작업을 해야한다.
             * 손쉽게 작업하기 위해서 처음부터 모든 특약을 미가입처리한채로 시작한다.
             */

            //활성화된 input값 0만원으로 초기화 (= 특약 미가입 처리)
            script = "$('tr:visible input[name*=ainsure]:not(:disabled)').val('0');";
            executeJavascript(script);

            //활성화된 select "선택" 값으로 초기화 (= 특약 미가입 처리)
            script = "$('tr:visible select[name*=ainsure]:not(:disabled) option[value=\"0\"]').prop('selected', true);";
            executeJavascript(script);

            //원수사 특약명이 존재하는 tr만 조회
            List<CrawlingTreaty> targetTreaties = new ArrayList<>();

            for(WebElement $tr : $trList) {
                WebElement $treatyNameTd = $tr.findElement(By.xpath(treatyNameTdTag));
                String targetTreatyName = $treatyNameTd.getText().trim().replaceAll(" ", "");

                //가입설계 특약 조회
                for(CrawlingTreaty treaty : treaties) {
                    String treatyName = treaty.treatyName.trim().replaceAll(" ", "");
                    String treatyAssureMoney = String.valueOf(treaty.assureMoney);
                    //가입설계 특약 보기, 납기, 가입금액 세팅하기
                    if(targetTreatyName.equals(treatyName)) {
                        CrawlingTreaty targetTreaty = new CrawlingTreaty();

                        String targetInsTerm;
                        String targetNapTerm;

                        //특약명 보이게 스크롤 이동
                        moveToElementByJavascriptExecutor($tr);
                        helper.executeJavascript("window.scrollBy(0, -50)");

                        logger.info("특약명 : {}", targetTreatyName);

                        // 특약명 태그가 th -> td1~3 / 특약명 태그가 td -> td2~4
                        String[] tdArray = treatyNameTdTag.contains("th") ? new String[]{"./td[1]", "./td[2]", "./td[3]"} : new String[]{"./td[2]", "./td[3]", "./td[4]"};

                        //원수사에서의 특약 보험기간 element 찾기
                        WebElement $treatyInsTermTd = $tr.findElement(By.xpath(tdArray[0]));
                        targetInsTerm = $treatyInsTermTd.getText().trim().replace("만기", "");

                        //원수사에서의 특약 납입기간 element 찾기
                        WebElement $treatyNapTermTd = $tr.findElement(By.xpath(tdArray[1]));
                        targetNapTerm = $treatyNapTermTd.getText().trim();

                        if (targetNapTerm.equals("전기납")) {
                            targetNapTerm = targetInsTerm;
                        } else {
                            targetNapTerm = targetNapTerm.replace("납", "년");
                        }

                        //원수사에서의 특약 가입금액 element
                        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath(tdArray[2]));
                        WebElement $child = null;
                        String targetAssureMoney = "";

                        int unit = 1;
                        String assureMoneyUnit = $treatyAssureMoneyTd.getText().trim();

                        if (assureMoneyUnit.equals("만원")) {
                            unit = 10000;
                        } else if (assureMoneyUnit.equals("천원")) {
                            unit = 1000;
                        }

                        try {
                            $child = $treatyAssureMoneyTd.findElement(By.xpath("./*[contains(@name, 'ainsure')]"));
                            $child.click();

                            if("input".equals($child.getTagName())) {
                                //가입금액 세팅란이 input인 경우
                                String type = $child.getAttribute("type");
                                if ("hidden".equals(type)) {
                                    // hidden 경우 수동으로 셋팅불가 -> 자동셋팅됨
                                    targetAssureMoney = treatyAssureMoney;
                                } else {
                                    // 특약금액을 선택할 때 다른 특약에 영향을 주는게 있어서 초기화 시키고 가입할 특약 금액 셋팅
                                    // 한화손해보험 공시실에서는 input을 초기화할 때 ctrl + a + delete가 작동하지 않는다.
                                    // 무조건 backspace로 지워야함. 현재 입력된 text의 길이만큼 backspace를 누른다.
                                    script = "return $(arguments[0]).val();";
                                    String currentValue = String.valueOf(executeJavascript(script, $child));
                                    for(int i = 0; i < currentValue.length(); i++) {
                                        $child.sendKeys(Keys.BACK_SPACE);
                                    }

                                    // 금액 단위에 따라 가입금액 변환
                                    treatyAssureMoney = String.valueOf(Integer.parseInt(treatyAssureMoney) / unit);
                                    // 가입금액 입력
                                    targetAssureMoney = helper.sendKeys4_check($child, treatyAssureMoney);
                                    // 입력된 금액 단위 반영해서 재변환
                                    targetAssureMoney = String.valueOf(Integer.parseInt(targetAssureMoney) * unit);
                                }
                            } else if("select".equals($child.getTagName())) {
                                //가입금액 세팅란이 select인 경우
                                targetAssureMoney = selectOptionFor($child, treatyAssureMoney);
                            }
                        } catch (Exception e) {
                            // 특약가입이 고정이라 $child 를 찾지 못한경우
                            logger.info("해당 특약은 가입금액이 고정입니다.");
                            $treatyAssureMoneyTd.click();
                            WaitUtil.waitFor(1);
                            targetAssureMoney = $treatyAssureMoneyTd.getText();
                            targetAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetAssureMoney));
                            logger.info("가입금액: {}", targetAssureMoney);
                        }

                        targetTreaty.setTreatyName(treatyName);
                        targetTreaty.setInsTerm(targetInsTerm);
                        targetTreaty.setNapTerm(targetNapTerm);
                        targetTreaty.setAssureMoney(Integer.parseInt(targetAssureMoney));

                        targetTreaties.add(targetTreaty);
                        break;
                    }
                }
            }

            // 원수사 자체적으로 특약 가입금액을 변경한게 있는지 확인하여 값 초기화
            logger.info("원수사 자체 특약 가입금액 변경 확인");
            for (WebElement $tr : $trList) {
                WebElement e = $tr.findElement(By.xpath("./td[3]/child::*"));
                String wTreatyNm = $tr.findElement(By.xpath("./th")).getText().trim().replaceAll(" ", "");
                int assureMoney = Integer.parseInt(e.getAttribute("value"));
                if (assureMoney != 0) {
                    int treatiesSize = treaties.size();
                    for (int i = 0; i < treatiesSize; i++) {
                        String uTreatyNm = targetTreaties.get(i).getTreatyName().trim().replaceAll(" ", "");
                        // 가설에 존재하지 않은 특약인지 확인 후 설정되어있는 값을 0으로 초기화
                        if (wTreatyNm.equals(uTreatyNm)) { break; }
                        if (i+1 == treatiesSize) {
                            logger.info("{} 항목 0원으로 금액 수정", wTreatyNm);
                            switch (e.getTagName()) {
                                case "select" : Select sel = new Select(e.findElement(By.xpath("./[td3]/select")));
                                    sel.selectByValue("0");
                                    break;
                                case "input" :
                                    e.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
                                    e.sendKeys("0");
                                    break;
                            }
                        }
                    }
                }
            }

            boolean result = false;
            List<String> inconsistentTreatyList = new ArrayList();
            logger.info("원수사와 가설 일치여부 비교");
            for (WebElement e : $trList) {
                String wTreatyNm = e.findElement(By.xpath("./th")).getText().trim().replaceAll(" ", "");
                String uTreatyNm = "";
                for (CrawlingTreaty treaty : treaties) {
                    uTreatyNm = treaty.getTreatyName().trim().replaceAll(" ", "");
                    // 특약 설정한 금액에 원수사 자체적으로 값 변경한게 있는지 확인하여 비교
                    if (uTreatyNm.equals(wTreatyNm)) {
                        String wInsTerm = e.findElement(By.xpath("./td[1]")).getText().replaceAll("만기", "");
                        String wNapTerm = e.findElement(By.xpath("./td[2]")).getText().replaceAll("납", "");
                        if ("전기".equals(wNapTerm)) {
                            wNapTerm = wInsTerm;
                        }
                        String wAssureMoneyUnit;
                        int wAssureMoney = Integer.parseInt(e.findElement(By.xpath("./td[3]/child::*")).getAttribute("value"));
                        String unit = e.findElement(By.xpath("./td[3]")).getText();
                        wAssureMoneyUnit = wAssureMoney + unit;
                        wAssureMoney = wAssureMoneyUnit.contains("만원") ? wAssureMoney * 10000 : wAssureMoneyUnit.contains("천원") ? wAssureMoney * 1000 : 0;

                        String uInsTerm = treaty.getInsTerm();
                        String uNapTerm = treaty.getNapTerm();
                        int uAssureMoney = treaty.getAssureMoney();

                        if (uInsTerm.equals(wInsTerm) && uNapTerm.equals(wNapTerm) && uAssureMoney == wAssureMoney) {
                            result = true;
                        } else {
                            inconsistentTreatyList.add(uTreatyNm);
                            result = false;
                        }
                        break;
                    }
                }
            }

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
     * 보험료 크롤링 메서드
     * @param obj
     * obj[0] = CrawlingProduct (필수)
     * obj[1] = 월 보험료 Tag ID (필수)
     * obj[2] = 적립보험료 Tag ID (생략가능, 있는 경우에만 추가)
     */
    protected void crawlAnnouncePagePremiums(Object...obj) throws Exception {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String monthlyPremiumTagId = (String) obj[1];

        try {
            moveToElement(By.id("btnReCalc"));
            logger.info("크롤링 위해 화면이동");
        } catch (Exception e) {
            logger.info("화면이동 필요없음");
        }

        String monthlyPremium = driver.findElement(By.id(monthlyPremiumTagId)).getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = monthlyPremium;
        logger.info("월 보험료 : {}", monthlyPremium + "원");

        if ("0".equals(info.treatyList.get(0).monthlyPremium)) {
            throw new Exception("주계약 보험료는 0원일 수 없습니다");
        }

        if(info.getProductCode().contains("BAB")){
            info.setNextMoney(monthlyPremium);
            logger.info("계속보험료 설정");
        }


        try {
            String savePremiumTagId = (String) obj[2];
            String savePremium = driver.findElement(By.id(savePremiumTagId)).getText().replaceAll("[^0-9]", "");

            logger.info("적립보험료 : {}", savePremium + "원");
            info.savePremium = savePremium;
        } catch (Exception e) {
            logger.info("이 상품은 적립보험료가 없는 상품입니다!!!");
        }
    }

    /**
     * 해약환급금 조회 메서드
     * @param obj
     * obj[0] = CrawlingProduct (필수)
     * obj[1] = type (해약환급금 표 구성에 따른 타입구분)
     * - type = 생략 시 -> 납입보험료, 최저환급금, 평균환급금, 공시환급금 모두 크롤링 (th1, td1~td7)
     * - type = "DSS" -> 납입보험료, 공시환급금, 공시환급률 (th1, td1~3)
     * - type = "AMD" -> 납입보험료, 공시환급금 (th1, td4, td5)
     */
    protected void crawlAnnouncePageReturnPremiums(Object...obj) {
        CrawlingProduct info = (CrawlingProduct) obj[0];

        logger.info("정확한 해약환급금 조회 위해 다시계산하기 버튼 클릭");
        announceBtnClick(By.id("btnReCalc"));

        logger.info("해약환급금 버튼 클릭");
        announceBtnClick(By.id("btnPopCancel"));

        logger.info("해약환급금 창으로 전환");
        currentHandle = driver.getWindowHandle();

        if (wait.until(ExpectedConditions.numberOfWindowsToBe(2))) {
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);

            List<WebElement> trList = driver.findElements(By.xpath("//tbody/tr"));
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            try {
                String type = (String) obj[1];
                if (type.equals("AMD")) {
                    for (WebElement $tr : trList) {
                        String term = $tr.findElement(By.xpath(".//th")).getText().replaceAll(" ", "");                  //경과기간
                        String premiumSum = $tr.findElement(By.xpath(".//td[4]")).getText().replaceAll("[^0-9]", "");    //납입보험료
                        String returnPremium = $tr.findElement(By.xpath(".//td[5]")).getText().replaceAll("[^0-9]", ""); //환급금

                        logger.info("|_______________________");
                        logger.info("|--경과기간: {}", term);
                        logger.info("|--납입보험료: {}", premiumSum);
                        logger.info("|--공시환급금: {}", returnPremium);
                        logger.info("|_______________________");

                        PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                        planReturnMoney.setTerm(term);
                        planReturnMoney.setPremiumSum(premiumSum);
                        planReturnMoney.setReturnMoney(returnPremium);

                        planReturnMoneyList.add(planReturnMoney);
                        info.returnPremium = returnPremium.replaceAll("[^0-9]", "");
                        logger.info("만기환급금 : {}원", info.returnPremium);

                    }
                } else if (type.equals("DSS")) {
                    for (WebElement $tr : trList) {
                        String term = $tr.findElement(By.xpath("./th[1]")).getText().replaceAll(" ", "");
                        String premiumSum = $tr.findElement(By.xpath("./td[1]")).getText().replaceAll("[^0-9]", "");
                        String returnPremium = $tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
                        String returnRate = $tr.findElement(By.xpath("./td[3]")).getText();

                        logger.info("|_______________________");
                        logger.info("|--경과기간: {}", term);
                        logger.info("|--납입보험료: {}", premiumSum);
                        logger.info("|--공시환급금 : {}", returnPremium);
                        logger.info("|--공시환급률: {}", returnRate);
                        logger.info("|_______________________");

                        PlanReturnMoney p = new PlanReturnMoney();
                        p.setTerm(term);
                        p.setPremiumSum(premiumSum);
                        p.setReturnMoney(returnPremium);
                        p.setReturnRate(returnRate);

                        planReturnMoneyList.add(p);
                        info.returnPremium = returnPremium;
                        logger.info("만기환급금 : {}원", info.returnPremium);
                    }
                }
            } catch (Exception e) {
                for (WebElement tr : trList) {
                    String term = tr.findElement(By.xpath("./th[1]")).getText(); //경과기간
                    String premiumSum = tr.findElement(By.xpath("./td[1]")).getText().replaceAll("[^0-9]", "");; //납입보험료
                    String returnMoneyMin = tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", ""); //최저해약환급금
                    String returnRateMin = tr.findElement(By.xpath("./td[3]")).getText(); //최저해약환급률
                    String returnMoneyAvg = tr.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", ""); //평균해약환급금
                    String returnRateAvg = tr.findElement(By.xpath("./td[5]")).getText(); //평균해약환급률
                    String returnMoney = tr.findElement(By.xpath("./td[6]")).getText().replaceAll("[^0-9]", ""); //공시해약환급금
                    String returnRate = tr.findElement(By.xpath("./td[7]")).getText(); //공시해약환급률

                    logger.info("|_______________________");
                    logger.info("|--경과기간: {}", term);
                    logger.info("|--납입보험료: {}", premiumSum);
                    logger.info("|--최저해약환급금: {}", returnMoneyMin);
                    logger.info("|--최저환급률: {}", returnRateMin);
                    logger.info("|--평균해약환급금: {}", returnMoneyAvg);
                    logger.info("|--평균환급률: {}", returnRateAvg);
                    logger.info("|--공시해약환급금: {}", returnMoney);
                    logger.info("|--공시환급률: {}", returnRate);
                    logger.info("|_______________________");

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
                    info.returnPremium = returnMoney;
                    logger.info("만기환급금 : {}원", info.returnPremium);
                }
            }
            info.planReturnMoneyList = planReturnMoneyList;
        }
    }

    // 주민등록번호(생년월일 대신 주민등록번호 입력하는 경우)
    protected void setRegistrationNumber(Object... obj) throws SetBirthdayException {
        String title = "주민등록번호";

        WebElement $input1 = (WebElement) obj[0];
        WebElement $input2 = (WebElement) obj[1];

        String fullBirth = (String) obj[2];
        String parentBirth = fullBirth.substring(2);

        int gender = (int) obj[3];

        int year = Integer.parseInt(fullBirth.substring(0, 4));
        String startGenderValue = (gender == MALE) ? "1" : "2";
        if (year >= 2000) {
            startGenderValue = (gender == MALE) ? "3" : "4";
        }

        try {
            // 주민등록번호 앞자리 입력
            String actualValue1 = helper.sendKeys4_check($input1, parentBirth);
            String actualValue2 = helper.sendKeys4_check($input2, startGenderValue);

            // 생년월일 비교
            super.printLogAndCompare(title, parentBirth, actualValue1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    // 갱신주기
    protected void setRenewCycle(Object... obj) throws SetRenewCycleException {
        String title = "갱신주기";

        WebElement $renewCycleSelect = (WebElement) obj[0];
        String expectedRenewCycleText = (String) obj[1];
        String actualRenewCycleText = "";

        try {
            // 갱신주기 설정
            actualRenewCycleText = helper.selectByText_check($renewCycleSelect, expectedRenewCycleText);

            // 갱신주기 비교
            super.printLogAndCompare(title, expectedRenewCycleText, actualRenewCycleText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_RENEW_CYCLE;
            throw new SetRenewCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    // 고지유형 설정
    protected void setAnnounceNoticeType(String textType) throws SetNoticeTypeException {
        String title = "고지유형";

        try {
            WebElement $select = driver.findElement(By.id("siisRtFlgcd"));
            String selectedNoticeType = helper.selectByText_check($select, textType);

            //비교
            printLogAndCompare(title, textType, selectedNoticeType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TEXTTYPE;
            throw new SetNoticeTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    // 공시실 보험기간 고정인 경우 확인 메서드
    protected void checkInsTerm(WebElement $insTermElement, String insTerm) throws Exception {
        String targetInsTerm = $insTermElement.getText().trim();

        if(!targetInsTerm.contains(insTerm)) {
            throw new InsTermMismatchException("홈페이지 보험기간(" + targetInsTerm + ")과 가입설계 보험기간(" + insTerm + ")이 일치하지 않습니다.");
        }
    }

    // 공시실 납입기간 고정인 경우 확인 메서드
    protected void checkNapTerm(WebElement $napTermElement,String napTerm) throws Exception{
        String targetNapTerm = $napTermElement.getText().trim();

        if(!napTerm.equals(targetNapTerm)) {
            throw new NapTermMismatchException("홈페이지 납입기간(" + targetNapTerm + ")과 가입설계 납입기간(" + napTerm + ")이 일치하지 않습니다.");
        }
    }

    // 공시실 납입기간 고정인 경우 확인 메서드
    protected void checkNapCycle(WebElement $napTermElement,String napCycle) throws Exception{
        String targetNapCycle = $napTermElement.getText().trim();

        if(!napCycle.equals(targetNapCycle)) {
            throw new NapTermMismatchException("홈페이지 납입기간(" + targetNapCycle + ")과 가입설계 납입주기(" + napCycle + ")가 일치하지 않습니다.");
        }
    }

    //공시실 로딩이미지 명시적 대기
    protected void waitAnnounceLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("popLoading")));
    }

    //element 클릭 명시적 대기
    protected WebElement waitElementToBeClickable(WebElement element) throws Exception {
        WebElement returnElement = null;
        boolean isClickable = element.isDisplayed() && element.isEnabled();

        if(isClickable) {
            //element가 화면상으로 보이며 활성화 되어있을 때만 클릭 가능함
            returnElement = wait.until(ExpectedConditions.elementToBeClickable(element));
        } else {
            throw new Exception("element가 클릭 불가능한 상태입니다.");
        }

        return returnElement;
    }

    // 자바스크립트 excute
    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }

    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    //공시실 버튼 클릭 메서드(공시실용 명시적 대기가 포함된 코드)
    protected void announceBtnClick(By element) {
        helper.waitElementToBeClickable(driver.findElement(element)).click();
        waitAnnounceLoadingImg();
    }

    protected String selectOptionFor(WebElement $select, String text) {
        String selectedText = "";

        helper.waitElementToBeClickable($select).click();
        List<WebElement> $options = $select.findElements(By.xpath("./option[not(@value=0)]"));

        for(WebElement $option : $options) {
            String optionText = $option.getText();
            String convertedOptionText = "";

            text = String.valueOf(MoneyUtil.getDigitMoneyFromHangul(text));
            convertedOptionText = String.valueOf(MoneyUtil.getDigitMoneyFromHangul(optionText));

            if(text.equals(convertedOptionText)) {
                selectedText = convertedOptionText;
                helper.waitElementToBeClickable($option).click();

                logger.info("selected option text : {}", optionText);
                logger.info("selected option converted text : {}", convertedOptionText);

                break;
            }
        }

        return selectedText;
    }

    //해당 element가 존재하는지 여부를 리턴
    protected boolean existElement(WebElement rootEl, By element) {

        boolean isExist = true;

        try {
            rootEl.findElement(element);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }

    //해당 element가 존재하는지 여부를 리턴
    protected boolean existElement(By element) {

        boolean isExist = true;

        try {
            driver.findElement(element);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }

    //element가 보이게끔 이동
    protected void moveToElement(By location) {
        Actions action = new Actions(driver);

        WebElement element = driver.findElement(location);
        action.moveToElement(element);
        action.perform();
    }

}
