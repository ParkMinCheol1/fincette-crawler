package com.welgram.crawler.direct.fire.ltf;

import com.google.gson.Gson;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.fire.CrawlingLTF;
import com.welgram.crawler.general.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LTF_DTL_F003 extends CrawlingLTFAnnounce {

    public static void main(String[] args) {
        executeCommand(new LTF_DTL_F003(), args);
    }

/*    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        disclosureRoomCrawling(info);
        return true;
    }


    private void disclosureRoomCrawling(CrawlingProduct info) throws Exception {
        WaitUtil.loading(4);

        // 공시실
        logger.info("공시실열기");
        openAnnouncePage(info.productName);
        WaitUtil.loading(4);


        // 생년월일
        logger.info("생년월일 세팅");
        setBirth(info.fullBirth, "input#PBirth");

        // 성별
        logger.info("성별 세팅");
        setGender(info.getGender(), By.cssSelector("input[name='PIsdsex1']"));

        // 직업
        logger.info("직업 세팅");
        setJob();

        // 보험기간
        logger.info("보험기간 세팅");
        setSelectBox(info.insTerm, "tmpNDCD");

        // 납입기간
        logger.info("납입기간 세팅");
        setSelectBox(info.napTerm, "tmpPYM_TRMCD");

        // 납입주기
        logger.info("납입주기 세팅");
        setNapCycle(info.napCycle, "#pymCyccd");

        // 특약 선택
        for (CrawlingTreaty item : info.treatyList) {
            // 특약선택
            setTreaty(item);
        }

        // 보험료 계산
        logger.info("보험료 계산 버튼 누르기");
        calculation();
        helper.waitForCSSElement("#loading");

        // 보험료 가져오기
        // for (CrawlingTreaty item : info.treatyList) {
        //     getPremium(info, item);
        // }

        // 보험료 저장
        String premium = driver.findElement(By.cssSelector("#mnRecPrm")).getText()
            .replaceAll("[^0-9]", "");
        info.getTreatyList().get(0).monthlyPremium = premium;
        logger.info("월 보험료 :: {}원", premium);

        // 스크롤 이동
        discusroomScrollBottom();

        // 스크린샷
        logger.info("스크린샷!");
        takeScreenShot(info);

        // 특약개수가 다를경우 result = false 처리
//            if (info.treatyList.size() != info.siteProductMasterCount) {
//                logger.info("특약개수가 다릅니다.");
//                logger.info("상품의 특약 개수 :: " + info.treatyList.size());
//                logger.info("DB에서 일치하는 특약 개수 :: " + info.siteProductMasterCount);
//                return result;
//            } else { // 특약개수가 같아도 상품 특약,DB에서 일치하는 특약 개수를 확인할 수 있는 log 추가
//                logger.info("특약개수가 똑같습니다.");
//                logger.info("상품의 특약 개수 :: " + info.treatyList.size());
//                logger.info("DB에서 일치하는 특약 개수 :: " + info.siteProductMasterCount);
//            }

        // + 적립보험료
        // getSavingPremium(info);

        // 해약환급금
        getReturnPremium(info);

        info.errorMessage = "";


    }


    protected void getReturnPremium(CrawlingProduct info) throws Exception {
        logger.info("현재창 핸들 저장");
        //currentHandle = driver.getWindowHandles().iterator().next();

        logger.info("해약환급금 버튼 클릭");
        helper.doClick(By.cssSelector(".bt_04_07"));
        WaitUtil.loading();

        // new windows 창으로 전환
        //switchtowindows(3);

        logger.info("해약환급금 팝업창으로 핸들 전환");

        // 새로운 팝업창 핸들러 ( 가입사항 창으로 전환 )

        String lastWindow = null;
        Set<String> handles = driver.getWindowHandles();
        for (String aux : handles) {
            lastWindow = aux;
        }
        logger.info("새로운 pop창(가입사항 창으로 전환)");
        driver.switchTo().window(lastWindow);
        WaitUtil.loading();

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        // elements = helper.waitPresenceOfElementLocated(By.cssSelector("#refund-tbody")).findElements(By.tagName("tr"));
        elements = driver.findElement(By.cssSelector("#refund-tbody"))
            .findElements(By.tagName("tr"));
        for (WebElement tr : elements) {

            String term = tr.findElements(By.tagName("td")).get(0).getText();
            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            logger.info("해약환급금 크롤링:: 납입기간 :: " + term);
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 합계 보험료 :: " + premiumSum);
            String returnMoneyMin = tr.findElements(By.tagName("td")).get(2).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(최저) :: " + returnMoneyMin);
            String returnRateMin = tr.findElements(By.tagName("td")).get(3).getText();
            logger.info("해약환급금 크롤링:: 환급률(최저) :: " + returnRateMin);
            String returnMoney = tr.findElements(By.tagName("td")).get(4).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(공시) :: " + returnMoney);
            String returnRate = tr.findElements(By.tagName("td")).get(5).getText();
            logger.info("해약환급금 크롤링:: 환급률(공시) :: " + returnRate);
            String returnMoneyAvg = tr.findElements(By.tagName("td")).get(6).getText()
                .replaceAll("[^0-9]", "");
            logger.info("해약환급금 크롤링:: 환급금(평균) :: " + returnMoneyAvg);
            String returnRateAvg = tr.findElements(By.tagName("td")).get(7).getText();
            logger.info("해약환급금 크롤링:: 환급률(평균) :: " + returnRateAvg);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
            planReturnMoney.setReturnRateMin(returnRateMin);
            ;
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
            planReturnMoney.setReturnRateAvg(returnRateAvg);

            planReturnMoneyList.add(planReturnMoney);

            info.returnPremium = returnMoney;

            // 납입기간까지만 데이터를 쌓고 나간다.
            // if (term.equals(info.getNapTerm())){
            // 	break;
            // }

        }
        info.setPlanReturnMoneyList(planReturnMoneyList);

        logger.debug("planReturnMoney :: " + new Gson().toJson(info.getPlanReturnMoneyList()));
    }*/
}
