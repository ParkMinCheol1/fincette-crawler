package com.welgram.crawler.direct.life.kdb;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class KDB_DMN_D003 extends CrawlingKDBDirect {

    public static void main(String[] args) {
        executeCommand(new KDB_DMN_D003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $button = null;

        waitLoadingBar();
        WaitUtil.waitFor(2);

        logger.info("생년월일");
        setBirthday(info.getFullBirth());

        logger.info("성별 설정");
        setGender(info.getGender());

        logger.info("보험료 확인 버튼 클릭!");
        $button = driver.findElement(By.id("btnCal"));
        click($button);

        logger.info("가입금액 설정");
        setButtonAssureMoney(info);

        logger.info("유형 선택");
        setPlan(info);

        logger.info("보험 기간 선택");
        setRadioButtonInsTerm(info.insTerm);

        logger.info("납입 기간 선택");
        setRadioButtonNapTerm(info.napTerm + "납");

        logger.info("결과 확인하기 버튼 클릭!");
        $button = driver.findElement(By.id("btnRslt"));
        click($button);

        logger.info("월 보험료 크롤링");
        crawlPremium(info, By.id("monthAmt"));

        logger.info("스크린샷");
        takeScreenShot(info);

        /* 22.10.05
            KDB 원수사에서 온전한 해약환급금표를 제공하지 않는 상황 [예) 90세 만기지만 51세일경우 39년에 대한 만기환급금 미제공 ]
            보답에서는 해약환급금(plan_return_money)테이블을 보여주되,
            만기환급금은 (plan_calc)를 보여주므로 만기환급금은 0원으로 적용 (모니터링도 문제없도록)
        */
        logger.info("해약환급금 조회");
        crawlReturnMoneyListTwo(info, By.cssSelector("#cancelRefund1 tr"));

        return true;
    }

//    public void crawlReturnMoneyListTwo(Object... obj) throws ReturnMoneyListCrawlerException {
//        CrawlingProduct info = (CrawlingProduct) obj[0];
//        By location = (By) obj[1];
//        WebElement $a = null;
//
//        try {
//            $a = driver.findElement(By.id("btnShowDetail"));
//            click($a);
//
//            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
//
//            String lastPremiumSum = "";
//            List<WebElement> trList = driver.findElements(location);
//
//            for (WebElement tr : trList) {
//                String term = tr.findElements(By.tagName("td")).get(0).getText();
//                String premiumSum = tr.findElements(By.tagName("td")).get(1).getText();
//                String returnMoney = tr.findElements(By.tagName("td")).get(2).getText();
//                String returnRate = tr.findElements(By.tagName("td")).get(3).getText();
//
//                logger.info("______해약환급급__________ ");
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
//            //해약환급금 테이블 크롤링 후 만기로 0원 데이터 추가
////            23.08.09 순수보장형이라고해도 만기환급금 무조건 넣는 것으로 한다.
////            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
////            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
////            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
////            planReturnMoney.setInsAge(Integer.parseInt(info.age));
////
////            planReturnMoney.setTerm("만기");
////            planReturnMoney.setPremiumSum(lastPremiumSum);
////            planReturnMoney.setReturnMoney("0");
////            planReturnMoney.setReturnRate("0");
////
////            planReturnMoneyList.add(planReturnMoney);
////            info.returnPremium = "0";
//
//            info.setPlanReturnMoneyList(planReturnMoneyList);
//        } catch (Exception e) {
//            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
//            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
//        }
//    }
}
