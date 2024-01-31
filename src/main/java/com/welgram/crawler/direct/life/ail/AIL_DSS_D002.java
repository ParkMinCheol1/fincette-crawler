package com.welgram.crawler.direct.life.ail;


import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty.ProductKind;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;



// 2022.08.22       | 최우진           | 다이렉트_미니질병
// AIL_DSS_D002     | (무)용종 뚝딱 미니보험
public class AIL_DSS_D002 extends CrawlingAIL {

    public static void main(String[] args) { executeCommand(new AIL_DSS_D002(), args); }



    private Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: AIL_DSS_D002 :: (무)용종 뚝딱 미니보험");
        logger.info("홈페이지에서 제공하는 데이터의 한계로 공시실에서 크롤링을 진행합니다");
        WaitUtil.waitFor(2);

        // ===== FIELDS ==============================================================
        logger.info("상품분류 선택 :: [ AIA Vitality 다이렉트 ]");
        Select selectProdcutCode = new Select(driver.findElement(By.xpath("//select[@id='product_kind']")));
        selectProdcutCode.selectByVisibleText("AIA Vitality 다이렉트");
        helper.click(By.xpath("//*[@id='product_kind']//ancestor::li//*[@class='btn btn-primary']"));
        WaitUtil.waitFor(2 );

        logger.info("보험상품명 선택 :: [ (무)용종 뚝딱 미니보험 ]");
        String title = "(무)용종 뚝딱 미니보험";
        Select selectProductName = new Select(driver.findElement(By.xpath("//select[@id='planNo']")));
        selectProductName.selectByVisibleText(title);
        helper.click(By.xpath("//*[@id='planNo']//ancestor::li//*[@class='btn btn-primary']"));
        WaitUtil.waitFor(4);

        // IFRAME 화면전환
        String lastWindow = null;
        Set<String> handles = driver.getWindowHandles();
        for (String aux : handles) {
            lastWindow = aux;
        }
        logger.info("새로운 pop창");
        driver.switchTo().window(lastWindow);
        driver.switchTo().frame(1); // frame 전환

        String tempName = PersonNameGenerator.generate();
        logger.info("이름 입력 :: [ " + tempName + " ]");
        driver.findElement(By.id("custNm")).sendKeys(tempName);
        WaitUtil.waitFor(2);

        logger.info("생년월일 입력 :: [ " + info.getFullBirth() + " ]");
        helper.sendKeys3_check(By.xpath("//input[@id='brthDt']"), info.getFullBirth());
        WaitUtil.waitFor(2);

        logger.info("성별 선택 :: [ {} ] ", info.getGender() == MALE ? "남" : "여");
        // todo | 기본 '남성'
        if(MALE == info.getGender()) {
            helper.click(By.xpath("//label[@for='grp-rdo1']"));
            logger.info("'남' 선택완료");
        } else {
            helper.click(By.xpath("//label[@for='grp-rdo2']"));
            logger.info("'여' 선택완료");
        }
        WaitUtil.waitFor(2);

        logger.info("운전여부 선택 :: []");
        helper.click(By.cssSelector("#drvgCd"));
        helper.click(By.cssSelector("#drvgCd > option:nth-child(2)"));
        WaitUtil.loading(2);

        logger.info("흡연여부 선택 :: []");
        // todo | 흡연여부 기본 : '일반' 현재 미처리되어있는 상태

        String jobOpt = "경영지원 사무직 관리자";
        logger.info("직업 입력 :: [ " + jobOpt + " ]");
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
        WaitUtil.loading(2);

        driver.findElement(By.id("job_name")).sendKeys(jobOpt);
        driver.findElement(By.cssSelector("body > div > div > div > div.modal-container > div.pop-job-form.clearfix > form:nth-child(1) > div > button")).click();
        WaitUtil.loading(2);
        driver.findElement(By.linkText(jobOpt)).click();

        driver.switchTo().window(vars.get("root").toString());
        driver.switchTo().frame(1);

        logger.info("보험료 계산하기 선택");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        WaitUtil.loading(2);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("보험료 확인");
        getpremium(info);
        WaitUtil.loading(4);

        logger.info("상품제안서 선택 ::: ");
        driver.findElement(By.cssSelector("body > form:nth-child(1) > div > div.bg1 > div > div.margin-top-l.margin-bottom-4xl.btn-btm > button")).click();
        WaitUtil.loading(4);

        logger.info("해약환급금 선택 ::: ");
        driver.findElement(By.cssSelector("#tabLISTBox > div:nth-child(1) > ul > li:nth-child(3) > a")).click();
        getWebReturnPremium(info);

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



    protected void getWebReturnPremium(CrawlingProduct info) throws Exception {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trElements = driver.findElements(By.cssSelector("#layer3 > div > div > table > tbody > tr"));
        for (WebElement tr : trElements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("td")).get(0).getText();
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getText().replaceAll("[^0-9]", "").trim();
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getText().replaceAll("[^0-9]", "").trim();
            String returnRate = tr.findElements(By.tagName("td")).get(3).getText();

            logger.info("=============================");
            logger.info(term + " :: " + premiumSum);
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
        logger.info("=============================");

        if(info.treatyList.get(0).productKind.equals(ProductKind.순수보장형)) {
            logger.info("보험형태 : {} 상품이므로 만기환급금을 0원으로 설정합니다", info.treatyList.get(0).productKind);
            info.returnPremium = "0";
        }
        logger.info("만기환급금 : {}원", info.returnPremium);

    }
}
