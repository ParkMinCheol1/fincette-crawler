package com.welgram.crawler.direct.fire.nhf;


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
import com.welgram.util.InsuranceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public abstract class CrawlingNHFAnnounce extends CrawlingNHFNew {

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

        try {
            By by = (By) obj[0];
            String gender = (String) obj[1];

            driver.findElement(by).click();

            // 검증
            checkValue("성별", gender, by);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {}

    @Override
    public void setJob(Object... obj) throws SetJobException {

        try {
            logger.info("직업 검색");
            driver.findElement(By.id("btnSchJob")).click();

            helper.sendKeys4_check(helper.waitElementToBeClickable(By.id("searchNm")), "사무원");
            driver.findElement(By.cssSelector(".tal span a")).click();
            WaitUtil.waitFor(1);

            driver.findElement(By.linkText("보험 사무원")).click();
            WaitUtil.waitFor(1);

            // 검증
            checkValue("직업", "보험 사무원", By.xpath("//div[@id='jobNm']"));

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try {
            By by = (By) obj[0];
            String insTerm = obj[1] + "만기";

            helper.selectByText_check(driver.findElement(by), insTerm);
            WaitUtil.waitFor(1);

            // 검증
            checkValue("보험기간", insTerm, by);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        try {
            By by = (By) obj[0];
            String napTerm = obj[1] + "납";

            helper.selectByText_check(driver.findElement(by), napTerm);
            WaitUtil.waitFor(1);

            // 검증
            checkValue("납입기간", napTerm, by);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        try {
            By by = (By) obj[0];
            String napCycle = (String) obj[1];

            String napCycleText = getNapCycleName(napCycle);
            helper.selectOptionContainsText(driver.findElement(by), napCycleText); // todo contains 안쓸 수 있는지 검토
            WaitUtil.waitFor(1);

            // 검증
            checkValue("납입주기", napCycleText, by);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(exceptionEnum.getMsg());
        }
    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {}

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        try {
            WebElement $selectEl = (WebElement) obj[0];
            int assureMoney = (int) obj[1];

            int unit = Integer.parseInt($selectEl.getAttribute("data-unit"));
            String value = String.valueOf(assureMoney / unit).trim();
            $selectEl.findElement(By.cssSelector("option[value='" + value + "']")).click();

            // 검증 : 특약비교 메서드에서 검증

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {}

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String savePremium = "";
            String monlyMoney = "";
            String labelText = ""; // 보험료 이름
            String fetalPremium = ""; // 출생후보험료

            savePremium = driver.findElement(By.id("result_money_2")).getAttribute("value");
            info.savePremium = savePremium.replaceAll("[^0-9]", "");
            logger.info("적립보험료 ::  {}", info.savePremium);

            monlyMoney = driver.findElement(By.id("result_money_3")).getAttribute("value").replaceAll("[^0-9]", "");

            // 태아보험
            if(info.getProductCode().contains("BAB")){
                // 보험료이름 // 납입보험료 or 출생후보험료
                labelText = driver.findElement(By.xpath("//label[@for='result_money_4']")).getText().trim();
                logger.info("보험료 이름 : {}", labelText);

                info.treatyList.get(0).monthlyPremium = monlyMoney;
                logger.info("초회보험료 ::  {}", info.treatyList.get(0).monthlyPremium);

                // 태아보험료 : 출생 전/후 보험료 구분이 없는 경우 초회 월보험료를 info.nextMoney에도 넣어준다.
                if(labelText.contains("출생후보험료")){
                    fetalPremium = driver.findElement(By.name("017_fetlSumPrem")).getAttribute("value");
                    info.nextMoney = fetalPremium;
                } else{
                    info.nextMoney = monlyMoney;
                }
                logger.info("계속보험료 ::  {}", info.nextMoney);

            } else{
                info.treatyList.get(0).monthlyPremium = monlyMoney;
                logger.info("보장보험료 ::  {}", info.treatyList.get(0).monthlyPremium);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 보험가입 불가 안내창에서 최소 가입 합계보험료를 얻어온다
    protected void setAndCrawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String errMsgSplit = "";
            boolean lessThan = false;

            // 얼럿에서 보험료 받아오기
            String errMsg = getMessageFromAlert();

            if (errMsg.contains("최소 납입보험료")) {
                errMsgSplit = errMsg.substring(errMsg.indexOf("최소 납입보험료"), (errMsg.indexOf(")")));

            } else if (errMsg.contains("최소보험료")) {
                errMsgSplit = errMsg.substring(errMsg.indexOf("최소보험료"), (errMsg.indexOf("미만")));
                lessThan = true;

            } else if (errMsg.contains("최저보험료")) {
                errMsgSplit = errMsg.substring(errMsg.indexOf("최저보험료"), (errMsg.indexOf("이상")));
                lessThan = true;

            } else if (errMsg.contains("보장보험료")) {
                errMsgSplit = errMsg.substring(errMsg.indexOf("1회보험료") + 1, (errMsg.indexOf("이상")));

            } else {
                throw new Exception();
            }

            //팝업의 최소 보험료
            String minPremium = errMsgSplit.replaceAll("[^0-9]", "");

            //입력할 월보험료
            int monlyPremium = 0;

            //2만원 미만일 경우
            if (lessThan) {
                monlyPremium = Integer.parseInt(minPremium);
            } else {
                //10원 단위로 짜르기
                String end = minPremium.substring(minPremium.length() - 1);
                if (!end.equals("0")) {
                    int plusMoney = 10 - Integer.parseInt(end);
                    monlyPremium = Integer.parseInt(minPremium) + plusMoney;
                } else {
                    //10원더하기
                    monlyPremium = Integer.parseInt(minPremium) + 10;
                }
            }
            driver.switchTo().alert().accept();
            WaitUtil.waitFor(1);

            final String format = DecimalFormat.getInstance().format(monlyPremium);
            logger.info("얻어온 합계보험료를 세팅한다 :: {}", format);

            helper.sendKeys4_check(By.id("result_money_4"), format);
//            helper.sendKeys4_check(By.id("result_money_4"), String.valueOf(monlyPremium));

            logger.info("계산 버튼 클릭");
            calcBtnClickforPremium(); // 보험료 확인버튼
            driver.switchTo().alert().accept();
            WaitUtil.waitFor(10);
            if(!helper.isAlertShowed()){
                WaitUtil.waitFor(3);
            }
            driver.switchTo().alert().accept();

            // 적립보험료 & 보장보험료 가져오기
            crawlPremium(info);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            By by = (By) obj[0];
            CrawlingProduct info = (CrawlingProduct) obj[1];

            driver.findElement(By.linkText("해약환급금")).click();

            helper.waitVisibilityOfAllElementsLocatedBy(by);
            List<WebElement> $trList = driver.findElements(by);

            if($trList.size() == 0){
                boolean isExist = helper.existElement(By.xpath("//table[@id=\"HykRetTable\"]//td[contains(.,'해약환급금이 존재하지 않습니다.')]"));
                if(isExist){
                    logger.info("해약환급금이 존재하지 않습니다.");
                } else {
                    throw new ReturnMoneyListCrawlerException("해약환급금 표를 찾지 못했습니다.");
                }
            } else {
                setPlanReturnMoney(info, $trList);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_PREMIUM;
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

    // 출산예정일
    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {

        try {
            By by = By.id("cbirPlaDt");
            //오늘 날짜로부터 12주후가 출산예정일이 된다.
            String dueDate = InsuranceUtil.getDateOfBirth(12);

            helper.waitElementToBeClickable(by);
            helper.sendKeys4_check(by, dueDate);

            // 검증
            checkValue("출산예정일", dueDate, by);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_DUEDATE;
            throw new SetDueDateException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {}

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        try {
            By by = (By) obj[0];
            String textType = (String) obj[1];

            helper.waitElementToBeClickable(by);
            helper.selectOptionContainsText(driver.findElement(by), textType);
            WaitUtil.waitFor(2);

            // 검증 :: selectOptionContainsText에서 선택 값을 로그로 남겨주는데 중복해서 출력할 필요는 없을 듯

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}

    // 태아가입여부
    public void setFetalStatus(By by, String status) throws CommonCrawlerException {

        try {
            helper.selectByText_check(by, status);

            // 검증
            checkValue("태아가입여부", status, by);

        } catch (Exception e) {
            throw new CommonCrawlerException("태아가입여부가 잘못되었습니다\n" + e.getMessage());
        }
    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

        try {
            String text = (String) obj[0];
            String driveOption = (text.equals("운전")) ? "04" : "02";
            By by = By.xpath("//*[@id='drvRadioBtn']//input[@value='" + driveOption + "']");
            String selectedVal = "";

            driver.findElement(by).click();
            WaitUtil.waitFor(2);

            // 검증
            selectedVal = driver.findElement(by).getAttribute("value");
            if (!selectedVal.equals(driveOption)) {
                logger.info("운전코드 선택 :: {}", selectedVal);
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_VEHICLE;
            throw new SetVehicleException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    // 특약 설정 메서드
    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        try{
            // 원수사 홈페이지 특약세팅
            List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
            setHomepageTreaties(welgramTreatyList);

            // 홈페이지에서 선택된 특약리스트
            List<WebElement> checkedTreatyList = null;
            // 태아보험의 경우 담보선택 탭이 존재
            if(info.getProductCode().contains("BAB")){
                checkedTreatyList = getTabTreatyList();
            }

            if(checkedTreatyList == null){
                checkedTreatyList = driver.findElements(By.cssSelector("tr:not([style*=\"display: none;\"]) input[name=cvgCd]:checked"));
            }
            List<CrawlingTreaty> targetTreatyList = getCheckedTreatyList(checkedTreatyList, welgramTreatyList);

            logger.info("특약 비교 및 확인");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), e.getMessage());
        }

    }

    protected void setHomepageTreaties(List<CrawlingTreaty> welgramTreatyList) throws CommonCrawlerException {

            for (CrawlingTreaty welgramTreaty : welgramTreatyList) {
                String welgramTreatyName = welgramTreaty.getTreatyName().trim();
                int welgramTreatyMoney = welgramTreaty.getAssureMoney();

                try{
                    WebElement $treatyNameTd = driver.findElement(By.xpath("//div[@class='contents']//tr[@id='HmbdHm' or @id='IdmnRspb'][not(@style[contains(.,'display: none;')])]//td[normalize-space()='" + welgramTreatyName + "']"));
                    helper.executeJavascript("arguments[0].scrollIntoView(true);", $treatyNameTd);
//                    WaitUtil.waitFor(1);
                    WaitUtil.waitForMsec(500);


                    WebElement $tr = $treatyNameTd.findElement(By.xpath("./parent::tr"));
                    WebElement checkBox = $tr.findElement(By.xpath("./td[1]/input[@id='cvgCd']"));
                    WebElement $treatyMoneyTd = $tr.findElement(By.xpath("./td[3]"));

                    if (checkBox.isEnabled() && !checkBox.isSelected()) {
                        logger.info("-----------------------------------");
                        helper.waitElementToBeClickable(checkBox);
                        helper.executeJavascript("arguments[0].click();", checkBox);
//                        WaitUtil.waitFor(1);
                        WaitUtil.waitForMsec(500);
                        logger.info("{} :: check", welgramTreatyName);

                        if (helper.isAlertShowed()) {
                            Alert alert = driver.switchTo().alert();
                            alert.accept();
                            WaitUtil.waitFor(1);
                        }
                    }

                    // 가입금액 세팅
                    try{
                        logger.info("가입금액 설정 :: {}", welgramTreatyMoney);
                        WebElement $homepageTreatyMoneyElement = $treatyMoneyTd.findElement(By.xpath(".//*[name()='input' or name()='select'][not(@style[contains(., 'display: none;')])]"));
                        helper.waitElementToBeClickable($homepageTreatyMoneyElement);

                        if ("input".equals($homepageTreatyMoneyElement.getTagName())) {
                            welgramTreatyMoney = welgramTreatyMoney / 10000;
                            helper.sendKeys4_check($homepageTreatyMoneyElement, String.valueOf(welgramTreatyMoney));

                        } else if ("select".equals($homepageTreatyMoneyElement.getTagName())) {
                            WebElement aMoneySelectEl = $treatyMoneyTd.findElement(By.tagName("select"));
                            if (aMoneySelectEl.isDisplayed()) {
                                setAssureMoney(aMoneySelectEl, welgramTreatyMoney);    //특약 가입금액 설정
                            }
                        }

                    } catch (Exception e) {
                        welgramTreatyMoney = welgramTreatyMoney / 10000;
                        String $homepageTreatyMoney = $treatyMoneyTd.getText().replaceAll("[^0-9]", "");
                    }


                } catch (Exception e){
                    throw new CommonCrawlerException("특약 설정이 잘못되었습니다\n" + e.getMessage());
                }
            }
    }

    // 원수사 페이지에서 선택된 특약리스트
    protected List<CrawlingTreaty> getCheckedTreatyList(List<WebElement> checkedTreatyList, List<CrawlingTreaty> welgramTreatyList) throws Exception {

        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        for (WebElement checkedTreaty : checkedTreatyList) {
            String checkedTreatyName = "";
            String checkedTreatyMoney = "";
            WebElement $tr = checkedTreaty.findElement(By.xpath("./ancestor::tr"));

            // 체크된 특약명
            checkedTreatyName = $tr.findElement(By.xpath("./td[2]")).getAttribute("innerHTML").trim();

            WebElement $treatyMoneyElement = null;
            WebElement $treatyMoneyTd = $tr.findElement(By.xpath("./td[3]"));

            // 가입금액 엘리먼트
            try {
                $treatyMoneyElement = $treatyMoneyTd.findElement(By.xpath(".//*[name()='input' or name()='select'][not(@style[contains(., 'display: none;')])]"));
            } catch (Exception e) {
                $treatyMoneyElement = $treatyMoneyTd;
            }

            // 체크된 특약 가입금액 가져오기
            checkedTreatyMoney = getCheckedTreatyMoney($treatyMoneyElement);

            logger.info("=========================================================");
            logger.info("특약명 : {}", checkedTreatyName);
            logger.info("가입금액 : {}", checkedTreatyMoney);
            logger.info("=========================================================");

            CrawlingTreaty targetTreaty = new CrawlingTreaty();

            targetTreaty.setTreatyName(checkedTreatyName);
            targetTreaty.setAssureMoney(Integer.parseInt(checkedTreatyMoney));

            targetTreatyList.add(targetTreaty);
        }

        return targetTreatyList;
    }

    protected String getCheckedTreatyMoney(WebElement $treatyMoneyElement) throws Exception {

        String targetTreatyMoney = "";
        String script = "return $(arguments[0]).find('option:selected').text();";

        if ("select".equals($treatyMoneyElement.getTagName())) {
            //실제 홈페이지에서 클릭된 select option 값 조회
            targetTreatyMoney = String.valueOf(helper.executeJavascript(script, $treatyMoneyElement));

        } else if ("input".equals($treatyMoneyElement.getTagName())) {
            script = "return $(arguments[0]).val();";
            targetTreatyMoney = String.valueOf(helper.executeJavascript(script, $treatyMoneyElement));
            targetTreatyMoney = targetTreatyMoney + "0000";

        } else {
            targetTreatyMoney = $treatyMoneyElement.getText().trim();
        }
        targetTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney(targetTreatyMoney));

        return targetTreatyMoney;
    }

    // 담보 탭에 체크된 특약리스트 선택
    protected List<WebElement> getTabTreatyList() throws CommonCrawlerException {

        List<WebElement> checkedTreatyList = null;

        try {
            String guaranteeName = driver.findElement(By.xpath("//li[contains(@id,'IsrdTab')][@class='on']")).getText().trim();
            WebElement $tabNameEl = driver.findElement(By.xpath("//h5[@class='popupSubTitle'][normalize-space()='" + guaranteeName + "']"));
            WebElement $table = $tabNameEl.findElement(By.xpath(".//parent::div//table"));

            checkedTreatyList = $table.findElements(By.cssSelector("tr:not([style*=\"display: none;\"]) input[name=cvgCd]:checked"));

        } catch (Exception e) {
            logger.info("담보 탭이 없는 상품입니다.\n" + e.getMessage());
        }

        return checkedTreatyList;
    }

    /*
     * 버튼 클릭 메서드(By로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(By element, int sec) throws Exception {
        driver.findElement(element).click();
        WaitUtil.loading(sec);
        if(helper.isAlertShowed()){
            waitLoadingImg();
        }
    }

    /*
     * 버튼 클릭 메서드(WebElement로)
     * @param element : 클릭할 element
     * */
    protected void btnClick(WebElement element, int sec) throws Exception {
        element.click();
        WaitUtil.loading(sec);
        if(helper.isAlertShowed()){
            waitLoadingImg();
        }
    }


    //로딩이미지 명시적 대기
    protected void waitLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader_image")));
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

    //보험료 확인 버튼 클릭 메서드
    protected void calcBtnClick() throws Exception {

        driver.findElement(By.linkText("보험료확인")).click();

        //몇 개의 알럿창이 뜨든 기다렸다가 확인버튼 클릭!
        boolean isShowed = helper.isAlertShowed();
        while (isShowed) {
            driver.switchTo().alert().accept();
            WaitUtil.loading(6);
            isShowed = helper.isAlertShowed();
        }
    }

    //보험료 확인 버튼 클릭 메서드
    protected void calcBtnClickforPremium() throws Exception {

        driver.findElement(By.linkText("보험료확인")).click();
        WaitUtil.waitFor(2);
    }

    // 보험가입 불가 안내창에서 최소 가입 합계보험료를 얻어온다
    protected String getMessageFromAlert(Object... obj) throws Exception {

        logger.info("일단 1원을 세팅한다");
        helper.sendKeys4_check(By.id("result_money_4"), "1");

        calcBtnClickforPremium(); // 보험료 확인버튼
        driver.switchTo().alert().accept();
        try {
            WaitUtil.waitFor(6);
        } catch (TimeoutException e) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader_image")));
        }

        logger.info("보험가입 불가 안내창에서 최소 가입 합계보험료를 얻어온다.");
        WaitUtil.waitFor(10);

        String errMsg = driver.switchTo().alert().getText();

        return errMsg;
    }

    // 특약기간 설정 메서드
    protected void setTreatyTerm(String treatyTerm) throws Exception {
        helper.selectByText_check(By.id("spcNabPrd"), treatyTerm);

        // 검증
        checkValue("특약기간", treatyTerm, By.id("spcNabPrd"));
    }

    protected void setPlanReturnMoney(CrawlingProduct info, List<WebElement> $trList) throws CommonCrawlerException {

        try{
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
            // 경과기간 (만기시점)
            String maturityYear = getMaturityYear(info);

            for (WebElement $tr : $trList) {
                String term = $tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = $tr.findElements(By.tagName("td")).get(2).getText();
                String returnMoney = $tr.findElements(By.tagName("td")).get(3).getText();
                String returnRate = $tr.findElements(By.tagName("td")).get(4).getText();
                String returnMoneyAvg = $tr.findElements(By.tagName("td")).get(5).getText();
                String returnRateAvg = $tr.findElements(By.tagName("td")).get(6).getText();
                String returnMoneyMin = $tr.findElements(By.tagName("td")).get(7).getText();
                String returnRateMin = $tr.findElements(By.tagName("td")).get(8).getText();

                logger.info("______해약환급급__________ ");
                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--최저해약환급금: {}", returnMoneyMin);
                logger.info("|--최저해약환급률: {}", returnRateMin);
                logger.info("|--평균해약환급금: {}", returnMoneyAvg);
                logger.info("|--평균해약환급률: {}", returnRateAvg);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                planReturnMoneyList.add(planReturnMoney);
                info.returnPremium = returnMoney;
                // 만기시점(경과기간)을 해약환급금 표에서 제공하는 경우
//                if(maturityYear.equals(term.trim()) || "만기".equals(term.trim())){
//                    info.setReturnPremium(returnMoney.replaceAll("[^0-9]",""));
//                }
            }

            // 만기환급금 크롤링이 불가한 경우
//            if(info.getProductCode().contains("TRM")) {
//                logger.info("정기보험은 만기환급금을 크롤링하지 않습니다");
//
//            } else if(info.getReturnPremium().equals("")){
//                info.setReturnPremium("-1");
//            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_RETURN_MONEY_LIST;
            throw new CommonCrawlerException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }

    /**
     * 선택값 검증 메서드
     *
     * @param   title           선택항목
     * @param   expectedValue   선택하려는 값
     * @param   obj             실제 선택된 엘리먼트
     */
    public void checkValue(String title, String expectedValue, Object obj) throws CommonCrawlerException {

        try{
            WebElement selectedElement = null;

            if(obj instanceof By){
                selectedElement = driver.findElement((By) obj);
            } else{
                selectedElement = (WebElement) obj;
            }

            // 실제 입력된 값
            String selectedValue = "";
            String script = "return $(arguments[0]).find('option:selected').text();";

            if(selectedElement.getTagName().equals("select")){
                selectedValue = String.valueOf(helper.executeJavascript(script,selectedElement));
            } else{
                selectedValue = selectedElement.getText().trim();

                if(selectedValue.equals("")){
                    script = "return $(arguments[0]).text();";
                    selectedValue = String.valueOf(helper.executeJavascript(script, selectedElement));
                }

                if(selectedValue.equals("")){
                    script = "return $(arguments[0]).val();";
                    selectedValue = String.valueOf(helper.executeJavascript(script, selectedElement));
                }

                if(title.equals("성별")){
                    selectedValue = selectedValue.equals("1") ? "남" : "여";
                }
            }

            printLogAndCompare(title, expectedValue, selectedValue);

        } catch (Exception e){
            throw new CommonCrawlerException("선택값 체크 중 오류가 발생했습니다.\n" + e.getMessage());
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

}



