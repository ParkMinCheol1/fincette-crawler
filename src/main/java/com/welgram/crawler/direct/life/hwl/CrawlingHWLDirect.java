package com.welgram.crawler.direct.life.hwl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.Job;
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
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy1;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.direct.life.hkl.PlanReturnMoneyField;
import com.welgram.crawler.direct.life.hkl.PlanReturnMoneyFieldEnum;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.ScrapableNew;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;



public class CrawlingHWLDirect extends SeleniumCrawler implements ScrapableNew {

    protected static List<PlanReturnMoneyField> returnMoneyFields_4 = new ArrayList<>();
    {
        returnMoneyFields_4.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.term, "경과 기간"));
        returnMoneyFields_4.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.premiumSum, "납입보험료"));
        returnMoneyFields_4.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.returnMoney, "해약환급금"));
        returnMoneyFields_4.add(new PlanReturnMoneyField(PlanReturnMoneyFieldEnum.returnRate, "환급률"));
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        return false;
    }



    protected void clickCalcButton(By position) throws CommonCrawlerException {

        try {
            driver.findElement(position).click();
            WaitUtil.waitFor(2);
            waitLoading();
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    public void applyConditions() throws CommonCrawlerException {

        try {
            String $buttonString = "//*[@id='condition-modal-footer']//button[contains(.,'선택 조건으로 적용하기')]\"), \"선택 조건으로 적용하기";

            if (helper.existElement(By.xpath($buttonString))){
                helper.click(By.xpath(
                    "//*[@id='condition-modal-footer']//button[contains(.,'선택 조건으로 적용하기')]"), "선택 조건으로 적용하기");
            } else {
                helper.click(By.xpath(
                    "//*[@id='condition-modal-footer']//button"), "확인");
            }
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    protected void setType(String type) throws CommonCrawlerException {

        try {
            String selectedVal = "";
            By by = By.xpath("//div[contains(@class,'tabs-container')]//div[@role='tablist']//button[contains(.,'" + type + "')]");
            helper.moveToElementByJavascriptExecutor(by);
            helper.click(helper.waitElementToBeClickable(by));

            selectedVal = getSelectedVal(by);
            printLogAndCompare(type, selectedVal);
            WaitUtil.loading(3);

            if (type.equals("내가 직접 설계") || type.equals("나의 플랜")) {
                helper.click(By.id("product-design-btn"), "보험료 설계하기 버튼");
                WaitUtil.waitFor(2);
            }
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    protected void setDiscountType(Object... obj) throws CommonCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            try{
                WebElement displayedResultPanel = getDisplayedResultPanel("div[id^='result']");
                WebElement button = displayedResultPanel.findElement(By.xpath(".//span[text()='흡연여부']/ancestor::li//button"));
                if (!button.isEnabled()) { return; }
                helper.click(button);

            } catch (Exception e){
                logger.info("result panel 없음");
            }

            List<WebElement> spans
                = helper.waitPesenceOfAllElementsLocatedBy(By.xpath("//input[@name='흡연여부']/following-sibling::label//span[@class='text']"));

            WebElement matched = spans.stream().filter(span -> {
                String spanDiscount = span.getText();
                String welgramDiscount = info.getDiscount().name();
                if (welgramDiscount.equals("일반")) { welgramDiscount = "흡연";}

                return spanDiscount.equals(welgramDiscount);
            }).findFirst().orElseThrow(() -> new SetAssureMoneyException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "흡연여부");

            WebElement $button = driver.findElement(By.xpath("//footer//span[text()='확인']/ancestor::button"));
            if ($button.isEnabled()){
                helper.click($button, "확인 버튼");
                waitLoading();
                WaitUtil.waitFor(5);
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SMOKE;
            throw new SetNapTermException(exceptionEnum.getMsg() + "\n" + e.getMessage());
        }
    }



    @NotNull
    private static WebElement getDisplayedResultPanel(String cssSelector) {

        new WebDriverWait(driver, 10).until(
            webDriver -> {
                List<WebElement> elementList = webDriver.findElements(By.cssSelector(cssSelector));
                return elementList.stream().anyMatch(WebElement::isDisplayed);
            });

        return driver.findElements(By.cssSelector(cssSelector))
            .stream().filter(WebElement::isDisplayed).findFirst().orElseThrow(
                () -> new RuntimeException("화면에 나타난 결과 패널이 없습니다."));
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try {
            By position = (By) obj[0];
            String birthday = (String) obj[1];

            WebElement birthDayEl = helper.waitElementToBeClickable(position);
            birthDayEl.clear();
            birthDayEl.sendKeys(birthday);

            printLogAndCompare(birthday, driver.findElement(position).getAttribute("value"));
            WaitUtil.loading(1);

        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }



    protected void printLogAndCompare(String welgramValue, String siteValue) {

        logger.info("Welgram Value : " + welgramValue);
        logger.info("Site Value : " + siteValue);

        boolean isSame = welgramValue.equals(siteValue);
        logger.info("Compare Result : " + isSame);

        if (!isSame) { throw new RuntimeException("set value is not same with welgram value"); }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try {
            String genderText = String.valueOf(Gender.values()[(int) obj[0]]).equals("M") ? "남자" : "여자";
            By defaultBy = By.xpath("//label[text()='성별']/following-sibling::div//span[text()='" + genderText + "']");
            By position = (obj[1] == null) ? defaultBy : (By) obj[1];

            if (position == null){
                driver.findElement(defaultBy).click();
            } else {
                driver.findElement(position).click();
            }

            printLogAndCompare(genderText, driver.findElement(position).getText());
            WaitUtil.loading(1);

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }



    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {}



    @Override
    public void setJob(Object... obj) throws SetJobException {

        try{
            String job = Job.MANAGER.getCodeValue();

            element = driver.findElement(By.id("realloss_SearchJob"));
            element.click();
            WaitUtil.waitFor(2);

            // 검색바
            element = driver.findElement(By.id("scJob_search"));
            element.clear();
            element.sendKeys(job);

            // 검색
            element = driver.findElements(By.className("btn04")).get(0);
            element.click();
            WaitUtil.waitFor(2);

            // 경영지원 사무직 관리자
            element = driver.findElement(By.id("scResultSearchJob"));
            element = element.findElements(By.tagName("li")).get(0);
            element.click();
            WaitUtil.waitFor(2);

            printLogAndCompare(job, element.getText());

            // 확인
            element = driver.findElements(By.className("btn05")).get(0);
            element.click();
            WaitUtil.waitFor(2);

        } catch (Exception e){
            throw new SetJobException(e);
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            try {
                WebElement displayedResultPanel = getDisplayedResultPanel("div[id^='result']");
                WebElement button = displayedResultPanel.findElement(By.xpath(".//span[text()='보험기간']/ancestor::li//button"));

                if (!button.isEnabled()) { return; }
                helper.click(button);

            } catch (Exception e){
                logger.info("result panel 없음");
            }

            List<WebElement> spans
                = helper.waitPesenceOfAllElementsLocatedBy(
                    By.xpath("//input[@name='보험기간']/following-sibling::label//span[@class='text']"));

            WebElement matched = spans.stream().filter(span -> {
                String spanInsTerm = span.getText();
                String welgramInsTerm = info.getInsTerm();

                return spanInsTerm.equals(welgramInsTerm);
            }).findFirst().orElseThrow(() -> new SetInsTermException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "보험기간");

            // todo 검증
            WebElement $button = driver.findElement(By.xpath("//footer//span[text()='확인']/ancestor::button"));
            if ($button.isEnabled()){
                helper.click($button, "확인 버튼");
            }

        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            try{
                WebElement displayedResultPanel = getDisplayedResultPanel("div[id^='result']");
                WebElement button = displayedResultPanel.findElement(By.xpath(".//span[text()='납입기간']/ancestor::li//button"));

                if (!button.isEnabled()) return;
                helper.click(button);

            } catch (Exception e){
                logger.info("result panel 없음");
            }

            List<WebElement> spans
                = helper.waitPesenceOfAllElementsLocatedBy(
                    By.xpath("//input[@name='납입기간']/following-sibling::label//span[@class='text']")
                );

            WebElement matched = spans.stream().filter(span -> {
                String spanNapTerm = span.getText();
                String welgramNapTerm = info.getNapTerm();

                return spanNapTerm.equals(welgramNapTerm);
            }).findFirst().orElseThrow(() -> new SetNapTermException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "납입기간");

            // todo 검증
            WebElement $button = driver.findElement(By.xpath("//footer//span[text()='확인']/ancestor::button"));
            if ($button.isEnabled()){
                helper.click($button, "확인 버튼");
            }

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {}



    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {}



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            List<WebElement> spans
                = helper.waitPesenceOfAllElementsLocatedBy(
                    By.xpath("//*[normalize-space()='보장금액' or normalize-space()='월 보험료']/following-sibling::div//span[@class='text']"));

            WebElement matched = spans.stream().filter(span -> {
                long spanMoney = MoneyUtil.toDigitMoney2(span.getText());
                long assureMoney = info.getTreatyList().get(0).getAssureMoney();
                return spanMoney == assureMoney;
            }).findFirst().orElseThrow(() -> new SetAssureMoneyException("일치하는 보장금액이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "보장금액");
            WaitUtil.loading(2);

            // todo 검증

        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {

        try {
            String refundType = (String) obj[0];

            helper.click(helper.waitElementToBeClickable(By.xpath(
                "//div[@role='dialog']//label[normalize-space()='보험종류']/parent::div//span[@class='text'][contains(.,'" + refundType + "')]")), "보험종류");

            // todo 검증
            WaitUtil.loading(1);

        } catch (Exception e) {
            throw new SetRefundTypeException(e);
        }
    }



    /**
     * 특약 설정
     * @param obj
     * @throws CommonCrawlerException
     */
    public void setTreaties(Object... obj) throws CommonCrawlerException {
        List<CrawlingTreaty> welgramTreatyList = (List<CrawlingTreaty>) obj[0];

        try{
            // 특약비교용 복사본
            List<CrawlingTreaty> copiedWelgramTreatyList = new ArrayList<>(welgramTreatyList);
            List<CrawlingTreaty> targetTreatyList = new ArrayList<>();
            List<WebElement> homepageTreatyList = new ArrayList<>();

            helper.click(helper.waitElementToBeClickable(By.xpath("//*[@id='coverage-change-btn']")), "보장항목 변경하기 버튼");
            WebElement displayedResultPanel = getDisplayedResultPanel("div[role^='dialog']");
            WaitUtil.loading(3);
            homepageTreatyList = displayedResultPanel.findElements(By.xpath("//label[@class='css-iubdmf']"));

            for (int i = 0; i < homepageTreatyList.size(); i++) {
                WebElement homepageTreaty = homepageTreatyList.get(i);
                String homepageTreatyNameText = homepageTreaty.getText().trim();
                logger.info("특약명 :: {}", homepageTreatyNameText);

                if (copiedWelgramTreatyList.size() == 0) {
                    try {
                        helper.click(helper.waitElementToBeClickable(homepageTreaty), "체크해제");
                    } catch (Exception e) {
                        helper.moveToElementByJavascriptExecutor(homepageTreatyList.get(i - 3));
                        helper.click(helper.waitElementToBeClickable(homepageTreaty), "체크해제");
                    }
                    continue;
                }

                for (int j = 0; j < copiedWelgramTreatyList.size(); j++) {
                    CrawlingTreaty welgramTreaty = copiedWelgramTreatyList.get(j);
                    String welgramTreatyName = welgramTreaty.getTreatyName().trim();
                    boolean isExist = false;

                    // 체크박스
                    WebElement checkBox = homepageTreaty.findElement(By.xpath(".//input"));

                    if (welgramTreatyName.equals(homepageTreatyNameText)) {
                        if (!checkBox.isSelected()) {
                            helper.click(homepageTreaty, "체크");
                            isExist = true;
                        }

                        // 가입금액 설정
                        By homepageTreatyNameBy = By.xpath("//div[@role='dialog']//label[@class='css-iubdmf' and normalize-space()='" + welgramTreatyName + "']");
                        WebElement homepageTreatyNameEl = driver.findElement(homepageTreatyNameBy);
                        String welgramTreatyAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());

                        setTreatyAssureMoney(homepageTreatyNameBy, welgramTreatyAssureMoney);
                        WebElement homepageTreatyMoneyButton = homepageTreatyNameEl.findElement(By.xpath("./following-sibling::button"));

                        String homepageTreatyMoney = homepageTreatyMoneyButton.getText().trim().replaceAll(",", "");
                        homepageTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney2(homepageTreatyMoney));

                        CrawlingTreaty targetTreaty = new CrawlingTreaty();
                        targetTreaty.setTreatyName(welgramTreatyName);
                        targetTreaty.setAssureMoney(Integer.parseInt(homepageTreatyMoney));

                        targetTreatyList.add(targetTreaty);
                        copiedWelgramTreatyList.remove(j);
                        j = j - 1;

                        break;
                    }

                    // 가설에 일치하는 특약이 없을 경우
                    if (((j == copiedWelgramTreatyList.size() - 1 ) && !isExist)) {

                        if (checkBox.isSelected()) {
                            try {
                                helper.click(helper.waitElementToBeClickable(homepageTreaty), "체크해제");
                            } catch (Exception e) {
                                helper.moveToElementByJavascriptExecutor(homepageTreatyList.get(i - 3));
                                helper.click(helper.waitElementToBeClickable(homepageTreaty), "체크해제");
                            }
                        }
                    }

                }
            }

            logger.info("특약 비교 및 확인");
            boolean result = advancedCompareTreaties(targetTreatyList, welgramTreatyList, new CrawlingTreatyEqualStrategy1());

            if (result) {
                logger.info("특약 정보가 모두 일치합니다");
            } else {
                logger.error("특약 정보 불일치");
                throw new Exception();
            }

            By reCalcButtonBy = By.xpath("//button[@id='re-calc-btn']");
            By saveButtonBy = By.xpath("//footer[@id='plan-save-info-modal-footer']//span[normalize-space()='저장하기']");

            if (helper.existElement(reCalcButtonBy)) { helper.click(reCalcButtonBy, "다시 계산하기 버튼"); }
            helper.click(By.xpath("//button[@id='save-plan-btn']"), "플랜 저장하기 버튼");
            if (helper.existElement(saveButtonBy)) { helper.click(saveButtonBy, "저장하기 버튼"); }

        } catch (Exception e) {
            throw new CommonCrawlerException("특약 설정 중 오류가 발생했습니다.\n" + e.getMessage());
        }
    }



    /**
     * 특약 가입금액 설정
     * @param treatyNameBy
     * @return money
     * @throws CommonCrawlerException
     */
    public void setTreatyAssureMoney(By treatyNameBy, String welgramTreatyAssureMoney) throws CommonCrawlerException {

        try {
            WebElement treatyNameEl = driver.findElement(treatyNameBy);
            WebElement treatyAssureMoneyButton = treatyNameEl.findElement(By.xpath("./following-sibling::button"));

            String treatyName = treatyNameEl.getText().trim();
            String homepageTreatyMoney = treatyAssureMoneyButton.getText().trim().replaceAll(",", "");
            homepageTreatyMoney = String.valueOf(MoneyUtil.toDigitMoney2(homepageTreatyMoney));

            if (!welgramTreatyAssureMoney.equals(homepageTreatyMoney)) {
                try {
                    treatyAssureMoneyButton.click();
                } catch (Exception e) {
                    helper.moveToElementByJavascriptExecutor(treatyAssureMoneyButton.findElement(By.xpath("./ancestor::ul[1]")));
                    treatyAssureMoneyButton.click();
                }

                List<WebElement> moneyButtonLiList = driver.findElements(By.xpath("//h3[normalize-space()='" + treatyName + "']//following-sibling::div//li"));
                for (WebElement moneyButtonLi : moneyButtonLiList) {
                    String moneyButtonText = moneyButtonLi.getText();
                    moneyButtonText = String.valueOf(MoneyUtil.toDigitMoney(moneyButtonText.substring(0, moneyButtonText.indexOf("\n")).trim()));

                    if (moneyButtonText.equals(welgramTreatyAssureMoney)) {
                        logger.info("특약명 :: {}", treatyName);
                        logger.info("선택된 가입금액 :: {}", moneyButtonText);
                        moneyButtonLi.click();

                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new CommonCrawlerException("특약 가입금액 설정 중 오류가 발생했습니다.\n" + e.getMessage());
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            String defaultPath = "p.price";
            String byPath = (obj[1] == null) ? defaultPath : (String) obj[1];

            WaitUtil.loading(5);
            helper.waitElementToBeClickable(By.cssSelector(byPath));
            WebElement displayedElement = getDisplayedResultPanel(byPath);

            String premium = displayedElement.getText().replaceAll("[^0-9]", "");
            logger.info("==================================");
            logger.info("월 보험료 : {}", premium);
            logger.info("==================================");

            info.treatyList.get(0).monthlyPremium = premium;

            takeScreenShot(info);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<PlanReturnMoneyField> fields = (List<PlanReturnMoneyField>) obj[1];
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            By tapPosition = By.xpath(
                "//*[@id='pc-product-info-container']//div[@role='tablist']//button[text()='해약환급금']");
            helper.moveToElementByJavascriptExecutor(tapPosition);
            WaitUtil.loading(2);
            helper.click(tapPosition, "해약환급금 탭");

            By button = By.xpath("//button[contains(.,'전체 기간 펼쳐보기')]");
            helper.waitElementToBeClickable(button).click();

            WebElement table
                = helper.waitPresenceOfElementLocated(By.xpath("//div[contains(@id,'해약환급금')]//table"));

            // todo 변수 처리 필요
            WebElement theadTr = table.findElement(By.cssSelector("thead tr"));

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

            // todo 변수 처리 필요
            List<WebElement> tbodyTr = table.findElements(By.cssSelector("tbody tr"));

            for (WebElement tr : tbodyTr) {

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                for (PlanReturnMoneyField field : fields) {
                    PlanReturnMoneyFieldEnum fieldEnum = field.getFieldEnum();
                    int tdIndex = field.getTdIndex();

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.term)) {
                        planReturnMoney.setTerm( // todo setter 내용 변수 처리 필요
                            tr.findElements(By.tagName("td")).get(tdIndex).getText());

                        logger.info("경과기간 : {} ", planReturnMoney.getTerm());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.premiumSum)) {
                        planReturnMoney.setPremiumSum(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText()
                                .replaceAll("[^0-9]", ""));

                        logger.info("납입보험료 : {} ", planReturnMoney.getPremiumSum());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnMoney)) {
                        planReturnMoney.setReturnMoney(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText()
                                .replaceAll("[^0-9]", ""));

                        logger.info("현공시이율 환급금 : {} ", planReturnMoney.getReturnMoney());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnRate)) {
                        planReturnMoney.setReturnRate(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText());

                        logger.info("현공시이율 환급률 : {} ", planReturnMoney.getReturnRate());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnMoneyMin)) {
                        planReturnMoney.setReturnMoneyMin(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText()
                                .replaceAll("[^0-9]", ""));

                        logger.info("최저보증이율 환급금 : {} ", planReturnMoney.getReturnMoneyMin());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnRateMin)) {
                        planReturnMoney.setReturnRateMin(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText());

                        logger.info("최저보증이율 환급률 : {} ", planReturnMoney.getReturnRateMin());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnMoneyAvg)) {
                        planReturnMoney.setReturnMoneyAvg(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText()
                                .replaceAll("[^0-9]", ""));

                        logger.info("평균공시이율 환급금 : {} ", planReturnMoney.getReturnMoneyAvg());
                    }

                    if (fieldEnum.equals(PlanReturnMoneyFieldEnum.returnRateAvg)) {
                        planReturnMoney.setReturnRateAvg(
                            tr.findElements(By.tagName("td")).get(tdIndex).getText());

                        logger.info("평균공시이율 환급률 : {} ", planReturnMoney.getReturnRateAvg());
                    }
                }

                logger.info("===========================================");
                planReturnMoneyList.add(planReturnMoney);
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            String returnMoney = info.getPlanReturnMoneyList().get(info.getPlanReturnMoneyList().size() - 1).getReturnMoney();
            info.returnPremium = returnMoney.replace(",", "").replace("원", "");

            logger.info("만기환급금 :: {} ", returnMoney);
            logger.info("=========================================");

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }



    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {

        try{
            By position = (By) obj[0];
            String annAge = (String) obj[1];
            String selectedVal = "";

            helper.waitElementToBeClickable(position);
            helper.selectByText_check(position, annAge);
            WaitUtil.loading(1);

            selectedVal = getSelectedVal(position);
            printLogAndCompare(annAge, selectedVal);

        } catch (Exception e){
            throw new SetAnnuityAgeException(e);
        }
    }



    @Override
    public void setAnnuityType(Object... obj) throws SetAnnuityTypeException {

        try{
            By position = (By) obj[0];
            String type = ((String) obj[1]).replaceAll(" ", "");

            elements = driver.findElements(position);

            for (WebElement option : elements) {
                String optionText = option.getText().replace(" ", "").replace("(", "").replace(")", "").replace("기간", "");

                if (optionText.contains(type.replace(" ", ""))) {
                    logger.info(option.getText() + "클릭!");
                    option.click();

                    // 검증
                    if (!type.equals(optionText)){
                        type = type + "보증";
                    }
                    printLogAndCompare(type, optionText);
                    break;
                }
            }
            WaitUtil.loading(1);

        } catch (Exception e){
            throw new SetAnnuityTypeException(e);
        }
    }



    public void crawlAnnuityPremium(By $tableLocation, CrawlingProduct info) throws CommonCrawlerException {

        try{
            WebElement $table = driver.findElement($tableLocation);
            PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

            String fixedAnnuityPremium10 = numberFormat($table.findElement(
                By.cssSelector("tbody > tr:nth-child(7) > td.last"))); // 확정 10년
            String fixedAnnuityPremium15 = numberFormat($table.findElement(
                By.cssSelector("tbody > tr:nth-child(8) > td.last"))); //확정 15년
            String fixedAnnuityPremium20 = numberFormat($table.findElement(
                By.cssSelector("tbody > tr:nth-child(9) > td.last"))); //확정 20년

            String annuitypremium10 = numberFormat($table.findElement(
                By.cssSelector("tbody > tr:nth-child(3) > td.last")));  // 종신 10년
            String annuitypremium20 = numberFormat($table.findElement(
                By.cssSelector("tbody > tr:nth-child(4) > td.last")));  //종신 20년
            String annuitypremium100 = numberFormat($table.findElement(
                By.cssSelector("tbody > tr:nth-child(5) > td.last"))); //종신 100세

            // 종신형
            planAnnuityMoney.setWhl10Y(annuitypremium10);      //종신 10년
            planAnnuityMoney.setWhl20Y(annuitypremium20);      //종신 20년
            planAnnuityMoney.setWhl100A(annuitypremium100);    //종신 100세

            // 확정형
            planAnnuityMoney.setFxd10Y(fixedAnnuityPremium10);    //확정 10년
            planAnnuityMoney.setFxd15Y(fixedAnnuityPremium15);    //확정 15년
            planAnnuityMoney.setFxd20Y(fixedAnnuityPremium20);    //확정 20년

            if (info.annuityType.contains("10년")) {
                info.fixedAnnuityPremium = fixedAnnuityPremium10; // 확정 10년
                info.annuityPremium = annuitypremium10;           // 종신 10년
            } else if (info.annuityType.contains("20년")) {
                info.fixedAnnuityPremium = fixedAnnuityPremium20; // 확정 20년
                info.annuityPremium = annuitypremium20;           // 종신 20년
            }

            logger.info("====================================");
            logger.info("연금수령액 :: : " + info.annuityPremium);
            logger.debug("확정연금액 :: : " + info.fixedAnnuityPremium);
            logger.info("====================================");

            logger.info("종신10년 :: " + planAnnuityMoney.getWhl10Y());
            logger.info("종신20년 :: " + planAnnuityMoney.getWhl20Y());
            logger.info("종신100세 :: " + planAnnuityMoney.getWhl100A());
            logger.info("확정10년 :: " + planAnnuityMoney.getFxd10Y());
            logger.info("확정15년 :: " + planAnnuityMoney.getFxd15Y());
            logger.info("확정20년 :: " + planAnnuityMoney.getFxd20Y());
            logger.info("====================================");

            info.planAnnuityMoney = planAnnuityMoney;

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(exceptionEnum.getMsg() + "\n" + e);
        }
    }



    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {}



    @Override
    public void setUserName(Object... obj) throws SetUserNameException {}



    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {}



    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {}



    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

        try{
            List<WebElement> spans
                = helper.waitPesenceOfAllElementsLocatedBy(
                    By.xpath("//input[@name='상품유형']/following-sibling::label//span[@class='text']"));

            WebElement matched = spans.stream().filter(span -> {
                String spanDiscount = span.getText();
                String welgramDiscount = "일반형";

                return spanDiscount.equals(welgramDiscount);
            }).findFirst().orElseThrow(() -> new SetAssureMoneyException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "상품유형");

            // todo 검증
            helper.click(
                helper.waitElementToBeClickable(
                    By.xpath("//footer[@id='condition-modal-footer']/button")), "확인 버튼");
            waitLoading();

        } catch (Exception e){
            throw new SetProductTypeException(e);
        }
    }



    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {}



    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {}



    protected void waitLoading() throws Exception{

        logger.info("wait loading...");
        helper.invisibilityOfElementLocated(By.xpath("//div[@id='loadingPop']"));
        WaitUtil.waitFor(2);
    }



    // 담보명 확인
    protected void checkProductMaster(CrawlingProduct info, String el) {

        try {
            for (CrawlingTreaty item : info.treatyList) {
                String treatyName = item.treatyName;
                String prdtName = driver.findElement(By.cssSelector(el)).getText();
                prdtName = prdtName.replaceAll("(\r\n|\r|\n|\n\r)", " ");

                if (treatyName.contains(prdtName)) {
                    info.siteProductMasterCount++;
                    logger.info("담보명 :: " + treatyName);
                    logger.info("담보명 확인 완료 !! ");
                }
            }
        } catch (Exception e) {
            logger.info("담보명 확인 에러 발생 !!");
        }
    }



    protected void ageChk(CrawlingProduct info) throws Exception {
        // 최대 가입 연령 = (연금개시나이 - 납입기간)세
        int maxAge = Integer.parseInt(info.annuityAge) - Integer.parseInt(info.napTerm.replaceAll("년", "").trim());
        if (maxAge < Integer.parseInt(info.age)) {
            throw new Exception("최대 가입 나이 초과");
        }
    }



    // 연금수령액 & 확정연금액 - 연금보험
    protected String numberFormat(WebElement el) throws Exception {

        String formatStr = "";

        if (el.getText().contains("억")) {
            formatStr = el.getText().replace("억", "0").replaceAll("[^0-9]", "") + "0000";
        } else {
            formatStr = el.getText().replaceAll("[^0-9]", "") + "0000";
        }

        return formatStr;
    }



    // 만 19세 ~ 68세 alert 확인창 뜰 경우
    protected void checkAgePopup() throws Exception{

        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            if (alert.getText().contains("만 19세 ~ 68세")){
                logger.info(alert.getText());
                throw new CommonCrawlerException("보험가입 나이를 확인해주세요.");
            }
        }
    }



    /**
     * 선택된 값 반환
     *
     * @param   by      실제 선택된 엘리먼트 by
     * @throws  CommonCrawlerException
     */
    public String getSelectedVal(By by) throws CommonCrawlerException {

        try{
            WebElement selectedElement = driver.findElement(by);
            // 실제 입력된 값
            String selectedValue = "";
            String script = "return $(arguments[0]).find('option:selected').text();";

            if (selectedElement.getTagName().equals("select")){
                selectedValue = String.valueOf(helper.executeJavascript(script,selectedElement));
            } else {
                selectedValue = selectedElement.getText().trim();

                if (selectedValue.equals("")){
                    script = "return $(arguments[0]).val();";
                    selectedValue = String.valueOf(helper.executeJavascript(script, selectedElement));
                }
            }

            return selectedValue;

        } catch (Exception e){
            throw new CommonCrawlerException("선택된 옵션을 찾던 중 에러 발생 \n" + e.getMessage());
        }
    }



    // 보장내용 체크
    // TODO 유효성 검사 필요. 현재는 일괄 체크
    public void setCoverage(By boxBy) throws CommonCrawlerException {

        try{
            driver.manage().window().maximize();
            helper.waitVisibilityOf(driver.findElement(boxBy));
            List <WebElement> checkBoxList = driver.findElements(boxBy);

            for (int i = 0; i < checkBoxList.size(); i++){
                WebElement checkBox = checkBoxList.get(i);
                helper.moveToElementByJavascriptExecutor(checkBox.findElement(By.xpath("./ancestor::li")));

                if (checkBox.isEnabled() && !checkBox.isSelected()){
                    helper.click(checkBox);
                }
            }

            By buttonPath = By.xpath("//button[@id=\"popup-insurance-calculation-signup-btn\"]");
            if (helper.existElement(buttonPath)) {
                helper.click(buttonPath, "설계내역 확인하기");
            } else {
                helper.click(By.xpath("//*[@id=\"re-calc-btn\"]//span[text()='다시 계산하기']"));
            }

            helper.click(By.xpath("//*[@id=\"save-plan-btn\"]//span[text()='플랜 저장하기']"));

        } catch (Exception e){
            throw new CommonCrawlerException("보장내용 선택 중 에러 발생\n" + e.getMessage());
        }
    }

}