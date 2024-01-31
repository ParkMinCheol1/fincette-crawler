package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DBF_DRV_F002 extends CrawlingDBFAnnounce {

    // 프로미 운전자보험
    public static void main(String[] args) {
        executeCommand(new DBF_DRV_F002(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);
        return true;
    }

    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

        logger.info("생년월일 입력");
        setBirthday(By.cssSelector("#birthDay"), info.fullBirth);

        logger.info("성별선택");
        setGender("chk", info.gender);

        logger.info("직업 입력");
        setJob("경영지원 사무직 관리자");

        logger.info("보험기간 선택 : 시작날짜만 선택시 자동으로 1년설정");
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = now.format(formatter);
        setInsTerm(By.cssSelector("#SCR_BOHUM_GIGAN1"), today);

        logger.info("특약 셋팅");
        List<WebElement> $trList = driver.findElements(By.xpath("//div[@id='subUp']/table/tbody/tr"));
        setTreatiesACD($trList, info.treatyList);

        logger.info("보험료 산출버튼 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.linkText("보험료 산출"))).click();
        waitAnnounceLoadingImg();

        logger.info("월 보험료 크롤링");
        WebElement $monthlyPremium = driver.findElement(By.xpath("//tbody[@id='retrieveResult']/tr[" + (info.treatyList.size()+1) + "]/td"));
        String premium = $monthlyPremium.getText().trim().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = premium;
        logger.info("월 보험료 확인 : " + premium);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);
    }
}
