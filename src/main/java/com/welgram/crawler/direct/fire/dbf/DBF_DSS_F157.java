package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DBF_DSS_F157 extends CrawlingDBFAnnounce {

    // 무배당 프로미라이프 나에게맞춘2Q간편건강보험2309 52종_맞춤2Q간편고지2.2.0_납면미적용_해약환급금미지급_(납중0%/납후50%)_세만기_자유설계
    public static void main(String[] args) {
        executeCommand(new DBF_DSS_F157(), args);
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

        logger.info("가입유형 클릭 : "+info.textType);
        setProductType("sl_pan_cd", info.textType);

        logger.info("운전형태 : 자가용 고정");
        setVehicle("DRIVE_TYPE_CD", "자가용");

        logger.info("계약갱신주기 선택(보험기간+납입기간 같이)");
        WebElement $termSelect = driver.findElement(By.xpath("//*[@id='form1']/div[2]/div[4]/div[2]/div/table/tbody/tr[2]/td[1]/div/select"));
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

        logger.info("보험료 산출 클릭");
        driver.findElement(By.xpath("//div[@class='btn_set spr2']/a[@class='btn in rs c1'][normalize-space()='보험료 산출']")).sendKeys(Keys.ENTER);
        WaitUtil.waitFor(2);

        logger.info("월 보험료 크롤링");
//        WebElement $monthlyPremiumTd = driver.findElement(By.xpath("//td[@class='ft rt']"));
//        crawlPremium($monthlyPremiumTd, info);
        crawlPremium(info);

        logger.info("해약 환급금 크롤링");
        getReturnPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            WebElement $monthlyPremiumTd = driver.findElement(By.xpath("//*[@id='retrieveResult']/tr/td[1]"));
            String premium = $monthlyPremiumTd.getText().replaceAll("[^0-9]", "");
            info.treatyList.get(0).monthlyPremium = premium;
            logger.info("월 보험료 확인 : "  + premium);

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getMessage());
        }
    }
}
