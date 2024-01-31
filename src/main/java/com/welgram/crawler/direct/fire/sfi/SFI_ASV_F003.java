
package com.welgram.crawler.direct.fire.sfi;

import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SFI_ASV_F003 extends CrawlingSFIAnnounce {

    public static void main(String[] args) {
        executeCommand(new SFI_ASV_F003(), args);
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

        logger.info("납입주기 설정");
        $select = driver.findElement(By.id("c_payfrqCd"));
        setNapCycle($select, info.getNapCycleName());

        logger.info("개시연령(=연금개시나이) 설정");
        $select = driver.findElement(By.id("c_zzanutBgAgeCd"));
        setAnnuityAge($select, info.getAnnuityAge());

        logger.info("지급기간(=확정N년???) 설정");
        $select = driver.findElement(By.id("c_zzanutPyPrdCd"));
        String annuityReceivePeriod = info.getAnnuityType().substring(3);
        setAnnuityReceivePeriod($select, annuityReceivePeriod);

        logger.info("(Fixed)수령방법 설정");
        $select = driver.findElement(By.id("c_cov_payfrqCd"));
        setAnnuityReceiveCycle($select, "매년");

        logger.info("(Fixed)지급형태 설정");
        $select = driver.findElement(By.id("c_cov_typannuitypayCd"));
        setAnnuityGiveType($select, "균등설계형");

        logger.info("1회 보험료 설정");
        setAssureMoney(info.getAssureMoney());

        logger.info("보험료 크롤링");
        crawlPremium(info);

        logger.info("스크린샷 찍기");
        WebElement $element = driver.findElement(By.xpath("//th[normalize-space()='납입기간']"));
        helper.moveToElementByJavascriptExecutor($element);
        takeScreenShot(info);

        logger.info("연금수령액 크롤링");
        crawlAnnuityPremium(info);

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyList(info);

        logger.info("환급금 크롤링");
        crawlReturnPremium(info);


        return true;
    }



    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "1회 보험료";

        String expectedOneTimePremium = (String) obj[0];
        String actualOneTimePremium = "";

        WebElement $oneTimePremiumInput = null;
        WebElement $premiumCalcBtn = null;
        WebElement $premiumSpan = null;
        String premium = "";

        try {

            //1회 보험료 설정
            logger.info("1회 보험료 설정(연금상품에서는 해당 값이 월 보험료가 된다)");
            $oneTimePremiumInput = driver.findElement(By.id("premaftertaxAm"));
            actualOneTimePremium = helper.sendKeys4_check($oneTimePremiumInput, expectedOneTimePremium);


            logger.info("보험료 계산 버튼 클릭");
            $premiumCalcBtn = driver.findElement(By.xpath("//span[text()='보험료 계산']/parent::button"));
            click($premiumCalcBtn);
            WaitUtil.waitFor(3);


            //비교
            super.printLogAndCompare(title, expectedOneTimePremium, actualOneTimePremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_ONE_TIME_PREMIUM;
            throw new SetAssureMoneyException(e, exceptionEnum.getMsg());
        }
    }



    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        String savePremium = "";


        try {

            WebElement $savePremiumSpan = driver.findElement(By.id("zaAcumPrm"));
            savePremium = $savePremiumSpan.getText().replaceAll("[^0-9]", "");

            //보험료 정보 세팅(연금 상품에서는 적립보험료가 월 보험료 금액이다)
            mainTreaty.monthlyPremium = savePremium;


            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        }  catch (Exception e) {
            throw new PremiumCrawlerException(e, exceptionEnum.getMsg());
        }

    }
}





