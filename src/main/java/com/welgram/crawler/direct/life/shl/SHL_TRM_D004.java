package com.welgram.crawler.direct.life.shl;

import com.welgram.common.PersonNameGenerator;
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
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 신한생명 - (무)신한인터넷정기보험 기본형
 *
 * @author SungEun Koo <aqua@welgram.com>
 */
// 2023.02.17       | 최우진           | 다이렉트_정기보험
// SHL_TRM_D004     | 신한인터넷정기보험(무배당)
public class SHL_TRM_D004 extends CrawlingSHL {

//
//    public static FinLogger logger = FinLogger.getFinLogger(SHL_TRM_D004.class);

    public static void main(String[] args) {
        executeCommand(new SHL_TRM_D004(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

//        openWebPage(info);            //웹 페이지 크롤링
//        openAnnouncePage(info);       //공시실 크롤링  http://www.shinhanlife.co.kr/pbf/k/PBFK2P1.jsp?goodCd=9001031&smart=Y

//        logger.info(getProductCode(), "START | SHL_TRM_D004 :: (무)신한인터넷정기보험 기본형");
//        logging("START | SHL_TRM_D004 :: (무)신한인터넷정기보험 기본형");

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
        try {
            WaitUtil.waitFor(1);
            driver.findElement(By.xpath("//div[@class='prdSorting']//button[@class='icoBtn_total']")).click();
        } catch (Exception e) {
            throw new CommonCrawlerException("관련 리스트의 모달이 존재하지 않습니다.");
        }

        logger.info("'신한인터넷정기보험(무배당)' 선택");
        try {
            WaitUtil.loading(1);
            helper.click(By.xpath("//ul[@class='mainDigiPrd']//div[text()='신한인터넷정기보험(무배당)']//parent::a"));
        } catch (Exception e) {
            throw new CommonCrawlerException("레이블 리스트에서 해당 상품 선택 불가");
        }

        // 화면 변환

        logger.info("성별 : {}", (info.gender == 0) ? "남자" : "여자");
        try {
            WaitUtil.loading(3);
            setGenderWeb(info.gender);

        } catch (Exception e) {
            throw new CommonCrawlerException("성별을 선택할 수 없습니다");
        }
        logger.info("생년월일 : {}", info.fullBirth);
        try {
            WaitUtil.loading(3);
//            driver.findElement(By.xpath("//div[@class='iptWrap']//input[@name='birymd']")).sendKeys(info.fullBirth);
            driver.findElement(By.id("birymd")).sendKeys(info.getFullBirth());

        } catch (Exception e) {
            throw new CommonCrawlerException("생년월일을 선택할 수 없습니다");
        }

        logger.info("'보험료 확인' 버튼 클릭");
        try {
            WaitUtil.loading(3);
            driver.findElement(By.id("btnCalInpFe")).click();
        } catch (Exception e) {
            throw new CommonCrawlerException("보험료 확인 버튼을 클릭할 수 없습니다");
        }

        logger.info("보험형태 설정1 : {}", info.textType);
        try {
            WaitUtil.loading(3);
            selectOptionByText(By.id("selInsFormCd"), info.textType);

        } catch(Exception e) {
            throw new CommonCrawlerException("보험형태를 설정 할 수 없습니다");
        }

        logger.info("보장기간 설정 : {}", info.insTerm);
        try {
            WaitUtil.loading(3);
            selectOptionByText(By.id("selectMnprIsteCn"), info.insTerm);

        } catch(Exception e) {
            throw new CommonCrawlerException("보장기간을 설정할 수 없습니다");
        }

        logger.info("납입기간 설정 : {}", info.napTerm);
        try{
            WaitUtil.loading(3);
            selectOptionByText(By.id("selectMnprPmpeTc"), info.napTerm);

            info.napTerm = (info.insTerm.equals(info.napTerm)) ? "전기납" : info.napTerm;
            selectOptionByText(By.id("selectMnprPmpeTc"), info.napTerm);

        } catch (Exception e){
            throw new CommonCrawlerException("납입기간 선택불가");
        }

        DecimalFormat decForm = new DecimalFormat("###,###");
        String formedAssuMoney = decForm.format(Integer.parseInt(info.assureMoney));
        logger.info("보험가입금액 설정 : {}", formedAssuMoney+"원");
        try {
            WaitUtil.waitFor(3);
            selectOptionByText(By.xpath("//*[@id='insuPlanArea1']/div[1]/div/div[1]/select"), formedAssuMoney+"원");
        }catch(Exception e) {
            throw new CommonCrawlerException("보험가입금액 수정 불가");
        }

        logger.info("'다시 계산하기' 클릭");
        try{
            WaitUtil.waitFor(3);
            driver.findElement(By.xpath("//button[text()='다시 계산하기']")).click();
        } catch(Exception e) {
            throw new CommonCrawlerException("'다시 계산하기'버튼이 존재하지 않습니다");
        }

        // GET AMOUNTS  ==================================================================================

        logger.info("월 보험료 확인");
        try {
            WaitUtil.waitFor(3);
            String monthlyFee = driver.findElement(By.xpath("//*[@id='insuPlanArea1']/div[2]/div[1]/div[2]/em")).getText().replaceAll("[^0-9]", "");
            logger.info("월 보험료 : " + monthlyFee);
            info.treatyList.get(0).monthlyPremium = monthlyFee;

        } catch(Exception e) {
            throw new CommonCrawlerException("월 보험료를 조회할 수 없습니다.");
        }

        logger.info("스크린샷");
        try {
            WaitUtil.waitFor(3);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("scroll(0, 250);");
            takeScreenShot(info);
            logger.info("찰칵!");
        } catch(Exception e) {
            throw new CommonCrawlerException("스크린샷을 찍을 수 없습니다.");
        }

        WaitUtil.waitFor(2);
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
                    logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
                    isValubale = false;
                }
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

            // 해당 상품의 해약환급금 표의 가장 마지막 값은 보험기간 만기 하루 전날의 해약환급금을 명시하고 있음.
            // 따라서 순수보장형 상품의 경우 만기환급금이 0원이 되어야하는게 맞음.
            if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
                logger.info("보험형태 : {} 상품이므로 만기환급금을 0원으로 설정합니다", info.treatyList.get(0).productKind);
                info.returnPremium = "0";
            }
            logger.info("만기환급금 : {}원", info.returnPremium);

        } catch(NoSuchElementException e) {
            throw new CommonCrawlerException("해약환급금이 존재하지 않습니다.");
        }
        logger.info("INNER PROCESS END");

