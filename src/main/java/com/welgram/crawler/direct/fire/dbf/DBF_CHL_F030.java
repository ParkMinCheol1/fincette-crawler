package com.welgram.crawler.direct.fire.dbf;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;


public class DBF_CHL_F030 extends CrawlingDBFAnnounce {

    public static void main(String[] args) {
        executeCommand(new DBF_CHL_F030(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("가입 유형 선택");
        setProductType("sl_pan_cd", info.getTextType());

        logger.info("운전형태 : 비운전자 고정");
        setVehicle("DRIVE_TYPE_CD", "비운전자");

        logger.info("보험기간 선택 (보험기간+납입기간 같이)");
        WebElement $termSelect = driver.findElement(By.cssSelector("select[name=exp_pytr]"));
        String insNapTerm = info.insTerm + "만기" + info.napTerm + "납";
        setTerm($termSelect, insNapTerm);

        logger.info("납입주기 선택");
        WebElement $napCycleSelect = driver.findElement(By.cssSelector("select[name=pym_mtd_cd]"));
        String napCycleText = info.napCycle.equals("01") ? "월납" : "연납";
        setNapCycle($napCycleSelect, napCycleText);

        logger.info("생년월일 입력");
        setBirthday(By.cssSelector("#birthDay2"), info.fullBirth);

        logger.info("성별선택");
        setGender("sx_cd", info.gender);

        logger.info("직업 입력");
        setJob("미취학아동");

        logger.info("보장목록 확인 클릭");
        helper.waitElementToBeClickable(driver.findElement(By.linkText("보장목록 확인"))).click();
        waitAnnounceLoadingImg();

        logger.info("특약 셋팅");
        List<WebElement> $trList = driver.findElements(By.cssSelector("#tableDamboList > tr"));
        setTreaties($trList, info.treatyList);

        logger.info("보험료를 확인");
        WebElement buttonPremium = driver.findElement(By.xpath("//a[text()='보험료 산출']"));
        helper.click(buttonPremium, "보험료 산출 버튼");
        WaitUtil.loading(3);

        logger.info("보험료 크롤링");
        WebElement $monthlyPremiumTd = driver.findElement(By.xpath("//td[@class='ft rt']"));
        helper.moveToElementByJavascriptExecutor($monthlyPremiumTd);
        crawlPremium($monthlyPremiumTd, info);

        logger.info(" 예상 총 환급금 크롤링");
        WebElement $returnMoneyTd = driver.findElement(By.xpath("//table[@id='tableResult']//td[@class='rt']"));
        crawlReturnPremium($returnMoneyTd, info);

        logger.info("스크린샷 찍기");
        takeScreenShot(info);

        logger.info("해약 환급금 크롤링");
        getReturnPremium(info);

        return true;
    }

//
//
//    /**
//     * DBF 공시실 월 납입보험료 셋팅
//     * @param obj
//     * obj[0] : 산출된 합계보험료 element(WebElement)
//     * obj[1] : CrawlingProduct info
//     * @throws PremiumCrawlerException
//     */
//    @Override
//    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
//
//        try {
//            WebElement $monthlyPremiumTd = (WebElement) obj[0];
//            CrawlingProduct info = (CrawlingProduct) obj[1];
//
//            String premium = $monthlyPremiumTd.getText().replaceAll("[^0-9]", "");
//            info.treatyList.get(0).monthlyPremium = premium;
//            logger.info("월 보험료 확인 : " + premium);
//
//            if ("0".equals(info.treatyList.get(0).monthlyPremium)) {
//                throw new Exception("주계약 보험료는 0원일 수 없습니다");
//            }
//        } catch (Exception e) {
//            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
//            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
//        }
//    }
//
//
//
//    /**
//     * 공시실 해약환급금 크롤링
//     * @param obj
//     * obj[0] : CrawlingProduct info (필수)
//     */
//    public void getReturnPremium(Object...obj) throws Exception {
//        CrawlingProduct info = (CrawlingProduct) obj[0];
//
//        logger.info("해약환급금 버튼 클릭");
//        helper.waitElementToBeClickable(driver.findElement(By.linkText("해약 환급금"))).click();
//        waitAnnounceLoadingImg();
//
//        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
//
//        List<WebElement> $refundTrList = driver.findElements(By.cssSelector("#refundList > tr"));
//
//        for (int i=0; i<$refundTrList.size(); i++) {
//            WebElement $tr = $refundTrList.get(i);
//
//            String term = $tr.findElement(By.cssSelector("td:nth-child(1)")).getText().trim();
//            String premiumSum = $tr.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
//            String returnMoneyMin = $tr.findElement(By.cssSelector("td:nth-child(3)")).getText()
//                .trim();
//            String returnRateMin = $tr.findElement(By.cssSelector("td:nth-child(4)")).getText()
//                .trim();
//            String returnMoneyAvg = $tr.findElement(By.cssSelector("td:nth-child(5)")).getText()
//                .trim();
//            String returnRateAvg = $tr.findElement(By.cssSelector("td:nth-child(6)")).getText()
//                .trim();
//            String returnMoney = $tr.findElement(By.cssSelector("td:nth-child(7)")).getText()
//                .trim();
//            String returnRate = $tr.findElement(By.cssSelector("td:nth-child(8)")).getText().trim();
//
//            logger.info("------------------------------------");
//            logger.info(term + " 경과기간 :: " + term);
//            logger.info(term + " 납입보험료 :: " + premiumSum);
//            logger.info(term + " 최저해약환급금 :: " + returnMoneyMin);
//            logger.info(term + " 최저해약환급률 :: " + returnRateMin);
//            logger.info(term + " 평균해약환급금 :: " + returnMoneyAvg);
//            logger.info(term + " 평균해약환급률 :: " + returnRateAvg);
//            logger.info(term + " 현재해약환급금 :: " + returnMoney);
//            logger.info(term + " 현재해약환급률 :: " + returnRate);
//            logger.info("------------------------------------");
//
//            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//            planReturnMoney.setTerm(term);
//            planReturnMoney.setPremiumSum(premiumSum);
//            planReturnMoney.setReturnMoneyMin(returnMoneyMin);
//            planReturnMoney.setReturnRateMin(returnRateMin);
//            planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
//            planReturnMoney.setReturnRateAvg(returnRateAvg);
//            planReturnMoney.setReturnMoney(returnMoney);
//            planReturnMoney.setReturnRate(returnRate);
//
//            planReturnMoneyList.add(planReturnMoney);
//
//            // 위치에 맞게 스크롤 내리기
//            if ($refundTrList.size() != (i + 1)) {
//                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
//                    $tr.findElement(By.cssSelector("td:nth-child(1)")));
//            }
//
//            /*
//            todo
//            만기환급금 정보 크롤링 메서드 따로 추출하기 ->  crawlReturnPremium
//            구현 CASE1 화면상에 만기환급금 이라고 명시적으로 게시하는 경우가 있다 그러면 그걸 크롤링하면 됨
//            구현 CASE2 없는 경우엔 중도해약환급금(crawlReturnPremium) 목록에서 경과기간이 만기에 해당하는 해약환급금을 만기환급금으로 간주, 크롤링한다.
//                   만기에 해당하는 해약환급금도 없을 경우엔 -1로 처리한다.
//             */
//            if(returnMoney.equals("")){
//                info.returnPremium = returnMoney.replace(",", "").replace("원", "");
//                logger.info("만기환급금 : {}원", info.returnPremium);
//            }
//        }
//
//        info.planReturnMoneyList = planReturnMoneyList;
//    }

}