//package com.welgram.crawler.direct.fire.sfi;
//
//import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
//import com.welgram.common.WaitUtil;
//import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
//import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
//import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
//import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
//import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
//import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
//import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
//import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
//import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
//import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
//import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
//import com.welgram.common.except.crawler.setUserInfo.SetJobException;
//import com.welgram.crawler.common.except.CrawlingException;
//import com.welgram.crawler.direct.fire.CrawlingSFI;
//import com.welgram.crawler.general.CrawlingProduct;
//import com.welgram.crawler.general.PlanReturnMoney;
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.List;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//
//public class SFI_ASV_F003 extends CrawlingSFI {
//
//    public static void main(String[] args) {
//        executeCommand(new SFI_ASV_F003(), args);
//    }
//
//    @Override
//    protected boolean scrap(CrawlingProduct info) throws Exception {
//
//        WaitUtil.loading(2);
//
//        helper.waitVisibilityOfElementLocated(By.id("c_prempayminybAm"));
//
//        setNapTermNew(info);    // 납입기간
//        setNapCycleNew(info);   // 납입주기
//        setAnnuityAge(info);    // 개시연령
//        setAnnuityType(info);   // 연금타입(지급기간)
//        setAnnuityMethod(info); // 수령방법
//
//        logger.info("지급형태");
//        helper.doSelectBox(
//            By.id("c_cov_typannuitypayCd"),
//            "균등설계형"
//        );
//
//        setBirthdayNew(info);          // 생년월일
//        setGenderNew(info);            // 성별
//        setInjuryLevel(info);          // 상해급수
//        setDrivingPurpose(info);       // 운전용도
//
//        crawlPremiumNew(info);         // 보험료 입력
//        WaitUtil.loading(3);
//        takeScreenShot(info);
//
//        crawlReturnMoneyListNew(info); // 해약환급금표
//        crawlAnnuityPremium(info);     // 예상 연금 수령액
//
//        return true;
//    }
//
//    private void crawlAnnuityPremium(Object obj) throws CrawlingException {
//        try {
//            logger.info("예상 연금 수령액");
//            CrawlingProduct info = (CrawlingProduct) obj;
//
//            helper.switchToWindow(driver.getWindowHandle(), driver.getWindowHandles(), true);
//            helper.doClick(By.id("zzParngAmtAmDtl"), "예상 환급 버튼");
//            helper.waitVisibilityOfElementLocated(By.cssSelector("tbody#contArea"));
//            String annuityPremiumStr = helper.waitPresenceOfElementLocated(By.cssSelector("#contArea > tr > td.txt-r"))
//                .getText().replaceAll("\\D","");
//
//            if (info.annuityType.startsWith("확정")) {
//                info.fixedAnnuityPremium = annuityPremiumStr;
//            } else {
//                info.annuityPremium = annuityPremiumStr;
//            }
//
//        } catch (Exception e) {
//            throw new CrawlingException("예상 연금 수령액 오류 : " + e.getMessage());
//        }
//    }
//
//    private void setDrivingPurpose(Object obj) throws CrawlingException {
//        try {
//            logger.info("운전차용도");
//            helper.doSelectBox(
//                By.id("p_zzdrvrTypCd"),
//                "자가용"
//            );
//        } catch (Exception e) {
//            throw new CrawlingException(e.getMessage());
//        }
//    }
//
//    private void setInjuryLevel(Object obj) throws CrawlingException {
//        try {
//            logger.info("상해급수 1급");
//            helper.doSelectBox(
//                By.id("p_zzinjryGrd3Cd"),
//                "1급"
//            );
//        } catch (Exception e) {
//            throw new CrawlingException(e.getMessage());
//        }
//    }
//
//    private void setAnnuityMethod(Object obj) throws CrawlingException {
//        try {
//            logger.info("수령방법");
//            helper.doSelectBox(
//                By.id("c_cov_payfrqCd"),
//                "매년"
//            );
//        } catch (Exception e) {
//            throw new CrawlingException(e.getMessage());
//        }
//    }
//
//    public void setAnnuityAge(Object obj) throws CrawlingException {
//        logger.info("개시연령");
//        try {
//            CrawlingProduct info = (CrawlingProduct) obj;
//            helper.doSelectBox(
//                By.id("c_zzanutBgAgeCd"),
//                info.annAge
//            );
//        } catch (Exception e) {
//            throw new CrawlingException("연금개시나이");
//        }
//    }
//
//    public void setAnnuityType(Object obj) throws CrawlingException {
//        logger.info("연금타입(지급기간)");
//        try {
//            CrawlingProduct info = (CrawlingProduct) obj;
//            String period = info.annuityType.substring(3);
//            helper.doSelectBox(
//                By.id("c_zzanutPyPrdCd"),
//                period
//            );
//        } catch (Exception e) {
//            throw new CrawlingException("연금타입(지급기간)");
//        }
//    }
//
//    @Override
//    public void setNapCycleNew(Object obj) throws SetNapCycleException {
//        logger.info("납입주기 월납 생략");
//    }
//
//    @Override
//    public void setBirthdayNew(Object obj) throws SetBirthdayException {
//        logger.info("생년월일");
//        try {
//            CrawlingProduct info = (CrawlingProduct) obj;
//            helper.doSendKeys(By.id("p_birthDt"), info.fullBirth);
//        } catch (Exception e) {
//            throw new SetBirthdayException(e);
//        }
//    }
//
//    @Override
//    public void setGenderNew(Object obj) throws SetGenderException {
//        logger.info("성별");
//        try {
//            CrawlingProduct info = (CrawlingProduct) obj;
//            String gender = info.gender == 0 ? "남자" : "여자";
//            helper.doSelectBox(
//                By.id("p_genderCd"),
//                gender
//            );
//        } catch (Exception e) {
//            throw new SetGenderException(e);
//        }
//    }
//
//    @Override
//    public void setJobNew(Object obj) throws SetJobException {
//        super.setJobNew(obj);
//    }
//
//    @Override
//    public void setRefundTypeNew(Object obj) throws SetRefundTypeException {
//        try {
//            super.setRefundTypeNew(obj);
//        } catch (Exception e) {
//            throw new SetRefundTypeException(e);
//        }
//    }
//
//    @Override
//    public void setInsTermNew(Object obj) throws SetInsTermException {
//        super.setInsTermNew(obj);
//    }
//
//    @Override
//    public void setNapTermNew(Object obj) throws SetNapTermException {
//        logger.info("납입기간");
//        try {
//            CrawlingProduct info = (CrawlingProduct) obj;
//            helper.doSelectBox(
//                By.id("c_prempayminybAm"),
//                info.napTerm
//            );
//        } catch (Exception e) {
//            throw new SetNapTermException(e);
//        }
//    }
//
//    @Override
//    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {
//        super.setRenewTypeNew(obj);
//    }
//
//    @Override
//    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException {
//        try {
//            CrawlingProduct info = (CrawlingProduct) obj;
//            // 보험료 입력
//            helper.doSendKeys(By.id("premaftertaxAm"), info.assureMoney);
//            info.treatyList.get(0).monthlyPremium = info.assureMoney;
//
//            // 보험료 계산 클릭
//            helper.doClick(By.xpath("//button[contains(.,'보험료 계산')]"));
//            WaitUtil.loading(1);
//            helper.waitForLoading();
//        } catch (Exception e) {
//            throw new PremiumCrawlerException(e);
//        }
//    }
//
//    @Override
//    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {
//        super.crawlReturnPremiumNew(obj);
//    }
//
//    @Override
//    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
//        try {
//            CrawlingProduct info = (CrawlingProduct) obj;
//
//            // 해약환급금
//            logger.info("해약환급금 테이블");
//            helper.doClick(By.xpath("//button[contains(.,'상세보기')]"), "해약환급금 테이블 보기(상세보기)");
//
//            // 해약환급금 창으로 전환
//            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
//            WaitUtil.loading(3);
//            helper.switchToWindow(driver.getWindowHandle(), driver.getWindowHandles(), true);
//
//            WebElement svg = new WebDriverWait(driver, 180).until(
//                ExpectedConditions.visibilityOfElementLocated(
//                    By.xpath("//*[@id='oziviw_1']//*[local-name()='svg']")
//                ));
//
//            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
//            JavascriptExecutor js = (JavascriptExecutor) driver;
//            String data = (String) js.executeScript("return xmlMsgAll");
//
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            InputSource is = new InputSource(new StringReader(data));
//            Document doc = builder.parse( is );
//            NodeList sr = doc.getElementsByTagName("SR");
//
//            for (int i = 0; i < sr.getLength(); i++) {
//                Node item = sr.item(i);
//                String term = ((DeferredElementImpl) item).getAttribute("sr0").toString();
//                String premiumSum = ((DeferredElementImpl) item).getAttribute("sr1").toString();
//                String returnMoneyMin = ((DeferredElementImpl) item).getAttribute("sr16").toString(); // 최저보증이율 예상해약환급금
//                String returnRateMin = ((DeferredElementImpl) item).getAttribute("sr17").toString(); // 최저보증이율 예상해약환급률
//                String returnMoney = ((DeferredElementImpl) item).getAttribute("sr29").toString(); // 공시이율 예상해약환급률
//                String returnRate = ((DeferredElementImpl) item).getAttribute("sr30").toString(); // 공시이율 예상해약환급률
//                String returnMoneyAvg = ((DeferredElementImpl) item).getAttribute("sr31").toString(); // 평균공시이율 예상해약환급률
//                String returnRateAvg = ((DeferredElementImpl) item).getAttribute("sr32").toString(); // 평균공시이율 예상해약환급률
//
//                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
//                planReturnMoney.setTerm(term);
//                planReturnMoney.setPremiumSum(premiumSum);
//                planReturnMoney.setReturnMoneyMin(returnMoneyMin);
//                planReturnMoney.setReturnRateMin(returnRateMin);;
//                planReturnMoney.setReturnMoney(returnMoney);
//                planReturnMoney.setReturnRate(returnRate);
//                planReturnMoney.setReturnMoneyAvg(returnMoneyAvg);
//                planReturnMoney.setReturnRateAvg(returnRateAvg);
//
//                info.returnPremium = returnMoney.replaceAll("\\D","");;
//                planReturnMoneyList.add(planReturnMoney);
//            }
//            info.setPlanReturnMoneyList(planReturnMoneyList);
//        } catch (Exception e) {
//            throw new ReturnMoneyListCrawlerException(e);
//        }
//    }
//
//    @Override
//    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {
//        super.setAssureMoneyNew(obj);
//    }
//}
