package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DBF_CHL_F021 extends CrawlingDBFAnnounce {

    // 무배당 프로미라이프 청춘어람종합보험2309 09종_무해약(납중0%/납후50%)_납면적용_평생보장
    public static void main(String[] args) {
        executeCommand(new DBF_CHL_F021(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        crawlFromHomepage(info);
        return true;
    }

    private void crawlFromHomepage(CrawlingProduct info) throws Exception {

        logger.info("가입 유형 선택");
        setProductType("sl_pan_cd", info.getTextType());

        logger.info("운전형태 : 비운전자 고정");
        setVehicle("DRIVE_TYPE_CD", "비운전자");

        logger.info("보험기간 선택 (보험기간+납입기간 같이)");
        WebElement $termSelect = driver.findElement(By.cssSelector("select[name=exp_pytr]"));
        String insNapTerm = info.insTerm + "만기" + info.napTerm + "납";
        setTerm($termSelect, insNapTerm);

        logger.info("납입주기 선택");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=pym_mtd_cd]"));
        String napCycleText = info.napCycle.equals("01") ? "월납" : "연납";
        setNapCycle($napCycleSelect, napCycleText);

        logger.info("생년월일 입력");
        setBirthday(By.cssSelector("#birthDay2"), info.fullBirth);

        logger.info("성별선택");
        setGender("sx_cd", info.gender);

        logger.info("직업 입력");
        setJob("미취학아동");

        logger.info("보장목록 확인 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.linkText("보장목록 확인"))).click();
        waitAnnounceLoadingImg();

        logger.info("특약 셋팅");
        List<WebElement> $trList = driver.findElements(By.cssSelector("#tableDamboList > tr"));
        setTreaties($trList, info.treatyList);

        logger.info("보험료 산출버튼 클릭");
        driver.findElement(By.linkText("보험료 산출")).click();
        waitAnnounceLoadingImg();

        logger.info("월 보험료 크롤링");
        WebElement $monthlyPremiumTd = driver.findElement(By.xpath("//td[@class='ft rt']"));
        crawlPremium($monthlyPremiumTd, info);

        logger.info(" 예상 총 환급금 크롤링");
        WebElement $returnMoneyTd = driver.findElement(By.xpath("//table[@id='tableResult']//td[@class='rt']"));
        crawlReturnPremium($returnMoneyTd, info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약 환급금 크롤링");
        getReturnPremium(info);

    }
}
