package com.welgram.crawler.direct.life.nhl;


import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
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
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;



public abstract class CrawlingNHLDirect extends CrawlingNHLNew {



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try{

            By by = (By) obj[0];
            String fullBirth = (String) obj[1];

            helper.waitElementToBeClickable(by);
            helper.sendKeys4_check(by, fullBirth);

            // 검증
            checkValue("생년월일", fullBirth, by);

        } catch (Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try{

            By by = (By) obj[0];
            String gender = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("성별", gender, by);

        } catch (Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {}



    @Override
    public void setJob(Object... obj) throws SetJobException {}



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try{

            By by = (By) obj[0];
            String insTerm = (String) obj[1];

            helper.selectByText_check(driver.findElement(by), insTerm);
            WaitUtil.waitFor(2);

            // 검증
            checkValue("보험기간", insTerm, by);

        } catch (Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try{

            By by = (By) obj[0];
            String napTerm = (String) obj[1];
            WebElement $select = driver.findElement(by);
            String unit = ""; // 납입기간 단위

            if (napTerm.equals("전기납")) {

                napTerm = "S065";
                helper.selectByValue_check($select, napTerm);

            } else{

                unit = driver.findElement(by).findElements(By.cssSelector("option")).get(1).getText().replaceAll("[0-9]","");

                napTerm = napTerm.replaceAll("[^0-9]", "") + unit;
                helper.selectByText_check($select, napTerm);

            }

            WaitUtil.waitFor(2);

            // 검증
            checkValue("납입기간", napTerm, by);

        } catch (Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {}



    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {}



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        try{

            By by = (By) obj[0];
            int treatyMoney = Integer.valueOf((String) obj[1]);
            int unit = 10000;

            String assureMoney = String.valueOf(treatyMoney / unit).trim();
            Select $select = new Select(driver.findElement(by));

            $select.selectByValue(assureMoney);

            // 검증
            checkValue("가입금액", assureMoney, by);

        } catch(Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {}



    // 월보험료 세팅(연금)
    protected void setPremium(By by, String premium) throws CommonCrawlerException {

        try{

            int unit = 10000;
            premium = String.valueOf(Integer.parseInt(premium) / unit);

            helper.sendKeys4_check(by, premium);
            WaitUtil.loading(2);

            // 검증
            checkValue("월보험료", premium, by);

        } catch (Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new CommonCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    // 주계약 보험료
    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try{

            By by = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            String premium = driver.findElement(by).getText().replaceAll("[^0-9]", "");

            if ("0".equals(premium)) {

                throw new Exception("주계약 보험료는 0원일 수 없습니다.");

            } else {

                logger.info("보험료 : {}원", premium);

            }

            info.treatyList.get(0).monthlyPremium = premium;

        } catch(Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    //해약환급금 - 납입 기간과 일치하는 경과기간을 찾아 예상 해약환급금 크롤링
    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {

            CrawlingProduct info = (CrawlingProduct) obj[0];
            By $trElement = (By) obj[1];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            try{

                // 해약환급금 버튼 클릭
                driver.findElement(By.xpath("//*[@id='showReturn1']")).click();
                WaitUtil.waitFor(1);

            } catch (Exception e){

                logger.info("해약환급금 버튼 없음");

            }

            // 경과기간 (만기시점)
//            String maturityYear = getMaturityYear(info);

            List<WebElement> $trList = driver.findElements($trElement);

            for (WebElement $tr : $trList) {
                String term = $tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = $tr.findElements(By.tagName("td")).get(1).getText();
                String returnMoney = $tr.findElements(By.tagName("td")).get(2).getText();
                String returnRate = $tr.findElements(By.tagName("td")).get(3).getText();

                logger.info("______________해약환급급______________");
                logger.info("경과기간 :: " + term);
                logger.info("납입보험료 누계:: " + premiumSum);
                logger.info("해약환급금 :: " + returnMoney);
                logger.info("환급률 :: " + premiumSum);
                logger.info("____________________________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term);    // 경과기간
                planReturnMoney.setPremiumSum(premiumSum);    // 납입보험료 누계
                planReturnMoney.setReturnMoney(returnMoney);    // 예상해약환급금
                planReturnMoney.setReturnRate(returnRate);    // 예상 환급률

                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
                // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                  info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                }
            }
            // 만기환급금 크롤링이 불가한 경우
//            if(info.getProductCode().contains("TRM")) {
//              logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//            } else if(info.getReturnPremium().equals("")){
//              info.setReturnPremium("-1");
//            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_PREMIUM;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    // 이율 선택에 따라 테이블의 데이터가 달라지는 경우
    // 해약환급금 - 납입 기간과 일치하는 경과기간을 찾아 예상 해약환급금 크롤링
    public void crawlReturnMoneyList2(Object... obj) throws ReturnMoneyListCrawlerException {

        try {

            CrawlingProduct info = (CrawlingProduct) obj[0];
            By $trElement = (By) obj[1];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
            // 해약환급금 버튼 클릭
            driver.findElement(By.xpath("//*[@id='showReturn1']")).click();
            WaitUtil.waitFor(1);

            // 이율선택 select
            helper.waitVisibilityOfElementLocated(By.cssSelector("#selUiTabCon"));
            List<WebElement> $selectOptions = driver.findElements(By.cssSelector("#selUiTabCon > option"));
            // planReturnMoneyList 포함 여부
            boolean isContain = false;

            // 경과기간 (만기시점)
//            String maturityYear = getMaturityYear(info);

            for (WebElement $option : $selectOptions) {

                $option.click();
                WaitUtil.waitFor(2);

                List<WebElement> $trList = driver.findElements($trElement);

                for (int i = 0; i < $trList.size(); i++) {

                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                    WebElement $tr = $trList.get(i);

                    String term = $tr.findElements(By.tagName("td")).get(0).getText();
                    String premiumSum = $tr.findElements(By.tagName("td")).get(1).getText();
                    String returnMoney = $tr.findElements(By.tagName("td")).get(2).getText();
                    String returnRate = $tr.findElements(By.tagName("td")).get(3).getText();

                    planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                    planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);

                    logger.info("______________해약환급급______________");
                    logger.info("경과기간 :: {}", term);
                    logger.info("납입보혐료 누계 :: {}", premiumSum);

                    if (isContain) {

                        planReturnMoney = planReturnMoneyList.get(i);

                    }

                    if ($option.getText().contains("최저")) {

                        String return_money_min = $tr.findElements(By.tagName("td")).get(2).getText();
                        String return_rate_min = $tr.findElements(By.tagName("td")).get(3).getText();

                        planReturnMoney.setReturnMoneyMin(return_money_min);
                        planReturnMoney.setReturnRateMin(return_rate_min);

                        logger.info("예상 최저 해약환급금 :: {}", return_money_min);
                        logger.info("예상 최저 환급률 :: {}", return_rate_min);
                        logger.info("------------------------------------");

                    } else if ($option.getText().contains("평균")) {

                        String return_money_avg = $tr.findElements(By.tagName("td")).get(2).getText();
                        String return_rate_avg = $tr.findElements(By.tagName("td")).get(3).getText();

                        planReturnMoney.setReturnMoneyAvg(return_money_avg);
                        planReturnMoney.setReturnRateAvg(return_rate_avg);

                        logger.info("예상 평균 해약환급금 :: {}", return_money_avg);
                        logger.info("예상 평균 환급률 :: {}", return_rate_avg);
                        logger.info("------------------------------------");

                    } else {

                        returnMoney = $tr.findElements(By.tagName("td")).get(2).getText();
                        returnRate = $tr.findElements(By.tagName("td")).get(3).getText();

                        planReturnMoney.setReturnMoney(returnMoney);
                        planReturnMoney.setReturnRate(returnRate);

                        logger.info("예상 공시이율 해약환급금 :: {}", returnMoney);
                        logger.info("예상 공시이율 환급률 :: {}", returnRate);
                        logger.info("------------------------------------");

                        // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                        if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                          info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                        }
                    }

                    if (!isContain) {

                        planReturnMoneyList.add(planReturnMoney);

                    }

                }

                // 만기환급금 크롤링이 불가한 경우
//                if(info.getProductCode().contains("TRM")) {
//                  logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//                } else if(info.getReturnPremium().equals("")){
//                  info.setReturnPremium("-1");
//                }

                isContain = true;

            }

            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.info("해약환급금 정보 ::: {}", info.getPlanReturnMoneyList());

//          만기환급금
            info.returnPremium = planReturnMoneyList.get(planReturnMoneyList.size() - 1)
                .getReturnMoney().replace(",", "").replace("원", "");
            logger.info("만기환급금 :: " + info.getReturnPremium());

        } catch (Exception e) {

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_PREMIUM;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    /**
     * 경과기간 메서드 (만기시점 확인 유효성 검사)
     *
     * 종신보험 - 만기환급시점 = 납입기간 + 10년 이 됩니다
     * 정기보험 - 만기환급금을 크롤링하지 않습니다 (사용하고 있지 않습니다)
     */
    public String getMaturityYear(CrawlingProduct info) throws CommonCrawlerException {

        try{

            String maturityYear = null;
            String insTerm = info.getInsTerm().trim();
            String napTerm = info.getNapTerm().trim();
            int age = Integer.parseInt(info.getAge());

            if(info.getProductCode().contains("WLF")){

                // 종신보험
                napTerm = napTerm.replaceAll("[^0-9]","");
                maturityYear = String.valueOf(Integer.parseInt(napTerm) + 10);

            } else if(insTerm.contains("년")){

                maturityYear = insTerm;

            } else if(insTerm.contains("세")){

                insTerm = insTerm.replaceAll("[^0-9]","");
                maturityYear = String.valueOf(Integer.parseInt(insTerm) - age);

            } else{

                throw new CommonCrawlerException("보험기간을 확인하세요.");

            }

            maturityYear = maturityYear.replaceAll("[^0-9]","") + "년";
            logger.info("경과기간 :: {}", maturityYear);

            return maturityYear;

        } catch (Exception e){

            throw new CommonCrawlerException("만기시점 확인 중 에러 발생 \n" + e.getMessage());

        }

    }



    // 연금개시나이
    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        try{

            By by = (By) obj[0];
            String annAge = (String) obj[1];

            helper.selectByValue_check(by, annAge);
            annAge = annAge + " 세";

            // 검증
            checkValue("연금개시나이", annAge, by);

        } catch (Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new SetAnnuityAgeException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {

        try{

            By by = (By) obj[0];
            String receiveType = (String) obj[1];
            String typeOption = "";
            boolean exist = false;

            if(receiveType.contains("종신 10년")){

                typeOption = "종신연금형(10년보증)";
                exist = true;

            } else if(receiveType.contains("종신 20년")){

                typeOption = "종신연금형(20년보증)";
                exist = true;

            } else if(receiveType.contains("종신 30년")){

                typeOption = "종신연금형(30년보증)";
                exist = true;

            }

            helper.selectByText_check(by, typeOption);

            if(exist = false){

                throw new SetAnnuityTypeException();

            }

            // 검증
            checkValue("연금형태", typeOption, by);

        } catch(Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_TYPE;
            throw new SetAnnuityTypeException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    // 연금개시시점의 예상 적립금
    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {

        try{

            CrawlingProduct info = (CrawlingProduct) obj[0];
            String[] annuityType = {"종신연금형(10년보증)","종신연금형(20년보증)","종신연금형(30년보증)"};
            WebElement $typeSelect = driver.findElement(By.id("annuityPayTypeMy"));
            String whl10y = "";
            String whl20y = "";
            String whl30y = "";
            String annuityPremium = "";

            for(int i = 0; i < annuityType.length; i++){

                $typeSelect.click();
                WaitUtil.waitFor(2);
                // 연금형태 선택
                helper.selectOptionContainsText($typeSelect, annuityType[i]);
                // 다시 계산하기 버튼
                calcBtnClickforPremium(By.id("reCalcPremium"));
                // 변동여부 팝업
                checkEventPopup(By.cssSelector(".pop-modal2.mes.open"), By.id("popCloseBtn"));

                annuityPremium = driver.findElement(By.id("returnPremiumMy")).getText().replace(",", "");

                if(annuityType[i].contains("종신연금형(10년보증)")){

                    whl10y = String.valueOf(MoneyUtil.toDigitMoney(annuityPremium));

                } else if(annuityType[i].contains("종신연금형(20년보증)")){

                    whl20y = String.valueOf(MoneyUtil.toDigitMoney(annuityPremium));

                } else if(annuityType[i].contains("종신연금형(30년보증)")){

                    whl30y = String.valueOf(MoneyUtil.toDigitMoney(annuityPremium));

                } else {

                    throw new ExpectedSavePremiumCrawlerException(
                        String.valueOf(ExceptionEnum.ERR_BY_ANNUITY_TYPE));

                }
            }

            if(whl10y.equals("") || whl10y.equals("0")){

                throw new Exception("종신 10년의 금액이 null이거나 0원입니다.");

            } else if(whl20y.equals("") || whl20y.equals("0")) {

                throw new Exception("종신 20년의 금액이 null이거나 0원입니다.");

            } else if(whl30y.equals("") || whl30y.equals("0")) {

                throw new Exception("종신 30년의 금액이 null이거나 0원입니다.");

            }

            // 예상연금수령액
            if(info.getAnnuityType().contains("종신 10년")){

                info.annuityPremium = whl10y;

            } else if(info.getAnnuityType().contains("종신 20년")) {

                info.annuityPremium = whl20y;

            } else if(info.getAnnuityType().contains("종신 30년")) {

                info.annuityPremium = whl30y;

            }

            PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

            planAnnuityMoney.setWhl10Y(whl10y);
            planAnnuityMoney.setWhl20Y(whl20y);
            planAnnuityMoney.setWhl30Y(whl30y);

            info.setPlanAnnuityMoney(planAnnuityMoney);

        } catch (Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new ExpectedSavePremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    // 연금지급주기 설정
    protected void setAnnuityReceiveCycle(By by, String receiveCycle) throws CommonCrawlerException{

        try{

            helper.selectByText_check(by, receiveCycle);

            // 검증
            checkValue("연금지급주기", receiveCycle, by);

        } catch (Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_CYCLE;
            throw new CommonCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }

    }



    @Override
    public void setUserName(Object... obj) throws SetUserNameException {}



    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {}



    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {}



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {}



    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {}



    // 특약 일치여부 확인
    protected void checkTreaties(List<CrawlingTreaty> welgramTreatyList) throws CommonCrawlerException {

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        for(CrawlingTreaty welgramTreaty : welgramTreatyList){

            // 가입설계 특약 이름
            String wTreatyName = welgramTreaty.getTreatyName();
            int wTreatyMoney = welgramTreaty.getAssureMoney();

            try{

                // 원수사 홈페이지 특약 이름
                WebElement $treatyNameEl = driver.findElement(By.xpath("//*[contains(text(),'"+ wTreatyName +"')]"));
                String homepageTreatyName = $treatyNameEl.getText().trim();

                // 원수사 홈페이지 특약 가입금액
                WebElement $treatyMoneyEl = $treatyNameEl.findElement(By.xpath("./parent::li/span[2]"));
                String homepageTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney2($treatyMoneyEl.getText().trim()));

                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                
                targetTreaty.setTreatyName(homepageTreatyName);
                targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyMoney));

                logger.info("==================================================");
                logger.info("가설 특약 : {}", wTreatyName);
                logger.info("가설 가입금액 : {}", wTreatyMoney);
                logger.info("--------------------------------------------------");
                logger.info("원수사 특약 : {}", homepageTreatyName);
                logger.info("원수사 가입금액 : {}", homepageTreatyMoney);
                logger.info("==================================================");

                targetTreatyList.add(targetTreaty);

            } catch(Exception e){

                continue;

            }

        }

        logger.info("특약 비교 및 확인");
        boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

        if (result) {

            logger.info("특약 정보가 모두 일치합니다");

        } else {

            logger.error("특약 정보 불일치");
            throw new CommonCrawlerException();

        }
    }



    /*
     * 버튼 클릭 메서드
     * @param1 by 클릭하고자 하는 요소
     * @param2 sec 대기 시간
     */
    protected void btnClick(By by, int sec) throws Exception {
        driver.findElement(by).click();
        WaitUtil.waitFor(sec);
    }



    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(WebElement element, int sec) throws Exception {

        element.click();
        WaitUtil.loading(sec);
//        waitLoadingImg();

    }



    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClickByScript(WebElement element, int sec) throws Exception {

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        WaitUtil.loading(sec);

    }



    //로딩이미지 명시적 대기
    protected void waitLoadingImg() {

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingimg")));

    }



    //보험료 확인 버튼 클릭 메서드
    protected void calcBtnClickforPremium(By by) throws Exception {

        driver.findElement(by).click();
        helper.waitForCSSElement("#uiPOPLoading1");
        WaitUtil.waitFor(2);

    }

    // 입력된 보험료 확인
    protected void checkPremium(By by, String assureMoney) throws CommonCrawlerException{

        String chkPremium = driver.findElement(by).getText().replaceAll("[^0-9]", "").trim();
        chkPremium = String.valueOf(Integer.parseInt(chkPremium) * 10000);

        if(!assureMoney.equals(chkPremium)){

            logger.info("가설의 보험료와 입력된 보험료가 다릅니다.");
            logger.info("------------------------------");
            logger.info("입력된 보험료 :: {}", chkPremium);
            logger.info("가설의 보험료 :: {}", assureMoney);
            logger.info("------------------------------");
            throw new CommonCrawlerException(ExceptionEnum.ERR_BY_PREMIUM);

        }

    }



    /* 보험료 알아보기 이벤트 팝업
     * @param1 by 팝업 영역
     * @param2 $byCloseBtn close 버튼 By
     */
    protected void checkEventPopup(By by, By $byCloseBtn) throws Exception {

        try{

            WebElement popUp = driver.findElement(by);

            if(helper.existElement(by) && popUp.isDisplayed()){

                logger.info("팝업 발생");
                popUp.findElement($byCloseBtn).click();
                WaitUtil.waitFor(1);

            }

        } catch (Exception e){

            logger.info("팝업 미발생");

        }

    }



    /**
     * 선택값 검증 메서드
     *
     * @param   title           선택항목
     * @param   expectedValue   선택하려는 값
     * @param   selectedBy      실제 선택된 엘리먼트
     */
    public void checkValue(String title, String expectedValue, By selectedBy) throws CommonCrawlerException {

        try{

            WebElement selectedElement = driver.findElement(selectedBy);
            // 실제 입력된 값
            String selectedValue = "";
            String script = "";

            if(selectedElement.getTagName().equals("select")){

                script = "return $(arguments[0]).find('option:selected').text();";
                selectedValue = String.valueOf(helper.executeJavascript(script,selectedElement));

            } else{

                selectedValue = selectedElement.getText();

                if(selectedValue.equals("")){

                    script = "return $(arguments[0]).val();";
                    ((JavascriptExecutor) driver).executeScript(script, selectedElement);
                    selectedValue = String.valueOf(((JavascriptExecutor) driver).executeScript(script, selectedElement));

                }

            }

            if(selectedValue.contains("\n")){

                selectedValue = selectedValue.substring(0, selectedValue.indexOf("\n"));

            }

            if (title.equals("가입금액")) {

                selectedValue = selectedValue.replaceAll("[^0-9]", "");

            }

            printLogAndCompare(title, expectedValue, selectedValue.trim());

        } catch (Exception e){

            throw new CommonCrawlerException("선택값 체크 중 오류가 발생했습니다.\n" + e.getMessage());

        }

    }

}