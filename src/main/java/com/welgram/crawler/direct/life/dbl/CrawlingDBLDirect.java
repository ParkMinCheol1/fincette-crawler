package com.welgram.crawler.direct.life.dbl;


import com.welgram.common.MoneyUtil;
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
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.ScrapableNew;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public abstract class CrawlingDBLDirect extends SeleniumCrawler implements ScrapableNew {



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try {
            String title = "생년월일";
            String welgramBirth = (String) obj[0];

            WebElement $birthLocation = driver.findElement(By.id("txtBirth"));
            String actualBirth = helper.sendKeys4_check($birthLocation, welgramBirth);
            super.printLogAndCompare(title, welgramBirth, actualBirth);

            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetBirthdayException("생일 오류\n" + e.getMessage());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";
        int gender = (int) obj[0];
        String expectedGender = (gender == MALE) ? "남자" : "여자";
        String actualGender = "";

        try{
            if(expectedGender.equals("남자")){
                driver.findElement(By.id("rdoSex-1")).findElement(By.xpath("following-sibling::label")).click();
            } else {
                driver.findElement(By.id("rdoSex-2")).findElement(By.xpath("following-sibling::label")).click();
            }

            actualGender = ((JavascriptExecutor)driver).executeScript("return $('input[name=rdoSex]:checked').next().text();").toString();
            super.printLogAndCompare(title, expectedGender, actualGender);

            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {

    }

    @Override
    public void setJob(Object... obj) throws SetJobException {

    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        driver.findElement(By.id("insdPrd")).findElement(By.xpath("following-sibling::div")).findElement(By.className("select-value")).click();

        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        expectedInsTerm = expectedInsTerm + "만기";

        try {
            WebElement $ulBox = driver.findElement(By.id("insdPrd")).findElement(By.xpath("following-sibling::div")).findElement(By.className("select-list")).findElement(By.tagName("ul"));

            List<WebElement> liList = $ulBox.findElements(By.tagName("li"));
            for(WebElement li : liList) {
                WebElement $btnLocation = li.findElement(By.tagName("button"));
                String actualInsTerm = $btnLocation.getText();
                if(expectedInsTerm.equals(actualInsTerm)){
                    $btnLocation.click();
                    super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);
                    break;
                }
            }

            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), e.getMessage());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        driver.findElement(By.id("pymtPrd")).findElement(By.xpath("following-sibling::div")).findElement(By.className("select-value")).click();

        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        if(!expectedNapTerm.equals("일시납")){
            expectedNapTerm = expectedNapTerm + "납";
        }


        try {

            WebElement $ulBox = driver.findElement(By.id("pymtPrd")).findElement(By.xpath("following-sibling::div")).findElement(By.className("select-list")).findElement(By.tagName("ul"));

            List<WebElement> liList = $ulBox.findElements(By.tagName("li"));
            for(WebElement li : liList) {
                WebElement $btnLocation = li.findElement(By.tagName("button"));
                String actualNapTerm  = $btnLocation.getText();
                if(expectedNapTerm.equals(actualNapTerm)){
                    $btnLocation.click();
                    super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);
                    break;
                }
            }

            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), e.getMessage());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {

    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        driver.findElement(By.id("insuredAmt")).findElement(By.xpath("following-sibling::div")).findElement(By.className("select-value")).click();

        String title = "가입금액";
        String expectedAssureMoney = (String) obj[0];
        expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000);

        if(expectedAssureMoney.length() == 5) {
            expectedAssureMoney = expectedAssureMoney.replaceAll("0000", "") + "억원";
        } else if (expectedAssureMoney.length() == 4 ){             // todo 만약 1,500만원일 경우에는??
            expectedAssureMoney = expectedAssureMoney.replaceAll("000", ",") + "000만원";
        } else {
            expectedAssureMoney= expectedAssureMoney + "만원";
        }

        try {

            WebElement $ulBox = driver.findElement(By.id("insuredAmt")).findElement(By.xpath("following-sibling::div")).findElement(By.className("select-list")).findElement(By.tagName("ul"));

            List<WebElement> liList = $ulBox.findElements(By.tagName("li"));
            for(WebElement li : liList) {
                WebElement $btnLocation = li.findElement(By.xpath("./button"));
                String actualAssureMoney = $btnLocation.getText();
                if(expectedAssureMoney.equals(actualAssureMoney)){
                    $btnLocation.click();
                    super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);
                    break;
                }
            }

            WaitUtil.waitFor(1);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), e.getMessage());
        }
    }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {

    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        driver.findElement(By.id("cancelRefund")).click();

        try {
            WaitUtil.waitFor(1);

            CrawlingProduct info = (CrawlingProduct) obj[0];
            String premium  = driver.findElement(By.className("hgroup")).findElement(By.tagName("em"))
                .getText().replaceAll("[^0-9]", "");
            logger.info("월보험료: " + premium);
            CrawlingTreaty treaty = info.getTreatyList().get(0);
            treaty.monthlyPremium= premium;

            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

            if("0".equals(treaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException("보험료 0원 오류\n" + exceptionEnum.getMsg());
            } else {
                logger.info("월 보험료 : {}원", premium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException("보험료 크롤링 오류\n" + e.getMessage());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            logger.info("전체보기 클릭");
            driver.findElement(By.className("more")).click();
        } catch (Exception e) {
            logger.info("전체보기가 없습니다");
        }

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            WebElement $tableLocation = driver.findElement(By.id("secondList"));
            WebElement $table = $tableLocation.findElement(By.tagName("tbody"));
            List<WebElement> $trList = $table.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                String term = $tr.findElement(By.xpath("./td[1]")).getText();
                String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();

                logger.info("경과기간 : {}", term);
                logger.info("납입보험료 : {}", premiumSum);
                logger.info("해약환급금 : {}", returnMoney);
                logger.info("환급률 : {}", returnRate);
                logger.info("==========================");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender(CrawlingProduct.Gender.M.getDesc().equals(info.getGender()) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                WaitUtil.waitFor(1);

            }

            info.planReturnMoneyList = planReturnMoneyList;

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
            throw new ReturnMoneyListCrawlerException("해약환급금 크롤링 오류\n" + e.getMessage());
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
    public void setProductType(Object... obj) throws SetProductTypeException {

    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }

    // for CCR
    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        WebElement $liBox = driver.findElement(By.id("elemListLi"));
        WebElement $ulBox = $liBox.findElement(By.className("elemList"));
        List<WebElement> $liList = $ulBox.findElements(By.xpath("./li"));

        // 특약 미선택 세팅
        for(WebElement $unSelected : $liList) {

            try {
                moveToElementByScrollIntoView($unSelected);
                WebElement $unSelectedDiv = $unSelected.findElement(By.xpath("./div"));
                $unSelectedDiv.click();

                WebElement $unSelectedBtn = $unSelected.findElement(By.xpath("./div/div[3]/ul"));
                WaitUtil.waitFor(2);
                String unSelected = "미선택";
                WebElement $unSelectedSpan = $unSelectedBtn.findElement(By.xpath(".//button[normalize-space()='" + unSelected + "']"));
                $unSelectedSpan.click();
            } catch (Exception e) {
                logger.info("미선택 항목이 없습니다");
            }

        }

        try {
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

            // 가입설계 리스트
            for(int i = 2; i < welgramTreatyList.size(); i++) {

                String welgramTreatyName = welgramTreatyList.get(i).getTreatyName().trim();
                String welgramTreatyAssureMoney = String.valueOf(welgramTreatyList.get(i).getAssureMoney());

                welgramTreatyAssureMoney = String.valueOf(Integer.parseInt(welgramTreatyAssureMoney) / 10000);
                if (welgramTreatyAssureMoney.length() == 5) {
                    welgramTreatyAssureMoney = welgramTreatyAssureMoney.replaceAll("0000", "") + "억원";
                } else if (welgramTreatyAssureMoney.length() == 4) {
                    welgramTreatyAssureMoney = welgramTreatyAssureMoney.replaceAll("000", ",") + "000만원";
                } else {
                    welgramTreatyAssureMoney = welgramTreatyAssureMoney + "만원";
                }

                for(int j = 2; j < $liList.size(); j++) {

                    WebElement $li = $liList.get(j);
                    // 특약
                    moveToElementByScrollIntoView($li);
                    WebElement targetTreatyNameDiv = $li.findElement(By.xpath("./span"));
                    String targetTreatyName = targetTreatyNameDiv.getText().trim();

                    if(targetTreatyName.equals(welgramTreatyName)) {
                        WebElement clickBtn = targetTreatyNameDiv.findElement(By.xpath("parent::li/div"));
                        clickBtn.click();

                        // 가입금액 설정
                        WebElement $buttonDiv = $li.findElement(By.xpath("./div/div[3]/ul"));
                        WaitUtil.waitFor(2);                                                                  // 여기서 기다려주지 않으면 element를 찾지 못함
                        WebElement $buttonSpan = $buttonDiv.findElement(By.xpath(".//button[normalize-space()='" + welgramTreatyAssureMoney + "']"));
                        String targetTreatyAssureMoney = $buttonSpan.getText();
                        targetTreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney.replace(",", "")));
                        $buttonSpan.click();

                        // 특약 적재
                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.setTreatyName(targetTreatyName);
                        targetTreaty.setAssureMoney(Integer.parseInt(targetTreatyAssureMoney));

                        targetTreatyList.add(targetTreaty);

                        break;
                    }

                    WaitUtil.waitFor(1);
                }
            }

            logger.info("===========================================================");
            logger.info("특약 비교 및 확인");

            boolean result = compareTreaties(targetTreatyList, welgramTreatyList);

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException("특약 오류\n" + e.getMessage());
        }
    }



    protected boolean compareTreaties(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList) throws Exception {

        boolean result = true;
        List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
        List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트
        List<String> samedTreatyNameList = null;				//가입설계와 홈페이지 둘 다 일치하는 특약명 리스트

        //홈페이지 특약명 리스트
        List<String> homepageTreatyNameList = new ArrayList<>();
        List<String> copiedHomepageTreatyNameList = null;
        for (CrawlingTreaty t : homepageTreatyList) {
            homepageTreatyNameList.add(t.treatyName);
        }
        copiedHomepageTreatyNameList = new ArrayList<>(homepageTreatyNameList);

        //가입설계 특약명 리스트
        List<String> myTreatyNameList = new ArrayList<>();
        List<String> copiedMyTreatyNameList = null;
        for (int i = 2; i < welgramTreatyList.size(); i++) {
            CrawlingTreaty t = welgramTreatyList.get(i);
            myTreatyNameList.add(t.treatyName);
        }
        copiedMyTreatyNameList = new ArrayList<>(myTreatyNameList);

        //일치하는 특약명만 추림
        homepageTreatyNameList.retainAll(myTreatyNameList);
        samedTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);

        //가입설계에 추가해야하는 특약명만 추림
        homepageTreatyNameList.removeAll(myTreatyNameList);
        toAddTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);

        //가입설계에서 제거해야하는 특약명만 추림
        myTreatyNameList.removeAll(homepageTreatyNameList);
        toRemoveTreatyNameList = new ArrayList<>(myTreatyNameList);
        myTreatyNameList = new ArrayList<>(copiedMyTreatyNameList);

        //특약명이 일치하는 경우에는 가입금액을 비교해준다.
        for (String treatyName : samedTreatyNameList) {
            CrawlingTreaty homepageTreaty = getCrawlingTreaty(homepageTreatyList, treatyName);
            CrawlingTreaty myTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);

            int homepageTreatyAssureMoney = homepageTreaty.assureMoney;
            int myTreatyAssureMoney = myTreaty.assureMoney;

            //가입금액 비교
            if (homepageTreatyAssureMoney == myTreatyAssureMoney) {
                //금액이 일치하는 경우
                logger.info("특약명 : {} | 가입금액 : {}원", treatyName, myTreatyAssureMoney);
            } else {
                //금액이 불일치하는 경우 특약정보 출력
                result = false;

                logger.info("[불일치 특약]");
                logger.info("특약명 : {}", treatyName);
                logger.info("가입설계 가입금액 : {}", myTreatyAssureMoney);
                logger.info("홈페이지 가입금액 : {}", homepageTreatyAssureMoney);
                logger.info("==============================================================");
            }
        }

        //가입설계 추가해야하는 특약정보 출력
        if (toAddTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
            logger.info("==============================================================");

            for (int i=0; i<toAddTreatyNameList.size(); i++) {
                String treatyName = toAddTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(homepageTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }
        }

        //가입설계 제거해야하는 특약정보 출력
        if(toRemoveTreatyNameList.size() > 0) {

            result = false;
            logger.info("==============================================================");
            logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
            logger.info("==============================================================");

            for (int i=0; i<toRemoveTreatyNameList.size(); i++) {

                String treatyName = toRemoveTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(welgramTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }
        }

        return result;
    }



    private CrawlingTreaty getCrawlingTreaty(List<CrawlingTreaty> treatyList, String treatyName) {

        CrawlingTreaty result = null;

        for (CrawlingTreaty treaty : treatyList) {
            if (treaty.treatyName.equals(treatyName)) {
                result = treaty;
            }
        }


        return result;
    }

    public void setPlan (Object... obj) throws SetProductTypeException {

        String expectedPlan = (String) obj[0];

        try {

            WebElement $planDiv = driver.findElement(By.xpath("//*[@id=\"contents\"]/div[3]/ul"));
            WebElement $planSpan = $planDiv.findElement(By.xpath(".//button[normalize-space()='" + expectedPlan + "']"));

            //플랜 선택
            $planSpan.click();
            String actualPlan = $planSpan.getText().trim();

            super.printLogAndCompare("플랜", expectedPlan, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TEXTTYPE;
            throw new SetProductTypeException("텍스트타입 오류\n" + e.getMessage());
        }
    }



    protected void removeNextButton() throws Exception{
        WebElement $button = driver.findElement(By.id("btn01"));
        String script = "$(arguments[0]).remove();";
        helper.executeJavascript(script, $button);
    }



    protected  void moveToElementByScrollIntoView(WebElement element) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block : 'center'});", element);
    }

}
