package com.welgram.crawler.direct.life.ail;

import com.welgram.common.enums.Job;
import com.welgram.crawler.direct.life.ail.CrawlingAIL.CrawlingAILAnnounce;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;



// 2023.08.04           | 최우진               | 대면_종신보험
// AIL_WLF_F005         | 무배당 AIA 안심+ 프라임 종신보험 (해약환급금 50%지급형) 1형(간편심사형)
public class AIL_WLF_F005 extends CrawlingAILAnnounce {

    public static void main(String[] args) { executeCommand(new AIL_WLF_F005(), args); }



    private final Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        innerScrap(info);

        return true;
    }



    public void innerScrap(CrawlingProduct info) throws Exception {

        logger.info("START :: AIL_WLF_F005 :: 무배당 AIA 안심+ 프라임 종신보험 (해약환급금 50%지급형) 1형(간편심사형)");

        driver.manage().window().maximize();

        // todo | 공통상수 클래스 하나 만들어두자
        int unitGJ = 10000; // 1만, 10k
        String tempJob = Job.MANAGER.getCodeValue();
        String drivingType = "승용차(자가용)";
        String[] arrTextType = info.getTextType().split("#");

        initAIL(info, arrTextType);

        setUserName("custNm");
        setBirthday("brthDt", info.getFullBirth());
        setVehicle("drvgCd", drivingType);
        setSmokeOption("grp-rdo3");
        setJob(tempJob);

        setInsTerm(driver.findElement(By.xpath("//*[@id='polprd_T40101_01A']")), info.getInsTerm());
        setNapTerm(driver.findElement(By.id("payprd_T40101_01A")), info.getNapTerm(), info.getInsTerm());
        setNapCycle(driver.findElement(By.xpath("//*[@name='payCyclCd']")), info.getNapCycleName());
        setAssureMoney(driver.findElement(By.xpath("//*[@name='sfaceAmt_T40101_01A']")), info.getAssureMoney(), unitGJ);
        
        // 보험료 계산하기
        pushButton(By.xpath("/html/body/form[1]/div/div[1]/div[3]/button"), 2);
        // 상품제안서 보기
        pushButton(By.xpath("/html/body/form[1]/div/div[2]/div/div[2]/button"), 2);
        crawlPremium(driver.findElement(By.xpath("//*[@id='layer1']/div[3]/table/tfoot/tr/td/strong")), info);
        takeScreenShot(info);
        // 해약환급금 보기
        pushButton(By.xpath("//*[@id='tabLISTBox']/div[1]/ul/li[3]/a"), 2);
        getWebReturnPremium(info);

        // ======================================================================

        logger.info(":: INNERSCRAP DONE ::");
    }



    // waitForWindow method
    public String waitForWindow(int timeout) {

        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<String> whNow = driver.getWindowHandles();
        Set<String> whThen = (Set<String>) vars.get("window_handles");
        if (whNow.size() > whThen.size()) {
            whNow.removeAll(whThen);
        }

        return whNow.iterator().next();
    }



    protected void getWebReturnPremium(CrawlingProduct info) throws Exception {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trElements = driver.findElements(By.cssSelector("#layer3 > div > div > table > tbody > tr"));
        for (WebElement tr : trElements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("td")).get(0).getAttribute("innerText");
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getAttribute("innerText");
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getAttribute("innerText");
            String returnRate = tr.findElements(By.tagName("td")).get(3).getAttribute("innerText");

            logger.info("=============================");
            logger.info(term + "  :: " + premiumSum);
            logger.info("해약환급금 :: " + returnMoney);
            logger.info("환급률    :: " + returnRate);

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
        logger.info("=============================");
        logger.error("더이상 참조할 차트가 없습니다");
        logger.info("종신보험의 경우 만기환급금이 존재하지 않습니다 (사망보험금 or 해약환급금)");
    }

}
