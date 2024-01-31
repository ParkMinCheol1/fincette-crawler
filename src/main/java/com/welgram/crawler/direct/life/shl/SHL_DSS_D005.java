package com.welgram.crawler.direct.life.shl;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;


// 2022.07.04       | 최우진           | 다이렉트_질병(미니질병보험)
// SHL_DSS_D005     | 신한포시즌케어보험M(무배당)
public class SHL_DSS_D005 extends CrawlingSHL {

    public static void main(String[] args) {
        executeCommand(new SHL_DSS_D005(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: SHL_DSS_D005 :: 신한포시즌케어보험M(무배당)");
        logger.info("text_type 확인");
        String[] arrTextType = info.getTextType().split("#");
        for(int i = 0; i < arrTextType.length; i++) {
            logger.info("ARRTEXTTYPE["+i+"] :: {}", arrTextType[i]);
            // 0 : 신한포시즌케어보험M(무배당)
        }

        // ======================================================
        // 모달 이벤트 창 닫기 (2022.09.01 포인트증정이벤트 - 모달창 수정)
        // 모달 창 없어짐 (2022.12.01 확인 | 코드 주석)
        // 모달 창 (2023.05.08)
        logger.info("모달 창 닫기");
        try {
//			driver.findElement(By.xpath("//*[@id='popu_100000000000500']/div[2]/div/div/div[3]/button")).click();
            driver.findElement(By.xpath("//span[text()='팝업 닫기']/parent::button")).click();
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            logger.info("모달창이 있었는데 없어졌습니다");
        }

        helper.findExistentElement(
                By.xpath("//div[@class='popContain']//button[@class='close']"), 1L)
            .ifPresent(el -> helper.click(el, "연금 저축 가입 이벤트 팝업 클릭"));


        logger.info("디지털 보험 상품 전체 리스트 버튼");
        WaitUtil.waitFor(1);
        driver.findElement(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']")).click();

        logger.info("'신한포시즌케어보험M(무배당) 선택'");
        WaitUtil.waitFor(2);
        driver.findElement(By.xpath("//ul[@class='mainDigiPrd']//div[text()='" + arrTextType[0] + "']")).click();
        WaitUtil.loading(3);

        logger.info("성별 : {}", (info.getGender() == 0) ? "남자" : "여자");
        setGenderWeb(info.getGender());

        logger.info("생년월일 : {}", info.getFullBirth());
        driver.findElement(By.xpath("//div[@class='iptWrap']//input[@name='birymd']")).sendKeys(info.getFullBirth());

        logger.info("'보험료 확인' 버튼 클릭");
        driver.findElement(By.id("btnCalInpFe")).click();
        WaitUtil.loading(5);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));

        logger.info("보장기간 선택 :: {}", info.getInsTerm());
        WaitUtil.loading(2);
        selectOptionByText(By.id("selectMnprIsteCn"), info.getInsTerm());

        logger.info("'다시 계산하기' 클릭");
        try{
            driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
        } catch(Exception e) {
            logger.error("'다시 계산하기'버튼이 존재하지 않습니다.");
        }
        WaitUtil.waitFor(2);

        logger.info("월 보험료 설정");
        String monthlyPremium  = driver.findElement(By.xpath("//em [@class='pointC5 sumInpFe']")).getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = monthlyPremium;
        logger.info("월 보험료 - 원수사 : {}원", monthlyPremium);
        logger.info("월 보험료 - INFO  : {}원", info.treatyList.get(0).monthlyPremium);

        logger.info("스크린샷");
        WaitUtil.waitFor(1);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("scroll(0, 250);");
        takeScreenShot(info);
        logger.info("찰칵!");

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

                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                    planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                    planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);

                    planReturnMoneyList.add(planReturnMoney);
                    info.returnPremium = returnMoney;

                } catch(NoSuchElementException nsee) {
                    isValubale = false;
                    logger.error("더 이상 참조할 차트가 존재하지 않습니다.");
                }
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch(Exception e) {
            logger.error("해약 환급금이 존재하지 않습니다.");
        }
        logger.info("END INNER PROCESS");

        return true;
    }
}
