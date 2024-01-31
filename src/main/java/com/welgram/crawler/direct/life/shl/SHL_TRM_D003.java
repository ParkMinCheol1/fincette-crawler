package com.welgram.crawler.direct.life.shl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;


/**
 * 신한생명 - (무)신한인터넷정기보험 재해보장형
 *
 * @author SungEun Koo <aqua@welgram.com>
 */

// 2022.07.08           | 최우진               | 정기보험 (일반)
// SHL_TRM_D003         | 신한인터넷정기보험(무배당)
public class SHL_TRM_D003 extends CrawlingSHL {

    public static void main(String[] args) {
        executeCommand(new SHL_TRM_D003(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START | SHL_TRM_D003 :: 신한인터넷정기보험(무배당) - 재해보장형");
        WaitUtil.waitFor(3);

        logger.info("textType 확인");
        String[] arrTextType = info.getTextType().split("#");
        for(int i = 0; i < arrTextType.length; i++ ) {
            arrTextType[i] = arrTextType[i].trim();
            logger.info("TEXT_TYPE :: {}", arrTextType[i]);
            // 0 :
            // 1 :
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


        logger.info("디지털 보험 상품 전체 레이블 리스트 모달 업");
        helper.click(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']"));
        WaitUtil.waitFor(1);

        logger.info("'신한인터넷정기보험(무배당)' 선택");
        helper.click(By.xpath("//ul[@class='mainDigiPrd']//div[text()='신한인터넷정기보험(무배당)']//parent::a"));
        WaitUtil.loading(4);

        // 화면 변환 ==============================================================

        logger.info("성별 : {}", (info.getGender() == 0) ? "남자" : "여자");
        try {
            setGenderWeb(info.getGender());

        } catch (Exception e) {
            throw new CommonCrawlerException("성별을 선택할 수 없습니다");
        }

        logger.info("생년월일 : {}", info.getFullBirth());
//        helper.doSendKeys(By.xpath("//div[@class='iptWrap']//input[@name='birymd']"), info.getFullBirth());
        driver.findElement(By.xpath("//div[@class='iptWrap']//input[@name='birymd']")).sendKeys(info.getFullBirth());

        logger.info("'보험료 확인' 버튼 클릭");
//        helper.doClick(By.id("btnCalInpFe"));
        driver.findElement(By.id("btnCalInpFe")).click();
        WaitUtil.loading(4);

        // 화면 변환 ==============================================================

        logger.info("보험형태 설정 :: {}", info.getTextType());
        try {
            selectOptionByText(By.id("selInsFormCd"), info.getTextType());
            WaitUtil.loading(2);

        } catch(Exception e) {
            throw new CommonCrawlerException("보험형태를 설정할 수 없습니다");
        }

        logger.info("보장기간 설정 :: {}", info.getInsTerm());
        try {
            selectOptionByText(By.id("selectMnprIsteCn"), info.getInsTerm());
            WaitUtil.loading(3);

        } catch(Exception e) {
            throw new CommonCrawlerException("보장기간을 설정할 수 없습니다");
        }

        logger.info("납입기간 설정 : {}", info.getNapTerm());
        try{
            selectOptionByText(By.id("selectMnprPmpeTc"), info.getNapTerm());
            String strNapTerm = (info.getInsTerm().equals(info.getNapTerm())) ? "전기납" : info.getNapTerm();
            selectOptionByText(By.id("selectMnprPmpeTc"), strNapTerm);
            WaitUtil.loading(3);

        } catch (Exception e){
            throw new CommonCrawlerException("납입기간을 선택할 수 없습니다");
        }

        DecimalFormat decForm = new DecimalFormat("###,###");
        String formedAssuMoney = decForm.format(Integer.parseInt(info.getAssureMoney()));
        logger.info("보험가입금액 설정 : {}", formedAssuMoney+"원");
        try {
            selectOptionByText(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select"), formedAssuMoney+"원");
            WaitUtil.waitFor(3);

        }catch(Exception e) {
            throw new CommonCrawlerException("보험가입금액을 설정할 수 없습니다");
        }

        logger.info("'다시 계산하기' 클릭");
        try{
            driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
            WaitUtil.waitFor(3);

        } catch(Exception e) {
            throw new CommonCrawlerException("'다시 계산하기'버튼이 존재하지 않습니다");
        }

        // GET AMOUNTS  ==================================================================================
        logger.info("월 보험료 확인");
        try {
            WaitUtil.waitFor(3);
            String monthlyFee = driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[2]/div[1]/div[2]/em")).getText().replaceAll("[^0-9]", "");
            logger.info("월 보험료 : " + monthlyFee);
            info.getTreatyList().get(0).monthlyPremium = monthlyFee;

        } catch(Exception e) {
            throw new CommonCrawlerException("월 보험료를 조회할 수 없습니다.");
        }

        logger.info("스크린샷");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("scroll(0, 250);");
        takeScreenShot(info);
        WaitUtil.waitFor(3);

        logger.info("해약환급금 예시 조회");
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

                    info.setReturnPremium(returnMoney);
                    rowIndex++;

                    logger.info("================================");
                    logger.info("경과기간 : {}", term);
                    logger.info("납입보험료 : {}", premiumSum);
                    logger.info("해약환급금 : {}", returnMoney);
                    logger.info("환급률 : {}", returnRate);

                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                    planReturnMoney.setPlanId(Integer.parseInt(info.planId));
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
                info.setPlanReturnMoneyList(planReturnMoneyList);
            }

        } catch(Exception e) {
            throw new CommonCrawlerException("해약환급금이 존재하지 않습니다.");
        }

        //해당 상품의 해약환급금 표의 가장 마지막 값은 보험기간 만기 하루 전날의 해약환급금을 명시하고 있음.
        // 따라서 순수보장형 상품의 경우 만기환급금이 0원이 되어야하는게 맞음.
        if(info.getTreatyList().get(0).productKind.equals(ProductKind.순수보장형)) {
            logger.info("보험형태 : {} 상품이므로 만기환급금을 0원으로 설정합니다", info.getTreatyList().get(0).productKind);
            info.setReturnPremium("0");
        }
        logger.info("만기환급금 : {}원", info.getReturnPremium());

        logger.info("INNER PROCESS END");
        logger.info("=================================");

        return true;
    }
}



