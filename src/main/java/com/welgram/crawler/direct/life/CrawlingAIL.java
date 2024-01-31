package com.welgram.crawler.direct.life;

import com.welgram.common.PersonNameGenerator;
import com.welgram.common.WaitUtil;
import com.welgram.common.except.NotFoundValueInSelectBoxException;
import com.welgram.common.except.crawler.setUserInfo.SetBirthdayException;
import com.welgram.crawler.SeleniumCrawler;
import com.welgram.crawler.general.CrawlingProduct;
import com.welgram.crawler.general.CrawlingTreaty;
import com.welgram.crawler.general.PlanReturnMoney;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;



public abstract class CrawlingAIL extends SeleniumCrawler {

    /* ============================= 공시실 ============================ */
    // 보험상품명 찾기
    protected void selectItem(CrawlingProduct info){
        elements = driver.findElements(By.cssSelector("#planNo > option")); // options

        for(WebElement option : elements){
            if(option.getText().replace(" ","").contains(
                    info.productName
                    .replace(" ","")
                    .replace("무배당",""))) {

                logger.info(option.getText()+"클릭!");
                option.click();
            }
        }
    }



    // 이름
    protected void setname() throws Exception {
        logger.info("이름");
        String name = PersonNameGenerator.generate();
        logger.debug("name: {}", name);
        helper.click(By.cssSelector("#custNm"));
        helper.sendKeys3_check(By.cssSelector("#custNm"), name);
    }



    // 생년월일
    protected void setBirth(By by , CrawlingProduct info) throws SetBirthdayException {
        try {
            helper.sendKeys3_check(by,info.fullBirth);
        } catch (Exception e) {
            throw new SetBirthdayException(e);
        }
    }



    // 성별
    protected void setGenderElder(int gender) throws Exception {
        elements = helper.waitVisibilityOfAllElementsLocatedBy(
            By.cssSelector("body > form:nth-child(1) > div > div.container > div:nth-child(3) > div > table > tbody > tr:nth-child(1) > td:nth-child(4) > label"));

        if (gender == 0) {
            elements.get(0).click(); // 남자
            logger.info("남성");
        } else {
            elements.get(1).click(); // 여자
            logger.info("여성");
        }
    }



    // 보험료
    protected void getpremium(CrawlingProduct info) throws InterruptedException {
        String premium;

        element = driver.findElement(By.cssSelector("#premium_tot"));
        // logger.info("element : " + element);
        WaitUtil.loading(2);
        premium = element.getText().replace("원", "").replace(",","").replace(" ","");
        // premium = driver.findElement(By.id("resMonthbill")).getText().replaceAll("[^0-9]", "");

        WaitUtil.loading(2);
        info.treatyList.get(0).monthlyPremium = premium;
        info.errorMsg = "";
        logger.info("월 보험료 : " + premium + "원");
    }



    // 해약환급금 가져오기
    protected void WebgetReturnPremium(CrawlingProduct info) throws Exception {
        List<PlanReturnMoney> planReturnMoneyList = new ArrayList<>();
        List<WebElement> trElements = driver.findElements(By.cssSelector("#layer3 > div > div > table > tbody > tr"));
        for (WebElement tr : trElements) {
            PlanReturnMoney planReturnMoney = new PlanReturnMoney();
            String term = tr.findElements(By.tagName("td")).get(0).getAttribute("innerText");
            String premiumSum = tr.findElements(By.tagName("td")).get(1)
                    .getAttribute("innerText")
                    .replaceAll("[^0-9]", "");
            String returnMoney = tr.findElements(By.tagName("td")).get(2)
                    .getAttribute("innerText")
                    .replaceAll("[^0-9]", "");
            String returnRate = tr.findElements(By.tagName("td")).get(3).getAttribute("innerText");

            logger.info("기간 ::" + term);
            logger.info("누적 :: " + premiumSum);
            logger.info("해약환급금 :: " + returnMoney);
            logger.info("환급률 :: " + returnRate);
            logger.info("=============================");

            planReturnMoney.setTerm(term);
            planReturnMoney.setPremiumSum(premiumSum);
            planReturnMoney.setReturnMoney(returnMoney);
            planReturnMoney.setReturnRate(returnRate);

            planReturnMoneyList.add(planReturnMoney);

            // 기본 해약환급금 세팅
            info.returnPremium = returnMoney.replaceAll("[^0-9]", "");
        }
        info.setPlanReturnMoneyList(planReturnMoneyList);
    }



