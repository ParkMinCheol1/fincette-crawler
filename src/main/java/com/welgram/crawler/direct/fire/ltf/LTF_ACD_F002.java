package com.welgram.crawler.direct.fire.ltf;

import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;

public class LTF_ACD_F002 extends CrawlingLTFAnnounce {

    public static void main(String[] args) {
        executeCommand(new LTF_ACD_F002(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        enterPage(info);
        setJob(By.xpath("//button[@title='직업조회']"));

        helper.click(
            By.xpath("//input[@id='CDA_radio" + info.textType + "']/ancestor::label")
            , "가입유형 선택 :" + info.textType
        );

        helper.click(
            By.cssSelector("#content > div:nth-child(5) > p > span > button")
            , "보험료 산출 누르기"
        );
        helper.waitForLoading();
        helper.executeJavascript("scrollTo(0, document.body.scrollHeight);");

        crawlPremium(info);
        takeScreenShot(info);

        return true;
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            String premium = driver.findElement(By.cssSelector("#tx_amount_str")).getAttribute("value")
                .replaceAll("[^0-9]", "");
            info.getTreatyList().get(0).monthlyPremium = premium;
            logger.info("월 보험료 :: {}원", premium);
        } catch (Exception e) {
            throw new PremiumCrawlerException(e);
        }
    }


    /*@Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        disclosureRoomCrawling(info);
        return true;
    }


    private void disclosureRoomCrawling(CrawlingProduct info) throws Exception {
        WaitUtil.loading(4);

        // 공시실
        logger.info("공시실열기");
        openAnnouncePage(info.productName);
        WaitUtil.loading(4);

        selectJob();

        elements = driver.findElements(By.cssSelector("#content > div:nth-child(4) > table > thead > tr:nth-child(1) > th"));
        int elementsSize = elements.size();

        for(int i=0; i<elementsSize; i++){

            if(i == 0){
                continue;
            }

            logger.info("id확인 : "+elements.get(i).findElement(By.cssSelector("label")).getText());

            if(elements.get(i).findElement(By.cssSelector("label")).getText().contains(info.textType)){
                elements.get(i).click();
                WaitUtil.waitFor(1);
                break;
            }
        }

        // 보험료 계산
        logger.info("보험료 계산 버튼 누르기");
        ACDcalcBtn();
        helper.waitForCSSElement("#loading");

        ((JavascriptExecutor) driver).executeScript("scrollTo(0, document.body.scrollHeight);");

        // 보험료 저장
        String premium = driver.findElement(By.cssSelector("#tx_amount_str")).getAttribute("value")
            .replaceAll("[^0-9]", "");
        info.getTreatyList().get(0).monthlyPremium = premium;
        logger.info("월 보험료 :: {}원", premium);

        // 스크린샷
        logger.info("스크린샷!");
        takeScreenShot(info);

        info.errorMessage = "";

    }*/
}
