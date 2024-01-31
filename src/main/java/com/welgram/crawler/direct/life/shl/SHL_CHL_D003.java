package com.welgram.crawler.direct.life.shl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;


// 2022.07.01           | 최우진               | 어린이보험 (미니어린이보험 : MCH)
// SHL_CHL_D003         | 신한우리아이교통보장보험M(무배당)
public class SHL_CHL_D003 extends CrawlingSHL {



    public static void main(String[] args) {
        executeCommand(new SHL_CHL_D003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: SHL_CHL_D003 :: 신한우리아이교통보장보험M(무배당)");
        WaitUtil.loading(3);

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

        // input
        try {
            logger.info("text_type 확인");
            String[] arrTextType = info.textType.split("#");
            for(int i = 0; i < arrTextType.length; i++) {
                logger.info("ARRTEXTTYPE["+i+"] :: {}", arrTextType[i]);
                // 0 : 신한우리아이교통보장보험M(무배당)
            }

            logger.info("디지털 보험 상품 전체 레이블 리스트 모달 업");
            helper.click(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']"));
            WaitUtil.waitFor(2);

            logger.info("'신한우리아이교통보장보험M(무배당)' 선택");
            helper.click(By.xpath("//ul[@class='mainDigiPrd']//div[text()='" + arrTextType[0] + "']//parent::a"));
            WaitUtil.loading(2);

            // 화면 변환

            logger.info("성별 : {}", (info.gender == 0) ? "남자" : "여자");
            setGenderWeb(info.gender);

            logger.info("생년월일 : {}", info.fullBirth);
//            helper.doSendKeys(By.xpath("//div[@class='iptWrap']//input[@name='birymd']"), info.fullBirth);
            driver.findElement(By.id("birymd")).sendKeys(info.getFullBirth());


            logger.info("'보험료 확인' 버튼 클릭");
            helper.click(By.id("btnCalInpFe"));
            WaitUtil.loading(5);

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));

        } catch(Exception e) {
            throw new CommonCrawlerException(e, "크롤링(모니터링)의 설정(INPUT)이 잘못 되었습니다.");
        }

        // output
        logger.info("월 보험료 설정");
        String monthlyPremium = driver.findElement(By.xpath("//em [@class='pointC5 sumInpFe']")).getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = monthlyPremium;
        logger.info("월 보험료 - INFO  : {}원", info.treatyList.get(0).monthlyPremium);

        logger.info("스크린샷");
        takeScreenShot(info);
        logger.info("찰칵!");

        logger.info("해약환급금 조회");
        try {
            helper.click(By.xpath("//a[text()='해약환급금 예시']"));
            WaitUtil.waitFor(2);
            // ex1)    	경과 	- 납입보험료 	- 해약환급금 	- 환급률
            //  		3개월 	- 15000원 		- 0원 			- 0.0%
            //			6개월	- 189,000원		- 0원			- 0.0%
            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            int rowIndex = 1;
            boolean isValubale = true;
            while(isValubale) {
                try {
                    int colIndex = 1;
                    String term = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/th")).getText();
                    String premiumSum = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
                    String returnMoney = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex++) + "]/span")).getText().replaceAll("[^0-9]", "");
                    String returnRate = driver.findElement(By.xpath("//*[@id='tbl_boardList01']/div/table/tbody/tr[" + rowIndex + "]/td[" + (colIndex) + "]")).getText();

                    info.returnPremium = returnMoney;
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

                } catch(NoSuchElementException nsee) {
                    isValubale = false;
                    logger.error("더 이상 참조할 차트가 존재하지 않습니다.");
                }
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);

            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new CommonCrawlerException("해약환급금이 존재하지 않습니다.");
        }

        logger.info("END INNER PROCESS");

        return true;
    }
}
