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
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.general.PlanReturnMoneyTdIdx;
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

public abstract class CrawlingSLIMobile extends CrawlingSLINew {


    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        By location = By.id("birthday");

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

    // 자녀 생년월일
    public void setChlBirthday(Object... obj) throws SetBirthdayException {
        String title = "자녀 생년월일";
        String expectedFullBirth = (String) obj[0];
        By location = By.id("stdPBirthBaby");

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

        String expectedGenderText = (gender == MALE) ? "남" : "여";
        String genderId = (gender == MALE) ? "calcMainGender1" : "calcMainGender2";
        String actualGenderText = "";

        try {

            //성별 element 찾기
            WebElement $gender = driver.findElement(By.id(genderId));

            //성별 클릭
            clickByJavascriptExecutor($gender);

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

    public void setChlGender(Object... obj) throws SetGenderException {
        String title = "자녀성별";

        int gender = (int) obj[0];

        String expectedGenderText = (gender == MALE) ? "남" : "여";
        String genderId = (gender == MALE) ? "calcPGender1Baby" : "calcPGender2Baby";
        String actualGenderText = "";

        try {

            //성별 element 찾기
            WebElement $gender = driver.findElement(By.id(genderId));

            //성별 클릭
            clickByJavascriptExecutor($gender);

            //실제 선택된 성별 값 읽어오기
            actualGenderText = ((JavascriptExecutor) driver)
                .executeScript("return $('input[name=stdPGenderBaby]:checked').next().text();").toString()
                .trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setRadioButtonAssureMoney(Object obj) throws SetAssureMoneyException {

        CrawlingProduct info = (CrawlingProduct) obj;
        CrawlingTreaty mainTreaty = info.treatyList.stream()
            .filter(t -> t.productGubun.equals(ProductGubun.주계약))
            .findFirst().orElseThrow(() -> new RuntimeException("주계약이 없습니다."));

        try {

            int assureMoney = mainTreaty.getAssureMoney();

            List<WebElement> labelList = helper.waitPesenceOfAllElementsLocatedBy(
//                By.xpath("//dt[text()='보장금액']/following-sibling::dd//label")
                By.xpath("//div[@class='label-list1']//label")
            ).stream().filter(WebElement::isDisplayed).collect(Collectors.toList());

            boolean find = false;
            for(int i = 0 ; i < labelList.size(); i++){
                String text = ":";
                int start = labelList.get(i).getText().indexOf(text);
                String treatyAssureMoney = labelList.get(i).getText().substring(start + 1);
                logger.info(treatyAssureMoney);
                logger.info(MoneyUtil.toDigitMoney(treatyAssureMoney) + " ");
                if(MoneyUtil.toDigitMoney(treatyAssureMoney) == assureMoney){
                    click(labelList.get(i));
                    find = true;
                    break;
                }
            }
            if(!find) throw new Exception("보장금액 선택 시 오류 발생");

//             23.06.16 홈페이지 개정으로 화면 바뀜
//            WebElement matchedLabel = labelList.stream().filter(label -> {
//
//                WebElement strong = label.findElement(By.cssSelector("strong"));
//                Long labelMoney = MoneyUtil.toDigitMoney2(strong.getText());
//
//                return labelMoney.equals((long) assureMoney);
//
//            }).findFirst().orElseThrow(() ->
//                new RuntimeException("선택할 수 있는 보장금액이 없습니다. 보장금액 : " + assureMoney));
//
//            click(matchedLabel);



        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
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
        String unit = "만";

        try {
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000);
            try {
                actualAssureMoney = helper.selectByText_check(location, expectedAssureMoney + unit);
            } catch (NoSuchElementException e) {
                DecimalFormat decFormat = new DecimalFormat("###,###");
                if(expectedAssureMoney.length() > 4){
                    unit = "억";
                    expectedAssureMoney = expectedAssureMoney.replaceAll("[^0-9]", "").replaceAll("0000", "");
                } else {
                    expectedAssureMoney = decFormat.format(Integer.parseInt(expectedAssureMoney));
                }

                try{
                    actualAssureMoney = helper.selectByText_check(location, expectedAssureMoney + unit);
                    expectedAssureMoney = expectedAssureMoney + unit;
                } catch (Exception ne){
                    actualAssureMoney = helper.selectByText_check(location, expectedAssureMoney + unit + "원");
                    expectedAssureMoney = expectedAssureMoney + unit + "원";
                }
            }

            //비교
            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
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

    public void crawlReturnMoneyList(CrawlingProduct info) throws ReturnMoneyListCrawlerException {
        WebElement $a = null;

        try {
            $a = driver.findElement(By.xpath("//a[text()='해약환급금']"));
            click($a);

            // 해약환급금 테이블 내용
            List<WebElement> trs = driver.findElements(By.xpath("//tbody[@id='returnCancel1']//tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            for (WebElement tr : trs) {

                helper.moveToElementByJavascriptExecutor(tr);

                List<WebElement> tds = tr.findElements(By.tagName("td"));
                planReturnMoneyList.add(
                    new PlanReturnMoney(
                        tds.get(0).getText(),
                        tds.get(1).getText(),
                        tds.get(2).getText(),
                        tds.get(3).getText()
                    )
                );
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.info("▉▉▉ CrawlingAnnounce.crawlReturnMoneyListNew 해약환급금 테이블 스크랩 : " +
                planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }

    // 보장유형 선택
    protected void setPlanType(By selPlan) throws CommonCrawlerException {

        try{
            helper.waitElementToBeClickable(selPlan);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(selPlan));
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new CommonCrawlerException("상품유형 선택 오류가 발생했습니다." + e.getMessage());
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
        WebElement $button = null;

        try {

            $a = driver.findElement(By.xpath("//a[text()='해약환급금']"));
            click($a);

            // 전체 기간 펼쳐보기
            $button = driver.findElement(By.xpath("//*[@id='uiTabProInfo2']//*[@class='btn-com2' and contains(.,'펼쳐보기')]"));
            helper.moveToElementByJavascriptExecutor($button);
            click($button);

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

    // 팝업창이 뜬 경우에 확인 버튼 클릭
    protected void checkPopup(By $byElement) throws Exception{

        if (helper.existElement($byElement)) {
            driver.findElement($byElement).findElement(By.xpath(".//button")).click();
            WaitUtil.waitFor(2);
        }
    }
}