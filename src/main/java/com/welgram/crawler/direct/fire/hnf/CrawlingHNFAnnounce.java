package com.welgram.crawler.direct.fire.hnf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;



public abstract class CrawlingHNFAnnounce extends CrawlingHNFNew {

    /**
     * 기본정보 설정
     * @param info
     * @throws Exception
     */
    public void setUserInfo(CrawlingProduct info) throws Exception {
        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());
    }



    /**
     * 가입조건 설정
     *
     * 공시실 모든 대면 상품을 대상으로 표준화를 해놓았기 때문에
     * 세팅할 항목이 존재하는지 여부를 먼저 판단한 후에 있을 경우에만 항목을 세팅한다.
     * @param info
     * @throws Exception
     */
    public void setJoinCondition(CrawlingProduct info) throws Exception {
        boolean isExist = false;

        isExist = helper.existElement(By.xpath("//label[normalize-space()='상품종형']"));
        if(isExist) {
            logger.info("상품종형 설정");
            setProductType2(info.getTextType());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='건강Grade']"));
        if(isExist) {
            logger.info("건강Grade 설정");
            setHealthGrade(info.getTextType());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='표준체']"));
        if(isExist) {
            logger.info("표준체 설정");
            setInsuName(info.getTextType());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='가입유형']"));
        if(isExist) {
            logger.info("가입유형 설정");
            setProductType(info.getTextType());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='플랜유형']"));
        if(isExist) {
            logger.info("플랜유형 설정");
            setProductType(info.getTextType());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='보험기간']"));
        if(isExist) {
            logger.info("보험기간 설정");
            setInsTerm(info.getInsTerm());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='납입기간']"));
        if(isExist) {
            logger.info("납입기간 설정");
            setNapTerm(info.getNapTerm());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='납입주기']"));
        if(isExist) {
            logger.info("납입주기 설정");
            setNapCycle(info.getNapCycleName());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='담보갱신주기']"));
        if(isExist) {
            logger.info("담보갱신주기 설정");
            setRenewType(info.getTextType());
        }

        isExist = helper.existElement(By.xpath("//label[normalize-space()='직업']"));
        if(isExist) {
            logger.info("직업 설정");
            setJob("전문가 및 관련 종사자", "과학 전문가 및 관련직", "생명과학 연구원", "생명과학 연구원");
        }

        WebElement $button = driver.findElement(By.id("btnTrtySrch"));
        click($button);
    }


    //todo 리팩토링 필요(영업보험료 세팅하는 케이스와 아닌 케이스로 분류해서)

