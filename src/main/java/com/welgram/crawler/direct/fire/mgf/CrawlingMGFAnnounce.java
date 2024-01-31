package com.welgram.crawler.direct.fire.mgf;


import com.welgram.common.DateUtil;
import com.welgram.common.WaitUtil;
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
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingMGFAnnounce extends CrawlingMGFNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try{
            String fullBirth = (String) obj[0];
            String idArr[] = (String[]) obj[1];
            String year = fullBirth.substring(0, 4).replaceAll("[^0-9]", "");
            String month = fullBirth.substring(4, 6).replaceAll("[^0-9]", "");
            String day = fullBirth.substring(6, 8).replaceAll("[^0-9]", "");

//            helper.waitElementToBeClickable(driver.findElement(By.id(idArr[0])));
            Select $selectYear = new Select(driver.findElement(By.id(idArr[0])));
            Select $selectMonth = new Select(driver.findElement(By.id(idArr[1])));
            Select $selectDay = new Select(driver.findElement(By.id(idArr[2])));

            logger.info("연도 : " + year);
            $selectYear.selectByVisibleText(year);
            WaitUtil.waitFor(1);

            logger.info("월 : " + month);
            $selectMonth.selectByVisibleText(month);
            WaitUtil.waitFor(1);

            logger.info("일 : " + day);
            $selectDay.selectByVisibleText(day);
            WaitUtil.waitFor(1);

        } catch (Exception e){
            throw new SetBirthdayException("생년월일 설정 오류 ::" + e.getMessage());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try {
            WebElement $label = (WebElement) obj[0];

            $label.click();
            WaitUtil.waitFor(1);

            if(helper.existElement(By.cssSelector("#sAlertWin > div.SAlert-Contents > p"))){
                String msg = driver.findElement(By.cssSelector("#sAlertWin > div.SAlert-Contents > p")).getText();
                throw new SetGenderException(msg);
            }

        } catch (Exception e) {
            throw new SetGenderException("성별 설정 오류 :: " + e.getMessage());
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

        try {
            Select $select = new Select((WebElement) obj[0]);
            String insTerm = (String) obj[1];
            String napTerm = (String) obj[2];

            String napIns = insTerm + "납 " + napTerm + "만기";
            logger.info("납기/보기 :: {}", napIns);
            helper.selectByText_check($select, napIns);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetInsTermException("보험기간 설정 오류 :: " + e.getMessage());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {

    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            WebElement $el = (WebElement) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];
            String premium = "";

            logger.info("보험료 계산 클릭");
            btnCalc(driver.findElement(By.cssSelector("#btnPostStep")));

            premium = $el.getAttribute("value").replaceAll("[^0-9]", "");
            info.treatyList.get(0).monthlyPremium = premium;
            logger.info("월보험료 : " + info.treatyList.get(0).monthlyPremium);

            // 페이지 로딩이 길어지는 경우 재시도
            if(premium.equals("0") || premium.equals("")){
                WaitUtil.waitFor(5);
                logger.info("retry");
                premium = $el.getAttribute("value").replaceAll("[^0-9]", "");
                info.treatyList.get(0).monthlyPremium = premium;
                logger.info("월보험료 : " + info.treatyList.get(0).monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException("보험료 오류 발생 :: " +  e.getMessage());
        }
    }

    protected void checkPremium(WebElement $element) throws CommonCrawlerException {

        try {
            WebElement alertMsg = null;
            String money = ""; // 얼럿에서 얻어온 합계보험료

            $element.sendKeys("1");
            WaitUtil.waitFor(1);

            logger.info("보험료 계산 클릭");
            btnCalc(driver.findElement(By.cssSelector("#btnPostStep")));
            waitForCSSElement(".blockOverlay");
            WaitUtil.waitFor(1);

            alertMsg = driver.findElement(By.cssSelector("#sAlertWin > div.SAlert-Contents > p"));
            money = helper.waitVisibilityOf(alertMsg).getText().replaceAll("[^0-9]", "").trim();
            logger.info("합계보험료 확인 : "+money);
            WaitUtil.waitFor(1);

            // OK
            driver.findElement(By.cssSelector("#sAlertWin > div.SAlert-Contents > div > button")).click();
            WaitUtil.waitFor(2);

            // 얼럿이 다음 페이지로 넘어가서 뜨는 경우와 현재 페이지에서 뜨는 경우가 존재
            try{
                // 이전 버튼
                driver.findElement(By.cssSelector("#btnPreStep")).click();
                WaitUtil.waitFor(2);
            } catch (Exception e){
                logger.debug("페이지 변동 없음");
                driver.findElement(By.cssSelector("#monthPrem")).clear();
            }

            logger.info("월납입 보험료 입력");
            helper.sendKeys4_check(driver.findElement(By.cssSelector("#monthPrem")), money);

        } catch (Exception e){
            throw new CommonCrawlerException("보험료 확인 중 에러 발생 :: " + e.getMessage());
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

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

        try{
            Date departure = DateUtil.addDay(new Date(), 7);
            Date arrival = DateUtil.addDay(departure, 7);

            logger.info("출발일 선택");
            selectDay(By.xpath("//input[@id='dateFromCalendar']/parent::div/button"), departure);

            logger.info("출발시간 선택");
            selectTime(By.id("time"), "12");

            logger.info("도착일 선택");
            selectDay(By.xpath("//input[@id='dateToCalendar']/parent::div/button"), arrival);


        } catch (Exception e){
            throw new SetTravelPeriodException("여행기간 선택 중 에러 발생 :: " + e.getMessage());
        }
    }

    // 여행 날짜 선택
    protected void selectDay(By $byButton, Date date) throws Exception{

        try{
            String dateStr = DateUtil.formatString(date, "yyyyMMdd");

            String yyyy = dateStr.substring(0,4);
            String mm = dateStr.substring(4,6);
            String dd = dateStr.substring(6,8);

            logger.info("달력 클릭");
            driver.findElement($byButton).click();
//		driver.findElement(By.cssSelector("#step1 > div > div.table_01.table_v > table > tbody > tr:nth-child(3) > td > div:nth-child(1) > button")).click();
            WaitUtil.waitFor(1);

            logger.info("연도 선택");
            By $by = By.cssSelector("select.smartUIcalendar-SelectYear");
            driver.findElement($by).click();
            WaitUtil.waitFor(1);

            helper.selectByValue_check($by, yyyy);
            WaitUtil.waitFor(1);

            logger.info("월 선택");
            $by = By.cssSelector("select.smartUIcalendar-SelectMonth");
            driver.findElement($by).click();
            WaitUtil.waitFor(1);

            if(mm.substring(0,1).equals("0")){
                mm = mm.substring(1,2);
            } else{
                mm = mm.substring(0,2);
            }
            helper.selectByValue_check($by, String.valueOf((Integer.parseInt(mm) - 1)));
            WaitUtil.waitFor(1);

            logger.info("일자 선택");
            int dateDd = 0;
            String ddSubString = dd.substring(0,1);

            if(ddSubString.equals("0")){
                dateDd = Integer.parseInt(dd.substring(1,2));
            } else{
                dateDd = Integer.parseInt(dd.substring(0,2));
            }
            driver.findElement(By.xpath("//*[@class='smartUIcalendarTable']//button[contains(.,'" + dateDd + "')]")).click();
            WaitUtil.waitFor(1);

        } catch (Exception e){
            throw new CommonCrawlerException("날짜 선택 중 에러 발생 :: " + e.getMessage());
        }
    }

    /*
     * 시간설정 메서드
     * @param1 : By
     * @param2 : time 선택할 시간 (ex) 12
     */
    protected void selectTime(By $byEl, String time) throws CommonCrawlerException{

        try {
            logger.info("시간 선택 :: {}시", time);
            helper.selectByValue_check($byEl, time + "00");
            WaitUtil.waitFor(2);

        } catch (Exception e){
            throw new CommonCrawlerException("시간 설정 중 오류 발생 :: " + e.getMessage());
        }
    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        try {
            WebElement $element = (WebElement) obj[0];
            $element.click();
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetProductTypeException("상품 타입 설정 오류 || " + e.getMessage());
        }
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }

    // 특약 설정 메서드
    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

    }

    // 특약 일치여부 확인
    protected void checkTreaties(List<WebElement> $elList, List<CrawlingTreaty> welgramTreatyList) throws CommonCrawlerException {

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        for(WebElement $el : $elList){
            String pageTreatyName = $el.findElement(By.xpath("./td[@class='tal']")).getText().trim();
            String pageTreatyMoney = $el.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "").trim();
            int unit = 10000;

            logger.info("========================================");
            logger.info("페이지 특약명 : {}", pageTreatyName);
            logger.info("페이지 가입금액 : {}", Integer.parseInt(pageTreatyMoney) * unit);
            logger.info("----------------------------------------");

            for(CrawlingTreaty welgramTreaty : welgramTreatyList) {
                String wTreatyName = welgramTreaty.getTreatyName();
                int wTreatyMoney = welgramTreaty.getAssureMoney();

                if(pageTreatyName.equals(wTreatyName)){

                    CrawlingTreaty targetTreaty = new CrawlingTreaty();
                    targetTreaty.setTreatyName(pageTreatyName);
                    targetTreaty.setAssureMoney(Integer.parseInt(pageTreatyMoney) * unit);

                    logger.info(" 가설 특약명 : {}", wTreatyName);
                    logger.info(" 가설 가입금액 : {}", wTreatyMoney);
                    logger.info("========================================");

                    targetTreatyList.add(targetTreaty);
                    break;
                }
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
     * 버튼 클릭 메서드(By로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(By element) throws Exception {
        driver.findElement(element).click();
        WaitUtil.loading(1);
    }


    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(WebElement element) throws Exception {
        element.click();
        WaitUtil.loading(1);
//        findForCssElement();
    }

    /*
     * 보험료 계산하기 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnCalc(WebElement element) throws Exception {
        element.click();
//        findForCssElement(".blockUI");
        waitForCSSElement(".blockOverlay");
        WaitUtil.loading(5);

        if(helper.existElement(By.id("sAlertWin"))){
            String msg = driver.findElement(By.xpath("//*[@id=\"sAlertWin\"]/div[2]/p")).getText();
            if(msg.contains("가입불가")){
                logger.info("{}", msg);
                throw new Exception("\n에러발생 :: " + msg );
            }
        }
    }



    //로딩 대기
    protected void waitForCSSElement(String css) throws Exception {
        int time = 0;
        boolean result = true;
        try {
            while (true) {
                logger.debug("displayed :: " + driver.findElement(By.cssSelector(css)).isDisplayed());
                if (driver.findElement(By.cssSelector(css)).isDisplayed()) {
                    logger.info("로딩 중....");
                    Thread.sleep(500);
                    time += 500;
                } else {
                    logger.info("로딩 끝....");
                    WaitUtil.loading(2);
                    break;
                }
                if (time > 120000) {
                    result = false;
                    throw new Exception("무한루프 오류 입니다.");
                }
            }
        } catch (Exception e) {
            if (!result) {
                throw new Exception(e);
            }
            logger.info("####### 로딩 끝....");
        }
    }


    // 보험 종류 탭 선택
    protected void selectTab (WebElement $tab) throws CommonCrawlerException{

        try{
            WaitUtil.waitFor(3);
            helper.waitElementToBeClickable($tab).click();
            WaitUtil.waitFor(2);

        } catch (Exception e){
            throw new CommonCrawlerException("보험상품 탭 선택 중 오류가 발생했습니다. " + e.getMessage());
        }
    }

    protected void findForCssElement(String css) throws Exception {
        WaitUtil.loading(1);
        for (int i = 0; i < 30; i++) {
            WaitUtil.loading(1);
            try {
                driver.findElement(By.cssSelector(".blockUI"));
                logger.info("로딩창이 있어요..");
            } catch (Exception e) {
                logger.info("####### 로딩 끝....");
                break;
            }
        }

    }

    // 상품 계산하기 버튼 클릭
    protected void selectTargetProduct(WebElement $element) throws CommonCrawlerException{

        try{
            try{
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $element.findElement(By.xpath("./ancestor::tr/preceding-sibling::tr")));
                WaitUtil.waitFor(1);
            } catch (Exception e){
                logger.debug("이전 형제 요소 없음");
            }

            $element.click();
            WaitUtil.waitFor(2);

        } catch(Exception e){
            throw new CommonCrawlerException("해당 상품을 찾는 중 오류가 발생했습니다. " + e.getMessage());
        }
    }
}



