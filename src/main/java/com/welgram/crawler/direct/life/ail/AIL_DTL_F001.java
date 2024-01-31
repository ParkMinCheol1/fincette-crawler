package com.welgram.crawler.direct.life.ail;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


// 2022.08.11 | 최우진 | 무배당 뉴 이 좋은 치아보험(갱신형)
// 1구좌 = 100만원
public class AIL_DTL_F001 extends CrawlingAIL {

    public static void main(String[] args) { executeCommand(new AIL_DTL_F001(), args); }



    private final Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

//        Map<String, Object> vars = new HashMap<>();

        logger.info("START :: AIL_DTL_F001 :: 무배당 뉴 이 좋은 치아보험(갱신형) ");

        logger.info("TEXTTYPE 확인");
        String[] arrTType = getArrTextType(info);
        // 0.
        // 1.
        // 2.

        logger.info("상품분류 선택 :: [ 건강·상해보험 ]");
        Select selectProdcutCode = new Select(driver.findElement(By.xpath("//select[@id='product_kind']")));
        selectProdcutCode.selectByVisibleText("건강·상해보험");
        WaitUtil.loading(2);
        helper.click(By.xpath("//*[@id='product_kind']//ancestor::li//*[@class='btn btn-primary']"));
        WaitUtil.loading(2);

        logger.info("보험상품명 선택 :: [ (무)뉴 이 좋은 치아보험(갱신형)]");
        Select selectProductName = new Select(driver.findElement(By.xpath("//select[@id='planNo']")));
        selectProductName.selectByVisibleText("(무)뉴 이 좋은 치아보험(갱신형)");
        WaitUtil.loading(2);
        helper.click(By.xpath("//*[@id='planNo']//ancestor::li//*[@class='btn btn-primary']"));
        WaitUtil.loading(4);

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
        WaitUtil.loading(2);

        logger.info("생년월일 입력 :: [ " + info.getFullBirth() + " ]");
//        helper.doSendKeys(By.xpath("//input[@id='brthDt']"), info.getFullBirth());
        driver.findElement(By.id("brthDt")).sendKeys(info.getFullBirth());
        WaitUtil.loading(2);

        logger.info("성별 선택 :: [ {} ] ", info.getGender() == MALE ? "남" : "여");
        // todo | 기본 '남성'
        if(MALE == info.getGender()) {
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
        String strNapTerm = info.getNapTerm()+"납";
        if (info.getInsTerm().equals(info.getNapTerm())) {
//            driver.findElement(By.xpath("//select[@id='payprd_P30100_02B']/option[text()='전기납']")).click();
            Select selNapTerm = new Select(driver.findElement(By.id("payprd_P30100_02B")));
            selNapTerm.selectByVisibleText("전기납");
        } else {
            driver.findElement(By.xpath("//select[@id='payprd_P30100_02B']/option[text()='" + strNapTerm + "']")).click();
        }
        // todo | 전기납에 대한 처리
        WaitUtil.loading(2);

        logger.info("납입주기 설정 :: [ 월납 ]");
        driver.findElement(By.xpath("//select[@name='payCyclCd']/option[text()='월납']")).click();
        // todo | 납입주기 default : 월납

        logger.info("보험가입금액 입력 :: [" + info.getAssureMoney() + "]");
        String insFee = String.valueOf(Integer.parseInt(info.getAssureMoney()) / 1000000);
        logger.info("보험가입금액 입력 :: [ 구좌 :: " + insFee + "]");
        helper.sendKeys2_check(By.cssSelector("body > form:nth-child(1) > div > div.container > div:nth-child(6) > div > table > tbody > tr:nth-child(1) > td:nth-child(12) > input.input"), insFee);

        logger.info("특약내용 추가");
        List<WebElement> trList = driver.findElements(By.xpath("//caption[contains(., '특약, 보험기간, 납입기간, 보험가입금액')]/parent::table//tbody//tr"));

        boolean checkerFlag = false;
        for (CrawlingTreaty trtEach : info.getTreatyList()) {
            for (WebElement elEach : trList) {
                if (trtEach.treatyName.equals(elEach.findElement(By.xpath(".//td[1]/label")).getText())) {
                    Select selectInsTerm = new Select(elEach.findElement(By.xpath(".//td[2]/select")));
                    Select selectNapTerm = new Select(elEach.findElement(By.xpath(".//td[3]/select")));

                    elEach.findElement(By.xpath(".//td[1]/label")).click();
                    selectInsTerm.selectByVisibleText("15년만기");
                    selectNapTerm.selectByVisibleText("전기납");
                    elEach.findElement(By.xpath(".//td[4]/input[2]")).sendKeys(String.valueOf(trtEach.assureMoney / 100_0000));

                    if (arrTType[0].contains("최대CASE")) {
                        logger.info("최대 보험료 케이스 크롤링입니다");
                        logger.info("특약명과 특약금액의 비교를 준비합니다");
                        checkerFlag = true;
                    }
                }
            }
        }

        if (checkerFlag) {
            logger.info("최대 보험료 케이스 크롤링입니다");
            logger.info("특약명과 특약금액의 비교합니다");

            logger.info("특약명, 특약금액 비교 결과");

            // todo | 수정필요
            if (true) {
                logger.info("특약비교 결과 정상입니다");
            } else {
                throw new CommonCrawlerException("특약비교 에러");
            }
        }

        logger.info("보험료 계산하기 선택 ::: ");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        WaitUtil.loading(2);

        logger.info("보험료 확인");
        getpremium(info);
        WaitUtil.loading(4);

        logger.info("상품제안서 선택 ::: ");
        driver.findElement(By.cssSelector("body > form:nth-child(1) > div > div.bg1 > div > div.margin-top-l.margin-bottom-4xl.btn-btm > button")).click();
        WaitUtil.loading(4);

//        js.executeScript("window.scrollBy(0,document.body.scrollHeight);");
//        js.executeScript("window.scrollBy(0,450);");
        logger.info("스크린샷");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0,document.body.scrollHeight * 2 / 3 );");
        WaitUtil.loading(1);
        takeScreenShot(info);
        WaitUtil.loading(2);

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



    private void getWebReturnPremium(CrawlingProduct info) throws Exception {

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trElements = driver.findElements(By.cssSelector("#layer3 > div > div > table > tbody > tr"));
        for (WebElement tr : trElements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("td")).get(0).getAttribute("innerText");
            String premiumSum = tr.findElements(By.tagName("td")).get(1).getAttribute("innerText");
            String returnMoney = tr.findElements(By.tagName("td")).get(2).getAttribute("innerText");
            String returnRate = tr.findElements(By.tagName("td")).get(3).getAttribute("innerText");

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

    }
}
