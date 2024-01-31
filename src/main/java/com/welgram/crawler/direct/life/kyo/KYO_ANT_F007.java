package com.welgram.crawler.direct.life.kyo;


import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.crawler.direct.life.CrawlingKYO;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


// 2023.05.09           | 최우진           | 대면_저축
// KYO_ANT_F007         | 교보하이브리드연금보험 23.05 (무배당,적립형)
public class KYO_ANT_F007 extends CrawlingKYO {

    public static void main(String[] args) { executeCommand(new KYO_ANT_F007(), args); }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        logger.info("START :: KYO_ANT_F007");

        logger.info("공시실 진입 후 연금 버튼 클릭");
        element = driver.findElement(By.linkText("연금"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(2);

        logger.info("상품명 : {} 클릭", info.productNamePublic);
        element = driver.findElement(By.xpath("//td[text()='" + info.productNamePublic + "']/parent::tr//button"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(3);

        logger.info("생년월일 설정");
        setBirthdayNew(info.fullBirth);

        logger.info("성별 설정");
        setGenderNew(info.gender);

        logger.info("보험종류 설정");
        setPlanType(info.textType);

        logger.info("납입주기 설정");
        setNapCycleNew(info.getNapCycleName());

        logger.info("보험기간 설정");
        this.setInsTermNew("종신");

        logger.info("납입기간 설정");
        setNapTermNew(info.napTerm);

        logger.info("보험료 설정");
        this.setAssureMoneyNew(info.assureMoney);

        logger.info("연금개시나이 설정");
        setAnnuityAge(info.annuityAge);

        //logger.info("특약 설정 및 비교");
        //setTreaties(info);

        logger.info("보험료 크롤링");
        WebElement element = driver.findElement(By.xpath("//div[@id='totPrmTx']/strong"));
        String premium = element.getText().replaceAll("[^0-9]", "");
        info.treatyList.get(0).monthlyPremium = premium;

        logger.info("스크린샷찍기");
        moveToElementByJavascriptExecutor(element);
        takeScreenShot(info);

        logger.info("보장내용 버튼 클릭");
        element = driver.findElement(By.xpath("//button[text()='보장내용']"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(1);
        waitAnnouncePageLoadingBar();

        logger.info("해약환급금 탭 버튼 클릭");
        element = driver.findElement(By.linkText("해약환급금"));
        waitElementToBeClickable(element).click();

        logger.info("해약환급금 크롤링");
        crawlReturnMoneyListNew(info);

        logger.info("연금예시 탭 버튼 클릭");
        element = driver.findElement(By.linkText("연금예시"));
        waitElementToBeClickable(element).click();

        logger.info("연금수령액 크롤링");
        crawlAnnuityPremium(info);

        // 05.10 '예상 적립금' 추가 내용
        logger.info("예상적립금 크롤링");
        info.expectSavePremium = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(10) > table > tbody > tr:nth-child(1) > td:nth-child(4)"))
            .getText()
            .replaceAll("[^0-9]", "");

        return true;
    }



    private void setPlanType(Object obj) throws Exception {

        String title = "보험종류";
        String welgramPlanType = (String) obj;

        //보험종류 클릭
        driver.findElement(By.xpath("//option[contains(.,'" + welgramPlanType + "')]")).click();
        WebElement select = driver.findElement(By.id("sel_gdcl"));
//        selectOptionByText(select, welgramPlanType);

        //실제로 클릭된 보험종류 읽어오기
        String script = "return $(arguments[0]).find('option:selected').text();";
        String targetPlanType = String.valueOf(executeJavascript(script, select));

        //비교
//        printAndCompare(title, welgramPlanType, targetPlanType);
    }



    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {

        String title = "보험기간";
        String welgramInsTerm = (String) obj;
        //welgramInsTerm = welgramInsTerm.replace("보장", "");

        try {
            //보험기간 클릭
            WebElement select = driver.findElement(By.xpath("//span[@id='show_isPd']//select[@name='pdtScnCd_isPd']"));
            selectOptionByText(select, welgramInsTerm);

            //실제로 클릭된 보험기간 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetInsTerm = String.valueOf(executeJavascript(script, select));

            //비교
            printAndCompare(title, welgramInsTerm, targetInsTerm);

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }
    }



    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {

        String title = "보험료";
        String welgramAssureMoney = (String) obj;
        String toSetAssureMoney = "";

        try {
            //보험료 입력
            WebElement input = driver.findElement(By.xpath("//td[@id='show_pdtPrm']//input[2]"));
            String unitText = input.findElement(By.xpath("./following-sibling::i")).getText();

            int unit = 1;
            switch (unitText) {
                case "억원":
                    unit = 100000000;
                    break;

                case "천만원":
                    unit = 10000000;
                    break;

                case "백만원":
                    unit = 1000000;
                    break;

                case "십만원":
                    unit = 100000;
                    break;

                case "만원":
                    unit = 10000;
                    break;

                case "천원":
                    unit = 1000;
                    break;

                case "백원":
                    unit = 100;
                    break;

                case "십원":
                    unit = 10;
                    break;

                case "원":
                    unit = 1;
                    break;
            }

            toSetAssureMoney = String.valueOf(Integer.parseInt(welgramAssureMoney) / unit);
            setTextToInputBox(input, toSetAssureMoney);

            //실제로 입력된 가입금액 읽어오기
            String script = "return $(arguments[0]).val();";
            String targetAssureMoney = String.valueOf(executeJavascript(script, input));

            //비교
            printAndCompare(title, toSetAssureMoney, targetAssureMoney);

        } catch (Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }



    private void setAnnuityAge(Object obj) throws Exception {

        String title = "연금개시나이";
        String welgramAnnAge = (String) obj;
        welgramAnnAge = welgramAnnAge + "세";

        try {
            //연금개시나이 클릭
            WebElement select = driver.findElement(By.xpath("//select[@name='anBgnAe']"));
            waitElementToBeClickable(select).click();
            selectOptionByText(select, welgramAnnAge);

            //실제로 클릭된 보험기간 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetInsTerm = String.valueOf(executeJavascript(script, select));

            //비교
            printAndCompare(title, welgramAnnAge, targetInsTerm);

        } catch (Exception e) {
            throw new SetInsTermException(e.getMessage());
        }

        logger.info("보험료 계산 버튼 클릭");
        element = driver.findElement(By.xpath("//div[@class='pbt']//button[text()='보험료 계산']"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();
    }



    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj;

        List<WebElement> $trList = driver.findElements(By.xpath("//div[@id='trmRview']//table/tbody/tr"));
        for(WebElement $tr : $trList) {
            String term = $tr.findElement(By.xpath("./td[1]")).getText();
            String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText();
            String returnMoneyMin = $tr.findElement(By.xpath("./td[3]")).getText();
            String returnRateMin = $tr.findElement(By.xpath("./td[4]")).getText();
            String returnMoneyAvg = $tr.findElement(By.xpath("./td[5]")).getText();
            String returnRateAvg = $tr.findElement(By.xpath("./td[6]")).getText();
            String returnMoney = $tr.findElement(By.xpath("./td[7]")).getText();
            String returnRate = $tr.findElement(By.xpath("./td[8]")).getText();
            returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));
            returnMoneyAvg = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg));
            returnMoneyMin = String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin));


            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("공시환급금 : {}", returnMoney);
            logger.info("공시환급률 : {}", returnRate);
            logger.info("평균환급금 : {}", returnMoneyAvg);
            logger.info("평균환급률 : {}", returnRateAvg);
            logger.info("최저환급금 : {}", returnMoneyMin);
            logger.info("최저환급률 : {}", returnRateMin);
            logger.info("==========================================");

            PlanReturnMoney p = new PlanReturnMoney();
            p.setTerm(term);
            p.setPremiumSum(premiumSum);
            p.setReturnMoney(returnMoney);
            p.setReturnRate(returnRate);
            p.setReturnMoneyAvg(returnMoneyAvg);
            p.setReturnRateAvg(returnRateAvg);
            p.setReturnMoneyMin(returnMoneyMin);
            p.setReturnRateMin(returnRateMin);

            //만기환급금 세팅
//            info.returnPremium = returnMoney;
            info.planReturnMoneyList.add(p);
        }

        logger.info("만기환급금 : {}", info.returnPremium);
    }



    private void crawlAnnuityPremium(CrawlingProduct info) throws Exception {

        WaitUtil.waitFor(2);

        String unitText = driver.findElement(By.cssSelector("#anXmplRview > div.ut-optset.optset.mt50.a-bot > div.opt.pr > span")).getText();
        unitText = unitText.replaceAll("[(:)]","");
        unitText = unitText.replace("단위","");

        int unit = 1;
        switch (unitText) {
            case "억원":
                unit = 100000000;
                break;

            case "천만원":
                unit = 10000000;
                break;

            case "백만원":
                unit = 1000000;
                break;

            case "십만원":
                unit = 100000;
                break;

            case "만원":
                unit = 10000;
                break;

            case "천원":
                unit = 1000;
                break;

            case "백원":
                unit = 100;
                break;

            case "십원":
                unit = 10;
                break;

            case "원":
                unit = 1;
                break;
        }

        PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();
        String whl10y = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(8) > table > tbody > tr:nth-child(1) > td:nth-child(3)")).getText() + unitText;
        String whl20y = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(8) > table > tbody > tr:nth-child(1) > td:nth-child(4)")).getText() + unitText;
        String whl30y = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(8) > table > tbody > tr:nth-child(1) > td:nth-child(5)")).getText() + unitText;
        String whl100a = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(8) > table > tbody > tr:nth-child(1) > td:nth-child(6)")).getText() + unitText;
        String fxd10y = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(9) > table > tbody > tr > td:nth-child(3)")).getText() + unitText;
        String fxd15y = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(9) > table > tbody > tr > td:nth-child(4)")).getText() + unitText;
        String fxd20y = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(9) > table > tbody > tr > td:nth-child(5)")).getText() + unitText;
        String fxd25y = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(9) > table > tbody > tr > td:nth-child(6)")).getText() + unitText;
        String fxd30y = driver.findElement(By.cssSelector("#anXmplRview > div:nth-child(9) > table > tbody > tr > td:nth-child(7)")).getText() + unitText;

        planAnnuityMoney.setWhl10Y(String.valueOf(MoneyUtil.toDigitMoney(whl10y)));
        planAnnuityMoney.setWhl20Y(String.valueOf(MoneyUtil.toDigitMoney(whl20y)));
        planAnnuityMoney.setWhl30Y(String.valueOf(MoneyUtil.toDigitMoney(whl30y)));
        planAnnuityMoney.setWhl100A(String.valueOf(MoneyUtil.toDigitMoney(whl100a)));

        planAnnuityMoney.setFxd10Y(String.valueOf(MoneyUtil.toDigitMoney(fxd10y)));
        planAnnuityMoney.setFxd15Y(String.valueOf(MoneyUtil.toDigitMoney(fxd15y)));
        planAnnuityMoney.setFxd20Y(String.valueOf(MoneyUtil.toDigitMoney(fxd20y)));
        planAnnuityMoney.setFxd25Y(String.valueOf(MoneyUtil.toDigitMoney(fxd25y)));
        planAnnuityMoney.setFxd30Y(String.valueOf(MoneyUtil.toDigitMoney(fxd30y)));

        info.planAnnuityMoney = planAnnuityMoney;

        if(info.annuityType.contains("종신 10년")) {
            info.annuityPremium = planAnnuityMoney.getWhl10Y();

        } else if(info.annuityType.contains("종신 20년")) {
            info.annuityPremium = planAnnuityMoney.getWhl20Y();

        } else if(info.annuityType.contains("종신 30년")) {
            info.annuityPremium = planAnnuityMoney.getWhl30Y();

        } else if (info.annuityType.contains("종신 100세")) {
            info.annuityPremium = planAnnuityMoney.getWhl100A();

        } else if(info.annuityType.contains("확정 10년")) {
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd10Y();

        } else if(info.annuityType.contains("확정 15년")) {
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd15Y();

        } else if(info.annuityType.contains("확정 20년")) {
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd20Y();

        } else if(info.annuityType.contains("확정 25년")) {
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd25Y();

        } else if(info.annuityType.contains("확정 30년")) {
            info.fixedAnnuityPremium = planAnnuityMoney.getFxd30Y();

        } else {
            logger.info("{} 을 찾을 수 없습니다.", info.annuityType);
            throw new Exception();
        }

        logger.info("info.annuityPremium :: {}", info.annuityPremium);
        logger.info("info.fixedAnnuityPremium :: {}", info.fixedAnnuityPremium);
        logger.info("|---보증--------------------");
        logger.info("|-- 10년 보증 :: {}", planAnnuityMoney.getWhl10Y());
        logger.info("|-- 20년 보증 :: {}", planAnnuityMoney.getWhl20Y());
        logger.info("|-- 30년 보증 :: {}", planAnnuityMoney.getWhl30Y());
        logger.info("|-- 100세 보증 :: {}", planAnnuityMoney.getWhl100A());
        logger.info("|---확정--------------------");
        logger.info("|-- 10년 확정 :: {}", planAnnuityMoney.getFxd10Y());
        logger.info("|-- 15년 확정 :: {}", planAnnuityMoney.getFxd15Y());
        logger.info("|-- 20년 확정 :: {}", planAnnuityMoney.getFxd20Y());
        logger.info("|-- 25년 확정 :: {}", planAnnuityMoney.getFxd25Y());
        logger.info("|-- 30년 확정 :: {}", planAnnuityMoney.getFxd30Y());
        logger.info("--------------------------");
    }
}
