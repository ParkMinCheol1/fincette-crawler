package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.direct.life.CrawlingKBL;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public class KBL_DTL_F001 extends CrawlingKBL {



    public static void main(String[] args) {

        executeCommand(new KBL_DTL_F001(), args);

    }

    @Override
    protected boolean scrap(CrawlingProduct info) throws Exception {
        WaitUtil.loading(2);
        logger.info("로딩 기다리기");
        helper.waitForCSSElement("div.loading");

        logger.info("생년월일");
        helper.sendKeys3_check(By.id("birthday"), info.fullBirth);

        logger.info("성별 :: ",info.gender == 0 ? "남자" : "여자");
        selectGender_front(info.gender);

        logger.info("보험료 계산하기");
        helper.click(By.id("calculateResult"));

        logger.info("로딩 기다리기");
        helper.waitForCSSElement("div.loading");

        try {
            logger.info("특정 나이에 오류로 가입금액 입력 전 시스템 알럿 발생");
            driver.findElement(By.xpath("//*[@id='systemAlert1']//button[@class='btn-4x btn-yellow modal-close']")).click();
        } catch (Exception e) {
            logger.info("시스템 알럿 없음");
        }

        logger.info("납입주기 :: {}", info.napCycle);
        helper.click(By.id("selectPaymentMethod"));
        WaitUtil.waitFor(1);
        String napCycle = info.napCycle == "01" ? "월납" : "연납";
        driver.findElement(By.xpath("//*[@id='paymentMethods']//a[text()='"+napCycle+"']")).click();


//        logger.info("보험기간 :: {}", info.insTerm);
//        WaitUtil.waitFor(1);
//        selectDropDown_front(By.id("insuranceTermsWrap"), info.insTerm.replace("년", "").replace("세", ""));
//
//        logger.info("납입기간 :: {}", info.napTerm);
//        selectDropDown_front(By.id("paymentTermsWrap"), info.napTerm.replace("년", "").replace("세", ""));

        logger.info("가입금액 :: {}", info.assureMoney);
        WaitUtil.waitFor(2);
        inputAssureMoney(info);

        logger.info("보험료 계산하기");
        WebElement element = driver.findElement(By.xpath("//button[@class='btn-reset']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", element);

        logger.info("로딩 기다리기");
        helper.waitForCSSElement("div.loading");

        if (alert("#systemAlert1")) {
            throw new Exception("납입보험료가 최저보험료 한도에 적합하지 않습니다.");
        }

        String monthlyPremium = driver.findElement(By.xpath("//*[@id='insurancePlanCards']/div[1]/div[1]/dl[1]/dd/span")).getText().replaceAll("[^0-9]", "");;
        info.treatyList.get(0).monthlyPremium = monthlyPremium;
        logger.info("월 보험료 :: {}", monthlyPremium);

        logger.info("스크린샷");
        takeScreenShot(info);

        logger.info("해약환급금");
        String[] returnMoneyPageList = new String[]{"3","4","5","6"};
        String moneyUtilLocation = "62";
        getViewerReturnPremium(info, returnMoneyPageList, moneyUtilLocation);

        return true;
    }


    protected void getViewerReturnPremium(CrawlingProduct info, String[] returnMoneyPageList, String moneyUtilLocation) throws Exception {
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

        logger.info("[보장내역 보기] 버튼 선택");
        moveToElement(By.id("buttonResultDocumentView"));
        helper.click(By.id("buttonResultDocumentView"));
        helper.waitForCSSElement("div.loading");
        WaitUtil.waitFor(3);

        logger.info("상품설명서 창 전환");
        ArrayList<String> tab = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tab.get(1));

        logger.info("해약환급금 페이지 조회");
        ArrayList arrayList = confirmReturnPremium(returnMoneyPageList);

        for(int i = 0 ; i < arrayList.size(); i++){
            logger.info("해약환급금 조회");

            driver.findElement(By.id("crownix-toolbar-move")).click();
            WaitUtil.waitFor(1);

            setTextToInputBox(By.cssSelector(".aTextbox"), String.valueOf(arrayList.get(i)));
            driver.findElement(By.xpath("//button[text()='확인']")).click();
            WaitUtil.waitFor(2);

            String moneyUnit = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div["+moneyUtilLocation+"]")).getText();
            int unitStart = moneyUnit.indexOf(":");
            int unitEnd = moneyUnit.indexOf(")");
            String unit = moneyUnit.substring(unitStart+1, unitEnd).replace(" ", "");

            List<WebElement> elements = driver.findElements(By.xpath("//*[@id='m2soft-crownix-text']//div"));
            int idx = 0;
            for(int j = 0; j < elements.size(); j++){
                try{
                    WebElement div = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+j+"]"));
                    if(div.getText().contains("B/A")){
                        idx = j + 1;
                        break;
                    }
                } catch (NoSuchElementException e){

                }
            }

            boolean isEnd = false;
            while(!isEnd){
                try{
                    moveToElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+idx+"]"));
                    String term = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+idx+"]")).getText();
                    String premiumSum = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+1)+"]")).getText();
                    String returnMoney = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+2) + ")")).getText();
                    String returnRate = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+3) + ")")).getText();

                    if(term.length() > 4) {
                        throw new NoSuchElementException("경과기간에 해당하는 div가 아닙니다.");
                    }

                    logger.info("================================");
                    logger.info("경과기간 : {}", term);
                    logger.info("납입보험료 : {}", premiumSum);
                    logger.info("해약환급금 : {}", returnMoney);
                    logger.info("환급률 : {}", returnRate);

                    PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                    planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                    planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                    planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                    planReturnMoney.setTerm(term);
                    planReturnMoney.setPremiumSum(String.valueOf(MoneyUtil.toDigitMoney(premiumSum+unit)));
                    planReturnMoney.setReturnMoney(String.valueOf(MoneyUtil.toDigitMoney(returnMoney+unit)));
                    planReturnMoney.setReturnRate(returnRate);

                    planReturnMoneyList.add(planReturnMoney);

                    info.returnPremium = planReturnMoney.getReturnMoney();

                    idx += 4;
                } catch(NoSuchElementException e) {
                    isEnd = true;
                }
            }

            info.setPlanReturnMoneyList(planReturnMoneyList);

            logger.info("만기환급급 :: {}원", info.returnPremium);
        }
    }
}


