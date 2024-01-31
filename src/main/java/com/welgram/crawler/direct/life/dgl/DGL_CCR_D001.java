package com.welgram.crawler.direct.life.dgl;

import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DGL_CCR_D001 extends CrawlingDGLMobile {

    public static void main(String[] args) {
        executeCommand(new DGL_CCR_D001(), args);
    }



    @Override
    protected boolean preValidation(CrawlingProduct info) {
        return info.getTreatyList().size() > 0;
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // iM 암보험 무배당 2309 (갱신형)

        WebElement $button = null;

        logger.info("보험료 알아보기 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[normalize-space()='보험료 알아보기']"));
        click($button);

        logger.info("생년월일 설정");
        setBirthday(info.getBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//a[normalize-space()='보험료 계산']"));
        click($button);

        logger.info("보험료 크롤링");
        driver.findElement(By.xpath("//*[@id='rcmrDgnLi_0']/div[1]/button")).click();
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];
            CrawlingTreaty mainTreaty
                = info.getTreatyList().stream().filter(t -> t.productGubun.equals(CrawlingTreaty.ProductGubun.주계약)).findFirst().get();

            WebElement $premium = driver.findElement(By.xpath("//*[@id='rcmrDgnLi_0']/div[1]/a/div[2]/div[3]"));
            String premium = $premium.getText().replaceAll("[^0-9]", "");
            logger.info(premium);

            mainTreaty.monthlyPremium = premium;
            logger.info(String.valueOf(mainTreaty));

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }
}