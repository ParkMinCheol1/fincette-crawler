package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;


// 2023.02.17           | 최우진               | 질병보험 (미니질병)
// SHL_DSS_D004         | 신한스마트폰건강케어보험M(무배당)
public class SHL_DSS_D004 extends CrawlingSHL {



    public static void main(String[] args) {
        executeCommand(new SHL_DSS_D004(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("| START | SHL_DSS_D004 :: 신한스마트폰건강케어보험M(무배당)");

        // 모달 이벤트 창 닫기 (2022.09.01 포인트증정이벤트 - 모달창 수정)
        try {
            driver.findElement(By.xpath("//*[@id='popu_100000000000500']/div[2]/div/div/div[3]/button")).click();
        } catch(Exception e) {
            logger.info("모달창이 있었는데 없어졌습니다");
        }

        logger.info("디지털 보험 상품 전체 리스트 버튼");
        try {
            WaitUtil.loading(1);
            driver.findElement(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']")).click();
//            driver.findElement(By.cssSelector("#pageWrap > section.digiProduct.layer.easeOutExpo.open > div > div > div > ul > li:nth-child(11) > a")).click();
        } catch(Exception e) {
            logger.error("해당 리스트업 버튼이 존재하지 않습니다.");
        }

        logger.info("신한스마트폰건강케어보험M(무배당)");
        try {
            WaitUtil.loading(1);
            helper.click(By.xpath("//ul[@class='mainDigiPrd']//div[text()='신한스마트폰건강케어보험M(무배당)']//parent::a"));
//            helper.doClick(By.cssSelector("#pageWrap > section.digiProduct.layer.easeOutExpo.open > div > div > div > ul > li:nth-child(11) > a"));
        } catch(Exception e) {
            logger.error("레이블 리스트에 해당상품이 존재하지 않습니다.");
        }

        // 화면 변경 (이동X) ========================================================

        WaitUtil.loading(3);
        logger.info("성별 : {}", (info.gender == 0) ? "남자" : "여자");
        setGenderWeb(info.gender);

        logger.info("생년월일 : {}", info.fullBirth);
        WaitUtil.loading(1);
        driver.findElement(By.xpath("//div[@class='iptWrap']//input[@name='birymd']")).sendKeys(info.fullBirth);

        logger.info("'보험료 확인' 버튼 클릭");
        WaitUtil.loading(1);
        driver.findElement(By.id("btnCalInpFe")).click();

        // 화면 변경 (이동X) ========================================================

        logger.info("보장기간 설정 : {}", info.insTerm);
        WaitUtil.loading(2);
        selectOptionByText(By.id("selectMnprIsteCn"), info.insTerm);

        logger.info("납입기간 설정 : {}", info.napTerm);
        try{
            WaitUtil.loading(1);
            selectOptionByText(By.id("selectMnprPmpeTc"), info.napTerm);
            // 중복 실행
            info.napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm;
            selectOptionByText(By.id("selectMnprPmpeTc"), info.napTerm);
        } catch (Exception e){
            logger.error("납입기간 선택불가");
        }

        DecimalFormat decForm = new DecimalFormat("###,###");
        String formedAssuMoney = decForm.format(Integer.parseInt(info.assureMoney));
        logger.info("보험가입금액 설정 : {}", formedAssuMoney+"원");
        try {
            WaitUtil.waitFor(1);
            selectOptionByText(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select"), formedAssuMoney+"원");
        }catch(Exception e) {
            logger.error("가입금액 수정 불가");
        }

        logger.info("'다시 계산하기' 클릭");
        try{
            WaitUtil.waitFor(1);
            driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
        } catch(Exception e) {
            logger.error("'다시 계산하기'버튼이 존재하지 않습니다.");
        }

        // =============================================================

        logger.info("월 보험료 설정");
        try {
            WaitUtil.waitFor(2);
            String monthlyPremium  = driver.findElement(By.xpath("//em [@class='pointC5 sumInpFe']")).getText().replaceAll("[^0-9]", "");
            info.treatyList.get(0).monthlyPremium = monthlyPremium;
            logger.info("월 보험료 - 원수사 : {}원", monthlyPremium);
            logger.info("월 보험료 - INFO  : {}원", info.treatyList.get(0).monthlyPremium);
        } catch(Exception e) {
            logger.error("월 보험료를 확인할 수 없습니다.");
        }

        logger.info("스크린샷");
        try {
            WaitUtil.waitFor(1);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("scroll(0, 250);");
            takeScreenShot(info);
            logger.info("찰칵!");

        } catch(Exception e) {
            logger.error("스크린샷을 찍을 수 없습니다.");
        }

        logger.info("해약환급금 조회");
        WaitUtil.waitFor(2);
        driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
        WaitUtil.waitFor(2);
        // ex1)    	경과 	- 납입보험료 	- 해약환급금 	- 환급률
        //  		3개월 	- 15000원 		- 0원 			- 0.0%
        //			6개월	- 189,000원		- 0원			- 0.0%
        try {
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
            int rowIndex = 1;
            boolean isValubale = true;
            while(isValubale) {
                try {
                    int colIndex = 1;
                    String term = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/th")).getText();
                    String premiumSum = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
                    String returnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
                    String returnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex) + "]")).getText();
                    rowIndex++;

                    logger.info("================================");
                    logger.info("경과기간 : {}", term);
                    logger.info("납입보험료 : {}", premiumSum);
                    logger.info("해약환급금 : {}", returnMoney);
                    logger.info("환급률 : {}", returnRate);

                    info.returnPremium = returnMoney;

                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                    planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                    planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);
                    planReturnMoneyList.add(planReturnMoney);

                } catch(NoSuchElementException nsee) {
                    isValubale = false;
                }
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch(Exception e) {
            logger.error("해약 환급금이 존재하지 않습니다.");
        }

        logger.info("INNER PROCESS END");

        return true;
    }
}
