package com.welgram.crawler.direct.life.shl.deleted;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanCalc;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;


/**
 * (무)신한인터넷암이면다암보험 21.07.07 SHL_CCR_D004 -> SHL_CCR_D005 로 전환
 */
// 2022.07.07       | 최우진           | 다이렉트_암
// SHL_CCR_D005     | 신한인터넷암이면다암보험(무배당, 갱신형)
public class SHL_CCR_D005 extends CrawlingSHL {



    public static void main(String[] args) {
        executeCommand(new SHL_CCR_D005(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        logger.info("START | SHL_CCR_D005 :: 신한인터넷암이면다암보험(무배당, 갱신형) 기본형");

        // INFORMATION
        logger.info("textType 확인");
        String[] arrTextType = info.getTextType().split("#");
        for(int i = 0; i < arrTextType.length; i++ ) {
            arrTextType[i] = arrTextType[i].trim();
            logger.info("TEXT_TYPE :: {}", arrTextType[i]);
            // 0 : (10년갱신)보장강화형 or (15년갱신)보장강화형
        }

        // 모달 이벤트 창 닫기 (2022.09.01 포인트증정이벤트 - 모달창 수정)
        try {
            driver.findElement(By.xpath("//*[@id='popu_100000000000500']/div[2]/div/div/div[3]/button")).click();
        } catch(Exception e) {
            logger.info("모달창이 있었는데 없어졌습니다");
        }

        logger.info("디지털 보험 상품 전체 리스트 버튼");

        driver.findElement(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']")).click();
        WaitUtil.loading(2);

        logger.info("'신한인터넷암이면다암보험(무배당, 갱신형) 선택");
        driver.findElement(By.xpath("//ul[@class='mainDigiPrd']//div[text()='신한인터넷암이면다암보험(무배당, 갱신형)']//parent::a")).click();
        WaitUtil.loading(4);

        // 화면 변환 ==============================================================
        logger.info("성별 : {}", (info.getGender() == 0) ? "남자" : "여자");
        try {
            setGenderWeb(info.getGender());
        } catch(Exception e) {
            throw new CommonCrawlerException(e, "성별의 설정이 잘못되었습니다");
        }

        logger.info("생년월일 : {}", info.getFullBirth());
        helper.sendKeys3_check(By.xpath("//div[@class='iptWrap']//input[@name='birymd']"), info.getFullBirth());

        logger.info("'보험료 확인' 버튼 클릭");
//        helper.doClick(By.id("btnCalInpFe"));
        driver.findElement(By.id("btnCalInpFe")).click();
        WaitUtil.loading(4);

        // 화면 변환 ==============================================================
        logger.info("보험형태 선택 : " + info.getTextType());
        try {
            selectOptionByText(By.id("selInsFormCd"), info.getTextType());  //(15년갱신)기본형
            WaitUtil.loading(3);
        } catch(Exception e) {
            throw new CommonCrawlerException("보험형태 설정이 잘못되었습니다");
        }

        DecimalFormat decForm = new DecimalFormat("###,###");
        String formedAssureMoney = decForm.format(Integer.parseInt(info.getAssureMoney()));
        logger.info("보험가입금액 설정 : {}", formedAssureMoney+"원");
        try {
            selectOptionByText(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select"), formedAssureMoney+"원");
            WaitUtil.loading(3);
        } catch(Exception e) {
            throw new CommonCrawlerException("보험가입금액의 설정이 잘못되었습니다");
        }

        logger.info("'다시 계산하기' 클릭");
        try {
            driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
            WaitUtil.loading(4);
        } catch(Exception e) {
            logger.error("[ 다시 계산하기 ] 버튼이 존재하지 않습니다");
        }

        // 화면 변환 ==============================================================

        logger.info("월 보험료 조회");
        try {
            String monthlyPremium = driver.findElement(By.xpath("//em [@class='pointC5 sumInpFe']")).getText().replaceAll("[^0-9]", "");
            info.getTreatyList().get(0).monthlyPremium = monthlyPremium;
            logger.info("월 보험료 - INFO  : {}원", info.getTreatyList().get(0).monthlyPremium);

            // 올페이 급여금 계산
            for(CrawlingTreaty eachTreaty : info.getTreatyList()) {
                if(eachTreaty.getAssureMoney() == 0) {
                    // 특수특약 가입금액 = 월 보험료 * 납입기간(년) * 12(월)
                    int tempAssureMoney = Integer.parseInt(monthlyPremium) * 12 * Integer.parseInt(info.getNapTerm().replaceAll("[^0-9]", ""));

                    PlanCalc exceptionalPlanCalc = new PlanCalc();
                    exceptionalPlanCalc.setGender((info.getGender() == MALE) ? "M" : "F");
                    exceptionalPlanCalc.setMapperId(Integer.parseInt(eachTreaty.mapperId));
                    exceptionalPlanCalc.setInsAge(Integer.parseInt(info.getAge()));
                    exceptionalPlanCalc.setAssureMoney(String.valueOf(tempAssureMoney));
                    eachTreaty.setPlanCalc(exceptionalPlanCalc);
                    logger.info("특수특약 특약명 :: {} | 가입금액 :: {}",eachTreaty.getTreatyName(), eachTreaty.getPlanCalc().getAssureMoney());
                }
            }
        } catch(Exception e) {
            throw new CommonCrawlerException("월 보험료 조회시 에러가 발생하였습니다 [" + e.getMessage() + "]");
        }

        logger.info("스크린샷");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("scroll(0, 250);");
        takeScreenShot(info);
        logger.info("찰칵!");

        logger.info("해약환급금 조회");
        try {
//            helper.doClick(By.xpath("//a[text()='해약환급금 예시']"));
            driver.findElement(By.xpath("//a[text()='해약환급금 예시']")).click();
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

                    rowIndex++;
                    info.setReturnPremium(returnMoney);

                    logger.info("================================");
                    logger.info("경과기간   : {}", term);
                    logger.info("납입보험료 : {}", premiumSum);
                    logger.info("해약환급금 : {}", returnMoney);
                    logger.info("환급률    : {}", returnRate);

                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                    planReturnMoney.setPlanId(Integer.parseInt(info.getPlanId()));
                    planReturnMoney.setGender((info.getGender() == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(premiumSum);
                    planReturnMoney.setReturnMoney(returnMoney);
                    planReturnMoney.setReturnRate(returnRate);
                    planReturnMoneyList.add(planReturnMoney);

                } catch(NoSuchElementException nsee) {
                    isValubale = false;
                    logger.info("=================================");
                    logger.error("더 이상 참조할 차트가 존재하지 않습니다");
                    logger.info("=================================");
                }
            }
            info.setPlanReturnMoneyList(planReturnMoneyList);
            logger.info("만기환급금 : {}", info.getReturnPremium());

        } catch(Exception e) {
            throw new CommonCrawlerException("해약 환급금이 존재하지 않습니다.");
        }
        logger.info("INNER PROCESS END");

        return true;
    }

    @Override
    protected boolean preValidation(CrawlingProduct info) {
        boolean result = true;
        if(info.getTreatyList().size() != 3){
            String selectedGender = (info.getGender() == 0 ? "남성" : "여성") ;
            logger.info(selectedGender+ "은 해당 가설에서 가입할 수 없는 성별입니다.");
            result = false;
        }

        return result;
    }
}



