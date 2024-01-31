package com.welgram.crawler.direct.life.hkl;

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
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.DisCount;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.ScrapableNew;
import java.util.List;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class HKL_TRM_D001 extends SeleniumCrawler implements ScrapableNew {

    // (무)흥국생명 온라인정기보험
    public static void main(String[] args) {
        executeCommand(new HKL_TRM_D001(), args);
    }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setMobile(true);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        enter(info);
        setBirthday(info);
        setGender(info);

        nextStep();
        setDiscount(info);
        setInsTerm(info);
        setNapTerm(info);

        helper.moveToElementByJavascriptExecutor(By.xpath("//label[contains(.,'아니오')]"));
        WebElement treatyProcessBtn
            = helper
                .findFirstDisplayedElement(By.xpath("//label[contains(.,'아니오')]"))
                .orElseThrow(() ->
                    new Exception("재해사망특약 가입을 제외할 버튼이 없습니다.")
                );
        helper.click(treatyProcessBtn, "재해사망특약 가입: 아니요 버튼 클릭");

        setAssureMoney(info);
        crawlPremium(info);
        crawlReturnMoneyList(info);
        crawlReturnPremium(info);

        return true;
    }



    private void nextStep() throws InterruptedException {

        helper.click(By.xpath("//button[contains(.,'보험료 계산하기')]"), "보험료 계산하기 클릭");
        WaitUtil.loading(2);
        helper.waitForLoading(By.id("circleG"));
    }



    private void enter(CrawlingProduct info) throws InterruptedException {

        helper.click(
            By.xpath("//a[contains(.,'정기/종신')]"),
            "정기/종신 카테고리 탭 클릭"
        );

        helper.click(
            By.xpath("//div[@class='info-name'][contains(.,'" + info.productName + "')]"),
            "상품카드 클릭"
        );

        helper.waitForLoading(By.id("circleG"));

        helper.click(
            By.xpath("//a[contains(.,'간편하게 보험료 계산하기')]"),
            "간편하게 보험료 계산하기 클릭"
        );
        helper.waitForLoading(By.id("circleG"));
        WaitUtil.loading(3);
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            WebElement el = helper.waitPresenceOfElementLocated(By.id("custBirth"));
            helper.sendKeys2_check(el, info.getFullBirth());

        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            if (info.gender == MALE) {
                helper.click(By.xpath("//label[@for='gender01']"));
            } else {
                helper.click(By.xpath("//label[@for='gender02']"));
            }

        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }



    public void setDiscount(CrawlingProduct info) throws CommonCrawlerException {

        try {
            DisCount discount = info.getDiscount();

            helper.waitForLoading(By.cssSelector("div.spin-dimm"));
            if (discount.equals(DisCount.일반)) {
                element = helper.waitElementToBeClickable(By.xpath("//label[@for='smokeY']"));
            } else {
                element = helper.waitElementToBeClickable(By.xpath("//label[@for='smokeN']"));
            }

            helper.click(element, "흡연여부 클릭");
            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new CommonCrawlerException(e);
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
            helper.click(By.xpath("//button[@title='보험기간 선택']"), "보험기간 클릭");

            WebElement displayedContainer
                = helper.findFirstDisplayedElement(By.cssSelector("div.popup-container"), 1L).get();

            WebElement matched
                = displayedContainer.findElement(By.xpath("//li//button[contains(.,'" + info.getInsTerm() + "')]"));

            helper.click(matched, matched.getText() + " 선택");
            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            helper.click(By.xpath("//button[@title='납입기간 선택']"), "납입기간 클릭");

            WebElement displayedContainer
                = helper.findFirstDisplayedElement(By.cssSelector("div.popup-container"), 1L).get();

            WebElement matched
                = displayedContainer.findElement(By.xpath("//li//button[contains(.,'" + info.getNapTerm() + "납" + "')]"));

            helper.click(matched, matched.getText() + " 선택");
            WaitUtil.loading(2);

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

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            helper.click(By.xpath("//button[@title='사망보험금 선택']"), "사망보험금 클릭");
            WaitUtil.loading(2);

            // 팝업
            WebElement displayedContainer = helper.findFirstDisplayedElement(
                By.cssSelector("div.popup-container"), 1L).get();

            // 팝업 내 가입금액 버튼들 중에서 선택
            List<WebElement> buttons = displayedContainer.findElements(By.xpath(".//li//button"));
            WebElement matchedButton
                = buttons
                    .stream()
                    .filter(button -> {
                        String dataValue = button.getAttribute("data-val").replaceAll("\\D", "");
                        return (Integer.parseInt(dataValue) * 10000) == Integer.parseInt(info.getAssureMoney());
                    })
                    .findFirst()
                    .orElseThrow(() ->
                        new SetAssureMoneyException("해당 보험금액이 없습니다.")
                    );

            // 스크롤 초기화
            WebElement ul = displayedContainer.findElement(By.cssSelector("ul.swiper-wrapper"));
            helper.executeJavascript("$(arguments[0]).css('transform','translate3d(0px, 0px, 0px)')", ul);

            // 누를 버튼이 보일 때까지 스크롤 내리기
            int scrollHeight = 0;
            int liHeight = matchedButton.getSize().getHeight();
            while (!matchedButton.isDisplayed()) {
                scrollHeight -= liHeight;
                helper.executeJavascript(
                    "$(arguments[0]).css('transform','translate3d(0px, " + scrollHeight + "px, 0px)')",
                    ul
                );
            }
            helper.click(matchedButton, matchedButton.getText() + " 선택");
            WaitUtil.loading(2);

        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }



    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException { }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            WebElement calcButton = helper.waitPresenceOfElementLocated(
                By.xpath("//a[contains(.,'다시 계산하기')]"));
            helper.click(calcButton, "다시 계산하기 클릭");
            helper.waitForLoading(By.cssSelector("div.spin-dimm"));

            info.getTreatyList().get(0).monthlyPremium =
                helper.waitPresenceOfElementLocated(
                    By.cssSelector("strong[name='bhRyo']")
                ).getText().replaceAll("\\D", "");

            logger.info("월보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            helper.executeJavascript("window.scrollTo(0, 0)"); // 화면 상단으로 올라가기
            helper.click(By.id("revRtnAmtBtn"), "해약환급금 버튼 클릭");
            WaitUtil.loading(1);

            WebElement table = helper.findFirstDisplayedElement(By.tagName("table"))
                .orElseThrow(() -> new Exception("해약환급금 테이블 요소가 없습니다."));

            List<WebElement> trs = table.findElements(By.cssSelector("tbody tr"));
            for (WebElement tr : trs) {

                // 빈 행 스킵
                if (tr.getText().isEmpty()) {
                    continue;
                }

                // 중도 해약환급금 스크랩
                List<WebElement> tds = tr.findElements(By.tagName("td"));
                info.getPlanReturnMoneyList().add(
                    new PlanReturnMoney(
                        tds.get(0).getText(),
                        tds.get(1).getText(),
                        tds.get(2).getText(),
                        tds.get(3).getText()
                    )
                );
            }

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            // 중도해약환급금 목록
            List<PlanReturnMoney> planReturnMoneyList = info.getPlanReturnMoneyList();

            // 중도해약환급금 목록에서 만기에 해당하는 planReturnMoney 객체 찾기
            Optional<PlanReturnMoney> returnMoneyOptional = planReturnMoneyList.stream().filter(
                planReturnMoney -> {

                    // planReturnMoney 경과기간
                    String termTxt = planReturnMoney.getTerm();
                    if (termTxt.contains("개월")) { // 개월단위는 제외
                        return false;
                    }

                    // planReturnMoney 경과기간에서 년으로 된 경과기간과 도달나이 추출
                    // 경과기간: ex) 2년\n(32세) <-- 이런식으로 되어있음 (편리하게도 말입니다 흥국생명님!! LOVE)
                    int year
                        = Integer.parseInt(termTxt.substring(0, termTxt.indexOf("\n")).replaceAll("\\D", ""));
                    int age = Integer.parseInt(termTxt.substring(termTxt.indexOf("\n")).replaceAll("\\D", ""));

                    // 해당 가설(info)의 보험기간 단위 추출 (세 or 년), 숫자 추출
                    String insTermUnit = info.insTerm.replaceAll("[0-9]", "");
                    int insTerm = Integer.parseInt(info.insTerm.replaceAll("[^0-9]", ""));

                    // 보험기간 단위에 따라 비교: 경과기간이 만기에 해당하는지 여부 반환
                    switch (insTermUnit) {
                        case "세": // 보험기간이 세형인경우
                            return age == insTerm; // 도달나이와 보험기간이 같은지 반환

                        case "년": // 보험기간이 년단위인 경우
                            return year == insTerm; // 경과기간과 보험기간이 같은지 반환
                    }

                    return false;
                }
            ).findFirst();

            if (returnMoneyOptional.isPresent()) {
                info.returnPremium = returnMoneyOptional.get().getReturnMoney();
            } else {
                info.returnPremium = "-1"; // 만기에 해당하는 중도해약환급금이 없을 경우
            }

            logger.info("만기환급금 :: {}", info.returnPremium);

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

