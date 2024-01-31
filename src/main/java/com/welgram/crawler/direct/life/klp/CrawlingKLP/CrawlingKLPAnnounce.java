package com.welgram.crawler.direct.life.klp.CrawlingKLP;

import com.welgram.common.MoneyUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
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
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingProduct.Gender;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.ScrapableNew;
import java.awt.event.WindowFocusListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CrawlingKLPAnnounce extends SeleniumCrawler implements ScrapableNew {

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        setBirthday(info);
        setGender(info);
        setProductType(info);
        helper.click(
            By.linkText("가입조건 및 보험료 확인")
        );
        helper.waitForLoading();

        if (!hasMainTreatyOnly(info)) compareTreaties(info);

        crawlPremium(info);
        crawlReturnMoneyList(info);
        crawlReturnPremium(info);

        return true;
    }

    protected boolean hasMainTreatyOnly(CrawlingProduct info) {
        return
            info.getTreatyList().size() == 1
                && info.getTreatyList().get(0).productGubun.equals(ProductGubun.주계약);
    }

    protected boolean compareTreaties(CrawlingProduct info) throws CommonCrawlerException {
        try {
            return advancedCompareTreaties(
                getHomepageTreatyList(),
                info.getTreatyList(),
                new CrawlingTreatyEqualStrategy2()
            );
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }

    protected List<CrawlingTreaty> getHomepageTreatyList() {
        List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
            By.xpath("//div[@id='intyListArea']//tbody//tr"));

        List<CrawlingTreaty> treatyList = new ArrayList<>();

        for (WebElement tr : trs) {
            List<WebElement> tds = tr.findElements(By.tagName("td"));

            treatyList.add(
                new CrawlingTreaty(
                    tds.get(1).getText(),
                    MoneyUtil.getDigitMoneyFromHangul(tds.get(2).getText()),
                    tds.get(3).getText(),
                    tds.get(4).getText().equals("0년") ? "일시납" : tds.get(4).getText()
                ));
        }

        return treatyList;
    }

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            helper.sendKeys1_check(By.id("plnnrBrdt"), info.fullBirth);
        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            Gender genderEnum = info.getGenderEnum();
            String genderDesc = genderEnum.getDesc();

            helper.click(
                By.xpath(
                    "//label[contains(text(),'" + genderDesc + "')]"
                )
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

    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

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
            String premium = helper.waitPresenceOfElementLocated(By.id("txtSumprm")).getText()
                .replaceAll("[^0-9]", "");

            info.getTreatyList().get(0).setMonthlyPremium(premium);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            helper.click(By.id("aRefundPop"));
            helper.switchToWindow2(true);

            List<WebElement> trs = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//tbody[@id='listArea']//tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            for (WebElement tr : trs) {
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

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
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
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            String textType = info.getTextType();

            helper.click(
                By.xpath(
                    "//label[contains(text(),'" + textType + "')]"
                )
            );

        } catch (Exception e) {
            throw new SetProductTypeException(e);
        }
    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }
}
