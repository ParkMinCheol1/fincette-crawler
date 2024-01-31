package com.welgram.crawler.direct.life.ail;

import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingProduct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;



// 2023.03.28           | 최우진               | 대면_정기 보험
// AIL_TRM_F001         | (무)평준정기보험(일반심사형)
public class AIL_TRM_F001 extends CrawlingAIL {

    public static void main(String[] args) {
        executeCommand(new AIL_TRM_F001(), args);
    }



    private final Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("=============================");
        logger.info("공시실 크롤링 시작!");
        logger.info("=============================");

        // 상품분류 , 보험상품명 클릭!
        logger.info("AIA Vitality 다이렉트 클릭!");
        selectDisclosureRoomProduct(info);

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
        setBirth(By.cssSelector("#brthDt"), info);

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
        setDisclosureRoomInsTerm(info.insTerm);

        logger.info("납입기간");
        if (info.insTerm.equals(info.napTerm)) {
            setDisclosureRoomNapTerm("전기납");
        } else {
            setDisclosureRoomNapTerm(info.napTerm);
        }

        String custBhRyo = String.valueOf(Integer.parseInt(info.assureMoney) / 1_0000);
        logger.info("월보험료 :: " + custBhRyo);
        helper.sendKeys2_check(By.cssSelector("body > form:nth-child(1) > div > div.container > div:nth-child(6) > div > table > tbody > tr:nth-child(1) > td:nth-child(12) > input.input"), custBhRyo);

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


