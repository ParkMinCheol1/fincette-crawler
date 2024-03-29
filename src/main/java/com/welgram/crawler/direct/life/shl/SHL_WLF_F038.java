package com.welgram.crawler.direct.life.shl;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingSHL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;



// 2023.03.17               | 최우진               | 대면_종신보험
// SHL_WLF_F038             | 원더풀라이프종신보험(무배당, 해약환급금 일부지급형)
public class SHL_WLF_F038 extends CrawlingSHL {

    public static void main(String[] args) {
        executeCommand(new SHL_WLF_F038(), args);
    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: 원더풀라이프종신보험(무배당, 해약환급금 일부지급형)");
        logger.info("대면보험의 경우 공시실의 정보를 크롤링합니다. [ SHL_WLF_'F'### ]");

        logger.info("텍스트 타입 확인");
        String[] arrTType = info.getTextType().split("#");
        for(int i = 0; i < arrTType.length; i++) {
            arrTType[i] = arrTType[i].trim();
            logger.info("[ " + i + " ] ::: " + arrTType[i]);
            // 0 : 원더풀라이프종신보험(무배당, 해약환급금 일부지급형)
            // 1 :
            // 2 :
        }
        WaitUtil.loading(2);

        logger.info("검색창에서 상품명 : [ {} ]을 조회합니다", "원더풀라이프종신보험(무배당, 해약환급금 일부지급형)");
        driver.findElement(By.id("meta04")).sendKeys("원더풀라이프종신보험(무배당, 해약환급금 일부지급형)");
        WaitUtil.waitFor(1);

        driver.findElement(By.id("btnSearch")).click();
        WaitUtil.waitFor(3);

        logger.info("보험료계산 버튼 클릭");
        driver.findElement(By.id("calc_0")).click();
        WaitUtil.waitFor(3);

        //  ====  고객정보(피보험자) 정보 입력  ==========================================================
        logger.info("▉▉▉ STEP01 ▉▉▉ [ 고객정보(피보험자) 내용 입력 ]  ");
        logger.info("생년월일");
        driver.findElement(By.xpath("//input[@type='text'][@title='생년월일']")).sendKeys(info.getFullBirth());

        logger.info("성별");
        String genderOpt = (info.getGender() == MALE) ? "filt1_1" : "filt1_2";
        helper.click(By.xpath("//input[@id='" + genderOpt + "']//parent::li"));

        logger.info("운전");
        Select select = new Select(driver.findElement(By.id("vhclKdCd")));
        select.selectByVisibleText("승용차(자가용)");

        logger.info("직업 :: 사무직 - 경영지원 사무직 관리자");
        String jobOpt = "경영지원 사무직 관리자";
        helper.click(By.xpath("//span[text()='검색']//parent::button[@class='btn_t m btnJobPop']"));
        helper.sendKeys3_check(By.id("jobNmPop"), jobOpt);
        helper.click(By.id("btnJobSearch"));
        helper.click(By.xpath("//span[@class='infoCell'][text()='"+jobOpt+"']"));

        logger.info("확인 버튼 클릭");
        helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnCstCfn']"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));

        //  ====  주계약계산 정보 입력 ==========================================================
        logger.info("▉▉▉ STEP02 ▉▉▉ [ 주계약계산 내용 입력 ]  ");
        logger.info("  [ 주계약계산 내용 입력 ]  ");
        logger.info("보험종류 선택 (1/5) ");
        try {
            Select selctInsKind = new Select(driver.findElement(By.xpath("//select[@title='보험종류']")));
            selctInsKind.selectByVisibleText("해약환급금일부지급형");
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            throw new CommonCrawlerException("보험종류를 설정할 수 없습니다\n ");
        }

        logger.info("납입주기 선택(2/5)");
        try{
            Select selctNapTerm = new Select(driver.findElement(By.xpath("//select[@title='납입주기']")));
            selctNapTerm.selectByVisibleText("월납");
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            throw new CommonCrawlerException("(SELECTBOX) [ '월납' ]을 선택할수 없습니다\n");
        }

        logger.info("보험기간 선택(3/5)");
        try{
            Select selectInsPeriod = new Select(driver.findElement(By.xpath("//select[@title='보험기간']")));
            selectInsPeriod.selectByVisibleText("종신");
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            throw new CommonCrawlerException("(SELECTBOX) [ INSTERM : "+info.getInsTerm()+" ]를 선택할수 없습니다\n");
        }

        logger.info("납입기간 선택(4/5)");
        try{
            Select selectPayPeriod = new Select(driver.findElement(By.xpath("//select[@title='납입기간']")));
            selectPayPeriod.selectByVisibleText(info.getNapTerm()+"납");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            throw new CommonCrawlerException("(SELECTBOX) [ NAPTERM : "+info.getNapTerm()+" ]를 선택할수 없습니다\n");
        }

        logger.info("가입금액 선택(5/5)");
        try {
            WebElement inputTextJoinFee = driver.findElement(By.xpath("//input[@title='가입금액']"));
            String joinFeeFormCutLast4 = info.getAssureMoney().substring(0, info.getAssureMoney().length() -4);
            logger.info("가입금액 확인 :: {}", joinFeeFormCutLast4);
            WaitUtil.waitFor(4);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.querySelector('#mnprDivision > ul:nth-child(2) > li:nth-child(3) > div.val > div > input').value = '';");

            inputTextJoinFee.click();
            inputTextJoinFee.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            inputTextJoinFee.sendKeys(joinFeeFormCutLast4);
            WaitUtil.waitFor(2);
        } catch(Exception e) {
            throw new CommonCrawlerException("(INPUT) [ ASSUREMONEY : " + info.getAssureMoney()+" ]를 입력할 수 없습니다\n" + e.getMessage());
        }

        logger.info("확인 버튼 클릭");
        driver.findElement(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnMnpr']")).click();
        WaitUtil.waitFor(1);

        //  ====  특약계산 정보 입력 ==========================================================
        logger.info("▉▉▉ STEP03 ▉▉▉ [ 특약계산 내용 입력 ]");
        logger.info("특약계산 :: 특이사항 없음");

        logger.info("확인 버튼 클릭");
        helper.click(By.xpath("//span[text()='확인']//parent::button[@class='btn_p m btnTrty']"));
        WaitUtil.waitFor(1);

        logger.info("보험료계산 버튼 클릭");
        driver.findElement(By.xpath("//span[text()='보험료계산']//parent::button[@class='btn_p btnInpFeCal']")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading']")));

        // GET ============================================================================
        logger.info("보험료 확인");
        try {
            String monthlyPremium = driver.findElement(By.xpath("//em[@class='rlpaAm']")).getText().replaceAll("[^0-9]", "");
            logger.info("월 보험료 : " + monthlyPremium);
            info.treatyList.get(0).monthlyPremium = monthlyPremium;
            WaitUtil.waitFor(1);
        } catch(Exception e) {
            throw new CommonCrawlerException("보험료 확인중 에러가 발생하였습니다");
        }

        logger.info("스크린샷 찍기");
        try {
            takeScreenShot(info);
            logger.info("찰칵");
        } catch(Exception e) {
            logger.error("스크린 샷을 찍는데 실패하였습니다.");
        }

        logger.info("해약환급금예시 확인");
        helper.click(By.xpath("//span[@class='scriptCell'][text()='해약환급금 예시']//parent::a"));
        WaitUtil.waitFor(6);
        // 경과기간		- 나이 		- 납입모험료 누계 		- 해약환급금 		- 환급률
        // 3개월 		- 30세		- 279,000				- 0					- 0.0
        // 6개월 		- 30세 		- 558,000				- 0 				- 0.0
        // 9개월 		- 30세 		- 837,000				- 0 				- 0.0
        // 1년	 		- 31세 		- 1,116,000				- 0 				- 0.0
        // 2년	 		- 32세 		- 2,232,000				- 196,029			- 8.7
        try {
            List<PlanReturnMoney> pRMList = new ArrayList<>();
            List<WebElement> trReturnMinInfoList = driver.findElements(By.xpath("//table[@id='tblInmRtFxty01']/tbody/tr"));
            for(WebElement trMin : trReturnMinInfoList) {
                String term = trMin.findElement(By.xpath("./td[1]")).getText();
                String premiumSum = trMin.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnMoney = trMin.findElement(By.xpath("./td[4]")).getText().replaceAll("[^0-9]", "");
                String returnRate = trMin.findElement(By.xpath("./td[5]")).getText();

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                pRMList.add(planReturnMoney);

                logger.info("===================================");
                logger.info("기간         : " + term);
                logger.info("납입보험료누계 : " + premiumSum);
                logger.info("해약환급금    : " + returnMoney);
                logger.info("환급률       : " + returnRate);
            }
            info.setPlanReturnMoneyList(pRMList);
            logger.info("===================================");
            logger.error("더이상 참조할 테이블이 존재하지 않습니다.");
            logger.info("종신보험의 경우 만기환급금이 존재하지 않습니다 (사망보험금 or 해약환급금)");
            logger.info("===================================");

        } catch(Exception e) {
            throw new CommonCrawlerException("해약환급금이 존재하지 않습니다.\n" + e.getMessage());
        }

        return true;
    }
}
