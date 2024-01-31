package com.welgram.crawler.direct.life.sli;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
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
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;



public abstract class CrawlingSLIAnnounce extends CrawlingSLINew {



    protected void findProduct(CrawlingProduct info) throws CommonCrawlerException {

        try {
            logger.info("해당 카테고리 탭 클릭");
            String contentId = "content0";
            if (info.getCategoryName().contains("정기")
                    || info.getCategoryName().contains("종신") ) {

                helper.waitPresenceOfElementLocated(By.cssSelector("#samsungLifeMain > section > div:nth-child(2) > div > div.tabs-nav > ul > li:nth-child(1)")).click();

            } else if ( info.getCategoryName().contains("암")
                    || info.getCategoryName().contains("치아")
                    || info.getCategoryName().contains("실손")
                    || info.getCategoryName().contains("질병")
                    || info.getCategoryName().contains("상해") ) {

                contentId = "content1";
                helper.waitPresenceOfElementLocated(By.cssSelector("#samsungLifeMain > section > div:nth-child(2) > div > div.tabs-nav > ul > li:nth-child(2)")).click();

            } else if (info.getCategoryName().contains("연금")
                    || info.getCategoryName().contains("연금저축")
                    || info.getCategoryName().contains("저축") ) {

                contentId = "content2";
                helper.waitPresenceOfElementLocated(By.cssSelector("#samsungLifeMain > section > div:nth-child(2) > div > div.tabs-nav > ul > li:nth-child(3)")).click();

            } else if (info.getCategoryName().contains("태아") || info.getCategoryName().contains("어린이") ) {

                if (info.getProductNamePublic().contains("성장보험") || info.getProductNamePublic().contains("건강보험") ) {

                    contentId = "content1";
                    helper.waitPresenceOfElementLocated(By.cssSelector("#samsungLifeMain > section > div:nth-child(2) > div > div.tabs-nav > ul > li:nth-child(2)")).click();

                } else {
                    contentId = "content3";
                    helper.waitPresenceOfElementLocated(By.cssSelector("#samsungLifeMain > section > div:nth-child(2) > div > div.tabs-nav > ul > li:nth-child(4)")).click();
                }
            }

            WaitUtil.loading(1);
            logger.info("리스트에서 해당 상품 찾기");
            String productName = "";

            elements = driver.findElements(By.cssSelector("#" + contentId + " > div > ul > li"));
            for (WebElement li : elements) {
                productName = li.findElements(By.tagName("P")).get(0).getText().trim();
                if (info.productNamePublic.trim().equals(productName)) {
                    logger.info("상품을 찾았습니다.");
                    WaitUtil.waitFor(2);
                    logger.info(li.findElement(By.tagName("button")).getText());
                    click(li.findElement(By.tagName("button")));
                    break;
                }
            }
        } catch (Exception e) {
            throw new CommonCrawlerException(e, "공시실에서 상품을 찾을 수 없습니다.");
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];

        String expectedYear = expectedFullBirth.substring(0, 4);
        String expectedMonthly = expectedFullBirth.substring(4, 6);
        String expectedDay = expectedFullBirth.substring(6, 8);

        String actualYear = "";
        String actualMonthly = "";
        String actualDay = "";

        try {
            WebElement $yearSelectBox = driver.findElement(By.id("selYear0"));
            actualYear = helper.selectByText_check($yearSelectBox, expectedYear);

            //연 비교
            super.printLogAndCompare(title, expectedYear, actualYear);

            WebElement $monthlySelectBox = driver.findElement(By.id("selMonth0"));
            expectedMonthly = StringUtils.stripStart(expectedMonthly, "0");
            actualMonthly = helper.selectByText_check($monthlySelectBox, expectedMonthly);

            //월 비교
            super.printLogAndCompare(title, expectedMonthly, actualMonthly);

            WebElement $daySelectBox = driver.findElement(By.id("selDay0"));
            expectedDay = StringUtils.stripStart(expectedDay, "0");
            actualDay = helper.selectByText_check($daySelectBox, expectedDay);

            //일 비교
            super.printLogAndCompare(title, expectedDay, actualDay);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setMotherBirthday(Object... obj) throws SetBirthdayException {

        String title = "임산부 생년월일";
        String expectedFullBirth = (String) obj[0];

        String expectedYear = expectedFullBirth.substring(0, 4);
        String expectedMonthly = expectedFullBirth.substring(4, 6);
        String expectedDay = expectedFullBirth.substring(6, 8);

        String actualYear = "";
        String actualMonthly = "";
        String actualDay = "";

        try {
            WebElement $yearSelectBox = driver.findElement(By.id("selYear1"));
            actualYear = helper.selectByText_check($yearSelectBox, expectedYear);

            //연도 비교
            super.printLogAndCompare(title, expectedYear, actualYear);

            WebElement $monthlySelectBox = driver.findElement(By.id("selMonth1"));
            expectedMonthly = StringUtils.stripStart(expectedMonthly, "0");
            actualMonthly = helper.selectByText_check($monthlySelectBox, expectedMonthly);

            //월 비교
            super.printLogAndCompare(title, expectedMonthly, actualMonthly);

            WebElement $daySelectBox = driver.findElement(By.id("selDay1"));
            expectedDay = StringUtils.stripStart(expectedDay, "0");
            actualDay = helper.selectByText_check($daySelectBox, expectedDay);

            //연도 비교
            super.printLogAndCompare(title, expectedDay, actualDay);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        String title = "성별";

        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {

            //성별 element 찾기
            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='radio-box-wrap']"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath("//label[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            actualGenderText = ((JavascriptExecutor) driver).executeScript("return $('input[name=sxdsCd0]:checked').next().text();").toString().trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

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

        String title = "직업선택";
        String expectedJob = (String) obj[0];
        String actualJob = "";

        WebElement $jobInput = null;
        WebElement $jobButton = null;
        WebElement $jobA = null;

        try{
            logger.info("직업 창 열기");
            $jobInput = driver.findElement(By.id("jobNm"));
            click($jobInput);

            $jobInput = driver.findElement(By.id("search_txt"));
            helper.sendKeys4_check($jobInput, expectedJob);

            $jobButton = driver.findElement(By.xpath("//div[@class='search-form']//button[@class='icon search']"));
            click($jobButton);

            $jobA = driver.findElements(By.cssSelector("li[class='result-list-item']")).get(0);
            click($jobA.findElement(By.tagName("a")));

            //실제 선택된 직업 값 읽어오기
            String script = "return $(arguments[0]).val();";
            $jobInput = driver.findElement(By.id("jobNm"));
            actualJob = String.valueOf(helper.executeJavascript(script, $jobInput));

            //비교
            super.printLogAndCompare(title, expectedJob, actualJob);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_JOB;
            throw new SetJobException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setPlan(Object... obj) throws CommonCrawlerException {

        String title = "플랜";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String expectedPlan = "";

        try {
            expectedPlan = (String)obj[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            expectedPlan = info.textType;
        }
        String actualPlan = "";

        try {
            WebElement $planSelectBox = driver.findElement(location);
            actualPlan = helper.selectByText_check($planSelectBox, expectedPlan);

            super.printLogAndCompare(title, actualPlan, expectedPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setPlanInputBox(Object... obj) throws CommonCrawlerException {

        String title = "플랜 입력";
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[0];

        String expectedPlan = (String) obj[1];
        String actualPlan = "";

        try {
            WebElement $planInputBox = driver.findElement(location);
            actualPlan = helper.sendKeys4_check($planInputBox, expectedPlan);

            super.printLogAndCompare(title, actualPlan, expectedPlan);

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
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];

        try{
            WebElement $insTermSelect = driver.findElement(location);
            expectedInsTerm = expectedInsTerm + "세";
            actualInsTerm = helper.selectByText_check($insTermSelect, expectedInsTerm);

            //비교
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
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];

        try {
            expectedNapTerm = expectedNapTerm.contains("납") ? expectedNapTerm : expectedNapTerm + "납";
            WebElement $napTermSelect = driver.findElement(location);
            actualNapTerm = helper.selectByText_check($napTermSelect, expectedNapTerm);

            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입 주기";
        String expectedNapCycle = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];

        String actualNapCycle = "";

        try {
            WebElement $napCycleSelectBox = driver.findElement(location);
            actualNapCycle = helper.selectByText_check($napCycleSelectBox, expectedNapCycle);

            super.printLogAndCompare(title, actualNapCycle, expectedNapCycle);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
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
    public void setRenewType(Object... obj) throws SetRenewTypeException {

    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "가입금액";
        String expectedAssureMoney = (String) obj[0];
        String actualAssureMoney = "";
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        int unit = MoneyUnit.천원.getValue();

        try {
            WebElement $assureMoneyInput = driver.findElement(location);
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / unit);
            actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney);
            actualAssureMoney = actualAssureMoney.replaceAll("[^0-9]", "");

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {

        super.setRefundType(obj);
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        String title = "보험료 크롤링";
        String script = "";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By monthlyPremium = (By) obj[1];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumEm = driver.findElement(monthlyPremium);
            String premium = $premiumEm.getText().replaceAll("[^0-9]", "");

            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void crawlNextPremium(Object... obj) throws PremiumCrawlerException {

        String title = "계속 보험료 크롤링";
        String script = "";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By nextPremium = (By) obj[1];
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(2);

            WebElement $premiumEm = driver.findElement(nextPremium);
            String premium = $premiumEm.getText().replaceAll("[^0-9]", "");

            info.nextMoney = premium;

            if ("".equals(info.nextMoney) || "0".equals(info.nextMoney)) {
                logger.info("계속 보험료는 0원일 수 없습니다. 확인해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("계속 보험료 : {}원", info.nextMoney);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

    }



    public void crawlReturnMoneyList1(Object... obj) throws ReturnMoneyListCrawlerException {

        String title = "해약환급금 크롤링";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];

        try {
            List<WebElement> list =  wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(location));
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
            int scrollTop = 0;
            EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
            for (WebElement tr : list) {
                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                String term = tr.findElements(By.tagName("td")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(3).getText();
                logger.info(term + " 경과 기간 :: " + term);
                logger.info(term + " 누적보험료 :: " + premiumSum);
                logger.info(term + " 해약환급금 :: " + returnMoney);
                logger.info(term + " 환급률 :: " + returnRate);

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney.replace(",", "").replace("원", "");

                scrollTop += 65;
                WaitUtil.mSecLoading(300);
                eventFiringWebDriver.executeScript("document.querySelector(\"div[class='section-main section-disclosure section-insurance-calculate']\").parentNode.scrollTop = " + scrollTop);
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        String title = "연금개시나이";
        String expectedAnnuityAge = (String) obj[0];
        String actualAnnuityAge = "";

        try {
            WebElement $annAgeInput = driver.findElement(By.id("anutBgnAge"));
            actualAnnuityAge = helper.sendKeys4_check($annAgeInput, expectedAnnuityAge);

            //연금개시나이를 설정한 후에 빈 영역을 클릭시켜줘야 한다.
            WebElement $annAgeTh = driver.findElement(By.xpath("//th[normalize-space()='연금개시연령']"));
            click($annAgeTh);

            //비교
            super.printLogAndCompare(title, expectedAnnuityAge, actualAnnuityAge);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new SetAnnuityAgeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {

        String title = "연금유형";
        String expectedAnnuityType = (String) obj[0];
        String actualAnnuityType = "";

        try {
            WebElement $annuityTypeSelect = driver.findElement(By.id("anutPymTypCd"));
            actualAnnuityType = helper.selectByText_check($annuityTypeSelect, expectedAnnuityType);

            super.printLogAndCompare(title, expectedAnnuityType, actualAnnuityType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_TYPE;
            throw new SetAnnuityTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setInputAssureMoney(Object... obj) throws SetAssureMoneyException {

        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];

        String expectedAssureMoney = info.getAssureMoney();
        String actualAssureMoney = "";

        try {

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setAnnuityReceivePeriod(Object... obj) throws CommonCrawlerException {

        String title = "연금수령기간";
        String expectedAnnuityReceivePeriod = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];

        String actualAnnuityReceivePeriod = "";

        try {

            WebElement $planSelectBox = driver.findElement(location);

            actualAnnuityReceivePeriod = helper.selectByText_check($planSelectBox, expectedAnnuityReceivePeriod);

            //비교
            super.printLogAndCompare(title, expectedAnnuityReceivePeriod, actualAnnuityReceivePeriod);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_PERIOD;
            throw new SetAnnuityAgeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void crawlAnnuityPremium(Object... obj) throws CommonCrawlerException {

    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        try {
            logger.info("해약환급금 예시 버튼 클릭");
            WebElement $button = driver.findElement(By.xpath("//a[normalize-space()='해약환급금 예시']"));
            helper.moveToElementByJavascriptExecutor($button);
            click($button);

            String script = "return $('tbody:visible')[0]";
            WebElement $tbody = (WebElement) helper.executeJavascript(script);
            List<WebElement> $trList = $tbody.findElements(By.tagName("tr"));

            for (WebElement $tr : $trList) {
                helper.moveToElementByJavascriptExecutor($tr);
                List<WebElement> $tdList = $tr.findElements(By.tagName("td"));

                //해약환급금 정보 크롤링
                String term = $tdList.get(0).getText().trim();
                String premiumSum = $tdList.get(1).getText().trim();
                String returnMoneyMin = $tdList.get(2).getText();
                String returnRateMin = $tdList.get(3).getText();
                String returnMoneyAvg = $tdList.get(4).getText();
                String returnRateAvg = $tdList.get(5).getText();
                String returnMoney = $tdList.get(6).getText();
                String returnRate = $tdList.get(7).getText();

                premiumSum = String.valueOf(MoneyUtil.toDigitMoney(premiumSum));
                returnMoneyMin = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin));
                returnMoneyAvg = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg));
                returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

                //해약환급금 적재
                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoneyMin(returnMoneyMin);
                p.setReturnRateMin(returnRateMin);
                p.setReturnMoneyAvg(returnMoneyAvg);
                p.setReturnRateAvg(returnRateAvg);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                planReturnMoneyList.add(p);

                logger.info("경과기간 : {} | 납입보험료 : {} | 최저환급금 : {} | 최저환급률 : {} | 평균환급금 : {} | 평균환급률 : {} | 환급금 : {} | 환급률 : {}"
                    , term, premiumSum, returnMoneyMin, returnRateMin, returnMoneyAvg, returnRateAvg, returnMoney, returnRate);

                //만기환급금 세팅
                info.returnPremium = returnMoney;
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
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

        String title = "보험종류";
        String expectedProductType = (String) obj[0];
        String actualProductType = "";

        try {
            WebElement $productTypeSelect = driver.findElement(By.id("hptsLineCd"));
            actualProductType = helper.selectByText_check($productTypeSelect, expectedProductType);

            super.printLogAndCompare(title, expectedProductType, actualProductType);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_TYPE;
            throw new SetProductTypeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setLargeAmountContract(String expectedLargeAmountContract) throws CommonCrawlerException {

        String title = "고액계약";
        String actualLargeAmountContract = "";

        try {
            WebElement $largeAmountContractSelect = driver.findElement(By.id("lgatContAppnCd"));
            actualLargeAmountContract = helper.selectByText_check($largeAmountContractSelect, expectedLargeAmountContract);

            super.printLogAndCompare(title, expectedLargeAmountContract, actualLargeAmountContract);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_LARGE_AMOUNT_CONTRACT;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }



    public void setVehicleCnt(Object... obj) throws CommonCrawlerException {

    }



    @Override
    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

    }



    public void setMainTreaty(CrawlingProduct info) throws SetTreatyException {

        CrawlingTreaty item = info.treatyList.stream().filter( t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();

        try {
            List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("section[class='table-wrap']")));

            boolean allSet = false;
            for (WebElement el : elements) {

                String title = el.findElement(By.className("table-title")).getText();
                if (title.equals("주보험조건")) {
                    logger.info("주보험 영역이 있음");

                    List<WebElement> trs = el.findElements(By.tagName("tr"));
                    // 구분, 보험기간, 가입기간 3건 조회
                    for (int i = 0; i < trs.size(); i++) {
                        WebElement tr = trs.get(i);
                        if (i == 0) {
                            logger.info("구분");
                            logger.info(tr.findElement(By.tagName("td")).getText());

                        } else if (i == 1) {
                            logger.info("보험기간, 납입기간 처리");

                            List<WebElement> tds = tr.findElements(By.tagName("td"));
                            for (int j = 0; j < tds.size(); j++) {
                                WebElement td = tds.get(j);
                                if (j == 0) {
                                    try {
                                        title = "주계약 보험기간 선택";
                                        String expectedInsTerm = "";
                                        if (!info.annuityType.equals("")) {
                                            logger.info("보험기간 처리 :: " + item.annAge + "세");
                                            expectedInsTerm = item.annAge + "세";
                                        } else {
                                            if (item.insTerm.equals("종신보장")) {
                                                logger.info("보험기간 처리 :: 종신보장 -> 종신");
                                                expectedInsTerm = "종신";
                                            } else {
                                                logger.info("보험기간 처리 :: " + item.insTerm);
                                                expectedInsTerm = item.insTerm;
                                            }
                                        }
                                        String actualInsTerm = "";

                                        WebElement $insTermSelectBox = td.findElement(By.tagName("select"));
                                        actualInsTerm = helper.selectByText_check($insTermSelectBox, expectedInsTerm);

                                        super.printLogAndCompare(title, actualInsTerm, expectedInsTerm);

                                    } catch (Exception e) {
                                        ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
                                        throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
                                    }
                                } else if (j == 1) {
                                    try {
                                        title = "주계약 납입기간 선택";
                                        logger.info("납입기간 처리 :: " + item.napTerm);
                                        String expectedNapTerm = item.napTerm.equals("일시납") ? item.napTerm = "일시납" : item.napTerm + "납";
                                        String actualNapTerm = "";

                                        WebElement $napTermSelectBox = td.findElement(By.tagName("select"));
                                        actualNapTerm = helper.selectByText_check($napTermSelectBox, expectedNapTerm);

                                        super.printLogAndCompare(title, actualNapTerm, expectedNapTerm);

                                    } catch (Exception e) {
                                        ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
                                        throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
                                    }
                                }
                            }
                        } else if (i == 2) {
                            try {
                                title = "주계약 가입금액";
                                String expectedAssureMoney = String.valueOf(Integer.valueOf(info.getAssureMoney()) / 10000);
                                String actualAssureMoney = "";

                                WebElement $assureMoneyInput = tr.findElement(By.tagName("input"));
                                try {
                                    String montyUtil = tr.findElement(By.xpath(".//span[@class='form-unit']")).getText();
                                    if (montyUtil.equals("천원")) {
                                        expectedAssureMoney = String.valueOf(Integer.valueOf(info.getAssureMoney()) / 1000);
                                    }
                                } catch (Exception e) { }

                                actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney).replaceAll("[^0-9]", "");

                                //비교
                                super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

                                allSet = true;

                            } catch (Exception e) {
                                ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
                                throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
                            }
                        }
                    }
                }

                if (allSet) { break; }
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    //특약 리스트의 첫 번째부터 특약을 확인하는 메소드
    public void setSubTreaties(CrawlingProduct info) throws SetTreatyException {

        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream().filter(t -> t.productGubun == ProductGubun.선택특약).collect(Collectors.toList());
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        try {
            for (CrawlingTreaty item : subTreatyList) {
                boolean matched = false; // treaty name이 일치한 경우
                boolean done = false; // treaty가 일치했을 때 지정한 action이 문제없이 완료된 경우

                // 스크롤을 위로 올림. 내려간 상태에선 section title(고정부가특약, 선택특약, ...)을 제대로 읽어오지 못함.
                WebElement top = helper.waitPresenceOfElementLocated(By.cssSelector("h1.progress-title"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", top);

                // 고정부가특약, 선택특약 등 영역
                List<WebElement> sections = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div#appModal section.table-wrap")));

                for (WebElement section : sections) {
                    String sectionTitle = section.findElement(By.tagName("h1")).getText();
                    if (sectionTitle.equals("선택특약") || sectionTitle.equals("고정부가특약")) {

                        List<WebElement> trs = section.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
                        // 테이블 TR 배열 (특약수와 비례)
                        for (int i = 0; i < trs.size(); i++) {
                            CrawlingTreaty targetTreaty = new CrawlingTreaty();
                            WebElement tr = trs.get(i);
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

                            String treatyNm = tr.findElements(By.tagName("td")).get(0).findElement(By.tagName("label")).getText();

                            if (treatyNm.replaceAll("\\s", "").contains(item.treatyName.replaceAll("\\s", ""))) { // 담보명과 이름이 같은지 확인
                                targetTreaty.setTreatyName(item.treatyName);
                                logger.info("특약명 :: {}",item.treatyName);
                                matched = true;
                                WebElement checkBox = helper.waitElementToBeClickable(tr.findElements(By.tagName("td")).get(0).findElement(By.cssSelector("input[type='checkbox']")));

                                // 선택박스 처리
                                WaitUtil.mSecLoading(100);
                                if (!checkBox.isSelected()) helper.click(checkBox, treatyNm + "체크박스");
                                try {
                                    String title = "선택특약 보험기간 선택";
                                    logger.info("보험기간 처리 :: " + item.insTerm);
                                    String expectedInsTerm = item.insTerm;
                                    String actualInsTerm = "";

                                    WebElement $insTermEl =  tr.findElements(By.tagName("td")).get(1).findElement(By.tagName("select"));
                                    actualInsTerm = helper.selectByText_check($insTermEl, expectedInsTerm);

                                    super.printLogAndCompare(title, actualInsTerm, expectedInsTerm);

                                    targetTreaty.setInsTerm(item.insTerm);

                                } catch (Exception e) {
                                    logger.info("보험기간 선택이 불가합니다.");
                                    targetTreaty.setInsTerm(item.insTerm);
                                }

                                try {
                                    String title = "선택특약 납입기간 선택";
                                    logger.info("납입기간 처리 :: " + item.napTerm);
                                    String expectedNapTerm = item.napTerm.equals("일시납") ? item.napTerm = "일시납" : item.napTerm + "납";
                                    String actualNapTerm = "";

                                    WebElement $napTermSelectBox = tr.findElements(By.tagName("td")).get(2).findElement(By.tagName("select"));
                                    actualNapTerm = helper.selectByText_check($napTermSelectBox, expectedNapTerm);

                                    super.printLogAndCompare(title, actualNapTerm, expectedNapTerm);

                                    targetTreaty.setNapTerm(item.napTerm);

                                } catch (Exception e) {
                                    logger.info("납입기간 선택이 불가합니다.");
                                    targetTreaty.setNapTerm(item.napTerm); // 그대로 넣어주는게 맞는가
                                }

                                try {
                                    String title = "선택특약 가입금액";
                                    String expectedAssureMoney = String.valueOf(item.assureMoney / 10000);
                                    String actualAssureMoney = "";

                                    WebElement $assureMoneyInput = tr.findElements(By.tagName("td")).get(4).findElement(By.cssSelector("input[type=text]"));

                                    if (item.treatyName.contains("주보험(적립)")) {
                                        targetTreaty.setAssureMoney(item.assureMoney);
                                    } else {
                                        actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney).replaceAll("[^0-9]", "");
                                        //비교
                                        super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);
                                        targetTreaty.setAssureMoney(item.assureMoney);
                                    }
                                } catch (Exception e) {
                                    ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
                                    throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
                                }

                                targetTreatyList.add(targetTreaty);
                                done = true;
                                break;
                            }
                        }
                        if (done) { break; }
                    } // end of -> if (title.equals(...
                }

                if (!matched) {
                    throw new Exception(item.treatyName + "이(가) 원수사 특약 중 존재하지 않습니다.");
                }
            }

            List<CrawlingTreaty> treatyList =
                    info.getTreatyList().stream()
                            .filter(t -> t.productGubun == ProductGubun.선택특약)
                            .collect(Collectors.toList());

            boolean result = advancedCompareTreaties(targetTreatyList, treatyList , new CrawlingTreatyEqualStrategy2());
            if (result) {
                logger.info("특약 정보가 모두 일치합니다~~~");
            } else {
                logger.error("특약 정보 불일치~~~");
                throw new Exception();
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    //특약 리스트를 돌지않고 특약명을 바로 찾아가는 메소드
    public void setDirectlySubTreaties(CrawlingProduct info) throws SetTreatyException {

        List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream().filter(t -> t.productGubun == ProductGubun.선택특약).collect(Collectors.toList());
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
        try {
            for (CrawlingTreaty item : subTreatyList) {
                boolean matched = false; // treaty name이 일치한 경우
                boolean done = false; // treaty가 일치했을 때 지정한 action이 문제없이 완료된 경우

                // 스크롤을 위로 올림. 내려간 상태에선 section title(고정부가특약, 선택특약, ...)을 제대로 읽어오지 못함.
                WebElement top = helper.waitPresenceOfElementLocated(By.cssSelector("h1.progress-title"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", top);

                // 고정부가특약, 선택특약 등 영역
                List<WebElement> sections = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div#appModal section.table-wrap")));

                for (WebElement section : sections) {
                    String sectionTitle = section.findElement(By.tagName("h1")).getText();
                    if (sectionTitle.equals("선택특약") || sectionTitle.equals("고정부가특약")) {

                        WebElement tr = section.findElement(By.xpath("//*[tbody]//tr//td//label[normalize-space()='"+item.treatyName+"']//ancestor::tr"));
                        // 테이블 TR 배열 (특약수와 비례)
                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tr);

                        if (tr.getText().replaceAll("\\s", "").contains(item.treatyName.replaceAll("\\s", ""))) { // 담보명과 이름이 같은지 확인
                            targetTreaty.setTreatyName(item.treatyName);
                            logger.info("특약명 :: {}",item.treatyName);
                            matched = true;

//							WebElement checkBox = helper.waitElementToBeClickable(tr.findElements(By.tagName("td")).get(0).findElement(By.cssSelector("input[type='checkbox']")));
                            WebElement checkBox = helper.waitElementToBeClickable(tr.findElement(By.xpath(".//input[@type='checkbox']")));

                            // 선택박스 처리
                            WaitUtil.mSecLoading(100);
                            if (!checkBox.isSelected()) helper.click(checkBox, tr.getText() + "체크박스");
                            try {
                                String title = "선택특약 보험기간 선택";
                                logger.info("보험기간 처리 :: " + item.insTerm);
                                String expectedInsTerm = item.insTerm;
                                String actualInsTerm = "";

//								WebElement $insTermEl =  tr.findElements(By.tagName("td")).get(1).findElement(By.tagName("select"));
                                WebElement $insTermEl =  tr.findElements(By.tagName("td")).get(1).findElement(By.tagName("select"));
                                actualInsTerm = helper.selectByText_check($insTermEl, expectedInsTerm);

                                super.printLogAndCompare(title, actualInsTerm, expectedInsTerm);

                                targetTreaty.setInsTerm(item.insTerm);

                            } catch (Exception e) {
                                ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
                                throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
                            }

                            try {
                                String title = "선택특약 납입기간 선택";
                                logger.info("납입기간 처리 :: " + item.napTerm);
                                String expectedNapTerm = item.napTerm.equals("일시납") ? item.napTerm = "일시납" : item.napTerm + "납";
                                String actualNapTerm = "";

                                WebElement $napTermSelectBox = tr.findElements(By.tagName("td")).get(2).findElement(By.tagName("select"));
                                actualNapTerm = helper.selectByText_check($napTermSelectBox, expectedNapTerm);

                                super.printLogAndCompare(title, actualNapTerm, expectedNapTerm);

                                targetTreaty.setNapTerm(item.napTerm);

                            } catch (Exception e) {
                                ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
                                throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
                            }

                            try {
                                String title = "선택특약 가입금액";
                                String expectedAssureMoney = String.valueOf(item.assureMoney / 10000);
                                String actualAssureMoney = "";

                                WebElement $assureMoneyInput = tr.findElements(By.tagName("td")).get(4).findElement(By.cssSelector("input[type=text]"));

                                if (item.treatyName.contains("주보험(적립)")) {
                                    targetTreaty.setAssureMoney(item.assureMoney);
                                } else {
                                    actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney).replaceAll("[^0-9]", "");
                                    //비교
                                    super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);
                                    targetTreaty.setAssureMoney(item.assureMoney);
                                }
                            } catch (Exception e) {
                                ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
                                throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
                            }

                            targetTreatyList.add(targetTreaty);
                            done = true;
                            break;
                        }

                        if (done) { break; }
                    } // end of -> if (title.equals(...
                }

                if (!matched) {
                    throw new Exception(item.treatyName + "이(가) 원수사 특약 중 존재하지 않습니다.");
                }
            }

            List<CrawlingTreaty> treatyList = info.getTreatyList().stream()
                .filter(t -> t.productGubun == ProductGubun.선택특약)
                .collect(Collectors.toList());

            boolean result = advancedCompareTreaties(targetTreatyList, treatyList , new CrawlingTreatyEqualStrategy2());
            if (result) {
                logger.info("특약 정보가 모두 일치합니다~~~");
            } else {
                logger.error("특약 정보 불일치~~~");
                throw new Exception();
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    protected void alert() throws Exception{

        boolean isShowed = helper.isAlertShowed();
        while (isShowed) {
            driver.switchTo().alert().accept();
            isShowed = helper.isAlertShowed();
        }
        WaitUtil.waitFor(2);
    }



    //TODO 추후에 helper로 뺄것
    public void click(WebElement $element) throws Exception {

        helper.waitElementToBeClickable($element).click();
        waitLoadingBar();
        WaitUtil.waitFor(1);
    }



    //TODO 추후에 helper로 뺄것
    public void click(By position) throws Exception {

        WebElement $element = driver.findElement(position);
        click($element);
    }



    protected void moveToElement(By location) {

        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(location);
        actions.moveToElement(element);
        actions.perform();
    }



    protected void moveToElement(WebElement location) {

        Actions actions = new Actions(driver);
        actions.moveToElement(location);
        actions.perform();
    }



    //로딩바 명시적 대기
    public void waitLoadingBar() {

        try {
            helper.waitForCSSElement("#loadingId");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}