package com.welgram.crawler.direct.life.ail;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



// 2023.05.18 | 최우진 | 무배당 AIA 치과비 걱정 없는 치아보험 (갱신형)
public class AIL_DTL_D001 extends CrawlingAIL {

    public static void main(String[] args) { executeCommand(new AIL_DTL_D001(), args); }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception { option.setMobile(true); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: 무배당 AIA 치과비 걱정 없는 치아보험 (갱신형)");

        logger.info("보험료 계산 버튼 클릭");
        element = driver.findElement(By.id("btnCalc"));
        WaitUtil.waitFor(3);
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(3);

        logger.info("주민등록번호 입력");
        int year = Integer.parseInt(info.fullBirth.substring(0, 4));
        String startGenderValue = (info.gender == MALE) ? "1" : "2";
        if(year >= 2000) {
            startGenderValue = (info.gender == MALE) ? "3" : "4";
        }
        driver.findElement(By.id("inputRegistNum1")).sendKeys(info.getBirth());
        driver.findElement(By.id("inputRegistNum2")).sendKeys(startGenderValue);

        logger.info("[보험료 설계] 버튼 클릭");
        element = driver.findElement(By.id("btnCalc02"));
        waitElementToBeClickable(element).click();
        waitMobileLoadingBar();
        WaitUtil.waitFor(3);

        logger.info("[상세 설계하기] 버튼 클릭");
        element = driver.findElement(By.id("btnCalc"));
        waitElementToBeClickable(element).click();
        waitMobileLoadingBar();
        WaitUtil.waitFor(3);

        logger.info("[고급] 탭 선택");
        element = driver.findElement(By.id("D0A0I0UM001001_CS30M"));
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

        // todo | 변경 필수!!
        if (info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
            logger.info(" {} ==  만기환급금 0원 세팅", info.treatyList.get(0).productKind);
            info.returnPremium = "0";
        }

        return true;
    }
}
