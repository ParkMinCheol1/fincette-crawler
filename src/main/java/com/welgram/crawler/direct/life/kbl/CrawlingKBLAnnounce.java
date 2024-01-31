package com.welgram.crawler.direct.life.kbl;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.common.enums.ExceptionEnum;
import com.welgram.common.except.crawler.CommonCrawlerException;
import com.welgram.common.except.crawler.crawl.ExpectedSavePremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.PremiumCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnMoneyListCrawlerException;
import com.welgram.common.except.crawler.crawl.ReturnPremiumCrawlerException;
import com.welgram.common.except.crawler.setPlanInfo.SetAnnuityAgeException;
import com.welgram.common.except.crawler.setPlanInfo.SetAssureMoneyException;
import com.welgram.common.except.crawler.setPlanInfo.SetInsTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapCycleException;
import com.welgram.common.except.crawler.setPlanInfo.SetNapTermException;
import com.welgram.common.except.crawler.setPlanInfo.SetPrevalenceTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetProductTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRefundTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetRenewTypeException;
import com.welgram.common.except.crawler.setPlanInfo.SetTreatyException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.common.except.crawler.setUserInfo.SetDueDateException;
import com.welgram.common.except.crawler.setUserInfo.SetGenderException;
import com.welgram.common.except.crawler.setUserInfo.SetInjuryLevelException;
import com.welgram.common.except.crawler.setUserInfo.SetJobException;
import com.welgram.common.except.crawler.setUserInfo.SetTravelPeriodException;
import com.welgram.common.except.crawler.setUserInfo.SetUserNameException;
import com.welgram.common.except.crawler.setUserInfo.SetVehicleException;
import com.welgram.common.strategy.CrawlingTreatyEqualStrategy2;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.CrawlingTreaty.ProductGubun;
import com.welgram.crawler.general.PlanAnnuityMoney;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class CrawlingKBLAnnounce extends CrawlingKBLNew {

    protected void findProduct(CrawlingProduct info) throws CommonCrawlerException {

        try{
            WebElement $a = driver.findElement(By.xpath("//a[@title='"+info.productName+"']"));
            click($a);
        } catch (Exception e){
            throw new CommonCrawlerException(e, "공시실에서 상품을 찾을 수 없습니다.");
        }
    }

    @Override
    public void setBirthday(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {

            WaitUtil.waitFor(3);
            //생년월일 element 찾기
            WebElement $birthInput = driver.findElement(By.id("birthday"));
            WaitUtil.waitFor(3);
            //생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

            //생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setBirthdayPanel(Object... obj) throws SetBirthdayException {
        String title = "생년월일";
        String expectedFullBirth = (String) obj[0];
        String actualFullBirth = "";

        try {

            //생년월일 element 찾기
            WebElement $birthInput = driver.findElement(By.id("strDateOfBirth"));

            //생년월일 설정
            actualFullBirth = helper.sendKeys4_check($birthInput, expectedFullBirth);

            //생년월일 비교
            super.printLogAndCompare(title, expectedFullBirth, actualFullBirth);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_BIRTH;
            throw new SetBirthdayException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setGender(Object... obj) throws SetGenderException {
        String title = "성별";

        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {

            //성별 element 찾기
            WebElement $genderDiv = driver.findElement(By.xpath("//div[@class='radio-check gender']"));
            WebElement $genderLabel = $genderDiv.findElement(By.xpath("//span[normalize-space()='" + expectedGenderText + "']"));

            //성별 클릭
            click($genderLabel);

            //실제 선택된 성별 값 읽어오기
            actualGenderText = ((JavascriptExecutor)driver).executeScript("return $('input[name=genderCode]:checked').next().text();").toString().trim();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setGenderPanel(Object... obj) throws SetGenderException {
        String title = "성별";

        int gender = (int) obj[0];
        String expectedGenderText = (gender == MALE) ? "남자" : "여자";
        String actualGenderText = "";

        try {
            WebElement $selectGender = driver.findElement(By.xpath("//*[@id='strGender']//parent::div"));
            click($selectGender);

            List<WebElement> genderList = $selectGender.findElements(By.tagName("option"));
            for(WebElement option : genderList) {
                String target = option.getText();

                if(target.contains(expectedGenderText)) {
                    click(option);
                    logger.info("성별 :: [{}] 선택", target);
                    break;
                }
            }

            //실제 선택된 플랜 값 읽어오기
            WebElement $selectedGender = $selectGender.findElement(By.xpath("//div[@class='select__ui']"));
            actualGenderText = $selectedGender.getText();

            //비교
            super.printLogAndCompare(title, expectedGenderText, actualGenderText);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_GENDER;
            throw new SetGenderException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setInjuryLevel(Object... obj) throws SetInjuryLevelException {

    }

    @Override
    public void setJob(Object... obj) throws SetJobException {

    }

    @Override
    public void setInsTerm(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];
        String actualInsTerm = "";

        try{

            WebElement $insTermA = driver.findElement(location);
            WebElement $insTermButton = $insTermA.findElement(By.className("select-box"));
            click($insTermButton);

            List<WebElement> list = $insTermButton.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.equals(expectedInsTerm)) {
                    click($a);
                    logger.info($a + "세 선택");
                    break;
                }
            }

            String $insTermSpan = helper.executeJavascript(script).toString();
            actualInsTerm = $insTermSpan;

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setInsTermPanel(Object... obj) throws SetInsTermException {
        String title = "보험기간";
        String expectedInsTerm = (String) obj[0];
        String actualInsTerm = "";

        try{
            WebElement $insTermDiv = driver.findElement(By.xpath("//*[@id='coveragePeriod']//parent::div"));
            click($insTermDiv);

            List<WebElement> insTermList = $insTermDiv.findElements(By.tagName("option"));
            for(WebElement option : insTermList) {
                String target = option.getText();

                if(target.contains(expectedInsTerm)) {
                    click(option);
                    logger.info("보험기간 :: [{}] 선택", target);
                    break;
                }
            }

            WebElement $selectedInsTerm = $insTermDiv.findElement(By.xpath("//div[@class='select__ui']"));
            actualInsTerm = $selectedInsTerm.getText();

            //비교
            super.printLogAndCompare(title, expectedInsTerm, actualInsTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_INSTERM;
            throw new SetInsTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapTerm(Object... obj) throws SetNapTermException {
        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];
        String actualNapTerm = "";

        try {

            WebElement $napTermA = driver.findElement(location);
            WebElement $napTermButton = $napTermA.findElement(By.className("select-box"));
            click($napTermButton);

            List<WebElement> list = $napTermButton.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.contains(expectedNapTerm)) {
                    click($a);
                    logger.info($a + "년 납입기간 선택");
                    break;
                }
            }

            String $napTermSpan = helper.executeJavascript(script).toString();
            actualNapTerm = $napTermSpan;

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setNapTermPanel(Object... obj) throws SetNapTermException {
        String title = "납입기간";
        String expectedNapTerm = (String) obj[0];
        String actualNapTerm = "";

        try {
            WebElement $napTermDiv = driver.findElement(By.xpath("//*[@id='paymentPeriod']//parent::div"));
            click($napTermDiv);

            List<WebElement> napTermList = $napTermDiv.findElements(By.tagName("option"));
            for(WebElement option : napTermList) {
                String target = option.getText();

                if(target.contains(expectedNapTerm)) {
                    click(option);
                    logger.info("납입기간 :: [{}] 선택", target);
                    break;
                }
            }

            WebElement $selectedNapTerm = $napTermDiv.findElement(By.xpath(".//div[@class='select__ui']"));
            actualNapTerm = $selectedNapTerm.getText();

            //비교
            super.printLogAndCompare(title, expectedNapTerm, actualNapTerm);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPTERM;
            throw new SetNapTermException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    @Override
    public void setNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입 주기";
        String expectedNapCycle = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];

        String actualNapCycle = "";

        try {
            if("01".equals(expectedNapCycle)){
                expectedNapCycle = "월납";
            }

            WebElement $napCycleDiv = driver.findElement(location);
            WebElement $napCycleLabel = $napCycleDiv.findElement(By.xpath("//label[normalize-space()='"+expectedNapCycle+"']"));
            click($napCycleLabel);

            String $actualNapCycleLabel = helper.executeJavascript(script).toString().trim();
            if($actualNapCycleLabel.contains(expectedNapCycle)){
                actualNapCycle = $actualNapCycleLabel;
            }

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setSelectBoxNapCycle(Object... obj) throws SetNapCycleException {

        String title = "납입 주기";
        String expectedNapCycle = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];

        String actualNapCycle = "";

        try {
            WebElement $napCycleA = driver.findElement(location);
            WebElement $napCycleLabel = $napCycleA.findElement(By.className("select-box"));
            click($napCycleLabel);

            List<WebElement> list = $napCycleLabel.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.equals(expectedNapCycle)) {
                    click($a);
                    logger.info($a + "선택");
                    break;
                }
            }

            String $napCycleSpan = helper.executeJavascript(script).toString();
            actualNapCycle = $napCycleSpan;

            //비교
            super.printLogAndCompare(title, expectedNapCycle, actualNapCycle);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_NAPCYCLE;
            throw new SetNapCycleException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    protected String getNapCycleName(String napCycle) {
        String napCycleText = "";

        if (napCycle.equals("01")) {
            napCycleText = "월납";
        } else if (napCycle.equals("02")) {
            napCycleText = "년납";
        } else if (napCycle.equals("00")) {
            napCycleText = "일시납";
        }

        return napCycleText;
    }

    @Override
    public void setRenewType(Object... obj) throws SetRenewTypeException {

    }

    @Override
    public void setAssureMoney(Object... obj) throws SetAssureMoneyException {

    }

    @Override
    public void setRefundType(Object... obj) throws SetRefundTypeException {
        super.setRefundType(obj);
    }

    @Override
    public void crawlPremium(Object... obj) throws PremiumCrawlerException {
        String title = "보험료 크롤링";
        String script = "";

        CrawlingProduct info = (CrawlingProduct) obj[0];
        By monthlyPremium = (By) obj[1];
        CrawlingTreaty mainTreaty = info.getTreatyList().stream().filter(t -> t.productGubun.equals(ProductGubun.주계약)).findFirst().get();
        ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_MONTHLY_PREMIUM;

        try {
            //보험료 크롤링 전에는 대기시간을 넉넉히 준다
            WaitUtil.waitFor(5);

            WebElement $premiumEm = driver.findElement(monthlyPremium);
            String premium = $premiumEm.getText().replaceAll("[^0-9]", "");

            mainTreaty.monthlyPremium = premium;

            if("".equals(mainTreaty.monthlyPremium) || "0".equals(mainTreaty.monthlyPremium)) {
                logger.info("주계약 보험료는 0원일 수 없습니다. 주계약 보험료를 세팅해주세요.");
                throw new PremiumCrawlerException(exceptionEnum.getMsg());
            } else {
                logger.info("주계약 보험료 : {}원", mainTreaty.monthlyPremium);
            }

        } catch (Exception e) {
            throw new PremiumCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    public void crawlReturnMoneyListSix(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        ArrayList returnMoneyPageList = ObjectUtils.isEmpty(obj[1]) ? null : (ArrayList)obj[1];
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        WebElement $button = null;

        try{
            for(int i = 0; i < returnMoneyPageList.size(); i++){
                try{
                    $button = driver.findElement(By.id("crownix-toolbar-move"));
                    click($button);
                } catch (ElementClickInterceptedException e){
                    e.printStackTrace();
                    logger.info(" [ "+returnMoneyPageList.get(i) + " ] 페이지는 존재하지 않는 페이지입니다.");
                    continue;
                }

                setTextToInputBox(By.cssSelector(".aTextbox"), String.valueOf(returnMoneyPageList.get(i)));
                $button = driver.findElement(By.xpath("//button[text()='확인']"));
                click($button);

                String moneyUnit = "";

                //단위 찾기
                moneyUnit = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[contains(., '단위')]")).getText();

                int unitStart = moneyUnit.indexOf(":");
                int unitEnd = moneyUnit.indexOf(")");
                String unit = moneyUnit.substring(unitStart+1, unitEnd).replace(" ", "");

                List<WebElement> elements = driver.findElements(By.xpath("//*[@id='m2soft-crownix-text']//div"));
                int idx = 0;
                for(int j = 0; j < elements.size(); j++){
                    try{
                        WebElement div = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+j+"]"));
                        if(div.getText().contains("D/A")){
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
                        String premiumSum = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+2)+"]")).getText();
                        String returnMoneyMin = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+3)+"]")).getText();
                        String returnRateMin = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+4)+"]")).getText();
                        String returnMoneyAvg = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+5)+"]")).getText();
                        String returnRateAvg = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+6)+"]")).getText();
                        String returnMoney = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+7) + ")")).getText();
                        String returnRate = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+8) + ")")).getText();

                        if(term.length() > 4) {
                            throw new NoSuchElementException("경과기간에 해당하는 div가 아닙니다.");
                        }

                        logger.info("================================");
                        logger.info("경과기간 : {}", term);
                        logger.info("납입보험료 : {}", premiumSum);
                        logger.info("최저 환급금 : {}", returnMoneyMin);
                        logger.info("최저 환급률 : {}", returnRateMin);
                        logger.info("평균 환급금 : {}", returnMoneyAvg);
                        logger.info("평균 환급률 : {}", returnRateAvg);
                        logger.info("해약환급금 : {}", returnMoney);
                        logger.info("환급률 : {}", returnRate);

                        PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                        planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                        planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                        planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                        planReturnMoney.setTerm(term);
                        planReturnMoney.setPremiumSum(String.valueOf(MoneyUtil.toDigitMoney(premiumSum+unit)));
                        planReturnMoney.setReturnMoneyMin(String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin+unit)));
                        planReturnMoney.setReturnRateMin(returnRateMin);
                        planReturnMoney.setReturnMoneyAvg(String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg+unit)));
                        planReturnMoney.setReturnRateAvg(returnRateAvg);
                        planReturnMoney.setReturnMoney(String.valueOf(MoneyUtil.toDigitMoney(returnMoney+unit)));
                        planReturnMoney.setReturnRate(returnRate);

                        planReturnMoneyList.add(planReturnMoney);

                        info.returnPremium = planReturnMoney.getReturnMoney();

                        idx += 9;
                    } catch(NoSuchElementException e) {
                        isEnd = true;
                    } catch (MoveTargetOutOfBoundsException e){
                        isEnd = true;
                    }
                }

                info.setPlanReturnMoneyList(planReturnMoneyList);

                logger.info("만기환급급 :: {}원", info.returnPremium);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void crawlReturnMoneyListTwo(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        ArrayList returnMoneyPageList = ObjectUtils.isEmpty(obj[1]) ? null : (ArrayList)obj[1];
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        WebElement $button = null;
        String unit = "";

        try{
            for(int i = 0; i < returnMoneyPageList.size(); i++){
                try{
                    $button = driver.findElement(By.id("crownix-toolbar-move"));
                    click($button);
                } catch (ElementClickInterceptedException e){
                    e.printStackTrace();
                    logger.info(" [ "+returnMoneyPageList.get(i) + " ] 페이지는 존재하지 않는 페이지입니다.");
                    continue;
                }

                setTextToInputBox(By.cssSelector(".aTextbox"), String.valueOf(returnMoneyPageList.get(i)));
                $button = driver.findElement(By.xpath("//button[text()='확인']"));
                click($button);


                if("".equals(unit)){
                    String moneyUnit = "";
                    //단위 찾기
                    moneyUnit = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[contains(., '단위')]")).getText();
                    int unitStart = moneyUnit.indexOf(":");
                    int unitEnd = moneyUnit.indexOf(")");
                    unit = moneyUnit.substring(unitStart+1, unitEnd).replace(" ", "");
                }


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
                    } catch (MoveTargetOutOfBoundsException e){
                        isEnd = true;
                    }
                }

                info.setPlanReturnMoneyList(planReturnMoneyList);

                logger.info("만기환급급 :: {}원", info.returnPremium);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void crawlReturnMoneyListSixTRM(Object... obj) throws ReturnMoneyListCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        ArrayList returnMoneyPageList = ObjectUtils.isEmpty(obj[1]) ? null : (ArrayList)obj[1];
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();
        WebElement $button = null;

        try{
            for(int i = 0; i < returnMoneyPageList.size(); i++){
                try{
                    $button = driver.findElement(By.id("crownix-toolbar-move"));
                    click($button);
                } catch (ElementClickInterceptedException e){
                    e.printStackTrace();
                    logger.info(" [ "+returnMoneyPageList.get(i) + " ] 페이지는 존재하지 않는 페이지입니다.");
                    continue;
                }

                setTextToInputBox(By.cssSelector(".aTextbox"), String.valueOf(returnMoneyPageList.get(i)));
                $button = driver.findElement(By.xpath("//button[text()='확인']"));
                click($button);

                String moneyUnit = "";

                //단위 찾기
                moneyUnit = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[contains(., '단위')]")).getText();

                int unitStart = moneyUnit.indexOf(":");
                int unitEnd = moneyUnit.indexOf(")");
                String unit = moneyUnit.substring(unitStart+1, unitEnd).replace(" ", "");

                List<WebElement> elements = driver.findElements(By.xpath("//*[@id='m2soft-crownix-text']//div"));
                int idx = 0;
                for(int j = 0; j < elements.size(); j++){
                    try{
                        WebElement div = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+j+"]"));
                        if(div.getText().contains("D/A")){
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
                        String premiumSum = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+3)+"]")).getText();
                        String returnMoneyMin = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+5)+"]")).getText();
                        String returnRateMin = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+6)+"]")).getText();
                        String returnMoneyAvg = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+8)+"]")).getText();
                        String returnRateAvg = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']//div["+(idx+9)+"]")).getText();
                        String returnMoney = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+11) + ")")).getText();
                        String returnRate = driver.findElement(By.cssSelector("#m2soft-crownix-text > div:nth-child(" + (idx+12) + ")")).getText();

                        if(term.length() > 4) {
                            throw new NoSuchElementException("경과기간에 해당하는 div가 아닙니다.");
                        }

                        logger.info("================================");
                        logger.info("경과기간 : {}", term);
                        logger.info("납입보험료 : {}", premiumSum);
                        logger.info("최저 환급금 : {}", returnMoneyMin);
                        logger.info("최저 환급률 : {}", returnRateMin);
                        logger.info("평균 환급금 : {}", returnMoneyAvg);
                        logger.info("평균 환급률 : {}", returnRateAvg);
                        logger.info("해약환급금 : {}", returnMoney);
                        logger.info("환급률 : {}", returnRate);

                        PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                        planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                        planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                        planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));

                        planReturnMoney.setTerm(term);
                        planReturnMoney.setPremiumSum(String.valueOf(MoneyUtil.toDigitMoney(premiumSum+unit)));
                        planReturnMoney.setReturnMoneyMin(String.valueOf(MoneyUtil.toDigitMoney(returnMoneyMin+unit)));
                        planReturnMoney.setReturnRateMin(returnRateMin);
                        planReturnMoney.setReturnMoneyAvg(String.valueOf(MoneyUtil.toDigitMoney(returnMoneyAvg+unit)));
                        planReturnMoney.setReturnRateAvg(returnRateAvg);
                        planReturnMoney.setReturnMoney(String.valueOf(MoneyUtil.toDigitMoney(returnMoney+unit)));
                        planReturnMoney.setReturnRate(returnRate);

                        planReturnMoneyList.add(planReturnMoney);

                        info.returnPremium = planReturnMoney.getReturnMoney();

                        idx += 13;
                    } catch(NoSuchElementException e) {
                        isEnd = true;
                    } catch (MoveTargetOutOfBoundsException e){
                        isEnd = true;
                    }
                }

                info.setPlanReturnMoneyList(planReturnMoneyList);

                logger.info("만기환급급 :: {}원", info.returnPremium);
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void crawlReturnMoneyListTwoPanel(Object... obj) throws ReturnMoneyListCrawlerException {

        WebElement $button = null;
        CrawlingProduct info = (CrawlingProduct) obj[0];

        try{
            $button = driver.findElement(By.xpath("//div[@class='accordion__cover']//button[text()='해약환급금']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", $button);
            click($button);

            WaitUtil.waitFor(2);

            List<WebElement> trList = $button.findElements(By.xpath("//div[@class='accordion --is-active']//tbody//tr"));

            List<PlanReturnMoney> planReturnMoneyList = new ArrayList<PlanReturnMoney>();

            for(int j = 0; j < trList.size(); j++) {
                WebElement tr = trList.get(j);

                String term = tr.findElements(By.tagName("th")).get(0).getText();
                String premiumSum = tr.findElements(By.tagName("td")).get(0).getText();
                String returnMoney = tr.findElements(By.tagName("td")).get(1).getText();
                String returnRate = tr.findElements(By.tagName("td")).get(2).getText();

                logger.info("|--경과기간: {}", term);
                logger.info("|--납입보험료: {}", premiumSum);
                logger.info("|--해약환급금: {}", returnMoney);
                logger.info("|--환급률: {}", returnRate);
                logger.info("|_______________________");

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();

                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender((info.gender == MALE) ? "M" : "F");
                planReturnMoney.setInsAge(Integer.parseInt(info.age));

                planReturnMoney.setTerm(term);
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setReturnMoney(returnMoney);
                planReturnMoney.setReturnRate(returnRate);

                planReturnMoneyList.add(planReturnMoney);

                info.returnPremium = returnMoney.replaceAll("[^0-9]", "");

            }
            info.planReturnMoneyList = planReturnMoneyList;
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new ReturnMoneyListCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void crawlReturnPremium(Object... obj) throws ReturnPremiumCrawlerException {

    }

    public void crawlReturnMoneyListAndAnnuityPremium(Object... obj) throws CommonCrawlerException {
        String title = "해약환급금과 연금수령액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        String[] targetPageList = ObjectUtils.isEmpty(obj[1]) ? null : (String[])obj[1];

        WebElement $button = null;

        try{
            ArrayList annuityMoneyPageList = confirmAnnuityPage(targetPageList);
            crawlAnnuityPremium(info, annuityMoneyPageList);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }


        try{
            ArrayList returnMoneyPageList = confirmReturnPage(targetPageList);
            crawlReturnMoneyListSix(info, returnMoneyPageList);
        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public ArrayList confirmAnnuityPage(String[] targetPageList) throws CommonCrawlerException {

        logger.info("연금수령액 페이지 조회");
        ArrayList arrayList = new ArrayList();
        WebElement $button = null;

        try{
            WaitUtil.waitFor(5);

            for(int i = 0; i < targetPageList.length; i++) {

                try{
                    $button = driver.findElement(By.id("crownix-toolbar-move"));
                    click($button);

                    setTextToInputBox(By.cssSelector(".aTextbox"), targetPageList[i]);
                    $button = driver.findElement(By.xpath("//button[text()='확인']"));
                    click($button);
                    if(existElement(By.xpath("//button[text()='OK']"))) {
                        $button = driver.findElement(By.xpath("//button[text()='OK']"));
                        click($button);
                    } else {
                        driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[12][contains(., '연금수령액 예시')]"));
                        arrayList.add(targetPageList[i]);
                    }
                } catch (Exception e){
                    logger.info("{}페이지는 연금수령액 예시 페이지가 아닙니다.", targetPageList[i]);
                }
            }

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }

        return arrayList;
    }

    protected ArrayList confirmReturnPage(String[] targetPageList) throws CommonCrawlerException {
        logger.info("해약해약금 페이지 조회");
        ArrayList arrayList = new ArrayList();
        WebElement $button = null;

        try{
            WaitUtil.waitFor(15);

            for(int i = 0; i < targetPageList.length; i++) {

                try{
                    $button = driver.findElement(By.id("crownix-toolbar-move"));
                    click($button);

                    setTextToInputBox(By.cssSelector(".aTextbox"), targetPageList[i]);
                    $button = driver.findElement(By.xpath("//button[text()='확인']"));
                    click($button);
                    if(existElement(By.xpath("//button[text()='OK']"))) {
                        $button = driver.findElement(By.xpath("//button[text()='OK']"));
                        click($button);
                        break;
                    } else {
                        //간혹 타이틀이 없이 해약환급금표를 제공하는 경우도 존재
                        Boolean exist = helper.existElement(By.xpath("//*[@id='m2soft-crownix-text'][contains(., '경과기간납입보험료(A)해약환급금(B)환급률(B/A)')]"));
                        if(exist){
                            arrayList.add(targetPageList[i]);
                        } else {
                            try{
                                driver.findElement(By.xpath("//*[@id='m2soft-crownix-text'][contains(., '해약환급금 예시')]"));
                                arrayList.add(targetPageList[i]);
                            } catch (NoSuchElementException e) {
                                try{
                                    driver.findElement(By.xpath("//*[@id='m2soft-crownix-text'][contains(., '해약환급금(주계약')]"));
                                    arrayList.add(targetPageList[i]);
                                } catch (NoSuchElementException ex) {
                                    try {
                                        driver.findElement(By.xpath("//*[@id='m2soft-crownix-text'][contains(., '해약환급금 및 주계약')]"));
                                        arrayList.add(targetPageList[i]);
                                    } catch (NoSuchElementException exe) {
                                        driver.findElement(By.xpath("//*[@id='m2soft-crownix-text'][contains(., '해약환급금 예시표(주계약')]"));
                                        arrayList.add(targetPageList[i]);
                                    }
                                }
                            }
                        }

                    }
                } catch (Exception e){
                    logger.info("{}페이지는 해약환급금 예시 페이지가 아닙니다.", targetPageList[i]);
                }
            }

        } catch (Exception e){
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPIN_RETURN_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }

        return arrayList;
    }

    @Override
    public void setAnnuityAge(Object... obj) throws SetAnnuityAgeException {
        String title = "연금개시나이";
        String expectedAnnuityAge = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];
        String actualAnnuityAge = "";

        try {

            //연금개시나이 세팅을 위해 버튼 클릭
            WebElement $annuityAgeA = driver.findElement(location);
            WebElement $annuityAgeButton = $annuityAgeA.findElement(By.className("select-box"));
            click($annuityAgeButton);

            List<WebElement> list = $annuityAgeButton.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.equals(expectedAnnuityAge)) {
                    click($a);
                    logger.info($a + "세 선택");
                    break;
                }
            }

            String $annuityAgeSpan = helper.executeJavascript(script).toString();
            actualAnnuityAge = $annuityAgeSpan;

            //비교
            super.printLogAndCompare(title, expectedAnnuityAge, actualAnnuityAge);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_AGE;
            throw new SetAnnuityAgeException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setInputAssureMoney(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];

        String expectedAssureMoney = info.getAssureMoney();
        String actualAssureMoney = "";

        try {
            //가입금액을 원수사의 가입금액 포맷에 맞게 text값 수정
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000).replaceAll("[^0-9]", "");

            WebElement $assureMoneyInput = driver.findElement(location);

            actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney);
            actualAssureMoney = actualAssureMoney.replaceAll("[^0-9]", "");

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setInputAssureMoneyPanel(Object... obj) throws SetAssureMoneyException {
        String title = "가입금액";
        CrawlingProduct info = (CrawlingProduct) obj[0];

        String expectedAssureMoney = info.getAssureMoney();
        String actualAssureMoney = "";

        try {
            //가입금액을 원수사의 가입금액 포맷에 맞게 text값 수정
            expectedAssureMoney = String.valueOf(Integer.parseInt(expectedAssureMoney) / 10000).replaceAll("[^0-9]", "");

            WebElement $assureMoneyInput = driver.findElement(By.id("faceAmount"));

            actualAssureMoney = helper.sendKeys4_check($assureMoneyInput, expectedAssureMoney);

            super.printLogAndCompare(title, expectedAssureMoney, actualAssureMoney);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ASSUREMONEY;
            throw new SetAssureMoneyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setAnnuityReceivePeriod(Object... obj) throws CommonCrawlerException {
        String title = "연금수령기간";
        String expectedAnnuityReceivePeriod = (String) obj[0];
        By location = ObjectUtils.isEmpty(obj[1]) ? null : (By)obj[1];
        String script = ObjectUtils.isEmpty(obj[2]) ? null : (String)obj[2];

        try {

            WebElement $annuityReceiveCycleA = driver.findElement(location);
            WebElement $annuityReceiveCycle = $annuityReceiveCycleA.findElement(By.className("select-box"));
            click($annuityReceiveCycle);

            List<WebElement> list = $annuityReceiveCycle.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.equals(expectedAnnuityReceivePeriod)) {
                    click($a);
                    logger.info($a + "년 납입기간 선택");
                    break;
                }
            }

            String $annuityReceivePeriodSpan = helper.executeJavascript(script).toString();
            String actualAnnuityReceivePeriod = $annuityReceivePeriodSpan;

            //비교
            super.printLogAndCompare(title, expectedAnnuityReceivePeriod, actualAnnuityReceivePeriod);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_ANNUITY_RECEIVE_PERIOD;
            throw new SetAnnuityAgeException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    /**
     * 연금수령방법 설정
     * @param $annuityReceiveCycleSelect
     * @param expectedAnnuityReceiveCycle
     * @throws CommonCrawlerException
     */
    public void setAnnuityReceiveCycle(WebElement $annuityReceiveCycleSelect, String expectedAnnuityReceiveCycle) throws CommonCrawlerException {

    }


    /**
     * 연금지급형태 설정
     * @param $annuityGiveTypeSelect
     * @param expectedAnnuityGiveType
     * @throws CommonCrawlerException
     */
    public void setAnnuityGiveType(WebElement $annuityGiveTypeSelect, String expectedAnnuityGiveType) throws CommonCrawlerException {

    }


    public void crawlAnnuityPremium(Object... obj) throws CommonCrawlerException {
        CrawlingProduct info = (CrawlingProduct) obj[0];
        ArrayList annuityMoneyPageList = ObjectUtils.isEmpty(obj[1]) ? null : (ArrayList)obj[1];

        WebElement $button = null;

        try{
            logger.info("연금수령액 크롤링");

            for(int i = 0; i < annuityMoneyPageList.size(); i++){
                try{
                    $button= driver.findElement(By.id("crownix-toolbar-move"));
                    click($button);
                    WaitUtil.waitFor(2);
                } catch (ElementClickInterceptedException e){
                    e.printStackTrace();
                    logger.info(" [ "+annuityMoneyPageList.get(i) + " ] 페이지는 존재하지 않는 페이지입니다.");
                    continue;
                }

                setTextToInputBox(By.cssSelector(".aTextbox"), String.valueOf(annuityMoneyPageList.get(i)));
                $button = driver.findElement(By.xpath("//button[text()='확인']"));
                click($button);
                WaitUtil.waitFor(1);

                String moneyUnit = "";

                //단위 찾기
                moneyUnit = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[contains(., '(단위:')]")).getText();

                int unitStart = moneyUnit.indexOf(":");
                int unitEnd = moneyUnit.indexOf(")");
                String unit = moneyUnit.substring(unitStart+1, unitEnd).replace(" ", "");

//element의 위치와 실제 나이의 위치와 동일한지 확인
//                String ageLocation = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[contains(., '"+info.annAge +"')]")).getText();
                String elementLocation = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[36]")).getText();

                PlanAnnuityMoney planAnnuityMoney = new PlanAnnuityMoney();

                String whl100a = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[42]")).getText().replaceAll("[^0-9]", "");;
                whl100a = String.valueOf(MoneyUtil.toDigitMoney(whl100a + unit));
                planAnnuityMoney.setWhl100A(whl100a);

                String whl10y = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[43]")).getText().replaceAll("[^0-9]", "");;
                whl10y = String.valueOf(MoneyUtil.toDigitMoney(whl10y + unit));
                planAnnuityMoney.setWhl10Y(whl10y);

                String whl20y = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[44]")).getText().replaceAll("[^0-9]", "");;
                whl20y = String.valueOf(MoneyUtil.toDigitMoney(whl20y + unit));
                planAnnuityMoney.setWhl20Y(whl20y);

                String fxd10y = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[45]")).getText().replaceAll("[^0-9]", "");;
                fxd10y = String.valueOf(MoneyUtil.toDigitMoney(fxd10y + unit));
                planAnnuityMoney.setFxd10Y(fxd10y);

                String fxd20y = driver.findElement(By.xpath("//*[@id='m2soft-crownix-text']/div[46]")).getText().replaceAll("[^0-9]", "");;
                fxd20y = String.valueOf(MoneyUtil.toDigitMoney(fxd20y + unit));
                planAnnuityMoney.setFxd20Y(fxd20y);

                info.planAnnuityMoney = planAnnuityMoney;

                //element를 잘 찾고 크롤링을 해왔다면 info에 값 세팅
                if("확정 10년".equals(info.annuityType)) {
                    info.fixedAnnuityPremium = planAnnuityMoney.getFxd10Y();
                } else if("확정 20년".equals(info.annuityType)) {
                    info.fixedAnnuityPremium = planAnnuityMoney.getFxd20Y();
                } else if("종신 100세".equals(info.annuityType)) {
                    info.annuityPremium = planAnnuityMoney.getWhl100A();
                } else if("종신 20년".equals(info.annuityType)) {
                    info.annuityPremium = planAnnuityMoney.getWhl20Y();
                } else {    //종신 10년
                    info.annuityPremium = planAnnuityMoney.getWhl10Y();
                }

                if(info.annuityType.contains("확정")){
                    if(info.fixedAnnuityPremium.equals("0") || info.fixedAnnuityPremium.equals("")){
                        info.treatyList.get(0).monthlyPremium = "0";
                        throw new Exception("확정형 가설이지만 확정 금액이 0원이거나 비어있습니다.");
                    }
                } else {
                    if(info.annuityPremium.equals("0") || info.annuityPremium.equals("")){
                        info.treatyList.get(0).monthlyPremium = "0";
                        throw new Exception("종신형 가설이지만 종신 금액이 0원이거나 비어있습니다.");
                    }
                }
            }

            logger.info("annMoney :: " + info.annuityPremium);
            logger.info("fixedAnnMoney :: " + info.fixedAnnuityPremium);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_SCRAPING_ANNUITY_MONEY;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setPlan(CrawlingProduct info, By location) throws CommonCrawlerException {
        String expectedPlan = info.textType;

        String title = "플랜";
        String actualPlan = "";
        String script = "";

        try {
            //플랜 관련 element 찾기
            WebElement $planUl = driver.findElement(location);
            WebElement $planA = $planUl.findElement(By.className("select-box"));
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,-50)");
            click($planA);

            List<WebElement> list = $planA.findElements(By.tagName("li"));
            for(WebElement li : list) {
                WebElement $a = li.findElement(By.tagName("a"));

                String target = $a.getText();

                if(target.contains(expectedPlan)) {
                    click($a);
                    logger.info("플랜 :: [{}] 선택", $a);
                    break;
                }
            }

            //실제 선택된 플랜 값 읽어오기
            WebElement $selectedButton = $planUl.findElement(By.xpath(".//a[@class='anchor']"));
            if($selectedButton.getText().contains(expectedPlan)){
                actualPlan = expectedPlan;
            }

            //비교
            super.printLogAndCompare(title, expectedPlan, actualPlan);


        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setPlanAndPlanName(CrawlingProduct info) throws CommonCrawlerException {
        String expectedPlanType = info.textType;
        String expectedPlanName = info.planName;

        String title = "플랜";
        String actualPlan = "";

        try {
            logger.info("모달 창 전환");
            driver.switchTo().frame("cyberIframe");

            //플랜 관련 element 찾기
            logger.info("상품군 :: {} 선택", info.textType);
            WebElement $selectCategory = driver.findElement(By.xpath("//*[@id='selCategory']//parent::div"));
            click($selectCategory);

            List<WebElement> categoryList = $selectCategory.findElements(By.tagName("option"));
            for(WebElement option : categoryList) {
                String target = option.getText();

                if(target.contains(expectedPlanType)) {
                    click(option);
                    logger.info("플랜 :: [{}] 선택", target);
                    break;
                }
            }

            //실제 선택된 플랜 값 읽어오기
            WebElement $selectedButton = $selectCategory.findElement(By.xpath("//div[@class='select__ui']"));
            actualPlan = $selectedButton.getText();

            //비교
            super.printLogAndCompare(title, expectedPlanType, actualPlan);

            logger.info("--------------------------------------");
            logger.info("상품명 {} 선택", info.planName);
            WebElement $selectProductName = driver.findElement(By.xpath("//*[@id='selProductist']//parent::div"));
            click($selectProductName);

            List<WebElement> productList = $selectProductName.findElements(By.tagName("option"));
            for(WebElement option : productList) {
                String target = option.getText();

                if(target.contains(expectedPlanName)) {
                    click(option);
                    logger.info("상품 :: [{}] 선택", target);
                    break;
                }
            }

            //실제 선택된 플랜 값 읽어오기
            $selectedButton = $selectProductName.findElement(By.xpath(".//div[@class='select__ui']"));
            actualPlan = $selectedButton.getText();

            //비교
            super.printLogAndCompare(title, expectedPlanName, actualPlan);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setProductDetail(CrawlingProduct info) throws CommonCrawlerException {
        String expectedPlanSubName = info.planSubName;

        String title = "서브네임";
        String actualPlanSubName = "";

        try {
            //플랜 관련 element 찾기
            logger.info("상품 서브네임 :: {} 선택", info.planSubName);
            WebElement $selectProductDetail = driver.findElement(By.xpath("//*[@id='selProductistDetail']//parent::div"));
            click($selectProductDetail);

            List<WebElement> categoryList = $selectProductDetail.findElements(By.tagName("option"));
            for(WebElement option : categoryList) {
                String target = option.getText();

                if(target.contains(expectedPlanSubName)) {
                    click(option);
                    logger.info("플랜 :: [{}] 선택", target);
                    break;
                }
            }

            //실제 선택된 플랜 값 읽어오기
            WebElement $selectedButton = $selectProductDetail.findElement(By.xpath(".//div[@class='select__ui']"));
            if($selectedButton.getText().contains(expectedPlanSubName)){
                actualPlanSubName = expectedPlanSubName;
            }

            //비교
            super.printLogAndCompare(title, expectedPlanSubName, actualPlanSubName);

        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERROR_BY_PLAN_NAME;
            throw new CommonCrawlerException(e.getCause(), exceptionEnum.getMsg());
        }
    }


    @Override
    public void crawlExpectedSavePremium(Object... obj) throws ExpectedSavePremiumCrawlerException {

    }

    @Override
    public void setUserName(Object... obj) throws SetUserNameException {

    }

    @Override
    public void setDueDate(Object... obj) throws SetDueDateException {

    }

    @Override
    public void setTravelDate(Object... obj) throws SetTravelPeriodException {

    }

    @Override
    public void setProductType(Object... obj) throws SetProductTypeException {

    }

    @Override
    public void setPrevalenceType(Object... obj) throws SetPrevalenceTypeException {

    }

    @Override
    public void setVehicle(Object... obj) throws SetVehicleException {

    }


    public void setVehicleCnt(Object... obj) throws CommonCrawlerException {

    }



    public void setTreaties(CrawlingProduct info) throws SetTreatyException {

        WebElement $span = null;
        WebElement $a = null;
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        try{
            int infoCnt = 0;
            int treatyCheck = 0;
            for(int i = 0; i<info.treatyList.size(); i++){
                if(!("주계약".equals(info.treatyList.get(i).productGubun.name()))){
                    infoCnt++;
                }
            }

            for(int i = 0; i < info.treatyList.size(); i++){

                CrawlingTreaty targetTreaty = new CrawlingTreaty();

                if(info.treatyList.get(i).productGubun.name().equals("주계약")){
                    continue;
                } else {
                    CrawlingTreaty treatyInfo = info.treatyList.get(i);

                    String treatyName = treatyInfo.treatyName;
                    String treatyInsTerm = treatyInfo.insTerm.replace("년", "").replace("세", "");
                    String treatyNapTerm = treatyInfo.napTerm.replace("년", "").replace("세", "");
                    int treatyAssureMoney = treatyInfo.assureMoney;

                    logger.info("특약명 :: " + treatyName);
                    logger.info("보험 기간 :: " + treatyInfo.insTerm);
                    logger.info("납입 기간 :: " + treatyInfo.napTerm);
                    logger.info("가입금액 :: " + treatyAssureMoney);

                    try{
                        WebElement tr = driver.findElement(By.xpath("//tbody[@id='riderOptions']//span[text()='" + treatyName + "']/ancestor::tr"));
                        WebElement insTerm = tr.findElement(By.xpath(".//a[@title='보험기간']/ancestor::td"));
                        WebElement napTerm = tr.findElement(By.xpath(".//a[@title='납입기간']/ancestor::td"));
                        WebElement assureMoney = tr.findElement(By.xpath(".//input[@title='가입금액']/ancestor::td"));

                        //특약 선택
                        $span = driver.findElement(By.xpath("//tbody[@id='riderOptions']//span[text()='" + treatyName + "']"));
                        $span.click();
                        treatyCheck++;

                        targetTreaty.setTreatyName(treatyName);

                        //보험기간 선택
                        try{
                            boolean exist = false;
                            logger.info("보험 기간 선택");
                            click(insTerm);
                            List<WebElement> liList = insTerm.findElements(By.xpath("./div/div/ul/li"));
                            for(WebElement li : liList) {
                                //보험기간 클릭
                                String liText = li.findElement(By.xpath("./a")).getText();
                                if(liText.contains(treatyInsTerm)){
                                    $a = li.findElement(By.tagName("a"));
                                    click($a);
                                    targetTreaty.setInsTerm(treatyInfo.insTerm);
                                    exist = true;
                                    break;
                                }
                            }
                            if(!exist) throw new Exception();
                        }catch (Exception e){
                            logger.info("[{}] 특약의 보험기간을 찾을 수 없습니다. 확인해주세요.", treatyName);
                            logger.info("보험 기간 :: " + treatyInsTerm);
                        }


                        //납입기간 선택
                        try{
                            boolean exist = false;
                            logger.info("납입 기간 선택");
                            click(napTerm);
                            List<WebElement> liList = napTerm.findElements(By.xpath("./div/div/ul/li"));
                            for(WebElement li : liList) {
                                //납입기간 클릭
                                String liText = li.findElement(By.xpath("./a")).getText();
                                if(liText.contains(treatyNapTerm)){
                                    $a = li.findElement(By.tagName("a"));
                                    $a.click();
                                    targetTreaty.setNapTerm(treatyInfo.napTerm);
                                    exist = true;
                                    break;
                                }
                            }
                            if(!exist) throw new Exception();
                        } catch (Exception e){
                            logger.info("[{}] 특약의 납입기간 찾을 수 없습니다. 확인해주세요.", treatyName);
                            logger.info("납입 기간 :: " + treatyNapTerm);
                        }

                        //가입금액 선택
                        try{
                            logger.info("가입금액 선택");
                            click(assureMoney);

                            String treatyMoney = String.valueOf(treatyAssureMoney / 10000);
                            WebElement inputAssureMoney = assureMoney.findElement(By.xpath(".//input"));
                            inputAssureMoney.sendKeys(Keys.DELETE);
                            inputAssureMoney.sendKeys(Keys.DELETE);
                            inputAssureMoney.sendKeys(Keys.DELETE);
                            inputAssureMoney.sendKeys(Keys.DELETE);
                            inputAssureMoney.sendKeys(Keys.DELETE);
                            inputAssureMoney.sendKeys(treatyMoney);
                            inputAssureMoney.sendKeys(Keys.TAB);
                            targetTreaty.setAssureMoney(treatyAssureMoney);

                            if (alert("#systemAlert1")) {
                                logger.info("[{}] 특약의 가입금액에 문제가 있습니다. 확인 바랍니다.", treatyName);
                                logger.info("가입 금액 :: " + treatyAssureMoney);
                                throw new Exception("가입금액오류.");
                            }

                        } catch (Exception e){
                            logger.info("[{}] 특약의 가입금액 입력부분에 오류가 발생하였습니다. 확인해주세요.", treatyName);
                        }

                    } catch (Exception e){
                        logger.info("[{}] 특약을 원수사 페이지에서 찾을 수 없습니다. 확인해주세요.", treatyName);
                    }
                    targetTreatyList.add(targetTreaty);

                }

            }
            if(infoCnt != treatyCheck) throw new Exception("가입설계 특약 개수와 일치하지 않습니다.");
            //비교

            List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
                .filter(t -> t.productGubun == ProductGubun.선택특약)
                .collect(Collectors.toList());

            boolean result = advancedCompareTreaties(targetTreatyList, subTreatyList , new CrawlingTreatyEqualStrategy2());

            if(result) {
                logger.info("특약 정보가 모두 일치합니다~~~");
            } else {
                logger.error("특약 정보 불일치~~~");
                throw new Exception();
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    public void setTreatiesPanel(CrawlingProduct info) throws SetTreatyException {
        WebElement $target = null;
        List<CrawlingTreaty> targetTreatyList = new ArrayList<>();

        int infoCnt = 0;
        int treatyCheck = 0;

        try{
            for(int i = 0; i<info.treatyList.size(); i++){
                if(!("주계약".equals(info.treatyList.get(i).productGubun.name()))){
                    infoCnt++;
                }
            }

            for(int i = 0; i < info.treatyList.size(); i++){
                CrawlingTreaty targetTreaty = new CrawlingTreaty();

                if(info.treatyList.get(i).productGubun.name().equals("주계약")) {
                    continue;
                } else {
                    CrawlingTreaty treatyInfo = info.treatyList.get(i);

                    String treatyName = treatyInfo.treatyName;
                    String treatyInsTerm = (treatyInfo.insTerm.equals("종신")) ? "종신" : treatyInfo.insTerm + "보장";
                    String treatyNapTerm = (treatyInfo.napTerm.equals(treatyInfo.insTerm)) ? "전기납" : treatyInfo.napTerm + "납";
                    String treatyAssureMoney = String.valueOf((treatyInfo.assureMoney) / 10000);

                    logger.info("특약명 :: " + treatyName);
                    logger.info("보험 기간 :: " + treatyInsTerm);
                    logger.info("납입 기간 :: " + treatyNapTerm);
                    logger.info("가입금액 :: " + treatyAssureMoney);

                    WebElement foundTreaty = driver.findElement(By.xpath("//*[@id='step3']//div[@class='panel__block']//label[normalize-space()='" + treatyName +"']"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", foundTreaty);

                    $target = driver.findElement(By.xpath("//*[@id='step3']//div[@class='panel__block']//label[normalize-space()='" + treatyName +"']"));
                    click($target);
                    targetTreaty.setTreatyName(treatyName);
                    treatyCheck++;
                    WaitUtil.waitFor(1);

                    $target = driver.findElement(By.xpath("//*[@id='step3']//div[@class='panel__block']//label[normalize-space()='" + treatyName +"']//parent::div//parent::div//select[@name='coveragePeriod']//option[text()='"+treatyInsTerm+"']"));
                    click($target);
                    targetTreaty.setInsTerm(treatyInfo.insTerm);
                    WaitUtil.waitFor(1);

                    $target = driver.findElement(By.xpath("//*[@id='step3']//div[@class='panel__block']//label[normalize-space()='" + treatyName +"']//parent::div//parent::div//select[@name='paymentPeriod']//option[text()='"+treatyNapTerm+"']"));
                    click($target);
                    targetTreaty.setNapTerm(treatyInfo.napTerm);
                    WaitUtil.waitFor(1);

                    $target = driver.findElement(By.xpath("//*[@id='step3']//div[@class='panel__block']//label[normalize-space()='" + treatyName +"']//parent::div//parent::div//input[@name='faceAmount']"));
                    click($target);
                    helper.sendKeys4_check($target, treatyAssureMoney);
                    targetTreaty.setAssureMoney(treatyInfo.assureMoney);

                    targetTreatyList.add(targetTreaty);
                }
            }

            if(infoCnt != treatyCheck) throw new Exception("특약 체크 개수와 가설의 특약 개수와 일치하지않습니다.");

            List<CrawlingTreaty> subTreatyList = info.getTreatyList().stream()
                .filter(t -> t.productGubun == ProductGubun.선택특약)
                .collect(Collectors.toList());

            boolean result = advancedCompareTreaties(targetTreatyList, subTreatyList , new CrawlingTreatyEqualStrategy2());

            if(result) {
                logger.info("특약 정보가 모두 일치합니다~~~");
            } else {
                logger.error("특약 정보 불일치~~~");
                throw new Exception();
            }
        } catch (Exception e) {
            ExceptionEnum exceptionEnum = ExceptionEnum.ERR_BY_TREATY;
            throw new SetTreatyException(e.getCause(), exceptionEnum.getMsg());
        }
    }

    protected void alert() throws Exception{
        boolean isShowed = helper.isAlertShowed();
        while (isShowed) {
            driver.switchTo().alert().accept();
            isShowed = helper.isAlertShowed();
        }
        WaitUtil.waitFor(2);
    }

    //로딩바 명시적 대기
    public void waitLoadingBar() {
        try {
            helper.waitForCSSElement("div.loading");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO 추후에 helper로 뺄것
    public void click(WebElement $element) throws Exception {
        helper.waitElementToBeClickable($element).click();
        waitLoadingBar();
        WaitUtil.waitFor(1);
    }


    //TODO 추후에 helper로 뺄것
    public void click(By position) throws Exception {
        WebElement $element = driver.findElement(position);
        click($element);
    }

    protected void moveToElement(By location){
        Actions actions = new Actions(driver);
        WebElement element = driver.findElement(location);
        actions.moveToElement(element);
        actions.perform();
    }

    protected void moveToElement(WebElement location){
        Actions actions = new Actions(driver);
        actions.moveToElement(location);
        actions.perform();
    }

    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }

    protected boolean existElement(By element) {
        boolean isExist = false;
        try {
            driver.findElement(element);
            isExist = true;
        } catch(NoSuchElementException e) {

        }
        return isExist;
    }

    protected boolean alert(String value) throws Exception {
        WebElement element = null;
        try {
            element = new WebDriverWait(driver, 2).until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(value))));
        } catch (Exception e){}

        if(element == null) {
            return false;
        } else {
            return true;
        }
    }

    protected void systemAlert() throws Exception {
        try {
            logger.info("특정 나이에 오류로 가입금액 입력 전 시스템 알럿 발생");
            driver.findElement(By.xpath("//*[@id='systemAlert1']//button[@class='btn-4x btn-yellow modal-close']")).click();
        } catch (Exception e) {
            logger.info("시스템 알럿 없음");
        }
    }
}