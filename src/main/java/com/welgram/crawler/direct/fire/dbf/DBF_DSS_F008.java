package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class DBF_DSS_F008 extends CrawlingDBFAnnounce {

    // 2Q초간편건강보험 2301(02종_자유설계_간편고지(2Q)(3개월,2년))
    public static void main(String[] args) {
        executeCommand(new DBF_DSS_F008(), args);
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

        // 플랜 서브네임: 40종_맞춤간편고지3.0.5_납면적용_해약환급금미지급_(납중0%/납후50%)_세만기_자유설계 -> 해당 네임으로 가입상품 선택
        logger.info("가입유형 클릭 : "+info.planSubName);
        setProductType("sl_pan_cd", info.planSubName);

        logger.info("운전형태 : 자가용 고정");
        setVehicle("DRIVE_TYPE_CD", "자가용");

        logger.info("계약 갱신 주기 선택");
        WebElement $termSelect = driver.findElement(By.cssSelector("select[name=pan_rnw_tpcd]"));
        String insNapTerm = info.insTerm + "만기자동갱신";
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