package com.welgram.crawler.direct.fire.ltf;

import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LTF_CCR_D003 extends CrawlingLTFDirect {
    public static void main(String[] args) { executeCommand(new LTF_CCR_D003(), args); }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            info.getTreatyList().get(0).monthlyPremium =
                helper.waitPesenceOfAllElementsLocatedBy(
                        By.xpath("//strong[text()='" + info.textType + "']/following-sibling::span/strong"))
                    .stream().filter(WebElement::isDisplayed)
                    .findFirst().get()
                    .getText().replaceAll("[^0-9]", "");

            logger.info("월 보험료 :: {}원", info.getTreatyList().get(0).monthlyPremium);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

}
