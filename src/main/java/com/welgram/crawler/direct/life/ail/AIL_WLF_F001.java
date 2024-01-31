package com.welgram.crawler.direct.life.ail;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingProduct;
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



// 2022.07.12           | 최우진               | 대면_종신보험
// AIL_WLF_F001         | (무)AIA Vitality 꼭 필요한 종신보험 1형 간편심사형
public class AIL_WLF_F001 extends CrawlingAIL {

    public static void main(String[] args) { executeCommand(new AIL_WLF_F001(), args); }



    private final Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        innerScrap(info);

        return true;
    }



    public void innerScrap(CrawlingProduct info) throws Exception {

        logger.info("START :: AIL_WLF_F001 :: (무)AIA Vitality 꼭 필요한 종신보험");

        driver.manage().window().maximize();

        logger.info(":: textType ::");
        String[] arrTType = info.getTextType().split("#");
        for(int i = 0; i < arrTType.length; i++) {
            arrTType[i] = arrTType[i].trim();
            logger.info("[" + i + "] :: " + arrTType[i]);
            // [0] : (무)AIA Vitality 꼭 필요한 종신보험
            // [1] : 경영지원 사무직 관리자
            // [2] :
        }
        WaitUtil.loading(2);

        logger.info("상품분류 선택 :: [ 종신·정기보험 ]");
        Select selectProdcutCode = new Select(driver.findElement(By.xpath("//select[@id='product_kind']")));
        selectProdcutCode.selectByVisibleText("종신·정기보험");
        WaitUtil.loading(2);
        helper.click(By.xpath("//*[@id='product_kind']//ancestor::li//*[@class='btn btn-primary']"));
        WaitUtil.loading(2);

        logger.info("보험상품명 선택 :: [ (무)AIA Vitality 꼭 필요한 종신보험 ]");
        Select selectProductName = new Select(driver.findElement(By.xpath("//select[@id='planNo']")));
        selectProductName.selectByVisibleText(arrTType[0]);
        WaitUtil.loading(2);
        helper.click(By.xpath("//*[@id='planNo']//ancestor::li//*[@class='btn btn-primary']"));
        WaitUtil.loading(2);

        // ======== IFRAME START ====================================================
        // IFRAME은 브라우저안의 또하나의 브라우저라고 취급하면 셀레니움 상에서 어떻게 조작해야 할지 이해하기 편합니다
        // 사용하려는 엘리먼트가 기존페이지의 엘리먼트인지 IFRAME내부의 엘리먼트인지 셀레니움에게 알려주어야 합니다
        // (화면전환 | 사용하려는게 기존페이지인지/iframe내부의 페이지인지 알려주어야 합니다)

        // IFRAME 화면전환
        String lastWindow = null;
        Set<String> handles = driver.getWindowHandles();
        for (String aux : handles) {
            lastWindow = aux;
        }
        logger.info("새로운 pop창");
        driver.switchTo().window(lastWindow);
        driver.switchTo().frame(1); // frame 전환
        WaitUtil.waitFor(2);
        // ======== IFRAME END =======================================================

        String tempName = PersonNameGenerator.generate();
        logger.info("이름 입력 :: [ " + tempName + " ]");
        driver.findElement(By.id("custNm")).sendKeys(tempName);
        WaitUtil.loading(2);

        logger.info("생년월일 입력 :: [ " + info.getFullBirth() + " ]");
        helper.sendKeys3_check(By.xpath("//input[@id='brthDt']"), info.getFullBirth());
        WaitUtil.loading(2);

        logger.info("성별 선택 :: [ {} ] ", info.getGender() == MALE ? "남" : "여");
        // todo | 기본 '남성'
        if (MALE == info.getGender()) {
            helper.click(By.xpath("//label[@for='grp-rdo1']"));
            logger.info("'남' 선택완료");
        } else {
            helper.click(By.xpath("//label[@for='grp-rdo2']"));
            logger.info("'여' 선택완료");
        }
        WaitUtil.loading(2);

        logger.info("운전여부 선택 :: []");
        helper.click(By.cssSelector("#drvgCd"));
        helper.click(By.cssSelector("#drvgCd > option:nth-child(2)"));
        WaitUtil.loading(2);

        logger.info("흡연여부 선택 :: []");
        // todo | 흡연여부 기본 : '일반'

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

        // ======================================================================

        logger.info("보험기간 설정 :: [" + info.getInsTerm() + "]");       // 종신
        elements = driver.findElements(By.cssSelector("#polprd_A30100_32C > option"));
        for (WebElement each : elements) {
            if (each.getText().contains("종신")) {
                each.click();
            } else {
                throw new CommonCrawlerException("잘못된 보험기간 설정입니다");
            }
        }
        WaitUtil.loading(2);

        logger.info("납입기간 설정 :: [" + info.getNapTerm() + "]");
        String strNapTerm = info.getNapTerm() + "납";
//        driver.findElement(By.xpath("//select[@id='payprd_T101V0_02B']/option[text()='" + strNapTerm + "']")).click();

        Select selNapTerm = new Select(driver.findElement(By.id("payprd_T101V0_03C")));
        selNapTerm.selectByVisibleText(strNapTerm);

        // todo | 전기납에 대한 처리
        WaitUtil.loading(2);
        
        logger.info("납입주기 설정 :: [ 월납 ]");
        // todo | 납입주기 default : 월납

        logger.info("보험가입금액 입력 :: [" + info.getAssureMoney() + "]");
        String custBhRyo = String.valueOf(Integer.parseInt(info.getAssureMoney()) / 10000);
        helper.sendKeys2_check(By.cssSelector("body > form:nth-child(1) > div > div.container > div:nth-child(6) > div > table > tbody > tr:nth-child(1) > td:nth-child(12) > input.input"), custBhRyo);

        logger.info("보험료 계산하기 선택 ::: ");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        WaitUtil.loading(2);

        // ======================================================================

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
            String premiumSum = tr.findElements(By.tagName("td")).get(3).getAttribute("innerText");
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getAttribute("innerText");
            String returnRate = tr.findElements(By.tagName("td")).get(4).getAttribute("innerText");

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
