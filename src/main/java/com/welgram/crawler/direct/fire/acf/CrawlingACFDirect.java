package com.welgram.crawler.direct.fire.acf;


import com.welgram.common.MoneyUtil;
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
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingACFDirect extends CrawlingACFNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try{
            By by = (By) obj[0];
            String inputBirthDay = (String) obj[1];

            driver.findElement(by).sendKeys(inputBirthDay);
            WaitUtil.loading(1);

            // 검증
            checkValue("생년월일", inputBirthDay, by);

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
            WaitUtil.waitFor(1);

            // 검증
            checkValue("성별", gender, by);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {}

    @Override
    public void setJob(Object... obj) throws SetJobException {

        String job = (String) obj[0];
        By jobInputBy = By.cssSelector("#jobName");

        try {
            logger.info("직업 검색");
            driver.findElement(By.id("btnJob")).click();
            WaitUtil.waitFor(2);

            logger.info("직업 : {} 입력", job);
            driver.findElement(jobInputBy).sendKeys(job);
            WaitUtil.loading(2);

            logger.info("{} 클릭", job);
            driver.findElement(By.xpath("//*[@id=\"mCSB_5_container\"]/ul/li/a[normalize-space()='" + job + "']")).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("직업", job, jobInputBy);

            logger.info("선택완료 클릭");
            driver.findElement(By.cssSelector("#jobOk")).click();
            WaitUtil.waitFor(2);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try{
            By byElement = (By) obj[0];
            String insTerm = (String) obj[1];
            String napCycle = (String) obj[2];
            String text = insTerm + " / " + napCycle;

            helper.selectByText_check(byElement, text);
            WaitUtil.waitFor(1);

            // 검증
            checkValue("보험기간", text, byElement);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {}
    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {}

    /*
     * 납입주기를 한글 형태의 문자열로 리턴한다.
     *  => 01을 전달하면 "월납"이라는 문자열을 리턴한다.
     *  @param napCycle : 납입주기       ex.01, 00, ...
     *  @return napCycleName : 납입주기의 한글 형태       ex.월납, 연납, ...
     * */
    protected String getNapCycleName(String napCycle) {

        String napCycleText = "";

        if (napCycle.equals("01")) {
            napCycleText = "월납";
        } else if (napCycle.equals("02")) {
            napCycleText = "년납";
        } else if (napCycle.equals("00")) {
            napCycleText = "일시납";
        }

        return napCycleText;
    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {}
    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {}
    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {}

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            By byElement = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];
            String premium = driver.findElement(byElement).getText().replaceAll("[^0-9]", "");

            info.treatyList.get(0).monthlyPremium = premium;
            logger.info("보험료 :: {}", info.treatyList.get(0).monthlyPremium);

        } catch(Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    /*
    * 상품 유형에 따른 보험료 가져오기
    * @param :: CrawlingProduct info
    */
    public void crawlPremiumByProductType(Object... obj) throws PremiumCrawlerException{

        CrawlingProduct info = (CrawlingProduct) obj[0];
        String textType = info.getTextType();
        String premium = "";

        try{
            if (textType.equals("실속형")) {
                logger.info("실속형!!");
                driver.findElement(By.cssSelector("th:nth-child(2) span")).click();
                WaitUtil.waitFor(2);
                waitLoadingImg();
                // 보험료
                premium = driver.findElement(By.cssSelector("th:nth-child(2) span em")).getText().replaceAll("[^0-9]", "");

            } else if (textType.equals("기본형")) {
                logger.info("기본형!!");
                driver.findElement(By.cssSelector("th:nth-child(3) span")).click();
                WaitUtil.waitFor(2);
                waitLoadingImg();
                // 보험료
                premium = driver.findElement(By.cssSelector("th:nth-child(3) span em")).getText().replaceAll("[^0-9]", "");

            } else if (textType.equals("고급형")) {
                // 현재 얘만 사용중입니다 - 실속형, 기본형, 자유설계형은 사용하지 않습니다
                try {
                    logger.info("고급형!!");
                    driver.findElement(By.cssSelector("th:nth-child(4) span")).click();
                    WaitUtil.waitFor(2);
                    waitLoadingImg();
                    // 보험료
                    premium = driver.findElement(By.cssSelector("th:nth-child(4) span em")).getText().replaceAll("[^0-9]", "");

                } catch(Exception e) {
                    throw new CommonCrawlerException("고급형을 설정할 수 없습니다");
                }

            } else if(textType.equals("자유설계형")) {
                // 최대케이스 | 최소케이스
                logger.info("자유설계형!!");
                logger.info("보답화면에 출력되는 치아보험은 실속형케이스(임플란트 옵션없음)에서 선택이 불가능합니다");
                logger.info("'자유설계'에서 임플란트옵션을 추가해야 기준케이스에 부합하게 됩니다");

                driver.findElement(By.xpath("//strong[text()='자유설계']")).click();
                WaitUtil.waitFor(2);

                String tempInput = ((JavascriptExecutor)driver).executeScript("return $('input[id='treatyCode_28176305']').is(':checked')").toString();
                // 크라운 끄기 (최저구하기)
                if(tempInput.equals("true")) {
                    driver.findElement(By.xpath("//input[@id='treatyCode_28176305']//parent::label")).click();
                    WaitUtil.waitFor(2);
                    logger.info("[최저CASE] 끌수 있는 옵션을 모두 끕니다");
                }

                // 임플란트 켜기
                logger.info("DEFAULT 변경없음");

                logger.info("보험료 가져오기");
                if(info.getProductKind().equals("순수보장형")) {
                    premium = driver.findElement(By.xpath("//em[@id='premium_M28506']")).getText().replaceAll("[^0-9]", "");
                    logger.info("순수보장형 보험료 : {}", premium);
                }
                else  {
                    premium = driver.findElement(By.xpath("//em[@id='premium_M28738']")).getText().replaceAll("[^0-9]", "");
                    logger.info("만기환급형 보험료 : {}", premium);
                }
            }
            //보험료 설정
            info.treatyList.get(0).monthlyPremium = premium;
            logger.info("보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }


    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try{
            CrawlingProduct info = (CrawlingProduct) obj[0];
            // 경과기간 (만기시점)
            String maturityYear = getMaturityYear(info);

            // 스크롤 이동
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,1000);");
            driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
            WaitUtil.waitFor(2);

            List<WebElement> $trList = driver.findElements(By.xpath("//tbody[@id='showCancelRefundExampleArea']/tr"));
            List<PlanReturnMoney> prmList = new ArrayList<>();

            for(WebElement $tr : $trList) {
                String term = $tr.findElement(By.xpath("./td[1]")).getText();
                String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnRate =$tr.findElement(By.xpath("./td[4]")).getText();

                PlanReturnMoney prm = new PlanReturnMoney();

                prm.setPlanId(Integer.parseInt(info.getPlanId()));
                prm.setGender((info.getGender() == MALE) ? "M" : "F");
                prm.setInsAge(Integer.parseInt(info.getAge()));

                prm.setTerm(term);
                prm.setPremiumSum(premiumSum);
                prm.setReturnMoney(returnMoney);
                prm.setReturnRate(returnRate);

                prmList.add(prm);

                logger.info("====  REFUND INFO  ==================");
                logger.info("기간 :: {}", term);
                logger.info("납입보험료 :: {}", premiumSum);
                logger.info("해약환급금 :: {}", returnMoney);
                logger.info("해약환급률 :: {}", returnRate);

                info.returnPremium = returnMoney;
//                // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                    info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                }
            }
//            // 만기환급금 크롤링이 불가한 경우
//            if(info.getProductCode().contains("TRM")) {
//                logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//            } else if(info.getReturnPremium().equals("")){
//                info.setReturnPremium("-1");
//            }
            logger.info("=====================================");
            info.setPlanReturnMoneyList(prmList);

        }catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
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

            maturityYear = maturityYear.replaceAll("[^0-9]","");

            try{
                maturityYear = maturityYear + "년";
            } catch (Exception e){
                maturityYear = maturityYear + "세";
            }
            logger.info("경과기간 :: {}", maturityYear);

            return maturityYear;

        } catch (Exception e){
            throw new CommonCrawlerException("만기시점 확인 중 에러 발생 \n" + e.getMessage());
        }
    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {}
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
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {

        try{
            logger.info("여행시작일 선택");
            driver.findElement(By.id("policyStartTime")).click();
            WaitUtil.loading(3);

            // 오늘 정보
            LocalDate today = LocalDate.now();
            LocalDate departureDateInfo = today.plusDays(7);
            LocalDate arrivalDateInfo = today.plusDays(13);

            // 출발, 도착 날짜 정보 String, Date | 년, 월, 일
            String departMonth = String.valueOf(departureDateInfo.getMonthValue());
            String departDate = String.valueOf(departureDateInfo.getDayOfMonth());
            String arrivalMonth = String.valueOf(arrivalDateInfo.getMonthValue());
            String arrivalDate = String.valueOf(arrivalDateInfo.getDayOfMonth());

            // 출발일 선택
            setTravelDepatureDate(today, departureDateInfo, departDate);

            // 도착일 선택
            setTravelArrivalDate(departMonth, arrivalMonth, arrivalDate);

            logger.info("여행날짜 선택완료");
            driver.findElement(By.id("_btnCalendarSelect_")).click();
            WaitUtil.waitFor(2);

        } catch (Exception e){

            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 여행출발날짜 선택
    protected void setTravelDepatureDate(LocalDate today, LocalDate departureDateInfo, String departureDate) throws SetTravelPeriodException{

        WebElement $tbody = null;
        List<WebElement> $tdList = null;
        String $targetDay = "";

        try{
            logger.info("출발일 선택");
            if(today.getMonthValue() != departureDateInfo.getMonthValue()) {
                logger.info("다음달 선택하기");
                driver.findElement(By.cssSelector(".xdsoft_datepicker.active > div.xdsoft_mounthpicker > button.xdsoft_next")).click();
                WaitUtil.loading(2);

            } else {
                logger.info("이번달 선택(default)");
            }

            logger.info("DEPARTURE DATE :: " + departureDate + "일");
            $tbody = driver.findElement(By.cssSelector(".xdsoft_datepicker.active > div.xdsoft_calendar > table > tbody"));
            $tdList = $tbody.findElements(By.tagName("td"));

            for (WebElement $td : $tdList) {
                if (!$td.getAttribute("class").contains("xdsoft_disabled")) {
                    $targetDay = $td.findElement(By.tagName("div")).getText().trim();

                    if (departureDate.equals($targetDay)) {

                        logger.info("ELEMENT FOUND :: {}", $targetDay);
                        $td.click();
                        WaitUtil.waitFor(2);
                        break;
                    }
                }
            }

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 여행도착날짜 선택
    protected void setTravelArrivalDate(String departMonth, String arrivalMonth, String arrivalDate) throws SetTravelPeriodException{

        WebElement $tbody = null;
        List<WebElement> $tdList = null;
        String $targetDay = "";

        try{
            logger.info("도착일 선택");
            logger.info("DEPART_MONTH  :: {}", departMonth);
            logger.info("ARRIVAL_MONTH :: {}", arrivalMonth);

            if(!arrivalMonth.equals(departMonth)) {
                driver.findElement(By.xpath("//*[@id='mCSB_4_container']/div[1]/div/div[2]/div[1]/div[1]/button[3]")).click();
                WaitUtil.waitFor(2);
                logger.info("출발월과 도착월이 다릅니다");
            }
            logger.info("ARRIVE DATE :: " + arrivalDate + "일");
            $tbody = driver.findElement(By.cssSelector("#mCSB_4_container > div.plan_area > div > div:nth-child(5) > div.xdsoft_datepicker.active > div.xdsoft_calendar > table > tbody"));
            $tdList = $tbody.findElements(By.tagName("td"));

            for (WebElement $td : $tdList) {
                if (!$td.getAttribute("class").contains("xdsoft_disabled")) {
                    $targetDay = $td.findElement(By.tagName("div")).getText().trim();

                    if (arrivalDate.equals($targetDay)) {
                        logger.info("DATE FOUND :: {}", $targetDay);
                        $td.click();
                        WaitUtil.waitFor(2);
                        break;
                    }
                }
            }
            WaitUtil.loading(2);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        try{
            String productKind = (String) obj[0];
            By by = null;

            if ("순수보장형".equals(productKind)) {
                by = By.cssSelector(".radio_type2:nth-child(1) > span");
            } else if ("만기환급형".equals(productKind)) {
                by = By.cssSelector(".radio_type2:nth-child(2) > span");
            }
            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("상품유형", productKind, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

        try {
            By by = (By) obj[0];
            String vehicleUseYn = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(1);

            // 검증메서드
            checkValue("운전용도", vehicleUseYn, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(exceptionEnum.getMsg() + "\n" + e.getMessage());

        }
    }

    @Override
    public void setTreaties(Object...obj) throws SetTreatyException {

        try{
            List<CrawlingTreaty> welgramTreatyList = (List<CrawlingTreaty>) obj[0];
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>(); // 가입설계 - 원수사 특약 일치여부 확인용
            int cntForNoneJoin = 0; // 가입안한 특약
            int cntDomestic = 0; // 국내 특약

            // 자유설계형 탭 클릭
            driver.findElement(By.cssSelector("#resultTable > thead > tr > th.pgContentFree > div > label")).click();
            WaitUtil.waitFor(2);

            List<WebElement> $trList = driver.findElements(By.cssSelector("#resultTable > tbody > tr"));
            logger.info(" trList SIZE :: " + $trList.size());

            for(WebElement $tr : $trList) {
                String treatyName = $tr.findElement(By.cssSelector("td:nth-child(1)")).getText();
                logger.info("특약명 :: {}", treatyName);

                if(treatyName.contains("국내")) {
                    logger.info("국내({})", ++cntDomestic);
                    WebElement $amtOptEl = $tr.findElement(By.xpath(".//td[5]//select"));
                    Select $amtOptSelect = new Select($amtOptEl);

                    if($amtOptEl.isDisplayed()){
                        $amtOptSelect.selectByVisibleText("가입안함");
                        logger.info("가입안함 처리 :: {}", treatyName);
                        cntForNoneJoin++;
                    } else{
                        logger.info("클릭이 불가능한 로우입니다 :: {}", treatyName);
                    }
                    WaitUtil.loading(1);
                }
            }
            logger.info("가입안함 처리 개수 :: {}", cntForNoneJoin);
            targetTreatyList = getPageTreatyList($trList);

            logger.info("특약 비교 및 확인");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    public List<CrawlingTreaty> getPageTreatyList(List<WebElement> $trList) throws CommonCrawlerException {

        try{
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            String script = "return $(arguments[0]).find('option:selected').text();";
            String treatyName = "";
            String treatyAmt = "";
            WebElement treatyAmtEl = null;

            for(WebElement $tr : $trList){
                CrawlingTreaty targetTreaty = new CrawlingTreaty();

                treatyName = $tr.findElement(By.xpath("./td[1]")).getText().trim();
                treatyAmtEl = $tr.findElement(By.xpath("./td[contains(@class,'on')]//select"));
                treatyAmt = String.valueOf(helper.executeJavascript(script, treatyAmtEl)).replaceAll("(\t\n|\t|\n|\n\t)", "");

                if(treatyAmt.equals("가입안함")){
                    continue;
                } else{
                    treatyAmt = String.valueOf(MoneyUtil.getDigitMoneyFromHangul(treatyAmt));

                    targetTreaty.setTreatyName(treatyName);
                    targetTreaty.setAssureMoney(Integer.parseInt(treatyAmt));
                }
            }

            return targetTreatyList;

        } catch (Exception e){
            throw new CommonCrawlerException("특약 일치여부 체크 중 에러 발생\n" + e.getMessage());
        }

    }



    // 로딩이미지 명시적 대기
    protected void waitLoadingImg() throws Exception{
        helper.waitForCSSElement("body > div.loading");
        WaitUtil.waitFor(1);
    }

    //버튼 클릭
    protected void btnClick(WebElement element) throws  Exception {
        element.click();
        WaitUtil.waitFor(2);
    }

    /*
    * 버튼 클릭 메서드
    * @param1 $element 클릭하고자 하는 요소
    * @param2 sec 대기 시간
    */
    protected void btnClick(WebElement $element, int sec) throws Exception {
        $element.click();
        WaitUtil.waitFor(sec);
    }

    /*
     * 버튼 클릭 메서드
     * @param1 byElement 클릭하고자 하는 요소
     * @param2 sec 대기 시간
     */
    protected void btnClick(By byElement, int sec) throws Exception {
        /** 로딩바 활성화 여부도 선택할지 고려할 것 */
        driver.findElement(byElement).click();
        WaitUtil.waitFor(sec);
    }

    /*
    * 특약 일치여부 확인
    * @param :: CrawlingProduct info
    */
    protected void compareTreaties(CrawlingProduct info) throws CommonCrawlerException{

        try {
            List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
            String homepageTreatyName = ""; // 원수사 홈페이지의 특약 이름
            String homepageTreatyAmt = ""; // 원수사 홈페이지의 특약 금액

            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            List<WebElement> $trList = driver.findElements(By.xpath("//table[@id='resultTable']/tbody/tr"));

            for (WebElement $tr : $trList) {
                homepageTreatyName = $tr.findElements(By.tagName("td")).get(0).getText().trim();
                homepageTreatyAmt = $tr.findElement(By.xpath("./td[contains(@class, 'on')]")).getText().trim();
                homepageTreatyAmt = toDigitMoney(homepageTreatyAmt); // 가입금액 단위변경

                if(homepageTreatyName.equals("만기환급금")){
                    info.setReturnPremium(homepageTreatyAmt);
                    logger.info("만기환급금 :: {}", info.getReturnPremium());
                    continue;
                }

                // 원수사 페이지 표에 모든 특약이 존재하므로 가입 대상이 아닌(금액이 없는) 특약은 세팅하지 않음
                if(!homepageTreatyAmt.equals("")){
                    logger.info("=============================================");
                    logger.info("특약명 :: {}", homepageTreatyName);
                    logger.info("가입금액 :: {}", homepageTreatyAmt);
                    logger.info("=============================================");

                    CrawlingTreaty targetTreaty = new CrawlingTreaty();

                    targetTreaty.setTreatyName(homepageTreatyName);
                    targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyAmt));

                    targetTreatyList.add(targetTreaty);
                }
            }

            // 특약 비교
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }
        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TREATIES_COMPOSIOTION;
            throw new CommonCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 서브플랜 설정
    protected void setPlan(By by, String planName) throws Exception {

        try{
            helper.selectOptionContainsText(driver.findElement(by), planName);
            WaitUtil.waitFor(2);

            checkValue("서브플랜", planName, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }


    // 가입금액을 숫자로 세팅
    protected String toDigitMoney(String homepageTreatyAmt) throws Exception{

        if (homepageTreatyAmt.contains("가입안함")) {
            logger.info("가입안함");

        } else if (homepageTreatyAmt.contains("가입")) {
                logger.info("가입");

        } else if (homepageTreatyAmt.contains(".")) { // 천원단위
            homepageTreatyAmt = homepageTreatyAmt.replace("만", "천");

        } else if (homepageTreatyAmt.contains("당")) { // 1인당, 개당 등
            homepageTreatyAmt = homepageTreatyAmt.substring(homepageTreatyAmt.indexOf("당"));

        } else if (homepageTreatyAmt.contains("회")) {// n원 연간 n회
            homepageTreatyAmt = homepageTreatyAmt.substring(0, homepageTreatyAmt.indexOf("원"));

        } else if (homepageTreatyAmt.contains("억")) {
            homepageTreatyAmt = homepageTreatyAmt.substring(0, homepageTreatyAmt.indexOf("억") + 1);
        }

        homepageTreatyAmt = homepageTreatyAmt
            .replace("백", "00")
            .replace("천", "000")
            .replace("만", "0000")
            .replace("억", "00000000")
            .replaceAll("[^0-9]", "");

        return homepageTreatyAmt;
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
            String script = "return $(arguments[0]).find('option:selected').text();";

            if(selectedElement.getTagName().equals("select")){
                selectedValue = String.valueOf(helper.executeJavascript(script,selectedElement));
            } else{
                selectedValue = selectedElement.getText().trim();

                if(selectedValue.equals("")){
                    script = "return $(arguments[0]).val();";
                    selectedValue = String.valueOf(helper.executeJavascript(script, selectedElement));
                }
            }

//            logger.info("selected value :: {}", selectedValue);
            printLogAndCompare(title, expectedValue, selectedValue);

        } catch (Exception e){
            throw new CommonCrawlerException("선택값 체크 중 오류가 발생했습니다.\n" + e.getMessage());
        }
    }

}