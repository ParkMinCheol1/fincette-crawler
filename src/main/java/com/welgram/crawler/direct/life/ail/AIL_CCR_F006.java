package com.welgram.crawler.direct.life.ail;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.Job;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.crawler.direct.life.CrawlingAIL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
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



// 2023.04.28           | 최우진               | 대면_암
// AIL_CCR_F006         | 무배당 AIA Vitality 베스트핏 보장보험
public class AIL_CCR_F006 extends CrawlingAIL implements Scrapable {

    public static void main(String[] args) { executeCommand(new AIL_CCR_F006(), args); }



    private final Map<String, Object> vars = new HashMap<>();



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START SCRAP :: 무배당 AIA Vitality 베스트핏 보장보험");
        logger.info("대면보험의 경우 공시실의 정보를 크롤링합니다. [ AIL_$$$_F### ]");

        logger.info("상품분류 선택 :: [ 건강·상해보험 ]");
        Select selectProdcutCode = new Select(driver.findElement(By.xpath("//select[@id='product_kind']")));
        selectProdcutCode.selectByVisibleText("종신·정기보험");
        WaitUtil.waitFor(1);
        helper.click(By.xpath("//*[@id='product_kind']//ancestor::li//*[@class='btn btn-primary']"));
        WaitUtil.waitFor(2);

        logger.info("보험상품명 선택 :: [ (무)AIA Vitality 베스트핏 보장보험 ]");
        Select selectProductName = new Select(driver.findElement(By.xpath("//select[@id='planNo']")));
        selectProductName.selectByVisibleText("(무)AIA Vitality 베스트핏 보장보험");
        WaitUtil.waitFor(1);
        helper.click(By.xpath("//*[@id='planNo']//ancestor::li//*[@class='btn btn-primary']"));
        WaitUtil.waitFor(2);

        // IFRAME 화면전환
        String lastWindow = null;
        String currentHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        for (String aux : handles) {
            lastWindow = aux;
        }

        logger.info("새로운 pop창");
        driver.switchTo().window(lastWindow);
        driver.switchTo().frame(1); // frame 전환

        // ========================================================================================

        // 1. 고객님의 정보를 입력해 주세요
        logger.info("1. 이름을 설정합니다");
        String tempName = PersonNameGenerator.generate();
        setName(tempName);

        logger.info("2. 생년월일, 성별을 설정합니다");
        setBirthdayNew(info.getFullBirth());
        setGenderNew(info.getGender());

        logger.info("3. 운전여부를 설정합니다");
        setDriveYn("승용차(자가용)");

        logger.info("4. 흡연여부를 설정합니다");
        setIsSmoker("일반");

        logger.info("5. 직업을 설정합니다");
        setJobNew(Job.MANAGER.getCodeValue());

        // ========================================================================================

        // 2. 주계약을 선택해 주세요
        // 2.1 보험기간
        logger.info("보험기간을 설정합니다");
        setInsTermNew(info.getInsTerm());

        // 2.2 납입기간
        logger.info("납입기간을 설정합니다");
        setNapTermNew(info);

        // 2.3 납입주기
        logger.info("납입주기를 설정합니다");
        setNapCycleNew(info.getNapCycleName());

        // 2.4 보험가입금액
        logger.info("보험가입금액을 설정합니다");
        logger.info("AIL | AIA 생명의 경우, '구좌'키워드를 사용합니다");
        logger.info("AIL | 해당상품의 1구좌는 10만원에 해당합니다");
        try {
            String mainAssureMoney = String.valueOf(Integer.parseInt(info.getAssureMoney()) / 1_0000);
            logger.info("주계약 가입금액 :: {}", mainAssureMoney);
            WebElement inpuEl= driver.findElement(By.xpath("//input[@name='sfaceAmt_N801V0_02B']"));
            inpuEl.clear();
            inpuEl.sendKeys(mainAssureMoney);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
//            throw new CommonCrawlerException("주계약 보험가입금액 설정중 에러가 발생하였습니다", e.getMessage());
            throw new CommonCrawlerException(e.getMessage());
        }

