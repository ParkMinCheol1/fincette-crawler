package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author aqua 무배당)KB착한연금보험Ⅱ
 */
public class KBL_ANT_F018 extends CrawlingKBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new KBL_ANT_F018(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {
        option.setImageLoad(true);  //해당 상품 이미지 true를 해둬야 뷰어가 나옴
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

        logger.info("연금 수령 기간");
        location = By.id("annuityGuaranteeTermsWrap");
        script = "return $(\"td[id='annuityGuaranteeTermsWrap'] div.select-box a.anchor\").text()";
        String annuityType = info.annuityType.replaceAll("[^0-9]", "");
        setAnnuityReceivePeriod(annuityType+"년", location, script);

        logger.info("보험료 계산하기 버튼 선택");
        $div = driver.findElement(By.id("insurancePlanCards"));
        click($div);

        logger.info("주계약 보험료 세팅");
        info.treatyList.get(0).monthlyPremium = info.assureMoney;

        logger.info("[보장내역 보기] 버튼 선택");
        $button = driver.findElement(By.id("buttonResultDocumentView"));
        click($button);

        logger.info("상품설명서 창 전환");
        ArrayList<String> tab = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tab.get(1));

        logger.info("연금수령액과 해약환급금 크롤링");
        String[] targetPageList = new String[]{"2","3","4","5","6","7"};
        crawlReturnMoneyListAndAnnuityPremium(info, targetPageList);

        logger.info("스크린샷");
        takeScreenShot(info);

        return true;
    }
}