    // 보험기간
    protected void setInsTermOV(String insTerm) throws Exception {
        boolean result = false;
        elements = driver.findElements(By.cssSelector("#polprd_N901V0_02B > option"));
        insTerm = insTerm.replace("년", "").replace("세", "");

        for (WebElement webElement : elements) {
            if (webElement.getText().contains(insTerm)) {
                logger.info(webElement.getText()+"클릭!");
                webElement.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!result) {
            throw new Exception("보험기간 선택 오류입니다.");
        }
    }



    // 납입기간
    protected void setNapTerm(String napTerm) throws Exception {
        boolean result = false;
        elements = driver.findElements(By.cssSelector("#payprd_N901V0_02B > option"));
        napTerm = napTerm.replace("년", "").replace("세", "");

        for (WebElement webElement : elements) {
            if (webElement.getText().contains(napTerm)) {
                logger.info(webElement.getText()+"클릭!");
                webElement.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!result) {
            throw new Exception("보험기간 선택 오류입니다.");
        }
    }



    protected void selectDisclosureRoomProduct(CrawlingProduct info) throws Exception {

        logger.info("공시실 옵션 입력 시작");
        elements = driver.findElements(By.cssSelector("#product_kind > option"));
        for(WebElement option : elements){
            // todo | 조건문 내용을 정규표현식 처리 가능하면 수정 ('종신'이라는 단어만 처리)
            if(option.getText().contains(info.categoryName)) {
                logger.info(option.getText()+"클릭!");
                option.click();
                helper.click(By.cssSelector("body > form:nth-child(9) > div > div > ul > li:nth-child(1) > button"));
                logger.info("상품분류 선택완료 :: {}", info.getCategoryName());
                break;
            }
        }

        elements = driver.findElements(By.cssSelector("#planNo > option"));
        for(WebElement option: elements) {
            if(info.productName.contains(option.getText())) {
                logger.info(option.getText()+"클릭!");
                option.click();
                helper.click(By.cssSelector("body > form:nth-child(9) > div > div > ul > li:nth-child(2) > button"));
                logger.info("보험상품명 선택완료  :: {}", info.getProductName());
                break;
            }
        }
    }



    // 특약선택
    protected void setTreaty(CrawlingProduct info, CrawlingTreaty item) throws Exception {
        String treatyName = item.treatyName;
        String assureMoney = String.valueOf(item.assureMoney);
        String iteminsTerm = item.insTerm.replace("년", "").replace("세", "");
        String itemnapTerm = item.napTerm.replace("년", "").replace("세", "");

        elements = helper.waitVisibilityOfAllElements(driver.findElements(By.cssSelector("body > form:nth-child(1) > div > div.container > div:nth-child(8) > div > table > tbody > tr")));

        for (WebElement tr : elements) {
            String tdTreatyName = tr.findElement(By.cssSelector("td.text-left > label")).getText().replace(" ","");

            // 담보명 일치 여부
            if (treatyName.replace(" ","").indexOf(tdTreatyName) > -1){

                // 체크박스 체크
                WebElement checkBox = tr.findElement(By.cssSelector("td.text-left > label > span > span"));
                if (!checkBox.isSelected()){
                    logger.info(treatyName+"클릭!");
                    checkBox.click();
                }

                elements = tr.findElements(By.tagName("td"));
                int counttd = 0;
                for(WebElement td : elements){

                    if(counttd == 1 ){ // 담보의 보험기간
                        elements = td.findElements(By.cssSelector("option"));
                        for(WebElement option : elements){
                            if(option.getText().contains(iteminsTerm)){
                                   logger.info(option.getText()+"클릭!");
                                   option.click();
                            }
                        }
                    }

                    if(counttd == 2 ){ // 담보의 납입기간
                        elements = td.findElements(By.cssSelector("option"));
                        for(WebElement option : elements){
                            if(option.getText().contains(itemnapTerm)){
                                logger.info(option.getText()+"클릭!");
                                option.click();
                            }
                        }
                    }

                    if(counttd==3){ // 담보의 구좌 입력
                        WebElement assureMoneyInput = td.findElements(By.cssSelector("input")).get(1);
                         String inputid = assureMoneyInput.getAttribute("id");
                         int intAssureMoney = Integer.parseInt(assureMoney) / 100000 ;
                         driver.findElement(By.id(inputid)).sendKeys(Integer.toString(intAssureMoney));
                         logger.info("특약 선택 :: " + treatyName + " 선택 : " + intAssureMoney + "구좌 입력");
                    }

                    counttd ++;
                }
            }
        } // for: tr
    }



    // 가입금액
    protected void setassureMoney(CrawlingProduct product) {
        elements = driver.findElements(By.cssSelector("body > form:nth-child(1) > div > div.container > div:nth-child(6) > div > table > tbody > tr:nth-child(1) > td:nth-child(12) > select > option"));

        for(WebElement option : elements){
            if(option.getAttribute("value").equals(Integer.toString(Integer.parseInt(product.assureMoney) / 10000))){
                logger.info(option.getText()+"클릭!");
                option.click();
            }
        }
    }



    // 모바일 - 플랜선택
    protected void searchingplan(CrawlingProduct product) throws Exception {

        if(product.planSubName.equals("딱 수술비만 플랜")){
            logger.info(product.planSubName + "클릭!");
            helper.click(By.cssSelector("#wrapper > div:nth-child(4)"));
        } else if (product.planSubName.equals("실속 강화 플랜")){
            logger.info(product.planSubName + "클릭!");
            helper.click(By.cssSelector("#wrapper > div:nth-child(5)"));
        } else if (product.planSubName.equals("암 기본 플랜")){
            logger.info(product.planSubName + "클릭!");
            helper.click(By.cssSelector("#wrapper > div:nth-child(6)"));
        } else if (product.planSubName.equals("암 실속 플랜")){
            logger.info(product.planSubName + "클릭!");
            helper.click(By.cssSelector("#wrapper > div:nth-child(7)"));
        } else if (product.planSubName.equals("암 집중 플랜")){
            logger.info(product.planSubName + "클릭!");
            helper.click(By.cssSelector("#wrapper > div:nth-child(8)"));
        } else if (product.planSubName.equals("2대질병 생활비 플랜")){
            logger.info(product.planSubName + "클릭!");
            helper.click(By.cssSelector("#wrapper > div:nth-child(9)"));
        }
    }



    // 보험기간
    protected void setDisclosureRoomInsTerm(String insTerm) throws Exception {
        boolean result = false;
        elements = driver.findElements(By.cssSelector("#polprd_A30100_32C > option"));
        insTerm = insTerm.replace("년", "").replace("세", "");

        for (WebElement webElement : elements) {
            if (webElement.getText().contains(insTerm)) {
                logger.info(webElement.getText()+"클릭!");
                webElement.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!result) {
            throw new Exception("보험기간 선택 오류입니다.");
        }
    }



    // 납입기간
    protected void setDisclosureRoomNapTerm(String napTerm) throws Exception {
        boolean result = false;
        elements = driver.findElements(By.cssSelector("#payprd_A30100_32C > option"));
        napTerm = napTerm.replace("년", "").replace("세", "");

        for (WebElement webElement : elements) {
            if (webElement.getText().contains(napTerm)) {
                logger.info(webElement.getText()+"클릭!");
                webElement.click();
                result = true;
                WaitUtil.waitFor();
                break;
            }
        }

        if (!result) {
            throw new Exception("납입기간 선택 오류입니다.");
        }
    }



    //모바일 사이트 로딩바 명시적 대기
    protected void waitMobileLoadingBar() throws Exception {
        wait.until(ExpectedConditions.invisibilityOfAllElements(driver.findElements(By.xpath("//div[@class[contains(., 'load')]]"))));
    }



    //element 클릭 가능한 상태가 될 때까지 대기
    protected WebElement waitElementToBeClickable(By by) throws Exception {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }



    //element 클릭 가능한 상태가 될 때까지 대기
    protected WebElement waitElementToBeClickable(WebElement element) throws Exception {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }



    //해당 element가 존재하는지 여부를 리턴
    protected boolean existElement(By by) {
        boolean isExist = true;

        try {
            driver.findElement(by);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }



    //해당 element가 존재하는지 여부를 리턴
    protected boolean existElement(WebElement rootEl, By by) {

        boolean isExist = true;

        try {
            rootEl.findElement(by);
        }catch(NoSuchElementException e) {
            isExist = false;
        }

        return isExist;
    }



    //select 박스에서 value로 option을 선택하는 메서드
    protected void selectOptionByValue(WebElement selectEl, String value) throws NotFoundValueInSelectBoxException {
        Select select = new Select(selectEl);
        try {
            select.selectByValue(value);
        } catch (NoSuchElementException e) {
            throw new NotFoundValueInSelectBoxException("selectBox에서 해당 value('" + value + "')값을 찾을 수 없습니다.");
        }
    }



    //input 태그에 text 입력
    protected void setTextToInputBox(WebElement inputBox, String text) throws Exception {
        inputBox.click();
        inputBox.clear();
        inputBox.sendKeys(text);
    }



    //input 태그에 text 입력
    protected void setTextToInputBox(By element, String text) throws Exception {
        WebElement inputBox = driver.findElement(element);
        setTextToInputBox(inputBox, text);
    }



    protected void moveToElementByJavascriptExecutor(WebElement element) throws Exception {
        //element 좌표에서 70을 빼는 이유는 모바일 버전에서 헤더부분에 가려져 element 클릭이 안되기 때문이다.
        int posY = element.getLocation().getY() - 70;
        posY = (posY < 0) ? 0 : posY;
//        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor)driver).executeScript("window.scrollTo(0, " + posY + ");");
        WaitUtil.waitFor(2);
    }



    protected void moveToElementByJavascriptExecutor(By by) throws Exception {
        WebElement element = driver.findElement(by);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
        WaitUtil.waitFor(2);
    }



    // textType 조회
    protected String[] getArrTextType(CrawlingProduct info) {
        logger.info("텍스트타입(textType)에 저장된 내용을 가져옵니다");
        String tType = info.textType;
        String[] arrTType = tType.split("#");
        for(int i = 0; i < arrTType.length; i++) {
            if(arrTType.length <= 1) {
                logger.info("LENGTH : 1");
            } else {
                arrTType[i] = arrTType[i].trim();
                logger.info("ARR [" + i + "] : " + arrTType[i]);
                // ex)
                // 0 : 신한CEO정기보험(무배당, 보증비용부과형)
                // 1 : (20%)체증형
                // 2 : 일반심사형

            }
        }
        return arrTType;
    }


}
