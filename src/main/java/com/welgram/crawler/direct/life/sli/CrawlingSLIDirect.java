package com.welgram.crawler.direct.life.sli;

import com.welgram.common.MoneyUtil;
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
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.common.except.CrawlingException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.PlanReturnMoneyTdIdx;
import com.welgram.util.InsuranceUtil;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingSLIDirect extends CrawlingSLINew {


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        By location = null;
        try{
            location = (By) obj[1];
        } catch (ArrayIndexOutOfBoundsException e){
            location = By.id("birthday");
        }

        String actualFullBirth = "";

        try {

            //생년월일 element 찾기
            WebElement $birthInput = driver.findElement(location);

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
            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='label-check1']"));
            WebElement $genderLabel = $genderDiv
                .findElement(By.xpath("//span[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            actualGenderText = ((JavascriptExecutor) driver)
                .executeScript("return $('input[name=pdgender]:checked').next().text();").toString()
                .trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setChildGender(Object... obj) throws SetGenderException {
        String title = "자녀 성별";

        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남아" : "여아";
        String actualGenderText = "";

        try {

            //성별 element 찾기
            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='label-check1']"));
            WebElement $genderLabel = $genderDiv
                .findElement(By.xpath("//span[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            actualGenderText = ((JavascriptExecutor) driver)
                .executeScript("return $('input[name=stdPGenderChild]:checked').next().text();").toString()
                .trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {
        String title = "출산예정일";
        String expectedDueDate = "";
        String actualDueDate = "";

        try {

            //생년월일 element 찾기
            WebElement $dueDateInput = driver.findElement(By.id("babyDue"));

            //출산예정일 12주
            expectedDueDate = InsuranceUtil.getDateOfBirth(12);

            //생년월일 설정
            actualDueDate = helper.sendKeys4_check($dueDateInput, expectedDueDate);

            //생년월일 비교
            super.printLogAndCompare(title, expectedDueDate, actualDueDate);

        } catch (Exception e) {
            logger.info("출산 예정일 선택 중 에러 발생");
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_DUEDATE;
            throw new SetDueDateException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By) obj[1];
        String actualInsTerm = "";

        try {
            actualInsTerm = helper.selectByText_check(location, expectedInsTerm);

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setInsTermRadioButton(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By) obj[1];
        String actualInsTerm = "";

        try {

            WebElement $insTermUl = driver.findElement(location);

            List<WebElement> list = $insTermUl.findElements(By.tagName("label"));
            for(WebElement li : list) {
                String target = li.getText();

                if(target.equals(expectedInsTerm)) {
                    click(li);
                    break;
                }
            }

            ((JavascriptExecutor)driver).executeScript("$('span.hide').remove();");
            actualInsTerm = ((JavascriptExecutor)driver).executeScript("return $('input[name=ins_term]:checked').next().text();").toString().trim();

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
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By) obj[1];
        String actualNapTerm = "";

        try {
            try {
                actualNapTerm = helper.selectByText_check(location, expectedNapTerm);

            } catch (NoSuchElementException e) {
                expectedNapTerm = expectedNapTerm + "(추천)";
                actualNapTerm = helper.selectByText_check(location, expectedNapTerm);
            }

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void setSelectBoxAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By) obj[1];

        String expectedAssureMoney = info.getAssureMoney();
        String actualAssureMoney = "";

        try {
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000);
            try {
                actualAssureMoney = helper.selectByText_check(location, expectedAssureMoney + "만원");
            } catch (NoSuchElementException e) {
                DecimalFormat decFormat = new DecimalFormat("###,###");
                expectedAssureMoney = decFormat.format(Integer.parseInt(expectedAssureMoney));
                actualAssureMoney = helper.selectByText_check(location, expectedAssureMoney + "만원");
            }

            expectedAssureMoney = expectedAssureMoney + "만원";
            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setInputAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By) obj[1];

        String expectedAssureMoney = info.getAssureMoney();
        String actualAssureMoney = "";

        try {
            //가입금액을 원수사의 가입금액 포맷에 맞게 text값 수정
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000)
                .replaceAll("[^0-9]", "");

            WebElement $assureMoneyInput = driver.findElement(location);

            actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney)
                .replaceAll("[^0-9]", "");

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTreaties(Object... obj) throws SetTreatyException {
        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By) obj[1];

        try{

            // 특약명과 가입금액이 들어있는 요소
            WebElement locate = driver.findElement(location);
            WebElement ul = locate.findElements(By.cssSelector("ul.info2.list-data1")).get(1);

            // 특약명과 가입금액 목록
            List<WebElement> lis = ul.findElements(By.tagName("li"));

            List<CrawlingTreaty> treatyList = info.getTreatyList();
            for (CrawlingTreaty treaty : treatyList) {

                // 특약명 (ex: 일반암, 고액암..)
                String trimmedName = treaty.getTreatyName()
                    .substring(0, treaty.getTreatyName().indexOf("(")).trim();

                // 특약 가입금액
                int assureMoney = treaty.getAssureMoney();

                lis.stream().filter(li -> { // 특약명과 가입금액 목록 순회

                    WebElement firstDiv = li.findElements(By.tagName("div")).get(0);
                    String firstDivText = firstDiv.getText();
                    return trimmedName.contains(firstDivText);

                }).findFirst().ifPresent(li -> { // 특약명과 일치하는 목록이 있을 때 가입금액 선택을 시도함

                    // 가입금액 정보가 있는 div
                    WebElement div = li.findElements(By.tagName("div")).get(1);

                    try { // select tag가 있다면 선택한다
                        Select select = new Select(div.findElement(By.tagName("select")));
                        int dividedBy10000 = assureMoney / 10000;

                        DecimalFormat df = new DecimalFormat("#,###");
                        String format = df.format(dividedBy10000);
                        String visibleTxt = format + "만원";

                        String selectedOptionTxt = select.getFirstSelectedOption().getText();
                        if (!selectedOptionTxt.equals(visibleTxt)) {
                            select.selectByVisibleText(visibleTxt);
                            logger.info("선택되어 있던 값 : " + selectedOptionTxt );
                            logger.info("새로 선택한 값 : " + visibleTxt );

                        } else {
                            logger.info("이미 선택되어있는 값입니다 : " + selectedOptionTxt );
                        }

                        // 다시 계산 버튼
                        try {
                            WebElement reClacButton = locate.findElement(
                                By.xpath(".//button[contains(.,'다시계산')]"));
                            if (reClacButton.isEnabled()) {
                                helper.click(reClacButton);
                                helper.waitForLoading();
                            }

                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                    } catch (NoSuchElementException e) { // <-- select tag

                        // select tag가 없다면, 해당 특약에 대해서 선택할 수 없는 ui이다.
                        logger.info(trimmedName + "은 가입금액을 선택하지 않습니다. ");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    protected int getPlanNum(CrawlingProduct info) {

        WebElement planEl =
            helper.waitVisibilityOfElementLocated(
                By.xpath("//label[contains(.,'" + info.textType + "')]")
            );

        String planInputId =
            planEl
                .findElement(By.xpath("./preceding-sibling::input"))
                .getAttribute("id");

        // 해약환급금 버튼과 테이블 요소에 쓰일 번호 추출
        int planNum = Integer.parseInt(planInputId.replaceAll("\\D", ""));

        logger.info("플랜 num : " + planNum);

        return planNum;
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        String title = "보험료 크롤링";
        String script = "";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By monthlyPremium = (By) obj[1];
        CrawlingTreaty mainTreaty =
            info.getTreatyList()
                .stream()
                .filter(t -> t.productGubun.equals(ProductGubun.주계약))
                .findFirst()
                .get();
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
        WebElement $span = null;
        String actualPlan = "";

        try {
            $span = driver.findElement(location);
            click($span);

            WebElement planEl = helper.waitVisibilityOfElementLocated(
                By.xpath("//label[contains(.,'" + info.textType + "')]"));

            if(planEl.getText().contains(expectedPlan)){
                actualPlan = expectedPlan;
            }
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
        String actualAnnuityAge = "";

        try {

            actualAnnuityAge = helper.selectByText_check(location, expectedAnnuityAge);

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
        String actualAnnuityReceivePeriod = "";

        try {
            //비교
            super.printLogAndCompare(title, expectedAnnuityReceivePeriod, actualAnnuityReceivePeriod);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_PERIOD;
            throw new SetAnnuityAgeException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void crawlAnnuityPremium(CrawlingProduct info) throws CommonCrawlerException {

        String title = "연금수령액 크롤링";
        PlanAnnuityMoney planAnnuityMoney = info.getPlanAnnuityMoney();
        WebElement $button = null;

        try {
            List<WebElement> trs_종신형 = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//h2[contains(.,'종신')][1]//following-sibling::div[contains(@class,'tbl-sub')][1]//tbody/tr")
            );

            List<WebElement> trs_확정형 = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//h2[contains(.,'확정')][1]//following-sibling::div[contains(@class,'tbl-sub')][1]//tbody/tr")
            );

            trs_종신형.forEach( tr -> {
                Actions actions = new Actions(driver);
                WebElement targetEl = helper.getWebElement(tr);
                actions.moveToElement(targetEl);
                actions.perform();

                String annType = tr.findElements(By.tagName("td")).get(0).getText();
                String amount = tr.findElements(By.tagName("td")).get(1).getText();
                String annAmount = amount.replaceAll("만원", "0000").replaceAll("\\D", "");

                if (annType.contains("10회")) {
                    planAnnuityMoney.setWhl10Y(annAmount);
                    info.annuityPremium = annAmount;
                } else if (annType.contains("20회")) {
                    planAnnuityMoney.setWhl20Y(annAmount);
                } else if (annType.contains("30회")) {
                    planAnnuityMoney.setWhl30Y(annAmount);
                } else if (annType.contains("100세")) {
                    planAnnuityMoney.setWhl100A(annAmount);
                }
            });

            trs_확정형.forEach( tr -> {

                Actions actions = new Actions(driver);
                WebElement targetEl = helper.getWebElement(tr);
                actions.moveToElement(targetEl);
                actions.perform();

                String annType = tr.findElements(By.tagName("td")).get(0).getText();
                String amount = tr.findElements(By.tagName("td")).get(1).getText();
                String annAmount = amount.replaceAll("만원", "0000").replaceAll("\\D", "");

                if (annType.contains("10년")) {
                    planAnnuityMoney.setFxd10Y(annAmount);
                    info.fixedAnnuityPremium = annAmount;
                } else if (annType.contains("15년")) {
                    planAnnuityMoney.setFxd15Y(annAmount);
                } else if (annType.contains("20년")) {
                    planAnnuityMoney.setFxd20Y(annAmount);
                } else if (annType.contains("25년")) {
                    planAnnuityMoney.setFxd25Y(annAmount);
                } else if (annType.contains("30년")) {
                    planAnnuityMoney.setFxd30Y(annAmount);
                }

            });

            info.planAnnuityMoney = planAnnuityMoney;

            logger.info("|---보증--------------------");
            logger.info("|-- 10년 보증 :: {}", planAnnuityMoney.getWhl10Y());
            logger.info("|-- 20년 보증 :: {}", planAnnuityMoney.getWhl20Y());
            logger.info("|-- 30년 보증 :: {}", planAnnuityMoney.getWhl30Y());
            logger.info("|-- 100세 보증 :: {}", planAnnuityMoney.getWhl100A());
            logger.info("|---확정--------------------");
            logger.info("|-- 10년 확정 :: {}", planAnnuityMoney.getFxd10Y());
            logger.info("|-- 15년 확정 :: {}", planAnnuityMoney.getFxd15Y());
            logger.info("|-- 20년 확정 :: {}", planAnnuityMoney.getFxd20Y());
            logger.info("|-- 25년 확정 :: {}", planAnnuityMoney.getFxd25Y());
            logger.info("|-- 30년 확정 :: {}", planAnnuityMoney.getFxd30Y());
            logger.info("--------------------------");

            logger.info("연금수령액 테이블 스크랩 :" + planAnnuityMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    protected void reCalculate(By by) throws CommonCrawlerException {

        try {
            WaitUtil.loading(1);
            WebElement $button = driver.findElement(by);

            if ($button.isDisplayed() && $button.isEnabled()) {
                click($button);
                waitLoadingBar();
            } else {
                logger.info("변경사항 없음");
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e, "다시 계산하기 버튼 클릭중 오류 발생");
        }
    }



    public void crawlReturnMoneyList1(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = (By) obj[1];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
        WebElement $a = null;

        try {

            $a = driver.findElement(By.xpath("//a[text()='해약환급금']"));
            click($a);

            // 해약환급금 tbody > tr List 불러오기
            List<WebElement> trs = new ArrayList<>();
            trs = driver.findElements(location);

            // tr List 순회하면서 PlanReturnMoneyList 스크랩
            setPlanReturnMoneyList(
                info,
                trs,
                () -> new PlanReturnMoneyTdIdx(0,1,2,3));

            info.returnPremium = info.planReturnMoneyList.get(info.planReturnMoneyList.size()-1).getReturnMoney().replaceAll("[^0-9]", "");;

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    public void crawlReturnMoneyList2(Object... obj) throws ReturnMoneyListCrawlerException {


        CrawlingProduct info = (CrawlingProduct) obj[0];
        int planNum = (int) obj[1];
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
        WebElement $a = null;

        try{
            $a = driver.findElement(By.xpath("//a[text()='해약환급금']"));
            click($a);

            List<WebElement> trs = driver.findElements(By.xpath("//tbody[@id='returnCancel']//tr"));

            for (WebElement tr : trs) {

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                helper.moveToElementByJavascriptExecutor(tr);

                List<WebElement> tds = tr.findElements(By.tagName("td"));
                List<WebElement> tds2 = tr.findElements(By.cssSelector("td.data" + planNum));

                String term = tds.get(0).getAttribute("innerHTML");
                String premiumSum = tds2.get(0).getAttribute("innerHTML");
                String returnMoney = tds2.get(1).getAttribute("innerHTML");
                String returnRate = tds2.get(2).getAttribute("innerHTML");

                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.info("해약환급금 테이블 스크랩 : " + planReturnMoneyList);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    public void crawlReturnMoneyList3(Object... obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        WebElement $a = null;

        try{
            $a = driver.findElement(By.xpath("//a[text()='해약환급금']"));
            click($a);

            // 해약환급금 tbody > tr List 불러오기, 공시
            List<WebElement> trs_default = driver.findElements(By.xpath("//tbody[@id='returnCancel1']//tr"));
            List<WebElement> trs_avg = driver.findElements(By.xpath("//tbody[@id='returnCancel2']//tr"));
            List<WebElement> trs_min = driver.findElements(By.xpath("//tbody[@id='returnCancel3']//tr"));

            // tr List 순회하면서 PlanReturnMoneyList 스크랩
            setPlanReturnMoneyList(info, trs_default,
                () -> new PlanReturnMoneyTdIdx(0, 1, 2, 3));

            info.returnPremium = info.planReturnMoneyList.get(info.planReturnMoneyList.size()-1).getReturnMoney().replaceAll("[^0-9]", "");

            setPlanReturnMoneyList(info, trs_avg,
                () -> new PlanReturnMoneyTdIdx(
                    0, 1,
                    -1, -1,
                    -1, -1,
                    2, 3));

            setPlanReturnMoneyList(info, trs_min,
                () -> new PlanReturnMoneyTdIdx(
                    0, 1,
                    -1, -1,
                    2, 3,
                    -1, -1));

            LinkedHashMap<String, PlanReturnMoney> map = new LinkedHashMap<>();
            List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();
            for(PlanReturnMoney p : planReturnMoneyList) {
                String term = p.getTerm();

                if(!map.containsKey(term)) {
                    map.put(term, p);
                }
            }

            List<PlanReturnMoney> newPlanReturnMoneyList = new ArrayList<>();
            for(String key : map.keySet()) {
                newPlanReturnMoneyList.add(map.get(key));
            }

            info.setPlanReturnMoneyList(newPlanReturnMoneyList);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }



    protected void setPlanReturnMoneyList(CrawlingProduct info, List<WebElement> trs, Supplier<PlanReturnMoneyTdIdx> getTdIdx) {

        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        PlanReturnMoneyTdIdx tdIdx = getTdIdx.get();
        int termIdx = tdIdx.getTerm();                  // 납입기간
        int premiumSumIdx = tdIdx.getPremiumSum();      // 합계 보험료
        int rMoneyIdx = tdIdx.getReturnMoney();         // 환급금(공시)
        int rRateIdx = tdIdx.getReturnRate();           // 환급률(공시)
        int rMoneyMinIdx = tdIdx.getReturnMoneyMin();   // 환급금(최저)
        int rRateMinIdx = tdIdx.getReturnRateMin();     // 환급률(최저)
        int rMoneyAvgIdx = tdIdx.getReturnMoneyAvg();   // 환급금(평균)
        int rRateAvgIdx = tdIdx.getReturnRateAvg();     // 환급률(평균)

        Map<String, PlanReturnMoney> planReturnMoneyMap = new HashMap<>();
        planReturnMoneyList.forEach( i -> planReturnMoneyMap.put(i.getTerm(), i));

        for (WebElement tr : trs) {

            helper.moveToElementByJavascriptExecutor(tr);

            List<WebElement> tds = tr.findElements(By.tagName("td"));

            String term = tds.get(termIdx).getAttribute("innerHTML");
            PlanReturnMoney planReturnMoney = planReturnMoneyMap.getOrDefault(term, new PlanReturnMoney(term));

            if (premiumSumIdx != -1) planReturnMoney.setPremiumSum(tds.get(premiumSumIdx).getAttribute("innerHTML"));
            if (rMoneyIdx != -1) planReturnMoney.setReturnMoney(tds.get(rMoneyIdx).getAttribute("innerHTML"));
            if (rRateIdx != -1) planReturnMoney.setReturnRate(tds.get(rRateIdx).getAttribute("innerHTML"));
            if (rMoneyMinIdx != -1) planReturnMoney.setReturnMoneyMin(tds.get(rMoneyMinIdx).getAttribute("innerHTML"));
            if (rRateMinIdx != -1) planReturnMoney.setReturnRateMin(tds.get(rRateMinIdx).getAttribute("innerHTML"));
            if (rMoneyAvgIdx != -1) planReturnMoney.setReturnMoneyAvg(tds.get(rMoneyAvgIdx).getAttribute("innerHTML"));
            if (rRateAvgIdx != -1) planReturnMoney.setReturnRateAvg(tds.get(rRateAvgIdx).getAttribute("innerHTML"));

            planReturnMoneyList.add(planReturnMoney);
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);
        logger.info("해약환급금 테이블 스크랩 : " +
            planReturnMoneyList);
    }



    protected void checkOptionalTreatyAssureMoney(Object... obj) throws CrawlingException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        String planNum = ObjectUtils.isEmpty(obj[1]) ? null : (String) obj[1];

        try {

            // 선택특약 리스트
            List<CrawlingTreaty> treatyList =
                info.getTreatyList().stream().filter(
                    treaty -> !treaty.productGubun.equals(ProductGubun.주계약)
                ).collect(Collectors.toList());

            // 팝업이 로딩될 때까지 기다리기
            helper.waitVisibilityOfAllElementsLocatedBy(By.id("uiPOPProInfo"));

            // 각 특약명 (보장내용 테이블의 제목)
            List<WebElement> treatyTitleList = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("div.tit-sub3 h2"));

            for (CrawlingTreaty treaty : treatyList) { // 선택특약 리스트 가입금액 원수사 페이지와 비교
                String treatyName = treaty.getTreatyName();
                int assureMoney = treaty.getAssureMoney();

                treatyTitleList.stream() // 원수사 페이지 보장내용테이블 제목 순회
                    .filter(h2 -> { // 특약과 매치되는 제목요소 찾기
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", h2);
                        return h2.getText().contains(treatyName);
                    }).findFirst().ifPresent(h2 ->
                    {
                        // 해당 제목 아래있는 보장내용 테이블
                        WebElement tbody = h2
                            .findElement(By.xpath("./parent::div/following-sibling::div[1]/table/tbody"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tbody);

                        // 보장내용 테이블에서 해당 플랜의 지급금액 더하기 (대부분 1개 행이지만 다수개의 행이 있는 경우도 대응 가능하도록 더한다.
                        int sum = 0;
                        List<WebElement> trs = tbody.findElements(By.tagName("tr"));
                        for (WebElement tr : trs) {
                            String partialMoney = tr.findElement(By.cssSelector("td.data" + planNum)).getText(); // 지급금액
                            sum += MoneyUtil.toDigitMoney(partialMoney).intValue();
                        }

                        String msg = treatyName + "의 가입금액 재확인이 필요합니다. :: " +
                            "가입설계에 등록된 assureMoney = " + assureMoney +
                            "원수사 지급금액 sum = " + sum;

                        if (assureMoney != sum) throw new RuntimeException(msg);
                    }
                );
            } // treatyList loop end

        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
            throw new CrawlingException(e.getMessage());
        }

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



    protected void alert() throws Exception {

        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            throw new Exception(alert.getText());
        }
    }



    //로딩바 명시적 대기
    public void waitLoadingBar() {

        try {
            helper.waitForCSSElement("#uiPOPLoading1");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}