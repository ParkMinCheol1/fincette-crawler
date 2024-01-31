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
import com.welgram.crawler.general.*;

import com.welgram.crawler.scraper.Scrapable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class KYO_CCR_D002 extends CrawlingKYO implements Scrapable{
    

    public static void main(String[] args) {
        executeCommand(new KYO_CCR_D002(), args);
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


        logger.info("보험종류 설정");
        this.setPlanType(info.planSubName);
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(1);


        logger.info("보험료 설정");
        this.setAssureMoneyNew(info.assureMoney);
        waitAnnouncePageLoadingBar();
        WaitUtil.waitFor(1);


        logger.info("보험기간 설정");
        this.setInsTermNew(info.insTerm);


        logger.info("납입기간 설정");
        this.setNapTermNew(info.insTerm, info.napTerm);


        logger.info("납입주기 설정");
        this.setNapCycleNew(info.getNapCycleName());


        logger.info("특약선택 버튼 클릭");
        element = driver.findElement(By.xpath("//*[@id=\"tabsld_calc\"]/div/ul/li[1]/div/div[2]/article[3]/div/div[1]/button"));
        waitElementToBeClickable(element).click();
        WaitUtil.waitFor(1);
        waitAnnouncePageLoadingBar();


        logger.info("특약 설정 및 비교");
        this.setTreaties(info);


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
        welgramInsTerm = welgramInsTerm + "만기";

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

    public void setNapTermNew(Object obj, Object obj2) throws SetNapTermException {
        String title = "납입기간";
        String welgramInsTerm = (String) obj;
        String welgramNapTerm = (String) obj2;
        welgramNapTerm = (welgramInsTerm.equals(welgramNapTerm)) ? "전기납" : welgramNapTerm + "납";


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

    protected void setTreaties(CrawlingProduct info) throws Exception {

        List<CrawlingTreaty> welgramTreaties = info.treatyList;
        CrawlingTreaty specialTreaty = null;

        for(CrawlingTreaty welgramTreaty : welgramTreaties) {

            //매번 가입금액이 바뀌는 특약에 대해서는 가설에서 0만원 특약으로 넘어온다.
            if(welgramTreaty.assureMoney == 0) {
                specialTreaty = welgramTreaty;
            }

            //선택특약일 경우에만 특약 세팅
            if(welgramTreaty.productGubun.equals(CrawlingTreaty.ProductGubun.선택특약)) {
                String treatyName = welgramTreaty.treatyName;
                int treatyAssureMoney = welgramTreaty.assureMoney;
                String treatyInsTerm = welgramTreaty.insTerm;
                String treatyNapTerm = welgramTreaty.napTerm;
                String unitText = "";
                String toSetAssureMoney = "";


                WebElement $tr = driver.findElement(By.xpath("//span[text()='" + treatyName +"']/ancestor::ul[@class='ut-accd slist']"));
                WebElement $joinInput = $tr.findElement(By.xpath(".//div[@class='hbox']//input"));
                WebElement $insTerm = $tr.findElement(By.xpath(".//select[@name='pdtScnCd_isPd']"));
                WebElement $napTerm = $tr.findElement(By.xpath(".//select[@name='pdtScnCd_paPd']"));
                WebElement $assureMoneyInput = $tr.findElement(By.xpath(".//input[@name='pdtScnCd_sbcAmt']"));
                unitText = $assureMoneyInput.findElement(By.xpath("./following-sibling::i")).getText();


                //가입금액 단위 설정
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
                toSetAssureMoney = String.valueOf(treatyAssureMoney / unit);


                //가입 체크박스 선택
                if(!$joinInput.isSelected()) {
//					WebElement label = $joinInput.findElement(By.xpath("./parent::label"));
//					waitElementToBeClickable(label).click();
//					waitElementToBeClickable($joinInput).click();
                    String script = "arguments[0].click();";
                    executeJavascript(script, $joinInput);
                }

                //보험기간 선택
                String toSetInsTerm = treatyInsTerm + "만기";
                selectOptionByText($insTerm, toSetInsTerm);


                //납입기간 선택
                String toSetNapTerm = "";
                try {
                    //특약의 보험기간과 납입기간이 같으면 납입기간을 "전기납"으로 치환.
                    toSetNapTerm = (treatyInsTerm.equals(treatyNapTerm)) ? "전기납" : treatyNapTerm + "납";
                    selectOptionByText($napTerm, toSetNapTerm);
                } catch(Exception e) {
                    //특약별로 보험기간과 납입기간이 같은경우 전기납으로 표기된 경우도 있고 아닌 경우도 있음.
                    //전기납으로 납입기간을 못찾은 경우는 그냥 납입기간으로 찾아보기
                    toSetNapTerm = treatyNapTerm.contains("납") ? treatyNapTerm : treatyNapTerm + "납";
                    selectOptionByText($napTerm, toSetNapTerm);
                }

                //가입금액 설정
                setTextToInputBox($assureMoneyInput, toSetAssureMoney);

            }

        }


        //현재 원수사에서 가입처리된 특약 정보만 크롤링
        List<CrawlingTreaty> targetTreaties = new ArrayList<>();
        CrawlingTreaty targetTreaty = new CrawlingTreaty();


        //주계약만 따로 저장
        WebElement input = driver.findElement(By.xpath("//div[@class='splist']/ul/li[2]/div/div/span/input"));
        String unit = input.findElement(By.xpath("./following-sibling::i")).getText();

        String script = "return $(arguments[0]).val();";
        String targetAssureMoney = String.valueOf(executeJavascript(script, input));

        String assureMoney = targetAssureMoney.concat(unit);

        targetTreaty.treatyName = "주계약";
        targetTreaty.assureMoney = Integer.parseInt(String.valueOf(MoneyUtil.toDigitMoney(assureMoney)));
        targetTreaties.add(targetTreaty);

        List<WebElement> $divList = (List<WebElement>) executeJavascript("return $('div[class=opts]:visible')");
        for(WebElement $div : $divList) {
            WebElement $targetTreatyName = $div.findElement(By.xpath("./parent::div/preceding-sibling::div/label/span"));
            WebElement $targetTreatyAssureMoney = null;
            String targetTreatyName = "";
            String targetTreatyAssureMoney = "";

            try {
                $targetTreatyAssureMoney = $div.findElement(By.xpath("./ul/li[3]/div/div/span/input"));
                unit = $targetTreatyAssureMoney.findElement(By.xpath("./following-sibling::i")).getText();

                script = "return $(arguments[0]).val();";
                targetTreatyName = $targetTreatyName.getText();
                targetTreatyAssureMoney = String.valueOf(executeJavascript(script, $targetTreatyAssureMoney));
                targetTreatyAssureMoney = targetTreatyAssureMoney.concat(unit);
            } catch (Exception e) {
                $targetTreatyAssureMoney = $div.findElement(By.xpath("./ul/li[3]/div/div/span"));
                unit = $targetTreatyAssureMoney.findElement(By.xpath("./i")).getText();

                script = "return $(arguments[0]).text();";
                targetTreatyName = $targetTreatyName.getText();
                targetTreatyAssureMoney = String.valueOf(executeJavascript(script, $targetTreatyAssureMoney));
            }

            targetTreaty = new CrawlingTreaty();
            targetTreaty.treatyName = targetTreatyName;
            targetTreaty.assureMoney = Integer.parseInt(String.valueOf(MoneyUtil.toDigitMoney(targetTreatyAssureMoney)));

            logger.info("특약명 확인 : {}", targetTreaty.treatyName);
            logger.info("가입금액 확인 : {}", targetTreaty.assureMoney);

            targetTreaties.add(targetTreaty);
        }

        //가입설계 특약정보와 원수사 특약정보 비교
        logger.info("가입하는 특약은 총 {}개입니다.", targetTreaties.size());

        boolean result = compareTreaties(targetTreaties, welgramTreaties);

        if(result) {
            logger.info("특약 정보 모두 일치 ^^");
        } else {
            throw new Exception("특약 불일치");
        }


        logger.info("보험료 확인 버튼 클릭");
        element = driver.findElement(By.xpath("//div[@class='btn-set btsend']/button[text()='보험료 확인']"));
        waitElementToBeClickable(element).click();
        waitAnnouncePageLoadingBar();

    }

    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj;

        List<WebElement> $trList = driver.findElements(By.xpath("//tbody[@id='trmRview_0']/tr"));
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