        return true;
    }


    protected void openWebPage(CrawlingProduct info) throws Exception{
        helper.waitForCSSElement(".blockUI.blockMsg.blockPage");
        moveToElement(By.xpath("//div[@class='topArticle']"));

        logger.info("성별 : {}", info.gender == 0 ? "남자" : "여자");
        setGenderWeb(info.gender);

        logger.info("생년월일 :: {}", info.fullBirth);
        driver.findElement(By.id("birth")).sendKeys(info.getFullBirth());

        logger.info("보험료 계산하기 버튼 클릭");
        driver.findElement(By.id("btnCalculate")).click();
        helper.waitForCSSElement(".blockUI.blockMsg.blockPage");
        WaitUtil.loading(2);

        logger.info("가입 설계");
        moveToPlanSetting();

        logger.info("보험형태 : 기본형(고정)");
        driver.findElement(By.cssSelector(".yesCalculated.zTop4 .sbSelector")).click();
        WaitUtil.waitFor(1);
        driver.findElement(By.xpath("//tr[@class='yesCalculated zTop4']//ul/li/a[text()='기본형']")).click();
        WaitUtil.waitFor(1);

        logger.info("보험기간 설정");
        driver.findElement(By.cssSelector(".yesCalculated.zTop5 .sbSelector")).click();
        WaitUtil.waitFor(1);
        driver.findElement(By.xpath("//tr[@class='yesCalculated zTop5']//ul/li/a[text()='" + info.insTerm + "']")).click();
        WaitUtil.waitFor(1);

        logger.info("납입기간 설정");
        driver.findElement(By.cssSelector(".yesCalculated.zTop6 .sbSelector")).click();
        WaitUtil.waitFor(1);
        driver.findElement(By.xpath("//tr[@class='yesCalculated zTop6']//ul/li/a[text()='" + info.napTerm + "']")).click();
        WaitUtil.waitFor(1);

        logger.info("보험료 계산하기 버튼 클릭");
        driver.findElement(By.id("btnCalculate")).click();
        if(helper.isAlertShowed()){
            throw new Exception("가입불가");
        }
        helper.waitForCSSElement(".blockUI.blockMsg.blockPage");
        WaitUtil.loading(1);

        logger.info("직접설계의 가입금액 설정");
        moveToElement(By.cssSelector(".calculrator .sbSelector"));
        driver.findElement(By.cssSelector(".calculrator .sbSelector")).click();
        WaitUtil.waitFor(1);
        driver.findElement(By.xpath("//div[@class='calculrator']//ul/li/a[@rel='" + info.assureMoney + "']")).click();
        helper.waitForCSSElement(".blockUI.blockMsg.blockPage");
        WaitUtil.waitFor(1);

        logger.info("계산 버튼 클릭");
        driver.findElement(By.cssSelector(".calculrator button")).click();
        helper.waitForCSSElement(".blockUI.blockMsg.blockPage");
        WaitUtil.loading(1);

        logger.info("월 보험료 설정");
        String monthlyPremium = driver.findElement(By.xpath("//div[@class[contains(., 'active')]]//span[@class='price']/em")).getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = monthlyPremium;
        logger.info("월 보험료 : {}원", info.treatyList.get(0).monthlyPremium);

        takeScreenShot(info);
        WaitUtil.waitFor(1);

        //정기보험도 해약환급금을 크롤링해야되나, 현재 원수사 홈페이지에 제대로된 해약환급표가 나오지않아 크롤링하지않음 (21.10.22 확인)
//        logger.info("상품설명서 버튼 클릭");
//        driver.findElement(By.xpath("//div[@class[contains(., 'active')]]//a[@class='dcConsult']")).click();
//
//        logger.info("상품설명서 창 전환");
//        ArrayList<String> tab = new ArrayList<>(driver.getWindowHandles());
//        driver.switchTo().window(tab.get(1));
//        helper.waitForCSSElement(".ers_progress");
//        WaitUtil.waitFor(3);
//
//        logger.info("해약환급금 조회");
//        boolean exist = confirmReturnPremium(info);
//        if(exist){
//            logger.info("해약환급금 조회");
//            getReturnPremium(info);
//        } else {
//            throw new Exception("해약환급금 페이지를 찾을 수 없습니다.");
//        }
    }

    @Override
    protected void openAnnouncePage(CrawlingProduct info) throws Exception{

        // 이름
        logger.info("[고객정보]이름 설정");
        String name = PersonNameGenerator.generate();
        logger.debug("name: {}", name);
        setName(name);

        // 생년월일
        logger.info("[고객정보]생년월일 설정");
        setBirth(info.fullBirth);

        // 성별
        logger.info("[고객정보]성별 설정");
        setGender(info.gender);

        // 설계목록가져오기
        logger.info("설계목록가져오기");
        getPlans();

        logger.info("보험형태 선택 :: {}", info.textType);
        selectPlan(By.id("insFormCd"), info.textType);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("gObverlay")));

