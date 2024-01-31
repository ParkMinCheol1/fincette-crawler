package com.welgram.crawler.direct.life.kdb;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KDB_CCR_D004 extends CrawlingKDBDirect {

    public static void main(String[] args) {
        executeCommand(new KDB_CCR_D004(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        WebElement $button = null;

        waitLoadingBar();
        WaitUtil.waitFor(2);

        // 광고 창 닫기
//        WebElement dialog = driver.findElement(By.xpath("//*[@id=\"dialogMainEvent2\"]/div/div/div"));
//        if (dialog.isDisplayed()) {
//            driver.findElement(By.xpath("//*[@id=\"dialogMainEvent2\"]/div/div/div/a")).click();
//        }
//        WaitUtil.waitFor(2);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("보험료 확인 버튼 클릭!");
        $button = driver.findElement(By.id("btnCal"));
        click($button);

        logger.info("가입금액 설정");
        setSelectBoxAssureMoney(info);

//        logger.info("상품유형 선택 : {}", info.textType);
//        setProductType(info.textType);

        logger.info("보험 기간 선택");
        setRadioButtonInsTerm(info.insTerm + "만기");

        logger.info("납입 기간 선택");
        setRadioButtonNapTerm(info.napTerm + "납");

        logger.info("결과 확인하기 버튼 클릭!");
        $button = driver.findElement(By.id("btnRslt"));
        click($button);

        logger.info("특약 가입금액 설정");
        setTreaties(info);

        logger.info("결과 확인하기 버튼 클릭!");
        $button = driver.findElement(By.id("btnRslt"));
        click($button);

        logger.info("월 보험료 크롤링");
        crawlPremium(info, By.id("monthAmt3"));

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금 조회");
        crawlReturnMoneyListTwo(info, By.cssSelector("#cancelRefund tr"));

        return true;
    }
}
