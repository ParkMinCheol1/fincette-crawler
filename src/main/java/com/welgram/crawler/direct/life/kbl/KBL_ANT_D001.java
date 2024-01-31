package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author aqua 무배당)KB착한연금보험Ⅱ
 */
public class KBL_ANT_D001 extends CrawlingKBLDirect {

    public static void main(String[] args) {
        executeCommand(new KBL_ANT_D001(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setImageLoad(false);
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

        logger.info("연금개시나이");
        By location = By.id("insuranceTermsWrap");
        String script = "return $(\"dd[id='insuranceTermsWrap'] div.select-box a.anchor\").text()";
        setAnnuityAge(info.annuityAge + "세 만기", location, script);

        logger.info("납입기간");
        location = By.id("paymentTermsWrap");
        script = "return $(\"dd[id='paymentTermsWrap'] div.select-box a.anchor\").text()";
        setNapTerm(info.napTerm + " 납", location, script);

        logger.info("납입 보험료 입력");
        location = By.xpath("//*[@id='mainPremiumLayer']//input");
        setInputAssureMoney(info, location);

        logger.info("주계약 보험료 세팅");
        info.treatyList.get(0).monthlyPremium = info.assureMoney;

        logger.info("연금 수령 기간");
        logger.info("20년종신으로 계산뒤 별도 테이블에서 기간 가져옴");
        location = By.id("annuityGuaranteeTermsWrap");
        script = "return $(\"td[id='annuityGuaranteeTermsWrap'] div.select-box a.anchor\").text()";
        setAnnuityReceivePeriod("20년", location, script);

        logger.info("보험료 계산하기 버튼 선택");
        $div = driver.findElement(By.id("insurancePlanCards"));
        click($div);

        logger.info("연금수령액 크롤링");
        crawlAnnuityPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyListSix(info);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}