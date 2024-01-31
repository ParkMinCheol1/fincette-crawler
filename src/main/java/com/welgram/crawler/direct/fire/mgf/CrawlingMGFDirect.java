package com.welgram.crawler.direct.fire.mgf;


import static com.welgram.crawler.general.CrawlingProduct.Type.갱신형;
import static com.welgram.crawler.general.CrawlingProduct.Type.비갱신형;

import com.welgram.common.DateUtil;
import com.welgram.common.MoneyUtil;
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
import com.welgram.crawler.general.CrawlingProduct.Type;
import com.welgram.crawler.general.CrawlingTreaty;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingMGFDirect extends CrawlingMGFNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try{
            WebElement $element = (WebElement) obj[0];
            String birth = (String) obj[1];

            $element.sendKeys(birth);
            WaitUtil.waitFor(1);

        } catch (Exception e){
            throw new SetBirthdayException("생년월일 설정 오류 ::" + e.getMessage());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try {
            WebElement $element = (WebElement) obj[0];

            $element.click();
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new SetGenderException("성별 설정 오류 :: " + e.getMessage());
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {

    }

    @Override
    public void setJob(Object... obj) throws SetJobException {

        try{
            logger.info("입력창 클릭");
            driver.findElement(By.cssSelector("#jobtxt")).click();
            WaitUtil.waitFor(2);

            logger.info("회사 사무직 종사자 클릭");
            driver.findElement(By.xpath("//*[@id=\"jobRepSchMale\"]//li/a[contains(.,'회사 사무직')]")).click();
            WaitUtil.waitFor(2);

            logger.info("확인 버튼 클릭");
            driver.findElement(By.cssSelector("#confBtnJobPop")).click();
            WaitUtil.waitFor(2);

        } catch (Exception e){
            throw new SetJobException("직업 선택 중 오류 발생 :: " + e.getMessage());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try {
            Select $select = new Select((WebElement) obj[0]);
            String insTerm = (String) obj[1];

            helper.selectByText_check($select, insTerm);
            waitForCSSElement(".processing > p");
            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new SetInsTermException("보험기간 설정 오류 :: " + e.getMessage());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try {
            Select $select = new Select((WebElement) obj[0]);
            String napTerm = (String) obj[1];

            helper.selectByText_check($select, napTerm);
            waitForCSSElement(".processing > p");
            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new SetNapTermException("납입기간 설정 오류 :: " + e.getMessage());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        try {
            Select $select = new Select((WebElement) obj[0]);
            String napCycle = (String) obj[1];

            switch (napCycle){
                case "00": napCycle = "일시납"; break;
                case "01": napCycle = "월납"; break;
                case "02": napCycle = "연납"; break;
                default: napCycle = ""; break;
            }

            helper.selectByText_check($select, napCycle);
            waitForCSSElement(".processing > p");
            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new SetNapCycleException("납입방법 설정 오류 :: " + e.getMessage());
        }
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

    // 플랜 설정
    protected void setPlan(WebElement $element) throws CommonCrawlerException {

        try {
            WaitUtil.loading(1);
            $element.click();

            waitForCSSElement(".processing > p");
            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new CommonCrawlerException("플랜 설정중 오류 발생 :: " + e.getMessage());
        }
    }

    // 폰 번호 입력
    protected void setPhoneNum(WebElement $element, String number) throws CommonCrawlerException {

        try {
            $element.sendKeys(number);
            WaitUtil.waitFor(1);

        } catch (Exception e) {
            throw new CommonCrawlerException("휴대전화 번호 설정 오류 :: " + e.getMessage());
        }
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            WebElement $el = (WebElement) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];
            String premium = "";

            premium = $el.getText().replaceAll("[^0-9]", "");
            info.treatyList.get(0).monthlyPremium = premium;
            logger.info("월보험료 : " + info.treatyList.get(0).monthlyPremium);

            // 페이지 로딩이 길어지는 경우 재시도
            if(premium.equals("0") || premium.equals("")){
                WaitUtil.waitFor(5);
                logger.info("retry");
                premium = $el.getText().replaceAll("[^0-9]", "");
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

    /*
    * param1 : obj[0] 갱신형 버튼 엘리먼트
    * */
    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        try {
            WebElement $element = (WebElement) obj[0];
            Type productType = (Type) obj[1];

            if (productType == 비갱신형) {
                logger.info("비갱신형 기본선택");
            } else if (productType == 갱신형) {
                $element.click();
                logger.info("갱신형 선택");
                waitForCSSElement(".processing > p");
            }
            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new SetProductTypeException("상품 타입 설정 오류 || " + e.getMessage());
        }
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

        try{
            WebElement $element = (WebElement) obj[0];

            $element.click();
            WaitUtil.waitFor(1);

        } catch (Exception e){
            throw new SetVehicleException("운전형태 선택 중 오류 발생 :: " + e.getMessage());
        }
    }

    // 특약 설정 메서드
    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        try{
            //title ex). (필수)필수 / 사망후유장해 / 질병 등...
//            //전체가입 or 일부가입 등 사용하지 않는 title의 버튼을 조작
            titleClick(info);
//
//            //플랜서브네임이 전체가입인 경우 사용
//            //셀프플랜을 확인할 때 사용
            selfPlan(info);

        } catch (Exception e){
            throw new SetTreatyException("특약 설정 중 오류 발생 :: " + e.getMessage());
        }
    }

    protected void titleClick(CrawlingProduct info) throws InterruptedException {

        int titleSize = 0;
        String textTypeStr[] = info.textType.split(",");

        List<String> titleList = new ArrayList<>();
        List<String> textTypeList = new ArrayList<>();

        // 담보 그룹명
        elements = driver.findElements(By.cssSelector("#innerContentArea > dl"));
        titleSize = elements.size();

        for(int i=0; i<titleSize; i++) {
            titleList.add(elements.get(i).findElement(By.cssSelector("dt > table > tbody > tr > td:nth-child(2) > span")).getText().trim());

            //(필수)필수 <-- 기본적으로 열려있으므로 클릭을 따라 하지 않음
            if(i != 0){
                elements.get(i).findElement(By.cssSelector("dt > table > tbody > tr > td:nth-child(2)")).click();
                WaitUtil.waitFor(1);
            }

        }


        for(int j=0; j<textTypeStr.length; j++){
            textTypeList.add(textTypeStr[j].trim());
        }

        titleList.removeAll(textTypeList);

        for(int i=0; i<titleSize; i++) { // dl

            for (int j=0; j<titleList.size(); j++) {

                if(elements.get(i).findElement(By.cssSelector("dt > table > tbody > tr > td:nth-child(2) > span")).getText().trim().equals(titleList.get(j).trim())){

                    logger.info("페이지 특약 이름 확인 : " + elements.get(i).findElement(By.cssSelector("dt > table > tbody > tr > td:nth-child(2) > span")).getText().trim());
                    logger.info("가설리스트 특약 이름 확인 : " + titleList.get(j).trim());
                    elements.get(i).findElement(By.cssSelector("dt > table > tbody > tr > td:nth-child(1) > strong > label")).click();
                    WaitUtil.waitFor(1);

                }
            }
        }
    }

    protected void selfPlan(CrawlingProduct info) throws InterruptedException {

        List<String> planTreatyList = new ArrayList<>();

        for (int i = 0; i < info.treatyList.size(); i++) {
            planTreatyList.add(info.treatyList.get(i).treatyName.trim());
        }

        List<WebElement> innerContentDlList = driver.findElements(By.cssSelector("#innerContentArea > dl"));
        int dlListSize = innerContentDlList.size();

        for (int i = 0; i < dlListSize; i++) {

            List<WebElement> ddList = innerContentDlList.get(i).findElements(By.cssSelector("dd > table > tbody > tr"));
            int ddListSize = ddList.size();

            for (int j = 0; j < ddListSize; j++) {

                //첫번째 요소는 <th>로 [보장내역 / 보장가입금액 / 월보험료]이므로 넘김
                if (j != 0) {

                    for (int z = 0; z < planTreatyList.size(); z++) {

                        if (ddList.get(j).findElement(By.cssSelector("td:nth-child(2)")).getText().trim().equals(planTreatyList.get(z).trim())) {

                            logger.info("반복횟수 : " + z+"번");

                            logger.info("금액확인 : " + info.treatyList.get(z).assureMoney);
                            double money = info.treatyList.get(z).assureMoney;
                            int intMoney = info.treatyList.get(z).assureMoney;
                            String StringMoney = Integer.toString(info.treatyList.get(z).assureMoney);
                            // 금액 변환
                            String moneyStr = convertMoney(StringMoney, money, intMoney);
                            logger.info("변환된 금액확인 : " + moneyStr);

                            logger.info("페이지 특약이름 확인 : " + ddList.get(j).findElement(By.cssSelector("td:nth-child(2)")).getText().trim());
                            logger.info("가설 특약이름 확인 : " + planTreatyList.get(z).trim());

                            //얘는 보장가입금액임
                            List<WebElement> moneyList = ddList.get(j).findElements(By.cssSelector("td:nth-child(3) > div > span"));
                            int moneyListSize = moneyList.size();

                            for (int m = 0; m < moneyListSize; m++) {

                                logger.info("금액확인 : " + moneyList.get(m).getText().trim());

                                if (moneyList.get(m).getText().trim().equals(moneyStr)) {
                                    moneyList.get(m).click();
                                    WaitUtil.waitFor(1);
                                    break;
                                }
                            }

                            //특약이 맞는 경우, 특약을 리스트에서 지워 버림 (완료된 특약을 없애서 속도향상)
                            //가입금액때문에 지금은 못쓸 듯 느려도 일단 주석처리하고 진행
                            //planTreatyList.remove(planTreatyList.get(z).trim());
                        }
                    }
                }
            }
        }
    }

    protected void recommendationPlan(CrawlingProduct info) throws  InterruptedException{

        logger.info("추천설정");
        List<String> planTreatyList = new ArrayList<>();

        for (int i = 0; i < info.treatyList.size(); i++) {
            planTreatyList.add(info.treatyList.get(i).treatyName.trim());
        }

        elements = driver.findElements(By.cssSelector("#innerContentArea > dl > dd > table > tbody > tr"));
        int elementsSize = elements.size();

        for(int i=0; i<elementsSize; i++){

            for(int j=0; j<planTreatyList.size(); j++){

                if(i != 0){
                    if(elements.get(i).findElement(By.cssSelector("td:nth-child(2)")).getText().trim().equals(planTreatyList.get(j))){

                        logger.info("금액확인 : " + info.treatyList.get(j).assureMoney);
                        double money = info.treatyList.get(j).assureMoney;
                        int intMoney = info.treatyList.get(j).assureMoney;
                        String StringMoney = Integer.toString(info.treatyList.get(j).assureMoney);
                        String moneyStr = "";

                        if (StringMoney.length() == 9) {
                            moneyStr = formatD(money / 100000000);
                            moneyStr = moneyStr + "억원";
                        } else {
                            DecimalFormat moneyDf = new DecimalFormat("###,###");
                            intMoney = intMoney / 10000;
                            moneyStr = moneyDf.format(intMoney) + "만원";
                        }

                        logger.info("변환된 금액확인 : " + moneyStr);

                        logger.info("페이지 특약이름 확인 : " + elements.get(i).findElement(By.cssSelector("td:nth-child(2)")).getText().trim());
                        logger.info("가설 특약이름 확인 : " + planTreatyList.get(j).trim());

                        //얘는 보장가입금액임
                        List<WebElement> moneyList = elements.get(i).findElements(By.cssSelector("td:nth-child(3) > div > span"));
                        int moneyListSize = moneyList.size();

                        for (int m = 0; m < moneyListSize; m++) {

                            logger.info("금액확인 : " + moneyList.get(m).getText().trim());

                            if (moneyList.get(m).getText().trim().equals(moneyStr)) {
                                moneyList.get(m).click();
                                WaitUtil.waitFor(1);
                                break;
                            }
                        }
                    }
                }

            }
        }
    }

    // 특약 일치여부 확인
    protected void checkTreaties(List<WebElement> $trList, List<CrawlingTreaty> welgramTreatyList, String $tdId) throws CommonCrawlerException {

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
        int unit = 10000;

        for (WebElement $tr : $trList) {
            String pageTreatyName = $tr.findElement(By.xpath(".//*[@class='item02']")).getText().trim();
            String pageTreatyMoneyText = $tr.findElement(By.xpath(".//*[contains(@id,'" + $tdId + "')]")).getText().replaceAll("[^0-9]", "").trim();
//            String pageTreatyMoneyText = $tr.findElement(By.xpath("./td[starts-with(@id,'" + $tdId + "')]")).getText().replaceAll("[^0-9]", "").trim();
            int pageTreatyMoney = Math.toIntExact(MoneyUtil.toDigitMoney(pageTreatyMoneyText)) * unit;

            logger.info("========================================");
            logger.info("페이지 특약명 : {}", pageTreatyName);
            logger.info("페이지 가입금액 : {}", pageTreatyMoney);
            logger.info("----------------------------------------");

            CrawlingTreaty targetTreaty = new CrawlingTreaty();
            targetTreaty.setTreatyName(pageTreatyName);
            targetTreaty.setAssureMoney(pageTreatyMoney);
            targetTreatyList.add(targetTreaty);
        }

        logger.info("특약 비교 및 확인");
        boolean result = advancedCompareTreaties(
            targetTreatyList,
            welgramTreatyList,
            new CrawlingTreatyEqualStrategy1());

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
    protected void btnClick(WebElement element, int sec) throws Exception {

        element.click();
        WaitUtil.loading(sec);
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

    /*
     * 보험료 재계산 버튼 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnReCalc(WebElement element) throws Exception {

        try {
            WebElement $button = element;

            logger.info("보험료 재계산하기 버튼이 있는 경우");
            $button.click();
            waitForCSSElement(".Loading_area");
            WaitUtil.waitFor(1);

        } catch (Exception e){
            logger.info("보험료 재계산하기 버튼이 존재하지 않음");
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

    // 개인정보 동의 팝업
    protected void privacyPopup() throws CommonCrawlerException {
        try {

            WebElement agreeAllBtn = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//label[@for='allAgree01pop']")
            ).stream().filter(WebElement::isDisplayed).findFirst().get();

            helper.click(
                agreeAllBtn
            , "전체 동의"
            );

            helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//label[contains(.,'미동의')]")
            ).stream().filter(
                WebElement::isDisplayed
            ).forEach(
                el -> helper.click(el, "미동의")
            );

            helper.click(
                By.xpath("//a[contains(.,'보험료 확인하기')]")
            ,"보험료 확인하기 버튼"
            );

            helper.waitForLoading(By.cssSelector("div.blockUI"));

        } catch (Exception e){
            throw new CommonCrawlerException("개인정보 동의 팝업에서 오류 발생 :: " + e.getMessage());
        }
    }

    // '고객님께 안내드립니다' 팝업
    protected void noticePopup() throws CommonCrawlerException {
        try{
            if (helper.existElement(By.cssSelector("#layer01checkInsAgChngDt > div > div"))) {
                logger.info("고객님 안내드립니다. 팝업있음");
                WaitUtil.waitFor(1);
                // 확인 버튼
                btnClick(driver.findElement(By.cssSelector("#confirmPopBtncheckInsAgChngDt")),2);
            } else {
                logger.info("고객님 안내드립니다. 팝업없음");
            }
        } catch (Exception e){
            throw new CommonCrawlerException("'고객님께 안내드립니다' 팝업에서 오류 발생 :: " + e.getMessage());
        }
    }

    public static String formatD(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }

    public String convertMoney(String StringMoney, double money, int intMoney){

        String moneyStr = "";

        if (StringMoney.length() == 9) {
            moneyStr = formatD(money / 100000000);
            moneyStr = moneyStr + "억원";
        } else {
            DecimalFormat moneyDf = new DecimalFormat("###,###");
            intMoney = intMoney / 10000;
            moneyStr = moneyDf.format(intMoney) + "만원";
        }
        return moneyStr;
    }

}



