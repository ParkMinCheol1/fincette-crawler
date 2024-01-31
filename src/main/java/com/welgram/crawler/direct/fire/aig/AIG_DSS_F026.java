package com.welgram.crawler.direct.fire.aig;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class AIG_DSS_F026 extends CrawlingAIGAnnounce {

    // 무배당 AIG 올인원 간편보험II2309(1종(간편고지형))
    public static void main(String[] args) {
        executeCommand(new AIG_DSS_F026(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;

        driver.manage().window().maximize();

        waitLoadingBar();

        logger.info("생년월일 설정");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("확인 버튼 클릭");
        $button = driver.findElement(By.linkText("확인"));
        click($button);

        logger.info("판매플랜 설정");
        setPlan(info.getTextType());

        logger.info("납입/보험기간 설정");
        setInsTerm(info.getInsTerm(), info.getNapTerm());

        logger.info("납입방법 설정");
        setNapCycle(info.getNapCycleName());

        logger.info("상해급수 설정");
        setInjuryLevel("1급");

        logger.info("특약 설정");
        setTreaties(info.getTreatyList());

        logger.info("계산하기 버튼 클릭");
        $button = driver.findElement(By.id("btnBlue"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        WebElement $element = driver.findElement(By.id("groupType1"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        logger.info("해약환금금 크롤링");
        crawlReturnMoneyList(info);

        return true;

    }

}
