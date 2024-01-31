package com.welgram.crawler.direct.life.ail;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;



// 2022.09.23           | 최우진               | 다이렉트_질병보험
// AIL_ACD_D001         | (무)퍼플 휴일교통재해장해보험
public class AIL_ACD_D001_Old extends CrawlingAIL {

    // AIL생명 - (무)퍼플 휴일교통재해장해보험
    public static void main(String[] args) { executeCommand(new AIL_ACD_D001_Old(), args); }



    private final Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

//        Map<String, Object> vars = new HashMap<>();

        logger.info("=============================");
        logger.info("공시실 크롤링 시작!");
        logger.info("=============================");

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
//        setBirth(By.cssSelector("#brthDt"),info);
        driver.findElement(By.cssSelector("#brthDt")).sendKeys(info.getFullBirth());

        logger.info("성별");
        setGenderElder(info.getGender());

        logger.info("운전여부"); // 승용차(자가용)
        helper.click(By.cssSelector("#drvgCd"));
        helper.click(By.cssSelector("#drvgCd > option:nth-child(2)"));
        WaitUtil.loading(2);

        vars.put("window_handles", driver.getWindowHandles());
        if (helper.isAlertShowed()) {
            Alert alert = driver.switchTo().alert();
            String text = alert.getText();

            if (text.contains("직업을")) {
                logger.info(text);
                alert.accept();
            }
        }
        driver.findElement(By.linkText("직업검색")).click();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Set<String> whNow = driver.getWindowHandles();
        Set<String> whThen = (Set<String>) vars.get("window_handles");
        if (whNow.size() > whThen.size()) {
            whNow.removeAll(whThen);
        }

        vars.put("win2149", whNow.iterator().next());
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

        logger.info("보험료 계산하기 클릭!");
        WaitUtil.loading(2);
        driver.findElement(By.cssSelector(".btn-primary")).click();
        WaitUtil.loading(2);

        logger.info("스크린샷!");
        takeScreenShot(info);

        logger.info("보험료 가져오기");
        getpremium(info);

        logger.info("상품제안서 클릭!");
        driver.findElement(By.cssSelector("body > form:nth-child(1) > div > div.bg1 > div > div.margin-top-l.margin-bottom-4xl.btn-btm > button")).click();
        WaitUtil.loading(2);

        logger.info("해약환급금 클릭!");
        driver.findElement(By.cssSelector("#tabLISTBox > div:nth-child(1) > ul > li:nth-child(3) > a")).click();
        WebgetReturnPremium(info);

        return true;
    }



    @Override
    protected void configCrawlingOption(CrawlingOption option) throws Exception {

        option.setImageLoad(true);
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
}


