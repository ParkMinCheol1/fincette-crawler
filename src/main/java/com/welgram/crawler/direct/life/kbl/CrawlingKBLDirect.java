package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public abstract class CrawlingKBLDirect extends CrawlingKBLNew {


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {

            //생년월일 element 찾기
            WebElement $birthInput = driver.findElement(By.id("birthday"));

            //생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

            //생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

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
            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='radio-check gender']"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath("//span[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            actualGenderText = ((JavascriptExecutor)driver).executeScript("return $('input[name=genderCode]:checked').next().text();").toString().trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];
        String actualInsTerm = "";

        try{

            WebElement $insTermA = driver.findElement(location);
            WebElement $insTermButton = $insTermA.findElement(By.className("select-box"));
            click($insTermButton);

            List<WebElement> list = $insTermButton.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.equals(expectedInsTerm)) {
                    click($a);
                    logger.info($a + "세 선택");
                    break;
                }
            }

            String $insTermSpan = helper.executeJavascript(script).toString();
            actualInsTerm = $insTermSpan;

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
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];
        String actualNapTerm = "";

        try {

            WebElement $napTermA = driver.findElement(location);
            WebElement $napTermButton = $napTermA.findElement(By.className("select-box"));
            click($napTermButton);

            List<WebElement> list = $napTermButton.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.equals(expectedNapTerm)) {
                    click($a);
                    logger.info($a + "년 납입기간 선택");
                    break;
                }
            }

            String $napTermSpan = helper.executeJavascript(script).toString();
            actualNapTerm = $napTermSpan;

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
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

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void crawlReturnMoneyListSix(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        WebElement $button = null;

        try {
            $button = driver.findElement(By.id("buttonRefundView"));
            click($button);

            elements = driver.findElement(By.xpath("//section[@id='annuityRefundPop']//tbody")).findElements(By.tagName("tr"));

            for (WebElement tr : elements) {

                // 납입기간
                String term 			= tr.findElements(By.tagName("td")).get(0).getText();
                logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                logger.info("해약환급금 크롤링:: 납입기간 :: " + term);

                // 합계 보험료
                String premiumSum 		= tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

                // -----------------------------------------------------------------
                // 공시 환급금
                String returnMoney 		= tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);

                // 공시 환급률
                String returnRate 		= tr.findElements(By.tagName("td")).get(3).getText();
                logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);


                // -----------------------------------------------------------------

                // 평균 환급금
                String returnMoneyAvg 	= tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);

                // 평균 환급률
                String returnRateAvg 	= tr.findElements(By.tagName("td")).get(5).getText();
                logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

                // -----------------------------------------------------------------

                // 최저 환급금
                String returnMoneyMin 	= tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);

                // 최저 환급률
                String returnRateMin 	= tr.findElements(By.tagName("td")).get(7).getText();
                logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

                // -----------------------------------------------------------------

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                planReturnMoney.setReturnRateMin(returnRateMin);;
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);
                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
                planReturnMoney.setReturnRateAvg(returnRateAvg);

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = planReturnMoneyList.get(planReturnMoneyList.size() - 1).getReturnMoney().replace(",", "").replace("원", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void crawlReturnMoneyListTwo(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        WebElement $button = null;

        try{
            $button = driver.findElement(By.id("buttonRefundView"));
            click($button);

            elements = driver.findElement(By.xpath("//section[@id='refundPop']//tbody")).findElements(By.tagName("tr"));

            for (WebElement tr : elements) {

                // 납입기간
                String term = tr.findElements(By.tagName("td")).get(0).getText();
                logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                logger.info("해약환급금 크롤링:: 납입기간 :: " + term);

                // 합계 보험료
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);

                // -----------------------------------------------------------------
                // 공시 환급금
                String returnMoney 		= tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);

                // 공시 환급률
                String returnRate 		= tr.findElements(By.tagName("td")).get(3).getText();
                logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = planReturnMoneyList.get(planReturnMoneyList.size() - 1).getReturnMoney().replace(",", "").replace("원", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {
        String title = "납입 주기";
        String expectedNapCycle = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];
        String actualNapCycle = "";

        try {
            if("01".equals(expectedNapCycle)){
                expectedNapCycle = "월납";
            }

            //연금개시나이 세팅을 위해 버튼 클릭
            WebElement $napCycleA = driver.findElement(location);
            WebElement $napCycleButton = $napCycleA.findElement(By.className("select-box"));
            click($napCycleButton);

            List<WebElement> list = $napCycleButton.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.equals(expectedNapCycle)) {
                    click($a);
                    logger.info($a + "세 선택");
                    break;
                }
            }

            String $napCycleSpan = helper.executeJavascript(script).toString();
            actualNapCycle = $napCycleSpan;

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    public void setPlan(CrawlingProduct info, By location) throws CommonCrawlerException {
        String expectedPlan = info.textType;

        String title = "플랜";
        String actualPlan = "";
        String script = "";

        try {
            //플랜 관련 element 찾기
            WebElement $planUl = driver.findElement(location);
            WebElement $planlabel = $planUl.findElement(By.xpath(".//label[contains(., '" + expectedPlan + "')]"));
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,-50)");

            //플랜 클릭
            click($planlabel);

            //실제 선택된 플랜 값 읽어오기
            WebElement $selectedButton = $planUl.findElement(By.xpath(".//input[@checked]//parent::label//span"));
            actualPlan = $selectedButton.getText();

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {
        String title = "연금개시나이";
        String expectedAnnuityAge = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];
        String actualAnnuityAge = "";

        try {

            //연금개시나이 세팅을 위해 버튼 클릭
            WebElement $annuityAgeA = driver.findElement(location);
            WebElement $annuityAgeButton = $annuityAgeA.findElement(By.className("select-box"));
            click($annuityAgeButton);

            List<WebElement> list = $annuityAgeButton.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.equals(expectedAnnuityAge)) {
                    click($a);
                    logger.info($a + "세 선택");
                    break;
                }
            }

            String $annuityAgeSpan = helper.executeJavascript(script).toString();
            actualAnnuityAge = $annuityAgeSpan;

            //비교
            super.printLogAndCompare(title, expectedAnnuityAge, actualAnnuityAge);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new SetAnnuityAgeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setAnnuityReceivePeriod(Object... obj) throws CommonCrawlerException {
        String title = "연금수령기간";
        String expectedAnnuityReceivePeriod = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];

        try {

            WebElement $annuityReceiveCycleA = driver.findElement(location);
            WebElement $annuityReceiveCycle = $annuityReceiveCycleA.findElement(By.className("select-box"));
            click($annuityReceiveCycle);

            List<WebElement> list = $annuityReceiveCycle.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.equals(expectedAnnuityReceivePeriod)) {
                    click($a);
                    logger.info($a + "년 납입기간 선택");
                    break;
                }
            }

            String $annuityReceivePeriodSpan = helper.executeJavascript(script).toString();
            String actualAnnuityReceivePeriod = $annuityReceivePeriodSpan;

            //비교
            super.printLogAndCompare(title, expectedAnnuityReceivePeriod, actualAnnuityReceivePeriod);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_PERIOD;
            throw new SetAnnuityAgeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setInputAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];

        String expectedAssureMoney = info.getAssureMoney();
        String actualAssureMoney = "";

        try {
            //가입금액을 원수사의 가입금액 포맷에 맞게 text값 수정
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000).replaceAll("[^0-9]", "");

            WebElement $assureMoneyInput = driver.findElement(location);

            actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney).replaceAll("[^0-9]", "");

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setSelectBoxAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];

        String expectedAssureMoney = info.getAssureMoney();

        try {
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000).replaceAll("[^0-9]", "");

            WebElement $assureMoneyA = driver.findElement(location);
            WebElement $assureMoneyAButton = $assureMoneyA.findElement(By.className("select-box"));
            click($assureMoneyAButton);

            List<WebElement> list = $assureMoneyAButton.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText().replaceAll("[^0-9]", "");;

                if(target.equals(expectedAssureMoney)) {
                    click($a);
                    logger.info($a + " 선택");
                    break;
                }
            }

            String $actualAssureMoneyInput = helper.executeJavascript(script).toString();
            String actualAssureMoney = $actualAssureMoneyInput.replaceAll("[^0-9]", "");

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void crawlAnnuityPremium(CrawlingProduct info) throws CommonCrawlerException {
        String title = "연금수령액 크롤링";
        PlanAnnuityMoney planAnnuityMoney = info.getPlanAnnuityMoney();
        WebElement $button = null;

        try {
            $button = driver.findElement(By.id("buttonAnnuityReceiptView"));
            click($button);

            elements = helper.waitVisibilityOfAllElements(
                driver.findElements(By.cssSelector("#annuityReceiptPop tbody tr")));
            WaitUtil.loading(1);

            for (WebElement tr : elements) {
                String tmpText = tr.getText();
                if (tmpText.contains("10년 보증")) {
                    String whl10y = tr.findElement(By.tagName("td")).getText().replaceAll("[^0-9]", "");
                    planAnnuityMoney.setWhl10Y(whl10y + "000");
                }
                if(tmpText.contains("20년 보증")) {
                    String whl20y = tr.findElement(By.tagName("td")).getText().replaceAll("[^0-9]", "");
                    planAnnuityMoney.setWhl20Y(whl20y + "000");
                }
                if(tmpText.contains("100세 보증")) {
                    String whl100a = tr.findElement(By.tagName("td")).getText().replaceAll("[^0-9]", "");
                    planAnnuityMoney.setWhl100A(whl100a + "000");
                }
                if (tmpText.contains("10년 확정형")) {
                    String fxd10y = tr.findElement(By.tagName("td")).getText().replaceAll("[^0-9]", "");
                    planAnnuityMoney.setFxd10Y(fxd10y + "000");
                }
                if (tmpText.contains("20년 확정형")) {
                    String fxd20y = tr.findElement(By.tagName("td")).getText().replaceAll("[^0-9]", "");
                    planAnnuityMoney.setFxd20Y(fxd20y + "000");
                }
            }

            if(info.annuityType.contains("종신 10년")){
                info.annuityPremium = planAnnuityMoney.getWhl10Y();
            } else if(info.annuityType.contains("종신 20년")){
                info.annuityPremium = planAnnuityMoney.getWhl20Y();
            } else if (info.annuityType.contains("종신 100세")){
                info.annuityPremium = planAnnuityMoney.getWhl100A();
            } else if(info.annuityType.contains("확정 10년")){
                info.fixedAnnuityPremium = planAnnuityMoney.getFxd10Y();
            } else if(info.annuityType.contains("확정 20년")){
                info.fixedAnnuityPremium = planAnnuityMoney.getFxd20Y();
            } else {
                logger.info("{} 을 찾을 수 없습니다.", info.annuityType);
                throw new Exception();
            }

            info.planAnnuityMoney = planAnnuityMoney;

            logger.info("info.annuityPremium :: {}", info.annuityPremium);
            logger.info("-------------------------");
            logger.info("10년 보증 :: {}", planAnnuityMoney.getWhl10Y());
            logger.info("20년 보증 :: {}", planAnnuityMoney.getWhl20Y());
            logger.info("100세 보증 :: {}", planAnnuityMoney.getWhl100A());
            logger.info("10년 확정 :: {}", planAnnuityMoney.getFxd10Y());
            logger.info("20년 확정 :: {}", planAnnuityMoney.getFxd20Y());

            logger.info("확인!");
            helper.click(By.cssSelector("#annuityReceiptPop > div > div.modal-footer > button"));

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    protected void surveyAlert() throws Exception {
        WebElement $button = null;

        try{
            //평생동안 한번이라도 흡연을 한 적 있으신가요? [예]
            driver.findElement(By.xpath("//input[@name='quest1']/parent::label//span[text()='예']")).click();
            //현재 흡연 중이거나, 최근 1년 이내에 흡연을 하신 적 있으신가요?	[예]
            driver.findElement(By.xpath("//input[@name='quest2']/parent::label//span[text()='예']")).click();
            //저혈압, 고혈압 진단을 받으신 적이 있으신가요? [아니오]
            driver.findElement(By.xpath("//input[@name='quest3']/parent::label//span[text()='아니오']")).click();
            //당뇨 진단을 받으신 적 있으신가요? [아니오]
            driver.findElement(By.xpath("//input[@name='quest4']/parent::label//span[text()='아니오']")).click();
            //키와 몸무게를 입력해 주세요
            driver.findElement(By.id("lbl_height")).sendKeys("170");
            driver.findElement(By.id("lbl_weight")).sendKeys("60");

            $button = driver.findElement(By.id("buttonCalculationForOnlineHealth"));
            click($button);

        }catch (Exception e){
            logger.info("사전건강질문지가 없습니다.");
        }
    }

    protected void moveToElement(By location){
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(location);
        actions.moveToElement(element);
        actions.perform();
    }

    protected void moveToElement(WebElement location){
        Actions actions = new Actions(driver);
        actions.moveToElement(location);
        actions.perform();
    }

    //로딩바 명시적 대기
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("div.loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}