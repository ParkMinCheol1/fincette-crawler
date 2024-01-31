package com.welgram.crawler.direct.fire.sfi;

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class SFI_DSS_F064 extends CrawlingSFIAnnounce {

    public static void main(String[] args) {
        executeCommand(new SFI_DSS_F064(), args);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WebElement $input = null;
        WebElement $select = null;
        WebElement $button = null;

        driver.manage().window().maximize();
        waitLoadingBar();

        logger.info("생년월일 설정");
        $input = driver.findElement(By.id("p_birthDt"));
        setBirthday($input, info.getFullBirth());

        logger.info("성별 설정");
        $select = driver.findElement(By.id("p_genderCd"));
        setGender($select, info.getGender());

        logger.info("(Fixed)상해급수Ⅲ 설정");
        $select = driver.findElement(By.id("p_zzinjryGrd3Cd"));
        setInjuryLevel($select, "1급");

        logger.info("(Fixed)운전차용도 설정");
        $select = driver.findElement(By.id("p_zzdrvrTypCd"));
        setVehicle(info.getAge(), $select);

        logger.info("납입기간 설정");
        $select = driver.findElement(By.id("c_prempayminybAm"));
        setNapTerm($select, info.getNapTerm());

        logger.info("보험기간 설정");
        $select = driver.findElement(By.id("c_insdurinyearsAm"));
        setInsTerm($select, info.getInsTerm());

        logger.info("납입주기 설정");
        $select = driver.findElement(By.id("c_payfrqCd"));
        setNapCycle($select, info.getNapCycleName());

        logger.info("건강고지 유형");
        $select = driver.findElement(By.id("c_hlthDsTSCd"));
        setHealthType($select, info.planSubName);

        logger.info("담보 설정");
        setTreaties(info.getTreatyList());

        logger.info("1회 보험료 설정");
        setAssureMoney();

        logger.info("보험료 계산 버튼 클릭");
        $button = driver.findElement(By.xpath("//span[text()='보험료 계산']/parent::button"));
        click($button);
        WaitUtil.waitFor(3);

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        WebElement $element = driver.findElement(By.xpath("//th[normalize-space()='납입기간']"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        return true;
    }



    @Override
    public void crawlReturnMoneyList(Object... obj) throws ReturnMoneyListCrawlerException {
        try {
            CrawlingProduct info = (CrawlingProduct) obj[0];

            // 해약환급금
            logger.info("해약환급금 테이블");
            WebElement $button = driver.findElement(By.xpath("//span[text()='상세보기']/parent::button"));
            click($button);

            // 해약환급금 창으로 전환
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            WaitUtil.loading(3);
            helper.switchToWindow(driver.getWindowHandle(), driver.getWindowHandles(), true);

            WebElement svg = new WebDriverWait(driver, 180).until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='oziviw_1']//*[local-name()='svg']")
                ));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String data = (String) js.executeScript("return xmlMsgAll");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(data));
            Document doc = builder.parse( is );
            NodeList sr = doc.getElementsByTagName("SR");

            for (int i = 0; i < sr.getLength(); i++) {
                Node item = sr.item(i);
                String term = ((DeferredElementImpl) item).getAttribute("sr0").toString();
                String premiumSum = ((DeferredElementImpl) item).getAttribute("sr13").toString();
                String returnMoney = ((DeferredElementImpl) item).getAttribute("sr47").toString();
                String returnRate = ((DeferredElementImpl) item).getAttribute("sr48").toString();

                logger.info("경과기간 ::        {} | 납입보험료 ::       {} | 해약환급금 ::       {} | 해약환급률 ::       {}", term, premiumSum, returnMoney, returnRate);

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);


                info.returnPremium = returnMoney.replaceAll("\\D","");

                planReturnMoneyList.add(planReturnMoney);
            }
            logger.info("============================================================");
            logger.info("만기환급금 :: {}", info.returnPremium);
            info.setPlanReturnMoneyList(planReturnMoneyList);

        } catch (Exception e) {
            throw new ReturnMoneyListCrawlerException(e);
        }
    }
}