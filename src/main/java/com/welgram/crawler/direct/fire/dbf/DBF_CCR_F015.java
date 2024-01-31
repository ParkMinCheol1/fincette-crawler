package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DBF_CCR_F015 extends CrawlingDBFAnnounce {

    // New I'mOK 암보험 2304 06종_두번째암_해약환급금 지급형_납면미적용_갱신형
    public static void main(String[] args) {
        executeCommand(new DBF_CCR_F015(), args);
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
        setGender("sx_cd", info.gender);

        logger.info("직업 입력");
        setJob("경영지원 사무직 관리자");

        logger.info("가입 유형 선택");
        setProductType("sl_pan_cd", info.planSubName);

        logger.info("운전형태 : 자가용 고정");
        setVehicle("DRIVE_TYPE_CD", "자가용");

        logger.info("보험기간 선택 (보험기간+납입기간 같이)");
        WebElement $termSelect = driver.findElement(By.cssSelector("select[name=pan_rnw_tpcd]"));
        String insNapTerm = info.insTerm + "만기" + info.napTerm + "납";
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

        logger.info("희망보험료를 확인");
        WebElement $hopeAssureMoneyInput = driver.findElement(By.xpath("//input[@name='rsl_if__sm_prm_input']"));
        setHopeAssureMoney($hopeAssureMoneyInput);

        logger.info("월 보험료 크롤링");
        WebElement $monthlyPremiumTd = driver.findElement(By.xpath("//td[@class='ft rt']"));
        crawlPremium($monthlyPremiumTd, info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약 환급금 크롤링");
        getReturnPremium(info);

    }
}
