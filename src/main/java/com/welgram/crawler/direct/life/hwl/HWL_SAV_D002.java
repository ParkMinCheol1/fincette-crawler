package com.welgram.crawler.direct.life.hwl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



/**
 * 한화생명 - 한화생명 e암보험(갱신형) 무배당
 */
public class HWL_SAV_D002 extends CrawlingHWLDirect {

    public static void main(String[] args) {
        executeCommand(new HWL_SAV_D002(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        setBirthday(By.xpath("//label[text()='생년월일']/following-sibling::div//input"), info.getFullBirth());
        setGender(info.getGender(), null);
        clickCalcButton(By.xpath("//div[@id='ComputeAndResultArea']//span[text()='만기보험금 계산하기']/ancestor::button"));
        setType(info.getTextType());
        setAssureMoney(info);
        setDiscountType(info);
        setProductType();
        crawlPremium(info);
        crawlReturnMoneyList(info, CrawlingHWLDirect.returnMoneyFields_4); // 으 여기서부터!
        crawlReturnPremium(info);

        return true;
    }



    protected void setType(String type) throws CommonCrawlerException {

        try {
            helper.click(helper.waitElementToBeClickable(By.xpath(
                "//div[@class='tabs-container']//div[@role='tablist']//button[contains(.,'" + type + "')]")));
            WaitUtil.loading(3);

            if (type.equals("내가 직접 설계")) {
                helper.click(By.xpath("//span[normalize-space()='만기보험금 설계하기']"), "만기보험금 설계하기");

            }
        } catch (Exception e) {
            throw new CommonCrawlerException(e);
        }
    }



    protected void setDiscountType(Object... obj) throws SetNapTermException {
        try {
            List<WebElement> spans = helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath(
                    "//input[@name='상생할인']/following-sibling::label//span[@class='text']"));

            WebElement matched = spans.stream().filter(span -> {
                String spanDiscount = span.getText();
                String welgramDiscount = "해당사항 없음";

                return spanDiscount.equals(welgramDiscount);
            }).findFirst().orElseThrow(() -> new SetAssureMoneyException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "상생할인");

        } catch (Exception e) {
            throw new SetNapTermException(e);
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            int unit = 10000;

            WaitUtil.loading(6);

            String premium = helper.waitVisibilityOf(driver.findElement(By.xpath("//div[@id='result-panel-self1']//span[normalize-space()='월 보험료']/ancestor::li//button"))).getText().replaceAll("[^0-9]", "");
            premium = String.valueOf(Integer.parseInt(premium) * unit);
            logger.info("==================================");
            logger.info("월 보험료 : {}", premium);
            logger.info("==================================");

            info.treatyList.get(0).monthlyPremium = premium;

            takeScreenShot(info);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

}

