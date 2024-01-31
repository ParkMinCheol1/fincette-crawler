package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class KBL_TRM_F007 extends CrawlingKBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new KBL_TRM_F007(), args);
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

        logger.info("상세 상품명 :: {}", info.planSubName);
        setProductDetail(info);

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


    public void crawlReturnMoneyListTwoPanel(Object... obj) throws ReturnMoneyListCrawlerException {

        WebElement $button = null;
        CrawlingProduct info = (CrawlingProduct) obj[0];

        try{
            $button = driver.findElement(By.xpath("//div[@class='accordion__cover']//button[text()='해약환급금']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $button);
            click($button);

            WaitUtil.waitFor(2);

            List<WebElement> trList = $button.findElements(By.xpath("//div[@class='accordion --is-active']//tbody//tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            for(int j = 0; j < trList.size(); j++) {
                WebElement tr = trList.get(j);

                String term = tr.findElements(By.tagName("th")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(1).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(2).getText();

                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

//                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");

            }

            info.planReturnMoneyList = planReturnMoneyList;
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


}
