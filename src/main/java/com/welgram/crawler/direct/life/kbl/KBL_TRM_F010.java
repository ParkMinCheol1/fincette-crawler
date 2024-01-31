package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KBL_TRM_F010 extends CrawlingKBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new KBL_TRM_F010(), args);
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
        By location = By.xpath("//div[@id='productMappingArea']");
        setPlan(info, location);

        logger.info("보험 기간");
        location = By.id("insuranceTermsWrap");
        String script = "return $(\"dd[id='insuranceTermsWrap'] div.select-box a.anchor\").text()";
        setInsTerm(info.insTerm + " 만기", location, script);

        logger.info("납입기간");
        location = By.id("paymentTermsWrap");
        script = "return $(\"dd[id='paymentTermsWrap'] div.select-box a.anchor\").text()";
        info.napTerm = (info.napTerm.contains("년")) ? info.napTerm + " 납" : info.napTerm + " 만기";
        setNapTerm(info.napTerm, location, script);

        logger.info("가입 금액 입력");
        location = By.xpath("//*[@id='mainAmountLayer']//input");
        setInputAssureMoney(info, location);

        logger.info("특약 선택");
        setTreaties(info);

        logger.info("보험료 계산하기 버튼 선택");
        $div = driver.findElement(By.cssSelector(".pop-calL-replace"));
        moveToElement(By.xpath("//div[@id='productMappingArea']"));
        click($div);

        logger.info("알럿 확인");
        if (alert("#systemAlert1")) {
            throw new Exception("납입보험료가 최저보험료 한도에 적합하지 않습니다.");
        }

        logger.info("보험료 크롤링");
        By monthlyPremium = By.xpath("//div[@id='insurancePlanCards']//dl[1]//span");
        crawlPremium(info, monthlyPremium);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("[보장내역 보기] 버튼 선택");
        $button = driver.findElement(By.id("buttonResultDocumentView"));
        click($button);

        logger.info("상품설명서 창 전환");
        ArrayList<String> tab = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tab.get(1));

        logger.info("해약환급금");
        String[] targetPageList = new String[]{"7","8","9","10","11","12","13","14","15","16","17"};
        ArrayList returnMoneyPageList = confirmReturnPage(targetPageList);
        crawlReturnMoneyListSixTRM(info, returnMoneyPageList);

        return true;
    }
}
