package com.welgram.crawler.direct.fire.sfi;

import static com.welgram.crawler.direct.fire.CrawlingSFI.getAssureMoneyInt;
import static com.welgram.crawler.direct.fire.CrawlingSFI.getRefinedPageTreatyName;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.comparer.impl.PlanTreatyComparer;
import com.welgram.crawler.direct.fire.sfi.common.PlanState;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Type;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class Mobile2 extends SeleniumCrawler implements Scrapable {

    public void setPlanCondition(CrawlingProduct info)
        throws CommonCrawlerException, InterruptedException {

        helper.click(By.id("refund-rate"), "가입설계 조건 설정: 조건 변경하기 클릭");
        WaitUtil.loading(2);

        // 갱신형 선택
        setRenewTypeNew(info);

        // 보험기간, 납입기간
        if (info.getProductType().equals(Type.갱신형)) {
            setNapTermNew(info); // Scrapable
        } else {
            setInsTermNew(info);
            setNapTermNew(info);
        }

        // 납입방법 : 월납 생략

        // 환급형태
        setRefundTypeNew(info);

        helper.click(By.xpath("//button[text()='선택완료']"));
        WaitUtil.loading(2);
        helper.waitForLoading();

    }

    public void closeAlarm() {

        try {

            WaitUtil.loading(8);

            helper.waitVisibilityOfElementLocated(
                By.xpath("//div[contains(@id,'popup')]//button[contains(.,'확인')]")
            ).click();

        } catch (Exception e) {
            logger.info("알람 없음!");
        }
    }

    /**
     * 안내 모달창 닫기 메서드
     * 삼성화재 마케팅에 따라 안내 및 이벤트 모달창 갯수가 달라짐. 떠있는 모달창을 모두 닫고 싶을 때 사용
     */
    protected void closeModal() {
        try {

            // 3초 정도 모달이 나타나길 기다리는 게 좋을 것 같다.
            new WebDriverWait(driver, Duration.ofSeconds(6).getSeconds())
                .until( driver -> {
                    List<WebElement> modals = driver.findElements(By.cssSelector("*[id~='shell-popup'] section"));
                    return modals.stream().anyMatch(WebElement::isDisplayed);
                });
            logger.info("모달창이 존재하는 것으로 추정됨 - 요소(*[id~='shell-popup'] section 가 display 된 상태");

            List<WebElement> confirmBtns = driver.findElements(
                By.xpath("//*[@id[contains(.,'shell-popup')]]//button[text()='확인']"));

            logger.info("모달창 안의 버튼 갯수 : {}", confirmBtns.size());
            for (WebElement btn : confirmBtns) {
                helper.click(btn, "모달 확인 버튼");
            }

        } catch (Exception e) {
            logger.info(e.getClass().getSimpleName());
            logger.info(e.getMessage());
            logger.info(Arrays.toString(e.getStackTrace()));
            logger.info("모달 확인창 없음");
        }
    }

    protected void setSubPlan(CrawlingProduct info)
        throws CommonCrawlerException, InterruptedException {

        String planSubName = info.planSubName; // (서브)플랜명
        String jobDesc = "플랜 선택";
        logger.info("CrawlingSFI.setPlan :: {} :: {}", jobDesc, planSubName);

        String subStr =
            planSubName.substring(0,
                    planSubName.contains("플랜") ? planSubName.indexOf("플랜") : planSubName.length())
                .trim();

        // 선택 가능한 플랜
        List<WebElement> list = helper.waitPesenceOfAllElementsLocatedBy(
            By.cssSelector("#calc-dambolist-table div.row")
        );

        // 일치하는 플랜
        Optional<WebElement> matchedOpt = list.stream().filter( el ->
            el.getText().contains(subStr)).findFirst();

        // 없으면 throw
        matchedOpt.orElseThrow(
            () -> new CommonCrawlerException(
                ExceptionEnum.ERR_BY_SUB_PLAN,
                new java.util.NoSuchElementException(info.planSubName + "이 선택항목에 없습니다.")));

        WebElement matchedEl = matchedOpt.get();
        if (!matchedEl.getAttribute("class").contains("active")) {
            helper.click(matchedEl, jobDesc, planSubName);
            helper.waitForLoading();
        }
    }

    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        CrawlingProduct info = (CrawlingProduct) obj;
        try {
            WaitUtil.loading(2);

            WebElement input = helper.waitElementToBeClickable(
                By.cssSelector("input[id^='birth']"));

            helper.click(input);
            helper.sendKeys3_check(input, info.fullBirth, "생년월일");

            WaitUtil.loading(1);
        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }

    @Override
    public void setGenderNew(Object obj) throws SetGenderException {

        try {
            WaitUtil.loading(2);

            CrawlingProduct info = (CrawlingProduct) obj;
            String infoGender = info.getGenderEnum().getDesc();

            Optional<WebElement> genderBtn = helper.waitPesenceOfAllElementsLocatedBy(
                    By.xpath("//section[@id='V2Dropdown']//button"))
                .stream().filter(
                    label -> label.getText().contains(infoGender))
                .peek(label -> logger.info("선택된 성별 : " + label.getText()))
                .findFirst();

            helper.click(genderBtn.get(), "성별 선택 - ", infoGender );

            if (!genderBtn.get().getText().contains(infoGender)) throw new Exception("성별이 잘 선택되지 않았습니다.");

            WaitUtil.loading(2);
            helper.waitForLoading(By.id("loading-common"));

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }

    public void goNext() throws InterruptedException {
        logger.info("다음 버튼 클릭 2");
        helper.click(By.cssSelector("#footer > div > button"));

        WaitUtil.loading(2);
    }

    @Override
    public void setJobNew(Object obj) throws SetJobException {
        try {
            logger.info("직업입력");
            WebElement jobSearchEl = helper.waitVisibilityOfElementLocated(By.id("input-search-job"));
            WaitUtil.loading(1);

            // 직업입력: 검색단어 입력
            helper.sendKeys3_check(jobSearchEl, "회사");
            WaitUtil.loading(2);

            // 직업입력: 제시되는 첫번째 단어 아무거나. 회사 사무직 종사자일 것.
            helper.click(
                helper.waitVisibilityOfAllElementsLocatedBy(By.cssSelector("ul[class*='autocomplete-list'] li button")).get(0)
            );

            // 직업입력: 고지의무 확인 클릭
            helper.click(By.xpath("//label[@for='checkbox-job-agree']"));

            logger.info("다음 버튼 클릭 1");
            helper.click(By.cssSelector("#V2JobSearch > footer > div > button"));

            WaitUtil.loading(1);

        } catch (Exception e) {
            throw new SetJobException(e);
        }
    }

    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj;

            String insTerm = info.getInsTerm();
            String jobDesc = "보험기간 선택";
            logger.info("CrawlingSFI.setInsTerm :: {} :: {}", jobDesc, insTerm);
            List<WebElement> labels;

            try {
                // 선택가능한 보험기간
                labels = helper.waitPesenceOfAllElementsLocatedBy(
                        By.xpath("//input[@name='insured-term']/following-sibling::label"));
            } catch (Exception e) {
                throw new SetInsTermException(e, jobDesc + " 요소를 찾지 못했습니다.");
            }

            // 일치하는 보험기간
            Optional<WebElement> matched = labels.stream().filter(el ->
                el.getText().contains(insTerm)).findFirst();

            // 없으면 throw
            matched.orElseThrow(
                () -> new SetInsTermException(
                    new java.util.NoSuchElementException(insTerm + "이 선택항목에 없습니다.")));

            // 보험기간 클릭
            WebElement matchedEl = matched.get();
            helper.click(matchedEl, jobDesc, insTerm);

        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }

    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj;

            String napTerm = info.getNapTerm();
            String jobDesc = "납입기간 선택";
            logger.info("CrawlingSFI.setNapTerm :: {} :: {}", jobDesc, napTerm);
            List<WebElement> labels;

            try {
                // 선택가능한 납입기간
                labels = helper.waitPesenceOfAllElementsLocatedBy(
                    By.xpath("//input[@name='payment-term']/following-sibling::label"));
            } catch (Exception e) {
                throw new SetNapTermException(e, jobDesc + " 요소를 찾지 못했습니다.");
            }

            // 일치하는 납입기간
            Optional<WebElement> matched = labels.stream().filter(el ->
                el.getText().contains(napTerm)).findFirst();

            // 없으면 throw
            matched.orElseThrow(
                () -> new SetNapTermException(
                    new java.util.NoSuchElementException(napTerm + "이 선택항목에 없습니다.")));

            // 납입기간 클릭
            WebElement matchedEl = matched.get();
            helper.click(matchedEl, jobDesc, napTerm);

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }

    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException {

    }

    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj;

            String renewType = info.getProductType().name();
            String jobDesc = "갱신형태 선택";
            logger.info("CrawlingSFI.setRenewType :: {} :: {}", jobDesc, renewType);
            List<WebElement> labels;

            try {
                // 선택가능한 보험기간
                labels = helper.waitPesenceOfAllElementsLocatedBy(
                    By.xpath("//input[@name='product-cls']/following-sibling::label"));
            } catch (Exception e) {
                throw new SetInsTermException(e, jobDesc + " 요소를 찾지 못했습니다.");
            }

            // 일치하는 보험기간
            Optional<WebElement> matched = labels.stream().filter(el ->
                el.getText().contains(renewType)).findFirst();

            // 없으면 throw
            matched.orElseThrow(
                () -> new SetRenewTypeException(
                    new java.util.NoSuchElementException(renewType + "이 선택항목에 없습니다.")));

            // 보험기간 클릭
            WebElement matchedEl = matched.get();
            helper.click(matchedEl, jobDesc, renewType);

        } catch (Exception e) {
            throw new SetRenewTypeException(e);
        }

    }

    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {

    }

    @Override
    public void setRefundTypeNew(Object obj) throws SetRefundTypeException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj;
            String productKind = info.getProductKind(); // 환급형태
            String jobDesc = "환급형태 선택";
            logger.info("CrawlingSFI.setReturnType :: {} :: {}", jobDesc, productKind);

            // 선택 가능한 환급형태
            List<WebElement> list = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//input[@name='refund-rate']/following-sibling::label"));

            // 일치하는 환급형태 버튼
            Optional<WebElement> matchedOpt = list.stream().filter(el -> {
                String returnType = "없음";
                if (productKind.equals("순수보장형")) {
                    returnType = "순수보장형";
                } else if (productKind.contains("환급")) {
                    returnType = "환급형";
                }

                return el.getText().contains(returnType);
            }).findFirst();

            // 없으면 throw
            matchedOpt.orElseThrow(
                () -> new SetRefundTypeException(
                    new java.util.NoSuchElementException(productKind + "이 선택항목에 없습니다.")));

            // 환급형태 클릭
            WebElement matchedEl = matchedOpt.get();
            helper.click(matchedEl, jobDesc, productKind);

            /*if (!matchedEl.getAttribute("class").contains("active")) {
                helper.doClick(matchedEl, jobDesc, productKind);
                helper.waitForLoading();
            }*/

        } catch (Exception e) {
            throw new SetRefundTypeException(e);
        }
    }

    @Override
    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException {
        try {
            logger.info("보험료 가져오기" );
            CrawlingProduct info = (CrawlingProduct) obj;

            info.treatyList.get(0).monthlyPremium = helper.waitPresenceOfElementLocated(
                    By.cssSelector("#Calculation > section > div.direct-price > div.price > div > strong"))
                .getText().replaceAll("\\D", "");

            logger.info("월 보험료 : {} ", info.treatyList.get(0).monthlyPremium);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

    public void compareTreaty(Object obj) throws Exception {

        CrawlingProduct info = (CrawlingProduct) obj;

        // 한번에 보장 변경 클릭!
        helper.click(By.id("btn-all-change"), "한번에 보장 변경 클릭");

        logger.info("원수사 현재 담보구성 저장하기");
        info.setCurrentTreatyList(
            getPageTreatyListFromMobileModal(info, false));


        logger.info("담보구성 비교하기");
        if (!new PlanTreatyComparer(info).comparePlanComposition()) {
            throw new CommonCrawlerException(ExceptionEnum.ERROR_BY_TREATIES_COMPOSIOTION);
        }
    }

    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {

    }

    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj;
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

            helper.click(By.cssSelector("#refund-rate-area button"), "예상 환급률 확인 클릭"); // 예상 환급률 확인 클릭
            helper.waitForLoading();

            List<WebElement> returnTypeBtnList = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("div.tab-menu button"));

            List<WebElement> tableList = helper.waitPesenceOfAllElementsLocatedBy(
                By.cssSelector("div.tab-panel table"));

            for (int tabIndex = 0; tabIndex < returnTypeBtnList.size(); tabIndex++) {
                WebElement btn = returnTypeBtnList.get(tabIndex);
                helper.click(btn);

                WebElement table = tableList.get(tabIndex);
                elements = table.findElements(By.cssSelector("tbody > tr"));

                for (int trIndex = 0; trIndex < elements.size(); trIndex++) {

                    PlanReturnMoney planReturnMoney;
                    if (tabIndex == 0) {
                        planReturnMoney = new PlanReturnMoney();
                    } else {
                        planReturnMoney = planReturnMoneyList.get(trIndex);
                    }

                    String napTerm = "";
                    String premiumSum = "";
                    String returnMoneyMin = null;
                    String returnRateMin = null;
                    String returnMoney = null;
                    String returnRate = null;

                    napTerm = elements.get(trIndex).findElement(By.cssSelector("td:nth-child(1)")).getText().trim();
                    premiumSum = elements.get(trIndex).findElement(By.cssSelector("td:nth-child(2)")).getText().trim();

                    logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    logger.info("해약환급금 크롤링:: 납입기간 :: " + napTerm.replaceAll("[^0-9]", ""));
                    logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum.replaceAll("[^0-9]", ""));
                    planReturnMoney.setTerm(napTerm);
                    planReturnMoney.setPremiumSum(premiumSum);

                    if (btn.getText().contains("최저")) {

                        returnMoneyMin = elements.get(trIndex).findElement(By.cssSelector("td:nth-child(3)")).getText().trim();
                        returnRateMin = elements.get(trIndex).findElement(By.cssSelector("td:nth-child(4)")).getText().trim();
                        logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin.replaceAll("[^0-9]", ""));
                        logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);

                        planReturnMoney.setReturnMoneyMin(returnMoneyMin);
                        planReturnMoney.setReturnRateMin(returnRateMin);

                    } else {
                        returnMoney = elements.get(trIndex).findElement(By.cssSelector("td:nth-child(3)")).getText().trim();
                        returnRate = elements.get(trIndex).findElement(By.cssSelector("td:nth-child(4)")).getText().trim();
                        logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney.replaceAll("[^0-9]", ""));
                        logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);

                        planReturnMoney.setReturnMoney(returnMoney);
                        planReturnMoney.setReturnRate(returnRate);
                    }

                    if (tabIndex == 0) {
                        planReturnMoneyList.add(planReturnMoney);
                    }

                    info.returnPremium = returnMoney;
                }
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnPremiumCrawlerException(e);
        }
    }



    @Override // 사용 안함
    protected boolean scrap(CrawlingProduct info) throws Exception {
        return false;
    }

    protected WebElement reGetTreatyEl(By by, int i) {
        return helper.waitPesenceOfAllElementsLocatedBy(by).get(i);
    }

    protected List<CrawlingTreaty> getPageTreatyListFromMobileModal(CrawlingProduct info, Boolean customizedPlan) throws Exception {

        List<WebElement> modalLiList = helper.waitPesenceOfAllElementsLocatedBy(
            By.cssSelector("div#V2HealthcareDamboAllChange ul.change-list li"));
        List<CrawlingTreaty> newTreatyList = new ArrayList<>();

        for (int i = 0; i < modalLiList.size(); i++) {
            WaitUtil.loading(1);
//            helper.waitForLoading();

            By byOfLi = By.cssSelector("div#V2HealthcareDamboAllChange ul.change-list li");
            WebElement li = reGetTreatyEl(byOfLi, i);

            PlanState planState;           	// planState:  가입 / 미가입 / 가입불가 상태
            String pageTreatyName =			// pageTreatyName: 원수사 보장명
                getRefinedPageTreatyName( li.findElement(By.cssSelector("div.tit")).getText());
            String joinStatusTxt = li.findElement(By.cssSelector("div.btn-box div.txt")).getText();
            int pageAssureMoney = 0;		// pageAssureMoney: 보장금액

            // customizedPlan = true, 원수사에서 제시된 실속,표준,고급 디폴트 플랜이 아니라 보답에서 새로 정의한 플랜이라는 의미
            // treatyInCustomizedPlan: 현재 <원수사 treaty loop> 에서 원수사 treaty와 일치하는 api treaty가 있는지 여부
/*            boolean treatyInCustomizedPlan = false;
            if (customizedPlan) {
                long matchCount = info.treatyList.stream()
                    .filter(t -> pageTreatyName.contains(t.treatyName))
                    .count();
                if (matchCount > 0) {
                    treatyInCustomizedPlan = true;
                }
            }*/

            boolean hasTreaty = info.treatyList.stream().anyMatch(t -> pageTreatyName.contains(t.treatyName));

            // 가입 가능한 상태. 대표가설의 경우 가입시 금액을 가져와야 함
            if (joinStatusTxt.contains("미가입")) {
                planState = PlanState.미가입;

                // 보답임의설계인데 해당 특약이 포함된 경우 : 가입금액 가져오고 가입버튼도 눌러놓기
                if (customizedPlan && hasTreaty) {
                    WebElement label = li.findElement(By.tagName("label"));
                    helper.click(label);

                    pageAssureMoney = getAssureMoneyInt(
                        reGetTreatyEl(byOfLi, i)
                            .findElement(By.cssSelector("div.money")).getText());
                    planState = PlanState.가입;
                }

            } else if (joinStatusTxt.contains("-")) { // 가입불가일 경우 : 사실 모바일의 경우 이 케이스는 없는듯함
                planState = PlanState.가입불가;

            } else { // 특약이 이미 가입된 상태.

                //보답임의설계에 해당 원수사 특약이 포함되지 아니한 경우 : 미가입처리
                if (customizedPlan && !hasTreaty) {
                    WebElement label = li.findElement(By.tagName("label"));
                    helper.click(label);
                    planState = PlanState.미가입;

                } else { // 금액 가져오기. 상세 모달에 들어가지 않아도 row에서 바로 보이는 가입금액
                    pageAssureMoney = getAssureMoneyInt(
                        reGetTreatyEl(byOfLi, i)
                            .findElement(By.cssSelector("div.money")).getText());
                    planState = PlanState.가입;
                }

            }

            // 가입 상태인 특약을 원수사 특약 리스트에 넣어준다
            if (planState == PlanState.가입) {
                CrawlingTreaty crawlingTreaty = new CrawlingTreaty();
                crawlingTreaty.treatyName = pageTreatyName;
                crawlingTreaty.assureMoney = pageAssureMoney;
                newTreatyList.add(crawlingTreaty);
            }


            // 스크롤 : 해당 특약 정보를 가지고 올 수 있도록
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", reGetTreatyEl(byOfLi, i));
        }

        logger.info("수정사항 적용 후 보험료 확인을 위해 버튼 클릭");
        helper.click(By.cssSelector("#V2HealthcareDamboAllChange button"));
        WaitUtil.loading(2);

        return newTreatyList;
    }

    // type 3: 모바일 페이지에서 가져오기
    protected List<CrawlingTreaty> getPageTreatyListFromMobile(CrawlingProduct info, int planColNo) throws Exception {

        logger.info("모바일 :: 원수사 가입설계 내용 담기");
        List<CrawlingTreaty> newTreatyList = new ArrayList<>();
        String rowGroupCss = "#calc-dambolist-table > div.result-list > div.row-group";
        List<WebElement> rowGroupList = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector(rowGroupCss));

        for (WebElement rowGroup : rowGroupList) {

            String span = "";
            WebElement titNameDiv = rowGroup.findElement(By.cssSelector("div.tit-name"));
            try {
                span = titNameDiv.findElement(By.tagName("span")).getText();
            } catch (Exception e) {
                // span 태그 없음
            }

            String pageTreatyName = getRefinedPageTreatyName(
                titNameDiv.getText().replaceAll(span, ""));

            String pageTreayAmountStr = rowGroup.findElement(
                By.cssSelector("div.row:nth-child(" + planColNo + ") > button > span")).getText();

            if (!pageTreayAmountStr.contains("-")) {

                int pageAssureMoney = getAssureMoneyInt(pageTreayAmountStr);
                PlanState planState = PlanState.가입;

                logger.info("-------------------------------");
                logger.info("원수사 특약 ::" + pageTreatyName);
                logger.info("원수사 특약 가입금액 (한글) :: " + pageTreayAmountStr);
                logger.info("원수사 특약 가입금액 (숫자) :: " + pageAssureMoney);
                logger.info("원수사 특약 가입상태 :: " + planState);

                CrawlingTreaty crawlingTreaty = new CrawlingTreaty();
                crawlingTreaty.treatyName = pageTreatyName;
                crawlingTreaty.assureMoney = pageAssureMoney;
                newTreatyList.add(crawlingTreaty);
            }
        }

//		return pagePlanTreatyList;
        return newTreatyList;
    }



}
