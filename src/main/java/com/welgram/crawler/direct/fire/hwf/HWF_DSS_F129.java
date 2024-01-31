package com.welgram.crawler.direct.fire.hwf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HWF_DSS_F129 extends CrawlingHWFAnnounce {

    // 한화 3N5 WELL100 실속간편건강보험2310 무배당(일반고지형) 6종(납입후50%해약환급금지급형, 납입면제 미운영형, 일반고지형)- 100세 20년납, 갱신주기 20년
    public static void main(String[] args) {
        executeCommand(new HWF_DSS_F129(), args);
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

        logger.info("차량용도 설정 : 자가용");
        WebElement $vehicleSelect = driver.findElement(By.name("cha"));
        setVehicle($vehicleSelect, "자가용");

        logger.info("가입구분 설정");
        WebElement $productTypeSelect = driver.findElement(By.cssSelector("select[name=gubun]"));
        setProductType($productTypeSelect, info.textType);

        logger.info("보험기간 설정 : {}", info.insTerm);
        WebElement $insTermSelect = driver.findElement(By.cssSelector("select[name=bogi]"));
        setInsTerm($insTermSelect, info.insTerm);

        logger.info("납입기간 설정: {}", info.napTerm);
        WebElement $napTermSelect = driver.findElement(By.cssSelector("select[name=napgi]"));
        String napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm;
        setNapTerm($napTermSelect, napTerm);

        logger.info("납입주기 설정: 월납");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=napbang]"));
        setNapCycle($napCycleSelect, "월납");

        logger.info("갱신주기 :: 20년");
        WebElement $renewCycleSelect = driver.findElement(By.id("re_cycle"));
        setRenewCycle($renewCycleSelect, "20년");

        logger.info("특약별 가입금액 설정");
        List<WebElement> $trList = driver.findElements(By.xpath("//*[@class='tb_right02 tbl103_last']/parent::tr"));
        setTreaties(info.treatyList, $trList, "./th[1]");

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
