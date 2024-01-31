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

public class KBL_CHL_F003 extends CrawlingKBLAnnounce {

    public static void main(String[] args) {
        executeCommand(new KBL_CHL_F003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;
        WebElement $div = null;
        WebElement $a = null;

        waitLoadingBar();
        WaitUtil.loading(3);

        logger.info("공시실 상품 찾기");
        findProduct(info);

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
        WaitUtil.waitFor(5);

        logger.info("다음 버튼 선택");
        $a = driver.findElement(By.id("step3next"));
        click($a);

        logger.info("보험료 크롤링");
        WaitUtil.waitFor(5);
        By monthlyPremium = By.xpath("//*[@id='step4']//b[@class='c-red   ff-condensed']");
        crawlPremium(info, monthlyPremium);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금");
        crawlReturnMoneyListTwoPanel(info);

        return true;
    }

//    public void crawlReturnMoneyListTwoPanel(Object... obj) throws ReturnMoneyListCrawlerException {
//
//        WebElement $button = null;
//        CrawlingProduct info = (CrawlingProduct) obj[0];
//
//        try{
//            $button = driver.findElement(By.xpath("//div[@class='accordion__cover']//button[text()='해약환급금']"));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $button);
//            click($button);
//
//            WaitUtil.waitFor(2);
//
//            List<WebElement> trList = $button.findElements(By.xpath("//div[@class='accordion --is-active']//tbody//tr"));
//            String lastPremiumSum = "";
//            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
//
//            for(int j = 0; j < trList.size(); j++) {
//                WebElement tr = trList.get(j);
//
//                String term = tr.findElements(By.tagName("th")).get(0).getText();
//                String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
//                String returnMoney = tr.findElements(By.tagName("td")).get(1).getText();
//                String returnRate = tr.findElements(By.tagName("td")).get(2).getText();
//
//                logger.info("|--경과기간: {}", term);
//                logger.info("|--납입보험료: {}", premiumSum);
//                logger.info("|--해약환급금: {}", returnMoney);
//                logger.info("|--환급률: {}", returnRate);
//                logger.info("|_______________________");
//
//                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//
//                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
//                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
//                planReturnMoney.setInsAge(Integer.parseInt(info.age));
//
//                planReturnMoney.setTerm(term);
//                planReturnMoney.setPremiumSum(premiumSum);
//                planReturnMoney.setReturnMoney(returnMoney);
//                planReturnMoney.setReturnRate(returnRate);
//
//                planReturnMoneyList.add(planReturnMoney);
//                lastPremiumSum = premiumSum;
//                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
//            }
//
//            //해약환급금 테이블 크롤링 후 만기로 0원 데이터 추가
//            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
//            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
//            planReturnMoney.setInsAge(Integer.parseInt(info.age));
//
//            planReturnMoney.setTerm("만기");
//            planReturnMoney.setPremiumSum(lastPremiumSum);
//            planReturnMoney.setReturnMoney("0");
//            planReturnMoney.setReturnRate("0");
//
//            planReturnMoneyList.add(planReturnMoney);
//            info.returnPremium = "0";
//
//            info.planReturnMoneyList = planReturnMoneyList;
//        } catch (Exception e) {
//            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
//            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
//        }
//    }


}


