package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class DBF_AMD_F005 extends CrawlingDBFAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBF_AMD_F005(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("생년월일 입력");
        setBirthday(By.cssSelector("#birthDay"), info.fullBirth);

        logger.info("성별선택");
        setGender("sx_cd", info.gender);

        logger.info("직업 입력");
        setJob("경영지원 사무직 관리자");
        WaitUtil.waitFor(3);

        logger.info("가입 유형 선택");
        setProductType("sl_pan_cd", info.textType);
        WaitUtil.waitFor(3);

        /**
         * 원수사 보험기간 선택지는 3년만기3년납만 있으나
         * 전 특약 1년 갱신인 상품이라 가설의 보기/납기는 1년이 맞음 (1/24)
         */
        logger.info("보험기간 선택 : 3년만기3년납(1년갱신) 선택 고정");
        WebElement $termSelect = driver.findElement(By.cssSelector("select[name=exp_pytr]"));
        String insNapTerm = "3년만기3년납";
        setTerm($termSelect, insNapTerm);

        logger.info("납입주기 선택");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=pym_mtd_cd]"));
        String napCycleText = info.napCycle.equals("01") ? "월납" : "연납";
        setNapCycle($napCycleSelect, napCycleText);

        logger.info("보장목록 확인 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.linkText("보장목록 확인"))).click();
        waitAnnounceLoadingImg();

        logger.info("특약 셋팅");
        List<WebElement> $trList = driver.findElements(By.cssSelector("#tableDamboList > tr"));
        setTreaties($trList, info.treatyList);

        logger.info("보험료 산출버튼 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.linkText("보험료 산출"))).click();
        waitAnnounceLoadingImg();

        logger.info("월 보험료 크롤링");
        WebElement $monthlyPremiumTd = driver.findElement(By.xpath("//td[@class='ft rt']"));
        crawlPremium($monthlyPremiumTd, info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        return true;
    }
}