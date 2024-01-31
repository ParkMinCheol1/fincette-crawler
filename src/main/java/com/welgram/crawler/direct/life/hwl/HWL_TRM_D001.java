package com.welgram.crawler.direct.life.hwl;

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

public class HWL_TRM_D001 extends CrawlingHWLDirect {

    public static void main(String[] args) {
        executeCommand(new HWL_TRM_D001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        webCrawling(info);

        return true;
    }



    // 사이트웹 버전
    private void webCrawling(CrawlingProduct info) throws Exception {

        setBirthday(By.xpath("//label[text()='생년월일']/following-sibling::div//input"), info.getFullBirth());
        setGender(info.getGender(), null);
        clickCalcButton(By.xpath("//div[@id='ComputeAndResultArea']//span[text()='보험료 계산하기']/ancestor::button"));
        setType(info.textType);
        setRefundType(info.getProductKind());
        setAssureMoney(info);
        setInsTerm(info);
        setNapTerm(info);
        setDiscountType(info);
        applyConditions();
        crawlPremium(info, "#result-panel-self1 > div.user-container.after > div.user-content.mt-3 > p");
        crawlReturnMoneyList(info, CrawlingHWLDirect.returnMoneyFields_4); // 으 여기서부터!
        crawlReturnPremium(info);

    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            List<WebElement> spans = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//div[@role='dialog']//label[normalize-space()='보험기간']/parent::div//span[@class='text'][contains(.,'" + info.getInsTerm() + "')]"));

            WebElement matched = spans.stream().filter(span -> {
                String spanInsTerm = span.getText();
                String welgramInsTerm = info.getInsTerm();

                return spanInsTerm.equals(welgramInsTerm);
            }).findFirst().orElseThrow(() -> new SetInsTermException("일치하는 항목이 없습니다."));

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
                    "//div[@role='dialog']//label[normalize-space()='납입기간']/parent::div//span[@class='text'][contains(.,'" + info.getNapTerm() + "')]"));

            WebElement matched = spans.stream().filter(span -> {
                String spanNapTerm = span.getText();
                String welgramNapTerm = info.getNapTerm();

                return spanNapTerm.equals(welgramNapTerm);
            }).findFirst().orElseThrow(() -> new SetNapTermException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "납입기간");

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }



    @Override
    protected void setDiscountType(Object... obj) throws SetNapTermException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            String discountType = "해당 사항 없음";

            logger.info("건강고객할인 :: {}", discountType);

            List<WebElement> spans = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//div[@role='dialog']//label[normalize-space()='건강고객할인']/parent::div//span[@class='text'][contains(.,'" + discountType + "')]"));

            WebElement matched = spans.stream().filter(span -> {
                String spanDiscount = span.getText();
                String welgramDiscount = "해당 사항 없음";

                return spanDiscount.equals(welgramDiscount);
            }).findFirst().orElseThrow(() -> new SetAssureMoneyException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "건강고객할인");

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }

}