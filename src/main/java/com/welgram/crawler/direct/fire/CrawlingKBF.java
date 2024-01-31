package com.welgram.crawler.direct.fire;

import com.welgram.common.MoneyUtil;
import com.welgram.common.WaitUtil;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.PlanReturnMoney;
import com.welgram.util.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.tess4j.Tesseract;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class CrawlingKBF extends SeleniumCrawler {

    public CrawlingKBF(String productCode) {
        super(productCode);
    }

    public CrawlingKBF() {

    }

//    //크롤링 시 필요한 옵션 정의
//    protected void setChromeOptionKBF(CrawlingProduct info) {
//        CrawlingOption option = info.getCrawlingOption();
//        option.setBrowserType(CrawlingOption.BrowserType.Chrome);
//        option.setImageLoad(true);
//        option.setUserData(false);
//        info.setCrawlingOption(option);
//    }

    //로딩화면이 사라질 때까지 대기	-	모든 KBF
    protected void waitForLoading() {
        wait.until(
                ExpectedConditions.attributeContains(By.id("loading_wrap"), "style", "display: none"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading_wrap")));
    }

    protected void selectOption(WebElement selectEl, String text) {
        Select select = new Select(selectEl);
        select.selectByVisibleText(text);
    }

    protected void selectOption(By element, String text) {
        WebElement selectEl = driver.findElement(element);
        selectOption(selectEl, text);
    }

    //성별 선택	-	CCR, CHL, DMN, DRV, DSS2, DTL, MDC1, MDR2, MDR3, OST
    protected void selectGender(CrawlingProduct info) throws Exception {
        if (info.gender == MALE) {
            helper.click(By.id("man"));
            logger.info("남자");
        } else {
            helper.click(By.id("woman"));
            logger.info("여자");
        }
    }

    //생년월일 set	-	BAB, CCR, CHL, DMN, DRV, DSS2, DTL, MDC1, MDR2, MDR3, OST
    protected void setBirth(CrawlingProduct info) throws Exception {
        helper.waitElementToBeClickable(By.id("usernum1"));
        WaitUtil.waitFor(2);
        helper.click(By.id("usernum1"));
        driver.findElement(By.id("usernum1")).sendKeys(info.fullBirth);
        logger.info(info.fullBirth);
    }

    //직업정보 set	-	BAB, CCR, DMN, DSS2, MDC1
    protected void setJob() throws Exception {
        WaitUtil.loading(3);
        helper.sendKeys3_check(By.id("ids_ser1"), "교사");
        logger.info("교사");

        // 검색 버튼 클릭
        helper.click(driver.findElement(By.id("ids_ser1")).findElement(
                By.xpath("./../button"))); // 검색 버튼 클릭 By.cssSelector("button.pc_btn_serach")
        waitForLoading();
        helper.click(By.partialLinkText("중·고등학교 "));
        logger.info("중·고등학교");
        helper.click(By.xpath("//button[contains(text(), '선택완료')]"));
        waitForLoading();
    }

    //알럿 확인	-	CHL, DSS2
    protected void checkPopup() {
        try {
            if (driver.findElement(By.cssSelector(".alert_wrap")).isDisplayed()) {
                logger.debug("알럿표시 확인!!!");
                logger.debug("순수보장형이나 만기환급금으로 변경되는케이스");
                helper.click(By.linkText("확인"));

                waitForLoading();
            }
        } catch (Exception e) {
            logger.info("알럿표시 없음!!!");
            // TODO: handle exception
        }
    }

    //보기/납기 set	-	BAB, CCR, CHL, DMN, DRV, DSS2, DTL, MDR2
    protected void selectTerm(CrawlingProduct info) throws Exception {
        logger.info("납기 :" + info.napTerm);
        logger.info("보기 :" + info.insTerm);
        boolean found = false;
        boolean selected = false;
        List<WebElement> termList = helper
                .waitVisibilityOfAllElementsLocatedBy(By.cssSelector("ul.pc_urgent_top li a p"));

        for (WebElement termElement : termList) {

            String[] terms = termElement.getText().split("/");
            String napTerm = terms[0];
            String insTerm = terms[1];

            if (napTerm.contains(info.napTerm) && insTerm.contains(info.insTerm)) {
                waitForLoading();
                wait.until(ExpectedConditions
                        .invisibilityOfElementLocated(By.cssSelector("div.ngdialog-overlay")));
                helper.click(termElement);
                waitForLoading();
                logger.info(termElement.getText() + " 클릭");
                found = true;

                if (termElement.findElement(By.xpath("ancestor::a")).getAttribute("class")
                        .equals("on")) {
                    selected = true;
                }
                break;
            }
        }

        if (!found) {
            throw new Exception("보기에 선택할 보기/납기 옵션이 없습니다.");
        }

        // 보기/납기 옵션은 존재해도 클릭할 수 없을 때 Exception을 던져야한다.
        if (!selected) {
            throw new Exception("해당 보기/납기 옵션이 활성화되어 있지 않습니다.");
        }
        WaitUtil.waitFor(3);
    }

    protected void updateSelectTerm(CrawlingProduct info) throws Exception {
        logger.info("보기 :" + info.insTerm);
        logger.info("납기 :" + info.napTerm);
        boolean insTermFound = false;
        boolean napTermFound = false;
        List<WebElement> insTtermList = driver.findElements(By.cssSelector("ul.clfix._item2 li a span"));
        WebDriverWait wait = new WebDriverWait(driver, 70);

        for(WebElement instermElement : insTtermList){
            if(instermElement.getText().contains(info.insTerm)){
                helper.click(instermElement);
                WaitUtil.waitFor(2);
                try {
                    if (driver.findElement(By.cssSelector(".alert_wrap")).isDisplayed()) {
                        logger.debug("알럿표시 확인!!!");
                        helper.click(By.linkText("확인"));
                        WaitUtil.waitFor(2);
                        wait.until(
                            ExpectedConditions.invisibilityOfElementLocated(By.id("loading_wrap")));
                    }
                }catch (NoSuchElementException e){
                    logger.info("알럿 표시 없음");
                }
                logger.info("보험기간 :: {} 선택", instermElement.getText());
                insTermFound = true;
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading_wrap")));
                break;
            }
        }
        if(insTtermList.size() == 0){
            String insTerm= driver.findElement(By.cssSelector("ul.clfix._item1 li a span")).getText();
            if(insTerm.contains(info.insTerm)){
                insTermFound = true;
            }
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading_wrap")));
        List<WebElement> napTtermList = driver.findElements(By.xpath("//ul[@class='clfix _item3']//span[@class='ng-binding']"));
        for(WebElement naptermElement : napTtermList){
            if(naptermElement.getText().contains(info.napTerm)){
                wait.until(ExpectedConditions.elementToBeClickable(naptermElement));
                helper.click(naptermElement);
                WaitUtil.waitFor(2);
                if (driver.findElement(By.cssSelector(".alert_wrap")).isDisplayed()) {
                    logger.debug("알럿표시 확인!!!");
                    helper.click(By.linkText("확인"));
                    WaitUtil.waitFor(2);
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading_wrap")));
                }
                logger.info("납입기간 :: {} 선택", naptermElement.getText());
                napTermFound = true;
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading_wrap")));
                break;
            }
        }

        if (!insTermFound || !napTermFound) {
            throw new Exception("보기에 선택할 보기/납기 옵션이 없습니다.");
        }
    }

    //플랜 선택	-	CCR, CHL, DMN, DRV, DSS2, DTL, MDR2, MDR3, OST
    protected void selectPlan(CrawlingProduct info) throws Exception {

        boolean found = false;
        List<WebElement> planList = helper
                .waitVisibilityOfAllElementsLocatedBy(By.cssSelector("button.pc_btn_plans"));

        for (WebElement plan : planList) {

            String planName = plan.findElement(By.cssSelector("span:first-child")).getText();
            logger.info("플랜명1 : " + planName);
//            String infoPlanName = this.getPlanName(info.planName);
            String infoTextType = info.textType;
            logger.info("플랜명2 : " + infoTextType);
            // 무배당 KB 다이렉트 자녀보험 (15세이하) : 표준형
            if (planName.contains(infoTextType)) {
                WaitUtil.loading(2);
                waitForLoading();
                plan.click();
                waitForLoading();
                logger.info(planName + " 클릭");
                found = true;
                break;
            }
        }

        if (!found) {
            throw new Exception("보기에 선택할 플랜 옵션이 없습니다.");
        }
        WaitUtil.waitFor(3);
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

    //해당 element가 보이게 스크롤 이동
    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // |가설에 추가된 특약만 선택 (AMD_D, BAB, CCR, DMN, DRV, DSS2, MDC1, MDC2)
    protected void treatyCheck(CrawlingProduct info) throws Exception {
        List<String> homepageTreatyNameList = new ArrayList<>();
        List<String> notSameTreatyAssureMoneyList = new ArrayList<>();
        List<String> notExistTreatyNameList = new ArrayList<>();
        int homepageTreatyCnt = driver.findElements(By.xpath("//tbody//label")).size();         //홈페이지 특약 개수
        logger.info("홈페이지 특약 개수 :: {}", homepageTreatyCnt);
        logger.info("가설의 특약 개수 :: {}",info.treatyList.size());
        Actions actions = new Actions(driver);
        int checkBoxCount = 0;

        //특약의 보장금액을 비교하기 위해 선택된 플랜의 index값 가져오기 ex. 고급형 0, 표준형 1, 기본형 2
        List<WebElement> elements = driver.findElements(By.xpath("//div[@class[contains(., 'pc_plan_cover_bg')]]"));
        int idx = -1;
        for(int i = 0; i<elements.size(); i++){
            String classValue = elements.get(i).getAttribute("class");
            if(classValue.contains("on")) {
                idx = i;
            }
        }
        int planNum = 0;

        //홈페이지 특약개수 >= 가입설계 특약개수
        if (homepageTreatyCnt >= info.treatyList.size()) {

            for (int i = 0; i < homepageTreatyCnt; i++) {
                //todo 특약명 보이게 스크롤 처리
                WebElement el = driver.findElements(By.xpath("//tbody//label")).get(i);
                actions.moveToElement(el);
                actions.perform();

                String homepageTreatyName = driver.findElements(By.xpath("//tbody//label")).get(i).getText();
                homepageTreatyNameList.add(homepageTreatyName);     //홈페이지 특약명을 저장
            }

            logger.info("homepageTreatyNameList size :: " + homepageTreatyNameList.size());
            for (int i = 0; i < info.treatyList.size(); i++) {
                String treatyName = info.treatyList.get(i).treatyName;
                String treatyNameTrim = treatyName.replaceAll(" ", "");

                try {
                    WebElement foundTreaty = driver.findElement(By.xpath("//tbody//label[text()='" + treatyName + "']"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", foundTreaty);
                    treatyName = treatyName;
                } catch (NoSuchElementException e){     //특약명에 공백이 있을 경우 예외 처리
                    try {
                        WebElement foundTreaty = driver.findElement(By.xpath("//tbody//label[text()='" + treatyNameTrim + "']"));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", foundTreaty);
                        treatyName = treatyNameTrim;
                    } catch (Exception ex){             //특약에 "보장개시" 라는 문구로 인해 오류발생 시 예외 처리
                        try{
                            treatyName = info.treatyList.get(i).treatyName.replaceAll("\\([0-9]세보장개시\\)", "");
                            WebElement foundTreaty = driver.findElement(By.xpath("//tbody//label[text()='" + treatyName + "']"));
                            treatyName = treatyName;
                        } catch (Exception except){
                            notExistTreatyNameList.add(treatyName);
                            continue;
                        }
                    }
                }

                String foundTreatyName = driver.findElement(By.xpath("//tbody//label[text()='" + treatyName + "']")).getText();
                String foundTreatyAssureMoney = "";
                planNum = (idx == -1) ? 3 : 2;

                try{
                    foundTreatyAssureMoney = driver.findElement(By.xpath("//tbody//label[text()='" + treatyName + "']//ancestor::tr//td[" + (idx + planNum) + "]//span")).getAttribute("textContent");
                } catch (NoSuchElementException e){
                    foundTreatyAssureMoney = driver.findElement(By.xpath("//tbody//label[text()='" + treatyName + "']//ancestor::tr//td[" + (idx + planNum) + "]")).getAttribute("textContent");
                }


                if(info.textType.equals("")||foundTreatyAssureMoney.contains(info.textType)){
                    String TreatyAssureMoney = "";
                    if(foundTreatyAssureMoney.contains("억원")){
                        String zero = "00000000";
                        String convertMoney  = foundTreatyAssureMoney.replaceAll("[^0-9.]", "");
                        if(convertMoney.contains(".")){
                            String front = convertMoney.replaceAll("[^0-9]", "");
                            int decimal = convertMoney.indexOf(".");
                            String change = convertMoney.substring(decimal+1);
                            int size = change.length();
                            zero = zero.substring(size);
                            TreatyAssureMoney = front + zero;
                        } else {
                            TreatyAssureMoney = convertMoney + "00000000";
                        }

                    } else {
                        TreatyAssureMoney = String.valueOf(MoneyUtil.toDigitMoney(foundTreatyAssureMoney));
                    }
                    String planAssureMoney = String.valueOf(info.treatyList.get(i).assureMoney);
                    if(TreatyAssureMoney.equals(planAssureMoney)){
                        logger.info("특약 가입금액 일치 :: {} - {}원", treatyName, planAssureMoney);
                        homepageTreatyNameList.remove(foundTreatyName);
                    } else {
                        notSameTreatyAssureMoneyList.add(treatyName);
                    }
                } else {
                    notSameTreatyAssureMoneyList.add(treatyName);
//                    throw new Exception("KB손보의 특약 가입금액에는 플랜유형이 포함 [ex.표준형1,000만원] 되어있으며 일치하지 않아 exception 발생 [특약명 :: " + treatyName);
                }

            }

            boolean clearTreaty = true;
            if(notExistTreatyNameList.size() > 0){
                for(int k = 0; k<notExistTreatyNameList.size(); k++){
                    logger.info("홈페이지에는 존재하지 않는 특약이 가설에 있습니다. 특약명 :: {}", notExistTreatyNameList.get(k));
                }
                clearTreaty = false;
            }

            if(notSameTreatyAssureMoneyList.size() > 0 ){
                for(int j = 0; j<notSameTreatyAssureMoneyList.size(); j++){
                    logger.info("가설 특약 가입금액과 홈페이지의 가입 금액이 일치하지않습니다. 특약명 :: " + notSameTreatyAssureMoneyList.get(j));
                }
                clearTreaty = false;
            }

            if(clearTreaty == false){
                throw new Exception("가설 특약 가입금액과 홈페이지의 가입 금액을 확인해주세요.");
            }

            logger.info("가입설계에 없는 홈페이지 특약들 :: " + homepageTreatyNameList);


            //homepageTreatyNameList의 특약들을 체크해제 처리
            for (int i = 0; i < homepageTreatyNameList.size(); i++) {
                String toUncheckedTreatyName = homepageTreatyNameList.get(i);

                WebElement moveTreaty = driver.findElement(By.xpath("//tbody//label[text()='" + toUncheckedTreatyName + "']"));
                actions.moveToElement(moveTreaty);
                actions.perform();
                waitForLoading();

                try {
                    WebElement label = driver.findElement(By.xpath("//tbody//label[text()='" + toUncheckedTreatyName + "']"));
                    WebElement checkBox = label.findElement(By.xpath("./../input"));

                    //체크박스가 활성화되어있고 선택되어 있는 경우에만 체크해제시킴
                    if (checkBox.isEnabled() && checkBox.isSelected()) {
                        logger.info("[{}] 특약을 체크해제합니다...", toUncheckedTreatyName);
                        ((JavascriptExecutor)driver).executeScript("arguments[0].click();" , label);
                        waitForLoading();
                        try {
                           if (driver.findElement(By.cssSelector(".alert_wrap")).isDisplayed()) {
                                logger.debug("알럿표시 확인!!!");
                                WaitUtil.waitFor(1);
                                helper.click(By.linkText("확인"));
                                waitForLoading();
                            }
                        } catch (Exception e) {

                        }
                    }
                } catch (NoSuchElementException e) {
                    logger.info("체크박스 없는 경우(필수 특약입니다)");
                }
                waitForLoading();

            }

            for (int i = 0; i < homepageTreatyCnt; i++) {
                WebElement el = driver.findElements(By.xpath("//tbody//label")).get(i);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);

                //특약 체크 count
                try {
                    WebElement checkBox = el.findElement(By.xpath("./../input[@type='checkbox']"));
                    if (checkBox.isSelected()) {
                        checkBoxCount++;
                    }
                } catch (NoSuchElementException e) {
                    if(info.productCode.equals("KBF_MDC_D005")){
                        //실손보험일 경우 필수만 가입하므로, 필수 중에서도 가입금액이 있을 경우 해당 특약을 가입하는 것으로 간주
                        try{
                            String existTreatyMoney = el.findElement(By.xpath("./../../../../tr//td[" + (idx + planNum) + "]")).getAttribute("textContent").trim();
                            logger.info(existTreatyMoney);
//                            String existTreatyMoney = el.findElement(By.xpath("./../../../../tr//td[" + (idx + planNum) + "]")).getText();
                            if(!(existTreatyMoney.equals(""))){
                                checkBoxCount++;
                            }
                        } catch (NoSuchElementException ex) {}
                    } else {
                        logger.info("필수 특약 count");
                        checkBoxCount++;
                    }
                }
            }

            WaitUtil.waitFor(2);
            logger.info("체크된 특약 갯수 :: {}", checkBoxCount);
            logger.info("가설에 있는 특약 갯수 :: {}", info.treatyList.size());
            if(checkBoxCount != info.treatyList.size()){
                throw new Exception("가설 특약 갯수와 체크된 특약의 수가 다릅니다.");
            }

        } else {
            //홈페이지 특약개수 < 가입설계 특약개수
            ArrayList<String> treatyList = new ArrayList<>();

            for(int i=0; i<info.treatyList.size(); i++) {
                treatyList.add(info.treatyList.get(i).treatyName);
            }


            for(int i=0; i<homepageTreatyCnt; i++) {
                WebElement el = driver.findElements(By.xpath("//tbody//label")).get(i);
                actions.moveToElement(el);
                actions.perform();

                String homepageTreatyName = driver.findElements(By.xpath("//tbody//label")).get(i).getText();
                treatyList.remove(homepageTreatyName);

            }

            logger.info(treatyList + "특약들은 해당 나이({})에서는 가입할 수 없는 특약입니다.", info.age);
            throw new Exception("나이("+info.age+")에 가입불가한 특약이 존재함");

        }
    }

    protected void returnMoneyCheck(CrawlingProduct info, By returnMoneyElement, By returnRateElement, By notExistreturnMoneyElement) throws Exception {

            try{
                info.returnPremium = helper.waitVisibilityOf(driver.findElement(returnMoneyElement)).getText().replaceAll("[^0-9]","");

                //해약환급금은 있으나, 해약환급률이 없을 경우
                String returnRate = "";
                try{
                    returnRate = driver.findElement(returnRateElement).getText();
                } catch (NoSuchElementException e){
                    returnRate = "0%";
                }
                if(returnRate.equals("") || !(returnRate.contains("%"))){
                    throw new Exception("예상만기환급률이 빈 값이거나 잘못된 값입니다.");
                }
                int monthlyPremium = Integer.parseInt(driver.findElement(By.id("count1")).getText().replaceAll("[^0-9]", ""));
                int napTerm = Integer.parseInt(info.napTerm.replaceAll("[^0-9]", ""));
                String premiumSum = String.valueOf(monthlyPremium * napTerm * 12);

                List<PlanReturnMoney> returnMoneyList = new ArrayList<PlanReturnMoney>();

                PlanReturnMoney planReturnMoney = new PlanReturnMoney();
                planReturnMoney.setPlanId(Integer.parseInt(info.planId));
                planReturnMoney.setGender(info.getGenderEnum().name());
                planReturnMoney.setInsAge(Integer.parseInt(info.getAge()));
                planReturnMoney.setReturnMoney(info.returnPremium);				// 예상만기환급금
                planReturnMoney.setReturnRate(returnRate);				        // 예상만기환급률
                planReturnMoney.setPremiumSum(premiumSum);
                planReturnMoney.setTerm(info.napTerm);					        // 납입기간

                returnMoneyList.add(planReturnMoney);

                WaitUtil.loading(1);
                info.setPlanReturnMoneyList(returnMoneyList);

                logger.info("|--예상합계보험료 : {}", premiumSum);
                logger.info("|--예상만기환급금 : {}", info.returnPremium);
                logger.info("|--예상만기환급률 : {}", returnRate);

            } catch (NoSuchElementException e){
                String returnPremium = helper.waitVisibilityOf(driver.findElement(notExistreturnMoneyElement)).getText();
                if("환급금 없음".equals(returnPremium) || returnPremium.contains("순수보장형")) {
                    info.returnPremium = "0";
                } else {
                    throw new Exception();
                }
            }

    }


    protected void tesseract(CrawlingProduct info) throws Exception {
        File img = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        WaitUtil.waitFor(2);

        Tesseract instance = new Tesseract();
        instance.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        instance.setLanguage("kor");
        instance.setTessVariable("user_defined_dpi", "70");
        WaitUtil.waitFor(1);
        String result = instance.doOCR(img);

        logger.info("추출된 문자열 : {}", result);
        WaitUtil.waitFor(1);

        int start = 0;
        String firstPremium = "";
        String[] text = {"보험료", "보엄료", "보멈료", "보범료"};
        boolean isFound = false;
        for(int i = 0; i < text.length; i++){
            start = result.indexOf("초회"+text[i]);
            isFound = (start != -1) ? true : false;

            if(isFound) {
                break;
            }
        }
        if(isFound){
            int end = result.indexOf("원", start);
            firstPremium = result.substring(start, end + 1);
        }

        if(!isFound){
            start = 0 ;
            int end = 0;
            boolean find = false;

            for(int i = 0; i<result.length(); i++){
                start = result.indexOf( "보험료");
                end = result.indexOf("원", start);
                find = true;
                if(find) break;
            }

            int targetNum = result.lastIndexOf(" ", end);
            firstPremium = result.substring(targetNum+1, end);
        }

        String premium = firstPremium.replaceAll("[^0-9]", "");
        if(StringUtil.isEmpty(premium) || (Integer.parseInt(premium) > 100000) || (Integer.parseInt(premium) < 1000)){
            throw new Exception("잘못된 값이 들어가 있습니다. 다시 확인해주세요.");
        }

        info.getTreatyList().get(0).monthlyPremium = premium;
        logger.info("월 보험료 :: {}원", premium);
    }

    protected void setTextToInputBox(By element, String text) {
        WebElement inputBox = driver.findElement(element);
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }
}