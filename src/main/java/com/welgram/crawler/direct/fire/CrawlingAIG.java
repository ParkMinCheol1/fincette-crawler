package com.welgram.crawler.direct.fire;

import com.welgram.common.DateUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.NotFoundTreatyException;
import com.welgram.common.except.PlanTypeMismatchException;
import com.welgram.common.except.TreatyMisMatchException;
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
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.crawler.scraper.Scrapable;
import com.welgram.util.InsuranceUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public abstract class CrawlingAIG extends SeleniumCrawler implements Scrapable {

    @Override
    public void setBirthdayNew(Object obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedFullBirth = (String) obj;

        try {

            //생년월일 설정
            WebElement $birthInput = driver.findElement(By.id("brdt"));
            String actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

            //비교
            printLogAndCompare(title, expectedFullBirth, actualFullBirth);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    @Override
    public void setGenderNew(Object obj) throws SetGenderException {
        String title = "성별";

        int gender = (int) obj;
        String expectedGenderText = (gender == MALE) ? "남" : "여";
        String script = "return $('input[name=sexClcd]:checked').attr('id');";

        try {

            //성별 설정
            WebElement $genderLabel = driver.findElement(By.xpath("//label[text()='" + expectedGenderText + "']"));
            helper.waitElementToBeClickable($genderLabel).click();

            //실제 선택된 성별 text 읽어오기
            String actualGenderId = String.valueOf(helper.executeJavascript(script, expectedGenderText));
            String actualGenderText = driver.findElement(By.xpath("//label[@for='" + actualGenderId + "']")).getText();

            //비교
            printLogAndCompare(title, expectedGenderText, actualGenderText);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }

    }

    @Override
    public void setJobNew(Object obj) throws SetJobException {

    }

    @Override
    public void setInsTermNew(Object obj) throws SetInsTermException {

    }

    @Override
    public void setNapTermNew(Object obj) throws SetNapTermException {

    }

    @Override
    public void setNapCycleNew(Object obj) throws SetNapCycleException {
        String title = "납입방법";
        String expectedNapCycle = (String)obj;

        try {

            //납입방법 설정
            WebElement $napCycleSelect = driver.findElement(By.id("paymentMethod"));
            String actualNapCycle = helper.selectByText_check($napCycleSelect, expectedNapCycle);

            //비교
            printLogAndCompare(title, expectedNapCycle, actualNapCycle);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setRenewTypeNew(Object obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoneyNew(Object obj) throws SetAssureMoneyException {

    }

    @Override
    public void setRefundTypeNew(Object obj) throws SetRefundTypeException {

    }

    @Override
    public void crawlPremiumNew(Object obj) throws PremiumCrawlerException {
        CrawlingTreaty mainTreaty = (CrawlingTreaty) obj;

        String premium = driver.findElement(By.xpath("//p[@class='price']")).getText();
        premium = premium.replaceAll("[^0-9]", "");
        mainTreaty.monthlyPremium = premium;

        logger.info("보험료 : {}원", mainTreaty.monthlyPremium);

        if(StringUtils.isEmpty(mainTreaty.monthlyPremium)) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PREMIUM;
            throw new PremiumCrawlerException(exceptionEnum.getMsg());
        }

    }

    @Override
    public void crawlReturnMoneyListNew(Object obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj;

        try {
            logger.info("해약환급금 확인 버튼 클릭");
            element = driver.findElement(By.id("btnCancellationRefundAmount"));
            waitElementToBeClickable(element).click();


            logger.info("해약환급금 창으로 전환");
            currentHandle = driver.getWindowHandle();
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));
            helper.switchToWindow(currentHandle, driver.getWindowHandles(), true);


            //해약환금금 표시 단위 크롤링
            String unitText = waitVisibilityOfElementLocated(By.xpath("//p[@class='txtPara textR']")).getText();
            int startIdx = unitText.indexOf(":");
            int endIdx = unitText.lastIndexOf(",");
            unitText = unitText.substring(startIdx + 1, endIdx);
            logger.info("해약환급금 표시 단위 : {}", unitText);

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



            logger.info("해약환급금 크롤링 시작");
            List<WebElement> $trList = waitVisibilityOfAllElementsLocatedBy(By.xpath("//tbody[@id='contents']/tr"));
            for(WebElement $tr : $trList) {
                String term = $tr.findElement(By.xpath("./th[1]")).getText();
                String premiumSum = $tr.findElement(By.xpath("./th[2]")).getText().replaceAll("[^0-9]", "");
                String returnMoneyMin = $tr.findElement(By.xpath("./td[1]")).getText().replaceAll("[^0-9]", "");
                String returnRateMin = $tr.findElement(By.xpath("./td[2]")).getText();
                String returnMoney = $tr.findElement(By.xpath("./td[3]")).getText().replaceAll("[^0-9]", "");
                String returnRate = $tr.findElement(By.xpath("./td[4]")).getText();
                premiumSum = String.valueOf(Integer.parseInt(premiumSum) * unit);
                returnMoneyMin = String.valueOf(Integer.parseInt(returnMoneyMin) * unit);
                returnMoney = String.valueOf(Integer.parseInt(returnMoney) * unit);

                logger.info("경과기간 : {}", term);
                logger.info("납입보험료 : {}", premiumSum);
                logger.info("최저환급금 : {}", returnMoneyMin);
                logger.info("최저환급률 : {}", returnRateMin);
                logger.info("공시환급금 : {}", returnMoney);
                logger.info("공시환급률 : {}", returnRate);
                logger.info("==========================================");

                PlanReturnMoney p = new PlanReturnMoney();
                p.setTerm(term);
                p.setPremiumSum(premiumSum);
                p.setReturnMoneyMin(returnMoneyMin);
                p.setReturnRateMin(returnRateMin);
                p.setReturnMoney(returnMoney);
                p.setReturnRate(returnRate);

                info.returnPremium = returnMoney;
                info.planReturnMoneyList.add(p);
            }

            logger.info("만기환급금 : {}원", info.returnPremium);
        } catch(Exception e) {
            throw new ReturnMoneyListCrawlerException(e.getMessage());
        }

    }

    @Override
    public void crawlReturnPremiumNew(Object obj) throws ReturnPremiumCrawlerException {

    }



    public void setAnnounceProductName(String expectedProductName) throws CommonCrawlerException {
        String title = "상품명";

        try {

            //상품명 설정
            WebElement $productNameSelect = driver.findElement(By.id("prodCd"));
            String actualProductName = helper.selectByText_check($productNameSelect, expectedProductName);

            //비교
            printLogAndCompare(title, expectedProductName, actualProductName);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_PRODUCT_NAME;
            throw new CommonCrawlerException(exceptionEnum, e.getCause());
        }

    }

    public void setAnnouncePlanType(String expectedPlanType) throws CommonCrawlerException {
        String title = "판매플랜";

        try {

            //판매플랜 설정
            WebElement $planTypeSelect = driver.findElement(By.id("prodPlanCd"));
            String actualPlanType = helper.selectByText_check($planTypeSelect, expectedPlanType);

            //비교
            printLogAndCompare(title, expectedPlanType, actualPlanType);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_SUB_PLAN;
            throw new CommonCrawlerException(exceptionEnum, e.getCause());
        }

    }


    public void setAnnounceTerms(String expectedInsTerm, String expectedNapTerm) throws CommonCrawlerException {
        String title = "납입/보험기간";
        String expectedTerm = expectedNapTerm + "납 " + expectedInsTerm + "만기";

        try {

            //납입/보험기간 설정
            WebElement $termSelect = driver.findElement(By.id("paymentPeriod"));
            String actualTerm = helper.selectByText_check($termSelect, expectedTerm);

            //비교
            printLogAndCompare(title, expectedTerm, actualTerm);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new CommonCrawlerException(exceptionEnum, e.getCause());
        }

    }




    public void setAndCompareTreaties(List<CrawlingTreaty> welgramTreaties) throws CommonCrawlerException {

        WebElement $tTr = null;
        WebElement $tNameTd = null;
        WebElement $tCheckboxInput = null;
        WebElement $tAssureMoneySelect = null;
        WebElement $tTermSelect = null;
        String tName = "";
        String tInsTerm = "";
        String tNapTerm = "";
        String tAssureMoney = "";
        String script = "";

        try {

            //가입설계를 바탕으로 특약 설정
            for(CrawlingTreaty welgramTreaty : welgramTreaties) {
                tName = welgramTreaty.getTreatyName();                              //특약명
                tInsTerm = welgramTreaty.getInsTerm();                              //특약 보험기간
                tNapTerm = welgramTreaty.getNapTerm();                              //특약 납입기간
                tAssureMoney = String.valueOf(welgramTreaty.getAssureMoney());      //특약 가입금액

                $tNameTd = driver.findElement(By.xpath("//tbody[@id='prodContents']//td[text()='" + tName + "']"));
                $tTr = $tNameTd.findElement(By.xpath("./parent::tr"));
                $tCheckboxInput = $tTr.findElement(By.xpath("./th[1]/input"));
                $tAssureMoneySelect = $tTr.findElement(By.xpath("./td[2]/select"));
                $tTermSelect = $tTr.findElement(By.xpath("./td[3]/select"));

                //특약 체크박스 선택
                if (!$tCheckboxInput.isSelected()) {
                    script = "$(arguments[0]).click();";
                    helper.executeJavascript(script, $tCheckboxInput);
                } else {
                    logger.info("{} 특약은 필수 가입 특약입니다.", tName);
                }

                //특약 가입금액 설정
                helper.selectByValue_check($tAssureMoneySelect, tAssureMoney);

                //특약 납입/보험기간 설정
                String tTerm = tNapTerm + "납 " + tInsTerm + "만기";
                helper.selectByText_check($tTermSelect, tTerm);
            }



            //실제 원수사에 선택된 특약 정보 읽어오기
            List<CrawlingTreaty> targetTreaties = new ArrayList<>();
            List<WebElement> $checkboxList = driver.findElements(By.cssSelector("#prodContents input[name^=coverageCode]:checked"));
            for(WebElement $checkbox : $checkboxList) {
                $tTr = $checkbox.findElement(By.xpath("./ancestor::tr[1]"));
                $tNameTd = $tTr.findElement(By.xpath("./td[1]"));
                $tAssureMoneySelect = $tTr.findElement(By.xpath("./td[2]/select"));
                $tTermSelect = $tTr.findElement(By.xpath("./td[3]/select"));

                //특약명 읽어오기
                tName = $tNameTd.getText();

                //선택된 특약 가입금액 읽어오기
                script = "return $(arguments[0]).find('option:selected').val();";
                tAssureMoney = String.valueOf(helper.executeJavascript(script, $tAssureMoneySelect));

                //선택된 특약 납입/보험기간 읽어오기
                script = "return $(arguments[0]).find('option:selected').text();";
                String tTerm = String.valueOf(helper.executeJavascript(script, $tTermSelect));
                int idx = tTerm.indexOf(" ");
                tNapTerm = tTerm.substring(0, idx);
                tInsTerm = tTerm.substring(idx + 1);


                CrawlingTreaty treaty = new CrawlingTreaty();
                treaty.setTreatyName(tName);
                treaty.setAssureMoney(Integer.parseInt(tAssureMoney));
                treaty.setNapTerm(tNapTerm);
                treaty.setInsTerm(tInsTerm);

                targetTreaties.add(treaty);
            }



            boolean result = compareTreaties(targetTreaties, welgramTreaties);


            if(result) {
                logger.info("특약 정보 모두 일치 ^^");
            } else {
                throw new Exception("특약 불일치");
            }


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_CRAWL_TREATIES;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }



    }


























    //홈페이지용 버튼 클릭 메서드(홈페이지 명시적 대기코드가 추가돼있음)
    protected void homepageBtnClick(By element) throws Exception{
        driver.findElement(element).click();
        waitHomepageLoadingImg();
        WaitUtil.waitFor(2);
    }

    //홈페이지용 버튼 클릭 메서드(홈페이지 명시적 대기코드가 추가돼있음)
    protected void homepageBtnClick(WebElement element) throws Exception{
        element.click();
        waitHomepageLoadingImg();
        WaitUtil.waitFor(2);
    }

    //공시실용 버튼 클릭 메서드(공시실 명시적 대기코드가 추가돼있음)
    protected void announceBtnClick(By element) throws Exception{
        driver.findElement(element).click();
        waitAnnounceLoadingImg();
        WaitUtil.waitFor(2);
    }

    //공시실용 버튼 클릭 메서드(공시실 명시적 대기코드가 추가돼있음)
    protected void announceBtnClick(WebElement element) throws Exception{
        element.click();
        waitAnnounceLoadingImg();
        WaitUtil.waitFor(2);
    }


    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    //inputBox에 텍스트 입력하는 메서드
    protected void setTextToInputBox(WebElement element, String text) {
        element.click();
        element.clear();
        element.sendKeys(text);
    }


    //홈페이지용 보험료 계산 버튼 클릭 메서드
    protected void homepageCalcBtnClick() throws Exception{
        driver.findElement(By.xpath("//div[@id='layerPop_easyPlanDp']//a[text()='보험료 계산']")).click();
        waitHomepageLoadingImg();
    }

    //홈페이지용 직접 설계 버튼 클릭 메서드
    protected void homepageDirectDesignBtnClick() throws Exception{
        homepageBtnClick(By.linkText("직접 설계"));
    }


//    //select box에서 text가 일치하는 option을 클릭하는 메서드
//    protected void selectOption(By element, String text) throws Exception{
//        Select select = new Select(driver.findElement(element));
//        select.selectByVisibleText(text);
//    }
//
//
//    //select box에서 text가 일치하는 option을 클릭하는 메서드
//    protected void selectOption(WebElement selectEl, String text) throws Exception{
//        Select select = new Select(selectEl);
//        select.selectByVisibleText(text);
//    }


    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOption(By element, String text) throws Exception {
        WebElement selectEl =  driver.findElement(element);
        selectOption(selectEl, text);
    }

    //select 박스에서 text로 option 선택하는 메서드
    protected void selectOption(WebElement selectEl, String text) throws Exception {
        Select select = new Select(selectEl);

        try {
            select.selectByVisibleText(text);
        }catch (NoSuchElementException e) {
            throw new NoSuchElementException("selectbox에서 해당 text(" + text + ")를 찾을 수 없습니다");
        }
    }

    //select box에서 text와 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(By element, String text) throws Exception{
//        Select select = new Select(driver.findElement(element));
//        select.selectByVisibleText(text);
        selectOption(element, text);

    }


    //select box에서 text와 일치하는 option 클릭하는 메서드
    protected void selectOptionByText(WebElement element, String text) throws Exception{
//        Select select = new Select(driver.findElement(element));
//        select.selectByVisibleText(text);
        selectOption(element, text);

    }


    //select box에서 value값이 일치하는 option 클릭하는 메서드
    protected void selectOptionByValue(WebElement selectEl, String value) throws Exception{
        Select select = new Select(selectEl);
        select.selectByValue(value);
    }



    //홈페이지용 로딩이미지 명시적 대기
    protected void waitHomepageLoadingImg() throws Exception{
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadBox")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading")));
    }


    //홈페이지용 성별 설정 메서드
    protected void setHomepageGender(int gender) throws Exception{
        String genderTag = (gender == MALE) ? "man" : "woman";
        homepageBtnClick(By.cssSelector("label[for='" + genderTag + "']"));
    }


    //홈페이지용 생년월일 설정 메서드(1개 입력)
    protected void setHomepageBirth(By element, String fullBirth) {
        setTextToInputBox(element, fullBirth);
    }


    //홈페이지용 플랜 설정 메서드
    protected String setHomepagePlan(String planType) {
        String monthlyPremium = "";
        List<WebElement> planList = driver.findElements(By.cssSelector("div.directPlan > div.directPlanSubIn > strong"));

        for(WebElement planEl : planList) {
            String targetPlanType = planEl.getText();

            if(targetPlanType.contains(planType)) {
                WebElement premiumEl = planEl.findElement(By.xpath("parent::div")).findElement(By.cssSelector("div.potBox > p"));
                monthlyPremium = premiumEl.getText().replaceAll("[^0-9]", "");

                return monthlyPremium;
            }
        }

        return monthlyPremium;
    }


    //홈페이지용 납입기간, 보험기간 동시 설정 메서드
    protected void setHomepageTerms(String napTerm, String insTerm) throws Exception{
        logger.info("상품정보 변경 버튼 클릭!");
        helper.waitElementToBeClickable((By.xpath("//button[text()='상품정보 변경']"))).click();
        WaitUtil.waitFor(2);

        String myTerms = napTerm + "납" + insTerm + "만기";

        try {
            helper.waitElementToBeClickable((By.xpath("//label[text()='" + myTerms + "']"))).click();
            WaitUtil.waitFor(2);
            waitHomepageLoadingImg();
        }catch(NoSuchElementException e) {
            throw new Exception("납기,보기(" + myTerms + ")가 존재하지 않습니다.");
        }

        //클릭된 납기,보기가 제대로 클릭된게 맞는지 검사
        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='prdCd']:checked\").attr('id')").toString();
        String checkedTerms = driver.findElement(By.cssSelector("label[for='" + checkedElId + "']")).getText();

        logger.info("클릭된 납기,보기 : {}", checkedTerms);

        if(!checkedTerms.equals(myTerms)) {

            logger.info("납기,보기 불일치");
            logger.info("@@@클릭된 납기,보기 : {}", checkedTerms);
            logger.info("@@@가입설계 납기,보기 : {}", myTerms);

            throw new Exception("납기,보기 불일치. 홈페이지 : " + checkedTerms + ", 가입설계 : " + myTerms);
        }
    }




    //홈페이지용 특약별 가입금액 설정 메서드
    protected void setHomepageSubTreaties(List<CrawlingTreaty> treatyList) throws Exception{
        int myTreatyCnt = treatyList.size();        //내 총 특약 개수
        int targetTreatyCnt = 0;                    //홈페이지에서 체크된 특약 개수

        List<WebElement> trList = driver.findElements(By.cssSelector("#prodContents tr"));

        for(CrawlingTreaty treaty : treatyList) {
            String myTreatyName = treaty.treatyName;
            int myAssureMoney = treaty.assureMoney;

            for(WebElement tr : trList) {
                List<WebElement> tdList = tr.findElements(By.tagName("td"));

                String targetTreatyName = tdList.get(1).findElement(By.tagName("a")).getText();

                if(myTreatyName.equals(targetTreatyName)) {
                    try {
                        WebElement checkBox = tdList.get(0).findElement(By.cssSelector("input[type=checkbox]"));

                        //기본계약일 경우 targetTreatyCnt를 증가시켜준다.
                        if(checkBox.isSelected()) {
                            targetTreatyCnt++;
                        }

                        //체크박스가 존재하며, 체크해제 돼있다면 클릭
                        if(checkBox != null && !checkBox.isSelected()) {
                            checkBox.click();
                            targetTreatyCnt++;
                        }

                        //특약 가입금액 설정
                        String convertedAssureMoney = convertHomepageAssureMoney(myAssureMoney);
                        WebElement selectEl = tdList.get(2).findElement(By.tagName("select"));
                        selectOption(selectEl, convertedAssureMoney);

                        logger.info("특약명 : {}, 가입금액 : {}", myTreatyName, convertedAssureMoney);
                    }catch(NoSuchElementException e){

                    }
                    break;
                }
            }
        }

        logger.info("===================================");
        logger.info("내 특약개수 : {}개", myTreatyCnt);
        logger.info("홈페이지에 체크된 특약개수 : {}개", targetTreatyCnt);
        logger.info("===================================");

        //내 특약개수와 홈페이지에 체크된 특약개수가 다르면 예외처리
        if(myTreatyCnt != targetTreatyCnt) {
            throw new Exception("내 특약개수와 홈페이지에 체크된 특약개수가 일치하지 않습니다. 특약체크를 다시 진행해주세요");
        }
    }

    //홈페이지용 가입금액 변환 메서드
    protected String convertHomepageAssureMoney(int assureMoney) {
        String _assureMoney = String.valueOf(assureMoney);
        int value = Integer.parseInt(_assureMoney.substring(0, _assureMoney.indexOf("0")));
        String unit = "";

        switch (assureMoney / value) {
            case 10000000 :
                unit = "천만원";
                break;
            case 1000000 :
                unit = "백만원";
                break;
            case 100000 :
                unit = "십만원";
                break;
            case 10000 :
                unit = "만원";
                break;
        }

        unit = value + unit;

        return unit;
    }


    //홈페이지용 주계약 보험료 설정 메서드
    protected void setHomepagePremium(CrawlingTreaty mainTreaty, String monthlyPremium) {
        mainTreaty.monthlyPremium = monthlyPremium;
    }

    //홈페이지용 주계약 보험료 설정 메서드
    protected void setHomepagePremium(CrawlingProduct info) {
        String monthlyPremium = driver.findElement(By.xpath("//tr[@id='viewPlanList']//label[contains(., '" + info.planSubName  + "')]//span[@class='psSum']/em")).getText();

        logger.info("월 보험료 : {}원", monthlyPremium);

        info.treatyList.get(0).monthlyPremium = monthlyPremium.replaceAll("[^0-9]", "");
    }


    //홈페이지용 출발일 설정 메서드(여행자 보험의 경우 사용)
    protected void setHomepageDeparture() throws Exception{
        String deparetureTime = "01시";

        //1일 뒤를 출발일로 지정
        Date date = DateUtil.addDay(new Date(), 1);
        String departureDate = new SimpleDateFormat("yyyyMMdd").format(date);

        logger.info("출발일 : {}", departureDate);
        setTextToInputBox(By.id("insStDt"), departureDate);

        logger.info("출발시간 : {}", deparetureTime);
        selectOption(By.id("ctrSttm"), deparetureTime);
    }


    //홈페이지용 도착일 설정 메서드(여행자 보험의 경우 사용)
    protected void setHomepageArrival() throws Exception{
        String arrivalTime = "01시";

        //출발일로부터 7일 뒤를 도착일로 지정
        Date date = DateUtil.addDay(new Date(), 8);
        String arrivalDate = new SimpleDateFormat("yyyyMMdd").format(date);

        logger.info("도착일 : {}", arrivalDate);
        setTextToInputBox(By.id("insEndt"), arrivalDate);

        logger.info("도착시간 : {}", arrivalTime);
        selectOption(By.id("ctrCltm"), arrivalTime);
    }

    // 홈페이지용 스크린샷을 위한 스크롤 메서드
    protected void webscrollbottom(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,document.body.scrollHeight/10);");
    }

    //공시실용 보험료 계산 버튼 클릭 메서드
    protected void announceCalcBtnClick() throws Exception{
        announceBtnClick(By.linkText("계산하기"));
    }



    //공시실용 로딩이미지 명시적 대기
    protected void waitAnnounceLoadingImg() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadBox")));
    }


    //공시실 페이지에서 해당 보험을 찾는다.
    protected void findInsuFromAnnounce(String insuName) throws Exception {
        List<WebElement> thList = driver.findElements(By.cssSelector("#prodList tr th"));

        for (int i=0; i<thList.size(); i++) {
            WebElement th = driver.findElements(By.cssSelector("#prodList tr th")).get(i);
            String targetInsuName = th.getText().trim();

            if (targetInsuName.equals(insuName)) {
                WebElement btn = th.findElement(By.xpath("parent::tr")).findElement(By.tagName("td")).findElement(By.linkText("보험료계산"));
                btn.click();
                break;
            }
        }

        WaitUtil.waitFor(2);
    }


    //공시실용 생년월일 설정 메서드(1개 입력)
    protected void setAnnounceBirth(String fullBirth) {
        setTextToInputBox(By.id("brdt"), fullBirth);
    }


    //공시실용 성별 설정 메서드
    protected void setAnnounceGender(int gender) throws Exception{
        String genderTag = (gender == MALE) ? "type01" : "type02";
        announceBtnClick(By.cssSelector("label[for='" + genderTag + "']"));
    }


    //공시실용 플랜 설정 메서드
    protected void setAnnouncePlan(String planType) throws Exception{
        selectOptionByText(By.id("prodPlanCd"), planType);
    }


    //공시실용 출산예정일 설정 메서드
    protected void setAnnounceDueDate() {
        String dueDate = InsuranceUtil.getDateOfBirth(12);

        logger.info("출산예정일 : {}", dueDate);
        setTextToInputBox(By.id("chbrSchDt"), dueDate);
    }


    //공시실용 특약 설정 메서드
    protected void setAnnounceSubTreaties(CrawlingProduct info) throws Exception{
        int myTreatyCnt = info.treatyList.size();        //내 총 특약 개수
        int targetTreatyCnt = 0;                        //홈페이지에서 체크된 특약 개수

        for(CrawlingTreaty treaty : info.treatyList) {

            String myTreatyName = treaty.treatyName;
            String myAssureMoney = String.valueOf(treaty.assureMoney);

            List<WebElement> trList = driver.findElements(By.cssSelector(".tblList tbody tr"));

            for (WebElement tr : trList) {
                String targetTreatyName = tr.findElements(By.tagName("td")).get(0).getText();

                if (targetTreatyName.equals(myTreatyName)) {
                    logger.info("특약명 : {}", myTreatyName);

                    logger.info("특약 가입금액 : {}원", myAssureMoney);
                    WebElement selectEl = tr.findElements(By.tagName("td")).get(1).findElement(By.tagName("select"));
                    selectOptionByValue(selectEl, myAssureMoney);

                    try {
                        WebElement checkBox = tr.findElement(By.tagName("th")).findElement(By.tagName("input"));

                        if (!checkBox.isSelected()) {
                            checkBox.click();
                            targetTreatyCnt++;
                            break;
                        }
                    } catch (NoSuchElementException e) {
                        //주계약의 경우 체크박스가 존재하지 않아 NoSuchElementException 예외가 발생한다.
                        targetTreatyCnt++;
                        break;
                    }
                }
            }
        }

        logger.info("===================================");
        logger.info("내 특약개수 : {}개", myTreatyCnt);
        logger.info("홈페이지에 체크된 특약개수 : {}개", targetTreatyCnt);
        logger.info("===================================");

        //내 특약개수와 홈페이지에 체크된 특약개수가 다르면 예외처리
        if(myTreatyCnt != targetTreatyCnt) {
            throw new Exception("내 특약개수와 홈페이지에 체크된 특약개수가 일치하지 않습니다. 특약체크를 다시 진행해주세요");
        }
    }

    //공시실용 주계약 보험료 설정 메서드
    protected void setAnnouncePremium(CrawlingTreaty mainTreaty) {
        WebElement element = driver.findElement(By.cssSelector(".price strong"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//strong[text()='보험대상자']")));
        String monthlyPremium = element.getText().replaceAll("[^0-9]", "");

        logger.info("월 보험료 : {}", monthlyPremium);
        mainTreaty.monthlyPremium = monthlyPremium;
    }



    //플랜 설정 메서드
    protected void setPlan(String planSubName) throws Exception{
        helper.waitElementToBeClickable((By.xpath("//tr[@id='viewPlanList']//span[contains(., '" + planSubName + "')]"))).click();
        WaitUtil.waitFor(2);

        String checkedElId = ((JavascriptExecutor)driver).executeScript("return $(\"input[name='planCd']:checked\").attr('id')").toString();
        String checkedPlan = driver.findElement(By.cssSelector("label[for='" + checkedElId + "'] .psName")).getText();

        logger.info("플랜 : {} 클릭됨", checkedPlan);

        if(!checkedPlan.contains(planSubName)) {
            throw new PlanTypeMismatchException("클릭된 플랜 : " + checkedPlan + ", 가입설계 플랜 : " +  planSubName);
        }
    }


    //element가 보이게끔 이동
    protected void moveToElement(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element).build();
        action.perform();
    }



    //특약 정보 비교 메서드
    protected void compareTreaties2(List<CrawlingTreaty> treatyList) throws Exception {

        //홈페이지의 해당 플랜의 특약 정보를 담는다(key : 특약명, value : 특약금액)
        HashMap<String, String> homepageTreatyMap = new HashMap<>();
        List<WebElement> homepageTreatyList = driver.findElements(By.xpath("//tbody[@id='viewCovList']//td[@class[contains(., 'on')]]/em"));

        for(WebElement homepageTreaty : homepageTreatyList) {
            String homepageTreatyMoney = homepageTreaty.getText();

            if(!"-".equals(homepageTreatyMoney)) {
                String homepageTreatyName = homepageTreaty.findElement(By.xpath("./ancestor::tr/th/a[@class='psDetailView']")).getAttribute("data-cvrnm");
                homepageTreatyMoney = homepageTreaty.findElement(By.xpath("..")).getAttribute("data-alwval");

                homepageTreatyMap.put(homepageTreatyName, homepageTreatyMoney);
            }
        }


        if(homepageTreatyMap.size() == treatyList.size()) {
            //Good Case :: 홈페이지와 가입설계 특약 수가 일치할 때. 이 경우는 특약명이 일치하는지, 특약 가입금액이 일치하는지 비교해줘야 함.

            for(CrawlingTreaty myTreaty : treatyList) {
                String myTreatyName = myTreaty.treatyName;
                String myTreatyMoney = String.valueOf(myTreaty.assureMoney);

                //특약명이 불일치할 경우
                if(!homepageTreatyMap.containsKey(myTreatyName)) {
                    throw new NotFoundTreatyException("특약명(" + myTreatyName + ")은 존재하지 않는 특약입니다.");
                } else {
                    //특약명은 일치하지만, 금액이 다른경우
                    if(!homepageTreatyMap.get(myTreatyName).equals(myTreatyMoney)) {
                        logger.info("특약명 : {}", myTreatyName);
                        logger.info("홈페이지 금액 : {}원", homepageTreatyMap.get(myTreatyName));
                        logger.info("가입설계 금액 : {}원", myTreatyMoney);

                        throw new TreatyMisMatchException("특약명(" + myTreatyName + ")의 가입금액이 일치하지 않습니다.");
                    }
                }
            }

        } else if(homepageTreatyMap.size() > treatyList.size()) {
            //Wrong Case :: 홈페이지의 특약 개수가 더 많을 때. 이 경우 가입설계에 어떤 특약을 추가해야 하는지 알려야 함.

            List<String> myTreatyNameList = new ArrayList<>();
            for(CrawlingTreaty myTreaty :treatyList) {
                myTreatyNameList.add(myTreaty.treatyName);
            }

            List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
            targetTreatyList.removeAll(myTreatyNameList);

            logger.info("가입설계에 추가해야 할 특약 리스트 :: {}", targetTreatyList);

            throw new TreatyMisMatchException(targetTreatyList + "의 특약들을 추가해야 합니다.");

        } else {
            //Wrong Case : 가입설계의 특약 개수가 더 많을 때. 이 경우 가입설계에서 어떤 특약이 제거돼야 한다고 알려야 함.

            List<String> myTreatyNameList = new ArrayList<>();
            for(CrawlingTreaty myTreaty :treatyList) {
                myTreatyNameList.add(myTreaty.treatyName);
            }

            List<String> targetTreatyList = new ArrayList<>(homepageTreatyMap.keySet());
            myTreatyNameList.removeAll(targetTreatyList);

            logger.info("가입설계에서 제거돼야 할 특약 리스트 :: {}", myTreatyNameList);

            throw new TreatyMisMatchException(myTreatyNameList + "의 특약들을 제거해야 합니다.");

        }

    }






    //element 클릭 명시적 대기
    protected WebElement waitElementToBeClickable(WebElement element) throws Exception {
        WebElement returnElement = null;
        boolean isClickable = element.isDisplayed() && element.isEnabled();

        if(isClickable) {
            //element가 화면상으로 보이며 활성화 되어있을 때만 클릭 가능함
            returnElement = wait.until(ExpectedConditions.elementToBeClickable(element));
        } else {
            throw new Exception("element가 클릭 불가능한 상태입니다.");
        }

        return returnElement;
    }


    //element 보일때까지 명시적 대기
    protected WebElement waitPresenceOfElementLocated(By by) throws Exception {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    //element 보일때까지 명시적 대기
    protected List<WebElement> waitPresenceOfAllElementsLocatedBy(By by) throws Exception {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    //element 보일때까지 명시적 대기
    protected WebElement waitVisibilityOfElementLocated(By by) throws Exception {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    //element 보일때까지 명시적 대기
    protected List<WebElement> waitVisibilityOfAllElementsLocatedBy(By by) throws Exception {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }


    //element 보일때까지 명시적 대기
    protected List<WebElement> waitVisibilityOfAllElements(By by) throws Exception {
        List<WebElement> elements = driver.findElements(by);
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }


    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(By by) throws Exception {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


    //공시실 페이지 로딩바 대기
    protected void waitAnnouncePageLoadingBar() throws Exception {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading")));
    }


    protected Object executeJavascript(String script) {
        return ((JavascriptExecutor)driver).executeScript(script);
    }

    protected Object executeJavascript(String script, WebElement element) {
        return ((JavascriptExecutor)driver).executeScript(script, element);
    }





    protected boolean compareTreaties(List<CrawlingTreaty> homepageTreatyList, List<CrawlingTreaty> welgramTreatyList) throws Exception {
        boolean result = true;

        List<String> toAddTreatyNameList = null;				//가입설계에 추가해야할 특약명 리스트
        List<String> toRemoveTreatyNameList = null;				//가입설계에서 제거해야할 특약명 리스트
        List<String> samedTreatyNameList = null;				//가입설계와 홈페이지 둘 다 일치하는 특약명 리스트


        //홈페이지 특약명 리스트
        List<String> homepageTreatyNameList = new ArrayList<>();
        List<String> copiedHomepageTreatyNameList = null;
        for(CrawlingTreaty t : homepageTreatyList) {
            homepageTreatyNameList.add(t.treatyName);
        }
        copiedHomepageTreatyNameList = new ArrayList<>(homepageTreatyNameList);


        //가입설계 특약명 리스트
        List<String> myTreatyNameList = new ArrayList<>();
        List<String> copiedMyTreatyNameList = null;
        for(CrawlingTreaty t : welgramTreatyList) {
            myTreatyNameList.add(t.treatyName);
        }
        copiedMyTreatyNameList = new ArrayList<>(myTreatyNameList);




        //일치하는 특약명만 추림
        homepageTreatyNameList.retainAll(myTreatyNameList);
        samedTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);



        //가입설계에 추가해야하는 특약명만 추림
        homepageTreatyNameList.removeAll(myTreatyNameList);
        toAddTreatyNameList = new ArrayList<>(homepageTreatyNameList);
        homepageTreatyNameList = new ArrayList<>(copiedHomepageTreatyNameList);



        //가입설계에서 제거해야하는 특약명만 추림
        myTreatyNameList.removeAll(homepageTreatyNameList);
        toRemoveTreatyNameList = new ArrayList<>(myTreatyNameList);
        myTreatyNameList = new ArrayList<>(copiedMyTreatyNameList);



        //특약명이 일치하는 경우에는 가입금액을 비교해준다.
        for(String treatyName : samedTreatyNameList) {
            CrawlingTreaty homepageTreaty = getCrawlingTreaty(homepageTreatyList, treatyName);
            CrawlingTreaty myTreaty = getCrawlingTreaty(welgramTreatyList, treatyName);

            int homepageTreatyAssureMoney = homepageTreaty.assureMoney;
            int myTreatyAssureMoney = myTreaty.assureMoney;


            //가입금액 비교
            if(homepageTreatyAssureMoney == myTreatyAssureMoney) {
                //금액이 일치하는 경우
                logger.info("특약명 : {} | 가입금액 : {}원", treatyName, myTreatyAssureMoney);
            } else {
                //금액이 불일치하는 경우 특약정보 출력
                result = false;

                logger.info("[불일치 특약]");
                logger.info("특약명 : {}", treatyName);
                logger.info("가입설계 가입금액 : {}", myTreatyAssureMoney);
                logger.info("홈페이지 가입금액 : {}", homepageTreatyAssureMoney);
                logger.info("==============================================================");
            }
        }


        //가입설계 추가해야하는 특약정보 출력
        if(toAddTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 추가해야하는 특약정보({}개)]", toAddTreatyNameList.size());
            logger.info("==============================================================");

            for(int i=0; i<toAddTreatyNameList.size(); i++) {
                String treatyName = toAddTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(homepageTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }

        }



        //가입설계 제거해야하는 특약정보 출력
        if(toRemoveTreatyNameList.size() > 0) {
            result = false;

            logger.info("==============================================================");
            logger.info("[가입설계에 제거해야하는 특약정보({}개)]", toRemoveTreatyNameList.size());
            logger.info("==============================================================");

            for(int i=0; i<toRemoveTreatyNameList.size(); i++) {
                String treatyName = toRemoveTreatyNameList.get(i);

                CrawlingTreaty treaty = getCrawlingTreaty(welgramTreatyList, treatyName);
                logger.info("특약명 : {}", treaty.treatyName);
                logger.info("가입금액 : {}", treaty.assureMoney);
                logger.info("==============================================================");
            }
        }


        return result;
    }






    private CrawlingTreaty getCrawlingTreaty(List<CrawlingTreaty> treatyList, String treatyName) {
        CrawlingTreaty result = null;

        for(CrawlingTreaty treaty : treatyList) {
            if(treaty.treatyName.equals(treatyName)) {
                result = treaty;
            }
        }

        return result;
    }


}
