package com.welgram.crawler.direct.life.abl;

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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingABLDirect extends CrawlingABLNew {

    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";

        int gender = (int) obj[0];
        String expectedGender = (gender == MALE) ? "남성" : "여성";
        String actualGender = "";

        try{
            if(expectedGender.equals("남성")){
                helper.waitElementToBeClickable(By.cssSelector("label[for='sxdsScCd01']")).click();
            } else {
                helper.waitElementToBeClickable(By.cssSelector("label[for='sxdsScCd02']")).click();
            }

            actualGender = ((JavascriptExecutor)driver).executeScript("return $('input[name=sxdsScCd]:checked').next().text();").toString();
            super.printLogAndCompare(title, expectedGender, actualGender);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";

        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try{
            WebElement $birthLocation = driver.findElement(By.xpath("//*[@id=\"brthDay\"]"));

            helper.sendKeys4_check($birthLocation, expectedFullBirth);

            actualFullBirth = helper.sendKeys4_check($birthLocation, expectedFullBirth);
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "가입금액";
        String expectedAssureMoney = (String) obj[0];
        String actualAssureMoney = "";

        try{
            String $assureMoneyName = driver.findElement(By.xpath("//*[@id=\"mnContEntAmt00Div\"]/strong/label")).getText();
            WebElement $assureMoneyLocation = driver.findElement(By.xpath("//*[@id=\"mnContEntAmt00\"]"));

            if($assureMoneyName.equals("주계약 보험가입금액")){
                actualAssureMoney = helper.selectByValue_check($assureMoneyLocation, expectedAssureMoney);

            } else if ($assureMoneyName.equals("암진단시 최대보험금")){      // 암진단시 최대 보험금 = 가입금액 /2
                expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) /2);
                actualAssureMoney = helper.selectByValue_check($assureMoneyLocation, expectedAssureMoney);

            } else { // 월 납입 보험료 (SAV_D003)
                WebElement $assureMoneyBox = driver.findElement(By.id("mnContPrm00"));
                expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000);
                actualAssureMoney = helper.sendKeys4_check($assureMoneyBox, expectedAssureMoney);
            }

            // 비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try{
            boolean isExist = true;
            isExist = helper.existElement(By.xpath("//*[@id=\"mnInsrPrdYysRadioDiv00\"][@style='display: none;']"));

            if (isExist) {
                // select option
                WebElement $insTermSelect = driver.findElement(By.id("mnInsrPrdYys00"));
                actualInsTerm = helper.selectByText_check($insTermSelect, expectedInsTerm);

            } else {
                // 라디오버튼 선택일 때
                WebElement $insTermLocation = driver.findElement(By.xpath("//*[@id=\"mnInsrPrdYysInnerDiv00\"]"));
                List<WebElement> $insTermLo = $insTermLocation.findElements(By.tagName("label"));

                for(WebElement $insTerm : $insTermLo){
                    if(expectedInsTerm.equals($insTerm.getText())){
                        $insTerm.click();
                        actualInsTerm = $insTerm.getText();
                        break;
                    }
                }
            }

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
            boolean isExist = true;
            isExist = helper.existElement(By.xpath("//*[@id=\"mnInsrPadPrdYysRadioDiv00\"][@style='display: none;']"));

            if (isExist) {
                // selectOption
                WebElement $napTermLocationD = driver.findElement(By.id("mnInsrPadPrdYys00"));
                $napTermLocationD.click();
                actualNapTerm = helper.selectByText_check($napTermLocationD, expectedNapTerm);

            } else {
                // 라디오버튼 경우
                WebElement $napTermLocation = driver.findElement(By.xpath("//*[@id=\"mnInsrPadPrdYysInnerDiv00\"]"));
                List<WebElement> $napTermLo = $napTermLocation.findElements(By.tagName("label"));

                for (WebElement $napTerm : $napTermLo){
                    if (expectedNapTerm.equals($napTerm.getText())){
                        $napTerm.click();
                        actualNapTerm = $napTerm.getText();
                        break;
                    }
                }
            }

            //비교
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

        // 라디오버튼
        try {
            if (expectedNapCycle.equals("01")) {
                expectedNapCycle = "월납";
                driver.findElement(By.cssSelector("label[for='mpadYrpmtSlct00_02']")).click();
            } else if (expectedNapCycle.equals("02")) {
                expectedNapCycle = "연납";
                driver.findElement(By.cssSelector("label[for='mpadYrpmtSlct00_01']")).click();
            } else if (expectedNapCycle.equals("00")) {
                expectedNapCycle = "일시납";
            }

            actualNapCycle = ((JavascriptExecutor)driver).executeScript("return $('input[name=mpadYrpmtSlct00]:checked').next().text();").toString();

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setTreaties(List<CrawlingTreaty> welgramTreatyList) throws SetTreatyException {

        List<CrawlingTreaty>  targetTreatyList = new ArrayList<>();
        List<WebElement> $alreadyChecked = driver.findElements(By.cssSelector("input[type=checkbox][id^=trtyChk00]:checked"));

        // 체크된 특약 모두 해제
        for (WebElement $checkedInput : $alreadyChecked) {
            String id = $checkedInput.getAttribute("id");
            WebElement $label = driver.findElement(By.xpath("//label[@for='" + id +"']"));

            if ($checkedInput.isSelected()) {
                helper.waitElementToBeClickable($label).click();
            }
        }

        try {
            for (CrawlingTreaty $welgramTreaty : welgramTreatyList) {
                String welgramTreatyName = $welgramTreaty.getTreatyName();

                WebElement $targetTreaty = driver.findElement(By.xpath(".//span[text()='" + welgramTreatyName +"']"));
                String targetTreaetyName = $targetTreaty.getText();
                $targetTreaty.click();

                CrawlingTreaty targetTreaty = new CrawlingTreaty();
                targetTreaty.setTreatyName(targetTreaetyName);
                targetTreatyList.add(targetTreaty);
            }

            boolean result = compareTreaties(targetTreatyList, welgramTreatyList);

            if (result) {
                logger.info("특약 정보 모두 일치");
                logger.info("=================");
            } else {
                throw new Exception("특약 불일치");
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }

    }



    // 특약명만 비교
    protected boolean compareTreaties(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList) throws Exception {

        boolean result = true;

        List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
        List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트

        // 홈페이지 특약명 리스트
        List<String> homepageTreatyNameList = new ArrayList<>();
        List<String> copiedHomepageTreatyNameList = null;
        for(CrawlingTreaty t : homepageTreatyList) {
            homepageTreatyNameList.add(t.treatyName);
        }
        copiedHomepageTreatyNameList = new ArrayList<>(homepageTreatyNameList);

        // 가입설계 특약명 리스트
        List<String> myTreatyNameList = new ArrayList<>();
        List<String> copiedMyTreatyNameList = null;
        for(CrawlingTreaty t : welgramTreatyList) {
            myTreatyNameList.add(t.treatyName);
        }
        copiedMyTreatyNameList = new ArrayList<>(myTreatyNameList);

        // 일치하는 특약명만 추림
        homepageTreatyNameList.retainAll(myTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);

        // 가입설계에 추가해야하는 특약명만 추림
        homepageTreatyNameList.removeAll(myTreatyNameList);
        toAddTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);

        // 가입설계에서 제거해야하는 특약명만 추림
        myTreatyNameList.removeAll(homepageTreatyNameList);
        toRemoveTreatyNameList = new ArrayList<>(myTreatyNameList);

        // 가입설계 추가해야하는 특약정보 출력
        if (toAddTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
            logger.info("==============================================================");

            for (int i=0; i<toAddTreatyNameList.size(); i++) {
                String treatyName = toAddTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(homepageTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("==============================================================");
            }
        }

        // 가입설계 제거해야하는 특약정보 출력
        if (toRemoveTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
            logger.info("==============================================================");

            for (int i=0; i<toRemoveTreatyNameList.size(); i++) {
                String treatyName = toRemoveTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(welgramTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
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



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];

        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        // 다시 계산하기 버튼
        try {
            logger.info("다시계산하기 클릭");
            WaitUtil.waitFor(3);
            driver.findElement(By.xpath("//*[@id=\"calcPrm00Btn\"]")).click();
            waitLoadingImg();

        } catch (Exception e){
            logger.info("다시 계산하기 버튼이 없습니다");
        }

        // 보험료 계산
        try {
            WebElement $mPremiumLocation = helper.waitVisibilityOfElementLocated(By.xpath("//*[@id=\"vwMnContPrm00\"]"));
            String $mPremium = $mPremiumLocation.getText().replaceAll("[^0-9]", "");

            String $spanDiv = driver.findElement(By.className("vwMnContPrm_Won")).getText();
            if ($spanDiv.equals("만원")) {
                $mPremium = $mPremium + "0000";
            }

            info.treatyList.get(0).monthlyPremium = $mPremium.replaceAll("[^0-9]", "");

            if ("0".equals($mPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException("보험료 0원 오류\n" + exceptionEnum.getMsg());
            } else {
                logger.info("월 보험료 : {}", $mPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        logger.info("해약환급금표 전체보기");
        driver.findElement(By.xpath("//*[@id=\"tab_area_box_2\"]/div[2]/div/a")).click();

        CrawlingProduct info = (CrawlingProduct) obj[0];

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        try {
            WaitUtil.waitFor(3);
            WebElement $table = helper.waitVisibilityOf(driver.findElement(By.xpath("//*[@id='rscRefndList1']")));
            List<WebElement> $trList = $table.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                String term = $tdList.get(0).getText().replaceAll("12개월","1년");
                String premiumSum = $tdList.get(1).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(2).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(3).getText();

                logger.info("경과기간 : {}", term);
                logger.info("합계보험료 : {}", premiumSum);
                logger.info("해약환급금 : {}", returnMoney);
                logger.info("환급률 : {}", returnRate);
                logger.info("====================");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                // 만기환급금
                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

            logger.info("만기환급금 : {}", info.returnPremium);
            logger.info("===================================");

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void crawlReturnMoneyListTwo(Object... obj) throws ReturnMoneyListCrawlerException {

        logger.info("해약환급금표 전체보기");
        driver.findElement(By.xpath("//*[@id='tab_area_box_2']/div[2]/div/a")).click();

        CrawlingProduct info = (CrawlingProduct) obj[0];

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        try {
            WaitUtil.waitFor(3);
            WebElement $table = helper.waitVisibilityOf(driver.findElement(By.xpath("//*[@id='rscRefndList1']")));
            List<WebElement> $trList = $table.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                String term = $tdList.get(0).getText().replaceAll("12개월","1년");
                String premiumSum = $tdList.get(1).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tdList.get(3).getText().replaceAll("[^0-9]", "");
                String returnRate = $tdList.get(4).getText();

                logger.info("경과기간 : {}", term);
                logger.info("합계보험료 : {}", premiumSum);
                logger.info("해약환급금 : {}", returnMoney);
                logger.info("환급률 : {}", returnRate);
                logger.info("====================");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                // 만기환급금 (TRM)
//                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }

            logger.info("만기환급금 : {}", info.returnPremium);
            logger.info("===================================");

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    // 보험나이 초과 알림 팝업 체크
    public void ageCheckPopUp() {

        try {
            WebElement $alert = driver.findElement(By.xpath("//div[contains(@class, 'alert')]/strong[contains(.,'가입가능합니다.')]"));

            if ($alert.isDisplayed()) {
                // 확인 버튼 클릭
                $alert.findElement(By.xpath("./parent::div//a[normalize-space()='확인']")).click();
            }
        } catch (Exception e) {
            logger.info("팝업 미발생");
        }
    }



    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException { }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException { }



    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException { }



    @Override
    public void setJob(Object... obj) throws SetJobException { }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException { }



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException { }



    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException { }



    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException { }



    @Override
    public void setUserName(Object... obj) throws SetUserNameException { }



    @Override
    public void setDueDate(Object... obj) throws SetDueDateException { }



    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException { }



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException { }



    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException { }



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException { }



    protected void waitLoadingImg() {

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("globalLoadingDiv")));
    }
}
