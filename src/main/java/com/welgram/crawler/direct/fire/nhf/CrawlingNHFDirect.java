package com.welgram.crawler.direct.fire.nhf;


import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.NotFoundPlanTypeException;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingNHFDirect extends CrawlingNHFNew {

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try{
            By by = (By) obj[0];
            String birthDay = (String) obj[1];

            helper.waitVisibilityOfElementLocated(by);
            helper.waitElementToBeClickable(driver.findElement(by)).sendKeys(birthDay);
            WaitUtil.waitFor(1);

            // 검증
            checkValue("생년월일", birthDay, by);

        } catch(Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try {
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

        try {
            By by = By.id("popCmm0001_jobNm");

            driver.findElement(By.id("jobNm")).click();
            WaitUtil.waitFor(2);

            logger.info("교사 입력");
            wait.until(ExpectedConditions.elementToBeClickable(by));
            helper.sendKeys4_check(by, "교사");

            driver.findElement(By.id("popJobSchBtn")).click();
            WaitUtil.waitFor(2);

            logger.info("중·고등학교 교사 선택");
            driver.findElement(By.xpath("//*[@id='jobNmList1']//span[contains(., '중·고등학교 교사')]")).click();
            WaitUtil.waitFor(2);

            logger.info("직업 입력 버튼 클릭");
            driver.findElement(By.xpath("//span[text()='직업 입력']")).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("직업", "중·고등학교 교사", By.id("jobNm"));

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try{
            By by = (By) obj[0];
            String term = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            if (helper.isAlertShowed()) {
                logger.info("보험기간 변경시 보험료 다시 조회 알럿");
                Alert alert = driver.switchTo().alert();
                WaitUtil.loading(1);

                alert.accept();
                waitHomepageLoadingImg();
                WaitUtil.loading(1);
            }

            // 검증
            String selectedValue = driver.findElement(by).getText();
            if(selectedValue.contains("\n")){
                selectedValue = selectedValue.substring(selectedValue.indexOf("\n")).trim();
            }
            logger.info("selected Value :: {}", selectedValue);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try{
            By by = (By) obj[0];
            String term = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            if (helper.isAlertShowed()) {
                logger.info("보험기간 변경시 보험료 다시 조회 알럿");
                Alert alert = driver.switchTo().alert();
                WaitUtil.loading(1);

                alert.accept();
                waitHomepageLoadingImg();
                WaitUtil.loading(1);
            }

            // 검증
            checkValue("납입기간", term, by);

        } catch(Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입주기";
        By by = (By) obj[0];
        String welgramNapCycle = (String) obj[1];

        if (!welgramNapCycle.equals("월납")) {
            logger.info("납입주기가 월납이 아닌 다른 주기가 입력되었습니다. 입력 후 코드 수정해주세요.");
            throw new SetNapCycleException();
        }

        try {
            //납입기간 클릭
            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            if (helper.isAlertShowed()) {
                logger.info("납입주기 변경시 보험료 다시 조회 알럿");
                Alert alert = driver.switchTo().alert();
                WaitUtil.loading(1);

                alert.accept();
                waitHomepageLoadingImg();
                WaitUtil.loading(3);
            }

            // 검증
            checkValue("납입주기", welgramNapCycle, By.xpath("//ul[@id='pdtRvcyListArea']/li[@class[contains(., 'on')]]//span[@class='txt']"));


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }

    }

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

    // 월 보험료
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        By by = (By) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];
        //활성화된 td 클래스명
        String monthlyPremium = driver.findElement(by).getText().replaceAll("[^0-9]", "");

        info.treatyList.get(0).monthlyPremium = monthlyPremium;
        logger.info("월 보험료 :: {}", info.treatyList.get(0).monthlyPremium);
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try{
            By by = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            driver.findElement(By.xpath("//span[text()='해약환급금']")).click();
            WaitUtil.waitFor(3);
            waitHomepageLoadingImg();

            // 경과기간 (만기시점)
//            String maturityYear = getMaturityYear(info);

            List<WebElement> $returnTrList = driver.findElements(by);
            List<PlanReturnMoney> prmList = new ArrayList<>();

            for(WebElement $tr : $returnTrList) {
                String term = $tr.findElement(By.xpath("./td[1]")).getText();
                String premiumSum = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = $tr.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
                String returnRate =$tr.findElement(By.xpath("./td[5]")).getText();

                PlanReturnMoney prm = new PlanReturnMoney();

                prm.setTerm(term);
                prm.setPremiumSum(premiumSum);
                prm.setReturnMoney(returnMoney);
                prm.setReturnRate(returnRate);

                prmList.add(prm);
                info.returnPremium = returnMoney;
                // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                  info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                }

                logger.info("=====================================");
                logger.info("기간 :: {}", term);
                logger.info("납입보험료 :: {}", premiumSum);
                logger.info("해약환급금 :: {}", returnMoney);
                logger.info("해약환급률 :: {}", returnRate);

            }
            logger.info("=====================================");
            // 만기환급금 크롤링이 불가한 경우
//            if(info.getProductCode().contains("TRM")) {
//              logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//            } else if(info.getReturnPremium().equals("")){
//              info.setReturnPremium("-1");
//            }
            info.setPlanReturnMoneyList(prmList);
            logger.info("====  setPlanReturnMoneyList : {}", info.getPlanReturnMoneyList());

        }catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
            throw new ReturnMoneyListCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
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
            String departureDate = plusDateBasedOnToday(7);
            String arrivalDate = plusDateBasedOnToday(13);

            logger.info("여행시작일시 선택");
            driver.findElement(By.xpath("//div[@class='inp_txt date']//button[contains(., '출발')]")).click();
            WaitUtil.waitFor(2);

            logger.info("출발일 선택");
            selectDay(departureDate);

            logger.info("출발시간 선택");
            helper.selectByText_check(By.id("oInsOpenTime"), "00시");

            driver.findElement(By.xpath("//div[@class='inp_txt date']//button[contains(., '도착')]")).click();
            WaitUtil.waitFor(2);

            logger.info("도착일 선택");
            selectDay(arrivalDate);

            logger.info("도착시간 선택");
            helper.selectByText_check(By.id("oInsEndTime"), "24시");

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

        try{
            By by = (By) obj[0];
            String vehicleUse = (String) obj[1];

            driver.findElement(by).click();
            WaitUtil.waitFor(1);

            // 검증
            checkValue("운전 여부", vehicleUse, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    public void setTreaties(By by, CrawlingProduct info) throws SetTreatyException {

        String welgramPlanType = info.getTextType();
        List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();

        try{
            // 하단 고정 nav바 높이 구하기
            int height = getBottomNavHeight(By.cssSelector("#contents > div.btmNav"));
            //특약 그룹 펼침 버튼 모두 펼치기
            List<WebElement> $aList = driver.findElements(by);

            for (WebElement $a : $aList) {

                /*
                 * 펼침 버튼이 보이도록 스크롤 이동
                 * 단, scrollIntoView(true)를 통해 element를 상단에 맞춰 스크롤할 경우 위에 헤더바와 고정영역 nav?에 가려져 클릭이 안되므로
                 * scrollIntoView(false)를 통해 element를 하단에 맞춰 스크롤한다. 하지만 이래도 하단의 nav바에 가려져 클릭이 안되는데,
                 * 하단의 nav바 높이를 구해 그만큼 스크롤을 하단으로 이동시킨다.
                 *
                 * */

                //펼침 버튼이 보이도록 element를 하단에 맞춰 스크롤 이동
                helper.executeJavascript("arguments[0].scrollIntoView(false);", $a);

                //펼침 버튼이 보이게 스크롤 이동했어도, 하단 고정 nav바에 가려져 클릭이 안되므로 nav바 높이만큼 스크롤 하단으로 이동
                helper.executeJavascript("window.scrollBy(0, " + height + ")");

                //펼침 버튼 클릭
                $a.click();
                WaitUtil.waitFor(2);
            }

            //플랜 유형에 따라 내가 특약을 직접 세팅해야하는 경우가 있고, 고정인 경우가 있다.
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            CrawlingTreaty targetTreaty = null;

            boolean toSetTreaty = "자유설계".equals(welgramPlanType);

            if (toSetTreaty) {
                //자유설계의 경우( = 내가 직접 특약을 선택함)
                //가입설계 특약들만 가입처리(자유설계인 경우만)
                for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
                    String welgramTreatyName = welgramTreaty.getTreatyName();
                    String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());

                    //특약 한줄에서 필요한 요소 찾기
                    WebElement $label = driver.findElement(By.xpath(
                        "//ul[@id='barGrpArea']/li[@class[contains(., 'active')]]//ul[@class='rowInner accArea']/li//label[text()='"
                            + welgramTreatyName + "']"));
                    WebElement $li = $label.findElement(By.xpath("./ancestor::li[1]"));
                    WebElement $input = $li.findElement(By.xpath(".//input[@name='cvgChk']"));
                    WebElement $a = $li.findElement(By.xpath(".//a[@class[contains(., 'custom')]]"));
                    WebElement $popUp = null;
                    boolean isPopUpShow = false;

                    //특약이 보이도록 스크롤 이동
                    helper.executeJavascript("arguments[0].scrollIntoView(false)", $label);
                    helper.executeJavascript("window.scrollBy(0, " + height + ")");

                    //특약 체크박스 처리
                    if (!$input.isSelected()) {
                        //특약 체크박스 클릭
                        $label.click();
                        WaitUtil.waitFor(2);

                        //특약을 클릭하다가 popup이 뜰 수 있음
                        $popUp = driver.findElement(By.xpath("//*[@id='chkCvgRlpRlePop']//div"));
                        isPopUpShow = $popUp.getAttribute("class").contains("active");

                        //팝업창이 뜬 경우에 확인 버튼 클릭
                        checkPopup(By.xpath("//div[@id='okSelect']/a"));
                    }

                    //특약 가입금액 펼침 버튼 클릭
                    $a.click();
                    WaitUtil.waitFor(2);

                    //가입금액 선택
                    List<WebElement> $liList = $li.findElements(By.xpath(".//ul[@class='stepList between']/li"));
                    for (WebElement li : $liList) {
                        String targetAssureMoney = li.findElement(By.xpath("./a/span[1]")).getText();
                        targetAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetAssureMoney));

                        //가입금액 클릭
                        if (welgramTreatyAssureMoney.equals(targetAssureMoney)) {
                            $a = li.findElement(By.xpath("./a"));

                            helper.executeJavascript("arguments[0].scrollIntoView(false)", $a);
                            helper.executeJavascript("window.scrollBy(0, " + height + ")");
                            $a.click();
                            WaitUtil.waitFor(2);
                        }
                    }

                    //가입금액을 클릭하다가 popup이 뜰 수 있음
                    $popUp = driver.findElement(By.id("chkCvgRlpRlePop"));
                    isPopUpShow = $popUp.getAttribute("class").contains("active");

                    //팝업창이 뜬 경우에 확인 버튼 클릭
                    checkPopup(By.xpath("//div[@id='okSelect']/a"));

                    logger.info("특약명 : {} | 가입금액 : {} 처리 완료", welgramTreatyName, welgramTreatyAssureMoney);
                }

                //원수사에 실제 체크된 특약 정보만 크롤링
                List<WebElement> $inputs = driver.findElements(By.cssSelector("input[name=cvgChk]:checked"));
                targetTreatyList = checkedTreaties($inputs);

            } else {
                //특약이 고정인 경우
                List<WebElement> $liList = driver.findElements(By.xpath(
                    "//ul[@id='barGrpArea']/li[@class[contains(., 'active')]]//ul[@class='rowInner accArea']/li"));

                for (WebElement $li : $liList) {
                    WebElement $label = $li.findElement(By.xpath(".//div[@class[contains(., 'title')]]//label"));
                    WebElement $span = $li.findElement(By.xpath(".//span[@class[contains(., 'custom')]]/span[@class='price']"));

                    String targetTreatyName = $label.getText().trim();
                    String targetTreatyAssureMoney = $span.getText().trim();
                    targetTreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));

                    try {
                        if (Integer.parseInt(targetTreatyAssureMoney) != 0) {
                            targetTreaty = new CrawlingTreaty();
                            targetTreaty.treatyName = targetTreatyName;
                            targetTreaty.assureMoney = Integer.parseInt(targetTreatyAssureMoney);

                            logger.info("=============================================");
                            logger.info("특약명 :: {}", targetTreatyName);
                            logger.info("가입금액 :: {}", targetTreatyAssureMoney);
                            logger.info("=============================================");

                            targetTreatyList.add(targetTreaty);
                        }

                    } catch (NumberFormatException e) {
                        logger.info("특약명 : {} , 가입금액 : {}", targetTreatyName, targetTreatyAssureMoney);
                    }
                }

            }

            //가입설계 특약정보와 원수사 특약정보 비교
            logger.info("가입하는 특약은 총 {}개입니다.", targetTreatyList.size());
            logger.info("===========================================================");
            logger.info("특약 비교 및 확인");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }

        }catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    //원수사에 실제 체크된 특약 정보만 크롤링
    protected List<CrawlingTreaty> checkedTreaties(List<WebElement> checkedTreatyList) throws CommonCrawlerException{

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
        CrawlingTreaty targetTreaty = new CrawlingTreaty();

        for(WebElement checkedTreaty : checkedTreatyList){

            String targetTreatyName = checkedTreaty.findElement(By.xpath("./parent::div/label")).getText();
            String targetTreatyAssureMoney = checkedTreaty.findElement(By.xpath("./ancestor::li[1]//a/span[@class='price']")).getText();
            targetTreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney));

            targetTreaty = new CrawlingTreaty();
            targetTreaty.treatyName = targetTreatyName;
            targetTreaty.assureMoney = Integer.parseInt(targetTreatyAssureMoney);

            targetTreatyList.add(targetTreaty);

            logger.info("===========================================================");
            logger.info("특약명 :: {}", targetTreatyName);
            logger.info("가입금액 :: {}", targetTreatyAssureMoney);
            logger.info("===========================================================");
        }
        return targetTreatyList;
    }

    // 팝업창이 뜬 경우에 확인 버튼 클릭
    protected void checkPopup(By by) throws Exception {

        boolean isPopUpShow = false;
        isPopUpShow = driver.findElement(by).isDisplayed();

        if (isPopUpShow) {
            logger.info("팝업 발생");
            driver.findElement(by).click();
            WaitUtil.waitFor(2);
        }
    }


    //버튼 클릭
    protected void btnClick(WebElement element) throws  Exception {
        element.click();
        WaitUtil.waitFor(1);

    }

    /*
     * 버튼 클릭 메서드
     * @param1 by 클릭하고자 하는 요소
     * @param2 sec 대기 시간
     */
    protected void btnClick(By by, int sec) throws Exception {
        WebElement $element = driver.findElement(by);

        $element.click();
        WaitUtil.waitFor(sec);
    }

    // 달력에서 날짜 클릭하기
    protected void selectDay(String day) throws Exception {

        String year = day.substring(0,4);
        String month = day.substring(4,6);
        String date = day.substring(6);
        Select $select = null;

        helper.selectByText_check(By.xpath("//select[@class='ui-datepicker-year']"), year);

        month = month.charAt(0) == '0' ? String.valueOf(month.charAt(1)) : month;
        $select = new Select(helper.waitPresenceOfElementLocated(By.xpath("//select[@class='ui-datepicker-month']")));
        $select.selectByVisibleText(month+"월");
        logger.info(month+"월");

        date = date.charAt(0) == '0' ? String.valueOf(date.charAt(1)) : date;
        driver.findElement(By.xpath("//table[@class='ui-datepicker-calendar']//tbody//a[text()='"+date+"']")).click();
        WaitUtil.waitFor(2);
    }

    protected String plusDateBasedOnToday(int day) throws Exception {

        try {
            String date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = Calendar.getInstance();

            cal.setTime(new Date());
            cal.add(Calendar.DATE, day);
            date = sdf.format(cal.getTime());

            return date;

        } catch(Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_TRAVEL_PERIOD;
            throw new SetTravelPeriodException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    //홈페이지용 플랜 설정 메서드
    protected void setPlanType(By by, String planSubName) throws Exception{

        String title = "플랜유형";
        String welgramPlanType = planSubName;
        String targetPlanType = ""; //

        try {
            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            // 검증
            checkValue("플랜유형", welgramPlanType, By.xpath("//div[@id='planListArea']/li[@class[contains(., 'active')]]/a/strong"));

        } catch(NoSuchElementException e) {
            throw new NotFoundPlanTypeException("플랜(" + planSubName + ")을 찾을 수 없습니다.\n" + e.getMessage());
        }
    }

    protected void waitHomepageLoadingImg() throws Exception {

        try{
            logger.info("loading image");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"dirLoding_box\"]/div/img")));
        } catch (Exception e){
            logger.info("loading 없음");
        }
    }

    protected void printAndCompare(String title, String welgramData, String targetData)
        throws Exception {

        //가입설계 정보와 원수사 정보 출력
        logger.info("======================================================");
        logger.info("가입설계 {} : {}", title, welgramData);
        logger.info("홈페이지 {} : {}", title, targetData);
        logger.info("======================================================");

        if (!welgramData.equals(targetData)) {
            throw new Exception(title + " 불일치");
        }
    }

    protected int getBottomNavHeight(By by) throws Exception {

        // 하단 고정 nav바 높이 구하기
        element = driver.findElement(by);
        int height = element.getSize().getHeight();

        return height;
    }

    public void chkSecurityProgram() throws CommonCrawlerException {

        By by = By.xpath("//div[@class='installPC_wrap']");

        if (helper.existElement(by) && driver.findElement(by).isDisplayed()) {
            throw new CommonCrawlerException("보안프로그램 설치를 확인하세요.");
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
            String script = "return $(arguments[0]).find('option:selected').text();";

            if(selectedElement.getTagName().equals("select")){
                selectedValue = String.valueOf(helper.executeJavascript(script,selectedElement));
            } else{
                selectedValue = selectedElement.getText().trim();

                if(selectedValue.equals("")){
                    script = "return $(arguments[0]).val();";

                    selectedValue = String.valueOf(helper.executeJavascript(script, selectedElement));
                }

                if(title.equals("성별") && selectedValue.matches("[\\d]")){
                    selectedValue = selectedValue.equals("1") ? "남" : "여";
                }

                if(selectedValue.contains("\n")){
                    selectedValue = selectedValue.substring(0, selectedValue.indexOf("\n"));
                }
            }
            printLogAndCompare(title, expectedValue, selectedValue);

        } catch (Exception e){
            throw new CommonCrawlerException("선택값 검증 중 오류가 발생했습니다.\n" + e.getMessage());
        }
    }

}