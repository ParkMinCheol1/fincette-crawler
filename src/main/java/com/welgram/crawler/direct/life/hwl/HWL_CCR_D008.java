package com.welgram.crawler.direct.life.hwl;

import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



/**
 * 한화생명 - 한화생명 e암보험(갱신형) 무배당
 */
public class HWL_CCR_D008 extends CrawlingHWLDirect {

    public static void main(String[] args) {
        executeCommand(new HWL_CCR_D008(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        setBirthday(By.xpath("//label[text()='생년월일']/following-sibling::div//input"), info.fullBirth);
        setGender(info.gender, null);
        clickCalcButton(By.xpath("//div[@id='ComputeAndResultArea']//span[text()='보험료 계산하기']/ancestor::button"));
        setType(info.textType);
        setInsTerm(info);
        setNapTerm(info);
        setDiscountType(info);
        setCoverage(By.xpath("//div[@class='css-peue6e']//ul//span[@class='css-opnkpe']"));
        crawlPremium(info, null);
        crawlReturnMoneyList(info, CrawlingHWLDirect.returnMoneyFields_4); // 으 여기서부터!
        crawlReturnPremium(info);

        return true;
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
            }).findFirst().orElseThrow(() -> new SetInsTermException("일치하는 항목이 없습니다."));

            helper.click(matched.findElement(By.xpath("./ancestor::label")), "보험기간");

        } catch (Exception e) {
            throw new SetInsTermException(e);
        }
    }

}