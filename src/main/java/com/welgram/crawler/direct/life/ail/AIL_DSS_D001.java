package com.welgram.crawler.direct.life.ail;


import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;



public class AIL_DSS_D001 extends CrawlingAIL {

    // AIL생명 - 무배당 AIA Vitality 베스트핏 보장보험(디지털전용)
    public static void main(String[] args) { executeCommand(new AIL_DSS_D001(), args); }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setImageLoad(true);
    }



    private final Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        disclosureRoomCrawling(info);

        return true;
    }



    // 공시실
    private void disclosureRoomCrawling(CrawlingProduct info) throws Exception {

        logger.info("=============================");
        logger.info("공시실 크롤링 시작!");
        logger.info("=============================");

        // 임시 하드 코딩
        logger.info("AIA Vitality 다이렉트 클릭!");
        helper.click(By.cssSelector("#product_kind"));
        helper.click(By.cssSelector("#product_kind > option:nth-child(7)"));
        // 확인 클릭!
        helper.click(By.cssSelector("body > form:nth-child(9) > div > div > ul > li:nth-child(1) > button"));

        logger.info("보험상품 찾기!");
        selectItem(info);
        // 확인 클릭!
        helper.click(By.cssSelector("body > form:nth-child(9) > div > div > ul > li:nth-child(2) > button"));
        WaitUtil.loading();

        // 새로운 팝업창 핸들러
        String lastWindow = null;
        Set<String> handles = driver.getWindowHandles();
        for (String aux : handles) {
            lastWindow = aux;
        }
        logger.info("새로운 pop창");
        driver.switchTo().window(lastWindow);

        driver.switchTo().frame(1); // frame 전환

        logger.info("이름");
        setname();

        logger.info("생년월일");
        setBirth(By.cssSelector("#brthDt"),info);

        logger.info("성별");
        setGenderElder(info.getGender());

        logger.info("운전여부"); // 승용차(자가용)
        helper.click(By.cssSelector("#drvgCd"));
        helper.click(By.cssSelector("#drvgCd > option:nth-child(2)"));

        vars.put("window_handles", driver.getWindowHandles());
        driver.findElement(By.linkText("직업검색")).click();

        vars.put("win2149", waitForWindow(2000));
        vars.put("root", driver.getWindowHandle());
        driver.switchTo().window(vars.get("win2149").toString());
        driver.switchTo().frame(0); // frame 전환
        WaitUtil.loading();

        driver.findElement(By.id("job_name")).sendKeys("영업");
        driver.findElement(By.cssSelector("body > div > div > div > div.modal-container > div.pop-job-form.clearfix > form:nth-child(1) > div > button")).click();
        WaitUtil.loading();
        driver.findElement(By.linkText("영업 및 판매업체 관련 사무직 관리자")).click();

        driver.switchTo().window(vars.get("root").toString());
        driver.switchTo().frame(1);

        logger.info("보험기간");
        setInsTermOV(info.insTerm);

        logger.info("납입기간");
        setNapTerm(info.napTerm);

        logger.info("보험가입금액");
        setassureMoney(info);

        /* 특약선택 */
        for (CrawlingTreaty item : info.treatyList) {
            setTreaty(info, item);
        }

        logger.info("보험료 계산하기 클릭!");
        driver.findElement(By.cssSelector("body > form:nth-child(1) > div > div.container > div.margin-top-xxl.margin-bottom-4xl.btn-btm > button")).click();
        WaitUtil.loading(2);

        logger.info("스크롤 내리기!");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById(\"premium_tot\").scrollIntoView(true);");
        WaitUtil.loading(2);

        logger.info("스크린샷!");
        takeScreenShot(info);

        logger.info("보험료 가져오기!");
        getpremium(info);

        logger.info("상품제안서 클릭!");
        driver.findElement(By.cssSelector("body > form:nth-child(1) > div > div.bg1 > div > div.margin-top-l.margin-bottom-4xl.btn-btm > button")).click();
        WaitUtil.loading(2);

        logger.info("해약환급금 클릭!");
        driver.findElement(By.cssSelector("#tabLISTBox > div:nth-child(1) > ul > li:nth-child(3) > a")).click();
        ReturnPremium(info);
    }



    /* waitForWindow method */
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



    /* 공시실 보험료 */
    protected void getpremium(CrawlingProduct info) throws InterruptedException {

        String premium;

        element = driver.findElement(By.cssSelector("#premium_tot"));
        WaitUtil.loading();

        premium = element.getText().replace("원", "").replace(",","").replace(" ","");
        logger.info("premium : " + premium);
        WaitUtil.loading();

        logger.info("월 보험료: " + premium + "원");
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
    }



    /* 공시실 해약환급금 ( 바이탈리티 미적용시 ) */
    protected void ReturnPremium(CrawlingProduct info) {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();

        List<WebElement> trElements = driver.findElements(By.cssSelector("#layer3 > div > div > table > tbody > tr"));
        for (WebElement tr : trElements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("td")).get(0).getAttribute("innerText");
            String premiumSum = tr.findElements(By.tagName("td")).get(3).getAttribute("innerText");
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getAttribute("innerText");
            String returnRate = tr.findElements(By.tagName("td")).get(4).getAttribute("innerText");
            logger.info(term + " :: " + premiumSum);

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);
            planReturnMoneyList.add(planReturnMoney);

            // 기본 해약환급금 세팅
            info.returnPremium = returnMoney.replace(",", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
    }
}