        // 특약을 선택해 주세요 (선택특약의 선택유무 /  보험기간, 납입기간, 가입금액 입력)
        List<WebElement> trList = driver.findElements(By.xpath("//caption[contains(., '특약 선택 - 항목으로는 특약, 보험기간, 납입기간, 보험가입금액, 보험료가 있습니다.')]/parent::table//tbody//tr"));
        try {
            for(WebElement eachTr : trList) {
                for(CrawlingTreaty eachTrt : info.getTreatyList()) {
                    element = eachTr.findElement(By.xpath(".//td[1]/label"));
                    String trName = element.getText();
                    String trtName = eachTrt.treatyName;
                    if(trName.equals(trtName)) {
                        logger.info("Treaty :: {}", trtName);
                        logger.info("<tr>   :: {}", trName);
                        logger.info("============================================");

                        Select sbOptionalInsTerm = new Select(eachTr.findElement(By.xpath(".//td[2]/select")));
                        Select sbOptionalNapTerm = new Select(eachTr.findElement(By.xpath(".//td[3]/select")));
                        WebElement inputOptinalAssureMoney = eachTr.findElement(By.xpath(".//td[4]/input[2]"));

                        element.click();
                        String optinalInsTerm = eachTrt.getInsTerm() + "만기";
                        String optinalNapTerm = (eachTrt.getInsTerm().equals(eachTrt.getNapTerm())) ? "전기납" : eachTrt.getNapTerm() + "납";
                        String optinalAssureMoney = String.valueOf(eachTrt.getAssureMoney() / 10_0000);
                        logger.info("1구좌 단위:: {}", optinalAssureMoney);

                        sbOptionalInsTerm.selectByVisibleText(optinalInsTerm);
                        sbOptionalNapTerm.selectByVisibleText(optinalNapTerm);
                        inputOptinalAssureMoney.clear();
                        inputOptinalAssureMoney.sendKeys(optinalAssureMoney);

                    }
                }
            }

        } catch(Exception e) {
//            throw new CommonCrawlerException("특약을 선택해 주세요 - 선택특약 설정중 에러가 발생하였습니다");
            throw new CommonCrawlerException(e.getMessage());
        }

        logger.info("선택 특약에 대한 확인을 진행합니다");
// todo | 특약검증 (특약선택확인, 특약금액입력확인)

        logger.info("'보험료 계산하기' 클릭");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        WaitUtil.loading(4);

        logger.info("보험료 확인");
        String monthlyFee = driver.findElement(By.id("premium_tot")).getText().replaceAll("[^0-9]", "");
        info.getTreatyList().get(0).monthlyPremium = monthlyFee;
        logger.info("월 보험료 :: {}", info.getTreatyList().get(0).monthlyPremium);
        WaitUtil.waitFor(2);

        logger.info("상품제안서 선택 ::: ");
        driver.findElement(By.cssSelector("body > form:nth-child(1) > div > div.bg1 > div > div.margin-top-l.margin-bottom-4xl.btn-btm > button")).click();
        WaitUtil.loading(4);

        ((JavascriptExecutor) driver).executeScript("scrollTo(0, document.body.scrollHeight);");
        logger.info("스크린 샷 촬영");
        takeScreenShot(info);
        WaitUtil.waitFor(2);

        logger.info("해약환급금 확인");
        driver.findElement(By.xpath("//a[text()='해약환급금']")).click();
        WaitUtil.waitFor(1);

        List<WebElement> trListForRefund = driver.findElements(By.xpath("//caption[text()='해약환급금']/parent::table/tbody/tr"));
        List<PlanReturnMoney> returnMoneyList = new ArrayList<>();
        for(WebElement eachTr : trListForRefund) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = eachTr.findElement(By.xpath(".//td[1]")).getText();
            String sum = eachTr.findElement(By.xpath(".//td[4]")).getText().replaceAll("[^0-9]", "");
            String returnAmt = eachTr.findElement(By.xpath(".//td[3]")).getText().replaceAll("[^0-9]", "");
            String returnRate = eachTr.findElement(By.xpath(".//td[5]")).getText();

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(sum);
            planReturnMoney.setReturnMoney(returnAmt);
            planReturnMoney.setReturnRate(returnRate);

            returnMoneyList.add(planReturnMoney);

            info.setReturnPremium(returnAmt);

