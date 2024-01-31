package com.welgram.crawler.direct.life.kyo;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.direct.life.CrawlingKYO;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;

import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class KYO_WLF_D001 extends CrawlingKYO implements Scrapable{
    

    public static void main(String[] args) {
        executeCommand(new KYO_WLF_D001(), args);
    }

    @Override
    protected void configCrawlingOption(CrawlingOption option) {
//		option.setImageLoad(true);
    }


    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
//		doCrawlInsurancePublic(info);
        crawlFromAnnounce(info);
        return true;
    }


    private boolean crawlFromAnnounce(CrawlingProduct info) throws Exception {
        boolean result = true;

        logger.info("공시실 진입 후 다이렉트보험 버튼 클릭");
        element = driver.findElement(By.linkText("다이렉트보험"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(2);


        logger.info("상품명 : {} 클릭", info.productNamePublic);
        element = driver.findElement(By.xpath("//td[text()='" + info.productNamePublic + "']/parent::tr//button"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(3);


        logger.info("생년월일 설정");
        this.setBirthdayNew(info.fullBirth);


        logger.info("성별 설정");
        this.setGenderNew(info.gender);


        logger.info("내 보험료 확인하기 버튼 클릭");
        element = driver.findElement(By.xpath("//button[text()='내 보험료 확인하기']"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(3);


        logger.info("내가 직접 계산하기 버튼 클릭");
        element = driver.findElement(By.xpath("//button[text()='내가 직접 계산하기']"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(3);


        logger.info("흡연여부 설정");
        this.setSmoke(info.planSubName);
        WaitUtil.waitFor(1);


        logger.info("보험종류 설정");
        this.setPlanType(info.planSubName);
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(1);


        logger.info("보험료 설정");
        this.setAssureMoneyNew(info.assureMoney);
        waitAnnouncePageLoadingBar();


        logger.info("보험기간 설정");
        this.setInsTermNew(info.insTerm);


        logger.info("납입기간 설정");
        this.setNapTermNew(info.napTerm);


        logger.info("납입주기 설정");
        this.setNapCycleNew(info.getNapCycleName());


        logger.info("보험료 계산 다시하기 버튼 클릭");
        element = driver.findElement(By.xpath("//button[text()='보험료 계산 다시하기']"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(1);
        waitAnnouncePageLoadingBar();


        logger.info("보험료 결과 자세히 보기 버튼 클릭");
        element = driver.findElement(By.xpath("//button[text()='보험료 결과 자세히 보기']"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(3);
        waitAnnouncePageLoadingBar();


        logger.info("보험료 크롤링");
        WebElement element = driver.findElement(By.xpath("//*[@id=\"pop-calcrest\"]/div/div[2]/main/div/section[2]/div[1]/div[2]/div/div[2]/div/em"));
        String premium = element.getText().replaceAll("[^0-9]", "");
        if("0".equals(premium)) {
            throw new Exception("보험료는 0원일 수 없습니다.");
        } else {
            info.treatyList.get(0).monthlyPremium = premium;
        }
        WaitUtil.waitFor(1);


        logger.info("스크린샷찍기");
        moveToElementByJavascriptExecutor(element);
        takeScreenShot(info);
        WaitUtil.waitFor(1);


        logger.info("해약환급금 버튼 클릭");
        element = driver.findElement(By.xpath("//div[@class='spbots']//button[@onclick='oPopTrmRfnd(0);']"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(2);


        logger.info("해약환급금 크롤링");
        WaitUtil.waitFor(2);
        this.crawlReturnMoneyListNew(info);


        return result;
    }


    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        String title = "생년월일";
        String welgramBirth = (String) obj;

        try {
            //생년월일 입력
            WebElement input = driver.findElement(By.id("input_id0"));
            WebElement label = driver.findElement(By.xpath("//label[@for='" + input.getAttribute("id") + "']"));
            waitElementToBeClickable(label).click();
            setTextToInputBox(input, welgramBirth);

            //실제로 입력된 생년월일 읽어오기
            String script = "return $(arguments[0]).val();";
            String targetBirth = String.valueOf(executeJavascript(script, input));

            //비교
            printAndCompare(title, welgramBirth, targetBirth);
        } catch (Exception e) {
            throw new SetBirthdayException(e.getMessage());
        }

    }


    public void setGenderNew(Object obj) throws SetGenderException {
        String title = "성별";
        int welgramGender = (int) obj;
        String welgramGenderText = welgramGender == MALE ? "남성" : "여성";


        try {
            //성별 입력
            WebElement label = driver.findElement(By.xpath("//li[@class='gender']//span[text()='" + welgramGenderText + "']/parent::label"));
            waitElementToBeClickable(label).click();
            waitAnnouncePageLoadingBar();


            //실제로 클릭된 성별 읽어오기
            String script = "return $('input[name=\"sdt\"]:checked').attr('id');";
            String checkedGenderId = String.valueOf(executeJavascript(script));
            String targetGender = driver.findElement(By.xpath("//label[@for='" + checkedGenderId + "']")).getText().trim();

            //비교
            printAndCompare(title, welgramGenderText, targetGender);

        } catch (Exception e) {
            throw new SetGenderException(e.getMessage());
        }
    }


    private void setSmoke(Object obj) throws Exception {
        String title = "흡연여부 설정";
        String welgramSmoke = (String) obj;
        int idx = welgramSmoke.indexOf(",");
        welgramSmoke = welgramSmoke.substring(0, idx);
//        welgramSmoke = welgramSmoke.equals("일반") ? "표준체" : "비흡연체";

        //보험종류 클릭
        WebElement select = driver.findElement(By.id("htSsCd"));
        selectOptionByText(select, welgramSmoke);


        //실제로 클릭된 보험종류 읽어오기
        String script = "return $(arguments[0]).find('option:selected').text();";
        String targetPlanType = String.valueOf(executeJavascript(script, select));

        //비교
        printAndCompare(title, welgramSmoke, targetPlanType);
    }


    private void setPlanType(Object obj) throws Exception {
        String title = "보험종류";
        String welgramPlanType = (String) obj;
        int idx = welgramPlanType.indexOf(",");
        welgramPlanType = welgramPlanType.substring(idx+1);

        //보험종류 클릭
        WebElement select = driver.findElement(By.id("gdclCd"));
        selectOptionByText(select, welgramPlanType);


        //실제로 클릭된 보험종류 읽어오기
        String script = "return $(arguments[0]).find('option:selected').text();";
        String targetPlanType = String.valueOf(executeJavascript(script, select));

        //비교
        printAndCompare(title, welgramPlanType, targetPlanType);

    }

    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {
        String title = "가입금액";
        String welgramAssureMoney = (String) obj;
        String toSetAssureMoney = "";

        try {
            //가입금액 입력
            WebElement input = driver.findElement(By.xpath("//input[@name='pdtScnCd_sbcAmt']"));
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
            input.sendKeys(Keys.chord(Keys.ENTER));

            //실제로 입력된 가입금액 읽어오기
            String script = "return $(arguments[0]).val();";
            String targetAssureMoney = String.valueOf(executeJavascript(script, input));

            //비교
            printAndCompare(title, toSetAssureMoney, targetAssureMoney);

        } catch (Exception e) {
            throw new SetAssureMoneyException(e.getMessage());
        }
    }

    public void setInsTermNew(Object obj) throws SetInsTermException {
        String title = "보험기간";
        String welgramInsTerm = (String) obj;
        welgramInsTerm = welgramInsTerm.replace("보장", "");

        try {

            //보험기간 클릭
            WebElement select = driver.findElement(By.xpath("//span[@class='select def no-lb']//select[@name='pdtScnCd_isPd']"));
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

    public void setNapTermNew(Object obj) throws SetNapTermException {
        String title = "납입기간";
        String welgramNapTerm = (String) obj;
        welgramNapTerm = (welgramNapTerm.contains("납")) ? welgramNapTerm : welgramNapTerm + "납";


        try {

            //납입기간 클릭
            WebElement select = driver.findElement(By.xpath("//span[@class='select def no-lb']//select[@name='pdtScnCd_paPd']"));
            selectOptionByText(select, welgramNapTerm);


            //실제로 클릭된 납입기간 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetNapTerm = String.valueOf(executeJavascript(script, select));

            //비교
            printAndCompare(title, welgramNapTerm, targetNapTerm);

        } catch (Exception e) {
            throw new SetNapTermException(e.getMessage());
        }
    }

    public void setNapCycleNew(Object obj) throws SetNapCycleException {
        String title = "납입주기";
        String welgramNapCycle = (String) obj;

        try {

            //납입주기 클릭
            WebElement select = driver.findElement(By.xpath("//span[@class='select def no-lb']//select[@name='pdtScnCd_paCyc']"));
            selectOptionByText(select, welgramNapCycle);


            //실제로 클릭된 납입주기 읽어오기
            String script = "return $(arguments[0]).find('option:selected').text();";
            String targetNapCycle = String.valueOf(executeJavascript(script, select));

            //비교
            printAndCompare(title, welgramNapCycle, targetNapCycle);

        } catch (Exception e) {
            throw new SetNapCycleException(e.getMessage());
        }
    }

    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj;

        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        List<WebElement> $trList = driver.findElements(By.xpath("//tbody[@id='trmRview_0']/tr"));
        for(WebElement $tr : $trList) {
            String term = $tr.findElement(By.xpath("./td[1]")).getText();
            String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText();
            String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText();
            String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();
            String returnMoneyAvg = $tr.findElement(By.xpath("./td[5]")).getText();
            String returnRateAvg = $tr.findElement(By.xpath("./td[6]")).getText();
            String returnMoneyMin = $tr.findElement(By.xpath("./td[7]")).getText();
            String returnRateMin = $tr.findElement(By.xpath("./td[8]")).getText();
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
            planReturnMoneyList.add(p);
        }

        // 순수보장형 만기환급금 0
        info.returnPremium = 0+"";
        logger.info(info.napTerm + " 납 해약환급금 :: " + info.returnPremium);

        info.setPlanReturnMoneyList(planReturnMoneyList);
    }

}