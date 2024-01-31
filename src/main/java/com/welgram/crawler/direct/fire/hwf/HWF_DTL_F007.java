package com.welgram.crawler.direct.fire.hwf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HWF_DTL_F007 extends CrawlingHWFAnnounce {

    // 한화 하얀미소플러스치아보험Ⅱ2310 무배당(연만기 갱신형)
    public static void main(String[] args) {
        executeCommand(new HWF_DTL_F007(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromAnnounce(info);
        return true;

    }



    private void crawlFromAnnounce(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("생년월일 설정");
        WebElement $birthDayInput = driver.findElement(By.id("i_jumin"));
        setBirthday($birthDayInput, info.fullBirth);

        logger.info("성별 설정");
        WebElement $genderSelect = driver.findElement(By.id("i_no"));
        setGender($genderSelect, info.gender);

        logger.info("직업 설정");
        WebElement $jobSearch = driver.findElement(By.id("jobSearch"));
        setJob($jobSearch);

        logger.info("보험기간 설정 : {}", info.insTerm);
        String insTerm = info.insTerm + "만기";
        WebElement $insTermSelect = driver.findElement(By.cssSelector("select[name=period1]"));
        setInsTerm($insTermSelect, insTerm);

        logger.info("납입주기 설정: 월납");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=napbang]"));
        setNapCycle($napCycleSelect, "월납");

        logger.info("특약별 가입금액 설정");
        List<WebElement> $trList = driver.findElements(By.xpath("//*[@class='tb_right02 tbl104_last']/parent::tr"));
        setTreaties(info.treatyList, $trList, "./td[1]");

        logger.info("보험료 계산 버튼 클릭");
        announceBtnClick(By.id("btnCalc"));

        logger.info("스크린샷 찍기 위해 최상단으로 이동");
        helper.executeJavascript("window.scrollTo(0, 0);");

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("보험료 크롤링");
        crawlAnnouncePagePremiums(info, "gnPrm");

        logger.info("해약환급금 크롤링");
        crawlAnnouncePageReturnPremiums(info);

    }

}
