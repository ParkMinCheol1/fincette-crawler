package com.welgram.crawler.direct.fire.ltf;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.*;
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
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.scraper.ScrapableNew;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class CrawlingLTFDirect extends SeleniumCrawler implements ScrapableNew {

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        enter();
        setBirthday(info.getFullBirth());
        setGender(info);
        setMarry(info);                         // LTF_DSS_D002
        setDriverType(info);                    // LTF_DRV_D004

        helper.waitForLoading();                // 단계 전환

        setPlan(info);
        setNapTerm(info);
        compareLTFTreaties(info);
        crawlPremium(info);
        crawlReturnPremium(info);               // 롯데 다이렉트 상품은 현재까지 만기환급금 정보가 없어서 일괄 "0" 처리함

        return true;
    }



    protected void compareLTFTreaties(CrawlingProduct info) throws SetTreatyException {

        List<CrawlingTreaty> homepageTreatyList = getHomepageTreatyList();
        List<CrawlingTreaty> welgramTreatyList = getWelgramTreatyList(info);

        boolean compareResult = advancedCompareTreaties(
            homepageTreatyList,
            welgramTreatyList,
            new CrawlingTreatyEqualStrategy1());

        if (!compareResult) {
            throw new SetTreatyException("특약의 구성이 원수사와 다릅니다.");
        } else {
            logger.info("특약 구성이 원수사와 일치합니다");
        }
    }



    private List<CrawlingTreaty> getWelgramTreatyList(CrawlingProduct info) {

        List<CrawlingTreaty> welgramTreatyList = new ArrayList<>();

        info.treatyList.forEach(t -> {
                welgramTreatyList.add(
                    new CrawlingTreaty(
                        t.treatyName
                        , t.assureMoney
                    )
                );
            }
        );

        return welgramTreatyList;
    }



    private List<CrawlingTreaty> getHomepageTreatyList() {

        List<CrawlingTreaty> homepageTreatyList = new ArrayList<>();

        driver.findElements(By.xpath("//tbody")).stream()
            .filter(WebElement::isDisplayed)
            .map(tbody -> tbody.findElements(By.xpath(".//tr")))
            .flatMap(List::stream).collect(Collectors.toList()).forEach(tr -> {
                homepageTreatyList.add(
                    new CrawlingTreaty(
                        tr.findElement(By.cssSelector("span.tb-txt1")).getText()
                        , MoneyUtil.getDigitMoneyFromHangul(
                        tr.findElement(By.cssSelector("strong.tb-point")).getText())
                    )
                );
            });

        return homepageTreatyList;
    }



    protected void setPlan(CrawlingProduct info) throws CommonCrawlerException {

        try {

            if (helper.existElement(By.xpath("//h2[contains(.,'가입 유형 선택하기')]"))) {
                helper.click(By.xpath("//button[contains(.,'" + info.textType + "')]"),
                    info.textType + "플랜 클릭");

                logger.info("플랜 :: {}", info.textType);
                logger.info("슬라이딩 시간만큼 기다리기");
                WaitUtil.waitFor(2);
            }

        } catch (NoSuchElementException ignored) {
            logger.info("플랜 요소가 존재하지 않습니다.");
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    protected void enter() throws CommonCrawlerException {

        try {
            helper
                .findFirstDisplayedElement(By.id("btnBehaviorAgree"), 2L)
//                .findFirstDisplayedElement(By.id("btnBehaviorAgree"), Duration.ofSeconds(2L))
                .ifPresent(webElement -> {
                    helper.click(webElement,
                        "서비스 접근 권한 안내 동의 클릭");
                });

            helper.click(
                By.xpath("//a[contains(.,'가입')]"),
                "가입 버튼 클릭");
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {

        try {
            String fullBirth = (String) obj[0];
            helper.sendKeys4Birthday_check(By.id("birthStr"), fullBirth);
            WaitUtil.loading(1);

        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }



    @Override
    public void setGender(Object... obj) throws SetGenderException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int genderValue = info.gender + 1;
            helper.click(By.xpath(
                "//input[starts-with(@name,'gender') and @value='" + genderValue
                    + "']/ancestor::label"));
            WaitUtil.loading(2);
        } catch (Exception e) {
            throw new SetGenderException(e);
        }
    }



    protected void setMarry(Object... obj) throws CommonCrawlerException {
    }

    protected void setDriverType(Object... obj) throws CommonCrawlerException {
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {

    }

    @Override
    public void setJob(Object... obj) throws SetJobException {

    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            logger.info("납입기간 요소 존재 여부 확인 (5초 대기)");
//            helper.waitPesenceOfAllElementsLocatedBy(By.xpath("//input[@name='nabibPeriod']"));
//            new WebDriverWait(driver, 5).until(
//            new WebDriverWait(driver, Duration.ofSeconds(5)).until(
//                d -> d.findElement(By.xpath("//input[@name='nabibPeriod']")));

            helper.click(By.xpath(
                "//input[@name='nabibPeriod']/following-sibling::span[contains(.,'" + info.napTerm
                    + "')]/ancestor::label"));

            helper.waitForLoading();

            logger.info("납입기간 :: {}", info.napTerm);

        } catch (TimeoutException ignored) {

            logger.info("납입기간 요소가 존재하지 않습니다.");

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

            info.getTreatyList().get(0).monthlyPremium =
                helper.waitPesenceOfAllElementsLocatedBy(
                        By.xpath("//dt[text()='보험료']/following-sibling::dd"))
                    .stream().filter(WebElement::isDisplayed)
                    .findFirst().get()
                    .getText().replaceAll("[^0-9]", "");

            logger.info("월 보험료 :: {}원", info.getTreatyList().get(0).monthlyPremium);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {

    }



    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            info.returnPremium = "0";

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
