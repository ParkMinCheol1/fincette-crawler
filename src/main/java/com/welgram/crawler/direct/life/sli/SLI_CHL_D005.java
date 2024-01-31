package com.welgram.crawler.direct.life.sli;

import com.welgram.common.InsuranceUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SLI_CHL_D005 extends CrawlingSLIDirect {

    public static void main(String[] args) {
        executeCommand(new SLI_CHL_D005(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $label = null;
        WebElement $button = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.loading(2);

        logger.info("상품 [어린이] 선택");
        $label = driver.findElement(By.xpath("//label[@for='stdPrdtGrp2']"));
        click($label);

        logger.info("부모 생년월일은 30세로 고정 (보험료와 무관)");
        setBirthday(InsuranceUtil.getBirthday(30));

        logger.info("부모 성별은 여자로 고정 (보험료와 무관");
        setGender(2);

        logger.info("자녀 생년월일");
        By location = By.id("stdPBirthChild");
        setBirthday(info.getFullBirth(), location);

        logger.info("자녀 성별 선택");
        setChildGender(info.getGender());

        logger.info("내 보험료 확인 버튼 선택");
        $button = driver.findElement(By.id("calculate"));
        click($button);

        logger.info("보험 기간 선택");
        location = By.id("insTerm1");
        setInsTerm(info.insTerm + "만기", location);

        logger.info("납입 기간 선택");
        location = By.id("napTerm1");
        setNapTerm(info.napTerm + "납", location);

        logger.info("보장금액 선택");
        location = By.id("reCalcPrice1");
        setSelectBoxAssureMoney(info, location);

        logger.info("다시계산 버튼 클릭");
        location = By.id("recalcHrzntlBtn");
        reCalculate(location);

        logger.info("선택할 플랜 number");
        int planNum = getPlanNum(info);

        logger.info("플랜 선택");
        location = By.id("planArea" + planNum);
        setPlan(info, location);

        logger.info("보험료 크롤링");
        location = By.id("monthPremium" + planNum);
        crawlPremium(info, location);

        logger.info("해약환급금 버튼 클릭");
        $a = driver.findElement(By.xpath("//button[@onclick='setGrntAndRtnDtChild(" + planNum + ");']"));
        click($a);

        logger.info("해약환급금 스크랩");
        location = By.xpath("//tbody[@id='pReturnCancel']//tr");
        crawlReturnMoneyList1(info, location);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}
