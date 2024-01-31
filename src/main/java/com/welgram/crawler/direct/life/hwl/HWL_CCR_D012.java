package com.welgram.crawler.direct.life.hwl;

import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;



public class HWL_CCR_D012 extends CrawlingHWLDirect {

    public static void main(String[] args) {
        executeCommand(new HWL_CCR_D012(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        setBirthday(By.xpath("//label[text()='생년월일']/following-sibling::div//input"), info.fullBirth);
        setGender(info.getGender(), null);
        clickCalcButton(By.xpath("//div[@id='ComputeAndResultArea']//span[text()='보험료 계산하기']/ancestor::button"));
        setType(info.getTextType());
        setInsTerm(info);
        setNapTerm(info);
        setDiscountType(info);
        setTreaties(info.getTreatyList());
        crawlPremium(info, null);
        crawlReturnMoneyList(info, CrawlingHWLDirect.returnMoneyFields_4); // 으 여기서부터!
        crawlReturnPremium(info);

        return true;
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
                    By.xpath("//input[@name='보험기간']/following-sibling::label//span[@class='text']")
            );

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

            try{
                WebElement displayedResultPanel = getDisplayedResultPanel("div[id^='result']");
                WebElement button = displayedResultPanel.findElement(By.xpath(".//span[text()='납입기간']/ancestor::li//button"));

                if (!button.isEnabled()) { return; }
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

        } catch (Exception e) {
            throw new SetNapTermException(e);
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

}