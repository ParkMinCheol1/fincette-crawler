package com.welgram.crawler.direct.life.ail;


import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



// 2023.01.27 | 최우진 | 무배당 첫날부터 입원비보험 (갱신형)
public class AIL_DSS_D003 extends CrawlingAIL {

    public static void main(String[] args) { executeCommand(new AIL_DSS_D003(), args); }



    private Map<String, Object> vars = new HashMap<>();



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception { option.setMobile(true); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: AIL_DSS_D003 :: 무배당 첫날부터 입원비보험 (갱신형)");
        WaitUtil.waitFor(2);

        logger.info("보험료 계산 버튼 클릭");
        element = driver.findElement(By.id("btnCalc"));
        waitElementToBeClickable(element).click();

        WaitUtil.waitFor(3);
        logger.info("주민등록번호 입력");

        int year = Integer.parseInt(info.fullBirth.substring(0, 4));
        String startGenderValue = (info.gender == MALE) ? "1" : "2";
        if (year >= 2000) {
            startGenderValue = (info.gender == MALE) ? "3" : "4";
        }
        driver.findElement(By.id("inputRegistNum1")).sendKeys(info.getBirth());
        driver.findElement(By.id("inputRegistNum2")).sendKeys(startGenderValue);

        logger.info("보험료 설계 버튼 클릭");
        element = driver.findElement(By.id("btnCalc02"));
        waitElementToBeClickable(element).click();
        waitMobileLoadingBar();
        WaitUtil.waitFor(3);

        logger.info("상세 설계하기 버튼 클릭");
        element = driver.findElement(By.id("btnCalc"));
        waitElementToBeClickable(element).click();
        waitMobileLoadingBar();
        WaitUtil.waitFor(3);

        String monthlyPremium = driver.findElement(By.id("topDcbfTotPrm")).getText().replaceAll("[^0-9]", "");
        info.getTreatyList().get(0).monthlyPremium = monthlyPremium;
        logger.info("monthlyPremium :: {}", monthlyPremium);

        WebElement el = driver.findElement(By.xpath("//button[text()='해약환급금 보기']"));
        moveToElementByJavascriptExecutor(el);
        el.click();

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trList = driver.findElements(By.xpath("//tbody[@id='dsGrdSurrefnDtl']/tr"));
        for (WebElement tr : trList) {
            String term = tr.findElement(By.xpath("./th[1]")).getText();
            String premiumSum = tr.findElement(By.xpath("./td[1]")).getText().replaceAll("[^0-9]", "");
            String returnMoney = tr.findElement(By.xpath("./td[2]")).getText().replaceAll("[^0-9]", "");
            String returnRate = tr.findElement(By.xpath("./td[3]")).getText();

            info.returnPremium = returnMoney;

            logger.info("========= 해약환급금 ========");
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

        return true;
    }
}