            logger.info("term :: {} ", term);
            logger.info("sum  :: {} ", sum);
            logger.info("rAmt :: {} ", returnAmt);
            logger.info("rate :: {}", returnRate);
            logger.info("======================================");
        }
        logger.info("더 이상 참조할 차트가 존재하지 않습니다");
        info.setPlanReturnMoneyList(returnMoneyList);

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



    // TYL | 공시실 | 이름 설정
    public void setName(String name) throws CommonCrawlerException {
        try {
            logger.info("이름설정 :: {}", name);
            driver.findElement(By.id("custNm")).sendKeys(name);
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new CommonCrawlerException("이름 설정중 에러가 발생하였습니다");
        }
    }



    public void setDriveYn(String condition) throws CommonCrawlerException {
        try {
            logger.info("운전여부 설정 :: {}", condition);
            driver.findElement(By.id("drvgCd")).sendKeys(condition);
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new CommonCrawlerException("운전여부 설정중 에러가 발생하였습니다");
        }
    }



    public void setIsSmoker(String isSmoked) throws CommonCrawlerException {
        try {
            logger.info("흡연여부를 설정 :: {}", isSmoked);
            if(isSmoked.equals("일반")) {
                driver.findElement(By.id("grp-rdo3")).click();
            } else {
                logger.info("해당 상품은 default설정(일반)이 고정된 상품입니다");
            }
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new CommonCrawlerException("흡연여부 설정중 에러가 발생하였습니다");
        }
    }


    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        try {
            String birth = (String) obj;
            logger.info("생년월일 설정 :: {}",  birth);
            driver.findElement(By.id("brthDt")).sendKeys(birth);
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new SetBirthdayException();
        }
    }



    @Override
    public void setGenderNew(Object obj) throws SetGenderException {
        try {
            int gender = (int) obj;
            logger.info("성별 설정 :: {}", (gender == MALE)? "남" : "여");
            String elOption = (gender == MALE)? "grp-rdo1" : "grp-rdo2" ;
            driver.findElement(By.id(elOption)).click();
            WaitUtil.waitFor(1);

        } catch(Exception e) {
            throw new SetGenderException(e.getMessage());
        }
    }



    @Override
    public void setJobNew(Object obj) throws SetJobException {

// todo | ENUM의 직접 비교는 불가능한가? (그래도 '상수'아닌가?)
        String job = (String) obj;

        try {
            logger.info("직업을 설정 :: {}", job);

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
            WaitUtil.waitFor(2);

            driver.findElement(By.id("job_name")).sendKeys(job);
            driver.findElement(By.cssSelector("body > div > div > div > div.modal-container > div.pop-job-form.clearfix > form:nth-child(1) > div > button")).click();
            WaitUtil.waitFor(2);
            driver.findElement(By.linkText("경영지원 사무직 관리자")).click();
            WaitUtil.waitFor(2);

            // IFRAME ============================================
            driver.switchTo().window(vars.get("root").toString());
            driver.switchTo().frame(1);
            // ===================================================

        } catch(Exception e) {
            throw new SetJobException(e.getMessage());
        }
    }



    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {

        String insterm = (String) obj;

//        // IFRAME ============================================
//        driver.switchTo().window(vars.get("root").toString());
//        driver.switchTo().frame(1);
//        // ===================================================
        try {
            insterm += "만기";
            logger.info("보험기간을 설정 :: {}", insterm);
            Select sbInsTerm = new Select(driver.findElement(By.id("polprd_N801V0_02B")));
            sbInsTerm.selectByVisibleText(insterm);
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }



    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {

        CrawlingProduct info = (CrawlingProduct) obj;
        String insTerm = info.getInsTerm();
        String napTerm = info.getNapTerm();

        try {
            if(insTerm.equals(napTerm)) napTerm = "전기납";
            logger.info("납입기간을 설정 :: {}", napTerm);
            Select sbNapTerm = new Select(driver.findElement(By.id("payprd_N801V0_02B")));
            sbNapTerm.selectByVisibleText(napTerm + "납");
            WaitUtil.waitFor(2);

        } catch(Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }



    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException {

        String napCycleName = (String) obj;
        try {
            logger.info("납입주기를 설정 :: {}", napCycleName);
            Select sbNapCycle = new Select(driver.findElement(By.xpath("//select[@name='payCyclCd']")));
            sbNapCycle.selectByVisibleText(napCycleName);

        } catch(Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }
    }


    // 미사용
    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {}

    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException { }

    @Override
    public void setRefundTypeNew(Object obj) throws SetRefundTypeException { }

    @Override
    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException { }

    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException { }

    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException { }
}
