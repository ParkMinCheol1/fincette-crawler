package com.welgram.crawler.direct.life.bpl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.crawler.direct.life.CrawlingBPL;
import com.welgram.crawler.general.CrawlingOption;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;



public class BPL_ACD_D003 extends CrawlingBPL implements Scrapable {

    public static void main(String[] args) {
        executeCommand(new BPL_ACD_D003(), args);
    }



    @Override
    protected void configCrawlingOption(CrawlingOption option) {
//		option.setImageLoad(true);
    }



    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {

        crawlFromAnnounce(info);

        return true;
    }



    private boolean crawlFromAnnounce(CrawlingProduct info) throws Exception {

        boolean result = true;

        driver.findElement(By.cssSelector(".tabRadio_Subtab > li:nth-child(2) > label")).click();

        loading();

        logger.info("상품 선택: {}", info.productNamePublic);
        driver.findElement(By.name(info.productNamePublic)).click();
        loading();

        logger.info("생년월일 입력: {}", info.fullBirth);
        this.setBirthdayNew(info.fullBirth);
        loading();

        logger.info("성별 설정");
        setGenderNew(info.gender);
        loading();

        logger.info("가입금액 비교");
        this.compareAssureMoneyNew(info.assureMoney);

        logger.info("보험기간 설정");
        this.setInsTermNew(info.insTerm);

        logger.info("납입기간 비교");
        this.compareNapTermNew(info.insTerm, info.napTerm);

        logger.info("납입주기 설정");
        setNapCycleNew(info.getNapCycleName());

        logger.info("보험료 계산하기 클릭");
        element = driver.findElement(By.linkText("보험료 계산하기"));
        waitElementToBeClickable(element).click();
        loading();

        logger.info("보험료 크롤링");
        WebElement element = driver.findElement(By.xpath("//*[@id='A_result1']/tr/td"));
        String premium = element.getText().replaceAll("[^0-9]", "");
        if ("0".equals(premium)) {
            throw new Exception("보험료는 0원일 수 없습니다.");
        } else {
            info.treatyList.get(0).monthlyPremium = premium;
        }
        WaitUtil.waitFor(1);

        logger.info("스크린샷찍기");
        moveToElementByJavascriptExecutor(element);
        takeScreenShot(info);
        WaitUtil.waitFor(1);

        logger.info("해약환급금 크롤링");
        this.crawlReturnMoneyListNew(info);

        return result;
    }



    public void setBirthdayNew(Object obj) throws SetBirthdayException {

        String title = "생년월일";
        String welgramBirth = (String) obj;

        try {
            //생년월일 입력
            WebElement input = driver.findElement(By.id("P_HEALTH_E_MOST_jumin_no1"));
            waitElementToBeClickable(input).click();
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
        String welgramGenderText = (welgramGender == MALE) ? "male" : "female";

        try {
            //성별 입력
            WebElement genderElement = driver.findElement(By.xpath("//input[@id='P_HEALTH_E_MOST_" + welgramGenderText + "']/following-sibling::label"));
            waitElementToBeClickable(genderElement).click();
            loading();

        } catch (Exception e) {
            throw new SetGenderException(e.getMessage());
        }
    }

    private void compareAssureMoneyNew(Object obj) throws Exception {
        String title = "가입금액";
        String welgramAssureMoney = (String) obj;

        String targetAssureMoneyText = driver.findElement(By.xpath("//div[@name='bohmFee']/p")).getText();
        String targetAssureMoney = targetAssureMoneyText.replaceAll("[^0-9]", "").replaceAll(",","");
        String unitText = targetAssureMoneyText.replaceAll("[0-9]", "").replaceAll(",","");

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

        targetAssureMoney = String.valueOf(Integer.parseInt(targetAssureMoney) * unit);

        printAndCompare(title, welgramAssureMoney, targetAssureMoney);
    }



    public void setInsTermNew(Object obj) throws SetInsTermException {

        String title = "보험기간";
        String welgramInsTerm = (String) obj;
        welgramInsTerm = welgramInsTerm + "만기";

        try {
            //보험기간 클릭
            WebElement select = driver.findElement(By.xpath("//*[@id='P_HEALTH_E_MOST_insPeriod']"));
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



    private void compareNapTermNew(Object obj, Object obj2) throws Exception {

        String title = "납입기간";
        String welgramInsTerm = (String) obj;
        String welgramNapTerm = (String) obj2;
        String subWelgramNapTerm;
        String targetNapTerm = driver.findElement(By.xpath("//*[@id='P_HEALTH_E_MOST_payPeriod']")).getText();

        if (welgramNapTerm.equals("일시납")) {
            subWelgramNapTerm = welgramNapTerm;
        } else {
            subWelgramNapTerm = (welgramInsTerm.equals(welgramNapTerm)) ? "전기납" : welgramNapTerm + "납";
        }

        printAndCompare(title, subWelgramNapTerm, targetNapTerm);
    }



    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {

        CrawlingProduct info = (CrawlingProduct) obj;

        List<WebElement> $trList = driver.findElements(By.xpath("//tbody[@id='A_result5_2']/tr"));
        for(WebElement $tr : $trList) {
            String term = $tr.findElement(By.xpath("./td[1]")).getText();
            String premiumSum = $tr.findElement(By.xpath("./td[2]")).getText();
            String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText();
            String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();
            returnMoney = String.valueOf(MoneyUtil.toDigitMoney(returnMoney));

            logger.info("경과기간 : {}", term);
            logger.info("납입보험료 : {}", premiumSum);
            logger.info("공시환급금 : {}", returnMoney);
            logger.info("공시환급률 : {}", returnRate);
            logger.info("==========================================");

            PlanReturnMoney p = new PlanReturnMoney();
            p.setTerm(term);
            p.setPremiumSum(premiumSum);
            p.setReturnMoney(returnMoney);
            p.setReturnRate(returnRate);

            //만기환급금 세팅
            info.returnPremium = returnMoney;
            info.planReturnMoneyList.add(p);
        }

        logger.info("만기환급금 : {}", info.returnPremium);
    }
}
