package com.welgram.crawler.direct.life.mra;


import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.enums.MoneyUnit;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class MRA_CCR_D007 extends CrawlingMRADirect {

    public static void main(String[] args) {
        executeCommand(new MRA_CCR_D007(), args);
    }

    @Override
    protected boolean preValidation(CrawlingProduct info) {
        return info.getTreatyList().size() > 0;
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        driver.manage().window().maximize();
        waitLoadingBar();

        logger.info("성별에 따라 상품 선택");
        selectProductName(info.getGender());

        logger.info("[step1] 사용자 정보 입력");
        setUserInfo(info);

        logger.info("가입조건변경 버튼 클릭");
        $button = driver.findElement(By.id("chgterm"));
        click($button);

        logger.info("납입유형 설정");
        setNapCycle(info.getNapCycleName());

        logger.info("보험기간 설정");
        setInsTerm(info.getInsTerm());

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

    private void selectProductName(int gender) throws CommonCrawlerException {
        String productName = (gender == MALE) ? "남성미니암보험" : "여성미니암보험";

        try {
            logger.info("좋은보험 클릭");
            WebElement $menu = driver.findElement(By.id("online-menu0"));
            Actions actions = new Actions(driver);
            actions.moveToElement($menu);
            actions.build().perform();

            logger.info("상품명 : {} 클릭", productName);
            $menu = driver.findElement(By.xpath("//a[normalize-space()='" + productName + "']"));
            click($menu);
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_NAME;
            throw new CommonCrawlerException(exceptionEnum, e.getCause());
        }
    }
}
