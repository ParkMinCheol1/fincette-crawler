package com.welgram.crawler.direct.life.hwl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * 한화생명 - 한화생명 e암보험(비갱신형) 무배당
 */
public class HWL_CCR_D005 extends CrawlingHWLDirect {

    public static void main(String[] args) {
        executeCommand(new HWL_CCR_D005(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        setBirthday(By.xpath("//label[text()='생년월일']/following-sibling::div//input"), info.fullBirth);
        setGender(info.getGender(), null);
        clickCalcButton(By.xpath("//div[@id='ComputeAndResultArea']//span[text()='보험료 계산하기']/ancestor::button"));
        setType(info.textType);
        setAssureMoney(info);
        setInsTerm(info);
        setNapTerm(info);
        setDiscountType(info);
        applyConditions();
        crawlPremium(info, "#result-panel-self1 > div.user-container.after > div.user-content.mt-3 > p");
        crawlReturnMoneyList(info, CrawlingHWLDirect.returnMoneyFields_4); // 으 여기서부터!
        crawlReturnPremium(info);

        return true;
    }

    @Override
    protected void setDiscountType(Object... obj) throws SetNapTermException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<WebElement> spans = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//input[@name='흡연여부']/following-sibling::label//span[@class='text']")
            );

            WebElement matched = spans.stream().filter(span -> {
                String spanDiscount = span.getText();
                String welgramDiscount = info.getDiscount().name();
                if (welgramDiscount.equals("일반")) { welgramDiscount = "흡연"; }

                return spanDiscount.equals(welgramDiscount);
            }).findFirst().orElseThrow(() -> new SetAssureMoneyException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "흡연여부");
        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        try {

            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<WebElement> spans = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//input[@name='보장금액(일반암기준)']/following-sibling::label//span[@class='text']")
            );

            WebElement matched = spans.stream().filter(span -> {
                long spanMoney = MoneyUtil.toDigitMoney2(span.getText());
                long assureMoney = info.getTreatyList().get(0).getAssureMoney();
                return spanMoney == assureMoney;
            }).findFirst().orElseThrow(() -> new SetAssureMoneyException("일치하는 보장금액이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "보장금액(일반암기준)");

        } catch (Exception e) {
            throw new SetAssureMoneyException(e);
        }
    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<WebElement> spans = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//input[@name='보험기간']/following-sibling::label//span[@class='text']")
            );

            WebElement matched = spans.stream().filter(span -> {
                String spanInsTerm = span.getText();
                String welgramInsTerm = info.getInsTerm();
                return spanInsTerm.equals(welgramInsTerm);
            }).findFirst().orElseThrow(() -> new SetAssureMoneyException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "보험기간");
        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            List<WebElement> spans = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//input[@name='납입기간']/following-sibling::label//span[@class='text']")
            );

            WebElement matched = spans.stream().filter(span -> {
                String spanNapTerm = span.getText();
                String welgramNapTerm = info.getNapTerm();
                return spanNapTerm.equals(welgramNapTerm);
            }).findFirst().orElseThrow(() -> new SetAssureMoneyException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "납입기간");
        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }

}

