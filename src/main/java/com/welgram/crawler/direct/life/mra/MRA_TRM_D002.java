package com.welgram.crawler.direct.life.mra;


import com.welgram.common.enums.MoneyUnit;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MRA_TRM_D002 extends CrawlingMRADirect {

    public static void main(String[] args) {
        executeCommand(new MRA_TRM_D002(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        driver.manage().window().maximize();
        waitLoadingBar();

        logger.info("[step1] 사용자 정보 입력");
        setUserInfo(info);

        logger.info("가입조건변경 버튼 클릭");
        $button = driver.findElement(By.id("chgterm"));
        click($button);

        logger.info("보험기간 설정");
        setInsTerm(info.getInsTerm());

        logger.info("납입기간 설정");
        String napTerm = info.getNapTerm();
        napTerm = info.getInsTerm().equals(napTerm) ?  "전기납" : napTerm;
        napTerm = napTerm.contains("납") ? napTerm : napTerm + "납";
        setNapTerm(napTerm);

        logger.info("사망보험금 설정");
        setDeathBenefit(info.getAssureMoney());

        logger.info("적용 버튼 클릭");
        $button = driver.findElement(By.id("modalCalcBtn"));
        click($button);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info, MoneyUnit.원);

        return true;
    }
}
