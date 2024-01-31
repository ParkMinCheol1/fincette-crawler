package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DBF_OST_F001 extends CrawlingDBFAnnounce {

    // 프로미 해외여행보험I
    public static void main(String[] args) {
        executeCommand(new DBF_OST_F001(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);
        return true;
    }

    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

        logger.info("생년월일 입력");
        setBirthday(By.cssSelector("#birthymd"), info.fullBirth);

        logger.info("성별선택");
        setGender("gender", info.gender);

        logger.info("여행일 선택: 여행기간 7일 고정");
        WebElement $selectDate = driver.findElement(By.name("select_date"));
        helper.selectByText_check($selectDate, "7일까지");

        logger.info("여행지 선택: 일본 - 도쿄 고정");

        logger.info("국가 찾아보기 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.xpath("//a[contains(.,'찾아보기')]"))).click();
        WaitUtil.waitFor(1);

        logger.info("국가 일본 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.xpath("//a[contains(.,'일본')]"))).click();
        WaitUtil.waitFor(1);

        logger.info("도시 도쿄 입력");
        helper.sendKeys4_check(By.id("fd_journey_city"), "도쿄");
        WaitUtil.waitFor(1);

        logger.info("특약 셋팅");
        List<WebElement> $trList = driver.findElements(By.xpath("//td[contains(@id, 'dambo_nm')]/parent::tr"));
        setTreatiesACD($trList, info.treatyList);

        logger.info("보험료 산출버튼 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.linkText("보험료 산출"))).click();
        waitAnnounceLoadingImg();

        logger.info("월 보험료 크롤링");

        WebElement $monthlyPremium = driver.findElement(By.xpath("//tbody[@id='resultList']/tr[" + (info.treatyList.size()+1) + "]/td/div/input"));
        String premium = $monthlyPremium.getAttribute("value").trim().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = premium;
        logger.info("월 보험료 확인 : " + premium);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

    }

}
