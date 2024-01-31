package com.welgram.crawler.direct.life.mra;


import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MRA_CCR_D009 extends CrawlingMRADirect {

    public static void main(String[] args) {
        executeCommand(new MRA_CCR_D009(), args);
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

        logger.info("상품유형 설정");
        int idx = info.getTextType().indexOf(",");
        String type = info.getTextType().substring(0, idx);
        setProductType(type);

        logger.info("가입유형 설정");
        idx = info.getTextType().indexOf(",") + 1;
        type = info.getTextType().substring(idx).trim();
        setPlan(type);

        logger.info("주계약 가입금액 설정");
        setAssureMoney(info.getAssureMoney());

        logger.info("보험기간 설정");
        String insTerm = info.getInsTerm() + " 만기";
        setInsTerm(insTerm);

        logger.info("납입기간 설정");
        String napTerm = info.getNapTerm();
        napTerm = info.getInsTerm().equals(napTerm) ?  "전기납" : napTerm;
        napTerm = napTerm.contains("납") ? napTerm : napTerm + "납";
        setNapTerm(napTerm);

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

    public void setPlan(String expectedPlan) throws CommonCrawlerException {
        String title = "가입유형";
        String actualPlan = "";

        try {
            WebElement $planDiv = driver.findElement(By.id("type-select2"));
            WebElement $planButton = $planDiv.findElement(By.xpath("./button[normalize-space()='" + expectedPlan + "']"));
            click($planButton);

            //실제 클릭된 가입유형 읽어오기
            $planButton = $planDiv.findElement(By.xpath("./button[@class[contains(., 'primary')]]"));
            actualPlan = $planButton.getText().trim();

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }
}