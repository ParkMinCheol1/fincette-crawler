package com.welgram.crawler.direct.fire.ltf;

import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class LTF_ACD_F006 extends CrawlingLTFAnnounce {
    public static void main(String[] args) { executeCommand(new LTF_ACD_F006(), args); }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String premium = helper.waitPresenceOfElementLocated(By.cssSelector("#gnPrm")) // gnPrm
                .getText()
                .replaceAll("[^0-9]", "");
            info.getTreatyList().get(0).monthlyPremium = premium;
            logger.info("월 보험료 :: {}원", premium);

            String savePremium;
            savePremium = helper.waitPresenceOfElementLocated(By.id("dcbfCuPrm")).getText()
                .replaceAll("[^0-9]", "");
            info.savePremium = savePremium;
            logger.info("적립보험료 : " + savePremium + "원");

        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }

}
