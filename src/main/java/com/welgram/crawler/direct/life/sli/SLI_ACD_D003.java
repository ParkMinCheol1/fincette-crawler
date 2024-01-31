package com.welgram.crawler.direct.life.sli;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



public class SLI_ACD_D003 extends CrawlingSLIDirect {

    public static void main(String[] args) {
        executeCommand(new SLI_ACD_D003(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $label = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.loading(2);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별");
        setGender(info.getGender());

        logger.info("[운전] 선택");
        $label = driver.findElement(By.xpath("//label[@for='driveYnY']"));
        click($label);

        logger.info("내 보험료 확인 버튼 선택");
        $a = driver.findElement(By.id("calculate"));
        click($a);

        logger.info("납입 기간 선택");
        By location = By.id("napTerm");
        setNapTerm(info.napTerm + "납", location);

        logger.info("가입금액 선택");
        location = By.id("mainContAmt");
        setSelectBoxAssureMoney(info, location);

        logger.info("다시계산 버튼 클릭");
        location = By.id("reCalc1");
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
        $a = driver.findElement(By.xpath("//a[@data-tabnum='" + planNum + "']"));
        click($a);

        logger.info("해약환급금 스크랩");
        crawlReturnMoneyList2(info, planNum);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}