//    /**
//     * 영업보험료 세팅없이 보장보험료 그대로 세팅하는 케이스
//     * @param info
//     * @throws Exception
//     */
//    public void setPremium(CrawlingProduct info) throws Exception {
//        WebElement $premiumTd = null;               //보장보험료 영역
//        WebElement $businessPremiumInput = null;    //영업보험료 input
//        WebElement $savePremiumTd = null;           //적립보험료 영역
//        WebElement $returnPremiumTd = null;         //예상만기환급금 영역
//        String premium = "";                        //보장보험료
//        String savePremium = "";                    //적립보험료
//        String returnPremium = "";                  //예상만기환급금
//        WebElement $button = null;
//
//        logger.info("보험료계산 버튼 클릭");
//        $button = driver.findElement(By.id("btnInsCalc"));
//        click($button);
//
//        logger.info("보장보험료 금액 읽어오기");
//        $premiumTd = driver.findElement(By.id("nGrntPrem"));
//        premium = $premiumTd.getText();
//        premium = premium.replaceAll("[^0-9]", "");
//
//        logger.info("영업보험료 금액 세팅하기(보장보험료 금액 그대로 세팅한다)");
//        $businessPremiumInput = driver.findElement(By.id("txtBussPrem"));
//        helper.setTextToInputBox($businessPremiumInput, premium);
//
//        logger.info("재산출 버튼 클릭");
//        $button = driver.findElement(By.id("btnReCalc"));
//        click($button);
//
//        By modalPosition = By.xpath("//div[@class='alert_box']");
//        boolean isModal = helper.existElement(modalPosition);
//        if(isModal) {
//            //최저보험료 관련 모달창이 뜨는 경우
//            logger.info("최저보험료 관련 모달창이 떴습니다~~");
//            WebElement $alert = driver.findElement(modalPosition);
//            WebElement $alertBody = $alert.findElement(By.xpath(".//div[@class='alert_body']"));
//            String alertMessage = $alertBody.getText();
//            logger.info(alertMessage);
//            throw new Exception(alertMessage);
//        }
//
//        logger.info("보장보험료 금액 읽어오기");
//        $premiumTd = driver.findElement(By.id("nGrntPrem"));
//        premium = $premiumTd.getText();
//        premium = premium.replaceAll("[^0-9]", "");
//
//        logger.info("적립보험료 금액 읽어오기");
//        $savePremiumTd = driver.findElement(By.id("txtAccuPrem"));
//        savePremium = $savePremiumTd.getText();
//        savePremium = savePremium.replaceAll("[^0-9]", "");
//
//        logger.info("예상만기환급금 금액 읽어오기");
//        $returnPremiumTd = driver.findElement(By.id("txtExptEndRetrnAmt"));
//        returnPremium = $returnPremiumTd.getText();
//        returnPremium = returnPremium.replaceAll("[^0-9]", "");
//
//        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
//        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;
//
//        //주계약에 보험료 정보 세팅
//        mainTreaty.monthlyPremium = premium;
//        info.savePremium = savePremium;
//        info.returnPremium = returnPremium;
//
//        if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
//            logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
//            throw new PremiumCrawlerException(exceptionEnum.getMsg());
//        } else {
//            logger.info("보장보험료 : {}원", mainTreaty.monthlyPremium);
//            logger.info("적립보험료 : {}원", info.savePremium);
//            logger.info("예상만기환급금 : {}원", info.returnPremium);
//        }
//    }



    /**
     * 영업보험료 임의로 세팅 해야하는 케이스
     * @param info
     * @throws Exception
     */
    public void setPremium(CrawlingProduct info) throws Exception {
        WebElement $premiumTd = null;               //보장보험료 영역
        WebElement $businessPremiumInput = null;    //영업보험료 input
        WebElement $savePremiumTd = null;           //적립보험료 영역
        WebElement $returnPremiumTd = null;         //예상만기환급금 영역
        String premium = "";                        //보장보험료
        String savePremium = "";                    //적립보험료
        String returnPremium = "";                  //예상만기환급금
        WebElement $button = null;

        logger.info("영업보험료에 임의로 100만원 세팅하기");
        $businessPremiumInput = driver.findElement(By.id("txtBussPrem"));
        helper.sendKeys4_check($businessPremiumInput, "1000000");

        logger.info("보험료계산 버튼  클릭하기");
        $button = driver.findElement(By.id("btnInsCalc"));
        click($button);

        logger.info("보장보험료 금액 읽어오기");
        $premiumTd = driver.findElement(By.id("nGrntPrem"));
        premium = $premiumTd.getText();
        premium = premium.replaceAll("[^0-9]", "");

        logger.info("보장보험료 금액을 영업보험료에 세팅(적립보험료를 0원으로 만들기 위함)");
        $businessPremiumInput = driver.findElement(By.id("txtBussPrem"));
        helper.sendKeys4_check($businessPremiumInput, premium);

        logger.info("보험료계산 버튼 클릭하기");
        $button = driver.findElement(By.id("btnInsCalc"));
        click($button);

        By modalPosition = By.xpath("//div[@class='alert_box']");
        boolean isModal = helper.existElement(modalPosition);
        if(isModal) {
            //최저보험료 관련 모달창이 뜨는 경우
            logger.info("최저보험료 관련 모달창이 떴습니다~~");
            WebElement $alert = driver.findElement(modalPosition);
            WebElement $alertBody = $alert.findElement(By.xpath(".//div[@class='alert_body']"));
            String alertMessage = $alertBody.getText();
            logger.info(alertMessage);
            throw new Exception(alertMessage);
        }

        logger.info("보장보험료 금액 읽어오기");
        $premiumTd = driver.findElement(By.id("nGrntPrem"));
        premium = $premiumTd.getText();
        premium = premium.replaceAll("[^0-9]", "");

        logger.info("적립보험료 금액 읽어오기");
        $savePremiumTd = driver.findElement(By.id("txtAccuPrem"));
        savePremium = $savePremiumTd.getText();
        savePremium = savePremium.replaceAll("[^0-9]", "");

        logger.info("예상만기환급금 금액 읽어오기");
        $returnPremiumTd = driver.findElement(By.id("txtExptEndRetrnAmt"));
        returnPremium = $returnPremiumTd.getText();
        returnPremium = returnPremium.replaceAll("[^0-9]", "");

        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        //주계약에 보험료 정보 세팅
        mainTreaty.monthlyPremium = premium;
        info.savePremium = savePremium;
        info.returnPremium = returnPremium;

        if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
            logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
            throw new PremiumCrawlerException(exceptionEnum.getMsg());
        } else {
            logger.info("보장보험료 : {}원", mainTreaty.monthlyPremium);
            logger.info("적립보험료 : {}원", info.savePremium);
            logger.info("예상만기환급금 : {}원", info.returnPremium);
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedBirth = (String) obj[0];
        String actualBirth = "";

        try {
            WebElement $birthInput = driver.findElement(By.id("cal_birth"));

            // 생년월일 설정
            actualBirth = helper.sendKeys4_check($birthInput, expectedBirth);

            // 비교
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
        String expectedGender = (gender == MALE) ? "남" : "여";
        String actualGender = "";

        try {
            WebElement $genderSelect = driver.findElement(By.id("selSex"));

            // 성별 설정
            actualGender = helper.selectByText_check($genderSelect, expectedGender);

            // 성별 비교
            super.printLogAndCompare(title, expectedGender, actualGender);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {
        String title = "가입유형";

        String expectedProductType = (String) obj[0];
        String actualProductType = "";
        String[] textTypes = expectedProductType.split("\\|");

        try {
            /**
             * 가입유형 select element를 찾을 때 id 속성으로 찾게하고 싶었으나
             * 어떤 상품의 경우에는 id 속성이 없는 경우도 있음.
             * 그래서 공통으로 사용할 수 있는 속성이 title 속성이였음.
             * 하지만 어떤경우에는 title 속성으로 찾고 :visible 속성으로 눈에 보이는 element를
             * 찾게해도 2건 이상 조회되는 경우가 있어서 공통된 적절한 xpath를 찾음.
             *
             */
            WebElement $productTypeSelect = driver.findElement(By.xpath("//select[@title='가입유형 선택'][not(@class='hidden')]"));

            for(String textType : textTypes) {
                // 가입유형 설정
                try {
                    textType = textType.trim();
                    actualProductType = helper.selectByText_check($productTypeSelect, textType);
                    expectedProductType = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            String script = "return $(arguments[0]).find('option:selected').text();";
            $productTypeSelect = driver.findElement(By.xpath("//select[@title='가입유형 선택'][not(@class='hidden')]"));
            actualProductType = String.valueOf(helper.executeJavascript(script, $productTypeSelect));

            // 비교
            super.printLogAndCompare(title, expectedProductType, actualProductType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setProductType2(String expectedProductType) throws SetProductTypeException {
        String title = "상품종형";

        String[] textTypes = expectedProductType.split("\\|");
        String actualProductKind = "";

        try {
            WebElement $productKindSelect = driver.findElement(By.id("selProCd"));

            for(String textType : textTypes) {
                //상품종형 설정
                try {
                    textType = textType.trim();
                    actualProductKind = helper.selectByText_check($productKindSelect, textType);
                    expectedProductType = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            String script = "return $(arguments[0]).find('option:selected').text();";
            $productKindSelect = driver.findElement(By.id("selProCd"));
            actualProductKind = String.valueOf(helper.executeJavascript(script, $productKindSelect));

            // 비교
            super.printLogAndCompare(title, expectedProductType, actualProductKind);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setHealthGrade(String expectedHealthGrade) throws CommonCrawlerException {
        String title = "건강Grade";

        String[] textTypes = expectedHealthGrade.split("\\|");
        String actualHealthGrade = "";

        try {
            WebElement $healthGradeSelect = driver.findElement(By.id("selHealthCd"));

            for(String textType : textTypes) {
                // 건강Grade 설정
                try {
                    textType = textType.trim();
                    actualHealthGrade = helper.selectByText_check($healthGradeSelect, textType);

                    expectedHealthGrade = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            String script = "return $(arguments[0]).find('option:selected').text();";
            $healthGradeSelect = driver.findElement(By.id("selHealthCd"));
            actualHealthGrade = String.valueOf(helper.executeJavascript(script, $healthGradeSelect));

            // 비교
            super.printLogAndCompare(title, expectedHealthGrade, actualHealthGrade);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_HEALTH_GRADE;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setInsuName(String expectedInsuName) throws CommonCrawlerException {
        String title = "표준체";

        String[] textTypes = expectedInsuName.split("\\|");
        String actualInsuName = "";

        try {
            WebElement $insunameSelect = driver.findElement(By.id("selProCd"));

            for (String textType : textTypes) {
                // 건강Grade 설정
                try {
                    textType = textType.trim();
                    actualInsuName = helper.selectByText_check($insunameSelect, textType);

                    expectedInsuName = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            String script = "return $(arguments[0]).find('option:selected').text();";
            $insunameSelect = driver.findElement(By.id("selProCd"));
            actualInsuName = String.valueOf(helper.executeJavascript(script, $insunameSelect));

            // 비교
            super.printLogAndCompare(title, expectedInsuName, actualInsuName);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_HEALTH_GRADE;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setPlanType(String expectedPlanType) throws CommonCrawlerException {
        String title = "플랜유형";

        String[] textTypes = expectedPlanType.split("\\|");
        String actualPlanType = "";

        try {
            WebElement $planTypeSelect = driver.findElement(By.id("selGnrzCd3"));

            for(String textType : textTypes) {
                // 플랜유형 설정
                try {
                    textType = textType.trim();
                    actualPlanType = helper.selectByText_check($planTypeSelect, textType);

                    expectedPlanType = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            // 비교
            super.printLogAndCompare(title, expectedPlanType, actualPlanType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";

        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try {
            WebElement $insTermSelect = driver.findElement(By.id("selInsurTermCd"));

            // 보험기간 설정
            actualInsTerm = helper.selectByText_check($insTermSelect, expectedInsTerm);

            // 비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";

        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            WebElement $napTermSelect = driver.findElement(By.id("selPaymTermCd"));

            // 납입기간 설정
            actualNapTerm = helper.selectByText_check($napTermSelect, expectedNapTerm);

            // 비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입주기";

        String expectedNapCycle = (String) obj[0];
        String actualNapCycle = "";

        try {
            WebElement $napCycleSelect = driver.findElement(By.id("selPaymCyclCd"));

            // 납입주기 설정
            actualNapCycle = helper.selectByText_check($napCycleSelect, expectedNapCycle);

            // 비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {
        String title = "담보갱신주기";
        String expectedRenewType = (String) obj[0];
        String[] textTypes = expectedRenewType.split("\\|");
        String actualRenewType = "";

        try {
            WebElement $renewTypeSelect = driver.findElement(By.id("selCovdPaymCyclCd"));

            for (String textType : textTypes) {
                // 담보갱신주기 설정
                try {
                    textType = textType.trim();
                    actualRenewType = helper.selectByText_check($renewTypeSelect, textType);
                    expectedRenewType = textType;
                    break;
                } catch (NoSuchElementException e) {}
            }

            String script = "return $(arguments[0]).find('option:selected').text();";
            $renewTypeSelect = driver.findElement(By.id("selCovdPaymCyclCd"));
            actualRenewType = String.valueOf(helper.executeJavascript(script, $renewTypeSelect));

            // 비교
            super.printLogAndCompare(title, expectedRenewType, actualRenewType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RENEW_TYPE;
            throw new SetRenewTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setJob(Object... obj) throws SetJobException {
        String expectedLargeCategory = (String) obj[0];
        String expectedMediumCategory = (String) obj[1];
        String expectedSmallCategory = (String) obj[2];
        String expectedJobName = (String) obj[3];

        String[] titles = {"대분류", "중분류", "소분류", "직업명"};
        String[] ids = {"selJob1", "selJob2", "selJob3", "selJob4"};
        String[] values = {expectedLargeCategory, expectedMediumCategory, expectedSmallCategory, expectedJobName};

        try {
            for (int i = 0; i < titles.length; i++) {
                String actualValue = "";

                // 직업 선택
                logger.info("{} 선택", titles[i]);
                WebElement $select = helper.waitElementToBeClickable(driver.findElement(By.id(ids[i])));
                actualValue = helper.selectByText_check($select, values[i]);
                WaitUtil.loading(1);

                // 비교
                super.printLogAndCompare(titles[i], values[i], actualValue);

            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {
        try {
            String script = "return $('div#divInsResultBox tbody:visible')[0]";
            WebElement $treatyTbody = (WebElement) helper.executeJavascript(script);

            logger.info("가입설계 특약을 바탕으로 원수사에 세팅하기");
            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                String treatyName = welgramTreaty.treatyName;

                // 원수사에서 해당 특약 tr 얻어오기
                WebElement $treatyNameTd = $treatyTbody.findElement(By.xpath(".//td[normalize-space()='" + treatyName + "']"));
                WebElement $treatyTr = $treatyNameTd.findElement(By.xpath("./parent::tr"));

                // tr에 가입설계 특약정보 세팅하기
                setTreatyInfoFromTr($treatyTr, welgramTreaty);
            }

            logger.info("실제 원수사에 가입 체크된 특약 정보 읽어오기");
            List<WebElement> $treatyTrList = $treatyTbody.findElements(By.tagName("tr"));
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

            for (WebElement $treatyTr : $treatyTrList) {
                // tr로부터 특약정보 읽어오기
                CrawlingTreaty targetTreaty = getTreatyInfoFromTr($treatyTr);

                if(targetTreaty != null) {
                    targetTreatyList.add(targetTreaty);
                }
            }

            logger.info("원수사 특약 정보 vs 가입설계 특약 정보 비교");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy2());
            if (result) {
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



    /**
     * 특약 tr에 특약정보 세팅
     * 세팅하는 특약정보에는 보장금액이 있다.
     *
     * @param $tr 입력 대상이 되는 tr element
     * @param treatyInfo 입력할 특약 정보
     * @throws SetTreatyException
     */
    private void setTreatyInfoFromTr(WebElement $tr, CrawlingTreaty treatyInfo) throws Exception {
        String treatyAssureMoney = String.valueOf(treatyInfo.getAssureMoney());

        // 특약 보장금액 영역
        WebElement $treatyAssureMoneyTd = $tr.findElement(By.xpath("./td[5]"));
        WebElement $treatyAssureMoneySelect = $treatyAssureMoneyTd.findElement(By.tagName("select"));

        // 특약 보장금액 설정
        helper.selectByValue_check($treatyAssureMoneySelect, treatyAssureMoney);
    }



    /**
     * 특약 tr로부터 세팅되어진 특약정보를 읽어온다.
     * 특약정보에는 특약명, 보험기간, 납입기간, 보장금액이 있다.
     *
     * 가입(보장금액이 미가입이 아닌경우) 특약인 경우 특약정보를 담은 CrawlingTreaty 객체를 리턴하고,
     * 미가입(보장금액이 미가입인 경우) 특약인 경우 null을 리턴한다.
     * @param $tr
     * @return
     * @throws Exception
     */
    private CrawlingTreaty getTreatyInfoFromTr(WebElement $tr) throws Exception {
        CrawlingTreaty treaty = null;
        List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

        //특약명 영역
        WebElement $treatyNameTd = $tdList.get(1);

        //특약 보험기간 영역
        WebElement $treatyInsTermTd = $tdList.get(2);

        //특약 납입기간 영역
        WebElement $treatyNapTermTd = $tdList.get(3);

        //특약 보장금액 영역
        WebElement $treatyAssureMoneyTd = $tdList.get(4);
        WebElement $treatyAssureMoneySelect = $treatyAssureMoneyTd.findElement(By.tagName("select"));

        //특약 보장금액이 "미가입"이 아닌경우에만
        String script = "return $(arguments[0]).find('option:selected').text();";
        String treatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneySelect));
        boolean isJoin = !"미가입".equals(treatyAssureMoney);

        if (isJoin) {
            String treatyName = $treatyNameTd.getText().trim();
            String treatyInsTerm = $treatyInsTermTd.getText();
            String treatyNapTerm = $treatyNapTermTd.getText();

            treatyInsTerm = treatyInsTerm.replace("만기", "");
            treatyNapTerm = treatyNapTerm.replace("납", "");
            script = "return $(arguments[0]).find('option:selected').val();";
            treatyAssureMoney = String.valueOf(helper.executeJavascript(script, $treatyAssureMoneySelect));

            treaty = new CrawlingTreaty();
            treaty.setTreatyName(treatyName);
            treaty.setInsTerm(treatyInsTerm);
            treaty.setNapTerm(treatyNapTerm);
            treaty.setAssureMoney(Integer.parseInt(treatyAssureMoney));
        }

        return treaty;
    }



    @Override
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("#loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
