package com.welgram.crawler.direct.fire.ltf;

import com.google.gson.Gson;
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
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.common.strategy.TreatyNameComparators;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.ScrapableNew;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CrawlingLTFAnnounce extends SeleniumCrawler implements ScrapableNew {


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        enterPage(info);
        setBirthday(info);
        setGender(info);
        setJob();
        clickNext();

        setInsTerm(info);
        setNapTerm(info);
        clickNext();

        setTreaties(info);
        compareTreaties(info);
        calculate();

        crawlPremium(info);
        crawlReturnMoneyList(info);
        crawlReturnPremium(info);

        return true;
    }

    protected void clickNext() throws CommonCrawlerException {
        try {
            helper.click(By.xpath("//a[text()='다음']"));
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    protected void calculate() throws CommonCrawlerException {
        try {
            helper.click(By.xpath("//a[text()='보험료 산출']"));
            helper.waitForLoading(By.xpath("//img[@alt='loading']"));
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    protected void compareTreaties(CrawlingProduct info) throws CommonCrawlerException {
        try {
            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("#priceLA-step2-idambo-tbody tr")
            );

            List<CrawlingTreaty> siteTreatyList = new ArrayList<>();

            for (WebElement tr : trs) {

                CrawlingTreaty siteTreaty = new CrawlingTreaty();

                WebElement checkBox = tr.findElement(By.cssSelector("td.alignR.lst"))
                    .findElement(By.cssSelector("input[type='checkbox']"));
                if (checkBox.isSelected()) {
                    // 특약명
                    String siteTreatyName = tr.findElement(By.xpath(".//td/label")).getText();
                    siteTreaty.setTreatyName(siteTreatyName);

                    // 보험기간
/*                    String selectedInsterm = tr.findElement(By.cssSelector("td.dambo-ndcd.alignC"))
                        .findElement(By.cssSelector("option[selected]"))
                        .getAttribute("innerHTML")
                        .trim().replaceAll("만기", "");*/

                    WebElement $insTermTd = tr.findElement(By.cssSelector("td.dambo-ndcd.alignC"));
                    WebElement $insTermSelect = $insTermTd.findElement(By.tagName("select"));
                    String selectedInsterm;

                    if ($insTermSelect.isDisplayed()) {
                        selectedInsterm = $insTermSelect
                            .findElement(By.cssSelector("option[selected]"))
                            .getAttribute("innerHTML");
                    } else {
                        selectedInsterm = $insTermTd.getText();
                    }

                    selectedInsterm = selectedInsterm.trim().replaceAll("만기", "");
                    selectedInsterm = Integer.parseInt(selectedInsterm.replaceAll("\\D", "")) // ex) 03 -> 3 만들기
                            + selectedInsterm.replaceAll("\\d", ""); // 년 또는 세 단위 붙이기
                    siteTreaty.setInsTerm(selectedInsterm);

                    // 납입기간
                    String selectedNapTerm = tr.findElement(By.cssSelector("td.dambo-pymTrmcd.alignC")).getText();
                    if (selectedNapTerm.equals("전기납")) {
                        siteTreaty.setNapTerm(siteTreaty.insTerm);
                    } else {
                        selectedNapTerm = Integer.parseInt(selectedNapTerm.replaceAll("\\D", "")) // ex) 03 -> 3 만들기
                            + selectedNapTerm.replaceAll("\\d", "").replaceAll("납", ""); // 년 또는 세 단위 붙이기
                        siteTreaty.setNapTerm(selectedNapTerm);
                    }

                    // 가입금액
                    int assureMoney;
                    WebElement innerEl = tr.findElement(By.cssSelector("[name='isamt']")); // td 안의 input 또는 select
                    if (innerEl.isDisplayed()) {
                        assureMoney = Integer.parseInt(innerEl.getAttribute("value")) * 10000;
                    } else {
                        WebElement outerEl = innerEl.findElement(By.xpath("./ancestor::td")); // td
                        assureMoney = Integer.parseInt(outerEl.getText()) * 10000;
                    }
                    siteTreaty.setAssureMoney(assureMoney);

                    siteTreatyList.add(siteTreaty);
                }
            }

            boolean isEqual = advancedCompareTreaties(siteTreatyList, info.treatyList, new CrawlingTreatyEqualStrategy2());

            if (!isEqual) {
                throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_TREATIES_COMPOSIOTION);
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    protected void setTreaties(CrawlingProduct info) throws CommonCrawlerException {
        try {
            List<CrawlingTreaty> treatyList = info.getTreatyList();

            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("#priceLA-step2-idambo-tbody tr")
            );

            for (CrawlingTreaty treaty : treatyList) {

                logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                logger.info("특약명 :: " + treaty.treatyName);

                // 특약명이 동일한 tr 찾기
                Optional<WebElement> matchedTr = trs.stream().filter(tr -> {
                    WebElement checkBox = tr.findElement(By.cssSelector("td.alignR.lst"));
                    String siteTreatyName = checkBox.getAttribute("title");
                    String welgramTreatyName = treaty.treatyName;
                    return TreatyNameComparators.allApplied.equals(siteTreatyName, welgramTreatyName);
                }).findFirst();

                // 동일한 tr이 존재한다면,
                if (matchedTr.isPresent()) {
                    WebElement tr = matchedTr.get();

                    // 체크박스 클릭
                    WebElement checkBox = tr.findElement(By.cssSelector("td.alignR.lst"));
                    if (!checkBox.isSelected()) {
                        helper.moveToElementByJavascriptExecutor(checkBox);
                        helper.click(checkBox);
                        logger.info("체크박스 :: 체크");
                    } else {
                        logger.info("체크박스 이미 클릭됨");
                    }

                    // 보기 선택
                    WebElement instermSelect =
                        tr.findElement(By.cssSelector("td.dambo-ndcd.alignC"))
                            .findElement(By.tagName("select"));
                    if (instermSelect.isDisplayed()) {
                        helper.selectByText_check(instermSelect, treaty.insTerm + "만기");
                        logger.info("보험기간 :: " + treaty.insTerm + "만기");
                    }

                    // 납기 선택
                    WebElement napTermSelect =
                        tr.findElement(By.cssSelector("td.dambo-pymTrmcd.alignC"))
                            .findElement(By.tagName("select"));
                    if (napTermSelect.isDisplayed()) {
                        String napTerm = treaty.napTerm.replaceAll("\\D", "").equals(
                            treaty.insTerm.replaceAll("\\D", ""))
                            ? "전기납" : (treaty.napTerm + "납");
                        helper.selectByText_check(napTermSelect, napTerm);
                        logger.info("납입기간 :: " + napTerm);
                    }

                    // 가입금액 입력
                    WebElement assureMoneyEl = tr.findElement(By.cssSelector("[name='isamt']"));
                    int assureMoney = treaty.assureMoney / 10000;
                    switch (assureMoneyEl.getTagName()) {
                        case "input":
                            helper.sendKeys3_check(assureMoneyEl, String.valueOf(assureMoney));
                            break;
                        case "select":
                            if (assureMoneyEl.findElements(By.tagName("option")).size() > 1) {
                                helper.selectByText_check(assureMoneyEl, String.valueOf(assureMoney));
                                logger.info("가입금액:: {}만원", assureMoney);
                            } else {
                                logger.info("가입금액:: 이 고정입니다.");
                            }
                    }
                    logger.info("선택한 가입금액 :: {}만원", assureMoney);
                }
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    protected void setMDCTreaties(CrawlingProduct info) throws CommonCrawlerException {
        try {

            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("#priceLA-step2-idambo-tbody tr")
            );

            for (WebElement tr : trs) {
                // 실손은 체크박스 다 클릭
                WebElement checkBox = tr.findElement(By.cssSelector("td.alignR.lst"));
                helper.moveToElementByJavascriptExecutor(checkBox);
                helper.click(checkBox, "체크박스 클릭");

                // 가입설계 특약명과 동일한 tr인지 확인
                Optional<CrawlingTreaty> matchedTreaty = info.treatyList.stream().filter(treaty -> {
                    String siteTreatyName = checkBox.getAttribute("title").trim();
                    String welgramTreatyName = treaty.treatyName;

                    return TreatyNameComparators.allApplied.equals(siteTreatyName, welgramTreatyName);
                }).findFirst();

                // 가입설계 특약명과 동일하다면 가입금액 설정하기
                if (matchedTreaty.isPresent()) {
                    // 가입금액 입력
                    WebElement assureMoneyEl = tr.findElement(By.cssSelector("[name='isamt']"));
                    int assureMoney = matchedTreaty.get().assureMoney / 10000;
                    switch (assureMoneyEl.getTagName()) {
                        case "input":
                            helper.sendKeys3_check(assureMoneyEl, String.valueOf(assureMoney));
                            break;
                        case "select":
                            if (assureMoneyEl.findElements(By.tagName("option")).size() > 1) {
                                helper.selectByText_check(assureMoneyEl, String.valueOf(assureMoney));
                                logger.info("가입금액:: {}만원", assureMoney);
                            } else {
                                logger.info("가입금액:: 이 고정입니다.");
                            }
                    }
                    logger.info("선택한 가입금액 :: {}만원", assureMoney);
                }
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    protected void enterPage(CrawlingProduct info) throws CommonCrawlerException {
        try {
            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("tbody#before tr"));

            boolean isExist = false;
            for (WebElement tr : trs) {
                WebElement treatyNameTd = tr.findElement(By.cssSelector("td.alignL"));
                WebElement buttonTd = tr.findElement(By.cssSelector("td.alignC"));
                if (treatyNameTd.getText().equals(info.productName)) {
                    helper.click(buttonTd);
                    isExist = true;

                    helper.switchToWindow(
                        driver.getWindowHandle(),
                        driver.getWindowHandles(), false);
                    break;
                }
            }

            if (!isExist) {
                throw new CommonCrawlerException("해당 상품이 존재하지 않습니다. : " + info.productName);
            }
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            helper.sendKeys3_check(By.id("PBirth"), info.getFullBirth());
        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int gender = info.gender;
            if (gender == 0) {
                helper.click(By.cssSelector("#PIsdsex1_1"));
            } else {
                helper.click(By.cssSelector("#PIsdsex1_2"));
            }
        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {

    }

    @Override
    public void setJob(Object... obj) throws SetJobException {

        try {
            if (obj.length > 0) {
                By by = (By) obj[0];
                helper.click(by);
            } else {
                helper.click(By.cssSelector("#Jbnm"));
            }

            WaitUtil.loading(1);

            new WebDriverWait(driver, 3).until(
                ExpectedConditions.numberOfWindowsToBe(2));

            helper.switchToWindow(driver.getWindowHandle(), driver.getWindowHandles(), true);

            // 교사 검색
            helper.sendKeys3_check(
                By.cssSelector("input[title='직업명']"), "교사");
            helper.click(
                By.cssSelector("#content > div.section_udline > p > span > a"),
                "검색 버튼 클릭");

            // 검색결과에서 보건교사 선택
            logger.info("검색된 직업 선택");
            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("#addr_list > tr"));
            WebElement matchedTr = trs.stream().filter(
                    tr -> tr.findElement(By.cssSelector("td:nth-child(1) > a")).getText()
                        .equals("보건 교사"))
                .findFirst().orElseThrow(() -> new SetJobException("직업이 존재하지 않습니다. : 보건 교사"));
            helper.click(matchedTr.findElement(By.cssSelector("td:nth-child(1) > a")));

            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            WaitUtil.loading(1);

        } catch (Exception e) {
            throw new SetJobException(e);
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            helper.findExistentElement(By.id("ndcd"), 2L)
                .ifPresent(select -> {
                    if (select.isDisplayed()) {
                        try {
                            helper.selectOptionByClick(select, info.insTerm + "만기");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        helper.waitForLoading(By.xpath("//img[@alt='loading']"));
                    }
                });

        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            WebElement select = helper.waitPresenceOfElementLocated(By.cssSelector("select[title='납입기간']"));
            if (select.isDisplayed()) {
                helper.selectOptionByClick(select, info.napTerm + "납");
            }
            helper.waitForLoading(By.xpath("//img[@alt='loading']"));

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
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
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String premium = helper.waitPresenceOfElementLocated(By.id("gnPrm")) // gnPrm
                .getText()
                .replaceAll("[^0-9]", "");

            info.getTreatyList().get(0).monthlyPremium = premium;
            logger.info("월 보험료 :: {}원", premium);

            String savePremium;
            savePremium = helper.waitPresenceOfElementLocated(By.id("dcbfCuPrm")).getText()
                .replaceAll("[^0-9]", "");
            info.savePremium = savePremium;
            logger.info("적립보험료 : " + savePremium + "원");

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            helper.click(By.xpath("//a[text()='환급금조회']"));
            helper.waitForLoading(By.xpath("//img[@alt='loading']"));
            helper.switchToWindow(driver.getWindowHandle(), driver.getWindowHandles(), true);


            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            elements = helper.waitPresenceOfElementLocated(By.id("refund-tbody"))
                .findElements(By.tagName("tr"));
            for (WebElement tr : elements) {
                String term = tr.findElements(By.tagName("td")).get(0).getText();
                logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText()
                    .replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);
                String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText()
                    .replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
                String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
                logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);
                String returnMoney = tr.findElements(By.tagName("td")).get(4).getText()
                    .replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
                String returnRate = tr.findElements(By.tagName("td")).get(5).getText();
                logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
                String returnMoneyAvg = tr.findElements(By.tagName("td")).get(6).getText()
                    .replaceAll("[^0-9]", "");
                logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
                String returnRateAvg = tr.findElements(By.tagName("td")).get(7).getText();
                logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
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
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

            logger.info("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));


        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            Optional<PlanReturnMoney> returnMoneyOptional = getPlanReturnMoney(info);

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

    private Optional<PlanReturnMoney> getPlanReturnMoney(CrawlingProduct info) {

        Optional<PlanReturnMoney> returnMoneyOptional = Optional.empty();
        List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

        int planCalcAge = info.getCategoryName().equals("태아보험") ? 0
            : Integer.parseInt(info.age.replaceAll("\\D", ""));

        // 만기에 해당하는 환급금이 있는지 확인
        for (int i = planReturnMoneyList.size() - 1; i >= 0; i--) {

            PlanReturnMoney planReturnMoney = planReturnMoneyList.get(i);
            String term = planReturnMoney.getTerm();

            // 경과기간이 개월단위인 경우는 일단 제외
            if (term.contains("개월")) continue;

            if (term.equals("만기")) {
                returnMoneyOptional = Optional.of(planReturnMoney);
                break;
            }

            // 해약환급금 행에서 경과기간 추출 (년단위로 변환)
            int annualTerm = getAnnualTerm(term, planCalcAge);

            // 해당 가설(info)의 보험기간 추출 (년단위로 변환)
            int annualInsTerm = getAnnaulInsTerm(info, planCalcAge);

            // 경과기간이 만기에 해당하는지 여부 반환
            if (annualTerm == annualInsTerm) {

                logger.info("만기환급금 크롤링 :: 카테고리 :: {}", info.categoryName);
                logger.info("만기환급금 크롤링 :: 가설 케이스 나이 :: {}세", planCalcAge);
                logger.info("만기환급금 크롤링 :: 가설 보험기간 :: {}", info.insTerm);
                logger.info("만기환급금 크롤링 :: 가설 납입기간 :: {}", info.napTerm);
                logger.info("만기환급금 크롤링 :: 해약환급금 해당 경과기간 :: {}", planReturnMoney.getTerm());

                returnMoneyOptional = Optional.of(planReturnMoney);
            }


        }

        return returnMoneyOptional;
    }

    private static int getAnnualTerm(String term, int planCalcAge) {
        int annualTerm = -1;

        String termUnit = term.indexOf("년") > term.indexOf("세") ? "년" : "세";
        int termUnitIndex = term.indexOf(termUnit);
        int termNumberValue = Integer.parseInt(
            term.substring(0, termUnitIndex).replaceAll("\\D", ""));

        switch (termUnit) {
            case "년":
                annualTerm = termNumberValue;
                break;
            case "세":
                annualTerm = termNumberValue - planCalcAge;
                break;
        }

        return annualTerm;
    }

    private static int getAnnaulInsTerm(CrawlingProduct info, int planCalcAge) {

        int annaulInsTerm;
        String insTermUnit;
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

        switch (insTermUnit) {
            case "년":
                annaulInsTerm = insTermNumberValue;
                break;
            case "세":
                annaulInsTerm = insTermNumberValue - planCalcAge;
                break;
            default:
                annaulInsTerm = -1;
        }
        return annaulInsTerm;
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

    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }

}
