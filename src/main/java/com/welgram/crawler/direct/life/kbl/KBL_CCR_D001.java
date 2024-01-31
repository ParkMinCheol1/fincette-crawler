package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KBL_CCR_D001 extends CrawlingKBLDirect {

    public static void main(String[] args) {
        executeCommand(new KBL_CCR_D001(), args);
    }

    @Override
    protected boolean preValidation(CrawlingProduct info) {
        boolean result = true;

        if (info.gender == MALE) {
            logger.info("###### 여성전용상품입니다. ######");
            result = false;
        }

        return result;
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;
        WebElement $div = null;

        waitLoadingBar();
        WaitUtil.loading(3);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별");
        setGender(info.getGender());

        logger.info("보험료 계산하기");
        $button = driver.findElement(By.id("calculateResult"));
        click($button);

        logger.info("플랜 [{}] 선택", info.textType);
        By location = By.xpath("//div[@id='insurancePlanSubSlide']");
        setPlan(info, location);

        logger.info("보험 기간");
        location = By.id("insuranceTermsWrap");
        String script = "return $(\"dd[id='insuranceTermsWrap'] div.select-box a.anchor\").text()";
        setInsTerm(info.insTerm + " 만기", location, script);

        logger.info("납입기간");
        location = By.id("paymentTermsWrap");
        script = "return $(\"dd[id='paymentTermsWrap'] div.select-box a.anchor\").text()";
        info.napTerm = (info.napTerm.contains("년")) ? info.napTerm + " 납": info.napTerm + " 만기";
        setNapTerm(info.napTerm, location, script);

        logger.info("가입금액 입력");
        location = By.xpath("//*[@id='mainAmountLayer']//input");
        setInputAssureMoney(info, location);

        logger.info("보험료 계산하기 버튼 선택");
        $div = driver.findElement(By.id("insurancePlanCards"));
        click($div);

        logger.info("보험료 크롤링");
        By monthlyPremium = By.xpath("//div[@id='insurancePlanCards']//dl[1]//span");
        crawlPremium(info, monthlyPremium);

        logger.info("해약환급금 스크랩");
        crawlReturnMoneyListTwo(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }

}
