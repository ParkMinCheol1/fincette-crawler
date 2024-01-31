package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import javax.xml.soap.SAAJResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_DSS_D008 extends CrawlingSLIAnnounce {
//public class SLI_DSS_D008 extends CrawlingSLIMobile {
    // 23.08.21 모바일에 문제가 있어 공시실로 전환

    public static void main(String[] args) {
        executeCommand(new SLI_DSS_D008(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) {
//        option.setMobile(true);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        boolean result;

            //        crawlingMobile(info);
        result = crawlingAnnounce(info);

        return result;
    }

    public boolean crawlingAnnounce(CrawlingProduct info) throws Exception {
        WebElement $button = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.loading(2);

        logger.info("공시실 상품 찾기");
        findProduct(info);

        logger.info("생년월일 세팅");
        setBirthday(info.getFullBirth());

        logger.info("성별");
        setGender(info.getGender());

        logger.info("납입 주기 선택");
        By location = By.id("padCylCd");
        setNapCycle(getNapCycleName(info.getNapCycle()), location);

        logger.info("다음 버튼 선택");
        $button = driver.findElement(By.xpath("//button[contains(.,'다음')]"));
        click($button);

        logger.info("주계약 설정");
        setMainTreaty(info);

        logger.info("선택 특약 설정");
        setSubTreaties(info);

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//button[@class='btn primary secondary round']"));
        moveToElement($button);
        click($button);

        logger.info("보험료 크롤링");
        location = By.xpath("//span[@class='price']");
        crawlPremium(info, location);

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//a[contains(.,'해약환급금 예시')]"));
        click($a);

        logger.info("해약환급금 크롤링");
        location = By.xpath("//div[@class='component-wrap next-content']//tbody//tr");
        crawlReturnMoneyList1(info, location);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }

    public boolean crawlingMobile(CrawlingProduct info) throws Exception{

        WebElement $button = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.loading(2);

        logger.info("본인 직접가입 버튼 클릭");
        $button = driver.findElement(By.xpath("//button[contains(.,'본인 직접가입')]"));
        click($button);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별");
        setGender(info.getGender());

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.id("calculate"));
        click($button);

        logger.info("보장금액 선택");
//        setRadioButtonAssureMoney(info);

        logger.info("다시 계산하기 버튼 클릭");
        $button = driver.findElement(By.xpath("//button[text()='다시 계산하기']"));
        if($button.isDisplayed()) {
            click($button);
        }

        logger.info("보험료 크롤링");
        By location = By.id("monthPremium");
        crawlPremium(info, location);

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//a[text()='보장내용/해약환급금']"));
        click($a);

        logger.info("해약환급금 스크랩");
        crawlReturnMoneyList(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}
