package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.general.CrawlingProduct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KBL_TRM_F005 extends CrawlingKBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new KBL_TRM_F005(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.loading(3);

        logger.info("보험료 계산하기");
        $button = driver.findElement(By.id("calculateResult"));
        click($button);

        logger.info("상품군과 상품명 선택");
        setPlanAndPlanName(info);

        logger.info("다음 버튼 선택");
        $a = driver.findElement(By.id("start"));
        click($a);

        logger.info("생년월일 :: {}", info.fullBirth);
        setBirthdayPanel(info.getFullBirth());

        logger.info("성별 선택");
        setGenderPanel(info.getGender());

        logger.info("다음 버튼 선택");
        $a = driver.findElement(By.id("step1next"));
        click($a);

        logger.info("보험 기간");
        setInsTermPanel(info.insTerm +"보장");

        logger.info("납입기간");
        info.napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm + "납";
        setNapTermPanel(info.napTerm);

        logger.info("가입 금액 입력");
        setInputAssureMoneyPanel(info);

        logger.info("납입 주기");
        By location = By.xpath("//div[@class='inline-input-group --form-alignment']");
        String script = "return $(\"input[name='paymethod']:checked\").parents(\"label\").text()";
        setNapCycle(info.napCycle, location, script);

        logger.info("다음 버튼 선택");
        $a = driver.findElement(By.id("step2next"));
        click($a);

        logger.info("특약 선택 및 확인");
        setTreatiesPanel(info);

        logger.info("다음 버튼 선택");
        $a = driver.findElement(By.id("step3next"));
        click($a);

        logger.info("보험료 크롤링");
        helper.waitVisibilityOfAllElementsLocatedBy(By.id("step4"));
        By monthlyPremium = By.xpath("//*[@id='step4']//b[@class='c-red   ff-condensed']");
        crawlPremium(info, monthlyPremium);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금");
        crawlReturnMoneyListTwoPanel(info);

        return true;
    }
}
