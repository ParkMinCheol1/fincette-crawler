package com.welgram.crawler.direct.fire.hwf;


import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HWF_CHL_F007 extends CrawlingHWFAnnounce {

    // 무배당 한화 처음부터 함께하는 어린이보험2310 1종(기본형) - 부양자 모  선택, 0세설계, 1종(기본형), 100세, 20년납, 갱신주기20년선택
    public static void main(String[] args) {
        executeCommand(new HWF_CHL_F007(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        driver.manage().window().maximize();

        logger.info("부양자 정보 입력");
        WebElement $birthDayInput = driver.findElement(By.id("i_jumin"));
        setBirthday($birthDayInput, info.parent_FullBirth);

        WebElement $genderSelect = driver.findElement(By.id("i_no"));
        setGender($genderSelect, 2);

        WebElement $jobSearch = driver.findElement(By.id("jobSearch"));
        setJob($jobSearch);

        logger.info("자녀 정보");
        WebElement $birthDayInput2 = driver.findElement(By.name("baby_jumin"));
        setBirthday($birthDayInput2, info.fullBirth);

        WebElement $genderSelect2 = driver.findElement(By.name("baby_no"));
        setGender($genderSelect2, info.gender);

        WebElement $jobSearch2 = driver.findElement(By.id("jobSearch2"));
        setJob($jobSearch2);

        logger.info("가입구분");
        WebElement $productTypeSelect = driver.findElement(By.cssSelector("select[name=gubun]"));
        setProductType($productTypeSelect, info.textType);

        logger.info("차량용도: 비운전자");
        WebElement $vehicleSelect = driver.findElement(By.name("cha"));
        setVehicle($vehicleSelect, "비운전자");

        logger.info("보험기간");
        WebElement $insTermSelect = driver.findElement(By.cssSelector("select[name=period1]"));
        setInsTerm($insTermSelect, info.insTerm);

        logger.info("납입기간");
        WebElement $napTermSelect = driver.findElement(By.cssSelector("select[name=period2]"));
        setNapTerm($napTermSelect, info.napTerm);

        logger.info("납입주기");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=cycle]"));
        setNapCycle($napCycleSelect, info.napCycle);

        logger.info("갱신주기: 20년");
        WebElement $renewCycleSelect = driver.findElement(By.cssSelector("select[name=re_cycle]"));
        setRenewCycle($renewCycleSelect, "20년");

        logger.info("특약 설정");
        List<WebElement> $trList = driver.findElements(By.xpath("//*[@class='tb_right02 tbl103_last']/parent::tr"));
        setTreaties(info.treatyList, $trList, "./th[1]");

        logger.info("보험료 계산 버튼 클릭");
        announceBtnClick(By.id("btnCalc"));

        logger.info("스크린샷");
        helper.executeJavascript("window.scrollTo(0, 0);");
        takeScreenShot(info);

        logger.info("보험료 크롤링");
        crawlAnnouncePagePremiums(info, "gnPrm");

        logger.info("해약환급금 크롤링");
        crawlAnnouncePageReturnPremiums(info);

        return true;

    }



    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입주기";

        WebElement $cycleSelect = (WebElement) obj[0];
        String napCycle = (String) obj[1];
        String expectedCycleText =  napCycle.equals("01") ? "월납" : "연납";
        String actualCycleText = "";

        try {
            // 납입주기 설정
            actualCycleText = helper.selectByText_check($cycleSelect, expectedCycleText);

            // 납입주기 비교
            super.printLogAndCompare(title, expectedCycleText, actualCycleText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }

    }

}
