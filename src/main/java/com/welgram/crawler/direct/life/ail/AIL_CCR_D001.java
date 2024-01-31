package com.welgram.crawler.direct.life.ail;

import com.welgram.common.WaitUtil;
import com.welgram.common.except.InsTermMismatchException;
import com.welgram.common.except.NapTermMismatchException;
import com.welgram.common.except.NotFoundPlanTypeException;
import com.welgram.common.except.PlanTypeMismatchException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILMobile;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;



// 2022.09.23 | 최우진 | (무)든든튼튼 암보험
// 이름 헷갈리는 특약있음 - ~진단금이지만 빼야하는 특약이 있음
public class AIL_CCR_D001 extends CrawlingAILMobile {

    public static void main(String[] args) {
        executeCommand( new AIL_CCR_D001(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        // INFORMATION
        String[] arrTextType = info.getTextType().split("#");

        //홈페이지 접속시 바로 로딩바 뜸. 로딩바 사라질때까지 대기
        waitMobileLoadingBar();

        // PROCESS
//        initAIL(info, arrTextType);
//        logger.info("1111");

        // 보험료 계산
        pushButton("//*[@id='btnCalc']", 5);
        logger.info("2222");

        setGender(info);
        logger.info("3333");

        setBirthday("//*[@id='inputRegistNum1']", "//*[@id='inputRegistNum2']", info);
        logger.info("4444 :: //*[@id='inputRegistNum1']");

        // 보험료 설계
        pushButton("//*[@id='btnCalc02']", 5);
        waitMobileLoadingBar();
        logger.info("55555 :: //*[@id='btnCalc02']");

        // 상세 설계하기
        pushButton("//*[@id='btnCalc']", 5);
        waitMobileLoadingBar();
        logger.info("66666 :: //*[@id='btnCalc']");

        // 플랜선택
        logger.info("START :: {}", info.planSubName);
        logger.info("=====================================");
        setMobilePlanType(info.planSubName);

        List<CrawlingTreaty> treatyList = info.getTreatyList();
        List<WebElement> liList = driver.findElements(By.xpath("//ul[@id='table_area']/li"));

        logger.info("TRTLIST :: {}", treatyList.size());
        logger.info("LILIST  :: {}", liList.size());

        // 특약 설정
        setMobileTreaties(liList, info);

        // 보험료
        crawlPremium(driver.findElement(By.id("topDcbfTotPrm")), info);

        // 해약환급금
        moveToElementByJavascriptExecutor(By.xpath("//*[@class='u-pt--0 u-pb--48']"));
        pushButton("//button[contains(.,'해약환급금 보기')]", 2);
        getHomepageReturnPremiums(info);

        return true;
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {

        WebElement elment = (WebElement) obj[0];
        CrawlingProduct info = (CrawlingProduct) obj[1];

        try {
            info.getTreatyList().get(0).monthlyPremium =
                elment
                    .getText()
                    .replaceAll("[^0-9]", "");
            logger.info("보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);
            WaitUtil.waitFor(4);

        } catch(Exception e) {
            throw new PremiumCrawlerException("보험료 크롤링중 에러가 발생\n" + e.getMessage());
        }
    }



    private String convertMoney(int money) throws Exception {
        String moneyText = String.valueOf(money);
        if (moneyText.contains("0000000")) {
            moneyText = moneyText.replaceAll("0", "") + "천만원";
        } else if (moneyText.contains("00000000")) {
            moneyText = moneyText.replaceAll("0", "") + "억원";
        }
        return moneyText;
    }



    //플랜 유형 선택
    private void setMobilePlanType(String myPlanType) throws Exception {
        myPlanType = myPlanType.trim();

        try {
            //플랜유형 버튼 찾기
            WebElement button = driver.findElement(By.xpath("//div[@id='plan_button_area']/button[contains(., '" + myPlanType + "')]"));

            //버튼 클릭 후에 로딩바 대기
            waitElementToBeClickable(button).click();
            waitMobileLoadingBar();

            //실제 홈페이지에서 클릭된 플랜유형이 잘 클릭되었는지 확인
            //홈페이지의 클릭된 플랜유형 버튼을 찾는다.
            button = driver.findElement(By.xpath("//div[@id='plan_button_area']/button[@class[contains(., 'is-active')]]"));

            //홈페이지에서 클릭된 플랜유형 버튼의 이름
            String targetPlanType = button.getText().trim();

            logger.info("=======================================================");
            logger.info("가입설계 플랜유형 : {}", myPlanType);
            logger.info("홈페이지 플랜유형 : {}", targetPlanType);
            logger.info("=======================================================");

            if(targetPlanType.contains(myPlanType)) {
                logger.info("=======================================================");
                logger.info("가입설계 플랜유형({}) == 홈페이지 플랜유형({})", myPlanType, targetPlanType);
                logger.info("=======================================================");

            } else {
                logger.info("=======================================================");
                logger.info("가입설계 플랜유형({}) ≠ 홈페이지 플랜유형({})", myPlanType, targetPlanType);
                logger.info("=======================================================");

                throw new PlanTypeMismatchException("플랜유형 불일치");
            }

        } catch(NoSuchElementException e) {
            String errorMsg = "플랜유형(" + myPlanType + ")이 존재하지 않습니다.";
            logger.info(errorMsg);

            throw new NotFoundPlanTypeException(errorMsg);
        }
    }


    private void setMobileTreaties(List<WebElement> liList, CrawlingProduct info) throws Exception {

        for (WebElement li : liList) {
            WebElement el = li.findElement(By.xpath(".//input[@type='checkbox']"));
            boolean isChecked = li.findElement(By.xpath(".//input")).isSelected();
            boolean isUsable = li.findElement(By.xpath(".//input")).isEnabled();
            boolean isVisible = li.findElement(By.xpath(".//input")).isDisplayed();


            if (isVisible && isChecked) {
                String liName = li.findElement(By.xpath(".//strong")).getText().trim();
                logger.info("LI NAME :: {}", liName);
                // 일단 체크해제
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);

                // 체크 해제 팝업창
                if (helper.existElement(By.xpath("//*[@class='c-modal__dialog c-modal__dialog--alert']/div"))) {
                    logger.info("체크 해제 안내팝업 발생");
                    // 팝업 닫기
                    pushButton("//*[@class='c-modal__dialog c-modal__dialog--alert']/div//button", 2);

                    try {
                        // 금액 선택 버튼
                        pushButton("//ul[@id='table_area']/li//*[@class='c-btn-select']", 2);

                        String treatyMoney = convertMoney(info.getTreatyList().get(0).getAssureMoney());
                        WebElement $ul = driver.findElement(By.xpath("//div[@class='c-modal c-modal--bs msg-type is-active']/div/div[2]/div[3]/ul"));
                        WebElement $moneyButton = $ul.findElement(By.xpath(".//*[text()='" + treatyMoney + "']"));

                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", $moneyButton);
                        WaitUtil.waitFor(2);

                    } catch (Exception e) {
                        String fixedAssureMoney = li.findElement(By.xpath(".//span[@class='guarantee']")).getText().trim();
                        logger.info("금액 고정 특약 :: {}", fixedAssureMoney);
                    }
                } else {
                    isChecked = li.findElement(By.xpath(".//input")).isSelected();
                    logger.info("특약 체크여부 :: {}", isChecked);
                }
            }
        }

        // 다시 계산하기
        if (helper.existElement(By.xpath("//*[@class='ly-content__foot sticky']//button[contains(.,'다시 계산하기')]"))) {
            pushButton("//*[@id='btnCalc']", 4);
        }

//    for (CrawlingTreaty treaty : treatyList) {
//      for (WebElement li : liList) {
//        String liName = li.findElement(By.xpath(".//strong")).getText().trim();
//        WebElement el = li.findElement(By.xpath(".//input[@type='checkbox']"));
//        boolean isChecked = li.findElement(By.xpath(".//input")).isSelected();
//        boolean isUsable = li.findElement(By.xpath(".//input")).isEnabled();
//        boolean isVisible = li.findElement(By.xpath(".//input")).isDisplayed();
//
//        if (isVisible && isChecked) {
//          el.click();
//
//          if (treaty.productGubun == 선택특약
//              && liName.equals(treaty.getTreatyName())) {
//            logger.info("정상케이스");
//            logger.info("LI     NAME :: {}", liName);
//            logger.info("TREATY NAME :: {}", treaty.getTreatyName());
//
//            logger.info("isChecked :: {}", isChecked);
//            logger.info("isUsable  :: {}", isUsable);
//            logger.info("isVisible :: {}", isVisible);
//
//            logger.info("=====================================");
//            el.click();
//
//          }
//        }
//      }
//    }
    }



    //보험기간 설정
    private void setMobileInsTerm(String myInsTerm) throws Exception {

        String targetInsTerm = driver.findElement(By.id("poltermtext")).getText();

        logger.info("=======================================================");
        logger.info("가입설계 보험기간 : {}", myInsTerm);
        logger.info("홈페이지 설정된 보험기간 : {}", targetInsTerm);
        logger.info("=======================================================");

        if (targetInsTerm.contains(myInsTerm)) {
            logger.info("=======================================================");
            logger.info("가입설계 보험기간({}) == 홈페이지 보험기간({})", myInsTerm, targetInsTerm);
            logger.info("=======================================================");

        } else {
            logger.info("=======================================================");
            logger.info("가입설계 보험기간({}) ≠ 홈페이지 보험기간({})", myInsTerm, targetInsTerm);
            logger.info("=======================================================");

            throw new InsTermMismatchException("보험기간 불일치");
        }
    }



    //납입기간 설정
    private void setMobileNapTerm(String myNapTerm) throws Exception {

        String targetNapTerm = driver.findElement(By.id("paytermtext")).getText();

        logger.info("=======================================================");
        logger.info("가입설계 납입기간 : {}", myNapTerm);
        logger.info("홈페이지 설정된 납입기간 : {}", targetNapTerm);
        logger.info("=======================================================");

        if (targetNapTerm.contains(myNapTerm)) {
            logger.info("=======================================================");
            logger.info("가입설계 납입기간({}) == 홈페이지 납입기간({})", myNapTerm, targetNapTerm);
            logger.info("=======================================================");

        } else {
            logger.info("=======================================================");
            logger.info("가입설계 납입기간({}) ≠ 홈페이지 납입기간({})", myNapTerm, targetNapTerm);
            logger.info("=======================================================");

            throw new NapTermMismatchException("납입기간 불일치");
        }
    }



    //홈페이지 해약환급금 조회 메서드
    protected void getHomepageReturnPremiums(CrawlingProduct info) throws Exception {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        List<WebElement> trList = driver.findElements(By.xpath("//tbody[@id='dsGrdSurrefnDtl']/tr"));
        for (WebElement tr : trList) {
            String term = tr.findElement(By.xpath("./th[1]")).getText();
            String premiumSum = tr.findElement(By.xpath("./td[1]")).getText().replaceAll("[^0-9]", "");
            String returnMoney = tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
            String returnRate = tr.findElement(By.xpath("./td[3]")).getText();

            info.returnPremium = returnMoney;

            logger.info("=========해약환급금========");
            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("해약환급금 : {}", returnMoney);
            logger.info("해약환급률 : {}", returnRate);
            logger.info("===========================");

            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);
        }

        info.setPlanReturnMoneyList(planReturnMoneyList);

        logger.info("만기환급금 : {}원", info.returnPremium);
    }
}