//			setInsuranceKind(info.productKind);	// 보험종류
        logger.info(info.napCycle == "01" ? "월납" : "납기 확인 필요");
        setNapCycle(info.napCycle); // 납입주기

        // 보험가입금액(가입금액, 보험기간, 납입기간)
        logger.info("[주계약 /특약설계]보험가입금액 설정");
        setPremium(info);

        // 보험료 계산하기
        logger.info("보험료계산");
        calculatePremium();

        takeScreenShot(info);

        // 보험료 조회
        logger.info("보험료 조회");
        getCrawlingResults(info.treatyList);

        logger.info("해약환급금 조회");
        getReturns(info);
    }


    //웹 크롤링, 해약환급금 크롤링 (보험가입문서를 통한 크롤링)
    protected void getReturnPremium(CrawlingProduct info) throws Exception {
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        elements = helper.waitPesenceOfAllElementsLocatedBy(By.cssSelector("#m2soft-crownix-text > div"));

        String term = "";
        String premiumSum = "";
        String returnMoney = "";
        String returnRate = "";

        boolean read = true;
        boolean isChecked = false;
        int i = 0;

        while (true) {
            try{
                moveToElement(By.cssSelector("#m2soft-crownix-text > div:nth-child("+(i+10)+")"));
            } catch (NoSuchElementException e){
                break;
            }

            // 3개월이 처음으로 나올 때까지 div를 continue
            if (!elements.get(i).getText().equals("3개월") && isChecked == false ) {
                read = false;
            } else {
                read = true;
                isChecked = true;
            }

            if(!read){
                i++;
                continue;
            }

            logger.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

            term 			= elements.get(i).getText();
            logger.info("경과기간 :: " + term);
            premiumSum 		= elements.get(i + 2).getText();
            logger.info("납입 보험료 :: " + premiumSum);

            returnMoney     = elements.get(i + 3).getText();
            logger.info("해약환급금 :: " + returnMoney);
            returnRate      = elements.get(i + 4).getText();
            logger.info("환급률 :: " + returnRate);

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();

            planReturnMoney.setPlanId(Integer.parseInt(info.planId));
            planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
            planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            i = i + 5;
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);

        info.returnPremium = returnMoney;
        logger.info("만기환급급 :: " + info.returnPremium);
    }
}
