package com.welgram.crawler.direct.life.hkl;

import com.welgram.common.PersonNameGenerator;
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
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.ScrapableNew;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;


public class CrawlingHKLAnnounce extends SeleniumCrawler implements ScrapableNew {

    protected static List<PlanReturnMoneyField> returnMoneyFields_4 = new ArrayList<>();
    {
        returnMoneyFields_4.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.term, "경과기간"));
        returnMoneyFields_4.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.premiumSum, "납입보험료"));
        returnMoneyFields_4.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.returnMoney, "해약환급금"));
        returnMoneyFields_4.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.returnRate, "환급율(%)"));
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        setName();
        setGender(info.getGenderEnum());
        setBirthday(info.getFullBirth());

        if (info.productCode.contains("ANT")) {
            setType(info.getTextType());
            setNapTerm(info);
            setAnnuityAge(info);
            setAnnuityCycle();
            setAnnuityType(info);
            setAssureMoney(info, By.id("bhRyo"));

            calculatePremium();
            scrollToBottom();
            takeScreenShot(info);

//            crawlAnnuityPremium(info);

        } else {
            setType(info.getTextType());
            setInsTerm(info);
            setNapTerm(info);
            setNapCycle(info);
            setAssureMoney(info);
            setTreaties(info);

            calculatePremium();
            scrollToBottom();
            takeScreenShot(info);

            crawlPremium(info);
            crawlReturnMoneyList(info, returnMoneyFields_4);
            crawlReturnPremium(info);
        }

        return true;
    }



    private void setAnnuityCycle() throws CommonCrawlerException {

        try {
            helper.click(
                By.xpath("//*[@id='anJiCycl']/option[contains(text(),'매년')]"),
                "연금지급주기 선택 : 매년"
            );

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    public void setName() throws CommonCrawlerException {

        try {
            String name = PersonNameGenerator.generate();
            helper.sendKeys3_check(By.id("custNm"), name);

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    public void setType(String type) throws CommonCrawlerException {

        try {
            helper.selectByText_check(By.id("bhCd"), type, "보험종류");
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try {
            String birthday = (String) obj[0];
            helper.sendKeys3_check(By.id("birthday"), birthday, "생년월일");
        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try {
            CrawlingProduct.Gender genderEnum = (CrawlingProduct.Gender) obj[0];

            logger.info("====================");
            logger.info("성별 :: {}", genderEnum.getDesc());
            logger.info("====================");

            helper.click(By.xpath("//input[@name='gender' and @value='" + genderEnum.getIncrementedOrder() + "']"));

            String actual = (String) helper.executeJavascript("return $(\"input[name='gender']:checked\").val()");
            printLogAndCompare("성별",
                genderEnum.getIncrementedOrder(),
                actual
            );

        } catch (Exception e) {
            throw new SetGenderException(e);
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
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String ins;
            ins = info.insTerm.replace("년", "").replace("세", "");

            if (ins.equals("종신보장")) {
                ins = "999";
            }

            helper.selectByValue_check(By.id("bhTerm"), ins);

        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String nap = info.napTerm;
            String AnnAge = info.annuityAge;

            switch (nap) { // todo 정리 필요
                case "전기납":
                case "일시납":
                    nap = nap.replace("납", "");
            }

            if (!info.productCode.equals("HKL_MDC_F002") &&
                (nap.replace("세", "").equals(AnnAge) || nap.equals(info.insTerm))) {
                nap = "전기";
            }

            helper.selectByText_check(By.id("niTerm"), (nap + "납"), "납입기간");

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            if (info.napCycle.equals("00")) {
                info.napCycle = "일시납";

            } else if (info.napCycle.equals("01")) {
                info.napCycle = "월납";
            }

            helper.selectByText_check(By.id("niCycl"), info.napCycle, "납입주기");

        } catch (Exception e) {
            throw new SetNapCycleException(e);
        }
    }



    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {

    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            By by = obj.length > 1 ? (By) obj[1] : By.id("bhAmt");

            String assureMoney = String.valueOf(Integer.parseInt(info.assureMoney) / 10000);
            logger.info("====================");
            logger.info("가입금액 :: {}", info.assureMoney);
            logger.info("====================");

            element = driver.findElement(by);
            element.sendKeys(assureMoney);

        } catch (Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {

    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            element
                = driver
                    .findElement(By.cssSelector(
                        "#frmPage > dd.dd_first > div.table_wrap.overflow > table > tfoot > tr > td > ul > li:nth-child(1) > span")
                    );
            //  premium에 '원'이 붙어서 원을 제거해 주었습니다.
            String premium
                = element.getText().replace(",", "").replace("원", "").replace("만", "");
            logger.info("월 보험료: " + premium + "원");
            logger.info("====================");
            info.treatyList.get(0).monthlyPremium = premium;

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoneyField> fields = (List<PlanReturnMoneyField>) obj[1];

            // 해약환급금보기 창변환
            logger.info("해약환급금보기 창변환");
            logger.info("====================");
            helper.click(By.cssSelector(".first li:nth-child(3) strong"));

            logger.info("해약환급금 가져오기");
            logger.info("====================");
            WaitUtil.loading(3);

            // todo 변수 처리 필요
//            WebElement theadTr = helper.waitPresenceOfElementLocated(By.cssSelector(
//                "#frmPage > dd.dd_third > div.table_wrap.overflow > table > thead > tr"));
            WebElement theadTr
                = helper.waitPresenceOfElementLocated(By.xpath("//th[text()='경과기간']/ancestor::table//thead//tr"));

            // todo 변수 처리 필요
            List<WebElement> theadThs = theadTr.findElements(By.tagName("th"));

            // set td index
            fields.forEach((field) -> {
                for (int i = 0; i < theadThs.size(); i++) {
                    if (theadThs.get(i).getText().startsWith(field.getTdText())) {
                        field.setTdIndex(i);
                        break;
                    }
                }
            });

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            // todo 변수 처리 필요
//            List<WebElement> tbodyTr = driver.findElements(By.cssSelector(
//                "#frmPage > dd.dd_third > div.table_wrap.overflow > table > tbody > tr"));
            List<WebElement> tbodyTr = driver.findElements(By.xpath(
                "//th[text()='경과기간']/ancestor::table//tbody//tr"));

            for (WebElement tr : tbodyTr) {

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                for (PlanReturnMoneyField field : fields) {
                    PlanReturnMoneyFieldEnum fieldEnum = field.getFieldEnum();
                    int tdIndex = field.getTdIndex();

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.term)) {
                        // todo setter 내용 변수 처리 필요
                        planReturnMoney.setTerm(tr.findElements(By.tagName("td")).get(tdIndex).getText());
                        logger.info("경과기간 : {} ", planReturnMoney.getTerm());
                    }

                    // todo | case02
                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.premiumSum)) {
                        planReturnMoney.setPremiumSum(
                            tr.findElements(By.tagName("td"))
                                .get(tdIndex)
                                .getText()
                                .replaceAll("[^0-9]", "")
                        );
                        logger.info("납입보험료 : {} ", planReturnMoney.getPremiumSum());
                    }

                    // todo | case01
                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnMoney)) {
                        planReturnMoney.setReturnMoney(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText().replaceAll("[^0-9]", "")
                        );
                        logger.info("현공시이율 환급금 : {} ", planReturnMoney.getReturnMoney());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnRate)) {
                        planReturnMoney.setReturnRate(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText()
                        );
                        logger.info("현공시이율 환급률 : {} ", planReturnMoney.getReturnRate());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnMoneyMin)) {
                        planReturnMoney.setReturnMoneyMin(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText().replaceAll("[^0-9]", "")
                        );
                        logger.info("최저보증이율 환급금 : {} ", planReturnMoney.getReturnMoneyMin());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnRateMin)) {
                        planReturnMoney.setReturnRateMin(tr.findElements(By.tagName("td")).get(tdIndex).getText());
                        logger.info("최저보증이율 환급률 : {} ", planReturnMoney.getReturnRateMin());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnMoneyAvg)) {
                        planReturnMoney.setReturnMoneyAvg(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText().replaceAll("[^0-9]", "")
                        );
                        logger.info("평균공시이율 환급금 : {} ", planReturnMoney.getReturnMoneyAvg());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnRateAvg)) {
                        planReturnMoney.setReturnRateAvg(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText()
                        );
                        logger.info("평균공시이율 환급률 : {} ", planReturnMoney.getReturnRateAvg());
                    }
                }

                logger.info("===========================================");
                planReturnMoneyList.add(planReturnMoney);
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);
            info.returnPremium
                = planReturnMoneyList
                    .get(planReturnMoneyList.size() - 1)
                    .getReturnMoney()
                    .replace(",", "")
                    .replace("원", "");

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    @NotNull
    private PlanReturnMoney getResult(WebElement tr) {

        List<PlanReturnMoneyField> fields = new ArrayList<>();
        fields.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.term, "경과기간"));
        fields.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.premiumSum, "납입보험료"));
        fields.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.returnMoney, "해약환급금"));
        fields.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.returnRate, "환급율(%)"));

        List<WebElement> tds = tr.findElements(By.tagName("td"));

        // set td index
        fields.forEach((field) -> {
            for (int i = 0; i < tds.size(); i++) {
                if (tds.get(i).getText().startsWith(field.getTdText())) {
                    field.setTdIndex(i);
                    break;
                }
            }
        });

        for (PlanReturnMoneyField field : fields) {

            // todo 작업중? - 담당자 여쭤보기
            if (field.getFieldEnum().equals(PlanReturnMoneyFieldEnum.term)) {

            }
        }

        String term;
        String premiumSum;
        String returnMoney;
        String returnRate;
        String returnMoneyAvg;
        String returnRateAvg;
        String returnMoneyMin;
        String returnRateMin;

        PlanReturnMoney planReturnMoney = new PlanReturnMoney();

        term = tr.findElements(By.tagName("td")).get(0).getText();
        premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "");
        returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
        returnRate = tr.findElements(By.tagName("td")).get(3).getText();

        returnMoneyAvg = tr.findElements(By.tagName("td")).get(4).getText().replaceAll("[^0-9]", "");
        returnRateAvg = tr.findElements(By.tagName("td")).get(5).getText();

        returnMoneyMin = tr.findElements(By.tagName("td")).get(6).getText().replaceAll("[^0-9]", "");
        returnRateMin = tr.findElements(By.tagName("td")).get(7).getText();

        logger.info(term + " :: 납입보험료 :: " + premiumSum + " :: 해약환급금 :: " + returnMoney);
        logger.info("========================================================================");

        logger.info("|--경과기간: {}", term);
        logger.info("|--납입보험료(공시): {}", premiumSum);
        logger.info("|--해약환급금(공시): {}", returnMoney);
        logger.info("|--해약환급률(공시): {}", returnRate);

        logger.info("|--해약환급금(평균): {}", returnMoneyAvg);
        logger.info("|--해약환급률(평균): {}", returnRateAvg);

        logger.info("|--해약환급금(최저): {}", returnMoneyMin);
        logger.info("|--해약환급률(최저): {}", returnRateMin);
        logger.info("|_______________________");

        planReturnMoney.setTerm(term);
        planReturnMoney.setPremiumSum(premiumSum);

        planReturnMoney.setReturnMoney(returnMoney);
        planReturnMoney.setReturnRate(returnRate);

        planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
        planReturnMoney.setReturnRateAvg(returnRateAvg);

        planReturnMoney.setReturnMoneyMin(returnMoneyMin);
        planReturnMoney.setReturnRateMin(returnRateMin);

        return planReturnMoney;
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

        int planCalcAge
            = info.getCategoryName().equals("태아보험")
                ? 0
                : Integer.parseInt(info.age.replaceAll("\\D", ""));

        // 만기에 해당하는 환급금이 있는지 확인
        for (int i = planReturnMoneyList.size() - 1; i > 0; i--) {

            PlanReturnMoney planReturnMoney = planReturnMoneyList.get(i);
            String term = planReturnMoney.getTerm();

            // 경과기간이 개월단위인 경우는 일단 제외
            if (term.contains("개월")) {
                continue;
            }

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

/*    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int planCalcAge = info.getCategoryName().equals("태아보험") ? 0
                : Integer.parseInt(info.age.replaceAll("\\D", ""));

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
                    insTermNumberValue = Integer.parseInt(info.annAge.replaceAll("[^0-9]", ""));
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
    }*/



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            helper.click(
                By.xpath("//*[@id='anGsiAge']/option[contains(text(),'" + info.annuityAge + "')]"),
                "연금개시나이 클릭 : " + info.annuityAge
            );

        } catch (Exception e) {
            throw new SetAnnuityAgeException(e);
        }
    }



    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String annuityPaymentString =
                info.insTerm.replace("보장", "") + "연금형(개인," + info.napTerm + "보증형)";

            helper.click(
                By.xpath("//*[@id='antyCd']/option[contains(text(),'" + annuityPaymentString + "')]"),
                "연금지급형태 클릭 : " + annuityPaymentString
            );

        } catch (Exception e) {
            throw new SetAnnuityTypeException(e);
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

    }



    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }



    protected void setTreaties(CrawlingProduct info) throws Exception { // todo : 특약선택 refactoring

        boolean hasTreaties =
            info.getTreatyList().stream()
                .anyMatch(treaty -> !treaty.productGubun.equals(ProductGubun.주계약));

        if (!hasTreaties) {
            return;
        }

        List<CrawlingTreaty> treaties = new ArrayList<>();
        for (CrawlingTreaty item : info.treatyList) {
            String treatyName = item.treatyName;

            // 특약정보입력표의 tr
            elements = helper.waitVisibilityOfAllElements(
                driver.findElement(By.id("tblTkyk"))
                    .findElements(By.cssSelector("tbody tr")));

            boolean set = false;
            for (WebElement tr : elements) {
                WebElement treaty = tr.findElement(By.cssSelector("td:nth-of-type(2) div"));

                // 담보명 일치할 경우
                if (treaty.getText().equals(treatyName)) {
                    info.siteProductMasterCount++; // 등록된 담보명과 같은지 검증하는 카운트
                    WebElement checkBox = tr.findElement(
                        By.cssSelector("td:nth-of-type(1) input[name='tkykChk']")); // 체크박스
                    WebElement TreatyAssureMoney = tr.findElement(By.id("tkykBhAmt")); // 특약가입금액

                    logger.info("특약 :: {} 선택", treaty.getText());

                    // 체크가 안돼있을 경우 클릭
                    if (!checkBox.isSelected()) {
                        checkBox.click();
                    }

                    TreatyAssureMoney.clear();
                    TreatyAssureMoney.click();
                    TreatyAssureMoney.clear();
                    TreatyAssureMoney.sendKeys(Integer.toString(item.assureMoney / 10000));
                    logger.info("가입금액 :: {} 만원", item.assureMoney / 10000);

                    set = true;
                }
            }

            if (!set) {
                treaties.add(item);
            }
        }

        treaties.forEach((treaty) -> logger.info("선택하지 못한 특약 : " + treaty.treatyName));
        if (treaties.isEmpty()) {
            throw new Exception("설정하지 못한 특약이 있습니다.");
        }
    }



    protected void calculatePremium() throws CommonCrawlerException {

        try {
            logger.info("====================");
            logger.info("계산하기");
            logger.info("====================");

            helper.click(driver.findElement(By.cssSelector("div.btn_box.mt20"))
                .findElement(By.linkText("계산하기")));
            WaitUtil.loading(2);
            Alert alert = driver.switchTo().alert();
            String message = alert.getText();

            if (message.contains("보험료를 계산하시겠습니까?")) {
                alert.accept();
            } else {
                throw new CommonCrawlerException(message);
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    protected void scrollToBottom() {

        logger.info("====================");
        logger.info("스크롤 내리기");
        logger.info("====================");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight);");
    }



    protected void crawlAnnuityPremium(CrawlingProduct info, By by) {

        String annuityPremium;
        String fixedAnnuityPremium;
        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

        elements = driver.findElements(by);

        for (int i = 0; i < elements.size(); i++) {
            if (i == 1) { // 종신 10년
                annuityPremium = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                if (info.annuityType.contains("10년") && info.annuityType.contains("종신")) {
                    info.annuityPremium = annuityPremium; // 매년 종신연금형 10년  보증
                    logger.info("종신연금수령액 :: {} ", annuityPremium);
                }

                planAnnuityMoney.setWhl10Y(annuityPremium);
                logger.info("종신10년 :: " + annuityPremium);

            } else if (i == 2) { // 종신 20년
                String Whl20 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", ""); // 매년 종신연금형 20년 보증
                if (info.annuityType.contains("20년") && info.annuityType.contains("종신")) {
                    info.annuityPremium = Whl20;
                }
                logger.info("종신20년 :: " + Whl20);
                planAnnuityMoney.setWhl20Y(Whl20);

            } else if (i == 3) { // 종신 30년
                String Whl30 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", ""); // 매년 종신연금형 30년 보증
                logger.info("종신30년 :: " + Whl30);
                planAnnuityMoney.setWhl30Y(Whl30);

            } else if (i == 4) { // 종신 100세
                String Whl100 = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", ""); // 매년 종신연금형 100세 보증
                logger.info("종신100세 :: " + Whl100);
                planAnnuityMoney.setWhl100A(Whl100);

            } else if (i == 13) { // 확정 10년
                fixedAnnuityPremium = elements.get(i).findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "");
                if (info.annuityType.contains("10년") && info.annuityType.contains("확정")) {
                    info.fixedAnnuityPremium = fixedAnnuityPremium;
                    logger.info("확정연금수령액 :: {} ", fixedAnnuityPremium);
                }
                planAnnuityMoney.setFxd10Y(fixedAnnuityPremium); // 확정 10년
                logger.info("확정10년 :: " + fixedAnnuityPremium);

            }
            // 2022.04.15 기준 원수사 연금수령액테이블 개편으로 확정 15,확정 20년의 위치 변경
            else if (i == 14) { // 확정 15년
                String Fxd15 = elements.get(i).findElements(By.tagName("td")).get(2).getText(); // 매년 확정연금형 15년
                Fxd15 = Fxd15.replaceAll("[^0-9]", "");
                logger.info("확정15년 :: " + Fxd15);

                planAnnuityMoney.setFxd15Y(Fxd15);

            } else if (i == 15) { // 확정 20년
                String Fxd20 = elements.get(i).findElements(By.tagName("td")).get(2).getText(); // 매년 확정연금형 20년
                Fxd20 = Fxd20.replaceAll("[^0-9]", "");
                if (info.annuityType.contains("20년") && info.annuityType.contains("확정")) {
                    info.fixedAnnuityPremium = Fxd20;
                }
                logger.info("확정20년 :: " + Fxd20);
                logger.info("===================");
                planAnnuityMoney.setFxd20Y(Fxd20);
            }
        }

        info.planAnnuityMoney = planAnnuityMoney;
    }
}
