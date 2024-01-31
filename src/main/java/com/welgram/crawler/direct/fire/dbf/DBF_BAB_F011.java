package com.welgram.crawler.direct.fire.dbf;

import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.util.InsuranceUtil;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class DBF_BAB_F011 extends CrawlingDBFAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBF_BAB_F011(), args);
    }



    protected boolean preValidation(CrawlingProduct info) {

        boolean result = true;

        // 태아보험은 여성만 크롤링
        if (info.gender == MALE) {
            logger.info("남성은 가입불가합니다.");
            result = false;
        }

        return result;
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("가입 유형 선택");
        setProductType("sl_pan_cd", info.textType);

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

        logger.info("출산예정일자 입력 : 현재 임신주수를 12주로 계산");
        setBirthday(By.cssSelector("#birthDay2"), InsuranceUtil.getDateOfBirth(12));

        logger.info("성별선택");
        setGender("sx_cd", info.gender);

        logger.info("직업 입력");
        setJob("미취학아동");

        logger.info("임신개월수 셋팅: 임신3개월 고정");
        WebElement $pregnantMonthSelect = driver.findElement(By.cssSelector("select[name=preg_mth_cnt]"));
        setPregnantMonth($pregnantMonthSelect, "임신3개월");

        logger.info("보장목록 확인 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.linkText("보장목록 확인"))).click();
        waitAnnounceLoadingImg();

        logger.info("특약 셋팅");
        List<WebElement> $trList = driver.findElements(By.cssSelector("#tableDamboList > tr"));
        setTreaties($trList, info.treatyList);

        logger.info("희망보험료를 확인");
        WebElement $hopeAssureMoneyInput = driver.findElement(By.xpath("//input[@name='rsl_if__sm_prm_input']"));
        setHopeAssureMoney($hopeAssureMoneyInput);

        logger.info("태아 보험료 크롤링");
        WebElement $beforeBirthTd = driver.findElement(By.xpath("//td[@class='ft rt']"));
        List<WebElement> $tdList = driver.findElements(By.xpath("//table[@id='tableResult']/tbody/tr/td[@class='rt']"));
        WebElement $afterBirthTd = $tdList.get(0);
        babyCrawlPremium($beforeBirthTd, $afterBirthTd, info);

        logger.info(" 예상 총 환급금 크롤링");
        WebElement $returnMoneyTd = $tdList.get(1);
        crawlReturnPremium($returnMoneyTd, info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약 환급금 크롤링");
        getReturnPremium(info);        return true;
    }
}