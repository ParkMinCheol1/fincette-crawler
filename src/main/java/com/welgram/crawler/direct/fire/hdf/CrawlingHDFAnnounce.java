package com.welgram.crawler.direct.fire.hdf;

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
import com.welgram.common.strategy.TreatyNameComparators;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.ScrapableNew;
import com.welgram.util.InsuranceUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class CrawlingHDFAnnounce extends SeleniumCrawler implements ScrapableNew {

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        enterPage(info.productNamePublic);

        if (info.productCode.contains("_BAB_")) {

            helper.click(By.xpath("//label[@for='chk_isBaby']"), "태아 체크박스 클릭");
            helper.click(By.xpath("//div[@class='dialog_alert']//button[text()='확인']"), "알람창 확인 버튼 클릭");
            setBirthday(InsuranceUtil.getDateOfBirth(12));

        } else {
            setBirthday(info.fullBirth);
            setGender(info.gender);
            setJob("보건 교사");
        }

        setPlanType(info.textType);
        setNapCycle(info.getNapCycleName());
        setNapTerm(info);
        setTreaties(info);
        compareTreaties(info);
        crawlPremium(info);
        crawlReturnPremium(info);
        crawlReturnMoneyList(info);

        return true;
    }



    private void compareTreaties(CrawlingProduct info) throws CommonCrawlerException {
        try {
            List<CrawlingTreaty> welgramTreatyList = info.getTreatyList();
            List<CrawlingTreaty> homepageTreatyList = new ArrayList<>();

            List<WebElement> $checkboxes = driver.findElements(
                By.cssSelector("#tby_damboList input[name=chk_nabgi]:checked"));
            for (WebElement $checkbox : $checkboxes) {
                WebElement $tr = $checkbox.findElement(By.xpath("./ancestor::tr[1]"));
                WebElement $treatyName = $tr.findElement(By.xpath("./td[4]"));
                WebElement $treatyAssureMoney = $tr.findElement(By.xpath("./td[6]/input"));
                String treatyName = $treatyName.getText().trim();
                String treatyAssureMoney = "";

                String script = "return $(arguments[0]).val();";
                treatyAssureMoney = String.valueOf(
                    helper.executeJavascript(script, $treatyAssureMoney));
                treatyAssureMoney = treatyAssureMoney.replaceAll("[^0-9]", "") + "0000";

                CrawlingTreaty t = new CrawlingTreaty();
                t.treatyName =
                    treatyName.contains("\n") ? treatyName.substring(0, treatyName.indexOf("\n"))
                        : treatyName;
                t.assureMoney = Integer.parseInt(treatyAssureMoney);

                // todo 보기납기 비교

                if (t.assureMoney != 0) {
                    homepageTreatyList.add(t);
                }
            }

            boolean allMatched = advancedCompareTreaties(
                welgramTreatyList, homepageTreatyList, new CrawlingTreatyEqualStrategy1()
            );

            if (!allMatched) {
                throw new SetTreatyException("특약 불일치");
            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    protected void printAndCompare(String title, String welgramData, String targetData)
        throws Exception {

        // 가입설계 정보와 원수사 정보 출력
        logger.info("가입설계 {} : {}", title, welgramData);
        logger.info("홈페이지 {} : {}", title, targetData);
        logger.info("======================================================");

        if (!welgramData.equals(targetData)) {
            throw new Exception(title + " 불일치");
        }
    }

    protected void enterPage(String productName) throws CommonCrawlerException {
        try {
            currentHandle = driver.getWindowHandle();

            logger.info("공시실 버튼 클릭");
            element = driver.findElement(By.xpath("//a[text()='공시실']"));
            helper.waitElementToBeClickable(element).click();
            WaitUtil.waitFor(2);

            logger.info("보험가격공시실 버튼 클릭");
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), false);
            currentHandle = driver.getWindowHandle();
            element = driver.findElement(By.xpath("//span[text()='보험가격공시실']/parent::a"));
            helper.waitElementToBeClickable(element).click();
            WaitUtil.waitFor(2);

            logger.info("상품명 : {} 클릭", productName);
            element = driver.findElement(
                By.xpath("//td[text()='" + productName + "']/following-sibling::td//button"));
            helper.waitElementToBeClickable(element).click();
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), false);
            currentHandle = driver.getWindowHandle();
            WaitUtil.waitFor(2);

            try {
                helper.click( // 기본정보(보장받으실 분)만 입력하신 후 보험료계산 버튼을 클릭해주세요.
                    By.xpath("//div[@class='dialog_alert']//button"));

            } catch (Exception ignored) {

            }

        } catch (Exception e) {
            throw new CommonCrawlerException(e, "공시실에서 상품 선택 중 오류 발생. 상품명 = " + productName);
        }
    }



    protected void setPlanType(String welgramPlanType, By... by) throws CommonCrawlerException {
        try {
            String title = "가입유형";
            By selectBy = by.length > 0 ? by[0] : By.name("cmb_joinType");

            logger.info("{} 선택", title);
            WebElement select = driver.findElement(selectBy);
            if (select.findElements(By.tagName("option"))
                .stream()
                .anyMatch(op -> op.getText().contains("종형없음"))) {
                return;
            }

            helper.selectOptionByClick(select, welgramPlanType);
            helper.waitForLoading(By.id("divCommonLoadingArea"));

            logger.info("선택된 {} 조회", title);
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetPlanType = String.valueOf(helper.executeJavascript(script, select));

            logger.info("{} 비교", title);
            printAndCompare(title, welgramPlanType, targetPlanType);
        } catch (Exception e) {
            throw new CommonCrawlerException(e, "setPlanType(String welgramPlanType)");
        }

    }



    public List<PlanReturnMoney> remakeFn(List<PlanReturnMoney> planReturnMoneyReq) {

        // 중복값 제거
        HashSet<PlanReturnMoney> listSet = new HashSet<PlanReturnMoney>(planReturnMoneyReq);

        List<PlanReturnMoney> processedList = new ArrayList<PlanReturnMoney>(listSet);
        for (PlanReturnMoney planReturnMoney : processedList) {
            logger.debug(planReturnMoney.getTerm());
            String term = planReturnMoney.getTerm();
            String yNum = "";
            String mNum = "";

            if (term.contains("개월") && term.contains("년")) {
                mNum = term.substring(term.indexOf("년") + 1).replace("개월", "");
            }
            if (term.contains("년")) {
                yNum = term.substring(0, term.indexOf("년"));
            }

            // 개월수만 있을 경우
            if (term.contains("개월") && !term.contains("년")) {
                yNum = "0";
                mNum = term.replace("개월", "");
            }
            logger.info("yNum :: " + yNum);
            logger.info("mNum :: " + mNum);
            String dNum = yNum + "." + mNum;
            planReturnMoney.setTmpSort(Double.parseDouble(dNum));
        }

        // 정렬
        processedList.sort(new Comparator<PlanReturnMoney>() {
            @Override
            public int compare(PlanReturnMoney vo1, PlanReturnMoney vo2) {
                return Double.compare(vo1.getTmpSort(), vo2.getTmpSort());
            }
        });

        return processedList;
    }



    protected void setTreaties(CrawlingProduct info) throws SetTreatyException {

        try {
            List<CrawlingTreaty> welgramTreaties = info.getTreatyList();

            // 가입금액 초기화
            helper.executeJavascript("$(\"input[name='ipt_amt']\").val(0)");

            //가입설계 특약 정보 세팅
            for (CrawlingTreaty welgramTreaty : welgramTreaties) {
                String treatyName = welgramTreaty.treatyName;    //가입설계 특약명
                String treatyAssureMoney = String.valueOf(
                    welgramTreaty.assureMoney / 10000);       //가입설계 특약가입금액

                WebElement $tbody = driver.findElement(By.id("tby_damboList"));

                logger.info("선택특약 선택할 특약명 :: {}", treatyName);
                // todo 뭔가 상당히 무식하다.. 문자 비교 다시 생각해보자
                // 현대해상은 로마자 표기에 일관성이 없기 때문에 특약명 비교시 이를 감안해야합니다.
                // 예를 들어 어떨때는 II ( I를 2개 겹쳐씀), 어떨때는 Ⅱ(유니코드) 를 쓰는 식입니다.
                Optional<WebElement> matchedTdOpt = $tbody.findElements(By.xpath(".//td[@class='l']"))
                    .stream().filter(td ->
                        TreatyNameComparators.allApplied.equals(td.getText(), treatyName))
                    .findFirst();/*.orElseThrow(
                        () -> new CommonCrawlerException(treatyName + " 을 찾을 수 없습니다.")
                    );*/

                if (matchedTdOpt.isPresent()) {
                    WebElement $td = matchedTdOpt.get();
                        WebElement $tr = $td.findElement(By.xpath("./parent::tr"));
                    WebElement $checkbox = $tr.findElement(By.xpath(".//input[@name='chk_nabgi']"));
                    WebElement $treatyAssureMoney = $tr.findElement(By.xpath(".//input[@name='ipt_amt']"));

                    //특약의 체크박스 선택
                    if (!$checkbox.isSelected()) {
                        String script = "arguments[0].click();";
                        helper.executeJavascript(script, $checkbox);
                        logger.info("선택특약 체크박스 :: 체크" );
                    }

                    //특약의 보험기간 납입기간 세팅
                    if (helper.existElement($tr, By.xpath(".//select"))) {

                        boolean has전기 = $tr.findElements(By.xpath(".//select//option"))
                            .stream().anyMatch(op -> op.getText().contains("전기"));

                        // option에 "전기"라는 텍스트가 있고, 보기,납기가 동일한 경우
                        if (welgramTreaty.insTerm.equals(welgramTreaty.napTerm) && has전기) {
                            welgramTreaty.napTerm = "전기";
                        }

                        String napTermInsTerm = welgramTreaty.napTerm + "납" + welgramTreaty.insTerm + "만기";
                        helper.selectByText_check(
                            $tr.findElement(By.xpath(".//select")), napTermInsTerm);
                        logger.info("선택특약 보기납기:: {}", napTermInsTerm);
                    }

                    //특약 가입금액 세팅
                    helper.sendKeys2_check($treatyAssureMoney, treatyAssureMoney);
                    logger.info("선택특약 가입금액 :: {}만원", treatyAssureMoney);
                }
            }

        } catch (Exception e) {
            throw new SetTreatyException(e);
        }

    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try {

            String title = "생년월일";
            String welgramBirth = (String) obj[0];
            By inputBy = obj.length > 1 ? (By) obj[1] : By.id("ipt_birthDt");

            logger.info("{} 입력", title);
            WebElement input = driver.findElement(inputBy);
            helper.sendKeys3_check(input, welgramBirth);

            logger.info("입력된 {} 조회", title);
            String script = "return $(arguments[0]).val();";
            String targetBirth = String.valueOf(helper.executeJavascript(script, input));

            logger.info("{} 비교", title);
            printAndCompare(title, welgramBirth, targetBirth);
        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {
        try {
            String title = "성별";
            int welgramGender = (int) obj[0];
            String welgramGenderText = (welgramGender == MALE) ? "남자" : "여자";
            By labelBy = obj.length > 1 ? (By) obj[1] : By.xpath("//label[text()='" + welgramGenderText + "']");

            logger.info("{} 선택", title);
            WebElement label = driver.findElement(labelBy);
            helper.waitElementToBeClickable(label).click();

            logger.info("선택된 {} 조회", title);
            String script = "return $(\"input[name='rdo_sexCat']:checked\").attr('id');";
            String targetGenderId = String.valueOf(helper.executeJavascript(script));
            String targetGender = driver.findElement(
                By.xpath("//label[@for='" + targetGenderId + "']")).getText();

            logger.info("{} 비교", title);
            printAndCompare(title, welgramGenderText, targetGender);
        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }



    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {

    }



    @Override
    public void setJob(Object... obj) throws SetJobException {
        String title = "직업";
        String welgramJob = (String) obj[0];

        try {

            logger.info("검색 버튼 클릭");
            element = driver.findElement(
                By.xpath("//th[text()='직업']/parent::tr//span[text()='검색']/parent::button"));
            helper.waitElementToBeClickable(element).click();
            WaitUtil.waitFor(1);

            logger.info("직업 선택 창으로 전환");
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            currentHandle = driver.getWindowHandle();

            logger.info("검색으로 찾기 버튼 클릭");
            helper.click(
                helper.waitPresenceOfElementLocated(
                    By.xpath("//span[text()='검색으로 찾기']/parent::a")));
            WaitUtil.waitFor(1);

            logger.info("{} 입력", welgramJob);
            helper.sendKeys3_check(By.id("searchTxt"), welgramJob);

            logger.info("검색 버튼 클릭");
            helper.click(By.id("btnJobSearch"));
            WaitUtil.waitFor(1);

            logger.info("{} 클릭", welgramJob);
            element = driver.findElement(By.xpath(
                "//tbody[@id='ui_keywordList']//td[text()='" + welgramJob
                    + "']/following-sibling::td//label"));
            helper.waitElementToBeClickable(element).click();
            WaitUtil.waitFor(1);

            logger.info("확인 버튼 클릭");
            element = driver.findElement(
                By.xpath("//div[@id='HHCM00030P_POP']//button[text()='확인']"));
            helper.waitElementToBeClickable(element).click();
            WaitUtil.waitFor(1);

            logger.info("창 전환");
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            currentHandle = driver.getWindowHandle();


        } catch (Exception e) {
            throw new SetJobException(e);
        }
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String title = "납입기간+보험기간";
            By selectBy = obj.length > 1 ? (By) obj[1] : By.name("cmb_payPeriod");
            String insTerm = info.insTerm + "만기";
            String napTerm = info.napTerm;
            napTerm = napTerm.contains("납") ? napTerm : napTerm + "납";
            String welgramTerm = napTerm + insTerm;

            logger.info("{} 선택", title);
            WebElement select = driver.findElement(selectBy);
            if (!select.findElements(By.tagName("option")).isEmpty() &&
                select.findElements(By.tagName("option"))
                    .stream()
                    .noneMatch(op -> op.getText().contains("선택안됨"))
            ) {
                helper.selectOptionByClick(select, welgramTerm);
                helper.selectByText_check(select, welgramTerm);

                logger.info("선택된 {} 조회", title);
                String script = "return $(arguments[0]).find('option:selected').text();";
                String targetTerm = String.valueOf(helper.executeJavascript(script, select));

                logger.info("{} 비교", title);
                printAndCompare(title, welgramTerm, targetTerm);
            } else {
                logger.info("납입기간+보험기간이 없는 상품입니다.");
            }

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        try {
            String title = "납입방법";
            String welgramNapCycle = (String) obj[0];
            By selectBy = obj.length > 1 ? (By) obj[1] : By.name("cmb_payMethod");

            logger.info("{} 선택", title);
            WebElement select = driver.findElement(selectBy);
            helper.selectOptionByClick(select, welgramNapCycle);

            logger.info("선택된 {} 조회", title);
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetNapCycle = String.valueOf(helper.executeJavascript(script, select));

            logger.info("{} 비교", title);
            targetNapCycle = targetNapCycle.replace("1월납", "월납");
            printAndCompare(title, welgramNapCycle, targetNapCycle);
        } catch (Exception e) {
            throw new SetNapCycleException(e);
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



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            logger.info("보험료계산 버튼 클릭");
            element = driver.findElement(By.xpath("//span[text()='보험료계산']/parent::button"));
            helper.waitElementToBeClickable(element).click();
            helper.waitForLoading(By.id("divCommonLoadingArea"));

            logger.info("주계약 보험료 크롤링");
            String monthlyPremium = "";
            String savePremium = "";

            element = driver.findElement(By.id("td_savePrem"));
            savePremium = element.getText().replaceAll("[^0-9]", "");
            element = driver.findElement(By.id("td_guarPrem"));
            monthlyPremium = element.getText().replaceAll("[^0-9]", "");

            info.savePremium = savePremium;
            info.getTreatyList().get(0).monthlyPremium = monthlyPremium;
            if(info.productCode.contains("_BAB")) {
                info.nextMoney = monthlyPremium;
            }

            helper.moveToElementByJavascriptExecutor(element);
            takeScreenShot(info);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            logger.info("예상해지환급금 버튼 클릭"); // 해약이 아니라 해지입니다 2023.11.07
            element = driver.findElement(By.xpath("//span[text()='예상해지환급금']/parent::button"));
            helper.waitElementToBeClickable(element).click();
            helper.waitForLoading(By.id("divCommonLoadingArea"));
            WaitUtil.waitFor(2);

            logger.info("해약환급금 창으로 전환");
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);
            currentHandle = driver.getWindowHandle();

            LinkedHashSet<String> termSet = new LinkedHashSet<>();

            //환급금 산출 값 세팅
            List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

            WebElement $select1 = driver.findElement(By.name("cmb_refundPeriod"));
            ;
            WebElement $select2 = driver.findElement(By.name("cmb_refundCout"));
            List<WebElement> $options = $select1.findElements(By.xpath("./option"));
            for (WebElement $option : $options) {

                helper.moveToElementByJavascriptExecutor($select1);

                //월 단위 선택
                String text = $option.getText();

                // 매3월 , 매년 , 매5년만 크롤링 - 2022.12.14
                if (text.equals("매월") || text.equals("매6월") || text.equals("격년") || text.equals(
                    "매3년")) {
                    continue;
                }
                helper.selectByText_check($select1, text);

                //환급금 몇 줄 보여줄지 선택 - 매3월일 경우 3개의 환급금만 그 외에는 6개의 환급금만
                if (text.equals("매3월")) {
                    helper.selectByText_check($select2, "3");
                } else {
                    helper.selectByText_check($select2, "6");
                }

                //환급금 산출 버튼 클릭
                element = driver.findElement(By.xpath("//span[text()='환급금 산출']/parent::button"));
                helper.waitElementToBeClickable(element).click();
                helper.waitForLoading(By.id("divCommonLoadingArea"));

                //해약환급금 크롤링
                List<WebElement> $trList = driver.findElements(
                    By.xpath("//tbody[@id='tbl_tbody']/tr"));
                for (WebElement $tr : $trList) {
                    helper.moveToElementByJavascriptExecutor($tr);

                    String[] termArray = $tr.findElement(By.xpath("./td[3]")).getText().trim()
                        .split("년");
                    String term = "";
                    String premiumSum = $tr.findElement(By.xpath("./td[4]")).getText()
                        .replaceAll("[^0-9]", "");
                    String returnMoney = $tr.findElement(By.xpath("./td[5]")).getText()
                        .replaceAll("[^0-9]", "");
                    String returnRate = $tr.findElement(By.xpath("./td[6]")).getText();

                    // 0년일 경우
                    if (termArray[0].equals("0")) {
                        term = termArray[1];
                    } else if (termArray[1].equals("0개월")) { // 0개월일 경우
                        term = termArray[0] + "년";
                    } else if (termArray[1].equals("12개월")) { // 12개월일 경우
                        term = (Integer.parseInt(termArray[0]) + 1) + "년";
                    } else {
                        term = $tr.findElement(By.xpath("./td[3]")).getText();
                    }

                    // 6년은 해약환급금에서 제외 - 2022.12.14
                    if (term.contains("6년")) {
                        continue;
                    }

                    if (!termSet.contains(term)) {

                        PlanReturnMoney p = new PlanReturnMoney();
                        p.setTerm(term);
                        p.setPremiumSum(premiumSum);
                        p.setReturnMoney(returnMoney);
                        p.setReturnRate(returnRate);

                        planReturnMoneyList.add(p);
                        logger.info(p.toString());
                    }

                    //경과기간 중복을 제거하기 위함
                    termSet.add(term);
                }
            }

            info.setPlanReturnMoneyList(remakeFn(planReturnMoneyList));

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            By by = obj.length > 1 ? (By) obj[1] : By.id("span_expExprRpymt");
            String returnPremium = "";

            element = driver.findElement(by);
            returnPremium = element.getText().replaceAll("[^0-9]", "");

            info.returnPremium = returnPremium;

/*            if (info.treatyList.get(0).productKind == ProductKind.순수보장형) {
                info.returnPremium = "0";
            } else {
                info.returnPremium = returnPremium;
            }*/

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
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
