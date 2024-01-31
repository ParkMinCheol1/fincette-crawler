package com.welgram.crawler.direct.life.dgl;

import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DGL_SAV_D004 extends CrawlingDGLMobile {

    public static void main(String[] args) {
        executeCommand(new DGL_SAV_D004(), args);
    }



    @Override
    protected boolean preValidation(CrawlingProduct info) {
        return info.getTreatyList().size() > 0;
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

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

        logger.info("보험기간 비교");
        setInsTerm(info.getInsTerm());

        logger.info("납입기간 비교");
        setNapTerm(info.getNapTerm());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }



    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {

        String title = "보험기간";
        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $infoUl = driver.findElement(By.xpath("//ul[@class='details']"));
            WebElement $insTermDiv = $infoUl.findElement(By.xpath(".//div[normalize-space()='" + title + "']"));
            $insTermDiv = $insTermDiv.findElement(By.xpath("./following-sibling::div[@class='data']"));
            actual = $insTermDiv.getText().trim();

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }



    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {

        String title = "납입기간";
        String expected = (String) obj[0];
        String actual = "";

        try {
            WebElement $infoUl = driver.findElement(By.xpath("//ul[@class='details']"));
            WebElement $napTermDiv = $infoUl.findElement(By.xpath(".//div[normalize-space()='" + title + "']"));
            $napTermDiv = $napTermDiv.findElement(By.xpath("./following-sibling::div[@class='data']"));
            actual = $napTermDiv.getText().trim();

            super.printLogAndCompare(title, expected, actual);

        } catch (Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream()
            .filter(t -> t.productGubun == ProductGubun.주계약)
            .findFirst()
            .get();

        try {
            WebElement $myPlanUl = driver.findElement(By.id("rcmrDgnUl"));
            WebElement $premiumDiv = $myPlanUl.findElement(By.xpath(".//div[@class='row2'][contains(., '보험료')]"));
            String premium = $premiumDiv.getText();

            int idx = -1;
            idx = premium.indexOf("보장기간");
            premium = premium.substring(0, idx);
            premium = premium.replaceAll("[^0-9]", "");

            mainTreaty.monthlyPremium = premium;

            if ("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                String msg = "주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.";
                logger.info(msg);
                throw new Exception(msg);

            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

            logger.info("스크린샷 찍기");
            takeScreenShot(info);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }
}